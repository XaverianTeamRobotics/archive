package org.firstinspires.ftc.teamcode.v2.main.utils.opmode;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.v2.main.utils.env.Environment;
import org.firstinspires.ftc.teamcode.v2.main.utils.gamepad.GamepadManager;

import java.util.HashMap;

/**
 * An {@link OperationMode} represents a program the robot can run.
 * <br>
 * <br>
 * To make an {@link OperationMode}, make a class extending extending this one, implement the necessary methods, and inplement {@link AutonomousOperation} or {@link TeleOperation} to determine the operation type. If both are implemented, {@link AutonomousOperation} will take priority.
 * <br>
 * <br>
 * To disable an {@link OperationMode}, annotate it with {@link Disabled}.
 * @see OperationModeRegistrar
 */
public abstract class OperationMode extends LinearOpMode {

    private double timestamp = 0;
    private HashMap<String, Double> otherTimestamps = new HashMap<>();

    @Override
    public void runOpMode() throws InterruptedException {
        GamepadManager gm = new GamepadManager(gamepad1, gamepad2);
        Environment.setGamepadManager(gm, "Ymd1ZWd0MzEweTl1Z2oyNGJqdXZuODlyM3VyOHYyaWt0LnQuLXA0Lnk7cHJoajk4ZzNodGlxZWc$paJx2IRk4Qk5uRMXATkFEq6KIxdtLtRmeMQRlay9R8YuC+ajBnLWRrnqOn1fURwibMfMaKZYx6RZUYgwWqTiyg");
        Environment.setHardwareMap(hardwareMap, "Ymd1ZWd0MzEweTl1Z2oyNGJqdXZuODlyM3VyOHYyaWt0LnQuLXA0Lnk7cHJoajk4ZzNodGlxZWc$paJx2IRk4Qk5uRMXATkFEq6KIxdtLtRmeMQRlay9R8YuC+ajBnLWRrnqOn1fURwibMfMaKZYx6RZUYgwWqTiyg");
        Environment.setTelemetry(telemetry, "Ymd1ZWd0MzEweTl1Z2oyNGJqdXZuODlyM3VyOHYyaWt0LnQuLXA0Lnk7cHJoajk4ZzNodGlxZWc$paJx2IRk4Qk5uRMXATkFEq6KIxdtLtRmeMQRlay9R8YuC+ajBnLWRrnqOn1fURwibMfMaKZYx6RZUYgwWqTiyg");
        construct();
        waitForStart();
        resetStartTime();
        while(opModeIsActive() && !gm.isEscaping()) {
            run();
        }
        destruct();
        requestOpModeStop();
    }

    /**
     * Updates the timestamp stored in the {@link OperationMode} to the current time.
     */
    public void updateDefaultTimestamp() {
        timestamp = time;
    }

    /**
     * Updates the timestamp stored in the {@link OperationMode} to a specific time.
     * @param time The time to set
     */
    public void updateDefaultTimestamp(double time) {
        timestamp = time;
    }

    /**
     * Gets the most recent timestamp set by {@link #updateDefaultTimestamp()} or {@link #updateDefaultTimestamp(double)}, or 0 if the timestamp has never been set.
     * @return The most recent timestamp
     */
    public double getDefaultTimestamp() {
        return timestamp;
    }

    /**
     * Updates a named timestamp to the current time. If a timestamp does not exist with this name, the timestamp is created.
     * @param name The name of the timestamp
     */
    public void updateNamedTimestamp(String name) {
        otherTimestamps.put(name, time);
    }

    /**
     * Updates a named timestamp to a specific time. If a timestamp does not exist with this name, the timestamp is created.
     * @param name The name of the timestamp
     * @param time The time to set
     */
    public void updateNamedTimestamp(String name, double time) {
        otherTimestamps.put(name, time);
    }

    /**
     * Gets the most recent timestamp with a certain name, or 0 if the timestamp has never been set.
     * @param name The name of the timestamp
     * @return The most recent timestamp with that name
     */
    public Double getNamedTimestamp(String name) {
        Double timestamp = otherTimestamps.get(name);
        if(timestamp == null) {
            timestamp = 0D;
        }
        return timestamp;
    }

    /**
     * The method to be called at the start of the {@link OperationMode}'s operation, after the INIT button is pressed but before the PLAY button is pressed. This will run once. You do not need to call {@link #waitForStart()}, the {@link OperationMode} will do this automatically after this method is finished.
     */
    public abstract void construct();

    /**
     * The method to be called during the {@link OperationMode}'s operation, after the PLAY button is pressed but before the STOP button is pressed. This will run constantly until the {@link OperationMode} ends.
     */
    public abstract void run();

    /**
     * The method to be called after the {@link OperationMode}'s operation, after the STOP button is pressed. This will run once. You do not need to call {@link #requestOpModeStop()}, the {@link OperationMode} will do this automatically after this method is finished.
     */
    public abstract void destruct();

}
