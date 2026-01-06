/*
 * Decompiled with CFR 0.152.
 */
package com.google.archivepatcher.shared;

import com.google.archivepatcher.shared.PartiallyUncompressingPipe;
import com.google.archivepatcher.shared.RandomAccessFileInputStream;
import com.google.archivepatcher.shared.TypedRange;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class DeltaFriendlyFile {
    public static final int DEFAULT_COPY_BUFFER_SIZE = 32768;

    public static <T> List<TypedRange<T>> generateDeltaFriendlyFile(List<TypedRange<T>> rangesToUncompress, File file, OutputStream deltaFriendlyOut) throws IOException {
        return DeltaFriendlyFile.generateDeltaFriendlyFile(rangesToUncompress, file, deltaFriendlyOut, true, 32768);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static <T> List<TypedRange<T>> generateDeltaFriendlyFile(List<TypedRange<T>> rangesToUncompress, File file, OutputStream deltaFriendlyOut, boolean generateInverse, int copyBufferSize) throws IOException {
        ArrayList<TypedRange<T>> inverseRanges = null;
        if (generateInverse) {
            inverseRanges = new ArrayList<TypedRange<T>>(rangesToUncompress.size());
        }
        long lastReadOffset = 0L;
        RandomAccessFileInputStream oldFileRafis = null;
        PartiallyUncompressingPipe filteredOut = new PartiallyUncompressingPipe(deltaFriendlyOut, copyBufferSize);
        try {
            oldFileRafis = new RandomAccessFileInputStream(file);
            for (TypedRange<T> rangeToUncompress : rangesToUncompress) {
                long gap = rangeToUncompress.getOffset() - lastReadOffset;
                if (gap > 0L) {
                    oldFileRafis.setRange(lastReadOffset, gap);
                    filteredOut.pipe(oldFileRafis, PartiallyUncompressingPipe.Mode.COPY);
                }
                oldFileRafis.setRange(rangeToUncompress.getOffset(), rangeToUncompress.getLength());
                long inverseRangeStart = filteredOut.getNumBytesWritten();
                filteredOut.pipe(oldFileRafis, PartiallyUncompressingPipe.Mode.UNCOMPRESS_NOWRAP);
                lastReadOffset = rangeToUncompress.getOffset() + rangeToUncompress.getLength();
                if (!generateInverse) continue;
                long inverseRangeEnd = filteredOut.getNumBytesWritten();
                long inverseRangeLength = inverseRangeEnd - inverseRangeStart;
                TypedRange<T> inverseRange = new TypedRange<T>(inverseRangeStart, inverseRangeLength, rangeToUncompress.getMetadata());
                inverseRanges.add(inverseRange);
            }
            long bytesLeft = oldFileRafis.length() - lastReadOffset;
            if (bytesLeft > 0L) {
                oldFileRafis.setRange(lastReadOffset, bytesLeft);
                filteredOut.pipe(oldFileRafis, PartiallyUncompressingPipe.Mode.COPY);
            }
        }
        finally {
            try {
                oldFileRafis.close();
            }
            catch (Exception exception) {}
            try {
                filteredOut.close();
            }
            catch (Exception exception) {}
        }
        return inverseRanges;
    }
}

