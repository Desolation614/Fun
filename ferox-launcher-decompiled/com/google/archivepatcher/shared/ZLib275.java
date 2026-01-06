/*
 * Decompiled with CFR 0.152.
 */
package com.google.archivepatcher.shared;

import com.google.archivepatcher.shared.DefaultDeflateCompatibilityWindow;
import java.util.zip.CRC32;
import java.util.zip.Deflater;

public class ZLib275 {
    public static final boolean isBuggy;

    private static long compress(Deflater d, byte[] data) {
        byte[] out = new byte[1024];
        d.setInput(data);
        d.finish();
        CRC32 crc = new CRC32();
        while (!d.finished()) {
            int l = d.deflate(out);
            crc.update(out, 0, l);
        }
        return crc.getValue();
    }

    static {
        byte[] corpus = DefaultDeflateCompatibilityWindow.getCorpus();
        long baseline = ZLib275.compress(new Deflater(4), corpus);
        Deflater test = new Deflater(3);
        ZLib275.compress(test, corpus);
        test.setLevel(4);
        isBuggy = baseline != ZLib275.compress(test, corpus);
    }
}

