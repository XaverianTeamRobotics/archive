package org.firstinspires.ftc.teamcode.v2.main.utils.opmode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;

/**
 * An {@link OperationMode} represents a program the robot can run.
 * <br>
 * <br>
 * To make an {@link OperationMode}, make a class extending extending this one, implement the necessary methods, and inplement {@link TeleOperation} and/or {@link AutonomousOperation} to determine the operation type.
 * <br>
 * <br>
 * To disable an {@link OperationMode}, annotate it with {@link Disabled}.
 * @see OperationModeRegistrar
 */
public abstract class OperationMode extends LinearOpMode {

    @Override
    public void runOpMode() throws InterruptedException {
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
     * The method to be called at the start of the {@link OperationMode}'s operation, after the INIT button is pressed but before the PLAY button is pressed. This will run once.
     */
    public abstract void construct();

    /**
     * The method to be called during the {@link OperationMode}'s operation, after the PLAY button is pressed but before the STOP button is pressed. This will run constantly until the {@link OperationMode} ends.
     */
    public abstract void run();

    /**
     * The method to be called after the {@link OperationMode}'s operation, after the STOP button is pressed. This will run once.
     */
    public abstract void destruct();

}
