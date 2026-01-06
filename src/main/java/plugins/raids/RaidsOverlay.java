/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.inject.Inject
 *  net.runelite.api.Client
 *  net.runelite.api.FriendsChatManager
 *  net.runelite.api.MenuAction
 *  net.runelite.http.api.worlds.World
 *  net.runelite.http.api.worlds.WorldRegion
 *  net.runelite.http.api.worlds.WorldResult
 */
package net.runelite.client.plugins.raids;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.FriendsChatManager;
import net.runelite.api.MenuAction;
import net.runelite.client.game.WorldService;
import net.runelite.client.plugins.raids.RaidRoom;
import net.runelite.client.plugins.raids.RaidsConfig;
import net.runelite.client.plugins.raids.RaidsPlugin;
import net.runelite.client.plugins.raids.solver.Room;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.TitleComponent;
import net.runelite.http.api.worlds.World;
import net.runelite.http.api.worlds.WorldRegion;
import net.runelite.http.api.worlds.WorldResult;

class RaidsOverlay
extends OverlayPanel {
    private static final int OLM_PLANE = 0;
    private static final String SCREENSHOT_ACTION = "Screenshot";
    private final Client client;
    private final RaidsPlugin plugin;
    private final RaidsConfig config;
    private final WorldService worldService;
    private boolean scoutOverlayShown = false;

    @Inject
    private RaidsOverlay(Client client, RaidsPlugin plugin, RaidsConfig config, WorldService worldService) {
        super(plugin);
        this.setPosition(OverlayPosition.TOP_LEFT);
        this.setPriority(0.0f);
        this.client = client;
        this.plugin = plugin;
        this.config = config;
        this.worldService = worldService;
        this.addMenuEntry(MenuAction.RUNELITE_OVERLAY_CONFIG, "Configure", "Raids overlay");
        this.addMenuEntry(MenuAction.RUNELITE_OVERLAY, SCREENSHOT_ACTION, "Raids overlay", e -> plugin.screenshotScoutOverlay());
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        this.scoutOverlayShown = this.shouldShowOverlay();
        if (!this.scoutOverlayShown) {
            return null;
        }
        Color color = Color.WHITE;
        String layout = this.plugin.getRaid().getLayout().toCodeString();
        if (this.config.enableLayoutWhitelist() && !this.plugin.getLayoutWhitelist().contains(layout.toLowerCase())) {
            color = Color.RED;
        }
        this.panelComponent.getChildren().add(TitleComponent.builder().text(layout).color(color).build());
        if (this.config.fcDisplay()) {
            World world;
            WorldRegion region;
            color = Color.RED;
            FriendsChatManager friendsChatManager = this.client.getFriendsChatManager();
            FontMetrics metrics = graphics.getFontMetrics();
            String worldString = "W" + this.client.getWorld();
            WorldResult worldResult = this.worldService.getWorlds();
            if (worldResult != null && (region = (world = worldResult.findWorld(this.client.getWorld())).getRegion()) != null) {
                String countryCode = region.getAlpha2();
                worldString = worldString + " (" + countryCode + ")";
            }
            String owner = "Join a FC";
            if (friendsChatManager != null) {
                owner = friendsChatManager.getOwner();
                color = Color.ORANGE;
            }
            this.panelComponent.setPreferredSize(new Dimension(Math.max(129, metrics.stringWidth(worldString) + metrics.stringWidth(owner) + 14), 0));
            this.panelComponent.getChildren().add(LineComponent.builder().left(worldString).right(owner).leftColor(Color.ORANGE).rightColor(color).build());
        }
        for (Room layoutRoom : this.plugin.getRaid().getLayout().getRooms()) {
            int position = layoutRoom.getPosition();
            RaidRoom room = this.plugin.getRaid().getRoom(position);
            if (room == null) continue;
            color = Color.WHITE;
            switch (room.getType()) {
                case COMBAT: {
                    if (this.plugin.getRoomWhitelist().contains(room.getName().toLowerCase())) {
                        color = Color.GREEN;
                    } else if (this.plugin.getRoomBlacklist().contains(room.getName().toLowerCase()) || this.config.enableRotationWhitelist() && !this.plugin.getRotationMatches()) {
                        color = Color.RED;
                    }
                    String name = room == RaidRoom.UNKNOWN_COMBAT ? "Unknown" : room.getName();
                    this.panelComponent.getChildren().add(LineComponent.builder().left(room.getType().getName()).right(name).rightColor(color).build());
                    break;
                }
                case PUZZLE: {
                    if (this.plugin.getRoomWhitelist().contains(room.getName().toLowerCase())) {
                        color = Color.GREEN;
                    } else if (this.plugin.getRoomBlacklist().contains(room.getName().toLowerCase())) {
                        color = Color.RED;
                    }
                    String name = room == RaidRoom.UNKNOWN_PUZZLE ? "Unknown" : room.getName();
                    this.panelComponent.getChildren().add(LineComponent.builder().left(room.getType().getName()).right(name).rightColor(color).build());
                }
            }
        }
        return super.render(graphics);
    }

    private boolean shouldShowOverlay() {
        if (this.plugin.getRaid() == null || this.plugin.getRaid().getLayout() == null || !this.config.scoutOverlay()) {
            return false;
        }
        if (this.plugin.isInRaidChambers()) {
            if (this.client.getVarbitValue(5425) > 0) {
                if (this.client.getPlane() == 0) {
                    return false;
                }
                return this.config.scoutOverlayInRaid();
            }
            return true;
        }
        return this.plugin.getRaidPartyID() != -1 && this.config.scoutOverlayAtBank();
    }

    public boolean isScoutOverlayShown() {
        return this.scoutOverlayShown;
    }
}

