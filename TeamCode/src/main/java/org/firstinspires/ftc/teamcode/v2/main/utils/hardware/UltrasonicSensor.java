package org.firstinspires.ftc.teamcode.v2.main.utils.hardware;

import org.firstinspires.ftc.teamcode.v2.main.utils.env.Environment;

/**
 * An {@link UltrasonicSensor} can detect signals of ultrasonic wavelengths.
 */
public class UltrasonicSensor extends Hardware {

    private final com.qualcomm.robotcore.hardware.UltrasonicSensor SENSOR;

    /**
     * Creates a new {@link UltrasonicSensor}.
     * @param name The name of the sensor as defined by the robot config
     */
    public UltrasonicSensor(String name) {
        SENSOR = Environment.getHardwareMap().get(com.qualcomm.robotcore.hardware.UltrasonicSensor.class, name);
        SENSOR.resetDeviceConfigurationForOpMode();
    }

    /**
     * Gets the level of ultrasonic waves detected by the sensor.
     * @return The ultrasonic data
     */
    public double getUltrasonicData() {
        return SENSOR.getUltrasonicLevel();
    }

    /**
     * Gets the readable status of the ultrasonic sensor.
     * @return The status
     */
    public String getStatus() {
        return SENSOR.status();
    }

    public com.qualcomm.robotcore.hardware.UltrasonicSensor getInternalSensor() {
        return SENSOR;
    }

    /**
     * Stops the ultrasonic sensor's operation.
     */
    @Override
    public void stop() {

    }

}
