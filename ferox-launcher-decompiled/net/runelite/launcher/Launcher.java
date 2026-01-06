/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.launcher;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import com.google.archivepatcher.applier.FileByFileV1DeltaApplier;
import com.google.archivepatcher.shared.DefaultDeflateCompatibilityWindow;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.Streams;
import com.google.common.hash.HashCode;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;
import com.google.common.hash.HashingOutputStream;
import com.google.gson.Gson;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.Signature;
import java.security.SignatureException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.Set;
import java.util.function.IntConsumer;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.zip.GZIPInputStream;
import javax.swing.SwingUtilities;
import joptsimple.OptionException;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import net.runelite.launcher.CertPathExtractor;
import net.runelite.launcher.ConfigurationFrame;
import net.runelite.launcher.FatalErrorDialog;
import net.runelite.launcher.FilesystemPermissions;
import net.runelite.launcher.ForkLauncher;
import net.runelite.launcher.HardwareAccelerationMode;
import net.runelite.launcher.JvmLauncher;
import net.runelite.launcher.LaunchMode;
import net.runelite.launcher.LauncherProperties;
import net.runelite.launcher.LauncherSettings;
import net.runelite.launcher.LinkBrowser;
import net.runelite.launcher.OS;
import net.runelite.launcher.PackrConfig;
import net.runelite.launcher.ReflectionLauncher;
import net.runelite.launcher.SplashScreen;
import net.runelite.launcher.TrustManagerUtil;
import net.runelite.launcher.VerificationException;
import net.runelite.launcher.beans.Artifact;
import net.runelite.launcher.beans.Bootstrap;
import net.runelite.launcher.beans.Diff;
import net.runelite.launcher.beans.Platform;
import org.slf4j.LoggerFactory;

public class Launcher {
    private static final org.slf4j.Logger log = LoggerFactory.getLogger(Launcher.class);
    static final File RUNELITE_DIR = new File(System.getProperty("user.home"), ".ferox");
    static final File LOGS_DIR = new File(RUNELITE_DIR, "logs");
    static final File REPO_DIR = new File(RUNELITE_DIR, "repository2");
    public static final File CRASH_FILES = new File(LOGS_DIR, "jvm_crash_pid_%p.log");
    private static final String USER_AGENT = "Ferox/" + LauncherProperties.getVersion();
    static final String LAUNCHER_EXECUTABLE_NAME_WIN = "Ferox.exe";
    static final String LAUNCHER_EXECUTABLE_NAME_OSX = "Ferox";
    static boolean nativesLoaded;
    private static HttpClient httpClient;

    private static OptionSet parseArgs(String[] args) {
        OptionSet options;
        args = Launcher.parseApplicationURI(args);
        OptionParser parser = new OptionParser(false);
        parser.allowsUnrecognizedOptions();
        parser.accepts("postinstall", "Perform post-install tasks");
        parser.accepts("debug", "Enable debug logging");
        parser.accepts("nodiff", "Always download full artifacts instead of diffs");
        parser.accepts("insecure-skip-tls-verification", "Disable TLS certificate and hostname verification");
        parser.accepts("scale", "Custom scale factor for Java 2D").withRequiredArg();
        parser.accepts("noupdate", "Skips the launcher self-update");
        parser.accepts("help", "Show this text (use -- --help for client help)").forHelp();
        parser.accepts("classpath", "Classpath for the client").withRequiredArg();
        parser.accepts("J", "JVM argument (FORK or JVM launch mode only)").withRequiredArg();
        parser.accepts("configure", "Opens configuration GUI");
        parser.accepts("launch-mode", "JVM launch method (JVM, FORK, REFLECT)").withRequiredArg().ofType(LaunchMode.class);
        parser.accepts("hw-accel", "Java 2D hardware acceleration mode (OFF, DIRECTDRAW, OPENGL, METAL)").withRequiredArg().ofType(HardwareAccelerationMode.class);
        parser.accepts("mode", "Alias of hw-accel").withRequiredArg().ofType(HardwareAccelerationMode.class);
        if (OS.getOs() == OS.OSType.MacOS) {
            parser.accepts("p").withRequiredArg();
        }
        try {
            options = parser.parse(args);
        }
        catch (OptionException ex) {
            log.error("unable to parse arguments", ex);
            SwingUtilities.invokeLater(() -> new FatalErrorDialog("Ferox was unable to parse the provided application arguments: " + ex.getMessage()).open());
            throw ex;
        }
        if (options.has("help")) {
            try {
                parser.printHelpOn(System.out);
            }
            catch (IOException e) {
                log.error(null, e);
            }
            System.exit(0);
        }
        return options;
    }

    private static String[] parseApplicationURI(String[] args) {
        if (args.length > 0 && args[0].startsWith("runelite-jav://")) {
            log.info("Launched using URI {}", (Object)args[0]);
            return new String[]{"--jav_config", args[0].replace("runelite-jav", "http")};
        }
        return args;
    }

    public static void main(String[] args) {
        OptionSet options = Launcher.parseArgs(args);
        if (options.has("configure")) {
            ConfigurationFrame.open();
            return;
        }
        LauncherSettings settings = LauncherSettings.loadSettings();
        settings.apply(options);
        boolean postInstall = options.has("postinstall");
        LOGS_DIR.mkdirs();
        if (settings.isDebug()) {
            Logger logger = (Logger)LoggerFactory.getLogger("ROOT");
            logger.setLevel(Level.DEBUG);
        }
        Launcher.initDll();
        Launcher.initDllBlacklist();
        try {
            Bootstrap bootstrap;
            if (options.has("classpath")) {
                TrustManagerUtil.setupTrustManager();
                String classpathOpt = String.valueOf(options.valueOf("classpath"));
                List<File> classpath = Streams.stream(Splitter.on(File.pathSeparatorChar).split(classpathOpt)).map(name -> new File(REPO_DIR, (String)name)).collect(Collectors.toList());
                try {
                    ReflectionLauncher.launch(classpath, Launcher.getClientArgs(settings));
                }
                catch (Exception e) {
                    log.error("error launching client", e);
                }
                return;
            }
            LinkedHashMap<String, String> jvmProps = new LinkedHashMap<String, String>();
            if (settings.scale != null) {
                jvmProps.put("sun.java2d.dpiaware", "true");
                jvmProps.put("sun.java2d.uiScale", Double.toString(settings.scale));
            }
            HardwareAccelerationMode hardwareAccelMode = settings.hardwareAccelerationMode == HardwareAccelerationMode.AUTO ? HardwareAccelerationMode.defaultMode(OS.getOs()) : settings.hardwareAccelerationMode;
            jvmProps.putAll(hardwareAccelMode.toParams(OS.getOs()));
            if (OS.getOs() == OS.OSType.MacOS) {
                jvmProps.put("apple.awt.application.appearance", "system");
            }
            jvmProps.put(LauncherProperties.getVersionKey(), LauncherProperties.getVersion());
            if (settings.isSkipTlsVerification()) {
                jvmProps.put("runelite.insecure-skip-tls-verification", "true");
            }
            log.info("Ferox Launcher version {}", (Object)LauncherProperties.getVersion());
            log.info("Launcher configuration:" + System.lineSeparator() + "{}", (Object)settings.configurationStr());
            log.info("OS name: {}, version: {}, arch: {}", System.getProperty("os.name"), System.getProperty("os.version"), System.getProperty("os.arch"));
            log.info("Using hardware acceleration mode: {}", (Object)hardwareAccelMode);
            Launcher.setJvmParams(jvmProps);
            if (settings.isSkipTlsVerification()) {
                TrustManagerUtil.setupInsecureTrustManager();
                System.setProperty("jdk.internal.httpclient.disableHostnameVerification", Boolean.TRUE.toString());
            } else {
                TrustManagerUtil.setupTrustManager();
            }
            httpClient = HttpClient.newBuilder().followRedirects(HttpClient.Redirect.ALWAYS).build();
            if (postInstall) {
                Launcher.postInstall(settings);
                return;
            }
            SplashScreen.init();
            SplashScreen.stage(0.0, "Preparing", "Setting up environment");
            if (log.isDebugEnabled()) {
                RuntimeMXBean runtime = ManagementFactory.getRuntimeMXBean();
                log.debug("Command line arguments: {}", (Object)String.join((CharSequence)" ", args));
                log.debug("Java VM arguments: {}", (Object)String.join((CharSequence)" ", runtime.getInputArguments()));
                log.debug("Java Environment:");
                Properties p = System.getProperties();
                Enumeration<Object> keys = p.keys();
                while (keys.hasMoreElements()) {
                    String key = (String)keys.nextElement();
                    String value = (String)p.get(key);
                    log.debug("  {}: {}", (Object)key, (Object)value);
                }
            }
            if (FilesystemPermissions.check()) {
                return;
            }
            if (!REPO_DIR.exists() && !REPO_DIR.mkdirs()) {
                log.error("unable to create directory {}", (Object)REPO_DIR);
                SwingUtilities.invokeLater(() -> new FatalErrorDialog("Unable to create Ferox directory " + REPO_DIR.getAbsolutePath() + ". Check your filesystem permissions are correct.").open());
                return;
            }
            SplashScreen.stage(0.05, null, "Downloading bootstrap");
            try {
                bootstrap = Launcher.getBootstrap();
            }
            catch (IOException | InvalidKeyException | NoSuchAlgorithmException | SignatureException | CertificateException | VerificationException ex) {
                log.error("error fetching bootstrap", ex);
                String extract = CertPathExtractor.extract(ex);
                if (extract != null) {
                    log.error("untrusted certificate chain: {}", (Object)extract);
                }
                SwingUtilities.invokeLater(() -> FatalErrorDialog.showNetErrorWindow("downloading the bootstrap", ex));
                SplashScreen.stop();
                return;
            }
            SplashScreen.stage(0.1, null, "Tidying the cache");
            if (Launcher.jvmOutdated(bootstrap)) {
                return;
            }
            PackrConfig.updateLauncherArgs(bootstrap, settings);
            List<Artifact> artifacts = Arrays.stream(bootstrap.getArtifacts()).filter(a -> {
                if (a.getPlatform() == null) {
                    return true;
                }
                String os = System.getProperty("os.name");
                String arch = System.getProperty("os.arch");
                for (Platform platform : a.getPlatform()) {
                    OS.OSType platformOs;
                    if (platform.getName() == null || !((platformOs = OS.parseOs(platform.getName())) == OS.OSType.Other ? platform.getName().equals(os) : platformOs == OS.getOs()) || platform.getArch() != null && !platform.getArch().equals(arch)) continue;
                    return true;
                }
                return false;
            }).collect(Collectors.toList());
            Launcher.clean(artifacts);
            try {
                Launcher.download(artifacts, settings.isNodiffs());
            }
            catch (IOException ex) {
                log.error("unable to download artifacts", ex);
                SwingUtilities.invokeLater(() -> FatalErrorDialog.showNetErrorWindow("downloading the client", ex));
                SplashScreen.stop();
                return;
            }
            SplashScreen.stage(0.8, null, "Verifying");
            try {
                Launcher.verifyJarHashes(artifacts);
            }
            catch (VerificationException ex) {
                log.error("Unable to verify artifacts", ex);
                SwingUtilities.invokeLater(() -> FatalErrorDialog.showNetErrorWindow("verifying downloaded files", ex));
                SplashScreen.stop();
                return;
            }
            Collection<String> clientArgs = Launcher.getClientArgs(settings);
            SplashScreen.stage(0.9, "Starting the client", "");
            List<File> classpath = artifacts.stream().map(dep -> new File(REPO_DIR, dep.getName())).collect(Collectors.toList());
            ArrayList<String> jvmParams = new ArrayList<String>();
            log.debug("Setting JVM crash log location to {}", (Object)CRASH_FILES);
            jvmParams.add("-XX:ErrorFile=" + CRASH_FILES.getAbsolutePath());
            jvmParams.addAll(Launcher.getJvmArgs(settings));
            if (settings.launchMode == LaunchMode.REFLECT) {
                log.debug("Using launch mode: REFLECT");
                ReflectionLauncher.launch(classpath, clientArgs);
            } else if (settings.launchMode == LaunchMode.FORK || settings.launchMode == LaunchMode.AUTO && ForkLauncher.canForkLaunch()) {
                log.debug("Using launch mode: FORK");
                ForkLauncher.launch(bootstrap, classpath, clientArgs, jvmProps, jvmParams);
            } else {
                if (System.getenv("APPIMAGE") != null) {
                    throw new RuntimeException("JVM launcher is not supported from the appimage");
                }
                log.debug("Using launch mode: JVM");
                JvmLauncher.launch(bootstrap, classpath, clientArgs, jvmProps, jvmParams);
            }
        }
        catch (Exception e) {
            log.error("Failure during startup", e);
            if (!postInstall) {
                SwingUtilities.invokeLater(() -> new FatalErrorDialog("Ferox has encountered an unexpected error during startup.").open());
            }
        }
        catch (Error e) {
            log.error("Failure during startup", e);
            throw e;
        }
        finally {
            SplashScreen.stop();
        }
    }

    private static void setJvmParams(Map<String, String> params) {
        for (Map.Entry<String, String> entry : params.entrySet()) {
            System.setProperty(entry.getKey(), entry.getValue());
        }
    }

    private static Bootstrap getBootstrap() throws IOException, CertificateException, NoSuchAlgorithmException, InvalidKeyException, SignatureException, VerificationException {
        HttpResponse<byte[]> bootstrapSigResp;
        HttpResponse<byte[]> bootstrapResp;
        HttpRequest bootstrapReq = HttpRequest.newBuilder().uri(URI.create(LauncherProperties.getBootstrap())).header("User-Agent", USER_AGENT).GET().build();
        HttpRequest bootstrapSigReq = HttpRequest.newBuilder().uri(URI.create(LauncherProperties.getBootstrapSig())).header("User-Agent", USER_AGENT).GET().build();
        try {
            bootstrapResp = httpClient.send(bootstrapReq, HttpResponse.BodyHandlers.ofByteArray());
            bootstrapSigResp = httpClient.send(bootstrapSigReq, HttpResponse.BodyHandlers.ofByteArray());
        }
        catch (InterruptedException ex) {
            throw new IOException(ex);
        }
        if (bootstrapResp.statusCode() != 200) {
            throw new IOException("Unable to download bootstrap (status code " + bootstrapResp.statusCode() + "): " + new String(bootstrapResp.body()));
        }
        if (bootstrapSigResp.statusCode() != 200) {
            throw new IOException("Unable to download bootstrap signature (status code " + bootstrapSigResp.statusCode() + "): " + new String(bootstrapSigResp.body()));
        }
        byte[] bytes = bootstrapResp.body();
        byte[] signature = bootstrapSigResp.body();
        Certificate certificate = Launcher.getCertificate();
        Signature s = Signature.getInstance("SHA256withRSA");
        s.initVerify(certificate);
        s.update(bytes);
        if (!s.verify(signature)) {
            throw new VerificationException("Unable to verify bootstrap signature");
        }
        Gson g = new Gson();
        return g.fromJson((Reader)new InputStreamReader(new ByteArrayInputStream(bytes)), Bootstrap.class);
    }

    private static boolean jvmOutdated(Bootstrap bootstrap) {
        boolean launcherTooOld = bootstrap.getRequiredLauncherVersion() != null && Launcher.compareVersion(bootstrap.getRequiredLauncherVersion(), LauncherProperties.getVersion()) > 0;
        boolean jvmTooOld = false;
        try {
            if (bootstrap.getRequiredJVMVersion() != null) {
                jvmTooOld = Runtime.Version.parse(bootstrap.getRequiredJVMVersion()).compareTo(Runtime.version()) > 0;
            }
        }
        catch (IllegalArgumentException e) {
            log.warn("Unable to parse bootstrap version", e);
        }
        if (launcherTooOld) {
            SwingUtilities.invokeLater(() -> new FatalErrorDialog("Your launcher is too old to start Ferox. Please download and install a more recent one from Ferox.ps.").addButton("Ferox.ps", () -> LinkBrowser.browse(LauncherProperties.getDownloadLink())).open());
            return true;
        }
        if (jvmTooOld) {
            SwingUtilities.invokeLater(() -> new FatalErrorDialog("Your Java installation is too old. Ferox now requires Java " + bootstrap.getRequiredJVMVersion() + " to run. You can get a platform specific version from Ferox.ps, or install a newer version of Java.").addButton("Ferox.ps", () -> LinkBrowser.browse(LauncherProperties.getDownloadLink())).open());
            return true;
        }
        return false;
    }

    private static Collection<String> getClientArgs(LauncherSettings settings) {
        ArrayList<String> args = new ArrayList<String>(settings.clientArguments);
        String clientArgs = System.getenv("RUNELITE_ARGS");
        if (!Strings.isNullOrEmpty(clientArgs)) {
            args.addAll(Splitter.on(' ').omitEmptyStrings().trimResults().splitToList(clientArgs));
        }
        if (settings.debug) {
            args.add("--debug");
        }
        if (settings.safemode) {
            args.add("--safe-mode");
        }
        return args;
    }

    private static List<String> getJvmArgs(LauncherSettings settings) {
        String envArgs;
        ArrayList<String> args = new ArrayList<String>(settings.jvmArguments);
        if (settings.ipv4) {
            args.add("-Djava.net.preferIPv4Stack=true");
        }
        if (!Strings.isNullOrEmpty(envArgs = System.getenv("RUNELITE_VMARGS"))) {
            args.addAll(Splitter.on(' ').omitEmptyStrings().trimResults().splitToList(envArgs));
        }
        return args;
    }

    private static void download(List<Artifact> artifacts, boolean nodiff) throws IOException {
        ArrayList<Artifact> toDownload = new ArrayList<Artifact>(artifacts.size());
        HashMap<Artifact, Diff> diffs = new HashMap<Artifact, Diff>();
        int totalDownloadBytes = 0;
        boolean isCompatible = new DefaultDeflateCompatibilityWindow().isCompatible();
        if (!isCompatible && !nodiff) {
            log.debug("System zlib is not compatible with archive-patcher; not using diffs");
            nodiff = true;
        }
        for (Artifact artifact : artifacts) {
            String hash;
            File dest = new File(REPO_DIR, artifact.getName());
            try {
                hash = Launcher.hash(dest);
            }
            catch (FileNotFoundException ex) {
                hash = null;
            }
            catch (IOException ex) {
                dest.delete();
                hash = null;
            }
            if (Objects.equals(hash, artifact.getHash())) {
                log.debug("Hash for {} up to date", (Object)artifact.getName());
                continue;
            }
            int downloadSize = artifact.getSize();
            if (!nodiff && artifact.getDiffs() != null) {
                for (Diff diff : artifact.getDiffs()) {
                    String oldhash;
                    File old = new File(REPO_DIR, diff.getFrom());
                    try {
                        oldhash = Launcher.hash(old);
                    }
                    catch (IOException ex) {
                        continue;
                    }
                    if (!diff.getFromHash().equals(oldhash)) continue;
                    diffs.put(artifact, diff);
                    downloadSize = diff.getSize();
                }
            }
            toDownload.add(artifact);
            totalDownloadBytes += downloadSize;
        }
        double START_PROGRESS = 0.15;
        int downloaded = 0;
        SplashScreen.stage(0.15, "Downloading", "");
        for (Artifact artifact : toDownload) {
            File dest = new File(REPO_DIR, artifact.getName());
            int total = downloaded;
            Diff diff = (Diff)diffs.get(artifact);
            if (diff != null) {
                log.debug("Downloading diff {}", (Object)diff.getName());
                try {
                    HashCode hash;
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    int totalBytes = totalDownloadBytes;
                    Launcher.download(diff.getPath(), diff.getHash(), completed -> SplashScreen.stage(0.15, 0.8, null, diff.getName(), total + completed, totalBytes, true), out);
                    downloaded += diff.getSize();
                    File old = new File(REPO_DIR, diff.getFrom());
                    try (GZIPInputStream patchStream = new GZIPInputStream(new ByteArrayInputStream(out.toByteArray()));
                         HashingOutputStream fout = new HashingOutputStream(Hashing.sha256(), Files.newOutputStream(dest.toPath(), new OpenOption[0]));){
                        new FileByFileV1DeltaApplier().applyDelta(old, patchStream, fout);
                        hash = fout.hash();
                    }
                    if (artifact.getHash().equals(hash.toString())) {
                        log.debug("Patching successful for {}", (Object)artifact.getName());
                        continue;
                    }
                    log.debug("Patched artifact hash mismatches! {}: got {} expected {}", artifact.getName(), hash.toString(), artifact.getHash());
                }
                catch (IOException | VerificationException e) {
                    log.warn("unable to download patch {}", (Object)diff.getName(), (Object)e);
                }
                totalDownloadBytes -= diff.getSize();
                totalDownloadBytes += artifact.getSize();
            }
            log.debug("Downloading {}", (Object)artifact.getName());
            try {
                OutputStream fout = Files.newOutputStream(dest.toPath(), new OpenOption[0]);
                try {
                    int totalBytes = totalDownloadBytes;
                    Launcher.download(artifact.getPath(), artifact.getHash(), completed -> SplashScreen.stage(0.15, 0.8, null, artifact.getName(), total + completed, totalBytes, true), fout);
                    downloaded += artifact.getSize();
                }
                finally {
                    if (fout == null) continue;
                    fout.close();
                }
            }
            catch (VerificationException e) {
                log.warn("unable to verify jar {}", (Object)artifact.getName(), (Object)e);
            }
        }
    }

    private static void clean(List<Artifact> artifacts) {
        File[] existingFiles = REPO_DIR.listFiles();
        if (existingFiles == null) {
            return;
        }
        HashSet<String> artifactNames = new HashSet<String>();
        for (Artifact artifact : artifacts) {
            artifactNames.add(artifact.getName());
            if (artifact.getDiffs() == null) continue;
            for (Diff diff : artifact.getDiffs()) {
                artifactNames.add(diff.getFrom());
            }
        }
        for (File file : existingFiles) {
            if (!file.isFile() || artifactNames.contains(file.getName())) continue;
            if (file.delete()) {
                log.debug("Deleted old artifact {}", (Object)file);
                continue;
            }
            log.warn("Unable to delete old artifact {}", (Object)file);
        }
    }

    private static void verifyJarHashes(List<Artifact> artifacts) throws VerificationException {
        for (Artifact artifact : artifacts) {
            String fileHash;
            String expectedHash = artifact.getHash();
            try {
                fileHash = Launcher.hash(new File(REPO_DIR, artifact.getName()));
            }
            catch (IOException e) {
                throw new VerificationException("unable to hash file", e);
            }
            if (!fileHash.equals(expectedHash)) {
                log.warn("Expected {} for {} but got {}", expectedHash, artifact.getName(), fileHash);
                throw new VerificationException("Expected " + expectedHash + " for " + artifact.getName() + " but got " + fileHash);
            }
            log.info("Verified hash of {}", (Object)artifact.getName());
        }
    }

    private static String hash(File file) throws IOException {
        HashFunction sha256 = Hashing.sha256();
        return com.google.common.io.Files.asByteSource(file).hash(sha256).toString();
    }

    private static Certificate getCertificate() throws CertificateException {
        CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
        Certificate certificate = certFactory.generateCertificate(Launcher.class.getResourceAsStream("runelite.crt"));
        return certificate;
    }

    static int compareVersion(String a, String b) {
        Pattern tok = Pattern.compile("[^0-9a-zA-Z]");
        return Arrays.compare(tok.split(a), tok.split(b), (x, y) -> {
            Integer ix = null;
            try {
                ix = Integer.parseInt(x);
            }
            catch (NumberFormatException numberFormatException) {
                // empty catch block
            }
            Integer iy = null;
            try {
                iy = Integer.parseInt(y);
            }
            catch (NumberFormatException numberFormatException) {
                // empty catch block
            }
            if (ix == null && iy == null) {
                return x.compareToIgnoreCase((String)y);
            }
            if (ix == null) {
                return -1;
            }
            if (iy == null) {
                return 1;
            }
            if (ix > iy) {
                return 1;
            }
            if (ix < iy) {
                return -1;
            }
            return 0;
        });
    }

    static void download(String path, String hash, IntConsumer progress, OutputStream out) throws IOException, VerificationException {
        HttpResponse<InputStream> response;
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(path)).header("User-Agent", USER_AGENT).GET().build();
        try {
            response = httpClient.send(request, HttpResponse.BodyHandlers.ofInputStream());
        }
        catch (InterruptedException ex) {
            throw new IOException(ex);
        }
        if (response.statusCode() != 200) {
            throw new IOException("Unable to download " + path + " (status code " + response.statusCode() + ")");
        }
        int downloaded = 0;
        HashingOutputStream hout = new HashingOutputStream(Hashing.sha256(), out);
        try (InputStream in = response.body();){
            int i;
            byte[] buffer = new byte[0x100000];
            while ((i = in.read(buffer)) != -1) {
                hout.write(buffer, 0, i);
                progress.accept(downloaded += i);
            }
        }
        HashCode hashCode = hout.hash();
        if (!hash.equals(hashCode.toString())) {
            throw new VerificationException("Unable to verify resource " + path + " - expected " + hash + " got " + hashCode.toString());
        }
    }

    static boolean isJava17() {
        return Runtime.version().feature() >= 16;
    }

    private static void postInstall(LauncherSettings settings) {
        Bootstrap bootstrap;
        try {
            bootstrap = Launcher.getBootstrap();
        }
        catch (IOException | InvalidKeyException | NoSuchAlgorithmException | SignatureException | CertificateException | VerificationException ex) {
            log.error("error fetching bootstrap", ex);
            return;
        }
        PackrConfig.updateLauncherArgs(bootstrap, settings);
        log.info("Performed postinstall steps");
    }

    private static void initDll() {
        if (OS.getOs() != OS.OSType.Windows) {
            return;
        }
        String arch = System.getProperty("os.arch");
        if (!Set.of("x86", "amd64", "aarch64").contains(arch)) {
            log.debug("System architecture is not supported for launcher natives: {}", (Object)arch);
            return;
        }
        try {
            System.loadLibrary("launcher_" + arch);
            log.debug("Loaded launcher native launcher_{}", (Object)arch);
            nativesLoaded = true;
        }
        catch (Error ex) {
            log.debug("Error loading launcher native", ex);
        }
    }

    private static void initDllBlacklist() {
        String blacklistedDlls = System.getProperty("runelite.launcher.blacklistedDlls");
        if (blacklistedDlls == null || blacklistedDlls.isEmpty()) {
            return;
        }
        String[] dlls = blacklistedDlls.split(",");
        try {
            log.debug("Setting blacklisted dlls: {}", (Object)blacklistedDlls);
            Launcher.setBlacklistedDlls(dlls);
        }
        catch (UnsatisfiedLinkError ex) {
            log.debug("Error setting dll blacklist", ex);
        }
    }

    private static native void setBlacklistedDlls(String[] var0);

    static native String regQueryString(String var0, String var1);

    static native boolean regDeleteValue(String var0, String var1, String var2);

    static native boolean isProcessElevated(long var0);

    static native void setFileACL(String var0, String[] var1);

    static native String getUserSID();

    static native long runas(String var0, String var1);
}

