/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.inject.Provides
 *  javax.inject.Inject
 *  net.runelite.api.ChatMessageType
 *  net.runelite.api.Client
 *  net.runelite.api.GameState
 *  net.runelite.api.events.GameStateChanged
 *  net.runelite.api.events.GameTick
 */
package net.runelite.client.plugins.dailytaskindicators;

import com.google.inject.Provides;
import javax.inject.Inject;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.client.chat.ChatColorType;
import net.runelite.client.chat.ChatMessageBuilder;
import net.runelite.client.chat.ChatMessageManager;
import net.runelite.client.chat.QueuedMessage;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.dailytaskindicators.DailyTasksConfig;

@PluginDescriptor(name="Daily Task Indicator", description="Show chat notifications for daily tasks upon login", forceDisabled=true)
public class DailyTasksPlugin
extends Plugin {
    private static final int ONE_DAY = 86400000;
    private static final String HERB_BOX_MESSAGE = "You have herb boxes waiting to be collected at NMZ.";
    private static final int HERB_BOX_MAX = 15;
    private static final int HERB_BOX_COST = 9500;
    private static final String STAVES_MESSAGE = "You have battlestaves waiting to be collected from Zaff.";
    private static final String ESSENCE_MESSAGE = "You have essence waiting to be collected from Wizard Cromperty.";
    private static final String RUNES_MESSAGE = "You have random runes waiting to be collected from Lundail.";
    private static final String SAND_MESSAGE = "You have sand waiting to be collected from Bert.";
    private static final int SAND_QUEST_COMPLETE = 160;
    private static final String FLAX_MESSAGE = "You have bowstrings waiting to be converted from flax from the Flax keeper.";
    private static final String ARROWS_MESSAGE = "You have ogre arrows waiting to be collected from Rantz.";
    private static final String BONEMEAL_MESSAGE = "You have bonemeal and slime waiting to be collected from Robin.";
    private static final int BONEMEAL_PER_DIARY = 13;
    private static final String DYNAMITE_MESSAGE = "You have dynamite waiting to be collected from Thirus.";
    @Inject
    private Client client;
    @Inject
    private DailyTasksConfig config;
    @Inject
    private ChatMessageManager chatMessageManager;
    private long lastReset;
    private boolean loggingIn;

    @Provides
    DailyTasksConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(DailyTasksConfig.class);
    }

    @Override
    public void startUp() {
        this.loggingIn = true;
    }

    @Override
    public void shutDown() {
        this.lastReset = 0L;
    }

    @Subscribe
    public void onGameStateChanged(GameStateChanged event) {
        if (event.getGameState() == GameState.LOGGING_IN) {
            this.loggingIn = true;
        }
    }

    @Subscribe
    public void onGameTick(GameTick event) {
        boolean dailyReset;
        long currentTime = System.currentTimeMillis();
        boolean bl = dailyReset = !this.loggingIn && currentTime - this.lastReset > 86400000L;
        if ((dailyReset || this.loggingIn) && this.client.getVarcIntValue(103) == 1) {
            this.lastReset = (long)Math.floor(currentTime / 86400000L) * 86400000L;
            this.loggingIn = false;
            if (this.config.showHerbBoxes()) {
                this.checkHerbBoxes(dailyReset);
            }
            if (this.config.showStaves()) {
                this.checkStaves(dailyReset);
            }
            if (this.config.showEssence()) {
                this.checkEssence(dailyReset);
            }
            if (this.config.showRunes()) {
                this.checkRunes(dailyReset);
            }
            if (this.config.showSand()) {
                this.checkSand(dailyReset);
            }
            if (this.config.showFlax()) {
                this.checkFlax(dailyReset);
            }
            if (this.config.showBonemeal()) {
                this.checkBonemeal(dailyReset);
            }
            if (this.config.showDynamite()) {
                this.checkDynamite(dailyReset);
            }
            if (this.config.showArrows()) {
                this.checkArrows(dailyReset);
            }
        }
    }

    private void checkHerbBoxes(boolean dailyReset) {
        if (this.client.getVarbitValue(1777) == 0 && this.client.getVarpValue(1060) >= 9500 && (this.client.getVarbitValue(3961) < 15 || dailyReset)) {
            this.sendChatMessage(HERB_BOX_MESSAGE);
        }
    }

    private void checkStaves(boolean dailyReset) {
        if (this.client.getVarbitValue(4479) == 1 && (this.client.getVarbitValue(4539) == 0 || dailyReset)) {
            this.sendChatMessage(STAVES_MESSAGE);
        }
    }

    private void checkEssence(boolean dailyReset) {
        if (this.client.getVarbitValue(4459) == 1 && (this.client.getVarbitValue(4547) == 0 || dailyReset)) {
            this.sendChatMessage(ESSENCE_MESSAGE);
        }
    }

    private void checkRunes(boolean dailyReset) {
        if (this.client.getVarbitValue(4466) == 1 && (this.client.getVarbitValue(4540) == 0 || dailyReset)) {
            this.sendChatMessage(RUNES_MESSAGE);
        }
    }

    private void checkSand(boolean dailyReset) {
        if (this.client.getVarbitValue(1777) != 2 && this.client.getVarbitValue(1527) >= 160 && (this.client.getVarbitValue(4549) == 0 || dailyReset)) {
            this.sendChatMessage(SAND_MESSAGE);
        }
    }

    private void checkFlax(boolean dailyReset) {
        if (this.client.getVarbitValue(4475) == 1 && (this.client.getVarbitValue(4559) == 0 || dailyReset)) {
            this.sendChatMessage(FLAX_MESSAGE);
        }
    }

    private void checkArrows(boolean dailyReset) {
        if (this.client.getVarbitValue(4471) == 1 && (this.client.getVarbitValue(4563) == 0 || dailyReset)) {
            this.sendChatMessage(ARROWS_MESSAGE);
        }
    }

    private void checkBonemeal(boolean dailyReset) {
        if (this.client.getVarbitValue(4488) == 1) {
            int collected = this.client.getVarbitValue(4543);
            int max = 13;
            if (this.client.getVarbitValue(4489) == 1) {
                max += 13;
                if (this.client.getVarbitValue(4490) == 1) {
                    max += 13;
                }
            }
            if (collected < max || dailyReset) {
                this.sendChatMessage(BONEMEAL_MESSAGE);
            }
        }
    }

    private void checkDynamite(boolean dailyReset) {
        if (this.client.getVarbitValue(7926) == 1 && (this.client.getVarbitValue(7939) == 0 || dailyReset)) {
            this.sendChatMessage(DYNAMITE_MESSAGE);
        }
    }

    private void sendChatMessage(String chatMessage) {
        String message = new ChatMessageBuilder().append(ChatColorType.HIGHLIGHT).append(chatMessage).build();
        this.chatMessageManager.queue(QueuedMessage.builder().type(ChatMessageType.CONSOLE).runeLiteFormattedMessage(message).build());
    }
}

