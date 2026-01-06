/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.inject.Inject
 *  net.runelite.api.Client
 *  net.runelite.api.GameState
 *  net.runelite.api.Item
 *  net.runelite.api.ItemContainer
 *  net.runelite.api.Point
 *  net.runelite.api.widgets.Widget
 */
package net.runelite.client.plugins.puzzlesolver;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.Item;
import net.runelite.api.ItemContainer;
import net.runelite.api.Point;
import net.runelite.api.widgets.Widget;
import net.runelite.client.game.SpriteManager;
import net.runelite.client.plugins.puzzlesolver.PuzzleSolverConfig;
import net.runelite.client.plugins.puzzlesolver.solver.PuzzleSolver;
import net.runelite.client.plugins.puzzlesolver.solver.PuzzleState;
import net.runelite.client.plugins.puzzlesolver.solver.heuristics.ManhattanDistance;
import net.runelite.client.plugins.puzzlesolver.solver.pathfinding.IDAStar;
import net.runelite.client.plugins.puzzlesolver.solver.pathfinding.IDAStarMM;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayUtil;
import net.runelite.client.ui.overlay.components.BackgroundComponent;
import net.runelite.client.ui.overlay.components.TextComponent;
import net.runelite.client.util.ImageUtil;

public class PuzzleSolverOverlay
extends Overlay {
    private static final int INFO_BOX_WIDTH = 100;
    private static final int INFO_BOX_OFFSET_Y = 50;
    private static final int INFO_BOX_TOP_BORDER = 2;
    private static final int INFO_BOX_BOTTOM_BORDER = 2;
    private static final int PUZZLE_TILE_SIZE = 39;
    private static final int DOT_MARKER_SIZE = 16;
    private final Client client;
    private final PuzzleSolverConfig config;
    private final ScheduledExecutorService executorService;
    private final SpriteManager spriteManager;
    private PuzzleSolver solver;
    private Future<?> solverFuture;
    private int[] cachedItems;
    private BufferedImage upArrow;
    private BufferedImage leftArrow;
    private BufferedImage rightArrow;

    @Inject
    public PuzzleSolverOverlay(Client client, PuzzleSolverConfig config, ScheduledExecutorService executorService, SpriteManager spriteManager) {
        this.setPosition(OverlayPosition.DYNAMIC);
        this.setPriority(0.75f);
        this.setLayer(OverlayLayer.ABOVE_WIDGETS);
        this.client = client;
        this.config = config;
        this.executorService = executorService;
        this.spriteManager = spriteManager;
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        Widget puzzleBox;
        if (!this.config.displaySolution() && !this.config.displayRemainingMoves() || this.client.getGameState() != GameState.LOGGED_IN) {
            return null;
        }
        boolean useNormalSolver = true;
        ItemContainer container = this.client.getItemContainer(140);
        if (container == null) {
            useNormalSolver = false;
            container = this.client.getItemContainer(221);
            if (container == null) {
                return null;
            }
        }
        if ((puzzleBox = this.client.getWidget(20054020)) == null) {
            return null;
        }
        Point puzzleBoxLocation = puzzleBox.getCanvasLocation();
        Object infoString = "Solving..";
        int[] itemIds = this.getItemIds(container, useNormalSolver);
        boolean shouldCache = false;
        if (this.solver != null) {
            if (this.solver.hasFailed()) {
                infoString = "The puzzle could not be solved";
            } else if (this.solver.hasSolution()) {
                PuzzleState currentState;
                int j;
                int i;
                boolean foundPosition = false;
                for (i = 0; i < 6 && (j = this.solver.getPosition() + i) != this.solver.getStepCount(); ++i) {
                    currentState = this.solver.getStep(j);
                    if (currentState == null || !currentState.hasPieces(itemIds)) continue;
                    foundPosition = true;
                    this.solver.setPosition(j);
                    if (i <= 0) break;
                    shouldCache = true;
                    break;
                }
                if (!foundPosition) {
                    for (i = 1; i < 6 && (j = this.solver.getPosition() - i) >= 0; ++i) {
                        currentState = this.solver.getStep(j);
                        if (currentState == null || !currentState.hasPieces(itemIds)) continue;
                        foundPosition = true;
                        shouldCache = true;
                        this.solver.setPosition(j);
                        break;
                    }
                }
                if (foundPosition) {
                    int stepsLeft = this.solver.getStepCount() - this.solver.getPosition() - 1;
                    infoString = stepsLeft == 0 ? "Solved!" : (this.config.displayRemainingMoves() ? "Moves left: " + stepsLeft : null);
                    if (this.config.displaySolution()) {
                        if (this.config.drawDots()) {
                            PuzzleState futureMove;
                            int j2;
                            graphics.setColor(Color.YELLOW);
                            for (int i2 = 1; i2 < 5 && (j2 = this.solver.getPosition() + i2) < this.solver.getStepCount() && (futureMove = this.solver.getStep(j2)) != null; ++i2) {
                                int blankX = futureMove.getEmptyPiece() % 5;
                                int blankY = futureMove.getEmptyPiece() / 5;
                                int markerSize = 16 - i2 * 3;
                                int x = puzzleBoxLocation.getX() + blankX * 39 + 19 - markerSize / 2;
                                int y = puzzleBoxLocation.getY() + blankY * 39 + 19 - markerSize / 2;
                                graphics.fillOval(x, y, markerSize, markerSize);
                            }
                        } else {
                            PuzzleState futureMove;
                            int j3;
                            PuzzleState currentMove = this.solver.getStep(this.solver.getPosition());
                            int lastBlankX = currentMove.getEmptyPiece() % 5;
                            int lastBlankY = currentMove.getEmptyPiece() / 5;
                            for (int i3 = 1; i3 < 4 && (j3 = this.solver.getPosition() + i3) < this.solver.getStepCount() && (futureMove = this.solver.getStep(j3)) != null; ++i3) {
                                int blankX = futureMove.getEmptyPiece() % 5;
                                int blankY = futureMove.getEmptyPiece() / 5;
                                int xDelta = blankX - lastBlankX;
                                int yDelta = blankY - lastBlankY;
                                BufferedImage arrow = xDelta > 0 ? this.getRightArrow() : (xDelta < 0 ? this.getLeftArrow() : (yDelta > 0 ? this.getDownArrow() : this.getUpArrow()));
                                if (arrow == null) continue;
                                int x = puzzleBoxLocation.getX() + blankX * 39 + 19 - arrow.getWidth() / 2;
                                int y = puzzleBoxLocation.getY() + blankY * 39 + 19 - arrow.getHeight() / 2;
                                OverlayUtil.renderImageLocation(graphics, new Point(x, y), arrow);
                                lastBlankX = blankX;
                                lastBlankY = blankY;
                            }
                        }
                    }
                }
            }
        }
        if (infoString != null) {
            int x = puzzleBoxLocation.getX() + puzzleBox.getWidth() / 2 - 50;
            int y = puzzleBoxLocation.getY() - 50;
            FontMetrics fm = graphics.getFontMetrics();
            int height = 2 + fm.getHeight() + 2;
            BackgroundComponent backgroundComponent = new BackgroundComponent();
            backgroundComponent.setRectangle(new Rectangle(x, y, 100, height));
            backgroundComponent.render(graphics);
            int textOffsetX = (100 - fm.stringWidth((String)infoString)) / 2;
            int textOffsetY = fm.getHeight();
            TextComponent textComponent = new TextComponent();
            textComponent.setPosition(new java.awt.Point(x + textOffsetX, y + textOffsetY));
            textComponent.setText((String)infoString);
            textComponent.render(graphics);
        }
        if (this.solver == null || this.cachedItems == null || !shouldCache && this.solver.hasExceededWaitDuration() && !Arrays.equals(this.cachedItems, itemIds)) {
            this.solve(itemIds, useNormalSolver);
            shouldCache = true;
        }
        if (shouldCache) {
            this.cacheItems(itemIds);
        }
        return null;
    }

    private int[] getItemIds(ItemContainer container, boolean useNormalSolver) {
        int[] itemIds = new int[25];
        Item[] items = container.getItems();
        for (int i = 0; i < items.length; ++i) {
            itemIds[i] = items[i].getId();
        }
        if (itemIds.length > items.length) {
            itemIds[items.length] = -1;
        }
        return this.convertToSolverFormat(itemIds, useNormalSolver);
    }

    private int[] convertToSolverFormat(int[] items, boolean useNormalSolver) {
        int lowestId = Integer.MAX_VALUE;
        int[] convertedItems = new int[items.length];
        for (int id : items) {
            if (id == -1 || lowestId <= id) continue;
            lowestId = id;
        }
        for (int i = 0; i < items.length; ++i) {
            if (items[i] != -1) {
                int value = items[i] - lowestId;
                if (!useNormalSolver) {
                    value /= 2;
                }
                convertedItems[i] = value;
                continue;
            }
            convertedItems[i] = -1;
        }
        return convertedItems;
    }

    private void cacheItems(int[] items) {
        this.cachedItems = new int[items.length];
        System.arraycopy(items, 0, this.cachedItems, 0, this.cachedItems.length);
    }

    private void solve(int[] items, boolean useNormalSolver) {
        if (this.solverFuture != null) {
            this.solverFuture.cancel(true);
        }
        PuzzleState puzzleState = new PuzzleState(items);
        this.solver = useNormalSolver ? new PuzzleSolver(new IDAStar(new ManhattanDistance()), puzzleState) : new PuzzleSolver(new IDAStarMM(new ManhattanDistance()), puzzleState);
        this.solverFuture = this.executorService.submit(this.solver);
    }

    private BufferedImage getDownArrow() {
        return this.spriteManager.getSprite(422, 1);
    }

    private BufferedImage getUpArrow() {
        if (this.upArrow == null) {
            this.upArrow = ImageUtil.rotateImage(this.getDownArrow(), Math.PI);
        }
        return this.upArrow;
    }

    private BufferedImage getLeftArrow() {
        if (this.leftArrow == null) {
            this.leftArrow = ImageUtil.rotateImage(this.getDownArrow(), 1.5707963267948966);
        }
        return this.leftArrow;
    }

    private BufferedImage getRightArrow() {
        if (this.rightArrow == null) {
            this.rightArrow = ImageUtil.rotateImage(this.getDownArrow(), 4.71238898038469);
        }
        return this.rightArrow;
    }
}

