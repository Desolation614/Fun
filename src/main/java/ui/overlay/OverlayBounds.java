/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.ui.overlay;

import java.awt.Rectangle;
import java.util.Arrays;
import java.util.Collection;
import net.runelite.client.ui.overlay.OverlayPosition;

final class OverlayBounds {
    private final Rectangle topLeft;
    private final Rectangle topCenter;
    private final Rectangle topRight;
    private final Rectangle bottomLeft;
    private final Rectangle bottomRight;
    private final Rectangle aboveChatboxRight;
    private final Rectangle canvasTopRight;

    OverlayBounds(OverlayBounds other) {
        this.topLeft = new Rectangle(other.topLeft);
        this.topCenter = new Rectangle(other.topCenter);
        this.topRight = new Rectangle(other.topRight);
        this.bottomLeft = new Rectangle(other.bottomLeft);
        this.bottomRight = new Rectangle(other.bottomRight);
        this.aboveChatboxRight = new Rectangle(other.aboveChatboxRight);
        this.canvasTopRight = new Rectangle(other.canvasTopRight);
    }

    OverlayBounds translated(int x, int y) {
        OverlayBounds translated = new OverlayBounds(this);
        translated.getTopRight().translate(x, 0);
        translated.getTopCenter().translate(x / 2, 0);
        translated.getBottomLeft().translate(0, y);
        translated.getBottomRight().translate(x, y);
        translated.getAboveChatboxRight().translate(x, y);
        translated.getCanvasTopRight().translate(x, 0);
        return translated;
    }

    Rectangle forPosition(OverlayPosition overlayPosition) {
        switch (overlayPosition) {
            case TOP_LEFT: {
                return this.topLeft;
            }
            case TOP_CENTER: {
                return this.topCenter;
            }
            case TOP_RIGHT: {
                return this.topRight;
            }
            case BOTTOM_LEFT: {
                return this.bottomLeft;
            }
            case BOTTOM_RIGHT: {
                return this.bottomRight;
            }
            case ABOVE_CHATBOX_RIGHT: {
                return this.aboveChatboxRight;
            }
            case CANVAS_TOP_RIGHT: {
                return this.canvasTopRight;
            }
        }
        throw new IllegalArgumentException();
    }

    OverlayPosition fromBounds(Rectangle bounds) {
        if (bounds == this.topLeft) {
            return OverlayPosition.TOP_LEFT;
        }
        if (bounds == this.topCenter) {
            return OverlayPosition.TOP_CENTER;
        }
        if (bounds == this.topRight) {
            return OverlayPosition.TOP_RIGHT;
        }
        if (bounds == this.bottomLeft) {
            return OverlayPosition.BOTTOM_LEFT;
        }
        if (bounds == this.bottomRight) {
            return OverlayPosition.BOTTOM_RIGHT;
        }
        if (bounds == this.aboveChatboxRight) {
            return OverlayPosition.ABOVE_CHATBOX_RIGHT;
        }
        if (bounds == this.canvasTopRight) {
            return OverlayPosition.CANVAS_TOP_RIGHT;
        }
        throw new IllegalArgumentException();
    }

    Collection<Rectangle> getBounds() {
        return Arrays.asList(this.topLeft, this.topCenter, this.topRight, this.bottomLeft, this.bottomRight, this.aboveChatboxRight, this.canvasTopRight);
    }

    public Rectangle getTopLeft() {
        return this.topLeft;
    }

    public Rectangle getTopCenter() {
        return this.topCenter;
    }

    public Rectangle getTopRight() {
        return this.topRight;
    }

    public Rectangle getBottomLeft() {
        return this.bottomLeft;
    }

    public Rectangle getBottomRight() {
        return this.bottomRight;
    }

    public Rectangle getAboveChatboxRight() {
        return this.aboveChatboxRight;
    }

    public Rectangle getCanvasTopRight() {
        return this.canvasTopRight;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof OverlayBounds)) {
            return false;
        }
        OverlayBounds other = (OverlayBounds)o;
        Rectangle this$topLeft = this.getTopLeft();
        Rectangle other$topLeft = other.getTopLeft();
        if (this$topLeft == null ? other$topLeft != null : !((Object)this$topLeft).equals(other$topLeft)) {
            return false;
        }
        Rectangle this$topCenter = this.getTopCenter();
        Rectangle other$topCenter = other.getTopCenter();
        if (this$topCenter == null ? other$topCenter != null : !((Object)this$topCenter).equals(other$topCenter)) {
            return false;
        }
        Rectangle this$topRight = this.getTopRight();
        Rectangle other$topRight = other.getTopRight();
        if (this$topRight == null ? other$topRight != null : !((Object)this$topRight).equals(other$topRight)) {
            return false;
        }
        Rectangle this$bottomLeft = this.getBottomLeft();
        Rectangle other$bottomLeft = other.getBottomLeft();
        if (this$bottomLeft == null ? other$bottomLeft != null : !((Object)this$bottomLeft).equals(other$bottomLeft)) {
            return false;
        }
        Rectangle this$bottomRight = this.getBottomRight();
        Rectangle other$bottomRight = other.getBottomRight();
        if (this$bottomRight == null ? other$bottomRight != null : !((Object)this$bottomRight).equals(other$bottomRight)) {
            return false;
        }
        Rectangle this$aboveChatboxRight = this.getAboveChatboxRight();
        Rectangle other$aboveChatboxRight = other.getAboveChatboxRight();
        if (this$aboveChatboxRight == null ? other$aboveChatboxRight != null : !((Object)this$aboveChatboxRight).equals(other$aboveChatboxRight)) {
            return false;
        }
        Rectangle this$canvasTopRight = this.getCanvasTopRight();
        Rectangle other$canvasTopRight = other.getCanvasTopRight();
        return !(this$canvasTopRight == null ? other$canvasTopRight != null : !((Object)this$canvasTopRight).equals(other$canvasTopRight));
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        Rectangle $topLeft = this.getTopLeft();
        result = result * 59 + ($topLeft == null ? 43 : ((Object)$topLeft).hashCode());
        Rectangle $topCenter = this.getTopCenter();
        result = result * 59 + ($topCenter == null ? 43 : ((Object)$topCenter).hashCode());
        Rectangle $topRight = this.getTopRight();
        result = result * 59 + ($topRight == null ? 43 : ((Object)$topRight).hashCode());
        Rectangle $bottomLeft = this.getBottomLeft();
        result = result * 59 + ($bottomLeft == null ? 43 : ((Object)$bottomLeft).hashCode());
        Rectangle $bottomRight = this.getBottomRight();
        result = result * 59 + ($bottomRight == null ? 43 : ((Object)$bottomRight).hashCode());
        Rectangle $aboveChatboxRight = this.getAboveChatboxRight();
        result = result * 59 + ($aboveChatboxRight == null ? 43 : ((Object)$aboveChatboxRight).hashCode());
        Rectangle $canvasTopRight = this.getCanvasTopRight();
        result = result * 59 + ($canvasTopRight == null ? 43 : ((Object)$canvasTopRight).hashCode());
        return result;
    }

    public String toString() {
        return "OverlayBounds(topLeft=" + String.valueOf(this.getTopLeft()) + ", topCenter=" + String.valueOf(this.getTopCenter()) + ", topRight=" + String.valueOf(this.getTopRight()) + ", bottomLeft=" + String.valueOf(this.getBottomLeft()) + ", bottomRight=" + String.valueOf(this.getBottomRight()) + ", aboveChatboxRight=" + String.valueOf(this.getAboveChatboxRight()) + ", canvasTopRight=" + String.valueOf(this.getCanvasTopRight()) + ")";
    }

    public OverlayBounds(Rectangle topLeft, Rectangle topCenter, Rectangle topRight, Rectangle bottomLeft, Rectangle bottomRight, Rectangle aboveChatboxRight, Rectangle canvasTopRight) {
        this.topLeft = topLeft;
        this.topCenter = topCenter;
        this.topRight = topRight;
        this.bottomLeft = bottomLeft;
        this.bottomRight = bottomRight;
        this.aboveChatboxRight = aboveChatboxRight;
        this.canvasTopRight = canvasTopRight;
    }
}

