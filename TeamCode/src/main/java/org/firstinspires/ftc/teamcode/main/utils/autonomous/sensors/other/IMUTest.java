package org.firstinspires.ftc.teamcode.main.utils.autonomous.sensors.other;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.main.utils.interactions.items.StandardIMU;

@TeleOp(name = "IMU Test")
public class IMUTest extends LinearOpMode {

    @Override
    public void runOpMode() {
        StandardIMU imu = new StandardIMU(hardwareMap);

        waitForStart();

        while (opModeIsActive()) {
            StandardIMU.CompassReturnData<StandardIMU.HeadingDataPoint, Float> data = imu.getCompassData();
            data.put(StandardIMU.HeadingDataPoint.HEADING, -data.getHeading());

            telemetry.addData("Heading", data.getHeading());
            telemetry.addData("Roll", data.getHeading());
            telemetry.addData("Pitch", data.getHeading());

            telemetry.update();
        }
    }
}
