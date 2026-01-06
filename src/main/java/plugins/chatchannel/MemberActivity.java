/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.runelite.api.ChatPlayer
 */
package net.runelite.client.plugins.chatchannel;

import net.runelite.api.ChatPlayer;
import net.runelite.client.plugins.chatchannel.ActivityType;

final class MemberActivity {
    private final ActivityType activityType;
    private final ChatType chatType;
    private final ChatPlayer member;
    private final Integer tick;

    public ActivityType getActivityType() {
        return this.activityType;
    }

    public ChatType getChatType() {
        return this.chatType;
    }

    public ChatPlayer getMember() {
        return this.member;
    }

    public Integer getTick() {
        return this.tick;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof MemberActivity)) {
            return false;
        }
        MemberActivity other = (MemberActivity)o;
        Integer this$tick = this.getTick();
        Integer other$tick = other.getTick();
        if (this$tick == null ? other$tick != null : !((Object)this$tick).equals(other$tick)) {
            return false;
        }
        ActivityType this$activityType = this.getActivityType();
        ActivityType other$activityType = other.getActivityType();
        if (this$activityType == null ? other$activityType != null : !((Object)((Object)this$activityType)).equals((Object)other$activityType)) {
            return false;
        }
        ChatType this$chatType = this.getChatType();
        ChatType other$chatType = other.getChatType();
        if (this$chatType == null ? other$chatType != null : !((Object)((Object)this$chatType)).equals((Object)other$chatType)) {
            return false;
        }
        ChatPlayer this$member = this.getMember();
        ChatPlayer other$member = other.getMember();
        return !(this$member == null ? other$member != null : !this$member.equals(other$member));
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        Integer $tick = this.getTick();
        result = result * 59 + ($tick == null ? 43 : ((Object)$tick).hashCode());
        ActivityType $activityType = this.getActivityType();
        result = result * 59 + ($activityType == null ? 43 : ((Object)((Object)$activityType)).hashCode());
        ChatType $chatType = this.getChatType();
        result = result * 59 + ($chatType == null ? 43 : ((Object)((Object)$chatType)).hashCode());
        ChatPlayer $member = this.getMember();
        result = result * 59 + ($member == null ? 43 : $member.hashCode());
        return result;
    }

    public String toString() {
        return "MemberActivity(activityType=" + String.valueOf((Object)this.getActivityType()) + ", chatType=" + String.valueOf((Object)this.getChatType()) + ", member=" + String.valueOf(this.getMember()) + ", tick=" + this.getTick() + ")";
    }

    public MemberActivity(ActivityType activityType, ChatType chatType, ChatPlayer member, Integer tick) {
        this.activityType = activityType;
        this.chatType = chatType;
        this.member = member;
        this.tick = tick;
    }

    static enum ChatType {
        FRIENDS_CHAT,
        CLAN_CHAT,
        GUEST_CHAT;

    }
}

