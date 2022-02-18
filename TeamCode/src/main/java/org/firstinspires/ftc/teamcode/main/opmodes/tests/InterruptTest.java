package org.firstinspires.ftc.teamcode.main.opmodes.tests;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.main.utils.scripting.AutonomousStubScript;
import org.firstinspires.ftc.teamcode.main.utils.scripting.Script;
import org.firstinspires.ftc.teamcode.main.utils.scripting.ScriptRunner;

@Autonomous(name="Interrupt Test", group="tests") // replace the name and group with your OpMode's name and group
public class InterruptTest extends LinearOpMode {

    @Override
    public void runOpMode() throws InterruptedException {
        waitForStart();

        testInterrupts();
    }

    private void testInterrupts() throws InterruptedException {
        do {
            telemetry.addData("Time", time);
            telemetry.update();
            if (isStopRequested()) {
                throw new InterruptedException();
            }
        } while (true);
    }
}
