package org.firstinspires.ftc.teamcode.v2.main.utils.hardware;

import com.qualcomm.robotcore.hardware.GyroSensor;

import org.firstinspires.ftc.teamcode.v2.main.utils.env.Environment;

/**
 * A {@link Gyro} is a gyroscopic sensor on the robot.
 */
public class Gyro extends Hardware {

    private final GyroSensor SENSOR;

    /**
     * Creates a new {@link Gyro}.
     * @param name The name of the gyroscopic sensor as defined by the robot config.
     */
    public Gyro(String name) {
        SENSOR = Environment.getHardwareMap().get(GyroSensor.class, name);
        try {
            SENSOR.calibrate();
            while(SENSOR.isCalibrating());
        } catch(UnsupportedOperationException ignored) {}
    }

    /**
     * Gets the heading of the gyroscopic sensor.
     * @return The heading.
     */
    public int getHeading() {
        return SENSOR.getHeading();
    }

    /**
     * Gets the raw data from the gyroscope as a set of integers [ X, Y, Z ].
     * @return The raw data from the gyroscope.
     */
    public int[] getRaw() {
        return new int[] { SENSOR.rawX(), SENSOR.rawY(), SENSOR.rawZ() };
    }

    /**
     * Gets the status of the gyroscopic sensor.
     * @return The status.
     */
    public String getStatus() {
        return SENSOR.status();
    }

    /**
     * Resets the Y axis of the gyroscopic sensor to 0.
     */
    public void resetY() {
        SENSOR.resetZAxisIntegrator();
    }

    public GyroSensor getInternalGyroSensor() {
        return SENSOR;
    }

    /**
     * Stops the gyro's operation.
     */
    @Override
    public void stop() {}

}
