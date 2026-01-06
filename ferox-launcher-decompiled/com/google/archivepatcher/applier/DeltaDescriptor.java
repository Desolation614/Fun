/*
 * Decompiled with CFR 0.152.
 */
package com.google.archivepatcher.applier;

import com.google.archivepatcher.shared.PatchConstants;
import com.google.archivepatcher.shared.TypedRange;

public class DeltaDescriptor {
    private final PatchConstants.DeltaFormat format;
    private final TypedRange<Void> deltaFriendlyOldFileRange;
    private final TypedRange<Void> deltaFriendlyNewFileRange;
    private final long deltaLength;

    public DeltaDescriptor(PatchConstants.DeltaFormat format, TypedRange<Void> deltaFriendlyOldFileRange, TypedRange<Void> deltaFriendlyNewFileRange, long deltaLength) {
        this.format = format;
        this.deltaFriendlyOldFileRange = deltaFriendlyOldFileRange;
        this.deltaFriendlyNewFileRange = deltaFriendlyNewFileRange;
        this.deltaLength = deltaLength;
    }

    public PatchConstants.DeltaFormat getFormat() {
        return this.format;
    }

    public TypedRange<Void> getDeltaFriendlyOldFileRange() {
        return this.deltaFriendlyOldFileRange;
    }

    public TypedRange<Void> getDeltaFriendlyNewFileRange() {
        return this.deltaFriendlyNewFileRange;
    }

    public long getDeltaLength() {
        return this.deltaLength;
    }

    public int hashCode() {
        int prime = 31;
        int result = 1;
        result = 31 * result + (this.deltaFriendlyNewFileRange == null ? 0 : this.deltaFriendlyNewFileRange.hashCode());
        result = 31 * result + (this.deltaFriendlyOldFileRange == null ? 0 : this.deltaFriendlyOldFileRange.hashCode());
        result = 31 * result + (int)(this.deltaLength ^ this.deltaLength >>> 32);
        result = 31 * result + (this.format == null ? 0 : this.format.hashCode());
        return result;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        DeltaDescriptor other = (DeltaDescriptor)obj;
        if (this.deltaFriendlyNewFileRange == null ? other.deltaFriendlyNewFileRange != null : !this.deltaFriendlyNewFileRange.equals(other.deltaFriendlyNewFileRange)) {
            return false;
        }
        if (this.deltaFriendlyOldFileRange == null ? other.deltaFriendlyOldFileRange != null : !this.deltaFriendlyOldFileRange.equals(other.deltaFriendlyOldFileRange)) {
            return false;
        }
        if (this.deltaLength != other.deltaLength) {
            return false;
        }
        return this.format == other.format;
    }
}

