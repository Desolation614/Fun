/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.plugins.friendnotes;

final class HoveredFriend {
    private final String friendName;
    private final String note;

    public HoveredFriend(String friendName, String note) {
        this.friendName = friendName;
        this.note = note;
    }

    public String getFriendName() {
        return this.friendName;
    }

    public String getNote() {
        return this.note;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof HoveredFriend)) {
            return false;
        }
        HoveredFriend other = (HoveredFriend)o;
        String this$friendName = this.getFriendName();
        String other$friendName = other.getFriendName();
        if (this$friendName == null ? other$friendName != null : !this$friendName.equals(other$friendName)) {
            return false;
        }
        String this$note = this.getNote();
        String other$note = other.getNote();
        return !(this$note == null ? other$note != null : !this$note.equals(other$note));
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        String $friendName = this.getFriendName();
        result = result * 59 + ($friendName == null ? 43 : $friendName.hashCode());
        String $note = this.getNote();
        result = result * 59 + ($note == null ? 43 : $note.hashCode());
        return result;
    }

    public String toString() {
        return "HoveredFriend(friendName=" + this.getFriendName() + ", note=" + this.getNote() + ")";
    }
}

