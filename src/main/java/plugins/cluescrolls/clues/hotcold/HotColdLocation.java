/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  net.runelite.api.coords.WorldPoint
 */
package net.runelite.client.plugins.cluescrolls.clues.hotcold;

import com.google.common.base.Preconditions;
import java.awt.Rectangle;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.cluescrolls.clues.Enemy;
import net.runelite.client.plugins.cluescrolls.clues.hotcold.HotColdArea;
import net.runelite.client.plugins.cluescrolls.clues.hotcold.HotColdTemperature;

public enum HotColdLocation {
    ASGARNIA_WARRIORS(HotColdType.MASTER, new WorldPoint(2860, 3562, 0), HotColdArea.ASGARNIA, "North of the Warriors' Guild in Burthorpe.", Enemy.BRASSICAN_MAGE),
    ASGARNIA_JATIX(HotColdType.MASTER, new WorldPoint(2915, 3425, 0), HotColdArea.ASGARNIA, "East of Jatix's Herblore Shop in Taverley.", Enemy.BRASSICAN_MAGE),
    ASGARNIA_BARB(HotColdType.MASTER, new WorldPoint(3033, 3438, 0), HotColdArea.ASGARNIA, "West of Barbarian Village.", Enemy.BRASSICAN_MAGE),
    ASGARNIA_MIAZRQA(HotColdType.MASTER, new WorldPoint(2972, 3486, 0), HotColdArea.ASGARNIA, "North of Miazrqa's tower, outside Goblin Village.", Enemy.BRASSICAN_MAGE),
    ASGARNIA_COW(HotColdType.MASTER, new WorldPoint(3031, 3304, 0), HotColdArea.ASGARNIA, "In the cow pen north of Sarah's Farming Shop.", Enemy.ANCIENT_WIZARDS),
    ASGARNIA_PARTY_ROOM(HotColdType.MASTER, new WorldPoint(3030, 3364, 0), HotColdArea.ASGARNIA, "Outside the Falador Party Room.", Enemy.BRASSICAN_MAGE),
    ASGARNIA_CRAFT_GUILD(HotColdType.MASTER, new WorldPoint(2917, 3295, 0), HotColdArea.ASGARNIA, "Outside the Crafting Guild cow pen.", Enemy.BRASSICAN_MAGE),
    ASGARNIA_RIMMINGTON(HotColdType.MASTER, new WorldPoint(2976, 3239, 0), HotColdArea.ASGARNIA, "In the centre of the Rimmington mine.", Enemy.BRASSICAN_MAGE),
    ASGARNIA_MUDSKIPPER(HotColdType.MASTER, new WorldPoint(2987, 3110, 0), HotColdArea.ASGARNIA, "Mudskipper Point, near the starfish in the south-west corner.", Enemy.BRASSICAN_MAGE),
    ASGARNIA_TROLL(HotColdType.MASTER, new WorldPoint(2910, 3615, 0), HotColdArea.ASGARNIA, "The Troll arena, where the player fights Dad during the Troll Stronghold quest. Bring climbing boots if travelling from Burthorpe.", Enemy.BRASSICAN_MAGE),
    DESERT_GENIE(HotColdType.MASTER, new WorldPoint(3359, 2912, 0), HotColdArea.DESERT, "West of Nardah genie cave.", Enemy.BRASSICAN_MAGE),
    DESERT_ALKHARID_MINE(HotColdType.MASTER, new WorldPoint(3279, 3263, 0), HotColdArea.DESERT, "West of Al Kharid mine.", Enemy.BRASSICAN_MAGE),
    DESERT_MENAPHOS_GATE(HotColdType.MASTER, new WorldPoint(3223, 2820, 0), HotColdArea.DESERT, "North of Menaphos gate.", Enemy.BRASSICAN_MAGE),
    DESERT_BEDABIN_CAMP(HotColdType.MASTER, new WorldPoint(3161, 3047, 0), HotColdArea.DESERT, "Bedabin Camp, near the north tent.", Enemy.BRASSICAN_MAGE),
    DESERT_UZER(HotColdType.MASTER, new WorldPoint(3432, 3105, 0), HotColdArea.DESERT, "West of Uzer.", Enemy.BRASSICAN_MAGE),
    DESERT_POLLNIVNEACH(HotColdType.MASTER, new WorldPoint(3288, 2976, 0), HotColdArea.DESERT, "West of Pollnivneach.", Enemy.BRASSICAN_MAGE),
    DESERT_MTA(HotColdType.MASTER, new WorldPoint(3347, 3295, 0), HotColdArea.DESERT, "Next to Mage Training Arena.", Enemy.BRASSICAN_MAGE),
    DESERT_RUINS_OF_ULLEK(HotColdType.MASTER, new WorldPoint(3428, 2773, 0), HotColdArea.DESERT, "South-east of Ruins of Ullek.", Enemy.BRASSICAN_MAGE),
    DESERT_SHANTY(HotColdType.MASTER, new WorldPoint(3292, 3107, 0), HotColdArea.DESERT, "South-west of Shantay Pass.", Enemy.BRASSICAN_MAGE),
    DRAYNOR_MANOR_MUSHROOMS(HotColdType.BEGINNER, new WorldPoint(3096, 3379, 0), HotColdArea.MISTHALIN, "Patch of mushrooms just northwest of Draynor Manor"),
    DRAYNOR_WHEAT_FIELD(HotColdType.BEGINNER, new WorldPoint(3120, 3282, 0), HotColdArea.MISTHALIN, "Inside the wheat field next to Draynor Village"),
    FELDIP_HILLS_JIGGIG(HotColdType.MASTER, new WorldPoint(2409, 3053, 0), HotColdArea.FELDIP_HILLS, "West of Jiggig, east of the fairy ring BKP.", Enemy.BRASSICAN_MAGE),
    FELDIP_HILLS_SW(HotColdType.MASTER, new WorldPoint(2586, 2897, 0), HotColdArea.FELDIP_HILLS, "West of the southeasternmost lake in Feldip Hills.", Enemy.BRASSICAN_MAGE),
    FELDIP_HILLS_GNOME_GLITER(HotColdType.MASTER, new WorldPoint(2555, 2972, 0), HotColdArea.FELDIP_HILLS, "East of the gnome glider (Lemantolly Undri).", Enemy.BRASSICAN_MAGE),
    FELDIP_HILLS_RANTZ(HotColdType.MASTER, new WorldPoint(2611, 2950, 0), HotColdArea.FELDIP_HILLS, "South of Rantz, west of the empty glass bottles.", Enemy.BRASSICAN_MAGE),
    FELDIP_HILLS_SOUTH(HotColdType.MASTER, new WorldPoint(2486, 3007, 0), HotColdArea.FELDIP_HILLS, "South of Jiggig.", Enemy.BRASSICAN_MAGE),
    FELDIP_HILLS_RED_CHIN(HotColdType.MASTER, new WorldPoint(2530, 2901, 0), HotColdArea.FELDIP_HILLS, "Outside the red chinchompa hunting ground entrance, south of the Hunting expert's hut.", Enemy.BRASSICAN_MAGE),
    FELDIP_HILLS_SE(HotColdType.MASTER, new WorldPoint(2569, 2918, 0), HotColdArea.FELDIP_HILLS, "South-east of the \u2229-shaped lake, near the Hunter icon.", Enemy.BRASSICAN_MAGE),
    FELDIP_HILLS_CW_BALLOON(HotColdType.MASTER, new WorldPoint(2451, 3112, 0), HotColdArea.FELDIP_HILLS, "Directly west of the Castle Wars balloon.", Enemy.BRASSICAN_MAGE),
    FREMENNIK_PROVINCE_MTN_CAMP(HotColdType.MASTER, new WorldPoint(2800, 3669, 0), HotColdArea.FREMENNIK_PROVINCE, "At the Mountain Camp.", Enemy.BRASSICAN_MAGE),
    FREMENNIK_PROVINCE_RELLEKKA_HUNTER(HotColdType.MASTER, new WorldPoint(2720, 3784, 0), HotColdArea.FREMENNIK_PROVINCE, "At the Rellekka Hunter area, near the Hunter icon.", Enemy.BRASSICAN_MAGE),
    FREMENNIK_PROVINCE_KELGADRIM_ENTRANCE(HotColdType.MASTER, new WorldPoint(2711, 3689, 0), HotColdArea.FREMENNIK_PROVINCE, "West of the Keldagrim entrance mine.", Enemy.BRASSICAN_MAGE),
    FREMENNIK_PROVINCE_SW(HotColdType.MASTER, new WorldPoint(2604, 3648, 0), HotColdArea.FREMENNIK_PROVINCE, "Outside the fence in the south-western corner of Rellekka.", Enemy.BRASSICAN_MAGE),
    FREMENNIK_PROVINCE_LIGHTHOUSE(HotColdType.MASTER, new WorldPoint(2585, 3601, 0), HotColdArea.FREMENNIK_PROVINCE, "South-east of the Lighthouse.", Enemy.BRASSICAN_MAGE),
    FREMENNIK_PROVINCE_ETCETERIA_CASTLE(HotColdType.MASTER, new WorldPoint(2617, 3862, 0), HotColdArea.FREMENNIK_PROVINCE, "South-east of Etceteria's castle.", Enemy.BRASSICAN_MAGE),
    FREMENNIK_PROVINCE_MISC_COURTYARD(HotColdType.MASTER, new WorldPoint(2527, 3868, 0), HotColdArea.FREMENNIK_PROVINCE, "Outside Miscellania's courtyard.", Enemy.BRASSICAN_MAGE),
    FREMENNIK_PROVINCE_FREMMY_ISLES_MINE(HotColdType.MASTER, new WorldPoint(2374, 3850, 0), HotColdArea.FREMENNIK_PROVINCE, "Central Fremennik Isles mine.", Enemy.ANCIENT_WIZARDS),
    FREMENNIK_PROVINCE_WEST_ISLES_MINE(HotColdType.MASTER, new WorldPoint(2313, 3850, 0), HotColdArea.FREMENNIK_PROVINCE, "West Fremennik Isles mine.", Enemy.ANCIENT_WIZARDS),
    FREMENNIK_PROVINCE_WEST_JATIZSO_ENTRANCE(HotColdType.MASTER, new WorldPoint(2393, 3812, 0), HotColdArea.FREMENNIK_PROVINCE, "West of the Jatizso mine entrance.", Enemy.BRASSICAN_MAGE),
    FREMENNIK_PROVINCE_PIRATES_COVE(HotColdType.MASTER, new WorldPoint(2211, 3817, 0), HotColdArea.FREMENNIK_PROVINCE, "Pirates' Cove", Enemy.ANCIENT_WIZARDS),
    FREMENNIK_PROVINCE_ASTRAL_ALTER(HotColdType.MASTER, new WorldPoint(2149, 3865, 0), HotColdArea.FREMENNIK_PROVINCE, "Astral altar", Enemy.ANCIENT_WIZARDS),
    FREMENNIK_PROVINCE_LUNAR_VILLAGE(HotColdType.MASTER, new WorldPoint(2084, 3916, 0), HotColdArea.FREMENNIK_PROVINCE, "Lunar Isle, inside the village.", Enemy.ANCIENT_WIZARDS),
    FREMENNIK_PROVINCE_LUNAR_NORTH(HotColdType.MASTER, new WorldPoint(2106, 3949, 0), HotColdArea.FREMENNIK_PROVINCE, "Lunar Isle, north of the village.", Enemy.ANCIENT_WIZARDS),
    ICE_MOUNTAIN(HotColdType.BEGINNER, new WorldPoint(3007, 3475, 0), HotColdArea.MISTHALIN, "Atop Ice Mountain"),
    ISLE_OF_SOULS_MINE(HotColdType.MASTER, new WorldPoint(2189, 2794, 0), HotColdArea.KANDARIN, "Isle of Souls Mine, south of the Soul Wars lobby", Enemy.BRASSICAN_MAGE),
    KANDARIN_SINCLAR_MANSION(HotColdType.MASTER, new WorldPoint(2730, 3588, 0), HotColdArea.KANDARIN, "North-west of the Sinclair Mansion, near the log balance shortcut.", Enemy.BRASSICAN_MAGE),
    KANDARIN_CATHERBY(HotColdType.MASTER, new WorldPoint(2774, 3436, 0), HotColdArea.KANDARIN, "Catherby, between the bank and the beehives, near small rock formation.", Enemy.BRASSICAN_MAGE),
    KANDARIN_GRAND_TREE(HotColdType.MASTER, new WorldPoint(2448, 3503, 0), HotColdArea.KANDARIN, "Grand Tree, just east of the terrorchick gnome enclosure.", Enemy.BRASSICAN_MAGE),
    KANDARIN_SEERS(HotColdType.MASTER, new WorldPoint(2732, 3485, 0), HotColdArea.KANDARIN, "Outside Seers' Village bank.", Enemy.BRASSICAN_MAGE),
    KANDARIN_MCGRUBORS_WOOD(HotColdType.MASTER, new WorldPoint(2653, 3485, 0), HotColdArea.KANDARIN, "McGrubor's Wood", Enemy.BRASSICAN_MAGE),
    KANDARIN_FISHING_BUILD(HotColdType.MASTER, new WorldPoint(2590, 3369, 0), HotColdArea.KANDARIN, "South of Fishing Guild", Enemy.BRASSICAN_MAGE),
    KANDARIN_WITCHHAVEN(HotColdType.MASTER, new WorldPoint(2707, 3306, 0), HotColdArea.KANDARIN, "Outside Witchaven, west of Jeb, Holgart, and Caroline.", Enemy.BRASSICAN_MAGE),
    KANDARIN_NECRO_TOWER(HotColdType.MASTER, new WorldPoint(2667, 3241, 0), HotColdArea.KANDARIN, "Ground floor inside the Necromancer Tower. Easily accessed by using fairy ring code DJP.", Enemy.ANCIENT_WIZARDS),
    KANDARIN_FIGHT_ARENA(HotColdType.MASTER, new WorldPoint(2587, 3135, 0), HotColdArea.KANDARIN, "South of the Fight Arena, north-west of the Nightmare Zone.", Enemy.BRASSICAN_MAGE),
    KANDARIN_TREE_GNOME_VILLAGE(HotColdType.MASTER, new WorldPoint(2530, 3164, 0), HotColdArea.KANDARIN, "Tree Gnome Village, near the general store icon.", Enemy.BRASSICAN_MAGE),
    KANDARIN_GRAVE_OF_SCORPIUS(HotColdType.MASTER, new WorldPoint(2467, 3227, 0), HotColdArea.KANDARIN, "Grave of Scorpius", Enemy.BRASSICAN_MAGE),
    KANDARIN_KHAZARD_BATTLEFIELD(HotColdType.MASTER, new WorldPoint(2522, 3252, 0), HotColdArea.KANDARIN, "Khazard Battlefield, south of Tracker gnome 2.", Enemy.BRASSICAN_MAGE),
    KANDARIN_WEST_ARDY(HotColdType.MASTER, new WorldPoint(2535, 3322, 0), HotColdArea.KANDARIN, "West Ardougne, near the staircase outside the Civic Office.", Enemy.BRASSICAN_MAGE),
    KANDARIN_SW_TREE_GNOME_STRONGHOLD(HotColdType.MASTER, new WorldPoint(2411, 3429, 0), HotColdArea.KANDARIN, "South-west Tree Gnome Stronghold", Enemy.BRASSICAN_MAGE),
    KANDARIN_OUTPOST(HotColdType.MASTER, new WorldPoint(2457, 3362, 0), HotColdArea.KANDARIN, "South of the Tree Gnome Stronghold, north-east of the Outpost.", Enemy.BRASSICAN_MAGE),
    KANDARIN_BAXTORIAN_FALLS(HotColdType.MASTER, new WorldPoint(2530, 3477, 0), HotColdArea.KANDARIN, "South-east of Almera's house on Baxtorian Falls.", Enemy.BRASSICAN_MAGE),
    KANDARIN_BA_AGILITY_COURSE(HotColdType.MASTER, new WorldPoint(2540, 3548, 0), HotColdArea.KANDARIN, "Inside the Barbarian Agility Course. Completion of Alfred Grimhand's Barcrawl is required.", Enemy.BRASSICAN_MAGE),
    KARAMJA_MUSA_POINT(HotColdType.MASTER, new WorldPoint(2913, 3169, 0), HotColdArea.KARAMJA, "Musa Point, banana plantation.", Enemy.BRASSICAN_MAGE),
    KARAMJA_BRIMHAVEN_FRUIT_TREE(HotColdType.MASTER, new WorldPoint(2782, 3215, 0), HotColdArea.KARAMJA, "Brimhaven, east of the fruit tree patch.", Enemy.BRASSICAN_MAGE),
    KARAMJA_WEST_BRIMHAVEN(HotColdType.MASTER, new WorldPoint(2718, 3167, 0), HotColdArea.KARAMJA, "West of Brimhaven.", Enemy.BRASSICAN_MAGE),
    KARAMJA_GLIDER(HotColdType.MASTER, new WorldPoint(2966, 2976, 0), HotColdArea.KARAMJA, "West of the gnome glider.", Enemy.BRASSICAN_MAGE),
    KARAMJA_KHARAZI_NE(HotColdType.MASTER, new WorldPoint(2904, 2925, 0), HotColdArea.KARAMJA, "North-eastern part of Kharazi Jungle.", Enemy.BRASSICAN_MAGE),
    KARAMJA_KHARAZI_SW(HotColdType.MASTER, new WorldPoint(2786, 2899, 0), HotColdArea.KARAMJA, "South-western part of Kharazi Jungle.", Enemy.ANCIENT_WIZARDS),
    KARAMJA_CRASH_ISLAND(HotColdType.MASTER, new WorldPoint(2909, 2737, 0), HotColdArea.KARAMJA, "Northern part of Crash Island.", Enemy.BRASSICAN_MAGE),
    LUMBRIDGE_COW_FIELD(HotColdType.BEGINNER, new WorldPoint(3174, 3336, 0), HotColdArea.MISTHALIN, "Cow field north of Lumbridge"),
    MISTHALIN_VARROCK_STONE_CIRCLE(HotColdType.MASTER, new WorldPoint(3225, 3356, 0), HotColdArea.MISTHALIN, "South of the stone circle near Varrock's entrance.", Enemy.BRASSICAN_MAGE),
    MISTHALIN_LUMBRIDGE(HotColdType.MASTER, new WorldPoint(3234, 3169, 0), HotColdArea.MISTHALIN, "Just north-west of the Lumbridge Fishing tutor.", Enemy.BRASSICAN_MAGE),
    MISTHALIN_LUMBRIDGE_2(HotColdType.MASTER, new WorldPoint(3169, 3279, 0), HotColdArea.MISTHALIN, "North of the pond between Lumbridge and Draynor Village.", Enemy.BRASSICAN_MAGE),
    MISTHALIN_GERTUDES(HotColdType.MASTER, new WorldPoint(3154, 3421, 0), HotColdArea.MISTHALIN, "North-east of Gertrude's house west of Varrock.", Enemy.BRASSICAN_MAGE),
    MISTHALIN_DRAYNOR_BANK(HotColdType.MASTER, new WorldPoint(3098, 3234, 0), HotColdArea.MISTHALIN, "South of Draynor Village bank.", Enemy.BRASSICAN_MAGE),
    MISTHALIN_LUMBER_YARD(HotColdType.MASTER, new WorldPoint(3301, 3484, 0), HotColdArea.MISTHALIN, "South of Lumber Yard, east of Assistant Serf.", Enemy.BRASSICAN_MAGE),
    MORYTANIA_BURGH_DE_ROTT(HotColdType.MASTER, new WorldPoint(3546, 3252, 0), HotColdArea.MORYTANIA, "In the north-east area of Burgh de Rott, by the reverse-L-shaped ruins.", Enemy.BRASSICAN_MAGE),
    MORYTANIA_DARKMEYER(HotColdType.MASTER, new WorldPoint(3604, 3326, 0), HotColdArea.MORYTANIA, "Southwestern part of Darkmeyer.", Enemy.BRASSICAN_MAGE),
    MORYTANIA_PORT_PHASMATYS(HotColdType.MASTER, new WorldPoint(3611, 3485, 0), HotColdArea.MORYTANIA, "West of Port Phasmatys, south-east of fairy ring ALQ.", Enemy.BRASSICAN_MAGE),
    MORYTANIA_HOLLOWS(HotColdType.MASTER, new WorldPoint(3499, 3421, 0), HotColdArea.MORYTANIA, "Inside The Hollows, south of the bridge which was repaired in a quest.", Enemy.BRASSICAN_MAGE),
    MORYTANIA_SWAMP(HotColdType.MASTER, new WorldPoint(3418, 3372, 0), HotColdArea.MORYTANIA, "Inside the Mort Myre Swamp, north-west of the Nature Grotto.", Enemy.BRASSICAN_MAGE),
    MORYTANIA_HAUNTED_MINE(HotColdType.MASTER, new WorldPoint(3444, 3255, 0), HotColdArea.MORYTANIA, "At Haunted Mine quest start.", Enemy.BRASSICAN_MAGE),
    MORYTANIA_MAUSOLEUM(HotColdType.MASTER, new WorldPoint(3499, 3539, 0), HotColdArea.MORYTANIA, "South of the Mausoleum.", Enemy.BRASSICAN_MAGE),
    MORYTANIA_MOS_LES_HARMLESS(HotColdType.MASTER, new WorldPoint(3740, 3041, 0), HotColdArea.MORYTANIA, "Northern area of Mos Le'Harmless, between the lakes.", Enemy.BRASSICAN_MAGE),
    MORYTANIA_MOS_LES_HARMLESS_BAR(HotColdType.MASTER, new WorldPoint(3666, 2972, 0), HotColdArea.MORYTANIA, "Near Mos Le'Harmless southern bar.", Enemy.BRASSICAN_MAGE),
    MORYTANIA_DRAGONTOOTH_NORTH(HotColdType.MASTER, new WorldPoint(3811, 3569, 0), HotColdArea.MORYTANIA, "Northern part of Dragontooth Island.", Enemy.BRASSICAN_MAGE),
    MORYTANIA_DRAGONTOOTH_SOUTH(HotColdType.MASTER, new WorldPoint(3803, 3532, 0), HotColdArea.MORYTANIA, "Southern part of Dragontooth Island.", Enemy.BRASSICAN_MAGE),
    MORYTANIA_SLEPE_TENTS(HotColdType.MASTER, new WorldPoint(3769, 3383, 0), HotColdArea.MORYTANIA, "North-east of Slepe, near the tents.", Enemy.BRASSICAN_MAGE),
    NORTHEAST_OF_AL_KHARID_MINE(HotColdType.BEGINNER, new WorldPoint(3332, 3313, 0), HotColdArea.MISTHALIN, "Northeast of Al Kharid Mine"),
    VARLAMORE_BAZAAR(HotColdType.MASTER, new WorldPoint(1680, 3107, 0), HotColdArea.VARLAMORE, "In the centre of the Bazaar in Civitas illa Fortis.", Enemy.BRASSICAN_MAGE),
    VARLAMORE_LOCUS_OASIS(HotColdType.MASTER, new WorldPoint(1695, 2990, 0), HotColdArea.VARLAMORE, "Amongst the trees at the Locus Oasis.", Enemy.BRASSICAN_MAGE),
    VARLAMORE_RAINFOREST_CENTRE(HotColdType.MASTER, new WorldPoint(1312, 3108, 0), HotColdArea.VARLAMORE, "In the centre of the Tlati Rainforest.", Enemy.BRASSICAN_MAGE),
    VARLAMORE_SUNSET_COAST(HotColdType.MASTER, new WorldPoint(1534, 2997, 0), HotColdArea.VARLAMORE, "North-east corner of Sunset Coast.", Enemy.ANCIENT_WIZARDS),
    VARLAMORE_VILLA_LUCENS(HotColdType.MASTER, new WorldPoint(1469, 2933, 0), HotColdArea.VARLAMORE, "On the Villa Lucens theatre stage in Aldarin. Completion of Death on the Isle is required.", Enemy.BRASSICAN_MAGE),
    WESTERN_PROVINCE_EAGLES_PEAK(HotColdType.MASTER, new WorldPoint(2297, 3529, 0), HotColdArea.WESTERN_PROVINCE, "North-west of Eagles' Peak.", Enemy.BRASSICAN_MAGE),
    WESTERN_PROVINCE_PISCATORIS(HotColdType.MASTER, new WorldPoint(2334, 3685, 0), HotColdArea.WESTERN_PROVINCE, "Piscatoris Fishing Colony", Enemy.ANCIENT_WIZARDS),
    WESTERN_PROVINCE_PISCATORIS_HUNTER_AREA(HotColdType.MASTER, new WorldPoint(2359, 3564, 0), HotColdArea.WESTERN_PROVINCE, "Eastern part of Piscatoris Hunter area, south-west of the Falconry.", Enemy.BRASSICAN_MAGE),
    WESTERN_PROVINCE_ARANDAR(HotColdType.MASTER, new WorldPoint(2370, 3319, 0), HotColdArea.WESTERN_PROVINCE, "South-west of the crystal gate to Arandar.", Enemy.BRASSICAN_OR_WIZARDS),
    WESTERN_PROVINCE_ELF_CAMP_EAST(HotColdType.MASTER, new WorldPoint(2268, 3242, 0), HotColdArea.WESTERN_PROVINCE, "East of Iorwerth Camp.", Enemy.BRASSICAN_MAGE),
    WESTERN_PROVINCE_ELF_CAMP_NW(HotColdType.MASTER, new WorldPoint(2177, 3282, 0), HotColdArea.WESTERN_PROVINCE, "North-west of Iorwerth Camp.", Enemy.BRASSICAN_MAGE),
    WESTERN_PROVINCE_LLETYA(HotColdType.MASTER, new WorldPoint(2337, 3166, 0), HotColdArea.WESTERN_PROVINCE, "In Lletya.", Enemy.BRASSICAN_MAGE),
    WESTERN_PROVINCE_TYRAS(HotColdType.MASTER, new WorldPoint(2206, 3158, 0), HotColdArea.WESTERN_PROVINCE, "Near Tyras Camp.", Enemy.BRASSICAN_MAGE),
    WESTERN_PROVINCE_ZULANDRA(HotColdType.MASTER, new WorldPoint(2196, 3057, 0), HotColdArea.WESTERN_PROVINCE, "The northern house at Zul-Andra.", Enemy.BRASSICAN_MAGE),
    WILDERNESS_5(HotColdType.MASTER, new WorldPoint(3173, 3556, 0), HotColdArea.WILDERNESS, "North of the Grand Exchange, level 5 Wilderness.", Enemy.ANCIENT_WIZARDS),
    WILDERNESS_12(HotColdType.MASTER, new WorldPoint(3036, 3612, 0), HotColdArea.WILDERNESS, "South-east of the Dark Warriors' Fortress, level 12 Wilderness.", Enemy.ANCIENT_WIZARDS),
    WILDERNESS_20(HotColdType.MASTER, new WorldPoint(3222, 3679, 0), HotColdArea.WILDERNESS, "East of the Corporeal Beast's lair, level 20 Wilderness.", Enemy.ANCIENT_WIZARDS),
    WILDERNESS_27(HotColdType.MASTER, new WorldPoint(3174, 3736, 0), HotColdArea.WILDERNESS, "Inside the Ruins north of the Graveyard of Shadows, level 27 Wilderness.", Enemy.BRASSICAN_MAGE),
    WILDERNESS_28(HotColdType.MASTER, new WorldPoint(3377, 3737, 0), HotColdArea.WILDERNESS, "South-east of wilderness crabs teleport, level 28 Wilderness.", Enemy.BRASSICAN_MAGE),
    WILDERNESS_32(HotColdType.MASTER, new WorldPoint(3311, 3773, 0), HotColdArea.WILDERNESS, "South of the Silk Chasm near the black knights, level 32 Wilderness.", Enemy.ANCIENT_WIZARDS),
    WILDERNESS_35(HotColdType.MASTER, new WorldPoint(3152, 3796, 0), HotColdArea.WILDERNESS, "East of the Wilderness canoe exit, level 35 Wilderness.", Enemy.BRASSICAN_OR_WIZARDS),
    WILDERNESS_37(HotColdType.MASTER, new WorldPoint(2974, 3814, 0), HotColdArea.WILDERNESS, "South-east of the Chaos Temple, level 37 Wilderness.", Enemy.BRASSICAN_MAGE),
    WILDERNESS_38(HotColdType.MASTER, new WorldPoint(3293, 3813, 0), HotColdArea.WILDERNESS, "Among the poison spiders south of Callisto's den, level 38 Wilderness.", Enemy.ANCIENT_WIZARDS),
    WILDERNESS_49(HotColdType.MASTER, new WorldPoint(3136, 3914, 0), HotColdArea.WILDERNESS, "South-west of the Deserted Keep, level 49 Wilderness.", Enemy.BRASSICAN_MAGE),
    WILDERNESS_54(HotColdType.MASTER, new WorldPoint(2981, 3944, 0), HotColdArea.WILDERNESS, "West of the Wilderness Agility Course, level 54 Wilderness.", Enemy.BRASSICAN_MAGE),
    ZEAH_BLASTMINE_BANK(HotColdType.MASTER, new WorldPoint(1504, 3859, 0), HotColdArea.ZEAH, "Next to the bank in the Lovakengj blast mine.", Enemy.BRASSICAN_MAGE),
    ZEAH_BLASTMINE_NORTH(HotColdType.MASTER, new WorldPoint(1488, 3881, 0), HotColdArea.ZEAH, "Northern part of the Lovakengj blast mine.", Enemy.BRASSICAN_MAGE),
    ZEAH_LOVAKITE_FURNACE(HotColdType.MASTER, new WorldPoint(1507, 3819, 0), HotColdArea.ZEAH, "Next to the lovakite furnace in Lovakengj.", Enemy.BRASSICAN_MAGE),
    ZEAH_LOVAKENGJ_MINE(HotColdType.MASTER, new WorldPoint(1477, 3778, 0), HotColdArea.ZEAH, "Next to mithril rock in the Lovakengj mine.", Enemy.BRASSICAN_MAGE),
    ZEAH_SULPHR_MINE(HotColdType.MASTER, new WorldPoint(1428, 3869, 0), HotColdArea.ZEAH, "Western entrance in the Lovakengj sulphur mine. Facemask or Slayer Helmet recommended.", Enemy.BRASSICAN_MAGE),
    ZEAH_SHAYZIEN_BANK(HotColdType.MASTER, new WorldPoint(1498, 3627, 0), HotColdArea.ZEAH, "South-east of the bank in Shayzien Encampment.", Enemy.BRASSICAN_MAGE),
    ZEAH_OVERPASS(HotColdType.MASTER, new WorldPoint(1467, 3714, 0), HotColdArea.ZEAH, "Overpass between Lovakengj and Shayzien.", Enemy.BRASSICAN_MAGE),
    ZEAH_LIZARDMAN(HotColdType.MASTER, new WorldPoint(1490, 3698, 0), HotColdArea.ZEAH, "Within Lizardman Canyon, east of the ladder.", Enemy.ANCIENT_WIZARDS),
    ZEAH_COMBAT_RING(HotColdType.MASTER, new WorldPoint(1557, 3624, 0), HotColdArea.ZEAH, "Shayzien Encampment, south-east of the Combat Ring.", Enemy.BRASSICAN_MAGE),
    ZEAH_SHAYZIEN_BANK_2(HotColdType.MASTER, new WorldPoint(1490, 3602, 0), HotColdArea.ZEAH, "North of the bank in Shayzien.", Enemy.BRASSICAN_MAGE),
    ZEAH_LIBRARY(HotColdType.MASTER, new WorldPoint(1603, 3843, 0), HotColdArea.ZEAH, "North-west of the Arceuus Library.", Enemy.BRASSICAN_MAGE),
    ZEAH_HOUSECHURCH(HotColdType.MASTER, new WorldPoint(1681, 3794, 0), HotColdArea.ZEAH, "By the entrance to the Arceuus church.", Enemy.ANCIENT_WIZARDS),
    ZEAH_DARK_ALTAR(HotColdType.MASTER, new WorldPoint(1698, 3880, 0), HotColdArea.ZEAH, "West of the Dark Altar.", Enemy.BRASSICAN_MAGE),
    ZEAH_ARCEUUS_HOUSE(HotColdType.MASTER, new WorldPoint(1710, 3700, 0), HotColdArea.ZEAH, "By the south-eastern entrance to Arceuus.", Enemy.BRASSICAN_MAGE),
    ZEAH_ESSENCE_MINE(HotColdType.MASTER, new WorldPoint(1762, 3852, 0), HotColdArea.ZEAH, "By the Arceuus essence mine.", Enemy.BRASSICAN_MAGE),
    ZEAH_ESSENCE_MINE_NE(HotColdType.MASTER, new WorldPoint(1773, 3867, 0), HotColdArea.ZEAH, "North-east of the Arceuus essence mine.", Enemy.BRASSICAN_MAGE),
    ZEAH_PISCARILUS_MINE(HotColdType.MASTER, new WorldPoint(1768, 3705, 0), HotColdArea.ZEAH, "South of the Piscarilius mine.", Enemy.ANCIENT_WIZARDS),
    ZEAH_GOLDEN_FIELD_TAVERN(HotColdType.MASTER, new WorldPoint(1718, 3643, 0), HotColdArea.ZEAH, "South of the gravestone in Kingstown.", Enemy.BRASSICAN_MAGE),
    ZEAH_MESS_HALL(HotColdType.MASTER, new WorldPoint(1656, 3621, 0), HotColdArea.ZEAH, "East of the Mess hall.", Enemy.BRASSICAN_MAGE),
    ZEAH_WATSONS_HOUSE(HotColdType.MASTER, new WorldPoint(1653, 3572, 0), HotColdArea.ZEAH, "East of Watson's house.", Enemy.BRASSICAN_MAGE),
    ZEAH_VANNAHS_FARM_STORE(HotColdType.MASTER, new WorldPoint(1807, 3523, 0), HotColdArea.ZEAH, "North of Tithe Farm, next to the pond.", Enemy.BRASSICAN_MAGE),
    ZEAH_FARMING_GUILD_W(HotColdType.MASTER, new WorldPoint(1208, 3736, 0), HotColdArea.ZEAH, "West of the Farming Guild.", Enemy.BRASSICAN_MAGE),
    ZEAH_DAIRY_COW(HotColdType.MASTER, new WorldPoint(1324, 3722, 0), HotColdArea.ZEAH, "North-east of the Kebos Lowlands, east of the dairy cow.", Enemy.BRASSICAN_MAGE),
    ZEAH_CRIMSON_SWIFTS(HotColdType.MASTER, new WorldPoint(1187, 3580, 0), HotColdArea.ZEAH, "South-west of the Kebos Swamp, below the crimson swifts.", Enemy.BRASSICAN_MAGE);

    private final HotColdType type;
    private final WorldPoint worldPoint;
    private final HotColdArea hotColdArea;
    private final String area;
    private final Enemy enemy;

    private HotColdLocation(HotColdType type, WorldPoint worldPoint, HotColdArea hotColdArea, String areaDescription) {
        this(type, worldPoint, hotColdArea, areaDescription, null);
        Preconditions.checkArgument((type == HotColdType.BEGINNER ? 1 : 0) != 0, (Object)"locations without bosses must be beginner");
    }

    public Rectangle getRect() {
        int digRadius = this.isBeginnerClue() ? HotColdTemperature.BEGINNER_VISIBLY_SHAKING.getMaxDistance() : HotColdTemperature.MASTER_VISIBLY_SHAKING.getMaxDistance();
        return new Rectangle(this.worldPoint.getX() - digRadius, this.worldPoint.getY() - digRadius, digRadius * 2 + 1, digRadius * 2 + 1);
    }

    public boolean isBeginnerClue() {
        return this.type == HotColdType.BEGINNER;
    }

    private HotColdLocation(HotColdType type, WorldPoint worldPoint, HotColdArea hotColdArea, String area, Enemy enemy) {
        this.type = type;
        this.worldPoint = worldPoint;
        this.hotColdArea = hotColdArea;
        this.area = area;
        this.enemy = enemy;
    }

    public HotColdType getType() {
        return this.type;
    }

    public WorldPoint getWorldPoint() {
        return this.worldPoint;
    }

    public HotColdArea getHotColdArea() {
        return this.hotColdArea;
    }

    public String getArea() {
        return this.area;
    }

    public Enemy getEnemy() {
        return this.enemy;
    }

    public static enum HotColdType {
        BEGINNER,
        MASTER;

    }
}

