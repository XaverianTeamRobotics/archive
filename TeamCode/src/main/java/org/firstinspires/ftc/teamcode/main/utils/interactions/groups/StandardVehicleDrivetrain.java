package org.firstinspires.ftc.teamcode.main.utils.interactions.groups;

import org.firstinspires.ftc.teamcode.main.utils.interactions.items.StandardMotor;

/**
 * <p>A StandardVehicleDrivetrain represents the motors which control movement of the robot in a way similar to a real-life vehicle, controlling each side on its own.</p>
 * <p>For example, a carfax drivetrain would be a StandardVehicleDrivetrain because a carfax drives each side of itself differently. For example, the right sie of a carfax is controlled differently than the left side, as opposed to each wheel on each side being controlled differently than each other wheel.</p>
 */
public abstract class StandardVehicleDrivetrain extends StandardDrivetrain {

    public abstract void driveDistance(int rightDistance, int leftDistance, int speed);

    public abstract void driveDistance(int distance, int speed);

    public abstract void driveWithEncoder(int rightSpeed, int leftSpeed);

    public abstract void driveWithEncoder(int speed);

    public abstract void driveWithoutEncoder(int rightPower, int leftPower);

    public abstract void driveWithoutEncoder(int power);

    public abstract StandardMotor getRightTop();

    public abstract StandardMotor getRightBottom();

    public abstract StandardMotor getLeftTop();

    public abstract StandardMotor getLeftBottom();
}
