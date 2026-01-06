/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.plugins.party.data;

import java.awt.Color;
import net.runelite.client.ui.overlay.components.PanelComponent;
import net.runelite.client.ui.overlay.worldmap.WorldMapPoint;

public class PartyData {
    private final long memberId;
    private final WorldMapPoint worldMapPoint;
    private final PanelComponent panel = new PanelComponent();
    private Color color = Color.WHITE;
    private int hitpoints;
    private int maxHitpoints;
    private int prayer;
    private int maxPrayer;
    private int runEnergy;
    private int specEnergy;
    private boolean vengeanceActive;

    public void setColor(Color color) {
        this.color = color;
    }

    public void setHitpoints(int hitpoints) {
        this.hitpoints = hitpoints;
    }

    public void setMaxHitpoints(int maxHitpoints) {
        this.maxHitpoints = maxHitpoints;
    }

    public void setPrayer(int prayer) {
        this.prayer = prayer;
    }

    public void setMaxPrayer(int maxPrayer) {
        this.maxPrayer = maxPrayer;
    }

    public void setRunEnergy(int runEnergy) {
        this.runEnergy = runEnergy;
    }

    public void setSpecEnergy(int specEnergy) {
        this.specEnergy = specEnergy;
    }

    public void setVengeanceActive(boolean vengeanceActive) {
        this.vengeanceActive = vengeanceActive;
    }

    public long getMemberId() {
        return this.memberId;
    }

    public WorldMapPoint getWorldMapPoint() {
        return this.worldMapPoint;
    }

    public PanelComponent getPanel() {
        return this.panel;
    }

    public Color getColor() {
        return this.color;
    }

    public int getHitpoints() {
        return this.hitpoints;
    }

    public int getMaxHitpoints() {
        return this.maxHitpoints;
    }

    public int getPrayer() {
        return this.prayer;
    }

    public int getMaxPrayer() {
        return this.maxPrayer;
    }

    public int getRunEnergy() {
        return this.runEnergy;
    }

    public int getSpecEnergy() {
        return this.specEnergy;
    }

    public boolean isVengeanceActive() {
        return this.vengeanceActive;
    }

    public PartyData(long memberId, WorldMapPoint worldMapPoint) {
        this.memberId = memberId;
        this.worldMapPoint = worldMapPoint;
    }
}

