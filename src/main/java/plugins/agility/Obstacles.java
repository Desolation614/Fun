/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableMultimap
 *  com.google.common.collect.ImmutableMultimap$Builder
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.Multimap
 */
package net.runelite.client.plugins.agility;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multimap;
import java.util.List;
import java.util.Set;
import net.runelite.client.game.AgilityShortcut;

class Obstacles {
    static final Set<Integer> OBSTACLE_IDS = ImmutableSet.of((Object)23134, (Object)23559, (Object)23560, (Object)23135, (Object)23138, (Object)23139, (Object[])new Integer[]{23145, 23557, 3572, 3571, 3570, 3566, 3578, 3565, 3553, 3557, 3561, 3559, 3564, 3551, 3583, 11404, 11405, 11406, 11430, 11630, 11631, 11632, 7527, 11633, 14398, 14402, 14403, 14404, 11634, 14409, 14399, 10857, 10865, 10860, 10868, 10882, 10886, 10857, 10884, 10859, 10861, 10865, 10859, 10888, 10868, 10851, 10855, 14412, 14413, 14414, 14832, 14833, 14834, 14835, 14836, 14841, 21120, 21126, 21128, 21129, 21130, 21131, 21132, 21133, 21134, 21148, 21149, 21150, 21151, 21152, 21153, 21154, 21155, 21156, 21172, 23131, 23144, 20211, 23547, 42487, 1948, 14843, 14844, 14845, 14848, 14846, 14894, 14847, 14897, 15412, 15414, 15417, 1747, 15487, 16062, 14898, 14899, 14901, 14903, 14904, 14905, 14911, 14919, 14920, 14921, 14922, 14923, 14924, 14925, 23137, 23132, 23556, 23542, 23640, 14927, 14928, 14932, 14929, 14930, 14931, 22569, 22572, 22564, 22552, 22557, 22664, 22634, 22635, 22650, 22651, 22609, 22608, 14935, 14936, 14937, 14938, 14939, 14940, 14941, 14944, 14945, 14946, 14947, 14987, 14990, 14991, 14992, 14994, 15608, 15609, 26635, 15610, 15611, 28912, 15612, 12945, 17958, 17959, 17960, 17961, 18122, 18124, 18037, 18038, 18070, 18071, 18072, 18073, 18129, 18130, 18078, 18132, 18133, 18083, 18085, 18086, 18087, 18088, 18089, 18090, 18091, 18093, 18094, 18095, 18096, 18097, 18098, 18099, 18100, 18135, 18136, 18105, 18106, 18107, 18108, 18109, 18110, 18112, 18111, 18114, 18113, 18116, 18117, 18118, 17978, 17980, 18054, 17999, 18000, 18001, 18002, 18056, 39172, 39173, 11643, 11638, 11639, 11640, 11657, 1747, 11644, 11645, 11646, 36221, 36225, 36227, 36228, 36229, 36231, 36232, 36233, 36234, 36235, 36236, 36237, 36238, 4551, 4553, 4554, 4556, 4558, 4559, 4557, 4555, 4552, 4550, 42209, 42211, 42212, 42213, 42214, 42215, 42216, 42217, 42218, 42219, 42220, 42221, 44595, 55178, 55180, 55179, 55184, 55186, 55190, 55191, 55192, 55194, 60228});
    static final Set<Integer> PORTAL_OBSTACLE_IDS = ImmutableSet.of((Object)36241, (Object)36242, (Object)36243, (Object)36244, (Object)36245, (Object)36246, (Object[])new Integer[0]);
    static final Multimap<Integer, AgilityShortcut> SHORTCUT_OBSTACLE_IDS;
    static final Set<Integer> TRAP_OBSTACLE_IDS;
    static final List<Integer> TRAP_OBSTACLE_REGIONS;
    static final Set<Integer> SEPULCHRE_OBSTACLE_IDS;
    static final Set<Integer> SEPULCHRE_SKILL_OBSTACLE_IDS;

    Obstacles() {
    }

    static {
        TRAP_OBSTACLE_IDS = ImmutableSet.of((Object)3550, (Object)10872, (Object)10873);
        TRAP_OBSTACLE_REGIONS = ImmutableList.of((Object)12105, (Object)13356);
        ImmutableMultimap.Builder builder = ImmutableMultimap.builder();
        for (AgilityShortcut item : AgilityShortcut.values()) {
            for (int obstacle : item.getObstacleIds()) {
                builder.put((Object)obstacle, (Object)item);
            }
        }
        SHORTCUT_OBSTACLE_IDS = builder.build();
        SEPULCHRE_OBSTACLE_IDS = ImmutableSet.of((Object)38460, (Object)38455, (Object)38456, (Object)38457, (Object)38458, (Object)38459, (Object[])new Integer[]{38470, 38477, 38462, 38463, 38464, 38465, 38466, 38467, 38468, 38469, 38471, 38472, 38473, 38474, 38475, 38476});
        SEPULCHRE_SKILL_OBSTACLE_IDS = ImmutableSet.of((Object)39524, (Object)39525, (Object)39526, (Object)39527, (Object)39528, (Object)39533, (Object[])new Integer[0]);
    }
}

