/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.plugins.puzzlesolver.solver.pathfinding;

import java.util.ArrayList;
import java.util.List;
import net.runelite.client.plugins.puzzlesolver.solver.PuzzleState;
import net.runelite.client.plugins.puzzlesolver.solver.heuristics.Heuristic;
import net.runelite.client.plugins.puzzlesolver.solver.pathfinding.Pathfinder;

public class IDAStar
extends Pathfinder {
    public IDAStar(Heuristic heuristic) {
        super(heuristic);
    }

    @Override
    public List<PuzzleState> computePath(PuzzleState root) {
        PuzzleState goalNode = this.path(root);
        ArrayList<PuzzleState> path = new ArrayList<PuzzleState>();
        for (PuzzleState parent = goalNode; parent != null; parent = parent.getParent()) {
            path.add(0, parent);
        }
        return path;
    }

    private PuzzleState path(PuzzleState root) {
        int bound = root.getHeuristicValue(this.getHeuristic());
        PuzzleState t;
        while ((t = this.search(root, 0, bound)) == null) {
            ++bound;
        }
        return t;
    }

    private PuzzleState search(PuzzleState node, int g, int bound) {
        int h = node.getHeuristicValue(this.getHeuristic());
        int f = g + h;
        if (f > bound) {
            return null;
        }
        if (h == 0) {
            return node;
        }
        for (PuzzleState successor : node.computeMoves()) {
            PuzzleState t = this.search(successor, g + 1, bound);
            if (t == null) continue;
            return t;
        }
        return null;
    }
}

