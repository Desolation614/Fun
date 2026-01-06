/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.plugins.interfacestyles;

import net.runelite.client.plugins.interfacestyles.Skin;

enum WidgetOverride {
    FIXED_CORNER_TOP_LEFT_2005(Skin.AROUND_2005, "1026", 35913791),
    FIXED_CORNER_TOP_RIGHT_2005(Skin.AROUND_2005, "1027", 35913797),
    FIXED_CORNER_BOTTOM_LEFT_2005(Skin.AROUND_2005, "1028", 35913775),
    FIXED_CORNER_BOTTOM_RIGHT_2005(Skin.AROUND_2005, "1029", 35913781),
    FIXED_TOP_LEFT_2005(Skin.AROUND_2005, "1030_top_left", 0x2240040, 35913793),
    FIXED_TOP_RIGHT_2005(Skin.AROUND_2005, "1030_top_right", 35913795, 0x2240044),
    FIXED_TOP_MIDDLE_2005(Skin.AROUND_2005, "1030_top_middle", 0x2240042),
    FIXED_BOTTOM_LEFT_2005(Skin.AROUND_2005, "1030_bottom_left", 35913777, 35913776),
    FIXED_BOTTOM_RIGHT_2005(Skin.AROUND_2005, "1030_bottom_middle", 35913778),
    FIXED_BOTTOM_MIDDLE_2005(Skin.AROUND_2005, "1030_bottom_right", 35913779, 35913780);

    private final Skin skin;
    private final String name;
    private final int[] widgetInfo;

    private WidgetOverride(Skin skin, String name, int ... widgetInfo) {
        this.skin = skin;
        this.name = name;
        this.widgetInfo = widgetInfo;
    }

    public Skin getSkin() {
        return this.skin;
    }

    public String getName() {
        return this.name;
    }

    public int[] getWidgetInfo() {
        return this.widgetInfo;
    }
}

