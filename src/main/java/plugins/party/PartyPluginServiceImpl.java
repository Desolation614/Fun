/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.inject.Inject
 *  javax.inject.Singleton
 */
package net.runelite.client.plugins.party;

import javax.inject.Inject;
import javax.inject.Singleton;
import net.runelite.client.plugins.party.PartyPlugin;
import net.runelite.client.plugins.party.PartyPluginService;
import net.runelite.client.plugins.party.data.PartyData;

@Singleton
public class PartyPluginServiceImpl
implements PartyPluginService {
    private final PartyPlugin plugin;

    @Inject
    private PartyPluginServiceImpl(PartyPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public PartyData getPartyData(long memberId) {
        return this.plugin.getPartyData(memberId);
    }
}

