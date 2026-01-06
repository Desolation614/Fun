/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.runelite.api.coords.WorldPoint
 */
package net.runelite.client.plugins.worldmap;

import net.runelite.api.coords.WorldPoint;

enum AgilityCourseLocation {
    AGILITY_PYRAMID("Agility Pyramid", new WorldPoint(3347, 2827, 0)),
    AL_KHARID_ROOFTOP_COURSE("Al Kharid Rooftop Course", new WorldPoint(3272, 3195, 0)),
    APE_ATOLL_AGILITY_COURSE("Ape Atoll Agility Course", new WorldPoint(2752, 2742, 0)),
    ARDOUGNE_ROOFTOP_COURSE("Ardougne Rooftop Course", new WorldPoint(2673, 3298, 0)),
    BARBARIAN_OUTPOST_AGILITY_COURSE("Barbarian Outpost Agility Course", new WorldPoint(2544, 3569, 0)),
    BRIMHAVEN_AGILITY_ARENA("Brimhaven Agility Arena", new WorldPoint(2806, 3193, 0)),
    CANIFIS_ROOFTOP_COURSE("Canifis Rooftop Course", new WorldPoint(3506, 3490, 0)),
    COLOSSAL_WYRM_COURSE("Colossal Wyrm Course", new WorldPoint(1649, 2930, 0)),
    DRAYNOR_VILLAGE_ROOFTOP_COURSE("Draynor Village Rooftop Course", new WorldPoint(3103, 3279, 0)),
    FALADOR_ROOFTOP_COURSE("Falador Rooftop Course", new WorldPoint(3035, 3342, 0)),
    GNOME_STRONGHOLD_AGILITY_COURSE("Gnome Stronghold Agility Course", new WorldPoint(2474, 3436, 0)),
    PENGUIN_AGILITY_COURSE("Penguin Agility Course", new WorldPoint(2638, 4041, 0)),
    POLLNIVNEACH_ROOFTOP_COURSE("Pollnivneach Rooftop Course", new WorldPoint(3350, 2963, 0)),
    PRIFDDINAS_AGILITY_COURSE("Prifddinas Agility Course", new WorldPoint(3253, 6109, 0)),
    RELLEKKA_ROOFTOP_COURSE("Rellekka Rooftop Course", new WorldPoint(2624, 3677, 0)),
    SEERS_VILLAGE_ROOFTOP_COURSE("Seers' Village Rooftop Course", new WorldPoint(2728, 3488, 0)),
    SHAYZIEN_COURSE("Shayzien Agility Course", new WorldPoint(1551, 3632, 0)),
    VARROCK_ROOFTOP_COURSE("Varrock Rooftop Course", new WorldPoint(3219, 3414, 0)),
    WEREWOLF_AGILITY_COURSE("Werewolf Agility Course", new WorldPoint(3542, 3463, 0)),
    WILDERNESS_AGILITY_COURSE("Wilderness Agility Course", new WorldPoint(2997, 3916, 0));

    private final String tooltip;
    private final WorldPoint location;
    private final boolean rooftopCourse;

    private AgilityCourseLocation(String tooltip, WorldPoint location) {
        this.tooltip = tooltip;
        this.location = location;
        this.rooftopCourse = this.name().contains("ROOFTOP_COURSE");
    }

    public String getTooltip() {
        return this.tooltip;
    }

    public WorldPoint getLocation() {
        return this.location;
    }

    public boolean isRooftopCourse() {
        return this.rooftopCourse;
    }
}

