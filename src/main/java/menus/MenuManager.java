/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.LinkedHashMultimap
 *  com.google.common.collect.Multimap
 *  javax.inject.Inject
 *  javax.inject.Singleton
 *  net.runelite.api.Client
 *  net.runelite.api.MenuAction
 *  net.runelite.api.MenuEntry
 *  net.runelite.api.events.MenuEntryAdded
 *  net.runelite.api.events.PlayerMenuOptionsChanged
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package net.runelite.client.menus;

import com.google.common.base.Preconditions;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import javax.inject.Inject;
import javax.inject.Singleton;
import net.runelite.api.Client;
import net.runelite.api.MenuAction;
import net.runelite.api.MenuEntry;
import net.runelite.api.events.MenuEntryAdded;
import net.runelite.api.events.PlayerMenuOptionsChanged;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.menus.WidgetMenuOption;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class MenuManager {
    private static final Logger log = LoggerFactory.getLogger(MenuManager.class);
    private static final int IDX_LOWER = 4;
    private static final int IDX_UPPER = 8;
    private final Client client;
    private final Map<Integer, String> playerMenuIndexMap = new HashMap<Integer, String>();
    private final Multimap<Integer, WidgetMenuOption> managedMenuOptions = LinkedHashMultimap.create();

    @Inject
    private MenuManager(Client client, EventBus eventBus) {
        this.client = client;
        eventBus.register(this);
    }

    public void addManagedCustomMenu(WidgetMenuOption customMenuOption, Consumer<MenuEntry> callback) {
        this.managedMenuOptions.put((Object)customMenuOption.getWidgetId(), (Object)customMenuOption);
        customMenuOption.callback = callback;
    }

    public void removeManagedCustomMenu(WidgetMenuOption customMenuOption) {
        this.managedMenuOptions.remove((Object)customMenuOption.getWidgetId(), (Object)customMenuOption);
    }

    private static boolean menuContainsCustomMenu(MenuEntry[] menuEntries, WidgetMenuOption customMenuOption) {
        for (MenuEntry menuEntry : menuEntries) {
            String option = menuEntry.getOption();
            String target = menuEntry.getTarget();
            if (!option.equals(customMenuOption.getMenuOption()) || !target.equals(customMenuOption.getMenuTarget())) continue;
            return true;
        }
        return false;
    }

    @Subscribe
    public void onMenuEntryAdded(MenuEntryAdded event) {
        if (this.client.isWidgetSelected() || event.getType() != MenuAction.CC_OP.getId()) {
            return;
        }
        int widgetId = event.getActionParam1();
        Collection options = this.managedMenuOptions.get((Object)widgetId);
        if (options.isEmpty()) {
            return;
        }
        MenuEntry[] menuEntries = this.client.getMenuEntries();
        int insertIdx = -1;
        for (WidgetMenuOption currentMenu : options) {
            if (MenuManager.menuContainsCustomMenu(menuEntries, currentMenu)) {
                return;
            }
            this.client.createMenuEntry(insertIdx--).setOption(currentMenu.getMenuOption()).setTarget(currentMenu.getMenuTarget()).setType(MenuAction.RUNELITE).setParam1(widgetId).onClick(currentMenu.callback);
        }
    }

    public void addPlayerMenuItem(String menuText) {
        Preconditions.checkNotNull((Object)menuText);
        int playerMenuIndex = this.findEmptyPlayerMenuIndex();
        if (playerMenuIndex == 8) {
            return;
        }
        this.addPlayerMenuItem(playerMenuIndex, menuText);
    }

    public void removePlayerMenuItem(String menuText) {
        Preconditions.checkNotNull((Object)menuText);
        for (Map.Entry<Integer, String> entry : this.playerMenuIndexMap.entrySet()) {
            if (!entry.getValue().equalsIgnoreCase(menuText)) continue;
            this.removePlayerMenuItem(entry.getKey());
            break;
        }
    }

    @Subscribe
    public void onPlayerMenuOptionsChanged(PlayerMenuOptionsChanged event) {
        int idx = event.getIndex();
        String menuText = this.playerMenuIndexMap.get(idx);
        if (menuText == null) {
            return;
        }
        int newIdx = this.findEmptyPlayerMenuIndex();
        if (newIdx == 8) {
            log.debug("Client has updated player menu index {} where option {} was, and there are no more free slots available", (Object)idx, (Object)menuText);
            return;
        }
        log.debug("Client has updated player menu index {} where option {} was, moving to index {}", new Object[]{idx, menuText, newIdx});
        this.playerMenuIndexMap.remove(idx);
        this.addPlayerMenuItem(newIdx, menuText);
    }

    private void addPlayerMenuItem(int playerOptionIndex, String menuText) {
        this.client.getPlayerOptions()[playerOptionIndex] = menuText;
        this.client.getPlayerOptionsPriorities()[playerOptionIndex] = true;
        this.client.getPlayerMenuTypes()[playerOptionIndex] = MenuAction.RUNELITE_PLAYER.getId();
        this.playerMenuIndexMap.put(playerOptionIndex, menuText);
    }

    private void removePlayerMenuItem(int playerOptionIndex) {
        this.client.getPlayerOptions()[playerOptionIndex] = null;
        this.playerMenuIndexMap.remove(playerOptionIndex);
    }

    private int findEmptyPlayerMenuIndex() {
        int index;
        String[] playerOptions = this.client.getPlayerOptions();
        for (index = 4; index < 8 && playerOptions[index] != null; ++index) {
        }
        return index;
    }
}

