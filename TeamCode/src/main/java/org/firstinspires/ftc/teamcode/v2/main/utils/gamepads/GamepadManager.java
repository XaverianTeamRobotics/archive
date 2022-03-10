package org.firstinspires.ftc.teamcode.v2.main.utils.gamepads;

import com.qualcomm.robotcore.hardware.Gamepad;

import org.firstinspires.ftc.teamcode.v2.main.utils.opmode.OperationMode;

/**
 * A {@link GamepadManager} holds references to the {@link Gamepad}s currently in use. It is instantiated on every {@link OperationMode} run, and stored as an environment variable.
 */
public class GamepadManager {

    private Gamepad primary, secondary;

    /**
     * Makes a new {@link GamepadManager}, with the primary and secondary (blue and red, by default) {@link Gamepad}s.
     * @param primaryGamepad The primary (blue) gamepad
     * @param secondaryGamepad The secondary (red) gamepad
     */
    public GamepadManager(Gamepad primaryGamepad, Gamepad secondaryGamepad) {
        primary = primaryGamepad;
        secondary = secondaryGamepad;
    }

    public Gamepad getPrimaryGamepad() {
        return primary;
    }

    public Gamepad getSecondaryGamepad() {
        return secondary;
    }

    public void setPrimaryGamepad(Gamepad gamepad) {
        primary = gamepad;
    }

    public void setSecondaryGamepad(Gamepad gamepad) {
        secondary = gamepad;
    }

    /**
     * Checks if at least one of the {@link Gamepad}s is pressing the back button, or escape, which tells the {@link OperationMode} to stop.
     * @return The status of the escape button on either gamepad
     */
    public boolean isEscaping() {
        return primary.back || secondary.back;
    }

}
