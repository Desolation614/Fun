/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.hash.HashCode
 *  com.google.common.hash.Hasher
 *  com.google.common.hash.Hashing
 *  com.google.common.hash.HashingOutputStream
 *  com.google.inject.Inject
 *  javax.annotation.Nullable
 *  javax.inject.Named
 *  okhttp3.OkHttpClient
 *  okhttp3.Request
 *  okhttp3.Request$Builder
 *  okhttp3.Response
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package net.runelite.client;

import com.google.common.hash.HashCode;
import com.google.common.hash.Hasher;
import com.google.common.hash.Hashing;
import com.google.common.hash.HashingOutputStream;
import com.google.inject.Inject;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.net.NetworkInterface;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileAttribute;
import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import javax.inject.Named;
import net.runelite.client.RuneLiteProperties;
import net.runelite.client.RuntimeConfig;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.ui.SplashScreen;
import net.runelite.client.util.OSType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class Updater {
    private static final Logger log = LoggerFactory.getLogger(Updater.class);
    private static final String LAUNCHER_EXECUTABLE_NAME_WIN = "RuneLite.exe";
    private static final String WIN64_URL = "https://github.com/runelite/launcher/releases/download/2.6.7/RuneLiteSetup.exe";
    private static final String WIN64_CHECKSUM = "6e388243311622782deaed24555fdcc89672c6d22b843245d8514fdaeee4586c";
    private static final int WIN64_SIZE = 29440032;
    private static final String WIN32_URL = "https://github.com/runelite/launcher/releases/download/2.6.7/RuneLiteSetup32.exe";
    private static final String WIN32_CHECKSUM = "3e79a0aa4d09ff8782e8ca7a30b4948abb935eacdce8b71d6263fb92e1d25e79";
    private static final int WIN32_SIZE = 25299936;
    private final OkHttpClient okHttpClient;
    private final RuntimeConfig runtimeConfig;
    private final ConfigManager configManager;
    private final boolean noupdate;

    @Inject
    Updater(OkHttpClient okHttpClient, @Nullable RuntimeConfig runtimeConfig, ConfigManager configManager, @Named(value="noupdate") boolean noupdate) {
        this.okHttpClient = okHttpClient;
        this.runtimeConfig = runtimeConfig;
        this.configManager = configManager;
        this.noupdate = noupdate;
    }

    void update() {
        try {
            this.tryUpdate();
        }
        catch (Exception ex) {
            log.error("error updating", (Throwable)ex);
        }
    }

    void tryUpdate() {
        Long updateAttemptTime;
        int size;
        String checksum;
        String downloadUrl;
        String osName = System.getProperty("os.name");
        if (!"Windows 10".equals(osName) && !"Windows 11".equals(osName)) {
            log.debug("Unsupported OS: {}", (Object)osName);
            return;
        }
        String arch = System.getProperty("os.arch");
        if ("amd64".equals(arch)) {
            downloadUrl = WIN64_URL;
            checksum = WIN64_CHECKSUM;
            size = 29440032;
        } else if ("x86".equals(arch)) {
            downloadUrl = WIN32_URL;
            checksum = WIN32_CHECKSUM;
            size = 25299936;
        } else {
            log.debug("Unsupported arch {}", (Object)arch);
            return;
        }
        ProcessHandle current = ProcessHandle.current();
        if (current.info().command().isEmpty()) {
            log.debug("Running process has no command");
            return;
        }
        Path path = Paths.get(current.info().command().get(), new String[0]);
        if (!path.getFileName().toString().equals(LAUNCHER_EXECUTABLE_NAME_WIN)) {
            log.debug("Skipping update check due to not running from installer, command is {}", (Object)current.info().command().get());
            return;
        }
        String launcherVersion = RuneLiteProperties.getLauncherVersion();
        if (launcherVersion == null || launcherVersion.isEmpty()) {
            log.debug("Skipping update check due to not running from installer, no launcher version");
            return;
        }
        log.debug("Running from installer");
        if (this.runtimeConfig == null || this.runtimeConfig.getUpdateLauncherWinVers() == null || !Arrays.asList(this.runtimeConfig.getUpdateLauncherWinVers()).contains(launcherVersion)) {
            log.debug("No update available");
            return;
        }
        if (this.noupdate) {
            log.info("Skipping update due to noupdate being set");
            return;
        }
        if (System.getenv("RUNELITE_UPGRADE") != null) {
            log.info("Skipping update due to launching from an upgrade");
            return;
        }
        Integer updateAttemptNum = (Integer)this.configManager.getConfiguration("runelite", "updateNum", (Type)((Object)Integer.class));
        if (updateAttemptNum == null) {
            updateAttemptNum = 0;
        }
        if ((updateAttemptTime = (Long)this.configManager.getConfiguration("runelite", "updateAttemptTime", (Type)((Object)Long.class))) == null) {
            updateAttemptTime = 0L;
        }
        String lastUpdateHash = this.configManager.getConfiguration("runelite", "lastUpdateHash");
        int hours = 1 << Math.min(9, updateAttemptNum);
        if (checksum.equals(lastUpdateHash) && Instant.ofEpochMilli(updateAttemptTime).isAfter(Instant.now().minus(hours, ChronoUnit.HOURS))) {
            log.info("Previous upgrade attempt was at {} (backoff: {} hours), skipping", (Object)LocalTime.from(Instant.ofEpochMilli(updateAttemptTime).atZone(ZoneId.systemDefault())), (Object)hours);
            return;
        }
        List allProcesses = ProcessHandle.allProcesses().collect(Collectors.toList());
        for (ProcessHandle ph : allProcesses) {
            if (ph.pid() == current.pid() || !ph.info().command().equals(current.info().command())) continue;
            log.info("Skipping update due to {} process {}", (Object)LAUNCHER_EXECUTABLE_NAME_WIN, (Object)ph);
            return;
        }
        if (this.runtimeConfig.getUpdateRollout() > 0.0 && Updater.installRollout() > this.runtimeConfig.getUpdateRollout()) {
            log.debug("Skipping update due to rollout");
            return;
        }
        log.info("Performing launcher update");
        this.configManager.setConfiguration("runelite", "updateAttemptNum", updateAttemptNum + 1);
        this.configManager.setConfiguration("runelite", "updateAttemptTime", System.currentTimeMillis());
        this.configManager.setConfiguration("runelite", "lastUpdateHash", checksum);
        this.configManager.sendConfig();
        try {
            HashCode hash;
            log.info("Downloading launcher update");
            Request request = new Request.Builder().url(downloadUrl).build();
            Path tempExe = Files.createTempFile("rlupdate", "exe", new FileAttribute[0]);
            try (Response response = this.okHttpClient.newCall(request).execute();
                 HashingOutputStream out = new HashingOutputStream(Hashing.sha256(), Files.newOutputStream(tempExe, new OpenOption[0]));){
                int i;
                if (!response.isSuccessful()) {
                    log.info("Bad response downloading {}", (Object)downloadUrl);
                    return;
                }
                InputStream in = response.body().byteStream();
                byte[] buffer = new byte[0x100000];
                int downloaded = 0;
                while ((i = in.read(buffer)) != -1) {
                    SplashScreen.stage(0.6, 1.0, null, "RuneLite Setup", downloaded += i, size, true);
                    out.write(buffer, 0, i);
                }
                hash = out.hash();
            }
            if (!hash.toString().equals(checksum)) {
                log.info("Hash mismatch for update. Expected {} got {}.", (Object)checksum, (Object)hash);
                return;
            }
            log.info("Launching installer");
            ProcessBuilder pb = new ProcessBuilder(tempExe.toFile().getAbsolutePath(), "/SILENT");
            Map<String, String> env = pb.environment();
            env.put("RUNELITE_UPGRADE", "1");
            pb.start();
            System.exit(0);
        }
        catch (IOException e) {
            log.error("io error performing upgrade", (Throwable)e);
        }
    }

    private static double installRollout() {
        try {
            Hasher hasher = Hashing.sha256().newHasher();
            Runtime runtime = Runtime.getRuntime();
            hasher.putByte((byte)OSType.getOSType().ordinal());
            hasher.putByte((byte)runtime.availableProcessors());
            hasher.putUnencodedChars((CharSequence)System.getProperty("os.arch", ""));
            hasher.putUnencodedChars((CharSequence)System.getProperty("os.version", ""));
            hasher.putUnencodedChars((CharSequence)System.getProperty("user.name", ""));
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
            while (networkInterfaces.hasMoreElements()) {
                NetworkInterface networkInterface = networkInterfaces.nextElement();
                byte[] hardwareAddress = networkInterface.getHardwareAddress();
                if (hardwareAddress == null) continue;
                hasher.putBytes(hardwareAddress);
            }
            HashCode hash = hasher.hash();
            return (double)(hash.asInt() & Integer.MAX_VALUE) / 2.147483647E9;
        }
        catch (Exception ex) {
            log.error("unable to generate machine id", (Throwable)ex);
            return Math.random();
        }
    }
}

