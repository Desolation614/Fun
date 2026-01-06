/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.plugins.puzzlesolver.solver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.runelite.client.plugins.puzzlesolver.solver.heuristics.Heuristic;

public class PuzzleState {
    private PuzzleState parent;
    private final int[] pieces;
    private int emptyPiece = -1;
    private int h = -1;

    public PuzzleState(int[] pieces) {
        if (pieces == null) {
            throw new IllegalStateException("Pieces cannot be null");
        }
        if (25 != pieces.length) {
            throw new IllegalStateException("Piece array does not have the right dimensions");
        }
        this.pieces = pieces;
        this.findEmptyPiece();
    }

    private PuzzleState(PuzzleState state) {
        this.pieces = Arrays.copyOf(state.pieces, state.pieces.length);
        this.emptyPiece = state.emptyPiece;
    }

    private void findEmptyPiece() {
        for (int i = 0; i < this.pieces.length; ++i) {
            if (this.pieces[i] != -1) continue;
            this.emptyPiece = i;
            return;
        }
        throw new IllegalStateException("Incorrect empty piece passed in!");
    }

    public List<PuzzleState> computeMoves() {
        PuzzleState state;
        ArrayList<PuzzleState> moves = new ArrayList<PuzzleState>();
        int emptyPieceX = this.emptyPiece % 5;
        int emptyPieceY = this.emptyPiece / 5;
        if (emptyPieceX > 0 && (this.parent == null || this.parent.emptyPiece != this.emptyPiece - 1)) {
            state = new PuzzleState(this);
            state.parent = this;
            state.pieces[this.emptyPiece - 1] = -1;
            state.pieces[this.emptyPiece] = this.pieces[this.emptyPiece - 1];
            --state.emptyPiece;
            moves.add(state);
        }
        if (emptyPieceX < 4 && (this.parent == null || this.parent.emptyPiece != this.emptyPiece + 1)) {
            state = new PuzzleState(this);
            state.parent = this;
            state.pieces[this.emptyPiece + 1] = -1;
            state.pieces[this.emptyPiece] = this.pieces[this.emptyPiece + 1];
            ++state.emptyPiece;
            moves.add(state);
        }
        if (emptyPieceY > 0 && (this.parent == null || this.parent.emptyPiece != this.emptyPiece - 5)) {
            state = new PuzzleState(this);
            state.parent = this;
            state.pieces[this.emptyPiece - 5] = -1;
            state.pieces[this.emptyPiece] = this.pieces[this.emptyPiece - 5];
            state.emptyPiece -= 5;
            moves.add(state);
        }
        if (emptyPieceY < 4 && (this.parent == null || this.parent.emptyPiece != this.emptyPiece + 5)) {
            state = new PuzzleState(this);
            state.parent = this;
            state.pieces[this.emptyPiece + 5] = -1;
            state.pieces[this.emptyPiece] = this.pieces[this.emptyPiece + 5];
            state.emptyPiece += 5;
            moves.add(state);
        }
        return moves;
    }

    public PuzzleState getParent() {
        return this.parent;
    }

    public boolean hasPieces(int[] pieces) {
        return Arrays.equals(pieces, this.pieces);
    }

    public int getPiece(int x, int y) {
        return this.pieces[y * 5 + x];
    }

    public int getEmptyPiece() {
        return this.emptyPiece;
    }

    public int getHeuristicValue(Heuristic heuristic) {
        if (this.h == -1) {
            this.h = heuristic.computeValue(this);
        }
        return this.h;
    }

    public PuzzleState swap(int x1, int y1, int x2, int y2) {
        int val1 = this.getPiece(x1, y1);
        int val2 = this.getPiece(x2, y2);
        if (!this.isValidSwap(x1, y1, x2, y2)) {
            throw new IllegalStateException(String.format("Invalid swap: (%1$d, %2$d), (%3$d, %4$d)", x1, y1, x2, y2));
        }
        PuzzleState newState = new PuzzleState(this);
        newState.pieces[y1 * 5 + x1] = val2;
        newState.pieces[y2 * 5 + x2] = val1;
        newState.findEmptyPiece();
        return newState;
    }

    private boolean isValidSwap(int x1, int y1, int x2, int y2) {
        int absX = Math.abs(x1 - x2);
        int absY = Math.abs(y1 - y2);
        if (this.getPiece(x1, y1) != -1 && this.getPiece(x2, y2) != -1) {
            return false;
        }
        if (x1 == x2 && absY == 1) {
            return true;
        }
        return y1 == y2 && absX == 1;
    }
}

