/*
 * Decompiled with CFR 0.152.
 */
package com.google.archivepatcher.applier.bsdiff;

import com.google.archivepatcher.applier.DeltaApplier;
import com.google.archivepatcher.applier.bsdiff.BsPatch;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;

public class BsDiffDeltaApplier
implements DeltaApplier {
    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void applyDelta(File oldBlob, InputStream deltaIn, OutputStream newBlobOut) throws IOException {
        RandomAccessFile oldBlobRaf = null;
        try {
            oldBlobRaf = new RandomAccessFile(oldBlob, "r");
            BsPatch.applyPatch(oldBlobRaf, newBlobOut, deltaIn);
        }
        finally {
            try {
                oldBlobRaf.close();
            }
            catch (Exception exception) {}
        }
    }
}

