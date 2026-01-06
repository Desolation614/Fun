/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.ui.components;

import java.awt.Component;
import java.awt.Container;
import java.awt.LayoutManager;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.dnd.DragSource;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BoxLayout;
import javax.swing.JLayeredPane;
import javax.swing.SwingUtilities;

public class DragAndDropReorderPane
extends JLayeredPane {
    private Point dragStartPoint;
    private Component draggingComponent;
    private int dragYOffset = 0;
    private int dragIndex = -1;
    private final List<DragListener> dragListeners = new ArrayList<DragListener>(0);

    public DragAndDropReorderPane() {
        this.setLayout(new DragAndDropReorderLayoutManager());
        DragAndDropReorderMouseAdapter mouseAdapter = new DragAndDropReorderMouseAdapter();
        this.addMouseListener(mouseAdapter);
        this.addMouseMotionListener(mouseAdapter);
    }

    @Override
    public void setLayout(LayoutManager layoutManager) {
        if (layoutManager != null && !(layoutManager instanceof DragAndDropReorderLayoutManager)) {
            throw new IllegalArgumentException("DragAndDropReorderPane only supports DragAndDropReorderLayoutManager");
        }
        super.setLayout(layoutManager);
    }

    public void addDragListener(DragListener dragListener) {
        this.dragListeners.add(dragListener);
    }

    public void removeDragListener(DragListener dragListener) {
        this.dragListeners.remove(dragListener);
    }

    private void startDragging(Point point) {
        this.draggingComponent = this.getDefaultLayerComponentAt(this.dragStartPoint);
        if (this.draggingComponent == null) {
            this.dragStartPoint = null;
            return;
        }
        this.dragYOffset = SwingUtilities.convertPoint((Component)this, (Point)this.dragStartPoint, (Component)this.draggingComponent).y;
        this.dragIndex = this.getPosition(this.draggingComponent);
        this.setLayer(this.draggingComponent, DRAG_LAYER);
        this.moveDraggingComponent(point);
    }

    private void drag(Point point) {
        this.moveDraggingComponent(point);
        Component component = this.getIntersectingComponent(this.draggingComponent.getBounds());
        if (component != null) {
            int newIndex;
            boolean dragUp;
            assert (component != this.draggingComponent);
            int targetMidY = component.getY() + component.getHeight() / 2;
            int index = this.getPosition(component);
            boolean bl = dragUp = index < this.dragIndex;
            if (dragUp && this.draggingComponent.getY() < targetMidY) {
                newIndex = index;
            } else if (!dragUp && this.draggingComponent.getY() + this.draggingComponent.getHeight() > targetMidY) {
                newIndex = index + 1;
            } else {
                return;
            }
            assert (newIndex != this.dragIndex);
            this.dragIndex = newIndex;
            this.revalidate();
        }
    }

    private void finishDragging() {
        if (this.draggingComponent != null) {
            Component draggedComponent = this.draggingComponent;
            this.setLayer(this.draggingComponent, DEFAULT_LAYER, this.dragIndex);
            this.draggingComponent = null;
            this.dragYOffset = 0;
            this.dragIndex = -1;
            this.revalidate();
            this.dragListeners.forEach(dl -> dl.onDrag(draggedComponent));
        }
        this.dragStartPoint = null;
    }

    private void moveDraggingComponent(Point point) {
        int y = point.y - this.dragYOffset;
        y = Math.max(y, 0);
        y = Math.min(y, this.getHeight() - this.draggingComponent.getHeight());
        this.draggingComponent.setLocation(new Point(0, y));
    }

    private Component getDefaultLayerComponentAt(Point point) {
        for (Component component : this.getComponentsInLayer(DEFAULT_LAYER)) {
            if (!component.contains(point.x - component.getX(), point.y - component.getY())) continue;
            return component;
        }
        return null;
    }

    private Component getIntersectingComponent(Rectangle bounds) {
        for (Component component : this.getComponentsInLayer(DEFAULT_LAYER)) {
            if (!bounds.intersects(component.getBounds())) continue;
            return component;
        }
        return null;
    }

    private class DragAndDropReorderMouseAdapter
    extends MouseAdapter {
        private DragAndDropReorderMouseAdapter() {
        }

        @Override
        public void mousePressed(MouseEvent e) {
            if (SwingUtilities.isLeftMouseButton(e) && DragAndDropReorderPane.this.getComponentCount() > 1) {
                DragAndDropReorderPane.this.dragStartPoint = e.getPoint();
            }
        }

        @Override
        public void mouseDragged(MouseEvent e) {
            if (SwingUtilities.isLeftMouseButton(e) && DragAndDropReorderPane.this.dragStartPoint != null) {
                Point point = e.getPoint();
                if (DragAndDropReorderPane.this.draggingComponent != null) {
                    DragAndDropReorderPane.this.drag(point);
                } else if (point.distance(DragAndDropReorderPane.this.dragStartPoint) > (double)DragSource.getDragThreshold()) {
                    DragAndDropReorderPane.this.startDragging(point);
                }
            }
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            if (SwingUtilities.isLeftMouseButton(e)) {
                DragAndDropReorderPane.this.finishDragging();
            }
        }
    }

    private class DragAndDropReorderLayoutManager
    extends BoxLayout {
        private DragAndDropReorderLayoutManager() {
            super(DragAndDropReorderPane.this, 1);
        }

        @Override
        public void layoutContainer(Container target) {
            if (DragAndDropReorderPane.this.draggingComponent != null) {
                Point location = DragAndDropReorderPane.this.draggingComponent.getLocation();
                DragAndDropReorderPane.this.setLayer(DragAndDropReorderPane.this.draggingComponent, JLayeredPane.DEFAULT_LAYER, DragAndDropReorderPane.this.dragIndex);
                DragAndDropReorderPane.this.revalidate();
                super.layoutContainer(target);
                DragAndDropReorderPane.this.setLayer(DragAndDropReorderPane.this.draggingComponent, JLayeredPane.DRAG_LAYER);
                DragAndDropReorderPane.this.draggingComponent.setLocation(location);
            } else {
                super.layoutContainer(target);
            }
        }
    }

    @FunctionalInterface
    public static interface DragListener {
        public void onDrag(Component var1);
    }
}

