package org.firstinspires.ftc.teamcode.v2.main.utils.opmode;

import com.qualcomm.robotcore.eventloop.opmode.OpModeManager;
import com.qualcomm.robotcore.eventloop.opmode.OpModeRegistrar;

import org.firstinspires.ftc.robotcore.internal.opmode.OpModeMeta;

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
     * @param manager The manager to register OpModes with, passed by the app itself as the app will call this method
     */
    @OpModeRegistrar
    public static void registerOperationModes(OpModeManager manager) {
        // TODO
        // manager.register(...);
    }

}
