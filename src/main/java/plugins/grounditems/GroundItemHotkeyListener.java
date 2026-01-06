/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.inject.Inject
 */
package net.runelite.client.plugins.grounditems;

import java.time.Duration;
import java.time.Instant;
import javax.inject.Inject;
import net.runelite.client.plugins.grounditems.GroundItemsConfig;
import net.runelite.client.plugins.grounditems.GroundItemsPlugin;
import net.runelite.client.util.HotkeyListener;

class GroundItemHotkeyListener
extends HotkeyListener {
    private final GroundItemsPlugin plugin;
    private final GroundItemsConfig config;
    private Instant lastPress;

    @Inject
    private GroundItemHotkeyListener(GroundItemsPlugin plugin, GroundItemsConfig config) {
        super(config::hotkey);
        this.plugin = plugin;
        this.config = config;
    }

    @Override
    public void hotkeyPressed() {
        if (this.plugin.isHideAll()) {
            this.plugin.setHideAll(false);
            this.plugin.setHotKeyPressed(true);
            this.lastPress = null;
        } else if (this.lastPress != null && !this.plugin.isHotKeyPressed() && this.config.doubleTapDelay() > 0 && Duration.between(this.lastPress, Instant.now()).compareTo(Duration.ofMillis(this.config.doubleTapDelay())) < 0) {
            this.plugin.setHideAll(true);
            this.lastPress = null;
        } else {
            this.plugin.setHotKeyPressed(true);
            this.lastPress = Instant.now();
        }
    }

    @Override
    public void hotkeyReleased() {
        this.plugin.setHotKeyPressed(false);
        this.plugin.setTextBoxBounds(null);
        this.plugin.setHiddenBoxBounds(null);
        this.plugin.setHighlightBoxBounds(null);
    }
}

