package org.firstinspires.ftc.teamcode.v2.main.utils.opmode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.v2.main.utils.env.Environment;

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

    @Override
    public void runOpMode() throws InterruptedException {
        Environment.setHardwareMap(hardwareMap, "Ymd1ZWd0MzEweTl1Z2oyNGJqdXZuODlyM3VyOHYyaWt0LnQuLXA0Lnk7cHJoajk4ZzNodGlxZWc$paJx2IRk4Qk5uRMXATkFEq6KIxdtLtRmeMQRlay9R8YuC+ajBnLWRrnqOn1fURwibMfMaKZYx6RZUYgwWqTiyg");
        Environment.setTelemetry(telemetry, "Ymd1ZWd0MzEweTl1Z2oyNGJqdXZuODlyM3VyOHYyaWt0LnQuLXA0Lnk7cHJoajk4ZzNodGlxZWc$paJx2IRk4Qk5uRMXATkFEq6KIxdtLtRmeMQRlay9R8YuC+ajBnLWRrnqOn1fURwibMfMaKZYx6RZUYgwWqTiyg");
        construct();
        waitForStart();
        resetStartTime();
        while(opModeIsActive()) {
            run();
        }
        destruct();
        requestOpModeStop();
    }

    /**
     * Updates the timestamp stored in the {@link OperationMode} to the current time.
     */
    public void updateTimestamp() {
        timestamp = time;
    }

    /**
     * Updates the timestamp stored in the {@link OperationMode} to a specific time.
     */
    public void updateTimestamp(double time) {
        timestamp = time;
    }

    /**
     * Gets the last timestamp set by the
     * @return
     */
    public double getTimestamp() {
        return time;
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
