/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.plugins.ferox;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;
import net.runelite.client.config.Keybind;

@ConfigGroup(value="ferox")
public interface FeroxConfig
extends Config {
    @ConfigSection(name="Shortcuts", description="Settings for keybind shortcuts.", position=0)
    public static final String shortcutsSection = "Shortcuts";
    @ConfigSection(name="Weapon Upgrades", description="Settings for upgraded weapon overlays.", position=50)
    public static final String weaponUpgradesSection = "Weapon Upgrades";
    @ConfigSection(name="Clue Scroll Step", description="Settings for displaying clue scroll step.", position=60)
    public static final String clueScrollStepSection = "Clue Scroll Step";

    @ConfigItem(keyName="teleporterKeybind", name="Teleporter shortcut", description="Keyboard shortcut for opening the teleporter.", position=1, section="Shortcuts")
    default public Keybind teleporterKeybind() {
        return new Keybind(84, 128);
    }

    @ConfigItem(keyName="lastTeleportKeybind", name="Last Teleport shortcut", description="Keyboard shortcut for teleporting to your last teleport location.", position=2, section="Shortcuts")
    default public Keybind lastTeleportKeybind() {
        return new Keybind(82, 128);
    }

    @ConfigItem(keyName="spawnKeybind", name="Spawn shortcut", description="Keyboard shortcut for ::spawn.", position=3, section="Shortcuts")
    default public Keybind spawnKeybind() {
        return new Keybind(83, 128);
    }

    @ConfigItem(keyName="presetsKeybind", name="Presets shortcut", description="Keyboard shortcut for ::presets.", position=4, section="Shortcuts")
    default public Keybind presetsKeybind() {
        return new Keybind(71, 128);
    }

    @ConfigItem(keyName="lastPresetKeybind", name="Last Preset shortcut", description="Keyboard shortcut for loading your most recent preset.", position=5, section="Shortcuts")
    default public Keybind lastPresetKeybind() {
        return new Keybind(76, 128);
    }

    @ConfigItem(keyName="bankKeybind", name="Bank shortcut", description="Keyboard shortcut for ::b.", position=6, section="Shortcuts")
    default public Keybind bankKeybind() {
        return new Keybind(66, 128);
    }

    @ConfigItem(keyName="homeKeybind", name="Teleport Home shortcut", description="Keyboard shortcut for ::home.", position=7, section="Shortcuts")
    default public Keybind homeKeybind() {
        return new Keybind(72, 128);
    }

    @ConfigItem(keyName="displayTierColors", name="Display tier glow underlays", description="Displays glow underlays corresponding to each tier.", position=51, section="Weapon Upgrades")
    default public boolean displayTierColors() {
        return true;
    }

    @ConfigItem(keyName="displayTierText", name="Display tier numerals", description="Displays the numeral text that corresponds to each tier.", position=52, section="Weapon Upgrades")
    default public boolean displayTierText() {
        return true;
    }

    @ConfigItem(keyName="displayWildernessClueScrollStep", name="Display wilderness clue scroll step", description="Displays a skull icon over any clue scroll step located in the Wilderness.", position=61, section="Clue Scroll Step")
    default public boolean displayWildernessClueScrollStep() {
        return true;
    }
}

