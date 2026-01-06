/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  net.runelite.api.Client
 *  net.runelite.api.ObjectComposition
 *  net.runelite.api.Point
 *  net.runelite.api.TileObject
 *  net.runelite.api.coords.LocalPoint
 *  net.runelite.api.coords.WorldPoint
 */
package net.runelite.client.plugins.cluescrolls.clues;

import com.google.common.collect.ImmutableList;
import java.awt.Color;
import java.awt.Graphics2D;
import java.util.List;
import net.runelite.api.Client;
import net.runelite.api.ObjectComposition;
import net.runelite.api.Point;
import net.runelite.api.TileObject;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.cluescrolls.ClueScrollPlugin;
import net.runelite.client.plugins.cluescrolls.ClueScrollWorldOverlay;
import net.runelite.client.plugins.cluescrolls.clues.ClueScroll;
import net.runelite.client.plugins.cluescrolls.clues.ObjectClueScroll;
import net.runelite.client.ui.overlay.OverlayUtil;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.PanelComponent;
import net.runelite.client.ui.overlay.components.TitleComponent;

public class MapClue
extends ClueScroll
implements ObjectClueScroll {
    public static final String CHAMPIONS_GUILD = "West of the Champions' Guild";
    public static final String VARROCK_EAST_MINE = "Outside Varrock East Mine";
    public static final String STANDING_STONES = "At the standing stones north of Falador";
    public static final String WIZARDS_TOWER_DIS = "On the south side of the Wizards' Tower (DIS)";
    public static final String SOUTH_OF_DRAYNOR_BANK = "South of Draynor Village Bank";
    static final List<MapClue> CLUES = ImmutableList.of((Object)new MapClue(12179, new WorldPoint(3300, 3291, 0), "Al Kharid mine"), (Object)new MapClue(2713, new WorldPoint(3166, 3361, 0), "West of the Champions' Guild"), (Object)new MapClue(2716, new WorldPoint(3290, 3374, 0), "Outside Varrock East Mine"), (Object)new MapClue(2719, new WorldPoint(3043, 3398, 0), "At the standing stones north of Falador"), (Object)new MapClue(3516, new WorldPoint(2612, 3482, 0), "Brother Galahad's house, West of McGrubor's Wood."), (Object)new MapClue(3518, new WorldPoint(3110, 3152, 0), "On the south side of the Wizards' Tower (DIS)"), (Object)new MapClue(7236, new WorldPoint(2970, 3415, 0), "North of Falador."), (Object)new MapClue(2827, new WorldPoint(3091, 3227, 0), "South of Draynor Village Bank"), (Object)new MapClue(3596, new WorldPoint(2907, 3295, 0), "West of the Crafting Guild"), (Object)new MapClue(3598, new WorldPoint(2658, 3488, 0), 357, "The crate in McGrubor's Wood. Fairy ring ALS"), (Object)new MapClue(3599, new WorldPoint(2651, 3231, 0), "North of the Tower of Life. Fairy ring DJP"), (Object)new MapClue(3601, new WorldPoint(2565, 3248, 0), 354, "The crate west of the Clocktower."), (Object[])new MapClue[]{new MapClue(3602, new WorldPoint(2924, 3210, 0), "Behind the Chemist's house in Rimmington."), new MapClue(7286, new WorldPoint(2536, 3865, 0), "Miscellania. Fairy ring CIP"), new MapClue(7288, new WorldPoint(3434, 3265, 0), "Mort Myre Swamp, west of Mort'ton. Fairy ring BIP"), new MapClue(7290, new WorldPoint(2454, 3230, 0), "At the entrance to the Ourania Cave."), new MapClue(7292, new WorldPoint(2578, 3597, 0), "South-east of the Lighthouse. Fairy ring ALP"), new MapClue(7294, new WorldPoint(2666, 3562, 0), "Between Seers' Village and Rellekka. South-west of Fairy ring CJR"), new MapClue(2722, new WorldPoint(3309, 3503, 0), 2620, "A crate in the Lumber Yard, north-east of Varrock."), new MapClue(3520, new WorldPoint(2615, 3078, 0), "Yanille anvils, south of the bank. You can dig from inside the building."), new MapClue(3522, new WorldPoint(2488, 3308, 0), "In the western section of West Ardougne."), new MapClue(3524, new WorldPoint(2457, 3182, 0), 18506, "In a crate by the stairs to the Observatory Dungeon."), new MapClue(3525, new WorldPoint(3026, 3628, 0), 354, "In a crate at the Dark Warriors' Fortress in level 14 Wilderness."), new MapClue(7239, new WorldPoint(3021, 3912, 0), "South-east of the Wilderness Agility Course in level 50 Wilderness."), new MapClue(7241, new WorldPoint(2722, 3338, 0), "South of the Legends' Guild. Fairy ring BLR"), new MapClue(12130, new WorldPoint(2449, 3130, 0), "South-west of Tree Gnome Village."), new MapClue(19782, new WorldPoint(2953, 9523, 1), "In the Mogre Camp, near Port Khazard. You require a Diving Apparatus and a Fishbowl Helmet"), new MapClue(19783, new WorldPoint(2202, 3062, 0), "Zul-Andra. Fairy ring BJS"), new MapClue(19784, new WorldPoint(1815, 3852, 0), "At the Soul Altar, north-east of the Arceuus essence mine."), new MapClue(19785, new WorldPoint(3538, 3208, 0), "East of Burgh de Rott."), new MapClue(19786, new WorldPoint(2703, 2716, 0), 6616, "The crate in south-western Ape Atoll"), new MapClue(23068, new WorldPoint(3203, 3213, 0), "Behind Lumbridge Castle, just outside the kitchen door"), new MapClue(23069, new WorldPoint(3108, 3262, 0), "South-west of the wheat field east of Draynor Village.")});
    private final int itemId;
    private final WorldPoint location;
    private final int objectId;
    private final String description;

    private MapClue(int itemId, WorldPoint location, int objectId) {
        this(itemId, location, objectId, null);
    }

    MapClue(int itemId, WorldPoint location, String description) {
        this(itemId, location, -1, description);
    }

    private MapClue(int itemId, WorldPoint location, int objectId, String description) {
        this.itemId = itemId;
        this.location = location;
        this.objectId = objectId;
        this.description = description;
        this.setRequiresSpade(objectId == -1);
    }

    @Override
    public WorldPoint getLocation(ClueScrollPlugin plugin) {
        return this.location;
    }

    @Override
    public void makeOverlayHint(PanelComponent panelComponent, ClueScrollPlugin plugin) {
        panelComponent.getChildren().add(TitleComponent.builder().text("Map Clue").build());
        panelComponent.getChildren().add(LineComponent.builder().left("Click the clue scroll along the edge of your world map to see your destination.").build());
        if (this.objectId != -1) {
            ObjectComposition objectToClick = plugin.getClient().getObjectDefinition(this.getObjectId());
            String objectName = "N/A";
            if (objectToClick != null) {
                objectName = objectToClick.getName();
            }
            panelComponent.getChildren().add(LineComponent.builder().left("Travel to the destination and click the " + objectName + ".").build());
        } else {
            panelComponent.getChildren().add(LineComponent.builder().left("Travel to the destination and dig on the marked tile.").build());
        }
        if (this.description != null) {
            panelComponent.getChildren().add(LineComponent.builder().build());
            panelComponent.getChildren().add(LineComponent.builder().left(this.description).build());
        }
        this.renderOverlayNote(panelComponent, plugin);
    }

    @Override
    public void makeWorldOverlayHint(Graphics2D graphics, ClueScrollPlugin plugin) {
        LocalPoint localLocation = LocalPoint.fromWorld((Client)plugin.getClient(), (WorldPoint)this.getLocation());
        if (localLocation == null) {
            return;
        }
        if (this.objectId != -1) {
            Point mousePosition = plugin.getClient().getMouseCanvasPosition();
            if (plugin.getObjectsToMark() != null) {
                for (TileObject gameObject : plugin.getObjectsToMark()) {
                    OverlayUtil.renderHoverableArea(graphics, gameObject.getClickbox(), mousePosition, ClueScrollWorldOverlay.CLICKBOX_FILL_COLOR, ClueScrollWorldOverlay.CLICKBOX_BORDER_COLOR, ClueScrollWorldOverlay.CLICKBOX_HOVER_BORDER_COLOR);
                    OverlayUtil.renderImageLocation(plugin.getClient(), graphics, gameObject.getLocalLocation(), plugin.getClueScrollImage(), 30);
                }
            }
        } else {
            OverlayUtil.renderTileOverlay(plugin.getClient(), graphics, localLocation, plugin.getSpadeImage(), Color.ORANGE);
        }
    }

    public static MapClue forItemId(int itemId) {
        for (MapClue clue : CLUES) {
            if (clue.itemId != itemId) continue;
            return clue;
        }
        return null;
    }

    @Override
    public int[] getObjectIds() {
        return new int[]{this.objectId};
    }

    @Override
    public int[] getConfigKeys() {
        return new int[]{this.location.hashCode()};
    }

    public int getItemId() {
        return this.itemId;
    }

    public int getObjectId() {
        return this.objectId;
    }

    public String getDescription() {
        return this.description;
    }

    private WorldPoint getLocation() {
        return this.location;
    }
}

