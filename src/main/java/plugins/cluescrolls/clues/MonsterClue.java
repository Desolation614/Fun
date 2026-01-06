/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableSet
 *  net.runelite.api.Client
 *  net.runelite.api.coords.LocalPoint
 *  net.runelite.api.coords.WorldPoint
 */
package net.runelite.client.plugins.cluescrolls.clues;

import com.google.common.collect.ImmutableSet;
import java.awt.Color;
import java.awt.Graphics2D;
import java.util.Set;
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

public class MonsterClue
extends ClueScroll
implements LocationClueScroll {
    private static final Set<MonsterClue> CLUES = ImmutableSet.of((Object)new MonsterClue("Gather information by killing Lava Dragons on Lava Dragon Isle.", new WorldPoint(3198, 3827, 0), "Lava dragons", "Lava Dragon Isle"), (Object)new MonsterClue("Gather information by killing Revenants.", new WorldPoint(3224, 10132, 0), "Revenants", "Revenant Caves"), (Object)new MonsterClue("Gather information by killing Elder chaos druids.", new WorldPoint(3238, 3612, 0), "Elder chaos druids", "Chaos Temple"), (Object)new MonsterClue("Gather information by killing Zombies in the Graveyard of Shadows.", new WorldPoint(3164, 3672, 0), "Zombies", "Graveyard of Shadows"), (Object)new MonsterClue("Gather information by killing Ice monsters at the Frozen Waste Plateau.", new WorldPoint(2954, 3897, 0), "Ice Monsters", "Frozen Waste Plateau"), (Object)new MonsterClue("Gather information by killing hellhounds south of the resource arena.", new WorldPoint(3185, 3914, 0), "Hellhounds", "South of Resource Arena"), (Object[])new MonsterClue[]{new MonsterClue("Gather information by killing hill giants in the deep wilderness dungeon.", new WorldPoint(3045, 10318, 0), "Hill Giants", "Deep Wilderness Dungeon")});
    private final String text;
    private final WorldPoint location;
    private final String monster;
    private final String textLocation;

    public MonsterClue(String text, WorldPoint location, String monster, String textLocation) {
        this.text = text;
        this.location = location;
        this.monster = monster;
        this.textLocation = textLocation;
    }

    @Override
    public void makeOverlayHint(PanelComponent panelComponent, ClueScrollPlugin plugin) {
        panelComponent.getChildren().add(TitleComponent.builder().text("PvM Clue").build());
        panelComponent.getChildren().add(LineComponent.builder().left("Monster:").build());
        panelComponent.getChildren().add(LineComponent.builder().left(this.getMonster()).leftColor(ClueScrollOverlay.TITLED_CONTENT_COLOR).build());
        panelComponent.getChildren().add(LineComponent.builder().left("Location:").build());
        panelComponent.getChildren().add(LineComponent.builder().left(this.getTextLocation()).leftColor(ClueScrollOverlay.TITLED_CONTENT_COLOR).build());
        panelComponent.getChildren().add(LineComponent.builder().left("Objective:").build());
        panelComponent.getChildren().add(LineComponent.builder().left("Kill the required monster a few times in order to progress to the next clue step.").leftColor(ClueScrollOverlay.TITLED_CONTENT_COLOR).build());
    }

    @Override
    public void makeWorldOverlayHint(Graphics2D graphics, ClueScrollPlugin plugin) {
        LocalPoint localPoint = LocalPoint.fromWorld((Client)plugin.getClient(), (WorldPoint)this.getLocation());
        if (localPoint != null) {
            OverlayUtil.renderTileOverlay(plugin.getClient(), graphics, localPoint, plugin.getClueScrollImage(), Color.ORANGE);
        }
    }

    @Override
    public WorldPoint getLocation(ClueScrollPlugin plugin) {
        return this.location;
    }

    public static MonsterClue forText(String text) {
        for (MonsterClue clue : CLUES) {
            if (!clue.getText().equalsIgnoreCase(text)) continue;
            return clue;
        }
        return null;
    }

    public String getText() {
        return this.text;
    }

    public WorldPoint getLocation() {
        return this.location;
    }

    public String getMonster() {
        return this.monster;
    }

    public String getTextLocation() {
        return this.textLocation;
    }
}

