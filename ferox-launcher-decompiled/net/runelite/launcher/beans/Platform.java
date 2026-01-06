/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.launcher.beans;

public class Platform {
    private String name;
    private String arch;

    public String getName() {
        return this.name;
    }

    public String getArch() {
        return this.arch;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setArch(String arch) {
        this.arch = arch;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof Platform)) {
            return false;
        }
        Platform other = (Platform)o;
        if (!other.canEqual(this)) {
            return false;
        }
        String this$name = this.getName();
        String other$name = other.getName();
        if (this$name == null ? other$name != null : !this$name.equals(other$name)) {
            return false;
        }
        String this$arch = this.getArch();
        String other$arch = other.getArch();
        return !(this$arch == null ? other$arch != null : !this$arch.equals(other$arch));
    }

    protected boolean canEqual(Object other) {
        return other instanceof Platform;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        String $name = this.getName();
        result = result * 59 + ($name == null ? 43 : $name.hashCode());
        String $arch = this.getArch();
        result = result * 59 + ($arch == null ? 43 : $arch.hashCode());
        return result;
    }

    public String toString() {
        return "Platform(name=" + this.getName() + ", arch=" + this.getArch() + ")";
    }
}

