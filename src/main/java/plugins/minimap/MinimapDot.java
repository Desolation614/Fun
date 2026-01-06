/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.runelite.api.Client
 *  net.runelite.api.SpritePixels
 */
package net.runelite.client.plugins.minimap;

import java.awt.Color;
import net.runelite.api.Client;
import net.runelite.api.SpritePixels;

class MinimapDot {
    private static final int MAP_DOT_WIDTH = 4;
    private static final int MAP_DOT_HEIGHT = 5;

    MinimapDot() {
    }

    private static int[] createPixels(Color color) {
        int rgb = color.getRGB();
        int[] pixels = new int[]{0, rgb, rgb, 0, rgb, rgb, rgb, rgb, rgb, rgb, rgb, rgb, 1, rgb, rgb, 1, 0, 1, 1, 0};
        return pixels;
    }

    static SpritePixels create(Client client, Color color) {
        int[] pixels = MinimapDot.createPixels(color);
        return client.createSpritePixels(pixels, 4, 5);
    }
}

