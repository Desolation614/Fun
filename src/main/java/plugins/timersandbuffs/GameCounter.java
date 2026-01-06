/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.plugins.timersandbuffs;

import java.awt.Color;
import java.util.function.BiPredicate;
import net.runelite.client.plugins.timersandbuffs.GameTimerImageType;

enum GameCounter {
    BURN_DAMAGE_ACCUMULATED(4767, GameTimerImageType.SPRITE, "Burn damage accumulated"),
    BURN_DAMAGE_NEXT_HIT(29574, GameTimerImageType.ITEM, "Burn damage next hit"),
    COLOSSEUM_DOOM(4766, GameTimerImageType.SPRITE, "Doom"),
    CURSE_OF_THE_MOONS_BLUE(29019, GameTimerImageType.ITEM, "Curse of the Moons (Blue Moon)", ColorBoundaryType.GREATER_THAN_EQUAL_TO, 18, Color.RED),
    CURSE_OF_THE_MOONS_ECLIPSE(29010, GameTimerImageType.ITEM, "Curse of the Moons (Eclipse Moon)"),
    STONE_OF_JAS_EMPOWERMENT(29559, GameTimerImageType.ITEM, "Stone of Jas empowerment", false),
    VENGEANCE_ACTIVE(561, GameTimerImageType.SPRITE, "Vengeance active", false),
    DHL_STACKS(22978, GameTimerImageType.ITEM, "Draconic Focus Stacks");

    private final int imageId;
    private final GameTimerImageType imageType;
    private final String description;
    private final ColorBoundaryType colorBoundaryType;
    private final int boundary;
    private final Color color;
    private final boolean shouldDisplayCount;

    private GameCounter(int imageId, GameTimerImageType idType, String description, ColorBoundaryType colorBoundaryType, int boundary, Color color) {
        this(imageId, idType, description, colorBoundaryType, boundary, color, true);
    }

    private GameCounter(int imageId, GameTimerImageType idType, String description, boolean shouldDisplayCount) {
        this(imageId, idType, description, ColorBoundaryType.NO_BOUNDARY, 0, Color.WHITE, shouldDisplayCount);
    }

    private GameCounter(int imageId, GameTimerImageType idType, String description) {
        this(imageId, idType, description, ColorBoundaryType.NO_BOUNDARY, 0, Color.WHITE, true);
    }

    int getImageId() {
        return this.imageId;
    }

    GameTimerImageType getImageType() {
        return this.imageType;
    }

    String getDescription() {
        return this.description;
    }

    ColorBoundaryType getColorBoundaryType() {
        return this.colorBoundaryType;
    }

    int getBoundary() {
        return this.boundary;
    }

    Color getColor() {
        return this.color;
    }

    boolean isShouldDisplayCount() {
        return this.shouldDisplayCount;
    }

    private GameCounter(int imageId, GameTimerImageType imageType, String description, ColorBoundaryType colorBoundaryType, int boundary, Color color, boolean shouldDisplayCount) {
        this.imageId = imageId;
        this.imageType = imageType;
        this.description = description;
        this.colorBoundaryType = colorBoundaryType;
        this.boundary = boundary;
        this.color = color;
        this.shouldDisplayCount = shouldDisplayCount;
    }

    static enum ColorBoundaryType {
        GREATER_THAN_EQUAL_TO((count, boundary) -> count >= boundary),
        LESS_THAN_EQUAL_TO((count, boundary) -> count <= boundary),
        NO_BOUNDARY((count, boundary) -> false);

        final BiPredicate<Integer, Integer> shouldRecolorPredicate;

        boolean shouldRecolor(int count, int boundary) {
            return this.shouldRecolorPredicate.test(count, boundary);
        }

        private ColorBoundaryType(BiPredicate<Integer, Integer> shouldRecolorPredicate) {
            this.shouldRecolorPredicate = shouldRecolorPredicate;
        }
    }
}

