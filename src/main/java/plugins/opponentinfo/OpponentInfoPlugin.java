/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.inject.Provides
 *  javax.inject.Inject
 *  net.runelite.api.Actor
 *  net.runelite.api.Client
 *  net.runelite.api.GameState
 *  net.runelite.api.MenuAction
 *  net.runelite.api.MenuEntry
 *  net.runelite.api.NPC
 *  net.runelite.api.events.GameStateChanged
 *  net.runelite.api.events.GameTick
 *  net.runelite.api.events.InteractingChanged
 *  net.runelite.api.events.MenuEntryAdded
 *  net.runelite.api.events.ScriptPostFired
 *  net.runelite.api.widgets.Widget
 */
package net.runelite.client.plugins.opponentinfo;

import com.google.common.annotations.VisibleForTesting;
import com.google.inject.Provides;
import java.text.DecimalFormat;
import java.time.Duration;
import java.time.Instant;
import javax.inject.Inject;
import net.runelite.api.Actor;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.MenuAction;
import net.runelite.api.MenuEntry;
import net.runelite.api.NPC;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.InteractingChanged;
import net.runelite.api.events.MenuEntryAdded;
import net.runelite.api.events.ScriptPostFired;
import net.runelite.api.widgets.Widget;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.hiscore.HiscoreEndpoint;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.opponentinfo.OpponentInfoConfig;
import net.runelite.client.plugins.opponentinfo.OpponentInfoOverlay;
import net.runelite.client.plugins.opponentinfo.PlayerComparisonOverlay;
import net.runelite.client.ui.overlay.OverlayManager;

@PluginDescriptor(name="Opponent Information", description="Show name and hitpoints information about the NPC you are fighting", tags={"combat", "health", "hitpoints", "npcs", "overlay"})
public class OpponentInfoPlugin
extends Plugin {
    private static final Duration WAIT = Duration.ofSeconds(5L);
    private static final DecimalFormat PERCENT_FORMAT = new DecimalFormat("0.0");
    @Inject
    private Client client;
    @Inject
    private OpponentInfoConfig config;
    @Inject
    private OverlayManager overlayManager;
    @Inject
    private OpponentInfoOverlay opponentInfoOverlay;
    @Inject
    private PlayerComparisonOverlay playerComparisonOverlay;
    private HiscoreEndpoint hiscoreEndpoint = HiscoreEndpoint.NORMAL;
    private Actor lastOpponent;
    @VisibleForTesting
    private Instant lastTime;

    @Provides
    OpponentInfoConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(OpponentInfoConfig.class);
    }

    @Override
    protected void startUp() throws Exception {
        this.overlayManager.add(this.opponentInfoOverlay);
        this.overlayManager.add(this.playerComparisonOverlay);
    }

    @Override
    protected void shutDown() throws Exception {
        this.lastOpponent = null;
        this.lastTime = null;
        this.overlayManager.remove(this.opponentInfoOverlay);
        this.overlayManager.remove(this.playerComparisonOverlay);
    }

    @Subscribe
    public void onGameStateChanged(GameStateChanged gameStateChanged) {
        if (gameStateChanged.getGameState() != GameState.LOGGED_IN) {
            return;
        }
        this.hiscoreEndpoint = HiscoreEndpoint.fromWorldTypes(this.client.getWorldType());
    }

    @Subscribe
    public void onInteractingChanged(InteractingChanged event) {
        if (event.getSource() != this.client.getLocalPlayer()) {
            return;
        }
        Actor opponent = event.getTarget();
        if (opponent == null) {
            this.lastTime = Instant.now();
            return;
        }
        this.lastOpponent = opponent;
    }

    @Subscribe
    public void onGameTick(GameTick gameTick) {
        if (this.lastOpponent != null && this.lastTime != null && this.client.getLocalPlayer().getInteracting() == null && Duration.between(this.lastTime, Instant.now()).compareTo(WAIT) > 0) {
            this.lastOpponent = null;
        }
    }

    @Subscribe
    public void onMenuEntryAdded(MenuEntryAdded menuEntryAdded) {
        if (menuEntryAdded.getType() != MenuAction.NPC_SECOND_OPTION.getId() || !menuEntryAdded.getOption().equals("Attack") || !this.config.showOpponentsInMenu()) {
            return;
        }
        NPC npc = menuEntryAdded.getMenuEntry().getNpc();
        if (npc == null) {
            return;
        }
        if (npc.getInteracting() == this.client.getLocalPlayer() || this.lastOpponent == npc) {
            MenuEntry[] menuEntries = this.client.getMenuEntries();
            menuEntries[menuEntries.length - 1].setTarget("*" + menuEntries[menuEntries.length - 1].getTarget());
        }
    }

    @Subscribe
    public void onScriptPostFired(ScriptPostFired event) {
        if (event.getScriptId() == 2103) {
            this.updateBossHealthBarText();
        }
    }

    private void updateBossHealthBarText() {
        Widget widget = this.client.getWidget(19857428);
        if (widget == null) {
            return;
        }
        int currHp = this.client.getVarbitValue(6099);
        int maxHp = this.client.getVarbitValue(6100);
        if (maxHp <= 0) {
            return;
        }
        switch (this.config.hitpointsDisplayStyle()) {
            case PERCENTAGE: {
                widget.setText(OpponentInfoPlugin.getPercentText(currHp, maxHp));
                break;
            }
            case BOTH: {
                widget.setText(widget.getText() + " (" + OpponentInfoPlugin.getPercentText(currHp, maxHp) + ")");
            }
        }
    }

    private static String getPercentText(int current, int maximum) {
        double percent = 100.0 * (double)current / (double)maximum;
        return PERCENT_FORMAT.format(percent) + "%";
    }

    HiscoreEndpoint getHiscoreEndpoint() {
        return this.hiscoreEndpoint;
    }

    Actor getLastOpponent() {
        return this.lastOpponent;
    }

    Instant getLastTime() {
        return this.lastTime;
    }
}

