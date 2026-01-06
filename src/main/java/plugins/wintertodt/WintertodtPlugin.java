/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.inject.Provides
 *  javax.inject.Inject
 *  net.runelite.api.ChatMessageType
 *  net.runelite.api.Client
 *  net.runelite.api.Item
 *  net.runelite.api.ItemContainer
 *  net.runelite.api.MessageNode
 *  net.runelite.api.Player
 *  net.runelite.api.events.AnimationChanged
 *  net.runelite.api.events.ChatMessage
 *  net.runelite.api.events.GameTick
 *  net.runelite.api.events.ItemContainerChanged
 *  net.runelite.api.events.VarbitChanged
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package net.runelite.client.plugins.wintertodt;

import com.google.inject.Provides;
import java.time.Duration;
import java.time.Instant;
import javax.inject.Inject;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.Item;
import net.runelite.api.ItemContainer;
import net.runelite.api.MessageNode;
import net.runelite.api.Player;
import net.runelite.api.events.AnimationChanged;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.ItemContainerChanged;
import net.runelite.api.events.VarbitChanged;
import net.runelite.client.Notifier;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.wintertodt.WintertodtActivity;
import net.runelite.client.plugins.wintertodt.WintertodtConfig;
import net.runelite.client.plugins.wintertodt.WintertodtInterruptType;
import net.runelite.client.plugins.wintertodt.WintertodtOverlay;
import net.runelite.client.plugins.wintertodt.config.WintertodtNotifyDamage;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.util.ColorUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@PluginDescriptor(name="Wintertodt", description="Show helpful information for the Wintertodt boss", tags={"minigame", "firemaking", "boss"})
public class WintertodtPlugin
extends Plugin {
    private static final Logger log = LoggerFactory.getLogger(WintertodtPlugin.class);
    private static final int WINTERTODT_REGION = 6462;
    @Inject
    private Notifier notifier;
    @Inject
    private Client client;
    @Inject
    private OverlayManager overlayManager;
    @Inject
    private WintertodtOverlay overlay;
    @Inject
    private WintertodtConfig config;
    private WintertodtActivity currentActivity = WintertodtActivity.IDLE;
    private int inventoryScore;
    private int totalPotentialinventoryScore;
    private int numLogs;
    private int numKindling;
    private boolean isInWintertodt;
    private boolean needRoundNotif;
    private Instant lastActionTime;
    private int previousTimerValue;

    @Provides
    WintertodtConfig getConfig(ConfigManager configManager) {
        return configManager.getConfig(WintertodtConfig.class);
    }

    @Override
    protected void startUp() throws Exception {
        this.reset();
        this.overlayManager.add(this.overlay);
    }

    @Override
    protected void shutDown() throws Exception {
        this.overlayManager.remove(this.overlay);
        this.reset();
    }

    private void reset() {
        this.inventoryScore = 0;
        this.totalPotentialinventoryScore = 0;
        this.numLogs = 0;
        this.numKindling = 0;
        this.currentActivity = WintertodtActivity.IDLE;
        this.lastActionTime = null;
    }

    private boolean isInWintertodtRegion() {
        if (this.client.getLocalPlayer() != null) {
            return this.client.getLocalPlayer().getWorldLocation().getRegionID() == 6462;
        }
        return false;
    }

    @Subscribe
    public void onGameTick(GameTick gameTick) {
        if (!this.isInWintertodtRegion()) {
            if (this.isInWintertodt) {
                log.debug("Left Wintertodt!");
                this.reset();
                this.isInWintertodt = false;
                this.needRoundNotif = true;
            }
            return;
        }
        if (!this.isInWintertodt) {
            this.reset();
            log.debug("Entered Wintertodt!");
            this.isInWintertodt = true;
        }
        this.checkActionTimeout();
    }

    @Subscribe
    public void onVarbitChanged(VarbitChanged varbitChanged) {
        if (varbitChanged.getVarbitId() == 7980) {
            int timeToNotify = this.config.roundNotification();
            if (timeToNotify > 0 && (this.isInWintertodt || this.needRoundNotif)) {
                int timeInSeconds = varbitChanged.getValue() * 30 / 50;
                int prevTimeInSeconds = this.previousTimerValue * 30 / 50;
                log.debug("Seconds left until round start: {}", (Object)timeInSeconds);
                if (prevTimeInSeconds > timeToNotify && timeInSeconds <= timeToNotify) {
                    this.notifier.notify("Wintertodt round is about to start");
                    this.needRoundNotif = false;
                }
            }
            this.previousTimerValue = varbitChanged.getValue();
        }
    }

    private void checkActionTimeout() {
        int currentAnimation;
        if (this.currentActivity == WintertodtActivity.IDLE) {
            return;
        }
        int n = currentAnimation = this.client.getLocalPlayer() != null ? this.client.getLocalPlayer().getAnimation() : -1;
        if (currentAnimation != -1 || this.lastActionTime == null) {
            return;
        }
        Duration actionTimeout = Duration.ofSeconds(3L);
        Duration sinceAction = Duration.between(this.lastActionTime, Instant.now());
        if (sinceAction.compareTo(actionTimeout) >= 0) {
            log.debug("Activity timeout!");
            this.currentActivity = WintertodtActivity.IDLE;
        }
    }

    @Subscribe
    public void onChatMessage(ChatMessage chatMessage) {
        WintertodtInterruptType interruptType;
        if (!this.isInWintertodt) {
            return;
        }
        ChatMessageType chatMessageType = chatMessage.getType();
        if (chatMessageType != ChatMessageType.GAMEMESSAGE && chatMessageType != ChatMessageType.SPAM) {
            return;
        }
        MessageNode messageNode = chatMessage.getMessageNode();
        if (messageNode.getValue().startsWith("You carefully fletch the root")) {
            this.setActivity(WintertodtActivity.FLETCHING);
            return;
        }
        if (messageNode.getValue().startsWith("The cold of")) {
            interruptType = WintertodtInterruptType.COLD;
        } else if (messageNode.getValue().startsWith("The freezing cold attack")) {
            interruptType = WintertodtInterruptType.SNOWFALL;
        } else if (messageNode.getValue().startsWith("The brazier is broken and shrapnel")) {
            interruptType = WintertodtInterruptType.BRAZIER;
        } else if (messageNode.getValue().startsWith("You have run out of bruma roots")) {
            interruptType = WintertodtInterruptType.OUT_OF_ROOTS;
        } else if (messageNode.getValue().startsWith("Your inventory is too full")) {
            interruptType = WintertodtInterruptType.INVENTORY_FULL;
        } else if (messageNode.getValue().startsWith("You fix the brazier")) {
            interruptType = WintertodtInterruptType.FIXED_BRAZIER;
        } else if (messageNode.getValue().startsWith("You light the brazier")) {
            interruptType = WintertodtInterruptType.LIT_BRAZIER;
        } else if (messageNode.getValue().startsWith("The brazier has gone out.")) {
            interruptType = WintertodtInterruptType.BRAZIER_WENT_OUT;
        } else {
            return;
        }
        boolean wasInterrupted = false;
        boolean neverNotify = false;
        switch (interruptType) {
            case COLD: 
            case BRAZIER: 
            case SNOWFALL: {
                messageNode.setRuneLiteFormatMessage(ColorUtil.wrapWithColorTag(messageNode.getValue(), this.config.damageNotificationColor()));
                this.client.refreshChat();
                if (this.currentActivity == WintertodtActivity.WOODCUTTING || this.currentActivity == WintertodtActivity.IDLE) break;
                wasInterrupted = true;
                break;
            }
            case INVENTORY_FULL: 
            case OUT_OF_ROOTS: 
            case BRAZIER_WENT_OUT: {
                wasInterrupted = true;
                break;
            }
            case LIT_BRAZIER: 
            case FIXED_BRAZIER: {
                wasInterrupted = true;
                neverNotify = true;
            }
        }
        if (!neverNotify) {
            boolean shouldNotify = false;
            switch (interruptType) {
                case COLD: {
                    WintertodtNotifyDamage notify = this.config.notifyCold();
                    shouldNotify = notify == WintertodtNotifyDamage.ALWAYS || notify == WintertodtNotifyDamage.INTERRUPT && wasInterrupted;
                    break;
                }
                case SNOWFALL: {
                    WintertodtNotifyDamage notify = this.config.notifySnowfall();
                    shouldNotify = notify == WintertodtNotifyDamage.ALWAYS || notify == WintertodtNotifyDamage.INTERRUPT && wasInterrupted;
                    break;
                }
                case BRAZIER: {
                    WintertodtNotifyDamage notify = this.config.notifyBrazierDamage();
                    shouldNotify = notify == WintertodtNotifyDamage.ALWAYS || notify == WintertodtNotifyDamage.INTERRUPT && wasInterrupted;
                    break;
                }
                case INVENTORY_FULL: {
                    shouldNotify = this.config.notifyFullInv();
                    break;
                }
                case OUT_OF_ROOTS: {
                    shouldNotify = this.config.notifyEmptyInv();
                    break;
                }
                case BRAZIER_WENT_OUT: {
                    shouldNotify = this.config.notifyBrazierOut();
                }
            }
            if (shouldNotify) {
                this.notifyInterrupted(interruptType, wasInterrupted);
            }
        }
        if (wasInterrupted) {
            this.currentActivity = WintertodtActivity.IDLE;
        }
    }

    private void notifyInterrupted(WintertodtInterruptType interruptType, boolean wasActivityInterrupted) {
        StringBuilder str = new StringBuilder();
        str.append("Wintertodt: ");
        if (wasActivityInterrupted) {
            str.append(this.currentActivity.getActionString());
            str.append(" interrupted! ");
        }
        str.append(interruptType.getInterruptSourceString());
        String notification = str.toString();
        log.debug("Sending notification: {}", (Object)notification);
        this.notifier.notify(notification);
    }

    @Subscribe
    public void onAnimationChanged(AnimationChanged event) {
        if (!this.isInWintertodt) {
            return;
        }
        Player local = this.client.getLocalPlayer();
        if (event.getActor() != local) {
            return;
        }
        int animId = local.getAnimation();
        switch (animId) {
            case 24: 
            case 867: 
            case 869: 
            case 871: 
            case 873: 
            case 875: 
            case 877: 
            case 879: 
            case 2117: 
            case 2846: 
            case 7264: 
            case 8303: 
            case 8324: 
            case 8778: 
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
            case 11939: 
            case 11940: 
            case 12026: {
                this.setActivity(WintertodtActivity.WOODCUTTING);
                break;
            }
            case 1248: {
                this.setActivity(WintertodtActivity.FLETCHING);
                break;
            }
            case 832: {
                this.setActivity(WintertodtActivity.FEEDING_BRAZIER);
                break;
            }
            case 733: {
                this.setActivity(WintertodtActivity.LIGHTING_BRAZIER);
                break;
            }
            case 3676: 
            case 8912: {
                this.setActivity(WintertodtActivity.FIXING_BRAZIER);
            }
        }
    }

    @Subscribe
    public void onItemContainerChanged(ItemContainerChanged event) {
        ItemContainer container = event.getItemContainer();
        if (!this.isInWintertodt || container != this.client.getItemContainer(93)) {
            return;
        }
        Item[] inv = container.getItems();
        this.inventoryScore = 0;
        this.totalPotentialinventoryScore = 0;
        this.numLogs = 0;
        this.numKindling = 0;
        block4: for (Item item : inv) {
            this.inventoryScore += WintertodtPlugin.getPoints(item.getId());
            this.totalPotentialinventoryScore += WintertodtPlugin.getPotentialPoints(item.getId());
            switch (item.getId()) {
                case 20695: {
                    ++this.numLogs;
                    continue block4;
                }
                case 20696: {
                    ++this.numKindling;
                }
            }
        }
        if (this.numLogs == 0 && this.currentActivity == WintertodtActivity.FLETCHING) {
            this.currentActivity = WintertodtActivity.IDLE;
        } else if (this.numLogs == 0 && this.numKindling == 0 && this.currentActivity == WintertodtActivity.FEEDING_BRAZIER) {
            this.currentActivity = WintertodtActivity.IDLE;
        }
    }

    private void setActivity(WintertodtActivity action) {
        this.currentActivity = action;
        this.lastActionTime = Instant.now();
    }

    private static int getPoints(int id) {
        switch (id) {
            case 20695: {
                return 10;
            }
            case 20696: {
                return 25;
            }
        }
        return 0;
    }

    private static int getPotentialPoints(int id) {
        switch (id) {
            case 20695: 
            case 20696: {
                return 25;
            }
        }
        return 0;
    }

    WintertodtActivity getCurrentActivity() {
        return this.currentActivity;
    }

    int getInventoryScore() {
        return this.inventoryScore;
    }

    int getTotalPotentialinventoryScore() {
        return this.totalPotentialinventoryScore;
    }

    int getNumLogs() {
        return this.numLogs;
    }

    int getNumKindling() {
        return this.numKindling;
    }

    boolean isInWintertodt() {
        return this.isInWintertodt;
    }
}

