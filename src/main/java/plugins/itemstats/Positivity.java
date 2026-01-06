/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.plugins.itemstats;

import java.awt.Color;
import net.runelite.client.plugins.itemstats.ItemStatConfig;

public enum Positivity {
    WORSE,
    NO_CHANGE,
    BETTER_CAPPED,
    BETTER_SOMECAPPED,
    BETTER_UNCAPPED;


    public static Color getColor(ItemStatConfig config, Positivity positivity) {
        switch (positivity) {
            case BETTER_UNCAPPED: {
                return config.colorBetterUncapped();
            }
            case BETTER_SOMECAPPED: {
                return config.colorBetterSomeCapped();
            }
            case BETTER_CAPPED: {
                return config.colorBetterCapped();
            }
            case NO_CHANGE: {
                return config.colorNoChange();
            }
            case WORSE: {
                return config.colorWorse();
            }
        }
        return Color.WHITE;
    }
}

