/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.plugins.skillcalculator.skills;

import net.runelite.client.plugins.skillcalculator.skills.ItemSkillAction;

public enum FishingAction implements ItemSkillAction
{
    RAW_SHRIMPS(317, 1, 10.0f),
    RAW_KARAMBWANJI(3150, 5, 5.0f),
    RAW_SARDINE(327, 5, 20.0f),
    RAW_GUPPY(25652, 7, 8.0f),
    RAW_HERRING(345, 10, 30.0f),
    RAW_ANCHOVIES(321, 15, 40.0f),
    RAW_MACKEREL(353, 16, 20.0f),
    RAW_CAVEFISH(25658, 20, 16.0f),
    RAW_BREAM(29216, 20, 20.0f),
    RAW_TROUT(335, 20, 50.0f),
    RAW_COD(341, 23, 45.0f),
    RAW_PIKE(349, 25, 60.0f),
    RAW_SLIMY_EEL(3379, 28, 80.0f),
    RAW_SALMON(331, 30, 70.0f),
    RAW_TETRA(25664, 33, 24.0f),
    RAW_TUNA(359, 35, 80.0f),
    RAW_CAVE_EEL(5001, 38, 80.0f),
    RAW_RAINBOW_FISH(10138, 38, 80.0f),
    RAW_LOBSTER(377, 40, 90.0f),
    RAW_CATFISH(25670, 46, 33.0f),
    RAW_BASS(363, 46, 100.0f),
    LEAPING_TROUT(11328, 48, 50.0f),
    RAW_SWORDFISH(371, 50, 100.0f),
    LEAPING_SALMON(11330, 58, 70.0f),
    RAW_MONKFISH(7944, 62, 120.0f),
    RAW_KARAMBWAN(3142, 65, 50.0f),
    LEAPING_STURGEON(11332, 70, 80.0f),
    RAW_SHARK(383, 76, 110.0f),
    RAW_SEA_TURTLE(395, 79, 38.0f),
    INFERNAL_EEL(21293, 80, 95.0f),
    RAW_MANTA_RAY(389, 81, 46.0f),
    MINNOW(21356, 82, 26.5f),
    RAW_ANGLERFISH(13439, 82, 120.0f),
    RAW_DARK_CRAB(11934, 85, 130.0f),
    SACRED_EEL(13339, 87, 105.0f);

    private final int itemId;
    private final int level;
    private final float xp;

    private FishingAction(int itemId, int level, float xp) {
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

