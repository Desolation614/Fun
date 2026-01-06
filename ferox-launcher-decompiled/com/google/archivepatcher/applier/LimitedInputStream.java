/*
 * Decompiled with CFR 0.152.
 */
package com.google.archivepatcher.applier;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

public class LimitedInputStream
extends FilterInputStream {
    private long numToRead;
    private byte[] ONE_BYTE = new byte[1];

    public LimitedInputStream(InputStream in, long numToRead) {
        super(in);
        if (numToRead < 0L) {
            throw new IllegalArgumentException("numToRead must be >= 0: " + numToRead);
        }
        this.numToRead = numToRead;
    }

    @Override
    public int read() throws IOException {
        if (this.read(this.ONE_BYTE, 0, 1) == 1) {
            return this.ONE_BYTE[0];
        }
        return -1;
    }

    @Override
    public int read(byte[] b) throws IOException {
        return this.read(b, 0, b.length);
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        if (this.numToRead == 0L) {
            return -1;
        }
        int maxRead = (int)Math.min((long)len, this.numToRead);
        int numRead = this.in.read(b, off, maxRead);
        if (numRead > 0) {
            this.numToRead -= (long)numRead;
        }
        return numRead;
    }
}

