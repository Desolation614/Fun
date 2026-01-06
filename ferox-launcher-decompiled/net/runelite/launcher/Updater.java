/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.launcher;

import com.google.common.escape.Escaper;
import com.google.common.escape.Escapers;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.CopyOption;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileAttribute;
import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import net.runelite.launcher.Launcher;
import net.runelite.launcher.LauncherProperties;
import net.runelite.launcher.LauncherSettings;
import net.runelite.launcher.OS;
import net.runelite.launcher.SplashScreen;
import net.runelite.launcher.VerificationException;
import net.runelite.launcher.beans.Bootstrap;
import net.runelite.launcher.beans.Update;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

class Updater {
    private static final Logger log = LoggerFactory.getLogger(Updater.class);
    private static final String RUNELITE_APP = "/Applications/Ferox.app";

    Updater() {
    }

    static void update(Bootstrap bootstrap, LauncherSettings launcherSettings, String[] args) {
        if (OS.getOs() == OS.OSType.Windows) {
            Updater.updateWindows(bootstrap, launcherSettings, args);
        } else if (OS.getOs() == OS.OSType.MacOS) {
            Updater.updateMacos(bootstrap, launcherSettings, args);
        }
    }

    private static void updateMacos(Bootstrap bootstrap, LauncherSettings launcherSettings, String[] args) {
        ProcessHandle current = ProcessHandle.current();
        Optional<String> command = current.info().command();
        if (command.isEmpty()) {
            log.debug("Running process has no command");
            return;
        }
        Path path = Paths.get(command.get(), new String[0]);
        if (!(path = path.normalize().resolveSibling(Path.of("..", "MacOS", path.getFileName().toString())).normalize()).getFileName().toString().equals("Ferox") || !path.startsWith(RUNELITE_APP)) {
            log.debug("Skipping update check due to not running from installer, command is {}", (Object)command.get());
            return;
        }
        log.debug("Running from installer");
        Update newestUpdate = Updater.findAvailableUpdate(bootstrap);
        if (newestUpdate == null) {
            return;
        }
        boolean noupdate = launcherSettings.isNoupdates();
        if (noupdate) {
            log.info("Skipping update {} due to noupdate being set", (Object)newestUpdate.getVersion());
            return;
        }
        if (System.getenv("RUNELITE_UPGRADE") != null) {
            log.info("Skipping update {} due to launching from an upgrade", (Object)newestUpdate.getVersion());
            return;
        }
        LauncherSettings settings = LauncherSettings.loadSettings();
        int hours = 1 << Math.min(9, settings.lastUpdateAttemptNum);
        if (newestUpdate.getHash().equals(settings.lastUpdateHash) && Instant.ofEpochMilli(settings.lastUpdateAttemptTime).isAfter(Instant.now().minus(hours, ChronoUnit.HOURS))) {
            log.info("Previous upgrade attempt to {} was at {} (backoff: {} hours), skipping", newestUpdate.getVersion(), LocalTime.from(Instant.ofEpochMilli(settings.lastUpdateAttemptTime).atZone(ZoneId.systemDefault())), hours);
            return;
        }
        if (newestUpdate.getRollout() > 0.0 && Math.random() > newestUpdate.getRollout()) {
            log.info("Skipping update {} due to rollout", (Object)newestUpdate.getVersion());
            return;
        }
        settings.lastUpdateAttemptTime = System.currentTimeMillis();
        settings.lastUpdateHash = newestUpdate.getHash();
        ++settings.lastUpdateAttemptNum;
        LauncherSettings.saveSettings(settings);
        try {
            String mountPoint;
            log.info("Downloading launcher {} from {}", (Object)newestUpdate.getVersion(), (Object)newestUpdate.getUrl());
            Path file = Files.createTempFile("rlupdate", "dmg", new FileAttribute[0]);
            try (OutputStream fout = Files.newOutputStream(file, new OpenOption[0]);){
                String name = newestUpdate.getName();
                int size = newestUpdate.getSize();
                try {
                    Launcher.download(newestUpdate.getUrl(), newestUpdate.getHash(), completed -> SplashScreen.stage(0.07, 1.0, null, name, completed, size, true), fout);
                }
                catch (VerificationException e) {
                    log.error("unable to verify update", e);
                    file.toFile().delete();
                    if (fout != null) {
                        fout.close();
                    }
                    return;
                }
            }
            log.debug("Mounting dmg {}", (Object)file);
            ProcessBuilder pb = new ProcessBuilder("hdiutil", "attach", "-nobrowse", "-plist", file.toAbsolutePath().toString());
            Process process = pb.start();
            if (!process.waitFor(5L, TimeUnit.SECONDS)) {
                process.destroy();
                log.error("timeout waiting for hdiutil to attach dmg");
                return;
            }
            if (process.exitValue() != 0) {
                log.error("error running hdiutil attach");
                return;
            }
            try (InputStream in = process.getInputStream();){
                mountPoint = Updater.parseHdiutilPlist(in);
            }
            log.debug("Removing old install from {}", (Object)RUNELITE_APP);
            Updater.delete(Path.of(RUNELITE_APP, new String[0]));
            log.debug("Copying new install from {}", (Object)mountPoint);
            Updater.copy(Path.of(mountPoint, "Ferox.app"), Path.of(RUNELITE_APP, new String[0]), new CopyOption[0]);
            log.debug("Unmounting dmg");
            pb = new ProcessBuilder("hdiutil", "detach", mountPoint);
            pb.start();
            log.debug("Done! Launching...");
            ArrayList<String> launchCmd = new ArrayList<String>(args.length + 1);
            launchCmd.add(path.toAbsolutePath().toString());
            launchCmd.addAll(Arrays.asList(args));
            pb = new ProcessBuilder(launchCmd);
            pb.environment().put("RUNELITE_UPGRADE", "1");
            pb.start();
            System.exit(0);
        }
        catch (Exception e) {
            log.error("error performing upgrade", e);
        }
    }

    static String parseHdiutilPlist(InputStream in) throws Exception {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document doc = db.parse(in);
        doc.getDocumentElement().normalize();
        Element plist = (Element)doc.getElementsByTagName("plist").item(0);
        Element dict = (Element)plist.getElementsByTagName("dict").item(0);
        Element arr = (Element)dict.getElementsByTagName("array").item(0);
        NodeList dicts = arr.getElementsByTagName("dict");
        for (int i = 0; i < dicts.getLength(); ++i) {
            NodeList dict2 = (NodeList)((Object)dicts.item(i));
            String lastKey = null;
            for (int j = 0; j < dict2.getLength(); ++j) {
                Node node = dict2.item(j);
                if (node.getNodeType() != 1) continue;
                if (node.getNodeName().equals("key")) {
                    lastKey = node.getTextContent();
                    continue;
                }
                if (lastKey == null) continue;
                if (lastKey.equals("mount-point")) {
                    return node.getTextContent();
                }
                lastKey = null;
            }
        }
        return null;
    }

    private static void updateWindows(Bootstrap bootstrap, LauncherSettings launcherSettings, String[] args) {
        String installLocation;
        ProcessHandle current = ProcessHandle.current();
        if (current.info().command().isEmpty()) {
            log.debug("Running process has no command");
            return;
        }
        try {
            installLocation = Launcher.regQueryString("Software\\Microsoft\\Windows\\CurrentVersion\\Uninstall\\RuneLite Launcher_is1", "InstallLocation");
        }
        catch (RuntimeException | UnsatisfiedLinkError ex) {
            log.debug("Skipping update check, error querying install location", ex);
            return;
        }
        Path path = Paths.get(current.info().command().get(), new String[0]);
        if (!path.startsWith(installLocation) || !path.getFileName().toString().equals("Ferox.exe")) {
            log.debug("Skipping update check due to not running from installer, command is {}", (Object)current.info().command().get());
            return;
        }
        log.debug("Running from installer");
        Update newestUpdate = Updater.findAvailableUpdate(bootstrap);
        if (newestUpdate == null) {
            return;
        }
        boolean noupdate = launcherSettings.isNoupdates();
        if (noupdate) {
            log.info("Skipping update {} due to noupdate being set", (Object)newestUpdate.getVersion());
            return;
        }
        if (System.getenv("RUNELITE_UPGRADE") != null) {
            log.info("Skipping update {} due to launching from an upgrade", (Object)newestUpdate.getVersion());
            return;
        }
        LauncherSettings settings = LauncherSettings.loadSettings();
        int hours = 1 << Math.min(9, settings.lastUpdateAttemptNum);
        if (newestUpdate.getHash().equals(settings.lastUpdateHash) && Instant.ofEpochMilli(settings.lastUpdateAttemptTime).isAfter(Instant.now().minus(hours, ChronoUnit.HOURS))) {
            log.info("Previous upgrade attempt to {} was at {} (backoff: {} hours), skipping", newestUpdate.getVersion(), LocalTime.from(Instant.ofEpochMilli(settings.lastUpdateAttemptTime).atZone(ZoneId.systemDefault())), hours);
            return;
        }
        List allProcesses = ProcessHandle.allProcesses().collect(Collectors.toList());
        for (ProcessHandle ph : allProcesses) {
            if (ph.pid() == current.pid() || !ph.info().command().equals(current.info().command())) continue;
            log.info("Skipping update {} due to {} process {}", newestUpdate.getVersion(), "Ferox.exe", ph);
            return;
        }
        if (newestUpdate.getRollout() > 0.0 && Updater.installRollout() > newestUpdate.getRollout()) {
            log.info("Skipping update {} due to rollout", (Object)newestUpdate.getVersion());
            return;
        }
        settings.lastUpdateAttemptTime = System.currentTimeMillis();
        settings.lastUpdateHash = newestUpdate.getHash();
        ++settings.lastUpdateAttemptNum;
        LauncherSettings.saveSettings(settings);
        try {
            log.info("Downloading launcher {} from {}", (Object)newestUpdate.getVersion(), (Object)newestUpdate.getUrl());
            Path file = Files.createTempFile("rlupdate", "exe", new FileAttribute[0]);
            try (OutputStream fout = Files.newOutputStream(file, new OpenOption[0]);){
                String name = newestUpdate.getName();
                int size = newestUpdate.getSize();
                try {
                    Launcher.download(newestUpdate.getUrl(), newestUpdate.getHash(), completed -> SplashScreen.stage(0.07, 1.0, null, name, completed, size, true), fout);
                }
                catch (VerificationException e) {
                    log.error("unable to verify update", e);
                    file.toFile().delete();
                    if (fout != null) {
                        fout.close();
                    }
                    return;
                }
            }
            log.info("Launching installer version {}", (Object)newestUpdate.getVersion());
            ProcessBuilder pb = new ProcessBuilder(file.toFile().getAbsolutePath(), "/SILENT");
            Map<String, String> env = pb.environment();
            StringBuilder argStr = new StringBuilder();
            Escaper escaper = Escapers.builder().addEscape('\"', "\\\"").build();
            for (String arg : args) {
                if (argStr.length() > 0) {
                    argStr.append(' ');
                }
                if (arg.contains(" ") || arg.contains("\"")) {
                    argStr.append('\"').append(escaper.escape(arg)).append('\"');
                    continue;
                }
                argStr.append(arg);
            }
            env.put("RUNELITE_UPGRADE", "1");
            env.put("RUNELITE_UPGRADE_PARAMS", argStr.toString());
            pb.start();
            System.exit(0);
        }
        catch (IOException e) {
            log.error("io error performing upgrade", e);
        }
    }

    private static Update findAvailableUpdate(Bootstrap bootstrap) {
        Update[] updates = bootstrap.getUpdates();
        if (updates == null) {
            return null;
        }
        String os = System.getProperty("os.name");
        String arch = System.getProperty("os.arch");
        String ver = System.getProperty("os.version");
        String launcherVersion = LauncherProperties.getVersion();
        if (os == null || arch == null || launcherVersion == null) {
            return null;
        }
        Update newestUpdate = null;
        for (Update update : updates) {
            OS.OSType updateOs = OS.parseOs(update.getOs());
            if (!(updateOs == OS.OSType.Other ? update.getOs().equals(os) : updateOs == OS.getOs()) || update.getOsName() != null && !update.getOsName().equals(os) || update.getOsVersion() != null && !update.getOsVersion().equals(ver) || update.getArch() != null && !arch.equals(update.getArch()) || Launcher.compareVersion(update.getVersion(), launcherVersion) <= 0 || update.getMinimumVersion() != null && Launcher.compareVersion(launcherVersion, update.getMinimumVersion()) < 0 || newestUpdate != null && Launcher.compareVersion(update.getVersion(), newestUpdate.getVersion()) <= 0) continue;
            log.info("Update {} is available", (Object)update.getVersion());
            newestUpdate = update;
        }
        return newestUpdate;
    }

    /*
     * Loose catch block
     */
    private static double installRollout() {
        block8: {
            BufferedReader reader;
            block7: {
                reader = new BufferedReader(new FileReader("install_id.txt"));
                String line = reader.readLine();
                if (line == null) break block7;
                line = line.trim();
                int i = Integer.parseInt(line);
                log.debug("Loaded install id {}", (Object)i);
                double d = (double)i / 2.147483647E9;
                reader.close();
                return d;
            }
            try {
                reader.close();
                break block8;
                {
                    catch (Throwable throwable) {
                        try {
                            reader.close();
                        }
                        catch (Throwable throwable2) {
                            throwable.addSuppressed(throwable2);
                        }
                        throw throwable;
                    }
                }
            }
            catch (IOException | NumberFormatException ex) {
                log.warn("unable to get install rollout", ex);
            }
        }
        return Math.random();
    }

    private static void delete(Path directory) throws IOException {
        Files.walkFileTree(directory, (FileVisitor<? super Path>)new SimpleFileVisitor<Path>(){

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Files.delete(file);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                Files.delete(dir);
                return FileVisitResult.CONTINUE;
            }
        });
    }

    private static void copy(final Path source, final Path target, final CopyOption ... options) throws IOException {
        Files.walkFileTree(source, (FileVisitor<? super Path>)new SimpleFileVisitor<Path>(){

            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                Files.createDirectories(target.resolve(source.relativize(dir).toString()), new FileAttribute[0]);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Files.copy(file, target.resolve(source.relativize(file).toString()), options);
                return FileVisitResult.CONTINUE;
            }
        });
    }
}

