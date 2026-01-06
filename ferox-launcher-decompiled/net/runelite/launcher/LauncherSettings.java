/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 */
package net.runelite.launcher;

import com.google.common.base.MoreObjects;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import joptsimple.OptionSet;
import net.runelite.launcher.HardwareAccelerationMode;
import net.runelite.launcher.LaunchMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.helpers.MessageFormatter;

class LauncherSettings {
    private static final Logger log = LoggerFactory.getLogger(LauncherSettings.class);
    private static final String LAUNCHER_SETTINGS = "settings.json";
    long lastUpdateAttemptTime;
    String lastUpdateHash;
    int lastUpdateAttemptNum;
    boolean debug;
    boolean nodiffs;
    boolean skipTlsVerification;
    boolean noupdates;
    boolean safemode;
    boolean ipv4;
    @Nullable
    Double scale;
    List<String> clientArguments = Collections.emptyList();
    List<String> jvmArguments = Collections.emptyList();
    HardwareAccelerationMode hardwareAccelerationMode = HardwareAccelerationMode.AUTO;
    LaunchMode launchMode = LaunchMode.AUTO;

    void apply(OptionSet options) {
        if (options.has("debug")) {
            this.debug = true;
        }
        if (options.has("nodiff")) {
            this.nodiffs = true;
        }
        if (options.has("insecure-skip-tls-verification")) {
            this.skipTlsVerification = true;
        }
        if (options.has("noupdate")) {
            this.noupdates = true;
        }
        if (options.has("scale")) {
            this.scale = Double.parseDouble(String.valueOf(options.valueOf("scale")));
        }
        if (options.has("J")) {
            this.jvmArguments = options.valuesOf("J").stream().filter(String.class::isInstance).map(String.class::cast).collect(Collectors.toList());
        }
        if (!options.nonOptionArguments().isEmpty()) {
            this.clientArguments = options.nonOptionArguments().stream().filter(String.class::isInstance).map(String.class::cast).collect(Collectors.toList());
        }
        if (options.has("hw-accel")) {
            this.hardwareAccelerationMode = (HardwareAccelerationMode)((Object)options.valueOf("hw-accel"));
        } else if (options.has("mode")) {
            this.hardwareAccelerationMode = (HardwareAccelerationMode)((Object)options.valueOf("mode"));
        }
        if ("true".equals(System.getProperty("runelite.launcher.reflect"))) {
            this.launchMode = LaunchMode.REFLECT;
        } else if (options.has("launch-mode")) {
            this.launchMode = (LaunchMode)((Object)options.valueOf("launch-mode"));
        }
    }

    String configurationStr() {
        return MessageFormatter.arrayFormat(" debug: {}" + System.lineSeparator() + " nodiffs: {}" + System.lineSeparator() + " skip tls verification: {}" + System.lineSeparator() + " noupdates: {}" + System.lineSeparator() + " safe mode: {}" + System.lineSeparator() + " ipv4: {}" + System.lineSeparator() + " scale: {}" + System.lineSeparator() + " client arguments: {}" + System.lineSeparator() + " jvm arguments: {}" + System.lineSeparator() + " hardware acceleration mode: {}" + System.lineSeparator() + " launch mode: {}", new Object[]{this.debug, this.nodiffs, this.skipTlsVerification, this.noupdates, this.safemode, this.ipv4, this.scale == null ? "system" : this.scale, this.clientArguments.isEmpty() ? "none" : this.clientArguments, this.jvmArguments.isEmpty() ? "none" : this.jvmArguments, this.hardwareAccelerationMode, this.launchMode}).getMessage();
    }

    @Nonnull
    static LauncherSettings loadSettings() {
        LauncherSettings launcherSettings;
        File settingsFile = new File(LAUNCHER_SETTINGS).getAbsoluteFile();
        InputStreamReader in = new InputStreamReader((InputStream)new FileInputStream(settingsFile), StandardCharsets.UTF_8);
        try {
            LauncherSettings settings = new Gson().fromJson((Reader)in, LauncherSettings.class);
            launcherSettings = MoreObjects.firstNonNull(settings, new LauncherSettings());
        }
        catch (Throwable throwable) {
            try {
                try {
                    in.close();
                }
                catch (Throwable throwable2) {
                    throwable.addSuppressed(throwable2);
                }
                throw throwable;
            }
            catch (FileNotFoundException ex) {
                log.debug("unable to load settings, file does not exist");
                return new LauncherSettings();
            }
            catch (JsonParseException | IOException e) {
                log.warn("unable to load settings", e);
                return new LauncherSettings();
            }
        }
        in.close();
        return launcherSettings;
    }

    static void saveSettings(LauncherSettings settings) {
        File settingsFile = new File(LAUNCHER_SETTINGS).getAbsoluteFile();
        try {
            File tmpFile = File.createTempFile(LAUNCHER_SETTINGS, "json");
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            try (FileOutputStream fout = new FileOutputStream(tmpFile);
                 FileChannel channel = fout.getChannel();
                 OutputStreamWriter writer = new OutputStreamWriter((OutputStream)fout, StandardCharsets.UTF_8);){
                channel.lock();
                gson.toJson((Object)settings, (Appendable)writer);
                writer.flush();
                channel.force(true);
            }
            try {
                Files.move(tmpFile.toPath(), settingsFile.toPath(), StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
            }
            catch (AtomicMoveNotSupportedException ex) {
                log.debug("atomic move not supported", ex);
                Files.move(tmpFile.toPath(), settingsFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            }
        }
        catch (IOException e) {
            log.error("unable to save launcher settings!", e);
            settingsFile.delete();
        }
    }

    public long getLastUpdateAttemptTime() {
        return this.lastUpdateAttemptTime;
    }

    public String getLastUpdateHash() {
        return this.lastUpdateHash;
    }

    public int getLastUpdateAttemptNum() {
        return this.lastUpdateAttemptNum;
    }

    public boolean isDebug() {
        return this.debug;
    }

    public boolean isNodiffs() {
        return this.nodiffs;
    }

    public boolean isSkipTlsVerification() {
        return this.skipTlsVerification;
    }

    public boolean isNoupdates() {
        return this.noupdates;
    }

    public boolean isSafemode() {
        return this.safemode;
    }

    public boolean isIpv4() {
        return this.ipv4;
    }

    @Nullable
    public Double getScale() {
        return this.scale;
    }

    public List<String> getClientArguments() {
        return this.clientArguments;
    }

    public List<String> getJvmArguments() {
        return this.jvmArguments;
    }

    public HardwareAccelerationMode getHardwareAccelerationMode() {
        return this.hardwareAccelerationMode;
    }

    public LaunchMode getLaunchMode() {
        return this.launchMode;
    }

    public void setLastUpdateAttemptTime(long lastUpdateAttemptTime) {
        this.lastUpdateAttemptTime = lastUpdateAttemptTime;
    }

    public void setLastUpdateHash(String lastUpdateHash) {
        this.lastUpdateHash = lastUpdateHash;
    }

    public void setLastUpdateAttemptNum(int lastUpdateAttemptNum) {
        this.lastUpdateAttemptNum = lastUpdateAttemptNum;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    public void setNodiffs(boolean nodiffs) {
        this.nodiffs = nodiffs;
    }

    public void setSkipTlsVerification(boolean skipTlsVerification) {
        this.skipTlsVerification = skipTlsVerification;
    }

    public void setNoupdates(boolean noupdates) {
        this.noupdates = noupdates;
    }

    public void setSafemode(boolean safemode) {
        this.safemode = safemode;
    }

    public void setIpv4(boolean ipv4) {
        this.ipv4 = ipv4;
    }

    public void setScale(@Nullable Double scale) {
        this.scale = scale;
    }

    public void setClientArguments(List<String> clientArguments) {
        this.clientArguments = clientArguments;
    }

    public void setJvmArguments(List<String> jvmArguments) {
        this.jvmArguments = jvmArguments;
    }

    public void setHardwareAccelerationMode(HardwareAccelerationMode hardwareAccelerationMode) {
        this.hardwareAccelerationMode = hardwareAccelerationMode;
    }

    public void setLaunchMode(LaunchMode launchMode) {
        this.launchMode = launchMode;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof LauncherSettings)) {
            return false;
        }
        LauncherSettings other = (LauncherSettings)o;
        if (!other.canEqual(this)) {
            return false;
        }
        if (this.getLastUpdateAttemptTime() != other.getLastUpdateAttemptTime()) {
            return false;
        }
        if (this.getLastUpdateAttemptNum() != other.getLastUpdateAttemptNum()) {
            return false;
        }
        if (this.isDebug() != other.isDebug()) {
            return false;
        }
        if (this.isNodiffs() != other.isNodiffs()) {
            return false;
        }
        if (this.isSkipTlsVerification() != other.isSkipTlsVerification()) {
            return false;
        }
        if (this.isNoupdates() != other.isNoupdates()) {
            return false;
        }
        if (this.isSafemode() != other.isSafemode()) {
            return false;
        }
        if (this.isIpv4() != other.isIpv4()) {
            return false;
        }
        Double this$scale = this.getScale();
        Double other$scale = other.getScale();
        if (this$scale == null ? other$scale != null : !((Object)this$scale).equals(other$scale)) {
            return false;
        }
        String this$lastUpdateHash = this.getLastUpdateHash();
        String other$lastUpdateHash = other.getLastUpdateHash();
        if (this$lastUpdateHash == null ? other$lastUpdateHash != null : !this$lastUpdateHash.equals(other$lastUpdateHash)) {
            return false;
        }
        List<String> this$clientArguments = this.getClientArguments();
        List<String> other$clientArguments = other.getClientArguments();
        if (this$clientArguments == null ? other$clientArguments != null : !((Object)this$clientArguments).equals(other$clientArguments)) {
            return false;
        }
        List<String> this$jvmArguments = this.getJvmArguments();
        List<String> other$jvmArguments = other.getJvmArguments();
        if (this$jvmArguments == null ? other$jvmArguments != null : !((Object)this$jvmArguments).equals(other$jvmArguments)) {
            return false;
        }
        HardwareAccelerationMode this$hardwareAccelerationMode = this.getHardwareAccelerationMode();
        HardwareAccelerationMode other$hardwareAccelerationMode = other.getHardwareAccelerationMode();
        if (this$hardwareAccelerationMode == null ? other$hardwareAccelerationMode != null : !((Object)((Object)this$hardwareAccelerationMode)).equals((Object)other$hardwareAccelerationMode)) {
            return false;
        }
        LaunchMode this$launchMode = this.getLaunchMode();
        LaunchMode other$launchMode = other.getLaunchMode();
        return !(this$launchMode == null ? other$launchMode != null : !((Object)((Object)this$launchMode)).equals((Object)other$launchMode));
    }

    protected boolean canEqual(Object other) {
        return other instanceof LauncherSettings;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        long $lastUpdateAttemptTime = this.getLastUpdateAttemptTime();
        result = result * 59 + (int)($lastUpdateAttemptTime >>> 32 ^ $lastUpdateAttemptTime);
        result = result * 59 + this.getLastUpdateAttemptNum();
        result = result * 59 + (this.isDebug() ? 79 : 97);
        result = result * 59 + (this.isNodiffs() ? 79 : 97);
        result = result * 59 + (this.isSkipTlsVerification() ? 79 : 97);
        result = result * 59 + (this.isNoupdates() ? 79 : 97);
        result = result * 59 + (this.isSafemode() ? 79 : 97);
        result = result * 59 + (this.isIpv4() ? 79 : 97);
        Double $scale = this.getScale();
        result = result * 59 + ($scale == null ? 43 : ((Object)$scale).hashCode());
        String $lastUpdateHash = this.getLastUpdateHash();
        result = result * 59 + ($lastUpdateHash == null ? 43 : $lastUpdateHash.hashCode());
        List<String> $clientArguments = this.getClientArguments();
        result = result * 59 + ($clientArguments == null ? 43 : ((Object)$clientArguments).hashCode());
        List<String> $jvmArguments = this.getJvmArguments();
        result = result * 59 + ($jvmArguments == null ? 43 : ((Object)$jvmArguments).hashCode());
        HardwareAccelerationMode $hardwareAccelerationMode = this.getHardwareAccelerationMode();
        result = result * 59 + ($hardwareAccelerationMode == null ? 43 : ((Object)((Object)$hardwareAccelerationMode)).hashCode());
        LaunchMode $launchMode = this.getLaunchMode();
        result = result * 59 + ($launchMode == null ? 43 : ((Object)((Object)$launchMode)).hashCode());
        return result;
    }

    public String toString() {
        return "LauncherSettings(lastUpdateAttemptTime=" + this.getLastUpdateAttemptTime() + ", lastUpdateHash=" + this.getLastUpdateHash() + ", lastUpdateAttemptNum=" + this.getLastUpdateAttemptNum() + ", debug=" + this.isDebug() + ", nodiffs=" + this.isNodiffs() + ", skipTlsVerification=" + this.isSkipTlsVerification() + ", noupdates=" + this.isNoupdates() + ", safemode=" + this.isSafemode() + ", ipv4=" + this.isIpv4() + ", scale=" + this.getScale() + ", clientArguments=" + String.valueOf(this.getClientArguments()) + ", jvmArguments=" + String.valueOf(this.getJvmArguments()) + ", hardwareAccelerationMode=" + String.valueOf((Object)this.getHardwareAccelerationMode()) + ", launchMode=" + String.valueOf((Object)this.getLaunchMode()) + ")";
    }
}

