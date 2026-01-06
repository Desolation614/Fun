/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  ch.qos.logback.classic.Level
 *  ch.qos.logback.classic.Logger
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.base.MoreObjects
 *  com.google.gson.Gson
 *  com.google.gson.JsonElement
 *  com.google.inject.Guice
 *  com.google.inject.Inject
 *  com.google.inject.Injector
 *  com.google.inject.Module
 *  io.sentry.Sentry
 *  javax.annotation.Nullable
 *  javax.inject.Provider
 *  javax.inject.Singleton
 *  joptsimple.ArgumentAcceptingOptionSpec
 *  joptsimple.OptionParser
 *  joptsimple.OptionSet
 *  joptsimple.OptionSpec
 *  joptsimple.OptionSpecBuilder
 *  joptsimple.ValueConversionException
 *  joptsimple.ValueConverter
 *  net.runelite.api.Client
 *  net.runelite.api.Constants
 *  net.runelite.http.api.RuneLiteAPI
 *  okhttp3.Cache
 *  okhttp3.OkHttpClient
 *  okhttp3.OkHttpClient$Builder
 *  okhttp3.Request
 *  okhttp3.Response
 *  org.apache.commons.lang3.SystemUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package net.runelite.client;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.MoreObjects;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Module;
import io.sentry.Sentry;
import java.applet.Applet;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.FileAttribute;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nullable;
import javax.inject.Provider;
import javax.inject.Singleton;
import javax.management.ObjectName;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;
import javax.swing.SwingUtilities;
import joptsimple.ArgumentAcceptingOptionSpec;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import joptsimple.OptionSpecBuilder;
import joptsimple.ValueConversionException;
import joptsimple.ValueConverter;
import net.runelite.api.Client;
import net.runelite.api.Constants;
import net.runelite.client.ClassPreloader;
import net.runelite.client.ClientSessionManager;
import net.runelite.client.RuneLiteModule;
import net.runelite.client.RuneLiteProperties;
import net.runelite.client.RuntimeConfig;
import net.runelite.client.RuntimeConfigLoader;
import net.runelite.client.TelemetryClient;
import net.runelite.client.Updater;
import net.runelite.client.account.SessionManager;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.discord.DiscordService;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.externalplugins.ExternalPluginManager;
import net.runelite.client.plugins.PluginManager;
import net.runelite.client.rs.ClientLoader;
import net.runelite.client.ui.ClientUI;
import net.runelite.client.ui.FatalErrorDialog;
import net.runelite.client.ui.SplashScreen;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.ui.overlay.WidgetOverlay;
import net.runelite.client.ui.overlay.tooltip.TooltipOverlay;
import net.runelite.client.ui.overlay.worldmap.WorldMapOverlay;
import net.runelite.client.util.OSType;
import net.runelite.client.util.ReflectUtil;
import net.runelite.http.api.RuneLiteAPI;
import okhttp3.Cache;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.commons.lang3.SystemUtils;
import org.slf4j.LoggerFactory;

@Singleton
public class RuneLite {
    private static final org.slf4j.Logger log = LoggerFactory.getLogger(RuneLite.class);
    public static final File RUNELITE_DIR = new File(System.getProperty("user.home"), ".ferox");
    public static final File CACHE_DIR = new File(RUNELITE_DIR, "cache");
    public static final File PLUGINS_DIR = new File(RUNELITE_DIR, "plugins");
    public static final File SCREENSHOT_DIR = new File(RUNELITE_DIR, "screenshots");
    public static final File LOGS_DIR = new File(RUNELITE_DIR, "logs");
    public static final File DEFAULT_SESSION_FILE = new File(RUNELITE_DIR, "session");
    public static final File NOTIFICATIONS_DIR = new File(RUNELITE_DIR, "notifications");
    private static final int MAX_OKHTTP_CACHE_SIZE = 0x1400000;
    public static String USER_AGENT = "RuneLite/" + RuneLiteProperties.getVersion() + "-" + RuneLiteProperties.getCommit() + (RuneLiteProperties.isDirty() ? "+" : "");
    private static Injector injector;
    @Inject
    private PluginManager pluginManager;
    @Inject
    private ExternalPluginManager externalPluginManager;
    @Inject
    private EventBus eventBus;
    @Inject
    private ConfigManager configManager;
    @Inject
    private SessionManager sessionManager;
    @Inject
    private DiscordService discordService;
    @Inject
    private ClientSessionManager clientSessionManager;
    @Inject
    private ClientUI clientUI;
    @Inject
    private OverlayManager overlayManager;
    @Inject
    private Provider<TooltipOverlay> tooltipOverlay;
    @Inject
    private Provider<WorldMapOverlay> worldMapOverlay;
    @Inject
    private Gson gson;
    @Inject
    @Nullable
    private Client client;
    @Inject
    @Nullable
    private RuntimeConfig runtimeConfig;
    @Inject
    @Nullable
    private TelemetryClient telemetryClient;
    @Inject
    private ScheduledExecutorService scheduledExecutorService;

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void main(String[] args) throws Exception {
        OkHttpClient okHttpClient;
        Locale.setDefault(Locale.ENGLISH);
        OptionParser parser = new OptionParser(false);
        parser.accepts("developer-mode", "Enable developer tools");
        parser.accepts("debug", "Show extra debugging output");
        parser.accepts("safe-mode", "Disables external plugins and the GPU plugin");
        parser.accepts("insecure-skip-tls-verification", "Disables TLS verification");
        parser.accepts("jav_config", "jav_config url").withRequiredArg().defaultsTo((Object)RuneLiteProperties.getJavConfig(), (Object[])new String[0]);
        parser.accepts("disable-telemetry", "Disable telemetry");
        parser.accepts("profile", "Configuration profile to use").withRequiredArg();
        parser.accepts("noupdate", "Skips the launcher update");
        parser.accepts("local-config", "Enables loading the applet params from resources");
        ArgumentAcceptingOptionSpec sessionfile = parser.accepts("sessionfile", "Use a specified session file").withRequiredArg().withValuesConvertedBy((ValueConverter)new ConfigFileConverter()).defaultsTo((Object)DEFAULT_SESSION_FILE, (Object[])new File[0]);
        OptionSpecBuilder insecureWriteCredentials = parser.accepts("insecure-write-credentials", "Dump authentication tokens from the Jagex Launcher to a text file to be used for development");
        parser.accepts("help", "Show this text").forHelp();
        OptionSet options = parser.parse(args);
        if (options.has("help")) {
            parser.printHelpOn((OutputStream)System.out);
            System.exit(0);
        }
        if (options.has("debug")) {
            Logger logger = (Logger)LoggerFactory.getLogger((String)"ROOT");
            logger.setLevel(Level.DEBUG);
        }
        Thread.setDefaultUncaughtExceptionHandler((thread, throwable) -> {
            log.error("Uncaught exception:", throwable);
            if (throwable instanceof AbstractMethodError) {
                log.error("Classes are out of date; Build with maven again.");
            }
        });
        RuneLiteAPI.CLIENT = okHttpClient = RuneLite.buildHttpClient(options.has("insecure-skip-tls-verification"));
        Sentry.init(sentryOptions -> {
            sentryOptions.setDsn("https://90caaed4ef6b7090e9c8a7ff4f927756@o480827.ingest.us.sentry.io/4509909296742400");
            sentryOptions.setRelease(RuneLiteProperties.getVersion() + "-" + RuneLiteProperties.getCommit());
            sentryOptions.setEnvironment(options.has("local-config") ? "development" : "production");
            sentryOptions.setAttachStacktrace(true);
            sentryOptions.setAttachServerName(false);
            sentryOptions.setSendDefaultPii(true);
        });
        try {
            Sentry.setTag((String)"os.arch", (String)SystemUtils.OS_ARCH);
            Sentry.setTag((String)"os.name", (String)SystemUtils.OS_NAME);
            Sentry.setTag((String)"os.version", (String)SystemUtils.OS_VERSION);
        }
        catch (Exception e) {
            Sentry.setTag((String)"os.arch", (String)"Unknown");
            Sentry.setTag((String)"os.name", (String)"Unknown");
            Sentry.setTag((String)"os.version", (String)"Unknown");
        }
        Sentry.setExtra((String)"launcher_version", (String)((String)MoreObjects.firstNonNull((Object)RuneLiteProperties.getLauncherVersion(), (Object)"unknown")));
        SplashScreen.init();
        SplashScreen.stage(0.0, "Preparing Ferox", "");
        try {
            boolean developerMode;
            RuntimeConfigLoader runtimeConfigLoader = new RuntimeConfigLoader(okHttpClient);
            ClientLoader clientLoader = new ClientLoader(okHttpClient, runtimeConfigLoader, (String)options.valueOf("jav_config"), options.has("local-config"));
            new Thread(() -> {
                clientLoader.get();
                ClassPreloader.preload();
            }, "Preloader").start();
            boolean bl = developerMode = options.has("developer-mode") && RuneLiteProperties.getLauncherVersion() == null;
            if (developerMode) {
                boolean assertions = false;
                if (!$assertionsDisabled) {
                    assertions = true;
                    if (!true) {
                        throw new AssertionError();
                    }
                }
                if (!assertions) {
                    SwingUtilities.invokeLater(() -> new FatalErrorDialog("Developers should enable assertions; Add `-ea` to your JVM arguments`").addHelpButtons().addBuildingGuide().open());
                    return;
                }
            }
            log.info("RuneLite {} (launcher version {}) starting up, args: {}", new Object[]{RuneLiteProperties.getVersion(), MoreObjects.firstNonNull((Object)RuneLiteProperties.getLauncherVersion(), (Object)"unknown"), args.length == 0 ? "none" : String.join((CharSequence)" ", args)});
            RuntimeMXBean runtime = ManagementFactory.getRuntimeMXBean();
            log.info("Java VM arguments: {}", (Object)String.join((CharSequence)" ", runtime.getInputArguments()));
            long start = System.currentTimeMillis();
            injector = Guice.createInjector((Module[])new Module[]{new RuneLiteModule(okHttpClient, clientLoader, runtimeConfigLoader, developerMode, options.has("safe-mode"), true, (File)options.valueOf((OptionSpec)sessionfile), (String)options.valueOf("profile"), options.has((OptionSpec)insecureWriteCredentials), options.has("noupdate"))});
            ((RuneLite)injector.getInstance(RuneLite.class)).start();
            long end = System.currentTimeMillis();
            long uptime = runtime.getUptime();
            log.info("Client initialization took {}ms. Uptime: {}ms", (Object)(end - start), (Object)uptime);
        }
        catch (Exception e) {
            log.error("Failure during startup", (Throwable)e);
            SwingUtilities.invokeLater(() -> new FatalErrorDialog("RuneLite has encountered an unexpected error during startup.").addHelpButtons().open());
        }
        finally {
            SplashScreen.stop();
        }
    }

    public void start() throws Exception {
        injector.injectMembers((Object)this.client);
        this.setupSystemProps();
        this.setupCompilerControl();
        RuneLite.copyJagexCache();
        Applet applet = (Applet)this.client;
        applet.setSize(Constants.GAME_FIXED_SIZE);
        System.setProperty("jagex.disableBouncyCastle", "true");
        System.setProperty("jagex.userhome", RUNELITE_DIR.getAbsolutePath());
        applet.init();
        applet.start();
        SplashScreen.stage(0.57, null, "Loading configuration");
        this.sessionManager.loadSession();
        this.configManager.load();
        Updater updater = (Updater)injector.getInstance(Updater.class);
        updater.update();
        this.pluginManager.loadCorePlugins();
        this.pluginManager.loadSideLoadPlugins();
        this.externalPluginManager.loadExternalPlugins();
        SplashScreen.stage(0.7, null, "Finalizing configuration");
        this.pluginManager.loadDefaultPluginConfiguration(null);
        this.clientSessionManager.start();
        this.eventBus.register(this.clientSessionManager);
        SplashScreen.stage(0.75, null, "Starting core interface");
        this.clientUI.init();
        this.discordService.init();
        this.eventBus.register(this.clientUI);
        this.eventBus.register(this.pluginManager);
        this.eventBus.register(this.externalPluginManager);
        this.eventBus.register(this.overlayManager);
        this.eventBus.register(this.configManager);
        this.eventBus.register(this.discordService);
        WidgetOverlay.createOverlays(this.overlayManager, this.client).forEach(this.overlayManager::add);
        this.overlayManager.add((Overlay)this.worldMapOverlay.get());
        this.overlayManager.add((Overlay)this.tooltipOverlay.get());
        this.pluginManager.startPlugins();
        SplashScreen.stop();
        this.clientUI.show();
        if (this.telemetryClient != null) {
            this.scheduledExecutorService.execute(() -> {
                this.telemetryClient.submitTelemetry();
                this.telemetryClient.submitVmErrors(LOGS_DIR);
            });
        }
        ReflectUtil.queueInjectorAnnotationCacheInvalidation(injector);
        ReflectUtil.invalidateAnnotationCaches();
    }

    @VisibleForTesting
    public static void setInjector(Injector injector) {
        RuneLite.injector = injector;
    }

    @VisibleForTesting
    static OkHttpClient buildHttpClient(boolean insecureSkipTlsVerification) {
        OkHttpClient.Builder builder = new OkHttpClient.Builder().pingInterval(30L, TimeUnit.SECONDS).addInterceptor(chain -> {
            Request request = chain.request();
            if (request.header("User-Agent") != null) {
                return chain.proceed(request);
            }
            Request userAgentRequest = request.newBuilder().header("User-Agent", USER_AGENT).build();
            return chain.proceed(userAgentRequest);
        }).cache(new Cache(new File(CACHE_DIR, "okhttp"), 0x1400000L)).addNetworkInterceptor(chain -> {
            Response res = chain.proceed(chain.request());
            if (res.code() >= 400 && "GET".equals(res.request().method())) {
                res = res.newBuilder().header("Cache-Control", "no-store").build();
            }
            return res;
        });
        try {
            if (insecureSkipTlsVerification || RuneLiteProperties.isInsecureSkipTlsVerification()) {
                RuneLite.setupInsecureTrustManager(builder);
            } else {
                RuneLite.setupTrustManager(builder);
            }
        }
        catch (KeyManagementException | KeyStoreException | NoSuchAlgorithmException e) {
            log.warn("error setting up trust manager", (Throwable)e);
        }
        return builder.build();
    }

    private static void copyJagexCache() {
    }

    private void setupSystemProps() {
        if (this.runtimeConfig == null || this.runtimeConfig.getSysProps() == null) {
            return;
        }
        for (Map.Entry<String, String> entry : this.runtimeConfig.getSysProps().entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            log.debug("Setting property {}={}", (Object)key, (Object)value);
            System.setProperty(key, value);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void setupCompilerControl() {
        try {
            Path file = Files.createTempFile("rl_compilercontrol", "", new FileAttribute[0]);
            try {
                if (this.runtimeConfig != null && this.runtimeConfig.getCompilerControl() != null) {
                    String json = this.gson.toJson((JsonElement)this.runtimeConfig.getCompilerControl());
                    Files.writeString(file, (CharSequence)json, StandardCharsets.UTF_8, new OpenOption[0]);
                } else {
                    try (InputStream in = RuneLite.class.getResourceAsStream("/compilercontrol.json");){
                        Files.copy(in, file, StandardCopyOption.REPLACE_EXISTING);
                    }
                }
                ManagementFactory.getPlatformMBeanServer().invoke(new ObjectName("com.sun.management:type=DiagnosticCommand"), "compilerDirectivesAdd", new Object[]{new String[]{file.toFile().getAbsolutePath()}}, new String[]{String[].class.getName()});
            }
            finally {
                Files.delete(file);
            }
        }
        catch (Exception e) {
            log.info("Failed to set compiler control", (Throwable)e);
        }
    }

    private static TrustManager[] loadTrustManagers(String trustStoreType) throws KeyStoreException, NoSuchAlgorithmException {
        String old = trustStoreType != null ? System.setProperty("javax.net.ssl.trustStoreType", trustStoreType) : System.clearProperty("javax.net.ssl.trustStoreType");
        TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        trustManagerFactory.init((KeyStore)null);
        TrustManager[] trustManagers = trustManagerFactory.getTrustManagers();
        if (old == null) {
            System.clearProperty("javax.net.ssl.trustStoreType");
        } else {
            System.setProperty("javax.net.ssl.trustStoreType", old);
        }
        return trustManagers;
    }

    private static void setupTrustManager(OkHttpClient.Builder okHttpClientBuilder) throws KeyStoreException, NoSuchAlgorithmException, KeyManagementException {
        if (OSType.getOSType() != OSType.Windows) {
            return;
        }
        TrustManager[] jreTms = RuneLite.loadTrustManagers(null);
        TrustManager[] windowsTms = RuneLite.loadTrustManagers("Windows-ROOT");
        final TrustManager[] trustManagers = new TrustManager[jreTms.length + windowsTms.length];
        System.arraycopy(jreTms, 0, trustManagers, 0, jreTms.length);
        System.arraycopy(windowsTms, 0, trustManagers, jreTms.length, windowsTms.length);
        X509TrustManager combiningTrustManager = new X509TrustManager(){

            @Override
            public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                CertificateException exception = null;
                for (TrustManager trustManager : trustManagers) {
                    if (!(trustManager instanceof X509TrustManager)) continue;
                    try {
                        ((X509TrustManager)trustManager).checkClientTrusted(chain, authType);
                        return;
                    }
                    catch (CertificateException ex) {
                        exception = ex;
                    }
                }
                if (exception != null) {
                    throw exception;
                }
                throw new CertificateException("no X509TrustManagers present");
            }

            @Override
            public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                CertificateException exception = null;
                for (TrustManager trustManager : trustManagers) {
                    if (!(trustManager instanceof X509TrustManager)) continue;
                    try {
                        ((X509TrustManager)trustManager).checkServerTrusted(chain, authType);
                        return;
                    }
                    catch (CertificateException ex) {
                        exception = ex;
                    }
                }
                if (exception != null) {
                    throw exception;
                }
                throw new CertificateException("no X509TrustManagers present");
            }

            @Override
            public X509Certificate[] getAcceptedIssuers() {
                ArrayList<X509Certificate> certificates = new ArrayList<X509Certificate>();
                for (TrustManager trustManager : trustManagers) {
                    if (!(trustManager instanceof X509TrustManager)) continue;
                    certificates.addAll(Arrays.asList(((X509TrustManager)trustManager).getAcceptedIssuers()));
                }
                return certificates.toArray(new X509Certificate[0]);
            }
        };
        SSLContext sc = SSLContext.getInstance("TLS");
        sc.init(null, new TrustManager[]{combiningTrustManager}, new SecureRandom());
        okHttpClientBuilder.sslSocketFactory(sc.getSocketFactory(), combiningTrustManager);
    }

    private static void setupInsecureTrustManager(OkHttpClient.Builder okHttpClientBuilder) throws NoSuchAlgorithmException, KeyManagementException {
        X509TrustManager trustManager = new X509TrustManager(){

            @Override
            public void checkClientTrusted(X509Certificate[] chain, String authType) {
            }

            @Override
            public void checkServerTrusted(X509Certificate[] chain, String authType) {
            }

            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[0];
            }
        };
        SSLContext sc = SSLContext.getInstance("TLS");
        sc.init(null, new TrustManager[]{trustManager}, new SecureRandom());
        okHttpClientBuilder.sslSocketFactory(sc.getSocketFactory(), trustManager);
    }

    public static Injector getInjector() {
        return injector;
    }

    private static /* synthetic */ void lambda$copyJagexCache$8(Path to, Path from, Path source) {
        try {
            Files.copy(source, to.resolve(from.relativize(source)), StandardCopyOption.COPY_ATTRIBUTES);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static class ConfigFileConverter
    implements ValueConverter<File> {
        private ConfigFileConverter() {
        }

        public File convert(String fileName) {
            File file = Paths.get(fileName, new String[0]).isAbsolute() || fileName.startsWith("./") || fileName.startsWith(".\\") ? new File(fileName) : new File(RUNELITE_DIR, fileName);
            if (!(!file.exists() || file.isFile() && file.canWrite())) {
                throw new ValueConversionException(String.format("File %s is not accessible", file.getAbsolutePath()));
            }
            return file;
        }

        public Class<? extends File> valueType() {
            return File.class;
        }

        public String valuePattern() {
            return null;
        }
    }
}

