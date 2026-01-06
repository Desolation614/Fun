/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;
import net.runelite.client.ui.Activatable;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.DynamicGridLayout;

public abstract class PluginPanel
extends JPanel
implements Activatable {
    public static final int PANEL_WIDTH = 225;
    public static final int SCROLLBAR_WIDTH = 17;
    public static final int BORDER_OFFSET = 6;
    private static final EmptyBorder BORDER_PADDING = new EmptyBorder(6, 6, 6, 6);
    private static final Dimension OUTER_PREFERRED_SIZE = new Dimension(242, 0);
    private final JScrollPane scrollPane;
    private final JPanel wrappedPanel;

    protected PluginPanel() {
        this(true);
    }

    protected PluginPanel(boolean wrap) {
        if (wrap) {
            this.setBorder(BORDER_PADDING);
            this.setLayout(new DynamicGridLayout(0, 1, 0, 3));
            this.setBackground(ColorScheme.DARK_GRAY_COLOR);
            JPanel northPanel = new JPanel();
            northPanel.setLayout(new BorderLayout());
            northPanel.add((Component)this, "North");
            northPanel.setBackground(ColorScheme.DARK_GRAY_COLOR);
            this.scrollPane = new JScrollPane(northPanel);
            this.scrollPane.setHorizontalScrollBarPolicy(31);
            this.wrappedPanel = new JPanel();
            this.wrappedPanel.setPreferredSize(OUTER_PREFERRED_SIZE);
            this.wrappedPanel.setLayout(new BorderLayout());
            this.wrappedPanel.add((Component)this.scrollPane, "Center");
        } else {
            this.scrollPane = null;
            this.wrappedPanel = this;
        }
    }

    @Override
    public Dimension getPreferredSize() {
        int width = this == this.wrappedPanel ? 242 : 225;
        return new Dimension(width, super.getPreferredSize().height);
    }

    @Override
    public Dimension getMinimumSize() {
        int width = this == this.wrappedPanel ? 242 : 225;
        return new Dimension(width, super.getMinimumSize().height);
    }

    protected JScrollPane getScrollPane() {
        return this.scrollPane;
    }

    public JPanel getWrappedPanel() {
        return this.wrappedPanel;
    }
}

