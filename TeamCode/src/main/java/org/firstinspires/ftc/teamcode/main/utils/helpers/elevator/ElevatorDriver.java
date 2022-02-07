package org.firstinspires.ftc.teamcode.main.utils.helpers.elevator;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.Gamepad;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.teamcode.main.utils.gamepads.GamepadManager;
import org.firstinspires.ftc.teamcode.main.utils.interactions.items.StandardDistanceSensor;
import org.firstinspires.ftc.teamcode.main.utils.interactions.items.StandardMotor;
import org.firstinspires.ftc.teamcode.main.utils.interactions.items.StandardServo;
import org.firstinspires.ftc.teamcode.main.utils.interactions.items.StandardTouchSensor;
import org.firstinspires.ftc.teamcode.main.utils.io.InputSpace;
import org.firstinspires.ftc.teamcode.main.utils.io.OutputSpace;

import java.util.HashMap;

public class ElevatorDriver {

    /*
    * INIT
    * */

    private final StandardMotor RIGHT_MOTOR, LEFT_MOTOR;
    private final StandardServo RIGHT_SERVO, LEFT_SERVO, HAND_SPINNER;
    private final StandardDistanceSensor DISTANCE;
    private final StandardTouchSensor LIMIT;

    /*
    * CONFIG VALUES
    * */
    
    private final int handGrabbingPositionRight = 35;
    private final int handGrabbingPositionLeft = 90;
    private final int handReleasingPositionRight = 100;
    private final int handReleasingPositionLeft = 30;
    private final int distanceSensorDistance = 120;
    private final int handTurningGrabbingPosition = 20;
    private final int handTurningDefaultPosition = 23;
    private final int handTurningBottomBallPosition = 36;
    private final int handTurningMiddleBallPosition = 36;
    private final int handTurningTopBallPosition = 36;
    private final int handTurningBottomBlockPosition = 38;
    private final int handTurningMediumBlockPosition = 40;
    private final int handTurningTopBlockPosition = 40;
    private final int handTurningSafePosition = 33;
    private final int elevatorSafePosition = -500;
    private final int elevatorLowerBallPosition = -20;
    private final int elevatorMiddleBallPosition = -350;
    private final int elevatorTopBallPosition = -700;
    private final int elevatorLowerBlockPosition = -150;
    private final int elevatorMiddleBlockPosition = -575;
    private final int elevatorTopBlockPosition = -1000;

    private int step = 0;
    private final LinearOpMode OP_MODE;
    private double time = 0;
    private double rumbleTracker = 0;

    /**
     * Whether the robot is stable or not. This should only be true if the robot is not moving and in its default position, {@link #step} is 0, and/or when all the "isPos*" boolean values are false besides.
     */
    private boolean isStable = true;
    private boolean resettingToOriginalPos = false;

    private boolean isPosIntake = false;
    private boolean isPosLowBall = false;
    private boolean isPosMedBall = false;
    private boolean isPosTopBall = false;
    private boolean isPosLowBlock = false;
    private boolean isPosMedBlock = false;
    private boolean isPosTopBlock = false;

    private GamepadManager optionalGamepadManager;

    /**
     * This creates an ElevatorDriver with two elevator motors, two hand grabber servos, a hand spinner servo, a limit switch, and a distance sensor to determine when the servos should grab the hand. It uses the default configuration for each motor, servo, and sensor, which at the time of writing is best for our 2021-2022 season robot.
     * @param input The InputSpace to get the motors and servos from
     * @param output The OutputSpace to get the distance sensor from
     * @param opMode The OpMode this driver is being used in
     */
    public ElevatorDriver(InputSpace input, OutputSpace output, LinearOpMode opMode) {
        RIGHT_MOTOR = ((StandardMotor) input.getElevatorRightLift().getInternalInteractionSurface());
        LEFT_MOTOR = ((StandardMotor) input.getElevatorLeftLift().getInternalInteractionSurface());
        RIGHT_SERVO = ((StandardServo) input.getRightHandGrabber().getInternalInteractionSurface());
        LEFT_SERVO = ((StandardServo) input.getLeftHandGrabber().getInternalInteractionSurface());
        DISTANCE = ((StandardDistanceSensor) output.getHandDistanceSensor().getInternalInteractionSurface());
        HAND_SPINNER = ((StandardServo) input.getHandSpinner().getInternalInteractionSurface());
        LIMIT = ((StandardTouchSensor) output.getElevatorBottomLimitSwitch().getInternalInteractionSurface());
        OP_MODE = opMode;
        stabalize();
    }

    /**
     * Tells the driver to send feedback to the specified destination. This is recommended in TeleOps. To tell the driver to stop sending feedback, simply pass null into the method. Since null is null, feedback has no where to go, and thus will be disabled.
     * @param gamepadManager The manager of the gamepads to send feedback to
     */
    public void setFeedbackDestination(GamepadManager gamepadManager) {
        optionalGamepadManager = gamepadManager;
    }

    /*
    * DRIVER
    * */

    /**
     * Runs the elevator. This method will run whatever needs to be ran for the elevator to reach the position it needs to. It should be called in a loop until the elevator has completed its task.
     */
    public void run() {
        if(!isStable()) {
            rumble();
            if(isPosIntake) {
                doPosIntake();
            }else if(isPosLowBall) {
                doPosLowBall();
            }else if(isPosMedBall) {
                doPosMedBall();
            }else if(isPosTopBall) {
                doPosTopBall();
            }else if(isPosLowBlock) {
                doPosLowBlock();
            }else if(isPosMedBlock) {
                doPosMedBlock();
            }else if(isPosTopBlock) {
                doPosTopBlock();
            }
        }else{
            derumble();
        }
    }

    /**
     * Tells the driver to attempt to drive to a position determined by the h paramater if possible.
     * The paramater isBlock determines whether the item being transported to position h is a block or ball This method is not recommended unless needed due to simplier methods being available.
     * <ul>
     *     <li>h = 0 - Intake Position</li>
     *     <li>h = 1 - Lower Position</li>
     *     <li>h = 2 - Medium Position</li>
     *     <li>h = 3 - Top Position</li>
     * </ul>
     * @param h The position to transport to
     * @param isBlock Whether the driver should account for a block being transported or a ball
     */
    public void setPosition(int h, boolean isBlock) {
        if(isStable()) {
            if(h == 0) {
                setToIntakePosition();
            }else if(h == 1 && !isBlock) {
                setToLowerBallPosition();
            }else if(h == 2 && !isBlock) {
                setToMediumBallPosition();
            }else if(h == 3 && !isBlock) {
                setToTopBallPosition();
            }else if(h == 1) {
                setToLowerBlockPosition();
            }else if(h == 2) {
                setToMediumBlockPosition();
            }else if(h == 3) {
                setToTopBlockPosition();
            }
        }
    }

    /*
    * CONTROLLERS
    * */

    /**
     * Tells the driver to attempt to drive to the intake position if possible.
     */
    public void setToIntakePosition() {
        if(isStable()) {
            unstabalize();
            isPosIntake = true;
        }
    }

    /**
     * Tells the driver to attempt to drive to the lower ball position if possible.
     */
    public void setToLowerBallPosition() {
        if(isStable()) {
            unstabalize();
            isPosLowBall = true;
        }
    }

    /**
     * Tells the driver to attempt to drive to the medium ball position if possible.
     */
    public void setToMediumBallPosition() {
        if(isStable()) {
            unstabalize();
            isPosMedBall = true;
        }
    }

    /**
     * Tells the driver to attempt to drive to the top ball position if possible.
     */
    public void setToTopBallPosition() {
        if(isStable()) {
            unstabalize();
            isPosTopBall = true;
        }
    }

    /**
     * Tells the driver to attempt to drive to the lower block position if possible.
     */
    public void setToLowerBlockPosition() {
        if(isStable()) {
            unstabalize();
            isPosLowBlock = true;
        }
    }

    /**
     * Tells the driver to attempt to drive to the medium block position if possible.
     */
    public void setToMediumBlockPosition() {
        if(isStable()) {
            unstabalize();
            isPosMedBlock = true;
        }
    }

    /**
     * Tells the driver to attempt to drive to the top block position if possible.
     */
    public void setToTopBlockPosition() {
        if(isStable()) {
            unstabalize();
            isPosTopBlock = true;
        }
    }

    /**
     * Tells the driver to attempt to reset after driving to the intake position if possible.
     */
    private void unsetFromIntakePosition() {
        stabalize();
    }

    /**
     * Tells the driver to attempt to reset after driving to the lower ball position if possible.
     */
    private void unsetFromLowerBallPosition() {
        stabalize();
    }

    /**
     * Tells the driver to attempt to reset after driving to the medium ball position if possible.
     */
    private void unsetFromMediumBallPosition() {
        stabalize();
    }

    /**
     * Tells the driver to attempt to reset after driving to the top ball position if possible.
     */
    private void unsetFromTopBallPosition() {
        stabalize();
    }

    /**
     * Tells the driver to attempt to reset after driving to the lower block position if possible.
     */
    private void unsetFromLowerBlockPosition() {
        stabalize();
    }

    /**
     * Tells the driver to attempt to reset after driving to the medium block position if possible.
     */
    private void unsetFromMediumBlockPosition() {
        stabalize();
    }

    /**
     * Tells the driver to attempt to reset after driving to the top block position if possible.
     */
    private void unsetFromTopBlockPosition() {
        stabalize();
    }

    private void unstabalize() {
        isStable = false;
    }

    private void stabalize() {
        isStable = true;
        step = 0;
        isPosIntake = false;
        isPosLowBall = false;
        isPosMedBall = false;
        isPosTopBall = false;
        isPosLowBlock = false;
        isPosMedBlock = false;
        isPosTopBlock = false;
        setResettingToOriginalPos(false);
    }

    private void updateTime() {
        time = OP_MODE.time;
    }

    private void rumble() {
        if(optionalGamepadManager != null && rumbleTracker + 1 <= getOpModeTime()) {
            optionalGamepadManager.functionOneGamepad().rumble(Gamepad.RUMBLE_DURATION_CONTINUOUS);
            optionalGamepadManager.functionTwoGamepad().rumble(Gamepad.RUMBLE_DURATION_CONTINUOUS);
            optionalGamepadManager.functionThreeGamepad().rumble(Gamepad.RUMBLE_DURATION_CONTINUOUS);
            optionalGamepadManager.functionFourGamepad().rumble(Gamepad.RUMBLE_DURATION_CONTINUOUS);
            optionalGamepadManager.functionFiveGamepad().rumble(Gamepad.RUMBLE_DURATION_CONTINUOUS);
            optionalGamepadManager.functionSixGamepad().rumble(Gamepad.RUMBLE_DURATION_CONTINUOUS);
            rumbleTracker = getOpModeTime();
        }
    }

    private void derumble() {
        if(optionalGamepadManager != null) {
            optionalGamepadManager.functionOneGamepad().stopRumble();
            optionalGamepadManager.functionTwoGamepad().stopRumble();
            optionalGamepadManager.functionThreeGamepad().stopRumble();
            optionalGamepadManager.functionFourGamepad().stopRumble();
            optionalGamepadManager.functionFiveGamepad().stopRumble();
            optionalGamepadManager.functionSixGamepad().stopRumble();
        }
    }

    /*
    * GETTERS
    * */

    /**
     * This method determines whether the elevator is ready to do another action because it is stable. When it is stable, it is at its default position and not moving in any form.
     * @return The robot's state; true if stable and false if unstable
     */
    public boolean isStable() {
        return isStable && step == 0 && !isPosIntake && !isPosLowBall && ! isPosMedBall && !isPosTopBall && !isPosLowBlock && !isPosMedBlock && !isPosTopBlock;
    }

    public int getStep() {
        return step;
    }

    public double getOpModeTime() {
        return OP_MODE.time;
    }

    /**
     * This returns a {@link HashMap} containing all calibration values. The reason for returning a hash map instead of a bunch of getters is to reduce the amount of god-awful lines in this file.
     * @return A hash map of all the values
     */
    public HashMap<String, Integer> getCalibration() {
        HashMap<String, Integer> map = new HashMap<>();
        map.put("handGrabbingPositionRight", handGrabbingPositionRight);
        map.put("handGrabbingPositionLeft", handGrabbingPositionLeft);
        map.put("handReleasingPositionRight", handReleasingPositionRight);
        map.put("handReleasingPositionLeft", handReleasingPositionLeft);
        map.put("distanceSensorDistance", distanceSensorDistance);
        map.put("handTurningGrabbingPosition", handTurningGrabbingPosition);
        map.put("handTurningDefaultPosition", handTurningDefaultPosition);
        map.put("handTurningBottomBallPosition", handTurningBottomBallPosition);
        map.put("handTurningMiddleBallPosition", handTurningMiddleBallPosition);
        map.put("handTurningTopBallPosition", handTurningTopBallPosition);
        map.put("handTurningBottomBlockPosition", handTurningBottomBlockPosition);
        map.put("handTurningMediumBlockPosition", handTurningMediumBlockPosition);
        map.put("handTurningTopBlockPosition", handTurningTopBlockPosition);
        map.put("handTurningSafePosition", handTurningSafePosition);
        map.put("elevatorSafePosition", elevatorSafePosition);
        map.put("elevatorLowerBallPosition", elevatorLowerBallPosition);
        map.put("elevatorMiddleBallPosition", elevatorMiddleBallPosition);
        map.put("elevatorTopBallPosition", elevatorTopBallPosition);
        map.put("elevatorLowerBlockPosition", elevatorLowerBlockPosition);
        map.put("elevatorMiddleBlockPosition", elevatorMiddleBlockPosition);
        map.put("elevatorTopBlockPosition", elevatorTopBlockPosition);
        return map;
    }

    /*
     * LOGIC
     * */

    private void doPosIntake() {
        if(step == 0) {
            HAND_SPINNER.setPosition(handTurningDefaultPosition);
            updateTime();
            step++;
        }
        // after moving the hand, move the elevator to the base position
        if(step == 1) {
            if(time + 1.75 <= getOpModeTime()) {
                if(LIMIT.isPressed()) {
                    step++;
                }else{
                    LEFT_MOTOR.driveWithEncoder(40);
                    RIGHT_MOTOR.driveWithEncoder(40);
                    step++;
                }
            }
        }
        // once the elevator is at the bottom, reset it
        if(step == 2 && LIMIT.isPressed()) {
            LEFT_MOTOR.driveWithEncoder(0);
            RIGHT_MOTOR.driveWithEncoder(0);
            LEFT_MOTOR.reset();
            RIGHT_MOTOR.reset();
            step++;
        }
        // once at base, move the hand to the intake position
        if(step == 3) {
            HAND_SPINNER.setPosition(handTurningGrabbingPosition);
            LEFT_SERVO.setPosition(handReleasingPositionLeft);
            RIGHT_SERVO.setPosition(handReleasingPositionRight);
            step++;
        }
        if(step == 4 && DISTANCE.getDistance(DistanceUnit.MM) <= distanceSensorDistance) {
            updateTime();
            step++;
        }
        if(step == 5 && time + 0.5 <= getOpModeTime()) {
            LEFT_SERVO.setPosition(handGrabbingPositionLeft);
            RIGHT_SERVO.setPosition(handGrabbingPositionRight);
            HAND_SPINNER.setPosition(handTurningDefaultPosition);
            updateTime();
            step++;
        }
        if(step == 6) {
            unsetFromIntakePosition();
        }
    }

    private void doPosLowBall() {
        if(step == 0) {
            LEFT_MOTOR.driveToPosition(elevatorSafePosition, 50);
            RIGHT_MOTOR.driveToPosition(elevatorSafePosition, 50);
            updateTime();
            step++;
        }
        // turn hand to safest position once elevator reaches its position
        if(step == 1 && LEFT_MOTOR.getDcMotor().getCurrentPosition() <= elevatorSafePosition) {
            HAND_SPINNER.setPosition(handTurningSafePosition);
            updateTime();
            step++;
        }
        // move elevator down to position
        if(step == 2 && time + 0.25 <= getOpModeTime()) {
            LEFT_SERVO.setPosition(handReleasingPositionLeft);
            RIGHT_SERVO.setPosition(handReleasingPositionRight);
            LEFT_MOTOR.driveToPosition(0, 50);
            RIGHT_MOTOR.driveToPosition(0, 50);
            step++;
        }
        // turn hand to the position to dispense the ball
        if(step == 3 && LEFT_MOTOR.getDcMotor().getCurrentPosition() >= elevatorLowerBallPosition) {
            updateTime();
            HAND_SPINNER.setPosition(handTurningBottomBallPosition);
            step++;
        }
        // turn hand back to a safe position and move elevator to turning point position
        if(step == 4 && time + 2 <= getOpModeTime()) {
            HAND_SPINNER.setPosition(handTurningSafePosition);
            LEFT_MOTOR.driveToPosition(elevatorSafePosition, 50);
            RIGHT_MOTOR.driveToPosition(elevatorSafePosition, 50);
            step++;
        }
        // tell hand/elevator to reset once in a safe position to do so
        if(step == 5 && LEFT_MOTOR.getDcMotor().getCurrentPosition() <= elevatorSafePosition) {
            LEFT_SERVO.setPosition(handGrabbingPositionLeft);
            RIGHT_SERVO.setPosition(handGrabbingPositionRight);
            HAND_SPINNER.setPosition(handTurningDefaultPosition);
            step++;
            setResettingToOriginalPos(true);
            updateTime();
        }
        if(step == 6) {
            if(time + 1.75 <= getOpModeTime()) {
                if(!LIMIT.isPressed()) {
                    LEFT_MOTOR.driveWithEncoder(40);
                    RIGHT_MOTOR.driveWithEncoder(40);
                }
                step++;
            }
        }
        // once the elevator is at the bottom, reset it
        if(step == 7 && LIMIT.isPressed()) {
            LEFT_MOTOR.driveWithEncoder(0);
            RIGHT_MOTOR.driveWithEncoder(0);
            LEFT_MOTOR.reset();
            RIGHT_MOTOR.reset();
            unsetFromLowerBallPosition();
        }
    }

    private void doPosMedBall() {
        if(step == 0) {
            LEFT_MOTOR.driveToPosition(elevatorSafePosition, 50);
            RIGHT_MOTOR.driveToPosition(elevatorSafePosition, 50);
            updateTime();
            step++;
        }
        // once at that position, turn hand to safe position
        if(step == 1 && LEFT_MOTOR.getDcMotor().getCurrentPosition() <= elevatorSafePosition) {
            HAND_SPINNER.setPosition(handTurningSafePosition);
            updateTime();
            step++;
        }
        // move hand down to dispensing position
        if(step == 2 && time <= getOpModeTime()) {
            LEFT_SERVO.setPosition(handReleasingPositionLeft);
            RIGHT_SERVO.setPosition(handReleasingPositionRight);
            LEFT_MOTOR.driveToPosition(elevatorMiddleBallPosition, 50);
            RIGHT_MOTOR.driveToPosition(elevatorMiddleBallPosition, 50);
            step++;
        }
        // turn hand to dispensing position
        if(step == 3 && LEFT_MOTOR.getDcMotor().getCurrentPosition() >= elevatorMiddleBallPosition) {
            updateTime();
            HAND_SPINNER.setPosition(handTurningMiddleBallPosition);
            step++;
        }
        // after ball rolls out, move to safe turning position
        if(step == 4 && time + 2 <= getOpModeTime()) {
            HAND_SPINNER.setPosition(handTurningSafePosition);
            LEFT_MOTOR.driveToPosition(elevatorSafePosition, 50);
            RIGHT_MOTOR.driveToPosition(elevatorSafePosition, 50);
            step++;
        }
        // reset once safe to do so
        if(step == 5 && LEFT_MOTOR.getDcMotor().getCurrentPosition() <= elevatorSafePosition) {
            LEFT_SERVO.setPosition(handGrabbingPositionLeft);
            RIGHT_SERVO.setPosition(handGrabbingPositionRight);
            HAND_SPINNER.setPosition(handTurningDefaultPosition);
            step++;
            setResettingToOriginalPos(true);
            updateTime();
        }
        if(step == 6) {
            if(time + 1.75 <= getOpModeTime()) {
                if(!LIMIT.isPressed()) {
                    LEFT_MOTOR.driveWithEncoder(40);
                    RIGHT_MOTOR.driveWithEncoder(40);
                }
                step++;
            }
        }
        // once the elevator is at the bottom, reset it
        if(step == 7 && LIMIT.isPressed()) {
            LEFT_MOTOR.driveWithEncoder(0);
            RIGHT_MOTOR.driveWithEncoder(0);
            LEFT_MOTOR.reset();
            RIGHT_MOTOR.reset();
            unsetFromMediumBallPosition();
        }
    }

    private void doPosTopBall() {
        // move to dispensing position, doesnt need to worry about safe position because its higher up
        if(step == 0) {
            LEFT_MOTOR.driveToPosition(elevatorTopBallPosition, 50);
            RIGHT_MOTOR.driveToPosition(elevatorTopBallPosition, 50);
            updateTime();
            step++;
        }
        // turn to dispensing position once position reached
        if(step == 1 && LEFT_MOTOR.getDcMotor().getCurrentPosition() <= elevatorTopBallPosition) {
            LEFT_SERVO.setPosition(handReleasingPositionLeft);
            RIGHT_SERVO.setPosition(handReleasingPositionRight);
            HAND_SPINNER.setPosition(handTurningTopBallPosition);
            updateTime();
            step++;
        }
        // after ball is dispensed, reset hand because its in a safe position
        if(step == 2 && time + 4 <= getOpModeTime()) {
            LEFT_SERVO.setPosition(handGrabbingPositionLeft);
            RIGHT_SERVO.setPosition(handGrabbingPositionRight);
            HAND_SPINNER.setPosition(handTurningDefaultPosition);
            step++;
            setResettingToOriginalPos(true);
            updateTime();
        }
        if(step == 3) {
            if(time + 1.75 <= getOpModeTime()) {
                if(!LIMIT.isPressed()) {
                    LEFT_MOTOR.driveWithEncoder(40);
                    RIGHT_MOTOR.driveWithEncoder(40);
                }
                step++;
            }
        }
        // once the elevator is at the bottom, reset it
        if(step == 4 && LIMIT.isPressed()) {
            LEFT_MOTOR.driveWithEncoder(0);
            RIGHT_MOTOR.driveWithEncoder(0);
            LEFT_MOTOR.reset();
            RIGHT_MOTOR.reset();
            unsetFromTopBallPosition();
        }
    }

    private void doPosLowBlock() {
        if(step == 0) {
            LEFT_MOTOR.driveToPosition(elevatorSafePosition, 50);
            RIGHT_MOTOR.driveToPosition(elevatorSafePosition, 50);
            updateTime();
            step++;
        }
        // turn hand to safest position once elevator reaches its position
        if(step == 1 && LEFT_MOTOR.getDcMotor().getCurrentPosition() <= elevatorSafePosition) {
            HAND_SPINNER.setPosition(handTurningSafePosition);
            updateTime();
            step++;
        }
        // move elevator down to position
        if(step == 2 && time + 0.25 <= getOpModeTime()) {
            LEFT_SERVO.setPosition(handReleasingPositionLeft);
            RIGHT_SERVO.setPosition(handReleasingPositionRight);
            LEFT_MOTOR.driveToPosition(elevatorLowerBlockPosition, 50);
            RIGHT_MOTOR.driveToPosition(elevatorLowerBlockPosition, 50);
            step++;
        }
        // turn hand to the position to dispense the ball
        if(step == 3 && LEFT_MOTOR.getDcMotor().getCurrentPosition() >= elevatorLowerBlockPosition) {
            HAND_SPINNER.setPosition(handTurningBottomBlockPosition);
            updateTime();
            step++;
        }
        // turn hand back to a safe position and move elevator to turning point position
        if(step == 4 && time + 2 <= getOpModeTime()) {
            HAND_SPINNER.setPosition(handTurningSafePosition);
            LEFT_MOTOR.driveToPosition(elevatorSafePosition, 50);
            RIGHT_MOTOR.driveToPosition(elevatorSafePosition, 50);
            step++;
        }
        // tell hand/elevator to reset once in a safe position to do so
        if(step == 5 && LEFT_MOTOR.getDcMotor().getCurrentPosition() <= elevatorSafePosition) {
            LEFT_SERVO.setPosition(handGrabbingPositionLeft);
            RIGHT_SERVO.setPosition(handGrabbingPositionRight);
            HAND_SPINNER.setPosition(handTurningDefaultPosition);
            step++;
            setResettingToOriginalPos(true);
            updateTime();
        }
        if(step == 6) {
            if(time + 1.75 <= getOpModeTime()) {
                if(!LIMIT.isPressed()) {
                    LEFT_MOTOR.driveWithEncoder(40);
                    RIGHT_MOTOR.driveWithEncoder(40);
                }
                step++;
            }
        }
        // once the elevator is at the bottom, reset it
        if(step == 7 && LIMIT.isPressed()) {
            LEFT_MOTOR.driveWithEncoder(0);
            RIGHT_MOTOR.driveWithEncoder(0);
            LEFT_MOTOR.reset();
            RIGHT_MOTOR.reset();
            unsetFromLowerBlockPosition();
        }
    }

    private void doPosMedBlock() {
        if(step == 0) {
            LEFT_MOTOR.driveToPosition(elevatorMiddleBlockPosition, 50);
            RIGHT_MOTOR.driveToPosition(elevatorMiddleBlockPosition, 50);
            updateTime();
            step++;
        }
        // turn hand to down position once elevator reaches its position
        if(step == 1 && LEFT_MOTOR.getDcMotor().getCurrentPosition() <= elevatorMiddleBlockPosition) {
            LEFT_SERVO.setPosition(handReleasingPositionLeft);
            RIGHT_SERVO.setPosition(handReleasingPositionRight);
            HAND_SPINNER.setPosition(handTurningMediumBlockPosition);
            updateTime();
            step++;
        }
        // tell hand/elevator to reset after block is dispensed
        if(step == 2 && time + 4 <= getOpModeTime()) {
            LEFT_SERVO.setPosition(handGrabbingPositionLeft);
            RIGHT_SERVO.setPosition(handGrabbingPositionRight);
            HAND_SPINNER.setPosition(handTurningDefaultPosition);
            step++;
            setResettingToOriginalPos(true);
            updateTime();
        }
        if(step == 3) {
            if(time + 1.75 <= getOpModeTime()) {
                if(!LIMIT.isPressed()) {
                    LEFT_MOTOR.driveWithEncoder(40);
                    RIGHT_MOTOR.driveWithEncoder(40);
                }
                step++;
            }
        }
        // once the elevator is at the bottom, reset it
        if(step == 4 && LIMIT.isPressed()) {
            LEFT_MOTOR.driveWithEncoder(0);
            RIGHT_MOTOR.driveWithEncoder(0);
            LEFT_MOTOR.reset();
            RIGHT_MOTOR.reset();
            unsetFromMediumBlockPosition();
        }
    }

    private void doPosTopBlock() {
        // move the elevator to dropping position
        if(step == 0) {
            LEFT_MOTOR.driveToPosition(elevatorTopBlockPosition, 50);
            RIGHT_MOTOR.driveToPosition(elevatorTopBlockPosition, 50);
            step++;
        }
        // turn hand to down position once elevator reaches its position
        if(step == 1 && LEFT_MOTOR.getDcMotor().getCurrentPosition() <= elevatorTopBlockPosition) {
            LEFT_SERVO.setPosition(handReleasingPositionLeft);
            RIGHT_SERVO.setPosition(handReleasingPositionRight);
            HAND_SPINNER.setPosition(handTurningTopBlockPosition);
            updateTime();
            step++;
        }
        // tell hand/elevator to reset after block is dispensed
        if(step == 2 && time + 4 <= getOpModeTime()) {
            LEFT_SERVO.setPosition(handGrabbingPositionLeft);
            RIGHT_SERVO.setPosition(handGrabbingPositionRight);
            HAND_SPINNER.setPosition(handTurningDefaultPosition);
            step++;
            setResettingToOriginalPos(true);
            updateTime();
        }
        if(step == 3) {
            if(time + 1.75 <= getOpModeTime()) {
                if(!LIMIT.isPressed()) {
                    LEFT_MOTOR.driveWithEncoder(40);
                    RIGHT_MOTOR.driveWithEncoder(40);
                }
                step++;
            }
        }
        // once the elevator is at the bottom, reset it
        if(step == 4 && LIMIT.isPressed()) {
            LEFT_MOTOR.driveWithEncoder(0);
            RIGHT_MOTOR.driveWithEncoder(0);
            LEFT_MOTOR.reset();
            RIGHT_MOTOR.reset();
            unsetFromTopBlockPosition();
        }
    }

    public boolean isResettingToOriginalPos() {
        return resettingToOriginalPos;
    }

    public void setResettingToOriginalPos(boolean resettingToOriginalPos) {
        this.resettingToOriginalPos = resettingToOriginalPos;
    }

    // TODO: need to calibrate grabber positions

}
