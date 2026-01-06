/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.util;

public enum OSType {
    Windows,
    MacOS,
    Linux,
    Other;

    private static final OSType OS_TYPE;

    public static OSType getOSType() {
        return OS_TYPE;
    }

    static {
        String OS = System.getProperty("os.name", "generic").toLowerCase();
        OS_TYPE = OS.contains("mac") || OS.contains("darwin") ? MacOS : (OS.contains("win") ? Windows : (OS.contains("nux") ? Linux : Other));
    }
}

