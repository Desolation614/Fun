/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.inject.Provides
 *  javax.inject.Inject
 *  net.runelite.api.Client
 *  net.runelite.api.Preferences
 *  net.runelite.api.events.GameTick
 */
package net.runelite.client.plugins.metronome;

import com.google.inject.Provides;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.Preferences;
import net.runelite.api.events.GameTick;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.metronome.MetronomePluginConfiguration;

@PluginDescriptor(name="Metronome", description="Play a sound on a specified tick to aid in efficient skilling", tags={"skilling", "tick", "timers"}, enabledByDefault=false)
public class MetronomePlugin
extends Plugin {
    @Inject
    private Client client;
    @Inject
    private MetronomePluginConfiguration config;
    private int tickCounter = 0;
    private boolean shouldTock = false;

    @Provides
    MetronomePluginConfiguration provideConfig(ConfigManager configManager) {
        return configManager.getConfig(MetronomePluginConfiguration.class);
    }

    @Override
    protected void shutDown() {
        this.tickCounter = 0;
        this.shouldTock = false;
    }

    @Subscribe
    public void onGameTick(GameTick tick) {
        if (this.config.tickCount() == 0) {
            return;
        }
        if (++this.tickCounter % this.config.tickCount() == 0) {
            Preferences preferences = this.client.getPreferences();
            int previousVolume = preferences.getSoundEffectVolume();
            if (this.shouldTock && this.config.tockVolume() > 0) {
                preferences.setSoundEffectVolume(this.config.tockVolume());
                this.client.playSoundEffect(3930, this.config.tockVolume());
            } else if (this.config.tickVolume() > 0) {
                preferences.setSoundEffectVolume(this.config.tickVolume());
                this.client.playSoundEffect(3929, this.config.tickVolume());
            }
            preferences.setSoundEffectVolume(previousVolume);
            this.shouldTock = !this.shouldTock;
        }
    }
}

