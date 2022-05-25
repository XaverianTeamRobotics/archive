package org.firstinspires.ftc.teamcode.main.scripts.teleop;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

@TeleOp(name = "DriveIt")
public class DriveIt extends LinearOpMode {

    private DcMotor FrontRightMotor;
    private DcMotor BackRightMotor;
    private DcMotor FrontLeftMotor;
    private DcMotor BackLeftMotor;
    private int driveDirection = 0;
    float pivMod,vertMod,horMod;

    /**
     * This function is executed when this Op Mode is selected from the Driver Station.
     */
    @Override
    public void runOpMode() {
        float vertical;
        float horizontal;
        float pivot;

        FrontRightMotor = hardwareMap.get(DcMotor.class, "Front Right");
        BackRightMotor = hardwareMap.get(DcMotor.class, "Back Right");
        FrontLeftMotor = hardwareMap.get(DcMotor.class, "Front Left");
        BackLeftMotor = hardwareMap.get(DcMotor.class, "Back Left");

        // Put initialization blocks here.
        FrontRightMotor.setDirection(DcMotorSimple.Direction.REVERSE);
        BackRightMotor.setDirection(DcMotorSimple.Direction.REVERSE);

        pivMod = 1;
        vertMod = 1;
        horMod = 1;

        waitForStart();
        if (opModeIsActive()) {
            // Put run blocks here.
            new Thread(switchDriveDirection).start();
            while (opModeIsActive()) {
                // Put loop blocks here.
                vertical = vertMod * gamepad1.right_stick_y;
                horizontal = horMod *  -gamepad1.right_stick_x;
                pivot = pivMod * -gamepad1.left_stick_x;
                FrontRightMotor.setPower(-pivot + (vertical - horizontal));
                BackRightMotor.setPower(-pivot + vertical + horizontal);
                FrontLeftMotor.setPower(pivot + vertical + horizontal);
                BackLeftMotor.setPower(pivot + (vertical - horizontal));
                telemetry.addData("Vert", vertical);
                telemetry.addData("Horiz", horizontal);
                telemetry.addData("Pivot", pivot);
                telemetry.addData("Drive Direction", driveDirection);
                telemetry.addData("RB", gamepad1.right_bumper);
                telemetry.update();
            }
        }
    }

    Runnable switchDriveDirection = new Runnable() {
        @Override
        public void run() {
            //
            while (opModeIsActive()) {
                if (gamepad1.right_bumper) {
                    driveDirection = driveDirection + 1;
                    if (driveDirection > 1) {
                        driveDirection = 0;
                    }

                    if (driveDirection == 0) {
                        pivMod = 1;
                        vertMod = 1;
                        horMod = 1;
                    }
                    else if (driveDirection == 1) {
                        pivMod = 1;
                        vertMod = -1;
                        horMod = -1;
                    }

                    while (gamepad1.right_bumper);
                }
            }
        }
    };
}