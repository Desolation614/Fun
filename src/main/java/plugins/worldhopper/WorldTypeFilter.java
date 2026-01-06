/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Sets
 *  net.runelite.http.api.worlds.WorldType
 */
package net.runelite.client.plugins.worldhopper;

import com.google.common.collect.Sets;
import java.util.EnumSet;
import java.util.Set;
import net.runelite.http.api.worlds.WorldType;

enum WorldTypeFilter {
    NORMAL{

        @Override
        boolean matches(Set<WorldType> types) {
            EnumSet<WorldType> normal = EnumSet.of(WorldType.MEMBERS, WorldType.SKILL_TOTAL, WorldType.LAST_MAN_STANDING);
            EnumSet<WorldType> inverse = EnumSet.complementOf(normal);
            return Sets.intersection(types, inverse).isEmpty();
        }
    }
    ,
    DEADMAN{

        @Override
        boolean matches(Set<WorldType> types) {
            return types.contains(WorldType.DEADMAN);
        }
    }
    ,
    SEASONAL{

        @Override
        boolean matches(Set<WorldType> types) {
            return types.contains(WorldType.SEASONAL);
        }
    }
    ,
    QUEST_SPEEDRUNNING{

        @Override
        boolean matches(Set<WorldType> types) {
            return types.contains(WorldType.QUEST_SPEEDRUNNING);
        }
    }
    ,
    FRESH_START_WORLD{

        @Override
        boolean matches(Set<WorldType> types) {
            return types.contains(WorldType.FRESH_START_WORLD);
        }
    }
    ,
    PVP{

        @Override
        boolean matches(Set<WorldType> types) {
            return types.contains(WorldType.PVP);
        }
    }
    ,
    SKILL_TOTAL{

        @Override
        boolean matches(Set<WorldType> types) {
            return types.contains(WorldType.SKILL_TOTAL);
        }
    }
    ,
    HIGH_RISK{

        @Override
        boolean matches(Set<WorldType> types) {
            return types.contains(WorldType.HIGH_RISK);
        }
    }
    ,
    BOUNTY_HUNTER{

        @Override
        boolean matches(Set<WorldType> types) {
            return types.contains(WorldType.BOUNTY);
        }
    };


    abstract boolean matches(Set<WorldType> var1);
}

