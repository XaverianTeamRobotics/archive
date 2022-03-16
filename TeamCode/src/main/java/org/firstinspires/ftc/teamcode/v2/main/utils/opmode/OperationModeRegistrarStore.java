package org.firstinspires.ftc.teamcode.v2.main.utils.opmode;

import com.qualcomm.robotcore.eventloop.opmode.OpModeManager;

import java.util.ArrayList;

/**
 * Stores necessary persistent information regarding {@link OperationMode} registration. Do not instantiate.
 */
public class OperationModeRegistrarStore {

    private static OpModeManager manager;
    private static ArrayList<Class<? extends OperationMode>> classes = new ArrayList<>();
    private static final String KEY = "a7216e0b6a49850c6092991040467037d0fc899960bba2c08c4afafeb8b3bf1bfd748fe8050348f614c7e4421af2449a47db9d01de07b13a8c2fb060dac3e1ed5053643c4739479ff3fc665a9dba57e47d65803838d1617de4b1658a9e022e9bc0eaf71b";

    /**
     * Private constructor to prevent instantiation.
     */
    private OperationModeRegistrarStore() {}

    public static void setManager(OpModeManager m, String k) throws SecurityException {
        if(!KEY.equals(k)) {
            throw new SecurityException("Incorrect key!");
        }else{
            manager = m;
        }
    }

    public static OpModeManager getManager(String k) throws SecurityException {
        if(!KEY.equals(k)) {
            throw new SecurityException("Incorrect key!");
        }else{
            return manager;
        }
    }

    public static void addClass(Class<? extends OperationMode> c, String k) throws SecurityException {
        if(!KEY.equals(k)) {
            throw new SecurityException("Incorrect key!");
        }else{
            classes.add(c);
        }
    }

    public static ArrayList<Class<? extends OperationMode>> getClasses(String k) throws SecurityException {
        if(!KEY.equals(k)) {
            throw new SecurityException("Incorrect key!");
        }else{
            return classes;
        }
    }

    public static void purgeClasses(String k) throws SecurityException {
        if(!KEY.equals(k)) {
            throw new SecurityException("Incorrect key!");
        }else{
            classes.clear();
        }
    }

}
