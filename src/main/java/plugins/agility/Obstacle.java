/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.runelite.api.Tile
 */
package net.runelite.client.plugins.agility;

import javax.annotation.Nullable;
import net.runelite.api.Tile;
import net.runelite.client.game.AgilityShortcut;

final class Obstacle {
    private final Tile tile;
    @Nullable
    private final AgilityShortcut shortcut;

    public Tile getTile() {
        return this.tile;
    }

    @Nullable
    public AgilityShortcut getShortcut() {
        return this.shortcut;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof Obstacle)) {
            return false;
        }
        Obstacle other = (Obstacle)o;
        Tile this$tile = this.getTile();
        Tile other$tile = other.getTile();
        if (this$tile == null ? other$tile != null : !this$tile.equals(other$tile)) {
            return false;
        }
        AgilityShortcut this$shortcut = this.getShortcut();
        AgilityShortcut other$shortcut = other.getShortcut();
        return !(this$shortcut == null ? other$shortcut != null : !((Object)((Object)this$shortcut)).equals((Object)other$shortcut));
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        Tile $tile = this.getTile();
        result = result * 59 + ($tile == null ? 43 : $tile.hashCode());
        AgilityShortcut $shortcut = this.getShortcut();
        result = result * 59 + ($shortcut == null ? 43 : ((Object)((Object)$shortcut)).hashCode());
        return result;
    }

    public String toString() {
        return "Obstacle(tile=" + String.valueOf(this.getTile()) + ", shortcut=" + String.valueOf((Object)this.getShortcut()) + ")";
    }

    public Obstacle(Tile tile, @Nullable AgilityShortcut shortcut) {
        this.tile = tile;
        this.shortcut = shortcut;
    }
}

