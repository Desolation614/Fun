/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Strings
 */
package net.runelite.client.ui.components.colorpicker;

import com.google.common.base.Strings;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.GraphicsConfiguration;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Arrays;
import java.util.Objects;
import java.util.function.Consumer;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.border.EmptyBorder;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.DocumentFilter;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.ui.ClientUI;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.components.colorpicker.ColorPanel;
import net.runelite.client.ui.components.colorpicker.ColorPickerManager;
import net.runelite.client.ui.components.colorpicker.ColorValuePanel;
import net.runelite.client.ui.components.colorpicker.HuePanel;
import net.runelite.client.ui.components.colorpicker.PreviewPanel;
import net.runelite.client.ui.components.colorpicker.RecentColors;
import net.runelite.client.util.ColorUtil;

public class RuneliteColorPicker
extends JDialog {
    static final String CONFIG_GROUP = "colorpicker";
    private static final int FRAME_WIDTH = 410;
    private static final int FRAME_HEIGHT = 380;
    private static final int TONE_PANEL_SIZE = 160;
    private static final String BLANK_HEX = "#000";
    private final ColorPanel colorPanel = new ColorPanel(160);
    private final HuePanel huePanel = new HuePanel(160);
    private final PreviewPanel afterPanel = new PreviewPanel();
    private final ColorValuePanel redSlider = new ColorValuePanel("Red");
    private final ColorValuePanel greenSlider = new ColorValuePanel("Green");
    private final ColorValuePanel blueSlider = new ColorValuePanel("Blue");
    private final ColorValuePanel alphaSlider = new ColorValuePanel("Opacity");
    private final JTextField hexInput = new JTextField();
    private final boolean alphaHidden;
    private Color selectedColor;
    private Consumer<Color> onColorChange;
    private Consumer<Color> onClose;

    RuneliteColorPicker(Window parent, final Color previousColor, String title, final boolean alphaHidden, ConfigManager configManager, final ColorPickerManager colorPickerManager) {
        super(parent, "RuneLite Color Picker - " + title, Dialog.ModalityType.MODELESS);
        this.selectedColor = previousColor;
        this.alphaHidden = alphaHidden;
        final RecentColors recentColors = new RecentColors(configManager);
        this.setDefaultCloseOperation(2);
        this.setResizable(false);
        this.setSize(410, 380);
        this.setDefaultCloseOperation(2);
        this.setIconImages(Arrays.asList(ClientUI.ICON_128, ClientUI.ICON_16));
        JPanel content = new JPanel(new BorderLayout());
        content.setBorder(new EmptyBorder(15, 15, 15, 15));
        JPanel colorSelection = new JPanel(new BorderLayout(15, 0));
        JPanel leftPanel = new JPanel(new BorderLayout(15, 0));
        leftPanel.add((Component)this.huePanel, "West");
        leftPanel.add((Component)this.colorPanel, "Center");
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new GridBagLayout());
        GridBagConstraints cx = new GridBagConstraints();
        cx.insets = new Insets(0, 0, 0, 0);
        JLabel old = new JLabel("Previous");
        old.setHorizontalAlignment(0);
        JLabel next = new JLabel(" Current ");
        next.setHorizontalAlignment(0);
        final PreviewPanel beforePanel = new PreviewPanel();
        beforePanel.setColor(previousColor);
        this.afterPanel.setColor(previousColor);
        JPanel hexContainer = new JPanel(new GridBagLayout());
        JLabel hexLabel = new JLabel("#");
        this.hexInput.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        JLabel label = new JLabel("Hex color");
        label.setVerticalAlignment(3);
        cx.weightx = 0.0;
        cx.fill = 1;
        cx.insets = new Insets(0, 1, 0, 1);
        hexContainer.add((Component)hexLabel, cx);
        cx.weightx = 1.0;
        cx.fill = 2;
        cx.gridwidth = 0;
        hexContainer.add((Component)this.hexInput, cx);
        cx.fill = 1;
        cx.weightx = 1.0;
        cx.weighty = 1.0;
        cx.gridy = 0;
        cx.gridx = 0;
        JPanel recentColorsContainer = recentColors.build(c -> {
            if (!alphaHidden) {
                this.alphaSlider.update(c.getAlpha());
            }
            this.colorChange((Color)c);
            this.updatePanels();
        }, alphaHidden);
        rightPanel.add((Component)recentColorsContainer, cx);
        cx.gridwidth = -1;
        ++cx.gridy;
        rightPanel.add((Component)old, cx);
        ++cx.gridx;
        rightPanel.add((Component)next, cx);
        cx.gridx = 0;
        ++cx.gridy;
        cx.gridwidth = -1;
        cx.fill = 1;
        rightPanel.add((Component)beforePanel, cx);
        ++cx.gridx;
        rightPanel.add((Component)this.afterPanel, cx);
        cx.gridwidth = 0;
        cx.gridx = 0;
        ++cx.gridy;
        rightPanel.add((Component)label, cx);
        ++cx.gridy;
        cx.fill = 2;
        rightPanel.add((Component)hexContainer, cx);
        JPanel slidersContainer = new JPanel(new GridLayout(4, 1, 0, 10));
        slidersContainer.setBorder(new EmptyBorder(15, 0, 0, 0));
        slidersContainer.add(this.redSlider);
        slidersContainer.add(this.greenSlider);
        slidersContainer.add(this.blueSlider);
        slidersContainer.add(this.alphaSlider);
        if (alphaHidden) {
            this.alphaSlider.setVisible(false);
            this.setSize(410, 340);
        }
        colorSelection.add((Component)leftPanel, "West");
        colorSelection.add((Component)rightPanel, "Center");
        colorSelection.add((Component)slidersContainer, "South");
        content.add((Component)colorSelection, "North");
        this.setContentPane(content);
        beforePanel.addMouseListener(new MouseAdapter(){

            @Override
            public void mousePressed(MouseEvent e) {
                if (!alphaHidden) {
                    RuneliteColorPicker.this.alphaSlider.update(beforePanel.getColor().getAlpha());
                }
                RuneliteColorPicker.this.colorChange(beforePanel.getColor());
                RuneliteColorPicker.this.updatePanels();
            }
        });
        this.huePanel.setOnColorChange((Integer y) -> {
            this.colorPanel.setBaseColor((int)y);
            this.updateText();
        });
        this.colorPanel.setOnColorChange(this::colorChange);
        ((AbstractDocument)this.hexInput.getDocument()).setDocumentFilter(new DocumentFilter(){

            @Override
            public void replace(DocumentFilter.FilterBypass fb, int offset, int length, String str, AttributeSet attrs) throws BadLocationException {
                String text = RuneliteColorPicker.getReplacedText(fb, offset, length, str = str.replaceAll("#|0x", ""));
                if (!ColorUtil.isHex(text)) {
                    Toolkit.getDefaultToolkit().beep();
                    return;
                }
                super.replace(fb, offset, length, str, attrs);
            }
        });
        this.hexInput.addFocusListener(new FocusAdapter(){

            @Override
            public void focusLost(FocusEvent e) {
                RuneliteColorPicker.this.updateHex();
            }
        });
        this.hexInput.addActionListener(e -> this.updateHex());
        this.redSlider.setOnValueChanged(i -> {
            this.colorChange(new Color((int)i, this.selectedColor.getGreen(), this.selectedColor.getBlue()));
            this.updatePanels();
        });
        this.greenSlider.setOnValueChanged(i -> {
            this.colorChange(new Color(this.selectedColor.getRed(), (int)i, this.selectedColor.getBlue()));
            this.updatePanels();
        });
        this.blueSlider.setOnValueChanged(i -> {
            this.colorChange(new Color(this.selectedColor.getRed(), this.selectedColor.getGreen(), (int)i));
            this.updatePanels();
        });
        this.alphaSlider.setOnValueChanged(i -> this.colorChange(new Color(this.selectedColor.getRed(), this.selectedColor.getGreen(), this.selectedColor.getBlue(), (int)i)));
        this.updatePanels();
        this.updateText();
        this.addWindowListener(new WindowAdapter(){

            @Override
            public void windowClosing(WindowEvent e) {
                RuneliteColorPicker cp;
                RuneliteColorPicker.this.updateHex();
                if (RuneliteColorPicker.this.onClose != null) {
                    RuneliteColorPicker.this.onClose.accept(RuneliteColorPicker.this.selectedColor);
                }
                if (!Objects.equals(previousColor, RuneliteColorPicker.this.selectedColor)) {
                    recentColors.add("" + RuneliteColorPicker.this.selectedColor.getRGB());
                }
                if (Objects.equals(cp = colorPickerManager.getCurrentPicker(), RuneliteColorPicker.this)) {
                    colorPickerManager.setCurrentPicker(null);
                }
            }
        });
        this.getRootPane().registerKeyboardAction(_ev -> this.dispatchEvent(new WindowEvent(this, 201)), KeyStroke.getKeyStroke(27, 0), 2);
    }

    private void updatePanels() {
        this.huePanel.select(this.selectedColor);
        this.colorPanel.moveToClosestColor(this.huePanel.getSelectedY(), this.selectedColor);
    }

    private void updateText() {
        String hex = this.alphaHidden ? ColorUtil.colorToHexCode(this.getSelectedColor()) : ColorUtil.colorToAlphaHexCode(this.getSelectedColor());
        this.hexInput.setText(hex.toUpperCase());
        this.afterPanel.setColor(this.selectedColor);
        this.redSlider.update(this.selectedColor.getRed());
        this.greenSlider.update(this.selectedColor.getGreen());
        this.blueSlider.update(this.selectedColor.getBlue());
        if (!this.alphaHidden) {
            this.alphaSlider.update(this.selectedColor.getAlpha());
        }
    }

    private void colorChange(Color newColor) {
        if (newColor == this.selectedColor) {
            return;
        }
        this.selectedColor = newColor;
        if (this.selectedColor.getAlpha() != this.alphaSlider.getValue()) {
            this.selectedColor = new Color(this.selectedColor.getRed(), this.selectedColor.getGreen(), this.selectedColor.getBlue(), this.alphaSlider.getValue());
        }
        this.updateText();
        if (this.onColorChange != null) {
            this.onColorChange.accept(this.selectedColor);
        }
    }

    private void updateHex() {
        Color color;
        String hex = this.hexInput.getText();
        if (Strings.isNullOrEmpty((String)hex)) {
            hex = BLANK_HEX;
        }
        if ((color = ColorUtil.fromHex(hex)) == null) {
            return;
        }
        if (!this.alphaHidden && ColorUtil.isAlphaHex(hex)) {
            this.alphaSlider.update(color.getAlpha());
        }
        this.colorChange(color);
        this.updatePanels();
    }

    @Override
    public void setLocationRelativeTo(Component c) {
        if (this.getOwner() == null) {
            super.setLocationRelativeTo(c);
            return;
        }
        GraphicsConfiguration gc = this.getOwner().getGraphicsConfiguration();
        Insets insets = Toolkit.getDefaultToolkit().getScreenInsets(gc);
        Rectangle gcBounds = gc.getBounds();
        gcBounds.setRect(gcBounds.x + insets.left, gcBounds.y + insets.top, gcBounds.width - insets.left - insets.right, gcBounds.height - insets.top - insets.bottom);
        Dimension compSize = c.getSize();
        Point compLocation = c.getLocationOnScreen();
        Dimension windowSize = this.getSize();
        int dx = compLocation.x + (compSize.width - windowSize.width) / 2;
        int dy = compLocation.y + (compSize.height - windowSize.height) / 2;
        if (dy + windowSize.height > gcBounds.y + gcBounds.height) {
            dy = gcBounds.y + gcBounds.height - windowSize.height;
        }
        if (dy < gcBounds.y) {
            dy = gcBounds.y;
        }
        if (dx + windowSize.width > gcBounds.x + gcBounds.width) {
            dx = gcBounds.x + gcBounds.width - windowSize.width;
        }
        if (dx < gcBounds.x) {
            dx = gcBounds.x;
        }
        this.setLocation(dx, dy);
    }

    static String getReplacedText(DocumentFilter.FilterBypass fb, int offset, int length, String str) throws BadLocationException {
        Document doc = fb.getDocument();
        StringBuilder sb = new StringBuilder(doc.getText(0, doc.getLength()));
        sb.replace(offset, offset + length, str);
        return sb.toString();
    }

    public Color getSelectedColor() {
        return this.selectedColor;
    }

    public void setOnColorChange(Consumer<Color> onColorChange) {
        this.onColorChange = onColorChange;
    }

    public void setOnClose(Consumer<Color> onClose) {
        this.onClose = onClose;
    }
}

