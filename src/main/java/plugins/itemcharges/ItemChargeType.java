/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.plugins.itemcharges;

import java.util.function.Predicate;
import net.runelite.client.plugins.itemcharges.ItemChargeConfig;

enum ItemChargeType {
    ABYSSAL_BRACELET(ItemChargeConfig::showAbyssalBraceletCharges),
    AMULET_OF_CHEMISTRY(ItemChargeConfig::showAmuletOfChemistryCharges),
    AMULET_OF_BOUNTY(ItemChargeConfig::showAmuletOfBountyCharges),
    BELLOWS(ItemChargeConfig::showBellowCharges),
    BRACELET_OF_SLAUGHTER(ItemChargeConfig::showBraceletOfSlaughterCharges),
    EXPEDITIOUS_BRACELET(ItemChargeConfig::showExpeditiousBraceletCharges),
    FUNGICIDE_SPRAY(ItemChargeConfig::showFungicideCharges),
    IMPBOX(ItemChargeConfig::showImpCharges),
    TELEPORT(ItemChargeConfig::showTeleportCharges),
    WATERCAN(ItemChargeConfig::showWateringCanCharges),
    WATERSKIN(ItemChargeConfig::showWaterskinCharges),
    DODGY_NECKLACE(ItemChargeConfig::showDodgyCount),
    BINDING_NECKLACE(ItemChargeConfig::showBindingNecklaceCharges),
    EXPLORER_RING(ItemChargeConfig::showExplorerRingCharges),
    FRUIT_BASKET(ItemChargeConfig::showBasketCharges),
    SACK(ItemChargeConfig::showSackCharges),
    RING_OF_FORGING(ItemChargeConfig::showRingOfForgingCount),
    POTION(ItemChargeConfig::showPotionDoseCount),
    GUTHIX_REST(ItemChargeConfig::showGuthixRestDoses),
    BLOOD_ESSENCE(ItemChargeConfig::showBloodEssenceCharges),
    BRACELET_OF_CLAY(ItemChargeConfig::showBraceletOfClayCharges);

    private final Predicate<ItemChargeConfig> enabled;

    private ItemChargeType(Predicate<ItemChargeConfig> enabled) {
        this.enabled = enabled;
    }

    public Predicate<ItemChargeConfig> getEnabled() {
        return this.enabled;
    }
}

