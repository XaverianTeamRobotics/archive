package org.firstinspires.ftc.teamcode.main.utils.locations;

import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.main.utils.interactions.InteractionSurface;
import org.firstinspires.ftc.teamcode.main.utils.interactions.items.StandardMotor;
import org.firstinspires.ftc.teamcode.main.utils.resources.Resources;

public class ElevatorRightLiftMotorLocation extends Location {

    public enum Action {
        SET_SPEED,
        SET_VOLTAGE,
        MOVE_DISTANCE_IN_INCHES,
        SET_POSITION
    }

    private StandardMotor RIGHT;

    public ElevatorRightLiftMotorLocation(HardwareMap hardware) {
        try {
            RIGHT = new StandardMotor(hardware, Resources.Elevator.Motors.RightLift, DcMotorSimple.Direction.FORWARD, 288, 1, 1);
        } catch(Exception ignored) {}
    }

    public void handleInput(Action action, int input) {
        if(RIGHT == null) {
            return;
        }
        switch(action) {
            case SET_SPEED:
                RIGHT.driveWithEncoder(input);
                break;
            case SET_VOLTAGE:
                RIGHT.driveWithoutEncoder(input);
                break;
            case MOVE_DISTANCE_IN_INCHES:
                RIGHT.driveDistance(input, 50);
                break;
            case SET_POSITION:
                RIGHT.driveToPosition(input, 50);
                break;
        }
    }

    @Override
    public void stop() {
        if(RIGHT == null) {
            return;
        }
        RIGHT.stop();
    }

    @Override
    public boolean isInputLocation() {
        return true;
    }

    @Override
    public boolean isOutputLocation() {
        return false;
    }

    @Override
    public InteractionSurface getInternalInteractionSurface() {
        return RIGHT;
    }

}
