/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.plugins.banktags.tabs;

import net.runelite.client.game.SpriteOverride;

public enum TabSprites implements SpriteOverride
{
    TAB_BACKGROUND(-201, "tag-tab.png"),
    TAB_BACKGROUND_ACTIVE(-202, "tag-tab-active.png"),
    UP_ARROW(-203, "up-arrow.png"),
    DOWN_ARROW(-204, "down-arrow.png"),
    NEW_TAB(-205, "new-tab.png");

    private final int spriteId;
    private final String fileName;

    private TabSprites(int spriteId, String fileName) {
        this.spriteId = spriteId;
        this.fileName = fileName;
    }

    @Override
    public int getSpriteId() {
        return this.spriteId;
    }

    @Override
    public String getFileName() {
        return this.fileName;
    }
}

