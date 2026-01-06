/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.plugins.menuentryswapper;

public enum HouseMode {
    ENTER("Enter"),
    HOME("Home"),
    BUILD_MODE("Build mode"),
    FRIENDS_HOUSE("Friend's House");

    private final String name;

    public String toString() {
        return this.name;
    }

    public String getName() {
        return this.name;
    }

    private HouseMode(String name) {
        this.name = name;
    }
}

