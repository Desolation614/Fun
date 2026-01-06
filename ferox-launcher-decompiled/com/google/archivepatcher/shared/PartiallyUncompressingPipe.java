/*
 * Decompiled with CFR 0.152.
 */
package com.google.archivepatcher.shared;

import com.google.archivepatcher.shared.CountingOutputStream;
import com.google.archivepatcher.shared.DeflateUncompressor;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class PartiallyUncompressingPipe
implements Closeable {
    private final DeflateUncompressor uncompressor;
    private final CountingOutputStream out;
    private final byte[] copyBuffer;

    public PartiallyUncompressingPipe(OutputStream out, int copyBufferSize) {
        this.out = new CountingOutputStream(out);
        this.uncompressor = new DeflateUncompressor();
        this.uncompressor.setCaching(true);
        this.copyBuffer = new byte[copyBufferSize];
    }

    public long pipe(InputStream in, Mode mode) throws IOException {
        long bytesWrittenBefore = this.out.getNumBytesWritten();
        if (mode == Mode.COPY) {
            int numRead = 0;
            while ((numRead = in.read(this.copyBuffer)) >= 0) {
                this.out.write(this.copyBuffer, 0, numRead);
            }
        } else {
            this.uncompressor.setNowrap(mode == Mode.UNCOMPRESS_NOWRAP);
            this.uncompressor.uncompress(in, this.out);
        }
        return this.out.getNumBytesWritten() - bytesWrittenBefore;
    }

    public long getNumBytesWritten() {
        return this.out.getNumBytesWritten();
    }

    @Override
    public void close() throws IOException {
        this.uncompressor.release();
        this.out.close();
    }

    public static enum Mode {
        COPY,
        UNCOMPRESS_WRAPPED,
        UNCOMPRESS_NOWRAP;

    }
}

