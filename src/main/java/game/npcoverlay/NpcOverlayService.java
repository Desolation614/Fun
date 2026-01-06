/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.inject.Inject
 *  javax.inject.Singleton
 *  net.runelite.api.Client
 *  net.runelite.api.GameState
 *  net.runelite.api.NPC
 *  net.runelite.api.events.GameStateChanged
 *  net.runelite.api.events.NpcChanged
 *  net.runelite.api.events.NpcDespawned
 *  net.runelite.api.events.NpcSpawned
 */
package net.runelite.client.game.npcoverlay;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import javax.inject.Inject;
import javax.inject.Singleton;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.NPC;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.NpcChanged;
import net.runelite.api.events.NpcDespawned;
import net.runelite.api.events.NpcSpawned;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.game.npcoverlay.HighlightedNpc;
import net.runelite.client.game.npcoverlay.NpcMinimapOverlay;
import net.runelite.client.game.npcoverlay.NpcOverlay;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.ui.overlay.outline.ModelOutlineRenderer;

@Singleton
public class NpcOverlayService {
    private final Client client;
    private final ClientThread clientThread;
    private final List<Function<NPC, HighlightedNpc>> highlightFunctions = new ArrayList<Function<NPC, HighlightedNpc>>();
    private final Map<NPC, HighlightedNpc> highlightedNpcs = new HashMap<NPC, HighlightedNpc>();

    @Inject
    private NpcOverlayService(Client client, ClientThread clientThread, OverlayManager overlayManager, ModelOutlineRenderer modelOutlineRenderer, EventBus eventBus) {
        this.client = client;
        this.clientThread = clientThread;
        overlayManager.add(new NpcOverlay(client, modelOutlineRenderer, this.highlightedNpcs));
        overlayManager.add(new NpcMinimapOverlay(this.highlightedNpcs));
        eventBus.register(this);
    }

    @Subscribe
    private void onGameStateChanged(GameStateChanged event) {
        if (event.getGameState() == GameState.LOGIN_SCREEN || event.getGameState() == GameState.HOPPING) {
            this.highlightedNpcs.clear();
        }
    }

    @Subscribe(priority=-1.0f)
    private void onNpcSpawned(NpcSpawned npcSpawned) {
        NPC npc = npcSpawned.getNpc();
        for (Function<NPC, HighlightedNpc> f : this.highlightFunctions) {
            HighlightedNpc highlightedNpc = f.apply(npc);
            if (highlightedNpc == null) continue;
            this.highlightedNpcs.put(npc, highlightedNpc);
            return;
        }
    }

    @Subscribe(priority=-1.0f)
    private void onNpcDespawned(NpcDespawned npcDespawned) {
        NPC npc = npcDespawned.getNpc();
        this.highlightedNpcs.remove(npc);
    }

    @Subscribe(priority=-1.0f)
    private void onNpcChanged(NpcChanged event) {
        NPC npc = event.getNpc();
        this.highlightedNpcs.remove(npc);
        for (Function<NPC, HighlightedNpc> f : this.highlightFunctions) {
            HighlightedNpc highlightedNpc = f.apply(npc);
            if (highlightedNpc == null) continue;
            this.highlightedNpcs.put(npc, highlightedNpc);
            return;
        }
    }

    public void rebuild() {
        this.clientThread.invoke(() -> {
            this.highlightedNpcs.clear();
            block0: for (NPC npc : this.client.getNpcs()) {
                for (Function<NPC, HighlightedNpc> f : this.highlightFunctions) {
                    HighlightedNpc highlightedNpc = f.apply(npc);
                    if (highlightedNpc == null) continue;
                    this.highlightedNpcs.put(npc, highlightedNpc);
                    continue block0;
                }
            }
        });
    }

    public void registerHighlighter(Function<NPC, HighlightedNpc> p) {
        this.highlightFunctions.add(p);
        this.rebuild();
    }

    public void unregisterHighlighter(Function<NPC, HighlightedNpc> p) {
        this.highlightFunctions.remove(p);
        this.rebuild();
    }
}

