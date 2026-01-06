/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.MoreObjects
 *  javax.inject.Inject
 *  net.runelite.api.ChatMessageType
 *  net.runelite.api.Client
 *  net.runelite.api.EnumComposition
 *  net.runelite.api.GameState
 *  net.runelite.api.ItemComposition
 *  net.runelite.api.MenuAction
 *  net.runelite.api.events.DraggingWidgetChanged
 *  net.runelite.api.events.MenuOptionClicked
 *  net.runelite.api.events.ScriptPostFired
 *  net.runelite.api.widgets.Widget
 *  net.runelite.api.widgets.WidgetUtil
 *  org.apache.commons.lang3.ArrayUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package net.runelite.client.plugins.prayer;

import com.google.common.base.MoreObjects;
import java.util.Arrays;
import java.util.Comparator;
import java.util.stream.Collectors;
import javax.inject.Inject;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.EnumComposition;
import net.runelite.api.GameState;
import net.runelite.api.ItemComposition;
import net.runelite.api.MenuAction;
import net.runelite.api.events.DraggingWidgetChanged;
import net.runelite.api.events.MenuOptionClicked;
import net.runelite.api.events.ScriptPostFired;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetUtil;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.chat.ChatMessageManager;
import net.runelite.client.chat.QueuedMessage;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ProfileChanged;
import net.runelite.client.menus.MenuManager;
import net.runelite.client.menus.WidgetMenuOption;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class PrayerReorder {
    private static final Logger log = LoggerFactory.getLogger(PrayerReorder.class);
    private static final int PRAYER_X_OFFSET = 37;
    private static final int PRAYER_Y_OFFSET = 37;
    private static final int QUICK_PRAYER_SPRITE_X_OFFSET = 2;
    private static final int QUICK_PRAYER_SPRITE_Y_OFFSET = 2;
    private static final int PRAYER_COLUMN_COUNT = 5;
    private static final String LOCK = "Disable prayer reordering";
    private static final String UNLOCK = "Enable prayer reordering";
    private static final WidgetMenuOption FIXED_PRAYER_TAB_LOCK = new WidgetMenuOption("Disable prayer reordering", "", 0x2240044);
    private static final WidgetMenuOption FIXED_PRAYER_TAB_UNLOCK = new WidgetMenuOption("Enable prayer reordering", "", 0x2240044);
    private static final WidgetMenuOption RESIZABLE_PRAYER_TAB_LOCK = new WidgetMenuOption("Disable prayer reordering", "", 10551360);
    private static final WidgetMenuOption RESIZABLE_PRAYER_TAB_UNLOCK = new WidgetMenuOption("Enable prayer reordering", "", 10551360);
    private static final WidgetMenuOption RESIZABLE_BOTTOM_LINE_PRAYER_TAB_LOCK = new WidgetMenuOption("Disable prayer reordering", "", 10747961);
    private static final WidgetMenuOption RESIZABLE_BOTTOM_LINE_PRAYER_TAB_UNLOCK = new WidgetMenuOption("Enable prayer reordering", "", 10747961);
    private final Client client;
    private final ClientThread clientThread;
    private final ConfigManager configManager;
    private final ChatMessageManager chatMessageManager;
    private final MenuManager menuManager;
    private boolean reordering;

    void startUp() {
        this.refreshPrayerTabOption();
        this.configManager.unsetConfiguration("reorderprayers", "unlockPrayerReordering");
        this.configManager.unsetConfiguration("reorderprayers", "prayerOrder");
        this.configManager.unsetConfiguration("runelite", "ReorderPrayersPlugin".toLowerCase());
        if (this.client.getGameState() == GameState.LOGGED_IN) {
            this.clientThread.invokeLater(this::redrawPrayers);
        }
    }

    void shutDown() {
        this.reordering = false;
        this.clearPrayerTabMenus();
        this.clientThread.invokeLater(() -> this.rebuildPrayers(false));
        this.clientThread.invokeLater(this::redrawPrayers);
    }

    void reset() {
        String[] str;
        for (String key : this.configManager.getConfigurationKeys("prayer.prayer_order_book")) {
            str = key.split("\\.", 2);
            if (str.length != 2) continue;
            this.configManager.unsetConfiguration(str[0], str[1]);
        }
        for (String key : this.configManager.getConfigurationKeys("prayer.prayer_hidden_book")) {
            str = key.split("\\.", 2);
            if (str.length != 2) continue;
            this.configManager.unsetConfiguration(str[0], str[1]);
        }
        this.clientThread.invokeLater(this::redrawPrayers);
    }

    private int[] getPrayerOrder(int prayerbook) {
        String s = this.configManager.getConfiguration("prayer", "prayer_order_book_" + prayerbook);
        if (s == null) {
            return null;
        }
        return Arrays.stream(s.split(",")).mapToInt(Integer::parseInt).toArray();
    }

    private void setPrayerOrder(int prayerbook, int[] prayers) {
        String s = Arrays.stream(prayers).mapToObj(Integer::toString).collect(Collectors.joining(","));
        this.configManager.setConfiguration("prayer", "prayer_order_book_" + prayerbook, s);
    }

    private boolean isHidden(int prayerbook, int prayer) {
        Boolean b = (Boolean)this.configManager.getConfiguration("prayer", "prayer_hidden_book_" + prayerbook + "_" + prayer, Boolean.TYPE);
        return b == Boolean.TRUE;
    }

    private void setHidden(int prayerbook, int prayer, boolean hidden) {
        if (hidden) {
            this.configManager.setConfiguration("prayer", "prayer_hidden_book_" + prayerbook + "_" + prayer, true);
        } else {
            this.configManager.unsetConfiguration("prayer", "prayer_hidden_book_" + prayerbook + "_" + prayer);
        }
    }

    @Subscribe
    public void onDraggingWidgetChanged(DraggingWidgetChanged event) {
        if (event.isDraggingWidget() && this.client.getMouseCurrentButton() == 0) {
            Widget draggedWidget = this.client.getDraggedWidget();
            Widget draggedOnWidget = this.client.getDraggedOnWidget();
            if (draggedWidget != null && draggedOnWidget != null) {
                int draggedGroupId = WidgetUtil.componentToInterface((int)draggedWidget.getId());
                int draggedOnGroupId = WidgetUtil.componentToInterface((int)draggedOnWidget.getId());
                if (draggedGroupId != 541 || draggedOnGroupId != 541) {
                    return;
                }
                int prayerbook = this.client.getVarbitValue(14826);
                int fromId = this.findPrayerIdFromComponent(prayerbook, draggedWidget);
                int toId = this.findPrayerIdFromComponent(prayerbook, draggedOnWidget);
                if (fromId == -1 || toId == -1) {
                    return;
                }
                this.client.setDraggedOnWidget(null);
                int[] prayerOrder = this.getPrayerOrder(prayerbook);
                if (prayerOrder == null) {
                    prayerOrder = this.defaultPrayerOrder(this.getPrayerBookEnum(prayerbook));
                }
                int fromIdx = ArrayUtils.indexOf((int[])prayerOrder, (int)fromId);
                int toIdx = ArrayUtils.indexOf((int[])prayerOrder, (int)toId);
                log.debug("Swapping prayer {}<->{} (idx {}<->{})", new Object[]{fromId, toId, fromIdx, toIdx});
                int tmp = prayerOrder[toIdx];
                prayerOrder[toIdx] = prayerOrder[fromIdx];
                prayerOrder[fromIdx] = tmp;
                this.setPrayerOrder(prayerbook, prayerOrder);
                this.rebuildPrayers(true);
            }
        }
    }

    @Subscribe
    public void onScriptPostFired(ScriptPostFired scriptPostFired) {
        int scriptId = scriptPostFired.getScriptId();
        if (this.reordering && scriptId == 463 || scriptId == 547 || scriptId == 466) {
            this.rebuildPrayers(this.reordering);
        }
    }

    private EnumComposition getPrayerBookEnum(int prayerbook) {
        boolean vigour;
        if (prayerbook == 1) {
            return this.client.getEnum(4959);
        }
        boolean deadeye = this.client.getVarbitValue(16097) != 0;
        boolean bl = vigour = this.client.getVarbitValue(16098) != 0;
        if (deadeye && vigour) {
            return this.client.getEnum(3343);
        }
        if (deadeye) {
            return this.client.getEnum(3341);
        }
        if (vigour) {
            return this.client.getEnum(3342);
        }
        return this.client.getEnum(4956);
    }

    private int[] defaultPrayerOrder(EnumComposition prayerEnum) {
        return Arrays.stream(prayerEnum.getKeys()).boxed().sorted(Comparator.comparing(id -> {
            int prayerObjId = prayerEnum.getIntValue(id.intValue());
            ItemComposition prayerObj = this.client.getItemDefinition(prayerObjId);
            return prayerObj.getIntValue(1753);
        })).mapToInt(i -> i).toArray();
    }

    private int findPrayerIdFromComponent(int prayerbook, Widget component) {
        EnumComposition prayers = this.getPrayerBookEnum(prayerbook);
        int[] keys = prayers.getKeys();
        int[] vals = prayers.getIntVals();
        for (int i = 0; i < keys.length; ++i) {
            ItemComposition prayer = this.client.getItemDefinition(vals[i]);
            if (prayer.getIntValue(1751) != component.getId()) continue;
            return keys[i];
        }
        return -1;
    }

    private void clearPrayerTabMenus() {
        this.menuManager.removeManagedCustomMenu(FIXED_PRAYER_TAB_LOCK);
        this.menuManager.removeManagedCustomMenu(RESIZABLE_PRAYER_TAB_LOCK);
        this.menuManager.removeManagedCustomMenu(RESIZABLE_BOTTOM_LINE_PRAYER_TAB_LOCK);
        this.menuManager.removeManagedCustomMenu(FIXED_PRAYER_TAB_UNLOCK);
        this.menuManager.removeManagedCustomMenu(RESIZABLE_PRAYER_TAB_UNLOCK);
        this.menuManager.removeManagedCustomMenu(RESIZABLE_BOTTOM_LINE_PRAYER_TAB_UNLOCK);
    }

    private void reordering(boolean state) {
        this.reordering = state;
        String message = this.reordering ? "Prayer book reordering is now enabled." : "Prayer book reordering is now disabled.";
        this.chatMessageManager.queue(QueuedMessage.builder().type(ChatMessageType.CONSOLE).runeLiteFormattedMessage(message).build());
        this.refreshPrayerTabOption();
        this.redrawPrayers();
    }

    private void refreshPrayerTabOption() {
        this.clearPrayerTabMenus();
        if (this.reordering) {
            this.menuManager.addManagedCustomMenu(FIXED_PRAYER_TAB_LOCK, e -> this.reordering(false));
            this.menuManager.addManagedCustomMenu(RESIZABLE_PRAYER_TAB_LOCK, e -> this.reordering(false));
            this.menuManager.addManagedCustomMenu(RESIZABLE_BOTTOM_LINE_PRAYER_TAB_LOCK, e -> this.reordering(false));
        } else {
            this.menuManager.addManagedCustomMenu(FIXED_PRAYER_TAB_UNLOCK, e -> this.reordering(true));
            this.menuManager.addManagedCustomMenu(RESIZABLE_PRAYER_TAB_UNLOCK, e -> this.reordering(true));
            this.menuManager.addManagedCustomMenu(RESIZABLE_BOTTOM_LINE_PRAYER_TAB_UNLOCK, e -> this.reordering(true));
        }
    }

    private void redrawPrayers() {
        Widget w = this.client.getWidget(541, 0);
        if (w != null) {
            this.client.runScript(w.getOnVarTransmitListener());
        }
    }

    private void rebuildPrayers(boolean unlocked) {
        int prayerbook = this.client.getVarbitValue(14826);
        EnumComposition prayerBookEnum = this.getPrayerBookEnum(prayerbook);
        int[] prayerIds = (int[])MoreObjects.firstNonNull((Object)this.getPrayerOrder(prayerbook), (Object)this.defaultPrayerOrder(prayerBookEnum));
        if (this.isInterfaceOpen(541)) {
            int index = 0;
            for (int prayerId : prayerIds) {
                int prayerObjId = prayerBookEnum.getIntValue(prayerId);
                ItemComposition prayerObj = this.client.getItemDefinition(prayerObjId);
                Widget prayerWidget = this.client.getWidget(prayerObj.getIntValue(1751));
                assert (prayerWidget != null);
                int widgetConfig = prayerWidget.getClickMask();
                widgetConfig = unlocked ? (widgetConfig |= 0x120000) : (widgetConfig &= 0xFFEDFFFF);
                prayerWidget.setClickMask(widgetConfig);
                boolean hidden = this.isHidden(prayerbook, prayerId);
                if (hidden && !unlocked) {
                    prayerWidget.setHidden(true);
                    ++index;
                    continue;
                }
                if (unlocked) {
                    prayerWidget.setHidden(false);
                    if (hidden) {
                        prayerWidget.setAction(3, "Unhide");
                        prayerWidget.getChild(1).setOpacity(200);
                    } else {
                        prayerWidget.setAction(3, "Hide");
                    }
                } else {
                    prayerWidget.setAction(3, null);
                }
                int x = index % 5;
                int y = index / 5;
                int widgetX = x * 37;
                int widgetY = y * 37;
                prayerWidget.setPos(widgetX, widgetY);
                prayerWidget.revalidate();
                ++index;
            }
            this.createWarning(unlocked);
        }
        if (this.isInterfaceOpen(77)) {
            Widget prayersContainer = this.client.getWidget(0x4D0004);
            if (prayersContainer == null) {
                return;
            }
            Widget[] prayerWidgets = prayersContainer.getDynamicChildren();
            if (prayerWidgets == null || prayerWidgets.length != prayerBookEnum.size() * 3) {
                return;
            }
            int[] sortedPrayers = this.defaultPrayerOrder(prayerBookEnum);
            int index = 0;
            for (int prayerId : prayerIds) {
                int x = index % 5;
                int y = index / 5;
                Widget prayerWidget = prayerWidgets[prayerId];
                prayerWidget.setPos(x * 37, y * 37);
                prayerWidget.revalidate();
                int sortedIdx = ArrayUtils.indexOf((int[])sortedPrayers, (int)prayerId);
                int childId = prayerBookEnum.size() + 2 * sortedIdx;
                Widget prayerSpriteWidget = prayerWidgets[childId];
                prayerSpriteWidget.setPos(2 + x * 37, 2 + y * 37);
                prayerSpriteWidget.revalidate();
                Widget prayerToggleWidget = prayerWidgets[childId + 1];
                prayerToggleWidget.setPos(x * 37, y * 37);
                prayerToggleWidget.revalidate();
                ++index;
            }
        }
    }

    private void createWarning(boolean unlocked) {
        Widget w = this.client.getWidget(35454976);
        w.deleteAllChildren();
        if (unlocked) {
            Widget c = w.createChild(3);
            c.setHeightMode(1);
            c.setWidthMode(1);
            c.setTextColor(0xFF0000);
            c.setFilled(true);
            c.setOpacity(220);
            c.revalidate();
        }
    }

    private boolean isInterfaceOpen(int interfaceId) {
        return this.client.getWidget(interfaceId, 0) != null;
    }

    @Subscribe
    public void onMenuOptionClicked(MenuOptionClicked menuOptionClicked) {
        if (this.reordering && menuOptionClicked.getMenuAction() == MenuAction.CC_OP && menuOptionClicked.getId() == 4 && ("Hide".equals(menuOptionClicked.getMenuOption()) || "Unhide".equals(menuOptionClicked.getMenuOption()))) {
            Widget widget = menuOptionClicked.getWidget();
            int prayerbook = this.client.getVarbitValue(14826);
            int prayerId = this.findPrayerIdFromComponent(prayerbook, widget);
            if (prayerId != -1) {
                widget = widget.getChild(1);
                boolean hidden = this.isHidden(prayerbook, prayerId);
                if (hidden) {
                    widget.setOpacity(0);
                    this.setHidden(prayerbook, prayerId, false);
                } else {
                    widget.setOpacity(200);
                    this.setHidden(prayerbook, prayerId, true);
                }
            }
        }
    }

    @Subscribe
    public void onProfileChanged(ProfileChanged e) {
        this.clientThread.invokeLater(this::redrawPrayers);
    }

    @Inject
    public PrayerReorder(Client client, ClientThread clientThread, ConfigManager configManager, ChatMessageManager chatMessageManager, MenuManager menuManager) {
        this.client = client;
        this.clientThread = clientThread;
        this.configManager = configManager;
        this.chatMessageManager = chatMessageManager;
        this.menuManager = menuManager;
    }
}

