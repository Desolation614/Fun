/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.inject.Inject
 *  net.runelite.api.MenuAction
 */
package net.runelite.client.plugins.attackstyles;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import javax.inject.Inject;
import net.runelite.api.MenuAction;
import net.runelite.client.plugins.attackstyles.AttackStyle;
import net.runelite.client.plugins.attackstyles.AttackStylesConfig;
import net.runelite.client.plugins.attackstyles.AttackStylesPlugin;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.TitleComponent;

class AttackStylesOverlay
extends OverlayPanel {
    private final AttackStylesPlugin plugin;
    private final AttackStylesConfig config;

    @Inject
    private AttackStylesOverlay(AttackStylesPlugin plugin, AttackStylesConfig config) {
        super(plugin);
        this.setPosition(OverlayPosition.ABOVE_CHATBOX_RIGHT);
        this.plugin = plugin;
        this.config = config;
        this.addMenuEntry(MenuAction.RUNELITE_OVERLAY_CONFIG, "Configure", "Attack style overlay");
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        boolean warnedSkillSelected = this.plugin.isWarnedSkillSelected();
        if (warnedSkillSelected || this.config.alwaysShowStyle()) {
            AttackStyle attackStyle = this.plugin.getAttackStyle();
            if (attackStyle == null) {
                return null;
            }
            String attackStyleString = attackStyle.getName();
            this.panelComponent.getChildren().add(TitleComponent.builder().text(attackStyleString).color(warnedSkillSelected ? Color.RED : Color.WHITE).build());
            this.panelComponent.setPreferredSize(new Dimension(graphics.getFontMetrics().stringWidth(attackStyleString) + 10, 0));
            return super.render(graphics);
        }
        return null;
    }
}

