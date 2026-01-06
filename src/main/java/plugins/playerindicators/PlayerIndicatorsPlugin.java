/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.inject.Provides
 *  javax.inject.Inject
 *  net.runelite.api.Client
 *  net.runelite.api.FriendsChatRank
 *  net.runelite.api.MenuAction
 *  net.runelite.api.MenuEntry
 *  net.runelite.api.Player
 *  net.runelite.api.events.ClientTick
 *  net.runelite.api.events.ScriptPostFired
 *  net.runelite.api.widgets.Widget
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package net.runelite.client.plugins.playerindicators;

import com.google.common.annotations.VisibleForTesting;
import com.google.inject.Provides;
import java.lang.reflect.Type;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.FriendsChatRank;
import net.runelite.api.MenuAction;
import net.runelite.api.MenuEntry;
import net.runelite.api.Player;
import net.runelite.api.events.ClientTick;
import net.runelite.api.events.ScriptPostFired;
import net.runelite.api.widgets.Widget;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ProfileChanged;
import net.runelite.client.game.ChatIconManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.playerindicators.PlayerIndicatorsConfig;
import net.runelite.client.plugins.playerindicators.PlayerIndicatorsMinimapOverlay;
import net.runelite.client.plugins.playerindicators.PlayerIndicatorsOverlay;
import net.runelite.client.plugins.playerindicators.PlayerIndicatorsService;
import net.runelite.client.plugins.playerindicators.PlayerIndicatorsTileOverlay;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.util.ColorUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@PluginDescriptor(name="Player Indicators", description="Highlight players on-screen and/or on the minimap", tags={"highlight", "minimap", "overlay", "players"})
public class PlayerIndicatorsPlugin
extends Plugin {
    private static final Logger log = LoggerFactory.getLogger(PlayerIndicatorsPlugin.class);
    private static final String TRADING_WITH_TEXT = "Trading with: ";
    @Inject
    private OverlayManager overlayManager;
    @Inject
    private PlayerIndicatorsConfig config;
    @Inject
    private PlayerIndicatorsOverlay playerIndicatorsOverlay;
    @Inject
    private PlayerIndicatorsTileOverlay playerIndicatorsTileOverlay;
    @Inject
    private PlayerIndicatorsMinimapOverlay playerIndicatorsMinimapOverlay;
    @Inject
    private PlayerIndicatorsService playerIndicatorsService;
    @Inject
    private Client client;
    @Inject
    private ChatIconManager chatIconManager;
    @Inject
    private ClientThread clientThread;
    @Inject
    private ConfigManager configManager;

    @Provides
    PlayerIndicatorsConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(PlayerIndicatorsConfig.class);
    }

    @Override
    protected void startUp() throws Exception {
        this.overlayManager.add(this.playerIndicatorsOverlay);
        this.overlayManager.add(this.playerIndicatorsTileOverlay);
        this.overlayManager.add(this.playerIndicatorsMinimapOverlay);
        this.migrate();
    }

    @Override
    protected void shutDown() throws Exception {
        this.overlayManager.remove(this.playerIndicatorsOverlay);
        this.overlayManager.remove(this.playerIndicatorsTileOverlay);
        this.overlayManager.remove(this.playerIndicatorsMinimapOverlay);
    }

    @Subscribe
    public void onProfileChanged(ProfileChanged profileChanged) {
        this.migrate();
    }

    private void migrate() {
        String[] keys = new String[]{"drawOwnName", "highlightSelf", "drawPartyMembers", "highlightPartyMembers", "drawFriendNames", "highlightFriends", "drawClanMemberNames", "highlightFriendsChat", "drawTeamMemberNames", "highlightTeamMembers", "drawClanChatMemberNames", "highlightClanMembers", "drawNonClanMemberNames", "highlightOthers"};
        Boolean disableOutsidePvP = (Boolean)this.configManager.getConfiguration("playerindicators", "disableOutsidePvP", (Type)((Object)Boolean.class));
        if (disableOutsidePvP != null) {
            this.configManager.unsetConfiguration("playerindicators", "disableOutsidePvP");
            for (int i = 0; i < keys.length; i += 2) {
                String old = keys[i];
                String new_ = keys[i + 1];
                Boolean value = (Boolean)this.configManager.getConfiguration("playerindicators", old, (Type)((Object)Boolean.class));
                if (value == null) continue;
                PlayerIndicatorsConfig.HighlightSetting newSetting = disableOutsidePvP != false && value != false ? PlayerIndicatorsConfig.HighlightSetting.PVP : (value != false ? PlayerIndicatorsConfig.HighlightSetting.ENABLED : PlayerIndicatorsConfig.HighlightSetting.DISABLED);
                this.configManager.setConfiguration("playerindicators", new_, newSetting);
                this.configManager.unsetConfiguration("playerindicators", old);
            }
            log.debug("Migrated player indicators config");
        }
    }

    @Subscribe
    public void onClientTick(ClientTick clientTick) {
        MenuEntry[] menuEntries;
        if (this.client.isMenuOpen()) {
            return;
        }
        for (MenuEntry entry : menuEntries = this.client.getMenuEntries()) {
            PlayerIndicatorsService.Decorations decorations;
            Player player;
            MenuAction type = entry.getType();
            if (type != MenuAction.WALK && type != MenuAction.WIDGET_TARGET_ON_PLAYER && type != MenuAction.ITEM_USE_ON_PLAYER && type != MenuAction.PLAYER_FIRST_OPTION && type != MenuAction.PLAYER_SECOND_OPTION && type != MenuAction.PLAYER_THIRD_OPTION && type != MenuAction.PLAYER_FOURTH_OPTION && type != MenuAction.PLAYER_FIFTH_OPTION && type != MenuAction.PLAYER_SIXTH_OPTION && type != MenuAction.PLAYER_SEVENTH_OPTION && type != MenuAction.PLAYER_EIGHTH_OPTION && type != MenuAction.RUNELITE_PLAYER || (player = entry.getPlayer()) == null || (decorations = this.playerIndicatorsService.getDecorations(player)) == null) continue;
            String oldTarget = entry.getTarget();
            String newTarget = this.decorateTarget(oldTarget, decorations);
            entry.setTarget(newTarget);
        }
    }

    @VisibleForTesting
    String decorateTarget(String oldTarget, PlayerIndicatorsService.Decorations decorations) {
        Object newTarget = oldTarget;
        if (decorations.getColor() != null && this.config.colorPlayerMenu()) {
            String prefix = "";
            int idx = oldTarget.indexOf("->");
            if (idx != -1) {
                prefix = oldTarget.substring(0, idx + 3);
                oldTarget = oldTarget.substring(idx + 3);
            }
            idx = oldTarget.indexOf(62);
            oldTarget = oldTarget.substring(idx + 1);
            newTarget = prefix + ColorUtil.prependColorTag(oldTarget, decorations.getColor());
        }
        FriendsChatRank rank = decorations.getFriendsChatRank();
        int image = -1;
        if (rank != null && rank != FriendsChatRank.UNRANKED && this.config.showFriendsChatRanks()) {
            image = this.chatIconManager.getIconNumber(rank);
        } else if (decorations.getClanTitle() != null && this.config.showClanChatRanks()) {
            image = this.chatIconManager.getIconNumber(decorations.getClanTitle());
        }
        if (image != -1) {
            newTarget = "<img=" + image + ">" + (String)newTarget;
        }
        return newTarget;
    }

    @Subscribe
    public void onScriptPostFired(ScriptPostFired event) {
        if (event.getScriptId() == 755) {
            this.clientThread.invokeLater(() -> {
                Widget tradeTitle = this.client.getWidget(21954591);
                String header = tradeTitle.getText();
                String playerName = header.substring(TRADING_WITH_TEXT.length());
                Player targetPlayer = this.findPlayer(playerName);
                if (targetPlayer == null) {
                    return;
                }
                PlayerIndicatorsService.Decorations decorations = this.playerIndicatorsService.getDecorations(targetPlayer);
                if (decorations != null && decorations.getColor() != null) {
                    tradeTitle.setText(TRADING_WITH_TEXT + ColorUtil.wrapWithColorTag(playerName, decorations.getColor()));
                }
            });
        }
    }

    private Player findPlayer(String name) {
        for (Player player : this.client.getPlayers()) {
            if (!player.getName().equals(name)) continue;
            return player;
        }
        return null;
    }
}

