package org.firstinspires.ftc.teamcode.v2.main.utils.hardware;

import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.teamcode.v2.main.utils.env.Environment;

/**
 * A {@link Motor} is a motor plugged into only one part of a motor port: the power port. If there is an encoder plugged into the encoder section of the port and it's not the motor's internal encoder, use an instance of this class to control the motor and a {@link Encoder} object to control the encoder. If the encoder is the motor's internal one, however, use {@link EncodedMotor}.
 */
public class Motor extends Hardware {

    private final DcMotorSimple MOTOR;

    /**
     * Creates a new {@link Motor}.
     * @param name The name of the motor as defined by the robot config.
     * @param direction The direction of the motor.
     */
    public Motor(String name, DcMotorSimple.Direction direction) {
        MOTOR = Environment.getHardwareMap().get(DcMotorSimple.class, name);
        MOTOR.setPower(0);
        MOTOR.setDirection(direction);
    }

    /**
     * Sets the motor's voltage.
     * @param power The voltage of the motor as a percentage of the motor's total voltage, between -100 and 100.
     */
    public void setPower(double power) {
        power = Range.clip(power, -100, 100);
        MOTOR.setPower(power / 100);
    }

    /**
     * Gets the motor's voltage.
     * @return The voltage of the motor.
     */
    public double getPower() {
        return MOTOR.getPower() * 100;
    }

    public DcMotorSimple getInternalUnencodedDcMotor() {
        return MOTOR;
    }

    /**
     * Stops the motor's operation.
     */
    @Override
    public void stop() {
        MOTOR.setPower(0);
    }

}
