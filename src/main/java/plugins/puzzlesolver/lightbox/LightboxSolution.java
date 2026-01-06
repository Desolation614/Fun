/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.plugins.puzzlesolver.lightbox;

import net.runelite.client.plugins.puzzlesolver.lightbox.Combination;

public class LightboxSolution {
    private int solution;

    public void flip(Combination c) {
        this.solution ^= 1 << c.ordinal();
    }

    public int numMoves() {
        int count = 0;
        int cur = this.solution;
        for (int i = 0; i < Combination.values().length; ++i) {
            count += cur & 1;
            cur >>= 1;
        }
        return count;
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        for (Combination combination : Combination.values()) {
            if ((this.solution >>> combination.ordinal() & 1) == 0) continue;
            stringBuilder.append(combination.name());
        }
        return stringBuilder.toString();
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof LightboxSolution)) {
            return false;
        }
        LightboxSolution other = (LightboxSolution)o;
        if (!other.canEqual(this)) {
            return false;
        }
        return this.solution == other.solution;
    }

    protected boolean canEqual(Object other) {
        return other instanceof LightboxSolution;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        result = result * 59 + this.solution;
        return result;
    }

    public LightboxSolution() {
    }

    public LightboxSolution(int solution) {
        this.solution = solution;
    }
}

