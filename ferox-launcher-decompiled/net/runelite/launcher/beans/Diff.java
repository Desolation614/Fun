/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.launcher.beans;

public class Diff {
    private String name;
    private String from;
    private String fromHash;
    private String hash;
    private String path;
    private int size;

    public String getName() {
        return this.name;
    }

    public String getFrom() {
        return this.from;
    }

    public String getFromHash() {
        return this.fromHash;
    }

    public String getHash() {
        return this.hash;
    }

    public String getPath() {
        return this.path;
    }

    public int getSize() {
        return this.size;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public void setFromHash(String fromHash) {
        this.fromHash = fromHash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof Diff)) {
            return false;
        }
        Diff other = (Diff)o;
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
        String this$from = this.getFrom();
        String other$from = other.getFrom();
        if (this$from == null ? other$from != null : !this$from.equals(other$from)) {
            return false;
        }
        String this$fromHash = this.getFromHash();
        String other$fromHash = other.getFromHash();
        if (this$fromHash == null ? other$fromHash != null : !this$fromHash.equals(other$fromHash)) {
            return false;
        }
        String this$hash = this.getHash();
        String other$hash = other.getHash();
        if (this$hash == null ? other$hash != null : !this$hash.equals(other$hash)) {
            return false;
        }
        String this$path = this.getPath();
        String other$path = other.getPath();
        return !(this$path == null ? other$path != null : !this$path.equals(other$path));
    }

    protected boolean canEqual(Object other) {
        return other instanceof Diff;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        result = result * 59 + this.getSize();
        String $name = this.getName();
        result = result * 59 + ($name == null ? 43 : $name.hashCode());
        String $from = this.getFrom();
        result = result * 59 + ($from == null ? 43 : $from.hashCode());
        String $fromHash = this.getFromHash();
        result = result * 59 + ($fromHash == null ? 43 : $fromHash.hashCode());
        String $hash = this.getHash();
        result = result * 59 + ($hash == null ? 43 : $hash.hashCode());
        String $path = this.getPath();
        result = result * 59 + ($path == null ? 43 : $path.hashCode());
        return result;
    }

    public String toString() {
        return "Diff(name=" + this.getName() + ", from=" + this.getFrom() + ", fromHash=" + this.getFromHash() + ", hash=" + this.getHash() + ", path=" + this.getPath() + ", size=" + this.getSize() + ")";
    }
}

