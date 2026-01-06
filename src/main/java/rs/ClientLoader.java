/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Strings
 *  javax.annotation.Nonnull
 *  net.runelite.api.Client
 *  net.runelite.http.api.worlds.World
 *  okhttp3.HttpUrl
 *  okhttp3.OkHttpClient
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  osrs.client
 */
package net.runelite.client.rs;

import com.google.common.base.Strings;
import java.applet.Applet;
import java.io.IOException;
import java.util.Map;
import java.util.function.Supplier;
import javax.annotation.Nonnull;
import javax.swing.SwingUtilities;
import net.runelite.api.Client;
import net.runelite.client.RuneLiteProperties;
import net.runelite.client.RuntimeConfig;
import net.runelite.client.RuntimeConfigLoader;
import net.runelite.client.rs.ClientConfigLoader;
import net.runelite.client.rs.ClientConfigLocalLoader;
import net.runelite.client.rs.RSAppletStub;
import net.runelite.client.rs.RSConfig;
import net.runelite.client.rs.WorldSupplier;
import net.runelite.client.ui.FatalErrorDialog;
import net.runelite.client.ui.SplashScreen;
import net.runelite.http.api.worlds.World;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import osrs.client;

public class ClientLoader
implements Supplier<Client> {
    private static final Logger log = LoggerFactory.getLogger(ClientLoader.class);
    private static final int NUM_ATTEMPTS = 6;
    private final ClientConfigLoader clientConfigLoader;
    private final WorldSupplier worldSupplier;
    private final RuntimeConfigLoader runtimeConfigLoader;
    private final String javConfigUrl;
    private final boolean localJavConfig;
    private Object client;

    public ClientLoader(OkHttpClient okHttpClient, RuntimeConfigLoader runtimeConfigLoader, String javConfigUrl, boolean localJavConfig) {
        this.clientConfigLoader = new ClientConfigLoader(okHttpClient);
        this.worldSupplier = new WorldSupplier(okHttpClient);
        this.runtimeConfigLoader = runtimeConfigLoader;
        this.javConfigUrl = javConfigUrl;
        this.localJavConfig = localJavConfig;
    }

    @Override
    public synchronized Client get() {
        if (this.client == null) {
            this.client = this.doLoad();
        }
        if (this.client instanceof Throwable) {
            throw new RuntimeException((Throwable)this.client);
        }
        return (Client)this.client;
    }

    private Object doLoad() {
        try {
            SplashScreen.stage(0.0, null, "Fetching applet viewer config");
            RSConfig config = this.localJavConfig ? this.loadLocalConfig() : this.downloadConfig();
            SplashScreen.stage(0.3, "Starting", "Starting Ferox");
            Client rs = this.loadClient(config);
            SplashScreen.stage(0.4, null, "Starting core classes");
            return rs;
        }
        catch (OutageException e) {
            return e;
        }
        catch (IOException | ClassNotFoundException | IllegalAccessException | InstantiationException | SecurityException e) {
            log.error("Error loading RS!", (Throwable)e);
            if (!this.checkOutages()) {
                SwingUtilities.invokeLater(() -> FatalErrorDialog.showNetErrorWindow("loading the client", e));
            }
            return e;
        }
    }

    private RSConfig loadLocalConfig() {
        ClientConfigLocalLoader loader = new ClientConfigLocalLoader();
        return loader.fetch();
    }

    private RSConfig downloadConfig() throws IOException {
        HttpUrl url = HttpUrl.get((String)this.javConfigUrl);
        IOException err = null;
        for (int attempt = 0; attempt < 6; ++attempt) {
            try {
                RSConfig config = this.clientConfigLoader.fetch(url);
                if (Strings.isNullOrEmpty((String)config.getCodeBase()) || Strings.isNullOrEmpty((String)config.getInitialJar()) || Strings.isNullOrEmpty((String)config.getInitialClass())) {
                    throw new IOException("Invalid or missing jav_config");
                }
                return config;
            }
            catch (IOException e) {
                log.info("Failed to get jav_config from host \"{}\" ({})", (Object)url.host(), (Object)e.getMessage());
                if (this.checkOutages()) {
                    throw new OutageException(e);
                }
                if (!this.javConfigUrl.equals(RuneLiteProperties.getJavConfig())) {
                    throw e;
                }
                err = e;
                continue;
            }
        }
        log.info("Falling back to backup client config");
        try {
            return this.downloadFallbackConfig();
        }
        catch (IOException ex) {
            log.debug("error downloading backup config", (Throwable)ex);
            throw err;
        }
    }

    @Nonnull
    private RSConfig downloadFallbackConfig() throws IOException {
        RSConfig backupConfig = this.clientConfigLoader.fetch(HttpUrl.get((String)RuneLiteProperties.getJavConfigBackup()));
        if (Strings.isNullOrEmpty((String)backupConfig.getCodeBase()) || Strings.isNullOrEmpty((String)backupConfig.getInitialJar()) || Strings.isNullOrEmpty((String)backupConfig.getInitialClass())) {
            throw new IOException("Invalid or missing jav_config");
        }
        if (Strings.isNullOrEmpty((String)backupConfig.getRuneLiteWorldParam())) {
            throw new IOException("Backup config does not have RuneLite gamepack url");
        }
        World world = this.worldSupplier.get();
        backupConfig.setCodebase("http://" + world.getAddress() + "/");
        Map<String, String> appletProperties = backupConfig.getAppletProperties();
        appletProperties.put(backupConfig.getRuneLiteWorldParam(), Integer.toString(world.getId()));
        return backupConfig;
    }

    private Client loadClient(RSConfig config) throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        String initialClass = config.getInitialClass();
        Class<client> clientClass = client.class;
        Client rs = (Client)clientClass.newInstance();
        ((Applet)rs).setStub(new RSAppletStub(config, this.runtimeConfigLoader));
        log.info("injected-client {}", (Object)rs.getBuildID());
        return rs;
    }

    private boolean checkOutages() {
        RuntimeConfig rtc = this.runtimeConfigLoader.tryGet();
        if (rtc != null) {
            return rtc.showOutageMessage();
        }
        return false;
    }

    private static class OutageException
    extends RuntimeException {
        private OutageException(Throwable cause) {
            super(cause);
        }
    }
}

