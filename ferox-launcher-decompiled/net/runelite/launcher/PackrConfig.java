/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.launcher;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.function.Consumer;
import net.runelite.launcher.Launcher;
import net.runelite.launcher.LauncherSettings;
import net.runelite.launcher.OS;
import net.runelite.launcher.beans.Bootstrap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class PackrConfig {
    private static final Logger log = LoggerFactory.getLogger(PackrConfig.class);

    PackrConfig() {
    }

    static void updateLauncherArgs(Bootstrap bootstrap, LauncherSettings settings) {
        String[] bootstrapVmArgs = PackrConfig.getVmArgs(bootstrap);
        if (bootstrapVmArgs == null || bootstrapVmArgs.length == 0) {
            log.warn("Launcher args are empty");
            return;
        }
        ArrayList<String> vmArgs = new ArrayList<String>(Arrays.asList(bootstrapVmArgs));
        if (settings.ipv4) {
            vmArgs.add("-Djava.net.preferIPv4Stack=true");
        }
        Map<String, String> env = PackrConfig.getEnv(bootstrap);
        PackrConfig.patch(config -> {
            config.put("vmArgs", vmArgs);
            config.put("env", env);
        });
    }

    static void patch(Consumer<Map> configConsumer) {
        Map config;
        OS.OSType os = OS.getOs();
        if (os != OS.OSType.Windows && os != OS.OSType.MacOS) {
            return;
        }
        File configFile = new File("config.json").getAbsoluteFile();
        if (!configFile.exists() || !configFile.canWrite()) {
            return;
        }
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try (FileInputStream fin = new FileInputStream(configFile);){
            config = gson.fromJson((Reader)new InputStreamReader(fin), Map.class);
        }
        catch (JsonIOException | JsonSyntaxException | IOException e) {
            log.warn("error deserializing launcher vm args!", e);
            return;
        }
        if (config == null) {
            log.warn("launcher config is null!");
            return;
        }
        configConsumer.accept(config);
        try {
            File tmpFile = File.createTempFile("runelite", null);
            try (FileOutputStream fout = new FileOutputStream(tmpFile);
                 FileChannel channel = fout.getChannel();
                 OutputStreamWriter writer = new OutputStreamWriter((OutputStream)fout, StandardCharsets.UTF_8);){
                channel.lock();
                gson.toJson((Object)config, (Appendable)writer);
                writer.flush();
                channel.force(true);
            }
            try {
                Files.move(tmpFile.toPath(), configFile.toPath(), StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
            }
            catch (AtomicMoveNotSupportedException ex) {
                log.debug("atomic move not supported", ex);
                Files.move(tmpFile.toPath(), configFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            }
            log.debug("patched packr config");
        }
        catch (IOException e) {
            log.warn("error updating launcher vm args!", e);
        }
    }

    private static String[] getVmArgs(Bootstrap bootstrap) {
        return Launcher.isJava17() ? PackrConfig.getArgsJvm17(bootstrap) : PackrConfig.getArgsJvm11(bootstrap);
    }

    private static String[] getArgsJvm17(Bootstrap bootstrap) {
        switch (OS.getOs()) {
            case Windows: {
                String[] args = bootstrap.getLauncherJvm17WindowsArguments();
                return args != null ? args : bootstrap.getLauncherJvm17Arguments();
            }
            case MacOS: {
                String[] args = bootstrap.getLauncherJvm17MacArguments();
                return args != null ? args : bootstrap.getLauncherJvm17Arguments();
            }
        }
        return bootstrap.getLauncherJvm17Arguments();
    }

    private static String[] getArgsJvm11(Bootstrap bootstrap) {
        switch (OS.getOs()) {
            case Windows: {
                String[] args = bootstrap.getLauncherJvm11WindowsArguments();
                return args != null ? args : bootstrap.getLauncherJvm11Arguments();
            }
            case MacOS: {
                String[] args = bootstrap.getLauncherJvm11MacArguments();
                return args != null ? args : bootstrap.getLauncherJvm11Arguments();
            }
        }
        return bootstrap.getLauncherJvm11Arguments();
    }

    private static Map<String, String> getEnv(Bootstrap bootstrap) {
        switch (OS.getOs()) {
            case Windows: {
                return bootstrap.getLauncherWindowsEnv();
            }
            case MacOS: {
                return bootstrap.getLauncherMacEnv();
            }
            case Linux: {
                return bootstrap.getLauncherLinuxEnv();
            }
        }
        return null;
    }
}

