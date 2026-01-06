/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableSet
 *  com.google.inject.Provides
 *  javax.inject.Inject
 *  javax.inject.Singleton
 *  net.runelite.api.Client
 *  net.runelite.api.MenuAction
 *  net.runelite.api.Prayer
 *  net.runelite.api.Skill
 *  net.runelite.api.events.GameStateChanged
 *  net.runelite.api.events.GameTick
 *  net.runelite.api.events.StatChanged
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package net.runelite.client.plugins.boosts;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Provides;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.Set;
import javax.inject.Inject;
import javax.inject.Singleton;
import net.runelite.api.Client;
import net.runelite.api.MenuAction;
import net.runelite.api.Prayer;
import net.runelite.api.Skill;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.StatChanged;
import net.runelite.client.Notifier;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.config.Notification;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.game.SkillIconManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.boosts.BoostIndicator;
import net.runelite.client.plugins.boosts.BoostsConfig;
import net.runelite.client.plugins.boosts.BoostsOverlay;
import net.runelite.client.plugins.boosts.CompactBoostsOverlay;
import net.runelite.client.plugins.boosts.StatChangeIndicator;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.ui.overlay.OverlayMenuEntry;
import net.runelite.client.ui.overlay.infobox.InfoBoxManager;
import net.runelite.client.util.ImageUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@PluginDescriptor(name="Boosts Information", description="Show combat and/or skill boost information", tags={"combat", "notifications", "skilling", "overlay"})
@Singleton
public class BoostsPlugin
extends Plugin {
    private static final Logger log = LoggerFactory.getLogger(BoostsPlugin.class);
    private static final Set<Skill> BOOSTABLE_COMBAT_SKILLS = ImmutableSet.of((Object)Skill.ATTACK, (Object)Skill.STRENGTH, (Object)Skill.DEFENCE, (Object)Skill.RANGED, (Object)Skill.MAGIC);
    private static final Set<Skill> BOOSTABLE_NON_COMBAT_SKILLS = ImmutableSet.of((Object)Skill.MINING, (Object)Skill.AGILITY, (Object)Skill.SMITHING, (Object)Skill.HERBLORE, (Object)Skill.FISHING, (Object)Skill.THIEVING, (Object[])new Skill[]{Skill.COOKING, Skill.CRAFTING, Skill.FIREMAKING, Skill.FLETCHING, Skill.WOODCUTTING, Skill.RUNECRAFT, Skill.SLAYER, Skill.FARMING, Skill.CONSTRUCTION, Skill.HUNTER});
    @Inject
    private Notifier notifier;
    @Inject
    private Client client;
    @Inject
    private InfoBoxManager infoBoxManager;
    @Inject
    private OverlayManager overlayManager;
    @Inject
    private BoostsOverlay boostsOverlay;
    @Inject
    private CompactBoostsOverlay compactBoostsOverlay;
    @Inject
    private BoostsConfig config;
    @Inject
    private ConfigManager configManager;
    @Inject
    private SkillIconManager skillIconManager;
    private final Set<Skill> skillsToDisplay = EnumSet.noneOf(Skill.class);
    private final Set<Skill> shownSkills = EnumSet.noneOf(Skill.class);
    private boolean isChangedDown = false;
    private boolean isChangedUp = false;
    private final int[] lastSkillLevels = new int[Skill.values().length];
    private int lastChangeDown = -1;
    private int lastChangeUp = -1;
    private boolean preserveBeenActive = false;
    private long lastTickMillis;

    @Provides
    BoostsConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(BoostsConfig.class);
    }

    @Override
    protected void startUp() throws Exception {
        this.convertConfig();
        OverlayMenuEntry menuEntry = new OverlayMenuEntry(MenuAction.RUNELITE_OVERLAY_CONFIG, "Configure", "Boosts overlay");
        this.boostsOverlay.getMenuEntries().add(menuEntry);
        this.compactBoostsOverlay.getMenuEntries().add(menuEntry);
        this.overlayManager.add(this.boostsOverlay);
        this.overlayManager.add(this.compactBoostsOverlay);
        this.updateShownSkills();
        Arrays.fill(this.lastSkillLevels, -1);
        this.infoBoxManager.addInfoBox(new StatChangeIndicator(true, ImageUtil.loadImageResource(this.getClass(), "debuffed.png"), this, this.config));
        this.infoBoxManager.addInfoBox(new StatChangeIndicator(false, ImageUtil.loadImageResource(this.getClass(), "buffed.png"), this, this.config));
        for (Skill skill : Skill.values()) {
            this.infoBoxManager.addInfoBox(new BoostIndicator(skill, this.skillIconManager.getSkillImage(skill), this, this.client, this.config));
        }
    }

    @Override
    protected void shutDown() throws Exception {
        this.boostsOverlay.getMenuEntries().clear();
        this.compactBoostsOverlay.getMenuEntries().clear();
        this.overlayManager.remove(this.boostsOverlay);
        this.overlayManager.remove(this.compactBoostsOverlay);
        this.infoBoxManager.removeIf(t -> t instanceof BoostIndicator || t instanceof StatChangeIndicator);
        this.preserveBeenActive = false;
        this.lastChangeDown = -1;
        this.lastChangeUp = -1;
        this.isChangedUp = false;
        this.isChangedDown = false;
        this.skillsToDisplay.clear();
    }

    @Subscribe
    public void onGameStateChanged(GameStateChanged event) {
        switch (event.getGameState()) {
            case LOGIN_SCREEN: 
            case HOPPING: {
                this.lastChangeDown = -1;
                this.lastChangeUp = -1;
            }
        }
    }

    private void convertConfig() {
        String migrated = this.configManager.getConfiguration("boosts", "migrated");
        if (migrated != null) {
            return;
        }
        int boostThreshold = this.config.boostThreshold();
        if (boostThreshold == 0) {
            this.configManager.setConfiguration("boosts", "notifyOnBoost", Notification.OFF);
            log.debug("Disabled boosts notification due to config migration");
        }
        this.configManager.setConfiguration("boosts", "migrated", "1");
    }

    @Subscribe
    public void onConfigChanged(ConfigChanged event) {
        if (!event.getGroup().equals("boosts")) {
            return;
        }
        this.updateShownSkills();
        if (this.config.displayNextBuffChange() == BoostsConfig.DisplayChangeMode.NEVER) {
            this.lastChangeDown = -1;
        }
        if (this.config.displayNextDebuffChange() == BoostsConfig.DisplayChangeMode.NEVER) {
            this.lastChangeUp = -1;
        }
    }

    @Subscribe
    public void onStatChanged(StatChanged statChanged) {
        Skill skill = statChanged.getSkill();
        if (!BOOSTABLE_COMBAT_SKILLS.contains(skill) && !BOOSTABLE_NON_COMBAT_SKILLS.contains(skill)) {
            return;
        }
        int skillIdx = skill.ordinal();
        int last = this.lastSkillLevels[skillIdx];
        int cur = this.client.getBoostedSkillLevel(skill);
        if (cur == last - 1) {
            this.lastChangeDown = this.client.getTickCount();
        }
        if (cur == last + 1) {
            this.lastChangeUp = this.client.getTickCount();
        }
        this.lastSkillLevels[skillIdx] = cur;
        this.updateBoostedStats();
        int boostThreshold = this.config.boostThreshold();
        int real = this.client.getRealSkillLevel(skill);
        int lastBoost = last - real;
        int boost = cur - real;
        if (boost <= boostThreshold && boostThreshold < lastBoost) {
            this.notifier.notify(this.config.notifyOnBoost(), skill.getName() + " level is getting low!");
        }
    }

    @Subscribe
    public void onGameTick(GameTick event) {
        this.lastTickMillis = System.currentTimeMillis();
        if (this.getChangeUpTicks() <= 0) {
            switch (this.config.displayNextDebuffChange()) {
                case ALWAYS: {
                    if (this.lastChangeUp == -1) break;
                    this.lastChangeUp = this.client.getTickCount();
                    break;
                }
                case BOOSTED: 
                case NEVER: {
                    this.lastChangeUp = -1;
                }
            }
        }
        if (this.getChangeDownTicks() <= 0) {
            switch (this.config.displayNextBuffChange()) {
                case ALWAYS: {
                    if (this.lastChangeDown == -1) break;
                    this.lastChangeDown = this.client.getTickCount();
                    break;
                }
                case BOOSTED: 
                case NEVER: {
                    this.lastChangeDown = -1;
                }
            }
        }
    }

    private void updateShownSkills() {
        switch (this.config.displayBoosts()) {
            case NONE: {
                this.shownSkills.removeAll(BOOSTABLE_COMBAT_SKILLS);
                this.shownSkills.removeAll(BOOSTABLE_NON_COMBAT_SKILLS);
                break;
            }
            case COMBAT: {
                this.shownSkills.addAll(BOOSTABLE_COMBAT_SKILLS);
                this.shownSkills.removeAll(BOOSTABLE_NON_COMBAT_SKILLS);
                break;
            }
            case NON_COMBAT: {
                this.shownSkills.removeAll(BOOSTABLE_COMBAT_SKILLS);
                this.shownSkills.addAll(BOOSTABLE_NON_COMBAT_SKILLS);
                break;
            }
            case BOTH: {
                this.shownSkills.addAll(BOOSTABLE_COMBAT_SKILLS);
                this.shownSkills.addAll(BOOSTABLE_NON_COMBAT_SKILLS);
            }
        }
        this.updateBoostedStats();
    }

    private void updateBoostedStats() {
        this.isChangedDown = false;
        this.isChangedUp = false;
        this.skillsToDisplay.clear();
        for (Skill skill : Skill.values()) {
            int base;
            if (!this.shownSkills.contains(skill)) continue;
            int boosted = this.client.getBoostedSkillLevel(skill);
            if (boosted > (base = this.client.getRealSkillLevel(skill))) {
                this.isChangedUp = true;
            } else if (boosted < base) {
                this.isChangedDown = true;
            }
            if (boosted == base) continue;
            this.skillsToDisplay.add(skill);
        }
    }

    int getChangeDownTicks() {
        if (this.lastChangeDown == -1 || this.config.displayNextBuffChange() == BoostsConfig.DisplayChangeMode.NEVER || this.config.displayNextBuffChange() == BoostsConfig.DisplayChangeMode.BOOSTED && !this.isChangedUp) {
            return -1;
        }
        int ticksSinceChange = this.client.getTickCount() - this.lastChangeDown;
        boolean isPreserveActive = this.client.isPrayerActive(Prayer.PRESERVE);
        if (isPreserveActive && (ticksSinceChange < 75 || this.preserveBeenActive) || ticksSinceChange > 125) {
            this.preserveBeenActive = true;
            return 150 - ticksSinceChange;
        }
        this.preserveBeenActive = false;
        return ticksSinceChange > 100 ? 125 - ticksSinceChange : 100 - ticksSinceChange;
    }

    int getChangeUpTicks() {
        if (this.lastChangeUp == -1 || this.config.displayNextDebuffChange() == BoostsConfig.DisplayChangeMode.NEVER || this.config.displayNextDebuffChange() == BoostsConfig.DisplayChangeMode.BOOSTED && !this.isChangedDown) {
            return -1;
        }
        int ticksSinceChange = this.client.getTickCount() - this.lastChangeUp;
        return 100 - ticksSinceChange;
    }

    int getChangeTime(int time) {
        long diff = System.currentTimeMillis() - this.lastTickMillis;
        return time != -1 ? (int)((double)((long)(time * 600) - diff) / 1000.0) : time;
    }

    public Set<Skill> getSkillsToDisplay() {
        return this.skillsToDisplay;
    }
}

