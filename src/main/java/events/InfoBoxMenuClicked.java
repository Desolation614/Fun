/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.events;

import net.runelite.client.ui.overlay.OverlayMenuEntry;
import net.runelite.client.ui.overlay.infobox.InfoBox;

public final class InfoBoxMenuClicked {
    private final OverlayMenuEntry entry;
    private final InfoBox infoBox;

    public InfoBoxMenuClicked(OverlayMenuEntry entry, InfoBox infoBox) {
        this.entry = entry;
        this.infoBox = infoBox;
    }

    public OverlayMenuEntry getEntry() {
        return this.entry;
    }

    public InfoBox getInfoBox() {
        return this.infoBox;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof InfoBoxMenuClicked)) {
            return false;
        }
        InfoBoxMenuClicked other = (InfoBoxMenuClicked)o;
        OverlayMenuEntry this$entry = this.getEntry();
        OverlayMenuEntry other$entry = other.getEntry();
        if (this$entry == null ? other$entry != null : !((Object)this$entry).equals(other$entry)) {
            return false;
        }
        InfoBox this$infoBox = this.getInfoBox();
        InfoBox other$infoBox = other.getInfoBox();
        return !(this$infoBox == null ? other$infoBox != null : !this$infoBox.equals(other$infoBox));
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        OverlayMenuEntry $entry = this.getEntry();
        result = result * 59 + ($entry == null ? 43 : ((Object)$entry).hashCode());
        InfoBox $infoBox = this.getInfoBox();
        result = result * 59 + ($infoBox == null ? 43 : $infoBox.hashCode());
        return result;
    }

    public String toString() {
        return "InfoBoxMenuClicked(entry=" + String.valueOf(this.getEntry()) + ", infoBox=" + String.valueOf(this.getInfoBox()) + ")";
    }
}

