/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.plugins.twitch.irc;

import java.util.Map;

public interface TwitchListener {
    public void privmsg(String var1, Map<String, String> var2, String var3);

    public void roomstate(Map<String, String> var1);

    public void usernotice(Map<String, String> var1, String var2);
}

