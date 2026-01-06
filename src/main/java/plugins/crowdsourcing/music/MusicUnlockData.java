/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.runelite.api.coords.WorldPoint
 */
package net.runelite.client.plugins.crowdsourcing.music;

import net.runelite.api.coords.WorldPoint;

public class MusicUnlockData {
    private final WorldPoint location;
    private final boolean isInInstance;
    private final String message;

    public WorldPoint getLocation() {
        return this.location;
    }

    public boolean isInInstance() {
        return this.isInInstance;
    }

    public String getMessage() {
        return this.message;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof MusicUnlockData)) {
            return false;
        }
        MusicUnlockData other = (MusicUnlockData)o;
        if (!other.canEqual(this)) {
            return false;
        }
        if (this.isInInstance() != other.isInInstance()) {
            return false;
        }
        WorldPoint this$location = this.getLocation();
        WorldPoint other$location = other.getLocation();
        if (this$location == null ? other$location != null : !this$location.equals(other$location)) {
            return false;
        }
        String this$message = this.getMessage();
        String other$message = other.getMessage();
        return !(this$message == null ? other$message != null : !this$message.equals(other$message));
    }

    protected boolean canEqual(Object other) {
        return other instanceof MusicUnlockData;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        result = result * 59 + (this.isInInstance() ? 79 : 97);
        WorldPoint $location = this.getLocation();
        result = result * 59 + ($location == null ? 43 : $location.hashCode());
        String $message = this.getMessage();
        result = result * 59 + ($message == null ? 43 : $message.hashCode());
        return result;
    }

    public String toString() {
        return "MusicUnlockData(location=" + String.valueOf(this.getLocation()) + ", isInInstance=" + this.isInInstance() + ", message=" + this.getMessage() + ")";
    }

    public MusicUnlockData(WorldPoint location, boolean isInInstance, String message) {
        this.location = location;
        this.isInInstance = isInInstance;
        this.message = message;
    }
}

