/*
 * Decompiled with CFR 0.152.
 */
package com.google.archivepatcher.applier;

import com.google.archivepatcher.applier.DeltaDescriptor;
import com.google.archivepatcher.shared.JreDeflateParameters;
import com.google.archivepatcher.shared.TypedRange;
import java.util.List;

public class PatchApplyPlan {
    private final List<TypedRange<Void>> oldFileUncompressionPlan;
    private final List<TypedRange<JreDeflateParameters>> deltaFriendlyNewFileRecompressionPlan;
    private final long deltaFriendlyOldFileSize;
    private final List<DeltaDescriptor> deltaDescriptors;

    public PatchApplyPlan(List<TypedRange<Void>> oldFileUncompressionPlan, long deltaFriendlyOldFileSize, List<TypedRange<JreDeflateParameters>> deltaFriendlyNewFileRecompressionPlan, List<DeltaDescriptor> deltaDescriptors) {
        this.oldFileUncompressionPlan = oldFileUncompressionPlan;
        this.deltaFriendlyOldFileSize = deltaFriendlyOldFileSize;
        this.deltaFriendlyNewFileRecompressionPlan = deltaFriendlyNewFileRecompressionPlan;
        this.deltaDescriptors = deltaDescriptors;
    }

    public List<TypedRange<Void>> getOldFileUncompressionPlan() {
        return this.oldFileUncompressionPlan;
    }

    public List<TypedRange<JreDeflateParameters>> getDeltaFriendlyNewFileRecompressionPlan() {
        return this.deltaFriendlyNewFileRecompressionPlan;
    }

    public long getDeltaFriendlyOldFileSize() {
        return this.deltaFriendlyOldFileSize;
    }

    public List<DeltaDescriptor> getDeltaDescriptors() {
        return this.deltaDescriptors;
    }
}

