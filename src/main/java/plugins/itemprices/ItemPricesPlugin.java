/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.inject.Provides
 *  javax.inject.Inject
 *  net.runelite.api.Client
 *  net.runelite.api.ItemComposition
 *  net.runelite.api.MenuAction
 *  net.runelite.api.MenuEntry
 *  net.runelite.api.events.BeforeRender
 *  net.runelite.api.widgets.Widget
 *  net.runelite.api.widgets.WidgetUtil
 */
package net.runelite.client.plugins.itemprices;

import com.google.inject.Provides;
import java.awt.Color;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.ItemComposition;
import net.runelite.api.MenuAction;
import net.runelite.api.MenuEntry;
import net.runelite.api.events.BeforeRender;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetUtil;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.itemprices.ItemPricesConfig;
import net.runelite.client.ui.overlay.tooltip.Tooltip;
import net.runelite.client.ui.overlay.tooltip.TooltipManager;
import net.runelite.client.util.ColorUtil;
import net.runelite.client.util.QuantityFormatter;

@PluginDescriptor(name="Item Prices", description="Show prices on hover for items in your inventory and bank", tags={"bank", "inventory", "overlay", "high", "alchemy", "grand", "exchange", "tooltips"}, enabledByDefault=false)
public class ItemPricesPlugin
extends Plugin {
    @Inject
    private Client client;
    @Inject
    private ItemPricesConfig config;
    @Inject
    private TooltipManager tooltipManager;
    @Inject
    private ItemManager itemManager;
    private final StringBuilder itemStringBuilder = new StringBuilder();

    @Override
    protected void startUp() {
    }

    @Override
    protected void shutDown() {
    }

    @Provides
    ItemPricesConfig getConfig(ConfigManager configManager) {
        return configManager.getConfig(ItemPricesConfig.class);
    }

    @Subscribe
    public void onBeforeRender(BeforeRender event) {
        if (this.client.isMenuOpen()) {
            return;
        }
        MenuEntry[] menuEntries = this.client.getMenuEntries();
        int last = menuEntries.length - 1;
        if (last < 0) {
            return;
        }
        MenuEntry menuEntry = menuEntries[last];
        MenuAction action = menuEntry.getType();
        int widgetId = menuEntry.getParam1();
        int groupId = WidgetUtil.componentToInterface((int)widgetId);
        boolean isAlching = menuEntry.getOption().equals("Cast") && menuEntry.getTarget().contains("High Level Alchemy");
        switch (action) {
            case WIDGET_TARGET_ON_WIDGET: {
                if (menuEntry.getWidget().getId() != 0x950000) break;
            }
            case WIDGET_USE_ON_ITEM: {
                if (!this.config.showWhileAlching() || !isAlching) break;
            }
            case CC_OP: 
            case ITEM_USE: 
            case ITEM_FIRST_OPTION: 
            case ITEM_SECOND_OPTION: 
            case ITEM_THIRD_OPTION: 
            case ITEM_FOURTH_OPTION: 
            case ITEM_FIFTH_OPTION: {
                this.addTooltip(menuEntry, isAlching, groupId);
                break;
            }
            case WIDGET_TARGET: {
                if (menuEntry.getWidget().getId() != 0x950000) break;
                this.addTooltip(menuEntry, isAlching, groupId);
            }
        }
    }

    private void addTooltip(MenuEntry menuEntry, boolean isAlching, int groupId) {
        switch (groupId) {
            case 483: {
                if (!this.config.showWhileAlching()) {
                    return;
                }
            }
            case 149: 
            case 674: {
                if (!(!this.config.hideInventory() || this.config.showWhileAlching() && isAlching)) {
                    return;
                }
            }
            case 12: 
            case 15: 
            case 630: 
            case 631: {
                String text = this.makeValueTooltip(menuEntry);
                if (text == null) break;
                this.tooltipManager.add(new Tooltip(ColorUtil.prependColorTag(text, new Color(238, 238, 238))));
            }
        }
    }

    private String makeValueTooltip(MenuEntry menuEntry) {
        if (!this.config.showGEPrice() && !this.config.showHAValue()) {
            return null;
        }
        int componentId = menuEntry.getParam1();
        if (componentId == 0x950000 || componentId == 983043 || componentId == 31653895 || componentId == 41287681 || componentId == 0x2A20000 || componentId == 786445 || componentId == 41353231) {
            Widget w = menuEntry.getWidget();
            if (w == null) {
                return null;
            }
            return this.getItemStackValueText(w.getItemId(), w.getItemQuantity());
        }
        return null;
    }

    private String getItemStackValueText(int id, int qty) {
        if ((id = this.itemManager.canonicalize(id)) == 995) {
            return QuantityFormatter.formatNumber(qty) + " gp";
        }
        if (id == 13204) {
            return QuantityFormatter.formatNumber((long)qty * 1000L) + " gp";
        }
        ItemComposition itemDef = this.itemManager.getItemComposition(id);
        if (itemDef.getPrice() <= 0) {
            return null;
        }
        int gePrice = 0;
        int haPrice = 0;
        int haProfit = 0;
        int itemHaPrice = itemDef.getHaPrice();
        if (this.config.showGEPrice()) {
            gePrice = this.itemManager.getItemPrice(id);
        }
        if (this.config.showHAValue()) {
            haPrice = itemHaPrice;
        }
        if (gePrice > 0 && itemHaPrice > 0 && this.config.showAlchProfit()) {
            haProfit = this.calculateHAProfit(itemHaPrice, gePrice);
        }
        if (gePrice > 0 || haPrice > 0) {
            return this.stackValueText(qty, gePrice, haPrice, haProfit);
        }
        return null;
    }

    private String stackValueText(int qty, int gePrice, int haValue, int haProfit) {
        if (gePrice > 0) {
            this.itemStringBuilder.append("GE: ").append(QuantityFormatter.quantityToStackSize((long)gePrice * (long)qty)).append(" gp");
            if (this.config.showEA() && qty > 1) {
                this.itemStringBuilder.append(" (").append(QuantityFormatter.quantityToStackSize(gePrice)).append(" ea)");
            }
        }
        if (haValue > 0) {
            if (gePrice > 0) {
                this.itemStringBuilder.append("</br>");
            }
            this.itemStringBuilder.append("HA: ").append(QuantityFormatter.quantityToStackSize((long)haValue * (long)qty)).append(" gp");
            if (this.config.showEA() && qty > 1) {
                this.itemStringBuilder.append(" (").append(QuantityFormatter.quantityToStackSize(haValue)).append(" ea)");
            }
        }
        if (haProfit != 0) {
            Color haColor = ItemPricesPlugin.haProfitColor(haProfit);
            this.itemStringBuilder.append("</br>");
            this.itemStringBuilder.append("HA Profit: ").append(ColorUtil.wrapWithColorTag(String.valueOf((long)haProfit * (long)qty), haColor)).append(" gp");
            if (this.config.showEA() && qty > 1) {
                this.itemStringBuilder.append(" (").append(ColorUtil.wrapWithColorTag(String.valueOf(haProfit), haColor)).append(" ea)");
            }
        }
        String text = this.itemStringBuilder.toString();
        this.itemStringBuilder.setLength(0);
        return text;
    }

    private int calculateHAProfit(int haPrice, int gePrice) {
        int natureRunePrice = this.itemManager.getItemPrice(561);
        return haPrice - gePrice - natureRunePrice;
    }

    private static Color haProfitColor(int haProfit) {
        return haProfit >= 0 ? Color.GREEN : Color.RED;
    }
}

