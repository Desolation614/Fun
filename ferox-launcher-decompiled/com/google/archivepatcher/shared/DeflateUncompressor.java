/*
 * Decompiled with CFR 0.152.
 */
package com.google.archivepatcher.shared;

import com.google.archivepatcher.shared.Uncompressor;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;

public class DeflateUncompressor
implements Uncompressor {
    private boolean nowrap = true;
    private int inputBufferSize = 32768;
    private int outputBufferSize = 32768;
    private Inflater inflater = null;
    private boolean caching = false;

    public boolean isNowrap() {
        return this.nowrap;
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

    public void setNowrap(boolean nowrap) {
        if (nowrap != this.nowrap) {
            this.release();
            this.nowrap = nowrap;
        }
    }

    public boolean isCaching() {
        return this.caching;
    }

    public void setCaching(boolean caching) {
        this.caching = caching;
    }

    protected Inflater createOrResetInflater() {
        Inflater result = this.inflater;
        if (result == null) {
            result = new Inflater(this.nowrap);
            if (this.caching) {
                this.inflater = result;
            }
        } else {
            result.reset();
        }
        return result;
    }

    public void release() {
        if (this.inflater != null) {
            this.inflater.end();
            this.inflater = null;
        }
    }

    @Override
    public void uncompress(InputStream compressedIn, OutputStream uncompressedOut) throws IOException {
        InflaterInputStream inflaterIn = new InflaterInputStream(compressedIn, this.createOrResetInflater(), this.inputBufferSize);
        byte[] buffer = new byte[this.outputBufferSize];
        int numRead = 0;
        while ((numRead = inflaterIn.read(buffer)) >= 0) {
            uncompressedOut.write(buffer, 0, numRead);
        }
        if (!this.isCaching()) {
            this.release();
        }
    }
}

