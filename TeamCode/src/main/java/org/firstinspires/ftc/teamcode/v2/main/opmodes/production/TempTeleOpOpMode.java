package org.firstinspires.ftc.teamcode.v2.main.opmodes.production;

import org.firstinspires.ftc.teamcode.v2.main.utils.env.Environment;
import org.firstinspires.ftc.teamcode.v2.main.utils.opmode.OperationMode;
import org.firstinspires.ftc.teamcode.v2.main.utils.opmode.TeleOperation;

/**
 * Temporary Op Mode to show how these things work.
 */
public class TempTeleOpOpMode extends OperationMode implements TeleOperation {

    @Override
    public void construct() {}

    @Override
    public void run() {
        Environment.getTelemetry().addData("Hello", "world!");
        Environment.getTelemetry().update();
    }

    @Override
    public void destruct() {}

}
