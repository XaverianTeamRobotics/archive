package org.firstinspires.ftc.teamcode.v2.main.utils.hardware;

import android.graphics.Color;

import com.qualcomm.robotcore.hardware.NormalizedColorSensor;
import com.qualcomm.robotcore.hardware.NormalizedRGBA;
import com.sun.tools.doclint.Env;

import org.firstinspires.ftc.teamcode.v2.main.utils.env.Environment;

/**
 * A {@link ColorSensor} represents a sensor which detects colors.
 */
public class ColorSensor extends Hardware {

    private final NormalizedColorSensor NORMALIZED_COLORS;
    private final String NAME;

    /**
     * Creates a new {@link ColorSensor}.
     * @param name The name of the sensor as defined by the robot config
     */
    public ColorSensor(String name) {
        NORMALIZED_COLORS = Environment.getHardwareMap().get(NormalizedColorSensor.class, name);
        NAME = name;
    }

    /**
     * Gets the RGBA (Red, Green, Blue, Alpha) value sensed by the sensor.
     * @return The RGBA value in an array such that [ R, G, B, A ]
     */
    public int[] getRGBA() {
        int[] vals = new int[4];
        vals[0] = (int) (NORMALIZED_COLORS.getNormalizedColors().red * 255);
        vals[1] = (int) (NORMALIZED_COLORS.getNormalizedColors().green * 255);
        vals[2] = (int) (NORMALIZED_COLORS.getNormalizedColors().blue * 255);
        vals[3] = (int) (NORMALIZED_COLORS.getNormalizedColors().alpha * 255);
        return vals;
    }

    /**
     * Gets the HSV (Hue, Saturation, Value) value sensed by the sensor.
     * @return The HSV value in an array such that [ H, S, V ]
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
     * Gets the GSV (Grayscale) value sensed by the sensor.
     * @return The GSV value
     */
    public int getGSV() {
        int[] vals = getRGBA();
        return vals[0] + vals[1] + vals[2];
    }

    /**
     * Gets the sensor's gain level.
     * @return The gain
     */
    public double getGain() {
        return NORMALIZED_COLORS.getGain();
    }

    /**
     * Gets the <strong>raw</strong> ARGB data from the sensor. All other methods of data are normalized, such that they are within the acceptable values of 0-255 in the SRGB colorspace and are returned in everyday formats like RGBA. The raw data, however, is returned exactly as the sensor sees it with no normalization. The data is packed, meaning bitshifting and masking must be used to extract the data.
     * <br>
     * <br>
     * Counterintuitively, this method actually takes significantly longer to execute than all other methods here due to the nature of color normalization in the FTC SDK. I would warn against calling this unless it's necessary. If you're simply looking for an ARGB value and are okay with it being normalized, I would recommend <code>ColorSensor#getInternalSensor#getNormalizedColors#toColor</code>.
     * @return The raw ARGB value in a packed 32-bit integer (to be interpreted as unsigned) containing all 4 color channels
     */
    public int getRaw() {
        return Environment.getHardwareMap().get(com.qualcomm.robotcore.hardware.ColorSensor.class, NAME).argb();
    }

    public NormalizedColorSensor getInternalSensor() {
        return NORMALIZED_COLORS;
    }

    /**
     * Stops the sensor's operation.
     */
    @Override
    public void stop() {}

}
