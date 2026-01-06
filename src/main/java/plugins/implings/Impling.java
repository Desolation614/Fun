/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap$Builder
 */
package net.runelite.client.plugins.implings;

import com.google.common.collect.ImmutableMap;
import java.util.Map;
import net.runelite.client.plugins.implings.ImplingType;

enum Impling {
    BABY(ImplingType.BABY, 1635),
    BABY_2(ImplingType.BABY, 1645),
    YOUNG(ImplingType.YOUNG, 1636),
    YOUNG_2(ImplingType.YOUNG, 1646),
    GOURMET(ImplingType.GOURMET, 1637),
    GOURMET_2(ImplingType.GOURMET, 1647),
    EARTH(ImplingType.EARTH, 1638),
    EARTH_2(ImplingType.EARTH, 1648),
    ESSENCE(ImplingType.ESSENCE, 1639),
    ESSENCE_2(ImplingType.ESSENCE, 1649),
    ECLECTIC(ImplingType.ECLECTIC, 1640),
    ECLECTIC_2(ImplingType.ECLECTIC, 1650),
    NATURE(ImplingType.NATURE, 1641),
    NATURE_2(ImplingType.NATURE, 1651),
    MAGPIE(ImplingType.MAGPIE, 1642),
    MAGPIE_2(ImplingType.MAGPIE, 1652),
    NINJA(ImplingType.NINJA, 1643),
    NINJA_2(ImplingType.NINJA, 1653),
    CRYSTAL(ImplingType.CRYSTAL, 8741),
    CRYSTAL_2(ImplingType.CRYSTAL, 8742),
    CRYSTAL_3(ImplingType.CRYSTAL, 8743),
    CRYSTAL_4(ImplingType.CRYSTAL, 8744),
    CRYSTAL_5(ImplingType.CRYSTAL, 8745),
    CRYSTAL_6(ImplingType.CRYSTAL, 8746),
    CRYSTAL_7(ImplingType.CRYSTAL, 8747),
    CRYSTAL_8(ImplingType.CRYSTAL, 8748),
    CRYSTAL_9(ImplingType.CRYSTAL, 8749),
    CRYSTAL_10(ImplingType.CRYSTAL, 8750),
    CRYSTAL_11(ImplingType.CRYSTAL, 8751),
    CRYSTAL_12(ImplingType.CRYSTAL, 8752),
    CRYSTAL_13(ImplingType.CRYSTAL, 8753),
    CRYSTAL_14(ImplingType.CRYSTAL, 8754),
    CRYSTAL_15(ImplingType.CRYSTAL, 8755),
    CRYSTAL_16(ImplingType.CRYSTAL, 8756),
    CRYSTAL_17(ImplingType.CRYSTAL, 8757),
    DRAGON(ImplingType.DRAGON, 1644),
    DRAGON_2(ImplingType.DRAGON, 1654),
    LUCKY(ImplingType.LUCKY, 7233),
    LUCKY_2(ImplingType.LUCKY, 7302);

    private ImplingType implingType;
    private final int npcId;
    private static final Map<Integer, Impling> IMPLINGS;

    static Impling findImpling(int npcId) {
        return IMPLINGS.get(npcId);
    }

    private Impling(ImplingType implingType, int npcId) {
        this.implingType = implingType;
        this.npcId = npcId;
    }

    public ImplingType getImplingType() {
        return this.implingType;
    }

    public int getNpcId() {
        return this.npcId;
    }

    static {
        ImmutableMap.Builder builder = new ImmutableMap.Builder();
        for (Impling impling : Impling.values()) {
            builder.put((Object)impling.npcId, (Object)impling);
        }
        IMPLINGS = builder.build();
    }
}

