/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.Gson
 *  com.google.gson.JsonParseException
 *  com.google.protobuf.ByteString
 *  com.google.protobuf.InvalidProtocolBufferException
 *  javax.inject.Inject
 *  javax.inject.Named
 *  javax.inject.Singleton
 *  okhttp3.HttpUrl
 *  okhttp3.OkHttpClient
 *  okhttp3.Response
 *  okhttp3.WebSocket
 *  okhttp3.WebSocketListener
 *  okio.ByteString
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package net.runelite.client.party;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.UUID;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.party.Party;
import net.runelite.client.party.WebsocketGsonFactory;
import net.runelite.client.party.events.UserJoin;
import net.runelite.client.party.events.UserPart;
import net.runelite.client.party.messages.PartyMemberMessage;
import net.runelite.client.party.messages.WebsocketMessage;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class WSClient
extends WebSocketListener
implements AutoCloseable {
    private static final Logger log = LoggerFactory.getLogger(WSClient.class);
    private final EventBus eventBus;
    private final OkHttpClient okHttpClient;
    private final HttpUrl runeliteWs;
    private final Collection<Class<? extends WebsocketMessage>> messages = new HashSet<Class<? extends WebsocketMessage>>();
    private volatile Gson gson;
    private UUID sessionId;
    private WebSocket webSocket;

    @Inject
    private WSClient(EventBus eventBus, OkHttpClient okHttpClient, @Named(value="runelite.ws") HttpUrl runeliteWs) {
        this.eventBus = eventBus;
        this.okHttpClient = okHttpClient;
        this.runeliteWs = runeliteWs;
        this.gson = WebsocketGsonFactory.build(WebsocketGsonFactory.factory(this.messages));
    }

    public boolean sessionExists() {
        return this.sessionId != null;
    }

    public void changeSession(UUID sessionId) {
        if (Objects.equals(sessionId, this.sessionId)) {
            return;
        }
        if (this.webSocket != null) {
            this.close();
            this.webSocket = null;
        }
        this.sessionId = sessionId;
        if (sessionId != null) {
            this.connect();
        }
    }

    void connect() {
        if (this.sessionId == null) {
            throw new IllegalStateException("Cannot connect with no session id");
        }
    }

    boolean isOpen() {
        return this.webSocket != null;
    }

    public void registerMessage(Class<? extends WebsocketMessage> message) {
        if (this.messages.add(message)) {
            this.gson = WebsocketGsonFactory.build(WebsocketGsonFactory.factory(this.messages));
        }
    }

    public void unregisterMessage(Class<? extends WebsocketMessage> message) {
        if (this.messages.remove(message)) {
            this.gson = WebsocketGsonFactory.build(WebsocketGsonFactory.factory(this.messages));
        }
    }

    void join(long partyId, long memberId) {
        Party.Join join = (Party.Join)Party.Join.newBuilder().setPartyId(partyId).setMemberId(memberId).build();
        Party.C2S c2s = (Party.C2S)Party.C2S.newBuilder().setJoin(join).build();
        this.send(c2s);
    }

    void part() {
        Party.Part part = (Party.Part)Party.Part.newBuilder().build();
        Party.C2S c2s = (Party.C2S)Party.C2S.newBuilder().setPart(part).build();
        this.send(c2s);
    }

    void send(WebsocketMessage message) {
        log.debug("Sending: {}", (Object)message);
        String json = this.gson.toJson((Object)message, WebsocketMessage.class);
        Party.Data data = (Party.Data)Party.Data.newBuilder().setType(message.getClass().getSimpleName()).setData(ByteString.copyFromUtf8((String)json)).build();
        Party.C2S c2s = (Party.C2S)Party.C2S.newBuilder().setData(data).build();
        this.send(c2s);
    }

    private void send(Party.C2S message) {
        if (this.webSocket == null) {
            log.debug("Reconnecting to server");
            this.connect();
        }
    }

    @Override
    public void close() {
        if (this.webSocket != null) {
            this.webSocket.close(1000, null);
        }
    }

    public void onOpen(WebSocket webSocket, Response response) {
        log.info("Websocket {} opened", (Object)webSocket);
    }

    public void onMessage(WebSocket webSocket, okio.ByteString bytes) {
        Party.S2C s2c;
        try {
            s2c = Party.S2C.parseFrom(bytes.toByteArray());
        }
        catch (InvalidProtocolBufferException e) {
            log.debug("Failed to deserialize message", (Throwable)e);
            return;
        }
        switch (s2c.getMsgCase()) {
            case JOIN: {
                Party.UserJoin join = s2c.getJoin();
                UserJoin userJoin = new UserJoin(join.getPartyId(), join.getMemberId());
                log.debug("Got: {}", (Object)userJoin);
                this.eventBus.post(userJoin);
                break;
            }
            case PART: {
                Party.UserPart part = s2c.getPart();
                UserPart userPart = new UserPart(part.getMemberId());
                log.debug("Got: {}", (Object)userPart);
                this.eventBus.post(userPart);
                break;
            }
            case DATA: {
                WebsocketMessage message;
                Party.PartyData data = s2c.getData();
                try {
                    message = (WebsocketMessage)this.gson.fromJson(data.getData().toStringUtf8(), WebsocketMessage.class);
                }
                catch (JsonParseException e) {
                    log.debug("Failed to deserialize message", (Throwable)e);
                    return;
                }
                if (message instanceof PartyMemberMessage) {
                    ((PartyMemberMessage)message).setMemberId(data.getMemberId());
                }
                log.debug("Got: {}", (Object)message);
                this.eventBus.post(message);
            }
        }
    }

    public void onClosed(WebSocket webSocket, int code, String reason) {
        log.info("Websocket {} closed: {}/{}", new Object[]{webSocket, code, reason});
        this.webSocket = null;
    }

    public void onFailure(WebSocket webSocket, Throwable t, Response response) {
        log.warn("Error in websocket: {}", (Object)response, (Object)t);
        this.webSocket = null;
    }

    public UUID getSessionId() {
        return this.sessionId;
    }
}

