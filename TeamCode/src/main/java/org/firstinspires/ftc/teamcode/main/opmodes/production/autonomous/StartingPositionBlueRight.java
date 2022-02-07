package org.firstinspires.ftc.teamcode.main.opmodes.production.autonomous;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.main.utils.autonomous.starting.StartingPositionManager;

@Autonomous(name = "Starting Position Blue Right")
public class StartingPositionBlueRight extends LinearOpMode {
    @Override
    public void runOpMode() throws InterruptedException {
        new StartingPositionManager(this, true, true, 3);
    }

}
