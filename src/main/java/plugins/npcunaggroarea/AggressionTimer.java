/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.plugins.npcunaggroarea;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import net.runelite.client.plugins.npcunaggroarea.NpcAggroAreaPlugin;
import net.runelite.client.ui.overlay.infobox.Timer;

class AggressionTimer
extends Timer {
    private final NpcAggroAreaPlugin plugin;

    AggressionTimer(Duration duration, BufferedImage image, NpcAggroAreaPlugin plugin) {
        super(duration.toMillis(), ChronoUnit.MILLIS, image, plugin);
        this.setTooltip("Time until NPCs become unaggressive");
        this.plugin = plugin;
    }

    @Override
    public Color getTextColor() {
        Duration timeLeft = Duration.between(Instant.now(), this.getEndTime());
        if (timeLeft.getSeconds() < 60L) {
            return Color.RED.brighter();
        }
        return Color.WHITE;
    }

    @Override
    public boolean render() {
        return this.plugin.shouldDisplayTimer() && super.render();
    }
}

