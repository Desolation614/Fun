/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.plugins.grandexchange;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import net.runelite.client.plugins.grandexchange.GrandExchangePlugin;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.util.AsyncBufferedImage;
import net.runelite.client.util.QuantityFormatter;

class GrandExchangeItemPanel
extends JPanel {
    private static final Dimension ICON_SIZE = new Dimension(32, 32);

    GrandExchangeItemPanel(final GrandExchangePlugin grandExchangePlugin, AsyncBufferedImage icon, final String name, final int itemID, int gePrice, int haPrice, int geItemLimit) {
        BorderLayout layout = new BorderLayout();
        layout.setHgap(5);
        this.setLayout(layout);
        this.setToolTipText(name);
        this.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        final Color background = this.getBackground();
        final ArrayList<JPanel> panels = new ArrayList<JPanel>();
        panels.add(this);
        MouseAdapter itemPanelMouseListener = new MouseAdapter(){

            @Override
            public void mouseEntered(MouseEvent e) {
                for (JPanel panel : panels) {
                    GrandExchangeItemPanel.this.matchComponentBackground(panel, ColorScheme.DARK_GRAY_HOVER_COLOR);
                }
                GrandExchangeItemPanel.this.setCursor(new Cursor(12));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                for (JPanel panel : panels) {
                    GrandExchangeItemPanel.this.matchComponentBackground(panel, background);
                }
                GrandExchangeItemPanel.this.setCursor(new Cursor(0));
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                grandExchangePlugin.openGeLink(name, itemID);
            }
        };
        this.addMouseListener(itemPanelMouseListener);
        this.setBorder(new EmptyBorder(5, 5, 5, 0));
        JLabel itemIcon = new JLabel();
        itemIcon.setPreferredSize(ICON_SIZE);
        if (icon != null) {
            icon.addTo(itemIcon);
        }
        this.add((Component)itemIcon, "Before");
        JPanel rightPanel = new JPanel(new GridLayout(3, 1));
        panels.add(rightPanel);
        rightPanel.setBackground(background);
        JLabel itemName = new JLabel();
        itemName.setForeground(Color.WHITE);
        itemName.setMaximumSize(new Dimension(0, 0));
        itemName.setPreferredSize(new Dimension(0, 0));
        itemName.setText(name);
        rightPanel.add(itemName);
        JLabel gePriceLabel = new JLabel();
        if (gePrice > 0) {
            gePriceLabel.setText(QuantityFormatter.formatNumber(gePrice) + " gp");
        } else {
            gePriceLabel.setText("N/A");
        }
        gePriceLabel.setForeground(ColorScheme.GRAND_EXCHANGE_PRICE);
        rightPanel.add(gePriceLabel);
        JPanel alchAndLimitPanel = new JPanel(new BorderLayout());
        panels.add(alchAndLimitPanel);
        alchAndLimitPanel.setBackground(background);
        JLabel haPriceLabel = new JLabel();
        haPriceLabel.setText(QuantityFormatter.formatNumber(haPrice) + " alch");
        haPriceLabel.setForeground(ColorScheme.GRAND_EXCHANGE_ALCH);
        alchAndLimitPanel.add((Component)haPriceLabel, "West");
        JLabel geLimitLabel = new JLabel();
        String limitLabelText = geItemLimit == 0 ? "" : "Limit " + QuantityFormatter.formatNumber(geItemLimit);
        geLimitLabel.setText(limitLabelText);
        geLimitLabel.setForeground(ColorScheme.GRAND_EXCHANGE_LIMIT);
        geLimitLabel.setBorder(new CompoundBorder(geLimitLabel.getBorder(), new EmptyBorder(0, 0, 0, 7)));
        alchAndLimitPanel.add((Component)geLimitLabel, "East");
        rightPanel.add(alchAndLimitPanel);
        this.add((Component)rightPanel, "Center");
    }

    private void matchComponentBackground(JPanel panel, Color color) {
        panel.setBackground(color);
        for (Component c : panel.getComponents()) {
            c.setBackground(color);
        }
    }
}

