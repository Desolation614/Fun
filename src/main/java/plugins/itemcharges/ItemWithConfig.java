/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap$Builder
 *  javax.annotation.Nullable
 */
package net.runelite.client.plugins.itemcharges;

import com.google.common.collect.ImmutableMap;
import java.util.Map;
import javax.annotation.Nullable;
import net.runelite.client.plugins.itemcharges.ItemChargeType;

enum ItemWithConfig {
    DODGY_NECKLACE(21143, "dodgyNecklace", ItemChargeType.DODGY_NECKLACE),
    BINDING_NECKLACE(5521, "bindingNecklace", ItemChargeType.BINDING_NECKLACE),
    EXPLORERS_RING_1(13125, "explorerRing", ItemChargeType.EXPLORER_RING),
    EXPLORERS_RING_2(13126, "explorerRing", ItemChargeType.EXPLORER_RING),
    EXPLORERS_RING_3(13127, "explorerRing", ItemChargeType.EXPLORER_RING),
    EXPLORERS_RING_4(13128, "explorerRing", ItemChargeType.EXPLORER_RING),
    RING_OF_FORGING(2568, "ringOfForging", ItemChargeType.RING_OF_FORGING),
    AMULET_OF_CHEMISTRY(21163, "amuletOfChemistry", ItemChargeType.AMULET_OF_CHEMISTRY),
    AMULET_OF_BOUNTY(21160, "amuletOfBounty", ItemChargeType.AMULET_OF_BOUNTY),
    BRACELET_OF_SLAUGHTER(21183, "braceletOfSlaughter", ItemChargeType.BRACELET_OF_SLAUGHTER),
    EXPEDITIOUS_BRACELET(21177, "expeditiousBracelet", ItemChargeType.EXPEDITIOUS_BRACELET),
    CHRONICLE(13660, "chronicle", ItemChargeType.TELEPORT),
    BLOOD_ESSENCE(26392, "bloodEssence", ItemChargeType.BLOOD_ESSENCE),
    BRACELET_OF_CLAY(11074, "braceletOfClay", ItemChargeType.BRACELET_OF_CLAY);

    private final int itemId;
    private final String configKey;
    private final ItemChargeType type;
    private static final Map<Integer, ItemWithConfig> ID_MAP;

    @Nullable
    static ItemWithConfig findItem(int itemId) {
        return ID_MAP.get(itemId);
    }

    private ItemWithConfig(int itemId, String configKey, ItemChargeType type) {
        this.itemId = itemId;
        this.configKey = configKey;
        this.type = type;
    }

    public int getItemId() {
        return this.itemId;
    }

    public String getConfigKey() {
        return this.configKey;
    }

    public ItemChargeType getType() {
        return this.type;
    }

    static {
        ImmutableMap.Builder builder = new ImmutableMap.Builder();
        for (ItemWithConfig item : ItemWithConfig.values()) {
            builder.put((Object)item.getItemId(), (Object)item);
        }
        ID_MAP = builder.build();
    }
}

