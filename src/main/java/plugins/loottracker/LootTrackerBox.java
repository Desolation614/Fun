/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Strings
 *  javax.annotation.Nullable
 *  net.runelite.http.api.loottracker.LootRecordType
 */
package net.runelite.client.plugins.loottracker;

import com.google.common.base.Strings;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.ToLongFunction;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.border.EmptyBorder;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.loottracker.LootTrackerItem;
import net.runelite.client.plugins.loottracker.LootTrackerMapping;
import net.runelite.client.plugins.loottracker.LootTrackerPriceType;
import net.runelite.client.plugins.loottracker.LootTrackerRecord;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.FontManager;
import net.runelite.client.util.AsyncBufferedImage;
import net.runelite.client.util.ImageUtil;
import net.runelite.client.util.QuantityFormatter;
import net.runelite.client.util.Text;
import net.runelite.http.api.loottracker.LootRecordType;

class LootTrackerBox
extends JPanel {
    private static final int ITEMS_PER_ROW = 5;
    private static final int TITLE_PADDING = 5;
    private final JPanel itemContainer = new JPanel();
    private final JLabel priceLabel = new JLabel();
    private final JLabel subTitleLabel = new JLabel();
    private final JPanel logTitle = new JPanel();
    private final ItemManager itemManager;
    private final String id;
    private final LootRecordType lootRecordType;
    private final LootTrackerPriceType priceType;
    private final boolean showPriceType;
    private int kills;
    private final List<LootTrackerItem> items = new ArrayList<LootTrackerItem>();
    private long totalPrice;
    private final boolean hideIgnoredItems;
    private final BiConsumer<String, Boolean> onItemToggle;

    LootTrackerBox(ItemManager itemManager, String id, LootRecordType lootRecordType, @Nullable String subtitle, boolean hideIgnoredItems, LootTrackerPriceType priceType, boolean showPriceType, BiConsumer<String, Boolean> onItemToggle, BiConsumer<String, Boolean> onEventToggle, boolean eventIgnored) {
        this.id = id;
        this.lootRecordType = lootRecordType;
        this.itemManager = itemManager;
        this.onItemToggle = onItemToggle;
        this.hideIgnoredItems = hideIgnoredItems;
        this.priceType = priceType;
        this.showPriceType = showPriceType;
        this.setLayout(new BorderLayout(0, 1));
        this.setBorder(new EmptyBorder(5, 0, 0, 0));
        this.logTitle.setLayout(new BoxLayout(this.logTitle, 0));
        this.logTitle.setBorder(new EmptyBorder(7, 7, 7, 7));
        this.logTitle.setBackground(eventIgnored ? ColorScheme.DARKER_GRAY_HOVER_COLOR : ColorScheme.DARKER_GRAY_COLOR.darker());
        JLabel titleLabel = new JLabel();
        titleLabel.setText(Text.removeTags(id));
        titleLabel.setFont(FontManager.getRunescapeSmallFont());
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setMinimumSize(new Dimension(1, titleLabel.getPreferredSize().height));
        this.logTitle.add(titleLabel);
        this.subTitleLabel.setFont(FontManager.getRunescapeSmallFont());
        this.subTitleLabel.setForeground(ColorScheme.LIGHT_GRAY_COLOR);
        if (!Strings.isNullOrEmpty((String)subtitle)) {
            this.subTitleLabel.setText(subtitle);
        }
        this.logTitle.add(Box.createRigidArea(new Dimension(5, 0)));
        this.logTitle.add(this.subTitleLabel);
        this.logTitle.add(Box.createHorizontalGlue());
        this.logTitle.add(Box.createRigidArea(new Dimension(5, 0)));
        this.priceLabel.setFont(FontManager.getRunescapeSmallFont());
        this.priceLabel.setForeground(ColorScheme.LIGHT_GRAY_COLOR);
        this.logTitle.add(this.priceLabel);
        this.add((Component)this.logTitle, "North");
        this.add((Component)this.itemContainer, "Center");
        JPopupMenu popupMenu = new JPopupMenu();
        popupMenu.setBorder(new EmptyBorder(5, 5, 5, 5));
        this.setComponentPopupMenu(popupMenu);
        JMenuItem toggle = new JMenuItem(eventIgnored ? "Include loot" : "Hide loot");
        toggle.addActionListener(e -> onEventToggle.accept(id, !eventIgnored));
        popupMenu.add(toggle);
    }

    private int getTotalKills() {
        return this.kills;
    }

    boolean matches(LootTrackerRecord record) {
        return record.getTitle().equals(this.id) && record.getType() == this.lootRecordType;
    }

    boolean matches(String id, LootRecordType type) {
        if (id == null) {
            return true;
        }
        return this.id.equals(id) && this.lootRecordType == type;
    }

    void addKill(LootTrackerRecord record) {
        if (!this.matches(record)) {
            throw new IllegalArgumentException(record.toString());
        }
        this.kills += record.getKills();
        block0: for (LootTrackerItem item : record.getItems()) {
            int mappedItemId = LootTrackerMapping.map(item.getId(), item.getName());
            for (int idx = 0; idx < this.items.size(); ++idx) {
                LootTrackerItem i = this.items.get(idx);
                if (mappedItemId != i.getId()) continue;
                int qty = i.getQuantity() + item.getQuantity();
                if (qty < 0) {
                    qty = Integer.MAX_VALUE;
                }
                this.items.set(idx, new LootTrackerItem(i.getId(), i.getName(), qty, i.getGePrice(), i.getHaPrice(), i.isIgnored()));
                continue block0;
            }
            LootTrackerItem mappedItem = mappedItemId == item.getId() ? item : new LootTrackerItem(mappedItemId, item.getName(), item.getQuantity(), item.getGePrice(), item.getHaPrice(), item.isIgnored());
            this.items.add(mappedItem);
        }
    }

    void rebuild() {
        this.buildItems();
        String priceTypeString = " ";
        if (this.showPriceType) {
            priceTypeString = this.priceType == LootTrackerPriceType.HIGH_ALCHEMY ? "HA: " : "GE: ";
        }
        this.priceLabel.setText(priceTypeString + QuantityFormatter.quantityToStackSize(this.totalPrice) + " gp");
        this.priceLabel.setToolTipText(QuantityFormatter.formatNumber(this.totalPrice) + " gp");
        long kills = this.getTotalKills();
        if (kills > 1L) {
            this.subTitleLabel.setText("x " + kills);
            this.subTitleLabel.setToolTipText(QuantityFormatter.formatNumber(this.totalPrice / kills) + " gp (average)");
        }
        this.revalidate();
    }

    void collapse() {
        if (!this.isCollapsed()) {
            this.itemContainer.setVisible(false);
            this.applyDimmer(false, this.logTitle);
        }
    }

    void expand() {
        if (this.isCollapsed()) {
            this.itemContainer.setVisible(true);
            this.applyDimmer(true, this.logTitle);
        }
    }

    boolean isCollapsed() {
        return !this.itemContainer.isVisible();
    }

    private void applyDimmer(boolean brighten, JPanel panel) {
        for (Component component : panel.getComponents()) {
            Color color = component.getForeground();
            component.setForeground(brighten ? color.brighter() : color.darker());
        }
    }

    private void buildItems() {
        boolean isHidden;
        this.totalPrice = 0L;
        List<LootTrackerItem> items = this.items;
        if (this.hideIgnoredItems) {
            items = items.stream().filter(item -> !item.isIgnored()).collect(Collectors.toList());
        }
        this.setVisible(!(isHidden = items.isEmpty()));
        if (isHidden) {
            return;
        }
        ToLongFunction<LootTrackerItem> getPrice = this.priceType == LootTrackerPriceType.HIGH_ALCHEMY ? LootTrackerItem::getTotalHaPrice : LootTrackerItem::getTotalGePrice;
        this.totalPrice = items.stream().mapToLong(getPrice).sum();
        items.sort(Comparator.comparingLong(getPrice).reversed());
        int rowSize = (items.size() % 5 == 0 ? 0 : 1) + items.size() / 5;
        this.itemContainer.removeAll();
        this.itemContainer.setLayout(new GridLayout(rowSize, 5, 1, 1));
        EmptyBorder emptyBorder = new EmptyBorder(5, 5, 5, 5);
        for (int i = 0; i < rowSize * 5; ++i) {
            JPanel slotContainer = new JPanel();
            slotContainer.setBackground(ColorScheme.DARKER_GRAY_COLOR);
            if (i < items.size()) {
                LootTrackerItem item2 = items.get(i);
                JLabel imageLabel = new JLabel();
                imageLabel.setToolTipText(LootTrackerBox.buildToolTip(item2));
                imageLabel.setVerticalAlignment(0);
                imageLabel.setHorizontalAlignment(0);
                AsyncBufferedImage itemImage = this.itemManager.getImage(item2.getId(), item2.getQuantity(), item2.getQuantity() > 1);
                if (item2.isIgnored()) {
                    itemImage.onLoaded(() -> {
                        BufferedImage transparentImage = ImageUtil.alphaOffset((Image)itemImage, 0.3f);
                        imageLabel.setIcon(new ImageIcon(transparentImage));
                    });
                } else {
                    itemImage.addTo(imageLabel);
                }
                slotContainer.add(imageLabel);
                JPopupMenu popupMenu = new JPopupMenu();
                popupMenu.setBorder(emptyBorder);
                slotContainer.setComponentPopupMenu(popupMenu);
                JMenuItem toggle = new JMenuItem("Toggle item");
                toggle.addActionListener(e -> {
                    item2.setIgnored(!item2.isIgnored());
                    this.onItemToggle.accept(item2.getName(), item2.isIgnored());
                });
                popupMenu.add(toggle);
            }
            this.itemContainer.add(slotContainer);
        }
        this.itemContainer.revalidate();
    }

    private static String buildToolTip(LootTrackerItem item) {
        String name = item.getName();
        int quantity = item.getQuantity();
        long gePrice = item.getTotalGePrice();
        long haPrice = item.getTotalHaPrice();
        String ignoredLabel = item.isIgnored() ? " - Ignored" : "";
        StringBuilder sb = new StringBuilder("<html>");
        sb.append(name).append(" x ").append(QuantityFormatter.formatNumber(quantity)).append(ignoredLabel);
        if (item.getId() == 995) {
            sb.append("</html>");
            return sb.toString();
        }
        sb.append("<br>GE: ").append(QuantityFormatter.quantityToStackSize(gePrice));
        if (quantity > 1) {
            sb.append(" (").append(QuantityFormatter.quantityToStackSize(item.getGePrice())).append(" ea)");
        }
        if (item.getId() == 13204) {
            sb.append("</html>");
            return sb.toString();
        }
        sb.append("<br>HA: ").append(QuantityFormatter.quantityToStackSize(haPrice));
        if (quantity > 1) {
            sb.append(" (").append(QuantityFormatter.quantityToStackSize(item.getHaPrice())).append(" ea)");
        }
        sb.append("</html>");
        return sb.toString();
    }

    String getId() {
        return this.id;
    }

    LootRecordType getLootRecordType() {
        return this.lootRecordType;
    }

    public List<LootTrackerItem> getItems() {
        return this.items;
    }
}

