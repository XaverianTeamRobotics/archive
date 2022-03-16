package org.firstinspires.ftc.teamcode.v2.main.utils.hardware;

import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.teamcode.v2.main.utils.env.Environment;

/**
 * A {@link ContinuousServo} represents a continuous rotation servo, or CR servo. These servos can only be sent a power to run at, not a position to run to.
 * <br>
 * <br>
 * Similar to the advice I gave in {@link FixedServo#getTargetPosition()}, I would also recommend using external encoders here if accurate positional tracking is needed, as legal continuous servos have no way of reporting their positions back to the robot.
 */
public class ContinuousServo extends Hardware {

    public final CRServo SERVO;

    /**
     * Creates a new {@link ContinuousServo}.
     * @param name The name of the servo as defined by the robot config
     * @param direction The direction of the servo
     */
    public ContinuousServo(String name, Servo.Direction direction) {
        SERVO = Environment.getHardwareMap().get(CRServo.class, name);
        SERVO.resetDeviceConfigurationForOpMode();
        if(direction.equals(Servo.Direction.FORWARD)) {
            SERVO.setDirection(DcMotorSimple.Direction.FORWARD);
        }else{
            SERVO.setDirection(DcMotorSimple.Direction.REVERSE);
        }
    }

    /**
     * Sets the power of the servo as a percentage of the maximum power it can handle.
     * @param power The power between -100 and 100
     */
    public void setPower(double power) {
        power = Range.clip(power, -100, 100);
        SERVO.setPower(power / 100);
    }

    /**
     * Gets the power of the servo as a percentage of the maximum power it can handle.
     * @return The power of the servo
     */
    public double getPower() {
        return SERVO.getPower() * 100;
    }

    public CRServo getInternalCRServoInstance() {
        return SERVO;
    }

    /**
     * Stops the servo's operation.
     */
    @Override
    public void stop() {}

}
