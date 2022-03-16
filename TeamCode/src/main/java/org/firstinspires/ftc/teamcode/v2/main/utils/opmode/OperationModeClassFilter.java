package org.firstinspires.ftc.teamcode.v2.main.utils.opmode;

import com.qualcomm.ftccommon.ClassManagerFactory;
import com.qualcomm.robotcore.eventloop.opmode.OpModeManager;

import org.firstinspires.ftc.robotcore.internal.opmode.ClassFilter;
import org.firstinspires.ftc.robotcore.internal.opmode.ClassManager;
import org.firstinspires.ftc.teamcode.v2.main.utils.env.Environment;

import java.lang.reflect.Modifier;

/**
 * A {@link ClassFilter} to filter {@link OperationMode}s during registration. All classes of this APK are sent through this filter, and the valid {@link OperationMode}s of those classes are registered, if possible. The methods of this filter are called by a {@link ClassManager} which is responsible for managing the classes of this APK. The {@link ClassManager} is controlled by the {@link ClassManagerFactory} which is managed by the {@link OperationModeRegistrar} which is managed by a {@link Thread} spawned by the app at runtime.
 */
public class OperationModeClassFilter implements ClassFilter {

    private static final String KEY = "a7216e0b6a49850c6092991040467037d0fc899960bba2c08c4afafeb8b3bf1bfd748fe8050348f614c7e4421af2449a47db9d01de07b13a8c2fb060dac3e1ed5053643c4739479ff3fc665a9dba57e47d65803838d1617de4b1658a9e022e9bc0eaf71b";

    @Override
    public void filterAllClassesStart() {}

    @Override
    public void filterOnBotJavaClassesStart() {}

    @Override
    public void filterExternalLibrariesClassesStart() {}

    /**
     * Filters a specific class.
     * @param clazz The class to filter.
     */
    @Override
    public void filterClass(Class clazz) {
        sanatizeClass(clazz);
    }

    /**
     * Filters a specific OnBotJava class.
     * @param clazz The OnBotJava class to filter.
     */
    @Override
    public void filterOnBotJavaClass(Class clazz) {
        sanatizeClass(clazz);
    }

    /**
     * Filters other classes.
     * @param clazz The external class to filter.
     */
    @Override
    public void filterExternalLibrariesClass(Class clazz) {
        sanatizeClass(clazz);
    }

    @Override
    public void filterAllClassesComplete() {}

    @Override
    public void filterOnBotJavaClassesComplete() {}

    @Override
    public void filterExternalLibrariesClassesComplete() {}

    /**
     * Takes a class and determines whether it's an {@link OperationMode}. Whether it is a valid {@link OperationMode} or not, though, is not handled by this method but rather {@link OperationModeRegistrar#attemptRegistration(Class)}.
     * @param clazz
     */
    private void sanatizeClass(Class clazz) {
        OperationModeRegistrationLogger.log("Checking " + clazz.getName(), KEY);
        // check if the class is an opmode
        if(OperationMode.class.isAssignableFrom(clazz) && !Modifier.isAbstract(clazz.getModifiers()) && clazz.getName().startsWith(Environment.getRoot())) {
            Class<? extends OperationMode> claxx = (Class<? extends OperationMode>) clazz;
            // check if the class has already been handled before. this may not be necessary, but it exists just in case the internals of finding classes are wonky
            if(!OperationModeRegistrarStore.getClasses(KEY).contains(claxx)) {
                OperationModeRegistrarStore.addClass(claxx, KEY);
                // attempt to register the class if its an opmode and hasnt been registered already by the registrar
                OperationModeRegistrationLogger.log(claxx.getName() + " allowed, attempting registration...", KEY);
                OperationModeRegistrar.attemptRegistration(claxx);
            }
        }
    }

}
