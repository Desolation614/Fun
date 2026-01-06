/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.ImmutableList
 *  net.runelite.api.Actor
 *  net.runelite.api.EquipmentInventorySlot
 *  net.runelite.api.Item
 *  net.runelite.api.NPC
 *  net.runelite.api.Point
 *  net.runelite.api.TileObject
 */
package net.runelite.client.plugins.cluescrolls.clues;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import net.runelite.api.Actor;
import net.runelite.api.EquipmentInventorySlot;
import net.runelite.api.Item;
import net.runelite.api.NPC;
import net.runelite.api.Point;
import net.runelite.api.TileObject;
import net.runelite.client.game.ItemVariationMapping;
import net.runelite.client.plugins.cluescrolls.ClueScrollOverlay;
import net.runelite.client.plugins.cluescrolls.ClueScrollPlugin;
import net.runelite.client.plugins.cluescrolls.ClueScrollWorldOverlay;
import net.runelite.client.plugins.cluescrolls.clues.ClueScroll;
import net.runelite.client.plugins.cluescrolls.clues.NamedObjectClueScroll;
import net.runelite.client.plugins.cluescrolls.clues.NpcClueScroll;
import net.runelite.client.plugins.cluescrolls.clues.item.AllRequirementsCollection;
import net.runelite.client.plugins.cluescrolls.clues.item.AnyRequirementCollection;
import net.runelite.client.plugins.cluescrolls.clues.item.ItemRequirement;
import net.runelite.client.plugins.cluescrolls.clues.item.ItemRequirements;
import net.runelite.client.plugins.cluescrolls.clues.item.SingleItemRequirement;
import net.runelite.client.ui.FontManager;
import net.runelite.client.ui.overlay.OverlayUtil;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.PanelComponent;
import net.runelite.client.ui.overlay.components.TitleComponent;

public class SkillChallengeClue
extends ClueScroll
implements NpcClueScroll,
NamedObjectClueScroll {
    private static final AnyRequirementCollection ANY_PICKAXE = ItemRequirements.any("Any Pickaxe", ItemRequirements.item(1265), ItemRequirements.item(1267), ItemRequirements.item(1269), ItemRequirements.item(12297), ItemRequirements.item(1273), ItemRequirements.item(1271), ItemRequirements.item(1275), ItemRequirements.item(11920), ItemRequirements.item(12797), ItemRequirements.item(23677), ItemRequirements.item(25376), ItemRequirements.item(30351), ItemRequirements.item(13243), ItemRequirements.item(25063), ItemRequirements.item(30345), ItemRequirements.item(13244), ItemRequirements.item(25369), ItemRequirements.item(30346), ItemRequirements.item(23276), ItemRequirements.item(20014), ItemRequirements.item(23680), ItemRequirements.item(23682), ItemRequirements.item(25112));
    private static final AnyRequirementCollection ANY_AXE = ItemRequirements.any("Any Axe", ItemRequirements.item(1351), ItemRequirements.item(28196), ItemRequirements.item(1349), ItemRequirements.item(28199), ItemRequirements.item(1353), ItemRequirements.item(28202), ItemRequirements.item(1361), ItemRequirements.item(28205), ItemRequirements.item(1355), ItemRequirements.item(28208), ItemRequirements.item(1357), ItemRequirements.item(28211), ItemRequirements.item(1359), ItemRequirements.item(28214), ItemRequirements.item(6739), ItemRequirements.item(25378), ItemRequirements.item(30352), ItemRequirements.item(28217), ItemRequirements.item(13241), ItemRequirements.item(25066), ItemRequirements.item(30347), ItemRequirements.item(13242), ItemRequirements.item(25371), ItemRequirements.item(30348), ItemRequirements.item(23279), ItemRequirements.item(20011), ItemRequirements.item(28226), ItemRequirements.item(23673), ItemRequirements.item(23675), ItemRequirements.item(28220), ItemRequirements.item(28223), ItemRequirements.item(25110));
    private static final AnyRequirementCollection ANY_HARPOON = ItemRequirements.any("Harpoon", ItemRequirements.item(311), ItemRequirements.item(10129), ItemRequirements.item(21028), ItemRequirements.item(25373), ItemRequirements.item(30349), ItemRequirements.item(21031), ItemRequirements.item(25059), ItemRequirements.item(30342), ItemRequirements.item(21033), ItemRequirements.item(25367), ItemRequirements.item(30343), ItemRequirements.item(23762), ItemRequirements.item(23764), ItemRequirements.item(25114));
    private static final AnyRequirementCollection ANY_HAMMER = ItemRequirements.any("Hammer", ItemRequirements.item(2347), ItemRequirements.item(25644), ItemRequirements.item(29775));
    private static final AllRequirementsCollection A_FULL_GRACEFUL_SET = ItemRequirements.all("A full Graceful set", ItemRequirements.any("", (ItemRequirement[])ItemVariationMapping.getVariations(11850).stream().map(ItemRequirements::item).toArray(SingleItemRequirement[]::new)), ItemRequirements.any("", (ItemRequirement[])ItemVariationMapping.getVariations(11854).stream().map(ItemRequirements::item).toArray(SingleItemRequirement[]::new)), ItemRequirements.any("", (ItemRequirement[])ItemVariationMapping.getVariations(11856).stream().map(ItemRequirements::item).toArray(SingleItemRequirement[]::new)), ItemRequirements.any("", (ItemRequirement[])ItemVariationMapping.getVariations(11858).stream().map(ItemRequirements::item).toArray(SingleItemRequirement[]::new)), ItemRequirements.any("", (ItemRequirement[])ItemVariationMapping.getVariations(11860).stream().map(ItemRequirements::item).toArray(SingleItemRequirement[]::new)), ItemRequirements.any("", (ItemRequirement[])Stream.of(ItemVariationMapping.getVariations(11852).stream(), ItemVariationMapping.getVariations(9771).stream(), ItemVariationMapping.getVariations(13280).stream()).reduce(Stream::concat).orElseGet(Stream::empty).map(ItemRequirements::item).toArray(SingleItemRequirement[]::new)));
    static final List<SkillChallengeClue> CLUES = ImmutableList.of((Object)new SkillChallengeClue(ChallengeType.CHARLIE, "i need to give charlie a cooked pike.", ItemRequirements.item(351)), (Object)new SkillChallengeClue(ChallengeType.CHARLIE, "i need to give charlie a cooked trout.", ItemRequirements.item(333)), (Object)new SkillChallengeClue(ChallengeType.CHARLIE, "i need to give charlie a leather body.", ItemRequirements.item(1129)), (Object)new SkillChallengeClue(ChallengeType.CHARLIE, "i need to give charlie some leather chaps.", ItemRequirements.item(1095)), (Object)new SkillChallengeClue(ChallengeType.CHARLIE, "i need to give charlie a raw herring.", ItemRequirements.item(345)), (Object)new SkillChallengeClue(ChallengeType.CHARLIE, "i need to give charlie a raw trout.", ItemRequirements.item(335)), (Object)new SkillChallengeClue(ChallengeType.CHARLIE, "i need to give charlie a piece of iron ore.", ItemRequirements.item(440)), (Object)new SkillChallengeClue(ChallengeType.CHARLIE, "i need to give charlie one iron dagger.", ItemRequirements.item(1203)), (Object)new SkillChallengeClue("Equip a Dragon Scimitar.", true, ItemRequirements.any("Any Dragon Scimitar", ItemRequirements.item(4587), ItemRequirements.item(20000))), (Object)new SkillChallengeClue("Enchant some Dragonstone Jewellery.", "enchant a piece of dragonstone jewellery.", ItemRequirements.xOfItem(564, 1), ItemRequirements.any("Water Rune x15", ItemRequirements.xOfItem(555, 15), ItemRequirements.xOfItem(4695, 15), ItemRequirements.xOfItem(4698, 15), ItemRequirements.xOfItem(4694, 15), ItemRequirements.item(1383), ItemRequirements.item(1395), ItemRequirements.item(1403), ItemRequirements.item(6562), ItemRequirements.item(6563), ItemRequirements.item(20730), ItemRequirements.item(20733), ItemRequirements.item(11787), ItemRequirements.item(11789), ItemRequirements.item(12795), ItemRequirements.item(12796), ItemRequirements.item(21006), ItemRequirements.item(25574)), ItemRequirements.any("Earth Rune x15", ItemRequirements.xOfItem(557, 15), ItemRequirements.xOfItem(4696, 15), ItemRequirements.xOfItem(4698, 15), ItemRequirements.xOfItem(4699, 15), ItemRequirements.item(1385), ItemRequirements.item(1399), ItemRequirements.item(1407), ItemRequirements.item(6562), ItemRequirements.item(6563), ItemRequirements.item(20736), ItemRequirements.item(20739), ItemRequirements.item(3053), ItemRequirements.item(3054), ItemRequirements.item(21198), ItemRequirements.item(21200)), ItemRequirements.any("Unenchanted Dragonstone Jewellery", ItemRequirements.item(1645), ItemRequirements.item(1664), ItemRequirements.item(11115), ItemRequirements.item(1702))), (Object)new SkillChallengeClue("Craft a nature rune.", ItemRequirements.any("Pure essence or Daeyalt essence", ItemRequirements.item(7936), ItemRequirements.item(24704))), (Object)new SkillChallengeClue("Catch a mottled eel with aerial fishing in Lake Molch.", ItemRequirements.any("Fish chunks or King worms", ItemRequirements.item(22818), ItemRequirements.item(2162)), ItemRequirements.emptySlot("No Gloves", EquipmentInventorySlot.GLOVES), ItemRequirements.any("No Weapon", ItemRequirements.emptySlot("", EquipmentInventorySlot.WEAPON), ItemRequirements.item(22816), ItemRequirements.item(22817)), ItemRequirements.emptySlot("No Shield", EquipmentInventorySlot.SHIELD)), (Object[])new SkillChallengeClue[]{new SkillChallengeClue("Score a goal in skullball.", true, ItemRequirements.any("Ring of Charos", ItemRequirements.item(4202), ItemRequirements.item(6465))), new SkillChallengeClue("Complete a lap of Ape atoll agility course.", true, ItemRequirements.any("Ninja Monkey Greegree", ItemRequirements.item(4024), ItemRequirements.item(4025), ItemRequirements.item(19525))), new SkillChallengeClue("Create a super defence potion.", ItemRequirements.item(107), ItemRequirements.item(239)), new SkillChallengeClue("Steal from a chest in Ardougne Castle.", new ItemRequirement[0]), new SkillChallengeClue("Craft a green dragonhide body.", ItemRequirements.xOfItem(1745, 3), ItemRequirements.any("Needle", ItemRequirements.item(1733), ItemRequirements.item(29920)), ItemRequirements.any("Thread", ItemRequirements.item(1734), ItemRequirements.item(29920))), new SkillChallengeClue("String a yew longbow.", ItemRequirements.item(66), ItemRequirements.item(1777)), new SkillChallengeClue("Kill a Dust Devil.", "slay a dust devil.", true, ItemRequirements.any("Facemask or Slayer Helmet", (ItemRequirement[])Stream.of(ItemVariationMapping.getVariations(11864).stream(), Stream.of(Integer.valueOf(4164))).reduce(Stream::concat).orElseGet(Stream::empty).map(ItemRequirements::item).toArray(SingleItemRequirement[]::new))), new SkillChallengeClue("Catch a black warlock.", ItemRequirements.any("Butterfly Net", ItemRequirements.item(10010), ItemRequirements.item(11259))), new SkillChallengeClue("Catch a red chinchompa.", ItemRequirements.item(10008)), new SkillChallengeClue("Mine a mithril ore.", ANY_PICKAXE), new SkillChallengeClue("Smith a mithril 2h sword.", ANY_HAMMER, ItemRequirements.xOfItem(2359, 3)), new SkillChallengeClue("Catch a raw shark.", ANY_HARPOON), new SkillChallengeClue("Cut a yew log.", ANY_AXE), new SkillChallengeClue("Fix a magical lamp in Dorgesh-Kaan.", new String[]{"Broken lamp"}, new int[]{10834, 10835}, ItemRequirements.item(10973)), new SkillChallengeClue("Burn a yew log.", ItemRequirements.item(1515), ItemRequirements.item(590)), new SkillChallengeClue("Cook a swordfish", "cook a swordfish", ItemRequirements.item(371)), new SkillChallengeClue("Craft multiple cosmic runes from a single essence.", ItemRequirements.any("Pure essence or Daeyalt essence", ItemRequirements.item(7936), ItemRequirements.item(24704))), new SkillChallengeClue("Plant a watermelon seed.", ItemRequirements.item(5341), ItemRequirements.item(5343), ItemRequirements.xOfItem(5321, 3)), new SkillChallengeClue("Activate the Chivalry prayer.", new ItemRequirement[0]), new SkillChallengeClue("Smith a tier 2 or above Shayzien platebody.", "smith a tier 2 or above shayzien platebody.", ANY_HAMMER, ItemRequirements.xOfItem(13354, 4)), new SkillChallengeClue("Equip an abyssal whip in front of the abyssal demons of the Slayer Tower.", true, ItemRequirements.any("Abyssal Whip", ItemRequirements.item(4151), ItemRequirements.item(12774), ItemRequirements.item(12773), ItemRequirements.item(26482), ItemRequirements.item(12006), ItemRequirements.item(26484))), new SkillChallengeClue("Smith a runite med helm.", ANY_HAMMER, ItemRequirements.item(2363)), new SkillChallengeClue("Teleport to a spirit tree you planted yourself.", new ItemRequirement[0]), new SkillChallengeClue("Create a Barrows teleport tablet.", ItemRequirements.item(13446), ItemRequirements.xOfItem(565, 1), ItemRequirements.xOfItem(563, 2), ItemRequirements.xOfItem(566, 2)), new SkillChallengeClue("Kill a Nechryael in the Slayer Tower.", "slay a nechryael in the slayer tower.", new ItemRequirement[0]), new SkillChallengeClue("Kill a Spiritual Mage while wearing something from their god.", "kill the spiritual, magic and godly whilst representing their own god.", new ItemRequirement[0]), new SkillChallengeClue("Create an unstrung dragonstone amulet at a furnace.", ItemRequirements.item(2357), ItemRequirements.item(1615), ItemRequirements.item(1595)), new SkillChallengeClue("Burn a magic log.", ItemRequirements.item(1513), ItemRequirements.item(590)), new SkillChallengeClue("Burn a redwood log.", ItemRequirements.item(19669), ItemRequirements.item(590)), new SkillChallengeClue("Complete a lap of Rellekka's Rooftop Agility Course", "complete a lap of the rellekka rooftop agility course whilst sporting the finest amount of grace.", true, A_FULL_GRACEFUL_SET), new SkillChallengeClue("Mix an anti-venom potion.", ItemRequirements.item(5952), ItemRequirements.xOfItem(12934, 20)), new SkillChallengeClue("Mine a piece of Runite ore", "mine a piece of runite ore whilst sporting the finest mining gear.", true, ANY_PICKAXE, ItemRequirements.all("Prospector kit", ItemRequirements.any("", ItemRequirements.item(12013), ItemRequirements.item(25549)), ItemRequirements.any("", ItemRequirements.item(12014), ItemRequirements.item(13107), ItemRequirements.item(25551)), ItemRequirements.any("", ItemRequirements.item(12015), ItemRequirements.item(25553)), ItemRequirements.any("", ItemRequirements.item(12016), ItemRequirements.item(25555)))), new SkillChallengeClue("Steal a gem from the Ardougne market.", new ItemRequirement[0]), new SkillChallengeClue("Pickpocket an elf.", new ItemRequirement[0]), new SkillChallengeClue("Bind a blood rune at the blood altar.", ItemRequirements.item(7938)), new SkillChallengeClue("Create a ranging mix potion.", "mix a ranging mix potion.", ItemRequirements.item(171), ItemRequirements.item(11326)), new SkillChallengeClue("Fletch a rune dart.", ItemRequirements.item(824), ItemRequirements.item(314)), new SkillChallengeClue("Cremate a set of fiyr remains.", ItemRequirements.any("Magic or Redwood Pyre Logs", ItemRequirements.item(3448), ItemRequirements.item(19672)), ItemRequirements.item(590), ItemRequirements.item(3404)), new SkillChallengeClue("Dissect a sacred eel.", ItemRequirements.item(946), ItemRequirements.any("Fishing rod", ItemRequirements.item(307), ItemRequirements.item(22846)), ItemRequirements.item(313)), new SkillChallengeClue("Kill a lizardman shaman.", new ItemRequirement[0]), new SkillChallengeClue("Catch an Anglerfish.", "angle for an anglerfish whilst sporting the finest fishing gear.", true, ItemRequirements.any("Fishing rod", ItemRequirements.item(307), ItemRequirements.item(22846)), ItemRequirements.any("Worms", ItemRequirements.item(13431), ItemRequirements.item(30773)), ItemRequirements.all("Angler's outfit", ItemRequirements.any("", ItemRequirements.item(13258), ItemRequirements.item(25592)), ItemRequirements.any("", ItemRequirements.item(13259), ItemRequirements.item(25594)), ItemRequirements.any("", ItemRequirements.item(13260), ItemRequirements.item(25596)), ItemRequirements.any("", ItemRequirements.item(13261), ItemRequirements.item(25598)))), new SkillChallengeClue("Chop a redwood log.", "chop a redwood log whilst sporting the finest lumberjack gear.", true, ANY_AXE, ItemRequirements.all("Lumberjack outfit", ItemRequirements.any("", ItemRequirements.item(10941), ItemRequirements.item(28173)), ItemRequirements.any("", ItemRequirements.item(10939), ItemRequirements.item(28169)), ItemRequirements.any("", ItemRequirements.item(10940), ItemRequirements.item(28171)), ItemRequirements.any("", ItemRequirements.item(10933), ItemRequirements.item(28175)))), new SkillChallengeClue("Craft a light orb in the Dorgesh-Kaan bank.", ItemRequirements.item(10981), ItemRequirements.item(10980)), new SkillChallengeClue("Kill a reanimated Abyssal Demon.", "kill a reanimated abyssal.", ItemRequirements.xOfItem(566, 4), ItemRequirements.xOfItem(565, 2), ItemRequirements.any("Nature Rune x4", ItemRequirements.xOfItem(561, 4), ItemRequirements.item(22370)), ItemRequirements.range("Ensouled abyssal head", 13507, 13508)), new SkillChallengeClue("Kill a Fiyr shade inside Mort'tons shade catacombs.", ItemRequirements.any("Any Gold or Silver Shade Key", ItemRequirements.item(25424), ItemRequirements.item(25426), ItemRequirements.item(25428), ItemRequirements.item(25430), ItemRequirements.item(25432), ItemRequirements.item(3465), ItemRequirements.item(3466), ItemRequirements.item(3467), ItemRequirements.item(3468), ItemRequirements.item(3469))), new SkillChallengeClue("Catch a tecu salamander.", ItemRequirements.item(954), ItemRequirements.item(303))});
    private final ChallengeType type;
    private final String challenge;
    private final String rawChallenge;
    private final String returnText;
    private final ItemRequirement[] itemRequirements;
    private final SingleItemRequirement returnItem;
    private final boolean requireEquip;
    private final String[] objectNames;
    private final int[] objectRegions;
    private boolean challengeCompleted;

    private SkillChallengeClue(ChallengeType challengeType, String clueText, SingleItemRequirement returnItem) {
        Preconditions.checkArgument((challengeType == ChallengeType.CHARLIE ? 1 : 0) != 0);
        this.type = challengeType;
        this.challenge = "";
        this.rawChallenge = clueText;
        this.returnText = clueText;
        this.itemRequirements = new ItemRequirement[0];
        this.returnItem = returnItem;
        this.challengeCompleted = true;
        this.requireEquip = false;
        this.objectNames = new String[0];
        this.objectRegions = null;
    }

    private SkillChallengeClue(String challenge, ItemRequirement ... itemRequirements) {
        this(challenge, challenge.toLowerCase(), itemRequirements);
    }

    private SkillChallengeClue(String challenge, String[] objectNames, int[] objectRegions, ItemRequirement ... itemRequirements) {
        this(challenge, challenge.toLowerCase(), false, objectNames, objectRegions, itemRequirements);
    }

    private SkillChallengeClue(String challenge, boolean requireEquip, ItemRequirement ... itemRequirements) {
        this(challenge, challenge.toLowerCase(), requireEquip, new String[0], null, itemRequirements);
    }

    private SkillChallengeClue(String challenge, String rawChallenge, ItemRequirement ... itemRequirements) {
        this(challenge, rawChallenge, false, new String[0], null, itemRequirements);
    }

    private SkillChallengeClue(String challenge, String rawChallenge, boolean requireEquip, ItemRequirement ... itemRequirements) {
        this(challenge, rawChallenge, requireEquip, new String[0], null, itemRequirements);
    }

    private SkillChallengeClue(String challenge, String rawChallenge, boolean requireEquip, String[] objectNames, int[] objectRegions, ItemRequirement ... itemRequirements) {
        this.type = ChallengeType.SHERLOCK;
        this.challenge = challenge;
        this.rawChallenge = rawChallenge;
        this.itemRequirements = itemRequirements;
        this.challengeCompleted = false;
        this.requireEquip = requireEquip;
        this.objectNames = objectNames;
        this.objectRegions = objectRegions;
        this.returnText = "<str>" + rawChallenge + "</str>";
        this.returnItem = null;
    }

    @Override
    public void makeOverlayHint(PanelComponent panelComponent, ClueScrollPlugin plugin) {
        panelComponent.getChildren().add(TitleComponent.builder().text("Skill Challenge Clue").build());
        if (!this.challengeCompleted) {
            panelComponent.getChildren().add(LineComponent.builder().left("Challenge:").build());
            panelComponent.getChildren().add(LineComponent.builder().left(this.challenge).leftColor(ClueScrollOverlay.TITLED_CONTENT_COLOR).build());
            if (this.itemRequirements.length > 0) {
                panelComponent.getChildren().add(LineComponent.builder().left(this.requireEquip ? "Equipment:" : "Items Required:").build());
                for (LineComponent line : SkillChallengeClue.getRequirements(plugin, this.requireEquip, this.itemRequirements)) {
                    panelComponent.getChildren().add(line);
                }
            }
        } else {
            panelComponent.getChildren().add(LineComponent.builder().left("NPC:").build());
            panelComponent.getChildren().add(LineComponent.builder().left(this.type.getName()).leftColor(ClueScrollOverlay.TITLED_CONTENT_COLOR).build());
            panelComponent.getChildren().add(LineComponent.builder().left("Location:").build());
            panelComponent.getChildren().add(LineComponent.builder().left(this.type.getLocation()).leftColor(ClueScrollOverlay.TITLED_CONTENT_COLOR).build());
            if (this.returnItem != null) {
                panelComponent.getChildren().add(LineComponent.builder().left("Item:").build());
                for (LineComponent line : SkillChallengeClue.getRequirements(plugin, false, this.returnItem)) {
                    panelComponent.getChildren().add(line);
                }
            }
        }
        this.renderOverlayNote(panelComponent, plugin);
    }

    @Override
    public void makeWorldOverlayHint(Graphics2D graphics, ClueScrollPlugin plugin) {
        if (plugin.getNpcsToMark() != null) {
            for (NPC npc : plugin.getNpcsToMark()) {
                OverlayUtil.renderActorOverlayImage(graphics, (Actor)npc, plugin.getClueScrollImage(), Color.ORANGE, 30);
            }
        }
        if (!this.challengeCompleted && this.objectNames.length > 0 && plugin.getNamedObjectsToMark() != null) {
            Point mousePosition = plugin.getClient().getMouseCanvasPosition();
            for (TileObject object : plugin.getNamedObjectsToMark()) {
                if (plugin.getClient().getPlane() != object.getPlane()) continue;
                OverlayUtil.renderHoverableArea(graphics, object.getClickbox(), mousePosition, ClueScrollWorldOverlay.CLICKBOX_FILL_COLOR, ClueScrollWorldOverlay.CLICKBOX_BORDER_COLOR, ClueScrollWorldOverlay.CLICKBOX_HOVER_BORDER_COLOR);
                OverlayUtil.renderImageLocation(plugin.getClient(), graphics, object.getLocalLocation(), plugin.getClueScrollImage(), 30);
            }
        }
    }

    private static List<LineComponent> getRequirements(ClueScrollPlugin plugin, boolean requireEquipped, ItemRequirement ... requirements) {
        ArrayList<LineComponent> components = new ArrayList<LineComponent>();
        Item[] equipment = plugin.getEquippedItems();
        Item[] inventory = plugin.getInventoryItems();
        if (equipment == null) {
            equipment = new Item[]{};
        }
        if (inventory == null) {
            inventory = new Item[]{};
        }
        Item[] combined = new Item[equipment.length + inventory.length];
        System.arraycopy(equipment, 0, combined, 0, equipment.length);
        System.arraycopy(inventory, 0, combined, equipment.length, inventory.length);
        for (ItemRequirement requirement : requirements) {
            boolean equipmentFulfilled = requirement.fulfilledBy(equipment);
            boolean combinedFulfilled = requirement.fulfilledBy(combined);
            components.add(LineComponent.builder().left(requirement.getCollectiveName(plugin.getClient())).leftColor(ClueScrollOverlay.TITLED_CONTENT_COLOR).right(combinedFulfilled ? "\u2713" : "\u2717").rightFont(FontManager.getDefaultFont()).rightColor(equipmentFulfilled || combinedFulfilled && !requireEquipped ? Color.GREEN : (combinedFulfilled ? Color.ORANGE : Color.RED)).build());
        }
        return components;
    }

    public static SkillChallengeClue forText(String text, String rawText) {
        for (SkillChallengeClue clue : CLUES) {
            if (rawText.equalsIgnoreCase(clue.returnText)) {
                clue.setChallengeCompleted(true);
                return clue;
            }
            if (!text.equals(clue.rawChallenge)) continue;
            clue.setChallengeCompleted(false);
            return clue;
        }
        return null;
    }

    @Override
    public String[] getNpcs(ClueScrollPlugin plugin) {
        return new String[]{this.type.getName()};
    }

    @Override
    public int[] getConfigKeys() {
        return new int[]{this.rawChallenge.hashCode()};
    }

    public ChallengeType getType() {
        return this.type;
    }

    public String getChallenge() {
        return this.challenge;
    }

    public String getRawChallenge() {
        return this.rawChallenge;
    }

    public String getReturnText() {
        return this.returnText;
    }

    public ItemRequirement[] getItemRequirements() {
        return this.itemRequirements;
    }

    public SingleItemRequirement getReturnItem() {
        return this.returnItem;
    }

    public boolean isRequireEquip() {
        return this.requireEquip;
    }

    @Override
    public String[] getObjectNames() {
        return this.objectNames;
    }

    @Override
    public int[] getObjectRegions() {
        return this.objectRegions;
    }

    public boolean isChallengeCompleted() {
        return this.challengeCompleted;
    }

    public void setChallengeCompleted(boolean challengeCompleted) {
        this.challengeCompleted = challengeCompleted;
    }

    static enum ChallengeType {
        CHARLIE("Charlie the Tramp", "Southern Entrance to Varrock"),
        SHERLOCK("Sherlock", "East of the Sorcerer's Tower in Seers' Village");

        private final String name;
        private final String location;

        private ChallengeType(String name, String location) {
            this.name = name;
            this.location = location;
        }

        public String getName() {
            return this.name;
        }

        public String getLocation() {
            return this.location;
        }
    }
}

