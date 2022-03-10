package org.firstinspires.ftc.teamcode.v2.main.utils.hardware;

import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.teamcode.v2.main.utils.env.Environment;

/**
 * An {@link Encoder} represents an external encoder connected to a motor port's encoder section. It should <strong>not</strong> represent an internal encoder of a motor; if internal encoder functionality is needed it should be enabled in the third argument of {@link Motor}'s constructor.
 */
public class Encoder extends Hardware {

    private final DcMotor ENCODER;

    /**
     * Creates a new {@link Encoder}. If a seperate motor is plugged into the motor section of this physical port, instantiate this instance BEFORE the {@link Motor}'s instance.
     * @param name The name of the encoder, as defined in the robot config under the motor section
     */
    public Encoder(String name) {
        ENCODER = Environment.getHardwareMap().get(DcMotor.class, name);
        ENCODER.setPower(0);
        ENCODER.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
    }

    /**
     * Gets the current position of the encoder.
     * @return The current position
     */
    public int getCurrentPosition() {
        return ENCODER.getCurrentPosition();
    }

    /**
     * Resets the encoder's position to 0. If a physical motor is plugged into the motor section of this port, the motor will also break to a stop before resetting the encoder's position.
     */
    public void reset() {
        end();
    }

    /**
     * Gets the internal underlying {@link DcMotor} interface controlling this encoder. Encoders are considered {@link DcMotor}s under the hood just like {@link Motor}s because motors and encoders are part of a dual-port system. One port controlls the motor, one the encoder, and the FTC SDK interperets these connections as one item, therefore meaning all encoders and motors are the same thing in its eyes.
     * @return The underlying interface controlling the encoder
     */
    public DcMotor getInternalEncoderMotor() {
        return ENCODER;
    }

    private void end() {
        ENCODER.setPower(0);
        ENCODER.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
    }

    /**
     * Stops the encoder's operation.
     */
    @Override
    public void stop() {
        end();
    }

}
