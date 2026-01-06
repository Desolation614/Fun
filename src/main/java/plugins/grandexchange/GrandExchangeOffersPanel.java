/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.inject.Inject
 *  net.runelite.api.GrandExchangeOffer
 *  net.runelite.api.GrandExchangeOfferState
 *  net.runelite.api.ItemComposition
 */
package net.runelite.client.plugins.grandexchange;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import javax.inject.Inject;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import net.runelite.api.GrandExchangeOffer;
import net.runelite.api.GrandExchangeOfferState;
import net.runelite.api.ItemComposition;
import net.runelite.client.plugins.grandexchange.GrandExchangeOfferSlot;
import net.runelite.client.plugins.grandexchange.GrandExchangePlugin;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.components.PluginErrorPanel;

class GrandExchangeOffersPanel
extends JPanel {
    private static final String ERROR_PANEL = "ERROR_PANEL";
    private static final String OFFERS_PANEL = "OFFERS_PANEL";
    private static final int MAX_OFFERS = 8;
    private final GrandExchangePlugin grandExchangePlugin;
    private final GridBagConstraints constraints = new GridBagConstraints();
    private final CardLayout cardLayout = new CardLayout();
    private final JPanel offerPanel = new JPanel();
    private final JPanel container = new JPanel(this.cardLayout);
    private final GrandExchangeOfferSlot[] offerSlotPanels = new GrandExchangeOfferSlot[8];

    @Inject
    private GrandExchangeOffersPanel(GrandExchangePlugin grandExchangePlugin) {
        this.grandExchangePlugin = grandExchangePlugin;
        this.setLayout(new BorderLayout());
        this.setBackground(ColorScheme.DARK_GRAY_COLOR);
        this.constraints.fill = 2;
        this.constraints.weightx = 1.0;
        this.constraints.gridx = 0;
        this.constraints.gridy = 0;
        JPanel offersWrapper = new JPanel(new BorderLayout());
        offersWrapper.setBackground(ColorScheme.DARK_GRAY_COLOR);
        offersWrapper.add((Component)this.offerPanel, "North");
        this.offerPanel.setLayout(new GridBagLayout());
        this.offerPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        this.offerPanel.setBackground(ColorScheme.DARK_GRAY_COLOR);
        JPanel errorWrapper = new JPanel(new BorderLayout());
        errorWrapper.setBackground(ColorScheme.DARK_GRAY_COLOR);
        PluginErrorPanel errorPanel = new PluginErrorPanel();
        errorWrapper.add((Component)errorPanel, "North");
        errorPanel.setBorder(new EmptyBorder(50, 20, 20, 20));
        errorPanel.setContent("No offers detected", "No grand exchange offers were found on your account.");
        this.container.add((Component)offersWrapper, OFFERS_PANEL);
        this.container.add((Component)errorWrapper, ERROR_PANEL);
        this.add((Component)this.container, "Center");
        this.resetOffers();
    }

    void resetOffers() {
        this.offerPanel.removeAll();
        Arrays.fill(this.offerSlotPanels, null);
        this.updateEmptyOffersPanel();
    }

    void updateOffer(ItemComposition item, BufferedImage itemImage, GrandExchangeOffer newOffer, int slot) {
        if (newOffer == null || newOffer.getState() == GrandExchangeOfferState.EMPTY) {
            if (this.offerSlotPanels[slot] != null) {
                this.offerPanel.remove(this.offerSlotPanels[slot]);
                this.offerSlotPanels[slot] = null;
                this.revalidate();
                this.repaint();
            }
            this.removeTopMargin();
            this.updateEmptyOffersPanel();
            return;
        }
        GrandExchangeOfferSlot offerSlot = this.offerSlotPanels[slot];
        if (offerSlot == null) {
            this.offerSlotPanels[slot] = offerSlot = new GrandExchangeOfferSlot(this.grandExchangePlugin);
            this.offerPanel.add((Component)offerSlot, this.constraints);
            ++this.constraints.gridy;
        }
        offerSlot.updateOffer(item, itemImage, newOffer);
        this.removeTopMargin();
        this.revalidate();
        this.repaint();
        this.updateEmptyOffersPanel();
    }

    private void removeTopMargin() {
        if (this.offerPanel.getComponentCount() <= 0) {
            return;
        }
        JPanel firstItem = (JPanel)this.offerPanel.getComponent(0);
        firstItem.setBorder(null);
    }

    private void updateEmptyOffersPanel() {
        int nullCount = 0;
        for (GrandExchangeOfferSlot slot : this.offerSlotPanels) {
            if (slot != null) continue;
            ++nullCount;
        }
        if (nullCount == 8) {
            this.offerPanel.removeAll();
            this.cardLayout.show(this.container, ERROR_PANEL);
        } else {
            this.cardLayout.show(this.container, OFFERS_PANEL);
        }
    }
}

