/*
 * Decompiled with CFR 0.152.
 */
package com.google.archivepatcher.applier.bsdiff;

import com.google.archivepatcher.applier.PatchFormatException;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.util.logging.Logger;

public class BsPatch {
    private static final boolean VERBOSE = false;
    private static final String SIGNATURE = "ENDSLEY/BSDIFF43";
    private static final int PATCH_BUFFER_SIZE = 51200;
    private static final long NEGATIVE_LONG_SIGN_MASK = Long.MIN_VALUE;
    private static final int PATCH_STREAM_BUFFER_SIZE = 4096;
    private static final int OUTPUT_STREAM_BUFFER_SIZE = 16384;
    private static final Logger logger = Logger.getLogger(BsPatch.class.getName());

    public static void applyPatch(RandomAccessFile oldData, OutputStream newData, InputStream patchData) throws PatchFormatException, IOException {
        BsPatch.applyPatch(oldData, newData, patchData, null);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void applyPatch(RandomAccessFile oldData, OutputStream newData, InputStream patchData, Long expectedNewSize) throws PatchFormatException, IOException {
        patchData = new BufferedInputStream(patchData, 4096);
        newData = new BufferedOutputStream(newData, 16384);
        try {
            BsPatch.applyPatchInternal(oldData, newData, patchData, expectedNewSize);
        }
        finally {
            newData.flush();
        }
    }

    private static void applyPatchInternal(RandomAccessFile oldData, OutputStream newData, InputStream patchData, Long expectedNewSize) throws PatchFormatException, IOException {
        byte[] signatureBuffer = new byte[SIGNATURE.length()];
        try {
            BsPatch.readFully(patchData, signatureBuffer, 0, signatureBuffer.length);
        }
        catch (IOException e) {
            throw new PatchFormatException("truncated signature");
        }
        String signature = new String(signatureBuffer, 0, signatureBuffer.length, "US-ASCII");
        if (!SIGNATURE.equals(signature)) {
            throw new PatchFormatException("bad signature");
        }
        long oldSize = oldData.length();
        if (oldSize > Integer.MAX_VALUE) {
            throw new PatchFormatException("bad oldSize");
        }
        long newSize = BsPatch.readBsdiffLong(patchData);
        if (newSize < 0L || newSize > Integer.MAX_VALUE) {
            throw new PatchFormatException("bad newSize");
        }
        if (expectedNewSize != null && expectedNewSize != newSize) {
            throw new PatchFormatException("expectedNewSize != newSize");
        }
        byte[] buffer1 = new byte[51200];
        byte[] buffer2 = new byte[51200];
        long oldDataOffset = 0L;
        long newDataBytesWritten = 0L;
        boolean numDirectives = false;
        while (newDataBytesWritten < newSize) {
            long diffSegmentLength = BsPatch.readBsdiffLong(patchData);
            long copySegmentLength = BsPatch.readBsdiffLong(patchData);
            long offsetToNextInput = BsPatch.readBsdiffLong(patchData);
            if (diffSegmentLength < 0L || diffSegmentLength > Integer.MAX_VALUE) {
                throw new PatchFormatException("bad diffSegmentLength");
            }
            if (copySegmentLength < 0L || copySegmentLength > Integer.MAX_VALUE) {
                throw new PatchFormatException("bad copySegmentLength");
            }
            if (offsetToNextInput < Integer.MIN_VALUE || offsetToNextInput > Integer.MAX_VALUE) {
                throw new PatchFormatException("bad offsetToNextInput");
            }
            long expectedFinalNewDataBytesWritten = newDataBytesWritten + diffSegmentLength + copySegmentLength;
            if (expectedFinalNewDataBytesWritten > newSize) {
                throw new PatchFormatException("expectedFinalNewDataBytesWritten too large");
            }
            long expectedFinalOldDataOffset = oldDataOffset + diffSegmentLength + offsetToNextInput;
            if (expectedFinalOldDataOffset > oldSize) {
                throw new PatchFormatException("expectedFinalOldDataOffset too large");
            }
            if (expectedFinalOldDataOffset < 0L) {
                throw new PatchFormatException("expectedFinalOldDataOffset is negative");
            }
            oldData.seek(oldDataOffset);
            if (diffSegmentLength > 0L) {
                BsPatch.transformBytes((int)diffSegmentLength, patchData, oldData, newData, buffer1, buffer2);
            }
            if (copySegmentLength > 0L) {
                BsPatch.pipe(patchData, newData, buffer1, (int)copySegmentLength);
            }
            newDataBytesWritten = expectedFinalNewDataBytesWritten;
            oldDataOffset = expectedFinalOldDataOffset;
        }
    }

    static void transformBytes(int diffLength, InputStream patchData, RandomAccessFile oldData, OutputStream newData, byte[] buffer1, byte[] buffer2) throws IOException {
        int numBytesThisRound;
        for (int numBytesLeft = diffLength; numBytesLeft > 0; numBytesLeft -= numBytesThisRound) {
            numBytesThisRound = Math.min(numBytesLeft, buffer1.length);
            oldData.readFully(buffer1, 0, numBytesThisRound);
            BsPatch.readFully(patchData, buffer2, 0, numBytesThisRound);
            for (int i = 0; i < numBytesThisRound; ++i) {
                int n = i;
                buffer1[n] = (byte)(buffer1[n] + buffer2[i]);
            }
            newData.write(buffer1, 0, numBytesThisRound);
        }
    }

    static final long readBsdiffLong(InputStream in) throws PatchFormatException, IOException {
        long result = 0L;
        for (int bitshift = 0; bitshift < 64; bitshift += 8) {
            result |= (long)in.read() << bitshift;
        }
        if (result == Long.MIN_VALUE) {
            throw new PatchFormatException("read negative zero");
        }
        if ((result & Long.MIN_VALUE) != 0L) {
            result = -(result & Long.MAX_VALUE);
        }
        return result;
    }

    static void readFully(InputStream in, byte[] destination, int startAt, int numBytes) throws IOException {
        int readNow;
        for (int numRead = 0; numRead < numBytes; numRead += readNow) {
            readNow = in.read(destination, startAt + numRead, numBytes - numRead);
            if (readNow != -1) continue;
            throw new IOException("truncated input stream");
        }
    }

    static void pipe(InputStream in, OutputStream out, byte[] buffer, int copyLength) throws IOException {
        while (copyLength > 0) {
            int maxCopy = Math.min(buffer.length, copyLength);
            BsPatch.readFully(in, buffer, 0, maxCopy);
            out.write(buffer, 0, maxCopy);
            copyLength -= maxCopy;
        }
    }
}

