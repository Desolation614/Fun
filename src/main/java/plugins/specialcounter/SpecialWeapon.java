/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableSet
 *  javax.annotation.Nullable
 *  net.runelite.api.NPC
 */
package net.runelite.client.plugins.specialcounter;

import com.google.common.collect.ImmutableSet;
import java.util.Set;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.runelite.api.NPC;
import net.runelite.client.plugins.specialcounter.SpecialCounterConfig;

public enum SpecialWeapon {
    DRAGON_WARHAMMER("Dragon Warhammer", new int[]{13576, 28035}, false, SpecialCounterConfig::dragonWarhammerThreshold){

        @Override
        public float computeDrainPercent(int hit, @Nullable NPC target) {
            if (hit > 0) {
                return 0.7f;
            }
            if (target != null && TEKTON_VARIANTS.contains(target.getId())) {
                return 0.95f;
            }
            return 0.0f;
        }
    }
    ,
    ARCLIGHT("Arclight", new int[]{19675}, false, SpecialCounterConfig::arclightThreshold),
    DARKLIGHT("Darklight", new int[]{6746}, false, SpecialCounterConfig::darklightThreshold),
    BANDOS_GODSWORD("Bandos Godsword", new int[]{11804, 20370}, true, SpecialCounterConfig::bandosGodswordThreshold){

        @Override
        public int computeHit(int hit, @Nullable NPC target) {
            if (hit == 0 && target != null && TEKTON_VARIANTS.contains(target.getId())) {
                return 10;
            }
            return super.computeHit(hit, target);
        }
    }
    ,
    BARRELCHEST_ANCHOR("Barrelchest Anchor", new int[]{10887}, true, c -> 0),
    BONE_DAGGER("Bone Dagger", new int[]{8872, 8874, 8876, 8878}, true, c -> 0),
    DORGESHUUN_CROSSBOW("Dorgeshuun Crossbow", new int[]{8880}, true, distance -> 60 + distance * 3, c -> 0),
    BULWARK("Dinh's Bulwark", new int[]{21015}, false, SpecialCounterConfig::bulwarkThreshold),
    ACCURSED_SCEPTRE("Accursed Sceptre", new int[]{27665, 27679}, false, distance -> 46 + distance * 10, c -> 0),
    TONALZTICS_OF_RALOS("Tonalztics of Ralos", new int[]{28922}, true, distance -> 50, c -> 0),
    ELDER_MAUL("Elder Maul", new int[]{21003, 27100}, false, distance -> 50, SpecialCounterConfig::elderMaulThreshold){

        @Override
        public float computeDrainPercent(int hit, @Nullable NPC target) {
            if (hit > 0) {
                return 0.65f;
            }
            if (target != null && TEKTON_VARIANTS.contains(target.getId())) {
                return 0.95f;
            }
            return 0.0f;
        }
    }
    ,
    SEERCULL("Seercull", new int[]{6724}, true, d -> 46 + d * 5, c -> 0),
    EMBERLIGHT("Emberlight", new int[]{29589}, false, SpecialCounterConfig::emberlightThreshold);

    private static final Set<Integer> TEKTON_VARIANTS;
    private final String name;
    private final int[] itemID;
    private final boolean damage;
    private final Function<Integer, Integer> clientCycleHitDelay;
    private final Function<SpecialCounterConfig, Integer> threshold;

    private SpecialWeapon(String name, int[] itemID, boolean damage, Function<SpecialCounterConfig, Integer> threshold) {
        this(name, itemID, damage, distance -> 0, threshold);
    }

    public int getHitDelay(int distance) {
        int serverCyclesDelay = this.getClientCycleHitDelay().apply(distance) / 30;
        return serverCyclesDelay + 1;
    }

    public float computeDrainPercent(int hit, @Nullable NPC target) {
        return 0.0f;
    }

    public int computeHit(int hit, @Nullable NPC target) {
        return hit;
    }

    private SpecialWeapon(String name, int[] itemID, boolean damage, Function<Integer, Integer> clientCycleHitDelay, Function<SpecialCounterConfig, Integer> threshold) {
        this.name = name;
        this.itemID = itemID;
        this.damage = damage;
        this.clientCycleHitDelay = clientCycleHitDelay;
        this.threshold = threshold;
    }

    public String getName() {
        return this.name;
    }

    public int[] getItemID() {
        return this.itemID;
    }

    public boolean isDamage() {
        return this.damage;
    }

    public Function<Integer, Integer> getClientCycleHitDelay() {
        return this.clientCycleHitDelay;
    }

    public Function<SpecialCounterConfig, Integer> getThreshold() {
        return this.threshold;
    }

    static {
        TEKTON_VARIANTS = ImmutableSet.of((Object)7540, (Object)7541, (Object)7542, (Object)7545, (Object)7543, (Object)7544, (Object[])new Integer[0]);
    }
}

