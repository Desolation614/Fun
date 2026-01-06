/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.plugins.interfacestyles;

public enum Skin {
    DEFAULT("Default"),
    AROUND_2005("2005"),
    AROUND_2006("2006", AROUND_2005),
    AROUND_2010("2010");

    private String name;
    private Skin extendSkin;

    private Skin(String name) {
        this(name, null);
    }

    public String toString() {
        return this.getName();
    }

    public String getName() {
        return this.name;
    }

    public Skin getExtendSkin() {
        return this.extendSkin;
    }

    private Skin(String name, Skin extendSkin) {
        this.name = name;
        this.extendSkin = extendSkin;
    }
}

