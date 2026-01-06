/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.runelite.api.Actor
 *  net.runelite.api.Client
 *  net.runelite.api.NPC
 *  net.runelite.api.coords.LocalPoint
 *  net.runelite.api.coords.WorldPoint
 */
package net.runelite.client.plugins.cluescrolls.clues;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.Set;
import java.util.stream.Collectors;
import net.runelite.api.Actor;
import net.runelite.api.Client;
import net.runelite.api.NPC;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.cluescrolls.ClueScrollOverlay;
import net.runelite.client.plugins.cluescrolls.ClueScrollPlugin;
import net.runelite.client.plugins.cluescrolls.clues.ClueScroll;
import net.runelite.client.plugins.cluescrolls.clues.LocationClueScroll;
import net.runelite.client.plugins.cluescrolls.clues.LocationsClueScroll;
import net.runelite.client.plugins.cluescrolls.clues.NpcClueScroll;
import net.runelite.client.plugins.cluescrolls.clues.hotcold.HotColdArea;
import net.runelite.client.plugins.cluescrolls.clues.hotcold.HotColdLocation;
import net.runelite.client.plugins.cluescrolls.clues.hotcold.HotColdSolver;
import net.runelite.client.plugins.cluescrolls.clues.hotcold.HotColdTemperature;
import net.runelite.client.plugins.cluescrolls.clues.hotcold.HotColdTemperatureChange;
import net.runelite.client.ui.overlay.OverlayUtil;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.PanelComponent;
import net.runelite.client.ui.overlay.components.TitleComponent;

public class HotColdClue
extends ClueScroll
implements LocationClueScroll,
LocationsClueScroll,
NpcClueScroll {
    private static final HotColdClue BEGINNER_CLUE = new HotColdClue("Buried beneath the ground, who knows where it's found. Lucky for you, A man called Reldo may have a clue.", "Reldo", "Speak to Reldo to receive a strange device.", new WorldPoint(3211, 3494, 0), true);
    private static final HotColdClue MASTER_CLUE = new HotColdClue("Buried beneath the ground, who knows where it's found. Lucky for you, A man called Jorral may have a clue.", "Jorral", "Speak to Jorral to receive a strange device.", new WorldPoint(2436, 3347, 0), false);
    private final String text;
    private final String npc;
    private final String solution;
    private final WorldPoint npcLocation;
    private final boolean isBeginner;
    private HotColdSolver hotColdSolver;
    private WorldPoint location;

    public static HotColdClue forText(String text) {
        if (HotColdClue.BEGINNER_CLUE.text.equalsIgnoreCase(text)) {
            BEGINNER_CLUE.reset();
            return BEGINNER_CLUE;
        }
        if (HotColdClue.MASTER_CLUE.text.equalsIgnoreCase(text)) {
            MASTER_CLUE.reset();
            return MASTER_CLUE;
        }
        return null;
    }

    private HotColdClue(String text, String npc, String solution, WorldPoint npcLocation, boolean isBeginner) {
        this.text = text;
        this.npc = npc;
        this.solution = solution;
        this.npcLocation = npcLocation;
        this.isBeginner = isBeginner;
        this.setRequiresSpade(true);
    }

    @Override
    public WorldPoint getLocation(ClueScrollPlugin plugin) {
        return this.location;
    }

    @Override
    public WorldPoint[] getLocations(ClueScrollPlugin plugin) {
        if (this.hotColdSolver.getLastWorldPoint() == null) {
            return new WorldPoint[]{this.npcLocation};
        }
        return (WorldPoint[])this.hotColdSolver.getPossibleLocations().stream().map(HotColdLocation::getWorldPoint).toArray(WorldPoint[]::new);
    }

    @Override
    public void makeOverlayHint(PanelComponent panelComponent, ClueScrollPlugin plugin) {
        panelComponent.getChildren().add(TitleComponent.builder().text("Hot/Cold Clue").build());
        if (this.hotColdSolver.getLastWorldPoint() == null && this.location == null) {
            if (this.getNpc() != null) {
                panelComponent.getChildren().add(LineComponent.builder().left("NPC:").build());
                panelComponent.getChildren().add(LineComponent.builder().left(this.getNpc()).leftColor(ClueScrollOverlay.TITLED_CONTENT_COLOR).build());
            }
            panelComponent.getChildren().add(LineComponent.builder().left("Solution:").build());
            panelComponent.getChildren().add(LineComponent.builder().left(this.getSolution()).leftColor(ClueScrollOverlay.TITLED_CONTENT_COLOR).build());
        } else {
            panelComponent.getChildren().add(LineComponent.builder().left("Possible locations:").build());
            EnumMap<HotColdArea, Integer> locationCounts = new EnumMap<HotColdArea, Integer>(HotColdArea.class);
            Set<HotColdLocation> digLocations = this.hotColdSolver.getPossibleLocations();
            for (HotColdLocation hotColdLocation : digLocations) {
                HotColdArea hotColdArea = hotColdLocation.getHotColdArea();
                if (locationCounts.containsKey((Object)hotColdArea)) {
                    locationCounts.put(hotColdArea, (Integer)locationCounts.get((Object)hotColdArea) + 1);
                    continue;
                }
                locationCounts.put(hotColdArea, 1);
            }
            if (digLocations.size() > 10) {
                for (HotColdArea area : locationCounts.keySet()) {
                    panelComponent.getChildren().add(LineComponent.builder().left(area.getName()).right(Integer.toString((Integer)locationCounts.get((Object)area))).build());
                }
            } else {
                for (HotColdArea area : locationCounts.keySet()) {
                    panelComponent.getChildren().add(LineComponent.builder().left(area.getName() + ":").build());
                    for (HotColdLocation hotColdLocation : digLocations) {
                        if (hotColdLocation.getHotColdArea() != area) continue;
                        panelComponent.getChildren().add(LineComponent.builder().left("- " + hotColdLocation.getArea()).leftColor(Color.LIGHT_GRAY).build());
                        if (digLocations.size() > 5 || hotColdLocation.getEnemy() == null) continue;
                        panelComponent.getChildren().add(LineComponent.builder().left(hotColdLocation.getEnemy().getText()).leftColor(Color.YELLOW).build());
                    }
                }
            }
        }
    }

    @Override
    public void makeWorldOverlayHint(Graphics2D graphics, ClueScrollPlugin plugin) {
        Set<HotColdLocation> digLocations;
        if (this.location != null) {
            LocalPoint localLocation = LocalPoint.fromWorld((Client)plugin.getClient(), (WorldPoint)this.getLocation());
            if (localLocation != null) {
                OverlayUtil.renderTileOverlay(plugin.getClient(), graphics, localLocation, plugin.getSpadeImage(), Color.ORANGE);
            }
            return;
        }
        if (this.hotColdSolver.getLastWorldPoint() == null && plugin.getNpcsToMark() != null) {
            for (NPC npcToMark : plugin.getNpcsToMark()) {
                OverlayUtil.renderActorOverlayImage(graphics, (Actor)npcToMark, plugin.getClueScrollImage(), Color.ORANGE, 30);
            }
        }
        if ((digLocations = this.hotColdSolver.getPossibleLocations()).size() < 10) {
            for (HotColdLocation hotColdLocation : digLocations) {
                WorldPoint wp = hotColdLocation.getWorldPoint();
                LocalPoint localLocation = LocalPoint.fromWorld((Client)plugin.getClient(), (int)wp.getX(), (int)wp.getY());
                if (localLocation == null) {
                    return;
                }
                OverlayUtil.renderTileOverlay(plugin.getClient(), graphics, localLocation, plugin.getSpadeImage(), Color.ORANGE);
            }
        }
    }

    public boolean update(String message, ClueScrollPlugin plugin) {
        Set<HotColdTemperature> temperatureSet = this.isBeginner() ? HotColdTemperature.BEGINNER_HOT_COLD_TEMPERATURES : HotColdTemperature.MASTER_HOT_COLD_TEMPERATURES;
        HotColdTemperature temperature = HotColdTemperature.getFromTemperatureSet(temperatureSet, message);
        if (temperature == null) {
            return false;
        }
        WorldPoint localWorld = WorldPoint.getMirrorPoint((WorldPoint)plugin.getClient().getLocalPlayer().getWorldLocation(), (boolean)true);
        if (this.isBeginner() && temperature == HotColdTemperature.BEGINNER_VISIBLY_SHAKING || !this.isBeginner() && temperature == HotColdTemperature.MASTER_VISIBLY_SHAKING) {
            this.markFinalSpot(localWorld);
        } else {
            this.location = null;
        }
        HotColdTemperatureChange temperatureChange = HotColdTemperatureChange.of(message);
        this.hotColdSolver.signal(localWorld, temperature, temperatureChange);
        return true;
    }

    @Override
    public void reset() {
        this.location = null;
        this.initializeSolver();
    }

    private void initializeSolver() {
        Set<HotColdLocation> locations = Arrays.stream(HotColdLocation.values()).filter(l -> l.isBeginnerClue() == this.isBeginner()).collect(Collectors.toSet());
        this.hotColdSolver = new HotColdSolver(locations);
    }

    private void markFinalSpot(WorldPoint wp) {
        this.location = wp;
    }

    @Override
    public String[] getNpcs(ClueScrollPlugin plugin) {
        return new String[]{this.npc};
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof HotColdClue)) {
            return false;
        }
        HotColdClue other = (HotColdClue)o;
        if (!other.canEqual(this)) {
            return false;
        }
        if (this.isBeginner() != other.isBeginner()) {
            return false;
        }
        String this$text = this.getText();
        String other$text = other.getText();
        if (this$text == null ? other$text != null : !this$text.equals(other$text)) {
            return false;
        }
        String this$npc = this.getNpc();
        String other$npc = other.getNpc();
        if (this$npc == null ? other$npc != null : !this$npc.equals(other$npc)) {
            return false;
        }
        String this$solution = this.getSolution();
        String other$solution = other.getSolution();
        if (this$solution == null ? other$solution != null : !this$solution.equals(other$solution)) {
            return false;
        }
        WorldPoint this$npcLocation = this.getNpcLocation();
        WorldPoint other$npcLocation = other.getNpcLocation();
        return !(this$npcLocation == null ? other$npcLocation != null : !this$npcLocation.equals(other$npcLocation));
    }

    protected boolean canEqual(Object other) {
        return other instanceof HotColdClue;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        result = result * 59 + (this.isBeginner() ? 79 : 97);
        String $text = this.getText();
        result = result * 59 + ($text == null ? 43 : $text.hashCode());
        String $npc = this.getNpc();
        result = result * 59 + ($npc == null ? 43 : $npc.hashCode());
        String $solution = this.getSolution();
        result = result * 59 + ($solution == null ? 43 : $solution.hashCode());
        WorldPoint $npcLocation = this.getNpcLocation();
        result = result * 59 + ($npcLocation == null ? 43 : $npcLocation.hashCode());
        return result;
    }

    public String getText() {
        return this.text;
    }

    public String getNpc() {
        return this.npc;
    }

    public String getSolution() {
        return this.solution;
    }

    public boolean isBeginner() {
        return this.isBeginner;
    }

    public HotColdSolver getHotColdSolver() {
        return this.hotColdSolver;
    }

    public WorldPoint getLocation() {
        return this.location;
    }

    private WorldPoint getNpcLocation() {
        return this.npcLocation;
    }
}

