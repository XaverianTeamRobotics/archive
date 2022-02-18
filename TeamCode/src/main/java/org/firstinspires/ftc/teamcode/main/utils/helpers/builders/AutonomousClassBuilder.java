package org.firstinspires.ftc.teamcode.main.utils.helpers.builders;

import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.main.utils.autonomous.location.pipeline.PositionSystem;
import org.firstinspires.ftc.teamcode.main.utils.autonomous.sensors.NavigationSensorCollection;
import org.firstinspires.ftc.teamcode.main.utils.interactions.groups.StandardVehicleDrivetrain;
import org.firstinspires.ftc.teamcode.main.utils.interactions.items.StandardDistanceSensor;
import org.firstinspires.ftc.teamcode.main.utils.interactions.items.StandardIMU;
import org.firstinspires.ftc.teamcode.main.utils.io.InputSpace;
import org.firstinspires.ftc.teamcode.main.utils.resources.Resources;

public class AutonomousClassBuilder {
    private PositionSystem positionSystem;
    private StandardIMU.VelocityDataPoint velocityDirection = StandardIMU.VelocityDataPoint.X;

    public PositionSystem buildPositionSystem(HardwareMap hardwareMap, boolean integrateDrivetrain) {
        PositionSystem positionSystem = new PositionSystem(new NavigationSensorCollection(
                new StandardDistanceSensor(hardwareMap, Resources.Navigation.Sensors.Distance.North),
                new StandardDistanceSensor(hardwareMap, Resources.Navigation.Sensors.Distance.East),
                new StandardDistanceSensor(hardwareMap, Resources.Navigation.Sensors.Distance.West),
                new StandardIMU(hardwareMap),
                270
        ));

        if (integrateDrivetrain) {
            positionSystem.setDrivetrain((StandardVehicleDrivetrain) new InputSpace(hardwareMap).getTank().getInternalInteractionSurface());
        }

        this.positionSystem = positionSystem;

        return positionSystem;
    }

    public PositionSystem getPositionSystem() {
        return positionSystem;
    }

    public void setPositionSystem(PositionSystem positionSystem) {
        this.positionSystem = positionSystem;
    }

    public StandardIMU.VelocityDataPoint getVelocityDirection() {
        return velocityDirection;
    }

    public void setVelocityDirection(StandardIMU.VelocityDataPoint velocityDirection) {
        this.velocityDirection = velocityDirection;
    }
}
