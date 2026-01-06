/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.collect.ImmutableSet
 *  com.google.inject.Provides
 *  javax.inject.Inject
 *  net.runelite.api.Client
 *  net.runelite.api.GraphicsObject
 *  net.runelite.api.NPC
 *  net.runelite.api.Player
 *  net.runelite.api.Projectile
 *  net.runelite.api.Renderable
 */
package net.runelite.client.plugins.entityhider;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Provides;
import java.util.Set;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.GraphicsObject;
import net.runelite.api.NPC;
import net.runelite.api.Player;
import net.runelite.api.Projectile;
import net.runelite.api.Renderable;
import net.runelite.client.callback.Hooks;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.game.NpcUtil;
import net.runelite.client.party.PartyService;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.entityhider.EntityHiderConfig;

@PluginDescriptor(name="Entity Hider", description="Hide players, NPCs, and/or projectiles", tags={"npcs", "players", "projectiles"}, enabledByDefault=false)
public class EntityHiderPlugin
extends Plugin {
    private static final Set<Integer> THRALL_IDS = ImmutableSet.of((Object)10878, (Object)10881, (Object)10884, (Object)10879, (Object)10882, (Object)10885, (Object[])new Integer[]{10880, 10883, 10886});
    private static final Set<Integer> RANDOM_EVENT_NPC_IDS = ImmutableSet.of((Object)6747, (Object)5426, (Object)307, (Object)314, (Object)322, (Object)6749, (Object[])new Integer[]{390, 6754, 6744, 6748, 5429, 5430, 5431, 5432, 312, 13445, 13446, 13443, 13444, 326, 327, 5438, 5441, 6746, 5437, 5440, 6750, 6751, 6752, 6753, 5436, 5439, 380, 6738, 6755, 375, 376, 5510, 6743, 12551, 12552});
    @Inject
    private Client client;
    @Inject
    private EntityHiderConfig config;
    @Inject
    private Hooks hooks;
    @Inject
    private NpcUtil npcUtil;
    @Inject
    private PartyService partyService;
    private boolean hideOthers;
    private boolean hideOthers2D;
    private boolean hidePartyMembers;
    private boolean hideFriends;
    private boolean hideFriendsChatMembers;
    private boolean hideClanMembers;
    private boolean hideIgnoredPlayers;
    private boolean hideLocalPlayer;
    private boolean hideLocalPlayer2D;
    private boolean hideNPCs;
    private boolean hideNPCs2D;
    private boolean hideDeadNpcs;
    private boolean hidePets;
    private boolean hideThralls;
    private boolean hideRandomEvents;
    private boolean hideAttackers;
    private boolean hideProjectiles;
    private final Hooks.RenderableDrawListener drawListener = this::shouldDraw;

    @Provides
    EntityHiderConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(EntityHiderConfig.class);
    }

    @Override
    protected void startUp() {
        this.updateConfig();
        this.hooks.registerRenderableDrawListener(this.drawListener);
    }

    @Override
    protected void shutDown() {
        this.hooks.unregisterRenderableDrawListener(this.drawListener);
    }

    @Subscribe
    public void onConfigChanged(ConfigChanged e) {
        if (e.getGroup().equals("entityhider")) {
            this.updateConfig();
        }
    }

    private void updateConfig() {
        this.hideOthers = this.config.hideOthers();
        this.hideOthers2D = this.config.hideOthers2D();
        this.hidePartyMembers = this.config.hidePartyMembers();
        this.hideFriends = this.config.hideFriends();
        this.hideFriendsChatMembers = this.config.hideFriendsChatMembers();
        this.hideClanMembers = this.config.hideClanChatMembers();
        this.hideIgnoredPlayers = this.config.hideIgnores();
        this.hideLocalPlayer = this.config.hideLocalPlayer();
        this.hideLocalPlayer2D = this.config.hideLocalPlayer2D();
        this.hideNPCs = this.config.hideNPCs();
        this.hideNPCs2D = this.config.hideNPCs2D();
        this.hideDeadNpcs = this.config.hideDeadNpcs();
        this.hidePets = this.config.hidePets();
        this.hideThralls = this.config.hideThralls();
        this.hideRandomEvents = this.config.hideRandomEvents();
        this.hideAttackers = this.config.hideAttackers();
        this.hideProjectiles = this.config.hideProjectiles();
    }

    @VisibleForTesting
    boolean shouldDraw(Renderable renderable, boolean drawingUI) {
        if (renderable instanceof Player) {
            Player player = (Player)renderable;
            Player local = this.client.getLocalPlayer();
            if (player.getName() == null) {
                return true;
            }
            if (player == local) {
                return !(!drawingUI ? this.hideLocalPlayer : this.hideLocalPlayer2D);
            }
            if (this.hideAttackers && player.getInteracting() == local) {
                return false;
            }
            if (this.partyService.isInParty() && this.partyService.getMemberByDisplayName(player.getName()) != null) {
                return !this.hidePartyMembers;
            }
            if (player.isFriend()) {
                return !this.hideFriends;
            }
            if (player.isFriendsChatMember()) {
                return !this.hideFriendsChatMembers;
            }
            if (player.isClanMember()) {
                return !this.hideClanMembers;
            }
            if (this.client.getIgnoreContainer().findByName(player.getName()) != null) {
                return !this.hideIgnoredPlayers;
            }
            return !(!drawingUI ? this.hideOthers : this.hideOthers2D);
        }
        if (renderable instanceof NPC) {
            NPC npc = (NPC)renderable;
            if (npc.getComposition().isFollower() && npc != this.client.getFollower()) {
                return !this.hidePets;
            }
            if (this.npcUtil.isDying(npc) && this.hideDeadNpcs) {
                return false;
            }
            if (npc.getInteracting() == this.client.getLocalPlayer()) {
                boolean b = this.hideAttackers;
                if (this.hideNPCs2D || this.hideNPCs) {
                    b &= drawingUI ? this.hideNPCs2D : this.hideNPCs;
                }
                return !b;
            }
            if (THRALL_IDS.contains(npc.getId())) {
                return !this.hideThralls;
            }
            if (RANDOM_EVENT_NPC_IDS.contains(npc.getId())) {
                return !this.hideRandomEvents;
            }
            return !(!drawingUI ? this.hideNPCs : this.hideNPCs2D);
        }
        if (renderable instanceof Projectile) {
            return !this.hideProjectiles;
        }
        if (renderable instanceof GraphicsObject) {
            if (!this.hideDeadNpcs) {
                return true;
            }
            switch (((GraphicsObject)renderable).getId()) {
                case 1562: 
                case 1563: 
                case 1564: 
                case 1565: 
                case 1566: 
                case 1567: {
                    return false;
                }
            }
            return true;
        }
        return true;
    }
}

