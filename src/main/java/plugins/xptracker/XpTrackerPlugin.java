/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.base.MoreObjects
 *  com.google.inject.Binder
 *  com.google.inject.Provides
 *  javax.inject.Inject
 *  net.runelite.api.Client
 *  net.runelite.api.GameState
 *  net.runelite.api.MenuAction
 *  net.runelite.api.Player
 *  net.runelite.api.Skill
 *  net.runelite.api.WorldType
 *  net.runelite.api.events.GameStateChanged
 *  net.runelite.api.events.GameTick
 *  net.runelite.api.events.MenuEntryAdded
 *  net.runelite.api.events.StatChanged
 *  net.runelite.api.widgets.WidgetUtil
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package net.runelite.client.plugins.xptracker;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.MoreObjects;
import com.google.inject.Binder;
import com.google.inject.Provides;
import java.awt.image.BufferedImage;
import java.lang.reflect.Type;
import java.time.temporal.ChronoUnit;
import java.util.EnumSet;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.MenuAction;
import net.runelite.api.Player;
import net.runelite.api.Skill;
import net.runelite.api.WorldType;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.MenuEntryAdded;
import net.runelite.api.events.StatChanged;
import net.runelite.api.widgets.WidgetUtil;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ClientShutdown;
import net.runelite.client.events.RuneScapeProfileChanged;
import net.runelite.client.game.SkillIconManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.xptracker.XpClient;
import net.runelite.client.plugins.xptracker.XpInfoBoxOverlay;
import net.runelite.client.plugins.xptracker.XpPanel;
import net.runelite.client.plugins.xptracker.XpPauseState;
import net.runelite.client.plugins.xptracker.XpSave;
import net.runelite.client.plugins.xptracker.XpSnapshotSingle;
import net.runelite.client.plugins.xptracker.XpState;
import net.runelite.client.plugins.xptracker.XpStateSingle;
import net.runelite.client.plugins.xptracker.XpTrackerConfig;
import net.runelite.client.plugins.xptracker.XpTrackerService;
import net.runelite.client.plugins.xptracker.XpTrackerServiceImpl;
import net.runelite.client.plugins.xptracker.XpUpdateResult;
import net.runelite.client.plugins.xptracker.XpWorldType;
import net.runelite.client.task.Schedule;
import net.runelite.client.ui.ClientToolbar;
import net.runelite.client.ui.NavigationButton;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.util.ImageUtil;
import net.runelite.client.util.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@PluginDescriptor(name="XP Tracker", description="Enable the XP Tracker panel", tags={"experience", "levels", "panel"})
public class XpTrackerPlugin
extends Plugin {
    private static final Logger log = LoggerFactory.getLogger(XpTrackerPlugin.class);
    private static final int XP_THRESHOLD = 10000;
    private static final String MENUOP_ADD_CANVAS_TRACKER = "Add to canvas";
    private static final String MENUOP_REMOVE_CANVAS_TRACKER = "Remove from canvas";
    @Inject
    private ClientToolbar clientToolbar;
    @Inject
    private Client client;
    @Inject
    private ClientThread clientThread;
    @Inject
    private SkillIconManager skillIconManager;
    @Inject
    private XpTrackerConfig xpTrackerConfig;
    @Inject
    private OverlayManager overlayManager;
    @Inject
    private XpClient xpClient;
    @Inject
    private XpState xpState;
    @Inject
    private ConfigManager configManager;
    private NavigationButton navButton;
    @VisibleForTesting
    private XpPanel xpPanel;
    private XpWorldType lastWorldType;
    private long lastAccount;
    private long lastTickMillis = 0L;
    private boolean fetchXp;
    private long lastXp = 0L;
    private int initializeTracker;
    private final XpPauseState xpPauseState = new XpPauseState();

    @Provides
    XpTrackerConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(XpTrackerConfig.class);
    }

    @Override
    public void configure(Binder binder) {
        binder.bind(XpTrackerService.class).to(XpTrackerServiceImpl.class);
    }

    @Override
    protected void startUp() throws Exception {
        this.xpPanel = new XpPanel(this, this.xpTrackerConfig, this.client, this.skillIconManager);
        BufferedImage icon = ImageUtil.loadImageResource(this.getClass(), "/skill_icons/overall.png");
        this.navButton = NavigationButton.builder().tooltip("XP Tracker").icon(icon).priority(2).panel(this.xpPanel).build();
        this.clientToolbar.addNavigation(this.navButton);
        this.fetchXp = true;
        this.initializeTracker = 2;
        this.lastAccount = -1L;
        this.clientThread.invokeLater(() -> {
            if (this.client.getGameState() == GameState.LOGGED_IN) {
                this.lastAccount = this.client.getAccountHash();
                this.lastWorldType = this.worldSetToType(this.client.getWorldType());
            }
        });
    }

    @Override
    protected void shutDown() throws Exception {
        this.overlayManager.removeIf(e -> e instanceof XpInfoBoxOverlay);
        this.xpState.reset();
        this.clientToolbar.removeNavigation(this.navButton);
    }

    @Subscribe
    public void onGameStateChanged(GameStateChanged event) {
        GameState state = event.getGameState();
        if (state == GameState.LOGGED_IN) {
            XpWorldType type = this.worldSetToType(this.client.getWorldType());
            if (this.client.getAccountHash() != this.lastAccount || this.lastWorldType != type) {
                log.debug("World change: {} -> {}, {} -> {}", new Object[]{this.lastAccount, this.client.getAccountHash(), MoreObjects.firstNonNull((Object)((Object)this.lastWorldType), (Object)"<unknown>"), MoreObjects.firstNonNull((Object)((Object)type), (Object)"<unknown>")});
                this.lastAccount = this.client.getAccountHash();
                this.fetchXp = true;
                this.lastWorldType = type;
                this.resetState();
                assert (this.initializeTracker > 0);
            }
        } else if (state == GameState.LOGGING_IN || state == GameState.HOPPING) {
            this.initializeTracker = 2;
        } else if (state == GameState.LOGIN_SCREEN) {
            Player local = this.client.getLocalPlayer();
            if (local == null) {
                return;
            }
            String username = local.getName();
            if (username == null) {
                return;
            }
            long totalXp = this.client.getOverallExperience();
            if (Math.abs(totalXp - this.lastXp) > 10000L) {
                this.xpClient.update(username);
                this.lastXp = totalXp;
            }
        }
    }

    @Subscribe
    public void onRuneScapeProfileChanged(RuneScapeProfileChanged event) {
        XpSave save = this.xpState.save();
        if (save != null) {
            this.saveSaveState(event.getPreviousProfile(), save);
        }
    }

    @Subscribe
    public void onClientShutdown(ClientShutdown event) {
        XpSave save = this.xpState.save();
        if (save != null) {
            this.saveSaveState(this.configManager.getRSProfileKey(), save);
        }
    }

    private XpWorldType worldSetToType(EnumSet<WorldType> types) {
        XpWorldType xpType = XpWorldType.NORMAL;
        for (WorldType type : types) {
            XpWorldType t = XpWorldType.of(type);
            if (t == XpWorldType.NORMAL) continue;
            xpType = t;
        }
        return xpType;
    }

    void addOverlay(Skill skill) {
        this.removeOverlay(skill);
        this.overlayManager.add(new XpInfoBoxOverlay(this, this.xpTrackerConfig, skill, this.skillIconManager.getSkillImage(skill)));
    }

    void removeOverlay(Skill skill) {
        this.overlayManager.removeIf(e -> e instanceof XpInfoBoxOverlay && ((XpInfoBoxOverlay)e).getSkill() == skill);
    }

    boolean hasOverlay(Skill skill) {
        return this.overlayManager.anyMatch(o -> o instanceof XpInfoBoxOverlay && ((XpInfoBoxOverlay)o).getSkill() == skill);
    }

    void resetAndInitState() {
        this.clearSaveState(this.configManager.getRSProfileKey());
        this.resetState();
        for (Skill skill : Skill.values()) {
            long currentXp = this.client.getSkillExperience(skill);
            this.xpState.initializeSkill(skill, currentXp);
            this.removeOverlay(skill);
        }
        this.xpState.initializeOverall(this.client.getOverallExperience());
    }

    private void resetState() {
        this.xpState.reset();
        this.xpPanel.resetAllInfoBoxes();
        this.xpPanel.updateTotal(new XpSnapshotSingle.XpSnapshotSingleBuilder().build());
        this.overlayManager.removeIf(e -> e instanceof XpInfoBoxOverlay);
    }

    void resetSkillState(Skill skill) {
        int currentXp = this.client.getSkillExperience(skill);
        this.xpState.initializeSkill(skill, currentXp);
        this.xpPanel.resetSkill(skill);
        this.removeOverlay(skill);
    }

    void resetOtherSkillState(Skill skill) {
        for (Skill s : Skill.values()) {
            if (skill == s) continue;
            this.resetSkillState(s);
        }
    }

    void resetSkillPerHourState(Skill skill) {
        this.xpState.resetSkillPerHour(skill);
    }

    void resetAllSkillsPerHourState() {
        for (Skill skill : Skill.values()) {
            this.xpState.resetSkillPerHour(skill);
        }
        this.xpState.resetOverallPerHour();
    }

    void updateSkillOrderState(Skill skill, int newPosition) {
        this.xpState.setOrder(skill, newPosition);
    }

    void setSkillCompactViewState(Skill skill, boolean compactView) {
        this.xpState.setCompactView(skill, compactView);
    }

    @Subscribe
    public void onStatChanged(StatChanged statChanged) {
        if (this.client.getVarbitValue(30029) == 1) {
            return;
        }
        Skill skill = statChanged.getSkill();
        int currentXp = statChanged.getXp();
        int currentLevel = statChanged.getLevel();
        int startGoal = XpTrackerPlugin.startGoalVarpForSkill(skill);
        int endGoal = XpTrackerPlugin.endGoalVarpForSkill(skill);
        int startGoalXp = this.client.getVarpValue(startGoal);
        int endGoalXp = this.client.getVarpValue(endGoal);
        if (this.initializeTracker > 0) {
            return;
        }
        if (this.xpTrackerConfig.hideMaxed() && currentLevel >= 99) {
            this.xpPanel.resetSkill(skill);
            this.removeOverlay(skill);
            return;
        }
        XpUpdateResult updateResult = this.xpState.updateSkill(skill, currentXp, startGoalXp, endGoalXp);
        this.xpPanel.updateSkillExperience(updateResult == XpUpdateResult.UPDATED, this.xpPauseState.isPaused(skill), skill, this.xpState.getSkillSnapshot(skill));
        this.xpState.updateOverall(this.client.getOverallExperience());
        this.xpPanel.updateTotal(this.xpState.getTotalSnapshot());
    }

    @Subscribe
    public void onGameTick(GameTick event) {
        if (this.initializeTracker > 0 && --this.initializeTracker == 0) {
            XpSave save;
            if (!this.xpState.isOverallInitialized() && this.xpTrackerConfig.saveState() && (save = this.loadSaveState(this.configManager.getRSProfileKey())) != null) {
                log.debug("Loading xp state from save");
                this.xpState.restore(save);
                for (Skill skill : save.skills.keySet()) {
                    int startGoal = XpTrackerPlugin.startGoalVarpForSkill(skill);
                    int endGoal = XpTrackerPlugin.endGoalVarpForSkill(skill);
                    int startGoalXp = this.client.getVarpValue(startGoal);
                    int endGoalXp = this.client.getVarpValue(endGoal);
                    XpStateSingle x = this.xpState.getSkill(skill);
                    x.updateGoals(x.getCurrentXp(), startGoalXp, endGoalXp);
                }
                for (Skill skill : save.skills.keySet()) {
                    this.xpPanel.updateSkillExperience(true, false, skill, this.xpState.getSkillSnapshot(skill));
                }
                this.xpPanel.updateTotal(this.xpState.getTotalSnapshot());
            }
            for (Skill skill : Skill.values()) {
                if (!this.xpState.isInitialized(skill)) continue;
                XpStateSingle skillState = this.xpState.getSkill(skill);
                int currentXp = this.client.getSkillExperience(skill);
                if (skillState.getCurrentXp() == (long)currentXp) continue;
                if ((long)currentXp < skillState.getCurrentXp()) {
                    log.debug("Xp is going backwards! {} {} -> {}", new Object[]{skill, skillState.getCurrentXp(), currentXp});
                    this.resetState();
                    this.clearSaveState(this.configManager.getRSProfileKey());
                    break;
                }
                log.debug("Skill xp for {} changed when offline: {} -> {}", new Object[]{skill, skillState.getCurrentXp(), currentXp});
                long diff = (long)currentXp - skillState.getCurrentXp();
                skillState.setStartXp(skillState.getStartXp() + diff);
            }
            for (Skill skill : Skill.values()) {
                if (this.xpState.isInitialized(skill)) continue;
                int currentXp = this.client.getSkillExperience(skill);
                XpUpdateResult xpUpdateResult = this.xpState.updateSkill(skill, currentXp, -1, -1);
                assert (xpUpdateResult == XpUpdateResult.INITIALIZED);
            }
            if (!this.xpState.isOverallInitialized()) {
                long overallXp = this.client.getOverallExperience();
                log.debug("Initializing XP tracker with {} overall exp", (Object)overallXp);
                this.xpState.initializeOverall(overallXp);
            }
        }
        if (this.fetchXp) {
            this.lastXp = this.client.getOverallExperience();
            this.fetchXp = false;
        }
    }

    @Subscribe
    public void onMenuEntryAdded(MenuEntryAdded event) {
        Skill skill;
        int widgetID = event.getActionParam1();
        if (WidgetUtil.componentToInterface((int)widgetID) != 320 || !event.getOption().startsWith("View") || !this.xpTrackerConfig.skillTabOverlayMenuOptions()) {
            return;
        }
        String skillText = event.getOption().split(" ")[1];
        try {
            skill = Skill.valueOf((String)Text.removeTags(skillText).toUpperCase());
        }
        catch (IllegalArgumentException ignored) {
            return;
        }
        this.client.createMenuEntry(-1).setTarget(skillText).setOption(this.hasOverlay(skill) ? MENUOP_REMOVE_CANVAS_TRACKER : MENUOP_ADD_CANVAS_TRACKER).setType(MenuAction.RUNELITE).onClick(e -> {
            if (this.hasOverlay(skill)) {
                this.removeOverlay(skill);
            } else {
                this.addOverlay(skill);
            }
        });
    }

    XpStateSingle getSkillState(Skill skill) {
        return this.xpState.getSkill(skill);
    }

    XpSnapshotSingle getSkillSnapshot(Skill skill) {
        return this.xpState.getSkillSnapshot(skill);
    }

    private static int startGoalVarpForSkill(Skill skill) {
        switch (skill) {
            case ATTACK: {
                return 1229;
            }
            case MINING: {
                return 1241;
            }
            case WOODCUTTING: {
                return 1246;
            }
            case DEFENCE: {
                return 1233;
            }
            case MAGIC: {
                return 1232;
            }
            case RANGED: {
                return 1231;
            }
            case HITPOINTS: {
                return 1234;
            }
            case AGILITY: {
                return 1236;
            }
            case STRENGTH: {
                return 1230;
            }
            case PRAYER: {
                return 1235;
            }
            case SLAYER: {
                return 1248;
            }
            case FISHING: {
                return 1243;
            }
            case RUNECRAFT: {
                return 1240;
            }
            case HERBLORE: {
                return 1237;
            }
            case FIREMAKING: {
                return 1245;
            }
            case CONSTRUCTION: {
                return 1250;
            }
            case HUNTER: {
                return 1251;
            }
            case COOKING: {
                return 1244;
            }
            case FARMING: {
                return 1249;
            }
            case CRAFTING: {
                return 1239;
            }
            case SMITHING: {
                return 1242;
            }
            case THIEVING: {
                return 1238;
            }
            case FLETCHING: {
                return 1247;
            }
        }
        throw new IllegalArgumentException();
    }

    private static int endGoalVarpForSkill(Skill skill) {
        switch (skill) {
            case ATTACK: {
                return 1253;
            }
            case MINING: {
                return 1265;
            }
            case WOODCUTTING: {
                return 1270;
            }
            case DEFENCE: {
                return 1257;
            }
            case MAGIC: {
                return 1256;
            }
            case RANGED: {
                return 1255;
            }
            case HITPOINTS: {
                return 1258;
            }
            case AGILITY: {
                return 1260;
            }
            case STRENGTH: {
                return 1254;
            }
            case PRAYER: {
                return 1259;
            }
            case SLAYER: {
                return 1272;
            }
            case FISHING: {
                return 1267;
            }
            case RUNECRAFT: {
                return 1264;
            }
            case HERBLORE: {
                return 1261;
            }
            case FIREMAKING: {
                return 1269;
            }
            case CONSTRUCTION: {
                return 1274;
            }
            case HUNTER: {
                return 1275;
            }
            case COOKING: {
                return 1268;
            }
            case FARMING: {
                return 1273;
            }
            case CRAFTING: {
                return 1263;
            }
            case SMITHING: {
                return 1266;
            }
            case THIEVING: {
                return 1262;
            }
            case FLETCHING: {
                return 1271;
            }
        }
        throw new IllegalArgumentException();
    }

    @Schedule(period=1L, unit=ChronoUnit.SECONDS)
    public void tickSkillTimes() {
        int pauseSkillAfter = this.xpTrackerConfig.pauseSkillAfter();
        for (Skill skill : Skill.values()) {
            long skillExperience = this.client.getSkillExperience(skill);
            this.xpPauseState.tickXp(skill, skillExperience, pauseSkillAfter);
        }
        this.xpPauseState.tickOverall(this.client.getOverallExperience(), pauseSkillAfter);
        boolean loggedIn = this.client.getGameState().getState() >= GameState.LOADING.getState();
        this.xpPauseState.tickLogout(this.xpTrackerConfig.pauseOnLogout(), loggedIn);
        if (this.lastTickMillis == 0L) {
            this.lastTickMillis = System.currentTimeMillis();
            return;
        }
        long nowMillis = System.currentTimeMillis();
        long tickDelta = nowMillis - this.lastTickMillis;
        this.lastTickMillis = nowMillis;
        for (Skill skill : Skill.values()) {
            if (this.xpPauseState.isPaused(skill)) continue;
            this.xpState.tick(skill, tickDelta);
        }
        if (!this.xpPauseState.isOverallPaused()) {
            this.xpState.tickOverall(tickDelta);
        }
        this.rebuildSkills();
    }

    @Schedule(period=1L, unit=ChronoUnit.MINUTES, asynchronous=true)
    public void tickStateSave() {
        XpSave save = this.xpState.save();
        if (save != null) {
            this.saveSaveState(this.configManager.getRSProfileKey(), save);
        }
    }

    private void rebuildSkills() {
        for (Skill skill : Skill.values()) {
            this.xpPanel.updateSkillExperience(false, this.xpPauseState.isPaused(skill), skill, this.xpState.getSkillSnapshot(skill));
        }
        this.xpPanel.updateTotal(this.xpState.getTotalSnapshot());
    }

    void pauseSkill(Skill skill, boolean pause) {
        if (pause ? this.xpPauseState.pauseSkill(skill) : this.xpPauseState.unpauseSkill(skill)) {
            this.xpPanel.updateSkillExperience(false, this.xpPauseState.isPaused(skill), skill, this.xpState.getSkillSnapshot(skill));
        }
    }

    void pauseAllSkills(boolean pause) {
        for (Skill skill : Skill.values()) {
            this.pauseSkill(skill, pause);
        }
        if (pause) {
            this.xpPauseState.pauseOverall();
        } else {
            this.xpPauseState.unpauseOverall();
        }
    }

    private void saveSaveState(String profile, XpSave state) {
        this.configManager.setConfiguration("xpTracker", profile, "state", state);
    }

    private void clearSaveState(String profile) {
        this.configManager.unsetConfiguration("xpTracker", profile, "state");
    }

    private XpSave loadSaveState(String profile) {
        return (XpSave)this.configManager.getConfiguration("xpTracker", profile, "state", (Type)((Object)XpSave.class));
    }

    void setXpPanel(XpPanel xpPanel) {
        this.xpPanel = xpPanel;
    }
}

