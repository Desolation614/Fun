/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.plugins.kourendlibrary;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup(value="kourendLibrary")
public interface KourendLibraryConfig
extends Config {
    public static final String GROUP_KEY = "kourendLibrary";

    @ConfigItem(keyName="hideButton", name="Hide when outside of the library", description="Don't show the button in the sidebar when you're not in the library.")
    default public boolean hideButton() {
        return true;
    }

    @ConfigItem(keyName="hideDuplicateBook", name="Hide duplicate book", description="Don't show the duplicate book locations in the library.")
    default public boolean hideDuplicateBook() {
        return true;
    }

    @ConfigItem(keyName="alwaysShowVarlamoreEnvoy", name="Show Varlamore envoy", description="Varlamore envoy is only needed during the depths of despair, and is never asked for.")
    default public boolean alwaysShowVarlamoreEnvoy() {
        return false;
    }

    @ConfigItem(keyName="showTutorialOverlay", name="Show tutorial overlay", description="Whether to show an overlay to help understand how to use the plugin.")
    default public boolean showTutorialOverlay() {
        return true;
    }

    @ConfigItem(keyName="showTargetHintArrow", name="Show target book arrow", description="Show a hint arrow pointing to the target bookcase.")
    default public boolean showTargetHintArrow() {
        return true;
    }
}

