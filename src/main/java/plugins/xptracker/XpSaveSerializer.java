/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.Gson
 *  com.google.inject.Inject
 */
package net.runelite.client.plugins.xptracker;

import com.google.gson.Gson;
import com.google.inject.Inject;
import net.runelite.client.config.Serializer;
import net.runelite.client.plugins.xptracker.XpSave;

class XpSaveSerializer
implements Serializer<XpSave> {
    private final Gson gson;

    @Inject
    private XpSaveSerializer(Gson gson) {
        this.gson = gson;
    }

    @Override
    public String serialize(XpSave value) {
        return this.gson.toJson((Object)value);
    }

    @Override
    public XpSave deserialize(String s) {
        return (XpSave)this.gson.fromJson(s, XpSave.class);
    }
}

