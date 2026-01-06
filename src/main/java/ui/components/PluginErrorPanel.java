/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.ui.components;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import net.runelite.client.ui.FontManager;
import net.runelite.client.ui.components.shadowlabel.JShadowedLabel;

public class PluginErrorPanel
extends JPanel {
    private final JLabel noResultsTitle = new JShadowedLabel();
    private final JLabel noResultsDescription = new JShadowedLabel();

    public PluginErrorPanel() {
        this.setOpaque(false);
        this.setBorder(new EmptyBorder(50, 10, 0, 10));
        this.setLayout(new BorderLayout());
        this.noResultsTitle.setForeground(Color.WHITE);
        this.noResultsTitle.setHorizontalAlignment(0);
        this.noResultsDescription.setFont(FontManager.getRunescapeSmallFont());
        this.noResultsDescription.setForeground(Color.GRAY);
        this.noResultsDescription.setHorizontalAlignment(0);
        this.add((Component)this.noResultsTitle, "North");
        this.add((Component)this.noResultsDescription, "Center");
        this.setVisible(false);
    }

    public void setContent(String title, String description) {
        this.noResultsTitle.setText(title);
        this.noResultsDescription.setText("<html><body style = 'text-align:center'>" + description + "</body></html>");
        this.setVisible(true);
    }
}

