package org.firstinspires.ftc.teamcode.main.opmodes.production.autonomous;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.main.utils.autonomous.starting.StartingPositionManager;

@Autonomous(name = "Starting Position Red Left", preselectTeleOp = "FullTeleOp")
public class StartingPositionRedLeft extends LinearOpMode {
    @Override
    public void runOpMode() throws InterruptedException {
        new StartingPositionManager(this, false, true, 3, false);
    }

}
