/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.runelite.api.coords.WorldPoint
 */
package net.runelite.client.plugins.raids;

import java.util.ArrayList;
import java.util.List;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.raids.RaidRoom;
import net.runelite.client.plugins.raids.RoomType;
import net.runelite.client.plugins.raids.solver.Layout;
import net.runelite.client.plugins.raids.solver.Room;

public class Raid {
    private final RaidRoom[] rooms = new RaidRoom[16];
    private Layout layout;
    private final WorldPoint gridBase;
    private final int lobbyIndex;

    public Raid(WorldPoint gridBase, int lobbyIndex) {
        this.gridBase = gridBase;
        this.lobbyIndex = lobbyIndex;
    }

    void updateLayout(Layout layout) {
        if (layout == null) {
            return;
        }
        this.layout = layout;
        for (int i = 0; i < this.rooms.length; ++i) {
            RaidRoom room;
            if (layout.getRoomAt(i) == null || (room = this.rooms[i]) != null) continue;
            RoomType type = RoomType.fromCode(layout.getRoomAt(i).getSymbol());
            room = type.getUnsolvedRoom();
            this.setRoom(room, i);
        }
    }

    public RaidRoom getRoom(int position) {
        return this.rooms[position];
    }

    public void setRoom(RaidRoom room, int position) {
        if (position < this.rooms.length) {
            this.rooms[position] = room;
        }
    }

    RaidRoom[] getCombatRooms() {
        ArrayList<RaidRoom> combatRooms = new ArrayList<RaidRoom>();
        for (Room room : this.layout.getRooms()) {
            if (room == null || this.rooms[room.getPosition()].getType() != RoomType.COMBAT) continue;
            combatRooms.add(this.rooms[room.getPosition()]);
        }
        return combatRooms.toArray(new RaidRoom[0]);
    }

    void setCombatRooms(RaidRoom[] combatRooms) {
        int index = 0;
        for (Room room : this.layout.getRooms()) {
            if (room == null || this.rooms[room.getPosition()].getType() != RoomType.COMBAT) continue;
            this.rooms[room.getPosition()] = combatRooms[index];
            ++index;
        }
    }

    public String toCode() {
        StringBuilder builder = new StringBuilder();
        for (RaidRoom room : this.rooms) {
            if (room != null) {
                builder.append(room.getType().getCode());
                continue;
            }
            builder.append(' ');
        }
        return builder.toString();
    }

    List<RaidRoom> getOrderedRooms() {
        ArrayList<RaidRoom> orderedRooms = new ArrayList<RaidRoom>();
        for (Room r : this.getLayout().getRooms()) {
            int position = r.getPosition();
            RaidRoom room = this.getRoom(position);
            if (room == null) continue;
            orderedRooms.add(room);
        }
        return orderedRooms;
    }

    String toRoomString() {
        StringBuilder sb = new StringBuilder();
        for (RaidRoom room : this.getOrderedRooms()) {
            switch (room.getType()) {
                case PUZZLE: 
                case COMBAT: {
                    sb.append(room.getName()).append(", ");
                }
            }
        }
        String roomsString = sb.toString();
        return roomsString.substring(0, roomsString.length() - 2);
    }

    public RaidRoom[] getRooms() {
        return this.rooms;
    }

    public Layout getLayout() {
        return this.layout;
    }

    public WorldPoint getGridBase() {
        return this.gridBase;
    }

    public int getLobbyIndex() {
        return this.lobbyIndex;
    }
}

