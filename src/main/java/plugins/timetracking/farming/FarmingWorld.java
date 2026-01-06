/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.HashMultimap
 *  com.google.common.collect.Multimap
 *  com.google.common.collect.Multimaps
 *  com.google.inject.Singleton
 *  net.runelite.api.coords.WorldPoint
 */
package net.runelite.client.plugins.timetracking.farming;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.google.inject.Singleton;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.stream.Collectors;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.timetracking.Tab;
import net.runelite.client.plugins.timetracking.farming.FarmingPatch;
import net.runelite.client.plugins.timetracking.farming.FarmingRegion;
import net.runelite.client.plugins.timetracking.farming.PatchImplementation;

@Singleton
class FarmingWorld {
    private Multimap<Integer, FarmingRegion> regions = HashMultimap.create();
    private Map<Tab, Set<FarmingPatch>> tabs = new HashMap<Tab, Set<FarmingPatch>>();
    private final Comparator<FarmingPatch> tabSorter = Comparator.comparing(FarmingPatch::getImplementation).thenComparing(p -> p.getRegion().getName()).thenComparing(FarmingPatch::getName);
    private final FarmingRegion farmingGuildRegion;

    FarmingWorld() {
        this.add(new FarmingRegion("Al Kharid", 13106, false, new FarmingPatch("", 4771, PatchImplementation.CACTUS, 310)), 13362, 13105);
        this.add(new FarmingRegion("Aldarin", 5421, false, new FarmingPatch("", 4771, PatchImplementation.HOPS, 14030)), 5165, 5166, 5422, 5677, 5678);
        this.add(new FarmingRegion("Ardougne", 10290, false, new FarmingPatch("", 4771, PatchImplementation.BUSH, 2677)), 10546);
        this.add(new FarmingRegion("Ardougne", 10548, false, new FarmingPatch("North", 4771, PatchImplementation.ALLOTMENT, 2665, 0), new FarmingPatch("South", 4772, PatchImplementation.ALLOTMENT, 2665, 1), new FarmingPatch("", 4773, PatchImplementation.FLOWER), new FarmingPatch("", 4774, PatchImplementation.HERB), new FarmingPatch("", 4775, PatchImplementation.COMPOST)), new int[0]);
        this.add(new FarmingRegion("Auburnvale", 5427, false, new FarmingPatch("", 4771, PatchImplementation.TREE, 14514), new FarmingPatch("", 4772, PatchImplementation.BELLADONNA)), 5428, 5684);
        this.add(new FarmingRegion("Avium Savannah", 6702, true, new FarmingPatch("", 4771, PatchImplementation.HARDWOOD_TREE, 13401)), 6446);
        this.add(new FarmingRegion("Brimhaven", 11058, false, new FarmingPatch("", 4771, PatchImplementation.FRUIT_TREE, 2669), new FarmingPatch("", 4772, PatchImplementation.SPIRIT_TREE, 2686)), 11057);
        this.add(new FarmingRegion("Catherby", 11062, false, new FarmingPatch[]{new FarmingPatch("North", 4771, PatchImplementation.ALLOTMENT, 2664, 0), new FarmingPatch("South", 4772, PatchImplementation.ALLOTMENT, 2664, 1), new FarmingPatch("", 4773, PatchImplementation.FLOWER), new FarmingPatch("", 4774, PatchImplementation.HERB), new FarmingPatch("", 4775, PatchImplementation.COMPOST)}){

            @Override
            public boolean isInBounds(WorldPoint loc) {
                if (loc.getX() >= 2816 && loc.getY() < 3456) {
                    return loc.getX() < 2840 && loc.getY() >= 3440 && loc.getPlane() == 0;
                }
                return true;
            }
        }, 11061, 11318, 11317);
        this.add(new FarmingRegion("Catherby", 11317, false, new FarmingPatch[]{new FarmingPatch("", 4771, PatchImplementation.FRUIT_TREE, 2670)}){

            @Override
            public boolean isInBounds(WorldPoint loc) {
                return loc.getX() >= 2840 || loc.getY() < 3440 || loc.getPlane() == 1;
            }
        }, new int[0]);
        this.add(new FarmingRegion("Civitas illa Fortis", 6192, false, new FarmingPatch("North", 4771, PatchImplementation.ALLOTMENT, 12766, 0), new FarmingPatch("South", 4772, PatchImplementation.ALLOTMENT, 12766, 1), new FarmingPatch("", 4773, PatchImplementation.FLOWER), new FarmingPatch("", 4774, PatchImplementation.HERB), new FarmingPatch("", 4775, PatchImplementation.COMPOST)), 6447, 6448, 6449, 6191, 6193);
        this.add(new FarmingRegion("Champions' Guild", 12596, true, new FarmingPatch("", 4771, PatchImplementation.BUSH, 2674)), new int[0]);
        this.add(new FarmingRegion("Draynor Manor", 12340, false, new FarmingPatch("", 4771, PatchImplementation.BELLADONNA)), new int[0]);
        this.add(new FarmingRegion("Entrana", 11060, false, new FarmingPatch("", 4771, PatchImplementation.HOPS, 2667)), 11316);
        this.add(new FarmingRegion("Etceteria", 10300, false, new FarmingPatch("", 4771, PatchImplementation.BUSH, 2676), new FarmingPatch("", 4772, PatchImplementation.SPIRIT_TREE, 2685)), new int[0]);
        this.add(new FarmingRegion("Falador", 11828, false, new FarmingPatch("", 4771, PatchImplementation.TREE, 2679)), 12084);
        this.add(new FarmingRegion("Falador", 12083, false, new FarmingPatch[]{new FarmingPatch("North West", 4771, PatchImplementation.ALLOTMENT, 2663, 0), new FarmingPatch("South East", 4772, PatchImplementation.ALLOTMENT, 2663, 1), new FarmingPatch("", 4773, PatchImplementation.FLOWER), new FarmingPatch("", 4774, PatchImplementation.HERB), new FarmingPatch("", 4775, PatchImplementation.COMPOST)}){

            @Override
            public boolean isInBounds(WorldPoint loc) {
                return loc.getY() >= 3272;
            }
        }, new int[0]);
        this.add(new FarmingRegion("Fossil Island", 14651, false, new FarmingPatch[]{new FarmingPatch("East", 4771, PatchImplementation.HARDWOOD_TREE, 7754), new FarmingPatch("Middle", 4772, PatchImplementation.HARDWOOD_TREE, 7755), new FarmingPatch("West", 4773, PatchImplementation.HARDWOOD_TREE, 7756)}){

            @Override
            public boolean isInBounds(WorldPoint loc) {
                if (loc.getX() == 3753 && loc.getY() >= 3868 && loc.getY() <= 3870) {
                    return false;
                }
                if ((loc.getX() == 3729 || loc.getX() == 3728 || loc.getX() == 3747 || loc.getX() == 3746) && loc.getY() <= 3832 && loc.getY() >= 3830) {
                    return false;
                }
                return loc.getPlane() == 0;
            }
        }, 14907, 14908, 15164, 14652, 14906, 14650, 15162, 15163);
        this.add(new FarmingRegion("Seaweed", 15008, false, new FarmingPatch("North", 4771, PatchImplementation.SEAWEED, 7758, 0), new FarmingPatch("South", 4772, PatchImplementation.SEAWEED, 7758, 1)), new int[0]);
        this.add(new FarmingRegion("Gnome Stronghold", 9781, true, new FarmingPatch("", 4771, PatchImplementation.TREE, 2687), new FarmingPatch("", 4772, PatchImplementation.FRUIT_TREE, 2682)), 9782, 9526, 9525);
        this.add(new FarmingRegion("Harmony", 15148, false, new FarmingPatch("", 4771, PatchImplementation.ALLOTMENT), new FarmingPatch("", 4772, PatchImplementation.HERB)), new int[0]);
        this.add(new FarmingRegion("Kastori", 5423, false, new FarmingPatch("", 4771, PatchImplementation.CALQUAT, 14515), new FarmingPatch("", 4772, PatchImplementation.FRUIT_TREE, 14516), new FarmingPatch("", 4773, PatchImplementation.FLOWER)), 5167, 5424);
        this.add(new FarmingRegion("Kourend", 6967, false, new FarmingPatch("North East", 4771, PatchImplementation.ALLOTMENT, 6921, 0), new FarmingPatch("South West", 4772, PatchImplementation.ALLOTMENT, 6921, 1), new FarmingPatch("", 4773, PatchImplementation.FLOWER), new FarmingPatch("", 4774, PatchImplementation.HERB), new FarmingPatch("", 4775, PatchImplementation.COMPOST), new FarmingPatch("", 7904, PatchImplementation.SPIRIT_TREE, 6814)), 6711);
        this.add(new FarmingRegion("Kourend", 7223, false, new FarmingPatch("East 1", 4953, PatchImplementation.GRAPES), new FarmingPatch("East 2", 4954, PatchImplementation.GRAPES), new FarmingPatch("East 3", 4955, PatchImplementation.GRAPES), new FarmingPatch("East 4", 4956, PatchImplementation.GRAPES), new FarmingPatch("East 5", 4957, PatchImplementation.GRAPES), new FarmingPatch("East 6", 4958, PatchImplementation.GRAPES), new FarmingPatch("West 1", 4959, PatchImplementation.GRAPES), new FarmingPatch("West 2", 4960, PatchImplementation.GRAPES), new FarmingPatch("West 3", 4961, PatchImplementation.GRAPES), new FarmingPatch("West 4", 4962, PatchImplementation.GRAPES), new FarmingPatch("West 5", 4963, PatchImplementation.GRAPES), new FarmingPatch("West 6", 4964, PatchImplementation.GRAPES)), new int[0]);
        this.add(new FarmingRegion("Lletya", 9265, false, new FarmingPatch("", 4771, PatchImplementation.FRUIT_TREE, 2689)), 11103);
        this.add(new FarmingRegion("Lumbridge", 12851, false, new FarmingPatch("", 4771, PatchImplementation.HOPS, 2672)), new int[0]);
        this.add(new FarmingRegion("Lumbridge", 12594, false, new FarmingPatch("", 4771, PatchImplementation.TREE, 2681)), 12850);
        this.add(new FarmingRegion("Morytania", 13622, false, new FarmingPatch("Mushroom", 4771, PatchImplementation.MUSHROOM)), 13878);
        this.add(new FarmingRegion("Morytania", 14391, false, new FarmingPatch("North West", 4771, PatchImplementation.ALLOTMENT, 2666, 0), new FarmingPatch("South East", 4772, PatchImplementation.ALLOTMENT, 2666, 1), new FarmingPatch("", 4773, PatchImplementation.FLOWER), new FarmingPatch("", 4774, PatchImplementation.HERB), new FarmingPatch("", 4775, PatchImplementation.COMPOST)), 14390);
        this.add(new FarmingRegion("Port Sarim", 12082, false, new FarmingPatch[]{new FarmingPatch("", 4771, PatchImplementation.SPIRIT_TREE, 2684)}){

            @Override
            public boolean isInBounds(WorldPoint loc) {
                return loc.getY() < 3272;
            }
        }, 12083);
        this.add(new FarmingRegion("Rimmington", 11570, false, new FarmingPatch("", 4771, PatchImplementation.BUSH, 2675)), 11826);
        this.add(new FarmingRegion("Seers' Village", 10551, false, new FarmingPatch("", 4771, PatchImplementation.HOPS, 2673)), 10550);
        this.add(new FarmingRegion("Tai Bwo Wannai", 11056, false, new FarmingPatch("", 4771, PatchImplementation.CALQUAT, 2688)), new int[0]);
        this.add(new FarmingRegion("Taverley", 11573, false, new FarmingPatch("", 4771, PatchImplementation.TREE, 2678)), 11829);
        this.add(new FarmingRegion("Tree Gnome Village", 9777, true, new FarmingPatch("", 4771, PatchImplementation.FRUIT_TREE, 2683)), 10033);
        this.add(new FarmingRegion("Troll Stronghold", 11321, true, new FarmingPatch("", 4771, PatchImplementation.HERB)), new int[0]);
        this.add(new FarmingRegion("Varrock", 12854, false, new FarmingPatch("", 4771, PatchImplementation.TREE, 11957)), 12853);
        this.add(new FarmingRegion("Yanille", 10288, false, new FarmingPatch("", 4771, PatchImplementation.HOPS, 2671)), new int[0]);
        this.add(new FarmingRegion("Weiss", 11325, false, new FarmingPatch("", 4771, PatchImplementation.HERB)), new int[0]);
        this.add(new FarmingRegion("Farming Guild", 5021, true, new FarmingPatch("Hespori", 7908, PatchImplementation.HESPORI)), new int[0]);
        this.farmingGuildRegion = new FarmingRegion("Farming Guild", 4922, true, new FarmingPatch("", 7905, PatchImplementation.TREE, 8534), new FarmingPatch("", 4775, PatchImplementation.HERB), new FarmingPatch("", 4772, PatchImplementation.BUSH, 8535, 3), new FarmingPatch("", 7906, PatchImplementation.FLOWER), new FarmingPatch("North", 4773, PatchImplementation.ALLOTMENT, 8535, 1), new FarmingPatch("South", 4774, PatchImplementation.ALLOTMENT, 8535, 2), new FarmingPatch("", 7912, PatchImplementation.BIG_COMPOST), new FarmingPatch("", 7904, PatchImplementation.CACTUS, 8535, 0), new FarmingPatch("", 4771, PatchImplementation.SPIRIT_TREE, 8537), new FarmingPatch("", 7909, PatchImplementation.FRUIT_TREE, 8533), new FarmingPatch("Anima", 7911, PatchImplementation.ANIMA), new FarmingPatch("", 7910, PatchImplementation.CELASTRUS, 8629), new FarmingPatch("", 7907, PatchImplementation.REDWOOD, 8536));
        this.add(this.farmingGuildRegion, 5177, 5178, 5179, 4921, 4923, 4665, 4666, 4667);
        this.add(new FarmingRegion("Prifddinas", 13151, false, new FarmingPatch("North", 4771, PatchImplementation.ALLOTMENT, 9138, 0), new FarmingPatch("South", 4772, PatchImplementation.ALLOTMENT, 9138, 1), new FarmingPatch("", 4773, PatchImplementation.FLOWER), new FarmingPatch("", 4775, PatchImplementation.CRYSTAL_TREE), new FarmingPatch("", 4774, PatchImplementation.COMPOST)), 12895, 12894, 13150, 12994, 12993, 12737, 12738, 12126, 12127, 13250);
        this.add(new FarmingRegion("Donor Zone", 5516, false, new FarmingPatch("", 4775, PatchImplementation.HERB)), new int[0]);
        this.add(new FarmingRegion("Extreme Donor Zone", 5004, false, new FarmingPatch("", 4771, PatchImplementation.HERB)), new int[0]);
        this.regions = Multimaps.unmodifiableMultimap(this.regions);
        TreeMap<Tab, Set<FarmingPatch>> umtabs = new TreeMap<Tab, Set<FarmingPatch>>();
        for (Map.Entry<Tab, Set<FarmingPatch>> e : this.tabs.entrySet()) {
            umtabs.put(e.getKey(), Collections.unmodifiableSet(e.getValue()));
        }
        this.tabs = Collections.unmodifiableMap(umtabs);
    }

    private void add(FarmingRegion r, int ... extraRegions) {
        this.regions.put((Object)r.getRegionID(), (Object)r);
        for (int er : extraRegions) {
            this.regions.put((Object)er, (Object)r);
        }
        for (FarmingPatch p : r.getPatches()) {
            this.tabs.computeIfAbsent(p.getImplementation().getTab(), k -> new TreeSet<FarmingPatch>(this.tabSorter)).add(p);
        }
    }

    Collection<FarmingRegion> getRegionsForLocation(WorldPoint location) {
        return this.regions.get((Object)location.getRegionID()).stream().filter(region -> region.isInBounds(location)).collect(Collectors.toSet());
    }

    public Map<Tab, Set<FarmingPatch>> getTabs() {
        return this.tabs;
    }

    public FarmingRegion getFarmingGuildRegion() {
        return this.farmingGuildRegion;
    }
}

