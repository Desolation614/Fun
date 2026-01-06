/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.launcher;

import java.nio.file.Path;
import java.nio.file.Paths;
import javax.swing.SwingUtilities;
import net.runelite.launcher.FatalErrorDialog;
import net.runelite.launcher.Launcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class JagexLauncherCompatibility {
    private static final Logger log = LoggerFactory.getLogger(JagexLauncherCompatibility.class);
    private static final String COMPAT_KEY = "SOFTWARE\\Microsoft\\Windows NT\\CurrentVersion\\AppCompatFlags\\Layers";

    JagexLauncherCompatibility() {
    }

    static boolean check() {
        if (!Launcher.nativesLoaded) {
            log.debug("Launcher natives were not loaded. Skipping Jagex launcher compatibility check.");
            return false;
        }
        ProcessHandle current = ProcessHandle.current();
        ProcessHandle parent = current.parent().orElse(null);
        if (parent == null || !JagexLauncherCompatibility.processIsJagexLauncher(parent) || !Launcher.isProcessElevated(current.pid()) || Launcher.isProcessElevated(parent.pid())) {
            return false;
        }
        log.error("RuneLite is running with elevated permissions, but the Jagex launcher is not. Privileged processes can't have environment variables passed to them from unprivileged processes. This will cause you to be unable to login. Either run RuneLite as a regular user, or run the Jagex launcher as an administrator.");
        String command = current.info().command().orElse(null);
        boolean regEdited = false;
        if (command != null) {
            regEdited |= Launcher.regDeleteValue("HKLM", COMPAT_KEY, command);
            if (regEdited |= Launcher.regDeleteValue("HKCU", COMPAT_KEY, command)) {
                log.info("Application compatibility settings have been unset for {}", (Object)command);
            }
        }
        JagexLauncherCompatibility.showErrorDialog(regEdited);
        return true;
    }

    private static boolean processIsJagexLauncher(ProcessHandle process) {
        ProcessHandle.Info info = process.info();
        if (info.command().isEmpty()) {
            return false;
        }
        return "JagexLauncher.exe".equals(JagexLauncherCompatibility.pathFilename(info.command().get()));
    }

    private static String pathFilename(String path) {
        Path p = Paths.get(path, new String[0]);
        return p.getFileName().toString();
    }

    private static void showErrorDialog(boolean patched) {
        String command = ProcessHandle.current().info().command().map(JagexLauncherCompatibility::pathFilename).orElse("Ferox.exe");
        StringBuilder sb = new StringBuilder();
        sb.append("Running RuneLite as an administrator is incompatible with the Jagex launcher.");
        if (patched) {
            sb.append(" RuneLite has attempted to fix this problem by changing the compatibility settings of ").append(command).append('.');
            sb.append(" Try running RuneLite again.");
        }
        sb.append(" If the problem persists, either run the Jagex launcher as administrator, or change the ").append(command).append(" compatibility settings to not run as administrator.");
        String message = sb.toString();
        SwingUtilities.invokeLater(() -> new FatalErrorDialog(message).open());
    }
}

