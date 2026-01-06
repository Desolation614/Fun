/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.inject.Inject
 *  net.runelite.api.Client
 *  net.runelite.api.Perspective
 *  net.runelite.api.Point
 *  net.runelite.api.coords.LocalPoint
 */
package net.runelite.client.plugins.poh;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.Perspective;
import net.runelite.api.Point;
import net.runelite.api.coords.LocalPoint;
import net.runelite.client.plugins.poh.PohConfig;
import net.runelite.client.plugins.poh.PohIcons;
import net.runelite.client.plugins.poh.PohPlugin;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;

public class PohOverlay
extends Overlay {
    private static final PohIcons[] PORTALS = new PohIcons[]{PohIcons.LUMBRIDGE, PohIcons.FALADOR, PohIcons.VARROCK, PohIcons.CAMELOT, PohIcons.ARDOUGNE, PohIcons.YANILLE, PohIcons.LUNARISLE, PohIcons.WATERBIRTH, PohIcons.FISHINGGUILD, PohIcons.SENNTISTEN, PohIcons.KHARYLL, PohIcons.ANNAKARL, PohIcons.KOUREND, PohIcons.MARIM, PohIcons.TROLLSTRONGHOLD, PohIcons.CARRALLANGER, PohIcons.CATHERBY, PohIcons.WEISS, PohIcons.GHORROCK, PohIcons.APEATOLLDUNGEON, PohIcons.BARROWS, PohIcons.BATTLEFRONT, PohIcons.CEMETERY, PohIcons.DRAYNORMANOR, PohIcons.FENKENSTRAINSCASTLE, PohIcons.HARMONYISLAND, PohIcons.ARCEUUSLIBRARY, PohIcons.MINDALTAR, PohIcons.SALVEGRAVEYARD, PohIcons.WESTARDOUGNE, PohIcons.CIVITASILLAFORTIS};
    private static final int MAX_DISTANCE = 2350;
    private final List<PohIcons> iconList = new ArrayList<PohIcons>();
    private final Client client;
    private final PohConfig config;
    private final PohPlugin plugin;

    @Inject
    public PohOverlay(Client client, PohConfig config, PohPlugin plugin) {
        this.setPosition(OverlayPosition.DYNAMIC);
        this.setLayer(OverlayLayer.ABOVE_WIDGETS);
        this.client = client;
        this.config = config;
        this.plugin = plugin;
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        LocalPoint localLocation = this.client.getLocalPlayer().getLocalLocation();
        this.plugin.getPohObjects().forEach((object, tile) -> {
            Point minimapLoc;
            PohIcons icon;
            LocalPoint location = object.getLocalLocation();
            if (tile.getPlane() == this.client.getPlane() && localLocation.distanceTo(location) <= 2350 && (icon = PohIcons.getIcon(object.getId())) != null && this.iconList.contains((Object)icon) && (minimapLoc = Perspective.getMiniMapImageLocation((Client)this.client, (LocalPoint)object.getLocalLocation(), (BufferedImage)icon.getImage())) != null) {
                graphics.drawImage((Image)icon.getImage(), minimapLoc.getX(), minimapLoc.getY(), null);
            }
        });
        return null;
    }

    public void updateConfig() {
        this.iconList.clear();
        if (this.config.showPortals()) {
            Collections.addAll(this.iconList, PORTALS);
        }
        if (this.config.showAltar()) {
            this.iconList.add(PohIcons.ALTAR);
        }
        if (this.config.showGlory()) {
            this.iconList.add(PohIcons.GLORY);
        }
        if (this.config.showRepairStand()) {
            this.iconList.add(PohIcons.REPAIR);
        }
        if (this.config.showPools()) {
            this.iconList.add(PohIcons.POOLS);
        }
        if (this.config.showExitPortal()) {
            this.iconList.add(PohIcons.EXITPORTAL);
        }
        if (this.config.showSpellbook()) {
            this.iconList.add(PohIcons.SPELLBOOKALTAR);
        }
        if (this.config.showJewelleryBox()) {
            this.iconList.add(PohIcons.JEWELLERYBOX);
        }
        if (this.config.showMagicTravel()) {
            this.iconList.add(PohIcons.MAGICTRAVEL);
        }
        if (this.config.showPortalNexus()) {
            this.iconList.add(PohIcons.PORTALNEXUS);
        }
        if (this.config.showDigsitePendant()) {
            this.iconList.add(PohIcons.DIGSITEPENDANT);
        }
        if (this.config.showXericsTalisman()) {
            this.iconList.add(PohIcons.XERICSTALISMAN);
        }
        if (this.config.showMythicalCape()) {
            this.iconList.add(PohIcons.MYTHICALCAPE);
        }
    }

    public List<PohIcons> getIconList() {
        return this.iconList;
    }
}

