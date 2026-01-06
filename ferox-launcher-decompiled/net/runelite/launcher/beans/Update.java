/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.launcher.beans;

public class Update {
    private String os;
    private String osName;
    private String osVersion;
    private String arch;
    private String name;
    private String version;
    private String minimumVersion;
    private String url;
    private String hash;
    private int size;
    private double rollout;

    public String getOs() {
        return this.os;
    }

    public String getOsName() {
        return this.osName;
    }

    public String getOsVersion() {
        return this.osVersion;
    }

    public String getArch() {
        return this.arch;
    }

    public String getName() {
        return this.name;
    }

    public String getVersion() {
        return this.version;
    }

    public String getMinimumVersion() {
        return this.minimumVersion;
    }

    public String getUrl() {
        return this.url;
    }

    public String getHash() {
        return this.hash;
    }

    public int getSize() {
        return this.size;
    }

    public double getRollout() {
        return this.rollout;
    }

    public void setOs(String os) {
        this.os = os;
    }

    public void setOsName(String osName) {
        this.osName = osName;
    }

    public void setOsVersion(String osVersion) {
        this.osVersion = osVersion;
    }

    public void setArch(String arch) {
        this.arch = arch;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public void setMinimumVersion(String minimumVersion) {
        this.minimumVersion = minimumVersion;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public void setRollout(double rollout) {
        this.rollout = rollout;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof Update)) {
            return false;
        }
        Update other = (Update)o;
        if (!other.canEqual(this)) {
            return false;
        }
        if (this.getSize() != other.getSize()) {
            return false;
        }
        if (Double.compare(this.getRollout(), other.getRollout()) != 0) {
            return false;
        }
        String this$os = this.getOs();
        String other$os = other.getOs();
        if (this$os == null ? other$os != null : !this$os.equals(other$os)) {
            return false;
        }
        String this$osName = this.getOsName();
        String other$osName = other.getOsName();
        if (this$osName == null ? other$osName != null : !this$osName.equals(other$osName)) {
            return false;
        }
        String this$osVersion = this.getOsVersion();
        String other$osVersion = other.getOsVersion();
        if (this$osVersion == null ? other$osVersion != null : !this$osVersion.equals(other$osVersion)) {
            return false;
        }
        String this$arch = this.getArch();
        String other$arch = other.getArch();
        if (this$arch == null ? other$arch != null : !this$arch.equals(other$arch)) {
            return false;
        }
        String this$name = this.getName();
        String other$name = other.getName();
        if (this$name == null ? other$name != null : !this$name.equals(other$name)) {
            return false;
        }
        String this$version = this.getVersion();
        String other$version = other.getVersion();
        if (this$version == null ? other$version != null : !this$version.equals(other$version)) {
            return false;
        }
        String this$minimumVersion = this.getMinimumVersion();
        String other$minimumVersion = other.getMinimumVersion();
        if (this$minimumVersion == null ? other$minimumVersion != null : !this$minimumVersion.equals(other$minimumVersion)) {
            return false;
        }
        String this$url = this.getUrl();
        String other$url = other.getUrl();
        if (this$url == null ? other$url != null : !this$url.equals(other$url)) {
            return false;
        }
        String this$hash = this.getHash();
        String other$hash = other.getHash();
        return !(this$hash == null ? other$hash != null : !this$hash.equals(other$hash));
    }

    protected boolean canEqual(Object other) {
        return other instanceof Update;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        result = result * 59 + this.getSize();
        long $rollout = Double.doubleToLongBits(this.getRollout());
        result = result * 59 + (int)($rollout >>> 32 ^ $rollout);
        String $os = this.getOs();
        result = result * 59 + ($os == null ? 43 : $os.hashCode());
        String $osName = this.getOsName();
        result = result * 59 + ($osName == null ? 43 : $osName.hashCode());
        String $osVersion = this.getOsVersion();
        result = result * 59 + ($osVersion == null ? 43 : $osVersion.hashCode());
        String $arch = this.getArch();
        result = result * 59 + ($arch == null ? 43 : $arch.hashCode());
        String $name = this.getName();
        result = result * 59 + ($name == null ? 43 : $name.hashCode());
        String $version = this.getVersion();
        result = result * 59 + ($version == null ? 43 : $version.hashCode());
        String $minimumVersion = this.getMinimumVersion();
        result = result * 59 + ($minimumVersion == null ? 43 : $minimumVersion.hashCode());
        String $url = this.getUrl();
        result = result * 59 + ($url == null ? 43 : $url.hashCode());
        String $hash = this.getHash();
        result = result * 59 + ($hash == null ? 43 : $hash.hashCode());
        return result;
    }

    public String toString() {
        return "Update(os=" + this.getOs() + ", osName=" + this.getOsName() + ", osVersion=" + this.getOsVersion() + ", arch=" + this.getArch() + ", name=" + this.getName() + ", version=" + this.getVersion() + ", minimumVersion=" + this.getMinimumVersion() + ", url=" + this.getUrl() + ", hash=" + this.getHash() + ", size=" + this.getSize() + ", rollout=" + this.getRollout() + ")";
    }
}

