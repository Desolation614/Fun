/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.plugins.timetracking;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.function.BooleanSupplier;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.timetracking.Tab;
import net.runelite.client.plugins.timetracking.TimeTrackingPanel;
import net.runelite.client.plugins.timetracking.TimeTrackingPlugin;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.FontManager;
import net.runelite.client.util.ImageUtil;

class OverviewItemPanel
extends JPanel {
    private static final ImageIcon ARROW_RIGHT_ICON;
    private static final Color HOVER_COLOR;
    private final JPanel textContainer;
    private final JLabel statusLabel;
    private final JLabel arrowLabel;
    private final BooleanSupplier isSelectable;
    private boolean isHighlighted;

    OverviewItemPanel(ItemManager itemManager, TimeTrackingPanel pluginPanel, Tab tab, String title) {
        this(itemManager, () -> pluginPanel.switchTab(tab), () -> true, tab.getItemID(), title);
    }

    OverviewItemPanel(ItemManager itemManager, final Runnable onTabSwitched, BooleanSupplier isSelectable, int iconItemID, String title) {
        this.isSelectable = isSelectable;
        this.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        this.setLayout(new BorderLayout());
        this.setBorder(new EmptyBorder(7, 7, 7, 7));
        JLabel iconLabel = new JLabel();
        iconLabel.setMinimumSize(new Dimension(36, 32));
        itemManager.getImage(iconItemID).addTo(iconLabel);
        this.add((Component)iconLabel, "West");
        this.textContainer = new JPanel();
        this.textContainer.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        this.textContainer.setLayout(new GridLayout(2, 1));
        this.textContainer.setBorder(new EmptyBorder(5, 7, 5, 7));
        this.addMouseListener(new MouseAdapter(){

            @Override
            public void mousePressed(MouseEvent mouseEvent) {
                onTabSwitched.run();
                OverviewItemPanel.this.setHighlighted(false);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                OverviewItemPanel.this.setHighlighted(true);
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                OverviewItemPanel.this.setHighlighted(true);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                OverviewItemPanel.this.setHighlighted(false);
            }
        });
        JLabel titleLabel = new JLabel(title);
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(FontManager.getRunescapeSmallFont());
        this.statusLabel = new JLabel();
        this.statusLabel.setForeground(Color.GRAY);
        this.statusLabel.setFont(FontManager.getRunescapeSmallFont());
        this.textContainer.add(titleLabel);
        this.textContainer.add(this.statusLabel);
        this.add((Component)this.textContainer, "Center");
        this.arrowLabel = new JLabel(ARROW_RIGHT_ICON);
        this.arrowLabel.setVisible(isSelectable.getAsBoolean());
        this.add((Component)this.arrowLabel, "East");
    }

    void updateStatus(String text, Color color) {
        this.statusLabel.setText(text);
        this.statusLabel.setForeground(color);
        this.arrowLabel.setVisible(this.isSelectable.getAsBoolean());
        if (this.isHighlighted && !this.isSelectable.getAsBoolean()) {
            this.setHighlighted(false);
        }
    }

    private void setHighlighted(boolean highlighted) {
        if (highlighted && !this.isSelectable.getAsBoolean()) {
            return;
        }
        this.setBackground(highlighted ? HOVER_COLOR : ColorScheme.DARKER_GRAY_COLOR);
        this.setCursor(new Cursor(highlighted && this.getMousePosition(true) != null ? 12 : 0));
        this.textContainer.setBackground(highlighted ? HOVER_COLOR : ColorScheme.DARKER_GRAY_COLOR);
        this.isHighlighted = highlighted;
    }

    static {
        HOVER_COLOR = ColorScheme.DARKER_GRAY_HOVER_COLOR;
        ARROW_RIGHT_ICON = new ImageIcon(ImageUtil.loadImageResource(TimeTrackingPlugin.class, "/util/arrow_right.png"));
    }
}

