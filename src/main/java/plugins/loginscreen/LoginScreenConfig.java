/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.plugins.loginscreen;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.plugins.loginscreen.LoginScreenOverride;

@ConfigGroup(value="loginscreen")
public interface LoginScreenConfig
extends Config {
    @ConfigItem(keyName="syncusername", name="Sync username", description="Syncs the username that is currently remembered between computers.")
    default public boolean syncUsername() {
        return true;
    }

    @ConfigItem(keyName="pasteenabled", name="Ctrl-V paste", description="Enables Ctrl-V pasting on the login screen.")
    default public boolean pasteEnabled() {
        return false;
    }

    @ConfigItem(keyName="username", name="", description="", hidden=true)
    default public String username() {
        return "";
    }

    @ConfigItem(keyName="username", name="", description="")
    public void username(String var1);

    @ConfigItem(keyName="loginScreen", name="Custom background", description="Force the login screen to use an image from the past instead of the current one.")
    default public LoginScreenOverride loginScreen() {
        return LoginScreenOverride.OFF;
    }

    @ConfigItem(keyName="showLoginFire", name="Display fire", description="Whether or not the fire in the braziers at the sides of the login screen should be on fire.")
    default public boolean showLoginFire() {
        return true;
    }
}

