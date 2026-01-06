/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.runelite.api.NPC
 */
package net.runelite.client.plugins.slayer;

import java.util.List;
import javax.annotation.Nullable;
import net.runelite.api.NPC;

public interface SlayerPluginService {
    public List<NPC> getTargets();

    @Nullable
    public String getTask();

    @Nullable
    public String getTaskLocation();

    public int getInitialAmount();

    public int getRemainingAmount();
}

