package org.firstinspires.ftc.teamcode.main.utils.autonomous.starting;

import androidx.annotation.NonNull;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.main.utils.autonomous.EncoderTimeoutManager;
import org.firstinspires.ftc.teamcode.main.utils.autonomous.image.ImgProc;
import org.firstinspires.ftc.teamcode.main.utils.autonomous.location.pipeline.PositionSystem;
import org.firstinspires.ftc.teamcode.main.utils.helpers.elevator.ElevatorDriver;
import org.firstinspires.ftc.teamcode.main.utils.helpers.geometry.Angle;
import org.firstinspires.ftc.teamcode.main.utils.interactions.groups.StandardTankVehicleDrivetrain;
import org.firstinspires.ftc.teamcode.main.utils.interactions.items.StandardMotor;
import org.firstinspires.ftc.teamcode.main.utils.io.InputSpace;
import org.firstinspires.ftc.teamcode.main.utils.io.OutputSpace;
import org.firstinspires.ftc.teamcode.main.utils.locations.*;
import org.firstinspires.ftc.teamcode.main.utils.resources.Resources;

public class StartingPositionManager {
    PositionSystem positionSystem;
    EncoderTimeoutManager encoderTimeout;
    LinearOpMode opMode;
    StandardTankVehicleDrivetrain tank;
    InputSpace input;
    OutputSpace output;
    int ballDropHeight;
    boolean intakeShouldBeDown = false;
    ImgProc imgProc;
    ElevatorDriver elevatorDriver;

    // this should be true if the camera is upside down in real life (it wont work as well upside down, but this provides some functionality)
    boolean isCameraUpsideDown = false;

    // this should be true if a block is loaded, false if a ball
    boolean isBlock = true;

    int h = 0;

    int step = 0;

    boolean isBlueSide, isCloseToParking;

    public StartingPositionManager(@NonNull LinearOpMode opMode, boolean isBlueSide, boolean isCloseToParking, int ballDropHeight, boolean simple) throws InterruptedException {
        this.opMode = opMode;
        this.isBlueSide = isBlueSide;
        this.isCloseToParking = isCloseToParking;
        this.ballDropHeight = ballDropHeight;
        positionSystem = Resources.Navigation.Sensors.getPositionSystem(opMode.hardwareMap);

        input = new InputSpace(opMode.hardwareMap);
        output = new OutputSpace(opMode.hardwareMap);
        tank = (StandardTankVehicleDrivetrain) input.getTank().getInternalInteractionSurface();
        elevatorDriver = new ElevatorDriver(input, output, opMode);
        positionSystem.setDrivetrain(tank);

        if (!simple) {
            imgProc = new ImgProc(opMode.hardwareMap, new String[]{"Duck", "Marker"}, "FreightFrenzy_DM.tflite");
            imgProc.confidence = 0.55f;
            imgProc.init();
            imgProc.activate();
            imgProc.setZoom(1.5, 16.0/9);

            while (h == 0) {
                try {
                    h = initialPositionsOrientation(imgProc.identifyStartingPos());
                }catch(Exception e) {
                    h = 2;
                }
            }
            this.ballDropHeight = h;
        }

        input.sendInputToLeftHandGrabber(LeftHandGrabbingServoLocation.Action.SET_POSITION, 90);
        input.sendInputToRightHandGrabber(RightHandGrabbingServoLocation.Action.SET_POSITION, 37);
        input.sendInputToHandSpinner(HandSpinningServoLocation.Action.SET_POSITION, 23);
        input.sendInputToIntakeLifter(IntakeLiftingServoLocation.Action.SET_POSITION, 70);

        if(!simple) {
            int turnModifier = 1;
            if (!isBlueSide) turnModifier = -turnModifier;

            opMode.waitForStart();

            opMode.telemetry.addAction(() -> opMode.telemetry.addData("Detected Position", h));

            opMode.telemetry.update();

            encoderTimeout = new EncoderTimeoutManager(0);

            // Drop the intake
            toggleIntakeLifter();

            int initialTurnModifier = turnModifier;
            if (!isCloseToParking) initialTurnModifier = -initialTurnModifier;

            while (opMode.opModeIsActive()) {
                if (step == 0) {
                    positionSystem.encoderDrive(5);
                }

                if (step == 1) {
                    positionSystem.turnWithCorrection(new Angle(-90 * initialTurnModifier, Angle.AngleUnit.DEGREE));
                }

                if (step == 2) {
                    positionSystem.encoderDrive(-12);
                }

                if (step == 3) {
                    positionSystem.turnWithCorrection(new Angle(-90 * initialTurnModifier, Angle.AngleUnit.DEGREE));
                }

                if (step == 4) {
                    positionSystem.encoderDrive(-5, 5, 30);
                }

                if (step == 5) {
                    runElevator(0);
                }

                if (step == 6) {
                    toggleIntakeLifter();
                    positionSystem.encoderDrive(-2.1);
                }

                while (opMode.opModeIsActive() && positionSystem.areMotorsBusy()) {}
                if (opMode.isStopRequested()) { break; }
                step++;
            }

        }else{
            opMode.waitForStart();
            encoderTimeout = new EncoderTimeoutManager(0);
            positionSystem.encoderDrive(-8);
            toggleIntakeLifter();
            drivetrainHold();
            elevatorDriver.setPosition(2, isBlock);
            while(!elevatorDriver.isStable()) {
                elevatorDriver.run();
            }
            toggleIntakeLifter();
            opMode.sleep(4000);
            positionSystem.encoderDrive(8);
            drivetrainHold();
        }
    }

    private void runElevator(double finalDist) throws InterruptedException {
        boolean hasDriven = false;

        elevatorDriver.setPosition(h, isBlock);

        while (!elevatorDriver.isStable()) {
            // controlEntireLiftAutonomously(ballDropHeight); // DEPRECATED IN FAVOR OF
            //                                                   ElevatorDriver.runToHeight
            elevatorDriver.run();
            if (elevatorDriver.isResettingToOriginalPos() && !hasDriven) {
                elevatorDriver.setPosition(h, isBlock);

                positionSystem.encoderDrive(finalDist);
                resetTimer();
                hasDriven = true;
            }

            if (opMode.isStopRequested()) {
                positionSystem.getDrivetrain().brake();
                throw new InterruptedException();
            }
        }
    }

    private void runElevator() throws InterruptedException {
        runElevator(2.1);
    }

    private void drivetrainHold() throws InterruptedException {
        resetTimer();

        while (positionSystem.areMotorsBusy() && !encoderTimeout.hasTimedOut() && opMode.opModeIsActive()) {
            opMode.telemetry.addData("Motors busy for", encoderTimeout.getOperationTime());
            opMode.telemetry.update();

            if (opMode.isStopRequested()) {
                positionSystem.getDrivetrain().brake();
                throw new InterruptedException();
            }
        }

        opMode.telemetry.clear();

        positionSystem.getDrivetrain().brake();
        encoderTimeout.restart();
    }

    private void resetTimer() {
        encoderTimeout.restart();
        encoderTimeout.durationMillis = 5000;
    }

    @Deprecated
    private void calibrateElevator() {
        // move elevator up for a second
        int timeAsOfLastElevatorCalibrationBegin = (int) opMode.time;
        while(output.receiveOutputFromElevatorBottomLimitSwitch(ElevatorBottomLimitSwitchLocation.Values.PRESSED) == 0 && timeAsOfLastElevatorCalibrationBegin > (int) opMode.time - 1) {
            input.sendInputToElevatorLeftLift(ElevatorLeftLiftMotorLocation.Action.SET_SPEED, -100);
            input.sendInputToElevatorRightLift(ElevatorRightLiftMotorLocation.Action.SET_SPEED, -100);
        }
        // move elevator down until it reaches the bottom
        while(output.receiveOutputFromElevatorBottomLimitSwitch(ElevatorBottomLimitSwitchLocation.Values.PRESSED) == 0) {
            input.sendInputToElevatorLeftLift(ElevatorLeftLiftMotorLocation.Action.SET_SPEED, 30);
            input.sendInputToElevatorRightLift(ElevatorRightLiftMotorLocation.Action.SET_SPEED, 30);
        }
        // reset the elevator and hand
        ((StandardMotor) input.getElevatorLeftLift().getInternalInteractionSurface()).reset();
        ((StandardMotor) input.getElevatorRightLift().getInternalInteractionSurface()).reset();
        input.sendInputToHandSpinner(HandSpinningServoLocation.Action.SET_POSITION, 23);
    }

    @Deprecated
    private void calibrateIntake() {
        // move the intake to the *UPPER* position
        input.sendInputToIntakeLifter(IntakeLiftingServoLocation.Action.SET_POSITION, 70);
    }

    private void toggleIntakeLifter() {
        // move the intake based on the left bumper's state
        intakeShouldBeDown = !intakeShouldBeDown;
        if(intakeShouldBeDown) {
            input.sendInputToIntakeLifter(IntakeLiftingServoLocation.Action.SET_POSITION, 30);
        }else{
            input.sendInputToIntakeLifter(IntakeLiftingServoLocation.Action.SET_POSITION, 70);
        }
    }

    private void setIntakeSpeed(int speed) {
        input.sendInputToIntakeSpinner(IntakeSpinningMotorLocation.Action.SET_SPEED, speed);
    }

    private int initialPositionsOrientation(int raw) {
        if (isCameraUpsideDown) {
            if (raw == 1) {
                raw = 3;
            }
            else if (raw == 3) {
                raw = 1;
            }
        }
        return raw;
    }
}
