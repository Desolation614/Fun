/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.plugins.skillcalculator.skills;

import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.skillcalculator.skills.ItemSkillAction;

public enum HerbloreAction implements ItemSkillAction
{
    GUAM_LEAF(249, 3, 2.5f),
    ATTACK_POTION_3(121, 3, 25.0f),
    MARRENTILL(251, 5, 3.8f),
    ANTIPOISON_3(175, 5, 37.5f),
    RELICYMS_BALM_3(4844, 8, 40.0f),
    TARROMIN(253, 11, 5.0f),
    STRENGTH_POTION_3(115, 12, 50.0f),
    SERUM_207_3(3410, 15, 50.0f),
    GUTHIX_REST_3(4419, 18, 59.0f),
    GUAM_TAR(10142, 19, 30.0f),
    HARRALANDER(255, 20, 6.3f),
    COMPOST_POTION_3(6472, 22, 60.0f),
    RESTORE_POTION_3(127, 22, 62.5f),
    RANARR_WEED(257, 25, 7.5f),
    ENERGY_POTION_3(3010, 26, 67.5f),
    TOADFLAX(2998, 30, 8.0f),
    DEFENCE_POTION_3(133, 30, 75.0f),
    MARRENTILL_TAR(10143, 31, 42.5f),
    AGILITY_POTION_3(3034, 34, 80.0f),
    COMBAT_POTION_3(9741, 36, 84.0f),
    PRAYER_POTION_3(139, 38, 87.5f),
    TARROMIN_TAR(10144, 39, 55.0f),
    IRIT_LEAF(259, 40, 8.8f),
    HARRALANDER_TAR(10145, 44, 72.5f),
    SUPER_ATTACK_3(145, 45, 100.0f),
    AVANTOE(261, 48, 10.0f),
    SUPERANTIPOISON_3(181, 48, 106.3f),
    FISHING_POTION_3(151, 50, 112.5f),
    SUPER_ENERGY_3(3018, 52, 117.5f),
    HUNTER_POTION_3(10000, 53, 120.0f),
    KWUARM(263, 54, 11.3f),
    GOADING_POTION_3(30140, 54, 132.0f),
    IRIT_TAR(28837, 55, 85.0f),
    SUPER_STRENGTH_3(157, 55, 125.0f),
    MAGIC_ESSENCE_POTION_3(9022, 57, 130.0f),
    HUASCA(30097, 58, 11.8f),
    PRAYER_REGENERATION_POTION(30128, 58, 132.0f),
    SNAPDRAGON(3000, 59, 11.8f),
    WEAPON_POISON(187, 60, 137.5f),
    ALCOAUGMENTATOR(30014, 60, 190.0f),
    LIPLACK_LIQUOR(30017, 60, 190.0f),
    MAMMOTHMIGHT_MIX(30011, 60, 190.0f),
    SUPER_RESTORE_3(3026, 63, 142.5f),
    MYSTIC_MANA_AMALGAM(30012, 63, 215.0f),
    CADANTINE(265, 65, 12.5f),
    SANFEW_SERUM_3(10927, 65, 160.0f),
    SUPER_DEFENCE_3(163, 66, 150.0f),
    MARLEYS_MOONLIGHT(30013, 66, 240.0f),
    LANTADYME(2481, 67, 13.1f),
    ANTIDOTE_PLUS_4(5943, 68, 155.0f),
    ANTIFIRE_POTION_3(2454, 69, 157.5f),
    AZURE_AURA_MIX(30016, 69, 265.0f),
    DIVINE_SUPER_ATTACK_POTION_4(23697, 70, 2.0f),
    DIVINE_SUPER_DEFENCE_POTION_4(23721, 70, 2.0f),
    DIVINE_SUPER_STRENGTH_POTION_4(23709, 70, 2.0f),
    DWARF_WEED(267, 70, 13.8f),
    RANGING_POTION_3(169, 72, 162.5f),
    AQUALUX_AMALGAM(30015, 72, 290.0f),
    WEAPON_POISON_PLUS(5937, 73, 190.0f),
    DIVINE_RANGING_POTION_4(23733, 74, 2.0f),
    TORSTOL(269, 75, 15.0f),
    MEGALITE_LIQUID(30019, 75, 315.0f),
    MAGIC_POTION_3(3042, 76, 172.5f),
    STAMINA_POTION_3(12627, 77, 76.5f),
    STAMINA_POTION_4(12625, 77, 102.0f),
    DIVINE_MAGIC_POTION_4(23745, 78, 2.0f),
    ZAMORAK_BREW_3(189, 78, 175.0f),
    ANTILEECH_LOTION(30018, 78, 340.0f),
    ANTIDOTE_PLUS_PLUS_4(5952, 79, 177.5f),
    BASTION_POTION_3(22464, 80, 155.0f),
    BATTLEMAGE_POTION_3(22452, 80, 155.0f),
    SARADOMIN_BREW_3(6687, 81, 180.0f),
    SURGE_POTION_3(30878, 81, 185.0f),
    MIXALOT(30020, 81, 365.0f),
    WEAPON_POISON_PLUS_PLUS(5940, 82, 190.0f),
    EXTENDED_ANTIFIRE_3(11953, 84, 82.5f),
    EXTENDED_ANTIFIRE_4(11951, 84, 110.0f),
    ANCIENT_BREW_4(26340, 85, 190.0f),
    DIVINE_BASTION_POTION_4(24635, 86, 2.0f),
    DIVINE_BATTLEMAGE_POTION_4(24623, 86, 2.0f),
    ANTIVENOM_3(12907, 87, 90.0f),
    ANTIVENOM_4(12905, 87, 120.0f),
    MENAPHITE_REMEDY_3(27205, 88, 200.0f),
    SUPER_COMBAT_POTION_4(12695, 90, 150.0f),
    FORGOTTEN_BREW_4(27629, 91, 145.0f),
    SUPER_ANTIFIRE_4(21978, 92, 130.0f),
    EXTENDED_ANTIVENOM_PLUS_4(29824, 94, 80.0f),
    ANTIVENOM_PLUS_4(12913, 94, 125.0f),
    DIVINE_SUPER_COMBAT_POTION_4(23685, 97, 2.0f),
    EXTENDED_SUPER_ANTIFIRE_3(22212, 98, 120.0f),
    EXTENDED_SUPER_ANTIFIRE_4(22209, 98, 160.0f);

    private final int itemId;
    private final int level;
    private final float xp;

    @Override
    public boolean isMembers(ItemManager itemManager) {
        return true;
    }

    private HerbloreAction(int itemId, int level, float xp) {
        this.itemId = itemId;
        this.level = level;
        this.xp = xp;
    }

    @Override
    public int getItemId() {
        return this.itemId;
    }

    @Override
    public int getLevel() {
        return this.level;
    }

    @Override
    public float getXp() {
        return this.xp;
    }
}

