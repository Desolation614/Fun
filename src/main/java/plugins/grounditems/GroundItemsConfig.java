/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.plugins.grounditems;

import java.awt.Color;
import net.runelite.client.config.Alpha;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;
import net.runelite.client.config.Keybind;
import net.runelite.client.config.Units;
import net.runelite.client.plugins.grounditems.Lootbeam;
import net.runelite.client.plugins.grounditems.config.DespawnTimerMode;
import net.runelite.client.plugins.grounditems.config.HighlightTier;
import net.runelite.client.plugins.grounditems.config.ItemHighlightMode;
import net.runelite.client.plugins.grounditems.config.MenuHighlightMode;
import net.runelite.client.plugins.grounditems.config.OwnershipFilterMode;
import net.runelite.client.plugins.grounditems.config.PriceDisplayMode;
import net.runelite.client.plugins.grounditems.config.ValueCalculationMode;

@ConfigGroup(value="grounditems")
public interface GroundItemsConfig
extends Config {
    public static final String GROUP = "grounditems";
    public static final String OWNERSHIP_FILTER_MODE = "ownershipFilterMode";
    @ConfigSection(name="Item lists", description="The highlighted and hidden item lists.", position=0)
    public static final String itemLists = "itemLists";

    @ConfigItem(keyName="highlightedItems", name="Highlighted items", description="Configures specifically highlighted ground items. Format: item1, item2, item3", position=0, section="itemLists")
    default public String getHighlightItems() {
        return "";
    }

    @ConfigItem(keyName="highlightedItems", name="", description="")
    public void setHighlightedItem(String var1);

    @ConfigItem(keyName="hiddenItems", name="Hidden items", description="Configures hidden ground items. Format: item1, item2, item3", position=1, section="itemLists")
    default public String getHiddenItems() {
        return "Vial, Ashes, Coins, Bones, Bucket, Jug, Seaweed";
    }

    @ConfigItem(keyName="hiddenItems", name="", description="")
    public void setHiddenItems(String var1);

    @ConfigItem(keyName="showHighlightedOnly", name="Show highlighted items only", description="Configures whether or not to draw items only on your highlighted list.", position=2)
    default public boolean showHighlightedOnly() {
        return false;
    }

    @ConfigItem(keyName="dontHideUntradeables", name="Do not hide untradeables", description="Configures whether or not untradeable items ignore hiding under settings.", position=3)
    default public boolean dontHideUntradeables() {
        return true;
    }

    @ConfigItem(keyName="showMenuItemQuantities", name="Show menu item quantities", description="Configures whether or not to show the item quantities in the menu.", position=4)
    default public boolean showMenuItemQuantities() {
        return true;
    }

    @ConfigItem(keyName="recolorMenuHiddenItems", name="Recolor menu hidden items", description="Configures whether or not hidden items in right-click menu will be recolored.", position=5)
    default public boolean recolorMenuHiddenItems() {
        return false;
    }

    @ConfigItem(keyName="deprioritizeHiddenItems", name="Deprioritize menu hidden items", description="Depriotizies the menu options for items which are hidden, requiring a right-click to pick up.", position=5)
    default public boolean deprioritizeHiddenItems() {
        return false;
    }

    @ConfigItem(keyName="highlightTiles", name="Highlight tiles", description="Configures whether or not to highlight tiles containing ground items.", position=6)
    default public boolean highlightTiles() {
        return false;
    }

    @ConfigItem(keyName="notifyHighlightedDrops", name="Notify for highlighted drops", description="Configures whether or not to notify for drops on your highlighted list.", position=7)
    default public boolean notifyHighlightedDrops() {
        return false;
    }

    @ConfigItem(keyName="notifyTier", name="Notify tier", description="Configures which price tiers will trigger a notification on drop.", position=8)
    default public HighlightTier notifyTier() {
        return HighlightTier.OFF;
    }

    @ConfigItem(keyName="priceDisplayMode", name="Price display mode", description="Configures which price types are shown alongside ground item name.", position=9)
    default public PriceDisplayMode priceDisplayMode() {
        return PriceDisplayMode.BOTH;
    }

    @ConfigItem(keyName="itemHighlightMode", name="Item highlight mode", description="Configures how ground items will be highlighted.", position=10)
    default public ItemHighlightMode itemHighlightMode() {
        return ItemHighlightMode.BOTH;
    }

    @ConfigItem(keyName="menuHighlightMode", name="Menu highlight mode", description="Configures what to highlight in right-click menu.", position=11)
    default public MenuHighlightMode menuHighlightMode() {
        return MenuHighlightMode.NAME;
    }

    @ConfigItem(keyName="highlightValueCalculation", name="Highlight value calculation", description="Configures which coin value is used to determine highlight color.", position=12)
    default public ValueCalculationMode valueCalculationMode() {
        return ValueCalculationMode.HIGHEST;
    }

    @ConfigItem(keyName="hideUnderValue", name="Hide under value", description="Configures hidden ground items under both GE and HA value.", position=13)
    default public int getHideUnderValue() {
        return 0;
    }

    @Alpha
    @ConfigItem(keyName="defaultColor", name="Default items", description="Configures the color for default, non-highlighted items.", position=14)
    default public Color defaultColor() {
        return Color.WHITE;
    }

    @Alpha
    @ConfigItem(keyName="highlightedColor", name="Highlighted items", description="Configures the color for highlighted items.", position=15)
    default public Color highlightedColor() {
        return Color.decode("#AA00FF");
    }

    @Alpha
    @ConfigItem(keyName="hiddenColor", name="Hidden items", description="Configures the color for hidden items in right-click menu and when holding ALT.", position=16)
    default public Color hiddenColor() {
        return Color.GRAY;
    }

    @Alpha
    @ConfigItem(keyName="lowValueColor", name="Low value items", description="Configures the color for low value items.", position=17)
    default public Color lowValueColor() {
        return Color.decode("#66B2FF");
    }

    @ConfigItem(keyName="lowValuePrice", name="Low value price", description="Configures the start price for low value items.", position=18)
    default public int lowValuePrice() {
        return 20000;
    }

    @Alpha
    @ConfigItem(keyName="mediumValueColor", name="Medium value items", description="Configures the color for medium value items.", position=19)
    default public Color mediumValueColor() {
        return Color.decode("#99FF99");
    }

    @ConfigItem(keyName="mediumValuePrice", name="Medium value price", description="Configures the start price for medium value items.", position=20)
    default public int mediumValuePrice() {
        return 100000;
    }

    @Alpha
    @ConfigItem(keyName="highValueColor", name="High value items", description="Configures the color for high value items.", position=21)
    default public Color highValueColor() {
        return Color.decode("#FF9600");
    }

    @ConfigItem(keyName="highValuePrice", name="High value price", description="Configures the start price for high value items.", position=22)
    default public int highValuePrice() {
        return 1000000;
    }

    @Alpha
    @ConfigItem(keyName="insaneValueColor", name="Insane value items", description="Configures the color for insane value items.", position=23)
    default public Color insaneValueColor() {
        return Color.decode("#FF66B2");
    }

    @ConfigItem(keyName="insaneValuePrice", name="Insane value price", description="Configures the start price for insane value items.", position=24)
    default public int insaneValuePrice() {
        return 10000000;
    }

    @ConfigItem(keyName="ownershipFilterMode", name="Ownership filter", description="Show all items, takeable items, or only your or your group's drops.", position=25)
    default public OwnershipFilterMode ownershipFilterMode() {
        return OwnershipFilterMode.ALL;
    }

    @ConfigItem(keyName="doubleTapDelay", name="Double-tap delay", description="Delay for the double-tap ALT to hide ground items. 0 to disable.", position=26)
    @Units(value="ms")
    default public int doubleTapDelay() {
        return 250;
    }

    @ConfigItem(keyName="collapseEntries", name="Collapse ground item menu", description="Collapses ground item menu entries together and appends count.", position=27)
    default public boolean collapseEntries() {
        return false;
    }

    @ConfigItem(keyName="groundItemTimers", name="Despawn timer", description="Shows despawn timers for items you've dropped and received as loot.", position=28)
    default public DespawnTimerMode groundItemTimers() {
        return DespawnTimerMode.OFF;
    }

    @ConfigItem(keyName="textOutline", name="Text outline", description="Use an outline around text instead of a text shadow.", position=29)
    default public boolean textOutline() {
        return false;
    }

    @ConfigItem(keyName="showLootbeamForHighlighted", name="Highlighted item lootbeams", description="Configures lootbeams to show for all highlighted items.", position=30)
    default public boolean showLootbeamForHighlighted() {
        return false;
    }

    @ConfigItem(keyName="showLootbeamTier", name="Lootbeam tier", description="Configures which price tiers will trigger a lootbeam.", position=31)
    default public HighlightTier showLootbeamTier() {
        return HighlightTier.HIGH;
    }

    @ConfigItem(keyName="lootbeamStyle", name="Lootbeam style", description="Style of lootbeam to use.", position=32)
    default public Lootbeam.Style lootbeamStyle() {
        return Lootbeam.Style.MODERN;
    }

    @ConfigItem(keyName="hotkey", name="Hotkey", description="Configures the hotkey used by the Ground Items plugin.", position=33)
    default public Keybind hotkey() {
        return Keybind.ALT;
    }
}

