package org.firstinspires.ftc.teamcode.main.utils.autonomous.location.pipeline;

import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.main.utils.interactions.items.StandardIMU;

public class VelocityTracker {
    private StandardIMU imu;

    private final ElapsedTime timer = new ElapsedTime();
    private double currentVel = 0;
    private double currentDisplacement = 0;
    private StandardIMU.VelocityDataPoint accelerationDirection = StandardIMU.VelocityDataPoint.X;

    public VelocityTracker(StandardIMU imu) {
        this.imu = imu;
    }

    public double getVelocity() {
        update();
        return currentVel;
    }

    public double getCurrentDisplacement() {
        update();
        return currentDisplacement;
    }

    @SuppressWarnings("ConstantConditions")
    public void update() {
        double acc = imu.getAcceleration().get(accelerationDirection);

        double deltaX = currentVel * timer.seconds() + 0.5 * acc * Math.pow(timer.seconds(), 2);

        currentDisplacement += deltaX;
        currentVel += acc * timer.seconds();
        timer.reset();
    }

    public StandardIMU getImu() {
        return imu;
    }

    public void setImu(StandardIMU imu) {
        this.imu = imu;
    }

    public StandardIMU.VelocityDataPoint getAccelerationDirection() {
        return accelerationDirection;
    }

    public void setAccelerationDirection(StandardIMU.VelocityDataPoint accelerationDirection) {
        this.accelerationDirection = accelerationDirection;
    }
}
