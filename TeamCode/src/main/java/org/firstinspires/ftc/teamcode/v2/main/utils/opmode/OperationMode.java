package org.firstinspires.ftc.teamcode.v2.main.utils.opmode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

/**
 * An {@link OperationMode} represents a program the robot can run.
 * <br>
 * <br>
 * To make an {@link OperationMode}, make a class extending extending this one, and implement the necessary methods.
 * <br>
 * <br>
 * <em>For advanced users:</em>
 * <br>
 * We're manually registering {@link OperationMode}s rather than using annotations. This is to make it easier to make OpModes, as it's stupidly easy to forget to annotate the OpMode. While there are a few ways to do this, the best way is internally using an {@link com.qualcomm.robotcore.eventloop.opmode.OpModeRegistrar}, as it's not deprecated nor discouraged. See {@link OperationModeRegistrar} for more details.
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
