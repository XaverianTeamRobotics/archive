package org.firstinspires.ftc.teamcode.main.utils.helpers.encoders;

import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.teamcode.main.utils.interactions.items.StandardMotor;

import java.util.Hashtable;

public class CustomEncoderDriver {
    private final StandardMotor topLeft;
    private final StandardMotor topRight;
    private final StandardMotor bottomLeft;
    private final StandardMotor bottomRight;

    private double driveDistanceTopLeft;
    private double driveDistanceTopRight;
    private double driveDistanceBottomLeft;
    private double driveDistanceBottomRight;
    private double toleranceInInches;

    public CustomEncoderDriver(StandardMotor topLeft, StandardMotor topRight, StandardMotor bottomLeft, StandardMotor bottomRight) {
        this.topLeft = topLeft;
        this.topRight = topRight;
        this.bottomLeft = bottomLeft;
        this.bottomRight = bottomRight;
    }

    private double getCountsPerInch(StandardMotor motor) {
        return motor.getCountsPerInch();
    }

    public double convertInchToRevs(double inches) {
        return topLeft.getCountsPerInch() * inches;
    }

    private double convertInchToRevs(double inches, StandardMotor standardMotor) {
        return standardMotor.getCountsPerInch() * inches;
    }

    public void setDriveDistance(double driveDistanceTopLeft, double driveDistanceTopRight, double driveDistanceBottomLeft, double driveDistanceBottomRight, double toleranceInInches) {
        setDriveDistanceTopLeft(driveDistanceTopLeft);
        setDriveDistanceTopRight(driveDistanceTopRight);
        setDriveDistanceBottomLeft(driveDistanceBottomLeft);
        setDriveDistanceBottomRight(driveDistanceBottomRight);

        this.toleranceInInches = toleranceInInches;

    }

    public void setDriveDistanceTopLeft(double driveDistance) {
        this.driveDistanceTopLeft = driveDistance;
    }
    public void setDriveDistanceTopRight(double driveDistance) {
        this.driveDistanceTopRight = driveDistance;
    }
    public void setDriveDistanceBottomLeft(double driveDistance) {
        this.driveDistanceBottomLeft = driveDistance;
    }
    public void setDriveDistanceBottomRight(double driveDistance) {
        this.driveDistanceBottomRight = driveDistance;
    }

    public void resetMotorDistances() {
        topLeft.getDcMotor().setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        topRight.getDcMotor().setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        bottomLeft.getDcMotor().setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        bottomRight.getDcMotor().setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
    }

    public void drive() {
        double error;
        error = driveDistanceTopLeft - topLeft.getDcMotor().getCurrentPosition();

        if (Math.abs(error) > convertInchToRevs(toleranceInInches, topLeft)) {
            topLeft.getDcMotor().setPower(error * 100);
        }

        error = driveDistanceTopRight - topRight.getDcMotor().getCurrentPosition();

        if (Math.abs(error) > convertInchToRevs(toleranceInInches, topRight)) {
            topRight.getDcMotor().setPower(error * 100);
        }

        error = driveDistanceBottomLeft - bottomLeft.getDcMotor().getCurrentPosition();

        if (Math.abs(error) > convertInchToRevs(toleranceInInches, bottomLeft)) {
            bottomLeft.getDcMotor().setPower(error * 100);
        }

        error = driveDistanceBottomRight - bottomRight.getDcMotor().getCurrentPosition();

        if (Math.abs(error) > convertInchToRevs(toleranceInInches, bottomRight)) {
            bottomRight.getDcMotor().setPower(error * 100);
        }
    }
}
