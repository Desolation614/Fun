/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.plugins.timetracking.farming;

import net.runelite.client.plugins.timetracking.farming.CropState;
import net.runelite.client.plugins.timetracking.farming.Produce;

final class PatchState {
    private final Produce produce;
    private final CropState cropState;
    private final int stage;

    int getStages() {
        return this.cropState == CropState.HARVESTABLE || this.cropState == CropState.FILLING ? this.produce.getHarvestStages() : this.produce.getStages();
    }

    int getTickRate() {
        switch (this.cropState) {
            case HARVESTABLE: {
                return this.produce.getRegrowTickrate();
            }
            case GROWING: {
                return this.produce.getTickrate();
            }
        }
        return 0;
    }

    public PatchState(Produce produce, CropState cropState, int stage) {
        this.produce = produce;
        this.cropState = cropState;
        this.stage = stage;
    }

    public Produce getProduce() {
        return this.produce;
    }

    public CropState getCropState() {
        return this.cropState;
    }

    public int getStage() {
        return this.stage;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof PatchState)) {
            return false;
        }
        PatchState other = (PatchState)o;
        if (this.getStage() != other.getStage()) {
            return false;
        }
        Produce this$produce = this.getProduce();
        Produce other$produce = other.getProduce();
        if (this$produce == null ? other$produce != null : !((Object)((Object)this$produce)).equals((Object)other$produce)) {
            return false;
        }
        CropState this$cropState = this.getCropState();
        CropState other$cropState = other.getCropState();
        return !(this$cropState == null ? other$cropState != null : !((Object)((Object)this$cropState)).equals((Object)other$cropState));
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        result = result * 59 + this.getStage();
        Produce $produce = this.getProduce();
        result = result * 59 + ($produce == null ? 43 : ((Object)((Object)$produce)).hashCode());
        CropState $cropState = this.getCropState();
        result = result * 59 + ($cropState == null ? 43 : ((Object)((Object)$cropState)).hashCode());
        return result;
    }

    public String toString() {
        return "PatchState(produce=" + String.valueOf((Object)this.getProduce()) + ", cropState=" + String.valueOf((Object)this.getCropState()) + ", stage=" + this.getStage() + ")";
    }
}

