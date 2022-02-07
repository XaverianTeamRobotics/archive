package org.firstinspires.ftc.teamcode.main.scripts.teleop;

import android.util.Log;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.teamcode.main.utils.gamepads.GamepadManager;
import org.firstinspires.ftc.teamcode.main.utils.helpers.elevator.ElevatorDriver;
import org.firstinspires.ftc.teamcode.main.utils.interactions.items.StandardMotor;
import org.firstinspires.ftc.teamcode.main.utils.interactions.items.StandardServo;
import org.firstinspires.ftc.teamcode.main.utils.io.InputSpace;
import org.firstinspires.ftc.teamcode.main.utils.io.OutputSpace;
import org.firstinspires.ftc.teamcode.main.utils.locations.*;
import org.firstinspires.ftc.teamcode.main.utils.resources.Resources;
import org.firstinspires.ftc.teamcode.main.utils.scripting.TeleOpScript;

import java.lang.reflect.Array;
import java.util.HashMap;

public class FullTeleOpScript extends TeleOpScript {

    private GamepadManager gamepadManager;
    private InputSpace inputSpace;
    private OutputSpace outputSpace;
    private boolean intakeShouldBeDown = false, intakeButtonWasDown = false, isAllowedToControl = false, noControlIntakeLifter = false;
    private ElevatorDriver elevatorDriver;

    public FullTeleOpScript(LinearOpMode opMode) {
        super(opMode);
        // set fields and calibrate robot
        /*
         * GamepadManager Functions:
         *  F1: Driving
         *  F2: Intake Motor Control, Intake Lift Control
         *  F3: Lift Control
         *  F4: Duck Spinner Control
         *  F5: Unassigned
         *  F6: Unassigned
         * Users:
         *  U1: F1, F4
         *  U2: F2, F3
         * */
        gamepadManager = new GamepadManager(getOpMode().gamepad1, getOpMode().gamepad2, getOpMode().gamepad2, getOpMode().gamepad1, getOpMode().gamepad1, getOpMode().gamepad1);
        getOpMode().gamepad1.reset();
        getOpMode().gamepad2.reset();
        inputSpace = new InputSpace(getOpMode().hardwareMap);
        outputSpace = new OutputSpace(getOpMode().hardwareMap);
        elevatorDriver = new ElevatorDriver(inputSpace, outputSpace, getOpMode());
        elevatorDriver.setFeedbackDestination(gamepadManager);
        // these are the upper values
        inputSpace.sendInputToIntakeLifter(IntakeLiftingServoLocation.Action.SET_POSITION, 23);
        inputSpace.sendInputToLeftHandGrabber(LeftHandGrabbingServoLocation.Action.SET_POSITION, 30);
        inputSpace.sendInputToRightHandGrabber(RightHandGrabbingServoLocation.Action.SET_POSITION, 60);
        getOpMode().sleep(500);
        // these are the lower values
        inputSpace.sendInputToLeftHandGrabber(LeftHandGrabbingServoLocation.Action.SET_POSITION, 55);
        inputSpace.sendInputToRightHandGrabber(RightHandGrabbingServoLocation.Action.SET_POSITION, 30);
        getOpMode().sleep(4000);
        calibrateElevator();
        inputSpace.sendInputToIntakeLifter(IntakeLiftingServoLocation.Action.SET_POSITION, 60);
        getOpMode().sleep(5000);
        // alert drivers robot is ready
        gamepadManager.functionOneGamepad().rumble(1000);
        gamepadManager.functionTwoGamepad().rumble(1000);
        gamepadManager.functionThreeGamepad().rumble(1000);
        gamepadManager.functionFourGamepad().rumble(1000);
        gamepadManager.functionFiveGamepad().rumble(1000);
        gamepadManager.functionSixGamepad().rumble(1000);
    }

    @Override
    public void main() {
        controlDrivetrain();
        controlIntakeLifter();
        controlIntake();
        controlEntireLiftAutonomously();
        controlDuck();
        updateLiftControlPermissions();
        // debug
        debug();
    }

    private void calibrateElevator() {
        // move elevator up for a second
        int timeAsOfLastElevatorCalibrationBegin = (int) getOpMode().time;
        while(outputSpace.receiveOutputFromElevatorBottomLimitSwitch(ElevatorBottomLimitSwitchLocation.Values.PRESSED) == 0 && timeAsOfLastElevatorCalibrationBegin > (int) getOpMode().time - 1) {
            inputSpace.sendInputToElevatorLeftLift(ElevatorLeftLiftMotorLocation.Action.SET_SPEED, -100);
            inputSpace.sendInputToElevatorRightLift(ElevatorRightLiftMotorLocation.Action.SET_SPEED, -100);
        }
        inputSpace.sendInputToHandSpinner(HandSpinningServoLocation.Action.SET_POSITION, 23);
        // move elevator down until it reaches the bottom
        while(outputSpace.receiveOutputFromElevatorBottomLimitSwitch(ElevatorBottomLimitSwitchLocation.Values.PRESSED) == 0) {
            inputSpace.sendInputToElevatorLeftLift(ElevatorLeftLiftMotorLocation.Action.SET_SPEED, 30);
            inputSpace.sendInputToElevatorRightLift(ElevatorRightLiftMotorLocation.Action.SET_SPEED, 30);
        }
        // reset the elevator and hand
        ((StandardMotor) inputSpace.getElevatorLeftLift().getInternalInteractionSurface()).reset();
        ((StandardMotor) inputSpace.getElevatorRightLift().getInternalInteractionSurface()).reset();
        inputSpace.sendInputToHandSpinner(HandSpinningServoLocation.Action.SET_POSITION, 23);
    }

    private void controlDrivetrain() {
        // calculate the x and y speeds
        int left = (int) Range.clip((gamepadManager.functionOneGamepad().left_stick_y) * 75, -75, 75);
        int right = (int) Range.clip((gamepadManager.functionOneGamepad().right_stick_y) * 75, -75, 75);
        // set the defined speeds
        inputSpace.sendInputToTank(TankDrivetrainLocation.Action.SET_SPEED, -right, -left);
    }

    private void controlIntakeLifter() {
        // move the intake based on the left bumper's state
        if(gamepadManager.functionTwoGamepad().left_bumper && !noControlIntakeLifter) {
            if(!intakeButtonWasDown) {
                intakeShouldBeDown = !intakeShouldBeDown;
            }
            intakeButtonWasDown = true;
        }else{
            intakeButtonWasDown = false;
        }
        if(intakeShouldBeDown) {
            inputSpace.sendInputToIntakeLifter(IntakeLiftingServoLocation.Action.SET_POSITION, 23);
        }else{
            inputSpace.sendInputToIntakeLifter(IntakeLiftingServoLocation.Action.SET_POSITION, 60);
        }
    }

    public void updateLiftControlPermissions() {
        isAllowedToControl = ((StandardServo) inputSpace.getIntakeLifter().getInternalInteractionSurface()).getPosition() != 60;
        if(!elevatorDriver.isStable()) {
            noControlIntakeLifter = true;
            intakeShouldBeDown = true;
            inputSpace.sendInputToIntakeLifter(IntakeLiftingServoLocation.Action.SET_POSITION, 23);
        }
    }

    private void controlIntake() {
        // control the intake motor based on the trigger inputs
        int intakeGas = (int) Range.clip(gamepadManager.functionTwoGamepad().left_trigger * 100, 0, 100);
        int intakeBrake = (int) Range.clip(gamepadManager.functionTwoGamepad().right_trigger * 100, 0, 100);
        int intakeSpeed = Range.clip(intakeGas - intakeBrake, -100, 100);
        inputSpace.sendInputToIntakeSpinner(IntakeSpinningMotorLocation.Action.SET_SPEED, intakeSpeed);
    }

    /**
     * This method controls all the autonomous stuff for the lift in TeleOps. Basically, it contains a bunch of routines. On every run, if no routine is running and a button is pressed to toggle a certain routine, the routine will fire. It will enable its routine, making all other routines impossible to run. During running, controllers will give feedback via vibrations to the user to let them know the elevator is performing a routine. Once a routine is complete, they will stop and the elevator will be able to run another routine once input is received.
     */
    private void controlEntireLiftAutonomously() {
        // enables intake pos routine if requested
        if(gamepadManager.functionThreeGamepad().a) {
            elevatorDriver.setToIntakePosition();
        }
        // enables lower level ball routine if requested
        if(gamepadManager.functionThreeGamepad().b && !gamepadManager.functionThreeGamepad().touchpad) {
            elevatorDriver.setToLowerBallPosition();
        }
        // enables middle level ball routine routine if requested
        if(gamepadManager.functionThreeGamepad().y && !gamepadManager.functionThreeGamepad().touchpad) {
            elevatorDriver.setToMediumBallPosition();
        }
        // enables top level ball routine if requested
        if(gamepadManager.functionThreeGamepad().x && !gamepadManager.functionThreeGamepad().touchpad) {
            elevatorDriver.setToTopBallPosition();
        }
        // enables bottom level block routine if requested
        if(gamepadManager.functionThreeGamepad().b && gamepadManager.functionThreeGamepad().touchpad) {
            elevatorDriver.setToLowerBlockPosition();
        }
        // enables middle level block routine if requested
        if(gamepadManager.functionThreeGamepad().y && gamepadManager.functionThreeGamepad().touchpad) {
            elevatorDriver.setToMediumBlockPosition();
        }
        // enables top level block routine if requested
        if(gamepadManager.functionThreeGamepad().x && gamepadManager.functionThreeGamepad().touchpad) {
            elevatorDriver.setToTopBlockPosition();
        }
    }

    private void controlDuck() {
        // turn duck motor slowly in the correct direction
        int speed = gamepadManager.functionFourGamepad().right_bumper ? -50 : 0;
        speed += gamepadManager.functionFourGamepad().left_bumper ? 50 : 0;
        inputSpace.sendInputToDuckMotor(DuckMotorLocation.Action.SET_SPEED, speed);
    }

    private void debug() {
        getOpMode().telemetry.addData("Elevator Encoder Position", ((StandardMotor) inputSpace.getElevatorLeftLift().getInternalInteractionSurface()).getDcMotor().getCurrentPosition());
        getOpMode().telemetry.addData("Intake Lift %", ((StandardServo) inputSpace.getIntakeLifter().getInternalInteractionSurface()).getPosition());
        getOpMode().telemetry.addData("Hand %", ((StandardServo) inputSpace.getHandSpinner().getInternalInteractionSurface()).getPosition());
        getOpMode().telemetry.addData("Distance Sensor MM", outputSpace.receiveOutputFromHandDistanceSensor());
        getOpMode().telemetry.update();
    }

    @Override
    public void stop() {
        inputSpace.stop();
        outputSpace.stop();
    }

}
