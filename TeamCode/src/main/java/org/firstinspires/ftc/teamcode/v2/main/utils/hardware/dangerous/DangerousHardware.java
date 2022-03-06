package org.firstinspires.ftc.teamcode.v2.main.utils.hardware.dangerous;

import org.firstinspires.ftc.teamcode.v2.main.utils.hardware.Hardware;

/**
 * A {@link DangerousHardware} instance refers to lower-level types of hardware, or dangerous hardware. This includes things like raw analog channels, which are usually a bit dangerous to use.
 * <br>
 * <br>
 * To implement, just follow {@link Hardware}'s guide. Since this is such low-level, you're kind of expected to know what to do and are on your own for any other differences between safe and dangerous hardware. There shouldn't be much of a difference, but maybe there is. There's at least a 43.7% chance you're smarter than me, so.
 */
public abstract class DangerousHardware extends Hardware {

    @Override
    public boolean isDangerous() {
        return true;
    }

}
