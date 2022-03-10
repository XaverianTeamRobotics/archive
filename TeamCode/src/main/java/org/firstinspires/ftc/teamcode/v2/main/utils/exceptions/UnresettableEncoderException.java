package org.firstinspires.ftc.teamcode.v2.main.utils.exceptions;

/**
 * The exception thrown when an unresettable encoder attempts reset itself.
 */
public class UnresettableEncoderException extends RuntimeException {

    public UnresettableEncoderException(String message) {
        super(message);
    }

}
