/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.plugins.pestcontrol;

import net.runelite.client.plugins.pestcontrol.Portal;

enum Rotation {
    PBYR(Portal.PURPLE, Portal.BLUE, Portal.YELLOW, Portal.RED),
    PYBR(Portal.PURPLE, Portal.YELLOW, Portal.BLUE, Portal.RED),
    BRYP(Portal.BLUE, Portal.RED, Portal.YELLOW, Portal.PURPLE),
    BPRY(Portal.BLUE, Portal.PURPLE, Portal.RED, Portal.YELLOW),
    YRPB(Portal.YELLOW, Portal.RED, Portal.PURPLE, Portal.BLUE),
    YPRB(Portal.YELLOW, Portal.PURPLE, Portal.RED, Portal.BLUE);

    private final Portal[] portals;

    private Rotation(Portal first, Portal second, Portal third, Portal fourth) {
        this.portals = new Portal[]{first, second, third, fourth};
    }

    public Portal getPortal(int index) {
        if (index < 0 || index >= this.portals.length) {
            return null;
        }
        return this.portals[index];
    }
}

