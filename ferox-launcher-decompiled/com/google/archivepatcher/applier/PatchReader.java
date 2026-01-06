/*
 * Decompiled with CFR 0.152.
 */
package com.google.archivepatcher.applier;

import com.google.archivepatcher.applier.DeltaDescriptor;
import com.google.archivepatcher.applier.PatchApplyPlan;
import com.google.archivepatcher.applier.PatchFormatException;
import com.google.archivepatcher.shared.JreDeflateParameters;
import com.google.archivepatcher.shared.PatchConstants;
import com.google.archivepatcher.shared.TypedRange;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class PatchReader {
    public PatchApplyPlan readPatchApplyPlan(InputStream in) throws IOException {
        DataInputStream dataIn = new DataInputStream(in);
        byte[] expectedIdentifier = "GFbFv1_0".getBytes("US-ASCII");
        byte[] actualIdentifier = new byte[expectedIdentifier.length];
        dataIn.readFully(actualIdentifier);
        if (!Arrays.equals(expectedIdentifier, actualIdentifier)) {
            throw new PatchFormatException("Bad identifier");
        }
        dataIn.skip(4L);
        long deltaFriendlyOldFileSize = PatchReader.checkNonNegative(dataIn.readLong(), "delta-friendly old file size");
        int numOldFileUncompressionInstructions = (int)PatchReader.checkNonNegative(dataIn.readInt(), "old file uncompression instruction count");
        ArrayList<TypedRange<Object>> oldFileUncompressionPlan = new ArrayList<TypedRange<Object>>(numOldFileUncompressionInstructions);
        long lastReadOffset = -1L;
        for (int x = 0; x < numOldFileUncompressionInstructions; ++x) {
            long offset = PatchReader.checkNonNegative(dataIn.readLong(), "old file uncompression range offset");
            long length = PatchReader.checkNonNegative(dataIn.readLong(), "old file uncompression range length");
            if (offset < lastReadOffset) {
                throw new PatchFormatException("old file uncompression ranges out of order or overlapping");
            }
            TypedRange<Object> range = new TypedRange<Object>(offset, length, null);
            oldFileUncompressionPlan.add(range);
            lastReadOffset = offset + length;
        }
        int numDeltaFriendlyNewFileRecompressionInstructions = dataIn.readInt();
        PatchReader.checkNonNegative(numDeltaFriendlyNewFileRecompressionInstructions, "delta-friendly new file recompression instruction count");
        ArrayList<TypedRange<JreDeflateParameters>> deltaFriendlyNewFileRecompressionPlan = new ArrayList<TypedRange<JreDeflateParameters>>(numDeltaFriendlyNewFileRecompressionInstructions);
        lastReadOffset = -1L;
        for (int x = 0; x < numDeltaFriendlyNewFileRecompressionInstructions; ++x) {
            long offset = PatchReader.checkNonNegative(dataIn.readLong(), "delta-friendly new file recompression range offset");
            long length = PatchReader.checkNonNegative(dataIn.readLong(), "delta-friendly new file recompression range length");
            if (offset < lastReadOffset) {
                throw new PatchFormatException("delta-friendly new file recompression ranges out of order or overlapping");
            }
            lastReadOffset = offset + length;
            PatchReader.checkRange(dataIn.readByte(), PatchConstants.CompatibilityWindowId.DEFAULT_DEFLATE.patchValue, PatchConstants.CompatibilityWindowId.DEFAULT_DEFLATE.patchValue, "compatibility window id");
            int level = (int)PatchReader.checkRange(dataIn.readUnsignedByte(), 1L, 9L, "recompression level");
            int strategy = (int)PatchReader.checkRange(dataIn.readUnsignedByte(), 0L, 2L, "recompression strategy");
            int nowrapInt = (int)PatchReader.checkRange(dataIn.readUnsignedByte(), 0L, 1L, "recompression nowrap");
            TypedRange<JreDeflateParameters> range = new TypedRange<JreDeflateParameters>(offset, length, JreDeflateParameters.of(level, strategy, nowrapInt != 0));
            deltaFriendlyNewFileRecompressionPlan.add(range);
        }
        int numDeltaRecords = (int)PatchReader.checkRange(dataIn.readInt(), 1L, 1L, "num delta records");
        ArrayList<DeltaDescriptor> deltaDescriptors = new ArrayList<DeltaDescriptor>(numDeltaRecords);
        for (int x = 0; x < numDeltaRecords; ++x) {
            byte deltaFormatByte = (byte)PatchReader.checkRange(dataIn.readByte(), PatchConstants.DeltaFormat.BSDIFF.patchValue, PatchConstants.DeltaFormat.BSDIFF.patchValue, "delta format");
            long deltaFriendlyOldFileWorkRangeOffset = PatchReader.checkNonNegative(dataIn.readLong(), "delta-friendly old file work range offset");
            long deltaFriendlyOldFileWorkRangeLength = PatchReader.checkNonNegative(dataIn.readLong(), "delta-friendly old file work range length");
            long deltaFriendlyNewFileWorkRangeOffset = PatchReader.checkNonNegative(dataIn.readLong(), "delta-friendly new file work range offset");
            long deltaFriendlyNewFileWorkRangeLength = PatchReader.checkNonNegative(dataIn.readLong(), "delta-friendly new file work range length");
            long deltaLength = PatchReader.checkNonNegative(dataIn.readLong(), "delta length");
            DeltaDescriptor descriptor = new DeltaDescriptor(PatchConstants.DeltaFormat.fromPatchValue(deltaFormatByte), new TypedRange<Object>(deltaFriendlyOldFileWorkRangeOffset, deltaFriendlyOldFileWorkRangeLength, null), new TypedRange<Object>(deltaFriendlyNewFileWorkRangeOffset, deltaFriendlyNewFileWorkRangeLength, null), deltaLength);
            deltaDescriptors.add(descriptor);
        }
        return new PatchApplyPlan(Collections.unmodifiableList(oldFileUncompressionPlan), deltaFriendlyOldFileSize, Collections.unmodifiableList(deltaFriendlyNewFileRecompressionPlan), Collections.unmodifiableList(deltaDescriptors));
    }

    private static final long checkNonNegative(long value, String description) throws PatchFormatException {
        if (value < 0L) {
            throw new PatchFormatException("Bad value for " + description + ": " + value);
        }
        return value;
    }

    private static final long checkRange(long value, long min, long max, String description) throws PatchFormatException {
        if (value < min || value > max) {
            throw new PatchFormatException("Bad value for " + description + ": " + value + " (valid range: [" + min + "," + max + "]");
        }
        return value;
    }
}

