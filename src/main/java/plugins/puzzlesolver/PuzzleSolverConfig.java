/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.plugins.puzzlesolver;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup(value="puzzlesolver")
public interface PuzzleSolverConfig
extends Config {
    @ConfigItem(keyName="displaySolution", name="Display solution", description="Display a solution to the puzzle.")
    default public boolean displaySolution() {
        return true;
    }

    @ConfigItem(keyName="displayRemainingMoves", name="Display remaining moves", description="Add a text line above puzzle boxes displaying the amount of remaining moves.")
    default public boolean displayRemainingMoves() {
        return true;
    }

    @ConfigItem(keyName="drawDots", name="Draw dots instead of arrows", description="Draw dots increasing in size instead of arrows for the solution.")
    default public boolean drawDots() {
        return false;
    }
}

