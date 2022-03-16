package org.firstinspires.ftc.teamcode.v2.main.utils.hardware;

import org.firstinspires.ftc.teamcode.v2.main.utils.env.Environment;

/**
 * A {@link LightSensor} can detect light.
 */
public class LightSensor extends Hardware {

    private final com.qualcomm.robotcore.hardware.LightSensor SENSOR;

    /**
     * Creates a new {@link LightSensor}.
     * @param name The name of the sensor as defined by the robot config
     * @param enableOnboardLight Whether or not to enable the light's onboard LED by default
     */
    public LightSensor(String name, boolean enableOnboardLight) {
        SENSOR = Environment.getHardwareMap().get(com.qualcomm.robotcore.hardware.LightSensor.class, name);
        SENSOR.resetDeviceConfigurationForOpMode();
        if(enableOnboardLight) {
            SENSOR.enableLed(true);
        }
    }

    public void enableOnboardLight() {
        SENSOR.enableLed(true);
    }

    public void disableOnboardLight() {
        SENSOR.enableLed(false);
    }

    /**
     * Gets the percentage of brightness of the light detected, where 0 is absolute darkness and 100 is blindingly bright, according to the sensor's understanding of the human perception of the 4 septenvigintillion photons comprised inside our universe which we call "light".
     * @return Brightness
     * @see #getRaw()
     */
    public double getBrightness() {
        return SENSOR.getLightDetected();
    }

    /**
     * Gets the readable status of the light sensor.
     * @return The status
     */
    public String getStatus() {
        return SENSOR.status();
    }

    /**
     * Gets an array with the the raw light brightness and maximum raw light brightness. The maximum raw light brightness is the raw, unscaled version of {@link #getBrightness()}'s 100%.
     * <br>
     * <br>
     * In more formal terms, {@link #getRaw()}<code>[0]</code> is a value from 0 to {@link #getRaw()}<code>[1]</code> which determines the brightness of the light, where 0 is the darkest possible perceived brightness and {@link #getRaw()}<code>[1]</code> is the brighest possible perceived brightness as perceived in the eyes of the beholder which, in this case, is the {@link LightSensor} of which {@link #getRaw()} was invoked.
     * @return Unscaled brightness
     * @see #getBrightness();
     */
    public double[] getRaw() {
        return new double[] { SENSOR.getRawLightDetected(), SENSOR.getRawLightDetectedMax() };
    }

    /**
     * Stops the light sensor's operation.
     */
    @Override
    public void stop() {}

}
