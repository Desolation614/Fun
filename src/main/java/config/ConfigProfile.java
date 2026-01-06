/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package net.runelite.client.config;

import java.util.List;
import javax.annotation.Nullable;

public class ConfigProfile {
    private final long id;
    private String name;
    private boolean sync;
    private boolean active;
    private long rev;
    @Nullable
    private List<String> defaultForRsProfiles;

    public boolean isInternal() {
        return this.name.startsWith("$");
    }

    ConfigProfile(long id) {
        this.id = id;
    }

    public String toString() {
        return "ConfigProfile(id=" + this.getId() + ", name=" + this.getName() + ", sync=" + this.isSync() + ", active=" + this.isActive() + ", rev=" + this.getRev() + ", defaultForRsProfiles=" + String.valueOf(this.getDefaultForRsProfiles()) + ")";
    }

    public long getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    void setName(String name) {
        this.name = name;
    }

    public boolean isSync() {
        return this.sync;
    }

    public void setSync(boolean sync) {
        this.sync = sync;
    }

    public boolean isActive() {
        return this.active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    long getRev() {
        return this.rev;
    }

    void setRev(long rev) {
        this.rev = rev;
    }

    @Nullable
    public List<String> getDefaultForRsProfiles() {
        return this.defaultForRsProfiles;
    }

    public void setDefaultForRsProfiles(@Nullable List<String> defaultForRsProfiles) {
        this.defaultForRsProfiles = defaultForRsProfiles;
    }
}

