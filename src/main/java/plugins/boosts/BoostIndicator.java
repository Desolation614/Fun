/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.runelite.api.Client
 *  net.runelite.api.Skill
 */
package net.runelite.client.plugins.boosts;

import java.awt.Color;
import java.awt.image.BufferedImage;
import net.runelite.api.Client;
import net.runelite.api.Skill;
import net.runelite.client.plugins.boosts.BoostsConfig;
import net.runelite.client.plugins.boosts.BoostsPlugin;
import net.runelite.client.ui.overlay.infobox.InfoBox;
import net.runelite.client.ui.overlay.infobox.InfoBoxPriority;

class BoostIndicator
extends InfoBox {
    private final BoostsPlugin plugin;
    private final BoostsConfig config;
    private final Client client;
    private final Skill skill;

    BoostIndicator(Skill skill, BufferedImage image, BoostsPlugin plugin, Client client, BoostsConfig config) {
        super(image, plugin);
        this.plugin = plugin;
        this.config = config;
        this.client = client;
        this.skill = skill;
        this.setTooltip(skill.getName() + " boost");
        this.setPriority(InfoBoxPriority.HIGH);
    }

    @Override
    public String getText() {
        if (!this.config.useRelativeBoost()) {
            return String.valueOf(this.client.getBoostedSkillLevel(this.skill));
        }
        int boost = this.client.getBoostedSkillLevel(this.skill) - this.client.getRealSkillLevel(this.skill);
        Object text = String.valueOf(boost);
        if (boost > 0) {
            text = "+" + (String)text;
        }
        return text;
    }

    @Override
    public Color getTextColor() {
        int base;
        int boosted = this.client.getBoostedSkillLevel(this.skill);
        if (boosted < (base = this.client.getRealSkillLevel(this.skill))) {
            return new Color(238, 51, 51);
        }
        return boosted - base <= this.config.boostThreshold() ? Color.YELLOW : Color.GREEN;
    }

    @Override
    public boolean render() {
        return this.plugin.getSkillsToDisplay().contains(this.getSkill()) && this.config.displayInfoboxes();
    }

    @Override
    public String getName() {
        return "Boost " + this.skill.getName();
    }

    public Skill getSkill() {
        return this.skill;
    }
}

