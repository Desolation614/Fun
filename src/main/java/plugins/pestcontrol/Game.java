/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package net.runelite.client.plugins.pestcontrol;

import java.util.ArrayList;
import java.util.Collection;
import net.runelite.client.plugins.pestcontrol.Portal;
import net.runelite.client.plugins.pestcontrol.PortalContext;
import net.runelite.client.plugins.pestcontrol.Rotation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class Game {
    private static final Logger log = LoggerFactory.getLogger(Game.class);
    private Rotation[] possibleRotations = Rotation.values();
    private int shieldsDropped;
    private final PortalContext purple = new PortalContext(Portal.PURPLE);
    private final PortalContext blue = new PortalContext(Portal.BLUE);
    private final PortalContext yellow = new PortalContext(Portal.YELLOW);
    private final PortalContext red = new PortalContext(Portal.RED);

    Game() {
    }

    void fall(String color) {
        switch (color.toLowerCase()) {
            case "purple": {
                this.fall(this.purple);
                break;
            }
            case "red": {
                this.fall(this.red);
                break;
            }
            case "yellow": {
                this.fall(this.yellow);
                break;
            }
            case "blue": {
                this.fall(this.blue);
            }
        }
    }

    private void fall(PortalContext portal) {
        if (!portal.isShielded()) {
            return;
        }
        log.debug("Shield dropped for {}", (Object)portal.getPortal());
        portal.setShielded(false);
        int shieldDrop = this.shieldsDropped++;
        ArrayList<Rotation> rotations = new ArrayList<Rotation>();
        for (Rotation rotation : this.possibleRotations) {
            if (rotation.getPortal(shieldDrop) != portal.getPortal()) continue;
            rotations.add(rotation);
        }
        this.possibleRotations = rotations.toArray(new Rotation[rotations.size()]);
    }

    void die(PortalContext portal) {
        if (portal.isDead()) {
            return;
        }
        log.debug("Portal {} died", (Object)portal.getPortal());
        portal.setDead(true);
    }

    Collection<Portal> getNextPortals() {
        ArrayList<Portal> portals = new ArrayList<Portal>();
        for (Rotation rotation : this.possibleRotations) {
            Portal portal = rotation.getPortal(this.shieldsDropped);
            if (portal == null || portals.contains((Object)portal)) continue;
            portals.add(portal);
        }
        return portals;
    }

    public PortalContext getPurple() {
        return this.purple;
    }

    public PortalContext getBlue() {
        return this.blue;
    }

    public PortalContext getYellow() {
        return this.yellow;
    }

    public PortalContext getRed() {
        return this.red;
    }
}

