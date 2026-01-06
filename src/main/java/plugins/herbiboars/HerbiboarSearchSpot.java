/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableMultimap
 *  com.google.common.collect.ImmutableMultimap$Builder
 *  com.google.common.collect.ImmutableSet$Builder
 *  net.runelite.api.coords.WorldPoint
 */
package net.runelite.client.plugins.herbiboars;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.herbiboars.TrailToSpot;

enum HerbiboarSearchSpot {
    A_MUSHROOM(Group.A, new WorldPoint(3670, 3889, 0), new TrailToSpot(5742, 1, 31318), new TrailToSpot(5743, 1, 31321)),
    A_PATCH(Group.A, new WorldPoint(3672, 3890, 0), new TrailToSpot(5738, 2, 31306)),
    B_SEAWEED(Group.B, new WorldPoint(3728, 3893, 0), new TrailToSpot(5741, 2, 31315), new TrailToSpot(5742, 2, 31318), new TrailToSpot(5769, 1, 31336), new TrailToSpot(5770, 1, 31339)),
    C_MUSHROOM(Group.C, new WorldPoint(3697, 3875, 0), new TrailToSpot(5737, 2, 31303)),
    C_PATCH(Group.C, new WorldPoint(3699, 3875, 0), new TrailToSpot(5740, 1, 31312), new TrailToSpot(5741, 1, 31315)),
    D_PATCH(Group.D, new WorldPoint(3708, 3876, 0), new TrailToSpot(5746, 1, 31330), new TrailToSpot(5768, 1, 31333)),
    D_SEAWEED(Group.D, new WorldPoint(3710, 3877, 0), new TrailToSpot(5740, 2, 31312), new TrailToSpot(5770, 2, 31339)),
    E_MUSHROOM(Group.E, new WorldPoint(3668, 3865, 0), new TrailToSpot(5771, 1, 31342), new TrailToSpot(5772, 1, 31345)),
    E_PATCH(Group.E, new WorldPoint(3667, 3862, 0), new TrailToSpot(5743, 2, 31321)),
    F_MUSHROOM(Group.F, new WorldPoint(3681, 3860, 0), new TrailToSpot(5744, 1, 31324), new TrailToSpot(5745, 1, 31327), new TrailToSpot(5771, 2, 31342)),
    F_PATCH(Group.F, new WorldPoint(3681, 3859, 0), new TrailToSpot(5739, 2, 31309)),
    G_MUSHROOM(Group.G, new WorldPoint(3694, 3847, 0), new TrailToSpot(5768, 2, 31333), new TrailToSpot(5775, 1, 31354)),
    G_PATCH(Group.G, new WorldPoint(3698, 3847, 0), new TrailToSpot(5745, 2, 31327)),
    H_SEAWEED_EAST(Group.H, new WorldPoint(3715, 3851, 0), new TrailToSpot(5776, 1, 31357), new TrailToSpot(5777, 1, 31360)),
    H_SEAWEED_WEST(Group.H, new WorldPoint(3713, 3850, 0), new TrailToSpot(5746, 2, 31330), new TrailToSpot(5747, 1, 31363)),
    I_MUSHROOM(Group.I, new WorldPoint(3680, 3838, 0), new TrailToSpot(5773, 1, 31348), new TrailToSpot(5774, 1, 31351)),
    I_PATCH(Group.I, new WorldPoint(3680, 3836, 0), new TrailToSpot(5744, 2, 31324), new TrailToSpot(5772, 2, 31345)),
    J_PATCH(Group.J, new WorldPoint(3713, 3840, 0), new TrailToSpot(5776, 2, 31357), new TrailToSpot(5750, 1, 31372)),
    K_PATCH(Group.K, new WorldPoint(3706, 3811, 0), new TrailToSpot(5773, 2, 31348), new TrailToSpot(5748, 1, 31366), new TrailToSpot(5749, 1, 31369));

    private static final ImmutableMultimap<Group, HerbiboarSearchSpot> GROUPS;
    private static final Set<WorldPoint> SPOTS;
    private static final Set<Integer> TRAILS;
    private final Group group;
    private final WorldPoint location;
    private final List<TrailToSpot> trails;

    private HerbiboarSearchSpot(Group group, WorldPoint location, TrailToSpot ... trails) {
        this.group = group;
        this.location = location;
        this.trails = ImmutableList.copyOf((Object[])trails);
    }

    static boolean isTrail(int id) {
        return TRAILS.contains(id);
    }

    static boolean isSearchSpot(WorldPoint location) {
        return SPOTS.contains(location);
    }

    static List<WorldPoint> getGroupLocations(Group group) {
        return GROUPS.get((Object)group).stream().map(HerbiboarSearchSpot::getLocation).collect(Collectors.toList());
    }

    public Group getGroup() {
        return this.group;
    }

    public WorldPoint getLocation() {
        return this.location;
    }

    public List<TrailToSpot> getTrails() {
        return this.trails;
    }

    static {
        ImmutableMultimap.Builder groupBuilder = new ImmutableMultimap.Builder();
        ImmutableSet.Builder spotBuilder = new ImmutableSet.Builder();
        ImmutableSet.Builder trailBuilder = new ImmutableSet.Builder();
        for (HerbiboarSearchSpot spot : HerbiboarSearchSpot.values()) {
            groupBuilder.put((Object)spot.getGroup(), (Object)spot);
            spotBuilder.add((Object)spot.getLocation());
            for (TrailToSpot trail : spot.getTrails()) {
                trailBuilder.addAll(trail.getFootprintIds());
            }
        }
        GROUPS = groupBuilder.build();
        SPOTS = spotBuilder.build();
        TRAILS = trailBuilder.build();
    }

    static enum Group {
        A,
        B,
        C,
        D,
        E,
        F,
        G,
        H,
        I,
        J,
        K;

    }
}

