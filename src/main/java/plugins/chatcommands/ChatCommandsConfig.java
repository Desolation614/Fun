/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.plugins.chatcommands;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.Keybind;

@ConfigGroup(value="chatcommands")
public interface ChatCommandsConfig
extends Config {
    @ConfigItem(position=0, keyName="price", name="Price command", description="Configures whether the price command is enabled: !price [item]")
    default public boolean price() {
        return true;
    }

    @ConfigItem(position=1, keyName="lvl", name="Level command", description="Configures whether the level command is enabled: !lvl [skill]")
    default public boolean lvl() {
        return true;
    }

    @ConfigItem(position=2, keyName="clue", name="Clue command", description="Configures whether the clue command is enabled: !clues")
    default public boolean clue() {
        return true;
    }

    @ConfigItem(position=3, keyName="killcount", name="Killcount command", description="Configures whether the killcount command is enabled: !kc [boss]")
    default public boolean killcount() {
        return true;
    }

    @ConfigItem(position=4, keyName="qp", name="QP command", description="Configures whether the quest point command is enabled: !qp")
    default public boolean qp() {
        return true;
    }

    @ConfigItem(position=5, keyName="pb", name="PB command", description="Configures whether the personal best command is enabled: !pb [boss]")
    default public boolean pb() {
        return true;
    }

    @ConfigItem(position=6, keyName="gc", name="GC command", description="Configures whether the Barbarian Assault high gamble count command is enabled: !gc")
    default public boolean gc() {
        return true;
    }

    @ConfigItem(position=7, keyName="duels", name="Duels command", description="Configures whether the duel arena command is enabled: !duels")
    default public boolean duels() {
        return true;
    }

    @ConfigItem(position=8, keyName="bh", name="BH command", description="Configures whether the Bounty Hunter - Hunter command is enabled: !bh")
    default public boolean bh() {
        return true;
    }

    @ConfigItem(position=9, keyName="bhRogue", name="BH Rogue command", description="Configures whether the Bounty Hunter - Rogue command is enabled: !bhrogue")
    default public boolean bhRogue() {
        return true;
    }

    @ConfigItem(position=10, keyName="lms", name="LMS command", description="Configures whether the Last Man Standing command is enabled: !lms")
    default public boolean lms() {
        return true;
    }

    @ConfigItem(position=11, keyName="lp", name="LP command", description="Configures whether the League Points command is enabled: !lp")
    default public boolean lp() {
        return true;
    }

    @ConfigItem(position=12, keyName="sw", name="SW command", description="Configures whether the Soul Wars Zeal command is enabled: !sw")
    default public boolean sw() {
        return true;
    }

    @ConfigItem(position=13, keyName="pets", name="Pets command", description="Configures whether the player pet list command is enabled. Update your pet list by looking at the 'All Pets' tab in the collection log.")
    default public boolean pets() {
        return true;
    }

    @ConfigItem(position=14, keyName="ca", name="CA command", description="Configures whether the combat achievements command is enabled: !ca")
    default public boolean ca() {
        return true;
    }

    @ConfigItem(position=15, keyName="clog", name="CLOG command", description="Configures whether the collection log command is enabled: !clog")
    default public boolean clog() {
        return true;
    }

    @ConfigItem(position=20, keyName="clearSingleWord", name="Clear single word", description="Enable hotkey to clear single word at a time.")
    default public Keybind clearSingleWord() {
        return new Keybind(8, 128);
    }

    @ConfigItem(position=21, keyName="clearEntireChatBox", name="Clear chat box", description="Enable hotkey to clear entire chat box.")
    default public Keybind clearChatBox() {
        return Keybind.NOT_SET;
    }
}

