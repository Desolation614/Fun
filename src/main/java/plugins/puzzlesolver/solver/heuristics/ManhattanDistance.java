/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.plugins.puzzlesolver.solver.heuristics;

import net.runelite.client.plugins.puzzlesolver.solver.PuzzleState;
import net.runelite.client.plugins.puzzlesolver.solver.heuristics.Heuristic;

public class ManhattanDistance
implements Heuristic {
    @Override
    public int computeValue(PuzzleState state) {
        int value = 0;
        PuzzleState parent = state.getParent();
        if (parent == null) {
            for (int x = 0; x < 5; ++x) {
                for (int y = 0; y < 5; ++y) {
                    int piece = state.getPiece(x, y);
                    if (piece == -1) continue;
                    int goalX = piece % 5;
                    int goalY = piece / 5;
                    value += Math.abs(x - goalX) + Math.abs(y - goalY);
                }
            }
        } else {
            int targetY;
            int targetX;
            value = parent.getHeuristicValue(this);
            int x = parent.getEmptyPiece() % 5;
            int y = parent.getEmptyPiece() / 5;
            int x2 = state.getEmptyPiece() % 5;
            int y2 = state.getEmptyPiece() / 5;
            int piece = state.getPiece(x, y);
            value = x2 > x ? ((targetX = piece % 5) > x ? ++value : --value) : (x2 < x ? ((targetX = piece % 5) < x ? ++value : --value) : (y2 > y ? ((targetY = piece / 5) > y ? ++value : --value) : ((targetY = piece / 5) < y ? ++value : --value)));
        }
        return value;
    }
}

