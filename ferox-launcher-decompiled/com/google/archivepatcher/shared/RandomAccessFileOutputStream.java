/*
 * Decompiled with CFR 0.152.
 */
package com.google.archivepatcher.shared;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;

public class RandomAccessFileOutputStream
extends OutputStream {
    private final RandomAccessFile raf;

    public RandomAccessFileOutputStream(File outputFile, long expectedSize) throws IOException {
        this.raf = this.getRandomAccessFile(outputFile);
        if (expectedSize >= 0L) {
            this.raf.setLength(expectedSize);
            if (this.raf.length() != expectedSize) {
                throw new IOException("Unable to set the file size");
            }
        }
    }

    protected RandomAccessFile getRandomAccessFile(File file) throws IOException {
        return new RandomAccessFile(file, "rw");
    }

    @Override
    public void write(int b) throws IOException {
        this.raf.write(b);
    }

    @Override
    public void write(byte[] b) throws IOException {
        this.write(b, 0, b.length);
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        this.raf.write(b, off, len);
    }

    @Override
    public void flush() throws IOException {
        this.raf.getChannel().force(true);
    }

    @Override
    public void close() throws IOException {
        this.flush();
        this.raf.close();
    }
}

