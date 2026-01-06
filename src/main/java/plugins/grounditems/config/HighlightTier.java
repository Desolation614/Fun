/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.plugins.grounditems.config;

import net.runelite.client.plugins.grounditems.GroundItemsConfig;

public enum HighlightTier {
    OFF,
    LOW,
    MEDIUM,
    HIGH,
    INSANE;


    public int getValueFromTier(GroundItemsConfig config) {
        switch (this) {
            case LOW: {
                return config.lowValuePrice();
            }
            case MEDIUM: {
                return config.mediumValuePrice();
            }
            case HIGH: {
                return config.highValuePrice();
            }
            case INSANE: {
                return config.insaneValuePrice();
            }
        }
        throw new UnsupportedOperationException();
    }
}

