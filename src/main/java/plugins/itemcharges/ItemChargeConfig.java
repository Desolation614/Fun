/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.plugins.itemcharges;

import java.awt.Color;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;
import net.runelite.client.config.Notification;

@ConfigGroup(value="itemCharge")
public interface ItemChargeConfig
extends Config {
    public static final String GROUP = "itemCharge";
    public static final String KEY_AMULET_OF_BOUNTY = "amuletOfBounty";
    public static final String KEY_AMULET_OF_CHEMISTRY = "amuletOfChemistry";
    public static final String KEY_BINDING_NECKLACE = "bindingNecklace";
    public static final String KEY_BRACELET_OF_SLAUGHTER = "braceletOfSlaughter";
    public static final String KEY_CHRONICLE = "chronicle";
    public static final String KEY_DODGY_NECKLACE = "dodgyNecklace";
    public static final String KEY_EXPEDITIOUS_BRACELET = "expeditiousBracelet";
    public static final String KEY_EXPLORERS_RING = "explorerRing";
    public static final String KEY_RING_OF_FORGING = "ringOfForging";
    public static final String KEY_BLOOD_ESSENCE = "bloodEssence";
    public static final String KEY_BRACELET_OF_CLAY = "braceletOfClay";
    @ConfigSection(name="Charge settings", description="Configuration for which charges should be displayed.", position=98)
    public static final String chargesSection = "charges";
    @ConfigSection(name="Notification settings", description="Configuration for notifications.", position=99)
    public static final String notificationSection = "notifications";

    @ConfigItem(keyName="veryLowWarningColor", name="Very low warning", description="The color of the overlay when charges are very low.", position=1)
    default public Color veryLowWarningColor() {
        return Color.RED;
    }

    @ConfigItem(keyName="lowWarningColor", name="Low warning", description="The color of the overlay when charges are low.", position=2)
    default public Color lowWarningolor() {
        return Color.YELLOW;
    }

    @ConfigItem(keyName="veryLowWarning", name="Very low warning", description="The charge count for the very low warning color.", position=3)
    default public int veryLowWarning() {
        return 1;
    }

    @ConfigItem(keyName="lowWarning", name="Low warning", description="The charge count for the low warning color.", position=4)
    default public int lowWarning() {
        return 2;
    }

    @ConfigItem(keyName="showTeleportCharges", name="Teleport charges", description="Show teleport item charge counts.", position=5, section="charges")
    default public boolean showTeleportCharges() {
        return true;
    }

    @ConfigItem(keyName="showDodgyCount", name="Dodgy necklace count", description="Show dodgy necklace charges.", position=6, section="charges")
    default public boolean showDodgyCount() {
        return true;
    }

    @ConfigItem(keyName="dodgyNotification", name="Dodgy necklace notification", description="Send a notification when a dodgy necklace breaks.", position=7, section="notifications")
    default public Notification dodgyNotification() {
        return Notification.ON;
    }

    @ConfigItem(keyName="showImpCharges", name="Imp-in-a-box charges", description="Show imp-in-a-box item charges.", position=8, section="charges")
    default public boolean showImpCharges() {
        return true;
    }

    @ConfigItem(keyName="showFungicideCharges", name="Fungicide charges", description="Show fungicide item charges.", position=9, section="charges")
    default public boolean showFungicideCharges() {
        return true;
    }

    @ConfigItem(keyName="showWateringCanCharges", name="Watering can charges", description="Show watering can item charges.", position=10, section="charges")
    default public boolean showWateringCanCharges() {
        return true;
    }

    @ConfigItem(keyName="showWaterskinCharges", name="Waterskin charges", description="Show waterskin dose counts.", position=11, section="charges")
    default public boolean showWaterskinCharges() {
        return true;
    }

    @ConfigItem(keyName="showBellowCharges", name="Bellows charges", description="Show ogre bellows item charges.", position=12, section="charges")
    default public boolean showBellowCharges() {
        return true;
    }

    @ConfigItem(keyName="showBasketCharges", name="Basket charges", description="Show fruit basket item counts.", position=13, section="charges")
    default public boolean showBasketCharges() {
        return true;
    }

    @ConfigItem(keyName="showSackCharges", name="Sack charges", description="Show sack item counts.", position=14, section="charges")
    default public boolean showSackCharges() {
        return true;
    }

    @ConfigItem(keyName="showAbyssalBraceletCharges", name="Abyssal bracelet charges", description="Show abyssal bracelet item charges.", position=15, section="charges")
    default public boolean showAbyssalBraceletCharges() {
        return true;
    }

    @ConfigItem(keyName="showAmuletOfChemistryCharges", name="Amulet of chemistry charges", description="Show amulet of chemistry item charges.", position=16, section="charges")
    default public boolean showAmuletOfChemistryCharges() {
        return true;
    }

    @ConfigItem(keyName="showAmuletOfBountyCharges", name="Amulet of bounty charges", description="Show amulet of bounty item charges.", position=17, section="charges")
    default public boolean showAmuletOfBountyCharges() {
        return true;
    }

    @ConfigItem(keyName="recoilNotification", name="Ring of recoil notification", description="Send a notification when a ring of recoil breaks.", position=18, section="notifications")
    default public Notification recoilNotification() {
        return Notification.OFF;
    }

    @ConfigItem(keyName="showBindingNecklaceCharges", name="Binding necklace charges", description="Show binding necklace item charges.", position=19, section="charges")
    default public boolean showBindingNecklaceCharges() {
        return true;
    }

    @ConfigItem(keyName="bindingNotification", name="Binding necklace notification", description="Send a notification when a binding necklace breaks.", position=20, section="notifications")
    default public Notification bindingNotification() {
        return Notification.ON;
    }

    @ConfigItem(keyName="showExplorerRingCharges", name="Explorer's ring alch charges", description="Show explorer's ring alchemy charges.", position=21, section="charges")
    default public boolean showExplorerRingCharges() {
        return true;
    }

    @ConfigItem(keyName="showRingOfForgingCount", name="Ring of forging charges", description="Show ring of forging item charges.", position=22, section="charges")
    default public boolean showRingOfForgingCount() {
        return true;
    }

    @ConfigItem(keyName="ringOfForgingNotification", name="Ring of forging notification", description="Send a notification when a ring of forging breaks.", position=23, section="notifications")
    default public Notification ringOfForgingNotification() {
        return Notification.ON;
    }

    @ConfigItem(keyName="showInfoboxes", name="Infoboxes", description="Show an infobox with remaining charges for equipped items.", position=24)
    default public boolean showInfoboxes() {
        return false;
    }

    @ConfigItem(keyName="showPotionDoseCount", name="Potion doses", description="Show remaining potion doses.", position=25, section="charges")
    default public boolean showPotionDoseCount() {
        return false;
    }

    @ConfigItem(keyName="showBraceletOfSlaughterCharges", name="Bracelet of slaughter charges", description="Show bracelet of slaughter item charges.", position=26, section="charges")
    default public boolean showBraceletOfSlaughterCharges() {
        return true;
    }

    @ConfigItem(keyName="slaughterNotification", name="Bracelet of slaughter notification", description="Send a notification when a bracelet of slaughter breaks.", position=27, section="notifications")
    default public Notification slaughterNotification() {
        return Notification.ON;
    }

    @ConfigItem(keyName="showExpeditiousBraceletCharges", name="Expeditious bracelet charges", description="Show expeditious bracelet item charges.", position=28, section="charges")
    default public boolean showExpeditiousBraceletCharges() {
        return true;
    }

    @ConfigItem(keyName="expeditiousNotification", name="Expeditious bracelet notification", description="Send a notification when an expeditious bracelet breaks.", position=29, section="notifications")
    default public Notification expeditiousNotification() {
        return Notification.ON;
    }

    @ConfigItem(keyName="showGuthixRestDoses", name="Guthix rest doses", description="Show guthix rest doses.", position=29, section="charges")
    default public boolean showGuthixRestDoses() {
        return true;
    }

    @ConfigItem(keyName="showBloodEssenceCharges", name="Blood essence charges", description="Show blood essence charges.", position=30, section="charges")
    default public boolean showBloodEssenceCharges() {
        return true;
    }

    @ConfigItem(keyName="showBraceletOfClayCharges", name="Bracelet of clay charges", description="Show bracelet of clay item charges.", position=31, section="charges")
    default public boolean showBraceletOfClayCharges() {
        return true;
    }

    @ConfigItem(keyName="braceletOfClayNotification", name="Bracelet of clay notification", description="Send a notification when a bracelet of clay breaks.", position=32, section="notifications")
    default public Notification braceletOfClayNotification() {
        return Notification.ON;
    }

    @ConfigItem(keyName="amuletOfChemistryNotification", name="Amulet of chemistry notification", description="Send a notification when an amulet of chemistry breaks.", position=33, section="notifications")
    default public Notification amuletOfChemistryNotification() {
        return Notification.ON;
    }
}

