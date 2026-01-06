/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.runelite.api.Point
 *  net.runelite.api.coords.WorldPoint
 */
package net.runelite.client.plugins.party;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import net.runelite.api.Point;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.party.PartyMember;
import net.runelite.client.ui.overlay.worldmap.WorldMapPoint;
import net.runelite.client.util.ImageUtil;

class PartyWorldMapPoint
extends WorldMapPoint {
    private static final BufferedImage ARROW = ImageUtil.loadImageResource(PartyWorldMapPoint.class, "/util/clue_arrow.png");
    private BufferedImage partyImage;
    private final PartyMember member;

    PartyWorldMapPoint(WorldPoint worldPoint, PartyMember member) {
        super(worldPoint, null);
        this.member = member;
        this.setSnapToEdge(true);
        this.setJumpOnClick(true);
        this.setImagePoint(new Point(ARROW.getWidth() / 2, ARROW.getHeight()));
    }

    @Override
    public String getName() {
        return this.member.getDisplayName();
    }

    @Override
    public String getTooltip() {
        return this.member.getDisplayName();
    }

    @Override
    public BufferedImage getImage() {
        if (this.partyImage == null && this.member != null && this.member.getAvatar() != null) {
            this.partyImage = new BufferedImage(ARROW.getWidth(), ARROW.getHeight(), 2);
            Graphics g = this.partyImage.getGraphics();
            g.drawImage(ARROW, 0, 0, null);
            g.drawImage(ImageUtil.resizeImage(this.member.getAvatar(), 28, 28), 2, 2, null);
        }
        return this.partyImage;
    }
}

