/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.inject.Provides
 *  javax.inject.Inject
 *  net.runelite.api.ChatMessageType
 *  net.runelite.api.DecorativeObject
 *  net.runelite.api.GameState
 *  net.runelite.api.Item
 *  net.runelite.api.NPC
 *  net.runelite.api.events.ChatMessage
 *  net.runelite.api.events.DecorativeObjectDespawned
 *  net.runelite.api.events.DecorativeObjectSpawned
 *  net.runelite.api.events.GameStateChanged
 *  net.runelite.api.events.ItemContainerChanged
 */
package net.runelite.client.plugins.runecraft;

import com.google.common.collect.ImmutableList;
import com.google.inject.Provides;
import java.awt.Color;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Stream;
import javax.inject.Inject;
import net.runelite.api.ChatMessageType;
import net.runelite.api.DecorativeObject;
import net.runelite.api.GameState;
import net.runelite.api.Item;
import net.runelite.api.NPC;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.DecorativeObjectDespawned;
import net.runelite.api.events.DecorativeObjectSpawned;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.ItemContainerChanged;
import net.runelite.client.Notifier;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.game.npcoverlay.HighlightedNpc;
import net.runelite.client.game.npcoverlay.NpcOverlayService;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.runecraft.AbyssMinimapOverlay;
import net.runelite.client.plugins.runecraft.AbyssOverlay;
import net.runelite.client.plugins.runecraft.AbyssRifts;
import net.runelite.client.plugins.runecraft.EssencePouchOverlay;
import net.runelite.client.plugins.runecraft.RunecraftConfig;
import net.runelite.client.ui.overlay.OverlayManager;

@PluginDescriptor(name="Runecraft", description="Show minimap icons and clickboxes for abyssal rifts", tags={"abyssal", "minimap", "overlay", "rifts", "rc", "runecrafting", "essence", "pouch"})
public class RunecraftPlugin
extends Plugin {
    private static final String POUCH_DECAYED_NOTIFICATION_MESSAGE = "Your rune pouch has decayed.";
    private static final String POUCH_DECAYED_MESSAGE = "Your pouch has decayed through use.";
    private static final List<Integer> DEGRADED_POUCHES = ImmutableList.of((Object)5511, (Object)5513, (Object)5515, (Object)26786);
    static final int ABYSS_REGION = 12107;
    private final Set<DecorativeObject> abyssObjects = new HashSet<DecorativeObject>();
    private boolean degradedPouchInInventory;
    @Inject
    private OverlayManager overlayManager;
    @Inject
    private AbyssOverlay abyssOverlay;
    @Inject
    private AbyssMinimapOverlay abyssMinimapOverlay;
    @Inject
    private EssencePouchOverlay essencePouchOverlay;
    @Inject
    private RunecraftConfig config;
    @Inject
    private Notifier notifier;
    @Inject
    private NpcOverlayService npcOverlayService;
    private final Function<NPC, HighlightedNpc> highlightDarkMage = this::highlightDarkMage;

    @Provides
    RunecraftConfig getConfig(ConfigManager configManager) {
        return configManager.getConfig(RunecraftConfig.class);
    }

    @Override
    protected void startUp() throws Exception {
        this.npcOverlayService.registerHighlighter(this.highlightDarkMage);
        this.overlayManager.add(this.abyssOverlay);
        this.overlayManager.add(this.abyssMinimapOverlay);
        this.overlayManager.add(this.essencePouchOverlay);
    }

    @Override
    protected void shutDown() throws Exception {
        this.npcOverlayService.unregisterHighlighter(this.highlightDarkMage);
        this.overlayManager.remove(this.abyssOverlay);
        this.overlayManager.remove(this.abyssMinimapOverlay);
        this.overlayManager.remove(this.essencePouchOverlay);
        this.abyssObjects.clear();
        this.degradedPouchInInventory = false;
    }

    @Subscribe
    public void onChatMessage(ChatMessage event) {
        if (event.getType() != ChatMessageType.GAMEMESSAGE) {
            return;
        }
        if (event.getMessage().contains(POUCH_DECAYED_MESSAGE)) {
            this.notifier.notify(this.config.degradingNotification(), POUCH_DECAYED_NOTIFICATION_MESSAGE);
        }
    }

    @Subscribe
    public void onDecorativeObjectSpawned(DecorativeObjectSpawned event) {
        DecorativeObject decorativeObject = event.getDecorativeObject();
        if (AbyssRifts.getRift(decorativeObject.getId()) != null) {
            this.abyssObjects.add(decorativeObject);
        }
    }

    @Subscribe
    public void onDecorativeObjectDespawned(DecorativeObjectDespawned event) {
        DecorativeObject decorativeObject = event.getDecorativeObject();
        this.abyssObjects.remove(decorativeObject);
    }

    @Subscribe
    public void onGameStateChanged(GameStateChanged event) {
        GameState gameState = event.getGameState();
        if (gameState == GameState.LOADING) {
            this.abyssObjects.clear();
        }
    }

    @Subscribe
    public void onItemContainerChanged(ItemContainerChanged event) {
        if (event.getContainerId() != 93) {
            return;
        }
        Item[] items = event.getItemContainer().getItems();
        this.degradedPouchInInventory = Stream.of(items).anyMatch(i -> DEGRADED_POUCHES.contains(i.getId()));
    }

    private HighlightedNpc highlightDarkMage(NPC npc) {
        if (npc.getId() == 2583) {
            return HighlightedNpc.builder().npc(npc).tile(true).highlightColor(Color.GREEN).render(n -> this.config.hightlightDarkMage() && this.degradedPouchInInventory).build();
        }
        return null;
    }

    Set<DecorativeObject> getAbyssObjects() {
        return this.abyssObjects;
    }
}

