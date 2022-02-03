package org.firstinspires.ftc.teamcode.main.utils.autonomous.starting;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.main.utils.autonomous.EncoderTimeoutManager;
import org.firstinspires.ftc.teamcode.main.utils.autonomous.location.pipeline.PositionSystem;
import org.firstinspires.ftc.teamcode.main.utils.geometry.Angle;
import org.firstinspires.ftc.teamcode.main.utils.interactions.groups.StandardTankVehicleDrivetrain;
import org.firstinspires.ftc.teamcode.main.utils.io.InputSpace;
import org.firstinspires.ftc.teamcode.main.utils.resources.Resources;

@Autonomous(name = "Starting Position Test (OLD)")
@Deprecated
public class StartingPositionTestOld extends LinearOpMode {
    PositionSystem positionSystem;
    EncoderTimeoutManager encoderTimeout;
    @Override
    public void runOpMode() throws InterruptedException {
        positionSystem = Resources.Navigation.Sensors.getPositionSystem(hardwareMap);

        InputSpace input = new InputSpace(hardwareMap);
        StandardTankVehicleDrivetrain tank = (StandardTankVehicleDrivetrain) input.getTank().getInternalInteractionSurface();
        positionSystem.setDrivetrain(tank);

        waitForStart();

        while (opModeIsActive()) {
            encoderTimeout = new EncoderTimeoutManager(0);

            // Move Forward 1.5 tiles
            positionSystem.encoderDrive(24);
            motorHold();

            this.sleep(1000);

            // Turn clockwise 90 degrees
            positionSystem.turnNoCorrection(new Angle(-85, Angle.AngleUnit.DEGREE));
            motorHold();

            this.sleep(1000);

            // Move Forward 1 Tile
            positionSystem.encoderDrive(12);
            motorHold();

            this.sleep(1000);

            // Turn counter-clockwise 90 degrees
            positionSystem.turnNoCorrection(new Angle(95, Angle.AngleUnit.DEGREE));
            motorHold();

            this.sleep(2000);

            // Turn clockwise 90 degrees
            positionSystem.turnNoCorrection(new Angle(-85, Angle.AngleUnit.DEGREE));
            motorHold();

            this.sleep(1000);

            // Move Forward 1 Tile
            positionSystem.encoderDrive(12);
            motorHold();

            this.sleep(1000);

            // Turn clockwise 90 degrees
            positionSystem.turnNoCorrection(new Angle(-50, Angle.AngleUnit.DEGREE));
            motorHold();

            positionSystem.turnNoCorrection(new Angle(-45, Angle.AngleUnit.DEGREE));
            motorHold();

            this.sleep(1000);

            // Move Forward 1 Tile
            positionSystem.encoderDrive(13);
            motorHold();

            this.sleep(1000);

            // Turn counter-clockwise 90 degrees
            positionSystem.turnNoCorrection(new Angle(85, Angle.AngleUnit.DEGREE));
            motorHold();

            this.sleep(1000);

            // Move Forward 1 Tile
/*            positionSystem.encoderDrive(12);
            motorHold();*/

            break;
        }
    }

    private void motorHold() {
        encoderTimeout.restart();
        encoderTimeout.durationMillis = 5000;

        while (positionSystem.areMotorsBusy() && !encoderTimeout.hasTimedOut() && opModeIsActive()) {
            telemetry.addData("Motors busy for", encoderTimeout.getOperationTime());
            telemetry.update();
        }

        positionSystem.getDrivetrain().brake();
        encoderTimeout.restart();
    }
}
