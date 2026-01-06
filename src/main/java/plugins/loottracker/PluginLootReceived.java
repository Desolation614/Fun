/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  lombok.NonNull
 *  net.runelite.http.api.loottracker.LootRecordType
 */
package net.runelite.client.plugins.loottracker;

import java.util.Collection;
import javax.annotation.Nullable;
import lombok.NonNull;
import net.runelite.client.game.ItemStack;
import net.runelite.client.plugins.Plugin;
import net.runelite.http.api.loottracker.LootRecordType;

public final class PluginLootReceived {
    @NonNull
    private final Plugin source;
    @NonNull
    private final String name;
    private final int combatLevel;
    @NonNull
    private final LootRecordType type;
    @NonNull
    private final Collection<ItemStack> items;
    private final int amount;
    @Nullable
    private final Object metadata;

    private static int $default$combatLevel() {
        return -1;
    }

    private static int $default$amount() {
        return 1;
    }

    PluginLootReceived(@NonNull Plugin source, @NonNull String name, int combatLevel, @NonNull LootRecordType type, @NonNull Collection<ItemStack> items, int amount, @Nullable Object metadata) {
        if (source == null) {
            throw new NullPointerException("source is marked non-null but is null");
        }
        if (name == null) {
            throw new NullPointerException("name is marked non-null but is null");
        }
        if (type == null) {
            throw new NullPointerException("type is marked non-null but is null");
        }
        if (items == null) {
            throw new NullPointerException("items is marked non-null but is null");
        }
        this.source = source;
        this.name = name;
        this.combatLevel = combatLevel;
        this.type = type;
        this.items = items;
        this.amount = amount;
        this.metadata = metadata;
    }

    public static PluginLootReceivedBuilder builder() {
        return new PluginLootReceivedBuilder();
    }

    @NonNull
    public Plugin getSource() {
        return this.source;
    }

    @NonNull
    public String getName() {
        return this.name;
    }

    public int getCombatLevel() {
        return this.combatLevel;
    }

    @NonNull
    public LootRecordType getType() {
        return this.type;
    }

    @NonNull
    public Collection<ItemStack> getItems() {
        return this.items;
    }

    public int getAmount() {
        return this.amount;
    }

    @Nullable
    public Object getMetadata() {
        return this.metadata;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof PluginLootReceived)) {
            return false;
        }
        PluginLootReceived other = (PluginLootReceived)o;
        if (this.getCombatLevel() != other.getCombatLevel()) {
            return false;
        }
        if (this.getAmount() != other.getAmount()) {
            return false;
        }
        Plugin this$source = this.getSource();
        Plugin other$source = other.getSource();
        if (this$source == null ? other$source != null : !((Object)this$source).equals(other$source)) {
            return false;
        }
        String this$name = this.getName();
        String other$name = other.getName();
        if (this$name == null ? other$name != null : !this$name.equals(other$name)) {
            return false;
        }
        LootRecordType this$type = this.getType();
        LootRecordType other$type = other.getType();
        if (this$type == null ? other$type != null : !this$type.equals(other$type)) {
            return false;
        }
        Collection<ItemStack> this$items = this.getItems();
        Collection<ItemStack> other$items = other.getItems();
        if (this$items == null ? other$items != null : !((Object)this$items).equals(other$items)) {
            return false;
        }
        Object this$metadata = this.getMetadata();
        Object other$metadata = other.getMetadata();
        return !(this$metadata == null ? other$metadata != null : !this$metadata.equals(other$metadata));
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        result = result * 59 + this.getCombatLevel();
        result = result * 59 + this.getAmount();
        Plugin $source = this.getSource();
        result = result * 59 + ($source == null ? 43 : ((Object)$source).hashCode());
        String $name = this.getName();
        result = result * 59 + ($name == null ? 43 : $name.hashCode());
        LootRecordType $type = this.getType();
        result = result * 59 + ($type == null ? 43 : $type.hashCode());
        Collection<ItemStack> $items = this.getItems();
        result = result * 59 + ($items == null ? 43 : ((Object)$items).hashCode());
        Object $metadata = this.getMetadata();
        result = result * 59 + ($metadata == null ? 43 : $metadata.hashCode());
        return result;
    }

    public String toString() {
        return "PluginLootReceived(source=" + String.valueOf(this.getSource()) + ", name=" + this.getName() + ", combatLevel=" + this.getCombatLevel() + ", type=" + String.valueOf(this.getType()) + ", items=" + String.valueOf(this.getItems()) + ", amount=" + this.getAmount() + ", metadata=" + String.valueOf(this.getMetadata()) + ")";
    }

    public static class PluginLootReceivedBuilder {
        private Plugin source;
        private String name;
        private boolean combatLevel$set;
        private int combatLevel$value;
        private LootRecordType type;
        private Collection<ItemStack> items;
        private boolean amount$set;
        private int amount$value;
        private Object metadata;

        PluginLootReceivedBuilder() {
        }

        public PluginLootReceivedBuilder source(@NonNull Plugin source) {
            if (source == null) {
                throw new NullPointerException("source is marked non-null but is null");
            }
            this.source = source;
            return this;
        }

        public PluginLootReceivedBuilder name(@NonNull String name) {
            if (name == null) {
                throw new NullPointerException("name is marked non-null but is null");
            }
            this.name = name;
            return this;
        }

        public PluginLootReceivedBuilder combatLevel(int combatLevel) {
            this.combatLevel$value = combatLevel;
            this.combatLevel$set = true;
            return this;
        }

        public PluginLootReceivedBuilder type(@NonNull LootRecordType type) {
            if (type == null) {
                throw new NullPointerException("type is marked non-null but is null");
            }
            this.type = type;
            return this;
        }

        public PluginLootReceivedBuilder items(@NonNull Collection<ItemStack> items) {
            if (items == null) {
                throw new NullPointerException("items is marked non-null but is null");
            }
            this.items = items;
            return this;
        }

        public PluginLootReceivedBuilder amount(int amount) {
            this.amount$value = amount;
            this.amount$set = true;
            return this;
        }

        public PluginLootReceivedBuilder metadata(@Nullable Object metadata) {
            this.metadata = metadata;
            return this;
        }

        public PluginLootReceived build() {
            int combatLevel$value = this.combatLevel$value;
            if (!this.combatLevel$set) {
                combatLevel$value = PluginLootReceived.$default$combatLevel();
            }
            int amount$value = this.amount$value;
            if (!this.amount$set) {
                amount$value = PluginLootReceived.$default$amount();
            }
            return new PluginLootReceived(this.source, this.name, combatLevel$value, this.type, this.items, amount$value, this.metadata);
        }

        public String toString() {
            return "PluginLootReceived.PluginLootReceivedBuilder(source=" + String.valueOf(this.source) + ", name=" + this.name + ", combatLevel$value=" + this.combatLevel$value + ", type=" + String.valueOf(this.type) + ", items=" + String.valueOf(this.items) + ", amount$value=" + this.amount$value + ", metadata=" + String.valueOf(this.metadata) + ")";
        }
    }
}

