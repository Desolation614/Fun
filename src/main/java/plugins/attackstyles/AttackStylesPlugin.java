/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.collect.HashBasedTable
 *  com.google.common.collect.Table
 *  com.google.inject.Provides
 *  javax.annotation.Nullable
 *  javax.inject.Inject
 *  net.runelite.api.ChatMessageType
 *  net.runelite.api.Client
 *  net.runelite.api.GameState
 *  net.runelite.api.Skill
 *  net.runelite.api.StructComposition
 *  net.runelite.api.events.GameTick
 *  net.runelite.api.events.ScriptPostFired
 *  net.runelite.api.events.VarbitChanged
 *  net.runelite.api.widgets.Widget
 */
package net.runelite.client.plugins.attackstyles;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.google.inject.Provides;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.Set;
import javax.annotation.Nullable;
import javax.inject.Inject;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.Skill;
import net.runelite.api.StructComposition;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.ScriptPostFired;
import net.runelite.api.events.VarbitChanged;
import net.runelite.api.widgets.Widget;
import net.runelite.client.Notifier;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.chat.ChatColorType;
import net.runelite.client.chat.ChatMessageBuilder;
import net.runelite.client.chat.ChatMessageManager;
import net.runelite.client.chat.QueuedMessage;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.attackstyles.AttackStyle;
import net.runelite.client.plugins.attackstyles.AttackStylesConfig;
import net.runelite.client.plugins.attackstyles.AttackStylesOverlay;
import net.runelite.client.ui.overlay.OverlayManager;

@PluginDescriptor(name="Attack Styles", description="Show your current attack style as an overlay", tags={"combat", "defence", "magic", "overlay", "ranged", "strength", "warn", "pure"}, enabledByDefault=false)
public class AttackStylesPlugin
extends Plugin {
    private int equippedWeaponTypeVarbit = -1;
    private AttackStyle attackStyle;
    private AttackStyle prevAttackStyle;
    private final Set<Skill> warnedSkills = EnumSet.noneOf(Skill.class);
    private boolean warnedSkillSelected;
    private final Table<Integer, Integer, Boolean> widgetsToHide = HashBasedTable.create();
    @Inject
    private Client client;
    @Inject
    private ClientThread clientThread;
    @Inject
    private AttackStylesConfig config;
    @Inject
    private OverlayManager overlayManager;
    @Inject
    private AttackStylesOverlay overlay;
    @Inject
    private ChatMessageManager chatManager;
    @Inject
    private Notifier notifier;

    @Provides
    AttackStylesConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(AttackStylesConfig.class);
    }

    @Override
    protected void startUp() throws Exception {
        this.overlayManager.add(this.overlay);
        this.clientThread.invoke(() -> {
            this.resetWarnings();
            if (this.client.getGameState() == GameState.LOGGED_IN) {
                int attackStyleVarbit = this.client.getVarpValue(43);
                this.equippedWeaponTypeVarbit = this.client.getVarbitValue(357);
                int castingModeVarbit = this.client.getVarbitValue(2668);
                this.updateAttackStyle(this.equippedWeaponTypeVarbit, attackStyleVarbit, castingModeVarbit);
                this.updateWarning();
                this.processWidgets();
            }
        });
    }

    @Override
    protected void shutDown() {
        this.overlayManager.remove(this.overlay);
        this.clientThread.invokeLater(() -> {
            this.updateWidgetsToHide(false);
            this.processWidgets();
            AttackStylesPlugin.hideWidget(this.client.getWidget(38862879), false);
        });
        this.warnedSkills.clear();
    }

    @Nullable
    AttackStyle getAttackStyle() {
        return this.attackStyle;
    }

    boolean isWarnedSkillSelected() {
        return this.warnedSkillSelected;
    }

    @Subscribe
    public void onScriptPostFired(ScriptPostFired scriptPostFired) {
        if (scriptPostFired.getScriptId() == 7593) {
            this.processWidgets();
        }
    }

    private void processWidgets() {
        Iterator iterator = this.widgetsToHide.row((Object)this.equippedWeaponTypeVarbit).keySet().iterator();
        while (iterator.hasNext()) {
            int componentId = (Integer)iterator.next();
            AttackStylesPlugin.hideWidget(this.client.getWidget(componentId), (Boolean)this.widgetsToHide.get((Object)this.equippedWeaponTypeVarbit, (Object)componentId));
        }
        AttackStylesPlugin.hideWidget(this.client.getWidget(38862879), this.config.hideAutoRetaliate());
    }

    @Subscribe
    public void onVarbitChanged(VarbitChanged event) {
        if (event.getVarpId() == 43 || event.getVarbitId() == 357 || event.getVarbitId() == 2668) {
            int currentAttackStyleVarbit = this.client.getVarpValue(43);
            int currentEquippedWeaponTypeVarbit = this.client.getVarbitValue(357);
            int currentCastingModeVarbit = this.client.getVarbitValue(2668);
            boolean weaponSwitch = currentEquippedWeaponTypeVarbit != this.equippedWeaponTypeVarbit;
            this.equippedWeaponTypeVarbit = currentEquippedWeaponTypeVarbit;
            this.updateAttackStyle(this.equippedWeaponTypeVarbit, currentAttackStyleVarbit, currentCastingModeVarbit);
            this.updateWarning();
            if (weaponSwitch) {
                this.processWidgets();
            }
        }
    }

    @Subscribe
    public void onConfigChanged(ConfigChanged event) {
        if (event.getGroup().equals("attackIndicator")) {
            boolean enabled = Boolean.TRUE.toString().equals(event.getNewValue());
            this.clientThread.invokeLater(() -> {
                switch (event.getKey()) {
                    case "warnForDefensive": {
                        this.updateWarnedSkills(enabled, Skill.DEFENCE);
                        break;
                    }
                    case "warnForAttack": {
                        this.updateWarnedSkills(enabled, Skill.ATTACK);
                        break;
                    }
                    case "warnForStrength": {
                        this.updateWarnedSkills(enabled, Skill.STRENGTH);
                        break;
                    }
                    case "warnForRanged": {
                        this.updateWarnedSkills(enabled, Skill.RANGED);
                        break;
                    }
                    case "warnForMagic": {
                        this.updateWarnedSkills(enabled, Skill.MAGIC);
                        break;
                    }
                    case "removeWarnedStyles": {
                        this.updateWidgetsToHide(enabled);
                    }
                }
                this.updateWarning();
                this.processWidgets();
            });
        }
    }

    @Subscribe
    public void onGameTick(GameTick gameTick) {
        if (this.attackStyle != this.prevAttackStyle && this.warnedSkillSelected) {
            if (this.config.showChatWarnings()) {
                String message = new ChatMessageBuilder().append(ChatColorType.HIGHLIGHT).append("Your attack style has been changed to " + this.attackStyle.getName()).build();
                this.chatManager.queue(QueuedMessage.builder().type(ChatMessageType.CONSOLE).runeLiteFormattedMessage(message).build());
            }
            this.notifier.notify(this.config.warningNotification(), "Attack style changed to " + this.attackStyle.getName() + "!");
        }
        this.prevAttackStyle = this.attackStyle;
    }

    private void resetWarnings() {
        this.updateWarnedSkills(this.config.warnForAttack(), Skill.ATTACK);
        this.updateWarnedSkills(this.config.warnForStrength(), Skill.STRENGTH);
        this.updateWarnedSkills(this.config.warnForDefence(), Skill.DEFENCE);
        this.updateWarnedSkills(this.config.warnForRanged(), Skill.RANGED);
        this.updateWarnedSkills(this.config.warnForMagic(), Skill.MAGIC);
    }

    private void updateAttackStyle(int equippedWeaponType, int attackStyleIndex, int castingMode) {
        AttackStyle[] attackStyles = this.getWeaponTypeStyles(equippedWeaponType);
        if (attackStyleIndex < attackStyles.length) {
            if (attackStyleIndex == 4) {
                attackStyleIndex += castingMode;
            }
            this.attackStyle = attackStyles[attackStyleIndex];
            if (this.attackStyle == null) {
                this.attackStyle = AttackStyle.OTHER;
            }
        }
    }

    private AttackStyle[] getWeaponTypeStyles(int weaponType) {
        int weaponStyleEnum = this.client.getEnum(3908).getIntValue(weaponType);
        if (weaponStyleEnum == -1) {
            if (weaponType == 22) {
                return new AttackStyle[]{AttackStyle.ACCURATE, AttackStyle.AGGRESSIVE, null, AttackStyle.DEFENSIVE, AttackStyle.CASTING, AttackStyle.DEFENSIVE_CASTING};
            }
            if (weaponType == 30) {
                return new AttackStyle[]{AttackStyle.ACCURATE, AttackStyle.AGGRESSIVE, AttackStyle.AGGRESSIVE, AttackStyle.DEFENSIVE};
            }
            return new AttackStyle[0];
        }
        int[] weaponStyleStructs = this.client.getEnum(weaponStyleEnum).getIntVals();
        AttackStyle[] styles = new AttackStyle[weaponStyleStructs.length];
        int i = 0;
        for (int style : weaponStyleStructs) {
            StructComposition attackStyleStruct = this.client.getStructComposition(style);
            String attackStyleName = attackStyleStruct.getStringValue(1407);
            AttackStyle attackStyle = AttackStyle.valueOf(attackStyleName.toUpperCase());
            if (attackStyle == AttackStyle.OTHER) {
                ++i;
                continue;
            }
            if (i == 5 && attackStyle == AttackStyle.DEFENSIVE) {
                attackStyle = AttackStyle.DEFENSIVE_CASTING;
            }
            styles[i++] = attackStyle;
        }
        return styles;
    }

    private void updateWarnedSkills(boolean enabled, Skill skill) {
        if (enabled) {
            this.warnedSkills.add(skill);
        } else {
            this.warnedSkills.remove(skill);
        }
    }

    private void updateWarning() {
        this.warnedSkillSelected = false;
        if (this.attackStyle != null) {
            for (Skill skill : this.attackStyle.getSkills()) {
                if (!this.warnedSkills.contains(skill)) continue;
                this.warnedSkillSelected = true;
                break;
            }
        }
        this.updateWidgetsToHide(this.config.removeWarnedStyles());
    }

    private void updateWidgetsToHide(boolean enabled) {
        AttackStyle[] attackStyles = this.getWeaponTypeStyles(this.equippedWeaponTypeVarbit);
        block8: for (int i = 0; i < attackStyles.length; ++i) {
            AttackStyle attackStyle = attackStyles[i];
            if (attackStyle == null) continue;
            boolean warnedSkill = false;
            for (Skill skill : attackStyle.getSkills()) {
                if (!this.warnedSkills.contains(skill)) continue;
                warnedSkill = true;
                break;
            }
            switch (i) {
                case 0: {
                    this.widgetsToHide.put((Object)this.equippedWeaponTypeVarbit, (Object)38862853, (Object)(enabled && warnedSkill ? 1 : 0));
                    continue block8;
                }
                case 1: {
                    this.widgetsToHide.put((Object)this.equippedWeaponTypeVarbit, (Object)38862857, (Object)(enabled && warnedSkill ? 1 : 0));
                    continue block8;
                }
                case 2: {
                    this.widgetsToHide.put((Object)this.equippedWeaponTypeVarbit, (Object)38862861, (Object)(enabled && warnedSkill ? 1 : 0));
                    continue block8;
                }
                case 3: {
                    this.widgetsToHide.put((Object)this.equippedWeaponTypeVarbit, (Object)38862865, (Object)(enabled && warnedSkill ? 1 : 0));
                    continue block8;
                }
                case 4: {
                    this.widgetsToHide.put((Object)this.equippedWeaponTypeVarbit, (Object)38862869, (Object)(enabled && warnedSkill ? 1 : 0));
                    continue block8;
                }
                case 5: {
                    this.widgetsToHide.put((Object)this.equippedWeaponTypeVarbit, (Object)38862870, (Object)(enabled && warnedSkill ? 1 : 0));
                    this.widgetsToHide.put((Object)this.equippedWeaponTypeVarbit, (Object)38862872, (Object)(enabled && warnedSkill ? 1 : 0));
                    this.widgetsToHide.put((Object)this.equippedWeaponTypeVarbit, (Object)38862873, (Object)(enabled && warnedSkill ? 1 : 0));
                    this.widgetsToHide.put((Object)this.equippedWeaponTypeVarbit, (Object)38862874, (Object)(enabled && warnedSkill ? 1 : 0));
                }
            }
        }
    }

    private static void hideWidget(Widget widget, boolean hidden) {
        if (widget != null) {
            widget.setHidden(hidden);
        }
    }

    @VisibleForTesting
    Set<Skill> getWarnedSkills() {
        return this.warnedSkills;
    }

    @VisibleForTesting
    Table<Integer, Integer, Boolean> getHiddenWidgets() {
        return this.widgetsToHide;
    }
}

