/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  net.runelite.api.Client
 *  net.runelite.api.coords.LocalPoint
 *  net.runelite.api.coords.WorldPoint
 */
package net.runelite.client.plugins.cluescrolls.clues;

import com.google.common.collect.ImmutableList;
import java.awt.Color;
import java.awt.Graphics2D;
import java.util.List;
import net.runelite.api.Client;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.cluescrolls.ClueScrollOverlay;
import net.runelite.client.plugins.cluescrolls.ClueScrollPlugin;
import net.runelite.client.plugins.cluescrolls.clues.ClueScroll;
import net.runelite.client.plugins.cluescrolls.clues.LocationClueScroll;
import net.runelite.client.ui.overlay.OverlayUtil;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.PanelComponent;
import net.runelite.client.ui.overlay.components.TitleComponent;

public class FairyRingClue
extends ClueScroll
implements LocationClueScroll {
    static final List<FairyRingClue> CLUES = ImmutableList.of((Object)new FairyRingClue(19864, "A I R 2 3 3 1", new WorldPoint(2702, 3246, 0)), (Object)new FairyRingClue(19862, "A I Q 0 4 4 0", new WorldPoint(3000, 3110, 0)), (Object)new FairyRingClue(28915, "A J P 3 0 1 5", new WorldPoint(1646, 3012, 0)), (Object)new FairyRingClue(19866, "A L P 1 1 4 0", new WorldPoint(2504, 3633, 0)), (Object)new FairyRingClue(19872, "B L P 6 2 0 0", new WorldPoint(2439, 5132, 0)), (Object)new FairyRingClue(19870, "B J R 1 1 2 3", new WorldPoint(2648, 4729, 0)), (Object)new FairyRingClue(19868, "B I P 7 0 1 3", new WorldPoint(3407, 3330, 0)), (Object)new FairyRingClue(19880, "C I S 0 0 0 9", new WorldPoint(1630, 3868, 0)), (Object)new FairyRingClue(19874, "C K P 0 2 2 4", new WorldPoint(2073, 4846, 0)), (Object)new FairyRingClue(19876, "D I P 8 5 1 1", new WorldPoint(3041, 4770, 0)), (Object)new FairyRingClue(19878, "D K S 2 3 1 0", new WorldPoint(2747, 3720, 0)));
    private final int itemId;
    private final String text;
    private final WorldPoint location;

    private FairyRingClue(int itemId, String text, WorldPoint location) {
        this.itemId = itemId;
        this.text = text;
        this.location = location;
        this.setRequiresSpade(true);
    }

    @Override
    public WorldPoint getLocation(ClueScrollPlugin plugin) {
        return this.location;
    }

    @Override
    public void makeOverlayHint(PanelComponent panelComponent, ClueScrollPlugin plugin) {
        panelComponent.getChildren().add(TitleComponent.builder().text("Fairy Ring Clue").build());
        panelComponent.getChildren().add(LineComponent.builder().left("Code:").build());
        panelComponent.getChildren().add(LineComponent.builder().left(this.getText().substring(0, 5)).leftColor(ClueScrollOverlay.TITLED_CONTENT_COLOR).build());
        panelComponent.getChildren().add(LineComponent.builder().left("Travel to the fairy ring to see where to dig.").build());
        this.renderOverlayNote(panelComponent, plugin);
    }

    @Override
    public void makeWorldOverlayHint(Graphics2D graphics, ClueScrollPlugin plugin) {
        LocalPoint localLocation = LocalPoint.fromWorld((Client)plugin.getClient(), (WorldPoint)this.getLocation(plugin));
        if (localLocation == null) {
            return;
        }
        OverlayUtil.renderTileOverlay(plugin.getClient(), graphics, localLocation, plugin.getSpadeImage(), Color.ORANGE);
    }

    public static FairyRingClue forItemId(int itemId) {
        for (FairyRingClue clue : CLUES) {
            if (clue.itemId != itemId) continue;
            return clue;
        }
        return null;
    }

    @Override
    public int[] getConfigKeys() {
        return new int[]{this.text.hashCode()};
    }

    public static FairyRingClue forText(String text) {
        for (FairyRingClue clue : CLUES) {
            if (!clue.text.equalsIgnoreCase(text)) continue;
            return clue;
        }
        return null;
    }

    int getItemId() {
        return this.itemId;
    }

    public String getText() {
        return this.text;
    }
}

