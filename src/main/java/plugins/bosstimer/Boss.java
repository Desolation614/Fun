/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap$Builder
 */
package net.runelite.client.plugins.bosstimer;

import com.google.common.collect.ImmutableMap;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.Map;
import net.runelite.client.util.RSTimeUnit;

enum Boss {
    GENERAL_GRAARDOR(2215, 90L, ChronoUnit.SECONDS, 12650),
    KRIL_TSUTSAROTH(3129, 90L, ChronoUnit.SECONDS, 12652),
    KREEARRA(3162, 90L, ChronoUnit.SECONDS, 12649),
    COMMANDER_ZILYANA(2205, 90L, ChronoUnit.SECONDS, 12651),
    CALLISTO(6609, 11L, RSTimeUnit.GAME_TICKS, 13178),
    ARTIO(11992, 11L, RSTimeUnit.GAME_TICKS, 13178),
    CHAOS_ELEMENTAL(2054, 14L, RSTimeUnit.GAME_TICKS, 11995),
    CHAOS_FANATIC(6619, 14L, RSTimeUnit.GAME_TICKS, 4675),
    CRAZY_ARCHAEOLOGIST(6618, 9L, ChronoUnit.SECONDS, 11990),
    KING_BLACK_DRAGON(239, 9L, ChronoUnit.SECONDS, 12653),
    SCORPIA(6615, 14L, RSTimeUnit.GAME_TICKS, 13181),
    SCURRIUS(7221, 29L, RSTimeUnit.GAME_TICKS, 28801),
    SCURRIUS_PRIVATE(7222, 29L, RSTimeUnit.GAME_TICKS, 28801),
    VENENATIS(6610, 10L, RSTimeUnit.GAME_TICKS, 13177),
    SPINDEL(11998, 9L, RSTimeUnit.GAME_TICKS, 13177),
    VETION(6612, 15L, RSTimeUnit.GAME_TICKS, 13179),
    CALVARION(11994, 14L, RSTimeUnit.GAME_TICKS, 13179),
    DAGANNOTH_PRIME(2266, 90L, ChronoUnit.SECONDS, 12644),
    DAGANNOTH_REX(2267, 90L, ChronoUnit.SECONDS, 12645),
    DAGANNOTH_SUPREME(2265, 90L, ChronoUnit.SECONDS, 12643),
    CORPOREAL_BEAST(319, 30L, ChronoUnit.SECONDS, 12816),
    GIANT_MOLE(5779, 9000L, ChronoUnit.MILLIS, 12646),
    DERANGED_ARCHAEOLOGIST(7806, 30L, ChronoUnit.SECONDS, 21566),
    CERBERUS(5862, 8400L, ChronoUnit.MILLIS, 13247),
    THERMONUCLEAR_SMOKE_DEVIL(499, 8400L, ChronoUnit.MILLIS, 12648),
    KRAKEN(494, 8400L, ChronoUnit.MILLIS, 12655),
    KALPHITE_QUEEN(965, 30L, ChronoUnit.SECONDS, 12647),
    DUSK(7889, 5L, ChronoUnit.MINUTES, 21748),
    ALCHEMICAL_HYDRA(8622, 25200L, ChronoUnit.MILLIS, 22746),
    SARACHNIS(8713, 16L, RSTimeUnit.GAME_TICKS, 23495),
    ZALCANO(9050, 21600L, ChronoUnit.MILLIS, 23760),
    PHANTOM_MUSPAH(12080, 50L, RSTimeUnit.GAME_TICKS, 27590),
    THE_LEVIATHAN(12214, 30L, RSTimeUnit.GAME_TICKS, 28252),
    ARAXXOR(13669, 15L, RSTimeUnit.GAME_TICKS, 29836, true),
    AMOXLIATL(13685, 28L, RSTimeUnit.GAME_TICKS, 30154),
    HUEYCOATL(14012, 50L, RSTimeUnit.GAME_TICKS, 30152);

    private static final Map<Integer, Boss> bosses;
    private final int id;
    private final Duration spawnTime;
    private final int itemSpriteId;
    private final boolean ignoreDead;

    private Boss(int id, long period, TemporalUnit unit, int itemSpriteId) {
        this(id, period, unit, itemSpriteId, false);
    }

    private Boss(int id, long period, TemporalUnit unit, int itemSpriteId, boolean ignoreDead) {
        this.id = id;
        this.spawnTime = Duration.of(period, unit);
        this.itemSpriteId = itemSpriteId;
        this.ignoreDead = ignoreDead;
    }

    static Boss find(int id) {
        return bosses.get(id);
    }

    public int getId() {
        return this.id;
    }

    public Duration getSpawnTime() {
        return this.spawnTime;
    }

    public int getItemSpriteId() {
        return this.itemSpriteId;
    }

    public boolean isIgnoreDead() {
        return this.ignoreDead;
    }

    static {
        ImmutableMap.Builder builder = new ImmutableMap.Builder();
        for (Boss boss : Boss.values()) {
            builder.put((Object)boss.getId(), (Object)boss);
        }
        bosses = builder.build();
    }
}

