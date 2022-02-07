package org.firstinspires.ftc.teamcode.main.utils.autonomous.starting;

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

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

public class StartingPositionManager {
    PositionSystem positionSystem;
    EncoderTimeoutManager encoderTimeout;
    LinearOpMode opMode;
    StandardTankVehicleDrivetrain tank;
    InputSpace input;
    OutputSpace output;
    int ballDropHeight;
    double timeAsOfLastFullLiftMovement = 0;
    int step = 0;
    boolean intakeShouldBeDown = false, liftAutoMovementIsDone = false, liftIsMovingDown = false, robotIsMovingBackToTurningPositionAfterLiftMovement = false;
    boolean isMovingToLBall = false, isMovingToMBall = false, isMovingToTBall = false, isMovingToLBlock = false, isMovingToMBlock = false, isMovingToTBlock = false, isMovingToBasePos = false, isMovingToIntakePos = false;
    ImgProc imgProc;
    AtomicBoolean readyForElevator = new AtomicBoolean(false);
    ElevatorDriver elevatorDriver;

    // this should be true if the camera is upside down in real life (it wont work as well upside down, but this provides some functionality)
    boolean isCameraUpsideDown = false;

    // this should be true if a block is loaded, false if a ball
    boolean isBlock = true;

    int h = 0;

    boolean isBlueSide, isCloseToParking;

    public StartingPositionManager(LinearOpMode opMode, boolean isBlueSide, boolean isCloseToParking, int ballDropHeight) {
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

        imgProc = new ImgProc(opMode.hardwareMap, new String[]{"Duck", "Marker"}, "FreightFrenzy_DM.tflite");
        imgProc.init();
        imgProc.activate();
        imgProc.setZoom(1, 16.0/9);

        while (h == 0) {
            h = initialPositionsOrientation(imgProc.identifyStartingPos());
        }
        this.ballDropHeight = h;

        int turnModifier = 1;
        if (!isBlueSide) turnModifier = -turnModifier;
        int finalTurnModifier = turnModifier;

        opMode.waitForStart();

        opMode.telemetry.addAction(() -> opMode.telemetry.addLine("github.com/michaell4438"));

        encoderTimeout = new EncoderTimeoutManager(0);

        // Drop the intake
        toggleIntakeLifter();

        if (!isCloseToParking) {
            // Move Forward 1 Tile
            positionSystem.encoderDrive(15);
            drivetrainHold();
            // Turn counter-clockwise 135 degrees
            positionSystem.turnWithCorrection(new Angle(135 * finalTurnModifier, Angle.AngleUnit.DEGREE));
            drivetrainHold();
            // Drive Back two inches
            positionSystem.encoderDrive(-1.5);
            drivetrainHold();

            runElevator();
            drivetrainHold();

            // Drive forward 4 inches
            positionSystem.encoderDrive(3);
            drivetrainHold();
            // Turn counter-clockwise 33 degrees
            positionSystem.turnWithCorrection(new Angle(33 * finalTurnModifier, Angle.AngleUnit.DEGREE));
            drivetrainHold();
            // Turn clockwise 135 degrees
            positionSystem.turnWithCorrection(new Angle(-135 * finalTurnModifier, Angle.AngleUnit.DEGREE));
            drivetrainHold();
            // Raise the intake
            toggleIntakeLifter();
            drivetrainHold();
            // Go backward 1 tile
            positionSystem.encoderDrive(-15);
            drivetrainHold();
        }
        else {
            // Move Forward 1 Tile
            positionSystem.encoderDrive(15);
            drivetrainHold();
            // Turn clockwise 135 degrees
            positionSystem.turnWithCorrection(new Angle(-135 * finalTurnModifier, Angle.AngleUnit.DEGREE));
            drivetrainHold();
            // Move Back 2 Inches
            positionSystem.encoderDrive(-1.5);
            drivetrainHold();

            runElevator();
            drivetrainHold();

            // Turn counter-clockwise 33 degrees and raise intake
            positionSystem.turnWithCorrection(new Angle(33 * finalTurnModifier, Angle.AngleUnit.DEGREE));
            drivetrainHold();

            toggleIntakeLifter();
            // Drive One Tile
            positionSystem.encoderDrive(13);
            drivetrainHold();
        }
    }

    private void runElevator() {
        boolean hasDriven = false;

        elevatorDriver.setPosition(h, isBlock);

        while (!elevatorDriver.isStable()) {
            // controlEntireLiftAutonomously(ballDropHeight); // DEPRECATED IN FAVOR OF
            //                                                   ElevatorDriver.runToHeight
            elevatorDriver.run();
            if (elevatorDriver.isResettingToOriginalPos() && !hasDriven) {
                elevatorDriver.setPosition(h, isBlock);

                // Go forward 3 if we are ready to move
                positionSystem.encoderDrive(3);
                resetTimer();
                hasDriven = true;
            }
        }
    }

    private void encoderDriveAndReadyElevator(int distance) {
        positionSystem.encoderDrive(distance);
        readyForElevator.set(true);
    }

    private void drivetrainHold() {
        resetTimer();

        while (positionSystem.areMotorsBusy() && !encoderTimeout.hasTimedOut() && opMode.opModeIsActive()) {
            opMode.telemetry.addData("Motors busy for", encoderTimeout.getOperationTime());
            opMode.telemetry.update();
        }

        positionSystem.getDrivetrain().brake();
        encoderTimeout.restart();
    }

    private boolean isDrivetrainReady() {
        return !positionSystem.areMotorsBusy() && encoderTimeout.hasTimedOut() && opMode.opModeIsActive();
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
            input.sendInputToIntakeLifter(IntakeLiftingServoLocation.Action.SET_POSITION, 27);
        }else{
            input.sendInputToIntakeLifter(IntakeLiftingServoLocation.Action.SET_POSITION, 60);
        }
    }

    private void setIntakeSpeed(int speed) {
        input.sendInputToIntakeSpinner(IntakeSpinningMotorLocation.Action.SET_SPEED, speed);
    }

    @Deprecated
    private void controlEntireLiftAutonomously(int h) {
        // enables intake pos routine if requested
        if(!isMovingToBasePos && !isMovingToLBall && !isMovingToMBall && !isMovingToTBall && !isMovingToLBlock && !isMovingToMBlock && !isMovingToTBlock && !isMovingToIntakePos) {
            isMovingToIntakePos = true;
            step = 0;
        }
        if(isMovingToIntakePos) {
            
            // sets the hand to base position
            if(step == 0) {
                input.sendInputToHandSpinner(HandSpinningServoLocation.Action.SET_POSITION, 23);
                timeAsOfLastFullLiftMovement = opMode.time;
                step++;
            }
            // after moving the hand, move the elevator to the base position
            if(step == 1) {
                if(output.receiveOutputFromElevatorBottomLimitSwitch(ElevatorBottomLimitSwitchLocation.Values.PRESSED) != 0) {
                    step++;
                }else if(timeAsOfLastFullLiftMovement + 1.5 <= opMode.time) {
                    input.sendInputToElevatorLeftLift(ElevatorLeftLiftMotorLocation.Action.SET_SPEED, 40);
                    input.sendInputToElevatorRightLift(ElevatorRightLiftMotorLocation.Action.SET_SPEED, 40);
                    step++;
                }
            }
            // once the elevator is at the bottom, reset it
            if(step == 2 && output.receiveOutputFromElevatorBottomLimitSwitch(ElevatorBottomLimitSwitchLocation.Values.PRESSED) != 0) {
                input.sendInputToElevatorLeftLift(ElevatorLeftLiftMotorLocation.Action.SET_SPEED, 0);
                input.sendInputToElevatorRightLift(ElevatorRightLiftMotorLocation.Action.SET_SPEED, 0);
                ((StandardMotor) input.getElevatorLeftLift().getInternalInteractionSurface()).reset();
                ((StandardMotor) input.getElevatorRightLift().getInternalInteractionSurface()).reset();
                step++;
            }
            // once at base, move the hand to the intake position
            if(step == 3) {
                input.sendInputToHandSpinner(HandSpinningServoLocation.Action.SET_POSITION, 20);
                input.sendInputToLeftHandGrabber(LeftHandGrabbingServoLocation.Action.SET_POSITION, 30);
                input.sendInputToRightHandGrabber(RightHandGrabbingServoLocation.Action.SET_POSITION, 60);
                step++;
            }
            if(step == 4 && output.receiveOutputFromHandDistanceSensor() <= 120) {
                timeAsOfLastFullLiftMovement = opMode.time;
                step++;
            }
            // once ball is in place, move to base position
            if(step == 5 && timeAsOfLastFullLiftMovement + 0.5 <= opMode.time) {
                step = 0;
                isMovingToIntakePos = false;
                isMovingToBasePos = true;
            }
        }
        // moves to base pos - this is not a routine that can be enabled by user input, but rather enabled by other routines to reset them after use
        if(isMovingToBasePos) {
            
            // sets the hand to base position
            if(step == 0) {
                input.sendInputToLeftHandGrabber(LeftHandGrabbingServoLocation.Action.SET_POSITION, 55);
                input.sendInputToRightHandGrabber(RightHandGrabbingServoLocation.Action.SET_POSITION, 30);
                input.sendInputToHandSpinner(HandSpinningServoLocation.Action.SET_POSITION, 23);
                timeAsOfLastFullLiftMovement = opMode.time;
                step++;
            }
            // after moving the hand, move the elevator to the base position
            if(step == 1) {
                if(output.receiveOutputFromElevatorBottomLimitSwitch(ElevatorBottomLimitSwitchLocation.Values.PRESSED) != 0) {
                    step++;
                }else if(timeAsOfLastFullLiftMovement + 1.5 <= opMode.time) {
                    input.sendInputToElevatorLeftLift(ElevatorLeftLiftMotorLocation.Action.SET_SPEED, 40);
                    input.sendInputToElevatorRightLift(ElevatorRightLiftMotorLocation.Action.SET_SPEED, 40);
                    step++;
                }
            }
            // once the elevator is at the bottom, reset it
            if(step == 2 && output.receiveOutputFromElevatorBottomLimitSwitch(ElevatorBottomLimitSwitchLocation.Values.PRESSED) != 0) {
                input.sendInputToElevatorLeftLift(ElevatorLeftLiftMotorLocation.Action.SET_SPEED, 0);
                input.sendInputToElevatorRightLift(ElevatorRightLiftMotorLocation.Action.SET_SPEED, 0);
                ((StandardMotor) input.getElevatorLeftLift().getInternalInteractionSurface()).reset();
                ((StandardMotor) input.getElevatorRightLift().getInternalInteractionSurface()).reset();
                isMovingToBasePos = false;
                step = 0;
                liftAutoMovementIsDone = true;
            }
        }
        // enables lower level ball routine if requested
        if(!isMovingToBasePos && !isMovingToLBall && !isMovingToMBall && !isMovingToTBall && !isMovingToLBlock && !isMovingToMBlock && !isMovingToTBlock  && !isMovingToIntakePos) {
            isMovingToLBall = true;
            step = 0;
        }
        // dispenses ball at lower level
        if(isMovingToLBall) {
            
            // move the elevator to allow hand room to turn
            if(step == 0) {
                input.sendInputToElevatorLeftLift(ElevatorLeftLiftMotorLocation.Action.SET_POSITION, -500);
                input.sendInputToElevatorRightLift(ElevatorRightLiftMotorLocation.Action.SET_POSITION, -500);
                timeAsOfLastFullLiftMovement = opMode.time;
                step++;
            }
            // turn hand to safest position once elevator reaches its position
            if(step == 1 && ((StandardMotor) input.getElevatorLeftLift().getInternalInteractionSurface()).getDcMotor().getCurrentPosition() <= -500) {
                input.sendInputToHandSpinner(HandSpinningServoLocation.Action.SET_POSITION, 33);
                timeAsOfLastFullLiftMovement = opMode.time;
                step++;
            }
            // move elevator down to position
            if(step == 2 && timeAsOfLastFullLiftMovement + 0.25 <= opMode.time) {
                input.sendInputToLeftHandGrabber(LeftHandGrabbingServoLocation.Action.SET_POSITION, 30);
                input.sendInputToRightHandGrabber(RightHandGrabbingServoLocation.Action.SET_POSITION, 60);
                input.sendInputToElevatorLeftLift(ElevatorLeftLiftMotorLocation.Action.SET_POSITION, 0);
                input.sendInputToElevatorRightLift(ElevatorRightLiftMotorLocation.Action.SET_POSITION, 0);
                step++;
            }
            // turn hand to the position to dispense the ball
            if(step == 3 && ((StandardMotor) input.getElevatorLeftLift().getInternalInteractionSurface()).getDcMotor().getCurrentPosition() >= -20) {
                timeAsOfLastFullLiftMovement = opMode.time;
                input.sendInputToHandSpinner(HandSpinningServoLocation.Action.SET_POSITION, 36);
                step++;
            }
            // turn hand back to a safe position and move elevator to turning point position
            if(step == 4 && timeAsOfLastFullLiftMovement + 2 <= opMode.time) {
                input.sendInputToHandSpinner(HandSpinningServoLocation.Action.SET_POSITION, 31);
                input.sendInputToElevatorLeftLift(ElevatorLeftLiftMotorLocation.Action.SET_POSITION, -500);
                input.sendInputToElevatorRightLift(ElevatorRightLiftMotorLocation.Action.SET_POSITION, -500);
                step++;
            }
            // tell hand/elevator to reset once in a safe position to do so
            if(step == 5 && ((StandardMotor) input.getElevatorLeftLift().getInternalInteractionSurface()).getDcMotor().getCurrentPosition() <= -500) {
                step = 0;
                isMovingToLBall = false;
                isMovingToBasePos = true;
                liftAutoMovementIsDone = true;
            }
        }
        // enables middle level ball routine routine if requested
        if(!isMovingToBasePos && !isMovingToLBall && !isMovingToMBall && !isMovingToTBall && !isMovingToLBlock && !isMovingToMBlock && !isMovingToTBlock  && !isMovingToIntakePos) {
            isMovingToMBall = true;
            step = 0;
        }
        // dispenses ball at middle level
        if(isMovingToMBall) {
            
            // moves hand to safe turning position
            if(step == 0) {
                input.sendInputToElevatorLeftLift(ElevatorLeftLiftMotorLocation.Action.SET_POSITION, -500);
                input.sendInputToElevatorRightLift(ElevatorRightLiftMotorLocation.Action.SET_POSITION, -500);
                timeAsOfLastFullLiftMovement = opMode.time;
                step++;
            }
            // once at that position, turn hand to safe position
            if(step == 1 && ((StandardMotor) input.getElevatorLeftLift().getInternalInteractionSurface()).getDcMotor().getCurrentPosition() <= -500) {
                input.sendInputToHandSpinner(HandSpinningServoLocation.Action.SET_POSITION, 33);
                timeAsOfLastFullLiftMovement = opMode.time;
                step++;
            }
            // move hand down to dispensing position
            if(step == 2 && timeAsOfLastFullLiftMovement + 2 <= opMode.time) {
                input.sendInputToLeftHandGrabber(LeftHandGrabbingServoLocation.Action.SET_POSITION, 30);
                input.sendInputToRightHandGrabber(RightHandGrabbingServoLocation.Action.SET_POSITION, 60);
                input.sendInputToElevatorLeftLift(ElevatorLeftLiftMotorLocation.Action.SET_POSITION, -350);
                input.sendInputToElevatorRightLift(ElevatorRightLiftMotorLocation.Action.SET_POSITION, -350);
                step++;
            }
            // turn hand to dispensing position
            if(step == 3 && ((StandardMotor) input.getElevatorLeftLift().getInternalInteractionSurface()).getDcMotor().getCurrentPosition() >= -350) {
                timeAsOfLastFullLiftMovement = opMode.time;
                input.sendInputToHandSpinner(HandSpinningServoLocation.Action.SET_POSITION, 36);
                step++;
            }
            // after ball rolls out, move to safe turning position
            if(step == 4 && timeAsOfLastFullLiftMovement + 2 <= opMode.time) {
                input.sendInputToHandSpinner(HandSpinningServoLocation.Action.SET_POSITION, 31);
                input.sendInputToElevatorLeftLift(ElevatorLeftLiftMotorLocation.Action.SET_POSITION, -500);
                input.sendInputToElevatorRightLift(ElevatorRightLiftMotorLocation.Action.SET_POSITION, -500);
                step++;
            }
            // reset once safe to do so
            if(step == 5 && ((StandardMotor) input.getElevatorLeftLift().getInternalInteractionSurface()).getDcMotor().getCurrentPosition() <= -500) {
                step = 0;
                isMovingToMBall = false;
                isMovingToBasePos = true;
                liftAutoMovementIsDone = true;
            }
        }
        // enables top level ball routine if requested
        if(!isMovingToBasePos && !isMovingToLBall && !isMovingToMBall && !isMovingToTBall && !isMovingToLBlock && !isMovingToMBlock && !isMovingToTBlock && !isMovingToIntakePos) {
            isMovingToTBall = true;
            step = 0;
        }
        // dispenses ball at top level
        if(isMovingToTBall) {
            
            // move to dispensing position, doesnt need to worry about safe position because its higher up
            if(step == 0) {
                input.sendInputToLeftHandGrabber(LeftHandGrabbingServoLocation.Action.SET_POSITION, 30);
                input.sendInputToRightHandGrabber(RightHandGrabbingServoLocation.Action.SET_POSITION, 60);
                input.sendInputToElevatorLeftLift(ElevatorLeftLiftMotorLocation.Action.SET_POSITION, -700);
                input.sendInputToElevatorRightLift(ElevatorRightLiftMotorLocation.Action.SET_POSITION, -700);
                timeAsOfLastFullLiftMovement = opMode.time;
                step++;
            }
            // turn to dispensing position once position reached
            if(step == 1 && ((StandardMotor) input.getElevatorLeftLift().getInternalInteractionSurface()).getDcMotor().getCurrentPosition() <= -700) {
                input.sendInputToHandSpinner(HandSpinningServoLocation.Action.SET_POSITION, 36);
                timeAsOfLastFullLiftMovement = opMode.time;
                step++;
            }
            // after ball is dispensed, reset hand because its in a safe position
            if(step == 2 && timeAsOfLastFullLiftMovement + 3 <= opMode.time) {
                step = 0;
                isMovingToTBall = false;
                isMovingToBasePos = true;
                liftAutoMovementIsDone = true;
            }
        }
        // enables bottom level block routine if requested
        if(!isMovingToBasePos && !isMovingToLBall && !isMovingToMBall && !isMovingToTBall && !isMovingToLBlock && !isMovingToMBlock && !isMovingToTBlock && !isMovingToIntakePos) {
            isMovingToLBlock = true;
            step = 0;
        }
        // dispenses block at bottom
        if(isMovingToLBlock) {
            
            // move the elevator to allow hand room to turn
            if(step == 0) {
                input.sendInputToElevatorLeftLift(ElevatorLeftLiftMotorLocation.Action.SET_POSITION, -500);
                input.sendInputToElevatorRightLift(ElevatorRightLiftMotorLocation.Action.SET_POSITION, -500);
                timeAsOfLastFullLiftMovement = opMode.time;
                step++;
            }
            // turn hand to safest position once elevator reaches its position
            if(step == 1 && ((StandardMotor) input.getElevatorLeftLift().getInternalInteractionSurface()).getDcMotor().getCurrentPosition() <= -500) {
                input.sendInputToHandSpinner(HandSpinningServoLocation.Action.SET_POSITION, 33);
                timeAsOfLastFullLiftMovement = opMode.time;
                step++;
            }
            // move elevator down to position
            if(step == 2 && timeAsOfLastFullLiftMovement + 0.25 <= opMode.time) {
                input.sendInputToLeftHandGrabber(LeftHandGrabbingServoLocation.Action.SET_POSITION, 30);
                input.sendInputToRightHandGrabber(RightHandGrabbingServoLocation.Action.SET_POSITION, 60);
                input.sendInputToElevatorLeftLift(ElevatorLeftLiftMotorLocation.Action.SET_POSITION, -150);
                input.sendInputToElevatorRightLift(ElevatorRightLiftMotorLocation.Action.SET_POSITION, -150);
                step++;
            }
            // turn hand to the position to dispense the ball
            if(step == 3 && ((StandardMotor) input.getElevatorLeftLift().getInternalInteractionSurface()).getDcMotor().getCurrentPosition() >= -150) {
                timeAsOfLastFullLiftMovement = opMode.time;
                input.sendInputToHandSpinner(HandSpinningServoLocation.Action.SET_POSITION, 38);
                step++;
            }
            // turn hand back to a safe position and move elevator to turning point position
            if(step == 4 && timeAsOfLastFullLiftMovement + 2 <= opMode.time) {
                input.sendInputToHandSpinner(HandSpinningServoLocation.Action.SET_POSITION, 31);
                input.sendInputToElevatorLeftLift(ElevatorLeftLiftMotorLocation.Action.SET_POSITION, -500);
                input.sendInputToElevatorRightLift(ElevatorRightLiftMotorLocation.Action.SET_POSITION, -500);
                step++;
            }
            // tell hand/elevator to reset once in a safe position to do so
            if(step == 5 && ((StandardMotor) input.getElevatorLeftLift().getInternalInteractionSurface()).getDcMotor().getCurrentPosition() <= -500) {
                step = 0;
                isMovingToLBlock = false;
                isMovingToBasePos = true;
                liftAutoMovementIsDone = true;
            }
        }
        // enables middle level block routine if requested
        if(!isMovingToBasePos && !isMovingToLBall && !isMovingToMBall && !isMovingToTBall && !isMovingToLBlock && !isMovingToMBlock && !isMovingToTBlock && !isMovingToIntakePos) {
            isMovingToMBlock = true;
            step = 0;
        }
        // dispenses block at middle
        if(isMovingToMBlock) {
            
            // move the elevator to dropping position
            if(step == 0) {
                input.sendInputToElevatorLeftLift(ElevatorLeftLiftMotorLocation.Action.SET_POSITION, -575);
                input.sendInputToElevatorRightLift(ElevatorRightLiftMotorLocation.Action.SET_POSITION, -575);
                input.sendInputToLeftHandGrabber(LeftHandGrabbingServoLocation.Action.SET_POSITION, 30);
                input.sendInputToRightHandGrabber(RightHandGrabbingServoLocation.Action.SET_POSITION, 60);
                timeAsOfLastFullLiftMovement = opMode.time;
                step++;
            }
            // turn hand to down position once elevator reaches its position
            if(step == 1 && ((StandardMotor) input.getElevatorLeftLift().getInternalInteractionSurface()).getDcMotor().getCurrentPosition() <= -575) {
                input.sendInputToHandSpinner(HandSpinningServoLocation.Action.SET_POSITION, 40);
                timeAsOfLastFullLiftMovement = opMode.time;
                step++;
            }
            // tell hand/elevator to reset after block is dispensed
            if(step == 2 && timeAsOfLastFullLiftMovement + 4 <= opMode.time) {
                step = 0;
                isMovingToMBlock = false;
                isMovingToBasePos = true;
                liftAutoMovementIsDone = true;
            }
        }
        // enables top level block routine if requested
        if(!isMovingToBasePos && !isMovingToLBall && !isMovingToMBall && !isMovingToTBall && !isMovingToLBlock && !isMovingToMBlock && !isMovingToTBlock && !isMovingToIntakePos) {
            isMovingToTBlock = true;
            step = 0;
        }
        // dispenses block at top
        if(isMovingToTBlock) {
            
            // move the elevator to dropping position
            if(step == 0) {
                input.sendInputToElevatorLeftLift(ElevatorLeftLiftMotorLocation.Action.SET_POSITION, -1000);
                input.sendInputToElevatorRightLift(ElevatorRightLiftMotorLocation.Action.SET_POSITION, -1000);
                input.sendInputToLeftHandGrabber(LeftHandGrabbingServoLocation.Action.SET_POSITION, 30);
                input.sendInputToRightHandGrabber(RightHandGrabbingServoLocation.Action.SET_POSITION, 60);
                timeAsOfLastFullLiftMovement = opMode.time;
                step++;
            }
            // turn hand to down position once elevator reaches its position
            if(step == 1 && ((StandardMotor) input.getElevatorLeftLift().getInternalInteractionSurface()).getDcMotor().getCurrentPosition() <= -1000) {
                input.sendInputToHandSpinner(HandSpinningServoLocation.Action.SET_POSITION, 40);
                timeAsOfLastFullLiftMovement = opMode.time;
                step++;
            }
            // tell hand/elevator to reset after block is dispensed
            if(step == 2 && timeAsOfLastFullLiftMovement + 4 <= opMode.time) {
                step = 0;
                isMovingToTBlock = false;
                isMovingToBasePos = true;
                liftAutoMovementIsDone = true;
            }
        }
    }

    private int initialPositionsOrientation(int raw) {
        if (isCameraUpsideDown) {
            if (raw == 1) {
                raw = 3;
            } else if (raw == 3) {
                raw = 1;
            }
        }
        return raw;
    }

    private interface Movement {
        void run();
    }
}
