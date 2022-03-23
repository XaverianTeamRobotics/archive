package org.firstinspires.ftc.teamcode.v2.main.utils.odometry.wrapper;

import static org.firstinspires.ftc.teamcode.v2.main.utils.odometry.lib.drivers.DriveConstants.MAX_ACCEL;
import static org.firstinspires.ftc.teamcode.v2.main.utils.odometry.lib.drivers.DriveConstants.MAX_ANG_VEL;
import static org.firstinspires.ftc.teamcode.v2.main.utils.odometry.lib.drivers.DriveConstants.MAX_VEL;
import static org.firstinspires.ftc.teamcode.v2.main.utils.odometry.lib.drivers.DriveConstants.TRACK_WIDTH;

import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.roadrunner.control.PIDCoefficients;
import com.acmerobotics.roadrunner.trajectory.constraints.AngularVelocityConstraint;
import com.acmerobotics.roadrunner.trajectory.constraints.MecanumVelocityConstraint;
import com.acmerobotics.roadrunner.trajectory.constraints.MinVelocityConstraint;
import com.acmerobotics.roadrunner.trajectory.constraints.ProfileAccelerationConstraint;
import com.acmerobotics.roadrunner.trajectory.constraints.TrajectoryAccelerationConstraint;
import com.acmerobotics.roadrunner.trajectory.constraints.TrajectoryVelocityConstraint;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;

import java.util.Arrays;

/**
 * The RoadRunner configuration for autonomous control of mecanum drivetrains. These values can be updated programatically, via <code>acme-dashboard</code>, or by editing this file. Do not make anything in this class final (unless it needs to be), as final values cannot be edited via <code>acme-dashboard</code>. Due to ✨ compilation ✨ the only values which will persist between app runs are values manually edited in this file.
 * <br><br>
 * Distances are in inches.
 */
@Config
public class RoadrunnerConfig {

    public static final double TICKS_PER_REV = 1;
    public static final double MAX_RPM = 1;
    public static final boolean RUN_USING_ENCODER = true;
    public static PIDFCoefficients MOTOR_VELO_PID = new PIDFCoefficients(0, 0, 0, getMotorVelocityF(MAX_RPM / 60 * TICKS_PER_REV));

    public static double WHEEL_RADIUS = 2;
    public static double GEAR_RATIO = 1;
    public static double TRACK_WIDTH = 1;

    public static double kV = 1.0 / rpmToVelocity(MAX_RPM);
    public static double kA = 0;
    public static double kStatic = 0;

    public static double MAX_VEL = 30;
    public static double MAX_ACCEL = 30;
    public static double MAX_ANG_VEL = Math.toRadians(60);
    public static double MAX_ANG_ACCEL = Math.toRadians(60);

    public static double encoderTicksToInches(double ticks) {
        return WHEEL_RADIUS * 2 * Math.PI * GEAR_RATIO * ticks / TICKS_PER_REV;
    }

    public static double rpmToVelocity(double rpm) {
        return rpm * GEAR_RATIO * 2 * Math.PI * WHEEL_RADIUS / 60.0;
    }

    public static double getMotorVelocityF(double ticksPerSecond) {
        // see https://docs.google.com/document/d/1tyWrXDfMidwYyP_5H4mZyVgaEswhOC35gvdmP-V-5hA/edit#heading=h.61g9ixenznbx
        return 32767 / ticksPerSecond;
    }

    public static PIDCoefficients TRANSLATIONAL_PID = new PIDCoefficients(0, 0, 0);
    public static PIDCoefficients HEADING_PID = new PIDCoefficients(0, 0, 0);

    public static double LATERAL_MULTIPLIER = 1;

    public static double VX_WEIGHT = 1;
    public static double VY_WEIGHT = 1;
    public static double OMEGA_WEIGHT = 1;

    private static final TrajectoryVelocityConstraint VEL_CONSTRAINT = getVelocityConstraint(MAX_VEL, MAX_ANG_VEL, TRACK_WIDTH);
    private static final TrajectoryAccelerationConstraint ACCEL_CONSTRAINT = getAccelerationConstraint(MAX_ACCEL);

    public static TrajectoryVelocityConstraint getVelocityConstraint(double maxVel, double maxAngularVel, double trackWidth) {
        return new MinVelocityConstraint(Arrays.asList(
            new AngularVelocityConstraint(maxAngularVel),
            new MecanumVelocityConstraint(maxVel, trackWidth)
        ));
    }

    public static TrajectoryAccelerationConstraint getAccelerationConstraint(double maxAccel) {
        return new ProfileAccelerationConstraint(maxAccel);
    }

    public static double TICKS_PER_REV_LOCALIZER = 0;
    public static double WHEEL_RADIUS_LOCALIZER = 2;
    public static double GEAR_RATIO_LOCALIZER = 1;

    public static double LATERAL_DISTANCE = 10;
    public static double FORWARD_OFFSET = 4;

    public static double encoderTicksToInchesLocalizer(double ticks) {
        return WHEEL_RADIUS * 2 * Math.PI * GEAR_RATIO * ticks / TICKS_PER_REV;
    }

}
