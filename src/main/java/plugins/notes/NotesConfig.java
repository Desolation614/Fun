/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.plugins.notes;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup(value="notes")
public interface NotesConfig
extends Config {
    @ConfigItem(keyName="notesData", name="", description="", hidden=true)
    default public String notesData() {
        return "";
    }

    @ConfigItem(keyName="notesData", name="", description="")
    public void notesData(String var1);
}

