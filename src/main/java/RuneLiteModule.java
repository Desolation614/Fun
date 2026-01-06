/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Strings
 *  com.google.common.math.DoubleMath
 *  com.google.gson.Gson
 *  com.google.inject.AbstractModule
 *  com.google.inject.Provides
 *  com.google.inject.binder.ConstantBindingBuilder
 *  com.google.inject.name.Names
 *  com.google.inject.util.Providers
 *  javax.inject.Named
 *  javax.inject.Singleton
 *  net.runelite.api.Client
 *  net.runelite.api.hooks.Callbacks
 *  net.runelite.http.api.RuneLiteAPI
 *  okhttp3.HttpUrl
 *  okhttp3.OkHttpClient
 */
package net.runelite.client;

import com.google.common.base.Strings;
import com.google.common.math.DoubleMath;
import com.google.gson.Gson;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.binder.ConstantBindingBuilder;
import com.google.inject.name.Names;
import com.google.inject.util.Providers;
import java.applet.Applet;
import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.function.Supplier;
import javax.inject.Named;
import javax.inject.Singleton;
import javax.swing.SwingUtilities;
import net.runelite.api.Client;
import net.runelite.api.hooks.Callbacks;
import net.runelite.client.RuneLite;
import net.runelite.client.RuneLiteProperties;
import net.runelite.client.RuntimeConfig;
import net.runelite.client.RuntimeConfigLoader;
import net.runelite.client.RuntimeConfigRefresher;
import net.runelite.client.TelemetryClient;
import net.runelite.client.account.SessionManager;
import net.runelite.client.callback.Hooks;
import net.runelite.client.chat.ChatMessageManager;
import net.runelite.client.config.ChatColorConfig;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.config.RuneLiteConfig;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.game.ItemManager;
import net.runelite.client.menus.MenuManager;
import net.runelite.client.plugins.PluginManager;
import net.runelite.client.task.Scheduler;
import net.runelite.client.util.DeferredEventBus;
import net.runelite.client.util.ExecutorServiceExceptionLogger;
import net.runelite.http.api.RuneLiteAPI;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;

public class RuneLiteModule
extends AbstractModule {
    private final OkHttpClient bootupHttpClient;
    private final Supplier<Client> clientLoader;
    private final RuntimeConfigLoader configLoader;
    private final boolean developerMode;
    private final boolean safeMode;
    private final boolean disableTelemetry;
    private final File sessionfile;
    private final String profile;
    private final boolean insecureWriteCredentials;
    private final boolean noupdate;

    protected void configure() {
        Properties properties = RuneLiteProperties.getProperties();
        HashMap<Object, Object> props = new HashMap<Object, Object>(properties);
        RuntimeConfig runtimeConfig = this.configLoader.get();
        if (runtimeConfig != null && runtimeConfig.getProps() != null) {
            props.putAll(runtimeConfig.getProps());
        }
        for (Map.Entry entry : props.entrySet()) {
            ConstantBindingBuilder binder;
            String key = (String)entry.getKey();
            if (entry.getValue() instanceof String) {
                binder = this.bindConstant().annotatedWith((Annotation)Names.named((String)key));
                binder.to((String)entry.getValue());
                continue;
            }
            if (entry.getValue() instanceof Double) {
                binder = this.bindConstant().annotatedWith((Annotation)Names.named((String)key));
                if (DoubleMath.isMathematicalInteger((double)((Double)entry.getValue()))) {
                    binder.to((int)((Double)entry.getValue()).doubleValue());
                    continue;
                }
                binder.to(((Double)entry.getValue()).doubleValue());
                continue;
            }
            if (!(entry.getValue() instanceof Boolean)) continue;
            binder = this.bindConstant().annotatedWith((Annotation)Names.named((String)key));
            binder.to(((Boolean)entry.getValue()).booleanValue());
        }
        this.bindConstant().annotatedWith((Annotation)Names.named((String)"developerMode")).to(this.developerMode);
        this.bindConstant().annotatedWith((Annotation)Names.named((String)"safeMode")).to(this.safeMode);
        this.bindConstant().annotatedWith((Annotation)Names.named((String)"disableTelemetry")).to(this.disableTelemetry);
        this.bind(File.class).annotatedWith((Annotation)Names.named((String)"sessionfile")).toInstance((Object)this.sessionfile);
        this.bind(String.class).annotatedWith((Annotation)Names.named((String)"profile")).toProvider(Providers.of((Object)this.profile));
        this.bindConstant().annotatedWith((Annotation)Names.named((String)"insecureWriteCredentials")).to(this.insecureWriteCredentials);
        this.bindConstant().annotatedWith((Annotation)Names.named((String)"noupdate")).to(this.noupdate);
        this.bind(File.class).annotatedWith((Annotation)Names.named((String)"runeLiteDir")).toInstance((Object)RuneLite.RUNELITE_DIR);
        this.bind(ScheduledExecutorService.class).toInstance((Object)new ExecutorServiceExceptionLogger(Executors.newSingleThreadScheduledExecutor()));
        this.bind(RuntimeConfigLoader.class).toInstance((Object)this.configLoader);
        this.bind(RuntimeConfigRefresher.class).asEagerSingleton();
        this.bind(MenuManager.class);
        this.bind(ChatMessageManager.class);
        this.bind(ItemManager.class);
        this.bind(Scheduler.class);
        this.bind(PluginManager.class);
        this.bind(SessionManager.class);
        this.bind(Gson.class).toInstance((Object)RuneLiteAPI.GSON);
        this.bind(Callbacks.class).to(Hooks.class);
        this.bind(EventBus.class).toInstance((Object)new EventBus());
        this.bind(EventBus.class).annotatedWith((Annotation)Names.named((String)"Deferred EventBus")).to(DeferredEventBus.class);
    }

    @Provides
    @Singleton
    Applet provideApplet(Client client) {
        return (Applet)client;
    }

    @Provides
    @Singleton
    Client provideClient() {
        return this.clientLoader.get();
    }

    @Provides
    @Singleton
    RuntimeConfig provideRuntimeConfig() {
        return this.configLoader.get();
    }

    @Provides
    @Singleton
    RuneLiteConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(RuneLiteConfig.class);
    }

    @Provides
    @Singleton
    ChatColorConfig provideChatColorConfig(ConfigManager configManager) {
        return configManager.getConfig(ChatColorConfig.class);
    }

    @Provides
    @Singleton
    OkHttpClient provideHttpClient(Client client) {
        return this.bootupHttpClient.newBuilder().addInterceptor(chain -> {
            if (client.isClientThread()) {
                throw new IOException("Blocking network calls are not allowed on the client thread");
            }
            if (SwingUtilities.isEventDispatchThread()) {
                throw new IOException("Blocking network calls are not allowed on the event dispatch thread");
            }
            if (client.getEnvironment() != 0) {
                HttpUrl url = chain.request().url();
                for (String domain : RuneLiteProperties.getJagexBlockedDomains()) {
                    if (!url.host().endsWith(domain)) continue;
                    throw new IOException("Network call to " + String.valueOf(url) + " blocked outside of LIVE environment");
                }
            }
            return chain.proceed(chain.request());
        }).build();
    }

    @Provides
    @Named(value="runelite.api.base")
    HttpUrl provideApiBase(@Named(value="runelite.api.base") String s) {
        String prop = System.getProperty("runelite.http-service.url");
        return HttpUrl.get((String)(Strings.isNullOrEmpty((String)prop) ? s : prop));
    }

    @Provides
    @Named(value="runelite.session")
    HttpUrl provideSession(@Named(value="runelite.session") String s) {
        String prop = System.getProperty("runelite.session.url");
        return HttpUrl.get((String)(Strings.isNullOrEmpty((String)prop) ? s : prop));
    }

    @Provides
    @Named(value="runelite.static.base")
    HttpUrl provideStaticBase(@Named(value="runelite.static.base") String s) {
        String prop = System.getProperty("runelite.static.url");
        return HttpUrl.get((String)(Strings.isNullOrEmpty((String)prop) ? s : prop));
    }

    @Provides
    @Named(value="runelite.ws")
    HttpUrl provideWs(@Named(value="runelite.ws") String s) {
        String prop = System.getProperty("runelite.ws.url");
        return HttpUrl.get((String)(Strings.isNullOrEmpty((String)prop) ? s : prop));
    }

    @Provides
    @Named(value="runelite.pluginhub.url")
    HttpUrl providePluginHubBase(@Named(value="runelite.pluginhub.url") String s) {
        return HttpUrl.get((String)System.getProperty("runelite.pluginhub.url", s));
    }

    @Provides
    @Singleton
    TelemetryClient provideTelemetry(OkHttpClient okHttpClient, Gson gson, @Named(value="runelite.api.base") HttpUrl apiBase) {
        return this.disableTelemetry ? null : new TelemetryClient(okHttpClient, gson, apiBase);
    }

    public RuneLiteModule(OkHttpClient bootupHttpClient, Supplier<Client> clientLoader, RuntimeConfigLoader configLoader, boolean developerMode, boolean safeMode, boolean disableTelemetry, File sessionfile, String profile, boolean insecureWriteCredentials, boolean noupdate) {
        this.bootupHttpClient = bootupHttpClient;
        this.clientLoader = clientLoader;
        this.configLoader = configLoader;
        this.developerMode = developerMode;
        this.safeMode = safeMode;
        this.disableTelemetry = disableTelemetry;
        this.sessionfile = sessionfile;
        this.profile = profile;
        this.insecureWriteCredentials = insecureWriteCredentials;
        this.noupdate = noupdate;
    }
}

