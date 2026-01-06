/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.plugins.cooking;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.time.Duration;
import java.time.Instant;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.ui.overlay.infobox.InfoBox;

final class FermentTimer
extends InfoBox {
    private static final Duration FERMENT_TIME = Duration.ofMillis(13800L);
    private Instant fermentTime;

    FermentTimer(BufferedImage image, Plugin plugin) {
        super(image, plugin);
        this.reset();
    }

    @Override
    public String getText() {
        int seconds = this.timeUntilFerment();
        return Integer.toString(seconds);
    }

    @Override
    public Color getTextColor() {
        int seconds = this.timeUntilFerment();
        return seconds <= 3 ? Color.RED : Color.WHITE;
    }

    @Override
    public boolean cull() {
        int seconds = this.timeUntilFerment();
        return seconds <= 0;
    }

    void reset() {
        this.fermentTime = Instant.now().plus(FERMENT_TIME);
    }

    private int timeUntilFerment() {
        return (int)Duration.between(Instant.now(), this.fermentTime).getSeconds();
    }
}

