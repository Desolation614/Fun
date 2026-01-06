/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableSet
 *  com.google.inject.Provides
 *  javax.inject.Inject
 *  net.runelite.api.ChatMessageType
 *  net.runelite.api.Client
 *  net.runelite.api.GameState
 *  net.runelite.api.Quest
 *  net.runelite.api.QuestState
 *  net.runelite.api.events.GameStateChanged
 *  net.runelite.api.events.GameTick
 *  net.runelite.api.events.VarbitChanged
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package net.runelite.client.plugins.kingdomofmiscellania;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Provides;
import java.awt.image.BufferedImage;
import java.lang.reflect.Type;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import javax.inject.Inject;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.Quest;
import net.runelite.api.QuestState;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.VarbitChanged;
import net.runelite.client.chat.ChatColorType;
import net.runelite.client.chat.ChatMessageBuilder;
import net.runelite.client.chat.ChatMessageManager;
import net.runelite.client.chat.QueuedMessage;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.kingdomofmiscellania.KingdomConfig;
import net.runelite.client.plugins.kingdomofmiscellania.KingdomCounter;
import net.runelite.client.ui.overlay.infobox.InfoBoxManager;
import net.runelite.client.util.QuantityFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@PluginDescriptor(name="Kingdom of Miscellania", description="Show amount of approval when inside Miscellania", tags={"favor", "favour", "managing", "overlay", "approval", "coffer"}, enabledByDefault=false, forceDisabled=true)
public class KingdomPlugin
extends Plugin {
    private static final Logger log = LoggerFactory.getLogger(KingdomPlugin.class);
    private static final ImmutableSet<Integer> KINGDOM_REGION = ImmutableSet.of((Object)10044, (Object)10300);
    private static final String CONFIG_LAST_CHANGED_KEY = "lastChanged";
    private static final String CONFIG_COFFER_KEY = "coffer";
    private static final String CONFIG_APPROVAL_KEY = "approval";
    private static final String CHAT_MESSAGE_FORMAT = "Your Kingdom of Miscellania approval is %d%%, and your coffer has %s coins.";
    private static final int MAX_WITHDRAWAL_BASE = 50000;
    private static final int MAX_WITHDRAWAL_ROYAL_TROUBLE = 75000;
    private static final float APPROVAL_DECREMENT_BASE = 0.025f;
    private static final float APPROVAL_DECREMENT_ROYAL_TROUBLE = 0.01f;
    static final int MAX_APPROVAL = 127;
    private boolean loggingIn;
    @Inject
    private Client client;
    @Inject
    private InfoBoxManager infoBoxManager;
    @Inject
    private ItemManager itemManager;
    @Inject
    private KingdomConfig config;
    @Inject
    private ConfigManager configManager;
    @Inject
    private ChatMessageManager chatMessageManager;
    private KingdomCounter counter;

    @Override
    protected void shutDown() throws Exception {
        this.removeKingdomInfobox();
    }

    @Provides
    KingdomConfig getConfig(ConfigManager configManager) {
        return configManager.getConfig(KingdomConfig.class);
    }

    @Subscribe
    public void onVarbitChanged(VarbitChanged event) {
        if (event.getVarbitId() == 74 || event.getVarbitId() == 72) {
            int coffer = this.client.getVarbitValue(74);
            int approval = this.client.getVarbitValue(72);
            if (this.isThroneOfMiscellaniaCompleted() && (this.isInKingdom() || coffer > 0 && approval > 0) && (this.getCoffer() != coffer || this.getApproval() != approval)) {
                this.setLastChanged(Instant.now());
                this.setCoffer(coffer);
                this.setApproval(approval);
            }
        } else if (event.getVarpId() == 359) {
            this.processInfobox();
        }
    }

    @Subscribe
    public void onGameStateChanged(GameStateChanged event) {
        if (event.getGameState() == GameState.LOGGED_IN) {
            this.processInfobox();
        } else if (event.getGameState() == GameState.LOGGING_IN) {
            this.loggingIn = true;
        }
    }

    @Subscribe
    public void onGameTick(GameTick gameTick) {
        if (this.loggingIn) {
            this.loggingIn = false;
            this.createNotification();
        }
    }

    private void processInfobox() {
        if (this.client.getGameState() == GameState.LOGGED_IN && this.isThroneOfMiscellaniaCompleted() && this.isInKingdom()) {
            this.addKingdomInfobox();
        } else {
            this.removeKingdomInfobox();
        }
    }

    private void createNotification() {
        if (!this.config.shouldSendNotifications() || !this.isThroneOfMiscellaniaCompleted()) {
            return;
        }
        if (this.getLastChanged() == null) {
            log.debug("Kingdom Of Miscellania values not yet set. Visit Miscellania to automatically set values.");
            return;
        }
        Instant lastChanged = this.getLastChanged();
        int lastCoffer = this.getCoffer();
        int lastApproval = this.getApproval();
        int estimatedCoffer = this.estimateCoffer(lastChanged, lastCoffer);
        int estimatedApproval = this.estimateApproval(lastChanged, lastApproval);
        if (estimatedCoffer < this.config.getCofferThreshold() || KingdomPlugin.getApprovalPercent(estimatedApproval) < this.config.getApprovalThreshold()) {
            this.sendChatMessage(String.format(CHAT_MESSAGE_FORMAT, KingdomPlugin.getApprovalPercent(estimatedApproval), QuantityFormatter.quantityToStackSize(estimatedCoffer)));
        }
    }

    private void addKingdomInfobox() {
        if (this.counter == null) {
            this.counter = new KingdomCounter((BufferedImage)this.itemManager.getImage(8150), this);
            this.infoBoxManager.addInfoBox(this.counter);
            log.debug("Added Kingdom Infobox");
        }
    }

    private void removeKingdomInfobox() {
        if (this.counter != null) {
            this.infoBoxManager.removeInfoBox(this.counter);
            this.counter = null;
            log.debug("Removed Kingdom Infobox");
        }
    }

    private int estimateCoffer(Instant lastChanged, int lastCoffer) {
        int daysSince = KingdomPlugin.getNumbersOfDaysPassed(lastChanged);
        int maxDailyWithdrawal = this.isRoyalTroubleCompleted() ? 75000 : 50000;
        int maxDailyThreshold = maxDailyWithdrawal * 10;
        for (int i = 0; i < daysSince; ++i) {
            lastCoffer -= lastCoffer > maxDailyThreshold ? maxDailyWithdrawal : lastCoffer / 10;
        }
        return lastCoffer;
    }

    private int estimateApproval(Instant lastChanged, int lastApproval) {
        int daysSince = KingdomPlugin.getNumbersOfDaysPassed(lastChanged);
        float dailyPercentage = this.isRoyalTroubleCompleted() ? 0.01f : 0.025f;
        return Math.max(lastApproval -= (int)((float)daysSince * dailyPercentage * 127.0f), 0);
    }

    private static int getNumbersOfDaysPassed(Instant lastChanged) {
        lastChanged = lastChanged.truncatedTo(ChronoUnit.DAYS);
        Instant now = Instant.now().truncatedTo(ChronoUnit.DAYS);
        return (int)Duration.between(lastChanged, now).toDays();
    }

    private boolean isInKingdom() {
        return this.client.getLocalPlayer() != null && KINGDOM_REGION.contains((Object)this.client.getLocalPlayer().getWorldLocation().getRegionID());
    }

    private boolean isThroneOfMiscellaniaCompleted() {
        return this.client.getVarpValue(359) > 0;
    }

    private boolean isRoyalTroubleCompleted() {
        return Quest.ROYAL_TROUBLE.getState(this.client) == QuestState.FINISHED;
    }

    static int getApprovalPercent(int approval) {
        return approval * 100 / 127;
    }

    private void sendChatMessage(String chatMessage) {
        String message = new ChatMessageBuilder().append(ChatColorType.HIGHLIGHT).append(chatMessage).build();
        this.chatMessageManager.queue(QueuedMessage.builder().type(ChatMessageType.CONSOLE).runeLiteFormattedMessage(message).build());
    }

    private Instant getLastChanged() {
        return (Instant)this.configManager.getRSProfileConfiguration("kingdomofmiscellania", CONFIG_LAST_CHANGED_KEY, (Type)((Object)Instant.class));
    }

    private void setLastChanged(Instant lastChanged) {
        this.configManager.setRSProfileConfiguration("kingdomofmiscellania", CONFIG_LAST_CHANGED_KEY, lastChanged);
    }

    int getCoffer() {
        Integer coffer = (Integer)this.configManager.getRSProfileConfiguration("kingdomofmiscellania", CONFIG_COFFER_KEY, Integer.TYPE);
        return coffer == null ? 0 : coffer;
    }

    private void setCoffer(int coffer) {
        this.configManager.setRSProfileConfiguration("kingdomofmiscellania", CONFIG_COFFER_KEY, coffer);
    }

    int getApproval() {
        Integer approval = (Integer)this.configManager.getRSProfileConfiguration("kingdomofmiscellania", CONFIG_APPROVAL_KEY, Integer.TYPE);
        return approval == null ? 0 : approval;
    }

    private void setApproval(int approval) {
        this.configManager.setRSProfileConfiguration("kingdomofmiscellania", CONFIG_APPROVAL_KEY, approval);
    }
}

