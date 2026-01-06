/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.ui.components.materialtabs;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JPanel;
import net.runelite.client.ui.components.materialtabs.MaterialTab;

public class MaterialTabGroup
extends JPanel {
    private final JPanel display;
    private final List<MaterialTab> tabs = new ArrayList<MaterialTab>();

    public MaterialTabGroup(JPanel display) {
        this.display = display;
        if (display != null) {
            this.display.setLayout(new BorderLayout());
        }
        this.setLayout(new FlowLayout(1, 8, 0));
        this.setOpaque(false);
    }

    public MaterialTabGroup() {
        this((JPanel)null);
    }

    public MaterialTab getTab(int index) {
        if (this.tabs == null || this.tabs.isEmpty()) {
            return null;
        }
        return this.tabs.get(index);
    }

    public void addTab(MaterialTab tab) {
        this.tabs.add(tab);
        this.add((Component)tab, "North");
    }

    public boolean select(MaterialTab selectedTab) {
        if (!this.tabs.contains(selectedTab) || selectedTab.isSelected()) {
            return false;
        }
        if (!selectedTab.select()) {
            return false;
        }
        if (this.display != null) {
            this.display.removeAll();
            this.display.add(selectedTab.getContent());
            this.display.revalidate();
            this.display.repaint();
        }
        for (MaterialTab tab : this.tabs) {
            if (tab.equals(selectedTab)) continue;
            tab.unselect();
        }
        return true;
    }
}

