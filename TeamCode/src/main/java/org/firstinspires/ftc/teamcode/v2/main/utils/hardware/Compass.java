package org.firstinspires.ftc.teamcode.v2.main.utils.hardware;

import com.qualcomm.robotcore.hardware.CompassSensor;

import org.firstinspires.ftc.teamcode.v2.main.utils.env.Environment;
import org.firstinspires.ftc.teamcode.v2.main.utils.exceptions.CompassCalibrationException;

/**
 * A {@link Compass} can determine the robot's heading based on magnetic fields, like a real compass (it is one).
 */
public class Compass extends Hardware {

    private final String NAME;
    private final CompassSensor COMPASS;

    /**
     * Creates a new {@link Compass}.
     * @param name The name of the compass as defined in the robot config
     * @param calibrate Whether or not to calibrate the compass, which will take about 10 seconds
     * @throws CompassCalibrationException The exception thrown if the compass fails to calibrate itself
     */
    public Compass(String name, boolean calibrate) throws CompassCalibrationException {
        NAME = name;
        COMPASS = Environment.getHardwareMap().get(CompassSensor.class, name);
        COMPASS.resetDeviceConfigurationForOpMode();
        COMPASS.setMode(CompassSensor.CompassMode.MEASUREMENT_MODE);
        if(calibrate) {
            // begin calibration, wait 5 seconds, then start checking the status. check for 5 seconds, and if the failure is false after all those seconds, the compass is calibrated and we can move on. otherwise, throw a checked exception about it
            COMPASS.setMode(CompassSensor.CompassMode.CALIBRATION_MODE);
            long startTime = System.currentTimeMillis() + 5000;
            while(System.currentTimeMillis() <= startTime);
            startTime = System.currentTimeMillis() + 5000;
            boolean fail = COMPASS.calibrationFailed();
            while(startTime <= System.currentTimeMillis()) {
                fail = COMPASS.calibrationFailed();
            }
            COMPASS.setMode(CompassSensor.CompassMode.MEASUREMENT_MODE);
            if(fail) {
                throw new CompassCalibrationException("The compass \"" + NAME + "\" failed to calibrate!");
            }
        }
    }

    /**
     * Recalibrates the compass synchronously. This method is blocking and will take approximately 10 seconds.
     * @throws CompassCalibrationException The exception thrown if the compass fails to calibrate itself
     */
    public void recalibrateSync() throws CompassCalibrationException {
        // begin calibration, wait 5 seconds, then start checking the status. check for 5 seconds, and if the failure is false after all those seconds, the compass is calibrated and we can move on. otherwise, throw a checked exception about it
        COMPASS.setMode(CompassSensor.CompassMode.CALIBRATION_MODE);
        long startTime = System.currentTimeMillis() + 5000;
        while(System.currentTimeMillis() <= startTime);
        startTime = System.currentTimeMillis() + 5000;
        boolean fail = COMPASS.calibrationFailed();
        while(startTime <= System.currentTimeMillis()) {
            fail = COMPASS.calibrationFailed();
        }
        COMPASS.setMode(CompassSensor.CompassMode.MEASUREMENT_MODE);
        if(fail) {
            throw new CompassCalibrationException("The compass \"" + NAME + "\" failed to calibrate!");
        }
    }

    /**
     * Starts an asynchronous recalibration. This method of recalibration is more cumbersome to implement, but allows the robot to do other tasks while the compass is calibrating, although that may be a bad idea if it's moving around a lot.
     * @see #checkAsyncRecalibrationStatus()
     * @see #endAsyncRecalibration()
     */
    public void beginAsyncRecalibration() {
        COMPASS.setMode(CompassSensor.CompassMode.CALIBRATION_MODE);
    }

    /**
     * Checks whether or not the compass' calibration has been successful in an async calibration. This method may return inaccurate results if the calibration process does not have time to finish, so I recommend calling this about 10 seconds after starting calibration.
     * @return The status of the compass' calibration
     * @see #beginAsyncRecalibration()
     * @see #endAsyncRecalibration()
     */
    public boolean checkAsyncRecalibrationStatus() {
        return !COMPASS.calibrationFailed();
    }

    /**
     * Ends an asynchronous calibration of the compass. I would only call this method after {@link #checkAsyncRecalibrationStatus()} returns true 10 seconds after the calibration period started.
     * @see #beginAsyncRecalibration()
     * @see #checkAsyncRecalibrationStatus()
     */
    public void endAsyncRecalibration() {
        COMPASS.setMode(CompassSensor.CompassMode.MEASUREMENT_MODE);
    }

    /**
     * Gets the heading, from 0 to 360, of the robot going clockwise (north is 0, east is 90, south is 180, and west is 270).
     * @return The heading of the robot
     */
    public double getHeading() {
        return COMPASS.getDirection();
    }

    /**
     * Gets the readable status of the compass.
     * @return The status of the compass
     */
    public String getStatus() {
        return COMPASS.status();
    }

    public CompassSensor getInternalSensor() {
        return COMPASS;
    }

    /**
     * Stops the compass' operation.
     */
    @Override
    public void stop() {}

}
