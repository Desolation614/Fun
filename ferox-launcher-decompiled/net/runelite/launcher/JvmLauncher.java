/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.launcher;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import net.runelite.launcher.Launcher;
import net.runelite.launcher.LauncherProperties;
import net.runelite.launcher.OS;
import net.runelite.launcher.SplashScreen;
import net.runelite.launcher.beans.Bootstrap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class JvmLauncher {
    private static final Logger log = LoggerFactory.getLogger(JvmLauncher.class);
    private static final Logger logger = LoggerFactory.getLogger(JvmLauncher.class);

    JvmLauncher() {
    }

    private static String getJava() throws FileNotFoundException {
        Path javaHome = Paths.get(System.getProperty("java.home"), new String[0]);
        if (!Files.exists(javaHome, new LinkOption[0])) {
            throw new FileNotFoundException("JAVA_HOME is not set correctly! directory \"" + String.valueOf(javaHome) + "\" does not exist.");
        }
        Path javaPath = Paths.get(javaHome.toString(), "bin", "java.exe");
        if (!Files.exists(javaPath, new LinkOption[0])) {
            javaPath = Paths.get(javaHome.toString(), "bin", "java");
        }
        if (!Files.exists(javaPath, new LinkOption[0])) {
            throw new FileNotFoundException("java executable not found in directory \"" + String.valueOf(javaPath.getParent()) + "\"");
        }
        return javaPath.toAbsolutePath().toString();
    }

    static void launch(Bootstrap bootstrap, List<File> classpath, Collection<String> clientArgs, Map<String, String> jvmProps, List<String> jvmArgs) throws IOException {
        String javaExePath;
        StringBuilder classPath = new StringBuilder();
        for (File f : classpath) {
            if (classPath.length() > 0) {
                classPath.append(File.pathSeparatorChar);
            }
            classPath.append(f.getAbsolutePath());
        }
        try {
            javaExePath = JvmLauncher.getJava();
        }
        catch (FileNotFoundException ex) {
            logger.error("Unable to find java executable", ex);
            return;
        }
        ArrayList<Object> arguments = new ArrayList<Object>();
        arguments.add(javaExePath);
        arguments.add("-cp");
        arguments.add(classPath.toString());
        String[] jvmArguments = JvmLauncher.getJvmArguments(bootstrap);
        if (jvmArguments != null) {
            arguments.addAll(Arrays.asList(jvmArguments));
        }
        for (Map.Entry<String, String> entry : jvmProps.entrySet()) {
            arguments.add("-D" + entry.getKey() + "=" + entry.getValue());
        }
        arguments.addAll(jvmArgs);
        arguments.add(LauncherProperties.getMain());
        arguments.addAll(clientArgs);
        logger.info("Running {}", (Object)arguments);
        ProcessBuilder builder = new ProcessBuilder(arguments.toArray(new String[0]));
        builder.inheritIO();
        Process process = builder.start();
        if (log.isDebugEnabled()) {
            SplashScreen.stop();
            try {
                process.waitFor();
            }
            catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    static String[] getJvmArguments(Bootstrap bootstrap) {
        if (Launcher.isJava17()) {
            switch (OS.getOs()) {
                case Windows: {
                    String[] args = bootstrap.getClientJvm17WindowsArguments();
                    return args != null ? args : bootstrap.getClientJvm17Arguments();
                }
                case MacOS: {
                    String[] args = bootstrap.getClientJvm17MacArguments();
                    return args != null ? args : bootstrap.getClientJvm17Arguments();
                }
            }
            return bootstrap.getClientJvm17Arguments();
        }
        return bootstrap.getClientJvm9Arguments();
    }
}

