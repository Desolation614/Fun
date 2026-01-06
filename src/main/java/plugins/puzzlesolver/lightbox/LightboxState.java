/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.plugins.puzzlesolver.lightbox;

import java.util.Arrays;

public class LightboxState {
    private final boolean[][] state = new boolean[5][5];

    public void setState(int x, int y, boolean s) {
        this.state[x][y] = s;
    }

    public boolean getState(int x, int y) {
        return this.state[x][y];
    }

    public LightboxState diff(LightboxState other) {
        LightboxState newState = new LightboxState();
        for (int i = 0; i < 5; ++i) {
            for (int j = 0; j < 5; ++j) {
                newState.state[i][j] = this.state[i][j] ^ other.state[i][j];
            }
        }
        return newState;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof LightboxState)) {
            return false;
        }
        LightboxState other = (LightboxState)o;
        if (!other.canEqual(this)) {
            return false;
        }
        return Arrays.deepEquals((Object[])this.state, (Object[])other.state);
    }

    protected boolean canEqual(Object other) {
        return other instanceof LightboxState;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        result = result * 59 + Arrays.deepHashCode((Object[])this.state);
        return result;
    }
}

