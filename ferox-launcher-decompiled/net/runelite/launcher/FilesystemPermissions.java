/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.launcher;

import com.google.common.base.Stopwatch;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import javax.swing.SwingUtilities;
import net.runelite.launcher.FatalErrorDialog;
import net.runelite.launcher.Launcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class FilesystemPermissions {
    private static final Logger log = LoggerFactory.getLogger(FilesystemPermissions.class);
    private static final String SID_SYSTEM = "S-1-5-18";
    private static final String SID_ADMINISTRATORS = "S-1-5-32-544";
    private static final int MAX_FILES_PER_DIRECTORY = 64;

    FilesystemPermissions() {
    }

    static boolean check() {
        String sid;
        if (!Launcher.nativesLoaded) {
            log.debug("Launcher natives were not loaded. Skipping filesystem permission check.");
            return false;
        }
        boolean elevated = Launcher.isProcessElevated(ProcessHandle.current().pid());
        if (elevated) {
            log.info("Ferox is running as an administrator. This is not recommended because it can cause the files Ferox writes to {} to have more strict permissions than would otherwise be required.", (Object)Launcher.RUNELITE_DIR);
            try {
                sid = Launcher.getUserSID();
                log.info("Ferox is updating the ACLs of the files in {} to be: NT AUTHORITY\\SYSTEM, BUILTIN\\Administrators, and {} (your user SID). To avoid this, don't run Ferox with elevated permissions.", (Object)Launcher.RUNELITE_DIR, (Object)sid);
                Stopwatch sw = Stopwatch.createStarted();
                FilesystemPermissions.setTreeACL(Launcher.RUNELITE_DIR, sid);
                sw.stop();
                log.debug("setTreeACL time: {}", (Object)sw);
            }
            catch (Exception ex) {
                log.error("Unable to update file permissions", ex);
            }
        }
        if (!Launcher.RUNELITE_DIR.exists()) {
            if (!Launcher.RUNELITE_DIR.mkdirs()) {
                log.error("unable to create directory {} elevated: {}", (Object)Launcher.RUNELITE_DIR, (Object)elevated);
                String message = elevated ? "Unable to create Ferox directory " + String.valueOf(Launcher.RUNELITE_DIR) + " while elevated. Check your filesystem permissions are correct." : "Unable to create Ferox directory " + String.valueOf(Launcher.RUNELITE_DIR) + ". Check your filesystem permissions are correct. If you rerun Ferox as an administrator, Ferox will attempt to create the directory again and fix its permissions.";
                SwingUtilities.invokeLater(() -> {
                    FatalErrorDialog dialog = new FatalErrorDialog(message);
                    if (!elevated) {
                        dialog.addButton("Run as administrator", FilesystemPermissions::runas);
                    }
                    dialog.open();
                });
                return true;
            }
            if (elevated) {
                try {
                    sid = Launcher.getUserSID();
                    FilesystemPermissions.setTreeACL(Launcher.RUNELITE_DIR, sid);
                }
                catch (Exception ex) {
                    log.error("Unable to update file permissions", ex);
                }
            }
        }
        Stopwatch sw = Stopwatch.createStarted();
        boolean permissionsOk = FilesystemPermissions.checkPermissions(Launcher.RUNELITE_DIR, true);
        sw.stop();
        log.debug("checkPermissions time: {}", (Object)sw);
        if (!permissionsOk) {
            String message = elevated ? "The file permissions of " + String.valueOf(Launcher.RUNELITE_DIR) + ", or a file within it, is not correct. Check the logs for more details." : "The file permissions of " + String.valueOf(Launcher.RUNELITE_DIR) + ", or a file within it, is not correct. Check the logs for more details. If you rerun Ferox as an administrator, Ferox will attempt to fix the file permissions.";
            SwingUtilities.invokeLater(() -> {
                FatalErrorDialog dialog = new FatalErrorDialog(message);
                if (!elevated) {
                    dialog.addButton("Run as administrator", FilesystemPermissions::runas);
                }
                dialog.open();
            });
            return true;
        }
        return false;
    }

    private static boolean checkPermissions(File tree, boolean root) {
        File[] files = tree.listFiles();
        if (files == null) {
            log.error("Unable to list files in directory {} (IO error, or is not a directory)", (Object)tree);
            return false;
        }
        boolean ok = true;
        int numFiles = 0;
        for (File file : files) {
            Path path;
            if (file.isDirectory()) {
                log.debug("Checking permissions of directory {}", (Object)file);
                if (!root || FilesystemPermissions.checkPermissions(file, false)) continue;
                ok = false;
                continue;
            }
            if (numFiles++ >= 64) continue;
            try {
                path = file.toPath();
            }
            catch (InvalidPathException ex) {
                log.error("file is not a valid path", ex);
                continue;
            }
            log.debug("Checking permissions of {}", (Object)path);
            if (Files.isReadable(path) && Files.isWritable(path)) continue;
            log.error("Permissions for {} are incorrect. Readable: {} writable: {}", file, Files.isReadable(path), Files.isWritable(path));
            ok = false;
        }
        return ok;
    }

    private static void setTreeACL(File tree, String sid) throws IOException {
        log.debug("Setting ACL on {}", (Object)tree.getAbsolutePath());
        Launcher.setFileACL(tree.getAbsolutePath(), new String[]{SID_SYSTEM, SID_ADMINISTRATORS, sid});
        Files.setAttribute(tree.toPath(), "dos:readonly", false, new LinkOption[0]);
        for (File file : tree.listFiles()) {
            if (file.isDirectory()) {
                FilesystemPermissions.setTreeACL(file, sid);
                continue;
            }
            log.debug("Setting ACL on {}", (Object)file.getAbsolutePath());
            Launcher.setFileACL(file.getAbsolutePath(), new String[]{SID_SYSTEM, SID_ADMINISTRATORS, sid});
            Files.setAttribute(file.toPath(), "dos:readonly", false, new LinkOption[0]);
        }
    }

    private static void runas() {
        log.info("Relaunching as administrator");
        ProcessHandle current = ProcessHandle.current();
        Optional<String> command = current.info().command();
        if (command.isEmpty()) {
            log.error("Running process has no command");
            System.exit(-1);
            return;
        }
        Path path = Paths.get(command.get(), new String[0]);
        if (!path.getFileName().toString().equals("Ferox.exe")) {
            log.error("Running process is not the launcher: {}", (Object)path.getFileName().toString());
            System.exit(-1);
            return;
        }
        String commandPath = path.toAbsolutePath().toString();
        Launcher.runas(commandPath, "");
        System.exit(0);
    }
}

