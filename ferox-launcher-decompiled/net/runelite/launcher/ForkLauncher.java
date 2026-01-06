/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.launcher;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import net.runelite.launcher.JvmLauncher;
import net.runelite.launcher.OS;
import net.runelite.launcher.beans.Bootstrap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class ForkLauncher {
    private static final Logger log = LoggerFactory.getLogger(ForkLauncher.class);

    ForkLauncher() {
    }

    static boolean canForkLaunch() {
        String appimage;
        OS.OSType os = OS.getOs();
        if (os == OS.OSType.Linux && (appimage = System.getenv("APPIMAGE")) != null) {
            return true;
        }
        if (os == OS.OSType.Windows || os == OS.OSType.MacOS) {
            ProcessHandle current = ProcessHandle.current();
            Optional<String> command = current.info().command();
            if (command.isEmpty()) {
                return false;
            }
            Path path = Paths.get(command.get(), new String[0]);
            String name = path.getFileName().toString();
            return name.equals("Ferox.exe") || name.equals("Ferox");
        }
        return false;
    }

    static void launch(Bootstrap bootstrap, List<File> classpath, Collection<String> clientArgs, Map<String, String> jvmProps, List<String> jvmArgs) throws IOException {
        Path path;
        ProcessHandle current = ProcessHandle.current();
        switch (OS.getOs()) {
            case Windows: {
                path = Paths.get(current.info().command().get(), new String[0]);
                break;
            }
            case MacOS: {
                path = Paths.get(current.info().command().get(), new String[0]);
                path = path.normalize().resolveSibling(Path.of("..", "MacOS", path.getFileName().toString())).normalize();
                break;
            }
            case Linux: {
                String appimage = System.getenv("APPIMAGE");
                path = Path.of(appimage, new String[0]);
                break;
            }
            default: {
                throw new IllegalStateException("invalid os");
            }
        }
        ArrayList<Object> commands = new ArrayList<Object>();
        commands.add(path.toAbsolutePath().toString());
        commands.add("-c");
        String[] clientJvmArgs = JvmLauncher.getJvmArguments(bootstrap);
        if (clientJvmArgs != null) {
            for (String arg : clientJvmArgs) {
                commands.add("-J");
                commands.add(arg);
            }
        }
        for (Map.Entry entry : jvmProps.entrySet()) {
            commands.add("-J");
            commands.add("-D" + (String)entry.getKey() + "=" + (String)entry.getValue());
        }
        for (String string : jvmArgs) {
            commands.add("-J");
            commands.add(string);
        }
        commands.add("--");
        if (classpath.isEmpty()) {
            throw new RuntimeException("cannot fork launch with an empty classpath");
        }
        commands.add("--classpath");
        StringBuilder sb = new StringBuilder();
        for (File f : classpath) {
            if (sb.length() > 0) {
                sb.append(File.pathSeparatorChar);
            }
            sb.append(f.getName());
        }
        commands.add(sb.toString());
        commands.addAll(clientArgs);
        log.debug("Running process: {}", (Object)commands);
        ProcessBuilder processBuilder = new ProcessBuilder(commands.toArray(new String[0]));
        processBuilder.start();
    }
}

