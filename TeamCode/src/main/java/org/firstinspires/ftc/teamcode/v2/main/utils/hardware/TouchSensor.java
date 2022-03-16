package org.firstinspires.ftc.teamcode.v2.main.utils.hardware;

import android.text.method.Touch;

import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.teamcode.v2.main.utils.env.Environment;

/**
 * A {@link TouchSensor} represents a touchpad, trackpad, button, hall effect sensor, or anything else which is considered a touch sensor by Qualcomm under the hood.
 */
public class TouchSensor extends Hardware {

    private final com.qualcomm.robotcore.hardware.TouchSensor SENSOR;

    /**
     * Creates a new {@link TouchSensor}.
     * @param name The name of the sensor, as defined in the robot config
     */
    public TouchSensor(String name) {
        SENSOR = Environment.getHardwareMap().get(com.qualcomm.robotcore.hardware.TouchSensor.class, name);
        SENSOR.resetDeviceConfigurationForOpMode();
    }

    /**
     * Checks whether the sensor is being touched.
     * @return True if the sensor is being touched, false otherwise
     */
    public boolean isTouched() {
        return SENSOR.isPressed();
    }

    /**
     * Returns the force applied to the sensor on a scale of 0-100. For some rudimentary touch sensors, this will only ever be 0 or 100.
     * @return The force applied
     */
    public double getForce() {
        return Range.clip(SENSOR.getValue() * 100, 0, 100);
    }

    public com.qualcomm.robotcore.hardware.TouchSensor getInternalSensor() {
        return SENSOR;
    }

    /**
     * Stops the touch sensor.
     */
    @Override
    public void stop() {}

}
