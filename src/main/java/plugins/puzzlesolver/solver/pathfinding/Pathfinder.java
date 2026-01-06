/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.plugins.puzzlesolver.solver.pathfinding;

import java.util.List;
import net.runelite.client.plugins.puzzlesolver.solver.PuzzleState;
import net.runelite.client.plugins.puzzlesolver.solver.heuristics.Heuristic;

public abstract class Pathfinder {
    private final Heuristic heuristic;

    Pathfinder(Heuristic heuristic) {
        this.heuristic = heuristic;
    }

    Heuristic getHeuristic() {
        return this.heuristic;
    }

    public abstract List<PuzzleState> computePath(PuzzleState var1);
}

