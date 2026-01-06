/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.runelite.api.Point
 */
package net.runelite.client.plugins.statusbars;

import net.runelite.api.Point;

enum Viewport {
    RESIZED_BOX(10551369, new Point(20, -4), new Point(0, -4)),
    RESIZED_BOTTOM(10747974, new Point(61, -12), new Point(35, -12)),
    FIXED(35913807, new Point(20, -4), new Point(0, -4)),
    FIXED_BANK(983043, new Point(20, -4), new Point(0, -4));

    private final int viewport;
    private final Point offsetLeft;
    private final Point offsetRight;

    public int getViewport() {
        return this.viewport;
    }

    public Point getOffsetLeft() {
        return this.offsetLeft;
    }

    public Point getOffsetRight() {
        return this.offsetRight;
    }

    private Viewport(int viewport, Point offsetLeft, Point offsetRight) {
        this.viewport = viewport;
        this.offsetLeft = offsetLeft;
        this.offsetRight = offsetRight;
    }
}

