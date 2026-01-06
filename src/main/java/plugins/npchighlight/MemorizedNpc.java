/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.runelite.api.NPC
 *  net.runelite.api.NPCComposition
 *  net.runelite.api.coords.WorldPoint
 */
package net.runelite.client.plugins.npchighlight;

import java.util.ArrayList;
import java.util.List;
import net.runelite.api.NPC;
import net.runelite.api.NPCComposition;
import net.runelite.api.coords.WorldPoint;

class MemorizedNpc {
    private final int npcIndex;
    private final String npcName;
    private int npcSize;
    private int diedOnTick;
    private int respawnTime;
    private List<WorldPoint> possibleRespawnLocations;

    MemorizedNpc(NPC npc) {
        this.npcName = npc.getName();
        this.npcIndex = npc.getIndex();
        this.possibleRespawnLocations = new ArrayList<WorldPoint>(2);
        this.respawnTime = -1;
        this.diedOnTick = -1;
        NPCComposition composition = npc.getTransformedComposition();
        if (composition != null) {
            this.npcSize = composition.getSize();
        }
    }

    public int getNpcIndex() {
        return this.npcIndex;
    }

    public String getNpcName() {
        return this.npcName;
    }

    public int getNpcSize() {
        return this.npcSize;
    }

    public int getDiedOnTick() {
        return this.diedOnTick;
    }

    public void setDiedOnTick(int diedOnTick) {
        this.diedOnTick = diedOnTick;
    }

    public int getRespawnTime() {
        return this.respawnTime;
    }

    public void setRespawnTime(int respawnTime) {
        this.respawnTime = respawnTime;
    }

    public List<WorldPoint> getPossibleRespawnLocations() {
        return this.possibleRespawnLocations;
    }

    public void setPossibleRespawnLocations(List<WorldPoint> possibleRespawnLocations) {
        this.possibleRespawnLocations = possibleRespawnLocations;
    }
}

