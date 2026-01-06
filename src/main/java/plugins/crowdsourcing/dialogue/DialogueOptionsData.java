/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.plugins.crowdsourcing.dialogue;

import java.util.Arrays;

public class DialogueOptionsData {
    private final String[] options;

    public String[] getOptions() {
        return this.options;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof DialogueOptionsData)) {
            return false;
        }
        DialogueOptionsData other = (DialogueOptionsData)o;
        if (!other.canEqual(this)) {
            return false;
        }
        return Arrays.deepEquals(this.getOptions(), other.getOptions());
    }

    protected boolean canEqual(Object other) {
        return other instanceof DialogueOptionsData;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        result = result * 59 + Arrays.deepHashCode(this.getOptions());
        return result;
    }

    public String toString() {
        return "DialogueOptionsData(options=" + Arrays.deepToString(this.getOptions()) + ")";
    }

    public DialogueOptionsData(String[] options) {
        this.options = options;
    }
}

