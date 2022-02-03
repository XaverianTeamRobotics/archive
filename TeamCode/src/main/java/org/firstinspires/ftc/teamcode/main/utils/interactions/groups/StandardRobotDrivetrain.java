package org.firstinspires.ftc.teamcode.main.utils.interactions.groups;

/**
 * <p>A StandardRobotDrivetrain represents the motors which control movement of the robot in a way similar to most robots, controlling each wheel on its own.</p>
 * <p>For example, a mechanum drivetrain would be a StandardRobotDrivetrain, as every wheel of the drivetrain is controlled by itself.</p>
 */
public abstract class StandardRobotDrivetrain extends StandardDrivetrain {

    public abstract void driveDistance(int rightTopDistance, int rightBottomDistance, int leftTopDistance, int leftBottomDistance, int speed);

    public abstract void driveWithEncoder(int rightTopSpeed, int rightBottomSpeed, int leftTopSpeed, int leftBottomSpeed);

    public abstract void driveWithoutEncoder(int rightTopPower, int rightBottomPower, int leftTopPower, int leftBottomPower);

}
