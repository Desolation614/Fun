/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package net.runelite.client.plugins.worldhopper;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import javax.annotation.Nonnull;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import net.runelite.client.plugins.worldhopper.WorldHopperPlugin;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.FontManager;
import net.runelite.client.util.ImageUtil;

class WorldTableHeader
extends JPanel {
    private static final ImageIcon ARROW_UP;
    private static final ImageIcon HIGHLIGHT_ARROW_DOWN;
    private static final ImageIcon HIGHLIGHT_ARROW_UP;
    private static final Color ARROW_COLOR;
    private static final Color HIGHLIGHT_COLOR;
    private final JLabel textLabel = new JLabel();
    private final JLabel arrowLabel = new JLabel();
    private boolean ordering = false;

    WorldTableHeader(String title, boolean ordered, boolean ascending, @Nonnull Runnable onRefresh) {
        this.setLayout(new BorderLayout(5, 0));
        this.setBorder(new CompoundBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, ColorScheme.MEDIUM_GRAY_COLOR), new EmptyBorder(0, 5, 0, 2)));
        this.setBackground(ColorScheme.SCROLL_TRACK_COLOR);
        this.addMouseListener(new MouseAdapter(){

            @Override
            public void mouseEntered(MouseEvent mouseEvent) {
                WorldTableHeader.this.textLabel.setForeground(HIGHLIGHT_COLOR);
                if (!WorldTableHeader.this.ordering) {
                    WorldTableHeader.this.arrowLabel.setIcon(HIGHLIGHT_ARROW_UP);
                }
            }

            @Override
            public void mouseExited(MouseEvent mouseEvent) {
                if (!WorldTableHeader.this.ordering) {
                    WorldTableHeader.this.textLabel.setForeground(ARROW_COLOR);
                    WorldTableHeader.this.arrowLabel.setIcon(ARROW_UP);
                }
            }
        });
        this.textLabel.setText(title);
        this.textLabel.setFont(FontManager.getRunescapeSmallFont());
        JMenuItem refresh = new JMenuItem("Refresh worlds");
        refresh.addActionListener(e -> onRefresh.run());
        JPopupMenu popupMenu = new JPopupMenu();
        popupMenu.setBorder(new EmptyBorder(5, 5, 5, 5));
        popupMenu.add(refresh);
        this.textLabel.setComponentPopupMenu(popupMenu);
        this.setComponentPopupMenu(popupMenu);
        this.highlight(ordered, ascending);
        this.add((Component)this.textLabel, "West");
        this.add((Component)this.arrowLabel, "East");
    }

    @Override
    public void addMouseListener(MouseListener mouseListener) {
        super.addMouseListener(mouseListener);
        this.textLabel.addMouseListener(mouseListener);
        this.arrowLabel.addMouseListener(mouseListener);
    }

    public void highlight(boolean on, boolean ascending) {
        this.ordering = on;
        this.arrowLabel.setIcon(on ? (ascending ? HIGHLIGHT_ARROW_DOWN : HIGHLIGHT_ARROW_UP) : ARROW_UP);
        this.textLabel.setForeground(on ? HIGHLIGHT_COLOR : ARROW_COLOR);
    }

    static {
        ARROW_COLOR = ColorScheme.LIGHT_GRAY_COLOR;
        HIGHLIGHT_COLOR = ColorScheme.BRAND_ORANGE;
        BufferedImage arrowDown = ImageUtil.loadImageResource(WorldHopperPlugin.class, "arrow_down.png");
        BufferedImage arrowUp = ImageUtil.rotateImage(arrowDown, Math.PI);
        BufferedImage arrowUpFaded = ImageUtil.luminanceOffset(arrowUp, -80);
        ARROW_UP = new ImageIcon(arrowUpFaded);
        BufferedImage highlightArrowDown = ImageUtil.fillImage(arrowDown, HIGHLIGHT_COLOR);
        BufferedImage highlightArrowUp = ImageUtil.fillImage(arrowUp, HIGHLIGHT_COLOR);
        HIGHLIGHT_ARROW_DOWN = new ImageIcon(highlightArrowDown);
        HIGHLIGHT_ARROW_UP = new ImageIcon(highlightArrowUp);
    }
}

