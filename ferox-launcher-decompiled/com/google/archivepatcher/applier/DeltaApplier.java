/*
 * Decompiled with CFR 0.152.
 */
package com.google.archivepatcher.applier;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public interface DeltaApplier {
    public void applyDelta(File var1, InputStream var2, OutputStream var3) throws IOException;
}

