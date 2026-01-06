/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.launcher;

import java.io.File;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collection;
import java.util.List;
import javax.swing.UIManager;
import net.runelite.launcher.LauncherProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class ReflectionLauncher {
    private static final Logger log = LoggerFactory.getLogger(ReflectionLauncher.class);

    ReflectionLauncher() {
    }

    static void launch(List<File> classpath, Collection<String> clientArgs) throws MalformedURLException {
        URL[] jarUrls = new URL[classpath.size()];
        int i = 0;
        for (File file : classpath) {
            log.debug("Adding jar: {}", (Object)file);
            jarUrls[i++] = file.toURI().toURL();
        }
        ClassLoader parent = ClassLoader.getPlatformClassLoader();
        URLClassLoader loader = new URLClassLoader(jarUrls, parent);
        UIManager.put("ClassLoader", loader);
        Thread thread = new Thread(() -> {
            try {
                Class<?> mainClass = loader.loadClass(LauncherProperties.getMain());
                Method main = mainClass.getMethod("main", String[].class);
                main.invoke(null, new Object[]{clientArgs.toArray(new String[0])});
            }
            catch (Exception ex) {
                log.error("Unable to launch client", ex);
            }
        });
        thread.setName("Ferox");
        thread.start();
    }
}

