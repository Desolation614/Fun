/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.events;

import java.awt.image.BufferedImage;
import java.io.File;

public final class ScreenshotTaken {
    private final File path;
    private final BufferedImage screenshot;

    public ScreenshotTaken(File path, BufferedImage screenshot) {
        this.path = path;
        this.screenshot = screenshot;
    }

    public File getPath() {
        return this.path;
    }

    public BufferedImage getScreenshot() {
        return this.screenshot;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof ScreenshotTaken)) {
            return false;
        }
        ScreenshotTaken other = (ScreenshotTaken)o;
        File this$path = this.getPath();
        File other$path = other.getPath();
        if (this$path == null ? other$path != null : !((Object)this$path).equals(other$path)) {
            return false;
        }
        BufferedImage this$screenshot = this.getScreenshot();
        BufferedImage other$screenshot = other.getScreenshot();
        return !(this$screenshot == null ? other$screenshot != null : !this$screenshot.equals(other$screenshot));
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        File $path = this.getPath();
        result = result * 59 + ($path == null ? 43 : ((Object)$path).hashCode());
        BufferedImage $screenshot = this.getScreenshot();
        result = result * 59 + ($screenshot == null ? 43 : $screenshot.hashCode());
        return result;
    }

    public String toString() {
        return "ScreenshotTaken(path=" + String.valueOf(this.getPath()) + ", screenshot=" + String.valueOf(this.getScreenshot()) + ")";
    }
}

