/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.inject.Inject
 *  net.runelite.api.Client
 *  net.runelite.api.NPC
 *  net.runelite.api.events.NpcChanged
 *  net.runelite.api.events.NpcDespawned
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package net.runelite.client.plugins.bosstimer;

import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.NPC;
import net.runelite.api.events.NpcChanged;
import net.runelite.api.events.NpcDespawned;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.game.ItemManager;
import net.runelite.client.game.NpcUtil;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.bosstimer.Boss;
import net.runelite.client.plugins.bosstimer.RespawnTimer;
import net.runelite.client.ui.overlay.infobox.InfoBoxManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@PluginDescriptor(name="Boss Timers", description="Show boss spawn timer overlays", tags={"combat", "pve", "overlay", "spawn"})
public class BossTimersPlugin
extends Plugin {
    private static final Logger log = LoggerFactory.getLogger(BossTimersPlugin.class);
    @Inject
    private InfoBoxManager infoBoxManager;
    @Inject
    private ItemManager itemManager;
    @Inject
    private NpcUtil npcUtil;
    @Inject
    private Client client;

    @Override
    protected void shutDown() throws Exception {
        this.infoBoxManager.removeIf(t -> t instanceof RespawnTimer);
    }

    @Subscribe
    public void onNpcDespawned(NpcDespawned npcDespawned) {
        NPC npc = npcDespawned.getNpc();
        Boss boss = Boss.find(npc.getId());
        if (boss != null && (boss.isIgnoreDead() || this.npcUtil.isDying(npc))) {
            this.createTimer(npc, boss);
        }
    }

    @Subscribe
    public void onNpcChanged(NpcChanged npcChanged) {
        NPC npc = npcChanged.getNpc();
        Boss boss = Boss.find(npc.getId());
        if (boss == Boss.HUEYCOATL) {
            this.createTimer(npc, boss);
        }
    }

    private void createTimer(NPC npc, Boss boss) {
        if (this.client.getVarbitValue(30178) > 0) {
            return;
        }
        this.clearTimer(boss);
        log.debug("Creating spawn timer for {} ({})", (Object)npc.getName(), (Object)boss.getSpawnTime());
        boolean halvedTimer = this.client.getVarbitValue(30018) == 1;
        RespawnTimer timer = new RespawnTimer(boss, this.itemManager.getImage(boss.getItemSpriteId()), this, halvedTimer);
        timer.setTooltip(npc.getName());
        this.infoBoxManager.addInfoBox(timer);
    }

    private void clearTimer(Boss boss) {
        this.infoBoxManager.removeIf(t -> t instanceof RespawnTimer && ((RespawnTimer)t).getBoss() == boss);
    }
}

