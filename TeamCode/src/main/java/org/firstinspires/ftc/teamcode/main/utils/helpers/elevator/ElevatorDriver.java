package org.firstinspires.ftc.teamcode.main.utils.helpers.elevator;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.util.Range;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.teamcode.main.utils.gamepads.GamepadManager;
import org.firstinspires.ftc.teamcode.main.utils.interactions.items.StandardDistanceSensor;
import org.firstinspires.ftc.teamcode.main.utils.interactions.items.StandardMotor;
import org.firstinspires.ftc.teamcode.main.utils.interactions.items.StandardServo;
import org.firstinspires.ftc.teamcode.main.utils.interactions.items.StandardTouchSensor;
import org.firstinspires.ftc.teamcode.main.utils.io.InputSpace;
import org.firstinspires.ftc.teamcode.main.utils.io.OutputSpace;

import java.util.HashMap;

/**
 * <p>This class helps drive the elevator on our robot for the 2020-2021 season. It's extrememly complicated, and as such should probably not be edited unless you know what you're doing. It allows for the following:</p>
 * <ul>
 *     <li>Dispensal of object at the bottom, middle, and top levels</li>
 *     <li>Picking up of object</li>
 *     <li>Toggable manual control of lift</li>
 *     <li>Toggable feedback to physical drivers</li>
 *     <li>Dispensal of object in autonomous OpModes</li>
 * </ul>
 * <p>The elevator has a list of configuration fields for defining the location of safe zones, certain points to reach at a given time, etc. These values are designed to be easily editable by changing their corresponding field, and should be changed when the robot physically changes.</p>
 * <p>The elevator can be driven via #setTo&#60;<em>pos_item</em>&#62; where <em>pos</em> is the position and <em>item</em> is the item, block or ball, to dispense. The elevator can also be set via {@link #setPosition(int, boolean)}, although this is only recommended for autonomous OpModes. To physically run the elevator, the {@link #run()} method should be called iteratively.</p>
 * <p>Manual mode can be enabled via {@link #enableManualControl()}, after calling {@link #setManualController(GamepadManager)}. It can then be disabled by {@link #disableManualControl()}, where it will stop a user from having control over the elevator and attempt to safely reset the elevator to its default position.</p>
 * <p>Feedback is enabled by default, although you must specify where to send feedback to. Feedback is in the format of vibrations and as such can be sent to gamepads via a GamepadManager, using {@link #setFeedbackDestination(GamepadManager)}.</p>
 * <p>The hand can be manually toggled out of the intake position if it does not exit the position itself. This can be enabled by {@link #setIntakeToggleController(GamepadManager)}. If not enabled, the hand will only reset when it detects an object inside itself. Note: This does <strong>not</strong> disable the automatic reset when an object is detected, rather it allows both automatic and manual resetting.</p>
 */
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
    
    private final int handGrabbingPositionRight = 37;
    private final int handGrabbingPositionLeft = 90;
    private final int handReleasingPositionRight = 89;
    private final int handReleasingPositionLeft = 40;
    private final int distanceSensorDistance = 90;
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

    /*
    * END OF CONFIG VALUES
    * BELOW ARE OTHER MISC FIELDS
    * */

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

    private GamepadManager optionalFeedbackGamepadManager;

    private GamepadManager optionalIntakeToggleGamepadManager;

    /*
    * MANUAL
    * */

    private GamepadManager optionalControlGamepadManager;
    private boolean manualMode = false;
    private boolean manualModeIsResetting = false;
    private boolean grabberButtonWasDown = false;
    private boolean grabberShouldBeOpen = false;
    private int rightESpeed;
    private int leftESpeed;
    private int rightGPos;
    private int leftGPos;
    private int spinPos;

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
        optionalFeedbackGamepadManager = gamepadManager;
    }

    /**
     * Tells the driver to handle input from the {@link GamepadManager} when manual control is enabled. This needs to be called before attempting to enable manual control.
     * @param gamepadManager The manager of the gamepads to take input from
     */
    public void setManualController(GamepadManager gamepadManager) {
        optionalControlGamepadManager = gamepadManager;
    }

    /**
     * Tells the driver to listen to the provided {@link GamepadManager} for input from the second controller's cross button to enable manual closing of the hand during object intake.
     * @param gamepadManager The manager of the gamepadds to listen to
     */
    public void setIntakeToggleController(GamepadManager gamepadManager) {
        optionalIntakeToggleGamepadManager = gamepadManager;
    }

    /*
    * DRIVER
    * */

    /**
     * Runs the elevator. This method will run whatever needs to be ran for the elevator to reach the position it needs to. It should be called in a loop until the elevator has completed its task.
     */
    public void run() {
        if(!isStable()) {
            rumbleF2();
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
            }else if(manualMode) {
                doManualControl();
            }
        }else{
            derumbleF2();
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
            rumbleF1(4);
        }
    }

    /**
     * Tells the driver to attempt to drive to the lower ball position if possible.
     */
    public void setToLowerBallPosition() {
        if(isStable()) {
            unstabalize();
            isPosLowBall = true;
            rumbleF1(1);
        }
    }

    /**
     * Tells the driver to attempt to drive to the medium ball position if possible.
     */
    public void setToMediumBallPosition() {
        if(isStable()) {
            unstabalize();
            isPosMedBall = true;
            rumbleF1(2);
        }
    }

    /**
     * Tells the driver to attempt to drive to the top ball position if possible.
     */
    public void setToTopBallPosition() {
        if(isStable()) {
            unstabalize();
            isPosTopBall = true;
            rumbleF1(3);
        }
    }

    /**
     * Tells the driver to attempt to drive to the lower block position if possible.
     */
    public void setToLowerBlockPosition() {
        if(isStable()) {
            unstabalize();
            isPosLowBlock = true;
            rumbleF1(1);
        }
    }

    /**
     * Tells the driver to attempt to drive to the medium block position if possible.
     */
    public void setToMediumBlockPosition() {
        if(isStable()) {
            unstabalize();
            isPosMedBlock = true;
            rumbleF1(2);
        }
    }

    /**
     * Tells the driver to attempt to drive to the top block position if possible.
     */
    public void setToTopBlockPosition() {
        if(isStable()) {
            unstabalize();
            isPosTopBlock = true;
            rumbleF1(3);
        }
    }

    /**
     * Tells the driver to enable manual control of itself if possible.
     */
    public void enableManualControl() {
        if(isStable() && optionalControlGamepadManager != null) {
            unstabalize();
            resetManualVars();
            manualMode = true;
            rumbleF1(5);
        }
    }

    /**
     * Tells the driver to reset the manual control variables to their default state, such as the speeds of motors.
     */
    private void resetManualVars() {
        rightESpeed = 0;
        leftESpeed = 0;
        rightGPos = handGrabbingPositionRight;
        leftGPos = handGrabbingPositionLeft;
        spinPos = handTurningDefaultPosition;
    }

    /**
     * Tells the driver to attempt to disable manual control if possible. This will also attempt to drive the elevator to the correct, default position. Because of this, this method is more of a queueing method than a direct modifying method. It will queue the elevator to safely exit out of manual mode, which will disable manual mode once complete.
     */
    public void disableManualControl() {
        if(manualMode && !manualModeIsResetting) {
            manualModeIsResetting = true;
        }
    }

    /**
     * Tells the driver to unset manual control after disabling and resetting.
     */
    private void unsetManualControl() {
        resetManualVars();
        stabalize();
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

    /**
     * Tells the elevator it is not in a stable position to be ran.
     */
    private void unstabalize() {
        isStable = false;
    }

    /**
     * Tells the elevator it's in a stable position to be ran. This method has no form of error-checking to make sure the elevator is actually stable, so should only be called once the elevator is guaranteed to be physically stable.
     */
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
        manualMode = false;
        manualModeIsResetting = false;
        setResettingToOriginalPos(false);
    }

    /**
     * Updates the builtin {@link #time}stamp to the current time.
     */
    private void updateTime() {
        time = OP_MODE.time;
    }

    /**
     * Sends feedback, in the form of vibrations or <strong>rumbles</strong>, to a feedback destination if one exists. It will rumble the gamepads assigned to function three.
     */
    private void rumbleF2() {
        if(optionalFeedbackGamepadManager != null && rumbleTracker + 1 <= getOpModeTime()) {
            optionalFeedbackGamepadManager.functionThreeGamepad().rumble(Gamepad.RUMBLE_DURATION_CONTINUOUS);
            rumbleTracker = getOpModeTime();
        }
    }

    /**
     * Sets the amount of rumble blips to be sent to the gamepad assigned to function one of the feedback destination set, if at all.
     * <ul>
     *     <li>1 Blip - Lower Position</li>
     *     <li>2 Blips - Middle Position</li>
     *     <li>3 Blips - Top Position</li>
     *     <li>4 Blips - Intake Position</li>
     *     <li>5 Blips - Manual Control</li>
     * </ul>
     * @param blips The amount of blips to be sent
     */
    private void rumbleF1(int blips) {
        if(optionalFeedbackGamepadManager != null) {
            optionalFeedbackGamepadManager.functionOneGamepad().rumbleBlips(blips);
        }
    }

    /**
     * Cancels all feedback sent by {@link #rumbleF2()}. Got #rumbleisoverparty trending once.
     */
    private void derumbleF2() {
        if(optionalFeedbackGamepadManager != null) {
            optionalFeedbackGamepadManager.functionThreeGamepad().stopRumble();
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
        return isStable && step == 0 && !isPosIntake && !isPosLowBall && ! isPosMedBall && !isPosTopBall && !isPosLowBlock && !isPosMedBlock && !isPosTopBlock && !manualMode;
    }

    /**
     * <p>Gets the current step of whatever operation is occouring. Steps are arbitrary counters designed to help keep track of the elevator's movement.</p>
     *
     * <p>For example, when moving to <em>x</em> position, the elevator might be required to move in one direction a certain amount, followed by a movement in another direction. In this case, the step would equal 0 as the elevator is moving in one direction, and the step would equal 1 when it moves in another direction. If the elevator has more movements it must do following these two, the step will increase by 1 each time a notable movement has occoured.</p>
     *
     * <p>Note that the step will not increase for all movements, only important ones. For example, the step might stay at the same value while two servos are running, because in this example the combined movement of those serovs is important to track, not their individual movements.</p>
     * @return The step as defined above
     */
    public int getStep() {
        return step;
    }

    /**
     * Gets the current time, not the value of the {@link #time}stamp.
     * @return The time
     */
    private double getOpModeTime() {
        return OP_MODE.time;
    }

    /**
     * Returns whether manual mode is enabled regardless of whether a disablement has been queued by {@link #disableManualControl()}, not whether {@link #setManualController(GamepadManager)} has been called.
     * @return The status of manual mode
     */
    public boolean isManualModeEnabled() {
        return manualMode;
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
            if(LIMIT.isPressed()) {
                step++;
            }else if(time + 1.75 <= getOpModeTime()) {
                LEFT_MOTOR.driveWithEncoder(40);
                RIGHT_MOTOR.driveWithEncoder(40);
                step++;
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
            updateTime();
            step++;
        }
        if(step == 4 && time + 1 <= getOpModeTime()) {
            updateTime();
            step++;
        }
        // allow someone to cancel pickup if needed
        if(step >= 5 && step <= 6) {
            if(optionalIntakeToggleGamepadManager != null && optionalIntakeToggleGamepadManager.functionThreeGamepad().a) {
                updateTime();
                step = 7;
            }
        }
        // wait for an object to be picked up
        if(step == 5) {
            // if object picked up, go to next step, if not, skip next step
            if(DISTANCE.getDistance(DistanceUnit.MM) <= distanceSensorDistance) {
                updateTime();
                step++;
            }
        }
        // check if the object is still in the hand, if so go to next step, if not go back a step
        if(step == 6 && time + 0.5 <= getOpModeTime()) {
            if(DISTANCE.getDistance(DistanceUnit.MM) <= distanceSensorDistance) {
                updateTime();
                step++;
            }else{
                updateTime();
                step--;
            }
        }
        // move back to its original position, with the object in hand
        if(step == 7) {
            LEFT_SERVO.setPosition(handGrabbingPositionLeft);
            RIGHT_SERVO.setPosition(handGrabbingPositionRight);
            HAND_SPINNER.setPosition(handTurningDefaultPosition);
            updateTime();
            step++;
        }
        if(step == 8 && time + 0.5 <= getOpModeTime()) {
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
        // tell hand to reset once in a safe position to do so
        if(step == 5 && LEFT_MOTOR.getDcMotor().getCurrentPosition() <= elevatorSafePosition) {
            LEFT_SERVO.setPosition(handGrabbingPositionLeft);
            RIGHT_SERVO.setPosition(handGrabbingPositionRight);
            HAND_SPINNER.setPosition(handTurningDefaultPosition);
            step++;
            setResettingToOriginalPos(true);
            updateTime();
        }
        // tell elevator to reset once in a safe position to do so
        if(step == 6) {
            if(time + 1.75 <= getOpModeTime()) {
                if(!LIMIT.isPressed()) {
                    LEFT_MOTOR.driveWithEncoder(40);
                    RIGHT_MOTOR.driveWithEncoder(40);
                }
                step++;
            }
        }
        // once the elevator is at the bottom, reset its encoders
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
        // reset the elevator once ready to do so
        if(step == 6) {
            if(time + 1.75 <= getOpModeTime()) {
                if(!LIMIT.isPressed()) {
                    LEFT_MOTOR.driveWithEncoder(40);
                    RIGHT_MOTOR.driveWithEncoder(40);
                }
                step++;
            }
        }
        // once the elevator is at the bottom, reset its encoders
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
        // reset elevator once its in a safe position
        if(step == 3) {
            if(time + 1.75 <= getOpModeTime()) {
                if(!LIMIT.isPressed()) {
                    LEFT_MOTOR.driveWithEncoder(40);
                    RIGHT_MOTOR.driveWithEncoder(40);
                }
                step++;
            }
        }
        // once the elevator is at the bottom, reset its encoders
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
        // tell hand to reset once in a safe position to do so
        if(step == 5 && LEFT_MOTOR.getDcMotor().getCurrentPosition() <= elevatorSafePosition) {
            LEFT_SERVO.setPosition(handGrabbingPositionLeft);
            RIGHT_SERVO.setPosition(handGrabbingPositionRight);
            HAND_SPINNER.setPosition(handTurningDefaultPosition);
            step++;
            setResettingToOriginalPos(true);
            updateTime();
        }
        // tell elevator to reset itself once its ready
        if(step == 6) {
            if(time + 1.75 <= getOpModeTime()) {
                if(!LIMIT.isPressed()) {
                    LEFT_MOTOR.driveWithEncoder(40);
                    RIGHT_MOTOR.driveWithEncoder(40);
                }
                step++;
            }
        }
        // once the elevator is at the bottom, reset its encoders
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
        // tell hand to reset after block is dispensed
        if(step == 2 && time + 4 <= getOpModeTime()) {
            LEFT_SERVO.setPosition(handGrabbingPositionLeft);
            RIGHT_SERVO.setPosition(handGrabbingPositionRight);
            HAND_SPINNER.setPosition(handTurningDefaultPosition);
            step++;
            setResettingToOriginalPos(true);
            updateTime();
        }
        // tell elevator to reset itself once it is safe to do so
        if(step == 3) {
            if(time + 1.75 <= getOpModeTime()) {
                if(!LIMIT.isPressed()) {
                    LEFT_MOTOR.driveWithEncoder(40);
                    RIGHT_MOTOR.driveWithEncoder(40);
                }
                step++;
            }
        }
        // once the elevator is at the bottom, reset its encoders
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
        // tell hand to reset after block is dispensed
        if(step == 2 && time + 4 <= getOpModeTime()) {
            LEFT_SERVO.setPosition(handGrabbingPositionLeft);
            RIGHT_SERVO.setPosition(handGrabbingPositionRight);
            HAND_SPINNER.setPosition(handTurningDefaultPosition);
            step++;
            setResettingToOriginalPos(true);
            updateTime();
        }
        // tell elevator to reset to its default position once safe
        if(step == 3) {
            if(time + 1.75 <= getOpModeTime()) {
                if(!LIMIT.isPressed()) {
                    LEFT_MOTOR.driveWithEncoder(40);
                    RIGHT_MOTOR.driveWithEncoder(40);
                }
                step++;
            }
        }
        // once the elevator is at the bottom, reset its encoders
        if(step == 4 && LIMIT.isPressed()) {
            LEFT_MOTOR.driveWithEncoder(0);
            RIGHT_MOTOR.driveWithEncoder(0);
            LEFT_MOTOR.reset();
            RIGHT_MOTOR.reset();
            unsetFromTopBlockPosition();
        }
    }

    private void doManualControl() {
        // check if a disablement has been queued, and if so, reset
        if(manualModeIsResetting) {
            // move the elevator to the safe position
            if(step == 0) {
                LEFT_MOTOR.driveToPosition(elevatorSafePosition, 50);
                RIGHT_MOTOR.driveToPosition(elevatorSafePosition, 50);
                step++;
            }
            // once safe position reached, move the hand to safe position
            if(step == 1 && LEFT_MOTOR.getDcMotor().getCurrentPosition() <= elevatorSafePosition) {
                LEFT_SERVO.setPosition(handGrabbingPositionLeft);
                RIGHT_SERVO.setPosition(handGrabbingPositionRight);
                HAND_SPINNER.setPosition(handTurningDefaultPosition);
                updateTime();
                step++;
            }
            // I'm specifically NOT calling #setResettingToOriginalPos because I don't want to introduce that complexity. When anything manual is happening, the robot should not attempt to figure out what's going on. It should only know if it's being manually controlled or not, and that when it is not being manually controlled it doesn't need to know anything about manual control. Also, manual control is illegal during autonomous so this would be useless if I implemented it anyway.
            // move the elevator to the default position after everything else has been moved
            if(step == 2) {
                if(time + 2 <= getOpModeTime()) {
                    if(!LIMIT.isPressed()) {
                        LEFT_MOTOR.driveWithEncoder(40);
                        RIGHT_MOTOR.driveWithEncoder(40);
                    }else{
                        LEFT_MOTOR.driveWithEncoder(0);
                        RIGHT_MOTOR.driveWithEncoder(0);
                        step++;
                    }
                }
            }
            // finally disable manual control
            if(step == 3) {
                unsetManualControl();
                // yes, I know i could add a return here, but I prefer to use if/else statements for more complicated things because it's easier to understand. when you see an if/else, you immediately think "oh this wont be executed if this was", whereas if you don't youll have to find the return statement to confirm that. sure, when theres only a couple lines its easier that way, but when you have a 25+ line block it's a bit annoying to try to find a return
            }
        }else{
            GamepadManager gm = optionalControlGamepadManager;
            // for the elevator, get our inputs and, if the elevator is at the bottom, reset it prematurely and limit inputs to the correct direction
            int a = gm.functionSixGamepad().dpad_down ? 1 : 0;
            int y = gm.functionSixGamepad().dpad_up ? 1 : 0;
            int spd = a - y;
            int s = spd * 60;
            if(LIMIT.isPressed()) {
                if(s > 0) {
                    rightESpeed = 0;
                    leftESpeed = 0;
                    LEFT_MOTOR.reset();
                    RIGHT_MOTOR.reset();
                }else{
                    leftESpeed = Range.clip(s, -60, 60);
                    rightESpeed = Range.clip(s, -60, 60);
                }
            }else if(!LIMIT.isPressed()) {
                leftESpeed = Range.clip(s, -60, 60);
                rightESpeed = Range.clip(s, -60, 60);
            }
            if(Math.abs(LEFT_MOTOR.getDcMotor().getCurrentPosition()) >= 990) {
                if(s < 0) {
                    rightESpeed = 0;
                    leftESpeed = 0;
                }else{
                    leftESpeed = Range.clip(s, -60, 60);
                    rightESpeed = Range.clip(s, -60, 60);
                }
            }
            // get hand inputs
            if(gm.functionSixGamepad().dpad_right) {
                spinPos -= 1;
            }else if(gm.functionSixGamepad().dpad_left) {
                spinPos += 1;
            }
            // make sure theyre wthin boundaries
            spinPos = Range.clip(spinPos, 23, 43);
            // get grabber inputs
            if(gm.functionSixGamepad().touchpad) {
                if(!grabberButtonWasDown) {
                    grabberShouldBeOpen = !grabberShouldBeOpen;
                }
                grabberButtonWasDown = true;
            }else{
                grabberButtonWasDown = false;
            }
            // map inputs to devices
            LEFT_MOTOR.driveWithEncoder(leftESpeed);
            RIGHT_MOTOR.driveWithEncoder(rightESpeed);
            HAND_SPINNER.setPosition(spinPos);
            if(grabberShouldBeOpen) {
                LEFT_SERVO.setPosition(handReleasingPositionLeft);
                RIGHT_SERVO.setPosition(handReleasingPositionRight);
            }else{
                LEFT_SERVO.setPosition(handGrabbingPositionLeft);
                RIGHT_SERVO.setPosition(handGrabbingPositionRight);
            }
            // update the timeout variable
            updateTime();
        }
    }

    /*
    * AUTONOMOUS MEMBERS
    * */

    /**
     * Whether the elevator is in a state in which it has dispensed an object and is now driving back to its default position. This is basically useless unless you're using this in autonomous OpModes, where knowing the elevator's status is crucial to shaving off runtime by allowing the robot to multitask.
     * @return The state of the elevator
     */
    public boolean isResettingToOriginalPos() {
        return resettingToOriginalPos;
    }

    /**
     * Tells the elevator it is resetting its position to the default position. This is basically only to support {@link #isResettingToOriginalPos()}, and should be called a routine has reached the point where it has dispened an object and is now simply returning to its default positiion, meaning the robot does not have to sit next to the dispensing position and can do other things.
     * @param resettingToOriginalPos Whether the robot is resetting to the original position, as defined above
     */
    private void setResettingToOriginalPos(boolean resettingToOriginalPos) {
        this.resettingToOriginalPos = resettingToOriginalPos;
    }

}
