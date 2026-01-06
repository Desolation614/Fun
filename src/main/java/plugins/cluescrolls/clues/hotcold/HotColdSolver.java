/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  net.runelite.api.coords.WorldPoint
 */
package net.runelite.client.plugins.cluescrolls.clues.hotcold;

import java.awt.Rectangle;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.cluescrolls.clues.hotcold.HotColdLocation;
import net.runelite.client.plugins.cluescrolls.clues.hotcold.HotColdTemperature;
import net.runelite.client.plugins.cluescrolls.clues.hotcold.HotColdTemperatureChange;

public class HotColdSolver {
    private final Set<HotColdLocation> possibleLocations;
    @Nullable
    private WorldPoint lastWorldPoint;

    public HotColdSolver(Set<HotColdLocation> possibleLocations) {
        this.possibleLocations = possibleLocations;
    }

    public Set<HotColdLocation> signal(@Nonnull WorldPoint worldPoint, @Nonnull HotColdTemperature temperature, @Nullable HotColdTemperatureChange temperatureChange) {
        int maxSquaresAway = temperature.getMaxDistance();
        int minSquaresAway = temperature.getMinDistance();
        Rectangle maxDistanceArea = new Rectangle(worldPoint.getX() - maxSquaresAway, worldPoint.getY() - maxSquaresAway, 2 * maxSquaresAway + 1, 2 * maxSquaresAway + 1);
        Rectangle minDistanceArea = new Rectangle(worldPoint.getX() - minSquaresAway, worldPoint.getY() - minSquaresAway, 2 * minSquaresAway + 1, 2 * minSquaresAway + 1);
        this.possibleLocations.removeIf(entry -> minDistanceArea.contains(entry.getRect()) || !maxDistanceArea.intersects(entry.getRect()));
        if (this.lastWorldPoint != null && temperatureChange != null) {
            switch (temperatureChange) {
                case COLDER: {
                    this.possibleLocations.removeIf(location -> {
                        WorldPoint locationPoint = location.getWorldPoint();
                        return locationPoint.distanceTo2D(worldPoint) <= locationPoint.distanceTo2D(this.lastWorldPoint);
                    });
                    break;
                }
                case WARMER: {
                    this.possibleLocations.removeIf(location -> {
                        WorldPoint locationPoint = location.getWorldPoint();
                        return locationPoint.distanceTo2D(worldPoint) >= locationPoint.distanceTo2D(this.lastWorldPoint);
                    });
                    break;
                }
                case SAME: {
                    this.possibleLocations.removeIf(location -> {
                        WorldPoint locationPoint = location.getWorldPoint();
                        return locationPoint.distanceTo2D(worldPoint) != locationPoint.distanceTo2D(this.lastWorldPoint);
                    });
                }
            }
        }
        this.lastWorldPoint = worldPoint;
        return this.getPossibleLocations();
    }

    public Set<HotColdLocation> getPossibleLocations() {
        return this.possibleLocations;
    }

    @Nullable
    public WorldPoint getLastWorldPoint() {
        return this.lastWorldPoint;
    }
}

