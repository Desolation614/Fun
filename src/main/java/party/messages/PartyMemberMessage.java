/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.party.messages;

import net.runelite.client.party.messages.PartyMessage;

public abstract class PartyMemberMessage
extends PartyMessage {
    private transient long memberId;

    public long getMemberId() {
        return this.memberId;
    }

    public void setMemberId(long memberId) {
        this.memberId = memberId;
    }
}

