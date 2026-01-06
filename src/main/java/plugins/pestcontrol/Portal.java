/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.plugins.pestcontrol;

enum Portal {
    PURPLE(26738701, 26738709, 26738705),
    BLUE(26738702, 26738710, 26738706),
    YELLOW(26738703, 26738711, 26738707),
    RED(26738704, 26738712, 26738708);

    private final int shield;
    private final int hitpoints;
    private final int icon;

    private Portal(int shield, int hitpoints, int icon) {
        this.shield = shield;
        this.hitpoints = hitpoints;
        this.icon = icon;
    }

    public int getShield() {
        return this.shield;
    }

    public int getHitpoints() {
        return this.hitpoints;
    }

    public int getIcon() {
        return this.icon;
    }

    public String toString() {
        return "Portal." + this.name() + "(shield=" + this.getShield() + ", hitpoints=" + this.getHitpoints() + ", icon=" + this.getIcon() + ")";
    }
}

