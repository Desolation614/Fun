/*
 * Decompiled with CFR 0.152.
 */
package com.google.archivepatcher.shared;

import com.google.archivepatcher.shared.Compressor;
import com.google.archivepatcher.shared.ZLib275;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;

public class DeflateCompressor
implements Compressor {
    private int compressionLevel = -1;
    private int strategy = 0;
    private boolean nowrap = true;
    private int inputBufferSize = 32768;
    private int outputBufferSize = 32768;
    private Deflater deflater = null;
    private boolean caching = false;

    public boolean isNowrap() {
        return this.nowrap;
    }

    public void setNowrap(boolean nowrap) {
        if (nowrap != this.nowrap) {
            this.release();
            this.nowrap = nowrap;
        }
    }

    public int getCompressionLevel() {
        return this.compressionLevel;
    }

    public void setCompressionLevel(int compressionLevel) {
        if (compressionLevel < 0 || compressionLevel > 9) {
            throw new IllegalArgumentException("compressionLevel must be in the range [0,9]: " + compressionLevel);
        }
        if (this.deflater != null && compressionLevel != this.compressionLevel) {
            this.deflater.reset();
            this.deflater.setLevel(compressionLevel);
        }
        this.compressionLevel = compressionLevel;
    }

    public int getStrategy() {
        return this.strategy;
    }

    public void setStrategy(int strategy) {
        if (this.deflater != null && strategy != this.strategy) {
            this.deflater.reset();
            this.deflater.setStrategy(strategy);
        }
        this.strategy = strategy;
    }

    public int getInputBufferSize() {
        return this.inputBufferSize;
    }

    public void setInputBufferSize(int inputBufferSize) {
        this.inputBufferSize = inputBufferSize;
    }

    public int getOutputBufferSize() {
        return this.outputBufferSize;
    }

    public void setOutputBufferSize(int outputBufferSize) {
        this.outputBufferSize = outputBufferSize;
    }

    public boolean isCaching() {
        return this.caching;
    }

    public void setCaching(boolean caching) {
        this.caching = caching && !ZLib275.isBuggy;
    }

    public Deflater createOrResetDeflater() {
        Deflater result = this.deflater;
        if (result == null) {
            result = new Deflater(this.compressionLevel, this.nowrap);
            result.setStrategy(this.strategy);
            if (this.caching) {
                this.deflater = result;
            }
        } else {
            result.reset();
        }
        return result;
    }

    public void release() {
        if (this.deflater != null) {
            this.deflater.end();
            this.deflater = null;
        }
    }

    @Override
    public void compress(InputStream uncompressedIn, OutputStream compressedOut) throws IOException {
        byte[] buffer = new byte[this.inputBufferSize];
        Deflater deflater = this.createOrResetDeflater();
        DeflaterOutputStream deflaterOut = new DeflaterOutputStream(compressedOut, deflater, this.outputBufferSize);
        int numRead = 0;
        while ((numRead = uncompressedIn.read(buffer)) >= 0) {
            deflaterOut.write(buffer, 0, numRead);
        }
        deflaterOut.finish();
        deflaterOut.flush();
        if (!this.caching) {
            deflater.end();
        }
    }
}

