/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap$Builder
 *  net.runelite.api.Client
 *  net.runelite.api.coords.WorldPoint
 */
package net.runelite.client.plugins.agility;

import com.google.common.collect.ImmutableMap;
import java.util.Map;
import java.util.function.Function;
import net.runelite.api.Client;
import net.runelite.api.coords.WorldPoint;

enum Courses {
    GNOME(110.5, 9781, new WorldPoint(2484, 3437, 0), new WorldPoint(2487, 3437, 0)),
    SHAYZIEN_BASIC(153.5, 6200, new WorldPoint(1554, 3640, 0)),
    DRAYNOR(120.0, 12338, new WorldPoint(3103, 3261, 0)),
    AL_KHARID(216.0, 13105, new WorldPoint(3299, 3194, 0)),
    PYRAMID(722.0, 13356, new WorldPoint(3364, 2830, 0)),
    VARROCK(270.0, 12853, new WorldPoint(3236, 3417, 0)),
    PENGUIN(540.0, 10559, new WorldPoint(2652, 4039, 1)),
    BARBARIAN(153.2, 10039, new WorldPoint(2543, 3553, 0)),
    CANIFIS(240.0, 13878, new WorldPoint(3510, 3485, 0)),
    APE_ATOLL(580.0, 11050, new WorldPoint(2770, 2747, 0)),
    SHAYZIEN_ADVANCED(507.5, 5944, new WorldPoint(1522, 3625, 0)),
    FALADOR(586.0, 12084, new WorldPoint(3029, 3332, 0), new WorldPoint(3029, 3333, 0), new WorldPoint(3029, 3334, 0), new WorldPoint(3029, 3335, 0)),
    COLOSSAL_WYRM_BASIC(504.0),
    WILDERNESS(571.4, 11837, new WorldPoint(2993, 3933, 0), new WorldPoint(2994, 3933, 0), new WorldPoint(2995, 3933, 0)),
    WEREWOLF(730.0, 14234, new WorldPoint(3528, 9873, 0)),
    SEERS(570.0, 10806, new WorldPoint(2704, 3464, 0)),
    COLOSSAL_WYRM_ADVANCED(685.0),
    POLLNIVNEACH(client -> client.getVarbitValue(4485) == 1 ? 1016.0 : 890.0, 13358, new WorldPoint(3363, 2998, 0)),
    RELLEKA(client -> client.getVarbitValue(4493) == 1 ? 920.0 : 780.0, 10553, new WorldPoint(2653, 3676, 0)),
    PRIFDDINAS(1337.0, 12895, new WorldPoint(3240, 6109, 0)),
    ARDOUGNE(889.0, 10547, new WorldPoint(2668, 3297, 0));

    private static final Map<Integer, Courses> coursesByRegion;
    private final Function<Client, Double> totalXpProvider;
    private final int regionId;
    private final WorldPoint[] courseEndWorldPoints;

    private Courses(double totalXp) {
        this(totalXp, -1, null);
    }

    private Courses(double totalXp, int regionId, WorldPoint ... courseEndWorldPoints) {
        this(client -> totalXp, regionId, courseEndWorldPoints);
    }

    private Courses(Function<Client, Double> totalXpProvider, int regionId, WorldPoint ... courseEndWorldPoints) {
        this.totalXpProvider = totalXpProvider;
        this.regionId = regionId;
        this.courseEndWorldPoints = courseEndWorldPoints;
    }

    static Courses getCourse(int regionId) {
        return coursesByRegion.get(regionId);
    }

    public Function<Client, Double> getTotalXpProvider() {
        return this.totalXpProvider;
    }

    public int getRegionId() {
        return this.regionId;
    }

    public WorldPoint[] getCourseEndWorldPoints() {
        return this.courseEndWorldPoints;
    }

    static {
        ImmutableMap.Builder builder = new ImmutableMap.Builder();
        for (Courses course : Courses.values()) {
            if (course.regionId == -1) continue;
            builder.put((Object)course.regionId, (Object)course);
        }
        coursesByRegion = builder.build();
    }
}

