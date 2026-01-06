/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package net.runelite.launcher;

import javax.annotation.Nonnull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OS {
    private static final Logger log = LoggerFactory.getLogger(OS.class);
    private static final OSType DETECTED_OS;

    static OSType parseOs(@Nonnull String os) {
        if ((os = os.toLowerCase()).contains("mac") || os.contains("darwin")) {
            return OSType.MacOS;
        }
        if (os.contains("win")) {
            return OSType.Windows;
        }
        if (os.contains("linux")) {
            return OSType.Linux;
        }
        return OSType.Other;
    }

    public static OSType getOs() {
        return DETECTED_OS;
    }

    static {
        String os = System.getProperty("os.name", "generic").toLowerCase();
        DETECTED_OS = OS.parseOs(os);
        log.debug("Detect OS: {}", (Object)DETECTED_OS);
    }

    public static enum OSType {
        Windows,
        MacOS,
        Linux,
        Other;

    }
}

