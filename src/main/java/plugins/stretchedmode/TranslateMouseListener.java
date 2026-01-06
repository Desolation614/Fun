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
import java.awt.event.MouseEvent;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.client.input.MouseListener;

public class TranslateMouseListener
implements MouseListener {
    private final Client client;

    @Inject
    public TranslateMouseListener(Client client) {
        this.client = client;
    }

    @Override
    public MouseEvent mouseClicked(MouseEvent mouseEvent) {
        return this.translateEvent(mouseEvent);
    }

    @Override
    public MouseEvent mousePressed(MouseEvent mouseEvent) {
        return this.translateEvent(mouseEvent);
    }

    @Override
    public MouseEvent mouseReleased(MouseEvent mouseEvent) {
        return this.translateEvent(mouseEvent);
    }

    @Override
    public MouseEvent mouseEntered(MouseEvent mouseEvent) {
        return this.translateEvent(mouseEvent);
    }

    @Override
    public MouseEvent mouseExited(MouseEvent mouseEvent) {
        return this.translateEvent(mouseEvent);
    }

    @Override
    public MouseEvent mouseDragged(MouseEvent mouseEvent) {
        return this.translateEvent(mouseEvent);
    }

    @Override
    public MouseEvent mouseMoved(MouseEvent mouseEvent) {
        return this.translateEvent(mouseEvent);
    }

    private MouseEvent translateEvent(MouseEvent e) {
        Dimension stretchedDimensions = this.client.getStretchedDimensions();
        Dimension realDimensions = this.client.getRealDimensions();
        int newX = (int)((double)e.getX() / ((double)stretchedDimensions.width / realDimensions.getWidth()));
        int newY = (int)((double)e.getY() / ((double)stretchedDimensions.height / realDimensions.getHeight()));
        MouseEvent mouseEvent = new MouseEvent((Component)e.getSource(), e.getID(), e.getWhen(), e.getModifiersEx(), newX, newY, e.getClickCount(), e.isPopupTrigger(), e.getButton());
        if (e.isConsumed()) {
            mouseEvent.consume();
        }
        return mouseEvent;
    }
}

