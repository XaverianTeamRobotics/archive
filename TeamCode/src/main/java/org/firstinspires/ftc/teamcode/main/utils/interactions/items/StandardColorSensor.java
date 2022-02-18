package org.firstinspires.ftc.teamcode.main.utils.interactions.items;

import android.graphics.Color;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.NormalizedColorSensor;
import com.qualcomm.robotcore.hardware.NormalizedRGBA;

/**
 * A StandardColorSensor detects colors. It returns all of its colors in the range between 0-255.
 */
public class StandardColorSensor extends InteractionItem {

    NormalizedColorSensor SENSOR;

    /**
     * Creates a new StandardColorSensor, which can sense colors.
     * @param hardware The HardwareMap to get the physical servo from
     * @param name The servo's name in the configuration
     */
    public StandardColorSensor(HardwareMap hardware, String name) {
        SENSOR = hardware.get(NormalizedColorSensor.class, name);
    }

    /**
     * Gets the RGBA values of the sensor, formatted as [ Red, Green, Blue, Alpha ].
     * @return the RGBA values of the sensor
     */
    public int[] getRGBA() {
        NormalizedRGBA h = SENSOR.getNormalizedColors();
        return new int[] { (int) (h.red * 255), (int) (h.green * 255), (int) (h.blue * 255), (int) (h.alpha * 255) };
    }

    /**
     * Gets the HSV values of the sensor, formatted as [ Hue, Saturation, Value ].
     * @return The HSV values of the sensor
     */
    public double[] getHSV() {
        float[] colors = new float[3];
        int[] vals = getRGBA();
        Color.RGBToHSV(vals[0], vals[1], vals[2], colors);
        double[] colorsd = new double[3];
        colorsd[0] = colors[0];
        colorsd[1] = colors[1];
        colorsd[2] = colors[2];
        return colorsd;
    }

    /**
     * Gets the GSV value of the sensor, also known as grayscale.
     * @return The GSV value of the sensor
     */
    public int getGSV() {
        int[] vals = getRGBA();
        return vals[0] + vals[1] + vals[2];
    }

    /**
     * Gets the ARGB value of the sensor. This is <strong>NOT</strong> the same as {@link #getRGBA()}. The ARGB value is a packed 32-bit integer (to be interpreted as unsigned) containing all 4 color channels. It can be unpacked via bitshifting and bitmasking.
     * @return The ARGB value of the sensor
     */
    public int getARGB() {
        return SENSOR.getNormalizedColors().toColor();
    }

    public NormalizedColorSensor getInternalSensor() {
        return SENSOR;
    }

    @Override
    public void stop() {}

    @Override
    public boolean isInputDevice() {
        return false;
    }

    @Override
    public boolean isOutputDevice() {
        return true;
    }

}
