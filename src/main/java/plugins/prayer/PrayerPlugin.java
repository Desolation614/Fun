/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.inject.Provides
 *  javax.annotation.Nullable
 *  javax.inject.Inject
 *  net.runelite.api.Client
 *  net.runelite.api.Item
 *  net.runelite.api.ItemContainer
 *  net.runelite.api.Prayer
 *  net.runelite.api.Skill
 *  net.runelite.api.events.GameTick
 *  net.runelite.api.events.ItemContainerChanged
 *  net.runelite.api.widgets.Widget
 */
package net.runelite.client.plugins.prayer;

import com.google.inject.Provides;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import javax.annotation.Nullable;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.Item;
import net.runelite.api.ItemContainer;
import net.runelite.api.Prayer;
import net.runelite.api.Skill;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.ItemContainerChanged;
import net.runelite.api.widgets.Widget;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.game.ItemManager;
import net.runelite.client.game.SpriteManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.prayer.PrayerBarOverlay;
import net.runelite.client.plugins.prayer.PrayerConfig;
import net.runelite.client.plugins.prayer.PrayerCounter;
import net.runelite.client.plugins.prayer.PrayerDoseOverlay;
import net.runelite.client.plugins.prayer.PrayerFlickLocation;
import net.runelite.client.plugins.prayer.PrayerFlickOverlay;
import net.runelite.client.plugins.prayer.PrayerReorder;
import net.runelite.client.plugins.prayer.PrayerRestoreType;
import net.runelite.client.plugins.prayer.PrayerType;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.ui.overlay.infobox.InfoBoxManager;

@PluginDescriptor(name="Prayer", description="Show various information related to prayer", tags={"combat", "flicking", "overlay"})
public class PrayerPlugin
extends Plugin {
    private static final int PRAYER_BONUS_VARP = 10104;
    private final PrayerCounter[] prayerCounter = new PrayerCounter[PrayerType.values().length];
    private Instant startOfLastTick = Instant.now();
    private boolean prayersActive = false;
    private int prayerBonus;
    @Inject
    private Client client;
    @Inject
    private InfoBoxManager infoBoxManager;
    @Inject
    private SpriteManager spriteManager;
    @Inject
    private OverlayManager overlayManager;
    @Inject
    private PrayerFlickOverlay flickOverlay;
    @Inject
    private PrayerDoseOverlay doseOverlay;
    @Inject
    private PrayerBarOverlay barOverlay;
    @Inject
    private PrayerConfig config;
    @Inject
    private ItemManager itemManager;
    @Inject
    private PrayerReorder prayerReorder;
    @Inject
    private EventBus eventBus;

    @Provides
    PrayerConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(PrayerConfig.class);
    }

    @Override
    protected void startUp() {
        this.overlayManager.add(this.flickOverlay);
        this.overlayManager.add(this.doseOverlay);
        this.overlayManager.add(this.barOverlay);
        this.prayerReorder.startUp();
        this.eventBus.register(this.prayerReorder);
    }

    @Override
    protected void shutDown() {
        this.overlayManager.remove(this.flickOverlay);
        this.overlayManager.remove(this.doseOverlay);
        this.overlayManager.remove(this.barOverlay);
        this.removeIndicators();
        this.prayerReorder.shutDown();
        this.eventBus.unregister(this.prayerReorder);
    }

    @Override
    public void resetConfiguration() {
        this.prayerReorder.reset();
    }

    @Subscribe
    private void onConfigChanged(ConfigChanged event) {
        if (event.getGroup().equals("prayer")) {
            if (!this.config.prayerIndicator()) {
                this.removeIndicators();
            } else if (!this.config.prayerIndicatorOverheads()) {
                this.removeOverheadsIndicators();
            }
        }
    }

    @Subscribe
    public void onItemContainerChanged(ItemContainerChanged event) {
        int id = event.getContainerId();
        if (id == 93) {
            this.updatePotionBonus(event.getItemContainer(), this.client.getItemContainer(94));
        } else if (id == 94) {
            this.prayerBonus = this.totalPrayerBonus(event.getItemContainer().getItems());
        }
    }

    @Subscribe
    public void onGameTick(GameTick tick) {
        this.prayersActive = this.isAnyPrayerActive();
        if (!this.config.prayerFlickLocation().equals((Object)PrayerFlickLocation.NONE)) {
            this.startOfLastTick = Instant.now();
        }
        if (this.config.showPrayerDoseIndicator()) {
            this.doseOverlay.onTick();
        }
        if (this.config.showPrayerBar()) {
            this.barOverlay.onTick();
        }
        if (this.config.replaceOrbText() && this.isAnyPrayerActive()) {
            this.setPrayerOrbText(this.getEstimatedTimeRemaining(true));
        }
        if (!this.config.prayerIndicator()) {
            return;
        }
        for (PrayerType prayerType : PrayerType.values()) {
            int ord = prayerType.ordinal();
            if (prayerType.isActive(this.client)) {
                if (prayerType.isOverhead() && !this.config.prayerIndicatorOverheads() || this.prayerCounter[ord] != null) continue;
                PrayerCounter counter = this.prayerCounter[ord] = new PrayerCounter(this, prayerType);
                this.spriteManager.getSpriteAsync(prayerType.getSpriteID(), 0, counter::setImage);
                this.infoBoxManager.addInfoBox(counter);
                continue;
            }
            if (this.prayerCounter[ord] == null) continue;
            this.infoBoxManager.removeInfoBox(this.prayerCounter[ord]);
            this.prayerCounter[ord] = null;
        }
    }

    private int totalPrayerBonus(Item[] items) {
        return this.client.getVarpValue(10104);
    }

    private void updatePotionBonus(ItemContainer inventory, @Nullable ItemContainer equip) {
        PrayerRestoreType type;
        boolean hasPrayerPotion = false;
        boolean hasSuperRestore = false;
        boolean hasSanfew = false;
        boolean hasWrench = false;
        block6: for (Item item : inventory.getItems()) {
            type = PrayerRestoreType.getType(item.getId());
            if (type == null) continue;
            switch (type) {
                case PRAYERPOT: {
                    hasPrayerPotion = true;
                    continue block6;
                }
                case RESTOREPOT: {
                    hasSuperRestore = true;
                    continue block6;
                }
                case SANFEWPOT: {
                    hasSanfew = true;
                    continue block6;
                }
                case HOLYWRENCH: {
                    hasWrench = true;
                }
            }
        }
        if (!hasWrench && equip != null) {
            for (Item item : equip.getItems()) {
                type = PrayerRestoreType.getType(item.getId());
                if (type != PrayerRestoreType.HOLYWRENCH) continue;
                hasWrench = true;
                break;
            }
        }
        int prayerLevel = this.client.getRealSkillLevel(Skill.PRAYER);
        int restored = 0;
        if (hasSanfew) {
            restored = Math.max(restored, 4 + (int)Math.floor((double)prayerLevel * (hasWrench ? 0.32 : 0.3)));
        }
        if (hasSuperRestore) {
            restored = Math.max(restored, 8 + (int)Math.floor((double)prayerLevel * (hasWrench ? 0.27 : 0.25)));
        }
        if (hasPrayerPotion) {
            restored = Math.max(restored, 7 + (int)Math.floor((double)prayerLevel * (hasWrench ? 0.27 : 0.25)));
        }
        this.doseOverlay.setRestoreAmount(restored);
    }

    double getTickProgress() {
        long timeSinceLastTick = Duration.between(this.startOfLastTick, Instant.now()).toMillis();
        float tickProgress = (float)(timeSinceLastTick % 600L) / 600.0f;
        return (double)tickProgress * Math.PI;
    }

    private boolean isAnyPrayerActive() {
        for (Prayer pray : Prayer.values()) {
            if (!this.client.isPrayerActive(pray)) continue;
            return true;
        }
        return false;
    }

    private void removeIndicators() {
        this.infoBoxManager.removeIf(entry -> entry instanceof PrayerCounter);
    }

    private void removeOverheadsIndicators() {
        this.infoBoxManager.removeIf(entry -> entry instanceof PrayerCounter && ((PrayerCounter)entry).getPrayerType().isOverhead());
    }

    private void setPrayerOrbText(String text) {
        Widget prayerOrbText = this.client.getWidget(10485780);
        if (prayerOrbText != null) {
            prayerOrbText.setText(text);
        }
    }

    private static int getDrainEffect(Client client) {
        int drainEffect = 0;
        for (PrayerType prayerType : PrayerType.values()) {
            if (!prayerType.isActive(client)) continue;
            drainEffect += prayerType.getDrainEffect();
        }
        return drainEffect;
    }

    String getEstimatedTimeRemaining(boolean formatForOrb) {
        int drainEffect = PrayerPlugin.getDrainEffect(this.client);
        if (drainEffect == 0) {
            return "N/A";
        }
        int drainResistance = 2 * this.prayerBonus + 60;
        double secondsPerPoint = 0.6 * ((double)drainResistance / (double)drainEffect);
        int currentPrayer = this.client.getBoostedSkillLevel(Skill.PRAYER);
        double secondsLeft = (double)currentPrayer * secondsPerPoint;
        LocalTime timeLeft = LocalTime.ofSecondOfDay((long)secondsLeft);
        if (formatForOrb && (timeLeft.getHour() > 0 || timeLeft.getMinute() > 9)) {
            long minutes = Duration.ofSeconds((long)secondsLeft).toMinutes();
            return String.format("%dm", minutes);
        }
        if (timeLeft.getHour() > 0) {
            return timeLeft.format(DateTimeFormatter.ofPattern("H:mm:ss"));
        }
        return timeLeft.format(DateTimeFormatter.ofPattern("m:ss"));
    }

    boolean isPrayersActive() {
        return this.prayersActive;
    }

    int getPrayerBonus() {
        return this.prayerBonus;
    }

    void setPrayerBonus(int prayerBonus) {
        this.prayerBonus = prayerBonus;
    }
}

