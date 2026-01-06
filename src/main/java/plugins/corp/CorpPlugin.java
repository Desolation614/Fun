/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.inject.Provides
 *  javax.inject.Inject
 *  net.runelite.api.Actor
 *  net.runelite.api.ChatMessageType
 *  net.runelite.api.GameState
 *  net.runelite.api.MenuAction
 *  net.runelite.api.MenuEntry
 *  net.runelite.api.NPC
 *  net.runelite.api.events.GameStateChanged
 *  net.runelite.api.events.HitsplatApplied
 *  net.runelite.api.events.InteractingChanged
 *  net.runelite.api.events.MenuEntryAdded
 *  net.runelite.api.events.NpcDespawned
 *  net.runelite.api.events.NpcSpawned
 *  net.runelite.api.events.VarbitChanged
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package net.runelite.client.plugins.corp;

import com.google.inject.Provides;
import java.awt.Color;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;
import javax.inject.Inject;
import net.runelite.api.Actor;
import net.runelite.api.ChatMessageType;
import net.runelite.api.GameState;
import net.runelite.api.MenuAction;
import net.runelite.api.MenuEntry;
import net.runelite.api.NPC;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.HitsplatApplied;
import net.runelite.api.events.InteractingChanged;
import net.runelite.api.events.MenuEntryAdded;
import net.runelite.api.events.NpcDespawned;
import net.runelite.api.events.NpcSpawned;
import net.runelite.api.events.VarbitChanged;
import net.runelite.client.chat.ChatColorType;
import net.runelite.client.chat.ChatMessageBuilder;
import net.runelite.client.chat.ChatMessageManager;
import net.runelite.client.chat.QueuedMessage;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.game.npcoverlay.HighlightedNpc;
import net.runelite.client.game.npcoverlay.NpcOverlayService;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.corp.CorpConfig;
import net.runelite.client.plugins.corp.CorpDamageOverlay;
import net.runelite.client.ui.overlay.OverlayManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@PluginDescriptor(name="Corporeal Beast", description="Show damage statistics and highlight dark energy cores", tags={"bosses", "combat", "pve", "overlay"})
public class CorpPlugin
extends Plugin {
    private static final Logger log = LoggerFactory.getLogger(CorpPlugin.class);
    private static final String ATTACK = "Attack";
    private static final String DARK_ENERGY_CORE = "Dark energy core";
    private NPC corp;
    private NPC core;
    private int yourDamage;
    private int totalDamage;
    private final Set<Actor> players = new HashSet<Actor>();
    @Inject
    private ChatMessageManager chatMessageManager;
    @Inject
    private OverlayManager overlayManager;
    @Inject
    private CorpDamageOverlay corpOverlay;
    @Inject
    private CorpConfig config;
    @Inject
    private NpcOverlayService npcOverlayService;
    private final Function<NPC, HighlightedNpc> isCore = npc -> {
        if (npc == this.core) {
            return HighlightedNpc.builder().npc((NPC)npc).tile(true).highlightColor(Color.RED.brighter()).render(n -> this.config.markDarkCore()).build();
        }
        return null;
    };

    @Provides
    CorpConfig getConfig(ConfigManager configManager) {
        return configManager.getConfig(CorpConfig.class);
    }

    @Override
    protected void startUp() throws Exception {
        this.npcOverlayService.registerHighlighter(this.isCore);
        this.overlayManager.add(this.corpOverlay);
    }

    @Override
    protected void shutDown() throws Exception {
        this.npcOverlayService.unregisterHighlighter(this.isCore);
        this.overlayManager.remove(this.corpOverlay);
        this.core = null;
        this.corp = null;
        this.yourDamage = 0;
        this.totalDamage = 0;
        this.players.clear();
    }

    @Subscribe
    public void onGameStateChanged(GameStateChanged gameStateChanged) {
        if (gameStateChanged.getGameState() == GameState.LOADING) {
            this.players.clear();
        }
    }

    @Subscribe
    public void onNpcSpawned(NpcSpawned npcSpawned) {
        NPC npc = npcSpawned.getNpc();
        switch (npc.getId()) {
            case 319: {
                log.debug("Corporeal beast spawn: {}", (Object)npc);
                this.corp = npc;
                this.yourDamage = 0;
                this.totalDamage = 0;
                this.players.clear();
                break;
            }
            case 320: {
                this.core = npc;
            }
        }
    }

    @Subscribe
    public void onNpcDespawned(NpcDespawned npcDespawned) {
        NPC npc = npcDespawned.getNpc();
        if (npc == this.corp) {
            log.debug("Corporeal beast despawn: {}", (Object)npc);
            this.corp = null;
            this.players.clear();
            if (npc.isDead()) {
                String message = new ChatMessageBuilder().append(ChatColorType.NORMAL).append("Corporeal Beast: Your damage: ").append(ChatColorType.HIGHLIGHT).append(Integer.toString(this.yourDamage)).append(ChatColorType.NORMAL).append(", Total damage: ").append(ChatColorType.HIGHLIGHT).append(Integer.toString(this.totalDamage)).build();
                this.chatMessageManager.queue(QueuedMessage.builder().type(ChatMessageType.CONSOLE).runeLiteFormattedMessage(message).build());
            }
        } else if (npc == this.core) {
            this.core = null;
        }
    }

    @Subscribe
    public void onHitsplatApplied(HitsplatApplied hitsplatApplied) {
        Actor actor = hitsplatApplied.getActor();
        if (actor != this.corp) {
            return;
        }
        this.totalDamage += hitsplatApplied.getHitsplat().getAmount();
    }

    @Subscribe
    public void onInteractingChanged(InteractingChanged interactingChanged) {
        Actor source = interactingChanged.getSource();
        Actor target = interactingChanged.getTarget();
        if (this.corp == null || target != this.corp) {
            return;
        }
        this.players.add(source);
    }

    @Subscribe
    public void onVarbitChanged(VarbitChanged varbitChanged) {
        int myDamage;
        if (this.corp != null && varbitChanged.getVarbitId() == 999 && (myDamage = varbitChanged.getValue()) > 0) {
            this.yourDamage = myDamage;
        }
    }

    @Subscribe
    public void onMenuEntryAdded(MenuEntryAdded menuEntryAdded) {
        MenuEntry menuEntry = menuEntryAdded.getMenuEntry();
        NPC npc = menuEntry.getNpc();
        if (npc == null || !DARK_ENERGY_CORE.equals(npc.getName())) {
            return;
        }
        if (menuEntry.getType() != MenuAction.NPC_SECOND_OPTION || !menuEntry.getOption().equals(ATTACK) || !this.config.leftClickCore()) {
            return;
        }
        menuEntry.setDeprioritized(true);
    }

    NPC getCorp() {
        return this.corp;
    }

    NPC getCore() {
        return this.core;
    }

    int getTotalDamage() {
        return this.totalDamage;
    }

    Set<Actor> getPlayers() {
        return this.players;
    }
}

