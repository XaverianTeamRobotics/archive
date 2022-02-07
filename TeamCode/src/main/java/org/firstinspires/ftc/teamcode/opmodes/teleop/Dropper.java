package org.firstinspires.ftc.teamcode.opmodes.teleop;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.Servo;

@TeleOp(name = "Dropper", group="Iterative")
public class Dropper extends LinearOpMode {

    boolean aKeyDown, bKeyDown, xKeyDown, yKeyDown;
    boolean forceStop;
    private Servo servo1;
    double servo1power;
    double[] joystick_L = {0, 0};

    private void addControllerTelemetry() {
        //separate so I don't have to add the controller's telemetry data each time, and its easier to manage

        aKeyDown = isButtonDown(gamepad1, "a");
        bKeyDown = isButtonDown(gamepad1, "b");
        xKeyDown = isButtonDown(gamepad1, "x");
        yKeyDown = isButtonDown(gamepad1, "y");

        joystick_L[0] = gamepad1.left_stick_x;
        joystick_L[1] = gamepad1.left_stick_y;

        telemetry.addData("aKeyDown", aKeyDown);
        telemetry.addData("bKeyDown", bKeyDown);
        telemetry.addData("xKeyDown", xKeyDown);
        telemetry.addData("yKeyDown", yKeyDown);
        telemetry.addData("JoyStick_L", joystick_L[0] + ", " + joystick_L[1]);
    }

    private boolean isButtonDown(Gamepad controller, String controllerButton) {
        boolean toReturn = false;
        switch (controllerButton) {
            case "back":
                toReturn = controller.back;
                break;
            case "a":
                toReturn = controller.a;
                break;
            case "b":
                toReturn = controller.b;
                break;
            case "x":
                toReturn = controller.x;
                break;
            case "y":
                toReturn = controller.y;
                break;
            case "lb":
                toReturn = controller.left_bumper;
                break;
            case "rb":
                toReturn = controller.right_bumper;
                break;
            case "ls":
                toReturn = controller.left_stick_button;
                break;
            case "rs":
                toReturn = controller.right_stick_button;
                break;
            case "up":
                toReturn = controller.dpad_up;
                break;
            case "down":
                toReturn = controller.dpad_down;
                break;
            case "left":
                toReturn = controller.dpad_left;
                break;
            case "right":
                toReturn = controller.dpad_right;
                break;
        }

        return toReturn;
    }

    private void initialize() {
        aKeyDown = false;
        bKeyDown = false;
        xKeyDown = false;
        yKeyDown = false;
        servo1 = hardwareMap.get(Servo.class, "servo1");
        servo1power = 0;
        telemetry.addData("Status", "Initialized");
    }

    private void forever() {
        addControllerTelemetry();
        if (isButtonDown(gamepad1, "back")) {
            forceStop = true;
        }
        telemetry.addData("Force Stop", forceStop);

        servo1power += joystick_L[1] / 100;

        if (servo1power > 1) {
            servo1power = 1;
        }
        else if (servo1power < 0) {
            servo1power = 0;
        }

        servo1.setPosition(servo1power);

        telemetry.addData("Servo Position: ", servo1power);
    }


    //This function is executed when this Op Mode is selected from the Driver Station.

    @Override
    public void runOpMode() {
        initialize();
        waitForStart();
        while (opModeIsActive() && !forceStop) {
            telemetry.addData("Status", "Running");
            //loops while the op is active and it hasn't been force stopped
            forever();
            telemetry.update();
        }
    }
}