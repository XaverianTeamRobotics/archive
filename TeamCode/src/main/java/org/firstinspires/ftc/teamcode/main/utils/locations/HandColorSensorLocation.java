package org.firstinspires.ftc.teamcode.main.utils.locations;

import com.qualcomm.robotcore.hardware.HardwareMap;
import org.firstinspires.ftc.teamcode.main.utils.interactions.InteractionSurface;
import org.firstinspires.ftc.teamcode.main.utils.interactions.items.StandardColorSensor;
import org.firstinspires.ftc.teamcode.main.utils.resources.Resources;

public class HandColorSensorLocation extends Location {

    private StandardColorSensor SENSOR;

    public HandColorSensorLocation(HardwareMap hardware) {
        try {
            SENSOR = new StandardColorSensor(hardware, Resources.Hand.Sensors.HandColor);
        } catch(Exception ignored) {}
    }

    /**
     * Returns the distance detected by the sensor.
     * @return The distance in millimeters
     */
    public int[] returnOutput() {
        if(SENSOR == null) {
            return null;
        }
        return SENSOR.getRGBA();
    }

    @Override
    public void stop() {
        if(SENSOR == null) {
            return;
        }
        SENSOR.stop();
    }

    @Override
    public boolean isInputLocation() {
        return false;
    }

    @Override
    public boolean isOutputLocation() {
        return true;
    }

    @Override
    public InteractionSurface getInternalInteractionSurface() {
        return SENSOR;
    }

}
