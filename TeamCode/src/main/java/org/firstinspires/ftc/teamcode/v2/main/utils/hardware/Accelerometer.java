package org.firstinspires.ftc.teamcode.v2.main.utils.hardware;

import com.qualcomm.robotcore.hardware.AccelerationSensor;

import org.firstinspires.ftc.robotcore.external.navigation.Acceleration;
import org.firstinspires.ftc.teamcode.v2.main.utils.env.Environment;

/**
 * An {@link Accelerometer} is a sensor to detect acceleration.
 */
public class Accelerometer extends Hardware {

    private final AccelerationSensor SENSOR;

    /**
     * Creates a new {@link Accelerometer}.
     * @param name The name of the sensor as defined by the robot config.
     */
    public Accelerometer(String name) {
        SENSOR = Environment.getHardwareMap().get(AccelerationSensor.class, name);
        SENSOR.resetDeviceConfigurationForOpMode();
    }

    /**
     * Gets the acceleration detected.
     * @return The acceleration.
     */
    public Acceleration getAcceleration() {
        return SENSOR.getAcceleration();
    }

    /**
     * Gets the status of the sensor.
     * @return The status of the sensor.
     */
    public String getStatus() {
        return SENSOR.status();
    }

    /**
     * Gets the internal acceleration sensor behind the accelerometer.
     * @return The acceleration sensor.
     */
    public AccelerationSensor getInternalSensor() {
        return SENSOR;
    }

    /**
     * Stops the accelerometer's operation.
     */
    @Override
    public void stop() {}

}
