package org.firstinspires.ftc.teamcode.main.opmodes.tests;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.main.utils.autonomous.EncoderTimeoutManager;
import org.firstinspires.ftc.teamcode.main.utils.autonomous.location.pipeline.PositionSystem;
import org.firstinspires.ftc.teamcode.main.utils.helpers.elevator.ElevatorDriver;
import org.firstinspires.ftc.teamcode.main.utils.interactions.groups.StandardTankVehicleDrivetrain;
import org.firstinspires.ftc.teamcode.main.utils.io.InputSpace;
import org.firstinspires.ftc.teamcode.main.utils.io.OutputSpace;
import org.firstinspires.ftc.teamcode.main.utils.resources.Resources;
import org.firstinspires.ftc.teamcode.main.utils.scripting.Script;
import org.firstinspires.ftc.teamcode.main.utils.scripting.ScriptRunner;
import org.firstinspires.ftc.teamcode.main.utils.scripting.TeleOpStubScript;

@Autonomous(name="Go Go Power Rangers") // replace the name and group with your OpMode's name and group
public class FloorIt extends LinearOpMode {

    @Override
    public void runOpMode() throws InterruptedException {
        PositionSystem positionSystem = Resources.Navigation.Sensors.getPositionSystem(hardwareMap);

        InputSpace input = new InputSpace(hardwareMap);
        OutputSpace output = new OutputSpace(hardwareMap);
        StandardTankVehicleDrivetrain tank = (StandardTankVehicleDrivetrain) input.getTank().getInternalInteractionSurface();
        ElevatorDriver elevatorDriver = new ElevatorDriver(input, output, this);
        positionSystem.setDrivetrain(tank);

        waitForStart();

        positionSystem.encoderDrive(-35, 35, 75);

        EncoderTimeoutManager encoderTimeout = new EncoderTimeoutManager(5000);

        encoderTimeout.restart();

        while (positionSystem.areMotorsBusy() && !encoderTimeout.hasTimedOut() && opModeIsActive()) {
            telemetry.addData("Motors busy for", encoderTimeout.getOperationTime());
            telemetry.update();
        }

        positionSystem.getDrivetrain().brake();
        encoderTimeout.restart();
    }

}
