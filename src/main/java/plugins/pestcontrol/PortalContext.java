/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.plugins.pestcontrol;

import net.runelite.client.plugins.pestcontrol.Portal;

class PortalContext {
    private final Portal portal;
    private boolean isShielded = true;
    private boolean isDead;

    public PortalContext(Portal portal) {
        this.portal = portal;
    }

    public Portal getPortal() {
        return this.portal;
    }

    public boolean isShielded() {
        return this.isShielded;
    }

    public boolean isDead() {
        return this.isDead;
    }

    public void setShielded(boolean isShielded) {
        this.isShielded = isShielded;
    }

    public void setDead(boolean isDead) {
        this.isDead = isDead;
    }
}

