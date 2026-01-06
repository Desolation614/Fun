/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  javax.annotation.Nonnull
 *  net.runelite.api.Actor
 *  net.runelite.api.Item
 *  net.runelite.api.NPC
 *  net.runelite.api.coords.WorldPoint
 */
package net.runelite.client.plugins.cluescrolls.clues;

import com.google.common.collect.ImmutableList;
import java.awt.Color;
import java.awt.Graphics2D;
import java.util.List;
import javax.annotation.Nonnull;
import net.runelite.api.Actor;
import net.runelite.api.Item;
import net.runelite.api.NPC;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.cluescrolls.ClueScrollOverlay;
import net.runelite.client.plugins.cluescrolls.ClueScrollPlugin;
import net.runelite.client.plugins.cluescrolls.clues.ClueScroll;
import net.runelite.client.plugins.cluescrolls.clues.EmoteClue;
import net.runelite.client.plugins.cluescrolls.clues.NpcClueScroll;
import net.runelite.client.plugins.cluescrolls.clues.item.AnyRequirementCollection;
import net.runelite.client.plugins.cluescrolls.clues.item.ItemRequirement;
import net.runelite.client.plugins.cluescrolls.clues.item.RangeItemRequirement;
import net.runelite.client.plugins.cluescrolls.clues.item.SingleItemRequirement;
import net.runelite.client.ui.FontManager;
import net.runelite.client.ui.overlay.OverlayUtil;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.PanelComponent;
import net.runelite.client.ui.overlay.components.TitleComponent;

public class FaloTheBardClue
extends ClueScroll
implements NpcClueScroll {
    static final List<FaloTheBardClue> CLUES = ImmutableList.of((Object)new FaloTheBardClue("A blood red weapon, a strong curved sword, found on the island of primate lords.", FaloTheBardClue.any("Dragon scimitar", FaloTheBardClue.item(4587), FaloTheBardClue.item(20000))), (Object)new FaloTheBardClue("A book that preaches of some great figure, lending strength, might and vigour.", FaloTheBardClue.any("Any completed god book", FaloTheBardClue.item(3840), FaloTheBardClue.item(3844), FaloTheBardClue.item(3842), FaloTheBardClue.item(12610), FaloTheBardClue.item(12608), FaloTheBardClue.item(12612), FaloTheBardClue.item(26496), FaloTheBardClue.item(26488), FaloTheBardClue.item(26498), FaloTheBardClue.item(26492), FaloTheBardClue.item(26494), FaloTheBardClue.item(26490))), (Object)new FaloTheBardClue("A bow of elven craft was made, it shimmers bright, but will soon fade.", FaloTheBardClue.any("Crystal Bow", EmoteClue.ACTIVE_CRYSTAL_BOW_OR_BOW_OF_FAERDHINEN)), (Object)new FaloTheBardClue("A fiery axe of great inferno, when you use it, you'll wonder where the logs go.", FaloTheBardClue.any("Infernal axe", FaloTheBardClue.item(13241), FaloTheBardClue.item(13242), FaloTheBardClue.item(25066), FaloTheBardClue.item(25371), FaloTheBardClue.item(30347), FaloTheBardClue.item(30348))), (Object)new FaloTheBardClue("A mark used to increase one's grace, found atop a seer's place.", FaloTheBardClue.item(11849)), (Object)new FaloTheBardClue("A molten beast with fiery breath, you acquire these with its death.", FaloTheBardClue.item(11943)), (Object)new FaloTheBardClue("A shiny helmet of flight, to obtain this with melee, struggle you might.", FaloTheBardClue.item(11826)), (Object)new FaloTheBardClue("A sword held in the other hand, red its colour, Cyclops strength you must withstand.", FaloTheBardClue.any("Dragon or Avernic Defender", FaloTheBardClue.item(12954), FaloTheBardClue.item(19722), FaloTheBardClue.item(24143), FaloTheBardClue.item(27008), FaloTheBardClue.item(22322), FaloTheBardClue.item(24186), FaloTheBardClue.item(27550), FaloTheBardClue.item(27551), FaloTheBardClue.item(27552), FaloTheBardClue.item(27553))), (Object)new FaloTheBardClue("A token used to kill mythical beasts, in hopes of a blade or just for an xp feast.", FaloTheBardClue.item(8851)), (Object)new FaloTheBardClue("Green is my favourite, mature ale I do love, this takes your herblore above.", FaloTheBardClue.item(5743)), (Object)new FaloTheBardClue("It can hold down a boat or crush a goat, this object, you see, is quite heavy.", FaloTheBardClue.any("Barrelchest anchor", FaloTheBardClue.item(10887), FaloTheBardClue.item(27855))), (Object)new FaloTheBardClue("It comes from the ground, underneath the snowy plain. Trolls aplenty, with what looks like a mane.", FaloTheBardClue.item(22603)), (Object[])new FaloTheBardClue[]{new FaloTheBardClue("No attack to wield, only strength is required, made of obsidian, but with no room for a shield.", FaloTheBardClue.any("Tzhaar-ket-om", FaloTheBardClue.item(6528), FaloTheBardClue.item(23235))), new FaloTheBardClue("Penance healers runners and more, obtaining this body often gives much deplore.", FaloTheBardClue.any("Fighter Torso", FaloTheBardClue.item(10551), FaloTheBardClue.item(24175))), new FaloTheBardClue("Strangely found in a chest, many believe these gloves are the best.", FaloTheBardClue.item(7462)), new FaloTheBardClue("These gloves of white won't help you fight, but aid in cooking, they just might.", FaloTheBardClue.item(775)), new FaloTheBardClue("They come from some time ago, from a land unto the east. Fossilised they have become, this small and gentle beast.", FaloTheBardClue.item(21555)), new FaloTheBardClue("To slay a dragon you must first do, before this chest piece can be put on you.", FaloTheBardClue.item(1127)), new FaloTheBardClue("Vampyres are agile opponents, damaged best with a weapon of many components.", FaloTheBardClue.any("Rod of Ivandis or Ivandis/Blisterwood flail", FaloTheBardClue.range(7639, 7648), FaloTheBardClue.item(22398), FaloTheBardClue.item(24699)))});
    private static final WorldPoint LOCATION = new WorldPoint(2689, 3550, 0);
    private static final String FALO_THE_BARD = "Falo the Bard";
    private final String text;
    @Nonnull
    private final ItemRequirement[] itemRequirements;

    private static SingleItemRequirement item(int itemId) {
        return new SingleItemRequirement(itemId);
    }

    private static AnyRequirementCollection any(String name, ItemRequirement ... requirements) {
        return new AnyRequirementCollection(name, requirements);
    }

    private static RangeItemRequirement range(int startItemId, int endItemId) {
        return FaloTheBardClue.range(null, startItemId, endItemId);
    }

    private static RangeItemRequirement range(String name, int startItemId, int endItemId) {
        return new RangeItemRequirement(name, startItemId, endItemId);
    }

    private FaloTheBardClue(String text, ItemRequirement ... itemRequirements) {
        this.text = text;
        this.itemRequirements = itemRequirements;
    }

    @Override
    public void makeOverlayHint(PanelComponent panelComponent, ClueScrollPlugin plugin) {
        panelComponent.getChildren().add(TitleComponent.builder().text("Falo the Bard Clue").build());
        panelComponent.getChildren().add(LineComponent.builder().left("NPC:").build());
        panelComponent.getChildren().add(LineComponent.builder().left(FALO_THE_BARD).leftColor(ClueScrollOverlay.TITLED_CONTENT_COLOR).build());
        panelComponent.getChildren().add(LineComponent.builder().left("Item:").build());
        Item[] inventory = plugin.getInventoryItems();
        if (inventory == null) {
            inventory = new Item[]{};
        }
        for (ItemRequirement requirement : this.itemRequirements) {
            boolean inventoryFulfilled = requirement.fulfilledBy(inventory);
            panelComponent.getChildren().add(LineComponent.builder().left(requirement.getCollectiveName(plugin.getClient())).leftColor(ClueScrollOverlay.TITLED_CONTENT_COLOR).right(inventoryFulfilled ? "\u2713" : "\u2717").rightFont(FontManager.getDefaultFont()).rightColor(inventoryFulfilled ? Color.GREEN : Color.RED).build());
        }
        this.renderOverlayNote(panelComponent, plugin);
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

    @Override
    public String[] getNpcs(ClueScrollPlugin plugin) {
        return new String[]{FALO_THE_BARD};
    }

    @Override
    public int[] getConfigKeys() {
        return new int[]{this.text.hashCode()};
    }

    public static FaloTheBardClue forText(String text) {
        for (FaloTheBardClue clue : CLUES) {
            if (!clue.text.equalsIgnoreCase(text)) continue;
            return clue;
        }
        return null;
    }

    public String getText() {
        return this.text;
    }

    @Nonnull
    public ItemRequirement[] getItemRequirements() {
        return this.itemRequirements;
    }
}

