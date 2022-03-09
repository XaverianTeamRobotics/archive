package org.firstinspires.ftc.teamcode.v2.main.utils.opmode;

import static org.reflections.scanners.Scanners.SubTypes;

import android.content.Context;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpModeManager;
import com.qualcomm.robotcore.eventloop.opmode.OpModeRegistrar;

import org.firstinspires.ftc.robotcore.internal.opmode.OpModeMeta;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * This registers {@link OperationMode}s. It should not be instantiated.
 * <br>
 * <br>
 * <em>For advanced users:</em>
 * <br>
 * OpModes are a pain to create. Not because they're a lot of work or particulary hard, but because it's easy to forget to annotate them so the default registrar can find and register them. So, {@link OperationMode}s were created to solve this. They contain all the information needed to be registered without an annotation. To do this without annotations, we need to make our own registrar which finds OpModes differently. To do this, we create a static method annotated with {@link OpModeRegistrar} so the app can find the method. The method takes a manager, and that manager is what's used to actually register the OpModes. Via reflection, {@link OperationMode}s are found. They are then instantiated and their getters called which the values returned from are stored locally in the method. Finally, the metadata is used to register each OpMode via {@link OpModeManager#register(OpModeMeta, Class)}, assuming the OpMode is not disabled.
 */
public class OperationModeRegistrar {

    /**
     * Private constructor to prevent instantiation.
     */
    private OperationModeRegistrar() {}

    /**
     * The method which acts as a functional registrar. Do <strong>NOT</strong> call this yourself unless you are absolutely sure of what you're doing!
     * @param manager The manager to register OpModes with, passed by the app itself as the app is supposed to call this method, <em>not you (probably)</em>
     */
    @OpModeRegistrar
    public static void registerOperationModes(OpModeManager manager) {
        // using org.reflections, find all classes which are a subtype of OperationModes. Yes, I could've just used the builtin reflection API but that's hard lol
        Reflections reflections = new Reflections("org.inspires.ftc.teamcode");
        Set<Class<? extends OperationMode>> opmodes = reflections.getSubTypesOf(OperationMode.class);
        for(Class<? extends OperationMode> opmode : opmodes) {
            // check if the opmode is disabled, and skip this opmode if so
            if(opmode.isAnnotationPresent(Disabled.class)) {
                continue;
            }
            // initialize registration data
            boolean isPsuedoDisabled = true;
            String name = "A";
            String nextName = null;
            // begin creation of opmode meta
            OpModeMeta.Builder opModeMetaBuilder = new OpModeMeta.Builder().setName(opmode.getName()).setGroup(name).setSource(OpModeMeta.Source.EXTERNAL_LIBRARY);
            // check if auto or tele operation is implemented by opmode, and set it up accordingly
            if(AutonomousOperation.class.isAssignableFrom(opmode)) {
                // disable psudo disabling, which basically means the opmode isnt explicitly disabled but implements no ways of operation, so in essence is disabled anyway
                isPsuedoDisabled = false;
                // set to autonomous
                opModeMetaBuilder.setFlavor(OpModeMeta.Flavor.AUTONOMOUS);
                // we need to find the next opmode to queue after this, if it exists. To do this, find the method we need to run via reflection, invoke it, and cast the output to the necessary object assuming it's not null. If it's null or an exception is thrown, don't worry about this and move on
                try {
                    Method method = opmode.getMethod("getNext", (Class<?>[]) null);
                    Object next = method.invoke(opmode.newInstance(), (Object[]) null);
                    if(next != null) {
                        Class<? extends OperationMode> nextClass = (Class<? extends OperationMode>) next;
                        // make sure this class is a valid opmode and will be or has already been instantiated at some point
                        if(!nextClass.isAnnotationPresent(Disabled.class) && TeleOperation.class.isAssignableFrom(nextClass)) {
                            // if so, set the name of the transition target to the name of the opmode which will be the name of the class
                            nextName = nextClass.getName();
                        }
                    }
                }catch(NoSuchMethodException | SecurityException | IllegalAccessException | InstantiationException | InvocationTargetException | ClassCastException ignored) {}
                if(nextName != null) {
                    opModeMetaBuilder.setTransitionTarget(nextName);
                }
            }else if(TeleOperation.class.isAssignableFrom(opmode)) {
                // disable psudo disabling, which basically means the opmode isnt explicitly disabled but implements no ways of operation, so in essence is disabled anyway
                isPsuedoDisabled = false;
                // set to teleop
                opModeMetaBuilder.setFlavor(OpModeMeta.Flavor.TELEOP);
            }
            // finish building the opmode's meta
            OpModeMeta opModeMeta = opModeMetaBuilder.build();
            if(!isPsuedoDisabled) {
                // finally, register the opmode, assuming the opmode has either auto control or teleop control
                manager.register(opModeMeta, opmode);
            }
        }
    }

}
