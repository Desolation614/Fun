/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.plugins.timetracking.farming;

import net.runelite.client.plugins.timetracking.farming.FarmingPatch;

final class ProfilePatch {
    private final FarmingPatch patch;
    private final String rsProfileKey;

    public ProfilePatch(FarmingPatch patch, String rsProfileKey) {
        this.patch = patch;
        this.rsProfileKey = rsProfileKey;
    }

    public FarmingPatch getPatch() {
        return this.patch;
    }

    public String getRsProfileKey() {
        return this.rsProfileKey;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof ProfilePatch)) {
            return false;
        }
        ProfilePatch other = (ProfilePatch)o;
        FarmingPatch this$patch = this.getPatch();
        FarmingPatch other$patch = other.getPatch();
        if (this$patch == null ? other$patch != null : !this$patch.equals(other$patch)) {
            return false;
        }
        String this$rsProfileKey = this.getRsProfileKey();
        String other$rsProfileKey = other.getRsProfileKey();
        return !(this$rsProfileKey == null ? other$rsProfileKey != null : !this$rsProfileKey.equals(other$rsProfileKey));
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        FarmingPatch $patch = this.getPatch();
        result = result * 59 + ($patch == null ? 43 : $patch.hashCode());
        String $rsProfileKey = this.getRsProfileKey();
        result = result * 59 + ($rsProfileKey == null ? 43 : $rsProfileKey.hashCode());
        return result;
    }

    public String toString() {
        return "ProfilePatch(patch=" + String.valueOf(this.getPatch()) + ", rsProfileKey=" + this.getRsProfileKey() + ")";
    }
}

