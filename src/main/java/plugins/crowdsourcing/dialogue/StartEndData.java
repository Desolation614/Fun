/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.plugins.crowdsourcing.dialogue;

public class StartEndData {
    private final boolean isStart;

    public boolean isStart() {
        return this.isStart;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof StartEndData)) {
            return false;
        }
        StartEndData other = (StartEndData)o;
        if (!other.canEqual(this)) {
            return false;
        }
        return this.isStart() == other.isStart();
    }

    protected boolean canEqual(Object other) {
        return other instanceof StartEndData;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        result = result * 59 + (this.isStart() ? 79 : 97);
        return result;
    }

    public String toString() {
        return "StartEndData(isStart=" + this.isStart() + ")";
    }

    public StartEndData(boolean isStart) {
        this.isStart = isStart;
    }
}

