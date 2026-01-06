/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.runelite.api.coords.WorldPoint
 */
package net.runelite.client.plugins.grounditems;

import java.awt.Color;
import java.time.Duration;
import java.time.Instant;
import javax.annotation.Nullable;
import net.runelite.api.coords.WorldPoint;

class GroundItem {
    private int id;
    private int itemId;
    private String name;
    private int quantity;
    private WorldPoint location;
    private int height;
    private int haPrice;
    private int gePrice;
    private int offset;
    private boolean tradeable;
    private int ownership;
    private boolean isPrivate;
    @Nullable
    private Instant spawnTime;
    private boolean stackable;
    private Duration despawnTime;
    private Duration visibleTime;
    boolean highlighted;
    boolean hidden;
    Color color;

    int getHaPrice() {
        return this.haPrice * this.quantity;
    }

    int getGePrice() {
        return this.gePrice * this.quantity;
    }

    void reset() {
        this.hidden = false;
        this.highlighted = false;
        this.color = null;
    }

    GroundItem(int id, int itemId, String name, int quantity, WorldPoint location, int height, int haPrice, int gePrice, int offset, boolean tradeable, int ownership, boolean isPrivate, @Nullable Instant spawnTime, boolean stackable, Duration despawnTime, Duration visibleTime, boolean highlighted, boolean hidden, Color color) {
        this.id = id;
        this.itemId = itemId;
        this.name = name;
        this.quantity = quantity;
        this.location = location;
        this.height = height;
        this.haPrice = haPrice;
        this.gePrice = gePrice;
        this.offset = offset;
        this.tradeable = tradeable;
        this.ownership = ownership;
        this.isPrivate = isPrivate;
        this.spawnTime = spawnTime;
        this.stackable = stackable;
        this.despawnTime = despawnTime;
        this.visibleTime = visibleTime;
        this.highlighted = highlighted;
        this.hidden = hidden;
        this.color = color;
    }

    public static GroundItemBuilder builder() {
        return new GroundItemBuilder();
    }

    public int getId() {
        return this.id;
    }

    public int getItemId() {
        return this.itemId;
    }

    public String getName() {
        return this.name;
    }

    public int getQuantity() {
        return this.quantity;
    }

    public WorldPoint getLocation() {
        return this.location;
    }

    public int getHeight() {
        return this.height;
    }

    public int getOffset() {
        return this.offset;
    }

    public boolean isTradeable() {
        return this.tradeable;
    }

    public int getOwnership() {
        return this.ownership;
    }

    public boolean isPrivate() {
        return this.isPrivate;
    }

    @Nullable
    public Instant getSpawnTime() {
        return this.spawnTime;
    }

    public boolean isStackable() {
        return this.stackable;
    }

    public Duration getDespawnTime() {
        return this.despawnTime;
    }

    public Duration getVisibleTime() {
        return this.visibleTime;
    }

    public boolean isHighlighted() {
        return this.highlighted;
    }

    public boolean isHidden() {
        return this.hidden;
    }

    public Color getColor() {
        return this.color;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setLocation(WorldPoint location) {
        this.location = location;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public void setHaPrice(int haPrice) {
        this.haPrice = haPrice;
    }

    public void setGePrice(int gePrice) {
        this.gePrice = gePrice;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public void setTradeable(boolean tradeable) {
        this.tradeable = tradeable;
    }

    public void setOwnership(int ownership) {
        this.ownership = ownership;
    }

    public void setPrivate(boolean isPrivate) {
        this.isPrivate = isPrivate;
    }

    public void setSpawnTime(@Nullable Instant spawnTime) {
        this.spawnTime = spawnTime;
    }

    public void setStackable(boolean stackable) {
        this.stackable = stackable;
    }

    public void setDespawnTime(Duration despawnTime) {
        this.despawnTime = despawnTime;
    }

    public void setVisibleTime(Duration visibleTime) {
        this.visibleTime = visibleTime;
    }

    public void setHighlighted(boolean highlighted) {
        this.highlighted = highlighted;
    }

    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof GroundItem)) {
            return false;
        }
        GroundItem other = (GroundItem)o;
        if (!other.canEqual(this)) {
            return false;
        }
        if (this.getId() != other.getId()) {
            return false;
        }
        if (this.getItemId() != other.getItemId()) {
            return false;
        }
        if (this.getQuantity() != other.getQuantity()) {
            return false;
        }
        if (this.getHeight() != other.getHeight()) {
            return false;
        }
        if (this.getHaPrice() != other.getHaPrice()) {
            return false;
        }
        if (this.getGePrice() != other.getGePrice()) {
            return false;
        }
        if (this.getOffset() != other.getOffset()) {
            return false;
        }
        if (this.isTradeable() != other.isTradeable()) {
            return false;
        }
        if (this.getOwnership() != other.getOwnership()) {
            return false;
        }
        if (this.isPrivate() != other.isPrivate()) {
            return false;
        }
        if (this.isStackable() != other.isStackable()) {
            return false;
        }
        if (this.isHighlighted() != other.isHighlighted()) {
            return false;
        }
        if (this.isHidden() != other.isHidden()) {
            return false;
        }
        String this$name = this.getName();
        String other$name = other.getName();
        if (this$name == null ? other$name != null : !this$name.equals(other$name)) {
            return false;
        }
        WorldPoint this$location = this.getLocation();
        WorldPoint other$location = other.getLocation();
        if (this$location == null ? other$location != null : !this$location.equals(other$location)) {
            return false;
        }
        Instant this$spawnTime = this.getSpawnTime();
        Instant other$spawnTime = other.getSpawnTime();
        if (this$spawnTime == null ? other$spawnTime != null : !((Object)this$spawnTime).equals(other$spawnTime)) {
            return false;
        }
        Duration this$despawnTime = this.getDespawnTime();
        Duration other$despawnTime = other.getDespawnTime();
        if (this$despawnTime == null ? other$despawnTime != null : !((Object)this$despawnTime).equals(other$despawnTime)) {
            return false;
        }
        Duration this$visibleTime = this.getVisibleTime();
        Duration other$visibleTime = other.getVisibleTime();
        if (this$visibleTime == null ? other$visibleTime != null : !((Object)this$visibleTime).equals(other$visibleTime)) {
            return false;
        }
        Color this$color = this.getColor();
        Color other$color = other.getColor();
        return !(this$color == null ? other$color != null : !((Object)this$color).equals(other$color));
    }

    protected boolean canEqual(Object other) {
        return other instanceof GroundItem;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        result = result * 59 + this.getId();
        result = result * 59 + this.getItemId();
        result = result * 59 + this.getQuantity();
        result = result * 59 + this.getHeight();
        result = result * 59 + this.getHaPrice();
        result = result * 59 + this.getGePrice();
        result = result * 59 + this.getOffset();
        result = result * 59 + (this.isTradeable() ? 79 : 97);
        result = result * 59 + this.getOwnership();
        result = result * 59 + (this.isPrivate() ? 79 : 97);
        result = result * 59 + (this.isStackable() ? 79 : 97);
        result = result * 59 + (this.isHighlighted() ? 79 : 97);
        result = result * 59 + (this.isHidden() ? 79 : 97);
        String $name = this.getName();
        result = result * 59 + ($name == null ? 43 : $name.hashCode());
        WorldPoint $location = this.getLocation();
        result = result * 59 + ($location == null ? 43 : $location.hashCode());
        Instant $spawnTime = this.getSpawnTime();
        result = result * 59 + ($spawnTime == null ? 43 : ((Object)$spawnTime).hashCode());
        Duration $despawnTime = this.getDespawnTime();
        result = result * 59 + ($despawnTime == null ? 43 : ((Object)$despawnTime).hashCode());
        Duration $visibleTime = this.getVisibleTime();
        result = result * 59 + ($visibleTime == null ? 43 : ((Object)$visibleTime).hashCode());
        Color $color = this.getColor();
        result = result * 59 + ($color == null ? 43 : ((Object)$color).hashCode());
        return result;
    }

    public String toString() {
        return "GroundItem(id=" + this.getId() + ", itemId=" + this.getItemId() + ", name=" + this.getName() + ", quantity=" + this.getQuantity() + ", location=" + String.valueOf(this.getLocation()) + ", height=" + this.getHeight() + ", haPrice=" + this.getHaPrice() + ", gePrice=" + this.getGePrice() + ", offset=" + this.getOffset() + ", tradeable=" + this.isTradeable() + ", ownership=" + this.getOwnership() + ", isPrivate=" + this.isPrivate() + ", spawnTime=" + String.valueOf(this.getSpawnTime()) + ", stackable=" + this.isStackable() + ", despawnTime=" + String.valueOf(this.getDespawnTime()) + ", visibleTime=" + String.valueOf(this.getVisibleTime()) + ", highlighted=" + this.isHighlighted() + ", hidden=" + this.isHidden() + ", color=" + String.valueOf(this.getColor()) + ")";
    }

    public static class GroundItemBuilder {
        private int id;
        private int itemId;
        private String name;
        private int quantity;
        private WorldPoint location;
        private int height;
        private int haPrice;
        private int gePrice;
        private int offset;
        private boolean tradeable;
        private int ownership;
        private boolean isPrivate;
        private Instant spawnTime;
        private boolean stackable;
        private Duration despawnTime;
        private Duration visibleTime;
        private boolean highlighted;
        private boolean hidden;
        private Color color;

        GroundItemBuilder() {
        }

        public GroundItemBuilder id(int id) {
            this.id = id;
            return this;
        }

        public GroundItemBuilder itemId(int itemId) {
            this.itemId = itemId;
            return this;
        }

        public GroundItemBuilder name(String name) {
            this.name = name;
            return this;
        }

        public GroundItemBuilder quantity(int quantity) {
            this.quantity = quantity;
            return this;
        }

        public GroundItemBuilder location(WorldPoint location) {
            this.location = location;
            return this;
        }

        public GroundItemBuilder height(int height) {
            this.height = height;
            return this;
        }

        public GroundItemBuilder haPrice(int haPrice) {
            this.haPrice = haPrice;
            return this;
        }

        public GroundItemBuilder gePrice(int gePrice) {
            this.gePrice = gePrice;
            return this;
        }

        public GroundItemBuilder offset(int offset) {
            this.offset = offset;
            return this;
        }

        public GroundItemBuilder tradeable(boolean tradeable) {
            this.tradeable = tradeable;
            return this;
        }

        public GroundItemBuilder ownership(int ownership) {
            this.ownership = ownership;
            return this;
        }

        public GroundItemBuilder isPrivate(boolean isPrivate) {
            this.isPrivate = isPrivate;
            return this;
        }

        public GroundItemBuilder spawnTime(@Nullable Instant spawnTime) {
            this.spawnTime = spawnTime;
            return this;
        }

        public GroundItemBuilder stackable(boolean stackable) {
            this.stackable = stackable;
            return this;
        }

        public GroundItemBuilder despawnTime(Duration despawnTime) {
            this.despawnTime = despawnTime;
            return this;
        }

        public GroundItemBuilder visibleTime(Duration visibleTime) {
            this.visibleTime = visibleTime;
            return this;
        }

        public GroundItemBuilder highlighted(boolean highlighted) {
            this.highlighted = highlighted;
            return this;
        }

        public GroundItemBuilder hidden(boolean hidden) {
            this.hidden = hidden;
            return this;
        }

        public GroundItemBuilder color(Color color) {
            this.color = color;
            return this;
        }

        public GroundItem build() {
            return new GroundItem(this.id, this.itemId, this.name, this.quantity, this.location, this.height, this.haPrice, this.gePrice, this.offset, this.tradeable, this.ownership, this.isPrivate, this.spawnTime, this.stackable, this.despawnTime, this.visibleTime, this.highlighted, this.hidden, this.color);
        }

        public String toString() {
            return "GroundItem.GroundItemBuilder(id=" + this.id + ", itemId=" + this.itemId + ", name=" + this.name + ", quantity=" + this.quantity + ", location=" + String.valueOf(this.location) + ", height=" + this.height + ", haPrice=" + this.haPrice + ", gePrice=" + this.gePrice + ", offset=" + this.offset + ", tradeable=" + this.tradeable + ", ownership=" + this.ownership + ", isPrivate=" + this.isPrivate + ", spawnTime=" + String.valueOf(this.spawnTime) + ", stackable=" + this.stackable + ", despawnTime=" + String.valueOf(this.despawnTime) + ", visibleTime=" + String.valueOf(this.visibleTime) + ", highlighted=" + this.highlighted + ", hidden=" + this.hidden + ", color=" + String.valueOf(this.color) + ")";
        }
    }
}

