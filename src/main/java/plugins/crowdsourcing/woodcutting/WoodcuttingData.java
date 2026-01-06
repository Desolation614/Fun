/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.runelite.api.coords.WorldPoint
 */
package net.runelite.client.plugins.crowdsourcing.woodcutting;

import java.util.List;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.crowdsourcing.skilling.SkillingEndReason;

public class WoodcuttingData {
    private final int level;
    private final int startTick;
    private final int endTick;
    private final List<Integer> chopTicks;
    private final List<Integer> nestTicks;
    private final int axe;
    private final int treeId;
    private final WorldPoint treeLocation;
    private final SkillingEndReason reason;

    public int getLevel() {
        return this.level;
    }

    public int getStartTick() {
        return this.startTick;
    }

    public int getEndTick() {
        return this.endTick;
    }

    public List<Integer> getChopTicks() {
        return this.chopTicks;
    }

    public List<Integer> getNestTicks() {
        return this.nestTicks;
    }

    public int getAxe() {
        return this.axe;
    }

    public int getTreeId() {
        return this.treeId;
    }

    public WorldPoint getTreeLocation() {
        return this.treeLocation;
    }

    public SkillingEndReason getReason() {
        return this.reason;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof WoodcuttingData)) {
            return false;
        }
        WoodcuttingData other = (WoodcuttingData)o;
        if (!other.canEqual(this)) {
            return false;
        }
        if (this.getLevel() != other.getLevel()) {
            return false;
        }
        if (this.getStartTick() != other.getStartTick()) {
            return false;
        }
        if (this.getEndTick() != other.getEndTick()) {
            return false;
        }
        if (this.getAxe() != other.getAxe()) {
            return false;
        }
        if (this.getTreeId() != other.getTreeId()) {
            return false;
        }
        List<Integer> this$chopTicks = this.getChopTicks();
        List<Integer> other$chopTicks = other.getChopTicks();
        if (this$chopTicks == null ? other$chopTicks != null : !((Object)this$chopTicks).equals(other$chopTicks)) {
            return false;
        }
        List<Integer> this$nestTicks = this.getNestTicks();
        List<Integer> other$nestTicks = other.getNestTicks();
        if (this$nestTicks == null ? other$nestTicks != null : !((Object)this$nestTicks).equals(other$nestTicks)) {
            return false;
        }
        WorldPoint this$treeLocation = this.getTreeLocation();
        WorldPoint other$treeLocation = other.getTreeLocation();
        if (this$treeLocation == null ? other$treeLocation != null : !this$treeLocation.equals(other$treeLocation)) {
            return false;
        }
        SkillingEndReason this$reason = this.getReason();
        SkillingEndReason other$reason = other.getReason();
        return !(this$reason == null ? other$reason != null : !((Object)((Object)this$reason)).equals((Object)other$reason));
    }

    protected boolean canEqual(Object other) {
        return other instanceof WoodcuttingData;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        result = result * 59 + this.getLevel();
        result = result * 59 + this.getStartTick();
        result = result * 59 + this.getEndTick();
        result = result * 59 + this.getAxe();
        result = result * 59 + this.getTreeId();
        List<Integer> $chopTicks = this.getChopTicks();
        result = result * 59 + ($chopTicks == null ? 43 : ((Object)$chopTicks).hashCode());
        List<Integer> $nestTicks = this.getNestTicks();
        result = result * 59 + ($nestTicks == null ? 43 : ((Object)$nestTicks).hashCode());
        WorldPoint $treeLocation = this.getTreeLocation();
        result = result * 59 + ($treeLocation == null ? 43 : $treeLocation.hashCode());
        SkillingEndReason $reason = this.getReason();
        result = result * 59 + ($reason == null ? 43 : ((Object)((Object)$reason)).hashCode());
        return result;
    }

    public String toString() {
        return "WoodcuttingData(level=" + this.getLevel() + ", startTick=" + this.getStartTick() + ", endTick=" + this.getEndTick() + ", chopTicks=" + String.valueOf(this.getChopTicks()) + ", nestTicks=" + String.valueOf(this.getNestTicks()) + ", axe=" + this.getAxe() + ", treeId=" + this.getTreeId() + ", treeLocation=" + String.valueOf(this.getTreeLocation()) + ", reason=" + String.valueOf((Object)this.getReason()) + ")";
    }

    public WoodcuttingData(int level, int startTick, int endTick, List<Integer> chopTicks, List<Integer> nestTicks, int axe, int treeId, WorldPoint treeLocation, SkillingEndReason reason) {
        this.level = level;
        this.startTick = startTick;
        this.endTick = endTick;
        this.chopTicks = chopTicks;
        this.nestTicks = nestTicks;
        this.axe = axe;
        this.treeId = treeId;
        this.treeLocation = treeLocation;
        this.reason = reason;
    }
}

