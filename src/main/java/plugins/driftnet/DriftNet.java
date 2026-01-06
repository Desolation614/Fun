/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.runelite.api.GameObject
 *  net.runelite.api.coords.WorldPoint
 */
package net.runelite.client.plugins.driftnet;

import java.util.Set;
import net.runelite.api.GameObject;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.driftnet.DriftNetStatus;

class DriftNet {
    private final int objectId;
    private final int statusVarbit;
    private final int countVarbit;
    private final Set<WorldPoint> adjacentTiles;
    private GameObject net;
    private DriftNetStatus status;
    private int count;
    private DriftNetStatus prevTickStatus;

    boolean isNotAcceptingFish() {
        return this.status != DriftNetStatus.CATCH && this.status != DriftNetStatus.SET || this.prevTickStatus != DriftNetStatus.CATCH && this.prevTickStatus != DriftNetStatus.SET;
    }

    String getFormattedCountText() {
        return this.status != DriftNetStatus.UNSET ? this.count + "/10" : "";
    }

    public int getObjectId() {
        return this.objectId;
    }

    public Set<WorldPoint> getAdjacentTiles() {
        return this.adjacentTiles;
    }

    public GameObject getNet() {
        return this.net;
    }

    public DriftNetStatus getStatus() {
        return this.status;
    }

    public int getCount() {
        return this.count;
    }

    public DriftNetStatus getPrevTickStatus() {
        return this.prevTickStatus;
    }

    public void setNet(GameObject net) {
        this.net = net;
    }

    public void setStatus(DriftNetStatus status) {
        this.status = status;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof DriftNet)) {
            return false;
        }
        DriftNet other = (DriftNet)o;
        if (!other.canEqual(this)) {
            return false;
        }
        if (this.getObjectId() != other.getObjectId()) {
            return false;
        }
        if (this.getStatusVarbit() != other.getStatusVarbit()) {
            return false;
        }
        if (this.getCountVarbit() != other.getCountVarbit()) {
            return false;
        }
        if (this.getCount() != other.getCount()) {
            return false;
        }
        Set<WorldPoint> this$adjacentTiles = this.getAdjacentTiles();
        Set<WorldPoint> other$adjacentTiles = other.getAdjacentTiles();
        if (this$adjacentTiles == null ? other$adjacentTiles != null : !((Object)this$adjacentTiles).equals(other$adjacentTiles)) {
            return false;
        }
        GameObject this$net = this.getNet();
        GameObject other$net = other.getNet();
        if (this$net == null ? other$net != null : !this$net.equals(other$net)) {
            return false;
        }
        DriftNetStatus this$status = this.getStatus();
        DriftNetStatus other$status = other.getStatus();
        if (this$status == null ? other$status != null : !((Object)((Object)this$status)).equals((Object)other$status)) {
            return false;
        }
        DriftNetStatus this$prevTickStatus = this.getPrevTickStatus();
        DriftNetStatus other$prevTickStatus = other.getPrevTickStatus();
        return !(this$prevTickStatus == null ? other$prevTickStatus != null : !((Object)((Object)this$prevTickStatus)).equals((Object)other$prevTickStatus));
    }

    protected boolean canEqual(Object other) {
        return other instanceof DriftNet;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        result = result * 59 + this.getObjectId();
        result = result * 59 + this.getStatusVarbit();
        result = result * 59 + this.getCountVarbit();
        result = result * 59 + this.getCount();
        Set<WorldPoint> $adjacentTiles = this.getAdjacentTiles();
        result = result * 59 + ($adjacentTiles == null ? 43 : ((Object)$adjacentTiles).hashCode());
        GameObject $net = this.getNet();
        result = result * 59 + ($net == null ? 43 : $net.hashCode());
        DriftNetStatus $status = this.getStatus();
        result = result * 59 + ($status == null ? 43 : ((Object)((Object)$status)).hashCode());
        DriftNetStatus $prevTickStatus = this.getPrevTickStatus();
        result = result * 59 + ($prevTickStatus == null ? 43 : ((Object)((Object)$prevTickStatus)).hashCode());
        return result;
    }

    public String toString() {
        return "DriftNet(objectId=" + this.getObjectId() + ", statusVarbit=" + this.getStatusVarbit() + ", countVarbit=" + this.getCountVarbit() + ", adjacentTiles=" + String.valueOf(this.getAdjacentTiles()) + ", net=" + String.valueOf(this.getNet()) + ", status=" + String.valueOf((Object)this.getStatus()) + ", count=" + this.getCount() + ", prevTickStatus=" + String.valueOf((Object)this.getPrevTickStatus()) + ")";
    }

    public DriftNet(int objectId, int statusVarbit, int countVarbit, Set<WorldPoint> adjacentTiles) {
        this.objectId = objectId;
        this.statusVarbit = statusVarbit;
        this.countVarbit = countVarbit;
        this.adjacentTiles = adjacentTiles;
    }

    public int getStatusVarbit() {
        return this.statusVarbit;
    }

    public int getCountVarbit() {
        return this.countVarbit;
    }

    public void setPrevTickStatus(DriftNetStatus prevTickStatus) {
        this.prevTickStatus = prevTickStatus;
    }
}

