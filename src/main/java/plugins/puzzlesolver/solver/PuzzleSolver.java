/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Stopwatch
 */
package net.runelite.client.plugins.puzzlesolver.solver;

import com.google.common.base.Stopwatch;
import java.time.Duration;
import java.util.List;
import net.runelite.client.plugins.puzzlesolver.solver.PuzzleState;
import net.runelite.client.plugins.puzzlesolver.solver.pathfinding.Pathfinder;

public class PuzzleSolver
implements Runnable {
    public static final int DIMENSION = 5;
    public static final int BLANK_TILE_VALUE = -1;
    private static final Duration MAX_WAIT_DURATION = Duration.ofMillis(1500L);
    private final Pathfinder pathfinder;
    private final PuzzleState startState;
    private List<PuzzleState> solution;
    private int position;
    private Stopwatch stopwatch;
    private boolean failed = false;

    public PuzzleSolver(Pathfinder pathfinder, PuzzleState startState) {
        this.pathfinder = pathfinder;
        this.startState = startState;
    }

    public PuzzleState getStep(int stepIdx) {
        return this.solution.get(stepIdx);
    }

    public int getStepCount() {
        return this.solution.size();
    }

    public boolean hasSolution() {
        return this.solution != null;
    }

    public int getPosition() {
        return this.position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public boolean hasExceededWaitDuration() {
        return this.stopwatch != null && this.stopwatch.elapsed().compareTo(MAX_WAIT_DURATION) > 0;
    }

    public boolean hasFailed() {
        return this.failed;
    }

    @Override
    public void run() {
        this.stopwatch = Stopwatch.createStarted();
        this.solution = this.pathfinder.computePath(this.startState);
        this.failed = this.solution == null;
    }
}

