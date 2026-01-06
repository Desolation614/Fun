/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  lombok.NonNull
 */
package net.runelite.client.plugins.specialcounter;

import java.awt.Color;
import java.awt.Font;
import java.awt.image.BufferedImage;
import javax.annotation.Nullable;
import lombok.NonNull;
import net.runelite.client.ui.FontManager;

final class PlayerInfoDrop {
    private final int startCycle;
    private final int endCycle;
    private final int playerIdx;
    private final String text;
    @Nullable
    private final BufferedImage textBackground;
    private final int startHeightOffset;
    private final int endHeightOffset;
    private final Font font;
    private final Color color;
    @NonNull
    private final BufferedImage image;

    private static int $default$endHeightOffset() {
        return 200;
    }

    private static Font $default$font() {
        return FontManager.getRunescapeBoldFont();
    }

    private static Color $default$color() {
        return Color.WHITE;
    }

    PlayerInfoDrop(int startCycle, int endCycle, int playerIdx, String text, @Nullable BufferedImage textBackground, int startHeightOffset, int endHeightOffset, Font font, Color color, @NonNull BufferedImage image) {
        if (image == null) {
            throw new NullPointerException("image is marked non-null but is null");
        }
        this.startCycle = startCycle;
        this.endCycle = endCycle;
        this.playerIdx = playerIdx;
        this.text = text;
        this.textBackground = textBackground;
        this.startHeightOffset = startHeightOffset;
        this.endHeightOffset = endHeightOffset;
        this.font = font;
        this.color = color;
        this.image = image;
    }

    public static PlayerInfoDropBuilder builder() {
        return new PlayerInfoDropBuilder();
    }

    public int getStartCycle() {
        return this.startCycle;
    }

    public int getEndCycle() {
        return this.endCycle;
    }

    public int getPlayerIdx() {
        return this.playerIdx;
    }

    public String getText() {
        return this.text;
    }

    @Nullable
    public BufferedImage getTextBackground() {
        return this.textBackground;
    }

    public int getStartHeightOffset() {
        return this.startHeightOffset;
    }

    public int getEndHeightOffset() {
        return this.endHeightOffset;
    }

    public Font getFont() {
        return this.font;
    }

    public Color getColor() {
        return this.color;
    }

    @NonNull
    public BufferedImage getImage() {
        return this.image;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof PlayerInfoDrop)) {
            return false;
        }
        PlayerInfoDrop other = (PlayerInfoDrop)o;
        if (this.getStartCycle() != other.getStartCycle()) {
            return false;
        }
        if (this.getEndCycle() != other.getEndCycle()) {
            return false;
        }
        if (this.getPlayerIdx() != other.getPlayerIdx()) {
            return false;
        }
        if (this.getStartHeightOffset() != other.getStartHeightOffset()) {
            return false;
        }
        if (this.getEndHeightOffset() != other.getEndHeightOffset()) {
            return false;
        }
        String this$text = this.getText();
        String other$text = other.getText();
        if (this$text == null ? other$text != null : !this$text.equals(other$text)) {
            return false;
        }
        BufferedImage this$textBackground = this.getTextBackground();
        BufferedImage other$textBackground = other.getTextBackground();
        if (this$textBackground == null ? other$textBackground != null : !this$textBackground.equals(other$textBackground)) {
            return false;
        }
        Font this$font = this.getFont();
        Font other$font = other.getFont();
        if (this$font == null ? other$font != null : !((Object)this$font).equals(other$font)) {
            return false;
        }
        Color this$color = this.getColor();
        Color other$color = other.getColor();
        if (this$color == null ? other$color != null : !((Object)this$color).equals(other$color)) {
            return false;
        }
        BufferedImage this$image = this.getImage();
        BufferedImage other$image = other.getImage();
        return !(this$image == null ? other$image != null : !this$image.equals(other$image));
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        result = result * 59 + this.getStartCycle();
        result = result * 59 + this.getEndCycle();
        result = result * 59 + this.getPlayerIdx();
        result = result * 59 + this.getStartHeightOffset();
        result = result * 59 + this.getEndHeightOffset();
        String $text = this.getText();
        result = result * 59 + ($text == null ? 43 : $text.hashCode());
        BufferedImage $textBackground = this.getTextBackground();
        result = result * 59 + ($textBackground == null ? 43 : $textBackground.hashCode());
        Font $font = this.getFont();
        result = result * 59 + ($font == null ? 43 : ((Object)$font).hashCode());
        Color $color = this.getColor();
        result = result * 59 + ($color == null ? 43 : ((Object)$color).hashCode());
        BufferedImage $image = this.getImage();
        result = result * 59 + ($image == null ? 43 : $image.hashCode());
        return result;
    }

    public String toString() {
        return "PlayerInfoDrop(startCycle=" + this.getStartCycle() + ", endCycle=" + this.getEndCycle() + ", playerIdx=" + this.getPlayerIdx() + ", text=" + this.getText() + ", textBackground=" + String.valueOf(this.getTextBackground()) + ", startHeightOffset=" + this.getStartHeightOffset() + ", endHeightOffset=" + this.getEndHeightOffset() + ", font=" + String.valueOf(this.getFont()) + ", color=" + String.valueOf(this.getColor()) + ", image=" + String.valueOf(this.getImage()) + ")";
    }

    public static class PlayerInfoDropBuilder {
        private int startCycle;
        private int endCycle;
        private int playerIdx;
        private String text;
        private BufferedImage textBackground;
        private int startHeightOffset;
        private boolean endHeightOffset$set;
        private int endHeightOffset$value;
        private boolean font$set;
        private Font font$value;
        private boolean color$set;
        private Color color$value;
        private BufferedImage image;

        PlayerInfoDropBuilder() {
        }

        public PlayerInfoDropBuilder startCycle(int startCycle) {
            this.startCycle = startCycle;
            return this;
        }

        public PlayerInfoDropBuilder endCycle(int endCycle) {
            this.endCycle = endCycle;
            return this;
        }

        public PlayerInfoDropBuilder playerIdx(int playerIdx) {
            this.playerIdx = playerIdx;
            return this;
        }

        public PlayerInfoDropBuilder text(String text) {
            this.text = text;
            return this;
        }

        public PlayerInfoDropBuilder textBackground(@Nullable BufferedImage textBackground) {
            this.textBackground = textBackground;
            return this;
        }

        public PlayerInfoDropBuilder startHeightOffset(int startHeightOffset) {
            this.startHeightOffset = startHeightOffset;
            return this;
        }

        public PlayerInfoDropBuilder endHeightOffset(int endHeightOffset) {
            this.endHeightOffset$value = endHeightOffset;
            this.endHeightOffset$set = true;
            return this;
        }

        public PlayerInfoDropBuilder font(Font font) {
            this.font$value = font;
            this.font$set = true;
            return this;
        }

        public PlayerInfoDropBuilder color(Color color) {
            this.color$value = color;
            this.color$set = true;
            return this;
        }

        public PlayerInfoDropBuilder image(@NonNull BufferedImage image) {
            if (image == null) {
                throw new NullPointerException("image is marked non-null but is null");
            }
            this.image = image;
            return this;
        }

        public PlayerInfoDrop build() {
            int endHeightOffset$value = this.endHeightOffset$value;
            if (!this.endHeightOffset$set) {
                endHeightOffset$value = PlayerInfoDrop.$default$endHeightOffset();
            }
            Font font$value = this.font$value;
            if (!this.font$set) {
                font$value = PlayerInfoDrop.$default$font();
            }
            Color color$value = this.color$value;
            if (!this.color$set) {
                color$value = PlayerInfoDrop.$default$color();
            }
            return new PlayerInfoDrop(this.startCycle, this.endCycle, this.playerIdx, this.text, this.textBackground, this.startHeightOffset, endHeightOffset$value, font$value, color$value, this.image);
        }

        public String toString() {
            return "PlayerInfoDrop.PlayerInfoDropBuilder(startCycle=" + this.startCycle + ", endCycle=" + this.endCycle + ", playerIdx=" + this.playerIdx + ", text=" + this.text + ", textBackground=" + String.valueOf(this.textBackground) + ", startHeightOffset=" + this.startHeightOffset + ", endHeightOffset$value=" + this.endHeightOffset$value + ", font$value=" + String.valueOf(this.font$value) + ", color$value=" + String.valueOf(this.color$value) + ", image=" + String.valueOf(this.image) + ")";
        }
    }
}

