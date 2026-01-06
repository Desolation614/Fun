/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.plugins.puzzlesolver.solver;

public enum PuzzleSwapPattern {
    ROTATE_LEFT_UP(new int[]{1, -1, 0, -1, -1, -1, -1, 0}, 1, 1),
    ROTATE_LEFT_DOWN(1, -1),
    ROTATE_RIGHT_UP(-1, 1),
    ROTATE_RIGHT_DOWN(-1, -1),
    ROTATE_UP_LEFT(new int[]{-1, 1, -1, 0, -1, -1, 0, -1}, 1, 1),
    ROTATE_UP_RIGHT(-1, 1),
    ROTATE_DOWN_LEFT(1, -1),
    ROTATE_DOWN_RIGHT(-1, -1),
    LAST_PIECE_ROW(new int[]{-1, -1, 0, -1, -1, 0, -1, 1}, 1, 1),
    LAST_PIECE_COLUMN(new int[]{-1, -1, -1, 0, 0, -1, 1, -1}, 1, 1),
    SHUFFLE_UP_RIGHT(new int[]{1, -1, 0, -1}, 1, 1),
    SHUFFLE_UP_LEFT(new int[]{-1, -1, 0, -1}, 1, 1),
    SHUFFLE_UP_BELOW(new int[]{-1, 1, -1, 0}, 1, 1),
    SHUFFLE_UP_ABOVE(new int[]{-1, -1, -1, 0}, 1, 1);

    private final int[] points;
    private final int modX;
    private final int modY;

    private PuzzleSwapPattern(int modX, int modY) {
        this(null, modX, modY);
    }

    private PuzzleSwapPattern(int[] points, int modX, int modY) {
        this.points = points;
        this.modX = modX;
        this.modY = modY;
    }

    public int[] getPoints() {
        return this.points;
    }

    public int getModX() {
        return this.modX;
    }

    public int getModY() {
        return this.modY;
    }
}

