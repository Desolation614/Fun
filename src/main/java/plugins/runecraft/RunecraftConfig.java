/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.plugins.runecraft;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;
import net.runelite.client.config.Notification;

@ConfigGroup(value="runecraft")
public interface RunecraftConfig
extends Config {
    public static final String GROUP = "runecraft";
    @ConfigSection(name="Rift settings", description="Abyss rift overlay settings.", position=99)
    public static final String riftSection = "rifts";

    @ConfigItem(keyName="showPouch", name="Show pouch count", description="Configures whether the pouch essence count is displayed.", position=1)
    default public boolean showPouch() {
        return true;
    }

    @ConfigItem(keyName="pouchDegrade", name="Show pouch fills", description="Configures whether the pouch durability is shown as an approximate number of fills remaining.", position=2)
    default public boolean pouchDegrade() {
        return true;
    }

    @ConfigItem(keyName="showRifts", name="Show rifts in abyss", description="Configures whether the rifts in the abyss will be displayed.", position=2, section="rifts")
    default public boolean showRifts() {
        return true;
    }

    @ConfigItem(keyName="showClickBox", name="Show rift click box", description="Configures whether to display the click box of the rift.", position=3, section="rifts")
    default public boolean showClickBox() {
        return true;
    }

    @ConfigItem(keyName="showAir", name="Show air rift", description="Configures whether to display the air rift.", position=4, section="rifts")
    default public boolean showAir() {
        return true;
    }

    @ConfigItem(keyName="showBlood", name="Show blood rift", description="Configures whether to display the blood rift.", position=5, section="rifts")
    default public boolean showBlood() {
        return true;
    }

    @ConfigItem(keyName="showBody", name="Show body rift", description="Configures whether to display the body rift.", position=6, section="rifts")
    default public boolean showBody() {
        return true;
    }

    @ConfigItem(keyName="showChaos", name="Show chaos rift", description="Configures whether to display the chaos rift.", position=7, section="rifts")
    default public boolean showChaos() {
        return true;
    }

    @ConfigItem(keyName="showCosmic", name="Show cosmic rift", description="Configures whether to display the cosmic rift.", position=8, section="rifts")
    default public boolean showCosmic() {
        return true;
    }

    @ConfigItem(keyName="showDeath", name="Show death rift", description="Configures whether to display the death rift.", position=9, section="rifts")
    default public boolean showDeath() {
        return true;
    }

    @ConfigItem(keyName="showEarth", name="Show earth rift", description="Configures whether to display the earth rift.", position=10, section="rifts")
    default public boolean showEarth() {
        return true;
    }

    @ConfigItem(keyName="showFire", name="Show fire rift", description="Configures whether to display the fire rift.", position=11, section="rifts")
    default public boolean showFire() {
        return true;
    }

    @ConfigItem(keyName="showLaw", name="Show law rift", description="Configures whether to display the law rift.", position=12, section="rifts")
    default public boolean showLaw() {
        return true;
    }

    @ConfigItem(keyName="showMind", name="Show mind rift", description="Configures whether to display the mind rift.", position=13, section="rifts")
    default public boolean showMind() {
        return true;
    }

    @ConfigItem(keyName="showNature", name="Show nature rift", description="Configures whether to display the nature rift.", position=14, section="rifts")
    default public boolean showNature() {
        return true;
    }

    @ConfigItem(keyName="showSoul", name="Show soul rift", description="Configures whether to display the soul rift.", position=15, section="rifts")
    default public boolean showSoul() {
        return true;
    }

    @ConfigItem(keyName="showWater", name="Show water rift", description="Configures whether to display the water rift.", position=16, section="rifts")
    default public boolean showWater() {
        return true;
    }

    @ConfigItem(keyName="hightlightDarkMage", name="Highlight Dark mage NPC", description="Configures whether to highlight the Dark mage when pouches are degraded.", position=18)
    default public boolean hightlightDarkMage() {
        return true;
    }

    @ConfigItem(keyName="degradingNotification", name="Notify when pouch degrades", description="Send a notification when a pouch degrades.", position=19)
    default public Notification degradingNotification() {
        return Notification.ON;
    }
}

