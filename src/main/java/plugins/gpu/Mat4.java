/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.plugins.gpu;

public class Mat4 {
    private Mat4() {
    }

    public static float[] identity() {
        return new float[]{1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f};
    }

    public static float[] scale(float sx, float sy, float sz) {
        return new float[]{sx, 0.0f, 0.0f, 0.0f, 0.0f, sy, 0.0f, 0.0f, 0.0f, 0.0f, sz, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f};
    }

    public static float[] translate(float tx, float ty, float tz) {
        return new float[]{1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, tx, ty, tz, 1.0f};
    }

    public static float[] rotateX(float rx) {
        float s = (float)Math.sin(rx);
        float c = (float)Math.cos(rx);
        return new float[]{1.0f, 0.0f, 0.0f, 0.0f, 0.0f, c, s, 0.0f, 0.0f, -s, c, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f};
    }

    public static float[] rotateY(float ry) {
        float s = (float)Math.sin(ry);
        float c = (float)Math.cos(ry);
        return new float[]{c, 0.0f, -s, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, s, 0.0f, c, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f};
    }

    public static float[] projection(float w, float h, float n) {
        return new float[]{2.0f / w, 0.0f, 0.0f, 0.0f, 0.0f, -2.0f / h, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 2.0f * n, 0.0f};
    }

    public static void mul(float[] a, float[] b) {
        float b00 = b[0];
        float b10 = b[1];
        float b20 = b[2];
        float b30 = b[3];
        float b01 = b[4];
        float b11 = b[5];
        float b21 = b[6];
        float b31 = b[7];
        float b02 = b[8];
        float b12 = b[9];
        float b22 = b[10];
        float b32 = b[11];
        float b03 = b[12];
        float b13 = b[13];
        float b23 = b[14];
        float b33 = b[15];
        float ai0 = a[0];
        float ai1 = a[4];
        float ai2 = a[8];
        float ai3 = a[12];
        a[0] = ai0 * b00 + ai1 * b10 + ai2 * b20 + ai3 * b30;
        a[4] = ai0 * b01 + ai1 * b11 + ai2 * b21 + ai3 * b31;
        a[8] = ai0 * b02 + ai1 * b12 + ai2 * b22 + ai3 * b32;
        a[12] = ai0 * b03 + ai1 * b13 + ai2 * b23 + ai3 * b33;
        ai0 = a[1];
        ai1 = a[5];
        ai2 = a[9];
        ai3 = a[13];
        a[1] = ai0 * b00 + ai1 * b10 + ai2 * b20 + ai3 * b30;
        a[5] = ai0 * b01 + ai1 * b11 + ai2 * b21 + ai3 * b31;
        a[9] = ai0 * b02 + ai1 * b12 + ai2 * b22 + ai3 * b32;
        a[13] = ai0 * b03 + ai1 * b13 + ai2 * b23 + ai3 * b33;
        ai0 = a[2];
        ai1 = a[6];
        ai2 = a[10];
        ai3 = a[14];
        a[2] = ai0 * b00 + ai1 * b10 + ai2 * b20 + ai3 * b30;
        a[6] = ai0 * b01 + ai1 * b11 + ai2 * b21 + ai3 * b31;
        a[10] = ai0 * b02 + ai1 * b12 + ai2 * b22 + ai3 * b32;
        a[14] = ai0 * b03 + ai1 * b13 + ai2 * b23 + ai3 * b33;
        ai0 = a[3];
        ai1 = a[7];
        ai2 = a[11];
        ai3 = a[15];
        a[3] = ai0 * b00 + ai1 * b10 + ai2 * b20 + ai3 * b30;
        a[7] = ai0 * b01 + ai1 * b11 + ai2 * b21 + ai3 * b31;
        a[11] = ai0 * b02 + ai1 * b12 + ai2 * b22 + ai3 * b32;
        a[15] = ai0 * b03 + ai1 * b13 + ai2 * b23 + ai3 * b33;
    }
}

