/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.plugins.ferox;

final class MathUtil {
    private MathUtil() {
    }

    static float dist(int x1, int y1, int x2, int y2) {
        return (float)Math.sqrt((y2 - y1) * (y2 - y1) + (x2 - x1) * (x2 - x1));
    }

    static float smoothstep(float edge0, float edge1, float x) {
        x = MathUtil.constrain((x - edge0) / (edge1 - edge0), 0.0f, 1.0f);
        return x * x * (3.0f - 2.0f * x);
    }

    private static float constrain(float value, float min, float max) {
        return Math.min(Math.max(value, min), max);
    }
}

