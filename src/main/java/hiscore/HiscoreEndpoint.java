/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.runelite.api.WorldType
 *  okhttp3.HttpUrl
 */
package net.runelite.client.hiscore;

import java.util.Set;
import net.runelite.api.WorldType;
import okhttp3.HttpUrl;

public enum HiscoreEndpoint {
    NORMAL("Normal", "https://ferox.ps/api/hiscores"),
    IRONMAN("Ironman", "https://ferox.ps/api/hiscores"),
    HARDCORE_IRONMAN("Hardcore Ironman", "https://ferox.ps/api/hiscores"),
    ULTIMATE_IRONMAN("Ultimate Ironman", "https://ferox.ps/api/hiscores"),
    DEADMAN("Deadman", "https://ferox.ps/api/hiscores"),
    LEAGUE("Leagues", "https://ferox.ps/api/hiscores"),
    TOURNAMENT("Tournament", "https://ferox.ps/api/hiscores"),
    FRESH_START_WORLD("Fresh Start", "https://ferox.ps/api/hiscores"),
    PURE("1 Defence Pure", "https://ferox.ps/api/hiscores"),
    LEVEL_3_SKILLER("Level 3 Skiller", "https://ferox.ps/api/hiscores");

    private final String name;
    private final HttpUrl hiscoreURL;

    private HiscoreEndpoint(String name, String hiscoreURL) {
        this.name = name;
        this.hiscoreURL = HttpUrl.get((String)hiscoreURL);
    }

    public static HiscoreEndpoint fromWorldTypes(Set<WorldType> worldTypes) {
        if (worldTypes.contains(WorldType.SEASONAL)) {
            return LEAGUE;
        }
        if (worldTypes.contains(WorldType.TOURNAMENT_WORLD)) {
            return TOURNAMENT;
        }
        if (worldTypes.contains(WorldType.DEADMAN)) {
            return DEADMAN;
        }
        if (worldTypes.contains(WorldType.FRESH_START_WORLD)) {
            return FRESH_START_WORLD;
        }
        return NORMAL;
    }

    public String getName() {
        return this.name;
    }

    public HttpUrl getHiscoreURL() {
        return this.hiscoreURL;
    }
}

