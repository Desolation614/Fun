/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.inject.Singleton
 *  javax.inject.Inject
 */
package net.runelite.client.plugins.skillcalculator;

import com.google.inject.Singleton;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import javax.inject.Inject;
import javax.swing.ImageIcon;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import net.runelite.client.game.SkillIconManager;
import net.runelite.client.plugins.skillcalculator.CalculatorType;
import net.runelite.client.plugins.skillcalculator.SkillCalculator;
import net.runelite.client.plugins.skillcalculator.UICalculatorInputArea;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.PluginPanel;
import net.runelite.client.ui.components.materialtabs.MaterialTab;
import net.runelite.client.ui.components.materialtabs.MaterialTabGroup;

@Singleton
class SkillCalculatorPanel
extends PluginPanel {
    private final SkillCalculator uiCalculator;
    private final SkillIconManager iconManager;
    private final MaterialTabGroup tabGroup;
    private MaterialTab currentTab;
    private boolean shouldForceReload;

    @Inject
    SkillCalculatorPanel(SkillCalculator skillCalculator, SkillIconManager iconManager, UICalculatorInputArea uiInput) {
        this.getScrollPane().setVerticalScrollBarPolicy(22);
        this.iconManager = iconManager;
        this.uiCalculator = skillCalculator;
        this.setBorder(new EmptyBorder(10, 10, 10, 10));
        this.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.fill = 2;
        c.weightx = 1.0;
        c.gridx = 0;
        c.gridy = 0;
        this.tabGroup = new MaterialTabGroup();
        this.tabGroup.setLayout(new GridLayout(0, 6, 7, 7));
        this.addCalculatorButtons();
        uiInput.setBorder(new EmptyBorder(15, 0, 15, 0));
        uiInput.setBackground(ColorScheme.DARK_GRAY_COLOR);
        this.add((Component)this.tabGroup, c);
        ++c.gridy;
        this.add((Component)uiInput, c);
        ++c.gridy;
        this.add((Component)this.uiCalculator, c);
        ++c.gridy;
    }

    private void addCalculatorButtons() {
        for (CalculatorType calculatorType : CalculatorType.values()) {
            ImageIcon icon = new ImageIcon(this.iconManager.getSkillImage(calculatorType.getSkill(), true));
            MaterialTab tab = new MaterialTab(icon, this.tabGroup, null);
            tab.setOnSelectEvent(() -> {
                this.uiCalculator.openCalculator(calculatorType, this.shouldForceReload);
                this.currentTab = tab;
                this.shouldForceReload = false;
                return true;
            });
            this.tabGroup.addTab(tab);
        }
    }

    void reloadCurrentCalculator() {
        if (this.currentTab != null) {
            this.shouldForceReload = true;
            SwingUtilities.invokeLater(() -> this.tabGroup.select(this.currentTab));
        }
    }
}

