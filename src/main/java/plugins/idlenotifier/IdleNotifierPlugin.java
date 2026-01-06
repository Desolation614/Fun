/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.inject.Provides
 *  javax.inject.Inject
 *  net.runelite.api.Actor
 *  net.runelite.api.Client
 *  net.runelite.api.GameState
 *  net.runelite.api.Hitsplat
 *  net.runelite.api.NPC
 *  net.runelite.api.NPCComposition
 *  net.runelite.api.Player
 *  net.runelite.api.Skill
 *  net.runelite.api.coords.WorldPoint
 *  net.runelite.api.events.AnimationChanged
 *  net.runelite.api.events.GameStateChanged
 *  net.runelite.api.events.GameTick
 *  net.runelite.api.events.GraphicChanged
 *  net.runelite.api.events.HitsplatApplied
 *  net.runelite.api.events.InteractingChanged
 *  net.runelite.api.events.NpcChanged
 *  net.runelite.api.events.VarbitChanged
 */
package net.runelite.client.plugins.idlenotifier;

import com.google.inject.Provides;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import javax.inject.Inject;
import net.runelite.api.Actor;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.Hitsplat;
import net.runelite.api.NPC;
import net.runelite.api.NPCComposition;
import net.runelite.api.Player;
import net.runelite.api.Skill;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.AnimationChanged;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.GraphicChanged;
import net.runelite.api.events.HitsplatApplied;
import net.runelite.api.events.InteractingChanged;
import net.runelite.api.events.NpcChanged;
import net.runelite.api.events.VarbitChanged;
import net.runelite.client.Notifier;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.config.Notification;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.idlenotifier.IdleNotifierConfig;

@PluginDescriptor(name="Idle Notifier", description="Send a notification when going idle, or when HP/Prayer reaches a threshold", tags={"health", "hitpoints", "notifications", "prayer"}, enabledByDefault=false)
public class IdleNotifierPlugin
extends Plugin {
    private static final int IDLE_LOGOUT_WARNING_BUFFER = 1000;
    private static final int COMBAT_WARNING_MILLIS = 1140000;
    private static final int COMBAT_WARNING_CLIENT_TICKS = 57000;
    private static final int HIGHEST_MONSTER_ATTACK_SPEED = 8;
    private static final Duration SIX_HOUR_LOGOUT_WARNING_AFTER_DURATION = Duration.ofMinutes(340L);
    private static final String FISHING_SPOT = "Fishing spot";
    @Inject
    private Notifier notifier;
    @Inject
    private Client client;
    @Inject
    private IdleNotifierConfig config;
    @Inject
    private ConfigManager configManager;
    private Instant lastAnimating;
    private int lastAnimation = -1;
    private Instant lastInteracting;
    private Actor lastInteract;
    private Instant lastMoving;
    private WorldPoint lastPosition;
    private boolean notifyPosition = false;
    private boolean notifyHitpoints = true;
    private boolean notifyPrayer = true;
    private boolean shouldNotifyLowEnergy = false;
    private boolean shouldNotifyHighEnergy = false;
    private boolean notifyOxygen = true;
    private boolean notifyIdleLogout = true;
    private boolean notify6HourLogout = true;
    private int lastSpecEnergy = 1000;
    private int lastCombatCountdown = 0;
    private Instant sixHourWarningTime;
    private boolean ready;
    private boolean lastInteractWasCombat;
    private static final int BUFF_BAR_NOT_DISPLAYED = -1;

    @Provides
    IdleNotifierConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(IdleNotifierConfig.class);
    }

    @Override
    protected void startUp() {
        this.sixHourWarningTime = null;
        this.migrateConfig();
    }

    @Subscribe
    public void onAnimationChanged(AnimationChanged event) {
        if (this.client.getGameState() != GameState.LOGGED_IN) {
            return;
        }
        Player localPlayer = this.client.getLocalPlayer();
        if (localPlayer != event.getActor()) {
            return;
        }
        int graphic = localPlayer.getGraphic();
        int animation = localPlayer.getAnimation();
        switch (animation) {
            case 23: 
            case 24: 
            case 88: 
            case 335: 
            case 363: 
            case 364: 
            case 618: 
            case 619: 
            case 620: 
            case 621: 
            case 622: 
            case 623: 
            case 624: 
            case 625: 
            case 626: 
            case 627: 
            case 628: 
            case 629: 
            case 642: 
            case 643: 
            case 719: 
            case 720: 
            case 721: 
            case 726: 
            case 827: 
            case 830: 
            case 832: 
            case 867: 
            case 869: 
            case 871: 
            case 873: 
            case 875: 
            case 877: 
            case 879: 
            case 883: 
            case 884: 
            case 886: 
            case 887: 
            case 888: 
            case 889: 
            case 890: 
            case 891: 
            case 892: 
            case 894: 
            case 895: 
            case 896: 
            case 897: 
            case 898: 
            case 899: 
            case 931: 
            case 1193: 
            case 1248: 
            case 1249: 
            case 1648: 
            case 1649: 
            case 1650: 
            case 2116: 
            case 2117: 
            case 2270: 
            case 2280: 
            case 2281: 
            case 2282: 
            case 2292: 
            case 2305: 
            case 2390: 
            case 2793: 
            case 2794: 
            case 2795: 
            case 2846: 
            case 3285: 
            case 3286: 
            case 3287: 
            case 3288: 
            case 3289: 
            case 3290: 
            case 3291: 
            case 3292: 
            case 3705: 
            case 3866: 
            case 3873: 
            case 4067: 
            case 4068: 
            case 4412: 
            case 4436: 
            case 4437: 
            case 4438: 
            case 4439: 
            case 4440: 
            case 4441: 
            case 4442: 
            case 4462: 
            case 4471: 
            case 4481: 
            case 4482: 
            case 4483: 
            case 5108: 
            case 5249: 
            case 6108: 
            case 6217: 
            case 6295: 
            case 6298: 
            case 6459: 
            case 6678: 
            case 6679: 
            case 6680: 
            case 6681: 
            case 6682: 
            case 6683: 
            case 6684: 
            case 6685: 
            case 6686: 
            case 6687: 
            case 6688: 
            case 6689: 
            case 6702: 
            case 6709: 
            case 6746: 
            case 6747: 
            case 6748: 
            case 6749: 
            case 6750: 
            case 6751: 
            case 6752: 
            case 6753: 
            case 6754: 
            case 6755: 
            case 6756: 
            case 6757: 
            case 6758: 
            case 6932: 
            case 7139: 
            case 7140: 
            case 7151: 
            case 7199: 
            case 7201: 
            case 7202: 
            case 7264: 
            case 7266: 
            case 7282: 
            case 7283: 
            case 7284: 
            case 7401: 
            case 7402: 
            case 7529: 
            case 7531: 
            case 7553: 
            case 7699: 
            case 7860: 
            case 8188: 
            case 8189: 
            case 8190: 
            case 8191: 
            case 8192: 
            case 8193: 
            case 8303: 
            case 8305: 
            case 8312: 
            case 8313: 
            case 8314: 
            case 8323: 
            case 8324: 
            case 8327: 
            case 8336: 
            case 8344: 
            case 8345: 
            case 8346: 
            case 8347: 
            case 8349: 
            case 8350: 
            case 8472: 
            case 8473: 
            case 8474: 
            case 8475: 
            case 8476: 
            case 8477: 
            case 8478: 
            case 8479: 
            case 8480: 
            case 8481: 
            case 8777: 
            case 8778: 
            case 8780: 
            case 8783: 
            case 8784: 
            case 8786: 
            case 8787: 
            case 8788: 
            case 8789: 
            case 8886: 
            case 8887: 
            case 8888: 
            case 8911: 
            case 8969: 
            case 9350: 
            case 10064: 
            case 10065: 
            case 10066: 
            case 10067: 
            case 10068: 
            case 10069: 
            case 10070: 
            case 10071: 
            case 10072: 
            case 10073: 
            case 10074: 
            case 10517: 
            case 10518: 
            case 10519: 
            case 10520: 
            case 10521: 
            case 10522: 
            case 10523: 
            case 10524: 
            case 10525: 
            case 10526: 
            case 10527: 
            case 10563: 
            case 10564: 
            case 10565: 
            case 10566: 
            case 10567: 
            case 10568: 
            case 10569: 
            case 10570: 
            case 10571: 
            case 10572: 
            case 10573: 
            case 11075: 
            case 11095: 
            case 11099: 
            case 11102: 
            case 11604: 
            case 11634: 
            case 11639: 
            case 11644: 
            case 11825: 
            case 11826: 
            case 11827: 
            case 11831: 
            case 11832: 
            case 11833: 
            case 11837: 
            case 11838: 
            case 11839: 
            case 11867: 
            case 11868: 
            case 11939: 
            case 11940: 
            case 12026: 
            case 12177: 
            case 12178: 
            case 12179: 
            case 12180: 
            case 12181: 
            case 12182: 
            case 12183: 
            case 12184: 
            case 12185: 
            case 12186: 
            case 12187: 
            case 12188: 
            case 12189: 
            case 12190: 
            case 12191: 
            case 12192: 
            case 12193: 
            case 12194: {
                this.resetTimers();
                this.lastAnimation = animation;
                this.lastAnimating = Instant.now();
                break;
            }
            case 4413: {
                if (graphic == 746) {
                    this.resetTimers();
                    this.lastAnimation = animation;
                    this.lastAnimating = Instant.now();
                    break;
                }
            }
            case -1: {
                this.lastAnimating = Instant.now();
                break;
            }
            default: {
                this.lastAnimation = -1;
                this.lastAnimating = null;
            }
        }
    }

    @Subscribe
    public void onInteractingChanged(InteractingChanged event) {
        Actor source = event.getSource();
        if (source != this.client.getLocalPlayer()) {
            return;
        }
        Actor target = event.getTarget();
        if (target != null) {
            this.lastInteract = null;
        } else {
            this.lastInteracting = Instant.now();
        }
        if (!(target instanceof NPC)) {
            return;
        }
        this.checkNpcInteraction((NPC)target);
    }

    @Subscribe
    public void onNpcChanged(NpcChanged event) {
        NPC npc = event.getNpc();
        if (this.client.getLocalPlayer().getInteracting() != npc) {
            return;
        }
        this.checkNpcInteraction(npc);
    }

    @Subscribe
    public void onGameStateChanged(GameStateChanged gameStateChanged) {
        this.lastInteracting = null;
        GameState state = gameStateChanged.getGameState();
        switch (state) {
            case LOGIN_SCREEN: {
                this.resetTimers();
                break;
            }
            case LOGGING_IN: 
            case HOPPING: 
            case CONNECTION_LOST: {
                this.ready = true;
                break;
            }
            case LOGGED_IN: {
                if (!this.ready) break;
                this.sixHourWarningTime = Instant.now().plus(SIX_HOUR_LOGOUT_WARNING_AFTER_DURATION);
                this.ready = false;
                this.resetTimers();
            }
        }
    }

    @Subscribe
    public void onHitsplatApplied(HitsplatApplied event) {
        if (event.getActor() != this.client.getLocalPlayer()) {
            return;
        }
        Hitsplat hitsplat = event.getHitsplat();
        if (hitsplat.isMine()) {
            this.lastCombatCountdown = 8;
        }
    }

    @Subscribe
    public void onGraphicChanged(GraphicChanged event) {
        Actor actor = event.getActor();
        if (actor != this.client.getLocalPlayer()) {
            return;
        }
        if (actor.getGraphic() == 85) {
            this.lastCombatCountdown = 8;
        }
    }

    @Subscribe
    public void onGameTick(GameTick event) {
        Player local = this.client.getLocalPlayer();
        Duration waitDuration = Duration.ofMillis(this.config.getIdleNotificationDelay());
        this.lastCombatCountdown = Math.max(this.lastCombatCountdown - 1, 0);
        if (local == null || System.currentTimeMillis() - this.client.getMouseLastPressedMillis() < 1000L || this.client.getKeyboardIdleTicks() < 10) {
            this.resetTimers();
            return;
        }
        if (this.checkIdleLogout()) {
            this.notifier.notify(this.config.logoutIdle(), "You are about to log out from idling too long!");
        }
        if (this.check6hrLogout()) {
            this.notifier.notify(this.config.sixHourLogout(), "You are about to log out from being online for 6 hours!");
        }
        if (this.checkAnimationIdle(waitDuration, local)) {
            this.notifier.notify(this.config.animationIdle(), "You are now idle!");
        }
        if (this.checkMovementIdle(waitDuration, local)) {
            this.notifier.notify(this.config.movementIdle(), "You have stopped moving!");
        }
        if (this.checkInteractionIdle(waitDuration, local)) {
            if (this.lastInteractWasCombat) {
                this.notifier.notify(this.config.interactionIdle(), "You are now out of combat!");
            } else {
                this.notifier.notify(this.config.interactionIdle(), "You are now idle!");
            }
        }
        if (this.checkLowHitpoints()) {
            this.notifier.notify(this.config.getHitpointsNotification(), "You have low hitpoints!");
        }
        if (this.checkLowPrayer()) {
            this.notifier.notify(this.config.getPrayerNotification(), "You have low prayer!");
        }
        if (this.checkLowEnergy()) {
            this.notifier.notify(this.config.getLowEnergyNotification(), "You have low run energy!");
        }
        if (this.checkHighEnergy()) {
            this.notifier.notify(this.config.getHighEnergyNotification(), "You have restored run energy!");
        }
        if (this.checkLowOxygen()) {
            this.notifier.notify(this.config.getOxygenNotification(), "You have low oxygen!");
        }
        if (this.checkFullSpecEnergy()) {
            this.notifier.notify(this.config.getSpecNotification(), "You have restored spec energy!");
        }
    }

    @Subscribe
    public void onVarbitChanged(VarbitChanged event) {
        if (event.getVarpId() == 4007 && event.getValue() == -1) {
            this.resetTimers();
            this.lastAnimation = 867;
            this.lastAnimating = Instant.now();
        }
    }

    private void migrateConfig() {
        String migrated = this.configManager.getConfiguration("idlenotifier", "migrated");
        if ("1".equals(migrated)) {
            return;
        }
        int hitpointsThreshold = (Integer)this.configManager.getConfiguration("idlenotifier", "hitpoints", Integer.TYPE);
        if (hitpointsThreshold == 0) {
            this.configManager.setConfiguration("idlenotifier", "hitpoints", 1);
            this.configManager.setConfiguration("idlenotifier", "hitpointsNotification", Notification.OFF);
        } else {
            this.configManager.setConfiguration("idlenotifier", "hitpointsNotification", Notification.ON);
        }
        int prayerThreshold = (Integer)this.configManager.getConfiguration("idlenotifier", "prayer", Integer.TYPE);
        if (prayerThreshold == 0) {
            this.configManager.setConfiguration("idlenotifier", "prayer", 1);
            this.configManager.setConfiguration("idlenotifier", "prayerNotification", Notification.OFF);
        } else {
            this.configManager.setConfiguration("idlenotifier", "prayerNotification", Notification.ON);
        }
        int lowEnergyThreshold = (Integer)this.configManager.getConfiguration("idlenotifier", "lowEnergy", Integer.TYPE);
        if (lowEnergyThreshold == 100) {
            this.configManager.setConfiguration("idlenotifier", "lowEnergy", 0);
            this.configManager.setConfiguration("idlenotifier", "lowEnergyNotification", Notification.OFF);
        } else {
            this.configManager.setConfiguration("idlenotifier", "lowEnergyNotification", Notification.ON);
        }
        int highEnergyThreshold = (Integer)this.configManager.getConfiguration("idlenotifier", "highEnergy", Integer.TYPE);
        if (highEnergyThreshold == 0) {
            this.configManager.setConfiguration("idlenotifier", "highEnergy", 100);
            this.configManager.setConfiguration("idlenotifier", "highEnergyNotification", Notification.OFF);
        } else {
            this.configManager.setConfiguration("idlenotifier", "highEnergyNotification", Notification.ON);
        }
        int oxygenThreshold = (Integer)this.configManager.getConfiguration("idlenotifier", "oxygen", Integer.TYPE);
        if (oxygenThreshold == 0) {
            this.configManager.setConfiguration("idlenotifier", "oxygen", 1);
            this.configManager.setConfiguration("idlenotifier", "oxygenNotification", Notification.OFF);
        } else {
            this.configManager.setConfiguration("idlenotifier", "oxygenNotification", Notification.ON);
        }
        int specThreshold = (Integer)this.configManager.getConfiguration("idlenotifier", "spec", Integer.TYPE);
        if (specThreshold == 0) {
            this.configManager.setConfiguration("idlenotifier", "spec", 1);
            this.configManager.setConfiguration("idlenotifier", "specNotification", Notification.OFF);
        } else {
            this.configManager.setConfiguration("idlenotifier", "specNotification", Notification.ON);
        }
        this.configManager.setConfiguration("idlenotifier", "migrated", 1);
    }

    private void checkNpcInteraction(NPC target) {
        NPCComposition npcComposition = target.getComposition();
        List<String> npcMenuActions = Arrays.asList(npcComposition.getActions());
        if (npcMenuActions.contains("Attack")) {
            this.resetTimers();
            this.lastInteract = target;
            this.lastInteracting = Instant.now();
            this.lastInteractWasCombat = true;
        } else if (target.getName() != null && target.getName().contains(FISHING_SPOT)) {
            this.resetTimers();
            this.lastInteract = target;
            this.lastInteracting = Instant.now();
            this.lastInteractWasCombat = false;
        }
    }

    private boolean checkFullSpecEnergy() {
        int currentSpecEnergy = this.client.getVarpValue(300);
        int threshold = this.config.getSpecEnergyThreshold() * 10;
        if (threshold == 0) {
            this.lastSpecEnergy = currentSpecEnergy;
            return false;
        }
        boolean notify = this.lastSpecEnergy < threshold && currentSpecEnergy >= threshold && currentSpecEnergy - this.lastSpecEnergy <= 100;
        this.lastSpecEnergy = currentSpecEnergy;
        return notify;
    }

    private boolean checkLowOxygen() {
        if (this.config.getOxygenThreshold() == 0) {
            return false;
        }
        if ((double)this.config.getOxygenThreshold() >= (double)this.client.getVarbitValue(5811) * 0.1) {
            if (!this.notifyOxygen) {
                this.notifyOxygen = true;
                return true;
            }
        } else {
            this.notifyOxygen = false;
        }
        return false;
    }

    private boolean checkLowHitpoints() {
        if (this.config.getHitpointsThreshold() == 0) {
            return false;
        }
        if (this.client.getRealSkillLevel(Skill.HITPOINTS) > this.config.getHitpointsThreshold()) {
            if (this.client.getBoostedSkillLevel(Skill.HITPOINTS) + this.client.getVarbitValue(3956) <= this.config.getHitpointsThreshold()) {
                if (!this.notifyHitpoints) {
                    this.notifyHitpoints = true;
                    return true;
                }
            } else {
                this.notifyHitpoints = false;
            }
        }
        return false;
    }

    private boolean checkLowPrayer() {
        if (this.config.getPrayerThreshold() == 0) {
            return false;
        }
        if (this.client.getRealSkillLevel(Skill.PRAYER) > this.config.getPrayerThreshold()) {
            if (this.client.getBoostedSkillLevel(Skill.PRAYER) <= this.config.getPrayerThreshold()) {
                if (!this.notifyPrayer) {
                    this.notifyPrayer = true;
                    return true;
                }
            } else {
                this.notifyPrayer = false;
            }
        }
        return false;
    }

    private boolean checkLowEnergy() {
        if (this.config.getLowEnergyThreshold() >= 100) {
            return false;
        }
        if (this.client.getEnergy() / 100 <= this.config.getLowEnergyThreshold()) {
            if (this.shouldNotifyLowEnergy) {
                this.shouldNotifyLowEnergy = false;
                return true;
            }
        } else {
            this.shouldNotifyLowEnergy = true;
        }
        return false;
    }

    private boolean checkHighEnergy() {
        if (this.config.getHighEnergyThreshold() == 0) {
            return false;
        }
        if (this.client.getEnergy() / 100 >= this.config.getHighEnergyThreshold()) {
            if (this.shouldNotifyHighEnergy) {
                this.shouldNotifyHighEnergy = false;
                return true;
            }
        } else {
            this.shouldNotifyHighEnergy = true;
        }
        return false;
    }

    private boolean checkInteractionIdle(Duration waitDuration, Player local) {
        if (this.lastInteract == null) {
            return false;
        }
        Actor interact = local.getInteracting();
        if (interact == null) {
            if (this.lastInteracting != null && Instant.now().compareTo(this.lastInteracting.plus(waitDuration)) >= 0 && this.lastCombatCountdown == 0) {
                this.lastInteract = null;
                this.lastInteracting = null;
                this.lastAnimation = -1;
                this.lastAnimating = null;
                return true;
            }
        } else {
            this.lastInteracting = Instant.now();
        }
        return false;
    }

    private boolean checkIdleLogout() {
        int idleClientTicks = Math.min(this.client.getKeyboardIdleTicks(), this.client.getMouseIdleTicks());
        if (idleClientTicks < this.client.getIdleTimeout() - 1000) {
            this.notifyIdleLogout = true;
            return false;
        }
        if (this.lastCombatCountdown <= 0) {
            boolean warn = this.notifyIdleLogout;
            this.notifyIdleLogout = false;
            return warn;
        }
        long lastInteractionAgo = System.currentTimeMillis() - this.client.getMouseLastPressedMillis();
        if (lastInteractionAgo < 1140000L || this.client.getKeyboardIdleTicks() < 57000) {
            this.notifyIdleLogout = true;
            return false;
        }
        boolean warn = this.notifyIdleLogout;
        this.notifyIdleLogout = false;
        return warn;
    }

    private boolean check6hrLogout() {
        if (this.sixHourWarningTime == null) {
            return false;
        }
        if (Instant.now().compareTo(this.sixHourWarningTime) >= 0) {
            if (this.notify6HourLogout) {
                this.notify6HourLogout = false;
                return true;
            }
        } else {
            this.notify6HourLogout = true;
        }
        return false;
    }

    private boolean checkAnimationIdle(Duration waitDuration, Player local) {
        if (this.lastAnimation == -1) {
            return false;
        }
        int animation = local.getAnimation();
        if (animation == -1) {
            if (this.lastAnimating != null && Instant.now().compareTo(this.lastAnimating.plus(waitDuration)) >= 0) {
                this.lastAnimation = -1;
                this.lastAnimating = null;
                this.lastInteract = null;
                this.lastInteracting = null;
                return true;
            }
        } else {
            this.lastAnimating = Instant.now();
        }
        return false;
    }

    private boolean checkMovementIdle(Duration waitDuration, Player local) {
        if (this.lastPosition == null) {
            this.lastPosition = local.getWorldLocation();
            return false;
        }
        WorldPoint position = local.getWorldLocation();
        if (this.lastPosition.equals((Object)position)) {
            if (this.notifyPosition && local.getAnimation() == -1 && Instant.now().compareTo(this.lastMoving.plus(waitDuration)) >= 0) {
                this.notifyPosition = false;
                return this.lastAnimation == -1;
            }
        } else {
            this.notifyPosition = true;
            this.lastPosition = position;
            this.lastMoving = Instant.now();
        }
        return false;
    }

    private void resetTimers() {
        Player local = this.client.getLocalPlayer();
        this.lastAnimating = null;
        GameState gameState = this.client.getGameState();
        if (gameState == GameState.LOGIN_SCREEN || local == null || local.getAnimation() != this.lastAnimation) {
            this.lastAnimation = -1;
        }
        this.lastInteracting = null;
        if (gameState == GameState.LOGIN_SCREEN || local == null || local.getInteracting() != this.lastInteract) {
            this.lastInteract = null;
        }
    }
}

