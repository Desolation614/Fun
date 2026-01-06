/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.inject.Provides
 *  javax.inject.Inject
 *  net.runelite.api.ChatMessageType
 *  net.runelite.api.Client
 *  net.runelite.api.GameState
 *  net.runelite.api.Item
 *  net.runelite.api.coords.WorldArea
 *  net.runelite.api.coords.WorldPoint
 *  net.runelite.api.events.ChatMessage
 *  net.runelite.api.events.GameStateChanged
 *  net.runelite.api.events.ItemContainerChanged
 *  net.runelite.api.events.VarbitChanged
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package net.runelite.client.plugins.cannon;

import com.google.inject.Provides;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.Item;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.ItemContainerChanged;
import net.runelite.api.events.VarbitChanged;
import net.runelite.client.Notifier;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.cannon.CannonConfig;
import net.runelite.client.plugins.cannon.CannonCounter;
import net.runelite.client.plugins.cannon.CannonOverlay;
import net.runelite.client.plugins.cannon.CannonSpotOverlay;
import net.runelite.client.plugins.cannon.CannonSpots;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.ui.overlay.infobox.InfoBoxManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@PluginDescriptor(name="Cannon", description="Show information about cannon placement and/or amount of cannonballs", tags={"combat", "notifications", "ranged", "overlay"})
public class CannonPlugin
extends Plugin {
    private static final Logger log = LoggerFactory.getLogger(CannonPlugin.class);
    static final int MAX_OVERLAY_DISTANCE = 4100;
    private CannonCounter counter;
    private boolean cannonBallNotificationSent;
    private int cballsLeft;
    private boolean cannonPlaced;
    private WorldArea cannonPosition;
    private int cannonWorld = -1;
    private final List<WorldPoint> spotPoints = new ArrayList<WorldPoint>();
    @Inject
    private ItemManager itemManager;
    @Inject
    private InfoBoxManager infoBoxManager;
    @Inject
    private Notifier notifier;
    @Inject
    private OverlayManager overlayManager;
    @Inject
    private CannonOverlay cannonOverlay;
    @Inject
    private CannonSpotOverlay cannonSpotOverlay;
    @Inject
    private CannonConfig config;
    @Inject
    private Client client;
    @Inject
    private ClientThread clientThread;

    @Provides
    CannonConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(CannonConfig.class);
    }

    @Override
    protected void startUp() throws Exception {
        this.overlayManager.add(this.cannonOverlay);
        this.overlayManager.add(this.cannonSpotOverlay);
        this.clientThread.invoke(() -> {
            this.cballsLeft = this.client.getVarpValue(3);
        });
    }

    @Override
    protected void shutDown() throws Exception {
        this.cannonSpotOverlay.setHidden(true);
        this.overlayManager.remove(this.cannonOverlay);
        this.overlayManager.remove(this.cannonSpotOverlay);
        this.cannonPlaced = false;
        this.cannonWorld = -1;
        this.cannonPosition = null;
        this.cannonBallNotificationSent = false;
        this.cballsLeft = 0;
        this.removeCounter();
        this.spotPoints.clear();
    }

    @Subscribe
    public void onItemContainerChanged(ItemContainerChanged event) {
        if (event.getItemContainer() != this.client.getItemContainer(93)) {
            return;
        }
        boolean hasBase = false;
        boolean hasStand = false;
        boolean hasBarrels = false;
        boolean hasFurnace = false;
        boolean hasAll = false;
        if (!this.cannonPlaced) {
            for (Item item : event.getItemContainer().getItems()) {
                if (item == null) continue;
                switch (item.getId()) {
                    case 6: 
                    case 26520: {
                        hasBase = true;
                        break;
                    }
                    case 8: 
                    case 26522: {
                        hasStand = true;
                        break;
                    }
                    case 10: 
                    case 26524: {
                        hasBarrels = true;
                        break;
                    }
                    case 12: 
                    case 26526: {
                        hasFurnace = true;
                    }
                }
                if (!hasBase || !hasStand || !hasBarrels || !hasFurnace) continue;
                hasAll = true;
                break;
            }
        }
        this.cannonSpotOverlay.setHidden(!hasAll);
    }

    @Subscribe
    public void onConfigChanged(ConfigChanged event) {
        if (event.getGroup().equals("cannon")) {
            if (!this.config.showInfobox()) {
                this.removeCounter();
            } else if (this.cannonPlaced) {
                this.clientThread.invoke(this::addCounter);
            }
        }
    }

    @Subscribe
    public void onGameStateChanged(GameStateChanged gameStateChanged) {
        if (gameStateChanged.getGameState() != GameState.LOGGED_IN) {
            return;
        }
        this.spotPoints.clear();
        for (WorldPoint spot : CannonSpots.getCannonSpots()) {
            if (!WorldPoint.isInScene((Client)this.client, (int)spot.getX(), (int)spot.getY())) continue;
            this.spotPoints.add(spot);
        }
    }

    @Subscribe
    public void onVarbitChanged(VarbitChanged varbitChanged) {
        if (varbitChanged.getVarpId() == 3) {
            int old = this.cballsLeft;
            this.cballsLeft = varbitChanged.getValue();
            if (this.cballsLeft > old) {
                this.cannonBallNotificationSent = false;
            }
            if (!this.cannonBallNotificationSent && this.cballsLeft > 0 && this.config.lowWarningThreshold() >= this.cballsLeft) {
                this.notifier.notify(this.config.showCannonNotifications(), String.format("Your cannon has %d cannon balls remaining!", this.cballsLeft));
                this.cannonBallNotificationSent = true;
            }
        } else if (varbitChanged.getVarpId() == 4) {
            WorldPoint c = WorldPoint.fromCoord((int)varbitChanged.getValue());
            this.cannonPosition = CannonPlugin.buildCannonWorldArea(c);
        } else if (varbitChanged.getVarpId() == 2) {
            boolean bl = this.cannonPlaced = varbitChanged.getValue() == 4;
            if (this.cannonPlaced) {
                this.addCounter();
            } else {
                this.removeCounter();
            }
        }
    }

    @Subscribe
    public void onChatMessage(ChatMessage event) {
        if (event.getType() != ChatMessageType.SPAM && event.getType() != ChatMessageType.GAMEMESSAGE) {
            return;
        }
        if (event.getMessage().equals("You add the furnace.")) {
            this.cannonWorld = this.client.getWorld();
        } else if (event.getMessage().contains("Your cannon is out of ammo!")) {
            this.notifier.notify(this.config.showCannonNotifications(), "Your cannon is out of ammo!");
        }
    }

    Color getStateColor() {
        if (this.cballsLeft > 15) {
            return Color.green;
        }
        if (this.cballsLeft > 5) {
            return Color.orange;
        }
        return Color.red;
    }

    private void addCounter() {
        if (!this.config.showInfobox() || this.counter != null) {
            return;
        }
        this.counter = new CannonCounter((BufferedImage)this.itemManager.getImage(2), this);
        this.counter.setTooltip("Cannonballs");
        this.infoBoxManager.addInfoBox(this.counter);
    }

    private void removeCounter() {
        if (this.counter == null) {
            return;
        }
        this.infoBoxManager.removeInfoBox(this.counter);
        this.counter = null;
    }

    private static WorldArea buildCannonWorldArea(WorldPoint worldPoint) {
        return new WorldArea(worldPoint.getX(), worldPoint.getY(), 3, 3, worldPoint.getPlane());
    }

    public int getCballsLeft() {
        return this.cballsLeft;
    }

    public boolean isCannonPlaced() {
        return this.cannonPlaced;
    }

    public WorldArea getCannonPosition() {
        return this.cannonPosition;
    }

    public int getCannonWorld() {
        return this.cannonWorld;
    }

    public List<WorldPoint> getSpotPoints() {
        return this.spotPoints;
    }
}

