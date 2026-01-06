/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.runelite.api.Point
 */
package net.runelite.client.plugins.puzzlesolver.solver.pathfinding;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.runelite.api.Point;
import net.runelite.client.plugins.puzzlesolver.solver.PuzzleState;
import net.runelite.client.plugins.puzzlesolver.solver.PuzzleSwapPattern;
import net.runelite.client.plugins.puzzlesolver.solver.heuristics.Heuristic;
import net.runelite.client.plugins.puzzlesolver.solver.pathfinding.IDAStar;

public class IDAStarMM
extends IDAStar {
    private PuzzleState currentState;
    private final List<PuzzleState> stateList = new ArrayList<PuzzleState>();
    private final List<List<Integer>> validRowNumbers = new ArrayList<List<Integer>>();
    private final List<List<Integer>> validColumnNumbers = new ArrayList<List<Integer>>();

    public IDAStarMM(Heuristic heuristic) {
        super(heuristic);
        this.validRowNumbers.add(Arrays.asList(0, 1, 2, 3, 4));
        this.validRowNumbers.add(Arrays.asList(6, 7, 8, 9));
        this.validColumnNumbers.add(Arrays.asList(5, 10, 15, 20));
    }

    @Override
    public List<PuzzleState> computePath(PuzzleState root) {
        this.currentState = root;
        this.stateList.add(root);
        ArrayList<PuzzleState> path = new ArrayList<PuzzleState>();
        this.solveRow(0);
        this.solveColumn();
        this.solveRow(1);
        this.stateList.remove(this.stateList.size() - 1);
        path.addAll(super.computePath(this.currentState));
        path.addAll(0, this.stateList);
        return path;
    }

    private void solveRow(int row) {
        for (int i = row; i < 5; ++i) {
            int valTarget = row * 5 + i;
            int valCurrent = this.currentState.getPiece(i, row);
            if (valCurrent == valTarget) continue;
            this.moveTowardsVal(valTarget, i, row, true);
        }
    }

    private void solveColumn() {
        int column = 0;
        for (int i = column + 1; i < 5; ++i) {
            int valTarget = column + i * 5;
            int valCurrent = this.currentState.getPiece(column, i);
            if (valCurrent == valTarget) continue;
            this.moveTowardsVal(valTarget, column, i, false);
        }
    }

    private void moveTowardsVal(int valTarget, int x, int y, boolean rowMode) {
        boolean reached = false;
        while (this.currentState.getPiece(x, y) != valTarget) {
            Point locSwap;
            Point locVal = this.findPiece(valTarget);
            Point locBlank = this.findPiece(-1);
            if (reached) {
                if (rowMode) {
                    this.alignTargetX(valTarget, x, y);
                    this.swapUpRow(valTarget, x, y);
                    continue;
                }
                this.alignTargetY(valTarget, x, y);
                this.swapLeftColumn(valTarget, x, y);
                continue;
            }
            int distX = locVal.getX() - locBlank.getX();
            int distY = locVal.getY() - locBlank.getY();
            int distAbsX = Math.abs(distX);
            int distAbsY = Math.abs(distY);
            if (distX == 0) {
                if (distAbsY == 1) {
                    reached = true;
                    continue;
                }
                if (distY >= 2) {
                    locSwap = new Point(locBlank.getX(), locBlank.getY() + 1);
                    this.swap(locBlank, locSwap);
                    continue;
                }
                if (distY > -2) continue;
                locSwap = new Point(locBlank.getX(), locBlank.getY() - 1);
                this.swap(locBlank, locSwap);
                continue;
            }
            if (distY == 0) {
                if (distAbsX == 1) {
                    reached = true;
                    continue;
                }
                if (distX >= 2) {
                    locSwap = new Point(locBlank.getX() + 1, locBlank.getY());
                    this.swap(locBlank, locSwap);
                    continue;
                }
                if (distX > -2) continue;
                locSwap = new Point(locBlank.getX() - 1, locBlank.getY());
                this.swap(locBlank, locSwap);
                continue;
            }
            if (rowMode) {
                if (locBlank.getY() - 1 == y && this.validRowNumbers.get(y).contains(this.currentState.getPiece(locBlank.getX(), locBlank.getY() - 1)) && this.currentState.getPiece(locBlank.getX(), locBlank.getY() - 1) < valTarget && distY <= -1) {
                    locSwap = new Point(locBlank.getX() + 1, locBlank.getY());
                    this.swap(locBlank, locSwap);
                    continue;
                }
                if (distY >= 1) {
                    locSwap = new Point(locBlank.getX(), locBlank.getY() + 1);
                    this.swap(locBlank, locSwap);
                    continue;
                }
                if (distY > -1) continue;
                locSwap = new Point(locBlank.getX(), locBlank.getY() - 1);
                this.swap(locBlank, locSwap);
                continue;
            }
            if (locBlank.getX() - 1 == x && this.validColumnNumbers.get(x).contains(this.currentState.getPiece(locBlank.getX() - 1, locBlank.getY())) && this.currentState.getPiece(locBlank.getX() - 1, locBlank.getY()) < valTarget && distX <= -1) {
                locSwap = new Point(locBlank.getX(), locBlank.getY() + 1);
                this.swap(locBlank, locSwap);
                continue;
            }
            if (distX >= 1) {
                locSwap = new Point(locBlank.getX() + 1, locBlank.getY());
                this.swap(locBlank, locSwap);
                continue;
            }
            if (distX > -1) continue;
            locSwap = new Point(locBlank.getX() - 1, locBlank.getY());
            this.swap(locBlank, locSwap);
        }
    }

    private void alignTargetX(int valTarget, int x, int y) {
        Point locVal = this.findPiece(valTarget);
        if (locVal.getX() == x) {
            return;
        }
        int direction = Integer.signum(x - locVal.getX());
        while (locVal.getX() != x) {
            int diff;
            locVal = this.findPiece(valTarget);
            Point locBlank = this.findPiece(-1);
            if (x - locVal.getX() == 0) break;
            if (locVal.getX() == locBlank.getX()) {
                diff = locBlank.getY() - locVal.getY();
                if (diff == 1) {
                    Point loc1 = new Point(locBlank.getX() + direction, locBlank.getY());
                    Point loc2 = new Point(loc1.getX(), loc1.getY() - 1);
                    this.swap(locBlank, loc1);
                    this.swap(loc1, loc2);
                    this.swap(loc2, locVal);
                    continue;
                }
                if (diff != -1) continue;
                this.swap(locBlank, locVal);
                continue;
            }
            if (locVal.getY() != locBlank.getY()) continue;
            diff = locBlank.getX() - locVal.getX();
            if (diff == 1) {
                if (direction == 1) {
                    this.swap(locVal, locBlank);
                    continue;
                }
                if (direction != -1) continue;
                if (locVal.getY() == 4) {
                    this.performSwapPattern(locBlank, locVal, PuzzleSwapPattern.ROTATE_LEFT_UP);
                    continue;
                }
                this.performSwapPattern(locBlank, locVal, PuzzleSwapPattern.ROTATE_LEFT_DOWN);
                continue;
            }
            if (diff != -1) continue;
            if (direction == -1) {
                this.swap(locVal, locBlank);
                continue;
            }
            if (direction != 1) continue;
            if (locVal.getY() == 4) {
                this.performSwapPattern(locBlank, locVal, PuzzleSwapPattern.ROTATE_RIGHT_UP);
                continue;
            }
            this.performSwapPattern(locBlank, locVal, PuzzleSwapPattern.ROTATE_RIGHT_DOWN);
        }
    }

    private void swapUpRow(int valTarget, int x, int y) {
        Point locVal = this.findPiece(valTarget);
        Point locBlank = this.findPiece(-1);
        if (locVal.getX() == x && locVal.getY() == y) {
            return;
        }
        if (locBlank.getX() == x && locBlank.getY() == y && locVal.getY() - 1 == y) {
            this.swap(locBlank, locVal);
            return;
        }
        while (true) {
            int diff;
            locVal = this.findPiece(valTarget);
            locBlank = this.findPiece(-1);
            if (locVal.getX() == x && locVal.getY() == y) {
                return;
            }
            if (locVal.getX() == locBlank.getX()) {
                diff = locBlank.getY() - locVal.getY();
                if (diff == 1) {
                    if (x == 4) {
                        this.performSwapPattern(locBlank, locVal, PuzzleSwapPattern.LAST_PIECE_ROW);
                        return;
                    }
                    this.performSwapPattern(locBlank, locVal, PuzzleSwapPattern.ROTATE_UP_RIGHT);
                    continue;
                }
                if (diff != -1) continue;
                this.swap(locBlank, locVal);
                continue;
            }
            if (locVal.getY() != locBlank.getY()) continue;
            diff = locBlank.getX() - locVal.getX();
            if (diff == 1) {
                this.performSwapPattern(locBlank, locVal, PuzzleSwapPattern.SHUFFLE_UP_RIGHT);
                continue;
            }
            if (diff != -1) continue;
            if (locVal.getY() - 1 == y) {
                Point loc1 = new Point(locBlank.getX(), locBlank.getY() + 1);
                Point loc2 = new Point(loc1.getX() + 1, loc1.getY());
                this.swap(locBlank, loc1);
                this.swap(loc1, loc2);
                continue;
            }
            this.performSwapPattern(locBlank, locVal, PuzzleSwapPattern.SHUFFLE_UP_LEFT);
        }
    }

    private void alignTargetY(int valTarget, int x, int y) {
        Point locVal = this.findPiece(valTarget);
        if (locVal.getY() == y) {
            return;
        }
        int direction = Integer.signum(y - locVal.getY());
        while (locVal.getY() != y) {
            int diff;
            locVal = this.findPiece(valTarget);
            Point locBlank = this.findPiece(-1);
            if (y - locVal.getY() == 0) break;
            if (locVal.getY() == locBlank.getY()) {
                diff = locBlank.getX() - locVal.getX();
                if (diff == 1) {
                    Point loc1 = new Point(locBlank.getX(), locBlank.getY() + direction);
                    Point loc2 = new Point(loc1.getX() - 1, loc1.getY());
                    this.swap(locBlank, loc1);
                    this.swap(loc1, loc2);
                    this.swap(loc2, locVal);
                    continue;
                }
                if (diff != -1) continue;
                this.swap(locBlank, locVal);
                continue;
            }
            if (locVal.getX() != locBlank.getX()) continue;
            diff = locBlank.getY() - locVal.getY();
            if (diff == 1) {
                if (direction == 1) {
                    this.swap(locVal, locBlank);
                    continue;
                }
                if (direction != -1) continue;
                if (locVal.getX() == 4) {
                    this.performSwapPattern(locBlank, locVal, PuzzleSwapPattern.ROTATE_UP_LEFT);
                    continue;
                }
                this.performSwapPattern(locBlank, locVal, PuzzleSwapPattern.ROTATE_UP_RIGHT);
                continue;
            }
            if (diff != -1) continue;
            if (direction == -1) {
                this.swap(locVal, locBlank);
                continue;
            }
            if (direction != 1) continue;
            if (locVal.getX() == 4) {
                this.performSwapPattern(locBlank, locVal, PuzzleSwapPattern.ROTATE_DOWN_LEFT);
                continue;
            }
            this.performSwapPattern(locBlank, locVal, PuzzleSwapPattern.ROTATE_DOWN_RIGHT);
        }
    }

    private void swapLeftColumn(int valTarget, int x, int y) {
        Point locVal = this.findPiece(valTarget);
        Point locBlank = this.findPiece(-1);
        if (locVal.getX() == x && locVal.getY() == y) {
            return;
        }
        if (locBlank.getX() == x && locBlank.getY() == y && locVal.getX() - 1 == x) {
            this.swap(locBlank, locVal);
            return;
        }
        while (true) {
            int diff;
            locVal = this.findPiece(valTarget);
            locBlank = this.findPiece(-1);
            if (locVal.getX() == x && locVal.getY() == y) {
                return;
            }
            if (locVal.getX() == locBlank.getX()) {
                diff = locBlank.getY() - locVal.getY();
                if (diff == 1) {
                    this.performSwapPattern(locBlank, locVal, PuzzleSwapPattern.SHUFFLE_UP_BELOW);
                    continue;
                }
                if (diff != -1) continue;
                if (locVal.getX() - 1 == x) {
                    Point loc1 = new Point(locBlank.getX() + 1, locBlank.getY());
                    Point loc2 = new Point(loc1.getX(), loc1.getY() + 1);
                    this.swap(locBlank, loc1);
                    this.swap(loc1, loc2);
                    continue;
                }
                this.performSwapPattern(locBlank, locVal, PuzzleSwapPattern.SHUFFLE_UP_ABOVE);
                continue;
            }
            if (locVal.getY() != locBlank.getY()) continue;
            diff = locBlank.getX() - locVal.getX();
            if (diff == 1) {
                if (y == 4) {
                    this.performSwapPattern(locBlank, locVal, PuzzleSwapPattern.LAST_PIECE_COLUMN);
                    return;
                }
                this.performSwapPattern(locBlank, locVal, PuzzleSwapPattern.ROTATE_LEFT_DOWN);
                continue;
            }
            if (diff != -1) continue;
            this.swap(locBlank, locVal);
        }
    }

    private void swap(Point p1, Point p2) {
        PuzzleState newState;
        this.currentState = newState = this.currentState.swap(p1.getX(), p1.getY(), p2.getX(), p2.getY());
        this.stateList.add(newState);
    }

    private Point findPiece(int val) {
        for (int x = 0; x < 5; ++x) {
            for (int y = 0; y < 5; ++y) {
                if (this.currentState.getPiece(x, y) != val) continue;
                return new Point(x, y);
            }
        }
        throw new IllegalStateException("Piece wasn't found!");
    }

    private void performSwapPattern(Point locBlank, Point locVal, PuzzleSwapPattern pattern) {
        int[] offsets;
        switch (pattern) {
            case ROTATE_LEFT_UP: 
            case ROTATE_RIGHT_UP: 
            case ROTATE_RIGHT_DOWN: 
            case ROTATE_LEFT_DOWN: {
                offsets = PuzzleSwapPattern.ROTATE_LEFT_UP.getPoints();
                break;
            }
            case ROTATE_UP_LEFT: 
            case ROTATE_UP_RIGHT: 
            case ROTATE_DOWN_LEFT: 
            case ROTATE_DOWN_RIGHT: {
                offsets = PuzzleSwapPattern.ROTATE_UP_LEFT.getPoints();
                break;
            }
            default: {
                offsets = pattern.getPoints();
            }
        }
        if (offsets == null || offsets.length % 2 == 1) {
            throw new IllegalStateException("Unexpected points given in pattern!");
        }
        int modX = pattern.getModX();
        int modY = pattern.getModY();
        ArrayList<Point> points = new ArrayList<Point>();
        for (int i = 0; i < offsets.length; i += 2) {
            int x = locVal.getX() + modX * offsets[i];
            int y = locVal.getY() + modY * offsets[i + 1];
            points.add(new Point(x, y));
        }
        points.add(locVal);
        if (pattern != PuzzleSwapPattern.LAST_PIECE_ROW && pattern != PuzzleSwapPattern.LAST_PIECE_COLUMN) {
            Point start = locBlank;
            for (Point p : points) {
                this.swap(start, p);
                start = p;
            }
        } else {
            Point loc1 = (Point)points.get(0);
            Point loc2 = (Point)points.get(1);
            Point loc3 = (Point)points.get(2);
            Point loc4 = (Point)points.get(3);
            this.swap(locBlank, locVal);
            this.swap(locVal, loc3);
            this.swap(loc3, loc1);
            this.swap(loc1, loc2);
            this.swap(loc2, locVal);
            this.swap(locVal, loc3);
            this.swap(loc3, loc1);
            this.swap(loc1, loc2);
            this.swap(loc2, locVal);
            this.swap(locVal, locBlank);
            this.swap(locBlank, loc4);
            this.swap(loc4, loc3);
            this.swap(loc3, loc1);
            this.swap(loc1, loc2);
            this.swap(loc2, locVal);
        }
    }
}

