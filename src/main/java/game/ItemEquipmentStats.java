/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.annotations.SerializedName
 *  net.runelite.http.api.item.ItemEquipmentStats
 */
package net.runelite.client.game;

import com.google.gson.annotations.SerializedName;

public final class ItemEquipmentStats {
    private final int slot;
    @SerializedName(value="is2h")
    private final boolean isTwoHanded;
    private final int astab;
    private final int aslash;
    private final int acrush;
    private final int amagic;
    private final int arange;
    private final int dstab;
    private final int dslash;
    private final int dcrush;
    private final int dmagic;
    private final int drange;
    private final int str;
    private final int rstr;
    private final float mdmg;
    private final int prayer;
    private final int aspeed;

    net.runelite.http.api.item.ItemEquipmentStats toHttpApiFormat() {
        return net.runelite.http.api.item.ItemEquipmentStats.builder().slot(this.slot).isTwoHanded(this.isTwoHanded).astab(this.astab).aslash(this.aslash).acrush(this.acrush).amagic(this.amagic).arange(this.arange).dstab(this.dstab).dslash(this.dslash).dcrush(this.dcrush).dmagic(this.dmagic).drange(this.drange).str(this.str).rstr(this.rstr).mdmg((int)this.mdmg).prayer(this.prayer).aspeed(this.aspeed).build();
    }

    ItemEquipmentStats(int slot, boolean isTwoHanded, int astab, int aslash, int acrush, int amagic, int arange, int dstab, int dslash, int dcrush, int dmagic, int drange, int str, int rstr, float mdmg, int prayer, int aspeed) {
        this.slot = slot;
        this.isTwoHanded = isTwoHanded;
        this.astab = astab;
        this.aslash = aslash;
        this.acrush = acrush;
        this.amagic = amagic;
        this.arange = arange;
        this.dstab = dstab;
        this.dslash = dslash;
        this.dcrush = dcrush;
        this.dmagic = dmagic;
        this.drange = drange;
        this.str = str;
        this.rstr = rstr;
        this.mdmg = mdmg;
        this.prayer = prayer;
        this.aspeed = aspeed;
    }

    public static ItemEquipmentStatsBuilder builder() {
        return new ItemEquipmentStatsBuilder();
    }

    public int getSlot() {
        return this.slot;
    }

    public boolean isTwoHanded() {
        return this.isTwoHanded;
    }

    public int getAstab() {
        return this.astab;
    }

    public int getAslash() {
        return this.aslash;
    }

    public int getAcrush() {
        return this.acrush;
    }

    public int getAmagic() {
        return this.amagic;
    }

    public int getArange() {
        return this.arange;
    }

    public int getDstab() {
        return this.dstab;
    }

    public int getDslash() {
        return this.dslash;
    }

    public int getDcrush() {
        return this.dcrush;
    }

    public int getDmagic() {
        return this.dmagic;
    }

    public int getDrange() {
        return this.drange;
    }

    public int getStr() {
        return this.str;
    }

    public int getRstr() {
        return this.rstr;
    }

    public float getMdmg() {
        return this.mdmg;
    }

    public int getPrayer() {
        return this.prayer;
    }

    public int getAspeed() {
        return this.aspeed;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof ItemEquipmentStats)) {
            return false;
        }
        ItemEquipmentStats other = (ItemEquipmentStats)o;
        if (this.getSlot() != other.getSlot()) {
            return false;
        }
        if (this.isTwoHanded() != other.isTwoHanded()) {
            return false;
        }
        if (this.getAstab() != other.getAstab()) {
            return false;
        }
        if (this.getAslash() != other.getAslash()) {
            return false;
        }
        if (this.getAcrush() != other.getAcrush()) {
            return false;
        }
        if (this.getAmagic() != other.getAmagic()) {
            return false;
        }
        if (this.getArange() != other.getArange()) {
            return false;
        }
        if (this.getDstab() != other.getDstab()) {
            return false;
        }
        if (this.getDslash() != other.getDslash()) {
            return false;
        }
        if (this.getDcrush() != other.getDcrush()) {
            return false;
        }
        if (this.getDmagic() != other.getDmagic()) {
            return false;
        }
        if (this.getDrange() != other.getDrange()) {
            return false;
        }
        if (this.getStr() != other.getStr()) {
            return false;
        }
        if (this.getRstr() != other.getRstr()) {
            return false;
        }
        if (Float.compare(this.getMdmg(), other.getMdmg()) != 0) {
            return false;
        }
        if (this.getPrayer() != other.getPrayer()) {
            return false;
        }
        return this.getAspeed() == other.getAspeed();
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        result = result * 59 + this.getSlot();
        result = result * 59 + (this.isTwoHanded() ? 79 : 97);
        result = result * 59 + this.getAstab();
        result = result * 59 + this.getAslash();
        result = result * 59 + this.getAcrush();
        result = result * 59 + this.getAmagic();
        result = result * 59 + this.getArange();
        result = result * 59 + this.getDstab();
        result = result * 59 + this.getDslash();
        result = result * 59 + this.getDcrush();
        result = result * 59 + this.getDmagic();
        result = result * 59 + this.getDrange();
        result = result * 59 + this.getStr();
        result = result * 59 + this.getRstr();
        result = result * 59 + Float.floatToIntBits(this.getMdmg());
        result = result * 59 + this.getPrayer();
        result = result * 59 + this.getAspeed();
        return result;
    }

    public String toString() {
        return "ItemEquipmentStats(slot=" + this.getSlot() + ", isTwoHanded=" + this.isTwoHanded() + ", astab=" + this.getAstab() + ", aslash=" + this.getAslash() + ", acrush=" + this.getAcrush() + ", amagic=" + this.getAmagic() + ", arange=" + this.getArange() + ", dstab=" + this.getDstab() + ", dslash=" + this.getDslash() + ", dcrush=" + this.getDcrush() + ", dmagic=" + this.getDmagic() + ", drange=" + this.getDrange() + ", str=" + this.getStr() + ", rstr=" + this.getRstr() + ", mdmg=" + this.getMdmg() + ", prayer=" + this.getPrayer() + ", aspeed=" + this.getAspeed() + ")";
    }

    public static class ItemEquipmentStatsBuilder {
        private int slot;
        private boolean isTwoHanded;
        private int astab;
        private int aslash;
        private int acrush;
        private int amagic;
        private int arange;
        private int dstab;
        private int dslash;
        private int dcrush;
        private int dmagic;
        private int drange;
        private int str;
        private int rstr;
        private float mdmg;
        private int prayer;
        private int aspeed;

        ItemEquipmentStatsBuilder() {
        }

        public ItemEquipmentStatsBuilder slot(int slot) {
            this.slot = slot;
            return this;
        }

        public ItemEquipmentStatsBuilder isTwoHanded(boolean isTwoHanded) {
            this.isTwoHanded = isTwoHanded;
            return this;
        }

        public ItemEquipmentStatsBuilder astab(int astab) {
            this.astab = astab;
            return this;
        }

        public ItemEquipmentStatsBuilder aslash(int aslash) {
            this.aslash = aslash;
            return this;
        }

        public ItemEquipmentStatsBuilder acrush(int acrush) {
            this.acrush = acrush;
            return this;
        }

        public ItemEquipmentStatsBuilder amagic(int amagic) {
            this.amagic = amagic;
            return this;
        }

        public ItemEquipmentStatsBuilder arange(int arange) {
            this.arange = arange;
            return this;
        }

        public ItemEquipmentStatsBuilder dstab(int dstab) {
            this.dstab = dstab;
            return this;
        }

        public ItemEquipmentStatsBuilder dslash(int dslash) {
            this.dslash = dslash;
            return this;
        }

        public ItemEquipmentStatsBuilder dcrush(int dcrush) {
            this.dcrush = dcrush;
            return this;
        }

        public ItemEquipmentStatsBuilder dmagic(int dmagic) {
            this.dmagic = dmagic;
            return this;
        }

        public ItemEquipmentStatsBuilder drange(int drange) {
            this.drange = drange;
            return this;
        }

        public ItemEquipmentStatsBuilder str(int str) {
            this.str = str;
            return this;
        }

        public ItemEquipmentStatsBuilder rstr(int rstr) {
            this.rstr = rstr;
            return this;
        }

        public ItemEquipmentStatsBuilder mdmg(float mdmg) {
            this.mdmg = mdmg;
            return this;
        }

        public ItemEquipmentStatsBuilder prayer(int prayer) {
            this.prayer = prayer;
            return this;
        }

        public ItemEquipmentStatsBuilder aspeed(int aspeed) {
            this.aspeed = aspeed;
            return this;
        }

        public ItemEquipmentStats build() {
            return new ItemEquipmentStats(this.slot, this.isTwoHanded, this.astab, this.aslash, this.acrush, this.amagic, this.arange, this.dstab, this.dslash, this.dcrush, this.dmagic, this.drange, this.str, this.rstr, this.mdmg, this.prayer, this.aspeed);
        }

        public String toString() {
            return "ItemEquipmentStats.ItemEquipmentStatsBuilder(slot=" + this.slot + ", isTwoHanded=" + this.isTwoHanded + ", astab=" + this.astab + ", aslash=" + this.aslash + ", acrush=" + this.acrush + ", amagic=" + this.amagic + ", arange=" + this.arange + ", dstab=" + this.dstab + ", dslash=" + this.dslash + ", dcrush=" + this.dcrush + ", dmagic=" + this.dmagic + ", drange=" + this.drange + ", str=" + this.str + ", rstr=" + this.rstr + ", mdmg=" + this.mdmg + ", prayer=" + this.prayer + ", aspeed=" + this.aspeed + ")";
        }
    }
}

