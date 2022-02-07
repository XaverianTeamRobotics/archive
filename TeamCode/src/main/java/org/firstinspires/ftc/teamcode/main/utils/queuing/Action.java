package org.firstinspires.ftc.teamcode.main.utils.queuing;

import java.util.Hashtable;

public class Action {
    public static interface Runner { void run(); }
    public static interface BusyChecker { boolean run(); }
}
