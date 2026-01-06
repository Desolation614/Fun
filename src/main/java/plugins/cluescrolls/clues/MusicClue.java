/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  net.runelite.api.Actor
 *  net.runelite.api.NPC
 *  net.runelite.api.coords.WorldPoint
 */
package net.runelite.client.plugins.cluescrolls.clues;

import com.google.common.collect.ImmutableList;
import java.awt.Color;
import java.awt.Graphics2D;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.runelite.api.Actor;
import net.runelite.api.NPC;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.cluescrolls.ClueScrollOverlay;
import net.runelite.client.plugins.cluescrolls.ClueScrollPlugin;
import net.runelite.client.plugins.cluescrolls.clues.ClueScroll;
import net.runelite.client.plugins.cluescrolls.clues.LocationClueScroll;
import net.runelite.client.plugins.cluescrolls.clues.NpcClueScroll;
import net.runelite.client.ui.overlay.OverlayUtil;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.PanelComponent;
import net.runelite.client.ui.overlay.components.TitleComponent;

public class MusicClue
extends ClueScroll
implements NpcClueScroll,
LocationClueScroll {
    private static final WorldPoint LOCATION = new WorldPoint(2990, 3384, 0);
    private static final String CECILIA = "Cecilia";
    private static final Pattern SONG_PATTERN = Pattern.compile("<col=ffffff>([A-Za-z !&',.]+)</col>");
    static final List<MusicClue> CLUES = ImmutableList.of((Object)new MusicClue(23155, "Vision"), (Object)new MusicClue(23156, "The Forlorn Homestead"), (Object)new MusicClue(23157, "Tiptoe"), (Object)new MusicClue(23158, "Rugged Terrain"), (Object)new MusicClue(23159, "On the Shore"), (Object)new MusicClue(23160, "Alone"), (Object)new MusicClue(23138, "Karamja Jam"), (Object)new MusicClue(23139, "Faerie"), (Object)new MusicClue(23140, "Forgotten"), (Object)new MusicClue(23141, "Catch Me If You Can"), (Object)new MusicClue(23142, "Cave of Beasts"), (Object)new MusicClue(23143, "Devils May Care"), (Object[])new MusicClue[]{new MusicClue(23174, "Scorpia Dances"), new MusicClue(23175, "Complication"), new MusicClue(23176, "Subterranea"), new MusicClue(23177, "Little Cave of Horrors"), new MusicClue(23178, "Roc and Roll"), new MusicClue(23179, "La Mort"), new MusicClue(23180, "Fossilised"), new MusicClue(23181, "Hells Bells"), new MusicClue(25792, "Regal Pomp"), new MusicClue(28918, "The Moons of Ruin"), new MusicClue(24773, "Lament for the Hallowed"), new MusicClue(26943, "The Pharaoh")});
    private final int itemId;
    private final String song;

    @Override
    public void makeOverlayHint(PanelComponent panelComponent, ClueScrollPlugin plugin) {
        panelComponent.getChildren().add(TitleComponent.builder().text("Music Clue").build());
        panelComponent.getChildren().add(LineComponent.builder().left("NPC:").build());
        panelComponent.getChildren().add(LineComponent.builder().left(CECILIA).leftColor(ClueScrollOverlay.TITLED_CONTENT_COLOR).build());
        panelComponent.getChildren().add(LineComponent.builder().left("Location:").build());
        panelComponent.getChildren().add(LineComponent.builder().left("Falador Park").leftColor(ClueScrollOverlay.TITLED_CONTENT_COLOR).build());
        panelComponent.getChildren().add(LineComponent.builder().left("Song:").build());
        panelComponent.getChildren().add(LineComponent.builder().left(this.song).leftColor(ClueScrollOverlay.TITLED_CONTENT_COLOR).build());
    }

    @Override
    public void makeWorldOverlayHint(Graphics2D graphics, ClueScrollPlugin plugin) {
        if (!LOCATION.isInScene(plugin.getClient())) {
            return;
        }
        for (NPC npc : plugin.getNpcsToMark()) {
            OverlayUtil.renderActorOverlayImage(graphics, (Actor)npc, plugin.getClueScrollImage(), Color.ORANGE, 30);
        }
    }

    public static MusicClue forItemId(int itemId) {
        for (MusicClue clue : CLUES) {
            if (clue.itemId != itemId) continue;
            return clue;
        }
        return null;
    }

    public static MusicClue forSong(String song) {
        for (MusicClue clue : CLUES) {
            if (!clue.song.equals(song)) continue;
            return clue;
        }
        return null;
    }

    @Override
    public String[] getNpcs(ClueScrollPlugin plugin) {
        return new String[]{CECILIA};
    }

    public static MusicClue forText(String text) {
        Matcher m = SONG_PATTERN.matcher(text);
        if (m.find()) {
            String song = m.group(1);
            MusicClue clue = MusicClue.forSong(song);
            if (clue != null) {
                return clue;
            }
            return new MusicClue(-1, song);
        }
        return null;
    }

    @Override
    public WorldPoint getLocation(ClueScrollPlugin plugin) {
        return LOCATION;
    }

    private MusicClue(int itemId, String song) {
        this.itemId = itemId;
        this.song = song;
    }

    public int getItemId() {
        return this.itemId;
    }

    public String getSong() {
        return this.song;
    }
}

