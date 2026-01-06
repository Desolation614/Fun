/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.plugins.skillcalculator.skills;

import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.skillcalculator.skills.NamedSkillAction;

public enum ConstructionAction implements NamedSkillAction
{
    PLANK("Plank", 1, 29.0f, 960),
    DOCK_LEAF("Dock Leaf", 1, 31.0f, 8183),
    FERN_BIG_PLANT("Fern (big plant)", 1, 31.0f, 8186),
    PLANT("Plant", 1, 31.0f, 8180),
    SHORT_PLANT("Short Plant", 1, 31.0f, 8189),
    CRUDE_WOODEN_CHAIR("Crude Wooden Chair", 1, 58.0f, 8309),
    EXIT_PORTAL("Exit Portal", 1, 100.0f, 8168),
    BROWN_RUG("Brown Rug", 2, 30.0f, 8316),
    TORN_CURTAINS("Torn Curtains", 2, 132.0f, 8322),
    CLAY_FIREPLACE("Clay Fireplace", 3, 30.0f, 8325),
    WOODEN_BOOKCASE("Wooden Bookcase", 4, 115.0f, 8319),
    CAT_BLANKET("Cat Blanket", 5, 15.0f, 8236),
    TREE("Tree", 5, 31.0f, 8173),
    FIREPIT("Firepit", 5, 40.0f, 8216),
    DECORATIVE_ROCK("Decorative Rock", 5, 100.0f, 8169),
    BUSH("Bush", 6, 70.0f, 8187),
    LARGE_LEAF_BUSH("Large Leaf Bush", 6, 70.0f, 8190),
    SMALL_FERN("Small Fern", 6, 70.0f, 8181),
    THISTLE("Thistle", 6, 70.0f, 8184),
    WOODEN_SHELVES_1("Wooden Shelves 1", 6, 87.0f, 8223),
    BEER_BARREL("Beer Barrel", 7, 87.0f, 8239),
    PUMP_AND_DRAIN("Pump and Drain", 7, 100.0f, 8230),
    WOODEN_CHAIR("Wooden Chair", 8, 87.0f, 8310),
    WOODEN_LARDER("Wooden Larder", 9, 228.0f, 8233),
    NICE_TREE("Nice Tree", 10, 44.0f, 8174),
    POND("Pond", 10, 100.0f, 8170),
    WOODEN_BENCH("Wooden Bench", 10, 115.0f, 8108),
    WOOD_DINING_TABLE("Wood Dining Table", 10, 115.0f, 8115),
    FIREPIT_WITH_HOOK("Firepit with Hook", 11, 60.0f, 8217),
    WOOD_TABLE("Wood Table", 12, 87.0f, 8115),
    CIDER_BARREL("Cider Barrel", 12, 91.0f, 8240),
    FERN_SMALL_PLANT("Fern (small plant)", 12, 100.0f, 8182),
    HUGE_PLANT("Huge Plant", 12, 100.0f, 8191),
    REEDS("Reeds", 12, 100.0f, 8185),
    TALL_PLANT("Tall Plant", 12, 100.0f, 8188),
    WOODEN_SHELVES_2("Wooden Shelves 2", 12, 147.0f, 8224),
    RUG("Rug", 13, 60.0f, 8317),
    ROCKING_CHAIR("Rocking Chair", 14, 87.0f, 8311),
    OAK_PLANK("Oak Plank", 15, 60.0f, 8778),
    OAK_TREE("Oak Tree", 15, 70.0f, 8175),
    IMP_STATUE("Imp Statue", 15, 150.0f, 8171),
    OAK_DECORATION("Oak Decoration", 16, 120.0f, 8102),
    FIREPIT_WITH_POT("Firepit with Pot", 17, 80.0f, 8218),
    ASGARNIAN_ALE("Asgarnian Ale", 18, 184.0f, 1905),
    CURTAINS("Curtains", 18, 225.0f, 8323),
    CAT_BASKET("Cat Basket", 19, 58.0f, 8237),
    OAK_CHAIR("Oak Chair", 19, 120.0f, 8312),
    SHOE_BOX("Shoe Box", 20, 58.0f, 8038),
    WOODEN_BED("Wooden Bed", 20, 117.0f, 8031),
    SHAVING_STAND("Shaving Stand", 21, 30.0f, 8045),
    OAK_BENCH("Oak Bench", 22, 240.0f, 8109),
    OAK_DINING_TABLE("Oak Dining Table", 22, 240.0f, 8116),
    WOODEN_SHELVES_3("Wooden Shelves 3", 23, 147.0f, 8225),
    SMALL_OVEN("Small Oven", 24, 80.0f, 8219),
    OAK_CLOCK("Oak Clock", 25, 142.0f, 8052),
    ROPE_BELL_PULL("Rope Bell-Pull", 26, 64.0f, 8099),
    OAK_ARMCHAIR("Oak Armchair", 26, 180.0f, 8313),
    GREENMANS_ALE("Greenman's Ale", 26, 184.0f, 1909),
    OAK_DRAWERS("Oak Drawers", 27, 120.0f, 8039),
    PUMP_AND_TUB("Pump and Tub", 27, 200.0f, 8231),
    DEADMAN_RUG("Deadman rug", 28, 283.0f, 29598),
    OAK_SHAVING_STAND("Oak Shaving Stand", 29, 61.0f, 8046),
    LARGE_OVEN("Large Oven", 29, 100.0f, 8220),
    OAK_BOOKCASE("Oak Bookcase", 29, 180.0f, 8320),
    WILLOW_TREE("Willow Tree", 30, 100.0f, 8176),
    OAK_BED("Oak Bed", 30, 210.0f, 8032),
    LONG_BONE("Long Bone", 30, 4500.0f, 10976),
    CURVED_BONE("Curved Bone", 30, 6750.0f, 10977),
    CARVED_OAK_BENCH("Carved Oak Bench", 31, 240.0f, 8110),
    CARVED_OAK_TABLE("Carved Oak Table", 31, 360.0f, 8117),
    OAK_KITCHEN_TABLE("Oak Kitchen Table", 32, 180.0f, 8118),
    BOXING_RING("Boxing Ring", 32, 420.0f, 8023),
    STONE_FIREPLACE("Stone Fireplace", 33, 40.0f, 8326),
    CUSHIONED_BASKET("Cushioned Basket", 33, 58.0f, 8238),
    OAK_LARDER("Oak Larder", 33, 480.0f, 8234),
    GLOVE_RACK("Glove Rack", 34, 120.0f, 8028),
    STEEL_RANGE("Steel Range", 34, 120.0f, 8221),
    OAK_SHELVES_1("Oak Shelves 1", 34, 240.0f, 8226),
    LARGE_OAK_BED("Large Oak Bed", 34, 330.0f, 8033),
    TEAK_PLANK("Teak Plank", 35, 90.0f, 8780),
    TEAK_ARMCHAIR("Teak Armchair", 35, 180.0f, 8314),
    TEAK_DECORATION("Teak Decoration", 36, 180.0f, 8103),
    DRAGON_BITTER("Dragon Bitter", 36, 224.0f, 1911),
    BELL_PULL("Bell-Pull", 37, 120.0f, 8100),
    OAK_DRESSER("Oak Dresser", 37, 121.0f, 8047),
    TEAK_BENCH("Teak Bench", 38, 360.0f, 8112),
    TEAK_TABLE("Teak Table", 38, 360.0f, 8118),
    OAK_WARDROBE("Oak Wardrobe", 39, 180.0f, 8040),
    OAK_LECTERN("Oak Lectern", 40, 60.0f, 8334),
    MAHOGANY_PLANK("Mahogany Plank", 40, 140.0f, 8782),
    TEAK_BED("Teak Bed", 40, 300.0f, 8034),
    OPULENT_CURTAINS("Opulent Curtains", 40, 315.0f, 8324),
    MAHOGANY_BOOKCASE("Mahogany Bookcase", 40, 420.0f, 8321),
    GLOBE("Globe", 41, 180.0f, 8341),
    FENCING_RING("Fencing Ring", 41, 570.0f, 8024),
    FANCY_RANGE("Fancy Range", 42, 160.0f, 8222),
    CRYSTAL_BALL("Crystal Ball", 42, 280.0f, 8351),
    ALCHEMICAL_CHART("Alchemical Chart", 43, 30.0f, 8354),
    TEAK_LARDER("Teak larder", 43, 750.0f, 8235),
    WOODEN_TELESCOPE("Wooden Telescope", 44, 121.0f, 8348),
    WEAPONS_RACK("Weapons Rack", 44, 180.0f, 8029),
    CARVED_TEAK_BENCH("Carved Teak Bench", 44, 360.0f, 8112),
    MAPLE_TREE("Maple Tree", 45, 122.0f, 8177),
    OAK_SHELVES_2("Oak Shelves 2", 45, 240.0f, 8227),
    LARGE_TEAK_BED("Large Teak Bed", 45, 480.0f, 8035),
    CARVED_TEAK_TABLE("Carved Teak Table", 45, 600.0f, 8119),
    TEAK_DRESSER("Teak Dresser", 46, 181.0f, 8048),
    DEMON_LECTERN("Demon Lectern", 47, 120.0f, 8336),
    EAGLE_LECTERN("Eagle Lectern", 47, 120.0f, 8335),
    SINK("Sink", 47, 300.0f, 8232),
    MOUNTED_MYTHICAL_CAPE("Mounted Mythical Cape", 47, 370.0f, 21913),
    CHEFS_DELIGHT("Chef's Delight", 48, 224.0f, 5755),
    TELEPORT_FOCUS("Teleport Focus", 50, 40.0f, 8331),
    ORNAMENTAL_GLOBE("Ornamental Globe", 50, 270.0f, 8342),
    TEAK_PORTAL("Teak Portal", 50, 270.0f, 8328),
    MAHOGANY_ARMCHAIR("Mahogany Armchair", 50, 280.0f, 8315),
    TEAK_DRAWERS("Teak Drawers", 51, 180.0f, 8041),
    COMBAT_RING("Combat Ring", 51, 630.0f, 8025),
    TEAK_KITCHEN_TABLE("Teak Kitchen Table", 52, 270.0f, 8118),
    MAHOGANY_BENCH("Mahogany Bench", 52, 560.0f, 8113),
    MAHOGANY_TABLE("Mahogany Table", 52, 840.0f, 8120),
    FOUR_POSTER_BED("4-Poster Bed", 53, 450.0f, 8036),
    EXTRA_WEAPONS_RACK("Extra Weapons Rack", 54, 440.0f, 8030),
    ELEMENTAL_SPHERE("Elemental Sphere", 54, 580.0f, 8352),
    TEAK_CLOCK("Teak Clock", 55, 202.0f, 8053),
    FANCY_TEAK_DRESSER("Fancy Teak Dresser", 56, 182.0f, 8049),
    TEAK_SHELVES_1("Teak Shelves 1", 56, 330.0f, 8228),
    GILDED_DECORATION("Gilded Decoration", 56, 1020.0f, 8104),
    TEAK_DEMON_LECTERN("Teak Demon Lectern", 57, 180.0f, 8338),
    TEAK_EAGLE_LECTERN("Teak Eagle Lectern", 57, 180.0f, 8337),
    LIMESTONE_ATTACK_STONE("Limestone attack stone", 59, 200.0f, 8154),
    LUNAR_GLOBE("Lunar Globe", 59, 570.0f, 8343),
    YEW_TREE("Yew Tree", 60, 141.0f, 8178),
    SPICE_RACK("Spice Rack", 60, 374.0f, 24479),
    POSH_BELL_PULL("Posh Bell-Pull", 60, 420.0f, 8101),
    GILDED_FOUR_POSTER_BED("Gilded 4-Poster Bed", 60, 1330.0f, 8037),
    GILDED_BENCH("Gilded Bench", 61, 1760.0f, 8114),
    ASTRONOMICAL_CHART("Astronomical Chart", 63, 45.0f, 8355),
    TEAK_WARDROBE("Teak Wardrobe", 63, 270.0f, 8042),
    MARBLE_FIREPLACE("Marble Fireplace", 63, 500.0f, 8327),
    TEAK_TELESCOPE("Teak Telescope", 64, 181.0f, 8349),
    MAHOGANY_DRESSER("Mahogany Dresser", 64, 281.0f, 8050),
    OPULENT_RUG("Opulent Rug", 65, 360.0f, 8318),
    MAHOGANY_PORTAL("Mahogany Portal", 65, 420.0f, 8329),
    GREATER_FOCUS("Greater Focus", 65, 500.0f, 8332),
    TEAK_GARDEN_BENCH("Teak Garden Bench", 66, 540.0f, 20649),
    CRYSTAL_OF_POWER("Crystal of Power", 66, 890.0f, 8353),
    MAHOGANY_DEMON_LECTERN("Mahogany Demon Lectern", 67, 580.0f, 8340),
    MAHOGANY_EAGLE_LECTERN("Mahogany Eagle Lectern", 67, 580.0f, 8339),
    TEAK_SHELVES_2("Teak Shelves 2", 67, 930.0f, 8229),
    CELESTIAL_GLOBE("Celestial Globe", 68, 570.0f, 8344),
    DUNGEON_ENTRANCE("Dungeon Entrance", 70, 500.0f, 8172),
    RANGING_PEDESTALS("Ranging Pedestals", 71, 720.0f, 8026),
    OPULENT_TABLE("Opulent Table", 72, 3100.0f, 8121),
    GILDED_DRESSER("Gilded Dresser", 74, 582.0f, 8051),
    OAK_DOOR("Oak Door", 74, 600.0f, 8122),
    MAGIC_TREE("Magic Tree", 75, 223.0f, 8179),
    MAHOGANY_WARDROBE("Mahogany Wardrobe", 75, 420.0f, 8043),
    GNOME_BENCH("Gnome Bench", 77, 840.0f, 20650),
    ARMILLARY_GLOBE("Armillary Globe", 77, 960.0f, 8341),
    MARBLE_PORTAL("Marble Portal", 80, 1500.0f, 8330),
    SCRYING_POOL("Scrying Pool", 80, 2000.0f, 8333),
    BALANCE_BEAM("Balance Beam", 81, 1000.0f, 8027),
    INFERNAL_CHART("Infernal Chart", 83, 60.0f, 8356),
    MAHOGANY_TELESCOPE("Mahogany Telescope", 84, 281.0f, 8350),
    GILDED_CLOCK("Gilded Clock", 85, 602.0f, 8054),
    SMALL_ORRERY("Small Orrery", 86, 1320.0f, 8346),
    GILDED_WARDROBE("Gilded Wardrobe", 87, 720.0f, 8044),
    LARGE_ORRERY("Large Orrery", 95, 1420.0f, 8347);

    private final String name;
    private final int level;
    private final float xp;
    private final int icon;

    @Override
    public boolean isMembers(ItemManager itemManager) {
        return true;
    }

    private ConstructionAction(String name, int level, float xp, int icon) {
        this.name = name;
        this.level = level;
        this.xp = xp;
        this.icon = icon;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public int getLevel() {
        return this.level;
    }

    @Override
    public float getXp() {
        return this.xp;
    }

    @Override
    public int getIcon() {
        return this.icon;
    }
}

