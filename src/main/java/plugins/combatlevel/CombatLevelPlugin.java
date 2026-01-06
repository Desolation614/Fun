/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.inject.Provides
 *  javax.inject.Inject
 *  net.runelite.api.Client
 *  net.runelite.api.Experience
 *  net.runelite.api.Skill
 *  net.runelite.api.WorldType
 *  net.runelite.api.events.ScriptPostFired
 *  net.runelite.api.events.ScriptPreFired
 *  net.runelite.api.events.StatChanged
 *  net.runelite.api.widgets.Widget
 */
package net.runelite.client.plugins.combatlevel;

import com.google.inject.Provides;
import java.awt.Color;
import java.text.DecimalFormat;
import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.Experience;
import net.runelite.api.Skill;
import net.runelite.api.WorldType;
import net.runelite.api.events.ScriptPostFired;
import net.runelite.api.events.ScriptPreFired;
import net.runelite.api.events.StatChanged;
import net.runelite.api.widgets.Widget;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.combatlevel.CombatLevelConfig;
import net.runelite.client.plugins.combatlevel.CombatLevelOverlay;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.util.ColorUtil;
import net.runelite.client.util.Text;

@PluginDescriptor(name="Combat Level", description="Show a more accurate combat level in Combat Options panel and other combat level functions", tags={"wilderness", "attack", "range"})
public class CombatLevelPlugin
extends Plugin {
    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#.###");
    private static final String CONFIG_GROUP = "combatlevel";
    private static final String ATTACK_RANGE_CONFIG_KEY = "wildernessAttackLevelRange";
    private static final Pattern WILDERNESS_LEVEL_PATTERN = Pattern.compile("^Level: (\\d+)$");
    private static final int MIN_COMBAT_LEVEL = 3;
    private static final String COMBAT_LEVEL_SECTION_TEXT = "Combat Level:";
    private static final Color CHARACTER_SUMMARY_GREEN = Color.decode("#0dc10d");
    @Inject
    private Client client;
    @Inject
    private CombatLevelConfig config;
    @Inject
    private CombatLevelOverlay overlay;
    @Inject
    private OverlayManager overlayManager;
    private String combatLevelStr;

    @Provides
    CombatLevelConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(CombatLevelConfig.class);
    }

    @Override
    protected void startUp() throws Exception {
        double combatLevel = Experience.getCombatLevelPrecise((int)this.client.getRealSkillLevel(Skill.ATTACK), (int)this.client.getRealSkillLevel(Skill.STRENGTH), (int)this.client.getRealSkillLevel(Skill.DEFENCE), (int)this.client.getRealSkillLevel(Skill.HITPOINTS), (int)this.client.getRealSkillLevel(Skill.MAGIC), (int)this.client.getRealSkillLevel(Skill.RANGED), (int)this.client.getRealSkillLevel(Skill.PRAYER));
        this.combatLevelStr = DECIMAL_FORMAT.format(combatLevel);
        this.overlayManager.add(this.overlay);
        if (this.config.wildernessAttackLevelRange()) {
            this.appendAttackLevelRangeText();
        }
    }

    @Override
    protected void shutDown() throws Exception {
        String widgetText;
        this.overlayManager.remove(this.overlay);
        Widget combatLevelWidget = this.client.getWidget(38862852);
        if (combatLevelWidget != null && (widgetText = combatLevelWidget.getText()).contains(".")) {
            combatLevelWidget.setText(widgetText.substring(0, widgetText.indexOf(".")));
        }
        this.shutDownAttackLevelRange();
    }

    @Subscribe
    private void onStatChanged(StatChanged statChanged) {
        Skill skill = statChanged.getSkill();
        if (skill == Skill.ATTACK || skill == Skill.DEFENCE || skill == Skill.STRENGTH || skill == Skill.HITPOINTS || skill == Skill.MAGIC || skill == Skill.PRAYER || skill == Skill.RANGED) {
            double combatLevel = Experience.getCombatLevelPrecise((int)this.client.getRealSkillLevel(Skill.ATTACK), (int)this.client.getRealSkillLevel(Skill.STRENGTH), (int)this.client.getRealSkillLevel(Skill.DEFENCE), (int)this.client.getRealSkillLevel(Skill.HITPOINTS), (int)this.client.getRealSkillLevel(Skill.MAGIC), (int)this.client.getRealSkillLevel(Skill.RANGED), (int)this.client.getRealSkillLevel(Skill.PRAYER));
            this.combatLevelStr = DECIMAL_FORMAT.format(combatLevel);
        }
    }

    @Subscribe
    public void onConfigChanged(ConfigChanged event) {
        if (!CONFIG_GROUP.equals(event.getGroup()) || !ATTACK_RANGE_CONFIG_KEY.equals(event.getKey())) {
            return;
        }
        if (this.config.wildernessAttackLevelRange()) {
            this.appendAttackLevelRangeText();
        } else {
            this.shutDownAttackLevelRange();
        }
    }

    @Subscribe
    public void onScriptPreFired(ScriptPreFired scriptPreFired) {
        int scriptId = scriptPreFired.getScriptId();
        if (scriptId != 3948 && scriptId != 3950 || !this.config.showPreciseCombatLevel()) {
            return;
        }
        Object[] objectStack = this.client.getObjectStack();
        int objectStackSize = this.client.getObjectStackSize();
        if (scriptId == 3948) {
            String levelText = Text.removeTags((String)objectStack[objectStackSize - 1]);
            if (this.client.getLocalPlayer().getCombatLevel() != Integer.parseInt(levelText)) {
                return;
            }
        } else {
            String sectionText = Text.removeTags((String)objectStack[objectStackSize - 3]);
            if (!COMBAT_LEVEL_SECTION_TEXT.equals(sectionText)) {
                return;
            }
        }
        double combatLevelPrecise = Experience.getCombatLevelPrecise((int)this.client.getRealSkillLevel(Skill.ATTACK), (int)this.client.getRealSkillLevel(Skill.STRENGTH), (int)this.client.getRealSkillLevel(Skill.DEFENCE), (int)this.client.getRealSkillLevel(Skill.HITPOINTS), (int)this.client.getRealSkillLevel(Skill.MAGIC), (int)this.client.getRealSkillLevel(Skill.RANGED), (int)this.client.getRealSkillLevel(Skill.PRAYER));
        objectStack[objectStackSize - 1] = ColorUtil.wrapWithColorTag(DECIMAL_FORMAT.format(combatLevelPrecise), CHARACTER_SUMMARY_GREEN);
    }

    @Subscribe
    public void onScriptPostFired(ScriptPostFired scriptPostFired) {
        if (scriptPostFired.getScriptId() == 388 && this.config.wildernessAttackLevelRange()) {
            this.appendAttackLevelRangeText();
        }
        if (scriptPostFired.getScriptId() == 7593 && this.config.showPreciseCombatLevel()) {
            Widget combatLevelWidget = this.client.getWidget(38862852);
            combatLevelWidget.setText("Combat Lvl: " + this.combatLevelStr);
        }
    }

    private void appendAttackLevelRangeText() {
        Widget wildernessLevelWidget = this.client.getWidget(5898290);
        if (wildernessLevelWidget == null) {
            return;
        }
        String wildernessLevelText = wildernessLevelWidget.getText();
        Matcher m = WILDERNESS_LEVEL_PATTERN.matcher(wildernessLevelText);
        if (!m.matches() || WorldType.isPvpWorld((Collection)this.client.getWorldType())) {
            return;
        }
        int wildernessLevel = Integer.parseInt(m.group(1));
        int combatLevel = this.client.getLocalPlayer().getCombatLevel();
        wildernessLevelWidget.setText(wildernessLevelText + "<br>" + CombatLevelPlugin.combatAttackRange(combatLevel, wildernessLevel));
    }

    private void shutDownAttackLevelRange() {
        String wildernessLevelText;
        if (WorldType.isPvpWorld((Collection)this.client.getWorldType())) {
            return;
        }
        Widget wildernessLevelWidget = this.client.getWidget(5898290);
        if (wildernessLevelWidget != null && (wildernessLevelText = wildernessLevelWidget.getText()).contains("<br>")) {
            wildernessLevelWidget.setText(wildernessLevelText.substring(0, wildernessLevelText.indexOf("<br>")));
        }
    }

    private static String combatAttackRange(int combatLevel, int wildernessLevel) {
        return Math.max(3, combatLevel - wildernessLevel) + "-" + Math.min(126, combatLevel + wildernessLevel);
    }
}

