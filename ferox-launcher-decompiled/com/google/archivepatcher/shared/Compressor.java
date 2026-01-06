/*
 * Decompiled with CFR 0.152.
 */
package com.google.archivepatcher.shared;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public interface Compressor {
    public void compress(InputStream var1, OutputStream var2) throws IOException;
}

