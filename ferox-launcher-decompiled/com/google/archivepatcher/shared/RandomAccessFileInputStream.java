/*
 * Decompiled with CFR 0.152.
 */
package com.google.archivepatcher.shared;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;

public class RandomAccessFileInputStream
extends InputStream {
    private final RandomAccessFile raf;
    private long mark = -1L;
    private long rangeOffset;
    private long rangeLength;
    private final long fileLength;

    public RandomAccessFileInputStream(File file) throws IOException {
        this(file, 0L, file.length());
    }

    public RandomAccessFileInputStream(File file, long rangeOffset, long rangeLength) throws IOException {
        this.raf = this.getRandomAccessFile(file);
        this.fileLength = file.length();
        this.setRange(rangeOffset, rangeLength);
    }

    protected RandomAccessFile getRandomAccessFile(File file) throws IOException {
        return new RandomAccessFile(file, "r");
    }

    public void setRange(long rangeOffset, long rangeLength) throws IOException {
        if (rangeOffset < 0L) {
            throw new IllegalArgumentException("rangeOffset must be >= 0");
        }
        if (rangeLength < 0L) {
            throw new IllegalArgumentException("rangeLength must be >= 0");
        }
        if (rangeOffset + rangeLength > this.fileLength) {
            throw new IllegalArgumentException("Read range exceeds file length");
        }
        if (rangeOffset + rangeLength < 0L) {
            throw new IllegalArgumentException("Insane input size not supported");
        }
        this.rangeOffset = rangeOffset;
        this.rangeLength = rangeLength;
        this.mark = rangeOffset;
        this.reset();
        this.mark = -1L;
    }

    @Override
    public int available() throws IOException {
        long rangeRelativePosition = this.raf.getFilePointer() - this.rangeOffset;
        long result = this.rangeLength - rangeRelativePosition;
        if (result > Integer.MAX_VALUE) {
            return Integer.MAX_VALUE;
        }
        return (int)result;
    }

    public long getPosition() throws IOException {
        return this.raf.getFilePointer();
    }

    @Override
    public void close() throws IOException {
        this.raf.close();
    }

    @Override
    public int read() throws IOException {
        if (this.available() <= 0) {
            return -1;
        }
        return this.raf.read();
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        if (len <= 0) {
            return 0;
        }
        int available = this.available();
        if (available <= 0) {
            return -1;
        }
        int result = this.raf.read(b, off, Math.min(len, available));
        return result;
    }

    @Override
    public int read(byte[] b) throws IOException {
        return this.read(b, 0, b.length);
    }

    @Override
    public long skip(long n) throws IOException {
        if (n <= 0L) {
            return 0L;
        }
        int available = this.available();
        if (available <= 0) {
            return 0L;
        }
        int skipAmount = (int)Math.min((long)available, n);
        this.raf.seek(this.raf.getFilePointer() + (long)skipAmount);
        return skipAmount;
    }

    @Override
    public boolean markSupported() {
        return true;
    }

    @Override
    public void mark(int readlimit) {
        try {
            this.mark = this.raf.getFilePointer();
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void reset() throws IOException {
        if (this.mark < 0L) {
            throw new IOException("mark not set");
        }
        this.raf.seek(this.mark);
    }

    public long length() {
        return this.fileLength;
    }
}

