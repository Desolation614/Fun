/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap$Builder
 */
package net.runelite.client.plugins.runecraft;

import com.google.common.collect.ImmutableMap;
import java.util.Map;
import java.util.function.Predicate;
import net.runelite.client.plugins.runecraft.RunecraftConfig;

enum AbyssRifts {
    AIR_RIFT(25378, 556, RunecraftConfig::showAir),
    BLOOD_RIFT(43848, 565, RunecraftConfig::showBlood),
    BODY_RIFT(24973, 559, RunecraftConfig::showBody),
    CHAOS_RIFT(24976, 562, RunecraftConfig::showChaos),
    COSMIC_RIFT(24974, 564, RunecraftConfig::showCosmic),
    DEATH_RIFT(25035, 560, RunecraftConfig::showDeath),
    EARTH_RIFT(24972, 557, RunecraftConfig::showEarth),
    FIRE_RIFT(24971, 554, RunecraftConfig::showFire),
    LAW_RIFT(25034, 563, RunecraftConfig::showLaw),
    MIND_RIFT(25379, 558, RunecraftConfig::showMind),
    NATURE_RIFT(24975, 561, RunecraftConfig::showNature),
    SOUL_RIFT(25377, 566, RunecraftConfig::showSoul),
    WATER_RIFT(25376, 555, RunecraftConfig::showWater);

    private final int objectId;
    private final int itemId;
    private final Predicate<RunecraftConfig> configEnabled;
    private static final Map<Integer, AbyssRifts> rifts;

    static AbyssRifts getRift(int id) {
        return rifts.get(id);
    }

    private AbyssRifts(int objectId, int itemId, Predicate<RunecraftConfig> configEnabled) {
        this.objectId = objectId;
        this.itemId = itemId;
        this.configEnabled = configEnabled;
    }

    public int getObjectId() {
        return this.objectId;
    }

    public int getItemId() {
        return this.itemId;
    }

    public Predicate<RunecraftConfig> getConfigEnabled() {
        return this.configEnabled;
    }

    static {
        ImmutableMap.Builder builder = new ImmutableMap.Builder();
        for (AbyssRifts s : AbyssRifts.values()) {
            builder.put((Object)s.getObjectId(), (Object)s);
        }
        rifts = builder.build();
    }
}

