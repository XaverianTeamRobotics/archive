package org.firstinspires.ftc.teamcode.v2.main.utils.hardware.dangerous;

import com.qualcomm.robotcore.hardware.AnalogInput;
import com.qualcomm.robotcore.hardware.AnalogInputController;

import org.firstinspires.ftc.teamcode.v2.main.utils.env.Environment;

/**
 * An {@link AnalogDevice} is an analog device plugged into a specific analog port. While there are only two analog ports, there are four possible devices, as each port has two I/O pins allowing two individual channels. {@link AnalogDevice}s do not send voltages to analog devices, but rather receives voltages sent by said devices on their respective I/O channels. They are dangerous in a programming sense simply because of the nature of analog devices.
 */
public class AnalogDevice extends DangerousHardware {

    private final AnalogInput CONTROLLER;

    /**
     * Creates a new {@link AnalogDevice}.
     * @param name The name of the device, as specified by the robot config
     */
    public AnalogDevice(String name) {
        CONTROLLER = Environment.getHardwareMap().get(AnalogInput.class, name);
        CONTROLLER.resetDeviceConfigurationForOpMode();
    }

    /**
     * Gets the current voltage being sent by the physical analog device.
     * @return The current voltage
     */
    public double getCurrentVoltage() {
        return CONTROLLER.getVoltage();
    }

    /**
     * Gets the maximum possible voltage which can be sent by the physical analog device, as defined by the {@link AnalogDevice}'s {@link AnalogInput}'s {@link AnalogInputController}'s maximum possible voltage.
     * @return The maximum possible voltage
     */
    public double getMaxVoltage() {
        return CONTROLLER.getMaxVoltage();
    }

    public AnalogInput getInternalAnalogInputChannel() {
        return CONTROLLER;
    }

    /**
     * Stops the analog device's operation.
     */
    @Override
    public void stop() {}

}
