/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package net.runelite.client.plugins.customcursor;

import java.awt.image.BufferedImage;
import javax.annotation.Nullable;
import net.runelite.client.plugins.customcursor.CustomCursorPlugin;
import net.runelite.client.util.ImageUtil;

public enum CustomCursor {
    RS3_GOLD("RS3 Gold", "cursor-rs3-gold.png"),
    RS3_SILVER("RS3 Silver", "cursor-rs3-silver.png"),
    DRAGON_DAGGER("Dragon Dagger", "cursor-dragon-dagger.png"),
    DRAGON_DAGGER_POISON("Dragon Dagger (p)", "cursor-dragon-dagger-p.png"),
    TROUT("Trout", "cursor-trout.png"),
    DRAGON_SCIMITAR("Dragon Scimitar", "cursor-dragon-scimitar.png"),
    EQUIPPED_WEAPON("Equipped Weapon"),
    CUSTOM_IMAGE("Custom Image");

    private final String name;
    @Nullable
    private final BufferedImage cursorImage;

    private CustomCursor(String name) {
        this.name = name;
        this.cursorImage = null;
    }

    private CustomCursor(String name, String icon) {
        this.name = name;
        this.cursorImage = ImageUtil.loadImageResource(CustomCursorPlugin.class, icon);
    }

    public String toString() {
        return this.name;
    }

    public String getName() {
        return this.name;
    }

    @Nullable
    public BufferedImage getCursorImage() {
        return this.cursorImage;
    }
}

