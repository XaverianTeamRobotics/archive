package org.firstinspires.ftc.teamcode.v2.main.utils.opmode;

import org.firstinspires.ftc.robotcore.internal.android.dx.dex.file.DexFile;

import java.lang.reflect.Field;
import java.util.Enumeration;

import dalvik.system.PathClassLoader;

public class ClasspathScanner {

    private static final String TAG = ClasspathScanner.class.getSimpleName();

    private static Field dexField;

    static {
        try {
            dexField = PathClassLoader.class.getDeclaredField("mDexs");
            dexField.setAccessible(true);
        } catch (Exception e) {
            // TODO (1): handle this case gracefully - nobody promised that this field will always be there
            Log.e(TAG, "Failed to get mDexs field");
        }
    }

    public void run() {
        try {
            // TODO (2): check here - in theory, the class loader is not required to be a PathClassLoader
            PathClassLoader classLoader = (PathClassLoader) Thread.currentThread().getContextClassLoader();

            DexFile[] dexs = (DexFile[]) dexField.get(classLoader);
            for (DexFile dex : dexs) {

                Enumeration<String> entries = dex.getClassDefs();
                while (entries.hasMoreElements()) {
                    // (3) Each entry is a class name, like "foo.bar.MyClass"
                    String entry = entries.nextElement();
                    Log.d(TAG, "Entry: " + entry);

                    // (4) Load the class
                    Class<?> entryClass = dex.loadClass(entry, classLoader);
                    if (entryClass != null) {
                        Foo annotation = entryClass.getAnnotation(Foo.class);
                        if (annotation != null) {
                            Log.d(TAG, entry + ": " + annotation.value());
                        }
                    }
                }
            }
        } catch (Exception e) {
            // TODO (5): more precise error handling
            Log.e(TAG, "Error", e);
        }
    }
}
