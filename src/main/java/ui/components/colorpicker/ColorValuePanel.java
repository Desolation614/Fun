/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.ui.components.colorpicker;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.function.Consumer;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.components.colorpicker.ColorValueSlider;
import net.runelite.client.ui.components.colorpicker.RuneliteColorPicker;
import net.runelite.client.util.ColorUtil;

public class ColorValuePanel
extends JPanel {
    private static final int DEFAULT_VALUE = 255;
    private final ColorValueSlider slider = new ColorValueSlider();
    private final JTextField input = new JTextField();
    private Consumer<Integer> onValueChanged;

    void setOnValueChanged(Consumer<Integer> c) {
        this.onValueChanged = c;
        this.slider.setOnValueChanged(c);
    }

    ColorValuePanel(String labelText) {
        this.setLayout(new BorderLayout(10, 0));
        this.setBackground(ColorScheme.DARK_GRAY_COLOR);
        this.input.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        this.input.setPreferredSize(new Dimension(35, 30));
        this.input.setBorder(new EmptyBorder(5, 5, 5, 5));
        ((AbstractDocument)this.input.getDocument()).setDocumentFilter(new DocumentFilter(){

            @Override
            public void replace(DocumentFilter.FilterBypass fb, int offset, int length, String str, AttributeSet attrs) throws BadLocationException {
                try {
                    String text = RuneliteColorPicker.getReplacedText(fb, offset, length, str);
                    int value = Integer.parseInt(text);
                    if (value < 0 || value > 255) {
                        Toolkit.getDefaultToolkit().beep();
                        return;
                    }
                    super.replace(fb, offset, length, str, attrs);
                }
                catch (NumberFormatException e) {
                    Toolkit.getDefaultToolkit().beep();
                }
            }
        });
        this.input.addFocusListener(new FocusAdapter(){

            @Override
            public void focusLost(FocusEvent e) {
                ColorValuePanel.this.updateText();
            }
        });
        this.input.addActionListener(a -> this.updateText());
        JLabel label = new JLabel(labelText);
        label.setPreferredSize(new Dimension(45, 0));
        label.setForeground(Color.WHITE);
        this.slider.setBackground(ColorScheme.DARK_GRAY_COLOR);
        this.slider.setBorder(new EmptyBorder(0, 0, 5, 0));
        this.slider.setPreferredSize(new Dimension(259, 30));
        this.update(255);
        this.add((Component)label, "West");
        this.add((Component)this.slider, "Center");
        this.add((Component)this.input, "East");
    }

    private void updateText() {
        int value = Integer.parseInt(this.input.getText());
        this.update(value);
        if (this.onValueChanged != null) {
            this.onValueChanged.accept(this.getValue());
        }
    }

    public void update(int value) {
        value = ColorUtil.constrainValue(value);
        this.slider.setValue(value);
        this.input.setText("" + value);
    }

    public int getValue() {
        return this.slider.getValue();
    }
}

