/*
 * Decompiled with CFR 0.152.
 */
package com.google.archivepatcher.applier;

import java.io.IOException;

public class PatchFormatException
extends IOException {
    public PatchFormatException(String message) {
        super(message);
    }

    public PatchFormatException(String message, Throwable cause) {
        super(message);
        this.initCause(cause);
    }
}

