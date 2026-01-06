/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.plugins.skillcalculator;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.skillcalculator.skills.SkillAction;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.FontManager;
import net.runelite.client.ui.components.shadowlabel.JShadowedLabel;

class UIActionSlot
extends JPanel {
    private static final Border GREEN_BORDER = new CompoundBorder(BorderFactory.createMatteBorder(0, 4, 0, 0, ColorScheme.PROGRESS_COMPLETE_COLOR.darker()), BorderFactory.createEmptyBorder(7, 12, 7, 7));
    private static final Border RED_BORDER = new CompoundBorder(BorderFactory.createMatteBorder(0, 4, 0, 0, ColorScheme.PROGRESS_ERROR_COLOR.darker()), BorderFactory.createEmptyBorder(7, 12, 7, 7));
    private static final Border ORANGE_BORDER = new CompoundBorder(BorderFactory.createMatteBorder(0, 4, 0, 0, ColorScheme.PROGRESS_INPROGRESS_COLOR.darker()), BorderFactory.createEmptyBorder(7, 12, 7, 7));
    private static final Dimension ICON_SIZE = new Dimension(32, 32);
    private final SkillAction action;
    private String actionName;
    private final JShadowedLabel uiLabelActions;
    private final JPanel uiInfo;
    private boolean isAvailable;
    private boolean isSelected;
    private boolean isOverlapping;
    private int value;

    UIActionSlot(SkillAction action, ClientThread clientThread, ItemManager itemManager, JLabel uiIcon) {
        this.action = action;
        this.setLayout(new BorderLayout());
        this.setBorder(RED_BORDER);
        this.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        MouseAdapter hoverListener = new MouseAdapter(){

            @Override
            public void mouseEntered(MouseEvent mouseEvent) {
                if (!UIActionSlot.this.isSelected) {
                    UIActionSlot.this.setBackground(ColorScheme.DARKER_GRAY_HOVER_COLOR);
                }
            }

            @Override
            public void mouseExited(MouseEvent mouseEvent) {
                if (!UIActionSlot.this.isSelected) {
                    UIActionSlot.this.updateBackground();
                }
            }
        };
        this.addMouseListener(hoverListener);
        uiIcon.setMinimumSize(ICON_SIZE);
        uiIcon.setMaximumSize(ICON_SIZE);
        uiIcon.setPreferredSize(ICON_SIZE);
        uiIcon.setHorizontalAlignment(0);
        this.uiInfo = new JPanel(new GridLayout(2, 1));
        this.uiInfo.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        this.uiInfo.setBorder(new EmptyBorder(0, 5, 0, 0));
        JShadowedLabel uiLabelName = new JShadowedLabel();
        clientThread.invokeLater(() -> {
            this.actionName = action.getName(itemManager);
            SwingUtilities.invokeLater(() -> uiLabelName.setText(this.actionName));
        });
        uiLabelName.setForeground(Color.WHITE);
        this.uiLabelActions = new JShadowedLabel("Unknown");
        this.uiLabelActions.setFont(FontManager.getRunescapeSmallFont());
        this.uiLabelActions.setForeground(ColorScheme.LIGHT_GRAY_COLOR);
        this.uiInfo.add(uiLabelName);
        this.uiInfo.add(this.uiLabelActions);
        this.add((Component)uiIcon, "Before");
        this.add((Component)this.uiInfo, "Center");
    }

    void setSelected(boolean selected) {
        this.isSelected = selected;
        this.updateBackground();
    }

    void setAvailable(boolean available) {
        this.isAvailable = available;
        this.updateBackground();
    }

    void setOverlapping(boolean overlapping) {
        this.isOverlapping = overlapping;
        this.updateBackground();
    }

    void setText(String text) {
        this.uiLabelActions.setText(text);
    }

    private void updateBackground() {
        if (this.isAvailable) {
            this.setBorder(GREEN_BORDER);
        } else if (this.isOverlapping) {
            this.setBorder(ORANGE_BORDER);
        } else {
            this.setBorder(RED_BORDER);
        }
        this.setBackground(this.isSelected() ? ColorScheme.DARKER_GRAY_HOVER_COLOR.brighter() : ColorScheme.DARKER_GRAY_COLOR);
    }

    @Override
    public void setBackground(Color color) {
        super.setBackground(color);
        if (this.uiInfo != null) {
            this.uiInfo.setBackground(color);
        }
    }

    SkillAction getAction() {
        return this.action;
    }

    String getActionName() {
        return this.actionName;
    }

    boolean isAvailable() {
        return this.isAvailable;
    }

    boolean isSelected() {
        return this.isSelected;
    }

    boolean isOverlapping() {
        return this.isOverlapping;
    }

    int getValue() {
        return this.value;
    }

    void setValue(int value) {
        this.value = value;
    }
}

