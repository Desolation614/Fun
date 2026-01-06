/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.plugins.mining;

enum Rock {
    ROCK(0),
    MLM_ORE_VEIN(150),
    ORE_VEIN(150),
    AMETHYST(120),
    DAEYALT_ESSENCE(0),
    BARRONITE(140);

    private final int zOffset;

    private Rock(int zOffset) {
        this.zOffset = zOffset;
    }

    int getZOffset() {
        return this.zOffset;
    }
}

