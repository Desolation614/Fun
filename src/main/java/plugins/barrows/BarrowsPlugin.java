/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.UnmodifiableIterator
 *  com.google.inject.Provides
 *  javax.inject.Inject
 *  net.runelite.api.ChatMessageType
 *  net.runelite.api.Client
 *  net.runelite.api.GameState
 *  net.runelite.api.Item
 *  net.runelite.api.ItemContainer
 *  net.runelite.api.Player
 *  net.runelite.api.events.BeforeRender
 *  net.runelite.api.events.GameStateChanged
 *  net.runelite.api.events.WidgetClosed
 *  net.runelite.api.events.WidgetLoaded
 *  net.runelite.api.widgets.Widget
 *  org.apache.commons.lang3.ArrayUtils
 */
package net.runelite.client.plugins.barrows;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.UnmodifiableIterator;
import com.google.inject.Provides;
import java.time.temporal.ChronoUnit;
import javax.inject.Inject;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.Item;
import net.runelite.api.ItemContainer;
import net.runelite.api.Player;
import net.runelite.api.events.BeforeRender;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.WidgetClosed;
import net.runelite.api.events.WidgetLoaded;
import net.runelite.api.widgets.Widget;
import net.runelite.client.chat.ChatColorType;
import net.runelite.client.chat.ChatMessageBuilder;
import net.runelite.client.chat.ChatMessageManager;
import net.runelite.client.chat.QueuedMessage;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.game.ItemManager;
import net.runelite.client.game.SpriteManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.barrows.BarrowsBrotherSlainOverlay;
import net.runelite.client.plugins.barrows.BarrowsConfig;
import net.runelite.client.plugins.barrows.BarrowsOverlay;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.ui.overlay.infobox.InfoBoxManager;
import net.runelite.client.ui.overlay.infobox.InfoBoxPriority;
import net.runelite.client.ui.overlay.infobox.LoopTimer;
import net.runelite.client.util.QuantityFormatter;
import org.apache.commons.lang3.ArrayUtils;

@PluginDescriptor(name="Barrows Brothers", description="Show helpful information for the Barrows minigame", tags={"combat", "minigame", "bosses", "pve", "pvm"})
public class BarrowsPlugin
extends Plugin {
    private static final ImmutableList<Integer> POSSIBLE_SOLUTIONS = ImmutableList.of((Object)1638413, (Object)1638415, (Object)0x190011);
    private static final long PRAYER_DRAIN_INTERVAL_MS = 18200L;
    private static final int CRYPT_REGION_ID = 14231;
    private static final int BARROWS_REGION_ID = 14131;
    private LoopTimer barrowsPrayerDrainTimer;
    private Widget puzzleAnswer;
    @Inject
    private OverlayManager overlayManager;
    @Inject
    private BarrowsOverlay barrowsOverlay;
    @Inject
    private BarrowsBrotherSlainOverlay brotherOverlay;
    @Inject
    private Client client;
    @Inject
    private ItemManager itemManager;
    @Inject
    private SpriteManager spriteManager;
    @Inject
    private InfoBoxManager infoBoxManager;
    @Inject
    private ChatMessageManager chatMessageManager;
    @Inject
    private BarrowsConfig config;

    @Provides
    BarrowsConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(BarrowsConfig.class);
    }

    @Override
    protected void startUp() throws Exception {
        this.overlayManager.add(this.barrowsOverlay);
        this.overlayManager.add(this.brotherOverlay);
    }

    @Override
    protected void shutDown() {
        Widget barrowsBrothers;
        this.overlayManager.remove(this.barrowsOverlay);
        this.overlayManager.remove(this.brotherOverlay);
        this.puzzleAnswer = null;
        this.stopPrayerDrainTimer();
        Widget potential = this.client.getWidget(1572869);
        if (potential != null) {
            potential.setHidden(false);
        }
        if ((barrowsBrothers = this.client.getWidget(1572868)) != null) {
            barrowsBrothers.setHidden(false);
        }
    }

    @Subscribe
    public void onConfigChanged(ConfigChanged event) {
        if (event.getGroup().equals("barrows") && !this.config.showPrayerDrainTimer()) {
            this.stopPrayerDrainTimer();
        }
    }

    @Subscribe
    public void onGameStateChanged(GameStateChanged event) {
        if (event.getGameState() == GameState.LOGGED_IN) {
            boolean isInCrypt = this.isInCrypt();
            if (!isInCrypt && this.barrowsPrayerDrainTimer != null) {
                this.stopPrayerDrainTimer();
            } else if (isInCrypt && this.barrowsPrayerDrainTimer == null) {
                this.startPrayerDrainTimer();
            }
        }
    }

    @Subscribe
    public void onWidgetLoaded(WidgetLoaded event) {
        if (event.getGroupId() == 155 && this.config.showChestValue()) {
            ItemContainer barrowsRewardContainer = this.client.getItemContainer(141);
            if (barrowsRewardContainer == null) {
                return;
            }
            Item[] items = barrowsRewardContainer.getItems();
            long chestPrice = 0L;
            for (Item item : items) {
                long itemStack = (long)this.itemManager.getItemPrice(item.getId()) * (long)item.getQuantity();
                chestPrice += itemStack;
            }
            ChatMessageBuilder message = new ChatMessageBuilder().append(ChatColorType.HIGHLIGHT).append("Your chest is worth around ").append(QuantityFormatter.formatNumber(chestPrice)).append(" coins.").append(ChatColorType.NORMAL);
            this.chatMessageManager.queue(QueuedMessage.builder().type(ChatMessageType.ITEM_EXAMINE).runeLiteFormattedMessage(message.build()).build());
        } else if (event.getGroupId() == 25) {
            int answer = this.client.getWidget(1638403).getModelId() - 3;
            this.puzzleAnswer = null;
            UnmodifiableIterator unmodifiableIterator = POSSIBLE_SOLUTIONS.iterator();
            while (unmodifiableIterator.hasNext()) {
                int puzzleComponent = (Integer)unmodifiableIterator.next();
                Widget widgetToCheck = this.client.getWidget(puzzleComponent);
                if (widgetToCheck == null || widgetToCheck.getModelId() != answer) continue;
                this.puzzleAnswer = widgetToCheck;
                break;
            }
        }
    }

    @Subscribe
    public void onBeforeRender(BeforeRender beforeRender) {
        Widget potential;
        Widget barrowsBrothers = this.client.getWidget(1572868);
        if (barrowsBrothers != null) {
            barrowsBrothers.setHidden(true);
        }
        if ((potential = this.client.getWidget(1572869)) != null) {
            potential.setHidden(true);
        }
    }

    @Subscribe
    public void onWidgetClosed(WidgetClosed widgetClosed) {
        if (widgetClosed.getGroupId() == 25) {
            this.puzzleAnswer = null;
        }
    }

    private void startPrayerDrainTimer() {
        if (this.config.showPrayerDrainTimer()) {
            assert (this.barrowsPrayerDrainTimer == null);
            LoopTimer loopTimer = new LoopTimer(18200L, ChronoUnit.MILLIS, null, this, true);
            this.spriteManager.getSpriteAsync(779, 0, loopTimer);
            loopTimer.setPriority(InfoBoxPriority.MED);
            loopTimer.setTooltip("Prayer Drain");
            this.infoBoxManager.addInfoBox(loopTimer);
            this.barrowsPrayerDrainTimer = loopTimer;
        }
    }

    private void stopPrayerDrainTimer() {
        this.infoBoxManager.removeInfoBox(this.barrowsPrayerDrainTimer);
        this.barrowsPrayerDrainTimer = null;
    }

    private boolean isInCrypt() {
        Player localPlayer = this.client.getLocalPlayer();
        return localPlayer != null && localPlayer.getWorldLocation().getRegionID() == 14231;
    }

    boolean isBarrowsLoaded() {
        return ArrayUtils.contains((int[])this.client.getMapRegions(), (int)14131);
    }

    public Widget getPuzzleAnswer() {
        return this.puzzleAnswer;
    }
}

