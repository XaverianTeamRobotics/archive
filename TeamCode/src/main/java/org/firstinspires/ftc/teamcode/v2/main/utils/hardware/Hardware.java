package org.firstinspires.ftc.teamcode.v2.main.utils.hardware;

import com.qualcomm.robotcore.hardware.HardwareDevice;

import org.firstinspires.ftc.teamcode.v2.main.utils.env.Environment;

/**
 * A {@link Hardware} object refers to a physical piece of hardware on the real-life robot which can be controlled. Each subclass of {@link Hardware} wraps a subclass, or multiple subclasses, of a {@link com.qualcomm.robotcore.hardware.HardwareDevice}. Using subclasses of {@link Hardware} help simplify the creation and control of physical hardware.
 * <br>
 * <br>
 * To implement, I recommend creating a constructor which takes a string. Said string is the name of the device you're targeting, as defined in the robot's configuration, which you can find and edit on the Driver Station app. The constructor will save the output of the call {@link com.qualcomm.robotcore.hardware.HardwareMap#get(Class, String)} on {@link Environment#getHardwareMap()} in a field. Obviously, the class/interface of the wrapped {@link com.qualcomm.robotcore.hardware.HardwareDevice} and the provided string should be passed to the method. After this, the constructor should call {@link HardwareDevice#resetDeviceConfigurationForOpMode()} on the field, and then configure the field's device however it needs to be configured.
 */
public abstract class Hardware {

    /**
     * This method should be called when a piece of hardware is no longer needed. Oftentimes this method will do nothing, but occasionally it will.
     * <br>
     * <br>
     * To implement this into your own subclass, it probably doesn't need to do anything besides existing. A common misconception is that this method should call <code>#close();</code> on whatever {@link com.qualcomm.robotcore.hardware.HardwareDevice} you're wrapping, but this usually doesn't do what you want it to do. If you do want to call that, however, go ahead.
     */
    public abstract void stop();

    public boolean isDangerous() {
        return false;
    }

}
