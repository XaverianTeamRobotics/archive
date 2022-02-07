package org.firstinspires.ftc.teamcode.main.utils.autonomous.image.sample;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.main.utils.autonomous.image.ImgProc;

@TeleOp(name = "Identify Image Sample")
public class IdentifyTargetSample extends LinearOpMode {
    @Override
    public void runOpMode() throws InterruptedException {
        ImgProc imgProc = new ImgProc(hardwareMap);

        imgProc.init();
        imgProc.activate();

        waitForStart();

        while (opModeIsActive()) {
            telemetry.addData("Visible Target", imgProc.getVisibleVuforiaTarget().trackableName);
            telemetry.update();
            sleep(100);
        }
    }
}
