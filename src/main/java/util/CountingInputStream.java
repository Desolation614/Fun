/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.util;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.function.IntConsumer;

public class CountingInputStream
extends FilterInputStream {
    private final IntConsumer changed;
    private int read = 0;

    public CountingInputStream(InputStream in, IntConsumer changed) {
        super(in);
        this.changed = changed;
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        int thisRead = super.read(b, off, len);
        if (thisRead > 0) {
            this.read += thisRead;
        }
        this.changed.accept(this.read);
        return thisRead;
    }

    @Override
    public int read() throws IOException {
        int val = super.read();
        if (val != -1) {
            ++this.read;
        }
        return val;
    }

    @Override
    public long skip(long n) throws IOException {
        long thisRead = this.in.skip(n);
        this.read = (int)((long)this.read + thisRead);
        this.changed.accept(this.read);
        return thisRead;
    }

    @Override
    public boolean markSupported() {
        return false;
    }
}

