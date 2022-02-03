package org.firstinspires.ftc.teamcode.main.utils.queuing;

import java.util.ArrayList;
import java.util.Hashtable;

/*
 * Queuing is such a weird word
 */
public class ActionQueuing {
    public Hashtable<Action.Runner, Action.BusyChecker> busyChecker;
    public Hashtable<Action.Runner, Resources> requiredResources;
    public ArrayList<Action.Runner> actions;
    public ArrayList<Resources> resourcesUsed;
    public Hashtable<Resources, Action.Runner> activeActions;

    public void queueAction(Action.Runner runner, Action.BusyChecker busyChecker, Resources resource) {
        this.busyChecker.put(runner, busyChecker);
        requiredResources.put(runner, resource);
        actions.add(runner);
    }

    public void run() {
        for (Action.Runner runner :
                activeActions.values()) {
            if (!busyChecker.get(runner).run()) {
                actions.remove(runner);
                resourcesUsed.remove(requiredResources.get(runner));
                activeActions.remove(requiredResources.get(runner));
                requiredResources.remove(runner);
                busyChecker.remove(runner);
            }
        }

        for (Action.Runner action :
                actions) {
            Resources resource = requiredResources.get(action);
            if (!resourcesUsed.contains(resource)) {
                resourcesUsed.add(resource);
                activeActions.put(resource, action);
                action.run();
            }
        }
    }

    public static enum Resources {
        DRIVETRAIN("Drivetrain"),
        SENSORS("Sensors"),
        SPINNER("Spinner"),
        LIFT("Lift");

        public String resID;

        Resources(String resID) {
            this.resID = resID;
        }

        public static Resources make(String resID) {
            Resources temp = DRIVETRAIN;
            temp.resID = resID;
            return temp;
        }
    }
}
