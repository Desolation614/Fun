/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.inject.Inject
 *  net.runelite.api.Client
 *  net.runelite.api.Skill
 */
package net.runelite.client.plugins.boosts;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.util.Set;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.Skill;
import net.runelite.client.plugins.boosts.BoostsConfig;
import net.runelite.client.plugins.boosts.BoostsPlugin;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.util.ColorUtil;

class BoostsOverlay
extends OverlayPanel {
    private final Client client;
    private final BoostsConfig config;
    private final BoostsPlugin plugin;

    @Inject
    private BoostsOverlay(Client client, BoostsConfig config, BoostsPlugin plugin) {
        super(plugin);
        this.plugin = plugin;
        this.client = client;
        this.config = config;
        this.setPosition(OverlayPosition.TOP_LEFT);
        this.setPriority(0.5f);
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        Set<Skill> boostedSkills = this.plugin.getSkillsToDisplay();
        if (!this.config.displayPanel()) {
            return null;
        }
        int nextChange = this.plugin.getChangeDownTicks();
        if (nextChange != -1) {
            this.panelComponent.getChildren().add(LineComponent.builder().left("Next + restore in").right(String.valueOf(this.plugin.getChangeTime(nextChange))).build());
        }
        if ((nextChange = this.plugin.getChangeUpTicks()) != -1) {
            this.panelComponent.getChildren().add(LineComponent.builder().left("Next - restore in").right(String.valueOf(this.plugin.getChangeTime(nextChange))).build());
        }
        for (Skill skill : boostedSkills) {
            Object str;
            int boosted = this.client.getBoostedSkillLevel(skill);
            int base = this.client.getRealSkillLevel(skill);
            int boost = boosted - base;
            Color strColor = this.getTextColor(boost);
            if (this.config.useRelativeBoost()) {
                str = String.valueOf(boost);
                if (boost > 0) {
                    str = "+" + (String)str;
                }
            } else {
                str = ColorUtil.prependColorTag(Integer.toString(boosted), strColor) + ColorUtil.prependColorTag("/" + base, Color.WHITE);
            }
            this.panelComponent.getChildren().add(LineComponent.builder().left(skill.getName()).right((String)str).rightColor(strColor).build());
        }
        return super.render(graphics);
    }

    private Color getTextColor(int boost) {
        if (boost < 0) {
            return new Color(238, 51, 51);
        }
        return boost <= this.config.boostThreshold() ? Color.YELLOW : Color.GREEN;
    }
}

