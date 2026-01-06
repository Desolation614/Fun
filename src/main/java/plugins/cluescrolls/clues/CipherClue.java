/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.collect.ImmutableList
 *  javax.annotation.Nullable
 *  net.runelite.api.Actor
 *  net.runelite.api.NPC
 *  net.runelite.api.NPCComposition
 *  net.runelite.api.Quest
 *  net.runelite.api.QuestState
 *  net.runelite.api.coords.WorldPoint
 */
package net.runelite.client.plugins.cluescrolls.clues;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableList;
import java.awt.Color;
import java.awt.Graphics2D;
import java.util.List;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.runelite.api.Actor;
import net.runelite.api.NPC;
import net.runelite.api.NPCComposition;
import net.runelite.api.Quest;
import net.runelite.api.QuestState;
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

public class CipherClue
extends ClueScroll
implements NpcClueScroll,
LocationClueScroll {
    static final List<CipherClue> CLUES = ImmutableList.of((Object)CipherClue.builder().itemId(19768).text("BMJ UIF LFCBC TFMMFS").npc(11875).location(new WorldPoint(3354, 2974, 0)).area("Pollnivneach").question("How many coins would you need to purchase 133 kebabs from me?").answer("399").build(), (Object)CipherClue.builder().itemId(19772).text("GUHCHO").npc(9636).location(new WorldPoint(3440, 9895, 0)).area("Paterdomus").question("Please solve this for x: 7x - 28=21").answer("7").build(), (Object)CipherClue.builder().itemId(19770).text("HQNM LZM STSNQ").npc(311).location(new WorldPoint(3227, 3227, 0)).area("Outside Lumbridge castle").question("How many snakeskins are needed in order to craft 44 boots, 29 vambraces and 34 bandanas?").answer("666").build(), (Object)CipherClue.builder().itemId(19904).text("ZHLUG ROG PDQ").npc(954).location(new WorldPoint(3224, 3112, 0)).area("Kalphite Lair entrance. Fairy ring BIQ").question("SIX LEGS! All of them have 6! There are 25 of them! How many legs?").answer("150").build(), (Object)CipherClue.builder().itemId(19766).text("ECRVCKP MJCNGF").npc(6971).location(new WorldPoint(1845, 3754, 0)).area("Large eastern building in Port Piscarilius").question("How many fishing cranes can you find around here?").answer("5").build(), (Object)CipherClue.builder().itemId(19898).text("OVEXON").npc(5304).locationProvider(plugin -> CipherClue.isElunedInPrifddinas(plugin) ? new WorldPoint(3229, 6062, 0) : new WorldPoint(2289, 3144, 0)).areaProvider(plugin -> CipherClue.isElunedInPrifddinas(plugin) ? "Prifddinas" : "Outside Lletya").question("A question on elven crystal math. I have 5 and 3 crystals, large and small respectively. A large crystal is worth 10,000 coins and a small is worth but 1,000. How much are all my crystals worth?").answer("53,000").build(), (Object)CipherClue.builder().itemId(19906).text("VTYR APCNTGLW").npc(4058).location(new WorldPoint(2634, 4682, 1)).area("Fisher Realm, first floor. Fairy ring BJR").question("How many cannons are on this here castle?").answer("5").build(), (Object)CipherClue.builder().itemId(19900).text("UZZU MUJHRKYYKJ").npc(2914).location(new WorldPoint(2501, 3487, 0)).area("Otto's Grotto").question("How many pyre sites are found around this lake?").answer("3").build(), (Object)CipherClue.builder().itemId(19764).text("XJABSE USBJCPSO").npc(5081).location(new WorldPoint(3112, 3162, 0)).area("First floor of Wizards Tower. Fairy ring DIS").question("How many air runes would I need to cast 630 wind waves?").answer("3150").build(), (Object)CipherClue.builder().itemId(19908).text("HCKTA IQFHCVJGT").npc(1840).location(new WorldPoint(2446, 4428, 0)).area("Zanaris throne room").question("There are 3 inputs and 4 letters on each ring How many total individual fairy ring codes are possible?").answer("64").build(), (Object)CipherClue.builder().itemId(19902).text("ZSBKDO ZODO").npc(601).location(new WorldPoint(3680, 3537, 0)).area("Dock northeast of the Ectofuntus").build(), (Object)CipherClue.builder().itemId(19910).text("GBJSZ RVFFO").npc(1161).location(new WorldPoint(2347, 4435, 0)).area("Fairy Resistance Hideout").build(), (Object[])new CipherClue[]{CipherClue.builder().itemId(19762).text("QSPGFTTPS HSBDLMFCPOF").npc(7048).location(new WorldPoint(1625, 3802, 0)).area("Ground floor of Arceuus Library").question("How many round tables can be found on this floor of the library?").answer("9").build(), CipherClue.builder().itemId(23172).text("IWPPLQTP").npc(2153).location(new WorldPoint(2541, 3548, 0)).area("Barbarian Outpost Agility course").build(), CipherClue.builder().itemId(23170).text("BSOPME MZETQPS").npc(4293).location(new WorldPoint(2329, 3689, 0)).area("Piscatoris Fishing Colony general store/bank").build(), CipherClue.builder().itemId(23067).text("ESBZOPS QJH QFO").location(new WorldPoint(3077, 3260, 0)).area("Inside of Martin the Master Gardener's pig pen in Draynor Village.").build(), CipherClue.builder().itemId(28916).text("BXJA UNJMNA YRCAR").npc(13135).location(new WorldPoint(1559, 3045, 0)).area("Top of the Hunter Guild").build(), CipherClue.builder().itemId(30929).text("YAROYGR").npc(14762).location(new WorldPoint(1376, 3037, 0)).area("Kastori").question("If a fish can feed four people, and I'm hosting 15 guests, how many whole fish will I need?").answer("4").build()});
    private final int itemId;
    private final String text;
    private final int npc;
    private final Function<ClueScrollPlugin, WorldPoint> locationProvider;
    private final Function<ClueScrollPlugin, String> areaProvider;
    @Nullable
    private final String question;
    @Nullable
    private final String answer;

    private CipherClue(@Nullable Integer itemId, String text, Integer npc, @Nullable WorldPoint location, @Nullable Function<ClueScrollPlugin, WorldPoint> locationProvider, @Nullable String area, @Nullable Function<ClueScrollPlugin, String> areaProvider, @Nullable String question, @Nullable String answer) {
        this.itemId = itemId != null ? itemId : -1;
        this.text = "The cipher reveals who to speak to next: " + text;
        this.npc = npc != null ? npc : -1;
        this.locationProvider = locationProvider != null ? locationProvider : plugin -> location;
        this.areaProvider = areaProvider != null ? areaProvider : plugin -> area;
        this.question = question;
        this.answer = answer;
    }

    @Override
    public WorldPoint getLocation(ClueScrollPlugin plugin) {
        return this.locationProvider.apply(plugin);
    }

    @VisibleForTesting
    String getArea(ClueScrollPlugin plugin) {
        return this.areaProvider.apply(plugin);
    }

    @Override
    public void makeOverlayHint(PanelComponent panelComponent, ClueScrollPlugin plugin) {
        panelComponent.getChildren().add(TitleComponent.builder().text("Cipher Clue").build());
        NPCComposition npc = this.getNpcComposition(plugin);
        if (npc != null) {
            panelComponent.getChildren().add(LineComponent.builder().left("NPC:").build());
            panelComponent.getChildren().add(LineComponent.builder().left(npc.getName()).leftColor(ClueScrollOverlay.TITLED_CONTENT_COLOR).build());
        }
        panelComponent.getChildren().add(LineComponent.builder().left("Location:").build());
        panelComponent.getChildren().add(LineComponent.builder().left(this.getArea(plugin)).leftColor(ClueScrollOverlay.TITLED_CONTENT_COLOR).build());
        if (this.getAnswer() != null) {
            panelComponent.getChildren().add(LineComponent.builder().left("Answer:").build());
            panelComponent.getChildren().add(LineComponent.builder().left(this.getAnswer()).leftColor(ClueScrollOverlay.TITLED_CONTENT_COLOR).build());
        }
        this.renderOverlayNote(panelComponent, plugin);
    }

    @Override
    public void makeWorldOverlayHint(Graphics2D graphics, ClueScrollPlugin plugin) {
        if (!this.getLocation(plugin).isInScene(plugin.getClient())) {
            return;
        }
        if (plugin.getNpcsToMark() != null) {
            for (NPC npc : plugin.getNpcsToMark()) {
                OverlayUtil.renderActorOverlayImage(graphics, (Actor)npc, plugin.getClueScrollImage(), Color.ORANGE, 30);
            }
        }
    }

    public static CipherClue forItemId(int itemId) {
        for (CipherClue clue : CLUES) {
            if (clue.itemId != itemId) continue;
            return clue;
        }
        return null;
    }

    public static CipherClue forText(String text) {
        for (CipherClue clue : CLUES) {
            if (!text.equalsIgnoreCase(clue.text) && !text.equalsIgnoreCase(clue.question)) continue;
            return clue;
        }
        return null;
    }

    @Override
    public String[] getNpcs(ClueScrollPlugin plugin) {
        NPCComposition comp = this.getNpcComposition(plugin);
        return new String[]{comp == null ? null : comp.getName()};
    }

    @Override
    public int[] getConfigKeys() {
        return new int[]{this.text.hashCode()};
    }

    private NPCComposition getNpcComposition(ClueScrollPlugin plugin) {
        if (this.npc == -1) {
            return null;
        }
        NPCComposition composition = plugin.getClient().getNpcDefinition(this.npc);
        if (composition.getConfigs() != null) {
            composition = composition.transform();
        }
        return composition;
    }

    private static boolean isElunedInPrifddinas(ClueScrollPlugin plugin) {
        return Quest.SONG_OF_THE_ELVES.getState(plugin.getClient()) == QuestState.FINISHED;
    }

    public static CipherClueBuilder builder() {
        return new CipherClueBuilder();
    }

    public int getItemId() {
        return this.itemId;
    }

    public String getText() {
        return this.text;
    }

    public int getNpc() {
        return this.npc;
    }

    public Function<ClueScrollPlugin, WorldPoint> getLocationProvider() {
        return this.locationProvider;
    }

    public Function<ClueScrollPlugin, String> getAreaProvider() {
        return this.areaProvider;
    }

    @Nullable
    public String getQuestion() {
        return this.question;
    }

    @Nullable
    public String getAnswer() {
        return this.answer;
    }

    public static class CipherClueBuilder {
        private Integer itemId;
        private String text;
        private Integer npc;
        private WorldPoint location;
        private Function<ClueScrollPlugin, WorldPoint> locationProvider;
        private String area;
        private Function<ClueScrollPlugin, String> areaProvider;
        private String question;
        private String answer;

        CipherClueBuilder() {
        }

        public CipherClueBuilder itemId(@Nullable Integer itemId) {
            this.itemId = itemId;
            return this;
        }

        public CipherClueBuilder text(String text) {
            this.text = text;
            return this;
        }

        public CipherClueBuilder npc(Integer npc) {
            this.npc = npc;
            return this;
        }

        public CipherClueBuilder location(@Nullable WorldPoint location) {
            this.location = location;
            return this;
        }

        public CipherClueBuilder locationProvider(@Nullable Function<ClueScrollPlugin, WorldPoint> locationProvider) {
            this.locationProvider = locationProvider;
            return this;
        }

        public CipherClueBuilder area(@Nullable String area) {
            this.area = area;
            return this;
        }

        public CipherClueBuilder areaProvider(@Nullable Function<ClueScrollPlugin, String> areaProvider) {
            this.areaProvider = areaProvider;
            return this;
        }

        public CipherClueBuilder question(@Nullable String question) {
            this.question = question;
            return this;
        }

        public CipherClueBuilder answer(@Nullable String answer) {
            this.answer = answer;
            return this;
        }

        public CipherClue build() {
            return new CipherClue(this.itemId, this.text, this.npc, this.location, this.locationProvider, this.area, this.areaProvider, this.question, this.answer);
        }

        public String toString() {
            return "CipherClue.CipherClueBuilder(itemId=" + this.itemId + ", text=" + this.text + ", npc=" + this.npc + ", location=" + String.valueOf(this.location) + ", locationProvider=" + String.valueOf(this.locationProvider) + ", area=" + this.area + ", areaProvider=" + String.valueOf(this.areaProvider) + ", question=" + this.question + ", answer=" + this.answer + ")";
        }
    }
}

