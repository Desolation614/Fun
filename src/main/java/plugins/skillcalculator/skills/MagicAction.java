/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.plugins.skillcalculator.skills;

import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.skillcalculator.skills.SkillAction;

public enum MagicAction implements SkillAction
{
    WIND_STRIKE("Wind Strike", 1, 5.5f, 15, false),
    CONFUSE("Confuse", 3, 13.0f, 16, false),
    ENCHANT_OPAL_BOLT("Enchant Opal Bolt", 4, 9.0f, 358, true),
    WATER_STRIKE("Water Strike", 5, 7.5f, 17, false),
    ARCEUUS_LIBRARY_TELEPORT("Arceuus Library Teleport", 6, 10.0f, 1252, true),
    ENCHANT_SAPPHIRE_BOLT("Enchant Sapphire Bolt", 7, 17.5f, 358, true),
    ENCHANT_SAPPHIRE_JEWELLERY("Enchant Sapphire Jewellery", 7, 17.5f, 18, false),
    EARTH_STRIKE("Earth Strike", 9, 9.5f, 19, false),
    WEAKEN("Weaken", 11, 21.0f, 20, false),
    FIRE_STRIKE("Fire Strike", 13, 11.5f, 21, false),
    ENCHANT_JADE_BOLT("Enchant Jade Bolt", 14, 19.0f, 358, true),
    BONES_TO_BANANAS("Bones To Bananas", 15, 25.0f, 22, false),
    BASIC_REANIMATION("Basic Reanimation", 16, 32.0f, 1247, true),
    WIND_BOLT("Wind Bolt", 17, 13.5f, 23, false),
    DRAYNOR_MANOR_TELEPORT("Draynor Manor Teleport", 17, 16.0f, 1253, true),
    CURSE("Curse", 19, 29.0f, 24, false),
    BIND("Bind", 20, 30.0f, 319, false),
    LOW_LEVEL_ALCHEMY("Low Level Alchemy", 21, 31.0f, 25, false),
    WATER_BOLT("Water Bolt", 23, 16.5f, 26, false),
    ENCHANT_PEARL_BOLT("Enchant Pearl Bolt", 24, 29.0f, 358, true),
    VARROCK_TELEPORT("Varrock Teleport", 25, 35.0f, 27, false),
    ENCHANT_EMERALD_BOLT("Enchant Emerald Bolt", 27, 37.0f, 358, true),
    ENCHANT_EMERALD_JEWELLERY("Enchant Emerald Jewellery", 27, 37.0f, 28, false),
    MIND_ALTAR_TELEPORT("Mind Altar Teleport", 28, 22.0f, 1256, true),
    EARTH_BOLT("Earth Bolt", 29, 19.5f, 29, false),
    ENCHANT_TOPAZ_BOLT("Enchant Topaz Bolt", 29, 33.0f, 358, true),
    LUMBRIDGE_TELEPORT("Lumbridge Teleport", 31, 41.0f, 30, false),
    TELEKINETIC_GRAB("Telekinetic Grab", 33, 43.0f, 31, false),
    RESPAWN_TELEPORT("Respawn Teleport", 34, 27.0f, 1257, true),
    FIRE_BOLT("Fire Bolt", 35, 22.5f, 32, false),
    GHOSTLY_GRASP("Ghostly Grasp", 35, 22.5f, 1267, true),
    FALADOR_TELEPORT("Falador Teleport", 37, 48.0f, 33, false),
    RESURRECT_LESSER_THRALL("Resurrect Lesser Thrall", 38, 55.0f, 1270, true),
    CRUMBLE_UNDEAD("Crumble Undead", 39, 24.5f, 34, false),
    SALVE_GRAVEYARD_TELEPORT("Salve Graveyard Teleport", 40, 30.0f, 1254, true),
    TELEPORT_TO_HOUSE("Teleport To House", 40, 30.0f, 355, true),
    WIND_BLAST("Wind Blast", 41, 25.5f, 35, false),
    ADEPT_REANIMATION("Adept Reanimation", 41, 80.0f, 1248, true),
    SUPERHEAT_ITEM("Superheat Item", 43, 53.0f, 36, false),
    INFERIOR_DEMONBANE("Inferior Demonbane", 44, 27.0f, 1302, true),
    CAMELOT_TELEPORT("Camelot Teleport", 45, 55.5f, 37, true),
    WATER_BLAST("Water Blast", 47, 28.5f, 38, false),
    SHADOW_VEIL("Shadow Veil", 47, 58.0f, 1315, true),
    FENKENSTRAINS_CASTLE_TELEPORT("Fenkenstrain's Castle Teleport", 48, 50.0f, 1259, true),
    KOUREND_CASTLE_TELEPORT("Kourend Castle Teleport", 48, 58.0f, 360, true),
    ENCHANT_RUBY_BOLT("Enchant Ruby Bolt", 49, 59.0f, 358, true),
    ENCHANT_RUBY_JEWELLERY("Enchant Ruby Jewellery", 49, 59.0f, 39, false),
    IBAN_BLAST("Iban Blast", 50, 30.0f, 53, true),
    MAGIC_DART("Magic Dart", 50, 30.0f, 324, true),
    SMOKE_RUSH("Smoke Rush", 50, 30.0f, 329, true),
    DARK_LURE("Dark Lure", 50, 60.0f, 1316, true),
    SNARE("Snare", 50, 60.0f, 320, false),
    ARDOUGNE_TELEPORT("Ardougne Teleport", 51, 61.0f, 54, true),
    SHADOW_RUSH("Shadow Rush", 52, 31.0f, 337, true),
    EARTH_BLAST("Earth Blast", 53, 31.5f, 40, false),
    CIVITAS_ILLA_FORTIS_TELEPORT("Civitas illa Fortis Teleport", 54, 64.0f, 367, true),
    PADDEWWA_TELEPORT("Paddewwa Teleport", 54, 64.0f, 341, true),
    HIGH_LEVEL_ALCHEMY("High Level Alchemy", 55, 65.0f, 41, false),
    BLOOD_RUSH("Blood Rush", 56, 33.0f, 333, true),
    SKELETAL_GRASP("Skeletal Grasp", 56, 33.0f, 1268, true),
    CHARGE_WATER_ORB("Charge Water Orb", 56, 66.0f, 42, true),
    ENCHANT_DIAMOND_BOLT("Enchant Diamond Bolt", 57, 67.0f, 358, true),
    ENCHANT_DIAMOND_JEWELLERY("Enchant Diamond Jewellery", 57, 67.0f, 43, false),
    RESURRECT_SUPERIOR_THRALL("Resurrect Superior Thrall", 57, 70.0f, 2981, true),
    ICE_RUSH("Ice Rush", 58, 34.0f, 325, true),
    WATCHTOWER_TELEPORT("Watchtower Teleport", 58, 68.0f, 55, true),
    FIRE_BLAST("Fire Blast", 59, 34.5f, 44, false),
    MARK_OF_DARKNESS("Mark of Darkness", 59, 70.0f, 1305, true),
    CLAWS_OF_GUTHIX("Claws Of Guthix", 60, 35.0f, 60, true),
    FLAMES_OF_ZAMORAK("Flames Of Zamorak", 60, 35.0f, 59, true),
    SARADOMIN_STRIKE("Saradomin Strike", 60, 35.0f, 61, true),
    BONES_TO_PEACHES("Bones To Peaches", 60, 35.5f, 354, true),
    CHARGE_EARTH_ORB("Charge Earth Orb", 60, 70.0f, 45, true),
    SENNTISTEN_TELEPORT("Senntisten Teleport", 60, 70.0f, 342, true),
    TROLLHEIM_TELEPORT("Trollheim Teleport", 61, 68.0f, 323, true),
    WEST_ARDOUGNE_TELEPORT("West Ardougne Teleport", 61, 68.0f, 1260, true),
    SMOKE_BURST("Smoke Burst", 62, 36.0f, 330, true),
    SUPERIOR_DEMONBANE("Superior Demonbane", 62, 36.0f, 1303, true),
    WIND_WAVE("Wind Wave", 62, 36.0f, 46, true),
    CHARGE_FIRE_ORB("Charge Fire Orb", 63, 73.0f, 47, true),
    SHADOW_BURST("Shadow Burst", 64, 37.0f, 338, true),
    TELEPORT_APE_ATOLL("Teleport Ape Atoll", 64, 74.0f, 357, true),
    LESSER_CORRUPTION("Lesser Corruption", 64, 75.0f, 1307, true),
    WATER_WAVE("Water Wave", 65, 37.5f, 48, true),
    BAKE_PIE("Bake Pie", 65, 60.0f, 543, true),
    GEOMANCY("Geomancy", 65, 60.0f, 563, true),
    HARMONY_ISLAND_TELEPORT("Harmony Island Teleport", 65, 74.0f, 1261, true),
    CURE_PLANT("Cure Plant", 66, 60.0f, 567, true),
    MONSTER_EXAMINE("Monster Examine", 66, 61.0f, 577, true),
    CHARGE_AIR_ORB("Charge Air Orb", 66, 76.0f, 49, true),
    KHARYRLL_TELEPORT("Kharyrll Teleport", 66, 76.0f, 343, true),
    VILE_VIGOUR("Vile Vigour", 66, 76.0f, 1317, true),
    VULNERABILITY("Vulnerability", 66, 76.0f, 56, true),
    NPC_CONTACT("Npc Contact", 67, 63.0f, 568, true),
    BLOOD_BURST("Blood Burst", 68, 39.0f, 334, true),
    CURE_OTHER("Cure Other", 68, 65.0f, 559, true),
    HUMIDIFY("Humidify", 68, 65.0f, 578, true),
    ENCHANT_DRAGONSTONE_BOLT("Enchant Dragonstone Bolt", 68, 78.0f, 358, true),
    ENCHANT_DRAGONSTONE_JEWELLERY("Enchant Dragonstone Jewellery", 68, 78.0f, 50, true),
    MOONCLAN_TELEPORT("Moonclan Teleport", 69, 66.0f, 544, true),
    EARTH_WAVE("Earth Wave", 70, 40.0f, 51, true),
    ICE_BURST("Ice Burst", 70, 40.0f, 326, true),
    TELE_GROUP_MOONCLAN("Tele Group Moonclan", 70, 67.0f, 569, true),
    DEGRIME("Degrime", 70, 83.0f, 1318, true),
    CURE_ME("Cure Me", 71, 69.0f, 562, true),
    OURANIA_TELEPORT("Ourania Teleport", 71, 69.0f, 586, true),
    HUNTER_KIT("Hunter Kit", 71, 70.0f, 579, true),
    CEMETERY_TELEPORT("Cemetery Teleport", 71, 82.0f, 1264, true),
    WATERBIRTH_TELEPORT("Waterbirth Teleport", 72, 71.0f, 545, true),
    LASSAR_TELEPORT("Lassar Teleport", 72, 82.0f, 344, true),
    EXPERT_REANIMATION("Expert Reanimation", 72, 138.0f, 1249, true),
    TELE_GROUP_WATERBIRTH("Tele Group Waterbirth", 73, 72.0f, 570, true),
    ENFEEBLE("Enfeeble", 73, 83.0f, 57, true),
    WARD_OF_ARCEUUS("Ward of Arceuus", 73, 83.0f, 1306, true),
    SMOKE_BLITZ("Smoke Blitz", 74, 42.0f, 331, true),
    CURE_GROUP("Cure Group", 74, 74.0f, 565, true),
    TELEOTHER_LUMBRIDGE("Teleother Lumbridge", 74, 84.0f, 349, true),
    FIRE_WAVE("Fire Wave", 75, 42.5f, 52, true),
    BARBARIAN_TELEPORT("Barbarian Teleport", 75, 76.0f, 547, true),
    STAT_SPY("Stat Spy", 75, 76.0f, 576, true),
    SHADOW_BLITZ("Shadow Blitz", 76, 43.0f, 339, true),
    SPIN_FLAX("Spin Flax", 76, 75.0f, 585, true),
    TELE_GROUP_BARBARIAN("Tele Group Barbarian", 76, 77.0f, 575, true),
    RESURRECT_GREATER_THRALL("Resurrect Greater Thrall", 76, 88.0f, 2984, true),
    SUPERGLASS_MAKE("Superglass Make", 77, 78.0f, 548, true),
    KHAZARD_TELEPORT("Khazard Teleport", 78, 80.0f, 549, true),
    TAN_LEATHER("Tan Leather", 78, 81.0f, 583, true),
    DAREEYAK_TELEPORT("Dareeyak Teleport", 78, 88.0f, 345, true),
    RESURRECT_CROPS("Resurrect Crops", 78, 90.0f, 1266, true),
    UNDEAD_GRASP("Undead Grasp", 79, 46.5f, 1269, true),
    TELE_GROUP_KHAZARD("Tele Group Khazard", 79, 81.0f, 572, true),
    DREAM("Dream", 79, 82.0f, 580, true),
    ENTANGLE("Entangle", 79, 89.0f, 321, true),
    BLOOD_BLITZ("Blood Blitz", 80, 45.0f, 335, true),
    STRING_JEWELLERY("String Jewellery", 80, 83.0f, 550, true),
    DEATH_CHARGE("Death Charge", 80, 90.0f, 1310, true),
    STUN("Stun", 80, 90.0f, 58, true),
    CHARGE("Charge", 80, 180.0f, 322, true),
    WIND_SURGE("Wind Surge", 81, 44.5f, 362, true),
    STAT_RESTORE_POT_SHARE("Stat Restore Pot Share", 81, 84.0f, 554, true),
    DARK_DEMONBANE("Dark Demonbane", 82, 43.5f, 1304, true),
    ICE_BLITZ("Ice Blitz", 82, 46.0f, 327, true),
    MAGIC_IMBUE("Magic Imbue", 82, 86.0f, 552, true),
    TELEOTHER_FALADOR("Teleother Falador", 82, 92.0f, 350, true),
    FERTILE_SOIL("Fertile Soil", 83, 87.0f, 553, true),
    BARROWS_TELEPORT("Barrows Teleport", 83, 90.0f, 1262, true),
    CARRALLANGER_TELEPORT("Carrallanger Teleport", 84, 82.0f, 346, true),
    BOOST_POTION_SHARE("Boost Potion Share", 84, 88.0f, 551, true),
    DEMONIC_OFFERING("Demonic Offering", 84, 175.0f, 1311, true),
    TELEPORT_TO_TARGET("Teleport To Target", 85, 45.0f, 359, true),
    WATER_SURGE("Water Surge", 85, 46.5f, 363, true),
    TELE_BLOCK("Tele Block", 85, 80.0f, 352, false),
    FISHING_GUILD_TELEPORT("Fishing Guild Teleport", 85, 89.0f, 555, true),
    GREATER_CORRUPTION("Greater Corruption", 85, 95.0f, 1308, true),
    SMOKE_BARRAGE("Smoke Barrage", 86, 48.0f, 332, true),
    PLANK_MAKE("Plank Make", 86, 90.0f, 581, true),
    TELE_GROUP_FISHING_GUILD("Tele Group Fishing Guild", 86, 90.0f, 573, true),
    CATHERBY_TELEPORT("Catherby Teleport", 87, 92.0f, 556, true),
    ENCHANT_ONYX_BOLT("Enchant Onyx Bolt", 87, 97.0f, 358, true),
    ENCHANT_ONYX_JEWELLERY("Enchant Onyx Jewellery", 87, 97.0f, 353, true),
    SHADOW_BARRAGE("Shadow Barrage", 88, 48.0f, 340, true),
    TELE_GROUP_CATHERBY("Tele Group Catherby", 88, 93.0f, 574, true),
    ICE_PLATEAU_TELEPORT("Ice Plateau Teleport", 89, 96.0f, 557, true),
    RECHARGE_DRAGONSTONE("Recharge Dragonstone", 89, 97.5f, 584, true),
    EARTH_SURGE("Earth Surge", 90, 48.5f, 364, true),
    TELE_GROUP_ICE_PLATEAU("Tele Group Ice Plateau", 90, 99.0f, 575, true),
    ANNAKARL_TELEPORT("Annakarl Teleport", 90, 100.0f, 347, true),
    APE_ATOLL_TELEPORT("Ape Atoll Teleport", 90, 100.0f, 1263, true),
    TELEOTHER_CAMELOT("Teleother Camelot", 90, 100.0f, 351, true),
    MASTER_REANIMATION("Master Reanimation", 90, 170.0f, 1250, true),
    ENERGY_TRANSFER("Energy Transfer", 91, 100.0f, 558, true),
    BLOOD_BARRAGE("Blood Barrage", 92, 51.0f, 336, true),
    HEAL_OTHER("Heal Other", 92, 101.0f, 560, true),
    SINISTER_OFFERING("Sinister Offering", 92, 180.0f, 1312, true),
    VENGEANCE_OTHER("Vengeance Other", 93, 108.0f, 561, true),
    ENCHANT_ZENYTE_JEWELLERY("Enchant Zenyte Jewellery", 93, 110.0f, 361, true),
    ICE_BARRAGE("Ice Barrage", 94, 52.0f, 328, true),
    VENGEANCE("Vengeance", 94, 112.0f, 564, true),
    FIRE_SURGE("Fire Surge", 95, 50.5f, 365, true),
    HEAL_GROUP("Heal Group", 95, 124.0f, 566, true),
    GHORROCK_TELEPORT("Ghorrock Teleport", 96, 106.0f, 348, true),
    SPELLBOOK_SWAP("Spellbook Swap", 96, 130.0f, 582, true);

    private final String name;
    private final int level;
    private final float xp;
    private final int sprite;
    private final boolean isMembers;

    @Override
    public String getName(ItemManager itemManager) {
        return this.getName();
    }

    @Override
    public boolean isMembers(ItemManager itemManager) {
        return this.isMembers();
    }

    private MagicAction(String name, int level, float xp, int sprite, boolean isMembers) {
        this.name = name;
        this.level = level;
        this.xp = xp;
        this.sprite = sprite;
        this.isMembers = isMembers;
    }

    public String getName() {
        return this.name;
    }

    @Override
    public int getLevel() {
        return this.level;
    }

    @Override
    public float getXp() {
        return this.xp;
    }

    @Override
    public int getSprite() {
        return this.sprite;
    }

    public boolean isMembers() {
        return this.isMembers;
    }
}

