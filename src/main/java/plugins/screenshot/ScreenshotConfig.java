/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.plugins.screenshot;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;
import net.runelite.client.config.Keybind;

@ConfigGroup(value="screenshot")
public interface ScreenshotConfig
extends Config {
    @ConfigSection(name="What to screenshot", description="All the options that select what to screenshot.", position=99)
    public static final String whatSection = "what";

    @ConfigItem(keyName="includeFrame", name="Include client frame", description="Configures whether or not the client frame is included in screenshots.", position=0)
    default public boolean includeFrame() {
        return true;
    }

    @ConfigItem(keyName="displayDate", name="Display date", description="Configures whether or not the report button shows the date the screenshot was taken.", position=1)
    default public boolean displayDate() {
        return true;
    }

    @ConfigItem(keyName="notifyWhenTaken", name="Notify when taken", description="Configures whether or not you are notified when a screenshot has been taken.", position=2)
    default public boolean notifyWhenTaken() {
        return true;
    }

    @ConfigItem(keyName="copyToClipboard", name="Copy to clipboard", description="Copies the saved screenshot to clipboard.", position=4)
    default public boolean copyToClipboard() {
        return false;
    }

    @ConfigItem(keyName="hotkey", name="Screenshot hotkey", description="When you press this key a screenshot will be taken.", position=4)
    default public Keybind hotkey() {
        return Keybind.NOT_SET;
    }

    @ConfigItem(keyName="rewards", name="Screenshot rewards", description="Configures whether screenshots are taken of clues, barrows, and quest completion.", position=3, section="what")
    default public boolean screenshotRewards() {
        return true;
    }

    @ConfigItem(keyName="levels", name="Screenshot levels", description="Configures whether screenshots are taken of level ups.", position=4, section="what")
    default public boolean screenshotLevels() {
        return true;
    }

    @ConfigItem(keyName="kingdom", name="Screenshot kingdom reward", description="Configures whether screenshots are taken of kingdom reward.", position=5, section="what")
    default public boolean screenshotKingdom() {
        return true;
    }

    @ConfigItem(keyName="pets", name="Screenshot pet", description="Configures whether screenshots are taken of receiving pets.", position=6, section="what")
    default public boolean screenshotPet() {
        return true;
    }

    @ConfigItem(keyName="kills", name="Screenshot PvP kills", description="Configures whether or not screenshots are automatically taken of PvP kills.", position=8, section="what")
    default public boolean screenshotKills() {
        return false;
    }

    @ConfigItem(keyName="boss", name="Screenshot boss kills", description="Configures whether or not screenshots are automatically taken of boss kills.", position=9, section="what")
    default public boolean screenshotBossKills() {
        return false;
    }

    @ConfigItem(keyName="playerDeath", name="Screenshot deaths", description="Configures whether or not screenshots are automatically taken when you die.", position=10, section="what")
    default public boolean screenshotPlayerDeath() {
        return false;
    }

    @ConfigItem(keyName="friendDeath", name="Screenshot friend deaths", description="Configures whether or not screenshots are automatically taken when friends or friends chat members die.", position=11, section="what")
    default public boolean screenshotFriendDeath() {
        return false;
    }

    @ConfigItem(keyName="clanDeath", name="Screenshot clan deaths", description="Configures whether or not screenshots are automatically taken when clan members die.", position=12, section="what")
    default public boolean screenshotClanDeath() {
        return false;
    }

    @ConfigItem(keyName="duels", name="Screenshot duels", description="Configures whether or not screenshots are automatically taken of the duel end screen.", position=13, section="what")
    default public boolean screenshotDuels() {
        return false;
    }

    @ConfigItem(keyName="valuableDrop", name="Screenshot valuable drops", description="Configures whether screenshots are automatically taken when you receive a valuable drop.<br>Requires 'Loot drop notifications' to be enabled in the RuneScape settings.", position=14, section="what")
    default public boolean screenshotValuableDrop() {
        return false;
    }

    @ConfigItem(keyName="valuableDropThreshold", name="Valuable threshold", description="The minimum value to save screenshots of valuable drops.<br>Requires 'Minimum item value needed for loot notification' to be set to a lesser or equal value in the RuneScape settings.", position=15, section="what")
    default public int valuableDropThreshold() {
        return 0;
    }

    @ConfigItem(keyName="untradeableDrop", name="Screenshot untradeable drops", description="Configures whether screenshots are automatically taken when you receive an untradeable drop.<br>Requires 'Untradeable loot notifications' to be enabled in the RuneScape settings.", position=16, section="what")
    default public boolean screenshotUntradeableDrop() {
        return false;
    }

    @ConfigItem(keyName="ccKick", name="Screenshot kicks from FC", description="Take a screenshot when you kick a user from a friends chat.", position=17, section="what")
    default public boolean screenshotKick() {
        return false;
    }

    @ConfigItem(keyName="baHighGamble", name="Screenshot BA high gambles", description="Take a screenshot of your reward from a high gamble at Barbarian Assault.", position=18, section="what", hidden=true)
    default public boolean screenshotHighGamble() {
        return false;
    }

    @ConfigItem(keyName="collectionLogEntries", name="Screenshot collection log entries", description="Take a screenshot when completing an entry in the collection log.<br>Requires 'Collection log - New addition notification' to be enabled in the RuneScape settings.", position=19, section="what")
    default public boolean screenshotCollectionLogEntries() {
        return true;
    }

    @ConfigItem(keyName="combatAchievements", name="Screenshot combat achievements", description="Take a screenshot when completing a combat achievement task.", position=20, section="what")
    default public boolean screenshotCombatAchievements() {
        return true;
    }

    @ConfigItem(keyName="screenshotLeagueTasks", name="Screenshot league tasks", description="Take a screenshot when completing a league task.", position=21, section="what")
    default public boolean screenshotLeagueTasks() {
        return true;
    }

    @ConfigItem(keyName="wildernessLootChest", name="Screenshot wilderness loot chest", description="Take a screenshot when opening wilderness loot chest.", position=22, section="what")
    default public boolean screenshotWildernessLootChest() {
        return true;
    }
}

