/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.launcher.beans;

import java.util.Arrays;
import java.util.Map;
import net.runelite.launcher.beans.Artifact;
import net.runelite.launcher.beans.Update;

public class Bootstrap {
    private Artifact[] artifacts;
    private String[] clientJvm9Arguments;
    private String[] clientJvm17Arguments;
    private String[] clientJvm17WindowsArguments;
    private String[] clientJvm17MacArguments;
    private String[] launcherJvm11WindowsArguments;
    private String[] launcherJvm11MacArguments;
    private String[] launcherJvm11Arguments;
    private String[] launcherJvm17WindowsArguments;
    private String[] launcherJvm17MacArguments;
    private String[] launcherJvm17Arguments;
    private String requiredLauncherVersion;
    private String requiredJVMVersion;
    private Map<String, String> launcherWindowsEnv;
    private Map<String, String> launcherMacEnv;
    private Map<String, String> launcherLinuxEnv;
    private Update[] updates;

    public Artifact[] getArtifacts() {
        return this.artifacts;
    }

    public String[] getClientJvm9Arguments() {
        return this.clientJvm9Arguments;
    }

    public String[] getClientJvm17Arguments() {
        return this.clientJvm17Arguments;
    }

    public String[] getClientJvm17WindowsArguments() {
        return this.clientJvm17WindowsArguments;
    }

    public String[] getClientJvm17MacArguments() {
        return this.clientJvm17MacArguments;
    }

    public String[] getLauncherJvm11WindowsArguments() {
        return this.launcherJvm11WindowsArguments;
    }

    public String[] getLauncherJvm11MacArguments() {
        return this.launcherJvm11MacArguments;
    }

    public String[] getLauncherJvm11Arguments() {
        return this.launcherJvm11Arguments;
    }

    public String[] getLauncherJvm17WindowsArguments() {
        return this.launcherJvm17WindowsArguments;
    }

    public String[] getLauncherJvm17MacArguments() {
        return this.launcherJvm17MacArguments;
    }

    public String[] getLauncherJvm17Arguments() {
        return this.launcherJvm17Arguments;
    }

    public String getRequiredLauncherVersion() {
        return this.requiredLauncherVersion;
    }

    public String getRequiredJVMVersion() {
        return this.requiredJVMVersion;
    }

    public Map<String, String> getLauncherWindowsEnv() {
        return this.launcherWindowsEnv;
    }

    public Map<String, String> getLauncherMacEnv() {
        return this.launcherMacEnv;
    }

    public Map<String, String> getLauncherLinuxEnv() {
        return this.launcherLinuxEnv;
    }

    public Update[] getUpdates() {
        return this.updates;
    }

    public void setArtifacts(Artifact[] artifacts) {
        this.artifacts = artifacts;
    }

    public void setClientJvm9Arguments(String[] clientJvm9Arguments) {
        this.clientJvm9Arguments = clientJvm9Arguments;
    }

    public void setClientJvm17Arguments(String[] clientJvm17Arguments) {
        this.clientJvm17Arguments = clientJvm17Arguments;
    }

    public void setClientJvm17WindowsArguments(String[] clientJvm17WindowsArguments) {
        this.clientJvm17WindowsArguments = clientJvm17WindowsArguments;
    }

    public void setClientJvm17MacArguments(String[] clientJvm17MacArguments) {
        this.clientJvm17MacArguments = clientJvm17MacArguments;
    }

    public void setLauncherJvm11WindowsArguments(String[] launcherJvm11WindowsArguments) {
        this.launcherJvm11WindowsArguments = launcherJvm11WindowsArguments;
    }

    public void setLauncherJvm11MacArguments(String[] launcherJvm11MacArguments) {
        this.launcherJvm11MacArguments = launcherJvm11MacArguments;
    }

    public void setLauncherJvm11Arguments(String[] launcherJvm11Arguments) {
        this.launcherJvm11Arguments = launcherJvm11Arguments;
    }

    public void setLauncherJvm17WindowsArguments(String[] launcherJvm17WindowsArguments) {
        this.launcherJvm17WindowsArguments = launcherJvm17WindowsArguments;
    }

    public void setLauncherJvm17MacArguments(String[] launcherJvm17MacArguments) {
        this.launcherJvm17MacArguments = launcherJvm17MacArguments;
    }

    public void setLauncherJvm17Arguments(String[] launcherJvm17Arguments) {
        this.launcherJvm17Arguments = launcherJvm17Arguments;
    }

    public void setRequiredLauncherVersion(String requiredLauncherVersion) {
        this.requiredLauncherVersion = requiredLauncherVersion;
    }

    public void setRequiredJVMVersion(String requiredJVMVersion) {
        this.requiredJVMVersion = requiredJVMVersion;
    }

    public void setLauncherWindowsEnv(Map<String, String> launcherWindowsEnv) {
        this.launcherWindowsEnv = launcherWindowsEnv;
    }

    public void setLauncherMacEnv(Map<String, String> launcherMacEnv) {
        this.launcherMacEnv = launcherMacEnv;
    }

    public void setLauncherLinuxEnv(Map<String, String> launcherLinuxEnv) {
        this.launcherLinuxEnv = launcherLinuxEnv;
    }

    public void setUpdates(Update[] updates) {
        this.updates = updates;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof Bootstrap)) {
            return false;
        }
        Bootstrap other = (Bootstrap)o;
        if (!other.canEqual(this)) {
            return false;
        }
        if (!Arrays.deepEquals(this.getArtifacts(), other.getArtifacts())) {
            return false;
        }
        if (!Arrays.deepEquals(this.getClientJvm9Arguments(), other.getClientJvm9Arguments())) {
            return false;
        }
        if (!Arrays.deepEquals(this.getClientJvm17Arguments(), other.getClientJvm17Arguments())) {
            return false;
        }
        if (!Arrays.deepEquals(this.getClientJvm17WindowsArguments(), other.getClientJvm17WindowsArguments())) {
            return false;
        }
        if (!Arrays.deepEquals(this.getClientJvm17MacArguments(), other.getClientJvm17MacArguments())) {
            return false;
        }
        if (!Arrays.deepEquals(this.getLauncherJvm11WindowsArguments(), other.getLauncherJvm11WindowsArguments())) {
            return false;
        }
        if (!Arrays.deepEquals(this.getLauncherJvm11MacArguments(), other.getLauncherJvm11MacArguments())) {
            return false;
        }
        if (!Arrays.deepEquals(this.getLauncherJvm11Arguments(), other.getLauncherJvm11Arguments())) {
            return false;
        }
        if (!Arrays.deepEquals(this.getLauncherJvm17WindowsArguments(), other.getLauncherJvm17WindowsArguments())) {
            return false;
        }
        if (!Arrays.deepEquals(this.getLauncherJvm17MacArguments(), other.getLauncherJvm17MacArguments())) {
            return false;
        }
        if (!Arrays.deepEquals(this.getLauncherJvm17Arguments(), other.getLauncherJvm17Arguments())) {
            return false;
        }
        String this$requiredLauncherVersion = this.getRequiredLauncherVersion();
        String other$requiredLauncherVersion = other.getRequiredLauncherVersion();
        if (this$requiredLauncherVersion == null ? other$requiredLauncherVersion != null : !this$requiredLauncherVersion.equals(other$requiredLauncherVersion)) {
            return false;
        }
        String this$requiredJVMVersion = this.getRequiredJVMVersion();
        String other$requiredJVMVersion = other.getRequiredJVMVersion();
        if (this$requiredJVMVersion == null ? other$requiredJVMVersion != null : !this$requiredJVMVersion.equals(other$requiredJVMVersion)) {
            return false;
        }
        Map<String, String> this$launcherWindowsEnv = this.getLauncherWindowsEnv();
        Map<String, String> other$launcherWindowsEnv = other.getLauncherWindowsEnv();
        if (this$launcherWindowsEnv == null ? other$launcherWindowsEnv != null : !((Object)this$launcherWindowsEnv).equals(other$launcherWindowsEnv)) {
            return false;
        }
        Map<String, String> this$launcherMacEnv = this.getLauncherMacEnv();
        Map<String, String> other$launcherMacEnv = other.getLauncherMacEnv();
        if (this$launcherMacEnv == null ? other$launcherMacEnv != null : !((Object)this$launcherMacEnv).equals(other$launcherMacEnv)) {
            return false;
        }
        Map<String, String> this$launcherLinuxEnv = this.getLauncherLinuxEnv();
        Map<String, String> other$launcherLinuxEnv = other.getLauncherLinuxEnv();
        if (this$launcherLinuxEnv == null ? other$launcherLinuxEnv != null : !((Object)this$launcherLinuxEnv).equals(other$launcherLinuxEnv)) {
            return false;
        }
        return Arrays.deepEquals(this.getUpdates(), other.getUpdates());
    }

    protected boolean canEqual(Object other) {
        return other instanceof Bootstrap;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        result = result * 59 + Arrays.deepHashCode(this.getArtifacts());
        result = result * 59 + Arrays.deepHashCode(this.getClientJvm9Arguments());
        result = result * 59 + Arrays.deepHashCode(this.getClientJvm17Arguments());
        result = result * 59 + Arrays.deepHashCode(this.getClientJvm17WindowsArguments());
        result = result * 59 + Arrays.deepHashCode(this.getClientJvm17MacArguments());
        result = result * 59 + Arrays.deepHashCode(this.getLauncherJvm11WindowsArguments());
        result = result * 59 + Arrays.deepHashCode(this.getLauncherJvm11MacArguments());
        result = result * 59 + Arrays.deepHashCode(this.getLauncherJvm11Arguments());
        result = result * 59 + Arrays.deepHashCode(this.getLauncherJvm17WindowsArguments());
        result = result * 59 + Arrays.deepHashCode(this.getLauncherJvm17MacArguments());
        result = result * 59 + Arrays.deepHashCode(this.getLauncherJvm17Arguments());
        String $requiredLauncherVersion = this.getRequiredLauncherVersion();
        result = result * 59 + ($requiredLauncherVersion == null ? 43 : $requiredLauncherVersion.hashCode());
        String $requiredJVMVersion = this.getRequiredJVMVersion();
        result = result * 59 + ($requiredJVMVersion == null ? 43 : $requiredJVMVersion.hashCode());
        Map<String, String> $launcherWindowsEnv = this.getLauncherWindowsEnv();
        result = result * 59 + ($launcherWindowsEnv == null ? 43 : ((Object)$launcherWindowsEnv).hashCode());
        Map<String, String> $launcherMacEnv = this.getLauncherMacEnv();
        result = result * 59 + ($launcherMacEnv == null ? 43 : ((Object)$launcherMacEnv).hashCode());
        Map<String, String> $launcherLinuxEnv = this.getLauncherLinuxEnv();
        result = result * 59 + ($launcherLinuxEnv == null ? 43 : ((Object)$launcherLinuxEnv).hashCode());
        result = result * 59 + Arrays.deepHashCode(this.getUpdates());
        return result;
    }

    public String toString() {
        return "Bootstrap(artifacts=" + Arrays.deepToString(this.getArtifacts()) + ", clientJvm9Arguments=" + Arrays.deepToString(this.getClientJvm9Arguments()) + ", clientJvm17Arguments=" + Arrays.deepToString(this.getClientJvm17Arguments()) + ", clientJvm17WindowsArguments=" + Arrays.deepToString(this.getClientJvm17WindowsArguments()) + ", clientJvm17MacArguments=" + Arrays.deepToString(this.getClientJvm17MacArguments()) + ", launcherJvm11WindowsArguments=" + Arrays.deepToString(this.getLauncherJvm11WindowsArguments()) + ", launcherJvm11MacArguments=" + Arrays.deepToString(this.getLauncherJvm11MacArguments()) + ", launcherJvm11Arguments=" + Arrays.deepToString(this.getLauncherJvm11Arguments()) + ", launcherJvm17WindowsArguments=" + Arrays.deepToString(this.getLauncherJvm17WindowsArguments()) + ", launcherJvm17MacArguments=" + Arrays.deepToString(this.getLauncherJvm17MacArguments()) + ", launcherJvm17Arguments=" + Arrays.deepToString(this.getLauncherJvm17Arguments()) + ", requiredLauncherVersion=" + this.getRequiredLauncherVersion() + ", requiredJVMVersion=" + this.getRequiredJVMVersion() + ", launcherWindowsEnv=" + String.valueOf(this.getLauncherWindowsEnv()) + ", launcherMacEnv=" + String.valueOf(this.getLauncherMacEnv()) + ", launcherLinuxEnv=" + String.valueOf(this.getLauncherLinuxEnv()) + ", updates=" + Arrays.deepToString(this.getUpdates()) + ")";
    }
}

