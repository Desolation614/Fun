/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  net.runelite.api.coords.WorldPoint
 */
package net.runelite.client.plugins.cluescrolls.clues;

import com.google.common.collect.ImmutableList;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.cluescrolls.clues.LocationClueScroll;
import net.runelite.client.plugins.cluescrolls.clues.MapClue;

public class BeginnerMapClue
extends MapClue
implements LocationClueScroll {
    private static final ImmutableList<BeginnerMapClue> CLUES = ImmutableList.of((Object)new BeginnerMapClue(346, new WorldPoint(3166, 3361, 0), "West of the Champions' Guild"), (Object)new BeginnerMapClue(347, new WorldPoint(3290, 3374, 0), "Outside Varrock East Mine"), (Object)new BeginnerMapClue(348, new WorldPoint(3093, 3226, 0), "South of Draynor Village Bank"), (Object)new BeginnerMapClue(351, new WorldPoint(3043, 3398, 0), "At the standing stones north of Falador"), (Object)new BeginnerMapClue(356, new WorldPoint(3110, 3152, 0), "On the south side of the Wizards' Tower (DIS)"));
    private final int widgetGroupID;

    private BeginnerMapClue(int widgetGroupID, WorldPoint location, String description) {
        super(-1, location, description);
        this.widgetGroupID = widgetGroupID;
        this.setRequiresSpade(true);
    }

    public static BeginnerMapClue forWidgetID(int widgetGroupID) {
        for (BeginnerMapClue clue : CLUES) {
            if (clue.widgetGroupID != widgetGroupID) continue;
            return clue;
        }
        return null;
    }

    public int getWidgetGroupID() {
        return this.widgetGroupID;
    }
}

