/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.plugins.puzzlesolver.lightbox;

import net.runelite.client.plugins.puzzlesolver.lightbox.Combination;
import net.runelite.client.plugins.puzzlesolver.lightbox.LightboxSolution;
import net.runelite.client.plugins.puzzlesolver.lightbox.LightboxState;

public class LightboxSolver {
    private LightboxState initial;
    private final LightboxState[] switches = new LightboxState[8];

    static boolean isBitSet(int num, int bit) {
        return (num >>> bit & 1) != 0;
    }

    private static boolean isSolved(LightboxState s) {
        for (int i = 0; i < 5; ++i) {
            for (int j = 0; j < 5; ++j) {
                if (s.getState(i, j)) continue;
                return false;
            }
        }
        return true;
    }

    public LightboxSolution solve() {
        LightboxSolution solution = null;
        int i = 0;
        while ((double)i < Math.pow(2.0, 8.0)) {
            block5: {
                LightboxState s = this.initial;
                for (int bit = 0; bit < 8; ++bit) {
                    if (!LightboxSolver.isBitSet(i, bit)) continue;
                    if (this.switches[bit] != null) {
                        s = s.diff(this.switches[bit]);
                        continue;
                    }
                    break block5;
                }
                if (LightboxSolver.isSolved(s)) {
                    LightboxSolution sol = new LightboxSolution(i);
                    if (solution == null || sol.numMoves() < solution.numMoves()) {
                        solution = sol;
                    }
                }
            }
            ++i;
        }
        return solution;
    }

    public void setInitial(LightboxState initial) {
        this.initial = initial;
    }

    public void setSwitchChange(Combination combination, LightboxState newState) {
        this.switches[combination.ordinal()] = newState;
    }
}

