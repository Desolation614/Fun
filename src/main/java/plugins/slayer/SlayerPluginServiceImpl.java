/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.inject.Inject
 *  javax.inject.Singleton
 *  net.runelite.api.NPC
 */
package net.runelite.client.plugins.slayer;

import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;
import net.runelite.api.NPC;
import net.runelite.client.plugins.slayer.SlayerPlugin;
import net.runelite.client.plugins.slayer.SlayerPluginService;

@Singleton
class SlayerPluginServiceImpl
implements SlayerPluginService {
    private final SlayerPlugin plugin;

    @Inject
    private SlayerPluginServiceImpl(SlayerPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public List<NPC> getTargets() {
        return this.plugin.getTargets();
    }

    @Override
    public String getTask() {
        return this.plugin.getTaskName();
    }

    @Override
    public String getTaskLocation() {
        return this.plugin.getTaskLocation();
    }

    @Override
    public int getInitialAmount() {
        return this.plugin.getInitialAmount();
    }

    @Override
    public int getRemainingAmount() {
        return this.plugin.getAmount();
    }
}

