/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.discord;

import java.time.Instant;

public final class DiscordPresence {
    private final String state;
    private final String details;
    private final Instant startTimestamp;
    private final Instant endTimestamp;
    private final String largeImageKey;
    private final String largeImageText;
    private final String smallImageKey;
    private final String smallImageText;
    private final String partyId;
    private final int partySize;
    private final int partyMax;
    private final String matchSecret;
    private final String joinSecret;
    private final String spectateSecret;
    private final boolean instance;

    DiscordPresence(String state, String details, Instant startTimestamp, Instant endTimestamp, String largeImageKey, String largeImageText, String smallImageKey, String smallImageText, String partyId, int partySize, int partyMax, String matchSecret, String joinSecret, String spectateSecret, boolean instance) {
        this.state = state;
        this.details = details;
        this.startTimestamp = startTimestamp;
        this.endTimestamp = endTimestamp;
        this.largeImageKey = largeImageKey;
        this.largeImageText = largeImageText;
        this.smallImageKey = smallImageKey;
        this.smallImageText = smallImageText;
        this.partyId = partyId;
        this.partySize = partySize;
        this.partyMax = partyMax;
        this.matchSecret = matchSecret;
        this.joinSecret = joinSecret;
        this.spectateSecret = spectateSecret;
        this.instance = instance;
    }

    public static DiscordPresenceBuilder builder() {
        return new DiscordPresenceBuilder();
    }

    public DiscordPresenceBuilder toBuilder() {
        return new DiscordPresenceBuilder().state(this.state).details(this.details).startTimestamp(this.startTimestamp).endTimestamp(this.endTimestamp).largeImageKey(this.largeImageKey).largeImageText(this.largeImageText).smallImageKey(this.smallImageKey).smallImageText(this.smallImageText).partyId(this.partyId).partySize(this.partySize).partyMax(this.partyMax).matchSecret(this.matchSecret).joinSecret(this.joinSecret).spectateSecret(this.spectateSecret).instance(this.instance);
    }

    public String getState() {
        return this.state;
    }

    public String getDetails() {
        return this.details;
    }

    public Instant getStartTimestamp() {
        return this.startTimestamp;
    }

    public Instant getEndTimestamp() {
        return this.endTimestamp;
    }

    public String getLargeImageKey() {
        return this.largeImageKey;
    }

    public String getLargeImageText() {
        return this.largeImageText;
    }

    public String getSmallImageKey() {
        return this.smallImageKey;
    }

    public String getSmallImageText() {
        return this.smallImageText;
    }

    public String getPartyId() {
        return this.partyId;
    }

    public int getPartySize() {
        return this.partySize;
    }

    public int getPartyMax() {
        return this.partyMax;
    }

    public String getMatchSecret() {
        return this.matchSecret;
    }

    public String getJoinSecret() {
        return this.joinSecret;
    }

    public String getSpectateSecret() {
        return this.spectateSecret;
    }

    public boolean isInstance() {
        return this.instance;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof DiscordPresence)) {
            return false;
        }
        DiscordPresence other = (DiscordPresence)o;
        if (this.getPartySize() != other.getPartySize()) {
            return false;
        }
        if (this.getPartyMax() != other.getPartyMax()) {
            return false;
        }
        if (this.isInstance() != other.isInstance()) {
            return false;
        }
        String this$state = this.getState();
        String other$state = other.getState();
        if (this$state == null ? other$state != null : !this$state.equals(other$state)) {
            return false;
        }
        String this$details = this.getDetails();
        String other$details = other.getDetails();
        if (this$details == null ? other$details != null : !this$details.equals(other$details)) {
            return false;
        }
        Instant this$startTimestamp = this.getStartTimestamp();
        Instant other$startTimestamp = other.getStartTimestamp();
        if (this$startTimestamp == null ? other$startTimestamp != null : !((Object)this$startTimestamp).equals(other$startTimestamp)) {
            return false;
        }
        Instant this$endTimestamp = this.getEndTimestamp();
        Instant other$endTimestamp = other.getEndTimestamp();
        if (this$endTimestamp == null ? other$endTimestamp != null : !((Object)this$endTimestamp).equals(other$endTimestamp)) {
            return false;
        }
        String this$largeImageKey = this.getLargeImageKey();
        String other$largeImageKey = other.getLargeImageKey();
        if (this$largeImageKey == null ? other$largeImageKey != null : !this$largeImageKey.equals(other$largeImageKey)) {
            return false;
        }
        String this$largeImageText = this.getLargeImageText();
        String other$largeImageText = other.getLargeImageText();
        if (this$largeImageText == null ? other$largeImageText != null : !this$largeImageText.equals(other$largeImageText)) {
            return false;
        }
        String this$smallImageKey = this.getSmallImageKey();
        String other$smallImageKey = other.getSmallImageKey();
        if (this$smallImageKey == null ? other$smallImageKey != null : !this$smallImageKey.equals(other$smallImageKey)) {
            return false;
        }
        String this$smallImageText = this.getSmallImageText();
        String other$smallImageText = other.getSmallImageText();
        if (this$smallImageText == null ? other$smallImageText != null : !this$smallImageText.equals(other$smallImageText)) {
            return false;
        }
        String this$partyId = this.getPartyId();
        String other$partyId = other.getPartyId();
        if (this$partyId == null ? other$partyId != null : !this$partyId.equals(other$partyId)) {
            return false;
        }
        String this$matchSecret = this.getMatchSecret();
        String other$matchSecret = other.getMatchSecret();
        if (this$matchSecret == null ? other$matchSecret != null : !this$matchSecret.equals(other$matchSecret)) {
            return false;
        }
        String this$joinSecret = this.getJoinSecret();
        String other$joinSecret = other.getJoinSecret();
        if (this$joinSecret == null ? other$joinSecret != null : !this$joinSecret.equals(other$joinSecret)) {
            return false;
        }
        String this$spectateSecret = this.getSpectateSecret();
        String other$spectateSecret = other.getSpectateSecret();
        return !(this$spectateSecret == null ? other$spectateSecret != null : !this$spectateSecret.equals(other$spectateSecret));
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        result = result * 59 + this.getPartySize();
        result = result * 59 + this.getPartyMax();
        result = result * 59 + (this.isInstance() ? 79 : 97);
        String $state = this.getState();
        result = result * 59 + ($state == null ? 43 : $state.hashCode());
        String $details = this.getDetails();
        result = result * 59 + ($details == null ? 43 : $details.hashCode());
        Instant $startTimestamp = this.getStartTimestamp();
        result = result * 59 + ($startTimestamp == null ? 43 : ((Object)$startTimestamp).hashCode());
        Instant $endTimestamp = this.getEndTimestamp();
        result = result * 59 + ($endTimestamp == null ? 43 : ((Object)$endTimestamp).hashCode());
        String $largeImageKey = this.getLargeImageKey();
        result = result * 59 + ($largeImageKey == null ? 43 : $largeImageKey.hashCode());
        String $largeImageText = this.getLargeImageText();
        result = result * 59 + ($largeImageText == null ? 43 : $largeImageText.hashCode());
        String $smallImageKey = this.getSmallImageKey();
        result = result * 59 + ($smallImageKey == null ? 43 : $smallImageKey.hashCode());
        String $smallImageText = this.getSmallImageText();
        result = result * 59 + ($smallImageText == null ? 43 : $smallImageText.hashCode());
        String $partyId = this.getPartyId();
        result = result * 59 + ($partyId == null ? 43 : $partyId.hashCode());
        String $matchSecret = this.getMatchSecret();
        result = result * 59 + ($matchSecret == null ? 43 : $matchSecret.hashCode());
        String $joinSecret = this.getJoinSecret();
        result = result * 59 + ($joinSecret == null ? 43 : $joinSecret.hashCode());
        String $spectateSecret = this.getSpectateSecret();
        result = result * 59 + ($spectateSecret == null ? 43 : $spectateSecret.hashCode());
        return result;
    }

    public String toString() {
        return "DiscordPresence(state=" + this.getState() + ", details=" + this.getDetails() + ", startTimestamp=" + String.valueOf(this.getStartTimestamp()) + ", endTimestamp=" + String.valueOf(this.getEndTimestamp()) + ", largeImageKey=" + this.getLargeImageKey() + ", largeImageText=" + this.getLargeImageText() + ", smallImageKey=" + this.getSmallImageKey() + ", smallImageText=" + this.getSmallImageText() + ", partyId=" + this.getPartyId() + ", partySize=" + this.getPartySize() + ", partyMax=" + this.getPartyMax() + ", matchSecret=" + this.getMatchSecret() + ", joinSecret=" + this.getJoinSecret() + ", spectateSecret=" + this.getSpectateSecret() + ", instance=" + this.isInstance() + ")";
    }

    public static class DiscordPresenceBuilder {
        private String state;
        private String details;
        private Instant startTimestamp;
        private Instant endTimestamp;
        private String largeImageKey;
        private String largeImageText;
        private String smallImageKey;
        private String smallImageText;
        private String partyId;
        private int partySize;
        private int partyMax;
        private String matchSecret;
        private String joinSecret;
        private String spectateSecret;
        private boolean instance;

        DiscordPresenceBuilder() {
        }

        public DiscordPresenceBuilder state(String state) {
            this.state = state;
            return this;
        }

        public DiscordPresenceBuilder details(String details) {
            this.details = details;
            return this;
        }

        public DiscordPresenceBuilder startTimestamp(Instant startTimestamp) {
            this.startTimestamp = startTimestamp;
            return this;
        }

        public DiscordPresenceBuilder endTimestamp(Instant endTimestamp) {
            this.endTimestamp = endTimestamp;
            return this;
        }

        public DiscordPresenceBuilder largeImageKey(String largeImageKey) {
            this.largeImageKey = largeImageKey;
            return this;
        }

        public DiscordPresenceBuilder largeImageText(String largeImageText) {
            this.largeImageText = largeImageText;
            return this;
        }

        public DiscordPresenceBuilder smallImageKey(String smallImageKey) {
            this.smallImageKey = smallImageKey;
            return this;
        }

        public DiscordPresenceBuilder smallImageText(String smallImageText) {
            this.smallImageText = smallImageText;
            return this;
        }

        public DiscordPresenceBuilder partyId(String partyId) {
            this.partyId = partyId;
            return this;
        }

        public DiscordPresenceBuilder partySize(int partySize) {
            this.partySize = partySize;
            return this;
        }

        public DiscordPresenceBuilder partyMax(int partyMax) {
            this.partyMax = partyMax;
            return this;
        }

        public DiscordPresenceBuilder matchSecret(String matchSecret) {
            this.matchSecret = matchSecret;
            return this;
        }

        public DiscordPresenceBuilder joinSecret(String joinSecret) {
            this.joinSecret = joinSecret;
            return this;
        }

        public DiscordPresenceBuilder spectateSecret(String spectateSecret) {
            this.spectateSecret = spectateSecret;
            return this;
        }

        public DiscordPresenceBuilder instance(boolean instance) {
            this.instance = instance;
            return this;
        }

        public DiscordPresence build() {
            return new DiscordPresence(this.state, this.details, this.startTimestamp, this.endTimestamp, this.largeImageKey, this.largeImageText, this.smallImageKey, this.smallImageText, this.partyId, this.partySize, this.partyMax, this.matchSecret, this.joinSecret, this.spectateSecret, this.instance);
        }

        public String toString() {
            return "DiscordPresence.DiscordPresenceBuilder(state=" + this.state + ", details=" + this.details + ", startTimestamp=" + String.valueOf(this.startTimestamp) + ", endTimestamp=" + String.valueOf(this.endTimestamp) + ", largeImageKey=" + this.largeImageKey + ", largeImageText=" + this.largeImageText + ", smallImageKey=" + this.smallImageKey + ", smallImageText=" + this.smallImageText + ", partyId=" + this.partyId + ", partySize=" + this.partySize + ", partyMax=" + this.partyMax + ", matchSecret=" + this.matchSecret + ", joinSecret=" + this.joinSecret + ", spectateSecret=" + this.spectateSecret + ", instance=" + this.instance + ")";
        }
    }
}

