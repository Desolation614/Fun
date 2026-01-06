/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.inject.Inject
 *  javax.inject.Singleton
 */
package net.runelite.client.input;

import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import javax.inject.Inject;
import javax.inject.Singleton;
import net.runelite.client.config.RuneLiteConfig;
import net.runelite.client.input.MouseListener;
import net.runelite.client.input.MouseWheelListener;

@Singleton
public class MouseManager {
    private static final int MOUSE_BUTTON_4 = 4;
    private final List<MouseListener> mouseListeners = new CopyOnWriteArrayList<MouseListener>();
    private final List<MouseWheelListener> mouseWheelListeners = new CopyOnWriteArrayList<MouseWheelListener>();
    private final RuneLiteConfig runeLiteConfig;

    @Inject
    private MouseManager(RuneLiteConfig runeLiteConfig) {
        this.runeLiteConfig = runeLiteConfig;
    }

    public void registerMouseListener(MouseListener mouseListener) {
        if (!this.mouseListeners.contains(mouseListener)) {
            this.mouseListeners.add(mouseListener);
        }
    }

    public void registerMouseListener(int position, MouseListener mouseListener) {
        this.mouseListeners.add(position, mouseListener);
    }

    public void unregisterMouseListener(MouseListener mouseListener) {
        this.mouseListeners.remove(mouseListener);
    }

    public void registerMouseWheelListener(MouseWheelListener mouseWheelListener) {
        if (!this.mouseWheelListeners.contains(mouseWheelListener)) {
            this.mouseWheelListeners.add(mouseWheelListener);
        }
    }

    public void registerMouseWheelListener(int position, MouseWheelListener mouseWheelListener) {
        this.mouseWheelListeners.add(position, mouseWheelListener);
    }

    public void unregisterMouseWheelListener(MouseWheelListener mouseWheelListener) {
        this.mouseWheelListeners.remove(mouseWheelListener);
    }

    public MouseEvent processMousePressed(MouseEvent mouseEvent) {
        MouseListener mouseListener;
        if (mouseEvent.isConsumed()) {
            return mouseEvent;
        }
        this.checkExtraMouseButtons(mouseEvent);
        Iterator<MouseListener> iterator = this.mouseListeners.iterator();
        while (iterator.hasNext() && !(mouseEvent = (mouseListener = iterator.next()).mousePressed(mouseEvent)).isConsumed()) {
        }
        return mouseEvent;
    }

    public MouseEvent processMouseReleased(MouseEvent mouseEvent) {
        MouseListener mouseListener;
        if (mouseEvent.isConsumed()) {
            return mouseEvent;
        }
        this.checkExtraMouseButtons(mouseEvent);
        Iterator<MouseListener> iterator = this.mouseListeners.iterator();
        while (iterator.hasNext() && !(mouseEvent = (mouseListener = iterator.next()).mouseReleased(mouseEvent)).isConsumed()) {
        }
        return mouseEvent;
    }

    public MouseEvent processMouseClicked(MouseEvent mouseEvent) {
        MouseListener mouseListener;
        if (mouseEvent.isConsumed()) {
            return mouseEvent;
        }
        this.checkExtraMouseButtons(mouseEvent);
        Iterator<MouseListener> iterator = this.mouseListeners.iterator();
        while (iterator.hasNext() && !(mouseEvent = (mouseListener = iterator.next()).mouseClicked(mouseEvent)).isConsumed()) {
        }
        return mouseEvent;
    }

    private void checkExtraMouseButtons(MouseEvent mouseEvent) {
        int button = mouseEvent.getButton();
        if (button >= 4 && this.runeLiteConfig.blockExtraMouseButtons()) {
            mouseEvent.consume();
        }
    }

    public MouseEvent processMouseEntered(MouseEvent mouseEvent) {
        MouseListener mouseListener;
        if (mouseEvent.isConsumed()) {
            return mouseEvent;
        }
        Iterator<MouseListener> iterator = this.mouseListeners.iterator();
        while (iterator.hasNext() && !(mouseEvent = (mouseListener = iterator.next()).mouseEntered(mouseEvent)).isConsumed()) {
        }
        return mouseEvent;
    }

    public MouseEvent processMouseExited(MouseEvent mouseEvent) {
        MouseListener mouseListener;
        if (mouseEvent.isConsumed()) {
            return mouseEvent;
        }
        Iterator<MouseListener> iterator = this.mouseListeners.iterator();
        while (iterator.hasNext() && !(mouseEvent = (mouseListener = iterator.next()).mouseExited(mouseEvent)).isConsumed()) {
        }
        return mouseEvent;
    }

    public MouseEvent processMouseDragged(MouseEvent mouseEvent) {
        MouseListener mouseListener;
        if (mouseEvent.isConsumed()) {
            return mouseEvent;
        }
        Iterator<MouseListener> iterator = this.mouseListeners.iterator();
        while (iterator.hasNext() && !(mouseEvent = (mouseListener = iterator.next()).mouseDragged(mouseEvent)).isConsumed()) {
        }
        return mouseEvent;
    }

    public MouseEvent processMouseMoved(MouseEvent mouseEvent) {
        MouseListener mouseListener;
        if (mouseEvent.isConsumed()) {
            return mouseEvent;
        }
        Iterator<MouseListener> iterator = this.mouseListeners.iterator();
        while (iterator.hasNext() && !(mouseEvent = (mouseListener = iterator.next()).mouseMoved(mouseEvent)).isConsumed()) {
        }
        return mouseEvent;
    }

    public MouseWheelEvent processMouseWheelMoved(MouseWheelEvent mouseWheelEvent) {
        MouseWheelListener mouseWheelListener;
        if (mouseWheelEvent.isConsumed()) {
            return mouseWheelEvent;
        }
        Iterator<MouseWheelListener> iterator = this.mouseWheelListeners.iterator();
        while (iterator.hasNext() && !(mouseWheelEvent = (mouseWheelListener = iterator.next()).mouseWheelMoved(mouseWheelEvent)).isConsumed()) {
        }
        return mouseWheelEvent;
    }
}

