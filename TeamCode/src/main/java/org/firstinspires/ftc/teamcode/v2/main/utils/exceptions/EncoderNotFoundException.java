package org.firstinspires.ftc.teamcode.v2.main.utils.exceptions;

import org.firstinspires.ftc.teamcode.v2.main.utils.hardware.Motor;

/**
 * The exception thrown when {@link Motor#setPosition(int, double)} is called but the motor was not instantiated with its encoder enabled.
 */
public class EncoderNotFoundException extends RuntimeException {

    public EncoderNotFoundException(String message) {
        super(message);
    }

}
