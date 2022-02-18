package org.firstinspires.ftc.teamcode.main.opmodes.tests;

import com.qualcomm.hardware.bosch.NaiveAccelerationIntegrator;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.main.utils.autonomous.location.pipeline.PositionSystem;
import org.firstinspires.ftc.teamcode.main.utils.interactions.items.StandardIMU;
import org.firstinspires.ftc.teamcode.main.utils.resources.Resources;

@Autonomous(name="Velocity Test", group="tests")
public class VelocityTest extends LinearOpMode {

    PositionSystem positionSystem;
    StandardIMU imu;

    @Override
    public void runOpMode() throws InterruptedException {
        waitForStart();

        NaiveAccelerationIntegrator accelerationIntegrator;

        positionSystem = Resources.Navigation.Sensors.getPositionSystem(hardwareMap);
        imu = positionSystem.imu;
        // telemetry.addAction(this::doTelemetry);
        while (!isStopRequested()) {
            doTelemetry();
            telemetry.update();
        }
    }

    public void doTelemetry() {
        telemetry.addData("VELOCITY", imu.getVelocity().getY());
        telemetry.addData("DISPLACEMENT", positionSystem.getDisplacement());

        StandardIMU.VelocityReturnData acc = imu.getAcceleration();
        telemetry.addData("ACCELERATION", "");
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
