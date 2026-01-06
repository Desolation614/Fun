/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableSet
 *  com.google.inject.Binder
 *  com.google.inject.Inject
 *  com.google.inject.Provides
 *  net.runelite.api.Client
 *  net.runelite.api.ItemContainer
 *  net.runelite.api.events.GameTick
 *  net.runelite.api.events.ScriptPostFired
 *  net.runelite.api.events.VarbitChanged
 *  net.runelite.api.widgets.Widget
 */
package net.runelite.client.plugins.itemstats;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Binder;
import com.google.inject.Inject;
import com.google.inject.Provides;
import java.awt.FontMetrics;
import java.util.Map;
import net.runelite.api.Client;
import net.runelite.api.ItemContainer;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.ScriptPostFired;
import net.runelite.api.events.VarbitChanged;
import net.runelite.api.widgets.Widget;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.game.ItemEquipmentStats;
import net.runelite.client.game.ItemManager;
import net.runelite.client.game.ItemStats;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.itemstats.ItemStatChangesService;
import net.runelite.client.plugins.itemstats.ItemStatChangesServiceImpl;
import net.runelite.client.plugins.itemstats.ItemStatConfig;
import net.runelite.client.plugins.itemstats.ItemStatOverlay;
import net.runelite.client.ui.FontManager;
import net.runelite.client.ui.JagexColors;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.util.QuantityFormatter;

@PluginDescriptor(name="Item Stats", description="Show information about food and potion effects", tags={"food", "inventory", "overlay", "potion"})
public class ItemStatPlugin
extends Plugin {
    private static final int ORANGE_TEXT = JagexColors.DARK_ORANGE_INTERFACE_TEXT.getRGB();
    private static final int YELLOW_TEXT = JagexColors.YELLOW_INTERFACE_TEXT.getRGB();
    private static final int TEXT_HEIGHT = 11;
    @Inject
    private OverlayManager overlayManager;
    @Inject
    private ItemStatOverlay overlay;
    @Inject
    private Client client;
    @Inject
    private ItemManager itemManager;
    @Inject
    private ItemStatConfig config;
    @Inject
    private ClientThread clientThread;
    private Widget itemInformationTitle;

    @Provides
    ItemStatConfig getConfig(ConfigManager configManager) {
        return configManager.getConfig(ItemStatConfig.class);
    }

    @Override
    public void configure(Binder binder) {
        binder.bind(ItemStatChangesService.class).to(ItemStatChangesServiceImpl.class);
    }

    @Override
    protected void startUp() throws Exception {
        this.overlayManager.add(this.overlay);
    }

    @Override
    protected void shutDown() throws Exception {
        this.overlayManager.remove(this.overlay);
        this.clientThread.invokeLater(this::resetGEInventory);
    }

    @Subscribe
    public void onConfigChanged(ConfigChanged event) {
        if (event.getKey().equals("geStats")) {
            this.clientThread.invokeLater(this::resetGEInventory);
        }
    }

    @Subscribe
    public void onGameTick(GameTick event) {
        if (this.itemInformationTitle != null && this.config.geStats() && (this.client.getWidget(0x1D10000) == null || this.client.getWidget(0x1D10000).isHidden())) {
            this.resetGEInventory();
        }
    }

    @Subscribe
    public void onVarbitChanged(VarbitChanged event) {
        if (event.getVarpId() == 1151 && this.config.geStats()) {
            this.resetGEInventory();
        }
    }

    @Subscribe
    public void onScriptPostFired(ScriptPostFired event) {
        int currentGeItem;
        if (event.getScriptId() == 779 && this.config.geStats() && (currentGeItem = this.client.getVarpValue(1151)) != -1 && this.client.getVarbitValue(4397) == 0) {
            this.createItemInformation(currentGeItem);
        }
    }

    private void createItemInformation(int id) {
        ItemStats itemStats = this.itemManager.getItemStats(id);
        if (itemStats == null || !itemStats.isEquipable()) {
            return;
        }
        ItemEquipmentStats equipmentStats = itemStats.getEquipment();
        if (equipmentStats == null) {
            return;
        }
        Widget geInv = this.client.getWidget(30605312);
        if (geInv == null) {
            return;
        }
        Widget invContainer = this.getInventoryContainer();
        if (invContainer == null) {
            return;
        }
        invContainer.deleteAllChildren();
        geInv.setHidden(true);
        int yPos = 0;
        FontMetrics smallFM = this.client.getCanvas().getFontMetrics(FontManager.getRunescapeSmallFont());
        this.itemInformationTitle = ItemStatPlugin.createText(invContainer, "Item Information", 496, ORANGE_TEXT, 8, 8, invContainer.getWidth(), 16);
        this.itemInformationTitle.setYTextAlignment(1);
        Widget closeButton = invContainer.createChild(-1, 5);
        closeButton.setOriginalY(8);
        closeButton.setOriginalX(invContainer.getWidth() - 24);
        closeButton.setOriginalHeight(16);
        closeButton.setOriginalWidth(16);
        closeButton.setSpriteId(831);
        closeButton.setAction(0, "Close");
        closeButton.setOnMouseOverListener(new Object[]{ev -> closeButton.setSpriteId(832)});
        closeButton.setOnMouseLeaveListener(new Object[]{ev -> closeButton.setSpriteId(831)});
        closeButton.setOnOpListener(new Object[]{ev -> this.resetGEInventory()});
        closeButton.setHasListener(true);
        closeButton.revalidate();
        ItemStatPlugin.createSeparator(invContainer, yPos += 15);
        Widget icon = invContainer.createChild(-1, 5);
        icon.setOriginalX(8);
        icon.setOriginalY(yPos += 25);
        icon.setOriginalWidth(36);
        icon.setOriginalHeight(32);
        icon.setItemId(id);
        icon.setItemQuantityMode(0);
        icon.setBorderType(1);
        icon.revalidate();
        Widget itemName = ItemStatPlugin.createText(invContainer, this.itemManager.getItemComposition(id).getName(), 495, ORANGE_TEXT, 50, yPos, invContainer.getWidth() - 40, 30);
        itemName.setYTextAlignment(1);
        ItemStatPlugin.createSeparator(invContainer, yPos += 20);
        ItemStatPlugin.createText(invContainer, "Attack", 494, ORANGE_TEXT, 5, yPos += 25, 50, -1);
        int defenceXPos = invContainer.getWidth() - (smallFM.stringWidth("Defence") + 5);
        ItemStatPlugin.createText(invContainer, "Defence", 494, ORANGE_TEXT, defenceXPos, yPos, 50, -1);
        ImmutableSet stats = ImmutableSet.of((Object)"Stab", (Object)"Slash", (Object)"Crush", (Object)"Magic", (Object)"Ranged");
        ImmutableList attackStats = ImmutableList.of((Object)equipmentStats.getAstab(), (Object)equipmentStats.getAslash(), (Object)equipmentStats.getAcrush(), (Object)equipmentStats.getAmagic(), (Object)equipmentStats.getArange());
        ImmutableList defenceStats = ImmutableList.of((Object)equipmentStats.getDstab(), (Object)equipmentStats.getDslash(), (Object)equipmentStats.getDcrush(), (Object)equipmentStats.getDmagic(), (Object)equipmentStats.getDrange());
        int index = 0;
        for (Object stat : stats) {
            Widget widget = ItemStatPlugin.createText(invContainer, (String)stat, 494, ORANGE_TEXT, 0, yPos += 13, invContainer.getWidth(), -1);
            widget.setXTextAlignment(1);
            ItemStatPlugin.createText(invContainer, ((Integer)attackStats.get(index)).toString(), 494, YELLOW_TEXT, 5, yPos, 50, -1);
            int defenceX = invContainer.getWidth() - (smallFM.stringWidth(((Integer)defenceStats.get(index)).toString()) + 5);
            ItemStatPlugin.createText(invContainer, ((Integer)defenceStats.get(index)).toString(), 494, YELLOW_TEXT, defenceX, yPos, 50, -1);
            ++index;
        }
        yPos += 19;
        ImmutableMap miscStats = ImmutableMap.of((Object)"Strength", (Object)equipmentStats.getStr(), (Object)"Ranged Strength", (Object)equipmentStats.getRstr(), (Object)"Magic Damage", (Object)Float.valueOf(equipmentStats.getMdmg()), (Object)"Prayer Bonus", (Object)equipmentStats.getPrayer());
        for (Map.Entry entry : miscStats.entrySet()) {
            String name = (String)entry.getKey();
            String value = entry.getValue().toString();
            ItemStatPlugin.createText(invContainer, name, 494, ORANGE_TEXT, 5, yPos, 50, -1);
            int valueXPos = invContainer.getWidth() - (smallFM.stringWidth(value) + 5);
            ItemStatPlugin.createText(invContainer, value, 494, YELLOW_TEXT, valueXPos, yPos, 50, -1);
            yPos += 13;
        }
        ItemStatPlugin.createSeparator(invContainer, invContainer.getHeight() - 40);
        String coinText = "You have " + QuantityFormatter.quantityToStackSize(this.getCurrentGP()) + (this.getCurrentGP() == 1 ? " coin." : " coins.");
        Widget widget = ItemStatPlugin.createText(invContainer, coinText, 495, ORANGE_TEXT, 0, invContainer.getHeight() - 18, invContainer.getWidth(), -1);
        widget.setXTextAlignment(1);
    }

    private static Widget createText(Widget parent, String text, int fontId, int textColor, int x, int y, int width, int height) {
        Widget widget = parent.createChild(-1, 4);
        widget.setText(text);
        widget.setFontId(fontId);
        widget.setTextColor(textColor);
        widget.setTextShadowed(true);
        widget.setOriginalHeight(height == -1 ? 11 : height);
        widget.setOriginalWidth(width);
        widget.setOriginalY(y);
        widget.setOriginalX(x);
        widget.revalidate();
        return widget;
    }

    private static void createSeparator(Widget parent, int y) {
        Widget separator = parent.createChild(-1, 5);
        separator.setOriginalWidth(parent.getWidth());
        separator.setOriginalY(y);
        separator.setOriginalHeight(32);
        separator.setSpriteId(995);
        separator.revalidate();
    }

    private void resetGEInventory() {
        Widget geInv;
        Widget invContainer = this.getInventoryContainer();
        if (invContainer == null) {
            return;
        }
        if (this.itemInformationTitle != null && invContainer.getChild(0) == this.itemInformationTitle) {
            invContainer.deleteAllChildren();
            this.itemInformationTitle = null;
        }
        if ((geInv = this.client.getWidget(30605312)) != null) {
            geInv.setHidden(false);
        }
    }

    private int getCurrentGP() {
        ItemContainer inventory = this.client.getItemContainer(93);
        if (inventory == null) {
            return 0;
        }
        return inventory.count(995);
    }

    private Widget getInventoryContainer() {
        if (this.client.isResized()) {
            if (this.client.getVarbitValue(4607) == 1) {
                return this.client.getWidget(10747980);
            }
            return this.client.getWidget(10551375);
        }
        return this.client.getWidget(35913811);
    }
}

