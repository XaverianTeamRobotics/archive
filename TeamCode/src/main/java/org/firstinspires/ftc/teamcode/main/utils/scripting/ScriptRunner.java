package org.firstinspires.ftc.teamcode.main.utils.scripting;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

public class ScriptRunner {

    private final LinearOpMode OP_MODE;
    private final Script SCRIPT;
    private final ControlLoopManager LOOP_MANAGER;

    /**
     * Makes a new ScriptRunner, which initializes and runs a Script.
     *
     * @param opMode The OpMode the Script is running in
     * @param script The Script to run
     */
    public ScriptRunner(LinearOpMode opMode, Script script) {
        OP_MODE = opMode;
        SCRIPT = script;
        LOOP_MANAGER = new ControlLoopManager(OP_MODE);
        run();
    }

    private void run() {
        OP_MODE.waitForStart();
        OP_MODE.resetStartTime();
        while(LOOP_MANAGER.shouldContinue()) {
            loop();
        }
        SCRIPT.stop();
    }

    private void loop() {
        SCRIPT.main();
    }

}
