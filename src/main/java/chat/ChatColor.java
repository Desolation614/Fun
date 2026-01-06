/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.chat;

import java.awt.Color;
import net.runelite.client.chat.ChatColorType;

class ChatColor {
    private ChatColorType type;
    private Color color;
    private boolean transparent;
    private boolean isDefault;
    private int setting;

    public ChatColor(ChatColorType type, Color color, boolean transparent) {
        this(type, color, transparent, false, -1);
    }

    public ChatColorType getType() {
        return this.type;
    }

    public Color getColor() {
        return this.color;
    }

    public boolean isTransparent() {
        return this.transparent;
    }

    public boolean isDefault() {
        return this.isDefault;
    }

    public int getSetting() {
        return this.setting;
    }

    public void setType(ChatColorType type) {
        this.type = type;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public void setTransparent(boolean transparent) {
        this.transparent = transparent;
    }

    public void setDefault(boolean isDefault) {
        this.isDefault = isDefault;
    }

    public void setSetting(int setting) {
        this.setting = setting;
    }

    public String toString() {
        return "ChatColor(type=" + String.valueOf((Object)this.getType()) + ", color=" + String.valueOf(this.getColor()) + ", transparent=" + this.isTransparent() + ", isDefault=" + this.isDefault() + ", setting=" + this.getSetting() + ")";
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof ChatColor)) {
            return false;
        }
        ChatColor other = (ChatColor)o;
        if (!other.canEqual(this)) {
            return false;
        }
        if (this.isTransparent() != other.isTransparent()) {
            return false;
        }
        ChatColorType this$type = this.getType();
        ChatColorType other$type = other.getType();
        return !(this$type == null ? other$type != null : !((Object)((Object)this$type)).equals((Object)other$type));
    }

    protected boolean canEqual(Object other) {
        return other instanceof ChatColor;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        result = result * 59 + (this.isTransparent() ? 79 : 97);
        ChatColorType $type = this.getType();
        result = result * 59 + ($type == null ? 43 : ((Object)((Object)$type)).hashCode());
        return result;
    }

    public ChatColor(ChatColorType type, Color color, boolean transparent, boolean isDefault, int setting) {
        this.type = type;
        this.color = color;
        this.transparent = transparent;
        this.isDefault = isDefault;
        this.setting = setting;
    }
}

