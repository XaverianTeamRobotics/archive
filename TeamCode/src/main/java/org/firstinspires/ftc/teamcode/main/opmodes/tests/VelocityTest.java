package org.firstinspires.ftc.teamcode.main.opmodes.tests;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.main.utils.autonomous.location.pipeline.PositionSystem;
import org.firstinspires.ftc.teamcode.main.utils.autonomous.location.pipeline.VelocityTracker;
import org.firstinspires.ftc.teamcode.main.utils.interactions.items.StandardIMU;
import org.firstinspires.ftc.teamcode.main.utils.resources.Resources;

import java.util.concurrent.TimeUnit;

@Autonomous(name="Velocity Test", group="tests")
public class VelocityTest extends LinearOpMode {

    @Override
    public void runOpMode() throws InterruptedException {
        PositionSystem positionSystem = Resources.Navigation.Sensors.getPositionSystem(hardwareMap);
        StandardIMU imu = positionSystem.imu;
        positionSystem.setVelocityTracker(new VelocityTracker(imu));
        while (!isStopRequested()) {
            telemetry.addData("VELOCITY (CM/S)", positionSystem.getVelocity());
            telemetry.addData("DISPLACEMENT (CM)", positionSystem.getDisplacement());

            StandardIMU.VelocityReturnData acc = imu.getAcceleration();
            telemetry.addData("ACCELERATION (CM/S^2)", "");
            telemetry.addData("  X", acc.getX());
            telemetry.addData("  Y", acc.getY());
            telemetry.addData("  Z", acc.getZ());


            StandardIMU.VelocityReturnData ang = imu.getAngularVelocity();
            telemetry.addData("ANGULAR VELOCITY (DEG/S)", "");
            telemetry.addData("  X", ang.getX());
            telemetry.addData("  Y", ang.getY());
            telemetry.addData("  Z", ang.getZ());

            telemetry.update();
        }
    }
}
