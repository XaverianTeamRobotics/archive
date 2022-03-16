package org.firstinspires.ftc.teamcode.v2.main.utils.hardware;

import com.qualcomm.robotcore.hardware.DistanceSensor;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.teamcode.v2.main.utils.env.Environment;

/**
 * A {@link DigitalDistanceSensor} represents a digitally-based distance sensor. This type of distance sensor is plugged into a digital port.
 */
public class DigitalDistanceSensor extends Hardware {

    private DistanceSensor SENSOR;

    /**
     * Creates a new {@link DigitalDistanceSensor}.
     * @param name The name of the distance sensor, as defined by the robot config
     */
    public DigitalDistanceSensor(String name) {
        SENSOR = Environment.getHardwareMap().get(DistanceSensor.class, name);
        SENSOR.resetDeviceConfigurationForOpMode();
    }

    public double getDistance(DistanceUnit unit) {
        return SENSOR.getDistance(unit);
    }

    public DistanceSensor getInternalSensor() {
        return SENSOR;
    }

    /**
     * Stops the distance sensor's operation.
     */
    @Override
    public void stop() {}

}
