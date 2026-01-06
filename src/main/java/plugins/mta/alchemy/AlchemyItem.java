/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.plugins.mta.alchemy;

enum AlchemyItem {
    LEATHER_BOOTS("Leather Boots", 6893),
    ADAMANT_KITESHIELD("Adamant Kiteshield", 6894),
    ADAMANT_MED_HELM("Helm", 6895),
    EMERALD("Emerald", 6896),
    RUNE_LONGSWORD("Rune Longsword", 6897),
    EMPTY("", -1),
    UNKNOWN("Unknown", 7542);

    private final int id;
    private final String name;

    private AlchemyItem(String name, int id) {
        this.id = id;
        this.name = name;
    }

    public static AlchemyItem find(String item) {
        for (AlchemyItem alchemyItem : AlchemyItem.values()) {
            if (!item.toLowerCase().contains(alchemyItem.name.toLowerCase())) continue;
            return alchemyItem;
        }
        return null;
    }

    public int getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }
}

