/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.inject.Inject
 *  net.runelite.api.widgets.WidgetItem
 */
package net.runelite.client.plugins.mta;

import java.awt.Graphics2D;
import javax.inject.Inject;
import net.runelite.api.widgets.WidgetItem;
import net.runelite.client.plugins.mta.MTAConfig;

public abstract class MTARoom {
    protected final MTAConfig config;

    @Inject
    protected MTARoom(MTAConfig config) {
        this.config = config;
    }

    public abstract boolean inside();

    public void under(Graphics2D graphics2D) {
    }

    public void renderItemOverlay(Graphics2D graphics, int itemId, WidgetItem widgetItem) {
    }

    protected MTAConfig getConfig() {
        return this.config;
    }
}

