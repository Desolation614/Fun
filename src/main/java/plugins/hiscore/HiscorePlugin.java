/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.inject.Provides
 *  javax.inject.Inject
 *  javax.inject.Provider
 *  net.runelite.api.ChatMessageType
 *  net.runelite.api.Client
 *  net.runelite.api.IconID
 *  net.runelite.api.MenuAction
 *  net.runelite.api.Player
 *  net.runelite.api.events.ChatMessage
 *  net.runelite.api.events.MenuEntryAdded
 *  net.runelite.api.events.MenuOptionClicked
 *  net.runelite.api.events.VarbitChanged
 *  net.runelite.api.widgets.WidgetUtil
 */
package net.runelite.client.plugins.hiscore;

import com.google.inject.Provides;
import java.awt.image.BufferedImage;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.inject.Inject;
import javax.inject.Provider;
import javax.swing.SwingUtilities;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.IconID;
import net.runelite.api.MenuAction;
import net.runelite.api.Player;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.MenuEntryAdded;
import net.runelite.api.events.MenuOptionClicked;
import net.runelite.api.events.VarbitChanged;
import net.runelite.api.widgets.WidgetUtil;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.hiscore.HiscoreEndpoint;
import net.runelite.client.menus.MenuManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.hiscore.HiscoreConfig;
import net.runelite.client.plugins.hiscore.HiscorePanel;
import net.runelite.client.ui.ClientToolbar;
import net.runelite.client.ui.NavigationButton;
import net.runelite.client.util.ImageUtil;
import net.runelite.client.util.Text;

@PluginDescriptor(name="HiScore", description="Enable the HiScore panel and an optional Lookup option on players", tags={"panel", "players"})
public class HiscorePlugin
extends Plugin {
    private static final String LOOKUP = "Lookup";
    private static final Pattern BOUNTY_PATTERN = Pattern.compile("You have been assigned a new target: <col=[0-9a-f]+>(.*)</col>");
    @Inject
    private Client client;
    @Inject
    private Provider<MenuManager> menuManager;
    @Inject
    private ClientToolbar clientToolbar;
    @Inject
    private HiscoreConfig config;
    private NavigationButton navButton;
    private HiscorePanel hiscorePanel;
    private HiscoreEndpoint localHiscoreEndpoint;

    @Provides
    HiscoreConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(HiscoreConfig.class);
    }

    @Override
    protected void startUp() throws Exception {
        this.hiscorePanel = (HiscorePanel)this.injector.getInstance(HiscorePanel.class);
        BufferedImage icon = ImageUtil.loadImageResource(this.getClass(), "normal.png");
        this.navButton = NavigationButton.builder().tooltip("Hiscore").icon(icon).priority(5).panel(this.hiscorePanel).build();
        this.clientToolbar.addNavigation(this.navButton);
        if (this.config.playerOption()) {
            ((MenuManager)this.menuManager.get()).addPlayerMenuItem(LOOKUP);
        }
    }

    @Override
    protected void shutDown() throws Exception {
        this.hiscorePanel.shutdown();
        this.clientToolbar.removeNavigation(this.navButton);
        ((MenuManager)this.menuManager.get()).removePlayerMenuItem(LOOKUP);
    }

    @Subscribe
    public void onConfigChanged(ConfigChanged event) {
        if (event.getGroup().equals("hiscore")) {
            ((MenuManager)this.menuManager.get()).removePlayerMenuItem(LOOKUP);
            if (this.config.playerOption()) {
                ((MenuManager)this.menuManager.get()).addPlayerMenuItem(LOOKUP);
            }
        }
    }

    @Subscribe
    public void onMenuEntryAdded(MenuEntryAdded event) {
        if (event.getType() != MenuAction.CC_OP.getId() && event.getType() != MenuAction.CC_OP_LOW_PRIORITY.getId() || !this.config.menuOption()) {
            return;
        }
        String option = event.getOption();
        int componentId = event.getActionParam1();
        int groupId = WidgetUtil.componentToInterface((int)componentId);
        if (groupId == 429 && option.equals("Delete") || groupId == 7 && (option.equals("Add ignore") || option.equals("Remove friend")) || groupId == 162 && (option.equals("Add ignore") || option.equals("Message")) || groupId == 432 && option.equals("Delete") || (componentId == 45940742 || componentId == 46006278) && (option.equals("Add ignore") || option.equals("Remove friend")) || groupId == 163 && (option.equals("Add ignore") || option.equals("Message")) || groupId == 726 && (option.equals("Add friend") || option.equals("Remove friend") || option.equals("Remove ignore"))) {
            this.client.createMenuEntry(-2).setOption(LOOKUP).setTarget(event.getTarget()).setType(MenuAction.RUNELITE).setIdentifier(event.getIdentifier()).onClick(e -> {
                HiscoreEndpoint endpoint = HiscorePlugin.findHiscoreEndpointFromPlayerName(e.getTarget());
                String target = Text.removeTags(e.getTarget());
                this.lookupPlayer(target, endpoint);
            });
        }
    }

    @Subscribe
    public void onMenuOptionClicked(MenuOptionClicked event) {
        if (event.getMenuAction() == MenuAction.RUNELITE_PLAYER && event.getMenuOption().equals(LOOKUP)) {
            Player player = event.getMenuEntry().getPlayer();
            if (player == null) {
                return;
            }
            String target = player.getName();
            HiscoreEndpoint endpoint = this.getWorldEndpoint();
            this.lookupPlayer(target, endpoint);
        }
    }

    @Subscribe
    public void onChatMessage(ChatMessage event) {
        if (event.getType() != ChatMessageType.GAMEMESSAGE || !this.config.bountylookup()) {
            return;
        }
        String message = event.getMessage();
        Matcher m = BOUNTY_PATTERN.matcher(message);
        if (m.matches()) {
            this.lookupPlayer(m.group(1), HiscoreEndpoint.NORMAL);
        }
    }

    @Subscribe
    public void onVarbitChanged(VarbitChanged event) {
        this.localHiscoreEndpoint = this.findHiscoreEndpointFromLocalPlayer();
    }

    void lookupPlayer(String playerName, HiscoreEndpoint endpoint) {
        SwingUtilities.invokeLater(() -> {
            this.clientToolbar.openPanel(this.navButton);
            this.hiscorePanel.lookup(playerName, endpoint);
        });
    }

    HiscoreEndpoint getWorldEndpoint() {
        return HiscoreEndpoint.fromWorldTypes(this.client.getWorldType());
    }

    private HiscoreEndpoint findHiscoreEndpointFromLocalPlayer() {
        HiscoreEndpoint profile = this.getWorldEndpoint();
        if (profile != HiscoreEndpoint.NORMAL) {
            return profile;
        }
        switch (this.client.getVarbitValue(1777)) {
            case 1: {
                return HiscoreEndpoint.IRONMAN;
            }
            case 2: {
                return HiscoreEndpoint.ULTIMATE_IRONMAN;
            }
            case 3: {
                return HiscoreEndpoint.HARDCORE_IRONMAN;
            }
        }
        return HiscoreEndpoint.NORMAL;
    }

    private static HiscoreEndpoint findHiscoreEndpointFromPlayerName(String name) {
        if (name.contains(IconID.IRONMAN.toString())) {
            return HiscoreEndpoint.IRONMAN;
        }
        if (name.contains(IconID.ULTIMATE_IRONMAN.toString())) {
            return HiscoreEndpoint.ULTIMATE_IRONMAN;
        }
        if (name.contains(IconID.HARDCORE_IRONMAN.toString())) {
            return HiscoreEndpoint.HARDCORE_IRONMAN;
        }
        if (name.contains(IconID.LEAGUE.toString())) {
            return HiscoreEndpoint.LEAGUE;
        }
        return HiscoreEndpoint.NORMAL;
    }

    public HiscoreEndpoint getLocalHiscoreEndpoint() {
        return this.localHiscoreEndpoint;
    }
}

