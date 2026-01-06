/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.runelite.api.Client
 *  net.runelite.api.Prayer
 */
package net.runelite.client.plugins.prayer;

import net.runelite.api.Client;
import net.runelite.api.Prayer;

enum PrayerType {
    THICK_SKIN("Thick Skin", Prayer.THICK_SKIN, "+5% Defence", 115, false, 1),
    BURST_OF_STRENGTH("Burst of Strength", Prayer.BURST_OF_STRENGTH, "+5% Strength", 116, false, 1),
    CLARITY_OF_THOUGHT("Clarity of Thought", Prayer.CLARITY_OF_THOUGHT, "+5% Attack", 117, false, 1),
    SHARP_EYE("Sharp Eye", Prayer.SHARP_EYE, "+5% Ranged", 133, false, 1),
    MYSTIC_WILL("Mystic Will", Prayer.MYSTIC_WILL, "+5% Magical attack and defence", 134, false, 1),
    ROCK_SKIN("Rock Skin", Prayer.ROCK_SKIN, "+10% Defence", 118, false, 6),
    SUPERHUMAN_STRENGTH("Superhuman Strength", Prayer.SUPERHUMAN_STRENGTH, "+10% Strength", 119, false, 6),
    IMPROVED_REFLEXES("Improved Reflexes", Prayer.IMPROVED_REFLEXES, "+10% Attack", 120, false, 6),
    RAPID_RESTORE("Rapid Restore", Prayer.RAPID_RESTORE, "2 x Restore rate for all skills except Hitpoints and Prayer", 121, false, 1),
    RAPID_HEAL("Rapid Heal", Prayer.RAPID_HEAL, "2 x Restore rate for Hitpoints", 122, false, 2),
    PROTECT_ITEM("Protect Item", Prayer.PROTECT_ITEM, "Player keeps 1 extra item when they die", 123, false, 2),
    HAWK_EYE("Hawk Eye", Prayer.HAWK_EYE, "+10% Ranged", 502, false, 6),
    MYSTIC_LORE("Mystic Lore", Prayer.MYSTIC_LORE, "+10% Magical attack and defence", 503, false, 6),
    STEEL_SKIN("Steel Skin", Prayer.STEEL_SKIN, "+15% Defence", 124, false, 12),
    ULTIMATE_STRENGTH("Ultimate Strength", Prayer.ULTIMATE_STRENGTH, "+15% Strength", 125, false, 12),
    INCREDIBLE_REFLEXES("Incredible reflexes", Prayer.INCREDIBLE_REFLEXES, "+15% Attack", 126, false, 12),
    PROTECT_FROM_MAGIC("protect from magic", Prayer.PROTECT_FROM_MAGIC, "Protects against magic attacks", 127, true, 12),
    PROTECT_FROM_MISSILES("Protect from missiles", Prayer.PROTECT_FROM_MISSILES, "Protects against ranged attacks", 128, true, 12),
    PROTECT_FROM_MELEE("Protect from melee", Prayer.PROTECT_FROM_MELEE, "Protects against melee attacks", 129, true, 12),
    EAGLE_EYE("Eagle Eye", Prayer.EAGLE_EYE, "+15% Ranged", 504, false, 12){

        @Override
        boolean isEnabled(Client client) {
            return !DEADEYE.isEnabled(client);
        }
    }
    ,
    MYSTIC_MIGHT("Mystic Might", Prayer.MYSTIC_MIGHT, "+15% Magical attack and defence", 505, false, 12){

        @Override
        boolean isEnabled(Client client) {
            return !MYSTIC_VIGOUR.isEnabled(client);
        }
    }
    ,
    RETRIBUTION("Retribution", Prayer.RETRIBUTION, "Deals damage up to 25% of your Prayer level to nearby targets upon the user's death", 131, true, 3),
    REDEMPTION("Redemption", Prayer.REDEMPTION, "Heals the player if they fall below 10% health", 130, true, 6),
    SMITE("Smite", Prayer.SMITE, "Removes 1 Prayer point from an enemy for every 4 damage inflicted on the enemy", 132, true, 18),
    PRESERVE("Preserve", Prayer.PRESERVE, "Boosted stats last 50% longer", 947, false, 2),
    CHIVALRY("Chivalry", Prayer.CHIVALRY, "+15% Attack, +18% Strength, +20% Defence", 945, false, 24),
    DEADEYE("Deadeye", Prayer.DEADEYE, "+18% Ranged attack, +18% Ranged strength, +5% Defence", 1422, false, 12){

        @Override
        boolean isEnabled(Client client) {
            boolean inLms = client.getVarbitValue(5314) != 0;
            boolean deadeye = client.getVarbitValue(16097) != 0;
            return deadeye && !inLms;
        }
    }
    ,
    MYSTIC_VIGOUR("Mystic Vigour", Prayer.MYSTIC_VIGOUR, "+18% Magical attack and defence, +3% Magic damage, +5% Defence", 1423, false, 12){

        @Override
        boolean isEnabled(Client client) {
            boolean inLms = client.getVarbitValue(5314) != 0;
            boolean vigour = client.getVarbitValue(16098) != 0;
            return vigour && !inLms;
        }
    }
    ,
    PIETY("Piety", Prayer.PIETY, "+20% Attack, +23% Strength, +25% Defence", 946, false, 24),
    RIGOUR("Rigour", Prayer.RIGOUR, "+20% Ranged attack, +23% Ranged strength, +25% Defence", 1420, false, 24),
    AUGURY("Augury", Prayer.AUGURY, "+25% Magical attack and defence, +25% Defence", 1421, false, 24),
    RP_REJUVENATION("Rejuvenation", Prayer.RP_REJUVENATION, "3x Restore rate for Hitpoints", 4842, false, 4),
    RP_ANCIENT_STRENGTH("Ancient Strength", Prayer.RP_ANCIENT_STRENGTH, "+20% Attack and Strength", 4843, false, 18),
    RP_ANCIENT_SIGHT("Ancient Sight", Prayer.RP_ANCIENT_SIGHT, "+20% Ranged Attack & Strength", 4844, false, 18),
    RP_ANCIENT_WILL("Ancient Will", Prayer.RP_ANCIENT_WILL, "+20% Magic Attack", 4845, false, 18),
    RP_PROTECT_ITEM("Protect Item", Prayer.RP_PROTECT_ITEM, "Keep one extra item upon death", 4865, false, 18),
    RP_RUINOUS_GRACE("Ruinous Grace", Prayer.RP_RUINOUS_GRACE, "Prayer points are drained in place of run energy whilst running", 4846, false, 1),
    RP_DAMPEN_MAGIC("Dampen Magic", Prayer.RP_DAMPEN_MAGIC, "100% protection from magic attacks (40% in PvP), but increases damage taken by 10%", 4847, true, 14),
    RP_DAMPEN_RANGE("Dampen Ranged", Prayer.RP_DAMPEN_RANGED, "100% protection from ranged attacks (40% in PvP), but increases damage taken by 10%", 4848, true, 14),
    RP_DAMPEN_MELEE("Dampen Melee", Prayer.RP_DAMPEN_MELEE, "100% protection from melee attacks (40% in PvP), but increases damage taken by 10%", 4849, true, 14),
    RP_TRINITAS("Trinitas", Prayer.RP_TRINITAS, "+15% Melee Attack and Strength, +15% Ranged Attack and Strength, and +15% Magic Attack", 4850, false, 22),
    RP_BERSERKER("Berserker", Prayer.RP_BERSERKER, "Boosted combat stats last 50% longer", 4851, false, 2),
    RP_PURGE("Purge", Prayer.RP_PURGE, "Reduces the player's and opponent's prayer points by 33% of damage dealt", 4852, true, 18),
    RP_METABOLISE("Metabolise", Prayer.RP_METABOLISE, "25 ticks (15 seconds) after activation, allows players to attack 1 tick earlier after eating a piece of food", 4853, false, 12),
    RP_REBUKE("Rebuke", Prayer.RP_REBUKE, "Increases recoil damage by 50%", 4857, true, 12),
    RP_VINDICATION("Vindication", Prayer.RP_VINDICATION, "Heals the player by 25% of their base Prayer level if they fall below 10% health, draining their prayer points fully", 4863, true, 9),
    RP_DECIMATE("Decimate", Prayer.RP_DECIMATE, "+25% Attack, +27% Strength", 4854, false, 28),
    RP_ANNIHILATE("Annihilate", Prayer.RP_ANNIHILATE, "+25% Ranged Attack, +27% Ranged Strength", 4855, false, 28),
    RP_VAPORISE("Vaporise", Prayer.RP_VAPORISE, "+25% Magic Attack, +4% Magic Damage", 4856, false, 28),
    RP_FUMUS_VOW("Fumus' Vow", Prayer.RP_FUMUS_VOW, "Attacks that inflict poison will deal poison damage instantly", 4858, false, 14),
    RP_UMBRAS_VOW("Umbra's Vow", Prayer.RP_UMBRA_VOW, "Attacks drain 5% of the opponent's Defence, up to 15% of their base level", 4859, false, 14),
    RP_CRUORS_VOW("Cruor's Vow", Prayer.RP_CRUORS_VOW, "Heals 3 Hitpoints and drains 3 Prayer points upon killing a target", 4860, false, 14),
    RP_GLACIES_VOW("Glacies' Vow", Prayer.RP_GLACIES_VOW, "Attacks drain 5% of the opponent's Magic, up to 15% of their base level", 4861, false, 14),
    RP_WRATH("Wrath", Prayer.RP_WRATH, "Upon death, deals damage up to 25% of the player's base Prayer level to targets in a 5x5 radius", 4862, false, 3),
    RP_INTENSIFY("Intensify", Prayer.RP_INTENSIFY, "+40% accuracy in all attack styles", 4864, false, 28);

    private final String name;
    private final Prayer prayer;
    private final String description;
    private final int spriteID;
    private final boolean overhead;
    private final int drainEffect;

    boolean isEnabled(Client client) {
        return true;
    }

    boolean isActive(Client client) {
        return client.isPrayerActive(this.prayer) && this.isEnabled(client);
    }

    private PrayerType(String name, Prayer prayer, String description, int spriteID, boolean overhead, int drainEffect) {
        this.name = name;
        this.prayer = prayer;
        this.description = description;
        this.spriteID = spriteID;
        this.overhead = overhead;
        this.drainEffect = drainEffect;
    }

    public String getName() {
        return this.name;
    }

    public Prayer getPrayer() {
        return this.prayer;
    }

    public String getDescription() {
        return this.description;
    }

    public int getSpriteID() {
        return this.spriteID;
    }

    public boolean isOverhead() {
        return this.overhead;
    }

    public int getDrainEffect() {
        return this.drainEffect;
    }
}

