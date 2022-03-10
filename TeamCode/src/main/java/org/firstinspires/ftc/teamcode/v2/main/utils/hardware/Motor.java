package org.firstinspires.ftc.teamcode.v2.main.utils.hardware;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.robotcontroller.external.samples.SensorColor;
import org.firstinspires.ftc.teamcode.v2.main.utils.env.Environment;
import org.firstinspires.ftc.teamcode.v2.main.utils.exception.EncoderNotFoundException;

/**
 * A {@link Motor} represents a normal, DC motor connected to the robot via one of the motor ports.
 */
public class Motor extends Hardware {

    private final DcMotor MOTOR;
    private final boolean ENCODED;
    private final String NAME;

    /**
     * Creates a new {@link Motor}. If a seperate encoder is plugged into the encoder section of this physical port, instantiate this instance AFTER the {@link Encoder}'s instance.
     * @param name The name of the motor, as defined in the robot config
     * @param direction The direction of the motor
     * @param encoded Whether or not the motor's internal encoder is plugged into the encoder section of the motor port, <strong>not</strong> a standalone encoder
     */
    public Motor(String name, DcMotorSimple.Direction direction, boolean encoded) {
        ENCODED = encoded;
        NAME = name;
        MOTOR = Environment.getHardwareMap().get(DcMotor.class, name);
        MOTOR.setDirection(direction);
        MOTOR.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        MOTOR.setPower(0);
        if(ENCODED) {
            MOTOR.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        }
    }

    /**
     * Sets the motor's position, as defined by its encoder. With a new position set, the motor will attempt to drive to that position.
     * @param position The position, or encoder step, the motor should attempt to drive to
     * @param speed The maximum speed the motor is allowed to run at during this movement
     * @see #getPosition()
     * @see #isDrivingToPosition()
     */
    public void setPosition(int position, double speed) throws EncoderNotFoundException {
        if(ENCODED) {
            speed = Range.clip(speed, -100, 100);
            MOTOR.setTargetPosition(position);
            changeMode(DcMotor.RunMode.RUN_TO_POSITION);
            MOTOR.setPower(speed / 100);
        }else{
            throw new EncoderNotFoundException("The motor " + NAME + " does not have its encoder enabled. Did you instantiate it correctly?");
        }
    }

    /**
     * @see #setPosition(int, double)
     * @see #isDrivingToPosition()
     */
    public int getPosition() throws EncoderNotFoundException {
        if(ENCODED) {
            return MOTOR.getCurrentPosition();
        }else{
            throw new EncoderNotFoundException("The motor " + NAME + " does not have its encoder enabled. Did you instantiate it correctly?");
        }
    }

    /**
     * Checks whether the motor is currently attempting to drive to a specific position it was commanded to drive to.
     * @return True if the motor is attempting to drive, false otherwise
     * @see #setPosition(int, double)
     * @see #getPosition()
     */
    public boolean isDrivingToPosition() {
        return MOTOR.isBusy();
    }

    /**
     * Sets the speed of the motor. The motor will adjust its power level to stay at a consistent speed even with external forces applied.
     * @param speed The speed
     */
    public void setSpeed(double speed) throws EncoderNotFoundException {
        if(ENCODED) {
            speed = Range.clip(speed, -100, 100);
            changeMode(DcMotor.RunMode.RUN_USING_ENCODER);
            MOTOR.setPower(speed / 100);
        }else{
            throw new EncoderNotFoundException("The motor " + NAME + " does not have its encoder enabled. Did you instantiate it correctly?");
        }
    }

    /**
     * Sets the power of the motor. This usually equals the speed, although if the motor is experiencing a force against itself it will need more power to reach the same speed so the power will not equal the speed.
     * @param power The power
     */
    public void setPower(double power) {
        power = Range.clip(power, -100, 100);
        changeMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        MOTOR.setPower(power);
    }

    /**
     * Resets the motor's encoders to 0.
     */
    public void reset() {
        end();
    }

    private void changeMode(DcMotor.RunMode mode) {
        if(!MOTOR.getMode().equals(mode)) {
            MOTOR.setMode(mode);
        }
    }

    private void end() {
        MOTOR.setPower(0);
        if(ENCODED) {
            MOTOR.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        }
    }

    public DcMotor getInternalDcMotor() {
        return MOTOR;
    }


    /**
     * Fully stops the motor's operation.
     */
    @Override
    public void stop() {
        end();
    }

}
