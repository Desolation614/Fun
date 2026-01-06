/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.runelite.http.api.worlds.WorldResult
 */
package net.runelite.client.events;

import net.runelite.http.api.worlds.WorldResult;

public final class WorldsFetch {
    private final WorldResult worldResult;

    public WorldsFetch(WorldResult worldResult) {
        this.worldResult = worldResult;
    }

    public WorldResult getWorldResult() {
        return this.worldResult;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof WorldsFetch)) {
            return false;
        }
        WorldsFetch other = (WorldsFetch)o;
        WorldResult this$worldResult = this.getWorldResult();
        WorldResult other$worldResult = other.getWorldResult();
        return !(this$worldResult == null ? other$worldResult != null : !this$worldResult.equals(other$worldResult));
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        WorldResult $worldResult = this.getWorldResult();
        result = result * 59 + ($worldResult == null ? 43 : $worldResult.hashCode());
        return result;
    }

    public String toString() {
        return "WorldsFetch(worldResult=" + String.valueOf(this.getWorldResult()) + ")";
    }
}

