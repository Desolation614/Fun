/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.annotations.SerializedName
 */
package net.runelite.client.plugins.xptracker;

import com.google.gson.annotations.SerializedName;

class XpSaveSingle {
    @SerializedName(value="s")
    long startXp;
    @SerializedName(value="br")
    int xpGainedBeforeReset;
    @SerializedName(value="ar")
    int xpGainedSinceReset;
    @SerializedName(value="t")
    long time;

    XpSaveSingle() {
    }
}

