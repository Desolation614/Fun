/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.plugins.skillcalculator;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import net.runelite.client.game.SpriteManager;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.FontManager;
import net.runelite.client.ui.components.shadowlabel.JShadowedLabel;

class UICombinedActionSlot
extends JPanel {
    private static final Dimension ICON_SIZE = new Dimension(32, 32);
    private final JShadowedLabel uiLabelActions;
    private final JShadowedLabel uiLabelTitle;

    UICombinedActionSlot(SpriteManager spriteManager) {
        this.setLayout(new BorderLayout());
        this.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        this.setBorder(BorderFactory.createEmptyBorder(7, 7, 7, 7));
        JLabel uiIcon = new JLabel();
        uiIcon.setBorder(new EmptyBorder(0, 0, 0, 5));
        spriteManager.addSpriteTo(uiIcon, 582, 0);
        uiIcon.setMinimumSize(ICON_SIZE);
        uiIcon.setMaximumSize(ICON_SIZE);
        uiIcon.setPreferredSize(ICON_SIZE);
        uiIcon.setHorizontalAlignment(0);
        this.add((Component)uiIcon, "Before");
        JPanel uiInfo = new JPanel(new GridLayout(2, 1));
        uiInfo.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        this.uiLabelTitle = new JShadowedLabel("No Action Selected");
        this.uiLabelTitle.setForeground(Color.WHITE);
        this.uiLabelActions = new JShadowedLabel("Shift-click to select multiple");
        this.uiLabelActions.setFont(FontManager.getRunescapeSmallFont());
        this.uiLabelActions.setForeground(ColorScheme.LIGHT_GRAY_COLOR);
        uiInfo.add(this.uiLabelTitle);
        uiInfo.add(this.uiLabelActions);
        this.add((Component)uiInfo, "Center");
    }

    void setText(String text) {
        this.uiLabelActions.setText(text);
    }

    void setTitle(String text) {
        this.uiLabelTitle.setText(text);
    }
}

