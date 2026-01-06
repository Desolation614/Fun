/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.plugins.gpu.regions;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.runelite.client.plugins.gpu.regions.Region;

public class Regions {
    private static final Pattern PATTERN = Pattern.compile("^[ \\t]*(?<expr>//.*$|n|m[ \\t]*(?<mrx>[0-9]+)[ \\t]+(?<mry>[0-9]+)|r[ \\t]*(?<rx>[0-9]+)[ \\t]+(?<ry>[0-9]+)|R[ \\t]*(?<rx1>[0-9]+)[ \\t]+(?<ry1>[0-9]+)[ \\t]+(?<rx2>[0-9]+)[ \\t]+(?<ry2>[0-9]+)|c[ \\t]*(?<cx>[0-9-]+)[ \\t]+(?<cy>[0-9-]+)|C[ \\t]*(?<cx1>[0-9-]+)[ \\t]+(?<cy1>[0-9-]+)[ \\t]+(?<cx2>[0-9-]+)[ \\t]+(?<cy2>[0-9-]+)|)[ \\t]*");
    private final List<Region> regions = new ArrayList<Region>();
    private static final int REGION_MAP_SIZE = 1750;
    private final byte[] regionMap = new byte[1750];
    private Region last;

    public Regions(InputStream is, String filename) throws IOException {
        this(new InputStreamReader(is, StandardCharsets.UTF_8), filename);
    }

    public Regions(Reader reader, String filename) throws IOException {
        String line;
        BufferedReader br = new BufferedReader(reader);
        int lineNo = 1;
        int rx1 = 0;
        int ry1 = 0;
        int rx2 = 0;
        int ry2 = 0;
        int rid = 0;
        Matcher m = PATTERN.matcher("");
        while ((line = br.readLine()) != null) {
            m.reset(line);
            int end = 0;
            while (end < line.length()) {
                m.region(end, line.length());
                if (!m.find()) {
                    throw new IllegalArgumentException("Unexpected: \"" + line.substring(end) + "\" (" + filename + ":" + lineNo + ")");
                }
                end = m.end();
                String expr = m.group("expr");
                if (expr == null || expr.length() <= 0 || expr.startsWith("//")) continue;
                char cha = expr.charAt(0);
                switch (cha) {
                    case 'n': {
                        ++rid;
                        break;
                    }
                    case 'm': {
                        rx2 = rx1 = Integer.parseInt(m.group("mrx"));
                        ry2 = ry1 = Integer.parseInt(m.group("mry"));
                        break;
                    }
                    case 'R': 
                    case 'r': {
                        if (cha == 'r') {
                            rx2 = rx1 = Integer.parseInt(m.group("rx"));
                            ry2 = ry1 = Integer.parseInt(m.group("ry"));
                        } else {
                            rx1 = Integer.parseInt(m.group("rx1"));
                            ry1 = Integer.parseInt(m.group("ry1"));
                            rx2 = Integer.parseInt(m.group("rx2"));
                            ry2 = Integer.parseInt(m.group("ry2"));
                        }
                    }
                    case 'C': 
                    case 'c': {
                        int cx1 = rx1 * 8;
                        int cy1 = ry1 * 8;
                        int cx2 = rx2 * 8 + 7;
                        int cy2 = ry2 * 8 + 7;
                        if (cha == 'c') {
                            cx2 = cx1 += Integer.parseInt(m.group("cx"));
                            cy2 = cy1 += Integer.parseInt(m.group("cy"));
                        } else if (cha == 'C') {
                            cx2 = cx1 + Integer.parseInt(m.group("cx2"));
                            cy2 = cy1 + Integer.parseInt(m.group("cy2"));
                            cx1 += Integer.parseInt(m.group("cx1"));
                            cy1 += Integer.parseInt(m.group("cy1"));
                        }
                        if (cx1 > cx2 || cy1 > cy2) {
                            throw new IllegalArgumentException("First coord must be before second (" + filename + ":" + lineNo + ")");
                        }
                        Region r = new Region();
                        r.id = rid;
                        r.cx1 = cx1;
                        r.cy1 = cy1;
                        r.cx2 = cx2;
                        r.cy2 = cy2;
                        this.regions.add(r);
                        for (int rx = rx1; rx <= rx2; ++rx) {
                            for (int ry = ry1; ry <= ry2; ++ry) {
                                this.markRegion(rx, ry);
                            }
                        }
                        break;
                    }
                }
            }
            ++lineNo;
        }
    }

    public int getRegionId(int cx, int cy) {
        int rx = cx / 8;
        int ry = cy / 8;
        if (!this.isMarked(rx, ry)) {
            return 0;
        }
        if (this.last != null && Regions.intersects(cx, cy, this.last)) {
            return this.last.id;
        }
        for (Region r : this.regions) {
            if (!Regions.intersects(cx, cy, r)) continue;
            this.last = r;
            return r.id;
        }
        return 0;
    }

    private static boolean intersects(int cx, int cy, Region r) {
        return cx >= r.cx1 && cy >= r.cy1 && cx <= r.cx2 && cy <= r.cy2;
    }

    private void markRegion(int rx, int ry) {
        int id = rx << 8 | ry;
        int idx = id / 8;
        if (idx < this.regionMap.length) {
            int n = idx;
            this.regionMap[n] = (byte)(this.regionMap[n] | 1 << idx % 8);
        }
    }

    private boolean isMarked(int rx, int ry) {
        int id = rx << 8 | ry;
        int idx = id / 8;
        if (idx < this.regionMap.length) {
            return (this.regionMap[idx] & 1 << idx % 8) != 0;
        }
        return true;
    }

    List<Region> getRegions() {
        return this.regions;
    }
}

