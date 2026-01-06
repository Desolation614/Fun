/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.plugins.loottracker;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;
import net.runelite.client.plugins.loottracker.LootTrackerPriceType;

@ConfigGroup(value="loottracker")
public interface LootTrackerConfig
extends Config {
    public static final String GROUP = "loottracker";
    @ConfigSection(name="Ignored entries", description="The ignored items and sources.", position=-2, closedByDefault=true)
    public static final String ignored = "ignored";

    @ConfigItem(keyName="ignoredItems", name="Ignored items", description="Configures which items should be ignored when calculating loot prices.", section="ignored")
    default public String getIgnoredItems() {
        return "";
    }

    @ConfigItem(keyName="ignoredItems", name="", description="")
    public void setIgnoredItems(String var1);

    @ConfigItem(keyName="priceType", name="Price type", description="What type of price to use for calculating value.")
    default public LootTrackerPriceType priceType() {
        return LootTrackerPriceType.GRAND_EXCHANGE;
    }

    @ConfigItem(keyName="showPriceType", name="Show price type", description="Whether to show a GE: or HA: next to the total values in the tracker.")
    default public boolean showPriceType() {
        return false;
    }

    @ConfigItem(keyName="syncPanel", name="Remember loot", description="Saves loot between client sessions.")
    default public boolean syncPanel() {
        return true;
    }

    @ConfigItem(keyName="ignoredEvents", name="Ignored loot sources", description="Hide specific NPCs or sources of loot in the loot tracker (e.g., goblin, barrows chest, H.A.M. member).", section="ignored")
    default public String getIgnoredEvents() {
        return "";
    }

    @ConfigItem(keyName="ignoredEvents", name="", description="")
    public void setIgnoredEvents(String var1);

    @ConfigItem(keyName="npcKillChatMessage", name="Show chat message for NPC kills", description="Adds a chat message with monster name and kill value when receiving loot from an NPC kill.")
    default public boolean npcKillChatMessage() {
        return false;
    }

    @ConfigItem(keyName="pvpKillChatMessage", name="Show chat message for PvP kills", description="Adds a chat message with player name and kill value when receiving loot from a player kill.")
    default public boolean pvpKillChatMessage() {
        return false;
    }

    @ConfigItem(keyName="showRaidsLootValue", name="Show chat message for raids loot", description="Adds a chat message that displays the value of your loot at the end of the raid.")
    default public boolean showRaidsLootValue() {
        return true;
    }
}

