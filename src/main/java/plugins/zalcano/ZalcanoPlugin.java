/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.inject.Inject
 *  net.runelite.api.Actor
 *  net.runelite.api.Client
 *  net.runelite.api.GameObject
 *  net.runelite.api.GameState
 *  net.runelite.api.GraphicsObject
 *  net.runelite.api.Hitsplat
 *  net.runelite.api.NPC
 *  net.runelite.api.Projectile
 *  net.runelite.api.coords.LocalPoint
 *  net.runelite.api.coords.WorldPoint
 *  net.runelite.api.events.GameObjectSpawned
 *  net.runelite.api.events.GameStateChanged
 *  net.runelite.api.events.GraphicsObjectCreated
 *  net.runelite.api.events.HitsplatApplied
 *  net.runelite.api.events.NpcChanged
 *  net.runelite.api.events.NpcDespawned
 *  net.runelite.api.events.NpcSpawned
 *  net.runelite.api.events.ProjectileMoved
 *  net.runelite.api.events.VarbitChanged
 */
package net.runelite.client.plugins.zalcano;

import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import net.runelite.api.Actor;
import net.runelite.api.Client;
import net.runelite.api.GameObject;
import net.runelite.api.GameState;
import net.runelite.api.GraphicsObject;
import net.runelite.api.Hitsplat;
import net.runelite.api.NPC;
import net.runelite.api.Projectile;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.GameObjectSpawned;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GraphicsObjectCreated;
import net.runelite.api.events.HitsplatApplied;
import net.runelite.api.events.NpcChanged;
import net.runelite.api.events.NpcDespawned;
import net.runelite.api.events.NpcSpawned;
import net.runelite.api.events.ProjectileMoved;
import net.runelite.api.events.VarbitChanged;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.zalcano.ZalcanoOverlay;
import net.runelite.client.plugins.zalcano.ZalcanoPanel;
import net.runelite.client.ui.overlay.OverlayManager;

@PluginDescriptor(name="Zalcano", description="Assistance for the Zalcano fight", enabledByDefault=false)
public class ZalcanoPlugin
extends Plugin {
    private static final int ZALCANO_WEAKENED = 9050;
    private static final int GOLEM = 9051;
    @Inject
    private Client client;
    @Inject
    private OverlayManager overlayManager;
    @Inject
    private ZalcanoOverlay overlay;
    @Inject
    private ZalcanoPanel panel;
    @Inject
    private ClientThread clientThread;
    private LocalPoint targetedGlowingRock;
    private int targetedGlowingRockEndCycle;
    private WorldPoint lastGlowingRock;
    private final List<GraphicsObject> rocks = new ArrayList<GraphicsObject>();
    private int healthDamage;
    private int shieldDamage;
    private boolean inCavern;

    @Override
    protected void startUp() {
        this.overlayManager.add(this.overlay);
        this.overlayManager.add(this.panel);
        this.rocks.clear();
        this.resetDamageCounter();
        this.clientThread.invokeLater(() -> {
            if (this.client.getGameState() == GameState.LOGGED_IN) {
                this.inCavern = this.isHealthbarActive();
            }
        });
    }

    @Override
    protected void shutDown() {
        this.overlayManager.remove(this.overlay);
        this.overlayManager.remove(this.panel);
    }

    @Subscribe
    public void onGraphicsObjectCreated(GraphicsObjectCreated graphicsObjectCreated) {
        GraphicsObject graphicsObject = graphicsObjectCreated.getGraphicsObject();
        if (graphicsObject.getId() == 1727) {
            this.rocks.add(graphicsObject);
        }
    }

    @Subscribe
    public void onGameStateChanged(GameStateChanged event) {
        GameState gameState = event.getGameState();
        if (gameState == GameState.LOADING) {
            this.rocks.clear();
        }
    }

    @Subscribe
    public void onVarbitChanged(VarbitChanged event) {
        if (event.getVarpId() == 1683) {
            boolean wasInCavern = this.inCavern;
            this.inCavern = this.isHealthbarActive();
            if (!this.inCavern && wasInCavern) {
                this.resetDamageCounter();
            }
        }
    }

    @Subscribe
    public void onNpcSpawned(NpcSpawned event) {
        NPC npc = event.getNpc();
        if (npc.getId() == 9051) {
            this.client.setHintArrow(npc);
        }
    }

    @Subscribe
    public void onNpcDespawned(NpcDespawned event) {
        NPC npc = event.getNpc();
        if (npc.getId() == 9050) {
            this.client.clearHintArrow();
        } else if (npc.getId() == 9051 && this.lastGlowingRock != null) {
            this.client.setHintArrow(this.lastGlowingRock);
        }
    }

    private void resetDamageCounter() {
        this.healthDamage = 0;
        this.shieldDamage = 0;
    }

    @Subscribe
    public void onGameObjectSpawned(GameObjectSpawned event) {
        GameObject gameObject = event.getGameObject();
        if (gameObject.getId() == 36192) {
            WorldPoint worldLocation = this.lastGlowingRock = gameObject.getWorldLocation();
            this.client.setHintArrow(worldLocation);
        }
    }

    @Subscribe
    public void onNpcChanged(NpcChanged event) {
        NPC npc = event.getNpc();
        if (npc.getId() == 9050) {
            this.client.setHintArrow(npc);
        } else if (npc.getId() == 9049 && this.lastGlowingRock != null) {
            this.client.setHintArrow(this.lastGlowingRock);
        }
    }

    @Subscribe
    public void onProjectileMoved(ProjectileMoved event) {
        Projectile projectile = event.getProjectile();
        if (projectile.getId() == 1728) {
            this.targetedGlowingRock = event.getPosition();
            this.targetedGlowingRockEndCycle = projectile.getEndCycle();
        }
    }

    @Subscribe
    public void onHitsplatApplied(HitsplatApplied event) {
        Actor actor = event.getActor();
        if (!(actor instanceof NPC)) {
            return;
        }
        int npcId = ((NPC)actor).getId();
        if (npcId != 9050 && npcId != 9049) {
            return;
        }
        Hitsplat hitsplat = event.getHitsplat();
        int damage = hitsplat.getAmount();
        switch (hitsplat.getHitsplatType()) {
            case 16: 
            case 43: {
                this.healthDamage += damage;
                break;
            }
            case 20: 
            case 45: {
                this.shieldDamage += damage;
            }
        }
    }

    private boolean isHealthbarActive() {
        int npcId = this.client.getVarpValue(1683);
        return npcId == 9050 || npcId == 9049;
    }

    public LocalPoint getTargetedGlowingRock() {
        return this.targetedGlowingRock;
    }

    public int getTargetedGlowingRockEndCycle() {
        return this.targetedGlowingRockEndCycle;
    }

    public List<GraphicsObject> getRocks() {
        return this.rocks;
    }

    public int getHealthDamage() {
        return this.healthDamage;
    }

    public int getShieldDamage() {
        return this.shieldDamage;
    }

    public boolean isInCavern() {
        return this.inCavern;
    }
}

