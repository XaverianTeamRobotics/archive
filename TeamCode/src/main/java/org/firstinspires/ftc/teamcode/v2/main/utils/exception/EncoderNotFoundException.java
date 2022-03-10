package org.firstinspires.ftc.teamcode.v2.main.utils.exception;

import org.firstinspires.ftc.teamcode.v2.main.utils.hardware.Motor;

/**
 * The exception thrown when {@link Motor#setPosition(int, double)} is called but the motor was not instantiated with its encoder enabled.
 */
public class EncoderNotFoundException extends Exception {

    public EncoderNotFoundException(String message) {
        super(message);
    }

}
