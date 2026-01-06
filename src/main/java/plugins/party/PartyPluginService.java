/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package net.runelite.client.plugins.party;

import javax.annotation.Nullable;
import net.runelite.client.plugins.party.data.PartyData;

public interface PartyPluginService {
    @Nullable
    public PartyData getPartyData(long var1);
}

