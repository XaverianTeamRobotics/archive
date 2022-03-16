package org.firstinspires.ftc.teamcode.v2.main.utils.hardware;

import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.teamcode.v2.main.utils.env.Environment;

/**
 * An {@link Encoder} is a thing that can measure rotary distance. It is plugged into the encoder port of a motor port. If this encoder is the internal encoder of the motor plugged into the power port of the encoder's motor port (assuming a motor is plugged in), use {@link EncodedMotor}. If this encoder is external and unrelated to the motor or if there is no motor plugged in, use an instance of this class.
 */
public class Encoder extends Hardware {

    private final DcMotor ENCODER;
    private int offset;

    /**
     * Creates an {@link Encoder}.
     * @param name The name of the encoder.
     */
    public Encoder(String name) {
        ENCODER = Environment.getHardwareMap().get(DcMotor.class, name);
        ENCODER.resetDeviceConfigurationForOpMode();
        offset = ENCODER.getCurrentPosition();
    }

    /**
     * Gets the current position of the encoder.
     * @return The position of the encoder.
     */
    public int getPosition() {
        return ENCODER.getCurrentPosition() - offset;
    }

    /**
     * Resets the encoder's posiiton to 0. This will simply tell the motor to offset all encoder values by the value of the encoder at call time and continue doing its job; it won't drive the motor to its starting position.
     */
    public void resetPosition() {
        offset = ENCODER.getCurrentPosition();
    }

    public DcMotor getInternalEncoder() {
        return ENCODER;
    }

    /**
     * Stops the encoder's operation.
     */
    @Override
    public void stop() {}

}
