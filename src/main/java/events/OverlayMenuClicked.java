/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.events;

import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayMenuEntry;

public final class OverlayMenuClicked {
    private final OverlayMenuEntry entry;
    private final Overlay overlay;

    public OverlayMenuClicked(OverlayMenuEntry entry, Overlay overlay) {
        this.entry = entry;
        this.overlay = overlay;
    }

    public OverlayMenuEntry getEntry() {
        return this.entry;
    }

    public Overlay getOverlay() {
        return this.overlay;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof OverlayMenuClicked)) {
            return false;
        }
        OverlayMenuClicked other = (OverlayMenuClicked)o;
        OverlayMenuEntry this$entry = this.getEntry();
        OverlayMenuEntry other$entry = other.getEntry();
        if (this$entry == null ? other$entry != null : !((Object)this$entry).equals(other$entry)) {
            return false;
        }
        Overlay this$overlay = this.getOverlay();
        Overlay other$overlay = other.getOverlay();
        return !(this$overlay == null ? other$overlay != null : !this$overlay.equals(other$overlay));
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        OverlayMenuEntry $entry = this.getEntry();
        result = result * 59 + ($entry == null ? 43 : ((Object)$entry).hashCode());
        Overlay $overlay = this.getOverlay();
        result = result * 59 + ($overlay == null ? 43 : $overlay.hashCode());
        return result;
    }

    public String toString() {
        return "OverlayMenuClicked(entry=" + String.valueOf(this.getEntry()) + ", overlay=" + String.valueOf(this.getOverlay()) + ")";
    }
}

