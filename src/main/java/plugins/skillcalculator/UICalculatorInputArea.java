/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.inject.Inject
 *  com.google.inject.Singleton
 */
package net.runelite.client.plugins.skillcalculator;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.GridLayout;
import java.util.regex.Pattern;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.EmptyBorder;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.FontManager;
import net.runelite.client.ui.UnitFormatterFactory;
import net.runelite.client.ui.components.FlatTextField;

@Singleton
class UICalculatorInputArea
extends JPanel {
    private static final Pattern NON_NUMERIC = Pattern.compile("\\D");
    private final JTextField uiFieldCurrentLevel;
    private final JTextField uiFieldCurrentXP;
    private final JTextField uiFieldTargetLevel;
    private final JTextField uiFieldTargetXP;
    private final JSpinner uiFieldXPMultiplier;

    @Inject
    UICalculatorInputArea() {
        this.setLayout(new GridLayout(3, 2, 7, 7));
        this.uiFieldCurrentLevel = this.addComponent("Current Level");
        this.uiFieldCurrentXP = this.addComponent("Current Experience");
        this.uiFieldTargetLevel = this.addComponent("Target Level");
        this.uiFieldTargetXP = this.addComponent("Target Experience");
        this.uiFieldXPMultiplier = this.addMultiplicationSpinnerComponent("XP Multiplier", 32);
    }

    int getCurrentLevelInput() {
        return UICalculatorInputArea.getInput(this.uiFieldCurrentLevel);
    }

    void setCurrentLevelInput(int value) {
        UICalculatorInputArea.setInput(this.uiFieldCurrentLevel, (Object)value);
    }

    int getCurrentXPInput() {
        return UICalculatorInputArea.getInput(this.uiFieldCurrentXP);
    }

    void setCurrentXPInput(Object value) {
        UICalculatorInputArea.setInput(this.uiFieldCurrentXP, value);
    }

    int getTargetLevelInput() {
        return UICalculatorInputArea.getInput(this.uiFieldTargetLevel);
    }

    void setTargetLevelInput(Object value) {
        UICalculatorInputArea.setInput(this.uiFieldTargetLevel, value);
    }

    int getTargetXPInput() {
        return UICalculatorInputArea.getInput(this.uiFieldTargetXP);
    }

    void setTargetXPInput(Object value) {
        UICalculatorInputArea.setInput(this.uiFieldTargetXP, value);
    }

    int getXPMultiplierInput() {
        return UICalculatorInputArea.getInput(this.uiFieldXPMultiplier);
    }

    void setXPMultiplier(Object value) {
        UICalculatorInputArea.setInput(this.uiFieldXPMultiplier, value);
    }

    void setNeededXP(Object value) {
        this.uiFieldTargetXP.setToolTipText((String)value);
    }

    private static int getInput(JTextField field) {
        try {
            return Integer.parseInt(NON_NUMERIC.matcher(field.getText()).replaceAll(""));
        }
        catch (NumberFormatException e) {
            return 0;
        }
    }

    private static int getInput(JSpinner field) {
        try {
            return Integer.parseInt(NON_NUMERIC.matcher(field.getModel().getValue().toString()).replaceAll(""));
        }
        catch (NumberFormatException e) {
            return 0;
        }
    }

    private static void setInput(JTextField field, Object value) {
        field.setText(String.valueOf(value));
    }

    private static void setInput(JSpinner field, Object value) {
        ((JSpinner.DefaultEditor)field.getEditor()).getTextField().setValue(value);
    }

    private JTextField addComponent(String label) {
        JPanel container = new JPanel();
        container.setLayout(new BorderLayout());
        JLabel uiLabel = new JLabel(label);
        FlatTextField uiInput = new FlatTextField();
        uiInput.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        uiInput.setHoverBackgroundColor(ColorScheme.DARK_GRAY_HOVER_COLOR);
        uiInput.setBorder(new EmptyBorder(5, 7, 5, 7));
        uiLabel.setFont(FontManager.getRunescapeSmallFont());
        uiLabel.setBorder(new EmptyBorder(0, 0, 4, 0));
        uiLabel.setForeground(Color.WHITE);
        container.add((Component)uiLabel, "North");
        container.add((Component)uiInput, "Center");
        this.add(container);
        return uiInput.getTextField();
    }

    private JSpinner addMultiplicationSpinnerComponent(String label, int max) {
        JPanel container = new JPanel();
        container.setLayout(new BorderLayout());
        JLabel uiLabel = new JLabel(label);
        SpinnerNumberModel model = new SpinnerNumberModel(1, 1, max, 1);
        JSpinner uiInput = new JSpinner(model);
        JSpinner.DefaultEditor editor = (JSpinner.DefaultEditor)uiInput.getEditor();
        JFormattedTextField spinnerTextField = editor.getTextField();
        spinnerTextField.setHorizontalAlignment(2);
        spinnerTextField.setFormatterFactory(new UnitFormatterFactory(spinnerTextField.getFormatterFactory(), "x"));
        uiInput.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        uiInput.setBorder(new EmptyBorder(5, 7, 5, 7));
        uiLabel.setFont(FontManager.getRunescapeSmallFont());
        uiLabel.setBorder(new EmptyBorder(0, 0, 4, 0));
        uiLabel.setForeground(Color.WHITE);
        container.add((Component)uiLabel, "North");
        container.add((Component)uiInput, "Center");
        this.add(container);
        return uiInput;
    }

    public JTextField getUiFieldCurrentLevel() {
        return this.uiFieldCurrentLevel;
    }

    public JTextField getUiFieldCurrentXP() {
        return this.uiFieldCurrentXP;
    }

    public JTextField getUiFieldTargetLevel() {
        return this.uiFieldTargetLevel;
    }

    public JTextField getUiFieldTargetXP() {
        return this.uiFieldTargetXP;
    }

    public JSpinner getUiFieldXPMultiplier() {
        return this.uiFieldXPMultiplier;
    }
}

