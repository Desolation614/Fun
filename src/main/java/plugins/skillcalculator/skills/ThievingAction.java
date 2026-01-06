/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.plugins.skillcalculator.skills;

import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.skillcalculator.skills.NamedSkillAction;

public enum ThievingAction implements NamedSkillAction
{
    MAN_OR_WOMAN("Man / Woman", 1, 8.0f, 3241),
    WINTER_SQIRKJUICE("Winter Sq'irkjuice", 1, 350.0f, 10851),
    VEGETABLE_STALL("Vegetable Stall", 2, 10.0f, 1965),
    CAKE_STALL("Cake Stall", 5, 16.0f, 1891),
    MONKEY_FOOD_STALL("Monkey Food Stall", 5, 16.0f, 1963),
    TEA_STALL("Tea Stall", 5, 16.0f, 4242),
    CRAFTING_STALL("Crafting Stall", 5, 20.0f, 5601),
    MONKEY_GENERAL_STALL("Monkey General Stall", 5, 25.0f, 1931),
    FARMER("Farmer", 10, 14.5f, 3243),
    CHEST_10_COINS("Chest (10 Coins)", 13, 7.8f, 617),
    HAM_MEMBER("H.A.M. Member", 15, 22.2f, 4297),
    SILK_STALL("Silk Stall", 20, 24.0f, 950),
    WINE_STALL("Wine Stall", 22, 27.0f, 7919),
    WARRIOR_WOMEN_OR_AL_KHARID_WARRIOR("Warrior Women / Al-Kharid Warrior", 25, 26.0f, 3245),
    FRUIT_STALL("Fruit Stall", 25, 28.5f, 464),
    SPRING_SQIRKJUICE("Spring Sq'irkjuice", 25, 1350.0f, 10848),
    SEED_STALL("Seed Stall", 27, 10.0f, 5318),
    NATURE_RUNE_CHEST("Nature Rune Chest", 28, 25.0f, 561),
    ISLE_OF_SOULS_CHEST("Isle of Souls Dungeon Chest", 28, 150.0f, 25244),
    ROGUE("Rogue", 32, 36.5f, 3247),
    FUR_STALL("Fur Stall", 35, 45.0f, 958),
    CAVE_GOBLIN("Cave Goblin", 36, 40.0f, 10998),
    MASTER_FARMER("Master Farmer", 38, 43.0f, 5068),
    GUARD("Guard", 40, 46.8f, 3249),
    FISH_STALL("Fish Stall", 42, 42.0f, 331),
    CHEST_50_COINS("Chest (50 Coins)", 43, 125.0f, 617),
    BEARDED_POLLNIVNIAN_BANDIT("Bearded Pollnivnian Bandit", 45, 65.0f, 6782),
    FREMENNIK_CITIZEN("Fremennik Citizen", 45, 65.0f, 3686),
    AUTUMN_SQIRKJUICE("Autumn Sq'irkjuice", 45, 2350.0f, 10850),
    CHEST_STEEL_ARROWTIPS("Chest (Steel Arrowtips)", 47, 150.0f, 41),
    CROSSBOW_STALL("Crossbow Stall", 49, 52.0f, 837),
    WALL_SAFE("Wall Safe", 50, 70.0f, 5560),
    WEALTHY_CITIZEN("Wealthy Citizen", 50, 96.0f, 28821),
    SILVER_STALL("Silver Stall", 50, 205.0f, 2355),
    DORGESH_KAAN_AVERAGE_CHEST("Dorgesh-Kaan Average Chest", 52, 200.0f, 4537),
    DESERT_BANDIT("Desert Bandit", 53, 79.4f, 4625),
    KNIGHT("Knight", 55, 84.3f, 3251),
    POLLNIVNIAN_BANDIT("Pollnivnian Bandit", 55, 84.3f, 6781),
    STONE_CHEST("Stone Chest", 64, 280.0f, 13383),
    MAGIC_STALL("Magic Stall", 65, 90.0f, 6422),
    SPICE_STALL("Spice Stall", 65, 92.0f, 2007),
    MENAPHITE_THUG("Menaphite Thug", 65, 137.5f, 6780),
    YANILLE_WATCHMAN("Yanille Watchman", 65, 137.5f, 3253),
    SCIMITAR_STALL("Scimitar Stall", 65, 210.0f, 1325),
    SUMMER_SQIRKJUICE("Summer Sq'irkjuice", 65, 3000.0f, 10849),
    PALADIN("Paladin", 70, 131.8f, 3255),
    GNOME("Gnome", 75, 133.5f, 3257),
    GEM_STALL("Gem Stall", 75, 408.0f, 1607),
    DORGESH_KAAN_RICH_CHEST("Dorgesh-Kaan Rich Chest", 78, 650.0f, 5013),
    HERO("Hero", 80, 163.3f, 3259),
    VYRE("Vyre", 82, 306.9f, 24702),
    ORE_STALL("Ore Stall", 82, 350.0f, 451),
    ROGUES_CASTLE_CHEST("Wilderness Rogues' Chest", 84, 701.7f, 1615),
    ELF("Elf", 85, 353.3f, 6105),
    TZHAAR_HUR("TzHaar-Hur", 90, 103.4f, 21278);

    private final String name;
    private final int level;
    private final float xp;
    private final int icon;

    @Override
    public boolean isMembers(ItemManager itemManager) {
        return true;
    }

    private ThievingAction(String name, int level, float xp, int icon) {
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

