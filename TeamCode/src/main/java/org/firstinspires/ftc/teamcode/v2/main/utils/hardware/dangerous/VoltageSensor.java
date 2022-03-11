package org.firstinspires.ftc.teamcode.v2.main.utils.hardware.dangerous;

import org.firstinspires.ftc.teamcode.v2.main.utils.env.Environment;

/**
 * A {@link VoltageSensor} senses voltage. There's nothing inherentely dangerous about it, but it feels dangerous in some regard&#8212;not to mention the likely danger of whatever you're using it for. I personally cant think of a use case that I would consider "safe" in programming terms, besides voltage logging.
 */
public class VoltageSensor extends DangerousHardware {

    private final com.qualcomm.robotcore.hardware.VoltageSensor SENSOR;

    /**
     * Creates a new {@link VoltageSensor}.
     * @param name The name of the voltage sensor, as defined by the robot config
     */
    public VoltageSensor(String name) {
        SENSOR = Environment.getHardwareMap().get(com.qualcomm.robotcore.hardware.VoltageSensor.class, name);
    }

    public double getVoltage() {
        return SENSOR.getVoltage();
    }

    public com.qualcomm.robotcore.hardware.VoltageSensor getInternalSensor() {
        return SENSOR;
    }

    /**
     * Stops the voltage sensor's operation.
     */
    @Override
    public void stop() {}

}
