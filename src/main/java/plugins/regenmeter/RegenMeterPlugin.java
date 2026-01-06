/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.inject.Provides
 *  javax.inject.Inject
 *  net.runelite.api.Client
 *  net.runelite.api.GameState
 *  net.runelite.api.ItemContainer
 *  net.runelite.api.Prayer
 *  net.runelite.api.Skill
 *  net.runelite.api.events.GameStateChanged
 *  net.runelite.api.events.GameTick
 *  net.runelite.api.events.ItemContainerChanged
 *  net.runelite.api.events.VarbitChanged
 */
package net.runelite.client.plugins.regenmeter;

import com.google.inject.Provides;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.ItemContainer;
import net.runelite.api.Prayer;
import net.runelite.api.Skill;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.ItemContainerChanged;
import net.runelite.api.events.VarbitChanged;
import net.runelite.client.Notifier;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.regenmeter.RegenMeterConfig;
import net.runelite.client.plugins.regenmeter.RegenMeterOverlay;
import net.runelite.client.ui.overlay.OverlayManager;

@PluginDescriptor(name="Regeneration Meter", description="Track and show the hitpoints and special attack regeneration timers", tags={"combat", "health", "hitpoints", "special", "attack", "overlay", "notifications"})
public class RegenMeterPlugin
extends Plugin {
    private static final int SPEC_REGEN_TICKS = 50;
    private static final int NORMAL_HP_REGEN_TICKS = 100;
    @Inject
    private Client client;
    @Inject
    private OverlayManager overlayManager;
    @Inject
    private Notifier notifier;
    @Inject
    private RegenMeterOverlay overlay;
    @Inject
    private RegenMeterConfig config;
    private double hitpointsPercentage;
    private double specialPercentage;
    private int ticksSinceSpecRegen;
    private int ticksSinceHPRegen;
    private boolean wearingLightbearer;

    @Provides
    RegenMeterConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(RegenMeterConfig.class);
    }

    @Override
    protected void startUp() throws Exception {
        this.overlayManager.add(this.overlay);
    }

    @Override
    protected void shutDown() throws Exception {
        this.overlayManager.remove(this.overlay);
    }

    @Subscribe
    private void onGameStateChanged(GameStateChanged ev) {
        if (ev.getGameState() == GameState.HOPPING || ev.getGameState() == GameState.LOGIN_SCREEN) {
            this.ticksSinceHPRegen = -2;
            this.ticksSinceSpecRegen = 0;
        }
    }

    @Subscribe
    public void onItemContainerChanged(ItemContainerChanged event) {
        if (event.getContainerId() != 94) {
            return;
        }
        ItemContainer equipment = event.getItemContainer();
        boolean hasLightbearer = equipment.contains(25975);
        if (hasLightbearer == this.wearingLightbearer) {
            return;
        }
        this.ticksSinceSpecRegen = Math.max(0, this.ticksSinceSpecRegen - 25);
        this.wearingLightbearer = hasLightbearer;
    }

    @Subscribe
    private void onVarbitChanged(VarbitChanged ev) {
        if (ev.getVarbitId() == 4111) {
            this.ticksSinceHPRegen = 0;
        }
    }

    @Subscribe
    public void onGameTick(GameTick event) {
        int maxHP;
        int ticksPerSpecRegen = this.wearingLightbearer ? 25 : 50;
        this.ticksSinceSpecRegen = this.client.getVarpValue(300) == 1000 ? 0 : (this.ticksSinceSpecRegen + 1) % ticksPerSpecRegen;
        this.specialPercentage = (double)this.ticksSinceSpecRegen / (double)ticksPerSpecRegen;
        int ticksPerHPRegen = 100;
        if (this.client.isPrayerActive(Prayer.RAPID_HEAL)) {
            ticksPerHPRegen /= 2;
        }
        this.ticksSinceHPRegen = (this.ticksSinceHPRegen + 1) % ticksPerHPRegen;
        this.hitpointsPercentage = (double)this.ticksSinceHPRegen / (double)ticksPerHPRegen;
        int currentHP = this.client.getBoostedSkillLevel(Skill.HITPOINTS);
        if (currentHP == (maxHP = this.client.getRealSkillLevel(Skill.HITPOINTS)) && !this.config.showWhenNoChange()) {
            this.hitpointsPercentage = 0.0;
        } else if (currentHP > maxHP) {
            this.hitpointsPercentage = 1.0 - this.hitpointsPercentage;
        }
        if (this.config.getNotifyBeforeHpRegenSeconds() > 0 && currentHP < maxHP && this.shouldNotifyHpRegenThisTick(ticksPerHPRegen)) {
            this.notifier.notify("Your next hitpoint will regenerate soon!");
        }
    }

    private boolean shouldNotifyHpRegenThisTick(int ticksPerHPRegen) {
        int ticksBeforeHPRegen = ticksPerHPRegen - this.ticksSinceHPRegen;
        int notifyTick = (int)Math.ceil((double)this.config.getNotifyBeforeHpRegenSeconds() * 1000.0 / 600.0);
        return ticksBeforeHPRegen == notifyTick;
    }

    public double getHitpointsPercentage() {
        return this.hitpointsPercentage;
    }

    public double getSpecialPercentage() {
        return this.specialPercentage;
    }
}

