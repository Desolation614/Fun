/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.plugins.barbarianassault;

enum Role {
    ATTACKER(31784972, 31784971),
    DEFENDER(31916043, 31916042),
    COLLECTOR(31850507, 31850506),
    HEALER(31981579, 31981578);

    private final int roleText;
    private final int roleSprite;

    public String toString() {
        return this.name();
    }

    private Role(int roleText, int roleSprite) {
        this.roleText = roleText;
        this.roleSprite = roleSprite;
    }

    public int getRoleText() {
        return this.roleText;
    }

    public int getRoleSprite() {
        return this.roleSprite;
    }
}

