/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.plugins.skillcalculator.skills;

import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.skillcalculator.skills.ItemSkillAction;

public enum CookingAction implements ItemSkillAction
{
    SINEW(9436, 1, 3.0f),
    ANCHOVIES(319, 1, 30.0f),
    COOKED_CHICKEN(2140, 1, 30.0f),
    COOKED_MEAT(2142, 1, 30.0f),
    COOKED_RABBIT(3228, 1, 30.0f),
    SHRIMPS(315, 1, 30.0f),
    BREAD(2309, 1, 40.0f),
    SARDINE(325, 1, 40.0f),
    UGTHANKI_MEAT(1861, 1, 40.0f),
    POISON_KARAMBWAN(3146, 1, 80.0f),
    HERRING(347, 5, 50.0f),
    FRUIT_BLAST(2084, 6, 50.0f),
    GUPPY(25654, 7, 12.0f),
    BAKED_POTATO(6701, 7, 15.0f),
    PINEAPPLE_PUNCH(2048, 8, 70.0f),
    SPICY_SAUCE(7072, 9, 25.0f),
    MACKEREL(355, 10, 60.0f),
    REDBERRY_PIE(2325, 10, 78.0f),
    TOAD_CRUNCHIES(2217, 10, 100.0f),
    CHILLI_CON_CARNE(7062, 11, 55.0f),
    ROAST_BIRD_MEAT(9980, 11, 62.5f),
    THIN_SNAIL_MEAT(3369, 12, 70.0f),
    SPICY_CRUNCHIES(2213, 12, 100.0f),
    SCRAMBLED_EGG(7078, 13, 50.0f),
    WORM_CRUNCHIES(2205, 14, 104.0f),
    CIDER(5763, 14, 182.0f),
    TROUT(333, 15, 70.0f),
    ROAST_RABBIT(7223, 16, 72.5f),
    SPIDER_ON_SHAFT(6295, 16, 80.0f),
    SPIDER_ON_STICK(6293, 16, 80.0f),
    CHOCCHIP_CRUNCHIES(2209, 16, 100.0f),
    LEAN_SNAIL_MEAT(3371, 17, 80.0f),
    COD(339, 18, 75.0f),
    WIZARD_BLIZZARD(2054, 18, 110.0f),
    DWARVEN_STOUT(1913, 19, 215.0f, true),
    CAVEFISH(25660, 20, 23.0f),
    CUP_OF_TEA(712, 20, 52.0f),
    PIKE(351, 20, 80.0f),
    MEAT_PIE(2327, 20, 110.0f),
    SHORT_GREEN_GUY(2080, 20, 120.0f),
    SERVERY_MEAT_PIE(13403, 20, 160.0f),
    POT_OF_CREAM(2130, 21, 18.0f),
    ROAST_BEAST_MEAT(9988, 21, 82.5f),
    COOKED_CRAB_MEAT(7521, 21, 100.0f),
    FAT_SNAIL_MEAT(3373, 22, 95.0f),
    EGG_AND_TOMATO(7064, 23, 50.0f),
    COOKED_WILD_KEBBIT(29128, 23, 73.0f),
    ASGARNIAN_ALE(1905, 24, 248.0f, true),
    SALMON(329, 25, 90.0f),
    STEW(2003, 25, 117.0f),
    FRUIT_BATTA(2277, 25, 150.0f),
    SERVERY_STEW(13418, 25, 168.0f),
    TOAD_BATTA(2255, 26, 152.0f),
    WORM_BATTA(2253, 27, 154.0f),
    COOKED_SLIMY_EEL(3381, 28, 95.0f),
    SWEETCORN(5988, 28, 104.0f),
    VEGETABLE_BATTA(2281, 28, 156.0f),
    MUD_PIE(7170, 29, 128.0f),
    CHEESE_AND_TOMATO_BATTA(2259, 29, 158.0f),
    GREENMANS_ALE(1909, 29, 281.0f),
    COOKED_BREAM(29217, 30, 45.0f),
    COOKED_MOSS_LIZARD(29077, 30, 60.0f),
    ROASTED_CHOMPY(2878, 30, 100.0f),
    TUNA(361, 30, 100.0f),
    APPLE_PIE(2323, 30, 130.0f),
    WORM_HOLE(2191, 30, 170.0f),
    COOKED_KARAMBWAN(3144, 30, 190.0f),
    COOKED_LARUPIA(29146, 31, 92.0f),
    FISHCAKE(7530, 31, 100.0f),
    COOKED_BARBTAILED_KEBBIT(29131, 32, 106.0f),
    DRUNK_DRAGON(2092, 32, 160.0f),
    TETRA(25666, 33, 31.0f),
    CHOC_SATURDAY(2074, 33, 170.0f),
    GARDEN_PIE(7178, 34, 138.0f),
    WIZARDS_MIND_BOMB(1907, 34, 314.0f, true),
    RAINBOW_FISH(10136, 35, 110.0f),
    PLAIN_PIZZA(2289, 35, 143.0f),
    VEG_BALL(2195, 35, 175.0f),
    JUG_OF_WINE(1993, 35, 200.0f),
    BLURBERRY_SPECIAL(2064, 37, 180.0f),
    PAT_OF_BUTTER(6697, 38, 40.5f),
    CAVE_EEL(5003, 38, 115.0f),
    POTATO_WITH_BUTTER(6703, 39, 40.0f),
    DRAGON_BITTER(1911, 39, 347.0f),
    LOBSTER(379, 40, 120.0f),
    CAKE(1891, 40, 180.0f),
    TANGLED_TOADS_LEGS(2187, 40, 185.0f),
    COOKED_GRAAHK(29149, 41, 124.0f),
    COOKED_JUBBLY(7568, 41, 160.0f),
    CHILLI_POTATO(7054, 41, 165.5f),
    FRIED_ONIONS(7084, 42, 60.0f),
    CHOCOLATE_BOMB(2185, 42, 190.0f),
    BASS(365, 43, 130.0f),
    MOONLIGHT_MEAD(2955, 44, 380.0f),
    SWORDFISH(373, 45, 140.0f),
    MEAT_PIZZA(2293, 45, 169.0f),
    CATFISH(25672, 46, 43.0f),
    FRIED_MUSHROOMS(7082, 46, 60.0f),
    POTATO_WITH_CHEESE(6705, 47, 40.0f),
    FISH_PIE(7188, 47, 164.0f),
    CHEESE(1985, 48, 64.0f, true),
    AXEMANS_FOLLY(5751, 49, 413.0f),
    COOKED_OOMLIE_WRAP(2343, 50, 30.0f),
    CHOCOLATE_CAKE(1897, 50, 210.0f),
    COOKED_KYATT(29152, 51, 143.0f),
    EGG_POTATO(7056, 51, 195.5f),
    BOTANICAL_PIE(19662, 52, 180.0f),
    LAVA_EEL(2149, 53, 30.0f),
    CHEFS_DELIGHT(5755, 54, 446.0f),
    ANCHOVY_PIZZA(2297, 55, 182.0f),
    MUSHROOM_AND_ONION(7066, 57, 120.0f),
    PITTA_BREAD(1865, 58, 40.0f),
    UGTHANKI_KEBAB_FRESH(1883, 58, 80.0f),
    PYRE_FOX(29137, 59, 154.0f),
    SLAYERS_RESPITE(5759, 59, 479.0f),
    MUSHROOM_PIE(21690, 60, 200.0f),
    CURRY(2011, 60, 280.0f),
    MONKFISH(7946, 62, 150.0f),
    MUSHROOM_POTATO(7058, 64, 270.5f),
    PINEAPPLE_PIZZA(2301, 65, 188.0f),
    WINE_OF_ZAMORAK(245, 65, 200.0f, true),
    SERVERY_PINEAPPLE_PIZZA(13412, 65, 369.0f),
    TUNA_AND_CORN(7068, 67, 204.0f),
    COOKED_SUNLIGHT_ANTELOPE(29140, 68, 175.0f),
    TUNA_POTATO(7060, 68, 309.5f),
    ADMIRAL_PIE(7198, 70, 210.0f),
    SACRED_EEL(13339, 72, 109.0f),
    DRAGONFRUIT_PIE(22795, 73, 220.0f),
    SHARK(385, 80, 210.0f),
    SEA_TURTLE(397, 82, 211.3f),
    COOKED_DASHING_KEBBIT(29134, 82, 215.0f),
    ANGLERFISH(13441, 84, 230.0f),
    WILD_PIE(7208, 85, 240.0f),
    DARK_CRAB(11936, 90, 215.0f),
    MANTA_RAY(391, 91, 216.3f),
    COOKED_MOONLIGHT_ANTELOPE(29143, 92, 220.0f),
    SUMMER_PIE(7218, 95, 260.0f);

    private final int itemId;
    private final int level;
    private final float xp;
    private final boolean isMembersOverride;

    private CookingAction(int itemId, int level, float xp) {
        this(itemId, level, xp, false);
    }

    @Override
    public boolean isMembers(ItemManager itemManager) {
        return this.isMembersOverride() || ItemSkillAction.super.isMembers(itemManager);
    }

    private CookingAction(int itemId, int level, float xp, boolean isMembersOverride) {
        this.itemId = itemId;
        this.level = level;
        this.xp = xp;
        this.isMembersOverride = isMembersOverride;
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

    public boolean isMembersOverride() {
        return this.isMembersOverride;
    }
}

