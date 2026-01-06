/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.runelite.api.coords.WorldPoint
 */
package net.runelite.client.plugins.crowdsourcing.thieving;

import net.runelite.api.coords.WorldPoint;

public class PickpocketData {
    private final int level;
    private final int target;
    private final String message;
    private final WorldPoint location;
    private final boolean silence;
    private final boolean thievingCape;
    private final int ardougneDiary;

    public int getLevel() {
        return this.level;
    }

    public int getTarget() {
        return this.target;
    }

    public String getMessage() {
        return this.message;
    }

    public WorldPoint getLocation() {
        return this.location;
    }

    public boolean isSilence() {
        return this.silence;
    }

    public boolean isThievingCape() {
        return this.thievingCape;
    }

    public int getArdougneDiary() {
        return this.ardougneDiary;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof PickpocketData)) {
            return false;
        }
        PickpocketData other = (PickpocketData)o;
        if (!other.canEqual(this)) {
            return false;
        }
        if (this.getLevel() != other.getLevel()) {
            return false;
        }
        if (this.getTarget() != other.getTarget()) {
            return false;
        }
        if (this.isSilence() != other.isSilence()) {
            return false;
        }
        if (this.isThievingCape() != other.isThievingCape()) {
            return false;
        }
        if (this.getArdougneDiary() != other.getArdougneDiary()) {
            return false;
        }
        String this$message = this.getMessage();
        String other$message = other.getMessage();
        if (this$message == null ? other$message != null : !this$message.equals(other$message)) {
            return false;
        }
        WorldPoint this$location = this.getLocation();
        WorldPoint other$location = other.getLocation();
        return !(this$location == null ? other$location != null : !this$location.equals(other$location));
    }

    protected boolean canEqual(Object other) {
        return other instanceof PickpocketData;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        result = result * 59 + this.getLevel();
        result = result * 59 + this.getTarget();
        result = result * 59 + (this.isSilence() ? 79 : 97);
        result = result * 59 + (this.isThievingCape() ? 79 : 97);
        result = result * 59 + this.getArdougneDiary();
        String $message = this.getMessage();
        result = result * 59 + ($message == null ? 43 : $message.hashCode());
        WorldPoint $location = this.getLocation();
        result = result * 59 + ($location == null ? 43 : $location.hashCode());
        return result;
    }

    public String toString() {
        return "PickpocketData(level=" + this.getLevel() + ", target=" + this.getTarget() + ", message=" + this.getMessage() + ", location=" + String.valueOf(this.getLocation()) + ", silence=" + this.isSilence() + ", thievingCape=" + this.isThievingCape() + ", ardougneDiary=" + this.getArdougneDiary() + ")";
    }

    public PickpocketData(int level, int target, String message, WorldPoint location, boolean silence, boolean thievingCape, int ardougneDiary) {
        this.level = level;
        this.target = target;
        this.message = message;
        this.location = location;
        this.silence = silence;
        this.thievingCape = thievingCape;
        this.ardougneDiary = ardougneDiary;
    }
}

