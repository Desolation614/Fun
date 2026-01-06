/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.NonNull
 *  net.runelite.http.api.loottracker.LootRecordType
 */
package net.runelite.client.plugins.loottracker;

import java.util.Arrays;
import lombok.NonNull;
import net.runelite.client.plugins.loottracker.LootTrackerItem;
import net.runelite.http.api.loottracker.LootRecordType;

final class LootTrackerRecord {
    @NonNull
    private final String title;
    private final String subTitle;
    private final LootRecordType type;
    private final LootTrackerItem[] items;
    private final int kills;

    boolean matches(String id, LootRecordType type) {
        if (id == null) {
            return true;
        }
        return this.title.equals(id) && this.type == type;
    }

    public LootTrackerRecord(@NonNull String title, String subTitle, LootRecordType type, LootTrackerItem[] items, int kills) {
        if (title == null) {
            throw new NullPointerException("title is marked non-null but is null");
        }
        this.title = title;
        this.subTitle = subTitle;
        this.type = type;
        this.items = items;
        this.kills = kills;
    }

    @NonNull
    public String getTitle() {
        return this.title;
    }

    public String getSubTitle() {
        return this.subTitle;
    }

    public LootRecordType getType() {
        return this.type;
    }

    public LootTrackerItem[] getItems() {
        return this.items;
    }

    public int getKills() {
        return this.kills;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof LootTrackerRecord)) {
            return false;
        }
        LootTrackerRecord other = (LootTrackerRecord)o;
        if (this.getKills() != other.getKills()) {
            return false;
        }
        String this$title = this.getTitle();
        String other$title = other.getTitle();
        if (this$title == null ? other$title != null : !this$title.equals(other$title)) {
            return false;
        }
        String this$subTitle = this.getSubTitle();
        String other$subTitle = other.getSubTitle();
        if (this$subTitle == null ? other$subTitle != null : !this$subTitle.equals(other$subTitle)) {
            return false;
        }
        LootRecordType this$type = this.getType();
        LootRecordType other$type = other.getType();
        if (this$type == null ? other$type != null : !this$type.equals(other$type)) {
            return false;
        }
        return Arrays.deepEquals(this.getItems(), other.getItems());
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        result = result * 59 + this.getKills();
        String $title = this.getTitle();
        result = result * 59 + ($title == null ? 43 : $title.hashCode());
        String $subTitle = this.getSubTitle();
        result = result * 59 + ($subTitle == null ? 43 : $subTitle.hashCode());
        LootRecordType $type = this.getType();
        result = result * 59 + ($type == null ? 43 : $type.hashCode());
        result = result * 59 + Arrays.deepHashCode(this.getItems());
        return result;
    }

    public String toString() {
        return "LootTrackerRecord(title=" + this.getTitle() + ", subTitle=" + this.getSubTitle() + ", type=" + String.valueOf(this.getType()) + ", items=" + Arrays.deepToString(this.getItems()) + ", kills=" + this.getKills() + ")";
    }
}

