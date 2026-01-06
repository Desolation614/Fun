/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.plugins.worldmap;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup(value="worldmap")
public interface WorldMapConfig
extends Config {
    @ConfigItem(keyName="fairyRingTooltips", name="Fairy ring code tooltip", description="Display the code for fairy rings in the icon tooltip.", position=1)
    default public boolean fairyRingTooltips() {
        return true;
    }

    @ConfigItem(keyName="fairyRingIcon", name="Fairy ring travel icon", description="Override the travel icon for fairy rings.", position=2)
    default public boolean fairyRingIcon() {
        return true;
    }

    @ConfigItem(keyName="agilityShortcutTooltips", name="Agility level requirement", description="Display the required agility level in the icon tooltip.", position=3)
    default public boolean agilityShortcutTooltips() {
        return true;
    }

    @ConfigItem(keyName="agilityShortcutIcon", name="Indicate inaccessible shortcuts", description="Indicate shortcuts you do not have the level to use on the icon.", position=4)
    default public boolean agilityShortcutLevelIcon() {
        return true;
    }

    @ConfigItem(keyName="agilityCourseTooltips", name="Agility course tooltip", description="Displays the name of the agility course in the tooltip.", position=5)
    default public boolean agilityCourseTooltip() {
        return true;
    }

    @ConfigItem(keyName="agilityCourseRooftopIcon", name="Indicate rooftop courses", description="Replace the agility icon with a mark of grace for rooftop courses.", position=6)
    default public boolean agilityCourseRooftop() {
        return true;
    }

    @ConfigItem(keyName="standardSpellbookIcon", name="Standard spellbook destinations", description="Show icons at the destinations for teleports in the standard spellbook.", position=7)
    default public boolean normalTeleportIcon() {
        return true;
    }

    @ConfigItem(keyName="minigameTooltip", name="Minigame names", description="Display the name of the minigame in the icon tooltip.", position=8)
    default public boolean minigameTooltip() {
        return true;
    }

    @ConfigItem(keyName="ancientSpellbookIcon", name="Ancient magicks destinations", description="Show icons at the destinations for teleports in the ancient spellbook.", position=9)
    default public boolean ancientTeleportIcon() {
        return true;
    }

    @ConfigItem(keyName="lunarSpellbookIcon", name="Lunar spellbook destinations", description="Show icons at the destinations for teleports in the lunar spellbook.", position=10)
    default public boolean lunarTeleportIcon() {
        return true;
    }

    @ConfigItem(keyName="arceuusSpellbookIcon", name="Arceuus spellbook destinations", description="Show icons at the destinations for teleports in the Arceuus spellbook.", position=11)
    default public boolean arceuusTeleportIcon() {
        return true;
    }

    @ConfigItem(keyName="jewelleryIcon", name="Jewellery teleport destinations", description="Show icons at the destinations for teleports from jewellery.", position=12)
    default public boolean jewelleryTeleportIcon() {
        return true;
    }

    @ConfigItem(keyName="scrollIcon", name="Teleport scroll destinations", description="Show icons at the destinations for teleports from scrolls.", position=13)
    default public boolean scrollTeleportIcon() {
        return true;
    }

    @ConfigItem(keyName="miscellaneousTeleportIcon", name="Misc teleport destinations", description="Show icons at the destinations for miscellaneous teleport items.", position=14)
    default public boolean miscellaneousTeleportIcon() {
        return true;
    }

    @ConfigItem(keyName="questStartTooltips", name="Quest status icons", description="Shows completion status of quests on the quest's icon.", position=15)
    default public boolean questStartTooltips() {
        return true;
    }

    @ConfigItem(keyName="farmingpatchTooltips", name="Farming patch type", description="Display the type of farming patches in the icon tooltip.", position=16)
    default public boolean farmingPatchTooltips() {
        return true;
    }

    @ConfigItem(keyName="rareTreeTooltips", name="Rare tree type", description="Display the type of rare tree in the icon tooltip.", position=17)
    default public boolean rareTreeTooltips() {
        return true;
    }

    @ConfigItem(keyName="rareTreeIcon", name="Indicate unavailable trees", description="Indicate rare trees you do not have the level to cut on the icon.", position=18)
    default public boolean rareTreeLevelIcon() {
        return true;
    }

    @ConfigItem(keyName="transportationTooltips", name="Transportation tooltips", description="Indicates types and destinations of transportation.", position=19)
    default public boolean transportationTeleportTooltips() {
        return true;
    }

    @ConfigItem(keyName="runecraftingAltarIcon", name="Runecrafting altar locations", description="Show the icons of runecrafting altars.", position=20)
    default public boolean runecraftingAltarIcon() {
        return true;
    }

    @ConfigItem(keyName="miningSiteTooltips", name="Mining site tooltips", description="Indicates the ore available at mining sites.", position=21)
    default public boolean miningSiteTooltips() {
        return true;
    }

    @ConfigItem(keyName="dungeonTooltips", name="Dungeon tooltips", description="Indicates the names of dungeons.", position=22)
    default public boolean dungeonTooltips() {
        return true;
    }

    @ConfigItem(keyName="hunterAreaTooltips", name="Hunter area tooltips", description="Indicates the creatures inside a hunting area.", position=23)
    default public boolean hunterAreaTooltips() {
        return true;
    }

    @ConfigItem(keyName="fishingSpotTooltips", name="Fishing spot tooltips", description="Indicates the type of fish fishable at the fishing spot.", position=24)
    default public boolean fishingSpotTooltips() {
        return true;
    }
}

