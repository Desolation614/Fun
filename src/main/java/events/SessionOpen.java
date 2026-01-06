/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.events;

public class SessionOpen {
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof SessionOpen)) {
            return false;
        }
        SessionOpen other = (SessionOpen)o;
        return other.canEqual(this);
    }

    protected boolean canEqual(Object other) {
        return other instanceof SessionOpen;
    }

    public int hashCode() {
        boolean result = true;
        return 1;
    }

    public String toString() {
        return "SessionOpen()";
    }
}

