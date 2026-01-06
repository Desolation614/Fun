/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.inject.Inject
 *  net.runelite.api.ChatMessageType
 *  net.runelite.api.Client
 *  net.runelite.api.coords.LocalPoint
 *  net.runelite.api.coords.WorldPoint
 *  net.runelite.api.events.ChatMessage
 */
package net.runelite.client.plugins.crowdsourcing.music;

import javax.inject.Inject;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.ChatMessage;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.crowdsourcing.CrowdsourcingManager;
import net.runelite.client.plugins.crowdsourcing.music.MusicUnlockData;

public class CrowdsourcingMusic {
    private static final String MUSIC_UNLOCK_MESSAGE = "You have unlocked a new music track:";
    @Inject
    private Client client;
    @Inject
    private ClientThread clientThread;
    @Inject
    private CrowdsourcingManager manager;

    @Subscribe
    public void onChatMessage(ChatMessage event) {
        String message;
        if (event.getType() == ChatMessageType.GAMEMESSAGE && (message = event.getMessage()).contains(MUSIC_UNLOCK_MESSAGE)) {
            this.clientThread.invokeLater(() -> {
                LocalPoint local = LocalPoint.fromWorld((Client)this.client, (WorldPoint)this.client.getLocalPlayer().getWorldLocation());
                WorldPoint location = WorldPoint.fromLocalInstance((Client)this.client, (LocalPoint)local);
                boolean isInInstance = this.client.isInInstancedRegion();
                MusicUnlockData data = new MusicUnlockData(location, isInInstance, message);
                this.manager.storeEvent(data);
            });
        }
    }
}

