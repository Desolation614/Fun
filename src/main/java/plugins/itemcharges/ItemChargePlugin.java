/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.primitives.Ints
 *  com.google.inject.Provides
 *  javax.inject.Inject
 *  net.runelite.api.ChatMessageType
 *  net.runelite.api.Client
 *  net.runelite.api.EquipmentInventorySlot
 *  net.runelite.api.GameState
 *  net.runelite.api.Item
 *  net.runelite.api.ItemContainer
 *  net.runelite.api.events.ChatMessage
 *  net.runelite.api.events.GameStateChanged
 *  net.runelite.api.events.ItemContainerChanged
 *  net.runelite.api.events.VarbitChanged
 *  net.runelite.api.events.WidgetLoaded
 *  net.runelite.api.widgets.Widget
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package net.runelite.client.plugins.itemcharges;

import com.google.common.primitives.Ints;
import com.google.inject.Provides;
import java.awt.Color;
import java.lang.reflect.Type;
import java.util.EnumMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.inject.Inject;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.EquipmentInventorySlot;
import net.runelite.api.GameState;
import net.runelite.api.Item;
import net.runelite.api.ItemContainer;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.ItemContainerChanged;
import net.runelite.api.events.VarbitChanged;
import net.runelite.api.events.WidgetLoaded;
import net.runelite.api.widgets.Widget;
import net.runelite.client.Notifier;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.itemcharges.ItemChargeConfig;
import net.runelite.client.plugins.itemcharges.ItemChargeInfobox;
import net.runelite.client.plugins.itemcharges.ItemChargeOverlay;
import net.runelite.client.plugins.itemcharges.ItemChargeType;
import net.runelite.client.plugins.itemcharges.ItemWithCharge;
import net.runelite.client.plugins.itemcharges.ItemWithConfig;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.ui.overlay.infobox.InfoBoxManager;
import net.runelite.client.util.AsyncBufferedImage;
import net.runelite.client.util.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@PluginDescriptor(name="Item Charges", description="Show number of item charges remaining", tags={"inventory", "notifications", "overlay"})
public class ItemChargePlugin
extends Plugin {
    private static final Logger log = LoggerFactory.getLogger(ItemChargePlugin.class);
    private static final Pattern DODGY_CHECK_PATTERN = Pattern.compile("Your dodgy necklace has (\\d+) charges? left\\.");
    private static final Pattern DODGY_PROTECT_PATTERN = Pattern.compile("Your dodgy necklace protects you\\..*It has (\\d+) charges? left\\.");
    private static final Pattern DODGY_BREAK_PATTERN = Pattern.compile("Your dodgy necklace protects you\\..*It then crumbles to dust\\.");
    private static final String RING_OF_RECOIL_BREAK_MESSAGE = "Your Ring of Recoil has shattered.";
    private static final String BINDING_BREAK_TEXT = "Your Binding necklace has disintegrated.";
    private static final Pattern RING_OF_FORGING_CHECK_PATTERN = Pattern.compile("You can smelt ([0-9]+|one) more pieces? of iron ore before a ring melts\\.");
    private static final String RING_OF_FORGING_USED_TEXT = "You retrieve a bar of iron.";
    private static final String RING_OF_FORGING_BREAK_TEXT = "Your Ring of Forging has melted.";
    private static final String RING_OF_FORGING_VARROCK_PLATEBODY = "The Varrock platebody enabled you to smelt your next ore simultaneously.";
    private static final Pattern AMULET_OF_CHEMISTRY_CHECK_PATTERN = Pattern.compile("Your amulet of chemistry has (\\d) charges? left\\.");
    private static final Pattern AMULET_OF_CHEMISTRY_USED_PATTERN = Pattern.compile("Your amulet of chemistry helps you create a \\d-dose potion\\. It has (\\d|one) charges? left\\.");
    private static final Pattern AMULET_OF_CHEMISTRY_BREAK_PATTERN = Pattern.compile("Your amulet of chemistry helps you create a \\d-dose potion\\. It then crumbles to dust\\.");
    private static final Pattern AMULET_OF_BOUNTY_CHECK_PATTERN = Pattern.compile("Your amulet of bounty has (\\d+) charges? left\\.");
    private static final Pattern AMULET_OF_BOUNTY_USED_PATTERN = Pattern.compile("Your amulet of bounty saves some seeds for you\\. It has (\\d) charges? left\\.");
    private static final String AMULET_OF_BOUNTY_BREAK_TEXT = "Your amulet of bounty saves some seeds for you. It then crumbles to dust.";
    private static final Pattern CHRONICLE_ADD_PATTERN = Pattern.compile("You add (?:\\d+|a single) charges? to your book\\. It now has (\\d+|one) charges?\\.");
    private static final Pattern CHRONICLE_USE_AND_CHECK_PATTERN = Pattern.compile("Your book has (\\d+) charges left\\.");
    private static final String CHRONICLE_FULL_TEXT = "Your book is fully charged! It has 1,000 charges already.";
    private static final String CHRONICLE_ONE_CHARGE_TEXT = "You have one charge left in your book.";
    private static final String CHRONICLE_EMPTY_TEXT = "Your book has run out of charges.";
    private static final String CHRONICLE_NO_CHARGES_TEXT = "Your book does not have any charges. Purchase some Teleport Cards from Diango.";
    private static final Pattern BRACELET_OF_SLAUGHTER_ACTIVATE_PATTERN = Pattern.compile("Your bracelet of slaughter prevents your slayer count from decreasing. (?:It has (\\d{1,2}) charges? left\\.|It then crumbles to dust\\.|It then regenerates itself to full charge!)");
    private static final Pattern BRACELET_OF_SLAUGHTER_CHECK_PATTERN = Pattern.compile("Your bracelet of slaughter has (\\d{1,2}) charges? left\\.");
    private static final String BRACELET_OF_SLAUGHTER_BREAK_TEXT = "Your Bracelet of Slaughter has crumbled to dust.";
    private static final Pattern EXPEDITIOUS_BRACELET_ACTIVATE_PATTERN = Pattern.compile("Your expeditious bracelet helps you progress your slayer (?:task )?faster. (?:It has (\\d{1,2}) charges? left\\.|It then crumbles to dust\\.|It then regenerates itself to full charge!)");
    private static final Pattern EXPEDITIOUS_BRACELET_CHECK_PATTERN = Pattern.compile("Your expeditious bracelet has (\\d{1,2}) charges? left\\.");
    private static final String EXPEDITIOUS_BRACELET_BREAK_TEXT = "Your Expeditious Bracelet has crumbled to dust.";
    private static final Pattern BLOOD_ESSENCE_CHECK_PATTERN = Pattern.compile("Your blood essence has (\\d{1,4}) charges? remaining");
    private static final Pattern BLOOD_ESSENCE_EXTRACT_PATTERN = Pattern.compile("You manage to extract power from the Blood Essence and craft (\\d{1,3}) extra runes?\\.");
    private static final String BLOOD_ESSENCE_ACTIVATE_TEXT = "You activate the blood essence.";
    private static final String BRACELET_OF_CLAY_USE_TEXT = "You manage to mine some clay.";
    private static final String BRACELET_OF_CLAY_USE_TEXT_TRAHAEARN = "You manage to mine some soft clay.";
    private static final String BRACELET_OF_CLAY_BREAK_TEXT = "Your bracelet of clay crumbles to dust.";
    private static final Pattern BRACELET_OF_CLAY_CHECK_PATTERN = Pattern.compile("You can mine (\\d{1,2}) more pieces? of soft clay before your bracelet crumbles to dust\\.");
    private static final int MAX_DODGY_CHARGES = 10;
    private static final int MAX_BINDING_CHARGES = 16;
    private static final int MAX_EXPLORER_RING_CHARGES = 30;
    private static final int MAX_RING_OF_FORGING_CHARGES = 140;
    private static final int MAX_AMULET_OF_CHEMISTRY_CHARGES = 5;
    private static final int MAX_AMULET_OF_BOUNTY_CHARGES = 10;
    private static final int MAX_SLAYER_BRACELET_CHARGES = 30;
    private static final int MAX_BLOOD_ESSENCE_CHARGES = 1000;
    private static final int MAX_BRACELET_OF_CLAY_CHARGES = 28;
    @Inject
    private Client client;
    @Inject
    private ClientThread clientThread;
    @Inject
    private ConfigManager configManager;
    @Inject
    private OverlayManager overlayManager;
    @Inject
    private ItemChargeOverlay overlay;
    @Inject
    private ItemManager itemManager;
    @Inject
    private InfoBoxManager infoBoxManager;
    @Inject
    private Notifier notifier;
    @Inject
    private ItemChargeConfig config;
    private int lastCheckTick;
    private final Map<EquipmentInventorySlot, ItemChargeInfobox> infoboxes = new EnumMap<EquipmentInventorySlot, ItemChargeInfobox>(EquipmentInventorySlot.class);
    private boolean loginFlag;

    @Provides
    ItemChargeConfig getConfig(ConfigManager configManager) {
        return configManager.getConfig(ItemChargeConfig.class);
    }

    @Override
    protected void startUp() {
        this.overlayManager.add(this.overlay);
        if (this.client.getGameState() == GameState.LOGGED_IN) {
            this.clientThread.invokeLater(() -> this.updateExplorerRingCharges(this.client.getVarbitValue(4554)));
        }
    }

    @Override
    protected void shutDown() throws Exception {
        this.overlayManager.remove(this.overlay);
        this.infoBoxManager.removeIf(ItemChargeInfobox.class::isInstance);
        this.infoboxes.clear();
        this.lastCheckTick = -1;
    }

    @Subscribe
    public void onGameStateChanged(GameStateChanged e) {
        switch (e.getGameState()) {
            case LOGGING_IN: {
                this.loginFlag = true;
                break;
            }
            case LOGGED_IN: {
                if (!this.loginFlag) break;
                this.loginFlag = false;
                this.updateExplorerRingCharges(0);
            }
        }
    }

    @Subscribe
    public void onConfigChanged(ConfigChanged event) {
        if (!event.getGroup().equals("itemCharge")) {
            return;
        }
        this.clientThread.invoke(this::updateInfoboxes);
    }

    @Subscribe
    public void onChatMessage(ChatMessage event) {
        if (event.getType() == ChatMessageType.GAMEMESSAGE || event.getType() == ChatMessageType.SPAM) {
            String message = Text.removeTags(event.getMessage());
            Matcher dodgyCheckMatcher = DODGY_CHECK_PATTERN.matcher(message);
            Matcher dodgyProtectMatcher = DODGY_PROTECT_PATTERN.matcher(message);
            Matcher dodgyBreakMatcher = DODGY_BREAK_PATTERN.matcher(message);
            Matcher ringOfForgingCheckMatcher = RING_OF_FORGING_CHECK_PATTERN.matcher(message);
            Matcher amuletOfChemistryCheckMatcher = AMULET_OF_CHEMISTRY_CHECK_PATTERN.matcher(message);
            Matcher amuletOfChemistryUsedMatcher = AMULET_OF_CHEMISTRY_USED_PATTERN.matcher(message);
            Matcher amuletOfChemistryBreakMatcher = AMULET_OF_CHEMISTRY_BREAK_PATTERN.matcher(message);
            Matcher amuletOfBountyCheckMatcher = AMULET_OF_BOUNTY_CHECK_PATTERN.matcher(message);
            Matcher amuletOfBountyUsedMatcher = AMULET_OF_BOUNTY_USED_PATTERN.matcher(message);
            Matcher chronicleAddMatcher = CHRONICLE_ADD_PATTERN.matcher(message);
            Matcher chronicleUseAndCheckMatcher = CHRONICLE_USE_AND_CHECK_PATTERN.matcher(message);
            Matcher slaughterActivateMatcher = BRACELET_OF_SLAUGHTER_ACTIVATE_PATTERN.matcher(message);
            Matcher slaughterCheckMatcher = BRACELET_OF_SLAUGHTER_CHECK_PATTERN.matcher(message);
            Matcher expeditiousActivateMatcher = EXPEDITIOUS_BRACELET_ACTIVATE_PATTERN.matcher(message);
            Matcher expeditiousCheckMatcher = EXPEDITIOUS_BRACELET_CHECK_PATTERN.matcher(message);
            Matcher bloodEssenceCheckMatcher = BLOOD_ESSENCE_CHECK_PATTERN.matcher(message);
            Matcher bloodEssenceExtractMatcher = BLOOD_ESSENCE_EXTRACT_PATTERN.matcher(message);
            Matcher braceletOfClayCheckMatcher = BRACELET_OF_CLAY_CHECK_PATTERN.matcher(message);
            if (message.contains(RING_OF_RECOIL_BREAK_MESSAGE)) {
                this.notifier.notify(this.config.recoilNotification(), "Your Ring of Recoil has shattered");
            } else if (dodgyBreakMatcher.find()) {
                this.notifier.notify(this.config.dodgyNotification(), "Your dodgy necklace has crumbled to dust.");
                this.updateDodgyNecklaceCharges(10);
            } else if (dodgyCheckMatcher.find()) {
                this.updateDodgyNecklaceCharges(Integer.parseInt(dodgyCheckMatcher.group(1)));
            } else if (dodgyProtectMatcher.find()) {
                this.updateDodgyNecklaceCharges(Integer.parseInt(dodgyProtectMatcher.group(1)));
            } else if (amuletOfChemistryCheckMatcher.find()) {
                this.updateAmuletOfChemistryCharges(Integer.parseInt(amuletOfChemistryCheckMatcher.group(1)));
            } else if (amuletOfChemistryUsedMatcher.find()) {
                String match = amuletOfChemistryUsedMatcher.group(1);
                int charges = 1;
                if (!match.equals("one")) {
                    charges = Integer.parseInt(match);
                }
                this.updateAmuletOfChemistryCharges(charges);
            } else if (amuletOfChemistryBreakMatcher.find()) {
                this.notifier.notify(this.config.amuletOfChemistryNotification(), "Your amulet of chemistry has crumbled to dust.");
                this.updateAmuletOfChemistryCharges(5);
            } else if (amuletOfBountyCheckMatcher.find()) {
                this.updateAmuletOfBountyCharges(Integer.parseInt(amuletOfBountyCheckMatcher.group(1)));
            } else if (amuletOfBountyUsedMatcher.find()) {
                this.updateAmuletOfBountyCharges(Integer.parseInt(amuletOfBountyUsedMatcher.group(1)));
            } else if (message.equals(AMULET_OF_BOUNTY_BREAK_TEXT)) {
                this.updateAmuletOfBountyCharges(10);
            } else if (message.contains(BINDING_BREAK_TEXT)) {
                this.notifier.notify(this.config.bindingNotification(), BINDING_BREAK_TEXT);
                this.updateBindingNecklaceCharges(16);
            } else if (ringOfForgingCheckMatcher.find()) {
                String match = ringOfForgingCheckMatcher.group(1);
                int charges = 1;
                if (!match.equals("one")) {
                    charges = Integer.parseInt(match);
                }
                this.updateRingOfForgingCharges(charges);
            } else if (message.equals(RING_OF_FORGING_USED_TEXT) || message.equals(RING_OF_FORGING_VARROCK_PLATEBODY)) {
                ItemContainer inventory = this.client.getItemContainer(93);
                ItemContainer equipment = this.client.getItemContainer(94);
                if (equipment == null) {
                    return;
                }
                if (equipment.contains(2568) && (message.equals(RING_OF_FORGING_USED_TEXT) || inventory.count(440) > 1)) {
                    int charges = Ints.constrainToRange((int)(this.getItemCharges("ringOfForging") - 1), (int)0, (int)140);
                    this.updateRingOfForgingCharges(charges);
                }
            } else if (message.equals(RING_OF_FORGING_BREAK_TEXT)) {
                this.notifier.notify(this.config.ringOfForgingNotification(), "Your ring of forging has melted.");
                this.updateRingOfForgingCharges(141);
            } else if (chronicleAddMatcher.find()) {
                String match = chronicleAddMatcher.group(1);
                if (match.equals("one")) {
                    this.setItemCharges("chronicle", 1);
                } else {
                    this.setItemCharges("chronicle", Integer.parseInt(match));
                }
            } else if (chronicleUseAndCheckMatcher.find()) {
                this.setItemCharges("chronicle", Integer.parseInt(chronicleUseAndCheckMatcher.group(1)));
            } else if (message.equals(CHRONICLE_ONE_CHARGE_TEXT)) {
                this.setItemCharges("chronicle", 1);
            } else if (message.equals(CHRONICLE_EMPTY_TEXT) || message.equals(CHRONICLE_NO_CHARGES_TEXT)) {
                this.setItemCharges("chronicle", 0);
            } else if (message.equals(CHRONICLE_FULL_TEXT)) {
                this.setItemCharges("chronicle", 1000);
            } else if (slaughterActivateMatcher.find()) {
                String found = slaughterActivateMatcher.group(1);
                if (found == null) {
                    this.updateBraceletOfSlaughterCharges(30);
                    this.notifier.notify(this.config.slaughterNotification(), BRACELET_OF_SLAUGHTER_BREAK_TEXT);
                } else {
                    this.updateBraceletOfSlaughterCharges(Integer.parseInt(found));
                }
            } else if (slaughterCheckMatcher.find()) {
                this.updateBraceletOfSlaughterCharges(Integer.parseInt(slaughterCheckMatcher.group(1)));
            } else if (expeditiousActivateMatcher.find()) {
                String found = expeditiousActivateMatcher.group(1);
                if (found == null) {
                    this.updateExpeditiousBraceletCharges(30);
                    this.notifier.notify(this.config.expeditiousNotification(), EXPEDITIOUS_BRACELET_BREAK_TEXT);
                } else {
                    this.updateExpeditiousBraceletCharges(Integer.parseInt(found));
                }
            } else if (expeditiousCheckMatcher.find()) {
                this.updateExpeditiousBraceletCharges(Integer.parseInt(expeditiousCheckMatcher.group(1)));
            } else if (bloodEssenceCheckMatcher.find()) {
                this.updateBloodEssenceCharges(Integer.parseInt(bloodEssenceCheckMatcher.group(1)));
            } else if (bloodEssenceExtractMatcher.find()) {
                this.updateBloodEssenceCharges(this.getItemCharges("bloodEssence") - Integer.parseInt(bloodEssenceExtractMatcher.group(1)));
            } else if (message.contains(BLOOD_ESSENCE_ACTIVATE_TEXT)) {
                this.updateBloodEssenceCharges(1000);
            } else if (braceletOfClayCheckMatcher.find()) {
                this.updateBraceletOfClayCharges(Integer.parseInt(braceletOfClayCheckMatcher.group(1)));
            } else if (message.equals(BRACELET_OF_CLAY_USE_TEXT) || message.equals(BRACELET_OF_CLAY_USE_TEXT_TRAHAEARN)) {
                ItemContainer equipment = this.client.getItemContainer(94);
                if (equipment != null && equipment.contains(11074)) {
                    boolean ignore;
                    ItemContainer inventory = this.client.getItemContainer(93);
                    boolean bl = ignore = inventory != null && inventory.count() == 27 && message.equals(BRACELET_OF_CLAY_USE_TEXT_TRAHAEARN);
                    if (!ignore) {
                        int charges = Ints.constrainToRange((int)(this.getItemCharges("braceletOfClay") - 1), (int)0, (int)28);
                        this.updateBraceletOfClayCharges(charges);
                    }
                }
            } else if (message.equals(BRACELET_OF_CLAY_BREAK_TEXT)) {
                this.notifier.notify(this.config.braceletOfClayNotification(), "Your bracelet of clay has crumbled to dust");
                this.updateBraceletOfClayCharges(28);
            }
        }
    }

    @Subscribe
    public void onItemContainerChanged(ItemContainerChanged event) {
        if (event.getContainerId() != 94) {
            return;
        }
        this.updateInfoboxes();
    }

    @Subscribe
    private void onVarbitChanged(VarbitChanged event) {
        if (event.getVarbitId() == 4554) {
            this.updateExplorerRingCharges(event.getValue());
        } else if (event.getVarpId() == 487) {
            this.updateBindingNecklaceCharges(event.getValue());
        }
    }

    @Subscribe
    public void onWidgetLoaded(WidgetLoaded widgetLoaded) {
        if (widgetLoaded.getGroupId() == 193) {
            this.clientThread.invokeLater(() -> {
                Widget sprite = this.client.getWidget(0xC10001);
                if (sprite != null) {
                    switch (sprite.getItemId()) {
                        case 21143: {
                            log.debug("Reset dodgy necklace");
                            this.updateDodgyNecklaceCharges(10);
                            break;
                        }
                        case 2568: {
                            log.debug("Reset ring of forging");
                            this.updateRingOfForgingCharges(140);
                            break;
                        }
                        case 21163: {
                            log.debug("Reset amulet of chemistry");
                            this.updateAmuletOfChemistryCharges(5);
                            break;
                        }
                        case 21183: {
                            log.debug("Reset bracelet of slaughter");
                            this.updateBraceletOfSlaughterCharges(30);
                            break;
                        }
                        case 21177: {
                            log.debug("Reset expeditious bracelet");
                            this.updateExpeditiousBraceletCharges(30);
                        }
                    }
                }
            });
        }
    }

    private void updateDodgyNecklaceCharges(int value) {
        this.setItemCharges("dodgyNecklace", value);
        this.updateInfoboxes();
    }

    private void updateAmuletOfChemistryCharges(int value) {
        this.setItemCharges("amuletOfChemistry", value);
        this.updateInfoboxes();
    }

    private void updateAmuletOfBountyCharges(int value) {
        this.setItemCharges("amuletOfBounty", value);
        this.updateInfoboxes();
    }

    private void updateBindingNecklaceCharges(int value) {
        this.setItemCharges("bindingNecklace", value);
        this.updateInfoboxes();
    }

    private void updateExplorerRingCharges(int value) {
        this.setItemCharges("explorerRing", 30 - value);
        this.updateInfoboxes();
    }

    private void updateRingOfForgingCharges(int value) {
        this.setItemCharges("ringOfForging", value);
        this.updateInfoboxes();
    }

    private void updateBraceletOfSlaughterCharges(int value) {
        this.setItemCharges("braceletOfSlaughter", value);
        this.updateInfoboxes();
    }

    private void updateExpeditiousBraceletCharges(int value) {
        this.setItemCharges("expeditiousBracelet", value);
        this.updateInfoboxes();
    }

    private void updateBloodEssenceCharges(int value) {
        this.setItemCharges("bloodEssence", value);
        this.updateInfoboxes();
    }

    private void updateBraceletOfClayCharges(int value) {
        this.setItemCharges("braceletOfClay", value);
        this.updateInfoboxes();
    }

    private void updateInfoboxes() {
        ItemContainer itemContainer = this.client.getItemContainer(94);
        if (itemContainer == null) {
            return;
        }
        Item[] items = itemContainer.getItems();
        boolean showInfoboxes = this.config.showInfoboxes();
        for (EquipmentInventorySlot slot : EquipmentInventorySlot.values()) {
            ItemChargeInfobox infobox;
            boolean enabled;
            if (slot.getSlotIdx() >= items.length) break;
            Item i = items[slot.getSlotIdx()];
            int id = i.getId();
            ItemChargeType type = null;
            int charges = -1;
            ItemWithCharge itemWithCharge = ItemWithCharge.findItem(id);
            if (itemWithCharge != null) {
                type = itemWithCharge.getType();
                charges = itemWithCharge.getCharges();
            } else {
                ItemWithConfig itemWithConfig = ItemWithConfig.findItem(id);
                if (itemWithConfig != null) {
                    type = itemWithConfig.getType();
                    charges = this.getItemCharges(itemWithConfig.getConfigKey());
                }
            }
            boolean bl = enabled = type != null && type.getEnabled().test(this.config);
            if (showInfoboxes && enabled && charges > 0) {
                infobox = this.infoboxes.get(slot);
                if (infobox != null) {
                    if (infobox.getItem() == id) {
                        if (infobox.getCount() == charges) continue;
                        log.debug("Updating infobox count for {}", (Object)infobox);
                        infobox.setCount(charges);
                        continue;
                    }
                    log.debug("Rebuilding infobox {}", (Object)infobox);
                    this.infoBoxManager.removeInfoBox(infobox);
                    this.infoboxes.remove(slot);
                }
                String name = this.itemManager.getItemComposition(id).getName();
                AsyncBufferedImage image = this.itemManager.getImage(id);
                infobox = new ItemChargeInfobox(this, image, name, charges, id);
                this.infoBoxManager.addInfoBox(infobox);
                this.infoboxes.put(slot, infobox);
                continue;
            }
            infobox = this.infoboxes.remove(slot);
            if (infobox == null) continue;
            log.debug("Removing infobox {}", (Object)infobox);
            this.infoBoxManager.removeInfoBox(infobox);
        }
    }

    int getItemCharges(String key) {
        Integer i = (Integer)this.configManager.getConfiguration("itemCharge", key, (Type)((Object)Integer.class));
        if (i != null) {
            this.configManager.unsetConfiguration("itemCharge", key);
            this.configManager.setRSProfileConfiguration("itemCharge", key, i);
            return i;
        }
        i = (Integer)this.configManager.getRSProfileConfiguration("itemCharge", key, (Type)((Object)Integer.class));
        return i == null ? -1 : i;
    }

    private void setItemCharges(String key, int value) {
        this.configManager.setRSProfileConfiguration("itemCharge", key, value);
    }

    Color getColor(int charges) {
        Color color = Color.WHITE;
        if (charges <= this.config.veryLowWarning()) {
            color = this.config.veryLowWarningColor();
        } else if (charges <= this.config.lowWarning()) {
            color = this.config.lowWarningolor();
        }
        return color;
    }
}

