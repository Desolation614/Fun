/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.inject.Inject
 *  net.runelite.api.Actor
 *  net.runelite.api.Client
 *  net.runelite.api.MenuAction
 *  net.runelite.api.Player
 *  net.runelite.api.Skill
 */
package net.runelite.client.plugins.opponentinfo;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import javax.inject.Inject;
import net.runelite.api.Actor;
import net.runelite.api.Client;
import net.runelite.api.MenuAction;
import net.runelite.api.Player;
import net.runelite.api.Skill;
import net.runelite.client.hiscore.HiscoreManager;
import net.runelite.client.hiscore.HiscoreResult;
import net.runelite.client.hiscore.HiscoreSkill;
import net.runelite.client.plugins.opponentinfo.OpponentInfoConfig;
import net.runelite.client.plugins.opponentinfo.OpponentInfoPlugin;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.PanelComponent;
import net.runelite.client.ui.overlay.components.TitleComponent;
import net.runelite.client.util.Text;

class PlayerComparisonOverlay
extends Overlay {
    private static final Color HIGHER_STAT_TEXT_COLOR = Color.GREEN;
    private static final Color LOWER_STAT_TEXT_COLOR = Color.RED;
    private static final Color NEUTRAL_TEXT_COLOR = Color.WHITE;
    private static final Color HIGHLIGHT_COLOR = new Color(255, 200, 0, 255);
    private static final Skill[] COMBAT_SKILLS = new Skill[]{Skill.ATTACK, Skill.STRENGTH, Skill.DEFENCE, Skill.HITPOINTS, Skill.RANGED, Skill.MAGIC, Skill.PRAYER};
    private static final HiscoreSkill[] HISCORE_COMBAT_SKILLS = new HiscoreSkill[]{HiscoreSkill.ATTACK, HiscoreSkill.STRENGTH, HiscoreSkill.DEFENCE, HiscoreSkill.HITPOINTS, HiscoreSkill.RANGED, HiscoreSkill.MAGIC, HiscoreSkill.PRAYER};
    private static final String LEFT_COLUMN_HEADER = "Skill";
    private static final String RIGHT_COLUMN_HEADER = "You/Them";
    private final Client client;
    private final OpponentInfoPlugin opponentInfoPlugin;
    private final OpponentInfoConfig config;
    private final HiscoreManager hiscoreManager;
    private final PanelComponent panelComponent = new PanelComponent();

    @Inject
    private PlayerComparisonOverlay(Client client, OpponentInfoPlugin opponentInfoPlugin, OpponentInfoConfig config, HiscoreManager hiscoreManager) {
        super(opponentInfoPlugin);
        this.client = client;
        this.opponentInfoPlugin = opponentInfoPlugin;
        this.config = config;
        this.hiscoreManager = hiscoreManager;
        this.setPosition(OverlayPosition.BOTTOM_LEFT);
        this.setLayer(OverlayLayer.ABOVE_WIDGETS);
        this.addMenuEntry(MenuAction.RUNELITE_OVERLAY_CONFIG, "Configure", "Opponent info overlay");
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        if (!this.config.lookupOnInteraction()) {
            return null;
        }
        Actor opponent = this.opponentInfoPlugin.getLastOpponent();
        if (opponent == null) {
            return null;
        }
        if (!(opponent instanceof Player)) {
            return null;
        }
        String opponentName = Text.removeTags(opponent.getName());
        HiscoreResult hiscoreResult = this.hiscoreManager.lookupAsync(opponentName, this.opponentInfoPlugin.getHiscoreEndpoint());
        if (hiscoreResult == null) {
            return null;
        }
        this.panelComponent.getChildren().clear();
        this.generateComparisonTable(this.panelComponent, hiscoreResult);
        return this.panelComponent.render(graphics);
    }

    private void generateComparisonTable(PanelComponent panelComponent, HiscoreResult opponentSkills) {
        String opponentName = opponentSkills.getPlayer();
        panelComponent.getChildren().add(TitleComponent.builder().text(opponentName).color(HIGHLIGHT_COLOR).build());
        panelComponent.getChildren().add(LineComponent.builder().left(LEFT_COLUMN_HEADER).leftColor(HIGHLIGHT_COLOR).right(RIGHT_COLUMN_HEADER).rightColor(HIGHLIGHT_COLOR).build());
        for (int i = 0; i < COMBAT_SKILLS.length; ++i) {
            HiscoreSkill hiscoreSkill = HISCORE_COMBAT_SKILLS[i];
            Skill skill = COMBAT_SKILLS[i];
            net.runelite.client.hiscore.Skill opponentSkill = opponentSkills.getSkill(hiscoreSkill);
            if (opponentSkill == null || opponentSkill.getLevel() == -1) continue;
            int playerSkillLevel = this.client.getRealSkillLevel(skill);
            int opponentSkillLevel = opponentSkill.getLevel();
            panelComponent.getChildren().add(LineComponent.builder().left(hiscoreSkill.getName()).right(Integer.toString(playerSkillLevel) + "/" + Integer.toString(opponentSkillLevel)).rightColor(PlayerComparisonOverlay.comparisonStatColor(playerSkillLevel, opponentSkillLevel)).build());
        }
    }

    private static Color comparisonStatColor(int a, int b) {
        if (a > b) {
            return HIGHER_STAT_TEXT_COLOR;
        }
        if (a < b) {
            return LOWER_STAT_TEXT_COLOR;
        }
        return NEUTRAL_TEXT_COLOR;
    }
}

