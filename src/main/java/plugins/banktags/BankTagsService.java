/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package net.runelite.client.plugins.banktags;

import javax.annotation.Nullable;
import net.runelite.client.plugins.banktags.BankTag;
import net.runelite.client.plugins.banktags.tabs.Layout;

public interface BankTagsService {
    public static final int OPTION_ALLOW_MODIFICATIONS = 1;
    public static final int OPTION_HIDE_TAG_NAME = 2;
    public static final int OPTION_NO_LAYOUT = 4;

    public void openBankTag(String var1, int var2);

    public void closeBankTag();

    @Nullable
    public String getActiveTag();

    @Nullable
    public BankTag getActiveBankTag();

    @Nullable
    public Layout getActiveLayout();
}

