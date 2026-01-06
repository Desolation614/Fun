/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.events;

public final class PartyChanged {
    private final String passphrase;
    private final Long partyId;

    public PartyChanged(String passphrase, Long partyId) {
        this.passphrase = passphrase;
        this.partyId = partyId;
    }

    public String getPassphrase() {
        return this.passphrase;
    }

    public Long getPartyId() {
        return this.partyId;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof PartyChanged)) {
            return false;
        }
        PartyChanged other = (PartyChanged)o;
        Long this$partyId = this.getPartyId();
        Long other$partyId = other.getPartyId();
        if (this$partyId == null ? other$partyId != null : !((Object)this$partyId).equals(other$partyId)) {
            return false;
        }
        String this$passphrase = this.getPassphrase();
        String other$passphrase = other.getPassphrase();
        return !(this$passphrase == null ? other$passphrase != null : !this$passphrase.equals(other$passphrase));
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        Long $partyId = this.getPartyId();
        result = result * 59 + ($partyId == null ? 43 : ((Object)$partyId).hashCode());
        String $passphrase = this.getPassphrase();
        result = result * 59 + ($passphrase == null ? 43 : $passphrase.hashCode());
        return result;
    }

    public String toString() {
        return "PartyChanged(passphrase=" + this.getPassphrase() + ", partyId=" + this.getPartyId() + ")";
    }
}

