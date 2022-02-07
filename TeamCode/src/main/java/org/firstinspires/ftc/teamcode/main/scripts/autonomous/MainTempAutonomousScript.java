package org.firstinspires.ftc.teamcode.main.scripts.autonomous;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.main.utils.scripting.AutonomousScript;

public class MainTempAutonomousScript extends AutonomousScript {

    public MainTempAutonomousScript(LinearOpMode opMode) {
        super(opMode);
    }

    @Override
    public void main() {
        getOpMode().telemetry.addData("Hey", " I'm a temp script so git tracks this package");
        getOpMode().telemetry.update();
    }

    @Override
    public void stop() {}

}
