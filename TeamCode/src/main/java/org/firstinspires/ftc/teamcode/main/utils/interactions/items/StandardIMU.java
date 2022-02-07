package org.firstinspires.ftc.teamcode.main.utils.interactions.items;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.hardware.bosch.JustLoggingAccelerationIntegrator;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.navigation.Acceleration;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AngularVelocity;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;
import org.firstinspires.ftc.robotcore.external.navigation.Position;
import org.firstinspires.ftc.robotcore.external.navigation.Velocity;

import java.util.Hashtable;

public class StandardIMU extends InteractionItem {
    //----------------------------------------------------------------------------------------------
    // State
    //----------------------------------------------------------------------------------------------

    // The IMU sensor object
    BNO055IMU imu;

    float headingOffset;

    public float getHeadingOffset() {
        return headingOffset;
    }
    public void setHeadingOffset(float offset) {
        this.headingOffset = offset;
    }

    float rollOffset;
    public float getRollOffset() {
        return rollOffset;
    }
    public void setRollOffset(float offset) {
        this.rollOffset = offset;
    }

    float pitchOffset;
    public float getPitchOffset() {
        return headingOffset;
    }
    public void setPitchOffset(float offset) {
        this.pitchOffset = offset;
    }

    // State used for updating telemetry
    Orientation angles;

    public StandardIMU(HardwareMap hardwareMap) {
        imu = hardwareMap.get(BNO055IMU.class, "imu");

        // Set up the parameters with which we will use our IMU. Note that integration
        // algorithm here just reports accelerations to the logcat log; it doesn't actually
        // provide positional information.
        BNO055IMU.Parameters parameters = new BNO055IMU.Parameters();
        parameters.angleUnit           = BNO055IMU.AngleUnit.DEGREES;
        parameters.accelUnit           = BNO055IMU.AccelUnit.METERS_PERSEC_PERSEC;
        parameters.calibrationDataFile = "BNO055IMUCalibration.json"; // see the calibration sample opmode
        parameters.loggingEnabled      = true;
        parameters.loggingTag          = "IMU";
        parameters.accelerationIntegrationAlgorithm = new JustLoggingAccelerationIntegrator();

        imu.initialize(parameters);

        // Start the logging of measured acceleration
        imu.startAccelerationIntegration(new Position(), new Velocity(), 100);
    }

    public CompassReturnData<HeadingDataPoint, Float> getCompassData() {
        angles = imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES);

        float heading = -angles.firstAngle + headingOffset;
        /*if (heading < 0) {
            heading = 360 + heading;
        }*/

        float roll = angles.secondAngle + rollOffset;
        /*if (roll < 0) {
            roll = 360 + roll;
        }*/

        float pitch = angles.secondAngle + pitchOffset;
        /*if (pitch < 0) {
            pitch = 360 + pitch;
        }*/

        CompassReturnData<HeadingDataPoint, Float> toReturn = new CompassReturnData<>();
        toReturn.put(HeadingDataPoint.HEADING, heading);
        toReturn.put(HeadingDataPoint.ROLL, roll);
        toReturn.put(HeadingDataPoint.PITCH, pitch);

        return toReturn;
    }

    public VelocityReturnData<VelocityDataPoint, Float> getVelocity() {
        Velocity velocity = imu.getVelocity();
        VelocityReturnData<VelocityDataPoint, Float> velocityReturnData = new VelocityReturnData<>();
        velocityReturnData.put(VelocityDataPoint.X, (float) velocity.xVeloc);
        velocityReturnData.put(VelocityDataPoint.Y, (float) velocity.yVeloc);
        velocityReturnData.put(VelocityDataPoint.Z, (float) velocity.zVeloc);

        return velocityReturnData;
    }

    public VelocityReturnData<VelocityDataPoint, Float> getAcceleration() {
        Acceleration acceleration = imu.getLinearAcceleration();
        VelocityReturnData<VelocityDataPoint, Float> velocityReturnData = new VelocityReturnData<>();
        velocityReturnData.put(VelocityDataPoint.X, (float) acceleration.xAccel*100);
        velocityReturnData.put(VelocityDataPoint.Y, (float) acceleration.yAccel*100);
        velocityReturnData.put(VelocityDataPoint.Z, (float) acceleration.zAccel*100);

        return velocityReturnData;
    }

    public VelocityReturnData<VelocityDataPoint, Float> getAngularVelocity() {
        AngularVelocity velocity = imu.getAngularVelocity();
        VelocityReturnData<VelocityDataPoint, Float> velocityReturnData = new VelocityReturnData<>();
        velocityReturnData.put(VelocityDataPoint.X, (float) velocity.xRotationRate);
        velocityReturnData.put(VelocityDataPoint.Y, (float) velocity.yRotationRate);
        velocityReturnData.put(VelocityDataPoint.Z, (float) velocity.zRotationRate);

        return velocityReturnData;
    }

    @Override
    public void stop() {}

    @Override
    public boolean isInputDevice() {
        return false;
    }

    @Override
    public boolean isOutputDevice() {
        return true;
    }

    public enum HeadingDataPoint {HEADING, PITCH, ROLL}

    public static class CompassReturnData<K, V> extends Hashtable<K,V> {
        public CompassReturnData() {
            super();
        }

        public float getHeading() {
            return (float) this.get(HeadingDataPoint.HEADING);
        }

        public float getPitch() {
            return (float) this.get(HeadingDataPoint.PITCH);
        }

        public float getRoll() {
            return (float) this.get(HeadingDataPoint.ROLL);
        }
    }

    public enum VelocityDataPoint {X, Y, Z}

    public static class VelocityReturnData<K, V> extends Hashtable<K,V> {
        public VelocityReturnData() {
            super();
        }

        public float getX() {
            return (float) this.get(VelocityDataPoint.X);
        }

        public float getY() {
            return (float) this.get(VelocityDataPoint.Y);
        }

        public float getZ() {
            return (float) this.get(VelocityDataPoint.Z);
        }
    }
}