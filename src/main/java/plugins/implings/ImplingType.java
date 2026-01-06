/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.plugins.implings;

enum ImplingType {
    BABY("Baby"),
    YOUNG("Young"),
    GOURMET("Gourmet"),
    EARTH("Earth"),
    ESSENCE("Essence"),
    ECLECTIC("Eclectic"),
    NATURE("Nature"),
    MAGPIE("Magpie"),
    NINJA("Ninja"),
    CRYSTAL("Crystal"),
    DRAGON("Dragon"),
    LUCKY("Lucky");

    private final String name;

    private ImplingType(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }
}

