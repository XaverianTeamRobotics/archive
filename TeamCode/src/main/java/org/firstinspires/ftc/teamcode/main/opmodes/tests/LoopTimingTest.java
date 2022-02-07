package org.firstinspires.ftc.teamcode.main.opmodes.tests;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

import java.util.concurrent.TimeUnit;

@Autonomous(name="Loop Timing Test", group="tests")
public class LoopTimingTest extends LinearOpMode {

    @Override
    public void runOpMode() throws InterruptedException {
        waitForStart();

        double avg = 0;
        ElapsedTime timer = new ElapsedTime();

        timer.reset();

        while (opModeIsActive()) {
            avg = avg + timer.time(TimeUnit.MILLISECONDS);
            telemetry.addData("Average Loop Time (ms)", avg);
            telemetry.addData("Current Loop Time (ms)", timer.time(TimeUnit.MILLISECONDS));
            telemetry.update();

            timer.reset();
        }
    }
}
