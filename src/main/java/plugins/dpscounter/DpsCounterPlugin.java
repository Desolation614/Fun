/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableSet
 *  com.google.inject.Provides
 *  javax.inject.Inject
 *  net.runelite.api.Actor
 *  net.runelite.api.Client
 *  net.runelite.api.Hitsplat
 *  net.runelite.api.NPC
 *  net.runelite.api.Player
 *  net.runelite.api.events.HitsplatApplied
 *  net.runelite.api.events.NpcDespawned
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package net.runelite.client.plugins.dpscounter;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Provides;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.inject.Inject;
import net.runelite.api.Actor;
import net.runelite.api.Client;
import net.runelite.api.Hitsplat;
import net.runelite.api.NPC;
import net.runelite.api.Player;
import net.runelite.api.events.HitsplatApplied;
import net.runelite.api.events.NpcDespawned;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.PartyChanged;
import net.runelite.client.party.PartyMember;
import net.runelite.client.party.PartyService;
import net.runelite.client.party.WSClient;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.dpscounter.DpsConfig;
import net.runelite.client.plugins.dpscounter.DpsMember;
import net.runelite.client.plugins.dpscounter.DpsOverlay;
import net.runelite.client.plugins.dpscounter.DpsUpdate;
import net.runelite.client.ui.overlay.OverlayManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@PluginDescriptor(name="DPS Counter", description="Counts damage (per second) by a party", enabledByDefault=false)
public class DpsCounterPlugin
extends Plugin {
    private static final Logger log = LoggerFactory.getLogger(DpsCounterPlugin.class);
    private static final ImmutableSet<Integer> BOSSES = ImmutableSet.of((Object)5886, (Object)5887, (Object)5888, (Object)5889, (Object)5890, (Object)5891, (Object[])new Integer[]{5908, 8615, 8616, 8617, 8618, 8619, 8620, 8621, 8622, 1672, 1673, 1674, 1675, 1676, 1677, 13668, 13669, 8195, 6503, 6609, 5862, 5863, 5866, 2054, 6505, 6619, 2205, 6493, 319, 6618, 9021, 9022, 9023, 9024, 9035, 9036, 9037, 9038, 2265, 2266, 2267, 6496, 6497, 6498, 7849, 7850, 7851, 7852, 7853, 7854, 7855, 2215, 6494, 5779, 6499, 8583, 128, 963, 965, 4303, 4304, 6500, 6501, 239, 2642, 6502, 494, 6640, 6656, 3162, 6492, 3129, 6495, 7979, 8633, 11278, 11279, 11280, 11281, 11282, 378, 9426, 9427, 9428, 9429, 9430, 9431, 9432, 9433, 7416, 8713, 6615, 7221, 7222, 7286, 499, 7706, 3127, 6506, 6504, 6610, 6611, 6612, 8026, 8058, 8059, 8060, 8061, 9049, 9050, 2042, 2043, 2044, 14176, 8360, 8361, 8362, 8363, 8364, 8365, 8359, 8354, 8355, 8356, 8357, 8387, 8388, 8340, 8341, 8370, 8372, 8374, 7540, 7541, 7542, 7543, 7544, 7545, 7530, 7531, 7532, 7533, 7525, 7526, 7527, 7528, 7529, 7551, 7552, 7553, 7554, 7555, 7559, 7560, 7561, 7562, 7563, 7566, 7567, 7569, 7570, 7571, 7572, 7573, 7574, 7584, 7585, 7604, 7605, 7606});
    @Inject
    private Client client;
    @Inject
    private OverlayManager overlayManager;
    @Inject
    private PartyService partyService;
    @Inject
    private WSClient wsClient;
    @Inject
    private DpsOverlay dpsOverlay;
    @Inject
    private DpsConfig dpsConfig;
    private final Map<String, DpsMember> members = new ConcurrentHashMap<String, DpsMember>();
    private final DpsMember total = new DpsMember("Total");

    @Provides
    DpsConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(DpsConfig.class);
    }

    @Override
    protected void startUp() {
        this.total.reset();
        this.overlayManager.add(this.dpsOverlay);
        this.wsClient.registerMessage(DpsUpdate.class);
    }

    @Override
    protected void shutDown() {
        this.wsClient.unregisterMessage(DpsUpdate.class);
        this.overlayManager.remove(this.dpsOverlay);
        this.members.clear();
    }

    @Subscribe
    public void onPartyChanged(PartyChanged partyChanged) {
        this.members.clear();
    }

    @Subscribe
    public void onHitsplatApplied(HitsplatApplied hitsplatApplied) {
        Player player = this.client.getLocalPlayer();
        Actor actor = hitsplatApplied.getActor();
        if (!(actor instanceof NPC)) {
            return;
        }
        Hitsplat hitsplat = hitsplatApplied.getHitsplat();
        int npcId = ((NPC)actor).getId();
        boolean isBoss = BOSSES.contains((Object)npcId);
        if (hitsplat.isMine()) {
            int hit = hitsplat.getAmount();
            PartyMember localMember = this.partyService.getLocalMember();
            if (localMember != null) {
                DpsUpdate dpsUpdate = new DpsUpdate(hit, isBoss);
                this.partyService.send(dpsUpdate);
            }
            if (this.dpsConfig.bossDamage() && !isBoss) {
                return;
            }
            String name = localMember == null ? player.getName() : localMember.getDisplayName();
            DpsMember dpsMember = this.members.computeIfAbsent(name, DpsMember::new);
            dpsMember.addDamage(hit);
        } else if (hitsplat.isOthers()) {
            if ((this.dpsConfig.bossDamage() || actor != player.getInteracting()) && !isBoss) {
                return;
            }
        } else {
            return;
        }
        this.unpause();
        this.total.addDamage(hitsplat.getAmount());
    }

    @Subscribe
    public void onDpsUpdate(DpsUpdate dpsUpdate) {
        if (this.partyService.getLocalMember().getMemberId() == dpsUpdate.getMemberId()) {
            return;
        }
        String name = this.partyService.getMemberById(dpsUpdate.getMemberId()).getDisplayName();
        if (name == null) {
            return;
        }
        if (!dpsUpdate.isBoss() && this.dpsConfig.bossDamage()) {
            return;
        }
        this.unpause();
        DpsMember dpsMember = this.members.computeIfAbsent(name, DpsMember::new);
        dpsMember.addDamage(dpsUpdate.getHit());
    }

    @Subscribe
    public void onNpcDespawned(NpcDespawned npcDespawned) {
        NPC npc = npcDespawned.getNpc();
        if (npc.isDead() && BOSSES.contains((Object)npc.getId())) {
            log.debug("Boss has died!");
            if (this.dpsConfig.autoreset()) {
                this.members.values().forEach(DpsMember::reset);
                this.total.reset();
            } else if (this.dpsConfig.autopause()) {
                this.pause();
            }
        }
    }

    void reset() {
        this.members.clear();
        this.total.reset();
    }

    void pause() {
        if (this.total.isPaused()) {
            return;
        }
        log.debug("Pausing");
        for (DpsMember dpsMember : this.members.values()) {
            dpsMember.pause();
        }
        this.total.pause();
        this.dpsOverlay.setPaused(true);
    }

    void unpause() {
        if (!this.total.isPaused()) {
            return;
        }
        log.debug("Unpausing");
        for (DpsMember dpsMember : this.members.values()) {
            dpsMember.unpause();
        }
        this.total.unpause();
        this.dpsOverlay.setPaused(false);
    }

    Map<String, DpsMember> getMembers() {
        return this.members;
    }

    DpsMember getTotal() {
        return this.total;
    }
}

