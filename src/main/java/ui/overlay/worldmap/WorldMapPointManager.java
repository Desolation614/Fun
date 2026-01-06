/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.inject.Singleton
 */
package net.runelite.client.ui.overlay.worldmap;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Predicate;
import javax.inject.Singleton;
import net.runelite.client.ui.overlay.worldmap.WorldMapPoint;

@Singleton
public class WorldMapPointManager {
    private final List<WorldMapPoint> worldMapPoints = new CopyOnWriteArrayList<WorldMapPoint>();

    public void add(WorldMapPoint worldMapPoint) {
        this.worldMapPoints.add(worldMapPoint);
    }

    public void remove(WorldMapPoint worldMapPoint) {
        this.worldMapPoints.remove(worldMapPoint);
    }

    public void removeIf(Predicate<WorldMapPoint> filter) {
        this.worldMapPoints.removeIf(filter);
    }

    List<WorldMapPoint> getWorldMapPoints() {
        return this.worldMapPoints;
    }
}

