/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.MoreObjects
 *  com.google.common.cache.CacheBuilder
 *  com.google.common.cache.CacheLoader
 *  com.google.common.cache.LoadingCache
 *  com.google.common.collect.HashBasedTable
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableList$Builder
 *  com.google.common.collect.Table
 *  com.google.inject.Provides
 *  javax.inject.Inject
 *  net.runelite.api.Client
 *  net.runelite.api.GameState
 *  net.runelite.api.ItemComposition
 *  net.runelite.api.Menu
 *  net.runelite.api.MenuAction
 *  net.runelite.api.MenuEntry
 *  net.runelite.api.Tile
 *  net.runelite.api.TileItem
 *  net.runelite.api.coords.WorldPoint
 *  net.runelite.api.events.ClientTick
 *  net.runelite.api.events.FocusChanged
 *  net.runelite.api.events.GameStateChanged
 *  net.runelite.api.events.ItemDespawned
 *  net.runelite.api.events.ItemQuantityChanged
 *  net.runelite.api.events.ItemSpawned
 *  net.runelite.api.events.MenuEntryAdded
 */
package net.runelite.client.plugins.grounditems;

import com.google.common.base.MoreObjects;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Table;
import com.google.inject.Provides;
import java.awt.Color;
import java.awt.Rectangle;
import java.lang.reflect.Type;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.inject.Inject;
import javax.swing.SwingUtilities;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.ItemComposition;
import net.runelite.api.Menu;
import net.runelite.api.MenuAction;
import net.runelite.api.MenuEntry;
import net.runelite.api.Tile;
import net.runelite.api.TileItem;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.ClientTick;
import net.runelite.api.events.FocusChanged;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.ItemDespawned;
import net.runelite.api.events.ItemQuantityChanged;
import net.runelite.api.events.ItemSpawned;
import net.runelite.api.events.MenuEntryAdded;
import net.runelite.client.Notifier;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.events.ProfileChanged;
import net.runelite.client.game.ItemManager;
import net.runelite.client.input.KeyManager;
import net.runelite.client.input.MouseManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.grounditems.GroundItem;
import net.runelite.client.plugins.grounditems.GroundItemHotkeyListener;
import net.runelite.client.plugins.grounditems.GroundItemMouseAdapter;
import net.runelite.client.plugins.grounditems.GroundItemsConfig;
import net.runelite.client.plugins.grounditems.GroundItemsOverlay;
import net.runelite.client.plugins.grounditems.Lootbeam;
import net.runelite.client.plugins.grounditems.MenuEntryWithCount;
import net.runelite.client.plugins.grounditems.NamedQuantity;
import net.runelite.client.plugins.grounditems.WildcardMatchLoader;
import net.runelite.client.plugins.grounditems.config.HighlightTier;
import net.runelite.client.plugins.grounditems.config.ItemHighlightMode;
import net.runelite.client.plugins.grounditems.config.MenuHighlightMode;
import net.runelite.client.plugins.grounditems.config.OwnershipFilterMode;
import net.runelite.client.ui.components.colorpicker.ColorPickerManager;
import net.runelite.client.ui.components.colorpicker.RuneliteColorPicker;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.util.ColorUtil;
import net.runelite.client.util.QuantityFormatter;
import net.runelite.client.util.RSTimeUnit;
import net.runelite.client.util.Text;

@PluginDescriptor(name="Ground Items", description="Highlight ground items and/or show price information", tags={"grand", "exchange", "high", "alchemy", "prices", "highlight", "overlay", "lootbeam"})
public class GroundItemsPlugin
extends Plugin {
    private static final String HIGHLIGHT_COLOR_PREFIX = "highlight_";
    private static final int COINS = 995;
    private Map.Entry<Rectangle, GroundItem> textBoxBounds;
    private Map.Entry<Rectangle, GroundItem> hiddenBoxBounds;
    private Map.Entry<Rectangle, GroundItem> highlightBoxBounds;
    private boolean hotKeyPressed;
    private boolean hideAll;
    private List<String> hiddenItemList = new CopyOnWriteArrayList<String>();
    private List<String> highlightedItemsList = new CopyOnWriteArrayList<String>();
    @Inject
    private GroundItemHotkeyListener hotkeyListener;
    @Inject
    private GroundItemMouseAdapter mouseAdapter;
    @Inject
    private MouseManager mouseManager;
    @Inject
    private KeyManager keyManager;
    @Inject
    private Client client;
    @Inject
    private ClientThread clientThread;
    @Inject
    private ItemManager itemManager;
    @Inject
    private OverlayManager overlayManager;
    @Inject
    private GroundItemsConfig config;
    @Inject
    private GroundItemsOverlay overlay;
    @Inject
    private Notifier notifier;
    @Inject
    private ScheduledExecutorService executor;
    @Inject
    private ConfigManager configManager;
    @Inject
    private ColorPickerManager colorPickerManager;
    private final Table<WorldPoint, Integer, GroundItem> collectedGroundItems = HashBasedTable.create();
    private List<PriceHighlight> priceChecks = ImmutableList.of();
    private LoadingCache<NamedQuantity, Boolean> highlightedItems;
    private LoadingCache<NamedQuantity, Boolean> hiddenItems;
    private final Map<WorldPoint, Lootbeam> lootbeams = new HashMap<WorldPoint, Lootbeam>();

    @Provides
    GroundItemsConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(GroundItemsConfig.class);
    }

    @Override
    protected void startUp() {
        this.overlayManager.add(this.overlay);
        this.mouseManager.registerMouseListener(this.mouseAdapter);
        this.keyManager.registerKeyListener(this.hotkeyListener);
        this.migrate();
        this.executor.execute(this::reset);
    }

    @Override
    protected void shutDown() {
        this.overlayManager.remove(this.overlay);
        this.mouseManager.unregisterMouseListener(this.mouseAdapter);
        this.keyManager.unregisterKeyListener(this.hotkeyListener);
        this.highlightedItems.invalidateAll();
        this.highlightedItems = null;
        this.hiddenItems.invalidateAll();
        this.hiddenItems = null;
        this.hiddenItemList = null;
        this.highlightedItemsList = null;
        this.collectedGroundItems.clear();
        this.clientThread.invokeLater(this::removeAllLootbeams);
    }

    @Subscribe
    public void onConfigChanged(ConfigChanged event) {
        if (event.getGroup().equals("grounditems")) {
            this.executor.execute(this::reset);
        }
    }

    @Subscribe
    public void onProfileChanged(ProfileChanged profileChanged) {
        this.migrate();
    }

    private void migrate() {
        Boolean onlyShowOwnItems = (Boolean)this.configManager.getConfiguration("grounditems", "onlyShowLoot", (Type)((Object)Boolean.class));
        if (onlyShowOwnItems != null) {
            if (onlyShowOwnItems.booleanValue()) {
                this.configManager.setConfiguration("grounditems", "ownershipFilterMode", OwnershipFilterMode.DROPS);
            }
            this.configManager.unsetConfiguration("grounditems", "onlyShowLoot");
        }
    }

    @Subscribe
    public void onGameStateChanged(GameStateChanged event) {
        if (event.getGameState() == GameState.LOADING) {
            this.collectedGroundItems.clear();
            this.lootbeams.clear();
        }
    }

    @Subscribe
    public void onItemSpawned(ItemSpawned itemSpawned) {
        TileItem item = itemSpawned.getItem();
        Tile tile = itemSpawned.getTile();
        GroundItem groundItem = this.buildGroundItem(tile, item);
        GroundItem existing = (GroundItem)this.collectedGroundItems.get((Object)tile.getWorldLocation(), (Object)item.getId());
        if (existing != null) {
            existing.setQuantity(existing.getQuantity() + groundItem.getQuantity());
            existing.reset();
        } else {
            this.collectedGroundItems.put((Object)tile.getWorldLocation(), (Object)item.getId(), (Object)groundItem);
        }
        if (this.shouldDisplayItem(this.config.ownershipFilterMode(), groundItem.getOwnership(), this.client.getVarbitValue(1777))) {
            this.notifyHighlightedItem(groundItem);
        }
        this.handleLootbeam(tile.getWorldLocation());
    }

    void updateItemColor(GroundItem item) {
        if (item.color != null) {
            return;
        }
        Color highlighted = this.getHighlighted(item);
        Color hidden = this.getHidden(item);
        item.highlighted = highlighted != null;
        item.hidden = hidden != null;
        item.color = this.getItemColor(highlighted, hidden);
    }

    @Subscribe
    public void onItemDespawned(ItemDespawned itemDespawned) {
        TileItem item = itemDespawned.getItem();
        Tile tile = itemDespawned.getTile();
        GroundItem groundItem = (GroundItem)this.collectedGroundItems.get((Object)tile.getWorldLocation(), (Object)item.getId());
        if (groundItem == null) {
            return;
        }
        if (groundItem.getQuantity() <= item.getQuantity()) {
            this.collectedGroundItems.remove((Object)tile.getWorldLocation(), (Object)item.getId());
        } else {
            groundItem.setQuantity(groundItem.getQuantity() - item.getQuantity());
            groundItem.setSpawnTime(null);
            groundItem.reset();
        }
        this.handleLootbeam(tile.getWorldLocation());
    }

    @Subscribe
    public void onItemQuantityChanged(ItemQuantityChanged itemQuantityChanged) {
        TileItem item = itemQuantityChanged.getItem();
        Tile tile = itemQuantityChanged.getTile();
        int oldQuantity = itemQuantityChanged.getOldQuantity();
        int newQuantity = itemQuantityChanged.getNewQuantity();
        int diff = newQuantity - oldQuantity;
        GroundItem groundItem = (GroundItem)this.collectedGroundItems.get((Object)tile.getWorldLocation(), (Object)item.getId());
        if (groundItem != null) {
            groundItem.setQuantity(groundItem.getQuantity() + diff);
            groundItem.reset();
        }
        this.handleLootbeam(tile.getWorldLocation());
    }

    @Subscribe
    public void onClientTick(ClientTick event) {
        if (!this.config.collapseEntries()) {
            return;
        }
        MenuEntry[] menuEntries = this.client.getMenuEntries();
        ArrayList<MenuEntryWithCount> newEntries = new ArrayList<MenuEntryWithCount>(menuEntries.length);
        block0: for (int i = menuEntries.length - 1; i >= 0; --i) {
            MenuEntry menuEntry = menuEntries[i];
            MenuAction menuType = menuEntry.getType();
            if (menuType == MenuAction.GROUND_ITEM_FIRST_OPTION || menuType == MenuAction.GROUND_ITEM_SECOND_OPTION || menuType == MenuAction.GROUND_ITEM_THIRD_OPTION || menuType == MenuAction.GROUND_ITEM_FOURTH_OPTION || menuType == MenuAction.GROUND_ITEM_FIFTH_OPTION || menuType == MenuAction.EXAMINE_ITEM_GROUND) {
                for (MenuEntryWithCount entryWCount : newEntries) {
                    if (!entryWCount.getEntry().equals(menuEntry)) continue;
                    entryWCount.increment();
                    continue block0;
                }
            }
            newEntries.add(new MenuEntryWithCount(menuEntry));
        }
        Collections.reverse(newEntries);
        this.client.setMenuEntries((MenuEntry[])newEntries.stream().map(e -> {
            MenuEntry entry = e.getEntry();
            int count = e.getCount();
            if (count > 1) {
                entry.setTarget(entry.getTarget() + " x " + count);
            }
            return entry;
        }).toArray(MenuEntry[]::new));
    }

    private GroundItem buildGroundItem(Tile tile, TileItem item) {
        int itemId = item.getId();
        ItemComposition itemComposition = this.itemManager.getItemComposition(itemId);
        int realItemId = itemComposition.getNote() != -1 ? itemComposition.getLinkedNoteId() : itemId;
        int alchPrice = itemComposition.getHaPrice();
        int despawnTime = item.getDespawnTime() - this.client.getTickCount();
        int visibleTime = item.getVisibleTime() - this.client.getTickCount();
        GroundItem groundItem = GroundItem.builder().id(itemId).location(tile.getWorldLocation()).itemId(realItemId).quantity(item.getQuantity()).name(itemComposition.getName()).haPrice(alchPrice).height(tile.getItemLayer().getHeight()).tradeable(itemComposition.isTradeable()).ownership(item.getOwnership()).isPrivate(item.isPrivate()).spawnTime(Instant.now()).stackable(itemComposition.isStackable()).despawnTime(Duration.of(despawnTime, RSTimeUnit.GAME_TICKS)).visibleTime(Duration.of(visibleTime, RSTimeUnit.GAME_TICKS)).build();
        if (realItemId == 995) {
            groundItem.setHaPrice(1);
            groundItem.setGePrice(1);
        } else {
            groundItem.setGePrice(this.itemManager.getItemPrice(realItemId));
        }
        return groundItem;
    }

    private void reset() {
        this.hiddenItemList = Text.fromCSV(this.config.getHiddenItems());
        this.highlightedItemsList = Text.fromCSV(this.config.getHighlightItems());
        this.highlightedItems = CacheBuilder.newBuilder().maximumSize(512L).expireAfterAccess(10L, TimeUnit.MINUTES).build((CacheLoader)new WildcardMatchLoader(this.highlightedItemsList));
        this.hiddenItems = CacheBuilder.newBuilder().maximumSize(512L).expireAfterAccess(10L, TimeUnit.MINUTES).build((CacheLoader)new WildcardMatchLoader(this.hiddenItemList));
        ImmutableList.Builder priceCheckBuilder = ImmutableList.builder();
        if (this.config.insaneValuePrice() > 0) {
            priceCheckBuilder.add((Object)new PriceHighlight(this.config.insaneValuePrice(), this.config.insaneValueColor()));
        }
        if (this.config.highValuePrice() > 0) {
            priceCheckBuilder.add((Object)new PriceHighlight(this.config.highValuePrice(), this.config.highValueColor()));
        }
        if (this.config.mediumValuePrice() > 0) {
            priceCheckBuilder.add((Object)new PriceHighlight(this.config.mediumValuePrice(), this.config.mediumValueColor()));
        }
        if (this.config.lowValuePrice() > 0) {
            priceCheckBuilder.add((Object)new PriceHighlight(this.config.lowValuePrice(), this.config.lowValueColor()));
        }
        this.priceChecks = priceCheckBuilder.build();
        this.clientThread.invokeLater(() -> this.collectedGroundItems.values().forEach(GroundItem::reset));
        this.clientThread.invokeLater(this::handleLootbeams);
    }

    @Subscribe
    public void onMenuEntryAdded(MenuEntryAdded event) {
        MenuAction type = MenuAction.of((int)event.getType());
        boolean hotKeyPressed = this.client.isKeyPressed(81);
        if (type == MenuAction.GROUND_ITEM_FIRST_OPTION || type == MenuAction.GROUND_ITEM_SECOND_OPTION || type == MenuAction.GROUND_ITEM_THIRD_OPTION || type == MenuAction.GROUND_ITEM_FOURTH_OPTION || type == MenuAction.GROUND_ITEM_FIFTH_OPTION || type == MenuAction.WIDGET_TARGET_ON_GROUND_ITEM) {
            boolean canBeRecolored;
            int itemId = event.getIdentifier();
            int sceneX = event.getActionParam0();
            int sceneY = event.getActionParam1();
            MenuEntry[] menuEntries = this.client.getMenuEntries();
            MenuEntry lastEntry = menuEntries[menuEntries.length - 1];
            WorldPoint worldPoint = WorldPoint.fromScene((Client)this.client, (int)sceneX, (int)sceneY, (int)this.client.getPlane());
            GroundItem groundItem = (GroundItem)this.collectedGroundItems.get((Object)worldPoint, (Object)itemId);
            this.updateItemColor(groundItem);
            int quantity = groundItem.getQuantity();
            boolean bl = canBeRecolored = groundItem.highlighted || groundItem.hidden && this.config.recolorMenuHiddenItems();
            if ((this.config.itemHighlightMode() == ItemHighlightMode.MENU || this.config.itemHighlightMode() == ItemHighlightMode.BOTH) && canBeRecolored && !groundItem.color.equals(this.config.defaultColor())) {
                MenuHighlightMode mode = this.config.menuHighlightMode();
                if (mode == MenuHighlightMode.BOTH || mode == MenuHighlightMode.OPTION) {
                    lastEntry.setOption(ColorUtil.prependColorTag(lastEntry.getOption(), groundItem.color));
                }
                if (mode == MenuHighlightMode.BOTH || mode == MenuHighlightMode.NAME) {
                    String target = lastEntry.getTarget();
                    int i = target.lastIndexOf("<col=ff9040>");
                    lastEntry.setTarget(target.substring(0, i) + ColorUtil.colorTag(groundItem.color) + Text.removeTags(target.substring(i + 12)));
                }
            }
            if (this.config.showMenuItemQuantities() && groundItem.isStackable() && quantity > 1) {
                lastEntry.setTarget(lastEntry.getTarget() + " (" + quantity + ")");
            }
            if (groundItem.hidden && !groundItem.highlighted && this.config.deprioritizeHiddenItems()) {
                lastEntry.setDeprioritized(true);
            }
        } else if (hotKeyPressed && type == MenuAction.EXAMINE_ITEM_GROUND) {
            MenuEntry parent = this.client.createMenuEntry(-1).setOption("Color").setTarget(event.getTarget()).setType(MenuAction.RUNELITE);
            Menu submenu = parent.createSubMenu();
            int itemId = event.getIdentifier();
            Color color = this.getItemColor(itemId);
            if (color != null) {
                submenu.createMenuEntry(-1).setOption("Reset").setType(MenuAction.RUNELITE).onClick(e -> this.unsetItemColor(itemId));
            }
            submenu.createMenuEntry(-1).setOption("Pick").setType(MenuAction.RUNELITE).onClick(e -> SwingUtilities.invokeLater(() -> {
                RuneliteColorPicker colorPicker = this.colorPickerManager.create(this.client, color != null ? color : Color.decode("#FFFFFF"), "Item color", true);
                colorPicker.setOnClose(c -> this.setItemColor(itemId, (Color)c));
                colorPicker.setVisible(true);
            }));
            List colors = Stream.concat(this.collectedGroundItems.values().stream().map(GroundItem::getColor).filter(Objects::nonNull), Stream.of(Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW, Color.MAGENTA)).distinct().limit(5L).collect(Collectors.toList());
            colors.stream().filter(c -> !c.equals(color)).forEach(c -> submenu.createMenuEntry(-1).setOption(ColorUtil.prependColorTag("Color", c)).setType(MenuAction.RUNELITE).onClick(e -> this.setItemColor(itemId, (Color)c)));
        }
    }

    void updateList(String item, boolean hiddenList) {
        ArrayList<String> hiddenItemSet = new ArrayList<String>(this.hiddenItemList);
        ArrayList<String> highlightedItemSet = new ArrayList<String>(this.highlightedItemsList);
        if (hiddenList) {
            highlightedItemSet.removeIf(item::equalsIgnoreCase);
        } else {
            hiddenItemSet.removeIf(item::equalsIgnoreCase);
        }
        ArrayList<String> items = hiddenList ? hiddenItemSet : highlightedItemSet;
        if (!items.removeIf(item::equalsIgnoreCase)) {
            items.add(item);
        }
        this.config.setHiddenItems(Text.toCSV(hiddenItemSet));
        this.config.setHighlightedItem(Text.toCSV(highlightedItemSet));
    }

    private Color getHighlighted(GroundItem groundItem) {
        Color itemColor = this.getItemColor(groundItem.getItemId());
        NamedQuantity item = new NamedQuantity(groundItem);
        if (Boolean.TRUE.equals(this.highlightedItems.getUnchecked((Object)item))) {
            return itemColor != null ? itemColor : this.config.highlightedColor();
        }
        if (Boolean.TRUE.equals(this.hiddenItems.getUnchecked((Object)item))) {
            return null;
        }
        if (itemColor != null) {
            return itemColor;
        }
        int price = this.getValueByMode(groundItem.getGePrice(), groundItem.getHaPrice());
        for (PriceHighlight highlight : this.priceChecks) {
            if (price <= highlight.getPrice()) continue;
            return highlight.getColor();
        }
        return null;
    }

    private Color getHidden(GroundItem groundItem) {
        NamedQuantity item = new NamedQuantity(groundItem);
        boolean isExplicitHidden = Boolean.TRUE.equals(this.hiddenItems.getUnchecked((Object)item));
        boolean isExplicitHighlight = Boolean.TRUE.equals(this.highlightedItems.getUnchecked((Object)item));
        boolean canBeHidden = groundItem.getGePrice() > 0 || groundItem.isTradeable() || !this.config.dontHideUntradeables();
        boolean underGe = groundItem.getGePrice() < this.config.getHideUnderValue();
        boolean underHa = groundItem.getHaPrice() < this.config.getHideUnderValue();
        return isExplicitHidden || !isExplicitHighlight && canBeHidden && underGe && underHa ? this.config.hiddenColor() : null;
    }

    private Color getItemColor(Color highlighted, Color hidden) {
        if (highlighted != null) {
            return highlighted;
        }
        if (hidden != null) {
            return hidden;
        }
        return this.config.defaultColor();
    }

    @Subscribe
    public void onFocusChanged(FocusChanged focusChanged) {
        if (!focusChanged.isFocused()) {
            this.setHotKeyPressed(false);
        }
    }

    private void notifyHighlightedItem(GroundItem item) {
        String dropType;
        boolean shouldNotifyTier;
        boolean shouldNotifyHighlighted = this.config.notifyHighlightedDrops() && Boolean.TRUE.equals(this.highlightedItems.getUnchecked((Object)new NamedQuantity(item)));
        boolean bl = shouldNotifyTier = this.config.notifyTier() != HighlightTier.OFF && this.getValueByMode(item.getGePrice(), item.getHaPrice()) > this.config.notifyTier().getValueFromTier(this.config) && Boolean.FALSE.equals(this.hiddenItems.getUnchecked((Object)new NamedQuantity(item)));
        if (shouldNotifyHighlighted) {
            dropType = "highlighted";
        } else if (shouldNotifyTier) {
            dropType = "valuable";
        } else {
            return;
        }
        StringBuilder notificationStringBuilder = new StringBuilder().append("You received a ").append(dropType).append(" drop: ").append(item.getName());
        if (item.getQuantity() > 1) {
            notificationStringBuilder.append(" (").append(QuantityFormatter.quantityToStackSize(item.getQuantity())).append(')');
        }
        this.notifier.notify(notificationStringBuilder.toString());
    }

    private int getValueByMode(int gePrice, int haPrice) {
        switch (this.config.valueCalculationMode()) {
            case GE: {
                return gePrice;
            }
            case HA: {
                return haPrice;
            }
        }
        return Math.max(gePrice, haPrice);
    }

    private void handleLootbeam(WorldPoint worldPoint) {
        if (!this.config.showLootbeamForHighlighted() && this.config.showLootbeamTier() == HighlightTier.OFF) {
            this.removeLootbeam(worldPoint);
            return;
        }
        int highestPrice = -1;
        GroundItem highestItem = null;
        Collection groundItems = this.collectedGroundItems.row((Object)worldPoint).values();
        OwnershipFilterMode ownershipFilterMode = this.config.ownershipFilterMode();
        int accountType = this.client.getVarbitValue(1777);
        for (GroundItem groundItem : groundItems) {
            int itemPrice;
            if (!this.shouldDisplayItem(ownershipFilterMode, groundItem.getOwnership(), accountType)) continue;
            NamedQuantity item = new NamedQuantity(groundItem);
            if (this.config.showLootbeamForHighlighted() && Boolean.TRUE.equals(this.highlightedItems.getUnchecked((Object)item))) {
                this.addLootbeam(worldPoint, (Color)MoreObjects.firstNonNull((Object)this.getItemColor(groundItem.getItemId()), (Object)this.config.highlightedColor()));
                return;
            }
            if (Boolean.TRUE.equals(this.hiddenItems.getUnchecked((Object)item)) || (itemPrice = this.getValueByMode(groundItem.getGePrice(), groundItem.getHaPrice())) <= highestPrice) continue;
            highestPrice = itemPrice;
            highestItem = groundItem;
        }
        if (highestItem != null && this.config.showLootbeamTier() != HighlightTier.OFF) {
            for (PriceHighlight highlight : this.priceChecks) {
                if (highestPrice <= highlight.getPrice() || highestPrice <= this.config.showLootbeamTier().getValueFromTier(this.config)) continue;
                this.addLootbeam(worldPoint, (Color)MoreObjects.firstNonNull((Object)this.getItemColor(highestItem.getItemId()), (Object)highlight.color));
                return;
            }
        }
        this.removeLootbeam(worldPoint);
    }

    private void handleLootbeams() {
        for (WorldPoint worldPoint : this.collectedGroundItems.rowKeySet()) {
            this.handleLootbeam(worldPoint);
        }
    }

    private void removeAllLootbeams() {
        for (Lootbeam lootbeam : this.lootbeams.values()) {
            lootbeam.remove();
        }
        this.lootbeams.clear();
    }

    private void addLootbeam(WorldPoint worldPoint, Color color) {
        Lootbeam lootbeam = this.lootbeams.get(worldPoint);
        if (lootbeam == null) {
            lootbeam = new Lootbeam(this.client, this.clientThread, worldPoint, color, this.config.lootbeamStyle());
            this.lootbeams.put(worldPoint, lootbeam);
        } else {
            lootbeam.setColor(color);
            lootbeam.setStyle(this.config.lootbeamStyle());
        }
    }

    private void removeLootbeam(WorldPoint worldPoint) {
        Lootbeam lootbeam = this.lootbeams.remove(worldPoint);
        if (lootbeam != null) {
            lootbeam.remove();
        }
    }

    Color getItemColor(int itemId) {
        return (Color)this.configManager.getConfiguration("grounditems", HIGHLIGHT_COLOR_PREFIX + itemId, (Type)((Object)Color.class));
    }

    void setItemColor(int itemId, Color color) {
        this.configManager.setConfiguration("grounditems", HIGHLIGHT_COLOR_PREFIX + itemId, color);
    }

    void unsetItemColor(int itemId) {
        this.configManager.unsetConfiguration("grounditems", HIGHLIGHT_COLOR_PREFIX + itemId);
    }

    boolean shouldDisplayItem(OwnershipFilterMode filterMode, int ownership, int accountType) {
        switch (filterMode) {
            case DROPS: {
                return ownership == 1 || ownership == 3;
            }
            case TAKEABLE: {
                return ownership != 2 || accountType == 0;
            }
        }
        return true;
    }

    Map.Entry<Rectangle, GroundItem> getTextBoxBounds() {
        return this.textBoxBounds;
    }

    void setTextBoxBounds(Map.Entry<Rectangle, GroundItem> textBoxBounds) {
        this.textBoxBounds = textBoxBounds;
    }

    Map.Entry<Rectangle, GroundItem> getHiddenBoxBounds() {
        return this.hiddenBoxBounds;
    }

    void setHiddenBoxBounds(Map.Entry<Rectangle, GroundItem> hiddenBoxBounds) {
        this.hiddenBoxBounds = hiddenBoxBounds;
    }

    Map.Entry<Rectangle, GroundItem> getHighlightBoxBounds() {
        return this.highlightBoxBounds;
    }

    void setHighlightBoxBounds(Map.Entry<Rectangle, GroundItem> highlightBoxBounds) {
        this.highlightBoxBounds = highlightBoxBounds;
    }

    boolean isHotKeyPressed() {
        return this.hotKeyPressed;
    }

    void setHotKeyPressed(boolean hotKeyPressed) {
        this.hotKeyPressed = hotKeyPressed;
    }

    boolean isHideAll() {
        return this.hideAll;
    }

    void setHideAll(boolean hideAll) {
        this.hideAll = hideAll;
    }

    public Table<WorldPoint, Integer, GroundItem> getCollectedGroundItems() {
        return this.collectedGroundItems;
    }

    static final class PriceHighlight {
        private final int price;
        private final Color color;

        public PriceHighlight(int price, Color color) {
            this.price = price;
            this.color = color;
        }

        public int getPrice() {
            return this.price;
        }

        public Color getColor() {
            return this.color;
        }

        public boolean equals(Object o) {
            if (o == this) {
                return true;
            }
            if (!(o instanceof PriceHighlight)) {
                return false;
            }
            PriceHighlight other = (PriceHighlight)o;
            if (this.getPrice() != other.getPrice()) {
                return false;
            }
            Color this$color = this.getColor();
            Color other$color = other.getColor();
            return !(this$color == null ? other$color != null : !((Object)this$color).equals(other$color));
        }

        public int hashCode() {
            int PRIME = 59;
            int result = 1;
            result = result * 59 + this.getPrice();
            Color $color = this.getColor();
            result = result * 59 + ($color == null ? 43 : ((Object)$color).hashCode());
            return result;
        }

        public String toString() {
            return "GroundItemsPlugin.PriceHighlight(price=" + this.getPrice() + ", color=" + String.valueOf(this.getColor()) + ")";
        }
    }
}

