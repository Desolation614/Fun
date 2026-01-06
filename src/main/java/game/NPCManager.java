/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  javax.inject.Inject
 *  javax.inject.Singleton
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package net.runelite.client.game;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Singleton;
import net.runelite.client.game.NpcInfo;
import net.runelite.client.game.NpcInfoClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class NPCManager {
    private static final Logger log = LoggerFactory.getLogger(NPCManager.class);
    private final NpcInfoClient npcInfoClient;
    private Map<Integer, NpcInfo> npcMap = Collections.emptyMap();

    @Inject
    private NPCManager(NpcInfoClient npcInfoClient, ScheduledExecutorService scheduledExecutorService) {
        this.npcInfoClient = npcInfoClient;
        scheduledExecutorService.execute(this::loadNpcs);
    }

    @Nullable
    public NpcInfo getNpcInfo(int npcId) {
        return this.npcMap.get(npcId);
    }

    @Nullable
    public Integer getHealth(int npcId) {
        NpcInfo npcInfo = this.npcMap.get(npcId);
        return npcInfo == null ? null : Integer.valueOf(npcInfo.getHitpoints());
    }

    private void loadNpcs() {
        try {
            this.npcMap = this.npcInfoClient.getNpcs();
        }
        catch (IOException e) {
            log.warn("error loading npc stats", (Throwable)e);
        }
    }
}

