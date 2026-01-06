/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.runelite.http.api.worlds.World
 *  net.runelite.http.api.worlds.WorldType
 *  okhttp3.HttpUrl
 *  okhttp3.OkHttpClient
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package net.runelite.client.rs;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Queue;
import java.util.Random;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import net.runelite.client.RuneLiteProperties;
import net.runelite.client.game.WorldClient;
import net.runelite.http.api.worlds.World;
import net.runelite.http.api.worlds.WorldType;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class WorldSupplier
implements Supplier<World> {
    private static final Logger log = LoggerFactory.getLogger(WorldSupplier.class);
    private final OkHttpClient okHttpClient;
    private final Random random = new Random(System.nanoTime());
    private final Queue<World> worlds = new ArrayDeque<World>();

    @Override
    public World get() {
        if (!this.worlds.isEmpty()) {
            return this.worlds.poll();
        }
        try {
            List newWorlds = new WorldClient(this.okHttpClient, HttpUrl.get((String)RuneLiteProperties.getApiBase())).lookupWorlds().getWorlds().stream().filter(w -> w.getTypes().isEmpty() || EnumSet.of(WorldType.MEMBERS).equals(w.getTypes())).collect(Collectors.toList());
            Collections.shuffle(newWorlds, this.random);
            this.worlds.addAll(newWorlds.subList(0, 16));
        }
        catch (IOException e) {
            log.warn("Unable to retrieve world list", (Throwable)e);
        }
        while (this.worlds.size() < 2) {
            int id = this.random.nextInt(50) + 1;
            World world = World.builder().id(300 + id).address("oldschool" + id + ".runescape.COM").build();
            this.worlds.add(world);
        }
        return this.worlds.poll();
    }

    public WorldSupplier(OkHttpClient okHttpClient) {
        this.okHttpClient = okHttpClient;
    }
}

