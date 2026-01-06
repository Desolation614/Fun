/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.inject.Provides
 *  javax.inject.Inject
 *  net.runelite.api.ChatMessageType
 *  net.runelite.api.Client
 *  net.runelite.api.EnumComposition
 *  net.runelite.api.ItemComposition
 *  net.runelite.api.events.DraggingWidgetChanged
 *  net.runelite.api.events.ScriptCallbackEvent
 *  net.runelite.api.events.ScriptPreFired
 *  net.runelite.api.widgets.Widget
 *  net.runelite.api.widgets.WidgetUtil
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package net.runelite.client.plugins.spellbook;

import com.google.inject.Provides;
import java.util.Arrays;
import java.util.Comparator;
import java.util.stream.IntStream;
import javax.inject.Inject;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.EnumComposition;
import net.runelite.api.ItemComposition;
import net.runelite.api.events.DraggingWidgetChanged;
import net.runelite.api.events.ScriptCallbackEvent;
import net.runelite.api.events.ScriptPreFired;
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
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.spellbook.SpellbookConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@PluginDescriptor(name="Spellbook", description="Reorder and hide spells")
public class SpellbookPlugin
extends Plugin {
    private static final Logger log = LoggerFactory.getLogger(SpellbookPlugin.class);
    private static final String LOCK = "Disable spell reordering";
    private static final String UNLOCK = "Enable spell reordering";
    private static final int HIDE_UNHIDE_OP = 6;
    private static final WidgetMenuOption FIXED_MAGIC_TAB_LOCK = new WidgetMenuOption("Disable spell reordering", "", 35913797);
    private static final WidgetMenuOption FIXED_MAGIC_TAB_UNLOCK = new WidgetMenuOption("Enable spell reordering", "", 35913797);
    private static final WidgetMenuOption RESIZABLE_MAGIC_TAB_LOCK = new WidgetMenuOption("Disable spell reordering", "", 10551361);
    private static final WidgetMenuOption RESIZABLE_MAGIC_TAB_UNLOCK = new WidgetMenuOption("Enable spell reordering", "", 10551361);
    private static final WidgetMenuOption RESIZABLE_BOTTOM_LINE_MAGIC_TAB_LOCK = new WidgetMenuOption("Disable spell reordering", "", 10747962);
    private static final WidgetMenuOption RESIZABLE_BOTTOM_LINE_MAGIC_TAB_UNLOCK = new WidgetMenuOption("Enable spell reordering", "", 10747962);
    @Inject
    private Client client;
    @Inject
    private ClientThread clientThread;
    @Inject
    private MenuManager menuManager;
    @Inject
    private ChatMessageManager chatMessageManager;
    @Inject
    private ConfigManager configManager;
    private boolean reordering;

    @Provides
    SpellbookConfig getConfig(ConfigManager configManager) {
        return configManager.getConfig(SpellbookConfig.class);
    }

    @Override
    protected void startUp() {
        this.refreshReorderMenus();
        this.clientThread.invokeLater(this::reinitializeSpellbook);
    }

    @Override
    protected void shutDown() {
        this.clearReoderMenus();
        this.clientThread.invokeLater(this::reinitializeSpellbook);
    }

    @Subscribe
    public void onProfileChanged(ProfileChanged event) {
        this.clientThread.invokeLater(this::redrawSpellbook);
    }

    @Override
    public void resetConfiguration() {
        for (String key : this.configManager.getConfigurationKeys("spellbook.spell_")) {
            String[] str = key.split("\\.", 2);
            if (str.length != 2) continue;
            this.configManager.unsetConfiguration(str[0], str[1]);
        }
        this.clientThread.invokeLater(this::redrawSpellbook);
        log.debug("Reset spellbook");
    }

    private void clearReoderMenus() {
        this.menuManager.removeManagedCustomMenu(FIXED_MAGIC_TAB_LOCK);
        this.menuManager.removeManagedCustomMenu(RESIZABLE_MAGIC_TAB_LOCK);
        this.menuManager.removeManagedCustomMenu(RESIZABLE_BOTTOM_LINE_MAGIC_TAB_LOCK);
        this.menuManager.removeManagedCustomMenu(FIXED_MAGIC_TAB_UNLOCK);
        this.menuManager.removeManagedCustomMenu(RESIZABLE_MAGIC_TAB_UNLOCK);
        this.menuManager.removeManagedCustomMenu(RESIZABLE_BOTTOM_LINE_MAGIC_TAB_UNLOCK);
    }

    private void refreshReorderMenus() {
        this.clearReoderMenus();
        if (this.reordering) {
            this.menuManager.addManagedCustomMenu(FIXED_MAGIC_TAB_LOCK, e -> this.reordering(false));
            this.menuManager.addManagedCustomMenu(RESIZABLE_MAGIC_TAB_LOCK, e -> this.reordering(false));
            this.menuManager.addManagedCustomMenu(RESIZABLE_BOTTOM_LINE_MAGIC_TAB_LOCK, e -> this.reordering(false));
        } else {
            this.menuManager.addManagedCustomMenu(FIXED_MAGIC_TAB_UNLOCK, e -> this.reordering(true));
            this.menuManager.addManagedCustomMenu(RESIZABLE_MAGIC_TAB_UNLOCK, e -> this.reordering(true));
            this.menuManager.addManagedCustomMenu(RESIZABLE_BOTTOM_LINE_MAGIC_TAB_UNLOCK, e -> this.reordering(true));
        }
    }

    private void reordering(boolean state) {
        this.reordering = state;
        String message = this.reordering ? "Spell book reordering is now enabled." : "Spell book reordering is now disabled.";
        this.chatMessageManager.queue(QueuedMessage.builder().type(ChatMessageType.CONSOLE).runeLiteFormattedMessage(message).build());
        this.refreshReorderMenus();
        this.redrawSpellbook();
    }

    @Subscribe
    public void onScriptPreFired(ScriptPreFired event) {
        if (event.getScriptId() == 2616) {
            int[] stack = this.client.getIntStack();
            int sz = this.client.getIntStackSize();
            int spellBookEnum = stack[sz - 12];
            this.clientThread.invokeLater(() -> this.initializeSpells(spellBookEnum));
        }
    }

    @Subscribe
    public void onDraggingWidgetChanged(DraggingWidgetChanged event) {
        if (event.isDraggingWidget() && this.client.getMouseCurrentButton() == 0) {
            Widget draggedWidget = this.client.getDraggedWidget();
            Widget draggedOnWidget = this.client.getDraggedOnWidget();
            if (draggedWidget == null || draggedOnWidget == null) {
                return;
            }
            int draggedGroupId = WidgetUtil.componentToInterface((int)draggedWidget.getId());
            int draggedOnGroupId = WidgetUtil.componentToInterface((int)draggedOnWidget.getId());
            if (draggedGroupId != 218 || draggedOnGroupId != 218) {
                return;
            }
            int subSpellbookId = this.client.getEnum(5280).getIntValue(this.client.getVarbitValue(4070));
            int spellbookId = this.client.getEnum(subSpellbookId).getIntValue(this.client.getVarbitValue(9730));
            EnumComposition spellbook = this.client.getEnum(spellbookId);
            int[] order = this.calculateSpellbookOrder(spellbookId, spellbook);
            int fromIdx = this.findSpellIdxForComponent(spellbook, order, draggedWidget);
            int toIdx = this.findSpellIdxForComponent(spellbook, order, draggedOnWidget);
            ItemComposition fromSpell = this.client.getItemDefinition(spellbook.getIntValue(order[fromIdx]));
            ItemComposition toSpell = this.client.getItemDefinition(spellbook.getIntValue(order[toIdx]));
            log.debug("Insert {} ({}) at {} ({}) spellbook {}", new Object[]{fromSpell.getStringValue(601), fromIdx, toSpell.getStringValue(601), toIdx, spellbookId});
            log.debug("Set {} to {}", (Object)this.client.getItemDefinition(spellbook.getIntValue(order[fromIdx])).getStringValue(601), (Object)toIdx);
            this.setPosition(spellbookId, spellbook.getIntValue(order[fromIdx]), toIdx);
            if (fromIdx < toIdx) {
                for (int i = fromIdx + 1; i <= toIdx; ++i) {
                    log.debug("Set {} to {}", (Object)this.client.getItemDefinition(spellbook.getIntValue(order[i])).getStringValue(601), (Object)(i - 1));
                    this.setPosition(spellbookId, spellbook.getIntValue(order[i]), i - 1);
                }
            } else {
                for (int i = toIdx; i < fromIdx; ++i) {
                    log.debug("Set {} to {}", (Object)this.client.getItemDefinition(spellbook.getIntValue(order[i])).getStringValue(601), (Object)(i + 1));
                    this.setPosition(spellbookId, spellbook.getIntValue(order[i]), i + 1);
                }
            }
            this.redrawSpellbook();
        }
    }

    private int findSpellIdxForComponent(EnumComposition spellbook, int[] spells, Widget c) {
        for (int i = 0; i < spells.length; ++i) {
            ItemComposition spellObj = this.client.getItemDefinition(spellbook.getIntValue(spells[i]));
            Widget w = this.client.getWidget(spellObj.getIntValue(596));
            if (w != c) continue;
            return i;
        }
        return -1;
    }

    @Subscribe
    public void onScriptCallbackEvent(ScriptCallbackEvent event) {
        if (!"spellbookSort".equals(event.getEventName())) {
            return;
        }
        this.createWarning(this.reordering);
        int[] stack = this.client.getIntStack();
        int size = this.client.getIntStackSize();
        int spellbookEnumId = stack[size - 3];
        int spellArrayId = stack[size - 2];
        int numSpells = stack[size - 1];
        EnumComposition spellbookEnum = this.client.getEnum(spellbookEnumId);
        int[] spells = this.client.getArray(spellArrayId);
        int[] newSpells = new int[numSpells];
        int numNewSpells = 0;
        for (int i2 = 0; i2 < numSpells; ++i2) {
            ItemComposition spellObj = this.client.getItemDefinition(spellbookEnum.getIntValue(spells[i2]));
            Widget w = this.client.getWidget(spellObj.getIntValue(596));
            boolean hidden = this.isHidden(spellbookEnumId, spellObj.getId());
            int widgetConfig = w.getClickMask();
            if (this.reordering) {
                if (hidden) {
                    w.setOpacity(100);
                    w.setAction(6, "Unhide");
                } else {
                    w.setOpacity(0);
                    w.setAction(6, "Hide");
                }
                newSpells[numNewSpells++] = spells[i2];
                widgetConfig |= 0x120000;
            } else {
                if (hidden) {
                    w.setHidden(true);
                } else {
                    newSpells[numNewSpells++] = spells[i2];
                    w.setOpacity(0);
                    w.setAction(6, null);
                }
                widgetConfig &= 0xFFEDFFFF;
            }
            w.setClickMask(widgetConfig);
        }
        int[] order = this.calculateSpellbookOrder(spellbookEnumId, spellbookEnum);
        int[] indices = new int[order.length];
        for (int i3 = 0; i3 < order.length; ++i3) {
            indices[order[i3]] = i3;
        }
        newSpells = Arrays.stream(newSpells, 0, numNewSpells).boxed().sorted(Comparator.comparingInt(i -> indices[i])).mapToInt(i -> i).toArray();
        System.arraycopy(newSpells, 0, spells, 0, numNewSpells);
        stack[size - 1] = numSpells = numNewSpells;
    }

    private void createWarning(boolean unlocked) {
        Widget w = this.client.getWidget(0xDA0000);
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

    private void initializeSpells(int spellbookEnum) {
        EnumComposition spellbook = this.client.getEnum(spellbookEnum);
        for (int i = 0; i < spellbook.size(); ++i) {
            int spellObjId = spellbook.getIntValue(i);
            ItemComposition spellObj = this.client.getItemDefinition(spellObjId);
            int spellComponent = spellObj.getIntValue(596);
            Widget w = this.client.getWidget(spellComponent);
            if (w == null) continue;
            Object[] opListener = w.getOnOpListener();
            w.setOnOpListener(new Object[]{e -> {
                if (e.getOp() == 7) {
                    Widget s = e.getSource();
                    int subSpellbookId = this.client.getEnum(5280).getIntValue(this.client.getVarbitValue(4070));
                    int spellbookId = this.client.getEnum(subSpellbookId).getIntValue(this.client.getVarbitValue(9730));
                    boolean hidden = this.isHidden(spellbookId, spellObjId);
                    hidden = !hidden;
                    log.debug("Changing {} to hidden: {}", (Object)s.getName(), (Object)hidden);
                    this.setHidden(spellbookId, spellObjId, hidden);
                    s.setOpacity(hidden ? 100 : 0);
                    s.setAction(6, hidden ? "Unhide" : "Hide");
                    return;
                }
                if (opListener != null) {
                    this.client.runScript(opListener);
                }
            }});
        }
    }

    private void reinitializeSpellbook() {
        Widget w = this.client.getWidget(0xDA0000);
        if (w != null && w.getOnLoadListener() != null) {
            this.client.createScriptEvent(w.getOnLoadListener()).setSource(w).run();
        }
    }

    private void redrawSpellbook() {
        Widget w = this.client.getWidget(0xDA0000);
        if (w != null && w.getOnInvTransmitListener() != null) {
            this.client.createScriptEvent(w.getOnInvTransmitListener()).setSource(w).run();
        }
    }

    private int[] calculateSpellbookOrder(int spellbookId, EnumComposition spellbook) {
        int[] spells = this.defaultSpellbookOrder(spellbook);
        int[] indices = new int[spells.length];
        for (int i2 = 0; i2 < spells.length; ++i2) {
            int pos = this.getPosition(spellbookId, spellbook.getIntValue(spells[i2]));
            indices[spells[i2]] = pos != -1 ? pos : i2;
        }
        return Arrays.stream(spells).boxed().sorted(Comparator.comparingInt(i -> indices[i])).mapToInt(i -> i).toArray();
    }

    private int[] defaultSpellbookOrder(EnumComposition spellbook) {
        return IntStream.range(0, spellbook.size()).boxed().sorted((idx1, idx2) -> {
            ItemComposition i1 = this.client.getItemDefinition(spellbook.getIntValue(idx1.intValue()));
            ItemComposition i2 = this.client.getItemDefinition(spellbook.getIntValue(idx2.intValue()));
            int l1 = i1.getIntValue(604);
            int l2 = i2.getIntValue(604);
            return Integer.compare(l1, l2);
        }).mapToInt(i -> i).toArray();
    }

    private boolean isHidden(int spellbook, int spell) {
        Boolean b = (Boolean)this.configManager.getConfiguration("spellbook", "spell_hidden_book_" + spellbook + "_" + spell, Boolean.TYPE);
        return b == Boolean.TRUE;
    }

    private void setHidden(int spellbook, int spell, boolean hidden) {
        if (hidden) {
            this.configManager.setConfiguration("spellbook", "spell_hidden_book_" + spellbook + "_" + spell, true);
        } else {
            this.configManager.unsetConfiguration("spellbook", "spell_hidden_book_" + spellbook + "_" + spell);
        }
    }

    private int getPosition(int spellbook, int spell) {
        Integer pos = (Integer)this.configManager.getConfiguration("spellbook", "spell_pos_book_" + spellbook + "_" + spell, Integer.TYPE);
        return pos == null ? -1 : pos;
    }

    private void setPosition(int spellbook, int spell, int position) {
        this.configManager.setConfiguration("spellbook", "spell_pos_book_" + spellbook + "_" + spell, position);
    }
}

