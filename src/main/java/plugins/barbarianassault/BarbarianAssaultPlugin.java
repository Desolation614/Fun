/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.inject.Provides
 *  javax.inject.Inject
 *  net.runelite.api.ChatMessageType
 *  net.runelite.api.Client
 *  net.runelite.api.events.ChatMessage
 *  net.runelite.api.events.VarbitChanged
 *  net.runelite.api.events.WidgetLoaded
 *  net.runelite.api.widgets.Widget
 */
package net.runelite.client.plugins.barbarianassault;

import com.google.inject.Provides;
import java.awt.Image;
import javax.inject.Inject;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.VarbitChanged;
import net.runelite.api.events.WidgetLoaded;
import net.runelite.api.widgets.Widget;
import net.runelite.client.chat.ChatColorType;
import net.runelite.client.chat.ChatMessageBuilder;
import net.runelite.client.chat.ChatMessageManager;
import net.runelite.client.chat.QueuedMessage;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.barbarianassault.BarbarianAssaultConfig;
import net.runelite.client.plugins.barbarianassault.GameTimer;
import net.runelite.client.plugins.barbarianassault.HealerOverlay;
import net.runelite.client.plugins.barbarianassault.Role;
import net.runelite.client.plugins.barbarianassault.Round;
import net.runelite.client.plugins.barbarianassault.TimerOverlay;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.util.ImageUtil;

@PluginDescriptor(name="Barbarian Assault", description="Show a timer to the next call change and game/wave duration in chat.", tags={"minigame", "overlay", "timer"}, forceDisabled=true)
public class BarbarianAssaultPlugin
extends Plugin {
    private static final int BA_WAVE_NUM_INDEX = 2;
    private static final String START_WAVE = "1";
    private static final String ENDGAME_REWARD_NEEDLE_TEXT = "<br>5";
    private Image clockImage;
    private String currentWave = "1";
    private GameTimer gameTime;
    private Round currentRound;
    @Inject
    private Client client;
    @Inject
    private ChatMessageManager chatMessageManager;
    @Inject
    private OverlayManager overlayManager;
    @Inject
    private BarbarianAssaultConfig config;
    @Inject
    private TimerOverlay timerOverlay;
    @Inject
    private HealerOverlay healerOverlay;

    @Provides
    BarbarianAssaultConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(BarbarianAssaultConfig.class);
    }

    @Override
    protected void startUp() throws Exception {
        this.overlayManager.add(this.timerOverlay);
        this.overlayManager.add(this.healerOverlay);
        this.clockImage = ImageUtil.loadImageResource(this.getClass(), "clock.png");
    }

    @Override
    protected void shutDown() throws Exception {
        this.overlayManager.remove(this.timerOverlay);
        this.overlayManager.remove(this.healerOverlay);
        this.gameTime = null;
        this.currentWave = START_WAVE;
        this.clockImage = null;
    }

    @Subscribe
    public void onWidgetLoaded(WidgetLoaded event) {
        switch (event.getGroupId()) {
            case 497: {
                Widget rewardWidget = this.client.getWidget(32571449);
                if (!this.config.waveTimes() || rewardWidget == null || !rewardWidget.getText().contains(ENDGAME_REWARD_NEEDLE_TEXT) || this.gameTime == null) break;
                this.announceTime("Game finished, duration: ", this.gameTime.getTime(false));
                this.gameTime = null;
                break;
            }
            case 485: {
                this.setRound(Role.ATTACKER);
                break;
            }
            case 487: {
                this.setRound(Role.DEFENDER);
                break;
            }
            case 488: {
                this.setRound(Role.HEALER);
                break;
            }
            case 486: {
                this.setRound(Role.COLLECTOR);
            }
        }
    }

    @Subscribe
    public void onVarbitChanged(VarbitChanged event) {
        if (event.getVarbitId() == 3923 && event.getValue() == 0) {
            this.currentRound = null;
            if (this.config.waveTimes() && this.gameTime != null && this.client.isInInstancedRegion()) {
                this.announceTime("Wave " + this.currentWave + " duration: ", this.gameTime.getTime(true));
            }
        }
    }

    @Subscribe
    public void onChatMessage(ChatMessage event) {
        if (event.getType() == ChatMessageType.GAMEMESSAGE && event.getMessage().startsWith("---- Wave:")) {
            String[] message = event.getMessage().split(" ");
            this.currentWave = message[2];
            if (this.currentWave.equals(START_WAVE)) {
                this.gameTime = new GameTimer();
            } else if (this.gameTime != null) {
                this.gameTime.setWaveStartTime();
            }
        }
    }

    private void setRound(Role role) {
        if (this.currentRound == null) {
            this.currentRound = new Round(role);
        }
    }

    private void announceTime(String preText, String time) {
        String chatMessage = new ChatMessageBuilder().append(ChatColorType.NORMAL).append(preText).append(ChatColorType.HIGHLIGHT).append(time).build();
        this.chatMessageManager.queue(QueuedMessage.builder().type(ChatMessageType.CONSOLE).runeLiteFormattedMessage(chatMessage).build());
    }

    Image getClockImage() {
        return this.clockImage;
    }

    public Round getCurrentRound() {
        return this.currentRound;
    }
}

