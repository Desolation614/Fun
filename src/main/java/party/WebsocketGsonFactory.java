/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.Gson
 *  net.runelite.http.api.RuneLiteAPI
 */
package net.runelite.client.party;

import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import net.runelite.client.party.messages.PartyChatMessage;
import net.runelite.client.party.messages.UserSync;
import net.runelite.client.party.messages.WebsocketMessage;
import net.runelite.client.util.RuntimeTypeAdapterFactory;
import net.runelite.http.api.RuneLiteAPI;

class WebsocketGsonFactory {
    private static final Collection<Class<? extends WebsocketMessage>> MESSAGES;

    WebsocketGsonFactory() {
    }

    public static RuntimeTypeAdapterFactory<WebsocketMessage> factory(Collection<Class<? extends WebsocketMessage>> messages) {
        RuntimeTypeAdapterFactory<WebsocketMessage> factory = RuntimeTypeAdapterFactory.of(WebsocketMessage.class);
        for (Class<? extends WebsocketMessage> message : MESSAGES) {
            factory.registerSubtype(message);
        }
        for (Class<? extends WebsocketMessage> message : messages) {
            factory.registerSubtype(message);
        }
        return factory;
    }

    public static Gson build(RuntimeTypeAdapterFactory<WebsocketMessage> factory) {
        return RuneLiteAPI.GSON.newBuilder().registerTypeAdapterFactory(factory).create();
    }

    public static Gson build() {
        return WebsocketGsonFactory.build(WebsocketGsonFactory.factory(Collections.emptyList()));
    }

    static {
        ArrayList<Class<? extends WebsocketMessage>> messages = new ArrayList<Class<? extends WebsocketMessage>>();
        messages.add(UserSync.class);
        messages.add(PartyChatMessage.class);
        MESSAGES = messages;
    }
}

