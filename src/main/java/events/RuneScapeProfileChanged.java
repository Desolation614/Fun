/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package net.runelite.client.events;

import javax.annotation.Nullable;

public final class RuneScapeProfileChanged {
    @Nullable
    private final String previousProfile;
    @Nullable
    private final String newProfile;

    public RuneScapeProfileChanged(@Nullable String previousProfile, @Nullable String newProfile) {
        this.previousProfile = previousProfile;
        this.newProfile = newProfile;
    }

    @Nullable
    public String getPreviousProfile() {
        return this.previousProfile;
    }

    @Nullable
    public String getNewProfile() {
        return this.newProfile;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof RuneScapeProfileChanged)) {
            return false;
        }
        RuneScapeProfileChanged other = (RuneScapeProfileChanged)o;
        String this$previousProfile = this.getPreviousProfile();
        String other$previousProfile = other.getPreviousProfile();
        if (this$previousProfile == null ? other$previousProfile != null : !this$previousProfile.equals(other$previousProfile)) {
            return false;
        }
        String this$newProfile = this.getNewProfile();
        String other$newProfile = other.getNewProfile();
        return !(this$newProfile == null ? other$newProfile != null : !this$newProfile.equals(other$newProfile));
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        String $previousProfile = this.getPreviousProfile();
        result = result * 59 + ($previousProfile == null ? 43 : $previousProfile.hashCode());
        String $newProfile = this.getNewProfile();
        result = result * 59 + ($newProfile == null ? 43 : $newProfile.hashCode());
        return result;
    }

    public String toString() {
        return "RuneScapeProfileChanged(previousProfile=" + this.getPreviousProfile() + ", newProfile=" + this.getNewProfile() + ")";
    }
}

