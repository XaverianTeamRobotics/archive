package org.firstinspires.ftc.teamcode.v2.main.utils.hardware;

import org.firstinspires.ftc.teamcode.main.utils.devices.EyeSquaredSeaDistanceSensor;
import org.firstinspires.ftc.teamcode.v2.main.utils.devices.CustomUltrasonicEyeSquaredSeaDistanceSensor;
import org.firstinspires.ftc.teamcode.v2.main.utils.env.Environment;

/**
 * An {@link UltrasonicDistanceSensor} represents an ultrasonic, addressable, I2C-based distance sensor. This type of distance sensor is plugged into an I2C port.
 */
public class UltrasonicDistanceSensor extends Hardware {

    private final CustomUltrasonicEyeSquaredSeaDistanceSensor SENSOR;
    private boolean async = false;
    private boolean sync = false;

    /**
     * Creates a new {@link UltrasonicDistanceSensor}.
     * @param name The name of the sensor, as defined by the robot config
     */
    public UltrasonicDistanceSensor(String name) {
        SENSOR = Environment.getHardwareMap().get(CustomUltrasonicEyeSquaredSeaDistanceSensor.class, name);
        SENSOR.resetDeviceConfigurationForOpMode();
    }

    /**
     * Gets the distance provided by the ultrasonic sensor synchronously. This method takes at minimum 80 milliseconds to function, so it will block all operation on its thread for 80 milliseconds. When time is important, I strongly recommend using the async versions of this method.
     * @return The distance
     * @see #beginDataCollectionProcessAsync()
     * @see #endDataCollectionProcessAsync()
     * @throws IllegalStateException The exception thrown when the sensor is already in a sync or async collection process
     */
    public synchronized double getDistanceSync() throws IllegalStateException {
        if(sync || async) {
            throw new IllegalStateException("This ultrasonic sensor is currently either already in a synchronous collection process, or currently in an async collection process!");
        }
        sync = true;
        SENSOR.writeData(CustomUltrasonicEyeSquaredSeaDistanceSensor.Commands.WRITE_RANGE_COMMAND);
        try {
            wait(80L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        double data = SENSOR.readData(CustomUltrasonicEyeSquaredSeaDistanceSensor.Commands.READ_LAST.bVal);
        sync = false;
        return data;
    }

    /**
     * Asynchronously begins collecting data, which is the distance, from the ultrasonic sensor. After calling this method, call {@link #endDataCollectionProcessAsync()} at least 80 milliseconds later to receive the data.
     * @throws IllegalStateException The exception thrown when the sensor is already in a sync or async collection process
     */
    public void beginDataCollectionProcessAsync() throws IllegalStateException {
        if(sync || async) {
            throw new IllegalStateException("This ultrasonic sensor is currently either already in a synchronous collection process, or currently in an async collection process!");
        }
        async = true;
        SENSOR.writeData(CustomUltrasonicEyeSquaredSeaDistanceSensor.Commands.WRITE_RANGE_COMMAND);
    }

    /**
     * Asynchronously finishes collecting data, which is the distance, from the ultrasonic sensor. Call this method at least 80 milliseconds after {@link #beginDataCollectionProcessAsync()}.
     * @return The data collected, AKA the distance
     * @throws IllegalStateException The exception thrown when the sensor is already in a sync collection process or is not currently in an async collection process
     */
    public double endDataCollectionProcessAsync() throws IllegalStateException {
        if(sync || !async) {
            throw new IllegalStateException("This ultrasonic sensor is currently either already in a synchronous collection process, or is not currently in an async collection process!");
        }
        double data = SENSOR.readData(CustomUltrasonicEyeSquaredSeaDistanceSensor.Commands.READ_LAST.bVal);
        async = false;
        return data;
    }

    public CustomUltrasonicEyeSquaredSeaDistanceSensor getInternalSensor() {
        return SENSOR;
    }

    /**
     * Stops the ultrasonic sensor's operation.
     */
    @Override
    public void stop() {}

}
