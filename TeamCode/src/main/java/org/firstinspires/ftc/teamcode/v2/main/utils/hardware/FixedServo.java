package org.firstinspires.ftc.teamcode.v2.main.utils.hardware;

import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.teamcode.v2.main.utils.env.Environment;

/**
 * A {@link FixedServo} represents a servo with a fixed range of motion which can be commanded to move to a specific position.
 */
public class FixedServo extends Hardware {

    public final Servo SERVO;

    /**
     * Creates a new {@link FixedServo}.
     * @param name The name of the servo as identified by the hardware map
     * @param direction The direction of the servo
     */
    public FixedServo(String name, Servo.Direction direction) {
        SERVO = Environment.getHardwareMap().get(Servo.class, name);
        SERVO.setDirection(direction);
    }

    /**
     * Creates a new {@link FixedServo}.
     * @param name The name of the servo as identified by the hardware map
     * @param direction The direction of the servo
     * @param min The minimum boundary of the servo, expressed as a percentage of the servo's full range of motion (with a min of 20, the servo can only travel from 20% of its range of motion to max)
     * @param max The maximum boundary of the servo, expressed as a percentage of the servo's full range of motion (with a max of 80, the servo can only travel from min to 80% of its range of motion)
     */
    public FixedServo(String name, Servo.Direction direction, int min, int max) {
        SERVO = Environment.getHardwareMap().get(Servo.class, name);
        SERVO.setDirection(direction);
        SERVO.scaleRange((Range.clip(min, 0, 100)) / 100.0, (Range.clip(max, 0, 100)) / 100.0);
    }

    /**
     * The position the servo should travel to, expressed as a percentage of its range of motion, which is either its full range or a scaled range if constructed with a scaled range. For example, with a servo which can travel a full 360 degrees clockwise, passing 25 would mean the servo would travel to its 3 o'clock position as its 3, or 90 degrees clockwise, is 25% of 360 degrees.
     * @param position The position of the servo
     */
    public void setPosition(double position) {
        SERVO.setPosition(position / 100);
    }

    /**
     * Gets the <strong>target</strong> position of the servo. This will only return the position the servo was commanded to go to, not the current position of the servo. This is because FTC legal servos have no way to transmit their current position to the robot, at least internally.
     * <br>
     * <br>
     * If needed, there are a variety of methods to track servo position using encoders, potentiometors, limit switches, and other various sensors.
     * @return The target position of the servo
     */
    public double getTargetPosition() {
        return SERVO.getPosition() * 100;
    }

    public Servo getInternalServo() {
        return SERVO;
    }

    /**
     * Stops the servo's operation.
     */
    @Override
    public void stop() {}

}
