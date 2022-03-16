package org.firstinspires.ftc.teamcode.v2.main.utils.hardware;

import com.qualcomm.robotcore.hardware.IrSeekerSensor;

import org.firstinspires.ftc.teamcode.v2.main.utils.env.Environment;

/**
 * An {@link InfraredSensor} can detect infrared signals from a beacon and determine the robot's distance and rotation relative to said beacon.
 */
public class InfraredSensor extends Hardware {

    private final IrSeekerSensor SENSOR;

    /**
     * Creates a new {@link InfraredSensor}.
     * @param name The name of the sensor
     * @param mode The mode of the sensor
     */
    public InfraredSensor(String name, IrSeekerSensor.Mode mode) {
        SENSOR = Environment.getHardwareMap().get(IrSeekerSensor.class, name);
        SENSOR.resetDeviceConfigurationForOpMode();
        SENSOR.setMode(mode);
    }

    /**
     * Creates a new {@link InfraredSensor}.
     * @param name The name of the sensor
     * @param mode The mode of the sensor
     * @param threshold The minimum signal strength for the sensor to interperet as a valid signal
     */
    public InfraredSensor(String name, IrSeekerSensor.Mode mode, double threshold) {
        SENSOR = Environment.getHardwareMap().get(IrSeekerSensor.class, name);
        SENSOR.setMode(mode);
        SENSOR.setSignalDetectedThreshold(threshold);
    }

    /**
     * Gets the angle of the sensor relative to the beacon.
     * @return The angle
     */
    public double getAngle() {
        return SENSOR.getAngle();
    }

    /**
     * Gets the strength of the signal detected if one exists, which can be used to determine the sensor's distance relative to the beacon.
     * @return The strength of the signal
     */
    public double getStrength() {
        return SENSOR.getStrength();
    }

    /**
     * Checks whether or not a valid signal is detected, regardless of strength.
     * @return True if a valid signal is detected
     */
    public boolean isSignalDetected() {
        return SENSOR.signalDetected();
    }

    /**
     * Sets the rate, in hertz, of the sensor.
     * @param mode The mode referring to the desired rate
     */
    public void setMode(IrSeekerSensor.Mode mode) {
        SENSOR.setMode(mode);
    }

    /**
     * Sets the minimum signal strength for a detected infrared signal to be considered valid.
     * @param threshold The minimum signal strength
     */
    public void setThreshold(double threshold) {
        SENSOR.setSignalDetectedThreshold(threshold);
    }

    /**
     * Gets the rate, in hertz, of the sensor.
     * @return The mode referring to the current rate
     */
    public IrSeekerSensor.Mode getMode() {
        return SENSOR.getMode();
    }

    /**
     * Gets the minimum signal strength for a detected infrared signal to be considered vald.
     * @return The mimumum signal strength
     */
    public double getThreshold() {
        return SENSOR.getSignalDetectedThreshold();
    }

    /**
     * Gets the internal {@link IrSeekerSensor} controlling this {@link InfraredSensor}. This does not get the <em>individual</em> sensors, rather the collection of sensors considered to be part of this seeker. To find each individual sensor in the seeker, use {@link IrSeekerSensor#getIndividualSensors()}.
     * @return The internal seeker controlling this sensor
     */
    public IrSeekerSensor getInternalSeeker() {
        return SENSOR;
    }

    /**
     * Stops the infrared sensor's operation.
     */
    @Override
    public void stop() {}

}
