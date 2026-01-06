/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.launcher.beans;

import java.util.Arrays;
import net.runelite.launcher.beans.Diff;
import net.runelite.launcher.beans.Platform;

public class Artifact {
    private String name;
    private String path;
    private String hash;
    private int size;
    private Diff[] diffs;
    private Platform[] platform;

    public String getName() {
        return this.name;
    }

    public String getPath() {
        return this.path;
    }

    public String getHash() {
        return this.hash;
    }

    public int getSize() {
        return this.size;
    }

    public Diff[] getDiffs() {
        return this.diffs;
    }

    public Platform[] getPlatform() {
        return this.platform;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public void setDiffs(Diff[] diffs) {
        this.diffs = diffs;
    }

    public void setPlatform(Platform[] platform) {
        this.platform = platform;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof Artifact)) {
            return false;
        }
        Artifact other = (Artifact)o;
        if (!other.canEqual(this)) {
            return false;
        }
        if (this.getSize() != other.getSize()) {
            return false;
        }
        String this$name = this.getName();
        String other$name = other.getName();
        if (this$name == null ? other$name != null : !this$name.equals(other$name)) {
            return false;
        }
        String this$path = this.getPath();
        String other$path = other.getPath();
        if (this$path == null ? other$path != null : !this$path.equals(other$path)) {
            return false;
        }
        String this$hash = this.getHash();
        String other$hash = other.getHash();
        if (this$hash == null ? other$hash != null : !this$hash.equals(other$hash)) {
            return false;
        }
        if (!Arrays.deepEquals(this.getDiffs(), other.getDiffs())) {
            return false;
        }
        return Arrays.deepEquals(this.getPlatform(), other.getPlatform());
    }

    protected boolean canEqual(Object other) {
        return other instanceof Artifact;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        result = result * 59 + this.getSize();
        String $name = this.getName();
        result = result * 59 + ($name == null ? 43 : $name.hashCode());
        String $path = this.getPath();
        result = result * 59 + ($path == null ? 43 : $path.hashCode());
        String $hash = this.getHash();
        result = result * 59 + ($hash == null ? 43 : $hash.hashCode());
        result = result * 59 + Arrays.deepHashCode(this.getDiffs());
        result = result * 59 + Arrays.deepHashCode(this.getPlatform());
        return result;
    }

    public String toString() {
        return "Artifact(name=" + this.getName() + ", path=" + this.getPath() + ", hash=" + this.getHash() + ", size=" + this.getSize() + ", diffs=" + Arrays.deepToString(this.getDiffs()) + ", platform=" + Arrays.deepToString(this.getPlatform()) + ")";
    }
}

