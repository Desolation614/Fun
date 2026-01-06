/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.events;

public class SessionClose {
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof SessionClose)) {
            return false;
        }
        SessionClose other = (SessionClose)o;
        return other.canEqual(this);
    }

    protected boolean canEqual(Object other) {
        return other instanceof SessionClose;
    }

    public int hashCode() {
        boolean result = true;
        return 1;
    }

    public String toString() {
        return "SessionClose()";
    }
}

