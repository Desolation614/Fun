/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.annotations.VisibleForTesting
 *  javax.inject.Inject
 *  net.runelite.api.Client
 *  net.runelite.api.Experience
 *  net.runelite.api.Skill
 *  net.runelite.api.widgets.Widget
 */
package net.runelite.client.plugins.combatlevel;

import com.google.common.annotations.VisibleForTesting;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.Experience;
import net.runelite.api.Skill;
import net.runelite.api.widgets.Widget;
import net.runelite.client.plugins.combatlevel.CombatLevelConfig;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.tooltip.Tooltip;
import net.runelite.client.ui.overlay.tooltip.TooltipManager;
import net.runelite.client.util.ColorUtil;

class CombatLevelOverlay
extends Overlay {
    private static final Color COMBAT_LEVEL_COLOUR = new Color(16750623);
    private final Client client;
    private final CombatLevelConfig config;
    private final TooltipManager tooltipManager;

    @Inject
    private CombatLevelOverlay(Client client, CombatLevelConfig config, TooltipManager tooltipManager) {
        this.client = client;
        this.config = config;
        this.tooltipManager = tooltipManager;
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        if (!this.config.showLevelsUntil() || this.client.getLocalPlayer().getCombatLevel() == 126) {
            return null;
        }
        Widget combatTabLevelWidget = this.client.getWidget(38862852);
        Widget characterTabWidget = this.client.getWidget(46661634);
        Rectangle combatCanvas = null;
        if (combatTabLevelWidget != null && !combatTabLevelWidget.isHidden()) {
            combatCanvas = combatTabLevelWidget.getBounds();
        } else if (characterTabWidget != null && !characterTabWidget.isHidden()) {
            combatCanvas = characterTabWidget.getChild(9).getBounds();
        }
        if (combatCanvas == null) {
            return null;
        }
        if (combatCanvas.contains(this.client.getMouseCanvasPosition().getX(), this.client.getMouseCanvasPosition().getY())) {
            this.tooltipManager.add(new Tooltip(this.getLevelsUntilTooltip()));
        }
        return null;
    }

    @VisibleForTesting
    String getLevelsUntilTooltip() {
        int attackLevel = this.client.getRealSkillLevel(Skill.ATTACK);
        int strengthLevel = this.client.getRealSkillLevel(Skill.STRENGTH);
        int defenceLevel = this.client.getRealSkillLevel(Skill.DEFENCE);
        int hitpointsLevel = this.client.getRealSkillLevel(Skill.HITPOINTS);
        int magicLevel = this.client.getRealSkillLevel(Skill.MAGIC);
        int rangeLevel = this.client.getRealSkillLevel(Skill.RANGED);
        int prayerLevel = this.client.getRealSkillLevel(Skill.PRAYER);
        int meleeNeed = Experience.getNextCombatLevelMelee((int)attackLevel, (int)strengthLevel, (int)defenceLevel, (int)hitpointsLevel, (int)magicLevel, (int)rangeLevel, (int)prayerLevel);
        int hpDefNeed = Experience.getNextCombatLevelHpDef((int)attackLevel, (int)strengthLevel, (int)defenceLevel, (int)hitpointsLevel, (int)magicLevel, (int)rangeLevel, (int)prayerLevel);
        int rangeNeed = Experience.getNextCombatLevelRange((int)attackLevel, (int)strengthLevel, (int)defenceLevel, (int)hitpointsLevel, (int)magicLevel, (int)rangeLevel, (int)prayerLevel);
        int magicNeed = Experience.getNextCombatLevelMagic((int)attackLevel, (int)strengthLevel, (int)defenceLevel, (int)hitpointsLevel, (int)magicLevel, (int)rangeLevel, (int)prayerLevel);
        int prayerNeed = Experience.getNextCombatLevelPrayer((int)attackLevel, (int)strengthLevel, (int)defenceLevel, (int)hitpointsLevel, (int)magicLevel, (int)rangeLevel, (int)prayerLevel);
        StringBuilder sb = new StringBuilder();
        sb.append(ColorUtil.wrapWithColorTag("Next combat level:</br>", COMBAT_LEVEL_COLOUR));
        if (attackLevel + strengthLevel < 198) {
            sb.append(meleeNeed).append(" Attack/Strength</br>");
        }
        if (hitpointsLevel + defenceLevel < 198) {
            sb.append(hpDefNeed).append(" Defence/Hitpoints</br>");
        }
        if (rangeLevel < 99) {
            sb.append(rangeNeed).append(" Ranged</br>");
        }
        if (magicLevel < 99) {
            sb.append(magicNeed).append(" Magic</br>");
        }
        if (prayerLevel < 99) {
            sb.append(prayerNeed).append(" Prayer");
        }
        return sb.toString();
    }
}

