/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Ordering
 *  javax.annotation.Nullable
 *  net.runelite.api.EnumComposition
 *  net.runelite.http.api.worlds.World
 *  net.runelite.http.api.worlds.WorldType
 */
package net.runelite.client.plugins.worldhopper;

import com.google.common.collect.Ordering;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import javax.annotation.Nullable;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import net.runelite.api.EnumComposition;
import net.runelite.client.plugins.worldhopper.RegionFilterMode;
import net.runelite.client.plugins.worldhopper.SubscriptionFilterMode;
import net.runelite.client.plugins.worldhopper.WorldHopperPlugin;
import net.runelite.client.plugins.worldhopper.WorldTableHeader;
import net.runelite.client.plugins.worldhopper.WorldTableRow;
import net.runelite.client.plugins.worldhopper.WorldTypeFilter;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.DynamicGridLayout;
import net.runelite.client.ui.PluginPanel;
import net.runelite.http.api.worlds.World;
import net.runelite.http.api.worlds.WorldType;

class WorldSwitcherPanel
extends PluginPanel {
    private static final Color ODD_ROW = new Color(44, 44, 44);
    private static final int WORLD_COLUMN_WIDTH = 60;
    private static final int PLAYERS_COLUMN_WIDTH = 40;
    private static final int PING_COLUMN_WIDTH = 47;
    private final JPanel listContainer = new JPanel();
    private boolean active;
    private WorldTableHeader worldHeader;
    private WorldTableHeader playersHeader;
    private WorldTableHeader activityHeader;
    private WorldTableHeader pingHeader;
    private WorldOrder orderIndex = WorldOrder.WORLD;
    private boolean ascendingOrder = true;
    private final ArrayList<WorldTableRow> rows = new ArrayList();
    private final WorldHopperPlugin plugin;
    private SubscriptionFilterMode subscriptionFilterMode;
    private Set<RegionFilterMode> regionFilterMode;
    private Set<WorldTypeFilter> worldTypeFilters;

    WorldSwitcherPanel(WorldHopperPlugin plugin) {
        this.plugin = plugin;
        this.setBorder(null);
        this.setLayout(new DynamicGridLayout(0, 1));
        JPanel headerContainer = this.buildHeader();
        this.listContainer.setLayout(new GridLayout(0, 1));
        this.add(headerContainer);
        this.add(this.listContainer);
    }

    @Override
    public void onActivate() {
        this.active = true;
        this.updateList();
    }

    @Override
    public void onDeactivate() {
        this.active = false;
    }

    void switchCurrentHighlight(int newWorld, int lastWorld) {
        for (WorldTableRow row : this.rows) {
            if (row.getWorld().getId() == newWorld) {
                row.recolour(true);
                continue;
            }
            if (row.getWorld().getId() != lastWorld) continue;
            row.recolour(false);
        }
    }

    void updateListData(Map<Integer, Integer> worldData) {
        for (WorldTableRow worldTableRow : this.rows) {
            World world = worldTableRow.getWorld();
            Integer playerCount = worldData.get(world.getId());
            if (playerCount == null) continue;
            worldTableRow.updatePlayerCount(playerCount);
        }
        if (this.orderIndex == WorldOrder.PLAYERS) {
            this.updateList();
        }
    }

    void updatePing(int world, int ping) {
        for (WorldTableRow worldTableRow : this.rows) {
            if (worldTableRow.getWorld().getId() != world) continue;
            worldTableRow.setPing(ping);
            if (this.orderIndex != WorldOrder.PING) break;
            this.updateList();
            break;
        }
    }

    void hidePing() {
        for (WorldTableRow worldTableRow : this.rows) {
            worldTableRow.hidePing();
        }
    }

    void showPing() {
        for (WorldTableRow worldTableRow : this.rows) {
            worldTableRow.showPing();
        }
    }

    void updateList() {
        this.rows.sort((r1, r2) -> {
            switch (this.orderIndex) {
                case PING: {
                    return this.getCompareValue((WorldTableRow)r1, (WorldTableRow)r2, row -> {
                        int ping = row.getPing();
                        return ping > 0 ? Integer.valueOf(ping) : null;
                    });
                }
                case WORLD: {
                    return this.getCompareValue((WorldTableRow)r1, (WorldTableRow)r2, row -> Integer.valueOf(row.getWorld().getId()));
                }
                case PLAYERS: {
                    return this.getCompareValue((WorldTableRow)r1, (WorldTableRow)r2, WorldTableRow::getPlayerCount);
                }
                case ACTIVITY: {
                    return this.getCompareValue((WorldTableRow)r1, (WorldTableRow)r2, row -> {
                        String activity = row.getWorld().getActivity();
                        return !activity.equals("-") ? activity : null;
                    });
                }
            }
            return 0;
        });
        this.rows.sort((r1, r2) -> {
            boolean b1 = this.plugin.isFavorite(r1.getWorld());
            boolean b2 = this.plugin.isFavorite(r2.getWorld());
            return Boolean.compare(b2, b1);
        });
        this.listContainer.removeAll();
        for (int i = 0; i < this.rows.size(); ++i) {
            WorldTableRow row = this.rows.get(i);
            row.setBackground(i % 2 == 0 ? ODD_ROW : ColorScheme.DARK_GRAY_COLOR);
            this.listContainer.add(row);
        }
        this.listContainer.revalidate();
        this.listContainer.repaint();
    }

    private int getCompareValue(WorldTableRow row1, WorldTableRow row2, Function<WorldTableRow, Comparable> compareByFn) {
        Ordering ordering = Ordering.natural();
        if (!this.ascendingOrder) {
            ordering = ordering.reverse();
        }
        ordering = ordering.nullsLast();
        return ordering.compare((Object)compareByFn.apply(row1), (Object)compareByFn.apply(row2));
    }

    void updateFavoriteMenu(int world, boolean favorite) {
        for (WorldTableRow row : this.rows) {
            if (row.getWorld().getId() != world) continue;
            row.setFavoriteMenu(favorite);
        }
    }

    /*
     * Enabled aggressive block sorting
     */
    void populate(List<World> worlds, @Nullable EnumComposition worldLocations) {
        this.rows.clear();
        int i = 0;
        while (true) {
            block10: {
                World world;
                block11: {
                    if (i >= worlds.size()) {
                        this.updateList();
                        return;
                    }
                    world = worlds.get(i);
                    switch (this.subscriptionFilterMode) {
                        case FREE: {
                            if (!world.getTypes().contains(WorldType.MEMBERS)) break;
                            break block10;
                        }
                        case MEMBERS: {
                            if (!world.getTypes().contains(WorldType.MEMBERS)) break block10;
                        }
                    }
                    if (!this.regionFilterMode.isEmpty() && !this.regionFilterMode.contains((Object)RegionFilterMode.of(world.getRegion()))) break block10;
                    if (this.worldTypeFilters.isEmpty()) break block11;
                    boolean matches = false;
                    for (WorldTypeFilter worldTypeFilter : this.worldTypeFilters) {
                        matches |= worldTypeFilter.matches(world.getTypes());
                    }
                    if (!matches) break block10;
                }
                this.rows.add(this.buildRow(world, i % 2 == 0, world.getId() == this.plugin.getCurrentWorld() && this.plugin.getLastWorld() != 0, this.plugin.isFavorite(world), worldLocations != null ? worldLocations.getIntValue(world.getId()) : -1));
            }
            ++i;
        }
    }

    private void orderBy(WorldOrder order) {
        this.pingHeader.highlight(false, this.ascendingOrder);
        this.worldHeader.highlight(false, this.ascendingOrder);
        this.playersHeader.highlight(false, this.ascendingOrder);
        this.activityHeader.highlight(false, this.ascendingOrder);
        switch (order) {
            case PING: {
                this.pingHeader.highlight(true, this.ascendingOrder);
                break;
            }
            case WORLD: {
                this.worldHeader.highlight(true, this.ascendingOrder);
                break;
            }
            case PLAYERS: {
                this.playersHeader.highlight(true, this.ascendingOrder);
                break;
            }
            case ACTIVITY: {
                this.activityHeader.highlight(true, this.ascendingOrder);
            }
        }
        this.orderIndex = order;
        this.updateList();
    }

    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        JPanel leftSide = new JPanel(new BorderLayout());
        JPanel rightSide = new JPanel(new BorderLayout());
        this.pingHeader = new WorldTableHeader("Ping", this.orderIndex == WorldOrder.PING, this.ascendingOrder, this.plugin::refresh);
        this.pingHeader.setPreferredSize(new Dimension(47, 0));
        this.pingHeader.addMouseListener(new MouseAdapter(){

            @Override
            public void mousePressed(MouseEvent mouseEvent) {
                if (SwingUtilities.isRightMouseButton(mouseEvent)) {
                    return;
                }
                WorldSwitcherPanel.this.ascendingOrder = WorldSwitcherPanel.this.orderIndex != WorldOrder.PING || !WorldSwitcherPanel.this.ascendingOrder;
                WorldSwitcherPanel.this.orderBy(WorldOrder.PING);
            }
        });
        this.worldHeader = new WorldTableHeader("World", this.orderIndex == WorldOrder.WORLD, this.ascendingOrder, this.plugin::refresh);
        this.worldHeader.setPreferredSize(new Dimension(60, 0));
        this.worldHeader.addMouseListener(new MouseAdapter(){

            @Override
            public void mousePressed(MouseEvent mouseEvent) {
                if (SwingUtilities.isRightMouseButton(mouseEvent)) {
                    return;
                }
                WorldSwitcherPanel.this.ascendingOrder = WorldSwitcherPanel.this.orderIndex != WorldOrder.WORLD || !WorldSwitcherPanel.this.ascendingOrder;
                WorldSwitcherPanel.this.orderBy(WorldOrder.WORLD);
            }
        });
        this.playersHeader = new WorldTableHeader("#", this.orderIndex == WorldOrder.PLAYERS, this.ascendingOrder, this.plugin::refresh);
        this.playersHeader.setPreferredSize(new Dimension(40, 0));
        this.playersHeader.addMouseListener(new MouseAdapter(){

            @Override
            public void mousePressed(MouseEvent mouseEvent) {
                if (SwingUtilities.isRightMouseButton(mouseEvent)) {
                    return;
                }
                WorldSwitcherPanel.this.ascendingOrder = WorldSwitcherPanel.this.orderIndex != WorldOrder.PLAYERS || !WorldSwitcherPanel.this.ascendingOrder;
                WorldSwitcherPanel.this.orderBy(WorldOrder.PLAYERS);
            }
        });
        this.activityHeader = new WorldTableHeader("Activity", this.orderIndex == WorldOrder.ACTIVITY, this.ascendingOrder, this.plugin::refresh);
        this.activityHeader.addMouseListener(new MouseAdapter(){

            @Override
            public void mousePressed(MouseEvent mouseEvent) {
                if (SwingUtilities.isRightMouseButton(mouseEvent)) {
                    return;
                }
                WorldSwitcherPanel.this.ascendingOrder = WorldSwitcherPanel.this.orderIndex != WorldOrder.ACTIVITY || !WorldSwitcherPanel.this.ascendingOrder;
                WorldSwitcherPanel.this.orderBy(WorldOrder.ACTIVITY);
            }
        });
        leftSide.add((Component)this.worldHeader, "West");
        leftSide.add((Component)this.playersHeader, "Center");
        rightSide.add((Component)this.activityHeader, "Center");
        rightSide.add((Component)this.pingHeader, "East");
        header.add((Component)leftSide, "West");
        header.add((Component)rightSide, "Center");
        return header;
    }

    private WorldTableRow buildRow(World world, boolean stripe, boolean current, boolean favorite, int worldLocation) {
        WorldTableRow row = new WorldTableRow(world, current, favorite, this.plugin.getStoredPing(world), this.plugin::hopTo, (w, add) -> {
            if (add.booleanValue()) {
                this.plugin.addToFavorites((World)w);
            } else {
                this.plugin.removeFromFavorites((World)w);
            }
            this.updateList();
        }, worldLocation);
        row.setBackground(stripe ? ODD_ROW : ColorScheme.DARK_GRAY_COLOR);
        return row;
    }

    boolean isActive() {
        return this.active;
    }

    void setSubscriptionFilterMode(SubscriptionFilterMode subscriptionFilterMode) {
        this.subscriptionFilterMode = subscriptionFilterMode;
    }

    void setRegionFilterMode(Set<RegionFilterMode> regionFilterMode) {
        this.regionFilterMode = regionFilterMode;
    }

    void setWorldTypeFilters(Set<WorldTypeFilter> worldTypeFilters) {
        this.worldTypeFilters = worldTypeFilters;
    }

    private static enum WorldOrder {
        WORLD,
        PLAYERS,
        ACTIVITY,
        PING;

    }
}

