/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.inject.Provides
 *  javax.inject.Inject
 *  net.runelite.api.Client
 *  net.runelite.api.GameState
 *  net.runelite.api.HealthBarConfig
 *  net.runelite.api.Menu
 *  net.runelite.api.MenuAction
 *  net.runelite.api.MenuEntry
 *  net.runelite.api.Player
 *  net.runelite.api.SpritePixels
 *  net.runelite.api.events.BeforeMenuRender
 *  net.runelite.api.events.GameStateChanged
 *  net.runelite.api.events.MenuOpened
 *  net.runelite.api.events.PostClientTick
 *  net.runelite.api.events.PostHealthBarConfig
 *  net.runelite.api.events.ScriptCallbackEvent
 *  net.runelite.api.widgets.Widget
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package net.runelite.client.plugins.interfacestyles;

import com.google.inject.Provides;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.HealthBarConfig;
import net.runelite.api.Menu;
import net.runelite.api.MenuAction;
import net.runelite.api.MenuEntry;
import net.runelite.api.Player;
import net.runelite.api.SpritePixels;
import net.runelite.api.events.BeforeMenuRender;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.MenuOpened;
import net.runelite.api.events.PostClientTick;
import net.runelite.api.events.PostHealthBarConfig;
import net.runelite.api.events.ScriptCallbackEvent;
import net.runelite.api.widgets.Widget;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.game.SpriteManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.interfacestyles.HealthbarOverride;
import net.runelite.client.plugins.interfacestyles.InterfaceStylesConfig;
import net.runelite.client.plugins.interfacestyles.Skin;
import net.runelite.client.plugins.interfacestyles.SpriteOverride;
import net.runelite.client.plugins.interfacestyles.WidgetOffset;
import net.runelite.client.plugins.interfacestyles.WidgetOverride;
import net.runelite.client.util.ImageUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@PluginDescriptor(name="Interface Styles", description="Change the interface style to the 2005/2010 interface", tags={"2005", "2010", "skin", "theme", "ui", "hp", "bar"}, enabledByDefault=false)
public class InterfaceStylesPlugin
extends Plugin {
    private static final Logger log = LoggerFactory.getLogger(InterfaceStylesPlugin.class);
    @Inject
    private Client client;
    @Inject
    private ClientThread clientThread;
    @Inject
    private InterfaceStylesConfig config;
    @Inject
    private SpriteManager spriteManager;
    private SpritePixels[] defaultCrossSprites;

    @Provides
    InterfaceStylesConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(InterfaceStylesConfig.class);
    }

    @Override
    protected void startUp() throws Exception {
        this.queueUpdateAllOverrides();
    }

    @Override
    protected void shutDown() throws Exception {
        this.clientThread.invoke(() -> {
            this.restoreWidgetDimensions();
            this.removeGameframe();
            this.restoreHealthBars();
            this.restoreCrossSprites();
        });
    }

    @Subscribe
    public void onGameStateChanged(GameStateChanged gameStateChanged) {
        if (gameStateChanged.getGameState() == GameState.STARTING) {
            this.queueUpdateAllOverrides();
        }
    }

    private void queueUpdateAllOverrides() {
        this.clientThread.invoke(() -> {
            if (this.client.getGameState().getState() < GameState.LOGIN_SCREEN.getState()) {
                return false;
            }
            this.updateAllOverrides();
            return true;
        });
    }

    @Subscribe
    public void onConfigChanged(ConfigChanged config) {
        if (config.getGroup().equals("interfaceStyles")) {
            this.clientThread.invoke(this::updateAllOverrides);
        }
    }

    @Subscribe(priority=1.0f)
    public void onMenuOpened(MenuOpened event) {
        if (this.config.condensePlayerOptions()) {
            this.condensePlayerOptions();
        }
    }

    private void condensePlayerOptions() {
        MenuEntry[] menuEntries = this.client.getMenuEntries();
        MenuEntry[] newMenus = new MenuEntry[menuEntries.length];
        int newIdx = 0;
        Menu submenu = null;
        Player prev = null;
        boolean changed = false;
        for (MenuEntry menuEntry : menuEntries) {
            MenuAction type = menuEntry.getType();
            Player player = menuEntry.getPlayer();
            if (player != null && type != MenuAction.ITEM_USE_ON_PLAYER && type != MenuAction.WIDGET_TARGET_ON_PLAYER) {
                String option = menuEntry.getOption();
                boolean deprioritized = menuEntry.isDeprioritized();
                if (prev != player) {
                    menuEntry.setOption("");
                    menuEntry.setType(MenuAction.RUNELITE);
                    menuEntry.setDeprioritized(false);
                    submenu = menuEntry.createSubMenu();
                    newMenus[newIdx++] = menuEntry;
                }
                assert (submenu != null);
                submenu.createMenuEntry(-1).setIdentifier(menuEntry.getIdentifier()).setOption(option).setTarget(menuEntry.getTarget()).setType(type).setParam0(menuEntry.getParam0()).setParam1(menuEntry.getParam1()).setDeprioritized(deprioritized);
                changed = true;
            } else {
                newMenus[newIdx++] = menuEntry;
            }
            prev = player;
        }
        if (changed) {
            this.client.setMenuEntries(Arrays.copyOf(newMenus, newIdx));
        }
    }

    @Subscribe
    public void onScriptCallbackEvent(ScriptCallbackEvent event) {
        if ("forceStackStones".equals(event.getEventName()) && this.config.alwaysStack()) {
            int[] intStack = this.client.getIntStack();
            int intStackSize = this.client.getIntStackSize();
            intStack[intStackSize - 1] = 1;
        }
    }

    @Subscribe
    public void onPostClientTick(PostClientTick event) {
        this.adjustWidgetDimensions();
    }

    @Subscribe
    public void onPostHealthBarConfig(PostHealthBarConfig postHealthBar) {
        if (!this.config.hdHealthBars()) {
            return;
        }
        HealthBarConfig healthBar = postHealthBar.getHealthBarConfig();
        HealthbarOverride override = HealthbarOverride.get(healthBar.getHealthBarFrontSpriteId());
        if (override != null) {
            healthBar.setPadding(override.getPadding());
        }
    }

    private void updateAllOverrides() {
        this.removeGameframe();
        this.overrideSprites();
        this.overrideWidgetSprites();
        this.restoreWidgetDimensions();
        this.adjustWidgetDimensions();
        this.overrideHealthBars();
        this.overrideCrossSprites();
    }

    @Subscribe
    public void onBeforeMenuRender(BeforeMenuRender event) {
        if (this.config.hdMenu()) {
            this.client.draw2010Menu(this.config.menuAlpha());
            event.consume();
        } else if (this.config.menuAlpha() != 255) {
            this.client.drawOriginalMenu(this.config.menuAlpha());
            event.consume();
        }
    }

    private void overrideSprites() {
        Skin configuredSkin = this.config.skin();
        if (configuredSkin == Skin.DEFAULT) {
            return;
        }
        for (SpriteOverride spriteOverride : SpriteOverride.values()) {
            for (Skin skin : spriteOverride.getSkin()) {
                if (skin != configuredSkin) continue;
                String configSkin = skin.getExtendSkin() != null ? skin.getExtendSkin().toString() : skin.toString();
                String file = configSkin + "/" + spriteOverride.getSpriteID() + ".png";
                SpritePixels spritePixels = this.getFileSpritePixels(file);
                if (spriteOverride.getSpriteID() == 169) {
                    this.client.setCompass(spritePixels);
                    continue;
                }
                this.client.getSpriteOverrides().put(spriteOverride.getSpriteID(), spritePixels);
            }
        }
    }

    private void restoreSprites() {
        this.client.getWidgetSpriteCache().reset();
        for (SpriteOverride spriteOverride : SpriteOverride.values()) {
            this.client.getSpriteOverrides().remove(spriteOverride.getSpriteID());
        }
    }

    private void overrideWidgetSprites() {
        Skin configuredSkin = this.config.skin();
        if (configuredSkin == Skin.DEFAULT) {
            return;
        }
        for (WidgetOverride widgetOverride : WidgetOverride.values()) {
            String configSkin;
            String file;
            SpritePixels spritePixels;
            if (widgetOverride.getSkin() != configuredSkin && widgetOverride.getSkin() != configuredSkin.getExtendSkin() || (spritePixels = this.getFileSpritePixels(file = (configSkin = configuredSkin.getExtendSkin() != null ? configuredSkin.getExtendSkin().toString() : configuredSkin.toString()) + "/widget/" + widgetOverride.getName() + ".png")) == null) continue;
            for (int widgetInfo : widgetOverride.getWidgetInfo()) {
                this.client.getWidgetSpriteOverrides().put(widgetInfo, spritePixels);
            }
        }
    }

    private void restoreWidgetSprites() {
        for (WidgetOverride widgetOverride : WidgetOverride.values()) {
            for (int widgetInfo : widgetOverride.getWidgetInfo()) {
                this.client.getWidgetSpriteOverrides().remove(widgetInfo);
            }
        }
    }

    private SpritePixels getFileSpritePixels(String file) {
        try {
            log.debug("Loading: {}", (Object)file);
            BufferedImage image = ImageUtil.loadImageResource(this.getClass(), file);
            return ImageUtil.getImageSpritePixels(image, this.client);
        }
        catch (RuntimeException ex) {
            log.debug("Unable to load image: ", (Throwable)ex);
            return null;
        }
    }

    private void adjustWidgetDimensions() {
        Skin skin = this.config.skin();
        if (skin == Skin.DEFAULT) {
            return;
        }
        for (WidgetOffset widgetOffset : WidgetOffset.values()) {
            Widget widget;
            if (widgetOffset.getSkin() != skin || (widget = this.client.getWidget(widgetOffset.getComponent())) == null) continue;
            if (widgetOffset.getOffsetX() != null) {
                widget.setRelativeX(widgetOffset.getOffsetX().intValue());
            }
            if (widgetOffset.getOffsetY() != null) {
                widget.setRelativeY(widgetOffset.getOffsetY().intValue());
            }
            if (widgetOffset.getWidth() != null) {
                widget.setWidth(widgetOffset.getWidth().intValue());
            }
            if (widgetOffset.getHeight() == null) continue;
            widget.setHeight(widgetOffset.getHeight().intValue());
        }
    }

    private void overrideHealthBars() {
        if (this.config.hdHealthBars()) {
            this.spriteManager.addSpriteOverrides(HealthbarOverride.values());
            this.clientThread.invokeLater(() -> ((Client)this.client).resetHealthBarCaches());
        } else {
            this.restoreHealthBars();
        }
    }

    private void restoreHealthBars() {
        this.spriteManager.removeSpriteOverrides(HealthbarOverride.values());
        this.clientThread.invokeLater(() -> ((Client)this.client).resetHealthBarCaches());
    }

    private void overrideCrossSprites() {
        if (this.config.rsCrossSprites()) {
            if (this.defaultCrossSprites != null) {
                return;
            }
            SpritePixels[] crossSprites = this.client.getCrossSprites();
            if (crossSprites == null) {
                return;
            }
            this.defaultCrossSprites = new SpritePixels[crossSprites.length];
            System.arraycopy(crossSprites, 0, this.defaultCrossSprites, 0, this.defaultCrossSprites.length);
            for (int i = 0; i < crossSprites.length; ++i) {
                SpritePixels newSprite = this.getFileSpritePixels("rs3/cross_sprites/" + i + ".png");
                if (newSprite == null) continue;
                crossSprites[i] = newSprite;
            }
        } else {
            this.restoreCrossSprites();
        }
    }

    private void restoreCrossSprites() {
        if (this.defaultCrossSprites == null) {
            return;
        }
        SpritePixels[] crossSprites = this.client.getCrossSprites();
        if (crossSprites != null && this.defaultCrossSprites.length == crossSprites.length) {
            System.arraycopy(this.defaultCrossSprites, 0, crossSprites, 0, this.defaultCrossSprites.length);
        }
        this.defaultCrossSprites = null;
    }

    private void restoreWidgetDimensions() {
        for (WidgetOffset widgetOffset : WidgetOffset.values()) {
            Widget widget = this.client.getWidget(widgetOffset.getComponent());
            if (widget == null) continue;
            widget.revalidate();
        }
    }

    private void removeGameframe() {
        this.restoreSprites();
        this.restoreWidgetSprites();
        BufferedImage compassImage = this.spriteManager.getSprite(169, 0);
        if (compassImage != null) {
            SpritePixels compass = ImageUtil.getImageSpritePixels(compassImage, this.client);
            this.client.setCompass(compass);
        }
    }
}

