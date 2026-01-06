/*
 * Decompiled with CFR 0.152.
 */
package com.google.archivepatcher.shared;

public class PatchConstants {
    public static final String IDENTIFIER = "GFbFv1_0";

    public static enum DeltaFormat {
        BSDIFF(0);

        public final byte patchValue;

        private DeltaFormat(byte patchValue) {
            this.patchValue = patchValue;
        }

        public static DeltaFormat fromPatchValue(byte patchValue) {
            switch (patchValue) {
                case 0: {
                    return BSDIFF;
                }
            }
            return null;
        }
    }

    public static enum CompatibilityWindowId {
        DEFAULT_DEFLATE(0);

        public final byte patchValue;

        private CompatibilityWindowId(byte patchValue) {
            this.patchValue = patchValue;
        }

        public static CompatibilityWindowId fromPatchValue(byte patchValue) {
            switch (patchValue) {
                case 0: {
                    return DEFAULT_DEFLATE;
                }
            }
            return null;
        }
    }
}

