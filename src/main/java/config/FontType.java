/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.config;

import java.awt.Font;
import net.runelite.client.ui.FontManager;

public enum FontType {
    REGULAR("Regular", FontManager.getRunescapeFont()),
    BOLD("Bold", FontManager.getRunescapeBoldFont()),
    SMALL("Small", FontManager.getRunescapeSmallFont());

    private final String name;
    private final Font font;

    public String toString() {
        return this.name;
    }

    public String getName() {
        return this.name;
    }

    public Font getFont() {
        return this.font;
    }

    private FontType(String name, Font font) {
        this.name = name;
        this.font = font;
    }
}

