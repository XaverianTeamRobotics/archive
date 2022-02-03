package org.firstinspires.ftc.teamcode.main.utils.io;

import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.main.utils.autonomous.sensors.distance.wrappers.DistanceSensorWrapper;
import org.firstinspires.ftc.teamcode.main.utils.interactions.items.StandardDistanceSensor;
import org.firstinspires.ftc.teamcode.main.utils.interactions.items.StandardTouchSensor;
import org.firstinspires.ftc.teamcode.main.utils.locations.ElevatorBottomLimitSwitchLocation;
import org.firstinspires.ftc.teamcode.main.utils.locations.HandDistanceSensorLocation;
import org.firstinspires.ftc.teamcode.main.utils.locations.IntakeLiftingDistanceSensorLocation;
import org.firstinspires.ftc.teamcode.main.utils.locations.IntakeLimitSwitchLocation;

/**
 * <p>This class can be used to receive output from locations. Locations get data from the interaction surfaces they handle, and then return that to this space.</p>
 * <p>For example, if the location of touch sensor $y finds out $y is being touched, the location will send that data to the space on request. If something fails, it will consume any exceptions and act like nothing happened, returning an arbitrary value. When creating an OutputSpace, the OutputSpace attempts to create locations for every output location on the robot. For example, if the team decides to build a robot with 4 motors, locations for all 4 will be created and able to receive data from. The possible locations are defined inside this class.</p>
 * <p>Since the OutputSpace is designed to handle output from the current robot, it should be built with the current robot and only the current robot in mind. Location classes are where all used locations should go. Remove any unused Locations from the OutputSpace's methods, and potentially delete the Location entirely if needed.</p>
 */
public class OutputSpace {

    private final HardwareMap HARDWARE;
    private final IntakeLimitSwitchLocation INTAKE_LIMIT_SWITCH;
    private final ElevatorBottomLimitSwitchLocation ELEVATOR_BOTTOM_LIMIT_SWITCH;
    private final IntakeLiftingDistanceSensorLocation INTAKE_DISTANCE_SENSOR;
    private final HandDistanceSensorLocation HAND_DISTANCE_SENSOR;

    public OutputSpace(HardwareMap hardware) {
        HARDWARE = hardware;
        INTAKE_LIMIT_SWITCH = new IntakeLimitSwitchLocation(HARDWARE);
        ELEVATOR_BOTTOM_LIMIT_SWITCH = new ElevatorBottomLimitSwitchLocation(HARDWARE);
        INTAKE_DISTANCE_SENSOR = new IntakeLiftingDistanceSensorLocation(HARDWARE);
        HAND_DISTANCE_SENSOR = new HandDistanceSensorLocation(HARDWARE);
    }

    public double receiveOutputFromIntakeLiftingDistanceSensor() {
        return INTAKE_DISTANCE_SENSOR.returnOutput();
    }

    public double receiveOutputFromIntakeLimitSwitch(IntakeLimitSwitchLocation.Values type) {
        return INTAKE_LIMIT_SWITCH.returnOutput(type);
    }

    public double receiveOutputFromElevatorBottomLimitSwitch(ElevatorBottomLimitSwitchLocation.Values type) {
        return ELEVATOR_BOTTOM_LIMIT_SWITCH.returnOutput(type);
    }

    public double receiveOutputFromHandDistanceSensor() {
        return HAND_DISTANCE_SENSOR.returnOutput();
    }

    public HardwareMap getHardware() {
        return HARDWARE;
    }

    public IntakeLiftingDistanceSensorLocation getIntakeLiftingDistanceSensor() {
        return INTAKE_DISTANCE_SENSOR;
    }

    public IntakeLimitSwitchLocation getIntakeLimitSwitch() {
        return INTAKE_LIMIT_SWITCH;
    }

    public ElevatorBottomLimitSwitchLocation getElevatorBottomLimitSwitch() {
        return ELEVATOR_BOTTOM_LIMIT_SWITCH;
    }

    public HandDistanceSensorLocation getHandDistanceSensor() {
        return HAND_DISTANCE_SENSOR;
    }

    public void stop() {
        close();
    }

    public void close() {
        INTAKE_DISTANCE_SENSOR.stop();
        ELEVATOR_BOTTOM_LIMIT_SWITCH.stop();
        INTAKE_LIMIT_SWITCH.stop();
    }

}
