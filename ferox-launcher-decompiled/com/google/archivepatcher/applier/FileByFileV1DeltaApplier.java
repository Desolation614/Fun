/*
 * Decompiled with CFR 0.152.
 */
package com.google.archivepatcher.applier;

import com.google.archivepatcher.applier.DeltaApplier;
import com.google.archivepatcher.applier.LimitedInputStream;
import com.google.archivepatcher.applier.PartiallyCompressingOutputStream;
import com.google.archivepatcher.applier.PatchApplyPlan;
import com.google.archivepatcher.applier.PatchReader;
import com.google.archivepatcher.applier.bsdiff.BsDiffDeltaApplier;
import com.google.archivepatcher.shared.DeltaFriendlyFile;
import com.google.archivepatcher.shared.RandomAccessFileOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class FileByFileV1DeltaApplier
implements DeltaApplier {
    private static final int DEFAULT_COPY_BUFFER_SIZE = 32768;
    private final File tempDir;

    public FileByFileV1DeltaApplier() {
        this(null);
    }

    public FileByFileV1DeltaApplier(File tempDir) {
        if (tempDir == null) {
            tempDir = new File(System.getProperty("java.io.tmpdir"));
        }
        this.tempDir = tempDir;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void applyDelta(File oldBlob, InputStream deltaIn, OutputStream newBlobOut) throws IOException {
        if (!this.tempDir.exists()) {
            this.tempDir.mkdirs();
        }
        File tempFile = File.createTempFile("gfbfv1", "old", this.tempDir);
        try {
            this.applyDeltaInternal(oldBlob, tempFile, deltaIn, newBlobOut);
        }
        finally {
            tempFile.delete();
        }
    }

    private void applyDeltaInternal(File oldBlob, File deltaFriendlyOldBlob, InputStream deltaIn, OutputStream newBlobOut) throws IOException {
        PatchReader patchReader = new PatchReader();
        PatchApplyPlan plan = patchReader.readPatchApplyPlan(deltaIn);
        this.writeDeltaFriendlyOldBlob(plan, oldBlob, deltaFriendlyOldBlob);
        long deltaLength = plan.getDeltaDescriptors().get(0).getDeltaLength();
        DeltaApplier deltaApplier = this.getDeltaApplier();
        LimitedInputStream limitedDeltaIn = new LimitedInputStream(deltaIn, deltaLength);
        PartiallyCompressingOutputStream recompressingNewBlobOut = new PartiallyCompressingOutputStream(plan.getDeltaFriendlyNewFileRecompressionPlan(), newBlobOut, 32768);
        deltaApplier.applyDelta(deltaFriendlyOldBlob, limitedDeltaIn, recompressingNewBlobOut);
        recompressingNewBlobOut.flush();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void writeDeltaFriendlyOldBlob(PatchApplyPlan plan, File oldBlob, File deltaFriendlyOldBlob) throws IOException {
        RandomAccessFileOutputStream deltaFriendlyOldFileOut = null;
        try {
            deltaFriendlyOldFileOut = new RandomAccessFileOutputStream(deltaFriendlyOldBlob, plan.getDeltaFriendlyOldFileSize());
            DeltaFriendlyFile.generateDeltaFriendlyFile(plan.getOldFileUncompressionPlan(), oldBlob, deltaFriendlyOldFileOut, false, 32768);
        }
        finally {
            try {
                deltaFriendlyOldFileOut.close();
            }
            catch (Exception exception) {}
        }
    }

    protected DeltaApplier getDeltaApplier() {
        return new BsDiffDeltaApplier();
    }
}

