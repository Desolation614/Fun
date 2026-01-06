/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.inject.Provides
 *  javax.inject.Inject
 *  net.runelite.api.Client
 *  net.runelite.api.GameState
 *  net.runelite.api.SpritePixels
 *  net.runelite.api.events.GameStateChanged
 *  net.runelite.api.events.ScriptPostFired
 *  net.runelite.api.widgets.Widget
 */
package net.runelite.client.plugins.minimap;

import com.google.inject.Provides;
import java.awt.Color;
import java.util.Arrays;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.SpritePixels;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.ScriptPostFired;
import net.runelite.api.widgets.Widget;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.minimap.MinimapConfig;
import net.runelite.client.plugins.minimap.MinimapDot;

@PluginDescriptor(name="Minimap", description="Customize the color of minimap dots, hide the minimap, and zoom", tags={"items", "npcs", "players", "zoom"})
public class MinimapPlugin
extends Plugin {
    private static final int DOT_ITEM = 0;
    private static final int DOT_NPC = 1;
    private static final int DOT_PLAYER = 2;
    private static final int DOT_FRIEND = 3;
    private static final int DOT_TEAM = 4;
    private static final int DOT_FRIENDSCHAT = 5;
    private static final int DOT_CLAN = 6;
    @Inject
    private Client client;
    @Inject
    private MinimapConfig config;
    private SpritePixels[] originalDotSprites;

    @Provides
    private MinimapConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(MinimapConfig.class);
    }

    @Override
    protected void startUp() {
        this.updateMinimapWidgetVisibility(this.config.hideMinimap());
        this.storeOriginalDots();
        this.replaceMapDots();
        this.client.setMinimapZoom(this.config.zoom());
    }

    @Override
    protected void shutDown() {
        this.updateMinimapWidgetVisibility(false);
        this.restoreOriginalDots();
        this.client.setMinimapZoom(false);
    }

    @Subscribe
    public void onGameStateChanged(GameStateChanged event) {
        GameState state = event.getGameState();
        if (state == GameState.STARTING) {
            this.originalDotSprites = null;
        } else if (state == GameState.LOGIN_SCREEN && this.originalDotSprites == null) {
            this.storeOriginalDots();
            this.replaceMapDots();
        }
    }

    @Subscribe
    public void onConfigChanged(ConfigChanged event) {
        if (!event.getGroup().equals("minimap")) {
            return;
        }
        if (event.getKey().equals("hideMinimap")) {
            this.updateMinimapWidgetVisibility(this.config.hideMinimap());
            return;
        }
        if (event.getKey().equals("zoom")) {
            this.client.setMinimapZoom(this.config.zoom());
            return;
        }
        this.restoreOriginalDots();
        this.replaceMapDots();
    }

    @Subscribe
    public void onScriptPostFired(ScriptPostFired scriptPostFired) {
        if (scriptPostFired.getScriptId() == 907) {
            this.updateMinimapWidgetVisibility(this.config.hideMinimap());
        }
    }

    private void updateMinimapWidgetVisibility(boolean enable) {
        Widget resizableNormalWidget;
        Widget resizableStonesWidget = this.client.getWidget(10551391);
        if (resizableStonesWidget != null) {
            resizableStonesWidget.setHidden(enable);
        }
        if ((resizableNormalWidget = this.client.getWidget(10747996)) != null && !resizableNormalWidget.isSelfHidden()) {
            for (Widget widget : resizableNormalWidget.getStaticChildren()) {
                if (widget.getId() == 10747938 || widget.getId() == 10747939) continue;
                widget.setHidden(enable);
            }
        }
    }

    private void replaceMapDots() {
        SpritePixels[] mapDots = this.client.getMapDots();
        if (mapDots == null) {
            return;
        }
        this.applyDot(mapDots, 0, this.config.itemColor());
        this.applyDot(mapDots, 1, this.config.npcColor());
        this.applyDot(mapDots, 2, this.config.playerColor());
        this.applyDot(mapDots, 3, this.config.friendColor());
        this.applyDot(mapDots, 4, this.config.teamColor());
        this.applyDot(mapDots, 5, this.config.friendsChatColor());
        this.applyDot(mapDots, 6, this.config.clanChatColor());
    }

    private void applyDot(SpritePixels[] mapDots, int id, Color color) {
        if (id < mapDots.length && color != null) {
            mapDots[id] = MinimapDot.create(this.client, color);
        }
    }

    private void storeOriginalDots() {
        SpritePixels[] originalDots = this.client.getMapDots();
        if (originalDots == null) {
            return;
        }
        this.originalDotSprites = Arrays.copyOf(originalDots, originalDots.length);
    }

    private void restoreOriginalDots() {
        SpritePixels[] mapDots = this.client.getMapDots();
        if (this.originalDotSprites == null || mapDots == null) {
            return;
        }
        System.arraycopy(this.originalDotSprites, 0, mapDots, 0, mapDots.length);
    }
}

