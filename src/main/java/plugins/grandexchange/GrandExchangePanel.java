/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.inject.Inject
 */
package net.runelite.client.plugins.grandexchange;

import java.awt.BorderLayout;
import java.awt.Component;
import javax.inject.Inject;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import net.runelite.client.plugins.grandexchange.GrandExchangeOffersPanel;
import net.runelite.client.plugins.grandexchange.GrandExchangeSearchPanel;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.PluginPanel;
import net.runelite.client.ui.components.materialtabs.MaterialTab;
import net.runelite.client.ui.components.materialtabs.MaterialTabGroup;

class GrandExchangePanel
extends PluginPanel {
    private final JPanel display = new JPanel();
    private final MaterialTabGroup tabGroup = new MaterialTabGroup(this.display);
    private final MaterialTab searchTab;
    private final GrandExchangeSearchPanel searchPanel;
    private final GrandExchangeOffersPanel offersPanel;

    @Inject
    private GrandExchangePanel(GrandExchangeSearchPanel searchPanel, GrandExchangeOffersPanel offersPanel) {
        super(false);
        this.searchPanel = searchPanel;
        this.offersPanel = offersPanel;
        this.setLayout(new BorderLayout());
        this.setBackground(ColorScheme.DARK_GRAY_COLOR);
        MaterialTab offersTab = new MaterialTab("Offers", this.tabGroup, (JComponent)offersPanel);
        this.searchTab = new MaterialTab("Search", this.tabGroup, (JComponent)searchPanel);
        this.tabGroup.setBorder(new EmptyBorder(5, 0, 0, 0));
        this.tabGroup.addTab(offersTab);
        this.tabGroup.addTab(this.searchTab);
        this.tabGroup.select(offersTab);
        this.add((Component)this.tabGroup, "North");
        this.add((Component)this.display, "Center");
    }

    void showSearch() {
        if (this.searchPanel.isShowing()) {
            return;
        }
        this.tabGroup.select(this.searchTab);
        this.revalidate();
    }

    public GrandExchangeSearchPanel getSearchPanel() {
        return this.searchPanel;
    }

    public GrandExchangeOffersPanel getOffersPanel() {
        return this.offersPanel;
    }
}

