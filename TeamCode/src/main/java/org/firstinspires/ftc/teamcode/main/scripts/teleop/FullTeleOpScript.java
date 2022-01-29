package org.firstinspires.ftc.teamcode.main.scripts.teleop;

import android.util.Log;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.teamcode.main.utils.gamepads.GamepadManager;
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
    private double timeAsOfLastManualIntakeMovement = 0, timeAsOfLastFullLiftMovement = 0, timeAsOfLastManualHandMovement = 0;
    private int step = 0, manualHandPos = 23;
    private boolean intakeShouldBeDown = false, intakeButtonWasDown = false, manualMode = false, isAllowedToControl = false, noControlIntakeLifter = false;
    private boolean isMovingToLBall = false, isMovingToMBall = false, isMovingToTBall = false, isMovingToLBlock = false, isMovingToMBlock = false, isMovingToTBlock = false, isMovingToBasePos = false, isMovingToIntakePos = false;

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
        // these are the upper values
        inputSpace.sendInputToIntakeLifter(IntakeLiftingServoLocation.Action.SET_POSITION, 27);
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
    
    private void rumbleAllControllers() {
        gamepadManager.functionOneGamepad().rumble(Gamepad.RUMBLE_DURATION_CONTINUOUS);
        gamepadManager.functionTwoGamepad().rumble(Gamepad.RUMBLE_DURATION_CONTINUOUS);
        gamepadManager.functionThreeGamepad().rumble(Gamepad.RUMBLE_DURATION_CONTINUOUS);
        gamepadManager.functionFourGamepad().rumble(Gamepad.RUMBLE_DURATION_CONTINUOUS);
        gamepadManager.functionFiveGamepad().rumble(Gamepad.RUMBLE_DURATION_CONTINUOUS);
        gamepadManager.functionSixGamepad().rumble(Gamepad.RUMBLE_DURATION_CONTINUOUS);
    }
    
    private void derumbleAllControllers() {
        gamepadManager.functionOneGamepad().stopRumble();
        gamepadManager.functionTwoGamepad().stopRumble();
        gamepadManager.functionThreeGamepad().stopRumble();
        gamepadManager.functionFourGamepad().stopRumble();
        gamepadManager.functionFiveGamepad().stopRumble();
        gamepadManager.functionSixGamepad().stopRumble();
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
        int left = (int) Range.clip((gamepadManager.functionOneGamepad().left_stick_y - gamepadManager.functionOneGamepad().left_stick_x) * 75, -75, 75);
        int right = (int) Range.clip((gamepadManager.functionOneGamepad().left_stick_y + gamepadManager.functionOneGamepad().left_stick_x) * 75, -75, 75);
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
            inputSpace.sendInputToIntakeLifter(IntakeLiftingServoLocation.Action.SET_POSITION, 27);
        }else{
            inputSpace.sendInputToIntakeLifter(IntakeLiftingServoLocation.Action.SET_POSITION, 60);
        }
    }

    public void updateLiftControlPermissions() {
        isAllowedToControl = ((StandardServo) inputSpace.getIntakeLifter().getInternalInteractionSurface()).getPosition() != 60;
        if(isMovingToBasePos || isMovingToLBall || isMovingToMBall || isMovingToTBall || isMovingToLBlock || isMovingToMBlock || isMovingToTBlock || isMovingToIntakePos) {
            noControlIntakeLifter = true;
            intakeShouldBeDown = true;
            inputSpace.sendInputToIntakeLifter(IntakeLiftingServoLocation.Action.SET_POSITION, 27);
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
     * This method controls all the autonomous stuff for the lift in TeleOps. Basically, it contains a bunch of routines. On every run, if no routine is running and a button is pressed to toggle a certain routine, the routine will fire. It will enable its routine, making all other routines impossible to run. It also has a step counter for routines with multiple steps. All of the steps are inside the statement checking if the routine is enabled.
     */
    private void controlEntireLiftAutonomously() {
        // enables intake pos routine if requested
        if(gamepadManager.functionThreeGamepad().a && !isMovingToBasePos && !isMovingToLBall && !isMovingToMBall && !isMovingToTBall && !isMovingToLBlock && !isMovingToMBlock && !isMovingToTBlock && !isMovingToIntakePos && isAllowedToControl) {
            isMovingToIntakePos = true;
            step = 0;
        }
        if(isMovingToIntakePos) {
            rumbleAllControllers();
            // sets the hand to base position
            if(step == 0) {
                inputSpace.sendInputToHandSpinner(HandSpinningServoLocation.Action.SET_POSITION, 23);
                timeAsOfLastFullLiftMovement = getOpMode().time;
                step++;
            }
            // after moving the hand, move the elevator to the base position
            if(step == 1) {
                if(outputSpace.receiveOutputFromElevatorBottomLimitSwitch(ElevatorBottomLimitSwitchLocation.Values.PRESSED) != 0) {
                    step++;
                }else if(timeAsOfLastFullLiftMovement + 1.5 <= getOpMode().time) {
                    inputSpace.sendInputToElevatorLeftLift(ElevatorLeftLiftMotorLocation.Action.SET_SPEED, 40);
                    inputSpace.sendInputToElevatorRightLift(ElevatorRightLiftMotorLocation.Action.SET_SPEED, 40);
                    step++;
                }
            }
            // once the elevator is at the bottom, reset it
            if(step == 2 && outputSpace.receiveOutputFromElevatorBottomLimitSwitch(ElevatorBottomLimitSwitchLocation.Values.PRESSED) != 0) {
                inputSpace.sendInputToElevatorLeftLift(ElevatorLeftLiftMotorLocation.Action.SET_SPEED, 0);
                inputSpace.sendInputToElevatorRightLift(ElevatorRightLiftMotorLocation.Action.SET_SPEED, 0);
                ((StandardMotor) inputSpace.getElevatorLeftLift().getInternalInteractionSurface()).reset();
                ((StandardMotor) inputSpace.getElevatorRightLift().getInternalInteractionSurface()).reset();
                step++;
            }
            // once at base, move the hand to the intake position
            if(step == 3) {
                inputSpace.sendInputToHandSpinner(HandSpinningServoLocation.Action.SET_POSITION, 20);
                inputSpace.sendInputToLeftHandGrabber(LeftHandGrabbingServoLocation.Action.SET_POSITION, 30);
                inputSpace.sendInputToRightHandGrabber(RightHandGrabbingServoLocation.Action.SET_POSITION, 60);
                step++;
            }
            if(step == 4 && outputSpace.receiveOutputFromHandDistanceSensor() <= 120) {
                timeAsOfLastFullLiftMovement = getOpMode().time;
                step++;
            }
            // once ball is in place, move to base position
            if(step == 5 && timeAsOfLastFullLiftMovement + 0.5 <= getOpMode().time) {
                step = 0;
                isMovingToIntakePos = false;
                isMovingToBasePos = true;
            }
        }
        // moves to base pos - this is not a routine that can be enabled by user input, but rather enabled by other routines to reset them after use
        if(isMovingToBasePos) {
            rumbleAllControllers();
            // sets the hand to base position
            if(step == 0) {
                inputSpace.sendInputToLeftHandGrabber(LeftHandGrabbingServoLocation.Action.SET_POSITION, 55);
                inputSpace.sendInputToRightHandGrabber(RightHandGrabbingServoLocation.Action.SET_POSITION, 30);
                inputSpace.sendInputToHandSpinner(HandSpinningServoLocation.Action.SET_POSITION, 23);
                timeAsOfLastFullLiftMovement = getOpMode().time;
                step++;
            }
            // after moving the hand, move the elevator to the base position
            if(step == 1) {
                if(outputSpace.receiveOutputFromElevatorBottomLimitSwitch(ElevatorBottomLimitSwitchLocation.Values.PRESSED) != 0) {
                    step++;
                }else if(timeAsOfLastFullLiftMovement + 1.5 <= getOpMode().time) {
                    inputSpace.sendInputToElevatorLeftLift(ElevatorLeftLiftMotorLocation.Action.SET_SPEED, 40);
                    inputSpace.sendInputToElevatorRightLift(ElevatorRightLiftMotorLocation.Action.SET_SPEED, 40);
                    step++;
                }
            }
            // once the elevator is at the bottom, reset it
            if(step == 2 && outputSpace.receiveOutputFromElevatorBottomLimitSwitch(ElevatorBottomLimitSwitchLocation.Values.PRESSED) != 0) {
                inputSpace.sendInputToElevatorLeftLift(ElevatorLeftLiftMotorLocation.Action.SET_SPEED, 0);
                inputSpace.sendInputToElevatorRightLift(ElevatorRightLiftMotorLocation.Action.SET_SPEED, 0);
                ((StandardMotor) inputSpace.getElevatorLeftLift().getInternalInteractionSurface()).reset();
                ((StandardMotor) inputSpace.getElevatorRightLift().getInternalInteractionSurface()).reset();
                isMovingToBasePos = false;
                step = 0;
                noControlIntakeLifter = false;
                derumbleAllControllers();
            }
        }
        // enables lower level ball routine if requested
        if(gamepadManager.functionThreeGamepad().b && !gamepadManager.functionThreeGamepad().touchpad && !isMovingToBasePos && !isMovingToLBall && !isMovingToMBall && !isMovingToTBall && !isMovingToLBlock && !isMovingToMBlock && !isMovingToTBlock  && !isMovingToIntakePos && isAllowedToControl) {
            isMovingToLBall = true;
            step = 0;
        }
        // dispenses ball at lower level
        if(isMovingToLBall) {
            rumbleAllControllers();
            // move the elevator to allow hand room to turn
            if(step == 0) {
                inputSpace.sendInputToElevatorLeftLift(ElevatorLeftLiftMotorLocation.Action.SET_POSITION, -500);
                inputSpace.sendInputToElevatorRightLift(ElevatorRightLiftMotorLocation.Action.SET_POSITION, -500);
                timeAsOfLastFullLiftMovement = getOpMode().time;
                step++;
            }
            // turn hand to safest position once elevator reaches its position
            if(step == 1 && ((StandardMotor) inputSpace.getElevatorLeftLift().getInternalInteractionSurface()).getDcMotor().getCurrentPosition() <= -500) {
                inputSpace.sendInputToHandSpinner(HandSpinningServoLocation.Action.SET_POSITION, 33);
                timeAsOfLastFullLiftMovement = getOpMode().time;
                step++;
            }
            // move elevator down to position
            if(step == 2 && timeAsOfLastFullLiftMovement + 0.25 <= getOpMode().time) {
                inputSpace.sendInputToLeftHandGrabber(LeftHandGrabbingServoLocation.Action.SET_POSITION, 30);
                inputSpace.sendInputToRightHandGrabber(RightHandGrabbingServoLocation.Action.SET_POSITION, 60);
                inputSpace.sendInputToElevatorLeftLift(ElevatorLeftLiftMotorLocation.Action.SET_POSITION, 0);
                inputSpace.sendInputToElevatorRightLift(ElevatorRightLiftMotorLocation.Action.SET_POSITION, 0);
                step++;
            }
            // turn hand to the position to dispense the ball
            if(step == 3 && ((StandardMotor) inputSpace.getElevatorLeftLift().getInternalInteractionSurface()).getDcMotor().getCurrentPosition() >= -20) {
                timeAsOfLastFullLiftMovement = getOpMode().time;
                inputSpace.sendInputToHandSpinner(HandSpinningServoLocation.Action.SET_POSITION, 36);
                step++;
            }
            // turn hand back to a safe position and move elevator to turning point position
            if(step == 4 && timeAsOfLastFullLiftMovement + 2 <= getOpMode().time) {
                inputSpace.sendInputToHandSpinner(HandSpinningServoLocation.Action.SET_POSITION, 31);
                inputSpace.sendInputToElevatorLeftLift(ElevatorLeftLiftMotorLocation.Action.SET_POSITION, -500);
                inputSpace.sendInputToElevatorRightLift(ElevatorRightLiftMotorLocation.Action.SET_POSITION, -500);
                step++;
            }
            // tell hand/elevator to reset once in a safe position to do so
            if(step == 5 && ((StandardMotor) inputSpace.getElevatorLeftLift().getInternalInteractionSurface()).getDcMotor().getCurrentPosition() <= -500) {
                step = 0;
                isMovingToLBall = false;
                isMovingToBasePos = true;
            }
        }
        // enables middle level ball routine routine if requested
        if(gamepadManager.functionThreeGamepad().y && !gamepadManager.functionThreeGamepad().touchpad && !isMovingToBasePos && !isMovingToLBall && !isMovingToMBall && !isMovingToTBall && !isMovingToLBlock && !isMovingToMBlock && !isMovingToTBlock  && !isMovingToIntakePos && isAllowedToControl) {
            isMovingToMBall = true;
            step = 0;
        }
        // dispenses ball at middle level
        if(isMovingToMBall) {
            rumbleAllControllers();
            // moves hand to safe turning position
            if(step == 0) {
                inputSpace.sendInputToElevatorLeftLift(ElevatorLeftLiftMotorLocation.Action.SET_POSITION, -500);
                inputSpace.sendInputToElevatorRightLift(ElevatorRightLiftMotorLocation.Action.SET_POSITION, -500);
                timeAsOfLastFullLiftMovement = getOpMode().time;
                step++;
            }
            // once at that position, turn hand to safe position
            if(step == 1 && ((StandardMotor) inputSpace.getElevatorLeftLift().getInternalInteractionSurface()).getDcMotor().getCurrentPosition() <= -500) {
                inputSpace.sendInputToHandSpinner(HandSpinningServoLocation.Action.SET_POSITION, 33);
                timeAsOfLastFullLiftMovement = getOpMode().time;
                step++;
            }
            // move hand down to dispensing position
            if(step == 2 && timeAsOfLastFullLiftMovement + 2 <= getOpMode().time) {
                inputSpace.sendInputToLeftHandGrabber(LeftHandGrabbingServoLocation.Action.SET_POSITION, 30);
                inputSpace.sendInputToRightHandGrabber(RightHandGrabbingServoLocation.Action.SET_POSITION, 60);
                inputSpace.sendInputToElevatorLeftLift(ElevatorLeftLiftMotorLocation.Action.SET_POSITION, -350);
                inputSpace.sendInputToElevatorRightLift(ElevatorRightLiftMotorLocation.Action.SET_POSITION, -350);
                step++;
            }
            // turn hand to dispensing position
            if(step == 3 && ((StandardMotor) inputSpace.getElevatorLeftLift().getInternalInteractionSurface()).getDcMotor().getCurrentPosition() >= -350) {
                timeAsOfLastFullLiftMovement = getOpMode().time;
                inputSpace.sendInputToHandSpinner(HandSpinningServoLocation.Action.SET_POSITION, 36);
                step++;
            }
            // after ball rolls out, move to safe turning position
            if(step == 4 && timeAsOfLastFullLiftMovement + 2 <= getOpMode().time) {
                inputSpace.sendInputToHandSpinner(HandSpinningServoLocation.Action.SET_POSITION, 31);
                inputSpace.sendInputToElevatorLeftLift(ElevatorLeftLiftMotorLocation.Action.SET_POSITION, -500);
                inputSpace.sendInputToElevatorRightLift(ElevatorRightLiftMotorLocation.Action.SET_POSITION, -500);
                step++;
            }
            // reset once safe to do so
            if(step == 5 && ((StandardMotor) inputSpace.getElevatorLeftLift().getInternalInteractionSurface()).getDcMotor().getCurrentPosition() <= -500) {
                step = 0;
                isMovingToMBall = false;
                isMovingToBasePos = true;
            }
        }
        // enables top level ball routine if requested
        if(gamepadManager.functionThreeGamepad().x && !gamepadManager.functionThreeGamepad().touchpad && !isMovingToBasePos && !isMovingToLBall && !isMovingToMBall && !isMovingToTBall && !isMovingToLBlock && !isMovingToMBlock && !isMovingToTBlock && !isMovingToIntakePos && isAllowedToControl) {
            isMovingToTBall = true;
            step = 0;
        }
        // dispenses ball at top level
        if(isMovingToTBall) {
            rumbleAllControllers();
            // move to dispensing position, doesnt need to worry about safe position because its higher up
            if(step == 0) {
                inputSpace.sendInputToLeftHandGrabber(LeftHandGrabbingServoLocation.Action.SET_POSITION, 30);
                inputSpace.sendInputToRightHandGrabber(RightHandGrabbingServoLocation.Action.SET_POSITION, 60);
                inputSpace.sendInputToElevatorLeftLift(ElevatorLeftLiftMotorLocation.Action.SET_POSITION, -700);
                inputSpace.sendInputToElevatorRightLift(ElevatorRightLiftMotorLocation.Action.SET_POSITION, -700);
                timeAsOfLastFullLiftMovement = getOpMode().time;
                step++;
            }
            // turn to dispensing position once position reached
            if(step == 1 && ((StandardMotor) inputSpace.getElevatorLeftLift().getInternalInteractionSurface()).getDcMotor().getCurrentPosition() <= -700) {
                inputSpace.sendInputToHandSpinner(HandSpinningServoLocation.Action.SET_POSITION, 36);
                timeAsOfLastFullLiftMovement = getOpMode().time;
                step++;
            }
            // after ball is dispensed, reset hand because its in a safe position
            if(step == 2 && timeAsOfLastFullLiftMovement + 4 <= getOpMode().time) {
                step = 0;
                isMovingToTBall = false;
                isMovingToBasePos = true;
            }
        }
        // enables bottom level block routine if requested
        if(gamepadManager.functionThreeGamepad().b && gamepadManager.functionThreeGamepad().touchpad && !isMovingToBasePos && !isMovingToLBall && !isMovingToMBall && !isMovingToTBall && !isMovingToLBlock && !isMovingToMBlock && !isMovingToTBlock && !isMovingToIntakePos && isAllowedToControl) {
            isMovingToLBlock = true;
            step = 0;
        }
        // dispenses block at bottom
        if(isMovingToLBlock) {
            rumbleAllControllers();
            // move the elevator to allow hand room to turn
            if(step == 0) {
                inputSpace.sendInputToElevatorLeftLift(ElevatorLeftLiftMotorLocation.Action.SET_POSITION, -500);
                inputSpace.sendInputToElevatorRightLift(ElevatorRightLiftMotorLocation.Action.SET_POSITION, -500);
                timeAsOfLastFullLiftMovement = getOpMode().time;
                step++;
            }
            // turn hand to safest position once elevator reaches its position
            if(step == 1 && ((StandardMotor) inputSpace.getElevatorLeftLift().getInternalInteractionSurface()).getDcMotor().getCurrentPosition() <= -500) {
                inputSpace.sendInputToHandSpinner(HandSpinningServoLocation.Action.SET_POSITION, 33);
                timeAsOfLastFullLiftMovement = getOpMode().time;
                step++;
            }
            // move elevator down to position
            if(step == 2 && timeAsOfLastFullLiftMovement + 0.25 <= getOpMode().time) {
                inputSpace.sendInputToLeftHandGrabber(LeftHandGrabbingServoLocation.Action.SET_POSITION, 30);
                inputSpace.sendInputToRightHandGrabber(RightHandGrabbingServoLocation.Action.SET_POSITION, 60);
                inputSpace.sendInputToElevatorLeftLift(ElevatorLeftLiftMotorLocation.Action.SET_POSITION, -150);
                inputSpace.sendInputToElevatorRightLift(ElevatorRightLiftMotorLocation.Action.SET_POSITION, -150);
                step++;
            }
            // turn hand to the position to dispense the ball
            if(step == 3 && ((StandardMotor) inputSpace.getElevatorLeftLift().getInternalInteractionSurface()).getDcMotor().getCurrentPosition() >= -150) {
                timeAsOfLastFullLiftMovement = getOpMode().time;
                inputSpace.sendInputToHandSpinner(HandSpinningServoLocation.Action.SET_POSITION, 38);
                step++;
            }
            // turn hand back to a safe position and move elevator to turning point position
            if(step == 4 && timeAsOfLastFullLiftMovement + 2 <= getOpMode().time) {
                inputSpace.sendInputToHandSpinner(HandSpinningServoLocation.Action.SET_POSITION, 31);
                inputSpace.sendInputToElevatorLeftLift(ElevatorLeftLiftMotorLocation.Action.SET_POSITION, -500);
                inputSpace.sendInputToElevatorRightLift(ElevatorRightLiftMotorLocation.Action.SET_POSITION, -500);
                step++;
            }
            // tell hand/elevator to reset once in a safe position to do so
            if(step == 5 && ((StandardMotor) inputSpace.getElevatorLeftLift().getInternalInteractionSurface()).getDcMotor().getCurrentPosition() <= -500) {
                step = 0;
                isMovingToLBlock = false;
                isMovingToBasePos = true;
            }
        }
        // enables middle level block routine if requested
        if(gamepadManager.functionThreeGamepad().y && gamepadManager.functionThreeGamepad().touchpad && !isMovingToBasePos && !isMovingToLBall && !isMovingToMBall && !isMovingToTBall && !isMovingToLBlock && !isMovingToMBlock && !isMovingToTBlock && !isMovingToIntakePos && isAllowedToControl) {
            isMovingToMBlock = true;
            step = 0;
        }
        // dispenses block at middle
        if(isMovingToMBlock) {
            rumbleAllControllers();
            // move the elevator to dropping position
            if(step == 0) {
                inputSpace.sendInputToElevatorLeftLift(ElevatorLeftLiftMotorLocation.Action.SET_POSITION, -575);
                inputSpace.sendInputToElevatorRightLift(ElevatorRightLiftMotorLocation.Action.SET_POSITION, -575);
                inputSpace.sendInputToLeftHandGrabber(LeftHandGrabbingServoLocation.Action.SET_POSITION, 30);
                inputSpace.sendInputToRightHandGrabber(RightHandGrabbingServoLocation.Action.SET_POSITION, 60);
                timeAsOfLastFullLiftMovement = getOpMode().time;
                step++;
            }
            // turn hand to down position once elevator reaches its position
            if(step == 1 && ((StandardMotor) inputSpace.getElevatorLeftLift().getInternalInteractionSurface()).getDcMotor().getCurrentPosition() <= -575) {
                inputSpace.sendInputToHandSpinner(HandSpinningServoLocation.Action.SET_POSITION, 40);
                timeAsOfLastFullLiftMovement = getOpMode().time;
                step++;
            }
            // tell hand/elevator to reset after block is dispensed
            if(step == 2 && timeAsOfLastFullLiftMovement + 4 <= getOpMode().time) {
                step = 0;
                isMovingToMBlock = false;
                isMovingToBasePos = true;
            }
        }
        // enables top level block routine if requested
        if(gamepadManager.functionThreeGamepad().x && gamepadManager.functionThreeGamepad().touchpad && !isMovingToBasePos && !isMovingToLBall && !isMovingToMBall && !isMovingToTBall && !isMovingToLBlock && !isMovingToMBlock && !isMovingToTBlock && !isMovingToIntakePos && isAllowedToControl) {
            isMovingToTBlock = true;
            step = 0;
        }
        // dispenses block at top
        if(isMovingToTBlock) {
            rumbleAllControllers();
            // move the elevator to dropping position
            if(step == 0) {
                inputSpace.sendInputToElevatorLeftLift(ElevatorLeftLiftMotorLocation.Action.SET_POSITION, -1000);
                inputSpace.sendInputToElevatorRightLift(ElevatorRightLiftMotorLocation.Action.SET_POSITION, -1000);
                inputSpace.sendInputToLeftHandGrabber(LeftHandGrabbingServoLocation.Action.SET_POSITION, 30);
                inputSpace.sendInputToRightHandGrabber(RightHandGrabbingServoLocation.Action.SET_POSITION, 60);
                timeAsOfLastFullLiftMovement = getOpMode().time;
                step++;
            }
            // turn hand to down position once elevator reaches its position
            if(step == 1 && ((StandardMotor) inputSpace.getElevatorLeftLift().getInternalInteractionSurface()).getDcMotor().getCurrentPosition() <= -1000) {
                inputSpace.sendInputToHandSpinner(HandSpinningServoLocation.Action.SET_POSITION, 40);
                timeAsOfLastFullLiftMovement = getOpMode().time;
                step++;
            }
            // tell hand/elevator to reset after block is dispensed
            if(step == 2 && timeAsOfLastFullLiftMovement + 4 <= getOpMode().time) {
                step = 0;
                isMovingToTBlock = false;
                isMovingToBasePos = true;
            }
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
