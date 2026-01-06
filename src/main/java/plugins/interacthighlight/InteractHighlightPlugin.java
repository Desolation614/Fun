/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.inject.Provides
 *  javax.annotation.Nullable
 *  javax.inject.Inject
 *  net.runelite.api.Actor
 *  net.runelite.api.Client
 *  net.runelite.api.DecorativeObject
 *  net.runelite.api.GameObject
 *  net.runelite.api.GameState
 *  net.runelite.api.GroundObject
 *  net.runelite.api.MenuAction
 *  net.runelite.api.NPC
 *  net.runelite.api.Scene
 *  net.runelite.api.Tile
 *  net.runelite.api.TileObject
 *  net.runelite.api.WallObject
 *  net.runelite.api.events.GameStateChanged
 *  net.runelite.api.events.GameTick
 *  net.runelite.api.events.InteractingChanged
 *  net.runelite.api.events.MenuOptionClicked
 *  net.runelite.api.events.NpcDespawned
 *  net.runelite.api.widgets.WidgetUtil
 */
package net.runelite.client.plugins.interacthighlight;

import com.google.inject.Provides;
import javax.annotation.Nullable;
import javax.inject.Inject;
import net.runelite.api.Actor;
import net.runelite.api.Client;
import net.runelite.api.DecorativeObject;
import net.runelite.api.GameObject;
import net.runelite.api.GameState;
import net.runelite.api.GroundObject;
import net.runelite.api.MenuAction;
import net.runelite.api.NPC;
import net.runelite.api.Scene;
import net.runelite.api.Tile;
import net.runelite.api.TileObject;
import net.runelite.api.WallObject;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.InteractingChanged;
import net.runelite.api.events.MenuOptionClicked;
import net.runelite.api.events.NpcDespawned;
import net.runelite.api.widgets.WidgetUtil;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.interacthighlight.InteractHighlightConfig;
import net.runelite.client.plugins.interacthighlight.InteractHighlightOverlay;
import net.runelite.client.ui.overlay.OverlayManager;

@PluginDescriptor(name="Interact Highlight", description="Outlines npcs and objects you interact with or hover over", enabledByDefault=false)
public class InteractHighlightPlugin
extends Plugin {
    @Inject
    private OverlayManager overlayManager;
    @Inject
    private InteractHighlightOverlay interactHighlightOverlay;
    @Inject
    private Client client;
    private TileObject interactedObject;
    private NPC interactedNpc;
    boolean attacked;
    private int clickTick;
    private int gameCycle;

    @Provides
    InteractHighlightConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(InteractHighlightConfig.class);
    }

    @Override
    protected void startUp() {
        this.overlayManager.add(this.interactHighlightOverlay);
    }

    @Override
    protected void shutDown() {
        this.overlayManager.remove(this.interactHighlightOverlay);
    }

    @Subscribe
    public void onGameStateChanged(GameStateChanged gameStateChanged) {
        if (gameStateChanged.getGameState() == GameState.LOADING) {
            this.interactedObject = null;
        }
    }

    @Subscribe
    public void onNpcDespawned(NpcDespawned npcDespawned) {
        if (npcDespawned.getNpc() == this.interactedNpc) {
            this.interactedNpc = null;
        }
    }

    @Subscribe
    public void onGameTick(GameTick gameTick) {
        if (this.client.getTickCount() > this.clickTick && this.client.getLocalDestinationLocation() == null) {
            this.interactedObject = null;
            this.interactedNpc = null;
        }
    }

    @Subscribe
    public void onInteractingChanged(InteractingChanged interactingChanged) {
        if (interactingChanged.getSource() == this.client.getLocalPlayer() && this.client.getTickCount() > this.clickTick && interactingChanged.getTarget() != this.interactedNpc) {
            this.interactedNpc = null;
            this.attacked = interactingChanged.getTarget() != null && interactingChanged.getTarget().getCombatLevel() > 0;
        }
    }

    @Subscribe
    public void onMenuOptionClicked(MenuOptionClicked menuOptionClicked) {
        switch (menuOptionClicked.getMenuAction()) {
            case WIDGET_TARGET_ON_GAME_OBJECT: 
            case GAME_OBJECT_FIRST_OPTION: 
            case GAME_OBJECT_SECOND_OPTION: 
            case GAME_OBJECT_THIRD_OPTION: 
            case GAME_OBJECT_FOURTH_OPTION: 
            case GAME_OBJECT_FIFTH_OPTION: {
                int x = menuOptionClicked.getParam0();
                int y = menuOptionClicked.getParam1();
                int id = menuOptionClicked.getId();
                this.interactedObject = this.findTileObject(x, y, id);
                this.interactedNpc = null;
                this.clickTick = this.client.getTickCount();
                this.gameCycle = this.client.getGameCycle();
                break;
            }
            case WIDGET_TARGET_ON_NPC: 
            case NPC_FIRST_OPTION: 
            case NPC_SECOND_OPTION: 
            case NPC_THIRD_OPTION: 
            case NPC_FOURTH_OPTION: 
            case NPC_FIFTH_OPTION: {
                this.interactedObject = null;
                this.interactedNpc = menuOptionClicked.getMenuEntry().getNpc();
                this.attacked = menuOptionClicked.getMenuAction() == MenuAction.NPC_SECOND_OPTION || menuOptionClicked.getMenuAction() == MenuAction.WIDGET_TARGET_ON_NPC && this.client.getSelectedWidget() != null && WidgetUtil.componentToInterface((int)this.client.getSelectedWidget().getId()) == 218;
                this.clickTick = this.client.getTickCount();
                this.gameCycle = this.client.getGameCycle();
                break;
            }
            case WALK: 
            case WIDGET_TARGET_ON_WIDGET: 
            case WIDGET_TARGET_ON_GROUND_ITEM: 
            case WIDGET_TARGET_ON_PLAYER: 
            case GROUND_ITEM_FIRST_OPTION: 
            case GROUND_ITEM_SECOND_OPTION: 
            case GROUND_ITEM_THIRD_OPTION: 
            case GROUND_ITEM_FOURTH_OPTION: 
            case GROUND_ITEM_FIFTH_OPTION: {
                this.interactedObject = null;
                this.interactedNpc = null;
                break;
            }
            default: {
                if (!menuOptionClicked.isItemOp()) break;
                this.interactedObject = null;
                this.interactedNpc = null;
            }
        }
    }

    TileObject findTileObject(int x, int y, int id) {
        Scene scene = this.client.getScene();
        Tile[][][] tiles = scene.getExtendedTiles();
        Tile tile = tiles[this.client.getPlane()][x += 40][y += 40];
        if (tile != null) {
            for (GameObject gameObject : tile.getGameObjects()) {
                if (gameObject == null || gameObject.getId() != id) continue;
                return gameObject;
            }
            WallObject wallObject = tile.getWallObject();
            if (wallObject != null && wallObject.getId() == id) {
                return wallObject;
            }
            DecorativeObject decorativeObject = tile.getDecorativeObject();
            if (decorativeObject != null && decorativeObject.getId() == id) {
                return decorativeObject;
            }
            GroundObject groundObject = tile.getGroundObject();
            if (groundObject != null && groundObject.getId() == id) {
                return groundObject;
            }
        }
        return null;
    }

    @Nullable
    Actor getInteractedTarget() {
        return this.interactedNpc != null ? this.interactedNpc : this.client.getLocalPlayer().getInteracting();
    }

    TileObject getInteractedObject() {
        return this.interactedObject;
    }

    boolean isAttacked() {
        return this.attacked;
    }

    int getGameCycle() {
        return this.gameCycle;
    }
}

