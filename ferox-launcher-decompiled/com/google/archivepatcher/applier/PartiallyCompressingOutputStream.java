/*
 * Decompiled with CFR 0.152.
 */
package com.google.archivepatcher.applier;

import com.google.archivepatcher.shared.JreDeflateParameters;
import com.google.archivepatcher.shared.TypedRange;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.List;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;

public class PartiallyCompressingOutputStream
extends FilterOutputStream {
    private final OutputStream normalOut;
    private Deflater deflater = null;
    private DeflaterOutputStream deflaterOut = null;
    private final byte[] internalCopyBuffer = new byte[1];
    private final int compressionBufferSize;
    private long numBytesWritten;
    private final Iterator<TypedRange<JreDeflateParameters>> rangeIterator;
    private TypedRange<JreDeflateParameters> nextCompressedRange = null;
    private JreDeflateParameters lastDeflateParameters = null;

    public PartiallyCompressingOutputStream(List<TypedRange<JreDeflateParameters>> compressionRanges, OutputStream out, int compressionBufferSize) {
        super(out);
        this.normalOut = out;
        this.compressionBufferSize = compressionBufferSize;
        this.rangeIterator = compressionRanges.iterator();
        this.nextCompressedRange = this.rangeIterator.hasNext() ? this.rangeIterator.next() : null;
    }

    @Override
    public void write(int b) throws IOException {
        this.internalCopyBuffer[0] = (byte)b;
        this.write(this.internalCopyBuffer, 0, 1);
    }

    @Override
    public void write(byte[] buffer) throws IOException {
        this.write(buffer, 0, buffer.length);
    }

    @Override
    public void write(byte[] buffer, int offset, int length) throws IOException {
        for (int writtenSoFar = 0; writtenSoFar < length; writtenSoFar += this.writeChunk(buffer, offset + writtenSoFar, length - writtenSoFar)) {
        }
    }

    private int writeChunk(byte[] buffer, int offset, int length) throws IOException {
        OutputStream writeTarget;
        int numBytesToWrite;
        if (this.bytesTillCompressionStarts() == 0L && !this.currentlyCompressing()) {
            JreDeflateParameters parameters = this.nextCompressedRange.getMetadata();
            if (this.deflater == null) {
                this.deflater = new Deflater(parameters.level, parameters.nowrap);
            } else if (parameters.requiresDeflaterChange(this.lastDeflateParameters)) {
                this.deflater.end();
                this.deflater = new Deflater(parameters.level, parameters.nowrap);
            } else if (parameters != this.lastDeflateParameters) {
                this.deflater.setLevel(parameters.level);
                this.deflater.setStrategy(parameters.strategy);
            }
            this.deflaterOut = new DeflaterOutputStream(this.normalOut, this.deflater, this.compressionBufferSize);
        }
        if (this.currentlyCompressing()) {
            numBytesToWrite = (int)Math.min((long)length, this.bytesTillCompressionEnds());
            writeTarget = this.deflaterOut;
        } else {
            writeTarget = this.normalOut;
            numBytesToWrite = this.nextCompressedRange == null ? length : (int)Math.min((long)length, this.bytesTillCompressionStarts());
        }
        writeTarget.write(buffer, offset, numBytesToWrite);
        this.numBytesWritten += (long)numBytesToWrite;
        if (this.currentlyCompressing() && this.bytesTillCompressionEnds() == 0L) {
            this.deflaterOut.finish();
            this.deflaterOut.flush();
            this.deflaterOut = null;
            this.deflater.reset();
            this.lastDeflateParameters = this.nextCompressedRange.getMetadata();
            if (this.rangeIterator.hasNext()) {
                this.nextCompressedRange = this.rangeIterator.next();
            } else {
                this.nextCompressedRange = null;
                this.deflater.end();
                this.deflater = null;
            }
        }
        return numBytesToWrite;
    }

    private boolean currentlyCompressing() {
        return this.deflaterOut != null;
    }

    private long bytesTillCompressionStarts() {
        if (this.nextCompressedRange == null) {
            return -1L;
        }
        return this.nextCompressedRange.getOffset() - this.numBytesWritten;
    }

    private long bytesTillCompressionEnds() {
        if (this.nextCompressedRange == null) {
            return -1L;
        }
        return this.nextCompressedRange.getOffset() + this.nextCompressedRange.getLength() - this.numBytesWritten;
    }
}

