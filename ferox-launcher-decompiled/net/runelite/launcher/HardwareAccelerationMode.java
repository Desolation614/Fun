/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.launcher;

import java.util.LinkedHashMap;
import java.util.Map;
import net.runelite.launcher.OS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public enum HardwareAccelerationMode {
    AUTO,
    OFF,
    DIRECTDRAW,
    OPENGL,
    METAL;

    private static final Logger log;

    public Map<String, String> toParams(OS.OSType os) {
        LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
        switch (this) {
            case DIRECTDRAW: {
                if (os != OS.OSType.Windows) {
                    throw new IllegalArgumentException("Directdraw is only available on Windows");
                }
                params.put("sun.java2d.d3d", "true");
                params.put("sun.java2d.opengl", "false");
                break;
            }
            case OPENGL: {
                if (os == OS.OSType.Windows) {
                    params.put("sun.java2d.d3d", "false");
                } else if (os == OS.OSType.MacOS) {
                    params.put("sun.java2d.metal", "false");
                }
                params.put("sun.java2d.opengl", "true");
                break;
            }
            case OFF: {
                if (os == OS.OSType.Windows) {
                    params.put("sun.java2d.d3d", "false");
                } else if (os == OS.OSType.MacOS) {
                    throw new IllegalArgumentException("Hardware acceleration mode on MacOS must be one of OPENGL or METAL");
                }
                params.put("sun.java2d.opengl", "false");
                break;
            }
            case METAL: {
                if (os != OS.OSType.MacOS) {
                    throw new IllegalArgumentException("Metal is only available on MacOS");
                }
                params.put("sun.java2d.metal", "true");
            }
        }
        return params;
    }

    public static HardwareAccelerationMode defaultMode(OS.OSType osType) {
        switch (osType) {
            case Windows: {
                return DIRECTDRAW;
            }
            case MacOS: {
                return OPENGL;
            }
        }
        return OFF;
    }

    static {
        log = LoggerFactory.getLogger(HardwareAccelerationMode.class);
    }
}

