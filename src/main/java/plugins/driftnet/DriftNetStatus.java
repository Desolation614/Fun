/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.plugins.driftnet;

import java.awt.Color;

enum DriftNetStatus {
    UNSET(Color.YELLOW),
    SET(Color.GREEN),
    CATCH(Color.GREEN),
    FULL(Color.RED);

    private final Color color;

    static DriftNetStatus of(int varbitValue) {
        switch (varbitValue) {
            case 0: {
                return UNSET;
            }
            case 1: {
                return SET;
            }
            case 2: {
                return CATCH;
            }
            case 3: {
                return FULL;
            }
        }
        return null;
    }

    public Color getColor() {
        return this.color;
    }

    private DriftNetStatus(Color color) {
        this.color = color;
    }
}

