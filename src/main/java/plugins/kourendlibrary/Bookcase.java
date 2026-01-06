/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.runelite.api.coords.WorldPoint
 */
package net.runelite.client.plugins.kourendlibrary;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.kourendlibrary.Book;

class Bookcase {
    private final WorldPoint location;
    private final List<Integer> index;
    private boolean isBookSet;
    private Book book;
    private final Set<Book> possibleBooks = new HashSet<Book>();

    Bookcase(WorldPoint location) {
        this.location = location;
        this.index = new ArrayList<Integer>();
    }

    void clearBook() {
        this.book = null;
        this.isBookSet = false;
    }

    void setBook(Book book) {
        this.book = book;
        this.isBookSet = true;
    }

    String getLocationString() {
        boolean west;
        StringBuilder b = new StringBuilder();
        boolean north = this.location.getY() > 3815;
        boolean bl = west = this.location.getX() < 1625;
        if (this.location.getPlane() == 0) {
            north = this.location.getY() > 3813;
            boolean bl2 = west = this.location.getX() < 1627;
        }
        if (north && west) {
            b.append("Northwest");
        } else if (north) {
            b.append("Northeast");
        } else if (west) {
            b.append("Southwest");
        } else {
            b.append("Center");
        }
        b.append(' ');
        switch (this.location.getPlane()) {
            case 0: {
                b.append("ground floor");
                break;
            }
            case 1: {
                b.append("middle floor");
                break;
            }
            case 2: {
                b.append("top floor");
            }
        }
        return b.toString();
    }

    public WorldPoint getLocation() {
        return this.location;
    }

    public List<Integer> getIndex() {
        return this.index;
    }

    public boolean isBookSet() {
        return this.isBookSet;
    }

    public Book getBook() {
        return this.book;
    }

    public Set<Book> getPossibleBooks() {
        return this.possibleBooks;
    }
}

