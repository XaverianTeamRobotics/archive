package org.firstinspires.ftc.teamcode.main.utils.autonomous.location.pipeline;

import android.graphics.Path;

import androidx.annotation.NonNull;

import org.firstinspires.ftc.teamcode.main.utils.autonomous.EncoderTimeoutManager;
import org.firstinspires.ftc.teamcode.main.utils.helpers.geometry.Angle;
import org.firstinspires.ftc.teamcode.main.utils.autonomous.location.pipeline.Axis.AxisReading;
import org.firstinspires.ftc.teamcode.main.utils.autonomous.sensors.NavigationSensorCollection;
import org.firstinspires.ftc.teamcode.main.utils.interactions.groups.StandardVehicleDrivetrain;
import org.firstinspires.ftc.teamcode.main.utils.interactions.items.StandardIMU;
import org.jetbrains.annotations.NotNull;

public class PositionSystem {
    public Axis leftToRight;
    public Axis upAndDown;

    public int rawNorthReading;
    public int rawEastReading;
    public int rawWestReading;

    public float northOffset = 0;

    public CoordinateSystem coordinateSystem;

    private StandardVehicleDrivetrain drivetrain = null;
    public StandardIMU imu;
    public StandardIMU.DataPoint imuDirection = StandardIMU.DataPoint.HEADING;
    public StandardIMU.ReturnData<StandardIMU.DataPoint, Float> imuData;
    public int imuOffset = 0;

    public PositionSystem(@NonNull NavigationSensorCollection sensors) {
        leftToRight = new Axis(sensors.east, sensors.west);
        upAndDown = new Axis(sensors.north, sensors.north);
        this.imu = sensors.imu;

        imuOffset = sensors.imuOffset;

        coordinateSystem = new CoordinateSystem();
    }

    public StandardVehicleDrivetrain getDrivetrain() {
        return drivetrain;
    }
    public void setDrivetrain(StandardVehicleDrivetrain drivetrain) {
        this.drivetrain = drivetrain;
    }

    public void getAndEvalReadings() {
        updateAngle();

        AxisReading ew = leftToRight.getReadings();
        AxisReading ns = upAndDown.getReadings();

        evalReadings(ew, ns);
    }
    private void evalReadings(@NotNull AxisReading eastWest, @NotNull AxisReading northSouth) {
        boolean eastWestValid = true;
        boolean northSouthValid = true;

        rawNorthReading = (int) northSouth.sensor1;
        rawEastReading = (int) eastWest.sensor1;
        rawWestReading = (int) eastWest.sensor2;

        // Check which axes are valid
        if (eastWest.sensor1 + eastWest.interSensorDistance + eastWest.sensor2 + 10 > CoordinateSystem.maxWidthInCM)
            eastWestValid = false;

        if (northSouth.sensor1 + northSouth.interSensorDistance + northSouth.sensor2 + 10 > CoordinateSystem.maxLengthInCM)
            northSouthValid = false;

        if (!northSouth.sensor1Valid & !northSouth.sensor2Valid) northSouthValid = false;
        if (!eastWest.sensor1Valid & !eastWest.sensor2Valid) eastWestValid = false;

        northSouth.sensor1 = northSouth.sensor1 + northOffset;

        double x = eastWest.sensor1;
        double y = northSouth.sensor1;

        // do some geometry-I honors level math
        int angleDegrees = coordinateSystem.angle.asDegree();
        double angleRadians = Math.toRadians(angleDegrees);

        x = x * Math.sin(angleRadians);
        y = y * Math.cos(angleRadians);

        if (x < 0 && eastWest.sensor2Valid) {
            x = eastWest.sensor2 * Math.sin(angleRadians);
        }
        else if (x < 0) {
            x = CoordinateSystem.maxWidthInCM - eastWest.interSensorDistance - Math.abs(x);
        }

        if (y < 0) {
            y = CoordinateSystem.maxLengthInCM - northSouth.interSensorDistance - Math.abs(y);
        }

        // Some special exceptions
        if (angleDegrees == 90) {
            x = CoordinateSystem.maxWidthInCM - northSouth.interSensorDistance - northSouth.sensor1;
            y = eastWest.sensor2;
        } else if (angleDegrees == 180) {
            x = eastWest.sensor2;
        } else if (angleDegrees == -90 || angleDegrees == 270) {
            y = eastWest.sensor1;
            x = northSouth.sensor1;
        }

        // Do our updating
        if (eastWestValid && northSouthValid) {
            coordinateSystem.update(CoordinateSystem.FieldCoordinates.make(x, y));
        } else if (eastWestValid) {
            coordinateSystem.update(CoordinateSystem.FieldCoordinates.make(x, coordinateSystem.current.y));
        } else if (northSouthValid) {
            coordinateSystem.update(CoordinateSystem.FieldCoordinates.make(coordinateSystem.current.x, y));
        }
    }
    private void updateCoordinateSystem(CoordinateSystem.FieldCoordinates coordinates) {
        coordinateSystem.update(coordinates);
    }
    public void setAngle(Angle angle) {
        coordinateSystem.angle = angle;
    }
    public void updateAngle() {
        switch (imuDirection) {
            case HEADING:
                imu.setHeadingOffset(imuOffset);
                break;
            case PITCH:
                imu.setPitchOffset(imuOffset);
                break;
            case ROLL:
                imu.setRollOffset(imuOffset);
                break;
        }

        imuData = imu.getCompassData();

        coordinateSystem.angle.convert(Angle.AngleUnit.DEGREE);
        coordinateSystem.angle.value = imuData.get(imuDirection);
    }

    public float normalizeDegrees(Float input) {
        if (input < 0) {
            input = Math.abs(input) + 180;
        }

        return input;
    }

    public void addDistance(double distance, double angleDegrees) {
        coordinateSystem.update(CoordinateSystem.FieldCoordinates.make(
                coordinateSystem.current.x + distance * Math.cos(Math.toRadians(angleDegrees)),
                coordinateSystem.current.y + distance * Math.sin(Math.toRadians(angleDegrees))));
    }
    public void encoderDrive(double distance) {
        if (drivetrain != null) {
            encoderDrive((int) distance, (int) -distance);

            addDistance(distance, this.coordinateSystem.angle.asDegree());
        }
    }
    public void encoderDrive(float distanceLeft, float distanceRight) {
        if (drivetrain != null) {
            drivetrain.driveDistance((int) distanceLeft, (int) distanceRight, 30);
        }
    }

    public boolean areMotorsBusy() {
        return drivetrain.getRightTop().getDcMotor().isBusy() &&
                drivetrain.getRightBottom().getDcMotor().isBusy() &&
                drivetrain.getLeftTop().getDcMotor().isBusy() &&
                drivetrain.getLeftBottom().getDcMotor().isBusy();
    }

    public void motorHold(int timeout) {
        EncoderTimeoutManager timeoutManager = new EncoderTimeoutManager(timeout);
        while (areMotorsBusy() || !timeoutManager.hasTimedOut()) {}
    }

    @Deprecated
    public void turnByIMU(int absoluteDegree, Path.Direction turnDirection) {
        if (drivetrain != null) {

            updateAngle();

            int leftInches = 1;
            int rightInches = -1;

            switch (turnDirection) {
                case CW:
                    rightInches = -rightInches;
                    while (normalizeDegrees((float) coordinateSystem.angle.asDegree()) <= normalizeDegrees((float) absoluteDegree)) {
                        encoderDrive(leftInches, rightInches);
                        while (drivetrain.getRightTop().getDcMotor().isBusy() || drivetrain.getLeftTop().getDcMotor().isBusy() || drivetrain.getLeftBottom().getDcMotor().isBusy() || drivetrain.getRightBottom().getDcMotor().isBusy()) {}
                    }
                    break;
                case CCW:
                    leftInches = -leftInches;
                    while (normalizeDegrees((float) coordinateSystem.angle.asDegree()) >= normalizeDegrees((float) absoluteDegree)) {
                        encoderDrive(leftInches, rightInches);
                        while (drivetrain.getRightTop().getDcMotor().isBusy() || drivetrain.getLeftTop().getDcMotor().isBusy() || drivetrain.getLeftBottom().getDcMotor().isBusy() || drivetrain.getRightBottom().getDcMotor().isBusy()) {}
                    }
                    break;
            }
        }
    }

    public void turnWithCorrection(Angle a) {
        int left = 10;
        int right = 10;

        double angle = a.asDegree();

        if (angle > 180) {
            angle = angle - 180;
        }

        if (angle < 0) {
            left = -left;
            right = -right;
        }

        updateAngle();
        angle = imuData.getHeading() - angle;

        EncoderTimeoutManager timeoutManager = new EncoderTimeoutManager(5000);

        drivetrain.driveWithEncoder(right, left);

        while (!timeoutManager.hasTimedOut() || areMotorsBusy()) {
            updateAngle();
            if (imuData.getHeading() < angle + 5 && imuData.getHeading() > angle - 5) {
                break;
            }
        }
    }

    public void turnNoCorrection(Angle a) {
        float left = 0.5F;
        float right = 0.5F;

        double angle = a.asRadian();

        if (angle > Math.PI) {
            angle = angle - Math.PI;
            angle = -angle;
        }

        // s = arc length
        // r = radius

        // FORMULA: s = r*a
        int r = 9;
        float s = (float) (angle * r);
        if (drivetrain != null) {
            drivetrain.driveDistance((int) (right*s), (int) (left*s), 30);
        }
    }
    public void runToPosition(CoordinateSystem.FieldCoordinates target) {
        getAndEvalReadings();

        if (drivetrain != null) {
            CoordinateSystem.FieldCoordinates current = coordinateSystem.current;
            Angle angle = new Angle(Math.atan2(target.y - current.y, target.x - current.y), Angle.AngleUnit.RADIAN);

            if (coordinateSystem.angle.lessThan(angle)) {
                turnByIMU(angle.asDegree(), Path.Direction.CW);
            } else if (coordinateSystem.angle.greaterThan(angle)) {
                turnByIMU(angle.asDegree(), Path.Direction.CCW);
            }

            encoderDrive(Math.sqrt(Math.pow(target.y - current.y, 2) + Math.pow(target.x - current.x, 2)));
        }
    }
}
