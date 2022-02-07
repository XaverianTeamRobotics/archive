package org.firstinspires.ftc.teamcode.opmodes.teleop;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Servo;

@TeleOp(name = "manualDropper", group="Iterative")
public class manualDropper extends LinearOpMode {

    private Servo servo1;
    private boolean aToggled;

    public void initialize() {
        aToggled = false;

        servo1 = hardwareMap.get(Servo.class, "servo1");
        servo1.setPosition(0);
    }

    public void forever() {
        if (gamepad1.a) {
            aToggled = !aToggled;
            sleep(125);
        }

        telemetry.addData("aToggled", aToggled);

        if (aToggled) {
            servo1.setPosition(0);
        }
        else {
            servo1.setPosition(0.4);
        }
    }

    @Override
    public void runOpMode() {
        initialize();
        waitForStart();
        while (opModeIsActive()) {
            forever();
            telemetry.update();
        }
    }
}