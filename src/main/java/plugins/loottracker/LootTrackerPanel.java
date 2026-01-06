/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Iterables
 *  com.google.common.collect.Lists
 *  net.runelite.http.api.loottracker.LootRecordType
 */
package net.runelite.client.plugins.loottracker;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButton;
import javax.swing.JToggleButton;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicButtonUI;
import javax.swing.plaf.basic.BasicToggleButtonUI;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.loottracker.LootTrackerBox;
import net.runelite.client.plugins.loottracker.LootTrackerConfig;
import net.runelite.client.plugins.loottracker.LootTrackerItem;
import net.runelite.client.plugins.loottracker.LootTrackerPlugin;
import net.runelite.client.plugins.loottracker.LootTrackerPriceType;
import net.runelite.client.plugins.loottracker.LootTrackerRecord;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.FontManager;
import net.runelite.client.ui.PluginPanel;
import net.runelite.client.ui.components.PluginErrorPanel;
import net.runelite.client.util.ColorUtil;
import net.runelite.client.util.ImageUtil;
import net.runelite.client.util.QuantityFormatter;
import net.runelite.client.util.SwingUtil;
import net.runelite.http.api.loottracker.LootRecordType;

class LootTrackerPanel
extends PluginPanel {
    private static final int MAX_LOOT_BOXES = 500;
    private static final ImageIcon SINGLE_LOOT_VIEW;
    private static final ImageIcon SINGLE_LOOT_VIEW_FADED;
    private static final ImageIcon SINGLE_LOOT_VIEW_HOVER;
    private static final ImageIcon GROUPED_LOOT_VIEW;
    private static final ImageIcon GROUPED_LOOT_VIEW_FADED;
    private static final ImageIcon GROUPED_LOOT_VIEW_HOVER;
    private static final ImageIcon BACK_ARROW_ICON;
    private static final ImageIcon BACK_ARROW_ICON_HOVER;
    private static final ImageIcon VISIBLE_ICON;
    private static final ImageIcon VISIBLE_ICON_HOVER;
    private static final ImageIcon INVISIBLE_ICON;
    private static final ImageIcon INVISIBLE_ICON_HOVER;
    private static final ImageIcon COLLAPSE_ICON;
    private static final ImageIcon EXPAND_ICON;
    private static final String HTML_LABEL_TEMPLATE = "<html><body style='color:%s'>%s<span style='color:white'>%s</span></body></html>";
    private static final String RESET_ALL_WARNING_TEXT = "<html>This will permanently delete <b>all</b> loot.</html>";
    private static final String RESET_CURRENT_WARNING_TEXT = "This will permanently delete \"%s\" loot.";
    private static final String RESET_ONE_WARNING_TEXT = "This will delete one kill.";
    private final PluginErrorPanel errorPanel = new PluginErrorPanel();
    private final JPanel logsContainer = new JPanel();
    private final JPanel overallPanel;
    private final JLabel overallKillsLabel = new JLabel();
    private final JLabel overallGpLabel = new JLabel();
    private final JLabel overallIcon = new JLabel();
    private final JPanel actionsPanel;
    private final JLabel detailsTitle = new JLabel();
    private final JButton backBtn = new JButton();
    private final JToggleButton viewHiddenBtn = new JToggleButton();
    private final JRadioButton singleLootBtn = new JRadioButton();
    private final JRadioButton groupedLootBtn = new JRadioButton();
    private final JButton collapseBtn = new JButton();
    private final List<LootTrackerRecord> aggregateRecords = new ArrayList<LootTrackerRecord>();
    private final List<LootTrackerRecord> sessionRecords = new ArrayList<LootTrackerRecord>();
    private final List<LootTrackerBox> boxes = new ArrayList<LootTrackerBox>();
    private final ItemManager itemManager;
    private final LootTrackerPlugin plugin;
    private final LootTrackerConfig config;
    private boolean groupLoot;
    private boolean hideIgnoredItems;
    private String currentView;
    private LootRecordType currentType;

    LootTrackerPanel(LootTrackerPlugin plugin, ItemManager itemManager, LootTrackerConfig config) {
        this.itemManager = itemManager;
        this.plugin = plugin;
        this.config = config;
        this.hideIgnoredItems = true;
        this.setBorder(new EmptyBorder(6, 6, 6, 6));
        this.setBackground(ColorScheme.DARK_GRAY_COLOR);
        this.setLayout(new BorderLayout());
        JPanel layoutPanel = new JPanel();
        layoutPanel.setLayout(new BoxLayout(layoutPanel, 1));
        this.add((Component)layoutPanel, "North");
        this.actionsPanel = this.buildActionsPanel();
        this.overallPanel = this.buildOverallPanel();
        this.logsContainer.setLayout(new BoxLayout(this.logsContainer, 1));
        layoutPanel.add(this.actionsPanel);
        layoutPanel.add(this.overallPanel);
        layoutPanel.add(this.logsContainer);
        this.errorPanel.setContent("Loot tracker", "You have not received any loot yet.");
        this.add(this.errorPanel);
    }

    private JPanel buildActionsPanel() {
        JPanel actionsContainer = new JPanel();
        actionsContainer.setLayout(new BorderLayout());
        actionsContainer.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        actionsContainer.setPreferredSize(new Dimension(0, 30));
        actionsContainer.setBorder(new EmptyBorder(5, 5, 5, 10));
        actionsContainer.setVisible(false);
        JPanel viewControls = new JPanel(new GridLayout(1, 3, 10, 0));
        viewControls.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        SwingUtil.removeButtonDecorations(this.collapseBtn);
        this.collapseBtn.setIcon(EXPAND_ICON);
        this.collapseBtn.setSelectedIcon(COLLAPSE_ICON);
        SwingUtil.addModalTooltip(this.collapseBtn, "Expand All", "Collapse All");
        this.collapseBtn.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        this.collapseBtn.setUI(new BasicButtonUI());
        this.collapseBtn.addActionListener(ev -> this.changeCollapse());
        viewControls.add(this.collapseBtn);
        SwingUtil.removeButtonDecorations(this.singleLootBtn);
        this.singleLootBtn.setIcon(SINGLE_LOOT_VIEW_FADED);
        this.singleLootBtn.setRolloverIcon(SINGLE_LOOT_VIEW_HOVER);
        this.singleLootBtn.setSelectedIcon(SINGLE_LOOT_VIEW);
        this.singleLootBtn.setToolTipText("Show each kill separately");
        this.singleLootBtn.addActionListener(e -> this.changeGrouping(false));
        SwingUtil.removeButtonDecorations(this.groupedLootBtn);
        this.groupedLootBtn.setIcon(GROUPED_LOOT_VIEW_FADED);
        this.groupedLootBtn.setRolloverIcon(GROUPED_LOOT_VIEW_HOVER);
        this.groupedLootBtn.setSelectedIcon(GROUPED_LOOT_VIEW);
        this.groupedLootBtn.setToolTipText("Group loot by source");
        this.groupedLootBtn.addActionListener(e -> this.changeGrouping(true));
        ButtonGroup groupSingleGroup = new ButtonGroup();
        groupSingleGroup.add(this.singleLootBtn);
        groupSingleGroup.add(this.groupedLootBtn);
        viewControls.add(this.groupedLootBtn);
        viewControls.add(this.singleLootBtn);
        this.changeGrouping(true);
        SwingUtil.removeButtonDecorations(this.viewHiddenBtn);
        this.viewHiddenBtn.setIconTextGap(0);
        this.viewHiddenBtn.setIcon(VISIBLE_ICON);
        this.viewHiddenBtn.setRolloverIcon(INVISIBLE_ICON_HOVER);
        this.viewHiddenBtn.setSelectedIcon(INVISIBLE_ICON);
        this.viewHiddenBtn.setRolloverSelectedIcon(VISIBLE_ICON_HOVER);
        this.viewHiddenBtn.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        this.viewHiddenBtn.setUI(new BasicToggleButtonUI());
        this.viewHiddenBtn.addActionListener(e -> this.changeItemHiding(this.viewHiddenBtn.isSelected()));
        SwingUtil.addModalTooltip(this.viewHiddenBtn, "Show ignored items", "Hide ignored items");
        this.changeItemHiding(true);
        viewControls.add(this.viewHiddenBtn);
        JPanel leftTitleContainer = new JPanel(new BorderLayout(5, 0));
        leftTitleContainer.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        this.detailsTitle.setForeground(Color.WHITE);
        SwingUtil.removeButtonDecorations(this.backBtn);
        this.backBtn.setIcon(BACK_ARROW_ICON);
        this.backBtn.setRolloverIcon(BACK_ARROW_ICON_HOVER);
        this.backBtn.setVisible(false);
        this.backBtn.addActionListener(ev -> {
            this.currentView = null;
            this.currentType = null;
            this.backBtn.setVisible(false);
            this.detailsTitle.setText("");
            this.rebuild();
        });
        leftTitleContainer.add((Component)this.backBtn, "West");
        leftTitleContainer.add((Component)this.detailsTitle, "Center");
        actionsContainer.add((Component)viewControls, "East");
        actionsContainer.add((Component)leftTitleContainer, "West");
        return actionsContainer;
    }

    private JPanel buildOverallPanel() {
        JPanel overallPanel = new JPanel();
        overallPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createMatteBorder(5, 0, 0, 0, ColorScheme.DARK_GRAY_COLOR), BorderFactory.createEmptyBorder(8, 10, 8, 10)));
        overallPanel.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        overallPanel.setLayout(new BorderLayout());
        overallPanel.setVisible(false);
        JPanel overallInfo = new JPanel();
        overallInfo.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        overallInfo.setLayout(new GridLayout(2, 1));
        overallInfo.setBorder(new EmptyBorder(2, 10, 2, 0));
        this.overallKillsLabel.setFont(FontManager.getRunescapeSmallFont());
        this.overallGpLabel.setFont(FontManager.getRunescapeSmallFont());
        overallInfo.add(this.overallKillsLabel);
        overallInfo.add(this.overallGpLabel);
        overallPanel.add((Component)this.overallIcon, "West");
        overallPanel.add((Component)overallInfo, "Center");
        JMenuItem reset = new JMenuItem("Reset All");
        reset.addActionListener(e -> {
            int result = JOptionPane.showOptionDialog(overallPanel, this.currentView == null ? RESET_ALL_WARNING_TEXT : String.format(RESET_CURRENT_WARNING_TEXT, this.currentView), "Are you sure?", 0, 2, null, new String[]{"Yes", "No"}, "No");
            if (result != 0) {
                return;
            }
            this.sessionRecords.removeIf(r -> r.matches(this.currentView, this.currentType));
            this.aggregateRecords.removeIf(r -> r.matches(this.currentView, this.currentType));
            this.boxes.removeIf(b -> b.matches(this.currentView, this.currentType));
            this.updateOverall();
            this.logsContainer.removeAll();
            this.logsContainer.revalidate();
            if (this.currentView != null) {
                assert (this.currentType != null);
                this.plugin.removeLootConfig(this.currentType, this.currentView);
            } else {
                this.plugin.removeAllLoot();
            }
        });
        JPopupMenu popupMenu = new JPopupMenu();
        popupMenu.setBorder(new EmptyBorder(5, 5, 5, 5));
        popupMenu.add(reset);
        overallPanel.setComponentPopupMenu(popupMenu);
        return overallPanel;
    }

    void updateCollapseText() {
        this.collapseBtn.setSelected(this.isAllCollapsed());
    }

    private boolean isAllCollapsed() {
        return this.boxes.stream().filter(LootTrackerBox::isCollapsed).count() == (long)this.boxes.size();
    }

    void loadHeaderIcon(BufferedImage img) {
        this.overallIcon.setIcon(new ImageIcon(img));
    }

    void add(String eventName, LootRecordType type, int actorLevel, LootTrackerItem[] items, int kills) {
        Object subTitle = type == LootRecordType.PICKPOCKET ? "(pickpocket)" : (actorLevel > 0 ? "(lvl-" + actorLevel + ")" : "");
        LootTrackerRecord record = new LootTrackerRecord(eventName, (String)subTitle, type, items, kills);
        this.sessionRecords.add(record);
        if (this.hideIgnoredItems && this.plugin.isEventIgnored(eventName)) {
            return;
        }
        LootTrackerBox box = this.buildBox(record);
        if (box != null) {
            box.rebuild();
            this.updateOverall();
        }
    }

    void clearRecords() {
        this.aggregateRecords.clear();
        this.sessionRecords.clear();
    }

    void addRecords(Collection<LootTrackerRecord> recs) {
        this.aggregateRecords.addAll(recs);
        this.rebuild();
    }

    private void changeGrouping(boolean group) {
        this.groupLoot = group;
        (group ? this.groupedLootBtn : this.singleLootBtn).setSelected(true);
        this.rebuild();
    }

    private void changeItemHiding(boolean hide) {
        this.hideIgnoredItems = hide;
        this.viewHiddenBtn.setSelected(hide);
        this.rebuild();
    }

    private void changeCollapse() {
        boolean isAllCollapsed = this.isAllCollapsed();
        for (LootTrackerBox box : this.boxes) {
            if (isAllCollapsed) {
                box.expand();
                continue;
            }
            if (box.isCollapsed()) continue;
            box.collapse();
        }
        this.updateCollapseText();
    }

    void updateIgnoredRecords() {
        for (LootTrackerRecord record : Iterables.concat(this.aggregateRecords, this.sessionRecords)) {
            for (LootTrackerItem item : record.getItems()) {
                item.setIgnored(this.plugin.isIgnored(item.getName()));
            }
        }
        this.rebuild();
    }

    void updatePriceTypeDisplay() {
        this.rebuild();
    }

    private void rebuild() {
        SwingUtil.fastRemoveAll(this.logsContainer);
        this.boxes.clear();
        if (this.groupLoot) {
            this.aggregateRecords.forEach(this::buildBox);
            this.sessionRecords.forEach(this::buildBox);
        } else {
            Lists.reverse(this.sessionRecords).stream().filter(r -> !this.hideIgnoredItems || !this.plugin.isEventIgnored(r.getTitle())).limit(500L).collect(Collectors.toCollection(ArrayDeque::new)).descendingIterator().forEachRemaining(this::buildBox);
        }
        this.boxes.forEach(LootTrackerBox::rebuild);
        this.updateOverall();
        this.logsContainer.revalidate();
    }

    private LootTrackerBox buildBox(LootTrackerRecord record) {
        if (!record.matches(this.currentView, this.currentType)) {
            return null;
        }
        boolean isIgnored = this.plugin.isEventIgnored(record.getTitle());
        if (this.hideIgnoredItems && isIgnored) {
            return null;
        }
        if (this.groupLoot) {
            for (LootTrackerBox box : this.boxes) {
                if (!box.matches(record)) continue;
                this.logsContainer.setComponentZOrder(box, 0);
                box.addKill(record);
                return box;
            }
        }
        this.remove(this.errorPanel);
        this.actionsPanel.setVisible(true);
        this.overallPanel.setVisible(true);
        final LootTrackerBox box = new LootTrackerBox(this.itemManager, record.getTitle(), record.getType(), record.getSubTitle(), this.hideIgnoredItems, this.config.priceType(), this.config.showPriceType(), this.plugin::toggleItem, this.plugin::toggleEvent, isIgnored);
        box.addKill(record);
        JPopupMenu popupMenu = box.getComponentPopupMenu();
        if (popupMenu == null) {
            popupMenu = new JPopupMenu();
            popupMenu.setBorder(new EmptyBorder(5, 5, 5, 5));
            box.setComponentPopupMenu(popupMenu);
        }
        box.addMouseListener(new MouseAdapter(){

            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() == 1) {
                    if (box.isCollapsed()) {
                        box.expand();
                    } else {
                        box.collapse();
                    }
                    LootTrackerPanel.this.updateCollapseText();
                }
            }
        });
        JMenuItem reset = new JMenuItem("Reset");
        reset.addActionListener(e -> {
            int result = JOptionPane.showOptionDialog(box, this.groupLoot ? String.format(RESET_CURRENT_WARNING_TEXT, box.getId()) : RESET_ONE_WARNING_TEXT, "Are you sure?", 0, 2, null, new String[]{"Yes", "No"}, "No");
            if (result != 0) {
                return;
            }
            Predicate<LootTrackerRecord> match = this.groupLoot ? r -> r.matches(record.getTitle(), record.getType()) : r -> r.equals(record);
            this.sessionRecords.removeIf(match);
            this.aggregateRecords.removeIf(match);
            this.boxes.remove(box);
            this.updateOverall();
            this.logsContainer.remove(box);
            this.logsContainer.revalidate();
            if (this.groupLoot) {
                this.plugin.removeLootConfig(box.getLootRecordType(), box.getId());
            }
        });
        popupMenu.add(reset);
        JMenuItem details = new JMenuItem("View details");
        details.addActionListener(e -> {
            this.currentView = record.getTitle();
            this.currentType = record.getType();
            this.detailsTitle.setText(this.currentView);
            this.backBtn.setVisible(true);
            this.rebuild();
        });
        popupMenu.add(details);
        this.boxes.add(box);
        this.logsContainer.add((Component)box, 0);
        if (!this.groupLoot && this.boxes.size() > 500) {
            this.logsContainer.remove(this.boxes.remove(0));
        }
        return box;
    }

    private void updateOverall() {
        long overallKills = 0L;
        long overallGe = 0L;
        long overallHa = 0L;
        Iterable<LootTrackerRecord> records = this.sessionRecords;
        if (this.groupLoot) {
            records = Iterables.concat(this.aggregateRecords, this.sessionRecords);
        }
        for (LootTrackerRecord record : records) {
            if (!record.matches(this.currentView, this.currentType) || this.hideIgnoredItems && this.plugin.isEventIgnored(record.getTitle())) continue;
            int present = record.getItems().length;
            for (LootTrackerItem item : record.getItems()) {
                if (this.hideIgnoredItems && item.isIgnored()) {
                    --present;
                    continue;
                }
                overallGe += item.getTotalGePrice();
                overallHa += item.getTotalHaPrice();
            }
            if (present <= 0) continue;
            overallKills += (long)record.getKills();
        }
        String priceType = "";
        if (this.config.showPriceType()) {
            priceType = this.config.priceType() == LootTrackerPriceType.HIGH_ALCHEMY ? "HA " : "GE ";
        }
        this.overallKillsLabel.setText(LootTrackerPanel.htmlLabel("Total count: ", overallKills));
        this.overallGpLabel.setText(LootTrackerPanel.htmlLabel("Total " + priceType + "value: ", this.config.priceType() == LootTrackerPriceType.HIGH_ALCHEMY ? overallHa : overallGe));
        this.overallGpLabel.setToolTipText("<html>Total GE price: " + QuantityFormatter.formatNumber(overallGe) + "<br>Total HA price: " + QuantityFormatter.formatNumber(overallHa) + "</html>");
        this.updateCollapseText();
    }

    private static String htmlLabel(String key, long value) {
        String valueStr = QuantityFormatter.quantityToStackSize(value);
        return String.format(HTML_LABEL_TEMPLATE, ColorUtil.toHexColor(ColorScheme.LIGHT_GRAY_COLOR), key, valueStr);
    }

    static {
        BufferedImage singleLootImg = ImageUtil.loadImageResource(LootTrackerPlugin.class, "single_loot_icon.png");
        BufferedImage groupedLootImg = ImageUtil.loadImageResource(LootTrackerPlugin.class, "grouped_loot_icon.png");
        BufferedImage backArrowImg = ImageUtil.loadImageResource(LootTrackerPlugin.class, "back_icon.png");
        BufferedImage visibleImg = ImageUtil.loadImageResource(LootTrackerPlugin.class, "visible_icon.png");
        BufferedImage invisibleImg = ImageUtil.loadImageResource(LootTrackerPlugin.class, "invisible_icon.png");
        BufferedImage collapseImg = ImageUtil.loadImageResource(LootTrackerPlugin.class, "collapsed.png");
        BufferedImage expandedImg = ImageUtil.loadImageResource(LootTrackerPlugin.class, "expanded.png");
        SINGLE_LOOT_VIEW = new ImageIcon(singleLootImg);
        SINGLE_LOOT_VIEW_FADED = new ImageIcon(ImageUtil.alphaOffset((Image)singleLootImg, -180));
        SINGLE_LOOT_VIEW_HOVER = new ImageIcon(ImageUtil.alphaOffset((Image)singleLootImg, -220));
        GROUPED_LOOT_VIEW = new ImageIcon(groupedLootImg);
        GROUPED_LOOT_VIEW_FADED = new ImageIcon(ImageUtil.alphaOffset((Image)groupedLootImg, -180));
        GROUPED_LOOT_VIEW_HOVER = new ImageIcon(ImageUtil.alphaOffset((Image)groupedLootImg, -220));
        BACK_ARROW_ICON = new ImageIcon(backArrowImg);
        BACK_ARROW_ICON_HOVER = new ImageIcon(ImageUtil.alphaOffset((Image)backArrowImg, -180));
        VISIBLE_ICON = new ImageIcon(visibleImg);
        VISIBLE_ICON_HOVER = new ImageIcon(ImageUtil.alphaOffset((Image)visibleImg, -220));
        INVISIBLE_ICON = new ImageIcon(invisibleImg);
        INVISIBLE_ICON_HOVER = new ImageIcon(ImageUtil.alphaOffset((Image)invisibleImg, -220));
        COLLAPSE_ICON = new ImageIcon(collapseImg);
        EXPAND_ICON = new ImageIcon(expandedImg);
    }
}

