package org.firstinspires.ftc.teamcode.v2.main.utils.exceptions;

import org.firstinspires.ftc.teamcode.v2.main.utils.hardware.Compass;

/**
 * The exception thrown when a {@link Compass} attempts to calibrate itself but ultimately fails.
 */
public class CompassCalibrationException extends RuntimeException {

    public CompassCalibrationException(String message) {
        super(message);
    }

}
