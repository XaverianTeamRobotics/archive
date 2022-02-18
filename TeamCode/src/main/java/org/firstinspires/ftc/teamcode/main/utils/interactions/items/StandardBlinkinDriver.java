package org.firstinspires.ftc.teamcode.main.utils.interactions.items;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

/**
 * A StandardBlinkinDriver represents a REV Robotics Blinkin LED Driver. It can be used to control LEDs.
 */
public class StandardBlinkinDriver {

    private final Servo SERVO;

    /**
     * Creates a new StandardBlinkinDriver.
     * @param hardware The hardware map of the Blinkin driver
     * @param name The name of the Blinkin driver
     */
    public StandardBlinkinDriver(HardwareMap hardware, String name) {
        SERVO = hardware.get(Servo.class, name);
    }

    /**
     * Sets the LED to a behavior identified by a numerical ID between 0.2525 and 0.7475. Behaviors consist of solid colors, patterns with hardcoded colors, and patterns with colors based on the positions of the Color 1 and Color 2 dials on the real-life Blinkin driver itself.
     * @param id The behavior's ID
     * @throws IllegalArgumentException The exception thrown when the ID is not a valid Blinkin pattern ID
     */
    public void setLightBehavior(double id) throws IllegalArgumentException {
        if(id >= 0.2525 && id <= 0.7475) {
            SERVO.setPosition(id);
        }else{
            throw new IllegalArgumentException(id + " is not a valid Blinkin LED driver ID!");
        }
    }

    /**
     * Gets the light's current behavior's numerical ID, a number between 0.2525 and 0.7475.
     * @return The behavior's ID
     */
    public double getCurrentLightBehavior() {
        return SERVO.getPosition();
    }

    /**
     * Returns the internal {@link Servo} of the Blinkin driver. Yes it is a servo, Blinkin drivers are technically servos.
     * @return The Blinkin driver
     */
    public Servo getInternalServo() {
        return SERVO;
    }

}
