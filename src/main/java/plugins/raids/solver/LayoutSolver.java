/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.inject.Singleton
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package net.runelite.client.plugins.raids.solver;

import com.google.inject.Singleton;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.runelite.client.plugins.raids.solver.Layout;
import net.runelite.client.plugins.raids.solver.Room;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class LayoutSolver {
    private static final Logger log = LoggerFactory.getLogger(LayoutSolver.class);
    private static final List<Layout> layouts = new ArrayList<Layout>();
    private static final Pattern regex = Pattern.compile("^([A-Z]*)\\.([A-Z]*) - #([A-Z]*)#([A-Z]*)$");
    private static final String[] codes = new String[]{"FSCCP.PCSCF - #WNWSWN#ESEENW", "FSCCS.PCPSF - #WSEEEN#WSWNWS", "FSCPC.CSCPF - #WNWWSE#EENWWW", "SCCFC.PSCSF - #EEENWW#WSEEEN", "SCCFP.CCSPF - #NESEEN#WSWNWS", "SCFCP.CCSPF - #ESEENW#ESWWNW", "SCFCP.CSCFS - #ENEESW#ENWWSW", "SCFCPC.CSPCSF - #ESWWNWS#NESENES", "SCFPC.CSPCF - #WSWWNE#WSEENE", "SCFPC.PCCSF - #WSEENE#WWWSEE", "SCFPC.SCPCF - #NESENE#WSWWNE", "SCPFC.CCPSF - #NWWWSE#WNEESE", "SCPFC.CSPCF - #NEEESW#WWNEEE", "SCPFC.CSPSF - #WWSEEE#NWSWWN", "SCSPF.CCSPF - #ESWWNW#ESENES", "SFCCP.CSCPF - #WNEESE#NWSWWN", "SFCCS.PCPSF - #ENWWSW#ENESEN", "SPCFC.CSPCF - #WWNEEE#WSWNWS", "SPCFC.SCCPF - #ESENES#WWWNEE", "SPSFP.CCCSF - #NWSWWN#ESEENW", "SCFCP.CSCPF - #ENESEN#WWWSEE", "SCPFC.PCSCF - #WNEEES#NWSWNW", "SFCCPC.PCSCPF - #WSEENES#WWWNEEE", "FSPCC.PSCCF - #WWWSEE#ENWWSW", "FSCCP.PCSCF - #ENWWWS#NEESEN", "SCPFC.CCSSF - #NEESEN#WSWWNE"};

    public LayoutSolver() {
        this.build();
    }

    public Layout findLayout(String code) {
        Layout solution = null;
        int matches = 0;
        for (Layout layout : layouts) {
            boolean match = true;
            for (int i = 0; i < code.length(); ++i) {
                Room room = layout.getRoomAt(i);
                char c = code.charAt(i);
                if (room == null || c == ' ' || c == room.getSymbol()) continue;
                match = false;
                break;
            }
            if (!match) continue;
            solution = layout;
            ++matches;
            log.debug("Found matching layout: " + layout.toCode());
        }
        if (matches == 1) {
            return solution;
        }
        return null;
    }

    private int calcStart(String directions) {
        int startPos = 0;
        int position = 0;
        for (int i = 0; i < directions.length(); ++i) {
            char c = directions.charAt(i);
            int delta = this.dirToPosDelta(c);
            if ((position += delta) >= 0 && position < 8 && (position != 3 || delta != -1) && (position != 4 || delta != 1)) continue;
            position -= delta;
            startPos -= delta;
        }
        return startPos;
    }

    private int dirToPosDelta(char direction) {
        switch (String.valueOf(direction)) {
            case "N": {
                return -4;
            }
            case "E": {
                return 1;
            }
            case "S": {
                return 4;
            }
            case "W": {
                return -1;
            }
        }
        return 0;
    }

    private void build() {
        for (String code : codes) {
            Matcher match = regex.matcher(code);
            if (!match.find()) continue;
            int position = this.calcStart(match.group(3));
            Layout layout = new Layout();
            Room lastRoom = null;
            for (int floor = 0; floor < 2; ++floor) {
                Room room;
                String symbols = match.group(1 + floor);
                String directions = match.group(3 + floor);
                for (int i = 0; i < directions.length(); ++i) {
                    char symbol = i == 0 ? (char)'#' : (char)symbols.charAt(i - 1);
                    room = new Room(position, symbol);
                    if (lastRoom != null) {
                        lastRoom.setNext(room);
                        room.setPrevious(lastRoom);
                    }
                    layout.add(room);
                    lastRoom = room;
                    int delta = this.dirToPosDelta(directions.charAt(i));
                    position += delta;
                }
                room = new Room(position, '\u00a4');
                room.setPrevious(lastRoom);
                lastRoom.setNext(room);
                layout.add(room);
                position += 8;
            }
            layouts.add(layout);
        }
    }

    public static List<Layout> getLayouts() {
        return layouts;
    }
}

