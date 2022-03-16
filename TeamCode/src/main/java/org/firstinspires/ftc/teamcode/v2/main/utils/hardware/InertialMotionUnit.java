package org.firstinspires.ftc.teamcode.v2.main.utils.hardware;

import com.qualcomm.hardware.bosch.BNO055IMU;

import org.firstinspires.ftc.robotcore.external.navigation.Acceleration;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AngularVelocity;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;
import org.firstinspires.ftc.robotcore.external.navigation.Velocity;
import org.firstinspires.ftc.teamcode.main.utils.interactions.items.StandardIMU;
import org.firstinspires.ftc.teamcode.v2.main.utils.devices.IMUAccelerationIntegrator;
import org.firstinspires.ftc.teamcode.v2.main.utils.env.Environment;

import java.util.Hashtable;

/**
 * An {@link InertialMotionUnit} is the IMU of a control hub.
 */
public class InertialMotionUnit extends Hardware {

    private final BNO055IMU IMU;
    private float headingOffset, rollOffset, pitchOffset;

    /**
     * Creates a new {@link InertialMotionUnit}.
     * @param name The name of the unit, as defined by the robot config.
     */
    public InertialMotionUnit(String name) {
        IMU = Environment.getHardwareMap().get(BNO055IMU.class, name);
        // Set up the parameters with which we will use our IMU. Note that integration
        // algorithm here just reports accelerations to the logcat log; it doesn't actually
        // provide positional information.
        BNO055IMU.Parameters parameters = new BNO055IMU.Parameters();
        parameters.angleUnit           = BNO055IMU.AngleUnit.DEGREES;
        parameters.accelUnit           = BNO055IMU.AccelUnit.METERS_PERSEC_PERSEC;
        parameters.calibrationDataFile = "BNO055IMUCalibration.json"; // see the calibration sample opmode
        parameters.loggingEnabled      = true;
        parameters.loggingTag          = "IMU";
        parameters.accelerationIntegrationAlgorithm = new IMUAccelerationIntegrator();
        IMU.initialize(parameters);
        IMU.startAccelerationIntegration(null, null, 10);
    }

    /**
     * Gets the compass data from the IMU.
     * @return The compass data.
     */
    public StandardIMU.CompassReturnData<StandardIMU.HeadingDataPoint, Float> getCompassData() {
        Orientation angles = IMU.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES);
        float heading = -angles.firstAngle + headingOffset;
        float roll = angles.secondAngle + rollOffset;
        float pitch = angles.secondAngle + pitchOffset;
        StandardIMU.CompassReturnData<StandardIMU.HeadingDataPoint, Float> toReturn = new StandardIMU.CompassReturnData<>();
        toReturn.put(StandardIMU.HeadingDataPoint.HEADING, heading);
        toReturn.put(StandardIMU.HeadingDataPoint.ROLL, roll);
        toReturn.put(StandardIMU.HeadingDataPoint.PITCH, pitch);
        return toReturn;
    }

    /**
     * Gets the current velocity of the IMU.
     * @return The velocity.
     */
    public StandardIMU.VelocityReturnData<StandardIMU.VelocityDataPoint, Float> getVelocity() {
        Velocity velocity = IMU.getVelocity();
        StandardIMU.VelocityReturnData<StandardIMU.VelocityDataPoint, Float> velocityReturnData = new StandardIMU.VelocityReturnData<>();
        velocityReturnData.put(StandardIMU.VelocityDataPoint.X, (float) velocity.xVeloc);
        velocityReturnData.put(StandardIMU.VelocityDataPoint.Y, (float) velocity.yVeloc);
        velocityReturnData.put(StandardIMU.VelocityDataPoint.Z, (float) velocity.zVeloc);
        return velocityReturnData;
    }

    /**
     * Gets the current acceleration of the IMU.
     * @return The acceleration.
     */
    public StandardIMU.VelocityReturnData<StandardIMU.VelocityDataPoint, Float> getAcceleration() {
        Acceleration acceleration = IMU.getLinearAcceleration();
        StandardIMU.VelocityReturnData<StandardIMU.VelocityDataPoint, Float> velocityReturnData = new StandardIMU.VelocityReturnData<>();
        velocityReturnData.put(StandardIMU.VelocityDataPoint.X, (float) acceleration.xAccel);
        velocityReturnData.put(StandardIMU.VelocityDataPoint.Y, (float) acceleration.yAccel);
        velocityReturnData.put(StandardIMU.VelocityDataPoint.Z, (float) acceleration.zAccel);
        return velocityReturnData;
    }

    /**
     * Gets the velocity of the IMU relative to a predefined angle.
     * @return The velocity relative to a predefined angle.
     */
    public StandardIMU.VelocityReturnData<StandardIMU.VelocityDataPoint, Float> getAngularVelocity() {
        AngularVelocity velocity = IMU.getAngularVelocity();
        StandardIMU.VelocityReturnData<StandardIMU.VelocityDataPoint, Float> velocityReturnData = new StandardIMU.VelocityReturnData<>();
        velocityReturnData.put(StandardIMU.VelocityDataPoint.X, (float) velocity.xRotationRate);
        velocityReturnData.put(StandardIMU.VelocityDataPoint.Y, (float) velocity.yRotationRate);
        velocityReturnData.put(StandardIMU.VelocityDataPoint.Z, (float) velocity.zRotationRate);
        return velocityReturnData;
    }

    public float getRollOffset() {
        return rollOffset;
    }

    public void setRollOffset(float offset) {
        this.rollOffset = offset;
    }

    public float getHeadingOffset() {
        return headingOffset;
    }

    public void setHeadingOffset(float offset) {
        this.headingOffset = offset;
    }

    public float getPitchOffset() {
        return headingOffset;
    }

    public void setPitchOffset(float offset) {
        this.pitchOffset = offset;
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

    /**
     * Stops the IMU's operation.
     */
    @Override
    public void stop() {}

}
