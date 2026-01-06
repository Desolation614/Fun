/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.CharMatcher
 *  com.google.common.hash.Hashing
 *  javax.annotation.Nullable
 *  javax.inject.Inject
 *  javax.inject.Singleton
 *  net.runelite.api.ChatMessageType
 *  net.runelite.api.Client
 *  net.runelite.api.GameState
 *  net.runelite.api.ItemComposition
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package net.runelite.client.party;

import com.google.common.base.CharMatcher;
import com.google.common.hash.Hashing;
import java.awt.image.BufferedImage;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.UUID;
import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Singleton;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.ItemComposition;
import net.runelite.client.chat.ChatMessageManager;
import net.runelite.client.chat.QueuedMessage;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.PartyChanged;
import net.runelite.client.events.PartyMemberAvatar;
import net.runelite.client.party.PartyMember;
import net.runelite.client.party.WSClient;
import net.runelite.client.party.events.UserJoin;
import net.runelite.client.party.events.UserPart;
import net.runelite.client.party.messages.PartyChatMessage;
import net.runelite.client.party.messages.PartyMessage;
import net.runelite.client.party.messages.UserSync;
import net.runelite.client.util.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class PartyService {
    private static final Logger log = LoggerFactory.getLogger(PartyService.class);
    private static final int MAX_MESSAGE_LEN = 150;
    private static final String ALPHABET = "bcdfghjklmnpqrstvwxyz";
    private final Client client;
    private final WSClient wsClient;
    private final EventBus eventBus;
    private final ChatMessageManager chat;
    private final List<PartyMember> members = new ArrayList<PartyMember>();
    private long partyId;
    private long memberId = PartyService.randomMemberId();
    private String partyPassphrase;

    @Inject
    private PartyService(Client client, WSClient wsClient, EventBus eventBus, ChatMessageManager chat) {
        this.client = client;
        this.wsClient = wsClient;
        this.eventBus = eventBus;
        this.chat = chat;
        eventBus.register(this);
    }

    public String generatePassphrase() {
        int len;
        assert (this.client.isClientThread());
        Random r = new Random();
        StringBuilder sb = new StringBuilder();
        if (this.client.getGameState().getState() >= GameState.LOGIN_SCREEN.getState()) {
            len = 0;
            CharMatcher matcher = CharMatcher.javaLetter();
            do {
                String[] split;
                String token;
                int itemId;
                ItemComposition def;
                String name;
                if ((name = (def = this.client.getItemDefinition(itemId = r.nextInt(this.client.getItemCount()))).getMembersName()) == null || name.isEmpty() || name.equalsIgnoreCase("null") || !matcher.matchesAllOf((CharSequence)(token = (split = name.split(" "))[r.nextInt(split.length)])) || token.length() <= 2) continue;
                if (sb.length() > 0) {
                    sb.append('-');
                }
                sb.append(token.toLowerCase(Locale.US));
                ++len;
            } while (len < 4);
        } else {
            len = 0;
            do {
                if (sb.length() > 0) {
                    sb.append('-');
                }
                for (int i = 0; i < 5; ++i) {
                    sb.append(ALPHABET.charAt(r.nextInt(ALPHABET.length())));
                }
            } while (++len < 4);
        }
        String partyPassphrase = sb.toString();
        log.debug("Generated party passphrase {}", (Object)partyPassphrase);
        return partyPassphrase;
    }

    public void changeParty(@Nullable String passphrase) {
        if (this.wsClient.sessionExists()) {
            this.wsClient.part();
            this.memberId = PartyService.randomMemberId();
        }
        long id = passphrase != null ? PartyService.passphraseToId(passphrase) : 0L;
        log.debug("Party change to {} (id {})", (Object)passphrase, (Object)id);
        this.members.clear();
        this.partyId = id;
        this.partyPassphrase = passphrase;
        if (passphrase == null) {
            this.wsClient.changeSession(null);
            this.eventBus.post(new PartyChanged(this.partyPassphrase, null));
            return;
        }
        if (!this.wsClient.sessionExists()) {
            this.wsClient.changeSession(UUID.randomUUID());
        }
        this.eventBus.post(new PartyChanged(this.partyPassphrase, this.partyId));
        this.wsClient.join(this.partyId, this.memberId);
    }

    public <T extends PartyMessage> void send(T message) {
        if (!this.wsClient.isOpen()) {
            log.debug("Reconnecting to server");
            this.members.clear();
            this.wsClient.connect();
            this.wsClient.join(this.partyId, this.memberId);
        }
        this.wsClient.send(message);
    }

    @Subscribe(priority=1.0f)
    public void onUserJoin(UserJoin message) {
        PartyMember localMember;
        if (this.partyId != message.getPartyId()) {
            return;
        }
        PartyMember partyMember = this.getMemberById(message.getMemberId());
        if (partyMember == null) {
            partyMember = new PartyMember(message.getMemberId());
            this.members.add(partyMember);
            log.debug("User {} joins party, {} members", (Object)partyMember, (Object)this.members.size());
        }
        if ((localMember = this.getLocalMember()) != null && localMember == partyMember) {
            log.debug("Requesting sync");
            UserSync userSync = new UserSync();
            this.wsClient.send(userSync);
        }
    }

    @Subscribe(priority=1.0f)
    public void onUserPart(UserPart message) {
        if (this.members.removeIf(member -> member.getMemberId() == message.getMemberId())) {
            log.debug("User {} leaves party, {} members", (Object)message.getMemberId(), (Object)this.members.size());
        }
    }

    @Subscribe
    public void onPartyChatMessage(PartyChatMessage message) {
        PartyMember member = this.getMemberById(message.getMemberId());
        if (member == null || !member.isLoggedIn()) {
            log.debug("Dropping party chat from non logged-in member");
            return;
        }
        String sentMesage = Text.JAGEX_PRINTABLE_CHAR_MATCHER.retainFrom((CharSequence)message.getValue()).replaceAll("<img=.+>", "");
        if (sentMesage.length() > 150) {
            sentMesage = sentMesage.substring(0, 150);
        }
        this.chat.queue(QueuedMessage.builder().type(ChatMessageType.FRIENDSCHAT).sender("Party").name(member.getDisplayName()).runeLiteFormattedMessage(sentMesage).build());
    }

    public PartyMember getLocalMember() {
        return this.getMemberById(this.memberId);
    }

    public PartyMember getMemberById(long id) {
        for (PartyMember member : this.members) {
            if (id != member.getMemberId()) continue;
            return member;
        }
        return null;
    }

    public PartyMember getMemberByDisplayName(String name) {
        String sanitized = Text.removeTags(Text.toJagexName(name));
        for (PartyMember member : this.members) {
            if (!member.isLoggedIn() || !sanitized.equals(member.getDisplayName())) continue;
            return member;
        }
        return null;
    }

    public List<PartyMember> getMembers() {
        return Collections.unmodifiableList(this.members);
    }

    public boolean isInParty() {
        return this.partyId != 0L;
    }

    public void setPartyMemberAvatar(long memberID, BufferedImage image) {
        PartyMember memberById = this.getMemberById(memberID);
        if (memberById != null) {
            memberById.setAvatar(image);
            this.eventBus.post(new PartyMemberAvatar(memberID, image));
        }
    }

    private static long passphraseToId(String passphrase) {
        return Hashing.sha256().hashBytes(passphrase.getBytes(StandardCharsets.UTF_8)).asLong() & Long.MAX_VALUE;
    }

    private static long randomMemberId() {
        return new Random().nextLong() & Long.MAX_VALUE;
    }

    public long getPartyId() {
        return this.partyId;
    }

    public String getPartyPassphrase() {
        return this.partyPassphrase;
    }
}

