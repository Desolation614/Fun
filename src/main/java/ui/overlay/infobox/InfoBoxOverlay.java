/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Strings
 *  lombok.NonNull
 *  net.runelite.api.Client
 *  net.runelite.api.MenuAction
 *  net.runelite.api.events.MenuOptionClicked
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.slf4j.Marker
 *  org.slf4j.MarkerFactory
 */
package net.runelite.client.ui.overlay.infobox;

import com.google.common.base.Strings;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import lombok.NonNull;
import net.runelite.api.Client;
import net.runelite.api.MenuAction;
import net.runelite.api.events.MenuOptionClicked;
import net.runelite.client.config.RuneLiteConfig;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.InfoBoxMenuClicked;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayMenuEntry;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.ComponentOrientation;
import net.runelite.client.ui.overlay.components.InfoBoxComponent;
import net.runelite.client.ui.overlay.components.LayoutableRenderableEntity;
import net.runelite.client.ui.overlay.infobox.InfoBox;
import net.runelite.client.ui.overlay.infobox.InfoBoxManager;
import net.runelite.client.ui.overlay.tooltip.Tooltip;
import net.runelite.client.ui.overlay.tooltip.TooltipManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

public class InfoBoxOverlay
extends OverlayPanel {
    private static final Logger log = LoggerFactory.getLogger(InfoBoxOverlay.class);
    private static final int GAP = 1;
    private static final int DEFAULT_WRAP_COUNT = 4;
    private static final Marker DEDUPLICATE = MarkerFactory.getMarker((String)"DEDUPLICATE");
    private final InfoBoxManager infoboxManager;
    private final TooltipManager tooltipManager;
    private final Client client;
    private final RuneLiteConfig config;
    private final EventBus eventBus;
    private final String name;
    private ComponentOrientation orientation;
    private final List<InfoBox> infoBoxes = new CopyOnWriteArrayList<InfoBox>();
    private InfoBoxComponent hoveredComponent;

    InfoBoxOverlay(InfoBoxManager infoboxManager, TooltipManager tooltipManager, Client client2, RuneLiteConfig config, EventBus eventBus, String name, @NonNull ComponentOrientation orientation) {
        if (orientation == null) {
            throw new NullPointerException("orientation is marked non-null but is null");
        }
        this.tooltipManager = tooltipManager;
        this.infoboxManager = infoboxManager;
        this.client = client2;
        this.config = config;
        this.eventBus = eventBus;
        this.name = name;
        this.orientation = orientation;
        this.setPosition(OverlayPosition.TOP_LEFT);
        this.setClearChildren(false);
        this.setDragTargetable(true);
        this.panelComponent.setWrap(true);
        this.panelComponent.setBackgroundColor(null);
        this.panelComponent.setBorder(new Rectangle());
        this.panelComponent.setGap(new Point(1, 1));
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        boolean menuOpen = this.client.isMenuOpen();
        if (!menuOpen) {
            this.hoveredComponent = null;
        }
        if (this.infoBoxes.isEmpty()) {
            return null;
        }
        this.panelComponent.setPreferredSize(new Dimension(4 * (this.config.infoBoxSize() + 1), 4 * (this.config.infoBoxSize() + 1)));
        this.panelComponent.setOrientation(this.orientation);
        Font font = this.config.infoboxFontType().getFont();
        boolean infoBoxTextOutline = this.config.infoBoxTextOutline();
        Color overlayBackgroundColor = this.config.overlayBackgroundColor();
        Dimension preferredSize = new Dimension(this.config.infoBoxSize(), this.config.infoBoxSize());
        for (InfoBox box : this.infoBoxes) {
            block9: {
                try {
                    if (!box.render()) {
                    }
                    break block9;
                }
                catch (Exception ex) {
                    log.warn(DEDUPLICATE, "Error during infobox rendering", (Throwable)ex);
                }
                continue;
            }
            String text = box.getText();
            Color color = box.getTextColor();
            InfoBoxComponent infoBoxComponent = new InfoBoxComponent();
            infoBoxComponent.setText(text);
            infoBoxComponent.setFont(font);
            if (color != null) {
                infoBoxComponent.setColor(color);
            }
            infoBoxComponent.setOutline(infoBoxTextOutline);
            infoBoxComponent.setImage(box.getScaledImage());
            infoBoxComponent.setTooltip(box.getTooltip());
            infoBoxComponent.setPreferredSize(preferredSize);
            infoBoxComponent.setBackgroundColor(overlayBackgroundColor);
            infoBoxComponent.setInfoBox(box);
            this.panelComponent.getChildren().add(infoBoxComponent);
        }
        Dimension dimension = super.render(graphics);
        Point mouse = new Point(this.client.getMouseCanvasPosition().getX(), this.client.getMouseCanvasPosition().getY());
        for (LayoutableRenderableEntity child : this.panelComponent.getChildren()) {
            InfoBoxComponent component = (InfoBoxComponent)child;
            Rectangle intersectionRectangle = new Rectangle(component.getBounds());
            intersectionRectangle.translate(this.getBounds().x, this.getBounds().y);
            if (!intersectionRectangle.contains(mouse)) continue;
            String tooltip = component.getTooltip();
            if (!Strings.isNullOrEmpty((String)tooltip)) {
                this.tooltipManager.add(new Tooltip(tooltip));
            }
            if (menuOpen) break;
            this.hoveredComponent = component;
            break;
        }
        this.panelComponent.getChildren().clear();
        return dimension;
    }

    @Override
    public List<OverlayMenuEntry> getMenuEntries() {
        return this.hoveredComponent == null ? Collections.emptyList() : this.hoveredComponent.getInfoBox().getMenuEntries();
    }

    @Subscribe
    public void onMenuOptionClicked(MenuOptionClicked menuOptionClicked) {
        if (menuOptionClicked.getMenuAction() != MenuAction.RUNELITE_INFOBOX || this.hoveredComponent == null) {
            return;
        }
        InfoBox infoBox = this.hoveredComponent.getInfoBox();
        OverlayMenuEntry overlayMenuEntry = infoBox.getMenuEntries().stream().filter(me -> me.getOption().equals(menuOptionClicked.getMenuOption())).findAny().orElse(null);
        if (overlayMenuEntry != null) {
            this.eventBus.post(new InfoBoxMenuClicked(overlayMenuEntry, infoBox));
        }
    }

    @Override
    public boolean onDrag(Overlay source) {
        if (!(source instanceof InfoBoxOverlay)) {
            return false;
        }
        this.infoboxManager.mergeInfoBoxes((InfoBoxOverlay)source, this);
        return true;
    }

    ComponentOrientation flip() {
        this.orientation = this.orientation == ComponentOrientation.HORIZONTAL ? ComponentOrientation.VERTICAL : ComponentOrientation.HORIZONTAL;
        return this.orientation;
    }

    public List<InfoBox> getInfoBoxes() {
        return this.infoBoxes;
    }
}

