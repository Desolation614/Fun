/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.HashMultimap
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.Lists
 *  com.google.common.hash.Hashing
 *  com.google.common.hash.HashingInputStream
 *  com.google.common.io.FileWriteMode
 *  com.google.common.io.Files
 *  com.google.gson.Gson
 *  javax.annotation.Nullable
 *  javax.inject.Inject
 *  javax.inject.Named
 *  javax.inject.Singleton
 *  okhttp3.HttpUrl
 *  okhttp3.OkHttpClient
 *  okhttp3.Request$Builder
 *  okhttp3.Response
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package net.runelite.client.externalplugins;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.hash.Hashing;
import com.google.common.hash.HashingInputStream;
import com.google.common.io.FileWriteMode;
import com.google.common.io.Files;
import com.google.gson.Gson;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import javax.swing.SwingUtilities;
import net.runelite.client.RuneLite;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ExternalPluginsChanged;
import net.runelite.client.events.ProfileChanged;
import net.runelite.client.externalplugins.ExternalPluginClient;
import net.runelite.client.externalplugins.PluginHubClassLoader;
import net.runelite.client.externalplugins.PluginHubManifest;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginInstantiationException;
import net.runelite.client.plugins.PluginManager;
import net.runelite.client.ui.SplashScreen;
import net.runelite.client.util.CountingInputStream;
import net.runelite.client.util.Text;
import net.runelite.client.util.VerificationException;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class ExternalPluginManager {
    private static final Logger log = LoggerFactory.getLogger(ExternalPluginManager.class);
    private static final String PLUGIN_LIST_KEY = "externalPlugins";
    private static Class<? extends Plugin>[] builtinExternals = null;
    @Inject
    @Named(value="safeMode")
    private boolean safeMode;
    private final ConfigManager configManager;
    private final ExternalPluginClient externalPluginClient;
    private final ScheduledExecutorService executor;
    private final PluginManager pluginManager;
    private final EventBus eventBus;
    private final OkHttpClient okHttpClient;
    private final Gson gson;

    @Inject
    private ExternalPluginManager(ConfigManager configManager, ExternalPluginClient externalPluginClient, ScheduledExecutorService executor, PluginManager pluginManager, EventBus eventBus, OkHttpClient okHttpClient, Gson gson) {
        this.configManager = configManager;
        this.externalPluginClient = externalPluginClient;
        this.executor = executor;
        this.pluginManager = pluginManager;
        this.eventBus = eventBus;
        this.okHttpClient = okHttpClient;
        this.gson = gson;
        executor.scheduleWithFixedDelay(() -> externalPluginClient.submitPlugins(this.getInstalledExternalPlugins()), new Random().nextInt(60), 180L, TimeUnit.MINUTES);
    }

    public void loadExternalPlugins() throws PluginInstantiationException {
        this.refreshPlugins();
        if (builtinExternals != null) {
            this.pluginManager.loadPlugins(Lists.newArrayList((Object[])builtinExternals), null);
        }
    }

    @Subscribe
    public void onProfileChanged(ProfileChanged profileChanged) {
        this.executor.submit(this::refreshPlugins);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void refreshPlugins() {
        if (this.safeMode) {
            log.debug("External plugins are disabled in safe mode!");
            return;
        }
        HashSet<String> builtinExternalClasses = new HashSet<String>();
        if (builtinExternals != null) {
            for (Class<? extends Plugin> pluginClass : builtinExternals) {
                builtinExternalClasses.add(pluginClass.getName());
            }
        }
        HashMultimap loadedExternalPlugins = HashMultimap.create();
        for (Plugin p : this.pluginManager.getPlugins()) {
            PluginHubManifest.JarData m = ExternalPluginManager.getJarData(p.getClass());
            if (m == null) continue;
            loadedExternalPlugins.put((Object)m, (Object)p);
        }
        List<String> installedIDs = this.getInstalledExternalPlugins();
        if (installedIDs.isEmpty() && loadedExternalPlugins.isEmpty()) {
            return;
        }
        boolean startup = SplashScreen.isOpen();
        try {
            double splashLength;
            double splashStart = startup ? 0.6 : 0.0;
            double d = splashLength = startup ? 0.1 : 1.0;
            if (!startup) {
                SplashScreen.init();
            }
            Instant now = Instant.now();
            Instant keepAfter = now.minus(3L, ChronoUnit.DAYS);
            SplashScreen.stage(splashStart, null, "Downloading external plugins");
            HashSet<PluginHubManifest.JarData> externalPlugins = new HashSet<PluginHubManifest.JarData>();
            RuneLite.PLUGINS_DIR.mkdirs();
            try {
                PluginHubManifest.ManifestLite manifest = this.externalPluginClient.downloadManifestLite();
                Map manifests = (Map)manifest.getJars().stream().collect(ImmutableMap.toImmutableMap(PluginHubManifest.JarData::getInternalName, Function.identity()));
                HashSet<PluginHubManifest.JarData> needsDownload = new HashSet<PluginHubManifest.JarData>();
                HashSet<File> keep = new HashSet<File>();
                for (String string : installedIDs) {
                    PluginHubManifest.JarData jarData = (PluginHubManifest.JarData)manifests.get(string);
                    if (jarData == null) continue;
                    externalPlugins.add(jarData);
                    jarData.getJarFile().setLastModified(now.toEpochMilli());
                    if (!jarData.isValid()) {
                        needsDownload.add(jarData);
                        continue;
                    }
                    keep.add(jarData.getJarFile());
                }
                File[] files = RuneLite.PLUGINS_DIR.listFiles();
                if (files != null) {
                    for (File fi : files) {
                        if (keep.contains(fi) || fi.lastModified() >= keepAfter.toEpochMilli()) continue;
                        fi.delete();
                    }
                }
                int n = needsDownload.stream().mapToInt(PluginHubManifest.JarData::getJarSize).sum();
                int downloaded = 0;
                for (PluginHubManifest.JarData jarData : needsDownload) {
                    HttpUrl url = this.externalPluginClient.getJarURL(jarData);
                    try {
                        Response res = this.okHttpClient.newCall(new Request.Builder().url(url).build()).execute();
                        try {
                            int fdownloaded = downloaded;
                            downloaded += jarData.getJarSize();
                            HashingInputStream his = new HashingInputStream(Hashing.sha256(), (InputStream)new CountingInputStream(res.body().byteStream(), i -> SplashScreen.stage(splashStart + splashLength * 0.2, splashStart + splashLength * 0.8, null, "Downloading " + jarData.getDisplayName(), i + fdownloaded, toDownload, true)));
                            Files.asByteSink((File)jarData.getJarFile(), (FileWriteMode[])new FileWriteMode[0]).writeFrom((InputStream)his);
                            if (PluginHubManifest.HASH_ENCODER.encodeToString(his.hash().asBytes()).equals(jarData.getJarHash())) continue;
                            throw new VerificationException("Plugin " + jarData.getInternalName() + " didn't match its hash");
                        }
                        finally {
                            if (res == null) continue;
                            res.close();
                        }
                    }
                    catch (IOException | VerificationException e) {
                        externalPlugins.remove(jarData);
                        log.error("Unable to download external plugin \"{}\"", (Object)jarData.getInternalName(), (Object)e);
                    }
                }
            }
            catch (IOException | VerificationException e) {
                log.error("Unable to download external plugins", (Throwable)e);
                if (!startup) {
                    SplashScreen.stop();
                }
                return;
            }
            SplashScreen.stage(splashStart + splashLength * 0.8, null, "Starting external plugins");
            HashSet<PluginHubManifest.JarData> add = new HashSet<PluginHubManifest.JarData>();
            for (PluginHubManifest.JarData jarData : externalPlugins) {
                if (loadedExternalPlugins.removeAll((Object)jarData).size() > 0) continue;
                add.add(jarData);
            }
            Collection remove = loadedExternalPlugins.values();
            for (Plugin p : remove) {
                log.info("Stopping external plugin \"{}\"", p.getClass());
                try {
                    SwingUtilities.invokeAndWait(() -> {
                        try {
                            this.pluginManager.stopPlugin(p);
                        }
                        catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    });
                }
                catch (InterruptedException | InvocationTargetException e) {
                    log.warn("Unable to stop external plugin \"{}\"", (Object)p.getClass().getName(), (Object)e);
                }
                this.pluginManager.remove(p);
            }
            for (PluginHubManifest.JarData jarData : add) {
                if (!jarData.isValid()) {
                    log.warn("Invalid plugin for validated manifest: {}", (Object)jarData);
                    continue;
                }
                log.info("Loading external plugin \"{}\" jar \"{}\"", (Object)jarData.getInternalName(), (Object)jarData.getJarHash());
                List<Plugin> newPlugins = null;
                try {
                    PluginHubClassLoader pluginHubClassLoader = new PluginHubClassLoader(jarData, new URL[]{jarData.getJarFile().toURI().toURL()}, this.gson);
                    if (Arrays.stream(pluginHubClassLoader.getStub().getPlugins()).anyMatch(builtinExternalClasses::contains)) {
                        log.debug("Skipping loading \"{}\" from hub as a conflicting builtin external is present", (Object)jarData.getInternalName());
                        continue;
                    }
                    ArrayList clazzes = new ArrayList();
                    for (String className : pluginHubClassLoader.getStub().getPlugins()) {
                        clazzes.add(pluginHubClassLoader.loadClass(className));
                    }
                    List<Plugin> newPlugins2 = newPlugins = this.pluginManager.loadPlugins(clazzes, null);
                    if (startup) continue;
                    this.pluginManager.loadDefaultPluginConfiguration(newPlugins);
                    SwingUtilities.invokeAndWait(() -> {
                        try {
                            for (Plugin p : newPlugins2) {
                                this.pluginManager.startPlugin(p);
                            }
                        }
                        catch (PluginInstantiationException e) {
                            throw new RuntimeException(e);
                        }
                    });
                }
                catch (ThreadDeath threadDeath) {
                    throw threadDeath;
                }
                catch (Throwable throwable) {
                    log.warn("Unable to start or load external plugin \"{}\"", (Object)jarData.getInternalName(), (Object)throwable);
                    if (newPlugins == null) continue;
                    for (Plugin p : newPlugins) {
                        try {
                            SwingUtilities.invokeAndWait(() -> {
                                try {
                                    this.pluginManager.stopPlugin(p);
                                }
                                catch (Exception e2) {
                                    throw new RuntimeException(e2);
                                }
                            });
                        }
                        catch (InterruptedException | InvocationTargetException e2) {
                            log.info("Unable to fully stop plugin \"{}\"", (Object)jarData.getInternalName(), (Object)e2);
                        }
                        this.pluginManager.remove(p);
                    }
                }
            }
            if (!startup) {
                this.eventBus.post(new ExternalPluginsChanged());
            }
        }
        finally {
            if (!startup) {
                SplashScreen.stop();
            }
        }
    }

    public List<String> getInstalledExternalPlugins() {
        String externalPluginsStr = this.configManager.getConfiguration("runelite", PLUGIN_LIST_KEY);
        return Text.fromCSV(externalPluginsStr == null ? "" : externalPluginsStr);
    }

    public void install(String key) {
        HashSet<String> plugins = new HashSet<String>(this.getInstalledExternalPlugins());
        if (plugins.add(key)) {
            this.configManager.setConfiguration("runelite", PLUGIN_LIST_KEY, Text.toCSV(plugins));
            this.executor.submit(this::refreshPlugins);
        }
    }

    public void remove(String key) {
        HashSet<String> plugins = new HashSet<String>(this.getInstalledExternalPlugins());
        if (plugins.remove(key)) {
            this.configManager.setConfiguration("runelite", PLUGIN_LIST_KEY, Text.toCSV(plugins));
            this.executor.submit(this::refreshPlugins);
        }
    }

    public void update() {
        this.executor.submit(this::refreshPlugins);
    }

    @Nullable
    public static PluginHubManifest.JarData getJarData(Class<? extends Plugin> plugin) {
        ClassLoader cl = plugin.getClassLoader();
        if (cl instanceof PluginHubClassLoader) {
            PluginHubClassLoader ecl = (PluginHubClassLoader)cl;
            return ecl.getJarData();
        }
        return null;
    }

    @Nullable
    public static PluginHubManifest.DisplayData getDisplayData(Class<? extends Plugin> plugin) {
        ClassLoader cl = plugin.getClassLoader();
        if (cl instanceof PluginHubClassLoader) {
            PluginHubClassLoader ecl = (PluginHubClassLoader)cl;
            return ecl.getStub();
        }
        return null;
    }

    @Nullable
    public static String getInternalName(Class<? extends Plugin> plugin) {
        PluginHubManifest.JarData jd = ExternalPluginManager.getJarData(plugin);
        return jd == null ? null : jd.getInternalName();
    }

    public static void loadBuiltin(Class<? extends Plugin> ... plugins) {
        boolean assertsEnabled = false;
        if (!$assertionsDisabled) {
            assertsEnabled = true;
            if (!true) {
                throw new AssertionError();
            }
        }
        if (!assertsEnabled) {
            throw new RuntimeException("Assertions are not enabled, add '-ea' to your VM options. Enabling assertions during development catches undefined behavior and incorrect API usage.");
        }
        builtinExternals = plugins;
    }
}

