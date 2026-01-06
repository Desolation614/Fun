/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package net.runelite.client.plugins.timersandbuffs;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import javax.annotation.Nullable;
import net.runelite.client.plugins.timersandbuffs.GameTimerImageType;
import net.runelite.client.util.RSTimeUnit;

enum GameTimer {
    STAMINA(12625, GameTimerImageType.ITEM, "Stamina", false),
    ANTIFIRE(2452, GameTimerImageType.ITEM, "Antifire", false),
    OVERLOAD(11730, GameTimerImageType.ITEM, "Overload", false),
    CANNON(10, GameTimerImageType.ITEM, "Cannon", 25L, ChronoUnit.MINUTES),
    CANNON_REPAIR(1, GameTimerImageType.ITEM, "Broken Cannon", 10L, ChronoUnit.MINUTES),
    MAGICIMBUE(552, GameTimerImageType.SPRITE, "Magic imbue", false),
    SUPERANTIFIRE(21978, GameTimerImageType.ITEM, "Super antifire", false),
    BIND(319, GameTimerImageType.SPRITE, "Bind", 181, 8L, RSTimeUnit.GAME_TICKS, true),
    SNARE(320, GameTimerImageType.SPRITE, "Snare", 180, 16L, RSTimeUnit.GAME_TICKS, true),
    ENTANGLE(321, GameTimerImageType.SPRITE, "Entangle", 179, 24L, RSTimeUnit.GAME_TICKS, true),
    ICERUSH(325, GameTimerImageType.SPRITE, "Ice rush", 361, 8L, RSTimeUnit.GAME_TICKS, true),
    ICEBURST(326, GameTimerImageType.SPRITE, "Ice burst", 363, 16L, RSTimeUnit.GAME_TICKS, true),
    ICEBLITZ(327, GameTimerImageType.SPRITE, "Ice blitz", 367, 24L, RSTimeUnit.GAME_TICKS, true),
    ICEBARRAGE(328, GameTimerImageType.SPRITE, "Ice barrage", 369, 32L, RSTimeUnit.GAME_TICKS, true),
    IMBUEDHEART(20724, GameTimerImageType.ITEM, "Imbued/Saturated heart", false),
    VENGEANCE(564, GameTimerImageType.SPRITE, "Vengeance", 30L, ChronoUnit.SECONDS),
    HEAL_GROUP(566, GameTimerImageType.SPRITE, "Heal Group", 150L, RSTimeUnit.GAME_TICKS),
    OVERLOAD_RAID(20996, GameTimerImageType.ITEM, "Overload", false),
    PRAYER_ENHANCE(20964, GameTimerImageType.ITEM, "Prayer enhance", 290L, ChronoUnit.SECONDS, true),
    GOD_WARS_ALTAR(201, GameTimerImageType.SPRITE, "God wars altar", false),
    SCURRIUS_FOOD_PILE(1985, GameTimerImageType.ITEM, "Scurrius food pile", false),
    CHARGE(322, GameTimerImageType.SPRITE, "Charge", false),
    STAFF_OF_THE_DEAD(11791, GameTimerImageType.ITEM, "Staff of the Dead", 1L, ChronoUnit.MINUTES),
    ABYSSAL_SIRE_STUN(13262, GameTimerImageType.ITEM, "Abyssal Sire Stun", 46L, RSTimeUnit.GAME_TICKS, true),
    HOME_TELEPORT(356, GameTimerImageType.SPRITE, "Home Teleport", 30L, ChronoUnit.MINUTES),
    MINIGAME_TELEPORT(1053, GameTimerImageType.SPRITE, "Minigame Teleport", 20L, ChronoUnit.MINUTES),
    DRAGON_FIRE_SHIELD(11283, GameTimerImageType.ITEM, "Dragonfire Shield Special", false),
    DIVINE_ANCIENT_COMBAT(57282, GameTimerImageType.ITEM, "Divine Ancient Combat", false),
    DIVINE_SUPER_ATTACK(23697, GameTimerImageType.ITEM, "Divine Super Attack", false),
    DIVINE_SUPER_STRENGTH(23709, GameTimerImageType.ITEM, "Divine Super Strength", false),
    DIVINE_SUPER_DEFENCE(23721, GameTimerImageType.ITEM, "Divine Super Defence", false),
    DIVINE_SUPER_COMBAT(23685, GameTimerImageType.ITEM, "Divine Super Combat", false),
    DIVINE_RANGING(23733, GameTimerImageType.ITEM, "Divine Ranging", false),
    DIVINE_MAGIC(23745, GameTimerImageType.ITEM, "Divine Magic", false),
    DIVINE_BASTION(24635, GameTimerImageType.ITEM, "Divine Bastion", false),
    DIVINE_BATTLEMAGE(24623, GameTimerImageType.ITEM, "Divine Battlemage", false),
    MOONLIGHT_POTION(29080, GameTimerImageType.ITEM, "Moonlight potion", false),
    ANTIPOISON(2446, GameTimerImageType.ITEM, "Antipoison", false),
    ANTIVENOM(12905, GameTimerImageType.ITEM, "Anti-venom", false),
    TELEBLOCK(352, GameTimerImageType.SPRITE, "Teleblock", false),
    SHADOW_VEIL(1315, GameTimerImageType.SPRITE, "Shadow veil", true),
    RESURRECT_THRALL(2981, GameTimerImageType.SPRITE, "Resurrect thrall", false),
    WARD_OF_ARCEUUS(1306, GameTimerImageType.SPRITE, "Ward of Arceuus", true),
    DEATH_CHARGE(1310, GameTimerImageType.SPRITE, "Death charge", 60L, ChronoUnit.SECONDS, false),
    MARK_OF_DARKNESS(1305, GameTimerImageType.SPRITE, "Mark of Darkness", true),
    SHADOW_VEIL_COOLDOWN(1334, GameTimerImageType.SPRITE, "Shadow veil cooldown", 30L, ChronoUnit.SECONDS),
    RESURRECT_THRALL_COOLDOWN(2987, GameTimerImageType.SPRITE, "Resurrect thrall cooldown", 17L, RSTimeUnit.GAME_TICKS),
    WARD_OF_ARCEUUS_COOLDOWN(1325, GameTimerImageType.SPRITE, "Ward of Arceuus cooldown", 30L, ChronoUnit.SECONDS),
    DEATH_CHARGE_COOLDOWN(1329, GameTimerImageType.SPRITE, "Death charge cooldown", 60L, ChronoUnit.SECONDS),
    CORRUPTION_COOLDOWN(1327, GameTimerImageType.SPRITE, "Corruption cooldown", 30L, ChronoUnit.SECONDS),
    MARK_OF_DARKNESS_COOLDOWN(1324, GameTimerImageType.SPRITE, "Mark of Darkness cooldown", 10L, RSTimeUnit.GAME_TICKS, true),
    PICKPOCKET_STUN(206, GameTimerImageType.SPRITE, "Stunned", true),
    FARMERS_AFFINITY(1947, GameTimerImageType.ITEM, "Farmer's Affinity", false),
    SMELLING_SALTS(27343, GameTimerImageType.ITEM, "Smelling salts", false),
    MENAPHITE_REMEDY(27202, GameTimerImageType.ITEM, "Menaphite remedy", false),
    LIQUID_ADRENALINE(27339, GameTimerImageType.ITEM, "Liquid adrenaline", 150L, ChronoUnit.SECONDS, false),
    SILK_DRESSING(27323, GameTimerImageType.ITEM, "Silk dressing", 100L, RSTimeUnit.GAME_TICKS, true),
    BLESSED_CRYSTAL_SCARAB(27335, GameTimerImageType.ITEM, "Blessed crystal scarab", 40L, RSTimeUnit.GAME_TICKS, true),
    SPELLBOOK_SWAP(582, GameTimerImageType.SPRITE, "Spellbook Reset", 120L, ChronoUnit.SECONDS, false),
    SMOULDERING_HEART(29583, GameTimerImageType.ITEM, "Smouldering heart", false),
    SMOULDERING_GLAND(29587, GameTimerImageType.ITEM, "Smouldering gland", false),
    GOADING(30137, GameTimerImageType.ITEM, "Goading potion", false),
    PRAYER_REGENERATION(30125, GameTimerImageType.ITEM, "Prayer regeneration", false),
    SURGE_POTION(30875, GameTimerImageType.ITEM, "Surge potion", false),
    BONUS_XP(55006, GameTimerImageType.ITEM, "Bonus XP", false),
    BONUS_DROP_RATES(55012, GameTimerImageType.ITEM, "Bonus drop rates", false),
    BONUS_PET_RATES(55014, GameTimerImageType.ITEM, "Bonus pet rates", false),
    BONUS_COX_RATES(55018, GameTimerImageType.ITEM, "Bonus Chambers of Xeric points", false);

    @Nullable
    private final Duration duration;
    @Nullable
    private final Integer graphicId;
    private final String description;
    private final boolean removedOnDeath;
    private final int imageId;
    private final GameTimerImageType imageType;

    private GameTimer(int imageId, GameTimerImageType idType, String description, Integer graphicId, long time, TemporalUnit unit, boolean removedOnDeath) {
        this.description = description;
        this.graphicId = graphicId;
        this.duration = Duration.of(time, unit);
        this.imageId = imageId;
        this.imageType = idType;
        this.removedOnDeath = removedOnDeath;
    }

    private GameTimer(int imageId, GameTimerImageType idType, String description, long time, TemporalUnit unit, boolean removeOnDeath) {
        this(imageId, idType, description, null, time, unit, removeOnDeath);
    }

    private GameTimer(int imageId, GameTimerImageType idType, String description, long time, TemporalUnit unit) {
        this(imageId, idType, description, null, time, unit, false);
    }

    private GameTimer(int imageId, GameTimerImageType idType, String description, boolean removedOnDeath) {
        this.duration = null;
        this.graphicId = null;
        this.description = description;
        this.removedOnDeath = removedOnDeath;
        this.imageId = imageId;
        this.imageType = idType;
    }

    @Nullable
    Duration getDuration() {
        return this.duration;
    }

    @Nullable
    Integer getGraphicId() {
        return this.graphicId;
    }

    String getDescription() {
        return this.description;
    }

    boolean isRemovedOnDeath() {
        return this.removedOnDeath;
    }

    int getImageId() {
        return this.imageId;
    }

    GameTimerImageType getImageType() {
        return this.imageType;
    }
}

