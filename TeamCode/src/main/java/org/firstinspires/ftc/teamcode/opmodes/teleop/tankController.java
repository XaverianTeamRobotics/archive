package org.firstinspires.ftc.teamcode.opmodes.teleop;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

@TeleOp(name = "tankController", group = "Iterative")
public class tankController extends LinearOpMode {

    private DcMotor treadMotor1, treadMotor2, treadMotor3, treadMotor4;



    private boolean forceStop;

    public void initialize() {
        forceStop = false;

        //motor 1, and 3 are left | 3 is back
        //motor 2, and 4 are right | 4 is back

        treadMotor1 = hardwareMap.get(DcMotor.class, "treadMotor1");
        treadMotor2 = hardwareMap.get(DcMotor.class, "treadMotor2");
        treadMotor3 = hardwareMap.get(DcMotor.class, "treadMotor3");
        treadMotor4 = hardwareMap.get(DcMotor.class, "treadMotor4");
    }

    public void leftTrackSpeed(double speed) {
        treadMotor1.setPower(speed);
        treadMotor3.setPower(speed);
    }

    public void rightTrackSpeed(double speed) {
        treadMotor2.setPower(speed);
        treadMotor4.setPower(speed);
    }

    public void forever() {
        if (gamepad1.back) {
            forceStop = true;
        }

        rightTrackSpeed(1);
        leftTrackSpeed(1);
    }

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
