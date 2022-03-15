package org.firstinspires.ftc.teamcode.v2.main.utils.hardware;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.teamcode.v2.main.utils.env.Environment;

/**
 * An {@link EncodedMotor} is a motor which is connected to the robot via both sections of a motor port: the power and encoder ports. This should be used for motors which have their own encoder plugged into the encoder port, not an external encoder.
 */
public class EncodedMotor extends Hardware {

    private final DcMotor MOTOR;
    private int offset = 0;

    /**
     * Creates a new {@link EncodedMotor}.
     * @param name The name of the motor.
     * @param direction The direction of the motor.
     * @param behavior The behavior of the motor when it is told to stop sending power to the motor.
     */
    public EncodedMotor(String name, DcMotorSimple.Direction direction, DcMotor.ZeroPowerBehavior behavior) {
        MOTOR = Environment.getHardwareMap().get(DcMotor.class, name);
        MOTOR.setZeroPowerBehavior(behavior);
        MOTOR.setDirection(direction);
        MOTOR.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        MOTOR.setPower(0);
        MOTOR.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
    }

    /**
     * Sets the position the motor should attempt to drive to.
     * @param position The encoder position the motor should drive to.
     * @param maxSpeed The maximum speed the motor is allowed to travel at while moving to this position.
     */
    public void setPosition(int position, double maxSpeed) {
        maxSpeed = Range.clip(maxSpeed, -100, 100);
        MOTOR.setTargetPosition(position + offset);
        setModeSafely(DcMotor.RunMode.RUN_TO_POSITION);
        MOTOR.setPower(maxSpeed / 100);
    }

    /**
     * Sets the speed of the motor. The motor will attempt to run at this speed regardless of how much voltage it requires.
     * @param speed The speed of the motor as a percentage of its maximum speed, between -100 and 100.
     */
    public void setSpeed(double speed) {
        speed = Range.clip(speed, -100, 100);
        setModeSafely(DcMotor.RunMode.RUN_USING_ENCODER);
        MOTOR.setPower(speed / 100);
    }

    /**
     * Sets the raw voltage of the motor.
     * @param power The power of the motor as a percentage of its maximum power, between -100 and 100.
     */
    public void setPower(double power) {
        power = Range.clip(power, -100, 100);
        setModeSafely(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        MOTOR.setPower(power / 100);
    }

    /**
     * Gets the current position of the motor.
     * @return The current position of the motor.
     */
    public int getCurrentPosition() {
        return MOTOR.getCurrentPosition() - offset;
    }

    /**
     * Gets the target position of the motor, assuming the motor currently has a target position to drive to.
     * @return The target position of the motor.
     */
    public int getTargetPosition() {
        return MOTOR.getTargetPosition() - offset;
    }

    /**
     * Determines whether the motor is currently driving to its target position, if it has one.
     * @return True if the motor is driving to its target position, false if otherwise. Most of the time, false means the motor has already reached its position.
     */
    public boolean isDrivingToPosition() {
        return MOTOR.isBusy();
    }

    /**
     * Alias for {@link #getPower()}.
     */
    public double getSpeed() {
        return getPower();
    }

    /**
     * Gets the voltage level of the motor.
     * @return The voltage level of the motor.
     */
    public double getPower() {
        return Range.clip(MOTOR.getPower() * 100, -100, 100);
    }

    private void setModeSafely(DcMotor.RunMode mode) {
        if(MOTOR.getMode() != mode) {
            MOTOR.setMode(mode);
        }
    }

    public DcMotor getInternalDcMotor() {
        return MOTOR;
    }

    /**
     * Resets the motor's encoder to 0. This will simply tell the encoder to set itself to 0 and continue doing its job; it won't drive the motor to its starting position. A side effect of this is that the motor is forced to come to a complete stop either once or twice during this process, depending on the motor. This can be avoided with {@link #resetEncoder()}, although it may not be as precise of a reset at high speeds.
     * @see #resetEncoder()
     */
    public void resetEncoderNatively() {
        DcMotor.RunMode mode = MOTOR.getMode();
        double power = MOTOR.getPower();
        MOTOR.setPower(0);
        MOTOR.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        MOTOR.setMode(mode);
        MOTOR.setPower(power);
    }

    /**
     * Resets the motor's encoder to 0. This will simply tell the motor to offset all encoder values by the value of the encoder at call time and continue doing its job; it won't drive the motor to its starting position. Unlike {@link #resetEncoderNatively()}, this does nothing to the encoder and simply interperets the encoder's output differently, so the motor does not have to stop. This may result in inaccuracies at high speeds, although they should be minor unless this method decides it wants to take a really long time for whatever reason.
     */
    public void resetEncoder() {
        offset = MOTOR.getCurrentPosition();
    }

    /**
     * Stops the motor and encoder's operation.
     */
    @Override
    public void stop() {
        MOTOR.setPower(0);
        MOTOR.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
    }

}
