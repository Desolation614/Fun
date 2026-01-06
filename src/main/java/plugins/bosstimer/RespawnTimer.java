/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.plugins.bosstimer;

import java.awt.image.BufferedImage;
import java.time.temporal.ChronoUnit;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.bosstimer.Boss;
import net.runelite.client.ui.overlay.infobox.Timer;

class RespawnTimer
extends Timer {
    private final Boss boss;

    public RespawnTimer(Boss boss, BufferedImage bossImage, Plugin plugin, boolean halved) {
        super(boss.getSpawnTime().toMillis() / (long)(halved ? 2 : 1), ChronoUnit.MILLIS, bossImage, plugin);
        this.boss = boss;
    }

    public Boss getBoss() {
        return this.boss;
    }
}

