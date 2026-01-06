/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.account;

import java.time.Instant;
import java.util.UUID;

public class AccountSession {
    private final UUID uuid;
    private final Instant created;
    private final String username;

    public AccountSession(UUID uuid, Instant created, String username) {
        this.uuid = uuid;
        this.created = created;
        this.username = username;
    }

    public UUID getUuid() {
        return this.uuid;
    }

    public Instant getCreated() {
        return this.created;
    }

    public String getUsername() {
        return this.username;
    }

    public String toString() {
        return "AccountSession(uuid=" + String.valueOf(this.getUuid()) + ", created=" + String.valueOf(this.getCreated()) + ", username=" + this.getUsername() + ")";
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof AccountSession)) {
            return false;
        }
        AccountSession other = (AccountSession)o;
        if (!other.canEqual(this)) {
            return false;
        }
        UUID this$uuid = this.getUuid();
        UUID other$uuid = other.getUuid();
        return !(this$uuid == null ? other$uuid != null : !((Object)this$uuid).equals(other$uuid));
    }

    protected boolean canEqual(Object other) {
        return other instanceof AccountSession;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        UUID $uuid = this.getUuid();
        result = result * 59 + ($uuid == null ? 43 : ((Object)$uuid).hashCode());
        return result;
    }
}

