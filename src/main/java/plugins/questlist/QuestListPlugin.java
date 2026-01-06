/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Strings
 *  javax.inject.Inject
 *  net.runelite.api.Client
 *  net.runelite.api.events.ScriptCallbackEvent
 *  net.runelite.api.events.ScriptPostFired
 *  net.runelite.api.events.VarClientIntChanged
 *  net.runelite.api.events.VarbitChanged
 *  net.runelite.api.widgets.Widget
 */
package net.runelite.client.plugins.questlist;

import com.google.common.base.Strings;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.events.ScriptCallbackEvent;
import net.runelite.api.events.ScriptPostFired;
import net.runelite.api.events.VarClientIntChanged;
import net.runelite.api.events.VarbitChanged;
import net.runelite.api.widgets.Widget;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.game.chatbox.ChatboxPanelManager;
import net.runelite.client.game.chatbox.ChatboxTextInput;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;

@PluginDescriptor(name="Quest List", description="Adds a search filter to the quest list")
public class QuestListPlugin
extends Plugin {
    private static final String MENU_OPEN = "Open";
    private static final String MENU_CLOSE = "Close";
    private static final String MENU_SEARCH = "Search";
    @Inject
    private Client client;
    @Inject
    private ChatboxPanelManager chatboxPanelManager;
    @Inject
    private ClientThread clientThread;
    private ChatboxTextInput searchInput;
    private Widget questSearchButton;

    @Override
    protected void startUp() {
        this.clientThread.invoke(this::addQuestButtons);
    }

    @Override
    protected void shutDown() {
        Widget header = this.client.getWidget(26148864);
        if (header != null) {
            header.deleteAllChildren();
        }
    }

    @Subscribe
    public void onScriptPostFired(ScriptPostFired event) {
        if (event.getScriptId() == 1350) {
            this.addQuestButtons();
        }
    }

    private void addQuestButtons() {
        Widget header = this.client.getWidget(26148864);
        if (header != null) {
            header.deleteAllChildren();
            this.questSearchButton = header.createChild(-1, 5);
            this.questSearchButton.setSpriteId(1113);
            this.questSearchButton.setOriginalWidth(18);
            this.questSearchButton.setOriginalHeight(17);
            this.questSearchButton.setXPositionMode(2);
            this.questSearchButton.setOriginalX(5);
            this.questSearchButton.setOriginalY(0);
            this.questSearchButton.setHasListener(true);
            this.questSearchButton.setAction(1, MENU_OPEN);
            this.questSearchButton.setOnOpListener(new Object[]{e -> this.openSearch()});
            this.questSearchButton.setName(MENU_SEARCH);
            this.questSearchButton.revalidate();
        }
    }

    @Subscribe
    public void onVarbitChanged(VarbitChanged varbitChanged) {
        if (this.isChatboxOpen() && !this.isOnQuestTab()) {
            this.chatboxPanelManager.close();
        }
    }

    @Subscribe
    public void onVarClientIntChanged(VarClientIntChanged varClientIntChanged) {
        if (varClientIntChanged.getIndex() == 171 && this.isChatboxOpen() && !this.isOnQuestTab()) {
            this.chatboxPanelManager.close();
        }
    }

    @Subscribe
    public void onScriptCallbackEvent(ScriptCallbackEvent scriptCallbackEvent) {
        int intStackSize;
        if (!"questFilter".equals(scriptCallbackEvent.getEventName()) || !this.isChatboxOpen()) {
            return;
        }
        String filter = this.searchInput.getValue();
        if (Strings.isNullOrEmpty((String)filter)) {
            return;
        }
        int[] intStack = this.client.getIntStack();
        int row = intStack[(intStackSize = this.client.getIntStackSize()) - 1];
        String questName = (String)this.client.getDBTableField(row, 2, 0)[0];
        int n = questName.toLowerCase().contains(filter.toLowerCase()) ? 0 : 1;
        intStack[intStackSize - 2] = n;
    }

    private boolean isOnQuestTab() {
        return this.client.getVarbitValue(8168) == 1 && this.client.getVarcIntValue(171) == 2;
    }

    private boolean isChatboxOpen() {
        return this.searchInput != null && this.chatboxPanelManager.getCurrentInput() == this.searchInput;
    }

    private void closeSearch() {
        this.chatboxPanelManager.close();
        this.redrawQuests();
        this.client.playSoundEffect(2266);
    }

    private void openSearch() {
        this.client.playSoundEffect(2266);
        this.questSearchButton.setAction(1, MENU_CLOSE);
        this.questSearchButton.setOnOpListener(new Object[]{e -> this.closeSearch()});
        this.searchInput = this.chatboxPanelManager.openTextInput("Search quest list").onChanged(s -> this.redrawQuests()).onDone(s -> false).onClose(() -> {
            this.redrawQuests();
            this.questSearchButton.setOnOpListener(new Object[]{e -> this.openSearch()});
            this.questSearchButton.setAction(1, MENU_OPEN);
        }).build();
    }

    private void redrawQuests() {
        Widget w = this.client.getWidget(26148866);
        if (w == null) {
            return;
        }
        Object[] onVarTransmitListener = w.getOnVarTransmitListener();
        if (onVarTransmitListener == null) {
            return;
        }
        this.clientThread.invokeLater(() -> this.client.runScript(onVarTransmitListener));
    }
}

