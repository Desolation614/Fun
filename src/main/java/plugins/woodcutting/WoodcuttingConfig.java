/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.plugins.woodcutting;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;
import net.runelite.client.config.Notification;
import net.runelite.client.config.Units;
import net.runelite.client.plugins.woodcutting.config.ClueNestTier;

@ConfigGroup(value="woodcutting")
public interface WoodcuttingConfig
extends Config {
    @ConfigSection(name="Forestry", description="Configuration for forestry.", position=10)
    public static final String forestrySection = "forestry";

    @ConfigItem(position=1, keyName="statTimeout", name="Reset stats", description="Configures the time until statistic is reset. Also configures when tree indicator is hidden.")
    @Units(value=" mins")
    default public int statTimeout() {
        return 5;
    }

    @ConfigItem(position=2, keyName="showNestNotification", name="Bird nest notification", description="Configures whether to notify you of a bird nest spawn.")
    default public Notification showNestNotification() {
        return Notification.ON;
    }

    @ConfigItem(position=3, keyName="clueNestNotifyTier", name="Clue nest notification", description="Configures the clue tier from which to start notifying of a clue nest spawn.")
    default public ClueNestTier clueNestNotifyTier() {
        return ClueNestTier.BEGINNER;
    }

    @ConfigItem(position=4, keyName="showWoodcuttingStats", name="Show session stats", description="Configures whether to display woodcutting session stats.")
    default public boolean showWoodcuttingStats() {
        return true;
    }

    @ConfigItem(position=5, keyName="showRedwoods", name="Show Redwood trees", description="Configures whether to show a indicator for redwood trees.")
    default public boolean showRedwoodTrees() {
        return true;
    }

    @ConfigItem(position=6, keyName="showRespawnTimers", name="Show respawn timers", description="Configures whether to display the respawn timer overlay.")
    default public boolean showRespawnTimers() {
        return true;
    }

    @ConfigItem(position=10, keyName="forestryLeprechaunNotification", name="Leprechaun notification", description="Configures whether to notify you of a leprechaun event.", section="forestry")
    default public Notification forestryLeprechaunNotification() {
        return Notification.ON;
    }

    @ConfigItem(position=11, keyName="forestryRisingRootsNotification", name="Rising roots notification", description="Configures whether to notify you of a rising roots event.", section="forestry")
    default public Notification forestryRisingRootsNotification() {
        return Notification.ON;
    }

    @ConfigItem(position=12, keyName="forestryStrugglingSaplingNotification", name="Struggling sapling notification", description="Configures whether to notify you of a struggling sapling event.", section="forestry")
    default public Notification forestryStrugglingSaplingNotification() {
        return Notification.ON;
    }

    @ConfigItem(position=13, keyName="forestryFloweringTreeNotification", name="Flowering tree notification", description="Configures whether to notify you of a flowering tree event.", section="forestry")
    default public Notification forestryFloweringTreeNotification() {
        return Notification.ON;
    }

    @ConfigItem(position=14, keyName="forestryPoachersNotification", name="Poachers notification", description="Configures whether to notify you of a poachers event.", section="forestry")
    default public Notification forestryPoachersNotification() {
        return Notification.ON;
    }

    @ConfigItem(position=15, keyName="forestryPheasantControlNotification", name="Pheasant control notification", description="Configures whether to notify you of a pheasant control event.", section="forestry")
    default public Notification forestryPheasantControlNotification() {
        return Notification.ON;
    }

    @ConfigItem(position=16, keyName="forestryBeeHiveNotification", name="Bee hive notification", description="Configures whether to notify you of a bee hive event.", section="forestry")
    default public Notification forestryBeeHiveNotification() {
        return Notification.ON;
    }

    @ConfigItem(position=17, keyName="forestryEnchantmentRitualNotification", name="Enchantment ritual notification", description="Configures whether to notify you of an enchantment ritual event.", section="forestry")
    default public Notification forestryEnchantmentRitualNotification() {
        return Notification.ON;
    }

    @ConfigItem(position=18, keyName="forestryFriendlyEntNotification", name="Friendly ent notification", description="Configures whether to notify you of a friendly ent event.", section="forestry")
    default public Notification forestryFriendlyEntNotification() {
        return Notification.ON;
    }

    @ConfigItem(position=20, keyName="highlightGlowingRoots", name="Highlight glowing roots", description="Highlights glowing roots during rising roots events.", section="forestry")
    default public boolean highlightGlowingRoots() {
        return true;
    }

    @ConfigItem(position=21, keyName="highlightMulch", name="Highlight mulch ingredients", description="Highlights mulch ingredients during struggling sapling events.", section="forestry")
    default public boolean highlightMulch() {
        return true;
    }

    @ConfigItem(position=22, keyName="highlightFlowers", name="Highlight flowers", description="Highlights flowers during flowering tree events.", section="forestry")
    default public boolean highlightFlowers() {
        return true;
    }

    @ConfigItem(position=23, keyName="highlightFoxTrap", name="Highlight fox trap", description="Highlights the fox trap during poachers events.", section="forestry")
    default public boolean highlightFoxTrap() {
        return true;
    }

    @ConfigItem(position=24, keyName="highlightPheasantNest", name="Highlight pheasant nest", description="Highlights the pheasant next during pheasant control events.", section="forestry")
    default public boolean highlightPheasantNest() {
        return true;
    }

    @ConfigItem(position=25, keyName="highlightBeeHive", name="Highlight bee hive", description="Highlights the bee hive during bee hive events.", section="forestry")
    default public boolean highlightBeeHive() {
        return true;
    }

    @ConfigItem(position=26, keyName="highlightRitualCircle", name="Highlight ritual circle", description="Highlights the ritual circle during enchantment ritual events.", section="forestry")
    default public boolean highlightRitualCircle() {
        return true;
    }

    @ConfigItem(position=27, keyName="highlightLeprechaunRainbow", name="Highlight leprechaun rainbow", description="Highlights the end of rainbow during leprechaun events.", section="forestry")
    default public boolean highlightLeprechaunRainbow() {
        return true;
    }

    @ConfigItem(position=28, keyName="showLeprechaunLuck", name="Show leprechaun's luck", description="Shows an infobox with leprechaun's luck.", section="forestry")
    default public boolean showLeprechaunLuck() {
        return true;
    }
}

