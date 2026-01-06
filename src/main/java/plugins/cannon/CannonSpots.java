/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.runelite.api.coords.WorldPoint
 */
package net.runelite.client.plugins.cannon;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.runelite.api.coords.WorldPoint;

enum CannonSpots {
    ABERRANT_SPECTRES(new WorldPoint(2456, 9791, 0)),
    ANKOU(new WorldPoint(3177, 10193, 0), new WorldPoint(3360, 10077, 0)),
    BANDIT(new WorldPoint(3037, 3700, 0)),
    BEAR(new WorldPoint(3113, 3672, 0)),
    BLACK_DEMONS(new WorldPoint(2859, 9778, 0), new WorldPoint(2841, 9791, 0), new WorldPoint(1421, 10089, 1), new WorldPoint(3174, 10154, 0), new WorldPoint(3089, 9960, 0)),
    BLACK_DRAGON(new WorldPoint(3239, 10206, 0), new WorldPoint(3362, 10156, 0)),
    BLACK_KNIGHTS(new WorldPoint(2906, 9685, 0), new WorldPoint(3053, 3852, 0)),
    BLOODVELDS(new WorldPoint(2439, 9821, 0), new WorldPoint(2448, 9821, 0), new WorldPoint(2472, 9832, 0), new WorldPoint(2453, 9817, 0), new WorldPoint(3596, 9743, 0)),
    BLUE_DRAGON(new WorldPoint(1933, 8973, 1)),
    BRINE_RAT(new WorldPoint(2707, 10132, 0)),
    CAVE_HORROR(new WorldPoint(3785, 9460, 0)),
    DAGGANOTH(new WorldPoint(2524, 10020, 0), new WorldPoint(2478, 10443, 0), new WorldPoint(2420, 10425, 0)),
    DARK_BEAST(new WorldPoint(1992, 4655, 0)),
    DARK_WARRIOR(new WorldPoint(3030, 3632, 0)),
    DUST_DEVIL(new WorldPoint(3218, 9366, 0)),
    EARTH_WARRIOR(new WorldPoint(3120, 9987, 0)),
    ELDER_CHAOS_DRUID(new WorldPoint(3237, 3622, 0)),
    ELVES(new WorldPoint(3278, 6098, 0)),
    FIRE_GIANTS(new WorldPoint(2393, 9782, 0), new WorldPoint(2412, 9776, 0), new WorldPoint(2401, 9780, 0), new WorldPoint(3047, 10340, 0)),
    GREATER_DEMONS(new WorldPoint(1435, 10086, 2), new WorldPoint(3224, 10132, 0), new WorldPoint(3427, 10149, 0)),
    GREEN_DRAGON(new WorldPoint(3225, 10068, 0), new WorldPoint(3399, 10122, 0)),
    HELLHOUNDS(new WorldPoint(2431, 9776, 0), new WorldPoint(2413, 9786, 0), new WorldPoint(2783, 9686, 0), new WorldPoint(3198, 10071, 0)),
    HILL_GIANT(new WorldPoint(3044, 10318, 0)),
    ICE_GIANT(new WorldPoint(3207, 10164, 0), new WorldPoint(3339, 10056, 0)),
    ICE_WARRIOR(new WorldPoint(2955, 3876, 0)),
    KALPHITE(new WorldPoint(3307, 9528, 0)),
    LESSER_DEMON(new WorldPoint(2838, 9559, 0), new WorldPoint(3163, 10114, 0), new WorldPoint(3338, 10134, 0)),
    LIZARDMEN(new WorldPoint(1507, 3705, 0)),
    LIZARDMEN_SHAMAN(new WorldPoint(1423, 3715, 0)),
    MAGIC_AXE(new WorldPoint(3190, 3960, 0)),
    MAMMOTH(new WorldPoint(3168, 3595, 0)),
    MINIONS_OF_SCARABAS(new WorldPoint(3297, 9252, 0)),
    MOSS_GIANT(new WorldPoint(3159, 9903, 0)),
    ROGUE(new WorldPoint(3285, 3930, 0)),
    SCORPION(new WorldPoint(3233, 10335, 0)),
    SKELETON(new WorldPoint(3017, 3589, 0)),
    SMOKE_DEVIL(new WorldPoint(2398, 9444, 0)),
    SPIDER(new WorldPoint(3169, 3886, 0)),
    SUQAHS(new WorldPoint(2114, 3943, 0)),
    TROLLS(new WorldPoint(2401, 3856, 0), new WorldPoint(1242, 3517, 0)),
    WARPED_CREATURES(new WorldPoint(1490, 4263, 1)),
    WYRMS(new WorldPoint(1368, 9695, 0)),
    ZOMBIE(new WorldPoint(3172, 3677, 0));

    private static final List<WorldPoint> cannonSpots;
    private final WorldPoint[] spots;

    private CannonSpots(WorldPoint ... spots) {
        this.spots = spots;
    }

    public static List<WorldPoint> getCannonSpots() {
        return cannonSpots;
    }

    static {
        cannonSpots = new ArrayList<WorldPoint>();
        for (CannonSpots cannonSpot : CannonSpots.values()) {
            cannonSpots.addAll(Arrays.asList(cannonSpot.spots));
        }
    }
}

