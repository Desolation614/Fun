/*
 * Decompiled with CFR 0.152.
 */
package com.google.archivepatcher.shared;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class CountingOutputStream
extends FilterOutputStream {
    private long bytesWritten = 0L;

    public CountingOutputStream(OutputStream out) {
        super(out);
    }

    public long getNumBytesWritten() {
        return this.bytesWritten;
    }

    @Override
    public void write(int b) throws IOException {
        ++this.bytesWritten;
        this.out.write(b);
    }

    @Override
    public void write(byte[] b) throws IOException {
        this.bytesWritten += (long)b.length;
        this.out.write(b);
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        this.bytesWritten += (long)len;
        this.out.write(b, off, len);
    }
}

