/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.inject.Inject
 *  net.runelite.api.Client
 */
package net.runelite.client.plugins.stretchedmode;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.MouseWheelEvent;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.client.input.MouseWheelListener;

public class TranslateMouseWheelListener
implements MouseWheelListener {
    private final Client client;

    @Inject
    public TranslateMouseWheelListener(Client client) {
        this.client = client;
    }

    @Override
    public MouseWheelEvent mouseWheelMoved(MouseWheelEvent event) {
        return this.translateEvent(event);
    }

    private MouseWheelEvent translateEvent(MouseWheelEvent e) {
        Dimension stretchedDimensions = this.client.getStretchedDimensions();
        Dimension realDimensions = this.client.getRealDimensions();
        int newX = (int)((double)e.getX() / ((double)stretchedDimensions.width / realDimensions.getWidth()));
        int newY = (int)((double)e.getY() / ((double)stretchedDimensions.height / realDimensions.getHeight()));
        return new MouseWheelEvent((Component)e.getSource(), e.getID(), e.getWhen(), e.getModifiers(), newX, newY, e.getClickCount(), e.isPopupTrigger(), e.getScrollType(), e.getScrollAmount(), e.getWheelRotation());
    }
}

