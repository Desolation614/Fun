/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.plugins.raids.solver;

public class Room {
    private final int position;
    private final char symbol;
    private Room next;
    private Room previous;

    Room(int position, char symbol) {
        this.position = position;
        this.symbol = symbol;
    }

    public int getPosition() {
        return this.position;
    }

    public char getSymbol() {
        return this.symbol;
    }

    public Room getNext() {
        return this.next;
    }

    public void setNext(Room next) {
        this.next = next;
    }

    public Room getPrevious() {
        return this.previous;
    }

    public void setPrevious(Room previous) {
        this.previous = previous;
    }
}

