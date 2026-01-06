/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.MoreObjects
 *  com.google.common.primitives.Ints
 *  javax.inject.Inject
 *  javax.inject.Singleton
 *  net.runelite.api.ChatMessageType
 *  net.runelite.api.Client
 *  net.runelite.api.GameState
 *  net.runelite.api.events.BeforeRender
 *  net.runelite.api.events.ClientTick
 *  net.runelite.api.events.FocusChanged
 *  net.runelite.api.widgets.Widget
 *  net.runelite.api.widgets.WidgetItem
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.slf4j.Marker
 *  org.slf4j.MarkerFactory
 */
package net.runelite.client.ui.overlay;

import com.google.common.base.MoreObjects;
import com.google.common.primitives.Ints;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.swing.SwingUtilities;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.events.BeforeRender;
import net.runelite.api.events.ClientTick;
import net.runelite.api.events.FocusChanged;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetItem;
import net.runelite.client.chat.ChatMessageManager;
import net.runelite.client.chat.QueuedMessage;
import net.runelite.client.config.RuneLiteConfig;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.OverlayMenuClicked;
import net.runelite.client.input.KeyManager;
import net.runelite.client.input.MouseAdapter;
import net.runelite.client.input.MouseManager;
import net.runelite.client.ui.ClientUI;
import net.runelite.client.ui.JagexColors;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayBounds;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.ui.overlay.OverlayMenuEntry;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayUtil;
import net.runelite.client.ui.overlay.WidgetOverlay;
import net.runelite.client.util.ColorUtil;
import net.runelite.client.util.HotkeyListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

@Singleton
public class OverlayRenderer
extends MouseAdapter {
    private static final Logger log = LoggerFactory.getLogger(OverlayRenderer.class);
    private static final Marker DEDUPLICATE = MarkerFactory.getMarker((String)"DEDUPLICATE");
    private static final int BORDER = 5;
    private static final int BORDER_TOP = 20;
    private static final int PADDING = 2;
    private static final int OVERLAY_RESIZE_TOLERANCE = 5;
    private static final Dimension SNAP_CORNER_SIZE = new Dimension(80, 80);
    private static final Color SNAP_CORNER_COLOR = new Color(0, 255, 255, 50);
    private static final Color SNAP_CORNER_ACTIVE_COLOR = new Color(0, 255, 0, 100);
    private static final Color MOVING_OVERLAY_COLOR = new Color(255, 255, 0, 100);
    private static final Color MOVING_OVERLAY_ACTIVE_COLOR = new Color(255, 255, 0, 200);
    private static final Color MOVING_OVERLAY_TARGET_COLOR = Color.RED;
    private static final Color MOVING_OVERLAY_RESIZING_COLOR = new Color(255, 0, 255, 200);
    private final Client client;
    private final OverlayManager overlayManager;
    private final RuneLiteConfig runeLiteConfig;
    private final ClientUI clientUI;
    private final EventBus eventBus;
    private final ChatMessageManager chatMessageManager;
    private final Point overlayOffset = new Point();
    private final Point mousePosition = new Point();
    private Overlay currentManagedOverlay;
    private Overlay dragTargetOverlay;
    private Rectangle currentManagedBounds;
    private boolean inOverlayManagingMode;
    private boolean inOverlayResizingMode;
    private boolean inOverlayDraggingMode;
    private boolean startedMovingOverlay;
    private Overlay curHoveredOverlay;
    private Overlay lastHoveredOverlay;
    private Rectangle viewportBounds;
    private Rectangle chatboxBounds;
    private boolean chatboxHidden;
    private boolean isResizeable;
    private OverlayBounds emptySnapCorners;
    private OverlayBounds snapCorners;
    private boolean dragWarn;

    @Inject
    private OverlayRenderer(Client client2, OverlayManager overlayManager, RuneLiteConfig runeLiteConfig, MouseManager mouseManager, KeyManager keyManager, ClientUI clientUI, EventBus eventBus, ChatMessageManager chatMessageManager) {
        this.client = client2;
        this.overlayManager = overlayManager;
        this.runeLiteConfig = runeLiteConfig;
        this.clientUI = clientUI;
        this.eventBus = eventBus;
        this.chatMessageManager = chatMessageManager;
        HotkeyListener hotkeyListener = new HotkeyListener(runeLiteConfig::dragHotkey){

            @Override
            public void hotkeyPressed() {
                OverlayRenderer.this.inOverlayManagingMode = true;
            }

            @Override
            public void hotkeyReleased() {
                if (OverlayRenderer.this.inOverlayManagingMode) {
                    OverlayRenderer.this.inOverlayManagingMode = false;
                    OverlayRenderer.this.resetOverlayManagementMode();
                }
            }
        };
        keyManager.registerKeyListener(hotkeyListener);
        mouseManager.registerMouseListener(this);
        eventBus.register(this);
    }

    @Subscribe
    public void onFocusChanged(FocusChanged event) {
        if (!event.isFocused()) {
            if (this.inOverlayManagingMode) {
                this.inOverlayManagingMode = false;
                this.resetOverlayManagementMode();
            }
            this.curHoveredOverlay = null;
        }
    }

    @Subscribe
    protected void onClientTick(ClientTick t) {
        this.lastHoveredOverlay = this.curHoveredOverlay;
        Overlay overlay = this.curHoveredOverlay;
        if (overlay == null || this.client.isMenuOpen()) {
            return;
        }
        boolean shift = this.client.isKeyPressed(81);
        if (!shift) {
            return;
        }
        List<OverlayMenuEntry> menuEntries = overlay.getMenuEntries();
        if (menuEntries.isEmpty()) {
            return;
        }
        for (int i = menuEntries.size() - 1; i >= 0; --i) {
            OverlayMenuEntry overlayMenuEntry = menuEntries.get(i);
            this.client.createMenuEntry(-1).setOption(overlayMenuEntry.getOption()).setTarget(ColorUtil.wrapWithColorTag(overlayMenuEntry.getTarget(), JagexColors.MENU_TARGET)).setType(overlayMenuEntry.getMenuAction()).onClick((Consumer)MoreObjects.firstNonNull(overlayMenuEntry.callback, e -> this.eventBus.post(new OverlayMenuClicked(overlayMenuEntry, overlay))));
        }
    }

    @Subscribe
    public void onBeforeRender(BeforeRender event) {
        this.curHoveredOverlay = null;
        if (this.client.getGameState() == GameState.LOGGED_IN) {
            if (this.shouldInvalidateBounds()) {
                this.emptySnapCorners = this.buildSnapCorners();
            }
            this.snapCorners = new OverlayBounds(this.emptySnapCorners);
        }
    }

    public void renderOverlayLayer(Graphics2D graphics, OverlayLayer layer) {
        Collection<Overlay> overlays = this.overlayManager.getLayer(layer);
        this.renderOverlays(graphics, overlays, layer);
    }

    public void renderAfterInterface(Graphics2D graphics, int interfaceId, Collection<WidgetItem> widgetItems) {
        Collection<Overlay> overlays = this.overlayManager.getForInterface(interfaceId);
        this.overlayManager.setWidgetItems(widgetItems);
        this.renderOverlays(graphics, overlays, OverlayLayer.ABOVE_WIDGETS);
        this.overlayManager.setWidgetItems(Collections.emptyList());
    }

    public void renderAfterLayer(Graphics2D graphics, Widget layer, Collection<WidgetItem> widgetItems) {
        Collection<Overlay> overlays = this.overlayManager.getForLayer(layer.getId());
        this.overlayManager.setWidgetItems(widgetItems);
        this.renderOverlays(graphics, overlays, OverlayLayer.ABOVE_WIDGETS);
        this.overlayManager.setWidgetItems(Collections.emptyList());
    }

    private void renderOverlays(Graphics2D graphics, Collection<Overlay> overlays, OverlayLayer layer) {
        if (overlays == null || overlays.isEmpty() || this.client.getGameState() != GameState.LOGGED_IN) {
            return;
        }
        OverlayUtil.setGraphicProperties(graphics);
        if (this.inOverlayDraggingMode && layer == OverlayLayer.UNDER_WIDGETS && this.currentManagedOverlay != null && this.currentManagedOverlay.isSnappable()) {
            OverlayBounds translatedSnapCorners = this.snapCorners.translated(-OverlayRenderer.SNAP_CORNER_SIZE.width, -OverlayRenderer.SNAP_CORNER_SIZE.height);
            Color previous = graphics.getColor();
            for (Rectangle corner : translatedSnapCorners.getBounds()) {
                graphics.setColor(corner.contains(this.mousePosition) ? SNAP_CORNER_ACTIVE_COLOR : SNAP_CORNER_COLOR);
                graphics.fill(corner);
            }
            graphics.setColor(previous);
        }
        AffineTransform transform = graphics.getTransform();
        Stroke stroke = graphics.getStroke();
        Composite composite = graphics.getComposite();
        Paint paint = graphics.getPaint();
        RenderingHints renderingHints = graphics.getRenderingHints();
        Color background = graphics.getBackground();
        Rectangle clip = this.clipBounds(layer);
        graphics.setClip(clip);
        for (Overlay overlay : overlays) {
            Point location;
            OverlayPosition overlayPosition = this.getCorrectedOverlayPosition(overlay);
            Rectangle bounds = overlay.getBounds();
            Dimension dimension = bounds.getSize();
            Point preferredLocation = overlay.getPreferredLocation();
            Rectangle snapCorner = null;
            if (overlayPosition != OverlayPosition.DYNAMIC && overlayPosition != OverlayPosition.TOOLTIP && overlayPosition != OverlayPosition.DETACHED && preferredLocation == null) {
                snapCorner = this.snapCorners.forPosition(overlayPosition);
                Point translation = OverlayUtil.transformPosition(overlayPosition, dimension);
                int destX = snapCorner.x + translation.x;
                int destY = snapCorner.y + translation.y;
                location = this.clampOverlayLocation(destX, destY, dimension.width, dimension.height, overlay);
            } else {
                location = preferredLocation != null ? preferredLocation : bounds.getLocation();
                location = this.clampOverlayLocation(location.x, location.y, dimension.width, dimension.height, overlay);
            }
            if (overlay.getPreferredSize() != null) {
                bounds.setSize(overlay.getPreferredSize());
            }
            this.safeRender(overlay, graphics, location);
            if (snapCorner != null && bounds.width + bounds.height > 0) {
                OverlayUtil.shiftSnapCorner(overlayPosition, snapCorner, bounds, 2);
            }
            graphics.setTransform(transform);
            graphics.setStroke(stroke);
            graphics.setComposite(composite);
            graphics.setPaint(paint);
            graphics.setRenderingHints(renderingHints);
            graphics.setBackground(background);
            if (!graphics.getClip().equals(clip)) {
                graphics.setClip(clip);
            }
            if (bounds.isEmpty()) continue;
            if (this.inOverlayManagingMode && overlay.isMovable()) {
                Color boundsColor;
                if (this.inOverlayResizingMode && this.currentManagedOverlay == overlay) {
                    boundsColor = MOVING_OVERLAY_RESIZING_COLOR;
                } else if (this.inOverlayDraggingMode && this.currentManagedOverlay == overlay) {
                    boundsColor = MOVING_OVERLAY_ACTIVE_COLOR;
                } else if (this.inOverlayDraggingMode && overlay.isDragTargetable() && this.currentManagedOverlay.isDragTargetable() && this.currentManagedOverlay.getBounds().intersects(bounds)) {
                    boundsColor = MOVING_OVERLAY_TARGET_COLOR;
                    assert (this.currentManagedOverlay != overlay);
                    this.dragTargetOverlay = overlay;
                } else {
                    boundsColor = MOVING_OVERLAY_COLOR;
                }
                graphics.setColor(boundsColor);
                graphics.draw(bounds);
                graphics.setPaint(paint);
            }
            if (this.client.isMenuOpen() || this.client.isWidgetSelected() || !bounds.contains(this.mousePosition)) continue;
            this.curHoveredOverlay = overlay;
            overlay.onMouseOver();
        }
    }

    @Override
    public MouseEvent mousePressed(MouseEvent mouseEvent) {
        Point mousePoint = mouseEvent.getPoint();
        this.mousePosition.setLocation(mousePoint);
        if (!this.inOverlayManagingMode) {
            return mouseEvent;
        }
        this.currentManagedOverlay = this.lastHoveredOverlay;
        if (this.currentManagedOverlay == null || !this.currentManagedOverlay.isMovable()) {
            return mouseEvent;
        }
        if (SwingUtilities.isRightMouseButton(mouseEvent)) {
            if (this.currentManagedOverlay.isResettable()) {
                this.overlayManager.resetOverlay(this.currentManagedOverlay);
            }
        } else if (SwingUtilities.isLeftMouseButton(mouseEvent)) {
            Point offset = new Point(mousePoint.x, mousePoint.y);
            offset.translate(-this.currentManagedOverlay.getBounds().x, -this.currentManagedOverlay.getBounds().y);
            this.overlayOffset.setLocation(offset);
            this.inOverlayResizingMode = this.currentManagedOverlay != null && this.currentManagedOverlay.isResizable() && this.clientUI.getCurrentCursor() != this.clientUI.getDefaultCursor();
            this.inOverlayDraggingMode = !this.inOverlayResizingMode;
            this.startedMovingOverlay = true;
            this.currentManagedBounds = new Rectangle(this.currentManagedOverlay.getBounds());
        } else {
            return mouseEvent;
        }
        mouseEvent.consume();
        return mouseEvent;
    }

    @Override
    public MouseEvent mouseMoved(MouseEvent mouseEvent) {
        Point mousePoint = mouseEvent.getPoint();
        this.mousePosition.setLocation(mousePoint);
        if (!this.inOverlayManagingMode) {
            return mouseEvent;
        }
        if (!this.inOverlayResizingMode && !this.inOverlayDraggingMode) {
            this.currentManagedOverlay = this.lastHoveredOverlay;
        }
        if (this.currentManagedOverlay == null || !this.currentManagedOverlay.isResizable()) {
            this.clientUI.setCursor(this.clientUI.getDefaultCursor());
            return mouseEvent;
        }
        Rectangle toleranceRect = new Rectangle(this.currentManagedOverlay.getBounds());
        toleranceRect.grow(-5, -5);
        int outcode = toleranceRect.outcode(mouseEvent.getPoint());
        switch (outcode) {
            case 2: {
                this.clientUI.setCursor(Cursor.getPredefinedCursor(8));
                break;
            }
            case 3: {
                this.clientUI.setCursor(Cursor.getPredefinedCursor(6));
                break;
            }
            case 1: {
                this.clientUI.setCursor(Cursor.getPredefinedCursor(10));
                break;
            }
            case 9: {
                this.clientUI.setCursor(Cursor.getPredefinedCursor(4));
                break;
            }
            case 8: {
                this.clientUI.setCursor(Cursor.getPredefinedCursor(9));
                break;
            }
            case 12: {
                this.clientUI.setCursor(Cursor.getPredefinedCursor(5));
                break;
            }
            case 4: {
                this.clientUI.setCursor(Cursor.getPredefinedCursor(11));
                break;
            }
            case 6: {
                this.clientUI.setCursor(Cursor.getPredefinedCursor(7));
                break;
            }
            default: {
                this.clientUI.setCursor(this.clientUI.getDefaultCursor());
            }
        }
        return mouseEvent;
    }

    @Override
    public MouseEvent mouseDragged(MouseEvent mouseEvent) {
        Rectangle canvasRect;
        Point p = mouseEvent.getPoint();
        this.mousePosition.setLocation(p);
        if (!this.inOverlayManagingMode) {
            return mouseEvent;
        }
        if (this.currentManagedOverlay == null) {
            return mouseEvent;
        }
        if (this.dragTargetOverlay != null && !this.currentManagedOverlay.getBounds().intersects(this.dragTargetOverlay.getBounds())) {
            this.dragTargetOverlay = null;
        }
        if (!(canvasRect = new Rectangle(this.client.getRealDimensions())).contains(p)) {
            return mouseEvent;
        }
        if (this.inOverlayResizingMode) {
            int left = p.x - this.currentManagedBounds.x;
            int top = p.y - this.currentManagedBounds.y;
            int originalX = this.currentManagedBounds.x;
            int originalY = this.currentManagedBounds.y;
            int x = originalX;
            int y = originalY;
            int width = this.currentManagedBounds.width;
            int height = this.currentManagedBounds.height;
            switch (this.clientUI.getCurrentCursor().getType()) {
                case 8: {
                    y += top;
                    height -= top;
                    break;
                }
                case 6: {
                    x += left;
                    y += top;
                    width -= left;
                    height -= top;
                    break;
                }
                case 10: {
                    x += left;
                    width -= left;
                    break;
                }
                case 4: {
                    x += left;
                    width -= left;
                    height = top;
                    break;
                }
                case 9: {
                    height = top;
                    break;
                }
                case 5: {
                    width = left;
                    height = top;
                    break;
                }
                case 11: {
                    width = left;
                    break;
                }
                case 7: {
                    y += top;
                    width = left;
                    height -= top;
                    break;
                }
            }
            int minOverlaySize = this.currentManagedOverlay.getMinimumSize();
            int widthOverflow = Math.max(0, minOverlaySize - width);
            int heightOverflow = Math.max(0, minOverlaySize - height);
            int dx = x - originalX;
            int dy = y - originalY;
            if (widthOverflow > 0) {
                width = minOverlaySize;
                if (dx > 0) {
                    x -= widthOverflow;
                }
            }
            if (heightOverflow > 0) {
                height = minOverlaySize;
                if (dy > 0) {
                    y -= heightOverflow;
                }
            }
            this.currentManagedBounds.setRect(x, y, width, height);
            this.currentManagedOverlay.setPreferredSize(new Dimension(this.currentManagedBounds.width, this.currentManagedBounds.height));
            if (this.currentManagedOverlay.getPreferredLocation() != null) {
                this.currentManagedOverlay.setPreferredLocation(this.currentManagedBounds.getLocation());
            }
        } else if (this.inOverlayDraggingMode) {
            Point overlayPosition = new Point(p);
            overlayPosition.translate(-this.overlayOffset.x, -this.overlayOffset.y);
            Rectangle overlayBounds = this.currentManagedOverlay.getBounds();
            overlayPosition = this.clampOverlayLocation(overlayPosition.x, overlayPosition.y, overlayBounds.width, overlayBounds.height, this.currentManagedOverlay);
            this.currentManagedOverlay.setPreferredPosition(null);
            this.currentManagedOverlay.setPreferredLocation(overlayPosition);
        } else {
            return mouseEvent;
        }
        if (this.startedMovingOverlay) {
            this.overlayManager.rebuildOverlayLayers();
            this.startedMovingOverlay = false;
        }
        return mouseEvent;
    }

    @Override
    public MouseEvent mouseReleased(MouseEvent mouseEvent) {
        Point mousePoint = mouseEvent.getPoint();
        this.mousePosition.setLocation(mousePoint);
        if (!this.inOverlayManagingMode || this.currentManagedOverlay == null || !this.inOverlayDraggingMode && !this.inOverlayResizingMode) {
            return mouseEvent;
        }
        if (this.dragTargetOverlay != null && this.dragTargetOverlay.onDrag(this.currentManagedOverlay)) {
            mouseEvent.consume();
            this.resetOverlayManagementMode();
            return mouseEvent;
        }
        if (this.currentManagedOverlay.isSnappable() && this.inOverlayDraggingMode) {
            OverlayBounds snapCorners = this.emptySnapCorners.translated(-OverlayRenderer.SNAP_CORNER_SIZE.width, -OverlayRenderer.SNAP_CORNER_SIZE.height);
            for (Rectangle snapCorner : snapCorners.getBounds()) {
                if (!snapCorner.contains(mousePoint)) continue;
                OverlayPosition position = snapCorners.fromBounds(snapCorner);
                if (position == this.getCorrectedOverlayPosition(this.currentManagedOverlay)) {
                    position = null;
                }
                this.currentManagedOverlay.setPreferredPosition(position);
                this.currentManagedOverlay.setPreferredLocation(null);
                this.currentManagedOverlay.revalidate();
                break;
            }
        }
        if (this.inOverlayDraggingMode && this.currentManagedOverlay instanceof WidgetOverlay && !this.dragWarn) {
            this.dragWarn = true;
            this.chatMessageManager.queue(QueuedMessage.builder().type(ChatMessageType.CONSOLE).runeLiteFormattedMessage("You've repositioned one of the in-game interfaces. Hold " + String.valueOf(this.runeLiteConfig.dragHotkey()) + " and drag to reposition the interface again, or " + String.valueOf(this.runeLiteConfig.dragHotkey()) + " and right-click to reset.").build());
        }
        this.overlayManager.saveOverlay(this.currentManagedOverlay);
        this.resetOverlayManagementMode();
        mouseEvent.consume();
        return mouseEvent;
    }

    private Rectangle clipBounds(OverlayLayer layer) {
        if (!(this.isResizeable || layer != OverlayLayer.ABOVE_SCENE && layer != OverlayLayer.UNDER_WIDGETS)) {
            return new Rectangle(this.client.getViewportXOffset(), this.client.getViewportYOffset(), this.client.getViewportWidth(), this.client.getViewportHeight());
        }
        return new Rectangle(0, 0, this.client.getCanvasWidth(), this.client.getCanvasHeight());
    }

    private void safeRender(Overlay overlay, Graphics2D graphics, Point point) {
        Dimension overlayDimension;
        OverlayPosition position = overlay.getPosition();
        if (position == OverlayPosition.DYNAMIC || position == OverlayPosition.DETACHED) {
            graphics.setFont(this.runeLiteConfig.fontType().getFont());
        } else if (position == OverlayPosition.TOOLTIP) {
            graphics.setFont(this.runeLiteConfig.tooltipFontType().getFont());
        } else {
            graphics.setFont(this.runeLiteConfig.interfaceFontType().getFont());
        }
        graphics.translate(point.x, point.y);
        overlay.getBounds().setLocation(point);
        try {
            overlayDimension = overlay.render(graphics);
        }
        catch (Exception ex) {
            log.warn(DEDUPLICATE, "Error during overlay rendering", (Throwable)ex);
            return;
        }
        Dimension dimension = (Dimension)MoreObjects.firstNonNull((Object)overlayDimension, (Object)new Dimension());
        overlay.getBounds().setSize(dimension);
    }

    private OverlayPosition getCorrectedOverlayPosition(Overlay overlay) {
        OverlayPosition overlayPosition = overlay.getPosition();
        if (overlay.getPreferredPosition() != null) {
            overlayPosition = overlay.getPreferredPosition();
        }
        if (!this.isResizeable) {
            switch (overlayPosition) {
                case CANVAS_TOP_RIGHT: {
                    overlayPosition = OverlayPosition.TOP_RIGHT;
                    break;
                }
                case ABOVE_CHATBOX_RIGHT: {
                    overlayPosition = OverlayPosition.BOTTOM_RIGHT;
                }
            }
        }
        return overlayPosition;
    }

    private void resetOverlayManagementMode() {
        this.inOverlayResizingMode = false;
        this.inOverlayDraggingMode = false;
        this.currentManagedOverlay = null;
        this.dragTargetOverlay = null;
        this.currentManagedBounds = null;
        this.clientUI.setCursor(this.clientUI.getDefaultCursor());
    }

    private boolean shouldInvalidateBounds() {
        boolean viewportChanged;
        Widget viewportWidget;
        boolean chatboxHiddenChanged;
        boolean chatboxBoundsChanged;
        Widget chatbox = this.client.getWidget(0xA20022);
        boolean resizeableChanged = this.isResizeable != this.client.isResized();
        boolean changed = false;
        if (resizeableChanged) {
            this.isResizeable = this.client.isResized();
            changed = true;
        }
        boolean bl = chatboxBoundsChanged = chatbox == null || !chatbox.getBounds().equals(this.chatboxBounds);
        if (chatboxBoundsChanged) {
            this.chatboxBounds = chatbox != null ? chatbox.getBounds() : new Rectangle();
            changed = true;
        }
        boolean bl2 = chatboxHiddenChanged = this.chatboxHidden != (chatbox == null || chatbox.isHidden());
        if (chatboxHiddenChanged) {
            this.chatboxHidden = chatbox == null || chatbox.isHidden();
            changed = true;
        }
        Rectangle viewport = (viewportWidget = this.getViewportLayer()) != null ? viewportWidget.getBounds() : new Rectangle();
        boolean bl3 = viewportChanged = !viewport.equals(this.viewportBounds);
        if (viewportChanged) {
            this.viewportBounds = viewport;
            changed = true;
        }
        return changed;
    }

    private Widget getViewportLayer() {
        if (this.client.isResized()) {
            if (this.client.getVarbitValue(4607) == 1) {
                return this.client.getWidget(10747919);
            }
            return this.client.getWidget(10551311);
        }
        return this.client.getWidget(0x2240020);
    }

    private OverlayBounds buildSnapCorners() {
        Point topLeftPoint = new Point(this.viewportBounds.x + 5, this.viewportBounds.y + 20);
        Point topCenterPoint = new Point(this.viewportBounds.x + this.viewportBounds.width / 2, this.viewportBounds.y + 5);
        Point topRightPoint = new Point(this.viewportBounds.x + this.viewportBounds.width - 5, topCenterPoint.y);
        Point bottomLeftPoint = new Point(topLeftPoint.x, this.viewportBounds.y + this.viewportBounds.height - 5);
        Point bottomRightPoint = new Point(topRightPoint.x, bottomLeftPoint.y);
        if (this.isResizeable && this.chatboxHidden) {
            bottomLeftPoint.y += this.chatboxBounds.height;
        }
        Point rightChatboxPoint = this.isResizeable ? new Point(this.viewportBounds.x + this.chatboxBounds.width - 5, bottomLeftPoint.y) : bottomRightPoint;
        Point canvasTopRightPoint = this.isResizeable ? new Point((int)this.client.getRealDimensions().getWidth(), 0) : topRightPoint;
        return new OverlayBounds(new Rectangle(topLeftPoint, SNAP_CORNER_SIZE), new Rectangle(topCenterPoint, SNAP_CORNER_SIZE), new Rectangle(topRightPoint, SNAP_CORNER_SIZE), new Rectangle(bottomLeftPoint, SNAP_CORNER_SIZE), new Rectangle(bottomRightPoint, SNAP_CORNER_SIZE), new Rectangle(rightChatboxPoint, SNAP_CORNER_SIZE), new Rectangle(canvasTopRightPoint, SNAP_CORNER_SIZE));
    }

    private Point clampOverlayLocation(int overlayX, int overlayY, int overlayWidth, int overlayHeight, Overlay overlay) {
        Rectangle parentBounds = overlay.getParentBounds();
        if (parentBounds == null || parentBounds.isEmpty()) {
            Dimension dim = this.client.getRealDimensions();
            parentBounds = new Rectangle(0, 0, dim.width, dim.height);
        }
        return new Point(Ints.constrainToRange((int)overlayX, (int)parentBounds.x, (int)Math.max(parentBounds.x, parentBounds.x + parentBounds.width - overlayWidth)), Ints.constrainToRange((int)overlayY, (int)parentBounds.y, (int)Math.max(parentBounds.y, parentBounds.y + parentBounds.height - overlayHeight)));
    }
}

