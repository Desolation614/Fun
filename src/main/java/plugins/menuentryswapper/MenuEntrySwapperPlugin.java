/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.base.Predicates
 *  com.google.common.base.Strings
 *  com.google.common.collect.ArrayListMultimap
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.LinkedHashMultimap
 *  com.google.common.collect.Multimap
 *  com.google.inject.Provides
 *  javax.inject.Inject
 *  net.runelite.api.ChatMessageType
 *  net.runelite.api.Client
 *  net.runelite.api.ItemComposition
 *  net.runelite.api.Menu
 *  net.runelite.api.MenuAction
 *  net.runelite.api.MenuEntry
 *  net.runelite.api.NPC
 *  net.runelite.api.NPCComposition
 *  net.runelite.api.ObjectComposition
 *  net.runelite.api.events.ClientTick
 *  net.runelite.api.events.MenuOpened
 *  net.runelite.api.events.PostMenuSort
 *  net.runelite.api.widgets.Widget
 *  net.runelite.api.widgets.WidgetConfigNode
 *  net.runelite.api.widgets.WidgetUtil
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package net.runelite.client.plugins.menuentryswapper;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Predicates;
import com.google.common.base.Strings;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import com.google.inject.Provides;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;
import javax.inject.Inject;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.ItemComposition;
import net.runelite.api.Menu;
import net.runelite.api.MenuAction;
import net.runelite.api.MenuEntry;
import net.runelite.api.NPC;
import net.runelite.api.NPCComposition;
import net.runelite.api.ObjectComposition;
import net.runelite.api.events.ClientTick;
import net.runelite.api.events.MenuOpened;
import net.runelite.api.events.PostMenuSort;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetConfigNode;
import net.runelite.api.widgets.WidgetUtil;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.chat.ChatMessageBuilder;
import net.runelite.client.chat.ChatMessageManager;
import net.runelite.client.chat.QueuedMessage;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.game.ItemManager;
import net.runelite.client.game.ItemVariationMapping;
import net.runelite.client.game.NpcUtil;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.menuentryswapper.BuyMode;
import net.runelite.client.plugins.menuentryswapper.FairyRingMode;
import net.runelite.client.plugins.menuentryswapper.GEItemCollectMode;
import net.runelite.client.plugins.menuentryswapper.HouseMode;
import net.runelite.client.plugins.menuentryswapper.MenuEntrySwapperConfig;
import net.runelite.client.plugins.menuentryswapper.SellMode;
import net.runelite.client.plugins.menuentryswapper.ShiftDepositMode;
import net.runelite.client.plugins.menuentryswapper.ShiftWithdrawMode;
import net.runelite.client.plugins.menuentryswapper.Swap;
import net.runelite.client.util.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@PluginDescriptor(name="Menu Entry Swapper", description="Change the default option that is displayed when hovering over objects", tags={"npcs", "inventory", "items", "objects"}, enabledByDefault=false)
public class MenuEntrySwapperPlugin
extends Plugin {
    private static final Logger log = LoggerFactory.getLogger(MenuEntrySwapperPlugin.class);
    private static final String SHIFTCLICK_CONFIG_GROUP = "shiftclick";
    private static final String ITEM_KEY_PREFIX = "item_";
    private static final String OBJECT_KEY_PREFIX = "object_";
    private static final String OBJECT_SHIFT_KEY_PREFIX = "object_shift_";
    private static final String NPC_KEY_PREFIX = "npc_";
    private static final String NPC_SHIFT_KEY_PREFIX = "npc_shift_";
    private static final String WORN_ITEM_KEY_PREFIX = "wornitem_";
    private static final String WORN_ITEM_SHIFT_KEY_PREFIX = "wornitem_shift_";
    private static final String UI_KEY_PREFIX = "ui_";
    private static final String UI_SHIFT_KEY_PREFIX = "ui_shift_";
    private static final List<MenuAction> NPC_MENU_TYPES = ImmutableList.of((Object)MenuAction.NPC_FIRST_OPTION, (Object)MenuAction.NPC_SECOND_OPTION, (Object)MenuAction.NPC_THIRD_OPTION, (Object)MenuAction.NPC_FOURTH_OPTION, (Object)MenuAction.NPC_FIFTH_OPTION);
    private static final List<MenuAction> OBJECT_MENU_TYPES = ImmutableList.of((Object)MenuAction.GAME_OBJECT_FIRST_OPTION, (Object)MenuAction.GAME_OBJECT_SECOND_OPTION, (Object)MenuAction.GAME_OBJECT_THIRD_OPTION, (Object)MenuAction.GAME_OBJECT_FOURTH_OPTION, (Object)MenuAction.GAME_OBJECT_FIFTH_OPTION);
    private static final Set<String> ESSENCE_MINE_NPCS = ImmutableSet.of((Object)"aubury", (Object)"archmage sedridor", (Object)"wizard distentor", (Object)"wizard cromperty", (Object)"brimstail");
    private static final Set<String> TEMPOROSS_NPCS = ImmutableSet.of((Object)"captain dudi", (Object)"captain pudi", (Object)"first mate deri", (Object)"first mate peri");
    private static final int[][] EQUIPMENT_SUBOP_PARAMS = new int[][]{{661, 2074, 2082, 2090, 2098, 2106, 2114, 2122, 2130, 2138, 2146, 2154, 2162, 2170, 2178, 2186, 2194, 2202, 2210, 2218}, {662, 2075, 2083, 2091, 2099, 2107, 2115, 2123, 2131, 2139, 2147, 2155, 2163, 2171, 2179, 2187, 2195, 2203, 2211, 2219}, {663, 2076, 2084, 2092, 2100, 2108, 2116, 2124, 2132, 2140, 2148, 2156, 2164, 2172, 2180, 2188, 2196, 2204, 2212, 2220}, {2069, 2077, 2085, 2093, 2101, 2109, 2117, 2125, 2133, 2141, 2149, 2157, 2165, 2173, 2181, 2189, 2197, 2205, 2213, 2221}, {2070, 2078, 2086, 2094, 2102, 2110, 2118, 2126, 2134, 2142, 2150, 2158, 2166, 2174, 2182, 2190, 2198, 2206, 2214, 2222}, {2071, 2079, 2087, 2095, 2103, 2111, 2119, 2127, 2135, 2143, 2151, 2159, 2167, 2175, 2183, 2191, 2199, 2207, 2215, 2223}, {2072, 2080, 2088, 2096, 2104, 2112, 2120, 2128, 2136, 2144, 2152, 2160, 2168, 2176, 2184, 2192, 2200, 2208, 2216, 2224}, {2073, 2081, 2089, 2097, 2105, 2113, 2121, 2129, 2137, 2145, 2153, 2161, 2169, 2177, 2185, 2193, 2201, 2209, 2217, 2225}};
    @Inject
    private Client client;
    @Inject
    private ClientThread clientThread;
    @Inject
    private MenuEntrySwapperConfig config;
    @Inject
    private ConfigManager configManager;
    @Inject
    private ItemManager itemManager;
    @Inject
    private ChatMessageManager chatMessageManager;
    @Inject
    private NpcUtil npcUtil;
    private final Multimap<String, Swap> swaps = LinkedHashMultimap.create();
    private final ArrayListMultimap<String, Integer> cacheOptionIndexes = ArrayListMultimap.create();
    private Menu cacheOptionMenu;
    private boolean lastShift;
    private boolean curShift;

    @Provides
    MenuEntrySwapperConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(MenuEntrySwapperConfig.class);
    }

    @Override
    public void startUp() {
        this.setupSwaps();
        this.removeOldSwaps();
    }

    @Override
    public void shutDown() {
        this.swaps.clear();
    }

    @VisibleForTesting
    void setupSwaps() {
        this.swap("talk-to", "mage of zamorak", "teleport", this.config::swapAbyssTeleport);
        this.swap("talk-to", "bank", this.config::swapBank);
        this.swap("talk-to", "exchange", this.config::swapExchange);
        this.swap("talk-to", "help", this.config::swapHelp);
        this.swap("talk-to", "assignment", this.config::swapAssignment);
        this.swap("talk-to", "pay", this.config::swapPay);
        this.swapContains("talk-to", (Predicate<String>)Predicates.alwaysTrue(), "pay (", this.config::swapPay);
        this.swap("talk-to", "trade", this.config::swapTrade);
        this.swap("talk-to", "trade-with", this.config::swapTrade);
        this.swap("talk-to", "shop", this.config::swapTrade);
        this.swap("talk-to", "travel", this.config::swapTravel);
        this.swap("talk-to", "pay-fare", this.config::swapTravel);
        this.swap("talk-to", "charter", this.config::swapTravel);
        this.swap("talk-to", "take-boat", this.config::swapTravel);
        this.swap("talk-to", "fly", this.config::swapTravel);
        this.swap("talk-to", "jatizso", this.config::swapTravel);
        this.swap("talk-to", "neitiznot", this.config::swapTravel);
        this.swap("talk-to", "rellekka", this.config::swapTravel);
        this.swap("talk-to", "ungael", this.config::swapTravel);
        this.swap("talk-to", "pirate's cove", this.config::swapTravel);
        this.swap("talk-to", "waterbirth island", this.config::swapTravel);
        this.swap("talk-to", "island of stone", this.config::swapTravel);
        this.swap("talk-to", "miscellania", this.config::swapTravel);
        this.swap("talk-to", "follow", this.config::swapTravel);
        this.swap("talk-to", "transport", this.config::swapTravel);
        this.swap("talk-to", "quick-travel", this.config::swapQuick);
        this.swap("talk-to", ESSENCE_MINE_NPCS::contains, "teleport", this.config::swapEssenceMineTeleport);
        this.swap("talk-to", "deposit-items", this.config::swapDepositItems);
        this.swap("talk-to", TEMPOROSS_NPCS::contains, "leave", this.config::swapTemporossLeave);
        this.swap("pass", "energy barrier", "pay-toll(2-ecto)", this.config::swapTravel);
        this.swap("open", "gate", "pay-toll(10gp)", this.config::swapTravel);
        this.swap("inspect", "trapdoor", "travel", this.config::swapTravel);
        this.swap("board", "travel cart", "pay-fare", this.config::swapTravel);
        this.swap("board", "sacrificial boat", "quick-board", this.config::swapQuick);
        this.swap("cage", "harpoon", this.config::swapHarpoon);
        this.swap("big net", "harpoon", this.config::swapHarpoon);
        this.swap("net", "harpoon", this.config::swapHarpoon);
        this.swap("lure", "bait", this.config::swapBait);
        this.swap("net", "bait", this.config::swapBait);
        this.swap("small net", "bait", this.config::swapBait);
        this.swap("enter", "portal", "home", () -> this.config.swapHomePortal() == HouseMode.HOME);
        this.swap("enter", "portal", "build mode", () -> this.config.swapHomePortal() == HouseMode.BUILD_MODE);
        this.swap("enter", "portal", "friend's house", () -> this.config.swapHomePortal() == HouseMode.FRIENDS_HOUSE);
        for (String option : new String[]{"zanaris", "tree"}) {
            this.swapContains(option, (Predicate<String>)Predicates.alwaysTrue(), "last-destination", () -> this.config.swapFairyRing() == FairyRingMode.LAST_DESTINATION);
            this.swapContains(option, (Predicate<String>)Predicates.alwaysTrue(), "configure", () -> this.config.swapFairyRing() == FairyRingMode.CONFIGURE);
        }
        this.swapContains("configure", (Predicate<String>)Predicates.alwaysTrue(), "last-destination", () -> this.config.swapFairyRing() == FairyRingMode.LAST_DESTINATION || this.config.swapFairyRing() == FairyRingMode.ZANARIS);
        this.swapContains("tree", (Predicate<String>)Predicates.alwaysTrue(), "zanaris", () -> this.config.swapFairyRing() == FairyRingMode.ZANARIS);
        this.swap("check", "reset", this.config::swapBoxTrap);
        this.swap("dismantle", "reset", this.config::swapBoxTrap);
        this.swap("take", "lay", this.config::swapBoxTrap);
        this.swap("pick-up", "chase", this.config::swapChase);
        this.swap("interact", (String target) -> target.endsWith("birdhouse"), "empty", this.config::swapBirdhouseEmpty);
        this.swap("enter", "quick-enter", this.config::swapQuick);
        this.swap("enter-crypt", "quick-enter", this.config::swapQuick);
        this.swap("ring", "quick-start", this.config::swapQuick);
        this.swap("pass", "quick-pass", this.config::swapQuick);
        this.swap("pass", "quick pass", this.config::swapQuick);
        this.swap("open", "quick-open", this.config::swapQuick);
        this.swap("climb-down", "quick-start", this.config::swapQuick);
        this.swap("climb-down", "pay", this.config::swapQuick);
        this.swap("admire", "teleport", this.config::swapAdmire);
        this.swap("admire", "spellbook", this.config::swapAdmire);
        this.swap("admire", "perks", this.config::swapAdmire);
        this.swap("teleport menu", "emir's arena", this.config::swapJewelleryBox);
        this.swap("teleport menu", "castle wars", this.config::swapJewelleryBox);
        this.swap("teleport menu", "ferox enclave", this.config::swapJewelleryBox);
        this.swap("teleport menu", "fortis colosseum", this.config::swapJewelleryBox);
        this.swap("teleport menu", "burthorpe", this.config::swapJewelleryBox);
        this.swap("teleport menu", "barbarian outpost", this.config::swapJewelleryBox);
        this.swap("teleport menu", "corporeal beast", this.config::swapJewelleryBox);
        this.swap("teleport menu", "tears of guthix", this.config::swapJewelleryBox);
        this.swap("teleport menu", "wintertodt camp", this.config::swapJewelleryBox);
        this.swap("teleport menu", "warriors' guild", this.config::swapJewelleryBox);
        this.swap("teleport menu", "champions' guild", this.config::swapJewelleryBox);
        this.swap("teleport menu", "monastery", this.config::swapJewelleryBox);
        this.swap("teleport menu", "ranging guild", this.config::swapJewelleryBox);
        this.swap("teleport menu", "fishing guild", this.config::swapJewelleryBox);
        this.swap("teleport menu", "mining guild", this.config::swapJewelleryBox);
        this.swap("teleport menu", "crafting guild", this.config::swapJewelleryBox);
        this.swap("teleport menu", "cooking guild", this.config::swapJewelleryBox);
        this.swap("teleport menu", "woodcutting guild", this.config::swapJewelleryBox);
        this.swap("teleport menu", "farming guild", this.config::swapJewelleryBox);
        this.swap("teleport menu", "miscellania", this.config::swapJewelleryBox);
        this.swap("teleport menu", "grand exchange", this.config::swapJewelleryBox);
        this.swap("teleport menu", "falador park", this.config::swapJewelleryBox);
        this.swap("teleport menu", "dondakan's rock", this.config::swapJewelleryBox);
        this.swap("teleport menu", "edgeville", this.config::swapJewelleryBox);
        this.swap("teleport menu", "karamja", this.config::swapJewelleryBox);
        this.swap("teleport menu", "draynor village", this.config::swapJewelleryBox);
        this.swap("teleport menu", "al kharid", this.config::swapJewelleryBox);
        Arrays.asList("annakarl", "ape atoll dungeon", "ardougne", "barrows", "battlefront", "camelot", "carrallanger", "catherby", "cemetery", "draynor manor", "falador", "fenkenstrain's castle", "fishing guild", "ghorrock", "grand exchange", "great kourend", "harmony island", "kharyrll", "lumbridge", "arceuus library", "lunar isle", "marim", "mind altar", "salve graveyard", "seers' village", "senntisten", "troll stronghold", "varrock", "watchtower", "waterbirth island", "weiss", "west ardougne", "yanille").forEach(location -> this.swap((String)location, "portal nexus", "teleport menu", this.config::swapPortalNexus));
        this.swap("shared", "private", this.config::swapPrivate);
        this.swap("pick", "pick-lots", this.config::swapPick);
        this.swap("view offer", "abort offer", () -> this.shiftModifier() && this.config.swapGEAbort());
        this.swap("value", "buy 1", () -> this.shiftModifier() && this.config.shopBuy() == BuyMode.BUY_1);
        this.swap("value", "buy 5", () -> this.shiftModifier() && this.config.shopBuy() == BuyMode.BUY_5);
        this.swap("value", "buy 10", () -> this.shiftModifier() && this.config.shopBuy() == BuyMode.BUY_10);
        this.swap("value", "buy 50", () -> this.shiftModifier() && this.config.shopBuy() == BuyMode.BUY_50);
        this.swap("value", "sell 1", () -> this.shiftModifier() && this.config.shopSell() == SellMode.SELL_1);
        this.swap("value", "sell 5", () -> this.shiftModifier() && this.config.shopSell() == SellMode.SELL_5);
        this.swap("value", "sell 10", () -> this.shiftModifier() && this.config.shopSell() == SellMode.SELL_10);
        this.swap("value", "sell 50", () -> this.shiftModifier() && this.config.shopSell() == SellMode.SELL_50);
        this.swap("wear", "tele to poh", this.config::swapTeleToPoh);
        this.swap("wear", "rub", this.config::swapTeleportItem);
        this.swap("wear", "teleport", this.config::swapTeleportItem);
        this.swap("wield", "teleport", this.config::swapTeleportItem);
        this.swap("wield", "invoke", this.config::swapTeleportItem);
        this.swap("wear", "teleports", this.config::swapTeleportItem);
        this.swap("wear", "farm teleport", () -> this.config.swapArdougneCloakMode() == MenuEntrySwapperConfig.ArdougneCloakMode.FARM);
        this.swap("wear", "monastery teleport", () -> this.config.swapArdougneCloakMode() == MenuEntrySwapperConfig.ArdougneCloakMode.MONASTERY);
        this.swap("wear", "gem mine", () -> this.config.swapKaramjaGlovesMode() == MenuEntrySwapperConfig.KaramjaGlovesMode.GEM_MINE);
        this.swap("wear", "slayer master", () -> this.config.swapKaramjaGlovesMode() == MenuEntrySwapperConfig.KaramjaGlovesMode.SLAYER_MASTER);
        this.swap("equip", "kourend woodland", () -> this.config.swapRadasBlessingMode() == MenuEntrySwapperConfig.RadasBlessingMode.KOUREND_WOODLAND);
        this.swap("equip", "mount karuulm", () -> this.config.swapRadasBlessingMode() == MenuEntrySwapperConfig.RadasBlessingMode.MOUNT_KARUULM);
        this.swap("wear", "ecto teleport", () -> this.config.swapMorytaniaLegsMode() == MenuEntrySwapperConfig.MorytaniaLegsMode.ECTOFUNTUS);
        this.swap("wear", "burgh teleport", () -> this.config.swapMorytaniaLegsMode() == MenuEntrySwapperConfig.MorytaniaLegsMode.BURGH_DE_ROTT);
        this.swap("wear", "nardah", () -> this.config.swapDesertAmuletMode() == MenuEntrySwapperConfig.DesertAmuletMode.NARDAH);
        this.swap("wear", "kalphite cave", () -> this.config.swapDesertAmuletMode() == MenuEntrySwapperConfig.DesertAmuletMode.KALPHITE_CAVE);
        this.swap("bury", "use", this.config::swapBones);
        this.swap("clean", "use", this.config::swapHerbs);
        this.swap("collect-note", "collect-item", () -> this.config.swapGEItemCollect() == GEItemCollectMode.ITEMS);
        this.swap("collect-notes", "collect-items", () -> this.config.swapGEItemCollect() == GEItemCollectMode.ITEMS);
        this.swap("collect-item", "collect-note", () -> this.config.swapGEItemCollect() == GEItemCollectMode.NOTES);
        this.swap("collect-items", "collect-notes", () -> this.config.swapGEItemCollect() == GEItemCollectMode.NOTES);
        this.swap("collect to inventory", "collect to bank", () -> this.config.swapGEItemCollect() == GEItemCollectMode.BANK);
        this.swap("collect", "bank", () -> this.config.swapGEItemCollect() == GEItemCollectMode.BANK);
        this.swap("collect-note", "bank", () -> this.config.swapGEItemCollect() == GEItemCollectMode.BANK);
        this.swap("collect-notes", "bank", () -> this.config.swapGEItemCollect() == GEItemCollectMode.BANK);
        this.swap("collect-item", "bank", () -> this.config.swapGEItemCollect() == GEItemCollectMode.BANK);
        this.swap("collect-items", "bank", () -> this.config.swapGEItemCollect() == GEItemCollectMode.BANK);
        this.swap("tan 1", "tan all", this.config::swapTan);
        this.swap("climb", "climb-up", () -> (this.shiftModifier() ? this.config.swapStairsShiftClick() : this.config.swapStairsLeftClick()) == MenuEntrySwapperConfig.StairsMode.CLIMB_UP);
        this.swap("climb", "climb-down", () -> (this.shiftModifier() ? this.config.swapStairsShiftClick() : this.config.swapStairsLeftClick()) == MenuEntrySwapperConfig.StairsMode.CLIMB_DOWN);
    }

    private void removeOldSwaps() {
        String[] keys;
        for (String key : keys = new String[]{"swapBattlestaves", "swapPrayerBook", "swapContract", "claimSlime", "swapDarkMage", "swapCaptainKhaled", "swapDecant", "swapHardWoodGrove", "swapHardWoodGroveParcel", "swapHouseAdvertisement", "swapEnchant", "swapHouseTeleportSpell", "swapTeleportSpell", "swapStartMinigame", "swapQuickleave", "swapNpcContact", "swapNets", "swapGauntlet", "swapCollectMiscellania", "swapRockCake", "swapRowboatDive"}) {
            this.configManager.unsetConfiguration("menuentryswapper", key);
        }
    }

    private void swap(String option, String swappedOption, Supplier<Boolean> enabled) {
        this.swap(option, (Predicate<String>)Predicates.alwaysTrue(), swappedOption, enabled);
    }

    private void swap(String option, String target, String swappedOption, Supplier<Boolean> enabled) {
        this.swap(option, (Predicate<String>)Predicates.equalTo((Object)target), swappedOption, enabled);
    }

    private void swap(String option, Predicate<String> targetPredicate, String swappedOption, Supplier<Boolean> enabled) {
        this.swaps.put((Object)option, (Object)new Swap((Predicate<String>)Predicates.alwaysTrue(), targetPredicate, swappedOption, enabled, true));
    }

    private void swapContains(String option, Predicate<String> targetPredicate, String swappedOption, Supplier<Boolean> enabled) {
        this.swaps.put((Object)option, (Object)new Swap((Predicate<String>)Predicates.alwaysTrue(), targetPredicate, swappedOption, enabled, false));
    }

    private Integer getItemSwapConfig(boolean shift, int itemId) {
        String config = this.configManager.getConfiguration(shift ? SHIFTCLICK_CONFIG_GROUP : "menuentryswapper", ITEM_KEY_PREFIX + (itemId = ItemVariationMapping.map(itemId)));
        if (config == null || config.isEmpty()) {
            return null;
        }
        return Integer.parseInt(config);
    }

    private void setItemSwapConfig(boolean shift, int itemId, int index) {
        itemId = ItemVariationMapping.map(itemId);
        this.configManager.setConfiguration(shift ? SHIFTCLICK_CONFIG_GROUP : "menuentryswapper", ITEM_KEY_PREFIX + itemId, index);
    }

    private void unsetItemSwapConfig(boolean shift, int itemId) {
        itemId = ItemVariationMapping.map(itemId);
        this.configManager.unsetConfiguration(shift ? SHIFTCLICK_CONFIG_GROUP : "menuentryswapper", ITEM_KEY_PREFIX + itemId);
    }

    private Integer getWornItemSwapConfig(boolean shift, int itemId) {
        String config = this.configManager.getConfiguration("menuentryswapper", (shift ? WORN_ITEM_SHIFT_KEY_PREFIX : WORN_ITEM_KEY_PREFIX) + (itemId = ItemVariationMapping.map(itemId)));
        if (config == null || config.isEmpty()) {
            return null;
        }
        return Integer.parseInt(config);
    }

    private void setWornItemSwapConfig(boolean shift, int itemId, int index) {
        itemId = ItemVariationMapping.map(itemId);
        this.configManager.setConfiguration("menuentryswapper", (shift ? WORN_ITEM_SHIFT_KEY_PREFIX : WORN_ITEM_KEY_PREFIX) + itemId, index);
    }

    private void unsetWornItemSwapConfig(boolean shift, int itemId) {
        itemId = ItemVariationMapping.map(itemId);
        this.configManager.unsetConfiguration("menuentryswapper", (shift ? WORN_ITEM_SHIFT_KEY_PREFIX : WORN_ITEM_KEY_PREFIX) + itemId);
    }

    @Subscribe
    public void onMenuOpened(MenuOpened event) {
        this.configureObjectClick(event);
        this.configureNpcClick(event);
        this.configureWornItems(event);
        this.configureItems(event);
        this.configureUiSwap(event);
    }

    private void configureObjectClick(MenuOpened event) {
        if (!this.shiftModifier() || !this.config.objectCustomization()) {
            return;
        }
        MenuEntry[] entries = event.getMenuEntries();
        for (int idx = entries.length - 1; idx >= 0; --idx) {
            MenuEntry entry = entries[idx];
            if (entry.getType() != MenuAction.EXAMINE_OBJECT) continue;
            ObjectComposition composition = this.client.getObjectDefinition(entry.getIdentifier());
            String[] actions = composition.getActions();
            Integer swapConfig = this.getObjectSwapConfig(false, composition.getId());
            MenuAction currentAction = swapConfig == null ? MenuEntrySwapperPlugin.defaultAction(composition) : (swapConfig == -1 ? MenuAction.WALK : OBJECT_MENU_TYPES.get(swapConfig));
            Integer shiftSwapConfig = this.getObjectSwapConfig(true, composition.getId());
            MenuAction currentShiftAction = shiftSwapConfig == null ? MenuEntrySwapperPlugin.defaultAction(composition) : (shiftSwapConfig == -1 ? MenuAction.WALK : OBJECT_MENU_TYPES.get(shiftSwapConfig));
            MenuEntry swapLeftClick = this.client.createMenuEntry(idx).setOption("Swap left-click").setTarget(entry.getTarget()).setType(MenuAction.RUNELITE);
            MenuEntry swapShiftClick = this.client.createMenuEntry(idx).setOption("Swap shift-click").setTarget(entry.getTarget()).setType(MenuAction.RUNELITE);
            Menu subLeft = swapLeftClick.createSubMenu();
            Menu subShift = swapShiftClick.createSubMenu();
            for (int actionIdx = 0; actionIdx < OBJECT_MENU_TYPES.size(); ++actionIdx) {
                if (Strings.isNullOrEmpty((String)actions[actionIdx])) continue;
                MenuAction menuAction = OBJECT_MENU_TYPES.get(actionIdx);
                if (menuAction != currentAction) {
                    subLeft.createMenuEntry(0).setOption(actions[actionIdx]).setType(MenuAction.RUNELITE).onClick(this.objectConsumer(composition, actions, actionIdx, menuAction, false));
                }
                if (menuAction == currentShiftAction || menuAction == currentAction) continue;
                subShift.createMenuEntry(0).setOption(actions[actionIdx]).setType(MenuAction.RUNELITE).onClick(this.objectConsumer(composition, actions, actionIdx, menuAction, true));
            }
            if (currentAction != MenuAction.WALK) {
                subLeft.createMenuEntry(0).setOption("Walk here").setType(MenuAction.RUNELITE).onClick(this.walkHereConsumer(false, composition));
            }
            if (currentShiftAction != MenuAction.WALK) {
                subShift.createMenuEntry(0).setOption("Walk here").setType(MenuAction.RUNELITE).onClick(this.walkHereConsumer(true, composition));
            }
            if (swapConfig != null) {
                subLeft.createMenuEntry(0).setOption("Reset").setType(MenuAction.RUNELITE).onClick(this.objectResetConsumer(composition, false));
            }
            if (shiftSwapConfig == null) continue;
            subShift.createMenuEntry(0).setOption("Reset").setType(MenuAction.RUNELITE).onClick(this.objectResetConsumer(composition, true));
        }
    }

    private Consumer<MenuEntry> objectConsumer(ObjectComposition composition, String[] actions, int menuIdx, MenuAction menuAction, boolean shift) {
        return e -> {
            String message = new ChatMessageBuilder().append("The default ").append(shift ? "shift" : "left").append(" click option for '").append(Text.removeTags(composition.getName())).append("' ").append("has been set to '").append(actions[menuIdx]).append("'.").build();
            this.chatMessageManager.queue(QueuedMessage.builder().type(ChatMessageType.CONSOLE).runeLiteFormattedMessage(message).build());
            log.debug("Set object swap for {} to {}", (Object)composition.getId(), (Object)menuAction);
            this.setObjectSwapConfig(shift, composition.getId(), menuIdx);
        };
    }

    private Consumer<MenuEntry> objectResetConsumer(ObjectComposition composition, boolean shift) {
        return e -> {
            String message = new ChatMessageBuilder().append("The default ").append(shift ? "shift" : "left").append(" click option for '").append(Text.removeTags(composition.getName())).append("' ").append("has been reset.").build();
            this.chatMessageManager.queue(QueuedMessage.builder().type(ChatMessageType.CONSOLE).runeLiteFormattedMessage(message).build());
            log.debug("Unset object {} swap for {}", (Object)(shift ? "shift" : "left"), (Object)composition.getId());
            this.unsetObjectSwapConfig(shift, composition.getId());
        };
    }

    private Consumer<MenuEntry> walkHereConsumer(boolean shift, ObjectComposition composition) {
        return e -> {
            String message = new ChatMessageBuilder().append("The default ").append(shift ? "shift" : "left").append(" click option for '").append(Text.removeTags(composition.getName())).append("' ").append("has been set to Walk here.").build();
            this.chatMessageManager.queue(QueuedMessage.builder().type(ChatMessageType.CONSOLE).runeLiteFormattedMessage(message).build());
            log.debug("Set object {} click swap for {} to Walk here", (Object)(shift ? "shift" : "left"), (Object)composition.getId());
            this.setObjectSwapConfig(shift, composition.getId(), -1);
        };
    }

    private Consumer<MenuEntry> walkHereConsumer(boolean shift, NPCComposition composition) {
        return e -> {
            String message = new ChatMessageBuilder().append("The default ").append(shift ? "shift" : "left").append(" click option for '").append(Text.removeTags(composition.getName())).append("' ").append("has been set to Walk here.").build();
            this.chatMessageManager.queue(QueuedMessage.builder().type(ChatMessageType.CONSOLE).runeLiteFormattedMessage(message).build());
            log.debug("Set npc {} click swap for {} to Walk here", (Object)(shift ? "shift" : "left"), (Object)composition.getId());
            this.setNpcSwapConfig(shift, composition.getId(), -1);
        };
    }

    private void configureNpcClick(MenuOpened event) {
        if (!this.shiftModifier() || !this.config.npcCustomization()) {
            return;
        }
        MenuEntry[] entries = event.getMenuEntries();
        for (int idx = entries.length - 1; idx >= 0; --idx) {
            MenuAction currentAction;
            MenuEntry entry = entries[idx];
            MenuAction type = entry.getType();
            if (type != MenuAction.EXAMINE_NPC) continue;
            NPC npc = entry.getNpc();
            assert (npc != null);
            NPCComposition composition = npc.getTransformedComposition();
            assert (composition != null);
            String[] actions = composition.getActions();
            Integer swapConfig = this.getNpcSwapConfig(false, composition.getId());
            Integer shiftSwapConfig = this.getNpcSwapConfig(true, composition.getId());
            boolean hasAttack = Arrays.stream(composition.getActions()).anyMatch("Attack"::equalsIgnoreCase);
            MenuAction menuAction = swapConfig == null ? (hasAttack ? null : MenuEntrySwapperPlugin.defaultAction(composition)) : (currentAction = swapConfig == -1 ? MenuAction.WALK : NPC_MENU_TYPES.get(swapConfig));
            MenuAction currentShiftAction = shiftSwapConfig == null ? (hasAttack ? null : MenuEntrySwapperPlugin.defaultAction(composition)) : (shiftSwapConfig == -1 ? MenuAction.WALK : NPC_MENU_TYPES.get(shiftSwapConfig));
            MenuEntry swapLeftClick = this.client.createMenuEntry(idx).setOption("Swap left-click").setTarget(entry.getTarget()).setType(MenuAction.RUNELITE);
            MenuEntry swapShiftClick = this.client.createMenuEntry(idx).setOption("Swap shift-click").setTarget(entry.getTarget()).setType(MenuAction.RUNELITE);
            Menu subLeft = swapLeftClick.createSubMenu();
            Menu subShift = swapShiftClick.createSubMenu();
            for (int actionIdx = 0; actionIdx < NPC_MENU_TYPES.size(); ++actionIdx) {
                if (Strings.isNullOrEmpty((String)actions[actionIdx]) || "Attack".equalsIgnoreCase(actions[actionIdx]) || "Knock-Out".equals(actions[actionIdx]) || "Lure".equals(actions[actionIdx])) continue;
                MenuAction menuAction2 = NPC_MENU_TYPES.get(actionIdx);
                if (menuAction2 != currentAction) {
                    subLeft.createMenuEntry(0).setOption(actions[actionIdx]).setType(MenuAction.RUNELITE).onClick(this.npcConsumer(composition, actions, actionIdx, menuAction2, false));
                }
                if (menuAction2 == currentShiftAction) continue;
                subShift.createMenuEntry(0).setOption(actions[actionIdx]).setType(MenuAction.RUNELITE).onClick(this.npcConsumer(composition, actions, actionIdx, menuAction2, true));
            }
            subLeft.createMenuEntry(0).setOption("Walk here").setType(MenuAction.RUNELITE).onClick(this.walkHereConsumer(false, composition));
            subShift.createMenuEntry(0).setOption("Walk here").setType(MenuAction.RUNELITE).onClick(this.walkHereConsumer(true, composition));
            if (this.getNpcSwapConfig(false, composition.getId()) != null) {
                subLeft.createMenuEntry(0).setOption("Reset").setType(MenuAction.RUNELITE).onClick(this.npcResetConsumer(composition, false));
            }
            if (this.getNpcSwapConfig(true, composition.getId()) == null) continue;
            subShift.createMenuEntry(0).setOption("Reset").setType(MenuAction.RUNELITE).onClick(this.npcResetConsumer(composition, true));
        }
    }

    private Consumer<MenuEntry> npcConsumer(NPCComposition composition, String[] actions, int menuIdx, MenuAction menuAction, boolean shift) {
        return e -> {
            String message = new ChatMessageBuilder().append("The default ").append(shift ? "shift" : "left").append(" click option for '").append(Text.removeTags(composition.getName())).append("' ").append("has been set to '").append(actions[menuIdx]).append("'.").build();
            this.chatMessageManager.queue(QueuedMessage.builder().type(ChatMessageType.CONSOLE).runeLiteFormattedMessage(message).build());
            log.debug("Set npc {} swap for {} to {}", new Object[]{shift ? "shift" : "left", composition.getId(), menuAction});
            this.setNpcSwapConfig(shift, composition.getId(), menuIdx);
        };
    }

    private Consumer<MenuEntry> npcResetConsumer(NPCComposition composition, boolean shift) {
        return e -> {
            String message = new ChatMessageBuilder().append("The default ").append(shift ? "shift" : "left").append(" click option for '").append(Text.removeTags(composition.getName())).append("' ").append("has been reset.").build();
            this.chatMessageManager.queue(QueuedMessage.builder().type(ChatMessageType.CONSOLE).runeLiteFormattedMessage(message).build());
            log.debug("Unset npc {} swap for {}", (Object)(shift ? "shift" : "left"), (Object)composition.getId());
            this.unsetNpcSwapConfig(shift, composition.getId());
        };
    }

    private void configureWornItems(MenuOpened event) {
        if (!this.shiftModifier()) {
            return;
        }
        MenuEntry[] entries = event.getMenuEntries();
        for (int idx = entries.length - 1; idx >= 0; --idx) {
            MenuEntry entry = entries[idx];
            Widget w = entry.getWidget();
            if (w == null || WidgetUtil.componentToInterface((int)w.getId()) != 387 || !"Examine".equals(entry.getOption()) || entry.getIdentifier() != 10) continue;
            if ((w = w.getChild(1)) == null || w.getItemId() <= -1) break;
            ItemComposition itemComposition = this.itemManager.getItemComposition(w.getItemId());
            Integer leftClickOp = this.getWornItemSwapConfig(false, itemComposition.getId());
            Integer shiftClickOp = this.getWornItemSwapConfig(true, itemComposition.getId());
            MenuEntry swapLeftClick = this.client.createMenuEntry(idx).setOption("Swap left-click").setTarget(entry.getTarget()).setType(MenuAction.RUNELITE);
            MenuEntry swapShiftClick = this.client.createMenuEntry(idx).setOption("Swap shift-click").setTarget(entry.getTarget()).setType(MenuAction.RUNELITE);
            Menu subLeft = swapLeftClick.createSubMenu();
            Menu subShift = swapShiftClick.createSubMenu();
            int paramId = 451;
            int componentOpId = 2;
            int itemOpId = 1;
            while (paramId <= 458) {
                String opName = itemComposition.getStringValue(paramId);
                if (!Strings.isNullOrEmpty((String)opName)) {
                    int[] subopParams;
                    if (leftClickOp == null || leftClickOp != componentOpId) {
                        subLeft.createMenuEntry(0).setOption(opName).setType(MenuAction.RUNELITE).onClick(this.wornItemConsumer(itemComposition, opName, componentOpId, false));
                    }
                    if (shiftClickOp == null || shiftClickOp != componentOpId) {
                        subShift.createMenuEntry(0).setOption(opName).setType(MenuAction.RUNELITE).onClick(this.wornItemConsumer(itemComposition, opName, componentOpId, true));
                    }
                    for (int subopParam : subopParams = EQUIPMENT_SUBOP_PARAMS[itemOpId - 1]) {
                        String subop = itemComposition.getStringValue(subopParam);
                        if (Strings.isNullOrEmpty((String)subop)) continue;
                        if (leftClickOp == null || leftClickOp.intValue() != subop.hashCode()) {
                            subLeft.createMenuEntry(0).setOption(subop).setType(MenuAction.RUNELITE).onClick(this.wornItemConsumer(itemComposition, subop, subop.hashCode(), false));
                        }
                        if (shiftClickOp != null && shiftClickOp.intValue() == subop.hashCode()) continue;
                        subShift.createMenuEntry(0).setOption(subop).setType(MenuAction.RUNELITE).onClick(this.wornItemConsumer(itemComposition, subop, subop.hashCode(), true));
                    }
                }
                ++paramId;
                ++componentOpId;
                ++itemOpId;
            }
            if (leftClickOp != null) {
                subLeft.createMenuEntry(0).setOption("Reset").setType(MenuAction.RUNELITE).onClick(e -> {
                    String message = new ChatMessageBuilder().append("The default worn left-click option for '").append(itemComposition.getMembersName()).append("' ").append("has been reset.").build();
                    this.chatMessageManager.queue(QueuedMessage.builder().type(ChatMessageType.CONSOLE).runeLiteFormattedMessage(message).build());
                    log.debug("Unset worn item left swap for {}", (Object)itemComposition.getMembersName());
                    this.unsetWornItemSwapConfig(false, itemComposition.getId());
                });
            }
            if (shiftClickOp == null) break;
            subShift.createMenuEntry(0).setOption("Reset").setType(MenuAction.RUNELITE).onClick(e -> {
                String message = new ChatMessageBuilder().append("The default worn shift-click option for '").append(itemComposition.getMembersName()).append("' ").append("has been reset.").build();
                this.chatMessageManager.queue(QueuedMessage.builder().type(ChatMessageType.CONSOLE).runeLiteFormattedMessage(message).build());
                log.debug("Unset worn item shift swap for {}", (Object)itemComposition.getMembersName());
                this.unsetWornItemSwapConfig(true, itemComposition.getId());
            });
            break;
        }
    }

    private Consumer<MenuEntry> wornItemConsumer(ItemComposition itemComposition, String opName, int opIdx, boolean shift) {
        return e -> {
            String message = new ChatMessageBuilder().append("The default worn ").append(shift ? "shift" : "left").append(" click option for '").append(Text.removeTags(itemComposition.getMembersName())).append("' ").append("has been set to '").append(opName).append("'.").build();
            this.chatMessageManager.queue(QueuedMessage.builder().type(ChatMessageType.CONSOLE).runeLiteFormattedMessage(message).build());
            log.debug("Set worn item {} swap for {} to {}", new Object[]{shift ? "shift" : "left", itemComposition.getMembersName(), opIdx});
            this.setWornItemSwapConfig(shift, itemComposition.getId(), opIdx);
        };
    }

    private void configureItems(MenuOpened event) {
        if (!this.shiftModifier()) {
            return;
        }
        MenuEntry[] entries = event.getMenuEntries();
        for (int idx = entries.length - 1; idx >= 0; --idx) {
            MenuEntry entry = entries[idx];
            Widget w = entry.getWidget();
            if (w == null || WidgetUtil.componentToInterface((int)w.getId()) != 149 || !"Examine".equals(entry.getOption()) || entry.getIdentifier() != 10) continue;
            ItemComposition itemComposition = this.itemManager.getItemComposition(entry.getItemId());
            String[] actions = itemComposition.getInventoryActions();
            String[][] subops = itemComposition.getSubops();
            Integer leftClickOp = this.getItemSwapConfig(false, itemComposition.getId());
            Integer shiftClickOp = this.getItemSwapConfig(true, itemComposition.getId());
            int defaultLeftClickOp = this.defaultOp(itemComposition, false);
            int defaultShiftClickOp = this.defaultOp(itemComposition, true);
            MenuEntry swapLeftClick = this.client.createMenuEntry(idx).setOption("Swap left-click").setTarget(entry.getTarget()).setType(MenuAction.RUNELITE);
            MenuEntry swapShiftClick = this.client.createMenuEntry(idx).setOption("Swap shift-click").setTarget(entry.getTarget()).setType(MenuAction.RUNELITE);
            Menu subLeft = swapLeftClick.createSubMenu();
            Menu subShift = swapShiftClick.createSubMenu();
            for (int actionIdx = 0; actionIdx < actions.length; ++actionIdx) {
                String opName = actions[actionIdx];
                if (!Strings.isNullOrEmpty((String)opName)) {
                    if (this.config.leftClickCustomization() && defaultLeftClickOp != actionIdx && (leftClickOp == null || leftClickOp != actionIdx)) {
                        subLeft.createMenuEntry(0).setOption(opName).setType(MenuAction.RUNELITE).onClick(this.heldItemConsumer(itemComposition, opName, actionIdx, false));
                    }
                    if (this.config.shiftClickCustomization() && defaultShiftClickOp != actionIdx && (shiftClickOp == null || shiftClickOp != actionIdx)) {
                        subShift.createMenuEntry(0).setOption(opName).setType(MenuAction.RUNELITE).onClick(this.heldItemConsumer(itemComposition, opName, actionIdx, true));
                    }
                }
                if (subops != null && subops[actionIdx] != null) {
                    for (String subop : subops[actionIdx]) {
                        if (subop == null) continue;
                        if (leftClickOp == null || leftClickOp.intValue() != subop.hashCode()) {
                            subLeft.createMenuEntry(0).setOption(subop).setType(MenuAction.RUNELITE).onClick(this.heldItemConsumer(itemComposition, subop, subop.hashCode(), false));
                        }
                        if (shiftClickOp != null && shiftClickOp.intValue() == subop.hashCode()) continue;
                        subShift.createMenuEntry(0).setOption(subop).setType(MenuAction.RUNELITE).onClick(this.heldItemConsumer(itemComposition, subop, subop.hashCode(), true));
                    }
                }
                if (actionIdx + 1 != 4) continue;
                if (defaultLeftClickOp != -1 && this.config.leftClickCustomization()) {
                    subLeft.createMenuEntry(0).setOption("Use").setType(MenuAction.RUNELITE).onClick(this.heldItemConsumer(itemComposition, "Use", -1, false));
                }
                if (defaultShiftClickOp == -1 || !this.config.shiftClickCustomization()) continue;
                subShift.createMenuEntry(0).setOption("Use").setType(MenuAction.RUNELITE).onClick(this.heldItemConsumer(itemComposition, "Use", -1, true));
            }
            if (leftClickOp != null && this.config.leftClickCustomization()) {
                subLeft.createMenuEntry(0).setOption("Reset").setType(MenuAction.RUNELITE).onClick(e -> {
                    String message = new ChatMessageBuilder().append("The default held left-click option for '").append(itemComposition.getMembersName()).append("' ").append("has been reset.").build();
                    this.chatMessageManager.queue(QueuedMessage.builder().type(ChatMessageType.CONSOLE).runeLiteFormattedMessage(message).build());
                    log.debug("Unset held item left swap for {}", (Object)itemComposition.getMembersName());
                    this.unsetItemSwapConfig(false, itemComposition.getId());
                });
            }
            if (shiftClickOp == null || !this.config.shiftClickCustomization()) break;
            subShift.createMenuEntry(0).setOption("Reset").setType(MenuAction.RUNELITE).onClick(e -> {
                String message = new ChatMessageBuilder().append("The default held shift-click option for '").append(itemComposition.getMembersName()).append("' ").append("has been reset.").build();
                this.chatMessageManager.queue(QueuedMessage.builder().type(ChatMessageType.CONSOLE).runeLiteFormattedMessage(message).build());
                log.debug("Unset held item shift swap for {}", (Object)itemComposition.getMembersName());
                this.unsetItemSwapConfig(true, itemComposition.getId());
            });
            break;
        }
    }

    private Consumer<MenuEntry> heldItemConsumer(ItemComposition itemComposition, String opName, int opIdx, boolean shift) {
        return e -> {
            String message = new ChatMessageBuilder().append("The default held ").append(shift ? "shift" : "left").append(" click option for '").append(Text.removeTags(itemComposition.getMembersName())).append("' ").append("has been set to '").append(opName).append("'.").build();
            this.chatMessageManager.queue(QueuedMessage.builder().type(ChatMessageType.CONSOLE).runeLiteFormattedMessage(message).build());
            log.debug("Set held item {} swap for {} to {}", new Object[]{shift ? "shift" : "left", itemComposition.getMembersName(), opIdx});
            this.setItemSwapConfig(shift, itemComposition.getId(), opIdx);
        };
    }

    private void configureUiSwap(MenuOpened event) {
        if (!this.shiftModifier()) {
            return;
        }
        MenuEntry[] entries = event.getMenuEntries();
        MenuEntry swapLeftClick = null;
        MenuEntry swapShiftClick = null;
        Menu subLeft = null;
        Menu subShift = null;
        boolean initialized = false;
        for (int idx = entries.length - 1; idx >= 0; --idx) {
            int interId;
            Widget w;
            MenuEntry entry = entries[idx];
            if (entry.getType() != MenuAction.CC_OP && entry.getType() != MenuAction.CC_OP_LOW_PRIORITY && entry.getType() != MenuAction.WIDGET_TARGET || (w = entry.getWidget()) == null || w.getActions() == null || (interId = WidgetUtil.componentToInterface((int)w.getId())) == 149 || interId == 387 && w.getId() != 25362460 || w.getIndex() != -1 && w.getItemId() == -1) continue;
            int componentId = w.getId();
            int itemId = w.getIndex() == -1 ? -1 : ItemVariationMapping.map(w.getItemId());
            int identifier = this.getMungedId(entry);
            Integer leftClick = this.getUiSwapConfig(false, componentId, itemId);
            Integer shiftClick = this.getUiSwapConfig(true, componentId, itemId);
            int lowestOp = this.getMungedId(this.findLowestOp(w), w.getId(), w.getIndex());
            int highestOp = 10;
            for (int i = idx; i >= 0; --i) {
                MenuEntry opEntry = entries[i];
                if (opEntry.getWidget() != w) continue;
                highestOp = this.getMungedId(opEntry);
            }
            if (!initialized) {
                initialized = true;
                swapLeftClick = this.client.createMenuEntry(2).setOption("Swap left-click").setType(MenuAction.RUNELITE);
                swapShiftClick = this.client.createMenuEntry(2).setOption("Swap shift-click").setType(MenuAction.RUNELITE);
                subLeft = swapLeftClick.createSubMenu();
                subShift = swapShiftClick.createSubMenu();
            }
            if (identifier != lowestOp && (leftClick == null || leftClick != identifier)) {
                subLeft.createMenuEntry(0).setOption(entry.getOption()).setType(MenuAction.RUNELITE).onClick(this.uiConsumer(entry.getOption(), entry.getTarget(), false, componentId, itemId, identifier));
            }
            if (identifier != lowestOp && (shiftClick == null || shiftClick != identifier)) {
                subShift.createMenuEntry(0).setOption(entry.getOption()).setType(MenuAction.RUNELITE).onClick(this.uiConsumer(entry.getOption(), entry.getTarget(), true, componentId, itemId, identifier));
            }
            if (identifier != highestOp) continue;
            if (leftClick != null) {
                subLeft.createMenuEntry(0).setOption("Reset").setType(MenuAction.RUNELITE).onClick(menuEntry -> {
                    String message = new ChatMessageBuilder().append("The default left-click option for '").append(Text.removeTags(entry.getTarget())).append("' ").append("has been reset.").build();
                    this.chatMessageManager.queue(QueuedMessage.builder().type(ChatMessageType.CONSOLE).runeLiteFormattedMessage(message).build());
                    log.debug("Unset ui left swap for {}/{}", (Object)componentId, (Object)menuEntry.getTarget());
                    this.unsetUiSwapConfig(false, componentId, itemId);
                });
            }
            if (shiftClick != null) {
                subShift.createMenuEntry(0).setOption("Reset").setType(MenuAction.RUNELITE).onClick(menuEntry -> {
                    String message = new ChatMessageBuilder().append("The default shift-click option for '").append(Text.removeTags(entry.getTarget())).append("' ").append("has been reset.").build();
                    this.chatMessageManager.queue(QueuedMessage.builder().type(ChatMessageType.CONSOLE).runeLiteFormattedMessage(message).build());
                    log.debug("Unset ui shift swap for {}/{}", (Object)componentId, (Object)menuEntry.getTarget());
                    this.unsetUiSwapConfig(true, componentId, itemId);
                });
            }
            swapLeftClick.setTarget(entry.getTarget());
            swapShiftClick.setTarget(entry.getTarget());
        }
    }

    private int findLowestOp(Widget w) {
        for (int i = 0; i <= 9; ++i) {
            if (i == 5 && this.isOpTarget(w) && !Strings.isNullOrEmpty((String)w.getTargetVerb())) {
                return 0;
            }
            if (!this.testOpMask(w, i) && w.getOnOpListener() == null || Strings.isNullOrEmpty((String)w.getActions()[i])) continue;
            return i + 1;
        }
        return -1;
    }

    private boolean testOpMask(Widget w, int op) {
        WidgetConfigNode n = (WidgetConfigNode)this.client.getWidgetFlags().get((long)w.getId() << 32 | (long)w.getIndex());
        int mask = n != null ? n.getClickMask() : w.getClickMask();
        return (mask >> op + 1 & 1) != 0;
    }

    private boolean isOpTarget(Widget w) {
        WidgetConfigNode n = (WidgetConfigNode)this.client.getWidgetFlags().get((long)w.getId() << 32 | (long)w.getIndex());
        int mask = n != null ? n.getClickMask() : w.getClickMask();
        return (mask & 0x1F800) != 0;
    }

    private Consumer<MenuEntry> uiConsumer(String option, String target, boolean shift, int componentId, int itemId, int opId) {
        return e -> {
            String message = new ChatMessageBuilder().append("The default ").append(shift ? "shift" : "left").append(" click option for '").append(Text.removeTags(target)).append("' ").append("has been set to '").append(Text.removeTags(option)).append("'.").build();
            this.chatMessageManager.queue(QueuedMessage.builder().type(ChatMessageType.CONSOLE).runeLiteFormattedMessage(message).build());
            log.debug("Set ui {} swap for {}/{} to {}", new Object[]{shift ? "shift" : "left", componentId, itemId, opId});
            this.setUiSwapConfig(shift, componentId, itemId, opId);
        };
    }

    private boolean swapBank(Menu menu, MenuEntry menuEntry, MenuAction type) {
        if (type != MenuAction.CC_OP && type != MenuAction.CC_OP_LOW_PRIORITY) {
            return false;
        }
        int widgetGroupId = WidgetUtil.componentToInterface((int)menuEntry.getParam1());
        boolean isDepositBoxPlayerInventory = widgetGroupId == 192;
        boolean isChambersOfXericStorageUnitPlayerInventory = widgetGroupId == 551;
        boolean isGroupStoragePlayerInventory = widgetGroupId == 725;
        int ident = this.getMungedId(menuEntry);
        if (this.shiftModifier() && this.config.bankDepositShiftClick() != ShiftDepositMode.OFF && type == MenuAction.CC_OP && ident == (isDepositBoxPlayerInventory || isGroupStoragePlayerInventory || isChambersOfXericStorageUnitPlayerInventory ? 1 : 2) && (menuEntry.getOption().startsWith("Deposit-") || menuEntry.getOption().startsWith("Store") || menuEntry.getOption().startsWith("Donate"))) {
            ShiftDepositMode shiftDepositMode = this.config.bankDepositShiftClick();
            int opId = isDepositBoxPlayerInventory ? shiftDepositMode.getIdentifierDepositBox() : (isChambersOfXericStorageUnitPlayerInventory ? shiftDepositMode.getIdentifierChambersStorageUnit() : (isGroupStoragePlayerInventory ? shiftDepositMode.getIdentifierGroupStorage() : shiftDepositMode.getIdentifier()));
            MenuAction action = opId >= 6 ? MenuAction.CC_OP_LOW_PRIORITY : MenuAction.CC_OP;
            this.bankModeSwap(menu, action, opId);
            return true;
        }
        if (this.shiftModifier() && this.config.bankWithdrawShiftClick() != ShiftWithdrawMode.OFF && type == MenuAction.CC_OP && ident == 1 && menuEntry.getOption().startsWith("Withdraw")) {
            int opId;
            MenuAction action;
            ShiftWithdrawMode shiftWithdrawMode = this.config.bankWithdrawShiftClick();
            if (widgetGroupId == 271 || widgetGroupId == 550) {
                action = MenuAction.CC_OP;
                opId = shiftWithdrawMode.getIdentifierChambersStorageUnit();
            } else {
                action = shiftWithdrawMode.getMenuAction();
                opId = shiftWithdrawMode.getIdentifier();
            }
            this.bankModeSwap(menu, action, opId);
            return true;
        }
        return false;
    }

    private void bankModeSwap(Menu menu, MenuAction entryType, int entryIdentifier) {
        MenuEntry[] menuEntries = menu.getMenuEntries();
        for (int i = menuEntries.length - 1; i >= 0; --i) {
            MenuEntry entry = menuEntries[i];
            if (entry.getType() != entryType || this.getMungedId(entry) != entryIdentifier) continue;
            entry.setType(MenuAction.CC_OP);
            menuEntries[i] = menuEntries[menuEntries.length - 1];
            menuEntries[menuEntries.length - 1] = entry;
            menu.setMenuEntries(menuEntries);
            break;
        }
    }

    private void swapMenuEntry(MenuEntry parent, Menu menu, MenuEntry[] menuEntries, int index, MenuEntry menuEntry) {
        Swap swap;
        MenuAction swapAction;
        Integer customOption;
        Integer wornItemSwapConfig;
        Widget child;
        Integer swapIndex;
        Widget w;
        Menu sub = menuEntry.getSubMenu();
        if (sub != null) {
            MenuEntry[] subEntries;
            int subidx = 0;
            for (MenuEntry subEntry : subEntries = sub.getMenuEntries()) {
                this.swapMenuEntry(menuEntry, sub, subEntries, subidx++, subEntry);
            }
        }
        int eventId = menuEntry.getIdentifier();
        MenuAction menuAction = menuEntry.getType();
        String option = Text.removeTags(menuEntry.getOption()).toLowerCase();
        String target = Text.removeTags(menuEntry.getTarget()).toLowerCase();
        Widget widget = w = parent != null ? parent.getWidget() : menuEntry.getWidget();
        if (w != null && WidgetUtil.componentToInterface((int)w.getId()) == 149 && (this.lastShift ? this.config.shiftClickCustomization() : this.config.leftClickCustomization()) && (swapIndex = this.getItemSwapConfig(this.lastShift, w.getItemId())) != null) {
            if (swapIndex == -1) {
                this.swap(menu, menuEntries, "use", target, index, true);
            } else if (swapIndex + 1 == menuEntry.getItemOp()) {
                this.swap(menu, menuEntries, index, menuEntries.length - 1);
            } else if (parent != null && menuEntry.getOption().hashCode() == swapIndex.intValue()) {
                this.client.createMenuEntry(-1).setOption(menuEntry.getOption()).setTarget(menuEntry.getTarget()).setIdentifier(menuEntry.getIdentifier()).setType(menuEntry.getType() == MenuAction.CC_OP_LOW_PRIORITY ? MenuAction.CC_OP : menuEntry.getType()).setItemId(menuEntry.getItemId()).setParam0(menuEntry.getParam0()).setParam1(menuEntry.getParam1()).onClick(menuEntry.onClick());
            }
            return;
        }
        if (w != null && WidgetUtil.componentToInterface((int)w.getId()) == 387 && (child = w.getChild(1)) != null && child.getItemId() > -1 && (wornItemSwapConfig = this.getWornItemSwapConfig(this.shiftModifier(), child.getItemId())) != null) {
            if (wornItemSwapConfig.intValue() == menuEntry.getIdentifier()) {
                this.swap(menu, menuEntries, index, menuEntries.length - 1);
            } else if (parent != null && menuEntry.getOption().hashCode() == wornItemSwapConfig.intValue()) {
                this.client.createMenuEntry(-1).setOption(menuEntry.getOption()).setTarget(menuEntry.getTarget()).setIdentifier(menuEntry.getIdentifier()).setType(menuEntry.getType() == MenuAction.CC_OP_LOW_PRIORITY ? MenuAction.CC_OP : menuEntry.getType()).setItemId(menuEntry.getItemId()).setParam0(menuEntry.getParam0()).setParam1(menuEntry.getParam1()).onClick(menuEntry.onClick());
            }
            return;
        }
        if (OBJECT_MENU_TYPES.contains(menuAction)) {
            int objectId = eventId;
            ObjectComposition objectComposition = this.client.getObjectDefinition(objectId);
            if (objectComposition.getImpostorIds() != null) {
                objectComposition = objectComposition.getImpostor();
                objectId = objectComposition.getId();
            }
            if ((customOption = this.getObjectSwapConfig(this.shiftModifier(), objectId)) != null && customOption >= 0 && (swapAction = OBJECT_MENU_TYPES.get(customOption)) == menuAction) {
                this.swap(menu, menuEntries, index, menuEntries.length - 1);
                return;
            }
        }
        if (NPC_MENU_TYPES.contains(menuAction)) {
            NPC npc = menuEntry.getNpc();
            assert (npc != null);
            NPCComposition composition = npc.getTransformedComposition();
            assert (composition != null);
            customOption = this.getNpcSwapConfig(this.shiftModifier(), composition.getId());
            if (customOption != null && customOption >= 0 && (swapAction = NPC_MENU_TYPES.get(customOption)) == menuAction) {
                int i;
                for (i = index; i < menuEntries.length - 1 && NPC_MENU_TYPES.contains(menuEntries[i + 1].getType()); ++i) {
                }
                this.swap(menu, menuEntries, index, i);
                return;
            }
        }
        if (!(menuAction != MenuAction.CC_OP && menuAction != MenuAction.CC_OP_LOW_PRIORITY && menuAction != MenuAction.WIDGET_TARGET || w == null || w.getIndex() != -1 && w.getItemId() == -1 || w.getActions() == null || WidgetUtil.componentToInterface((int)w.getId()) == 149 || WidgetUtil.componentToInterface((int)w.getId()) == 387 && w.getId() != 25362460 || (index <= 0 || menuEntries[index - 1].getWidget() != w) && (index + 1 >= menuEntries.length || menuEntries[index + 1].getWidget() != w))) {
            int componentId = w.getId();
            int itemId = w.getIndex() == -1 ? -1 : ItemVariationMapping.map(w.getItemId());
            Integer op = this.getUiSwapConfig(this.shiftModifier(), componentId, itemId);
            if (op != null && op.intValue() == this.getMungedId(menuEntry)) {
                this.swap(menu, menuEntries, index, menuEntries.length - 1);
                return;
            }
        }
        if (this.swapBank(menu, menuEntry, menuAction)) {
            return;
        }
        NPC hintArrowNpc = this.client.getHintArrowNpc();
        if (hintArrowNpc != null && hintArrowNpc.getIndex() == eventId && NPC_MENU_TYPES.contains(menuAction)) {
            return;
        }
        Collection swaps = this.swaps.get((Object)option);
        Iterator iterator = swaps.iterator();
        while (!(!iterator.hasNext() || (swap = (Swap)iterator.next()).getTargetPredicate().test(target) && swap.getEnabled().get().booleanValue() && this.swap(menu, menuEntries, swap.getSwappedOption(), target, index, swap.isStrict()))) {
        }
    }

    @Subscribe
    public void onClientTick(ClientTick clientTick) {
        this.lastShift = this.curShift;
        this.curShift = this.shiftModifier();
        if (this.client.isMenuOpen()) {
            return;
        }
        for (MenuEntry menuEntry : this.client.getMenuEntries()) {
            boolean shift;
            Integer customOption;
            MenuAction type = menuEntry.getType();
            if (OBJECT_MENU_TYPES.contains(type)) {
                int objectId = menuEntry.getIdentifier();
                ObjectComposition objectComposition = this.client.getObjectDefinition(objectId);
                if (objectComposition.getImpostorIds() != null) {
                    objectComposition = objectComposition.getImpostor();
                    objectId = objectComposition.getId();
                }
                if (((customOption = this.getObjectSwapConfig(shift = this.shiftModifier(), objectId)) != null || !shift || !this.config.objectShiftClickWalkHere()) && (customOption == null || customOption != -1)) continue;
                menuEntry.setDeprioritized(true);
                continue;
            }
            if (NPC_MENU_TYPES.contains(type)) {
                NPC npc = menuEntry.getNpc();
                assert (npc != null);
                NPCComposition composition = npc.getTransformedComposition();
                assert (composition != null);
                shift = this.shiftModifier();
                customOption = this.getNpcSwapConfig(shift, composition.getId());
                if ((customOption != null || !shift || !this.config.npcShiftClickWalkHere()) && (customOption == null || customOption != -1)) continue;
                menuEntry.setDeprioritized(true);
                continue;
            }
            if (type != MenuAction.GROUND_ITEM_FIRST_OPTION && type != MenuAction.GROUND_ITEM_SECOND_OPTION && type != MenuAction.GROUND_ITEM_THIRD_OPTION && type != MenuAction.GROUND_ITEM_FOURTH_OPTION && type != MenuAction.GROUND_ITEM_FIFTH_OPTION || !this.shiftModifier() || !this.config.groundItemShiftClickWalkHere()) continue;
            menuEntry.setDeprioritized(true);
        }
    }

    @Subscribe
    public void onPostMenuSort(PostMenuSort postMenuSort) {
        if (this.client.isMenuOpen()) {
            return;
        }
        Menu root = this.client.getMenu();
        MenuEntry[] menuEntries = root.getMenuEntries();
        int idx = 0;
        for (MenuEntry entry : menuEntries) {
            this.swapMenuEntry(null, root, menuEntries, idx++, entry);
        }
        if (this.config.removeDeadNpcMenus()) {
            this.removeDeadNpcs();
        }
        this.cacheOptionIndexes.clear();
        this.cacheOptionMenu = null;
    }

    private void removeDeadNpcs() {
        MenuEntry[] newEntries;
        MenuEntry[] oldEntries = this.client.getMenuEntries();
        if (oldEntries.length != (newEntries = (MenuEntry[])Arrays.stream(oldEntries).filter(e -> {
            NPC npc = e.getNpc();
            return npc == null || !this.npcUtil.isDying(npc);
        }).toArray(MenuEntry[]::new)).length) {
            this.client.setMenuEntries(newEntries);
        }
    }

    private boolean swap(Menu menu, MenuEntry[] menuEntries, String option, String target, int index, boolean strict) {
        int optionIdx = this.findIndex(menu, menuEntries, index, option, target, strict);
        if (optionIdx >= 0) {
            this.swap(menu, menuEntries, optionIdx, index);
            return true;
        }
        return false;
    }

    private int findIndex(Menu menu, MenuEntry[] entries, int limit, String option, String target, boolean strict) {
        if (strict) {
            List<Integer> indexes = this.findOptionIndex(menu, option);
            for (int i = indexes.size() - 1; i >= 0; --i) {
                int idx = indexes.get(i);
                MenuEntry entry = entries[idx];
                String entryTarget = Text.removeTags(entry.getTarget()).toLowerCase();
                if (idx >= limit || !entryTarget.equals(target)) continue;
                return idx;
            }
        } else {
            for (int i = limit - 1; i >= 0; --i) {
                MenuEntry entry = entries[i];
                String entryOption = Text.removeTags(entry.getOption()).toLowerCase();
                String entryTarget = Text.removeTags(entry.getTarget()).toLowerCase();
                if (!entryOption.contains(option.toLowerCase()) || !entryTarget.equals(target)) continue;
                return i;
            }
        }
        return -1;
    }

    private List<Integer> findOptionIndex(Menu menu, String option) {
        if (this.cacheOptionMenu != menu || this.cacheOptionIndexes.isEmpty()) {
            int idx = 0;
            this.cacheOptionMenu = menu;
            this.cacheOptionIndexes.clear();
            for (MenuEntry entry : menu.getMenuEntries()) {
                String opt = Text.removeTags(entry.getOption()).toLowerCase();
                this.cacheOptionIndexes.put((Object)opt, (Object)idx++);
            }
            log.trace("[{}] Rebuilt option index cache with {} entries", (Object)this.client.getGameCycle(), (Object)idx);
        }
        return this.cacheOptionIndexes.get((Object)option);
    }

    private void swap(Menu menu, MenuEntry[] entries, int index1, int index2) {
        MenuEntry entry2;
        if (index1 == index2) {
            return;
        }
        MenuEntry entry1 = entries[index1];
        entries[index1] = entry2 = entries[index2];
        entries[index2] = entry1;
        if (entry1.getType() == MenuAction.CC_OP_LOW_PRIORITY) {
            entry1.setType(MenuAction.CC_OP);
        }
        if (entry2.getType() == MenuAction.CC_OP_LOW_PRIORITY) {
            entry2.setType(MenuAction.CC_OP);
        }
        menu.setMenuEntries(entries);
        if (this.cacheOptionMenu == menu) {
            String option1 = Text.removeTags(entry1.getOption()).toLowerCase();
            String option2 = Text.removeTags(entry2.getOption()).toLowerCase();
            List list1 = this.cacheOptionIndexes.get((Object)option1);
            List list2 = this.cacheOptionIndexes.get((Object)option2);
            list1.remove((Object)index1);
            list2.remove((Object)index2);
            MenuEntrySwapperPlugin.sortedInsert(list1, index2);
            MenuEntrySwapperPlugin.sortedInsert(list2, index1);
            log.trace("Swapped option index {} <-> {}", (Object)index1, (Object)index2);
        }
    }

    private static <T extends Comparable<? super T>> void sortedInsert(List<T> list, T value) {
        int idx = Collections.binarySearch(list, value);
        list.add(idx < 0 ? -idx - 1 : idx, value);
    }

    private boolean shiftModifier() {
        return this.client.isKeyPressed(81);
    }

    private Integer getObjectSwapConfig(boolean shift, int objectId) {
        String config = this.configManager.getConfiguration("menuentryswapper", (shift ? OBJECT_SHIFT_KEY_PREFIX : OBJECT_KEY_PREFIX) + objectId);
        if (config == null || config.isEmpty()) {
            return null;
        }
        return Integer.parseInt(config);
    }

    private void setObjectSwapConfig(boolean shift, int objectId, int index) {
        this.configManager.setConfiguration("menuentryswapper", (shift ? OBJECT_SHIFT_KEY_PREFIX : OBJECT_KEY_PREFIX) + objectId, index);
    }

    private void unsetObjectSwapConfig(boolean shift, int objectId) {
        this.configManager.unsetConfiguration("menuentryswapper", (shift ? OBJECT_SHIFT_KEY_PREFIX : OBJECT_KEY_PREFIX) + objectId);
    }

    private static MenuAction defaultAction(ObjectComposition objectComposition) {
        String[] actions = objectComposition.getActions();
        for (int i = 0; i < OBJECT_MENU_TYPES.size() - 1; ++i) {
            if (Strings.isNullOrEmpty((String)actions[i])) continue;
            return OBJECT_MENU_TYPES.get(i);
        }
        return null;
    }

    private Integer getNpcSwapConfig(boolean shift, int npcId) {
        String config = this.configManager.getConfiguration("menuentryswapper", (shift ? NPC_SHIFT_KEY_PREFIX : NPC_KEY_PREFIX) + npcId);
        if (config == null || config.isEmpty()) {
            return null;
        }
        return Integer.parseInt(config);
    }

    private void setNpcSwapConfig(boolean shift, int npcId, int index) {
        this.configManager.setConfiguration("menuentryswapper", (shift ? NPC_SHIFT_KEY_PREFIX : NPC_KEY_PREFIX) + npcId, index);
    }

    private void unsetNpcSwapConfig(boolean shift, int npcId) {
        this.configManager.unsetConfiguration("menuentryswapper", (shift ? NPC_SHIFT_KEY_PREFIX : NPC_KEY_PREFIX) + npcId);
    }

    private static MenuAction defaultAction(NPCComposition composition) {
        String[] actions = composition.getActions();
        for (int i = 0; i < NPC_MENU_TYPES.size(); ++i) {
            if (Strings.isNullOrEmpty((String)actions[i]) || actions[i].equalsIgnoreCase("Attack")) continue;
            return NPC_MENU_TYPES.get(i);
        }
        return null;
    }

    private int defaultOp(ItemComposition itemComposition, boolean shift) {
        int shiftClickActionIndex;
        if (shift && (shiftClickActionIndex = itemComposition.getShiftClickActionIndex()) >= 0) {
            return shiftClickActionIndex;
        }
        String[] actions = itemComposition.getInventoryActions();
        for (int actionIdx = 0; actionIdx < 3; ++actionIdx) {
            if (Strings.isNullOrEmpty((String)actions[actionIdx])) continue;
            return actionIdx;
        }
        return -1;
    }

    private Integer getUiSwapConfig(boolean shift, int componentId, int itemId) {
        String config = this.configManager.getConfiguration("menuentryswapper", (shift ? UI_SHIFT_KEY_PREFIX : UI_KEY_PREFIX) + componentId + (String)(itemId != -1 ? "_" + itemId : ""));
        if (config == null || config.isEmpty()) {
            return null;
        }
        return Integer.parseInt(config);
    }

    private void setUiSwapConfig(boolean shift, int componentId, int itemId, int op) {
        this.configManager.setConfiguration("menuentryswapper", (shift ? UI_SHIFT_KEY_PREFIX : UI_KEY_PREFIX) + componentId + (String)(itemId != -1 ? "_" + itemId : ""), op);
    }

    private void unsetUiSwapConfig(boolean shift, int componentId, int itemId) {
        this.configManager.unsetConfiguration("menuentryswapper", (shift ? UI_SHIFT_KEY_PREFIX : UI_KEY_PREFIX) + componentId + (String)(itemId != -1 ? "_" + itemId : ""));
    }

    private int getMungedId(MenuEntry entry) {
        if (entry.getType() == MenuAction.CC_OP || entry.getType() == MenuAction.CC_OP_LOW_PRIORITY) {
            return this.getMungedId(entry.getIdentifier(), entry.getParam1(), entry.getParam0());
        }
        return entry.getIdentifier();
    }

    private int getMungedId(int ident, int widgetId, int childIdx) {
        if (widgetId == 786445 && childIdx >= 0) {
            int delta = ident;
            int exclude = this.client.getVarbitValue(6590);
            if (delta == 1) {
                return 1;
            }
            if (exclude != 0 && --delta == 1) {
                return 2;
            }
            if (exclude != 1 && --delta == 1) {
                return 3;
            }
            if (exclude != 2 && --delta == 1) {
                return 4;
            }
            if (exclude != 3 && this.client.getVarbitValue(3960) > 0 && --delta == 1) {
                return 5;
            }
            if (--delta == 1) {
                return 6;
            }
            if (exclude != 4 && --delta == 1) {
                return 7;
            }
            if (--delta == 1) {
                return 8;
            }
        }
        return ident;
    }
}

