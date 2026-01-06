/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.plugins.banktags;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup(value="banktags")
public interface BankTagsConfig
extends Config {
    @ConfigItem(keyName="useTabs", name="Use tag tabs", description="Enable the ability to add tabs to your bank which allow fast access to tags.", position=1)
    default public boolean tabs() {
        return true;
    }

    @ConfigItem(keyName="rememberTab", name="Remember last tag tab", description="Enable the ability to remember last tag tab when closing/opening the bank.", position=2)
    default public boolean rememberTab() {
        return true;
    }

    @ConfigItem(keyName="removeTabSeparators", name="Remove tab separators", description="Remove the tab separators normally present in tag tabs.", position=3)
    default public boolean removeSeparators() {
        return false;
    }

    @ConfigItem(keyName="preventTagTabDrags", name="Prevent tag tab item dragging", description="Ignore dragged items to prevent unwanted bank item reordering.", position=4)
    default public boolean preventTagTabDrags() {
        return false;
    }

    @ConfigItem(keyName="position", name="", description="", hidden=true)
    default public int position() {
        return 0;
    }

    @ConfigItem(keyName="position", name="", description="")
    public void position(int var1);

    @ConfigItem(keyName="tab", name="", description="", hidden=true)
    default public String tab() {
        return "";
    }

    @ConfigItem(keyName="tab", name="", description="")
    public void tab(String var1);
}

