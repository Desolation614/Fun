/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.inject.Inject
 *  javax.inject.Singleton
 *  net.runelite.api.Client
 *  net.runelite.api.FriendsChatManager
 *  net.runelite.api.FriendsChatMember
 *  net.runelite.api.FriendsChatRank
 *  net.runelite.api.Player
 *  net.runelite.api.clan.ClanChannel
 *  net.runelite.api.clan.ClanChannelMember
 *  net.runelite.api.clan.ClanRank
 *  net.runelite.api.clan.ClanSettings
 *  net.runelite.api.clan.ClanTitle
 */
package net.runelite.client.plugins.playerindicators;

import java.awt.Color;
import java.util.function.BiConsumer;
import java.util.function.Predicate;
import javax.inject.Inject;
import javax.inject.Singleton;
import net.runelite.api.Client;
import net.runelite.api.FriendsChatManager;
import net.runelite.api.FriendsChatMember;
import net.runelite.api.FriendsChatRank;
import net.runelite.api.Player;
import net.runelite.api.clan.ClanChannel;
import net.runelite.api.clan.ClanChannelMember;
import net.runelite.api.clan.ClanRank;
import net.runelite.api.clan.ClanSettings;
import net.runelite.api.clan.ClanTitle;
import net.runelite.client.party.PartyService;
import net.runelite.client.plugins.playerindicators.PlayerIndicatorsConfig;
import net.runelite.client.util.Text;

@Singleton
class PlayerIndicatorsService {
    private final Client client;
    private final PlayerIndicatorsConfig config;
    private final PartyService partyService;

    @Inject
    private PlayerIndicatorsService(Client client, PlayerIndicatorsConfig config, PartyService partyService) {
        this.config = config;
        this.client = client;
        this.partyService = partyService;
    }

    void forEachPlayer(BiConsumer<Player, Decorations> consumer) {
        for (Player player : this.client.getPlayers()) {
            Decorations decorations;
            if (player == null || player.getName() == null || (decorations = this.getDecorations(player)) == null || decorations.getColor() == null) continue;
            consumer.accept(player, decorations);
        }
    }

    Decorations getDecorations(Player player) {
        if (player.getName() == null) {
            return null;
        }
        Predicate<PlayerIndicatorsConfig.HighlightSetting> isEnabled = hs -> hs == PlayerIndicatorsConfig.HighlightSetting.ENABLED || hs == PlayerIndicatorsConfig.HighlightSetting.PVP && (this.client.getVarbitValue(5963) == 1 || this.client.getVarbitValue(8121) == 1);
        Color color = null;
        if (player == this.client.getLocalPlayer()) {
            if (isEnabled.test(this.config.highlightOwnPlayer())) {
                color = this.config.getOwnPlayerColor();
            }
        } else if (this.partyService.isInParty() && isEnabled.test(this.config.highlightPartyMembers()) && this.partyService.getMemberByDisplayName(player.getName()) != null) {
            color = this.config.getPartyMemberColor();
        } else if (player.isFriend() && isEnabled.test(this.config.highlightFriends())) {
            color = this.config.getFriendColor();
        } else if (player.isFriendsChatMember() && isEnabled.test(this.config.highlightFriendsChat())) {
            color = this.config.getFriendsChatMemberColor();
        } else if (player.getTeam() > 0 && this.client.getLocalPlayer().getTeam() == player.getTeam() && isEnabled.test(this.config.highlightTeamMembers())) {
            color = this.config.getTeamMemberColor();
        } else if (player.isClanMember() && isEnabled.test(this.config.highlightClanMembers())) {
            color = this.config.getClanMemberColor();
        } else if (!player.isFriendsChatMember() && !player.isClanMember() && isEnabled.test(this.config.highlightOthers())) {
            color = this.config.getOthersColor();
        }
        FriendsChatRank rank = null;
        ClanTitle clanTitle = null;
        if (player.isFriendsChatMember() && this.config.showFriendsChatRanks()) {
            rank = this.getFriendsChatRank(player);
        }
        if (player.isClanMember() && this.config.showClanChatRanks()) {
            clanTitle = this.getClanTitle(player);
        }
        if (color == null && rank == null && clanTitle == null) {
            return null;
        }
        return new Decorations(rank, clanTitle, color);
    }

    private ClanTitle getClanTitle(Player player) {
        ClanChannel clanChannel = this.client.getClanChannel();
        ClanSettings clanSettings = this.client.getClanSettings();
        if (clanChannel == null || clanSettings == null) {
            return null;
        }
        ClanChannelMember member = clanChannel.findMember(player.getName());
        if (member == null) {
            return null;
        }
        ClanRank rank = member.getRank();
        return clanSettings.titleForRank(rank);
    }

    private FriendsChatRank getFriendsChatRank(Player player) {
        FriendsChatManager friendsChatManager = this.client.getFriendsChatManager();
        if (friendsChatManager == null) {
            return FriendsChatRank.UNRANKED;
        }
        FriendsChatMember friendsChatMember = (FriendsChatMember)friendsChatManager.findByName(Text.removeTags(player.getName()));
        return friendsChatMember != null ? friendsChatMember.getRank() : FriendsChatRank.UNRANKED;
    }

    static final class Decorations {
        private final FriendsChatRank friendsChatRank;
        private final ClanTitle clanTitle;
        private final Color color;

        public Decorations(FriendsChatRank friendsChatRank, ClanTitle clanTitle, Color color) {
            this.friendsChatRank = friendsChatRank;
            this.clanTitle = clanTitle;
            this.color = color;
        }

        public FriendsChatRank getFriendsChatRank() {
            return this.friendsChatRank;
        }

        public ClanTitle getClanTitle() {
            return this.clanTitle;
        }

        public Color getColor() {
            return this.color;
        }

        public boolean equals(Object o) {
            if (o == this) {
                return true;
            }
            if (!(o instanceof Decorations)) {
                return false;
            }
            Decorations other = (Decorations)o;
            FriendsChatRank this$friendsChatRank = this.getFriendsChatRank();
            FriendsChatRank other$friendsChatRank = other.getFriendsChatRank();
            if (this$friendsChatRank == null ? other$friendsChatRank != null : !this$friendsChatRank.equals(other$friendsChatRank)) {
                return false;
            }
            ClanTitle this$clanTitle = this.getClanTitle();
            ClanTitle other$clanTitle = other.getClanTitle();
            if (this$clanTitle == null ? other$clanTitle != null : !this$clanTitle.equals(other$clanTitle)) {
                return false;
            }
            Color this$color = this.getColor();
            Color other$color = other.getColor();
            return !(this$color == null ? other$color != null : !((Object)this$color).equals(other$color));
        }

        public int hashCode() {
            int PRIME = 59;
            int result = 1;
            FriendsChatRank $friendsChatRank = this.getFriendsChatRank();
            result = result * 59 + ($friendsChatRank == null ? 43 : $friendsChatRank.hashCode());
            ClanTitle $clanTitle = this.getClanTitle();
            result = result * 59 + ($clanTitle == null ? 43 : $clanTitle.hashCode());
            Color $color = this.getColor();
            result = result * 59 + ($color == null ? 43 : ((Object)$color).hashCode());
            return result;
        }

        public String toString() {
            return "PlayerIndicatorsService.Decorations(friendsChatRank=" + String.valueOf(this.getFriendsChatRank()) + ", clanTitle=" + String.valueOf(this.getClanTitle()) + ", color=" + String.valueOf(this.getColor()) + ")";
        }
    }
}

