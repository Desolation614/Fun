/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.plugins.kingdomofmiscellania;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.Range;

@ConfigGroup(value="kingdomofmiscellania")
public interface KingdomConfig
extends Config {
    public static final String CONFIG_GROUP_NAME = "kingdomofmiscellania";
    public static final int MAX_COFFER = 7500000;
    public static final int MAX_APPROVAL_PERCENT = 100;

    @ConfigItem(position=1, keyName="sendNotifications", name="Send notifications", description="Send chat notifications upon login showing current estimated coffer and approval.")
    default public boolean shouldSendNotifications() {
        return false;
    }

    @Range(max=7500000)
    @ConfigItem(position=2, keyName="cofferThreshold", name="Coffer threshold", description="Send notifications if coffer is below this value.")
    default public int getCofferThreshold() {
        return 7500000;
    }

    @Range(max=100)
    @ConfigItem(position=3, keyName="approvalThreshold", name="Approval threshold", description="Send notifications if approval percentage is below this value.")
    default public int getApprovalThreshold() {
        return 100;
    }
}

