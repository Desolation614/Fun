/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.plugins.timetracking.farming;

import net.runelite.client.plugins.timetracking.farming.CropState;
import net.runelite.client.plugins.timetracking.farming.Produce;

final class PatchPrediction {
    private final Produce produce;
    private final CropState cropState;
    private final long doneEstimate;
    private final int stage;
    private final int stages;

    public PatchPrediction(Produce produce, CropState cropState, long doneEstimate, int stage, int stages) {
        this.produce = produce;
        this.cropState = cropState;
        this.doneEstimate = doneEstimate;
        this.stage = stage;
        this.stages = stages;
    }

    public Produce getProduce() {
        return this.produce;
    }

    public CropState getCropState() {
        return this.cropState;
    }

    public long getDoneEstimate() {
        return this.doneEstimate;
    }

    public int getStage() {
        return this.stage;
    }

    public int getStages() {
        return this.stages;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof PatchPrediction)) {
            return false;
        }
        PatchPrediction other = (PatchPrediction)o;
        if (this.getDoneEstimate() != other.getDoneEstimate()) {
            return false;
        }
        if (this.getStage() != other.getStage()) {
            return false;
        }
        if (this.getStages() != other.getStages()) {
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
        long $doneEstimate = this.getDoneEstimate();
        result = result * 59 + (int)($doneEstimate >>> 32 ^ $doneEstimate);
        result = result * 59 + this.getStage();
        result = result * 59 + this.getStages();
        Produce $produce = this.getProduce();
        result = result * 59 + ($produce == null ? 43 : ((Object)((Object)$produce)).hashCode());
        CropState $cropState = this.getCropState();
        result = result * 59 + ($cropState == null ? 43 : ((Object)((Object)$cropState)).hashCode());
        return result;
    }

    public String toString() {
        return "PatchPrediction(produce=" + String.valueOf((Object)this.getProduce()) + ", cropState=" + String.valueOf((Object)this.getCropState()) + ", doneEstimate=" + this.getDoneEstimate() + ", stage=" + this.getStage() + ", stages=" + this.getStages() + ")";
    }
}

