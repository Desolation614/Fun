/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.plugins.specialcounter;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;
import net.runelite.client.plugins.specialcounter.SpecialCounterConfig;
import net.runelite.client.plugins.specialcounter.SpecialCounterPlugin;
import net.runelite.client.plugins.specialcounter.SpecialWeapon;
import net.runelite.client.ui.overlay.infobox.Counter;

class SpecialCounter
extends Counter {
    private final SpecialWeapon weapon;
    private final SpecialCounterConfig config;
    private final Map<String, Integer> partySpecs = new HashMap<String, Integer>();

    SpecialCounter(BufferedImage image, SpecialCounterPlugin plugin, SpecialCounterConfig config, int hitValue, SpecialWeapon weapon) {
        super(image, plugin, hitValue);
        this.weapon = weapon;
        this.config = config;
    }

    void addHits(int hit) {
        int count = this.getCount();
        this.setCount(count + hit);
    }

    @Override
    public String getTooltip() {
        int hitValue = this.getCount();
        if (this.partySpecs.isEmpty()) {
            return this.buildTooltip(hitValue);
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(this.buildTooltip(hitValue));
        for (Map.Entry<String, Integer> entry : this.partySpecs.entrySet()) {
            stringBuilder.append("</br>").append(entry.getKey() == null ? "You" : entry.getKey()).append(": ").append(this.buildTooltip(entry.getValue()));
        }
        return stringBuilder.toString();
    }

    private String buildTooltip(int hitValue) {
        if (!this.weapon.isDamage()) {
            if (hitValue == 1) {
                return this.weapon.getName() + " special has hit " + hitValue + " time.";
            }
            return this.weapon.getName() + " special has hit " + hitValue + " times.";
        }
        return this.weapon.getName() + " special has hit " + hitValue + " total.";
    }

    @Override
    public Color getTextColor() {
        int threshold = this.weapon.getThreshold().apply(this.config);
        if (threshold > 0) {
            int count = this.getCount();
            return count >= threshold ? Color.GREEN : Color.RED;
        }
        return super.getTextColor();
    }

    Map<String, Integer> getPartySpecs() {
        return this.partySpecs;
    }
}

