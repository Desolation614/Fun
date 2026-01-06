/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.plugins.screenmarkers.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import net.runelite.client.plugins.screenmarkers.ScreenMarkerPlugin;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.FontManager;
import net.runelite.client.ui.components.shadowlabel.JShadowedLabel;
import net.runelite.client.util.ImageUtil;

public class ScreenMarkerCreationPanel
extends JPanel {
    private static final ImageIcon CONFIRM_ICON = new ImageIcon(ImageUtil.loadImageResource(ScreenMarkerPlugin.class, "confirm_icon.png"));
    private static final ImageIcon CONFIRM_HOVER_ICON;
    private static final ImageIcon CONFIRM_LOCKED_ICON;
    private static final ImageIcon CANCEL_ICON;
    private static final ImageIcon CANCEL_HOVER_ICON;
    private final JShadowedLabel instructionsLabel = new JShadowedLabel();
    private final JLabel confirmLabel = new JLabel();
    private boolean lockedConfirm = true;

    ScreenMarkerCreationPanel(final ScreenMarkerPlugin plugin) {
        this.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        this.setBorder(new EmptyBorder(8, 8, 8, 8));
        this.setLayout(new BorderLayout());
        this.instructionsLabel.setFont(FontManager.getRunescapeSmallFont());
        this.instructionsLabel.setForeground(Color.WHITE);
        JPanel actionsContainer = new JPanel(new GridLayout(1, 2, 8, 0));
        actionsContainer.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        this.confirmLabel.setIcon(CONFIRM_LOCKED_ICON);
        this.confirmLabel.setToolTipText("Confirm and save");
        this.confirmLabel.addMouseListener(new MouseAdapter(){

            @Override
            public void mousePressed(MouseEvent mouseEvent) {
                if (!ScreenMarkerCreationPanel.this.lockedConfirm) {
                    plugin.finishCreation(false);
                }
            }

            @Override
            public void mouseEntered(MouseEvent mouseEvent) {
                ScreenMarkerCreationPanel.this.confirmLabel.setIcon(ScreenMarkerCreationPanel.this.lockedConfirm ? CONFIRM_LOCKED_ICON : CONFIRM_HOVER_ICON);
            }

            @Override
            public void mouseExited(MouseEvent mouseEvent) {
                ScreenMarkerCreationPanel.this.confirmLabel.setIcon(ScreenMarkerCreationPanel.this.lockedConfirm ? CONFIRM_LOCKED_ICON : CONFIRM_ICON);
            }
        });
        final JLabel cancelLabel = new JLabel(CANCEL_ICON);
        cancelLabel.setToolTipText("Cancel");
        cancelLabel.addMouseListener(new MouseAdapter(){

            @Override
            public void mousePressed(MouseEvent mouseEvent) {
                plugin.finishCreation(true);
            }

            @Override
            public void mouseEntered(MouseEvent mouseEvent) {
                cancelLabel.setIcon(CANCEL_HOVER_ICON);
            }

            @Override
            public void mouseExited(MouseEvent mouseEvent) {
                cancelLabel.setIcon(CANCEL_ICON);
            }
        });
        actionsContainer.add(this.confirmLabel);
        actionsContainer.add(cancelLabel);
        this.add((Component)this.instructionsLabel, "Center");
        this.add((Component)actionsContainer, "East");
    }

    public void unlockConfirm() {
        this.confirmLabel.setIcon(CONFIRM_ICON);
        this.lockedConfirm = false;
        this.instructionsLabel.setText("Confirm or cancel to finish.");
    }

    public void lockConfirm() {
        this.confirmLabel.setIcon(CONFIRM_LOCKED_ICON);
        this.lockedConfirm = true;
        this.instructionsLabel.setText("Drag in-game to draw");
    }

    static {
        CANCEL_ICON = new ImageIcon(ImageUtil.loadImageResource(ScreenMarkerPlugin.class, "cancel_icon.png"));
        BufferedImage confirmIcon = ImageUtil.bufferedImageFromImage(CONFIRM_ICON.getImage());
        CONFIRM_HOVER_ICON = new ImageIcon(ImageUtil.alphaOffset((Image)confirmIcon, 0.54f));
        CONFIRM_LOCKED_ICON = new ImageIcon(ImageUtil.grayscaleImage(confirmIcon));
        CANCEL_HOVER_ICON = new ImageIcon(ImageUtil.alphaOffset((Image)ImageUtil.bufferedImageFromImage(CANCEL_ICON.getImage()), 0.6f));
    }
}

