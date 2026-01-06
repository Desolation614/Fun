/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.plugins.screenmarkers.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import net.runelite.client.plugins.screenmarkers.ScreenMarkerOverlay;
import net.runelite.client.plugins.screenmarkers.ScreenMarkerPlugin;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.FontManager;
import net.runelite.client.ui.components.FlatTextField;
import net.runelite.client.ui.components.colorpicker.RuneliteColorPicker;
import net.runelite.client.util.ColorUtil;
import net.runelite.client.util.ImageUtil;

class ScreenMarkerPanel
extends JPanel {
    private static final int DEFAULT_FILL_OPACITY = 75;
    private static final Border NAME_BOTTOM_BORDER = new CompoundBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, ColorScheme.DARK_GRAY_COLOR), BorderFactory.createLineBorder(ColorScheme.DARKER_GRAY_COLOR));
    private static final ImageIcon BORDER_COLOR_ICON;
    private static final ImageIcon BORDER_COLOR_HOVER_ICON;
    private static final ImageIcon NO_BORDER_COLOR_ICON;
    private static final ImageIcon NO_BORDER_COLOR_HOVER_ICON;
    private static final ImageIcon FILL_COLOR_ICON;
    private static final ImageIcon FILL_COLOR_HOVER_ICON;
    private static final ImageIcon NO_FILL_COLOR_ICON;
    private static final ImageIcon NO_FILL_COLOR_HOVER_ICON;
    private static final ImageIcon LABEL_ICON;
    private static final ImageIcon LABEL_HOVER_ICON;
    private static final ImageIcon NO_LABEL_ICON;
    private static final ImageIcon NO_LABEL_HOVER_ICON;
    private static final ImageIcon VISIBLE_ICON;
    private static final ImageIcon VISIBLE_HOVER_ICON;
    private static final ImageIcon INVISIBLE_ICON;
    private static final ImageIcon INVISIBLE_HOVER_ICON;
    private static final ImageIcon DELETE_ICON;
    private static final ImageIcon DELETE_HOVER_ICON;
    private final ScreenMarkerPlugin plugin;
    private final ScreenMarkerOverlay marker;
    private final JLabel borderColorIndicator = new JLabel();
    private final JLabel fillColorIndicator = new JLabel();
    private final JLabel labelIndicator = new JLabel();
    private final JLabel visibilityLabel = new JLabel();
    private final JLabel deleteLabel = new JLabel();
    private final FlatTextField nameInput = new FlatTextField();
    private final JLabel save = new JLabel("Save");
    private final JLabel cancel = new JLabel("Cancel");
    private final JLabel rename = new JLabel("Rename");
    private final SpinnerModel spinnerModel = new SpinnerNumberModel(5, 0, Integer.MAX_VALUE, 1);
    private final JSpinner thicknessSpinner = new JSpinner(this.spinnerModel);
    private boolean visible;
    private boolean showLabel;

    ScreenMarkerPanel(final ScreenMarkerPlugin plugin, final ScreenMarkerOverlay marker) {
        this.plugin = plugin;
        this.marker = marker;
        this.visible = marker.getMarker().isVisible();
        this.showLabel = marker.getMarker().isLabelled();
        this.setLayout(new BorderLayout());
        this.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        JPanel nameWrapper = new JPanel(new BorderLayout());
        nameWrapper.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        nameWrapper.setBorder(NAME_BOTTOM_BORDER);
        JPanel nameActions = new JPanel(new BorderLayout(3, 0));
        nameActions.setBorder(new EmptyBorder(0, 0, 0, 8));
        nameActions.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        this.save.setVisible(false);
        this.save.setFont(FontManager.getRunescapeSmallFont());
        this.save.setForeground(ColorScheme.PROGRESS_COMPLETE_COLOR);
        this.save.addMouseListener(new MouseAdapter(){

            @Override
            public void mousePressed(MouseEvent mouseEvent) {
                ScreenMarkerPanel.this.save();
            }

            @Override
            public void mouseEntered(MouseEvent mouseEvent) {
                ScreenMarkerPanel.this.save.setForeground(ColorScheme.PROGRESS_COMPLETE_COLOR.darker());
            }

            @Override
            public void mouseExited(MouseEvent mouseEvent) {
                ScreenMarkerPanel.this.save.setForeground(ColorScheme.PROGRESS_COMPLETE_COLOR);
            }
        });
        this.cancel.setVisible(false);
        this.cancel.setFont(FontManager.getRunescapeSmallFont());
        this.cancel.setForeground(ColorScheme.PROGRESS_ERROR_COLOR);
        this.cancel.addMouseListener(new MouseAdapter(){

            @Override
            public void mousePressed(MouseEvent mouseEvent) {
                ScreenMarkerPanel.this.cancel();
            }

            @Override
            public void mouseEntered(MouseEvent mouseEvent) {
                ScreenMarkerPanel.this.cancel.setForeground(ColorScheme.PROGRESS_ERROR_COLOR.darker());
            }

            @Override
            public void mouseExited(MouseEvent mouseEvent) {
                ScreenMarkerPanel.this.cancel.setForeground(ColorScheme.PROGRESS_ERROR_COLOR);
            }
        });
        this.rename.setFont(FontManager.getRunescapeSmallFont());
        this.rename.setForeground(ColorScheme.LIGHT_GRAY_COLOR.darker());
        this.rename.addMouseListener(new MouseAdapter(){

            @Override
            public void mousePressed(MouseEvent mouseEvent) {
                ScreenMarkerPanel.this.nameInput.setEditable(true);
                ScreenMarkerPanel.this.updateNameActions(true);
            }

            @Override
            public void mouseEntered(MouseEvent mouseEvent) {
                ScreenMarkerPanel.this.rename.setForeground(ColorScheme.LIGHT_GRAY_COLOR.darker().darker());
            }

            @Override
            public void mouseExited(MouseEvent mouseEvent) {
                ScreenMarkerPanel.this.rename.setForeground(ColorScheme.LIGHT_GRAY_COLOR.darker());
            }
        });
        nameActions.add((Component)this.save, "East");
        nameActions.add((Component)this.cancel, "West");
        nameActions.add((Component)this.rename, "Center");
        this.nameInput.setText(marker.getMarker().getName());
        this.nameInput.setBorder(null);
        this.nameInput.setEditable(false);
        this.nameInput.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        this.nameInput.setPreferredSize(new Dimension(0, 24));
        this.nameInput.getTextField().setForeground(Color.WHITE);
        this.nameInput.getTextField().setBorder(new EmptyBorder(0, 8, 0, 0));
        this.nameInput.addKeyListener(new KeyAdapter(){

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == 10) {
                    ScreenMarkerPanel.this.save();
                } else if (e.getKeyCode() == 27) {
                    ScreenMarkerPanel.this.cancel();
                }
            }
        });
        this.nameInput.getTextField().addMouseListener(new MouseAdapter(){

            @Override
            public void mouseEntered(MouseEvent mouseEvent) {
                ScreenMarkerPanel.this.preview(true);
            }

            @Override
            public void mouseExited(MouseEvent mouseEvent) {
                ScreenMarkerPanel.this.preview(false);
            }
        });
        nameWrapper.add((Component)this.nameInput, "Center");
        nameWrapper.add((Component)nameActions, "East");
        JPanel bottomContainer = new JPanel(new BorderLayout());
        bottomContainer.setBorder(new EmptyBorder(8, 0, 8, 0));
        bottomContainer.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        JPanel leftActions = new JPanel(new FlowLayout(0, 8, 0));
        leftActions.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        this.borderColorIndicator.setToolTipText("Edit border color");
        this.borderColorIndicator.addMouseListener(new MouseAdapter(){

            @Override
            public void mousePressed(MouseEvent mouseEvent) {
                ScreenMarkerPanel.this.openBorderColorPicker();
            }

            @Override
            public void mouseEntered(MouseEvent mouseEvent) {
                ScreenMarkerPanel.this.borderColorIndicator.setIcon(marker.getMarker().getBorderThickness() == 0 ? NO_BORDER_COLOR_HOVER_ICON : BORDER_COLOR_HOVER_ICON);
            }

            @Override
            public void mouseExited(MouseEvent mouseEvent) {
                ScreenMarkerPanel.this.borderColorIndicator.setIcon(marker.getMarker().getBorderThickness() == 0 ? NO_BORDER_COLOR_ICON : BORDER_COLOR_ICON);
            }
        });
        this.fillColorIndicator.setToolTipText("Edit fill color");
        this.fillColorIndicator.addMouseListener(new MouseAdapter(){

            @Override
            public void mousePressed(MouseEvent mouseEvent) {
                ScreenMarkerPanel.this.openFillColorPicker();
            }

            @Override
            public void mouseEntered(MouseEvent mouseEvent) {
                ScreenMarkerPanel.this.fillColorIndicator.setIcon(marker.getMarker().getFill().getAlpha() == 0 ? NO_FILL_COLOR_HOVER_ICON : FILL_COLOR_HOVER_ICON);
            }

            @Override
            public void mouseExited(MouseEvent mouseEvent) {
                ScreenMarkerPanel.this.fillColorIndicator.setIcon(marker.getMarker().getFill().getAlpha() == 0 ? NO_FILL_COLOR_ICON : FILL_COLOR_ICON);
            }
        });
        this.thicknessSpinner.setValue(marker.getMarker().getBorderThickness());
        this.thicknessSpinner.setPreferredSize(new Dimension(50, 20));
        this.thicknessSpinner.addChangeListener(ce -> this.updateThickness(true));
        this.thicknessSpinner.setToolTipText("Border thickness");
        this.labelIndicator.addMouseListener(new MouseAdapter(){

            @Override
            public void mousePressed(MouseEvent mouseEvent) {
                ScreenMarkerPanel.this.toggleLabelling(!ScreenMarkerPanel.this.showLabel);
            }

            @Override
            public void mouseEntered(MouseEvent mouseEvent) {
                ScreenMarkerPanel.this.labelIndicator.setIcon(ScreenMarkerPanel.this.showLabel ? LABEL_HOVER_ICON : NO_LABEL_HOVER_ICON);
            }

            @Override
            public void mouseExited(MouseEvent mouseEvent) {
                ScreenMarkerPanel.this.labelIndicator.setIcon(ScreenMarkerPanel.this.showLabel ? LABEL_ICON : NO_LABEL_ICON);
            }
        });
        leftActions.add(this.borderColorIndicator);
        leftActions.add(this.fillColorIndicator);
        leftActions.add(this.labelIndicator);
        leftActions.add(this.thicknessSpinner);
        JPanel rightActions = new JPanel(new FlowLayout(2, 8, 0));
        rightActions.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        this.visibilityLabel.addMouseListener(new MouseAdapter(){

            @Override
            public void mousePressed(MouseEvent mouseEvent) {
                ScreenMarkerPanel.this.toggle(!ScreenMarkerPanel.this.visible);
            }

            @Override
            public void mouseEntered(MouseEvent mouseEvent) {
                ScreenMarkerPanel.this.visibilityLabel.setIcon(ScreenMarkerPanel.this.visible ? VISIBLE_HOVER_ICON : INVISIBLE_HOVER_ICON);
            }

            @Override
            public void mouseExited(MouseEvent mouseEvent) {
                ScreenMarkerPanel.this.updateVisibility();
            }
        });
        this.deleteLabel.setIcon(DELETE_ICON);
        this.deleteLabel.setToolTipText("Delete screen marker");
        this.deleteLabel.addMouseListener(new MouseAdapter(){

            @Override
            public void mousePressed(MouseEvent mouseEvent) {
                int confirm = JOptionPane.showConfirmDialog(ScreenMarkerPanel.this, "Are you sure you want to permanently delete this screen marker?", "Warning", 2);
                if (confirm == 0) {
                    plugin.deleteMarker(marker);
                }
            }

            @Override
            public void mouseEntered(MouseEvent mouseEvent) {
                ScreenMarkerPanel.this.deleteLabel.setIcon(DELETE_HOVER_ICON);
            }

            @Override
            public void mouseExited(MouseEvent mouseEvent) {
                ScreenMarkerPanel.this.deleteLabel.setIcon(DELETE_ICON);
            }
        });
        rightActions.add(this.visibilityLabel);
        rightActions.add(this.deleteLabel);
        bottomContainer.add((Component)leftActions, "West");
        bottomContainer.add((Component)rightActions, "East");
        this.add((Component)nameWrapper, "North");
        this.add((Component)bottomContainer, "Center");
        this.updateVisibility();
        this.updateFill();
        this.updateBorder();
        this.updateBorder();
        this.updateLabelling();
    }

    private void preview(boolean on) {
        if (this.visible) {
            return;
        }
        this.marker.getMarker().setVisible(on);
    }

    private void toggle(boolean on) {
        this.visible = on;
        this.marker.getMarker().setVisible(this.visible);
        this.plugin.updateConfig();
        this.updateVisibility();
    }

    private void toggleLabelling(boolean on) {
        this.showLabel = on;
        this.marker.getMarker().setLabelled(this.showLabel);
        this.plugin.updateConfig();
        this.updateLabelling();
    }

    private void save() {
        this.marker.getMarker().setName(this.nameInput.getText());
        this.plugin.updateConfig();
        this.nameInput.setEditable(false);
        this.updateNameActions(false);
        this.requestFocusInWindow();
    }

    private void cancel() {
        this.nameInput.setEditable(false);
        this.nameInput.setText(this.marker.getMarker().getName());
        this.updateNameActions(false);
        this.requestFocusInWindow();
    }

    private void updateNameActions(boolean saveAndCancel) {
        this.save.setVisible(saveAndCancel);
        this.cancel.setVisible(saveAndCancel);
        this.rename.setVisible(!saveAndCancel);
        if (saveAndCancel) {
            this.nameInput.getTextField().requestFocusInWindow();
            this.nameInput.getTextField().selectAll();
        }
    }

    private void updateThickness(boolean save) {
        this.marker.getMarker().setBorderThickness((Integer)this.thicknessSpinner.getValue());
        this.updateBorder();
        if (save) {
            this.plugin.updateConfig();
        }
    }

    private void updateVisibility() {
        this.visibilityLabel.setIcon(this.visible ? VISIBLE_ICON : INVISIBLE_ICON);
        this.visibilityLabel.setToolTipText(this.visible ? "Hide screen marker" : "Show screen marker");
    }

    private void updateLabelling() {
        this.labelIndicator.setIcon(this.showLabel ? LABEL_ICON : NO_LABEL_ICON);
        this.labelIndicator.setToolTipText(this.showLabel ? "Hide label" : "Show label");
    }

    private void updateFill() {
        boolean isFullyTransparent;
        boolean bl = isFullyTransparent = this.marker.getMarker().getFill().getAlpha() == 0;
        if (isFullyTransparent) {
            this.fillColorIndicator.setBorder(null);
        } else {
            Color color = this.marker.getMarker().getFill();
            Color fullColor = new Color(color.getRed(), color.getGreen(), color.getBlue());
            this.fillColorIndicator.setBorder(new MatteBorder(0, 0, 3, 0, fullColor));
        }
        this.fillColorIndicator.setIcon(isFullyTransparent ? NO_FILL_COLOR_ICON : FILL_COLOR_ICON);
    }

    private void updateBorder() {
        if (this.marker.getMarker().getBorderThickness() == 0) {
            this.borderColorIndicator.setBorder(null);
        } else {
            Color color = this.marker.getMarker().getColor();
            this.borderColorIndicator.setBorder(new MatteBorder(0, 0, 3, 0, color));
        }
        this.borderColorIndicator.setIcon(this.marker.getMarker().getBorderThickness() == 0 ? NO_BORDER_COLOR_ICON : BORDER_COLOR_ICON);
    }

    private void openFillColorPicker() {
        Color fillColor = this.marker.getMarker().getFill();
        RuneliteColorPicker colorPicker = this.plugin.getColorPickerManager().create(this, fillColor.getAlpha() == 0 ? ColorUtil.colorWithAlpha(fillColor, 75) : fillColor, this.marker.getMarker().getName() + " Fill", false);
        colorPicker.setLocationRelativeTo(this);
        colorPicker.setOnColorChange(c -> {
            this.marker.getMarker().setFill((Color)c);
            this.updateFill();
        });
        colorPicker.setOnClose(c -> this.plugin.updateConfig());
        colorPicker.setVisible(true);
    }

    private void openBorderColorPicker() {
        RuneliteColorPicker colorPicker = this.plugin.getColorPickerManager().create(this, this.marker.getMarker().getColor(), this.marker.getMarker().getName() + " Border", false);
        colorPicker.setLocationRelativeTo(this);
        colorPicker.setOnColorChange(c -> {
            this.marker.getMarker().setColor((Color)c);
            this.updateBorder();
        });
        colorPicker.setOnClose(c -> this.plugin.updateConfig());
        colorPicker.setVisible(true);
    }

    static {
        BufferedImage borderImg = ImageUtil.loadImageResource(ScreenMarkerPlugin.class, "border_color_icon.png");
        BufferedImage borderImgHover = ImageUtil.luminanceOffset(borderImg, -150);
        BORDER_COLOR_ICON = new ImageIcon(borderImg);
        BORDER_COLOR_HOVER_ICON = new ImageIcon(borderImgHover);
        NO_BORDER_COLOR_ICON = new ImageIcon(borderImgHover);
        NO_BORDER_COLOR_HOVER_ICON = new ImageIcon(ImageUtil.alphaOffset((Image)borderImgHover, -100));
        BufferedImage fillImg = ImageUtil.loadImageResource(ScreenMarkerPlugin.class, "fill_color_icon.png");
        BufferedImage fillImgHover = ImageUtil.luminanceOffset(fillImg, -150);
        FILL_COLOR_ICON = new ImageIcon(fillImg);
        FILL_COLOR_HOVER_ICON = new ImageIcon(fillImgHover);
        NO_FILL_COLOR_ICON = new ImageIcon(fillImgHover);
        NO_FILL_COLOR_HOVER_ICON = new ImageIcon(ImageUtil.alphaOffset((Image)fillImgHover, -100));
        BufferedImage labelImg = ImageUtil.loadImageResource(ScreenMarkerPlugin.class, "label_icon.png");
        BufferedImage labelImgHover = ImageUtil.luminanceOffset(labelImg, -150);
        LABEL_ICON = new ImageIcon(labelImg);
        LABEL_HOVER_ICON = new ImageIcon(labelImgHover);
        NO_LABEL_ICON = new ImageIcon(labelImgHover);
        NO_LABEL_HOVER_ICON = new ImageIcon(ImageUtil.alphaOffset((Image)labelImgHover, -100));
        BufferedImage visibleImg = ImageUtil.loadImageResource(ScreenMarkerPlugin.class, "visible_icon.png");
        VISIBLE_ICON = new ImageIcon(visibleImg);
        VISIBLE_HOVER_ICON = new ImageIcon(ImageUtil.alphaOffset((Image)visibleImg, -100));
        BufferedImage invisibleImg = ImageUtil.loadImageResource(ScreenMarkerPlugin.class, "invisible_icon.png");
        INVISIBLE_ICON = new ImageIcon(invisibleImg);
        INVISIBLE_HOVER_ICON = new ImageIcon(ImageUtil.alphaOffset((Image)invisibleImg, -100));
        BufferedImage deleteImg = ImageUtil.loadImageResource(ScreenMarkerPlugin.class, "delete_icon.png");
        DELETE_ICON = new ImageIcon(deleteImg);
        DELETE_HOVER_ICON = new ImageIcon(ImageUtil.alphaOffset((Image)deleteImg, -100));
    }
}

