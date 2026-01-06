/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.sentry.Sentry
 *  javax.annotation.Nullable
 *  javax.inject.Inject
 *  javax.inject.Named
 *  javax.inject.Singleton
 *  net.runelite.api.Client
 *  net.runelite.api.MainBufferProvider
 *  net.runelite.api.Player
 *  net.runelite.api.Renderable
 *  net.runelite.api.Skill
 *  net.runelite.api.coords.LocalPoint
 *  net.runelite.api.coords.WorldPoint
 *  net.runelite.api.events.BeforeRender
 *  net.runelite.api.events.FakeXpDrop
 *  net.runelite.api.events.GameTick
 *  net.runelite.api.events.PostClientTick
 *  net.runelite.api.events.ScriptCallbackEvent
 *  net.runelite.api.hooks.Callbacks
 *  net.runelite.api.widgets.Widget
 *  net.runelite.api.widgets.WidgetItem
 *  net.runelite.api.worldmap.WorldMap
 *  net.runelite.api.worldmap.WorldMapRenderer
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package net.runelite.client.callback;

import io.sentry.Sentry;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.VolatileImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import net.runelite.api.Client;
import net.runelite.api.MainBufferProvider;
import net.runelite.api.Player;
import net.runelite.api.Renderable;
import net.runelite.api.Skill;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.BeforeRender;
import net.runelite.api.events.FakeXpDrop;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.PostClientTick;
import net.runelite.api.events.ScriptCallbackEvent;
import net.runelite.api.hooks.Callbacks;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetItem;
import net.runelite.api.worldmap.WorldMap;
import net.runelite.api.worldmap.WorldMapRenderer;
import net.runelite.client.Notifier;
import net.runelite.client.RuneLiteProperties;
import net.runelite.client.RuntimeConfig;
import net.runelite.client.TelemetryClient;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.chat.ChatMessageManager;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.input.KeyManager;
import net.runelite.client.input.MouseManager;
import net.runelite.client.task.Scheduler;
import net.runelite.client.ui.ClientUI;
import net.runelite.client.ui.DrawManager;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayRenderer;
import net.runelite.client.ui.overlay.infobox.InfoBoxManager;
import net.runelite.client.util.DeferredEventBus;
import net.runelite.client.util.LinkBrowser;
import net.runelite.client.util.RSTimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class Hooks
implements Callbacks {
    private static final Logger log = LoggerFactory.getLogger(Hooks.class);
    private static final long CHECK = RSTimeUnit.GAME_TICKS.getDuration().toNanos();
    private static final GameTick GAME_TICK = new GameTick();
    private static final BeforeRender BEFORE_RENDER = new BeforeRender();
    private final Client client;
    private final OverlayRenderer renderer;
    private final EventBus eventBus;
    private final DeferredEventBus deferredEventBus;
    private final Scheduler scheduler;
    private final InfoBoxManager infoBoxManager;
    private final ChatMessageManager chatMessageManager;
    private final MouseManager mouseManager;
    private final KeyManager keyManager;
    private final ClientThread clientThread;
    private final DrawManager drawManager;
    private final Notifier notifier;
    private final ClientUI clientUi;
    @Nullable
    private final TelemetryClient telemetryClient;
    @Nullable
    private final RuntimeConfig runtimeConfig;
    private final boolean developerMode;
    private Dimension lastStretchedDimensions;
    private VolatileImage stretchedImage;
    private Graphics2D stretchedGraphics;
    private long lastCheck;
    private boolean shouldProcessGameTick;
    private static MainBufferProvider lastMainBufferProvider;
    private static Graphics2D lastGraphics;
    private long nextError;
    private boolean rateLimitedError;
    private int errorBackoff = 1;
    private final List<RenderableDrawListener> renderableDrawListeners = new ArrayList<RenderableDrawListener>();

    private static Graphics2D getGraphics(MainBufferProvider mainBufferProvider) {
        if (lastGraphics == null || lastMainBufferProvider != mainBufferProvider) {
            if (lastGraphics != null) {
                log.debug("Graphics reset!");
                lastGraphics.dispose();
            }
            lastMainBufferProvider = mainBufferProvider;
            lastGraphics = (Graphics2D)mainBufferProvider.getImage().getGraphics();
        }
        return lastGraphics;
    }

    @Inject
    private Hooks(Client client, OverlayRenderer renderer, EventBus eventBus, DeferredEventBus deferredEventBus, Scheduler scheduler, InfoBoxManager infoBoxManager, ChatMessageManager chatMessageManager, MouseManager mouseManager, KeyManager keyManager, ClientThread clientThread, DrawManager drawManager, Notifier notifier, ClientUI clientUi, @Nullable TelemetryClient telemetryClient, @Nullable RuntimeConfig runtimeConfig, @Named(value="developerMode") boolean developerMode) {
        this.client = client;
        this.renderer = renderer;
        this.eventBus = eventBus;
        this.deferredEventBus = deferredEventBus;
        this.scheduler = scheduler;
        this.infoBoxManager = infoBoxManager;
        this.chatMessageManager = chatMessageManager;
        this.mouseManager = mouseManager;
        this.keyManager = keyManager;
        this.clientThread = clientThread;
        this.drawManager = drawManager;
        this.notifier = notifier;
        this.clientUi = clientUi;
        this.telemetryClient = telemetryClient;
        this.runtimeConfig = runtimeConfig;
        this.developerMode = developerMode;
        eventBus.register(this);
    }

    public void post(Object event) {
        this.eventBus.post(event);
    }

    public void postDeferred(Object event) {
        this.deferredEventBus.post(event);
    }

    public void tick() {
        if (this.shouldProcessGameTick) {
            this.shouldProcessGameTick = false;
            this.deferredEventBus.replay();
            this.eventBus.post(GAME_TICK);
            int tick = this.client.getTickCount();
            this.client.setTickCount(tick + 1);
        }
        this.clientThread.invoke();
        long now = System.nanoTime();
        if (now - this.lastCheck < CHECK) {
            return;
        }
        this.lastCheck = now;
        try {
            this.scheduler.tick();
            this.infoBoxManager.cull();
            this.chatMessageManager.process();
            this.checkWorldMap();
        }
        catch (Exception ex) {
            log.error("error during main loop tasks", (Throwable)ex);
        }
    }

    public void tickEnd() {
        this.clientThread.invokeTickEnd();
        this.eventBus.post(new PostClientTick());
    }

    public void frame() {
        this.eventBus.post(BEFORE_RENDER);
    }

    private void checkWorldMap() {
        Widget widget = this.client.getWidget(38993927);
        if (widget != null) {
            return;
        }
        WorldMap worldMap = this.client.getWorldMap();
        if (worldMap == null) {
            return;
        }
        WorldMapRenderer manager = worldMap.getWorldMapRenderer();
        if (manager != null && manager.isLoaded()) {
            log.debug("World map was closed, reinitializing");
            worldMap.initializeWorldMap(worldMap.getWorldMapData());
        }
    }

    public MouseEvent mousePressed(MouseEvent mouseEvent) {
        return this.mouseManager.processMousePressed(mouseEvent);
    }

    public MouseEvent mouseReleased(MouseEvent mouseEvent) {
        return this.mouseManager.processMouseReleased(mouseEvent);
    }

    public MouseEvent mouseClicked(MouseEvent mouseEvent) {
        return this.mouseManager.processMouseClicked(mouseEvent);
    }

    public MouseEvent mouseEntered(MouseEvent mouseEvent) {
        return this.mouseManager.processMouseEntered(mouseEvent);
    }

    public MouseEvent mouseExited(MouseEvent mouseEvent) {
        return this.mouseManager.processMouseExited(mouseEvent);
    }

    public MouseEvent mouseDragged(MouseEvent mouseEvent) {
        return this.mouseManager.processMouseDragged(mouseEvent);
    }

    public MouseEvent mouseMoved(MouseEvent mouseEvent) {
        return this.mouseManager.processMouseMoved(mouseEvent);
    }

    public MouseWheelEvent mouseWheelMoved(MouseWheelEvent event) {
        return this.mouseManager.processMouseWheelMoved(event);
    }

    public void keyPressed(KeyEvent keyEvent) {
        this.keyManager.processKeyPressed(keyEvent);
    }

    public void keyReleased(KeyEvent keyEvent) {
        this.keyManager.processKeyReleased(keyEvent);
    }

    public void keyTyped(KeyEvent keyEvent) {
        this.keyManager.processKeyTyped(keyEvent);
    }

    public void draw(MainBufferProvider mainBufferProvider, Graphics graphics, int x, int y) {
        Image finalImage;
        if (graphics == null) {
            return;
        }
        Graphics2D graphics2d = Hooks.getGraphics(mainBufferProvider);
        try {
            this.renderer.renderOverlayLayer(graphics2d, OverlayLayer.ALWAYS_ON_TOP);
        }
        catch (Exception ex) {
            log.error("Error during overlay rendering", (Throwable)ex);
        }
        this.notifier.processFlash(graphics2d);
        this.clientUi.paintOverlays(graphics2d);
        if (this.client.isGpu()) {
            return;
        }
        Image image = mainBufferProvider.getImage();
        if (this.client.isStretchedEnabled()) {
            GraphicsConfiguration gc = this.clientUi.getGraphicsConfiguration();
            Dimension stretchedDimensions = this.client.getStretchedDimensions();
            int status = -1;
            if (!stretchedDimensions.equals(this.lastStretchedDimensions) || this.stretchedImage == null || (status = this.stretchedImage.validate(gc)) != 0) {
                log.debug("Volatile image non-OK status: {}", (Object)status);
                if (this.stretchedGraphics != null) {
                    this.stretchedGraphics.dispose();
                }
                if (!stretchedDimensions.equals(this.lastStretchedDimensions) || this.stretchedImage == null || status == 2) {
                    if (this.stretchedImage != null) {
                        this.stretchedImage.flush();
                    }
                    this.stretchedImage = gc.createCompatibleVolatileImage(stretchedDimensions.width, stretchedDimensions.height);
                    this.lastStretchedDimensions = stretchedDimensions;
                }
                this.stretchedGraphics = (Graphics2D)this.stretchedImage.getGraphics();
            }
            this.stretchedGraphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, this.client.isStretchedFast() ? RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR : RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            this.stretchedGraphics.drawImage(image, 0, 0, stretchedDimensions.width, stretchedDimensions.height, null);
            finalImage = this.stretchedImage;
        } else {
            if (this.stretchedImage != null) {
                log.debug("Releasing stretched volatile image");
                this.stretchedGraphics.dispose();
                this.stretchedImage.flush();
                this.stretchedGraphics = null;
                this.stretchedImage = null;
                this.lastStretchedDimensions = null;
            }
            finalImage = image;
        }
        graphics.drawImage(finalImage, 0, 0, this.client.getCanvas());
        this.drawManager.processDrawComplete(() -> this.screenshot(finalImage));
    }

    private Image screenshot(Image src) {
        AffineTransform transform = this.clientUi.getGraphicsConfiguration().getDefaultTransform();
        int swidth = src.getWidth(null);
        int sheight = src.getHeight(null);
        int twidth = (int)((double)swidth * transform.getScaleX() + 0.5);
        int theight = (int)((double)sheight * transform.getScaleY() + 0.5);
        BufferedImage image = new BufferedImage(twidth, theight, 1);
        Graphics2D graphics = (Graphics2D)image.getGraphics();
        graphics.setTransform(transform);
        graphics.drawImage(src, 0, 0, swidth, sheight, null);
        graphics.dispose();
        return image;
    }

    public void drawScene() {
        MainBufferProvider bufferProvider = (MainBufferProvider)this.client.getBufferProvider();
        Graphics2D graphics2d = Hooks.getGraphics(bufferProvider);
        try {
            this.renderer.renderOverlayLayer(graphics2d, OverlayLayer.ABOVE_SCENE);
        }
        catch (Exception ex) {
            log.error("Error during overlay rendering", (Throwable)ex);
        }
    }

    public void drawAboveOverheads() {
        MainBufferProvider bufferProvider = (MainBufferProvider)this.client.getBufferProvider();
        Graphics2D graphics2d = Hooks.getGraphics(bufferProvider);
        try {
            this.renderer.renderOverlayLayer(graphics2d, OverlayLayer.UNDER_WIDGETS);
        }
        catch (Exception ex) {
            log.error("Error during overlay rendering", (Throwable)ex);
        }
    }

    public void serverTick() {
        this.shouldProcessGameTick = true;
    }

    public void drawInterface(int interfaceId, List<WidgetItem> widgetItems) {
        MainBufferProvider bufferProvider = (MainBufferProvider)this.client.getBufferProvider();
        Graphics2D graphics2d = Hooks.getGraphics(bufferProvider);
        try {
            this.renderer.renderAfterInterface(graphics2d, interfaceId, widgetItems);
        }
        catch (Exception ex) {
            log.error("Error during overlay rendering", (Throwable)ex);
        }
    }

    public void drawLayer(Widget layer, List<WidgetItem> widgetItems) {
        MainBufferProvider bufferProvider = (MainBufferProvider)this.client.getBufferProvider();
        Graphics2D graphics2d = Hooks.getGraphics(bufferProvider);
        try {
            this.renderer.renderAfterLayer(graphics2d, layer, widgetItems);
        }
        catch (Exception ex) {
            log.error("Error during overlay rendering", (Throwable)ex);
        }
    }

    @Subscribe
    public void onScriptCallbackEvent(ScriptCallbackEvent scriptCallbackEvent) {
        if (!scriptCallbackEvent.getEventName().equals("fakeXpDrop")) {
            return;
        }
        int[] intStack = this.client.getIntStack();
        int intStackSize = this.client.getIntStackSize();
        int statId = intStack[intStackSize - 2];
        int xp = intStack[intStackSize - 1];
        Skill skill = Skill.values()[statId];
        FakeXpDrop fakeXpDrop = new FakeXpDrop(skill, xp);
        this.eventBus.post(fakeXpDrop);
    }

    public void registerRenderableDrawListener(RenderableDrawListener listener) {
        this.renderableDrawListeners.add(listener);
    }

    public void unregisterRenderableDrawListener(RenderableDrawListener listener) {
        this.renderableDrawListeners.remove(listener);
    }

    public boolean draw(Renderable renderable, boolean drawingUi) {
        try {
            for (RenderableDrawListener renderableDrawListener : this.renderableDrawListeners) {
                if (renderableDrawListener.draw(renderable, drawingUi)) continue;
                return false;
            }
        }
        catch (Exception ex) {
            log.error("exception from renderable draw listener", (Throwable)ex);
        }
        return true;
    }

    public void error(String message, Throwable reason) {
        long now = System.currentTimeMillis();
        if (now > this.nextError) {
            Sentry.withScope(scope -> {
                Player player;
                if (this.client.getClientThread() == Thread.currentThread() && (player = this.client.getLocalPlayer()) != null) {
                    WorldPoint p = WorldPoint.fromLocalInstance((Client)this.client, (LocalPoint)player.getLocalLocation());
                    scope.setExtra("coord", String.format("%d_%d_%d_%d_%d", p.getPlane(), p.getX() / 64, p.getY() / 64, p.getX() & 0x3F, p.getY() & 0x3F));
                }
                if (reason != null) {
                    if (message != null) {
                        scope.setExtra("message", message);
                    }
                    Sentry.captureException((Throwable)reason);
                } else if (message != null) {
                    Sentry.captureMessage((String)message);
                }
            });
            if (this.rateLimitedError) {
                ++this.errorBackoff;
                this.rateLimitedError = false;
            } else {
                this.errorBackoff = 1;
            }
            this.nextError = now + 10000L * (long)this.errorBackoff;
        } else {
            this.rateLimitedError = true;
        }
    }

    public void openUrl(String url) {
        if (url.contains("account-creation")) {
            url = "https://ferox.ps/register";
        } else if (url.equals("https://support.runescape.com/hc/en-gb/articles/360001552065")) {
            url = "https://discord.com/channels/1331330540992856194/1331330541454360691";
        } else if (url.contains("runescape.com") || url.contains("jagex.com")) {
            log.info("Skipping openUrl for {}", (Object)url);
            return;
        }
        LinkBrowser.browse(url);
    }

    public boolean isRuneLiteClientOutdated() {
        if (this.runtimeConfig == null || this.developerMode) {
            return false;
        }
        Set<String> outdatedClientVersions = this.runtimeConfig.getOutdatedClientVersions();
        if (outdatedClientVersions == null) {
            return false;
        }
        return outdatedClientVersions.contains(RuneLiteProperties.getVersion());
    }

    @FunctionalInterface
    public static interface RenderableDrawListener {
        public boolean draw(Renderable var1, boolean var2);
    }
}

