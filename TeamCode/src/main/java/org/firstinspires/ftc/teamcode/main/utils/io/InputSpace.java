package org.firstinspires.ftc.teamcode.main.utils.io;

import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.main.utils.locations.*;

/**
 * <p>This class can be used to send input to locations. Locations then attempt to handle the input.</p>
 * <p>For example, if the location of motor $x receives a value, the location will attempt to spin that motor at $x speed. If it fails, it will consume any exceptions and act like nothing happened. When creating an InputSpace, the InputSpace attempts to create locations for every input location on the robot. For example, if the team decides to build a robot with 4 motors, locations for all 4 will be created and able to send data to. The possible locations are defined inside this class.</p>
 * <p>Since the InputSpace is designed to handle input for the current robot, it should be built with the current robot and only the current robot in mind. Location classes are where all used locations should go. Remove any unused Locations from the InputSpace's methods, and potentially delete the Location entirely if needed.</p>
 */
public class InputSpace {

    private final HardwareMap HARDWARE;
    private final TankDrivetrainLocation TANK;
    private final DuckMotorLocation DUCK;
    private final ElevatorLeftLiftMotorLocation ELEVATOR_LEFT;
    private final ElevatorRightLiftMotorLocation ELEVATOR_RIGHT;
    private final IntakeLiftingServoLocation INTAKE_LIFTING_SERVO;
    private final IntakeSpinningMotorLocation INTAKE_SPINNING_MOTOR;
    private final HandSpinningServoLocation HAND_SPINNING_SERVO;
    private final LeftHandGrabbingServoLocation HAND_LEFT_GRABBER_SERVO;
    private final RightHandGrabbingServoLocation HAND_RIGHT_GRABBER_SERVO;

    public InputSpace(HardwareMap hardware) {
        HARDWARE = hardware;
        TANK = new TankDrivetrainLocation(HARDWARE);
        DUCK = new DuckMotorLocation(HARDWARE);
        ELEVATOR_LEFT = new ElevatorLeftLiftMotorLocation(HARDWARE);
        ELEVATOR_RIGHT = new ElevatorRightLiftMotorLocation(HARDWARE);
        INTAKE_LIFTING_SERVO = new IntakeLiftingServoLocation(HARDWARE);
        INTAKE_SPINNING_MOTOR = new IntakeSpinningMotorLocation(HARDWARE);
        HAND_SPINNING_SERVO = new HandSpinningServoLocation(HARDWARE);
        HAND_LEFT_GRABBER_SERVO = new LeftHandGrabbingServoLocation(HARDWARE);
        HAND_RIGHT_GRABBER_SERVO = new RightHandGrabbingServoLocation(HARDWARE);
    }

    public void sendInputToTank(TankDrivetrainLocation.Action action, int rightInput, int leftInput) {
        TANK.handleInput(action, rightInput, leftInput);
    }

    public void sendInputToDuckMotor(DuckMotorLocation.Action action, int input) {
        DUCK.handleInput(action, input);
    }

    public void sendInputToElevatorLeftLift(ElevatorLeftLiftMotorLocation.Action action, int input) {
        ELEVATOR_LEFT.handleInput(action, input);
    }

    public void sendInputToElevatorRightLift(ElevatorRightLiftMotorLocation.Action action, int input) {
        ELEVATOR_RIGHT.handleInput(action, input);
    }

    public void sendInputToIntakeLifter(IntakeLiftingServoLocation.Action action, int input) {
        INTAKE_LIFTING_SERVO.handleInput(action, input);
    }

    public void sendInputToIntakeSpinner(IntakeSpinningMotorLocation.Action action, int input) {
        INTAKE_SPINNING_MOTOR.handleInput(action, input);
    }

    public void sendInputToHandSpinner(HandSpinningServoLocation.Action action, int input) {
        HAND_SPINNING_SERVO.handleInput(action, input);
    }

    public void sendInputToLeftHandGrabber(LeftHandGrabbingServoLocation.Action action, int input) {
        HAND_LEFT_GRABBER_SERVO.handleInput(action, input);
    }

    public void sendInputToRightHandGrabber(RightHandGrabbingServoLocation.Action action, int input) {
        HAND_RIGHT_GRABBER_SERVO.handleInput(action, input);
    }

    public HardwareMap getHardwareMap() {
        return HARDWARE;
    }

    public TankDrivetrainLocation getTank() {
        return TANK;
    }

    public DuckMotorLocation getDuckMotor() {
        return DUCK;
    }

    public ElevatorLeftLiftMotorLocation getElevatorLeftLift() {
        return ELEVATOR_LEFT;
    }

    public ElevatorRightLiftMotorLocation getElevatorRightLift() {
        return ELEVATOR_RIGHT;
    }

    public IntakeLiftingServoLocation getIntakeLifter() {
        return INTAKE_LIFTING_SERVO;
    }

    public IntakeSpinningMotorLocation getIntakeSpinner() {
        return INTAKE_SPINNING_MOTOR;
    }

    public HandSpinningServoLocation getHandSpinner() {
        return HAND_SPINNING_SERVO;
    }

    public LeftHandGrabbingServoLocation getLeftHandGrabber() {
        return HAND_LEFT_GRABBER_SERVO;
    }

    public RightHandGrabbingServoLocation getRightHandGrabber() {
        return HAND_RIGHT_GRABBER_SERVO;
    }

    public void stop() {
        TANK.stop();
        DUCK.stop();
        ELEVATOR_LEFT.stop();
        ELEVATOR_RIGHT.stop();
        INTAKE_LIFTING_SERVO.stop();
        INTAKE_SPINNING_MOTOR.stop();
        HAND_SPINNING_SERVO.stop();
    }

}
