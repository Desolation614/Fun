/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.plugins.itemidentification;

import java.awt.Color;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;
import net.runelite.client.plugins.itemidentification.ItemIdentificationMode;

@ConfigGroup(value="itemidentification")
public interface ItemIdentificationConfig
extends Config {
    @ConfigSection(name="Categories", description="The categories of items to identify.", position=99)
    public static final String identificationSection = "identification";

    @ConfigItem(keyName="identificationType", name="Identification type", position=-4, description="How much to show of the item name.")
    default public ItemIdentificationMode identificationType() {
        return ItemIdentificationMode.SHORT;
    }

    @ConfigItem(keyName="textColor", name="Color", position=-3, description="The color of the identification text.")
    default public Color textColor() {
        return Color.WHITE;
    }

    @ConfigItem(keyName="showHerbSeeds", name="Seeds (herb)", description="Show identification on herb seeds.", section="identification")
    default public boolean showHerbSeeds() {
        return true;
    }

    @ConfigItem(keyName="showAllotmentSeeds", name="Seeds (allotment)", description="Show identification on allotment seeds.", section="identification")
    default public boolean showAllotmentSeeds() {
        return false;
    }

    @ConfigItem(keyName="showFlowerSeeds", name="Seeds (flower)", description="Show identification on flower seeds.", section="identification")
    default public boolean showFlowerSeeds() {
        return false;
    }

    @ConfigItem(keyName="showFruitTreeSeeds", name="Seeds (fruit tree)", description="Show identification on fruit tree seeds.", section="identification")
    default public boolean showFruitTreeSeeds() {
        return false;
    }

    @ConfigItem(keyName="showTreeSeeds", name="Seeds (tree)", description="Show identification on tree seeds.", section="identification")
    default public boolean showTreeSeeds() {
        return false;
    }

    @ConfigItem(keyName="showSpecialSeeds", name="Seeds (special)", description="Show identification on special seeds.", section="identification")
    default public boolean showSpecialSeeds() {
        return false;
    }

    @ConfigItem(keyName="showBerrySeeds", name="Seeds (berry)", description="Show identification on berry seeds.", section="identification")
    default public boolean showBerrySeeds() {
        return false;
    }

    @ConfigItem(keyName="showHopSeeds", name="Seeds (hops)", description="Show identification on hops seeds.", section="identification")
    default public boolean showHopsSeeds() {
        return false;
    }

    @ConfigItem(keyName="showSacks", name="Sacks", description="Show identification on sacks.", section="identification")
    default public boolean showSacks() {
        return false;
    }

    @ConfigItem(keyName="showHerbs", name="Herbs", description="Show identification on herbs.", section="identification")
    default public boolean showHerbs() {
        return false;
    }

    @ConfigItem(keyName="showLogs", name="Logs", description="Show identification on logs.", section="identification")
    default public boolean showLogs() {
        return false;
    }

    @ConfigItem(keyName="showPyreLogs", name="Logs (pyre)", description="Show identification on pyre logs.", section="identification")
    default public boolean showPyreLogs() {
        return false;
    }

    @ConfigItem(keyName="showPlanks", name="Planks", description="Show identification on planks.", section="identification")
    default public boolean showPlanks() {
        return false;
    }

    @ConfigItem(keyName="showSaplings", name="Saplings", description="Show identification on saplings and seedlings.", section="identification")
    default public boolean showSaplings() {
        return true;
    }

    @ConfigItem(keyName="showComposts", name="Composts", description="Show identification on composts.", section="identification")
    default public boolean showComposts() {
        return false;
    }

    @ConfigItem(keyName="showOres", name="Ores", description="Show identification on ores.", section="identification")
    default public boolean showOres() {
        return false;
    }

    @ConfigItem(keyName="showBars", name="Bars", description="Show identification on bars.", section="identification")
    default public boolean showBars() {
        return false;
    }

    @ConfigItem(keyName="showGems", name="Gems", description="Show identification on gems.", section="identification")
    default public boolean showGems() {
        return false;
    }

    @ConfigItem(keyName="showPotions", name="Potions", description="Show identification on potions.", section="identification")
    default public boolean showPotions() {
        return false;
    }

    @ConfigItem(keyName="showButterflyMothJars", name="Butterfly & Moth jars", description="Show identification on Butterfly and Moth jars", section="identification")
    default public boolean showButterflyMothJars() {
        return false;
    }

    @ConfigItem(keyName="showImplingJars", name="Impling jars", description="Show identification on impling jars.", section="identification")
    default public boolean showImplingJars() {
        return false;
    }

    @ConfigItem(keyName="showTablets", name="Tablets", description="Show identification on tablets.", section="identification")
    default public boolean showTablets() {
        return false;
    }

    @ConfigItem(keyName="showTeleportScrolls", name="Teleport scrolls", description="Show identification on teleport scrolls.", section="identification")
    default public boolean showTeleportScrolls() {
        return false;
    }

    @ConfigItem(keyName="showJewellery", name="Jewellery (unenchanted)", description="Show identification on unenchanted jewellery.", section="identification")
    default public boolean showJewellery() {
        return false;
    }

    @ConfigItem(keyName="showEnchantedJewellery", name="Jewellery (enchanted)", description="Show identification on enchanted jewellery.", section="identification")
    default public boolean showEnchantedJewellery() {
        return false;
    }

    @ConfigItem(keyName="showWines", name="Wines", description="Show identification on jugs of wine.", section="identification")
    default public boolean showWines() {
        return false;
    }
}

