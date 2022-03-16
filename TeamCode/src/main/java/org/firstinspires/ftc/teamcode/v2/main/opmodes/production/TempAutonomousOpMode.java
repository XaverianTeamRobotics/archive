package org.firstinspires.ftc.teamcode.v2.main.opmodes.production;

import org.firstinspires.ftc.teamcode.v2.main.utils.env.Environment;
import org.firstinspires.ftc.teamcode.v2.main.utils.opmode.AutonomousOperation;
import org.firstinspires.ftc.teamcode.v2.main.utils.opmode.OperationMode;

/**
 * Temporary Op Mode to show how these things work.
 */
public class TempAutonomousOpMode extends OperationMode implements AutonomousOperation {

    @Override
    public void construct() {}

    @Override
    public void run() {
        Environment.getTelemetry().addData("Hello", "world!");
        Environment.getTelemetry().update();
    }

    @Override
    public void destruct() {}

    @Override
    public Class<? extends OperationMode> getNext() {
        return TempTeleOpOpMode.class;
    }

}
