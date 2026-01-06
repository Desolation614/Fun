/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.inject.Inject
 *  net.runelite.api.Client
 *  net.runelite.api.widgets.Widget
 */
package net.runelite.client.plugins.cluescrolls;

import java.awt.Dimension;
import java.awt.Graphics2D;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.widgets.Widget;
import net.runelite.client.plugins.cluescrolls.ClueScrollPlugin;
import net.runelite.client.plugins.cluescrolls.clues.ClueScroll;
import net.runelite.client.plugins.cluescrolls.clues.EmoteClue;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;

class ClueScrollEmoteOverlay
extends Overlay {
    private final ClueScrollPlugin plugin;
    private final Client client;
    private boolean hasScrolled;

    @Inject
    private ClueScrollEmoteOverlay(ClueScrollPlugin plugin, Client client) {
        this.setPosition(OverlayPosition.DYNAMIC);
        this.setLayer(OverlayLayer.ABOVE_WIDGETS);
        this.plugin = plugin;
        this.client = client;
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        ClueScroll clue = this.plugin.getClue();
        if (!(clue instanceof EmoteClue)) {
            this.hasScrolled = false;
            return null;
        }
        EmoteClue emoteClue = (EmoteClue)clue;
        if (!emoteClue.getFirstEmote().hasSprite()) {
            return null;
        }
        Widget emoteContainer = this.client.getWidget(14155778);
        if (emoteContainer == null || emoteContainer.isHidden()) {
            return null;
        }
        Widget emoteWindow = this.client.getWidget(0xD80000);
        if (emoteWindow == null) {
            return null;
        }
        Widget firstEmoteWidget = null;
        Widget secondEmoteWidget = null;
        for (Widget emoteWidget : emoteContainer.getDynamicChildren()) {
            if (emoteWidget.getSpriteId() == emoteClue.getFirstEmote().getSpriteId()) {
                firstEmoteWidget = emoteWidget;
                this.plugin.highlightWidget(graphics, emoteWidget, emoteWindow, null, emoteClue.getSecondEmote() != null ? "1st" : null);
                continue;
            }
            if (emoteClue.getSecondEmote() == null || emoteWidget.getSpriteId() != emoteClue.getSecondEmote().getSpriteId()) continue;
            secondEmoteWidget = emoteWidget;
            this.plugin.highlightWidget(graphics, emoteWidget, emoteWindow, null, "2nd");
        }
        if (!this.hasScrolled) {
            this.hasScrolled = true;
            this.plugin.scrollToWidget(14155777, 14155780, firstEmoteWidget, secondEmoteWidget);
        }
        return null;
    }
}

