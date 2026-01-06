/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.plugins.raids.solver;

import java.util.ArrayList;
import java.util.List;
import net.runelite.client.plugins.raids.solver.Room;

public class Layout {
    private final List<Room> rooms = new ArrayList<Room>();

    public void add(Room room) {
        this.rooms.add(room);
    }

    public Room getRoomAt(int position) {
        for (Room room : this.rooms) {
            if (room.getPosition() != position) continue;
            return room;
        }
        return null;
    }

    public String toCode() {
        StringBuilder builder = new StringBuilder();
        for (Room room : this.rooms) {
            builder.append(room.getSymbol());
        }
        return builder.toString();
    }

    public String toCodeString() {
        return this.toCode().replace("#", "").replace("\u00a4", "");
    }

    public List<Room> getRooms() {
        return this.rooms;
    }
}

