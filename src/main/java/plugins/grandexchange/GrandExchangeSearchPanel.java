/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Strings
 *  javax.inject.Inject
 *  net.runelite.api.ItemComposition
 *  net.runelite.http.api.item.ItemPrice
 */
package net.runelite.client.plugins.grandexchange;

import com.google.common.base.Strings;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import javax.inject.Inject;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import net.runelite.api.ItemComposition;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.RuneLiteConfig;
import net.runelite.client.game.ItemManager;
import net.runelite.client.game.ItemStats;
import net.runelite.client.plugins.grandexchange.GrandExchangeItemPanel;
import net.runelite.client.plugins.grandexchange.GrandExchangeItems;
import net.runelite.client.plugins.grandexchange.GrandExchangePlugin;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.components.IconTextField;
import net.runelite.client.ui.components.PluginErrorPanel;
import net.runelite.client.util.AsyncBufferedImage;
import net.runelite.http.api.item.ItemPrice;

class GrandExchangeSearchPanel
extends JPanel {
    private static final String ERROR_PANEL = "ERROR_PANEL";
    private static final String RESULTS_PANEL = "RESULTS_PANEL";
    private static final int MAX_SEARCH_ITEMS = 100;
    private final GridBagConstraints constraints = new GridBagConstraints();
    private final CardLayout cardLayout = new CardLayout();
    private final ClientThread clientThread;
    private final ItemManager itemManager;
    private final ScheduledExecutorService executor;
    private final RuneLiteConfig runeLiteConfig;
    private final GrandExchangePlugin grandExchangePlugin;
    private final IconTextField searchBar = new IconTextField();
    private final JPanel searchItemsPanel = new JPanel();
    private final JPanel centerPanel = new JPanel(this.cardLayout);
    private final PluginErrorPanel errorPanel = new PluginErrorPanel();

    @Inject
    private GrandExchangeSearchPanel(ClientThread clientThread, ItemManager itemManager, ScheduledExecutorService executor, RuneLiteConfig runeLiteConfig, GrandExchangePlugin grandExchangePlugin) {
        this.clientThread = clientThread;
        this.itemManager = itemManager;
        this.executor = executor;
        this.runeLiteConfig = runeLiteConfig;
        this.grandExchangePlugin = grandExchangePlugin;
        this.setLayout(new BorderLayout());
        this.setBackground(ColorScheme.DARK_GRAY_COLOR);
        JPanel container = new JPanel();
        container.setLayout(new BorderLayout(5, 5));
        container.setBorder(new EmptyBorder(10, 10, 10, 10));
        container.setBackground(ColorScheme.DARK_GRAY_COLOR);
        this.searchBar.setIcon(IconTextField.Icon.SEARCH);
        this.searchBar.setPreferredSize(new Dimension(100, 30));
        this.searchBar.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        this.searchBar.setHoverBackgroundColor(ColorScheme.DARK_GRAY_HOVER_COLOR);
        this.searchBar.addActionListener(e -> executor.execute(() -> this.priceLookup(false)));
        this.searchBar.addClearListener(this::updateSearch);
        this.searchItemsPanel.setLayout(new GridBagLayout());
        this.searchItemsPanel.setBackground(ColorScheme.DARK_GRAY_COLOR);
        this.constraints.fill = 2;
        this.constraints.weightx = 1.0;
        this.constraints.gridx = 0;
        this.constraints.gridy = 0;
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(ColorScheme.DARK_GRAY_COLOR);
        wrapper.add((Component)this.searchItemsPanel, "North");
        JScrollPane resultsWrapper = new JScrollPane(wrapper);
        resultsWrapper.setBackground(ColorScheme.DARK_GRAY_COLOR);
        resultsWrapper.getVerticalScrollBar().setPreferredSize(new Dimension(12, 0));
        resultsWrapper.getVerticalScrollBar().setBorder(new EmptyBorder(0, 5, 0, 0));
        resultsWrapper.setVisible(false);
        JPanel errorWrapper = new JPanel(new BorderLayout());
        errorWrapper.setBackground(ColorScheme.DARK_GRAY_COLOR);
        errorWrapper.add((Component)this.errorPanel, "North");
        this.errorPanel.setContent("Grand Exchange Search", "Here you can search for an item by its name to find price information.");
        this.centerPanel.add((Component)resultsWrapper, RESULTS_PANEL);
        this.centerPanel.add((Component)errorWrapper, ERROR_PANEL);
        this.cardLayout.show(this.centerPanel, ERROR_PANEL);
        container.add((Component)this.searchBar, "North");
        container.add((Component)this.centerPanel, "Center");
        this.add((Component)container, "Center");
    }

    void priceLookup(String item) {
        this.searchBar.setText(item);
        this.executor.execute(() -> this.priceLookup(true));
    }

    private boolean updateSearch() {
        String lookup = this.searchBar.getText();
        if (Strings.isNullOrEmpty((String)lookup)) {
            this.searchItemsPanel.removeAll();
            SwingUtilities.invokeLater(this.searchItemsPanel::updateUI);
            return false;
        }
        this.searchItemsPanel.removeAll();
        this.searchBar.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        this.searchBar.setEditable(false);
        this.searchBar.setIcon(IconTextField.Icon.LOADING);
        return true;
    }

    private void priceLookup(boolean exactMatch) {
        if (!this.updateSearch()) {
            return;
        }
        List<ItemPrice> result = this.itemManager.search(this.searchBar.getText());
        if (result.isEmpty()) {
            this.searchBar.setIcon(IconTextField.Icon.ERROR);
            this.errorPanel.setContent("No results found.", "No items were found with that name, please try again.");
            this.cardLayout.show(this.centerPanel, ERROR_PANEL);
            this.searchBar.setEditable(true);
            return;
        }
        this.clientThread.invokeLater(() -> this.processResult(result, this.searchBar.getText(), exactMatch));
    }

    private void processResult(List<ItemPrice> result, String lookup, boolean exactMatch) {
        ArrayList<GrandExchangeItems> itemsList = new ArrayList<GrandExchangeItems>();
        this.cardLayout.show(this.centerPanel, RESULTS_PANEL);
        int count = 0;
        boolean useActivelyTradedPrice = this.runeLiteConfig.useWikiItemPrices();
        for (ItemPrice item : result) {
            int itemId = item.getId();
            ItemComposition itemComp = this.itemManager.getItemComposition(itemId);
            if (!itemComp.isTradeable()) continue;
            if (count++ > 100) break;
            ItemStats itemStats = this.itemManager.getItemStats(itemId);
            int itemPrice = useActivelyTradedPrice ? this.itemManager.getWikiPrice(item) : item.getPrice();
            int itemLimit = itemStats != null ? itemStats.getGeLimit() : 0;
            int haPrice = itemComp.getHaPrice();
            AsyncBufferedImage itemImage = this.itemManager.getImage(itemId);
            itemsList.add(new GrandExchangeItems(itemImage, item.getName(), itemId, itemPrice, haPrice, itemLimit));
            if (!exactMatch || !item.getName().equalsIgnoreCase(lookup)) continue;
            break;
        }
        SwingUtilities.invokeLater(() -> {
            int index = 0;
            for (GrandExchangeItems item : itemsList) {
                GrandExchangeItemPanel panel = new GrandExchangeItemPanel(this.grandExchangePlugin, item.getIcon(), item.getName(), item.getItemId(), item.getGePrice(), item.getHaPrice(), item.getGeItemLimit());
                if (index++ > 0) {
                    JPanel marginWrapper = new JPanel(new BorderLayout());
                    marginWrapper.setBackground(ColorScheme.DARK_GRAY_COLOR);
                    marginWrapper.setBorder(new EmptyBorder(5, 0, 0, 0));
                    marginWrapper.add((Component)panel, "North");
                    this.searchItemsPanel.add((Component)marginWrapper, this.constraints);
                } else {
                    this.searchItemsPanel.add((Component)panel, this.constraints);
                }
                ++this.constraints.gridy;
            }
            if (!exactMatch) {
                this.searchItemsPanel.requestFocusInWindow();
            }
            this.searchBar.setEditable(true);
            if (!itemsList.isEmpty()) {
                this.searchBar.setIcon(IconTextField.Icon.SEARCH);
            }
        });
    }
}

