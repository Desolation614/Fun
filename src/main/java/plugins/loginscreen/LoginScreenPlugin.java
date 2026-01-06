/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.CharMatcher
 *  com.google.common.base.Strings
 *  com.google.common.hash.Hashing
 *  com.google.common.io.FileWriteMode
 *  com.google.common.io.Files
 *  com.google.inject.Provides
 *  javax.inject.Inject
 *  javax.inject.Named
 *  net.runelite.api.Client
 *  net.runelite.api.GameState
 *  net.runelite.api.SpritePixels
 *  net.runelite.api.events.GameStateChanged
 *  okhttp3.Call
 *  okhttp3.Callback
 *  okhttp3.HttpUrl
 *  okhttp3.OkHttpClient
 *  okhttp3.Request
 *  okhttp3.Request$Builder
 *  okhttp3.Response
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package net.runelite.client.plugins.loginscreen;

import com.google.common.base.CharMatcher;
import com.google.common.base.Strings;
import com.google.common.hash.Hashing;
import com.google.common.io.FileWriteMode;
import com.google.common.io.Files;
import com.google.inject.Provides;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Random;
import java.util.function.Consumer;
import javax.imageio.ImageIO;
import javax.inject.Inject;
import javax.inject.Named;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.SpritePixels;
import net.runelite.api.events.GameStateChanged;
import net.runelite.client.RuneLite;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.events.ProfileChanged;
import net.runelite.client.input.KeyListener;
import net.runelite.client.input.KeyManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.loginscreen.LoginScreenConfig;
import net.runelite.client.plugins.loginscreen.LoginScreenOverride;
import net.runelite.client.util.ImageUtil;
import net.runelite.client.util.OSType;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@PluginDescriptor(name="Login Screen", description="Provides various enhancements for login screen")
public class LoginScreenPlugin
extends Plugin
implements KeyListener {
    private static final Logger log = LoggerFactory.getLogger(LoginScreenPlugin.class);
    private static final int MAX_USERNAME_LENGTH = 254;
    private static final int MAX_PIN_LENGTH = 6;
    private static final File CUSTOM_LOGIN_SCREEN_FILE = new File(RuneLite.RUNELITE_DIR, "login.png");
    private static final File LOGINSCREENS = new File(RuneLite.CACHE_DIR, "loginscreens");
    @Inject
    private Client client;
    @Inject
    private ClientThread clientThread;
    @Inject
    private LoginScreenConfig config;
    @Inject
    private KeyManager keyManager;
    @Inject
    private OkHttpClient okHttpClient;
    @Inject
    @Named(value="runelite.static.base")
    private HttpUrl staticBase;
    private String usernameCache;

    @Override
    protected void startUp() throws Exception {
        this.applyUsername();
        this.keyManager.registerKeyListener(this);
        this.clientThread.invoke(this::overrideLoginScreen);
    }

    @Override
    protected void shutDown() throws Exception {
        if (this.config.syncUsername()) {
            this.client.getPreferences().setRememberedUsername(this.usernameCache);
        }
        this.keyManager.unregisterKeyListener(this);
        this.clientThread.invoke(() -> {
            this.restoreLoginScreen();
            this.client.setShouldRenderLoginScreenFire(true);
        });
    }

    @Provides
    LoginScreenConfig getConfig(ConfigManager configManager) {
        return configManager.getConfig(LoginScreenConfig.class);
    }

    @Subscribe
    public void onConfigChanged(ConfigChanged event) {
        if (event.getGroup().equals("loginscreen")) {
            this.clientThread.invoke(this::overrideLoginScreen);
        }
    }

    @Subscribe
    public void onGameStateChanged(GameStateChanged event) {
        if (!this.config.syncUsername()) {
            return;
        }
        if (event.getGameState() == GameState.LOGIN_SCREEN) {
            this.applyUsername();
        } else if (event.getGameState() == GameState.LOGGED_IN) {
            String username = "";
            if (this.client.getPreferences().getRememberedUsername() != null) {
                username = this.client.getUsername();
            }
            if (this.config.username().equals(username)) {
                return;
            }
            log.debug("Saving username: {}", (Object)username);
            this.config.username(username);
        }
    }

    @Subscribe
    public void onProfileChanged(ProfileChanged profileChanged) {
        this.applyUsername();
    }

    private void applyUsername() {
        if (!this.config.syncUsername()) {
            return;
        }
        GameState gameState = this.client.getGameState();
        if (gameState == GameState.LOGIN_SCREEN) {
            String username = this.config.username();
            if (Strings.isNullOrEmpty((String)username)) {
                return;
            }
            if (this.usernameCache == null) {
                this.usernameCache = this.client.getPreferences().getRememberedUsername();
            }
            this.client.getPreferences().setRememberedUsername(username);
        }
    }

    @Override
    public boolean isEnabledOnLoginScreen() {
        return true;
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        boolean isModifierDown;
        if (!this.config.pasteEnabled() || this.client.getGameState() != GameState.LOGIN_SCREEN && this.client.getGameState() != GameState.LOGIN_SCREEN_AUTHENTICATOR) {
            return;
        }
        boolean bl = isModifierDown = OSType.getOSType() == OSType.MacOS ? e.isMetaDown() : e.isControlDown();
        if (e.getKeyCode() == 86 && isModifierDown) {
            try {
                String data = Toolkit.getDefaultToolkit().getSystemClipboard().getData(DataFlavor.stringFlavor).toString().trim();
                switch (this.client.getLoginIndex()) {
                    case 2: {
                        if (this.client.getCurrentLoginField() != 0) break;
                        this.client.setUsername(data.substring(0, Math.min(data.length(), 254)));
                        break;
                    }
                    case 4: {
                        data = CharMatcher.inRange((char)'0', (char)'9').retainFrom((CharSequence)data);
                        this.client.setOtp(data.substring(0, Math.min(data.length(), 6)));
                    }
                }
            }
            catch (UnsupportedFlavorException | IOException ex) {
                log.warn("failed to fetch clipboard data", (Throwable)ex);
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void overrideLoginScreen() {
        this.client.setShouldRenderLoginScreenFire(this.config.showLoginFire());
        LoginScreenOverride loginScreen = this.config.loginScreen();
        if (loginScreen == LoginScreenOverride.OFF) {
            this.restoreLoginScreen();
            return;
        }
        if (loginScreen == LoginScreenOverride.CUSTOM) {
            if (CUSTOM_LOGIN_SCREEN_FILE.exists()) {
                try {
                    Class<ImageIO> clazz = ImageIO.class;
                    synchronized (ImageIO.class) {
                        BufferedImage image2 = ImageIO.read(CUSTOM_LOGIN_SCREEN_FILE);
                        // ** MonitorExit[var3_2] (shouldn't be in output)
                        if (image2.getHeight() > 503) {
                            double scalar = 503.0 / (double)image2.getHeight();
                            image2 = ImageUtil.resizeImage(image2, (int)((double)image2.getWidth() * scalar), 503);
                        }
                        SpritePixels pixels = ImageUtil.getImageSpritePixels(image2, this.client);
                        this.client.setLoginScreen(pixels);
                    }
                }
                catch (IOException e) {
                    log.error("error loading custom login screen", (Throwable)e);
                    this.restoreLoginScreen();
                }
            }
        } else {
            if (loginScreen == LoginScreenOverride.RANDOM) {
                LoginScreenOverride[] filtered = (LoginScreenOverride[])Arrays.stream(LoginScreenOverride.values()).filter(screen -> screen.getFileName() != null).toArray(LoginScreenOverride[]::new);
                loginScreen = filtered[new Random().nextInt(filtered.length)];
            }
            this.fetchLoginScreenImage(loginScreen, image -> this.clientThread.invoke(() -> {
                SpritePixels pixels = ImageUtil.getImageSpritePixels(image, this.client);
                this.client.setLoginScreen(pixels);
            }));
        }
    }

    private void restoreLoginScreen() {
        this.client.setLoginScreen(null);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void fetchLoginScreenImage(final LoginScreenOverride ls, final Consumer<BufferedImage> imageConsumer) {
        File imagePath;
        block11: {
            imagePath = new File(LOGINSCREENS, ls.getFileName());
            try {
                BufferedImage image;
                String hash;
                if (!imagePath.exists() || !(hash = Files.asByteSource((File)imagePath).hash(Hashing.sha256()).toString()).equals(ls.getHash())) break block11;
                try (InputStream in = Files.asByteSource((File)imagePath).openStream();){
                    Class<ImageIO> clazz = ImageIO.class;
                    synchronized (ImageIO.class) {
                        image = ImageIO.read(in);
                        // ** MonitorExit[var7_7] (shouldn't be in output)
                    }
                }
                {
                    log.debug("Using cached login screen {}", (Object)ls.getFileName());
                    imageConsumer.accept(image);
                    return;
                }
            }
            catch (IOException ex) {
                log.debug(null, (Throwable)ex);
            }
        }
        log.info("Downloading login screen {}", (Object)ls.getFileName());
        HttpUrl url = this.staticBase.newBuilder().addPathSegments("loginscreens/" + ls.getFileName()).build();
        Request request = new Request.Builder().url(url).build();
        this.okHttpClient.newCall(request).enqueue(new Callback(){

            /*
             * WARNING - Removed try catching itself - possible behaviour change.
             */
            public void onResponse(Call call, Response response) throws IOException {
                BufferedImage image;
                InputStream in;
                LOGINSCREENS.mkdirs();
                try (Response response2 = response;){
                    in = response.body().byteStream();
                    try {
                        Files.asByteSink((File)imagePath, (FileWriteMode[])new FileWriteMode[0]).writeFrom(in);
                    }
                    finally {
                        if (in != null) {
                            in.close();
                        }
                    }
                }
                in = Files.asByteSource((File)imagePath).openStream();
                try {
                    Class<ImageIO> clazz = ImageIO.class;
                    synchronized (ImageIO.class) {
                        image = ImageIO.read(in);
                        // ** MonitorExit[var5_8] (shouldn't be in output)
                    }
                }
                finally {
                    if (in != null) {
                        in.close();
                    }
                }
                {
                    imageConsumer.accept(image);
                    return;
                }
            }

            public void onFailure(Call call, IOException e) {
                log.error("unable to download login screen {}", (Object)ls, (Object)e);
            }
        });
    }
}

