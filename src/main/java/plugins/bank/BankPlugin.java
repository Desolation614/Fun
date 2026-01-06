/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.base.Strings
 *  com.google.common.collect.HashMultiset
 *  com.google.common.collect.Multiset
 *  com.google.inject.Provides
 *  javax.annotation.Nullable
 *  javax.inject.Inject
 *  net.runelite.api.Client
 *  net.runelite.api.EnumComposition
 *  net.runelite.api.Item
 *  net.runelite.api.ItemComposition
 *  net.runelite.api.ItemContainer
 *  net.runelite.api.MenuEntry
 *  net.runelite.api.WidgetNode
 *  net.runelite.api.events.ItemContainerChanged
 *  net.runelite.api.events.MenuEntryAdded
 *  net.runelite.api.events.MenuShouldLeftClick
 *  net.runelite.api.events.ScriptCallbackEvent
 *  net.runelite.api.events.ScriptPostFired
 *  net.runelite.api.events.ScriptPreFired
 *  net.runelite.api.events.WidgetLoaded
 *  net.runelite.api.widgets.Widget
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package net.runelite.client.plugins.bank;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Strings;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import com.google.inject.Provides;
import java.awt.event.KeyEvent;
import java.text.ParseException;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Nullable;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.EnumComposition;
import net.runelite.api.Item;
import net.runelite.api.ItemComposition;
import net.runelite.api.ItemContainer;
import net.runelite.api.MenuEntry;
import net.runelite.api.WidgetNode;
import net.runelite.api.events.ItemContainerChanged;
import net.runelite.api.events.MenuEntryAdded;
import net.runelite.api.events.MenuShouldLeftClick;
import net.runelite.api.events.ScriptCallbackEvent;
import net.runelite.api.events.ScriptPostFired;
import net.runelite.api.events.ScriptPreFired;
import net.runelite.api.events.WidgetLoaded;
import net.runelite.api.widgets.Widget;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.config.Keybind;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.game.ItemManager;
import net.runelite.client.input.KeyListener;
import net.runelite.client.input.KeyManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.bank.BankConfig;
import net.runelite.client.plugins.bank.BankSearch;
import net.runelite.client.plugins.bank.ContainerPrices;
import net.runelite.client.util.QuantityFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@PluginDescriptor(name="Bank", description="Modifications to the banking interface", tags={"grand", "exchange", "high", "alchemy", "prices", "deposit", "pin", "sanctum"})
public class BankPlugin
extends Plugin {
    private static final Logger log = LoggerFactory.getLogger(BankPlugin.class);
    private static final String DEPOSIT_WORN = "Deposit worn items";
    private static final String DEPOSIT_INVENTORY = "Deposit inventory";
    private static final String DEPOSIT_LOOT = "Deposit loot";
    private static final String TOGGLE_PLACEHOLDERS = "Always set placeholders";
    private static final String SEED_VAULT_TITLE = "Seed Vault";
    private static final int POTION_STORE_TAB = 15;
    private static final String NUMBER_REGEX = "[0-9]+(\\.[0-9]+)?[kmb]?";
    private static final Pattern VALUE_SEARCH_PATTERN = Pattern.compile("^(?<mode>qty|ge|ha|alch|sanctum)? *(?<individual>i|iv|individual|per)? *(((?<op>[<>=]|>=|<=) *(?<num>[0-9]+(\\.[0-9]+)?[kmb]?))|((?<num1>[0-9]+(\\.[0-9]+)?[kmb]?) *- *(?<num2>[0-9]+(\\.[0-9]+)?[kmb]?)))$", 2);
    @Inject
    private Client client;
    @Inject
    private ClientThread clientThread;
    @Inject
    private ItemManager itemManager;
    @Inject
    private BankConfig config;
    @Inject
    private BankSearch bankSearch;
    @Inject
    private KeyManager keyManager;
    private boolean forceRightClickFlag;
    private Multiset<Integer> itemQuantities;
    private String searchString;
    private ContainerPrices prices;
    private final KeyListener searchHotkeyListener = new KeyListener(){

        @Override
        public void keyTyped(KeyEvent e) {
        }

        @Override
        public void keyPressed(KeyEvent e) {
            Keybind keybind = BankPlugin.this.config.searchKeybind();
            if (keybind.matches(e)) {
                Widget seedVaultSearchButton;
                Widget groupStorageSearchButton;
                Widget bankContainer = BankPlugin.this.client.getWidget(786445);
                if (bankContainer != null && !bankContainer.isSelfHidden()) {
                    log.debug("Search hotkey pressed");
                    BankPlugin.this.bankSearch.initSearch();
                    e.consume();
                }
                if ((groupStorageSearchButton = BankPlugin.this.client.getWidget(47448080)) != null) {
                    log.debug("Search hotkey pressed");
                    BankPlugin.this.clientThread.invoke(() -> {
                        Widget searchButton = BankPlugin.this.client.getWidget(47448080);
                        if (searchButton == null || searchButton.isHidden()) {
                            return;
                        }
                        Object[] searchToggleArgs = searchButton.getOnOpListener();
                        if (searchToggleArgs == null) {
                            return;
                        }
                        BankPlugin.this.client.createScriptEvent(searchToggleArgs).setOp(1).run();
                    });
                    e.consume();
                }
                if ((seedVaultSearchButton = BankPlugin.this.client.getWidget(41353240)) != null) {
                    log.debug("Search hotkey pressed");
                    BankPlugin.this.clientThread.invoke(() -> {
                        Widget searchButton = BankPlugin.this.client.getWidget(41353240);
                        if (searchButton == null || searchButton.isHidden()) {
                            return;
                        }
                        BankPlugin.this.client.runScript(searchButton.getOnOpListener());
                    });
                    e.consume();
                }
            }
        }

        @Override
        public void keyReleased(KeyEvent e) {
        }
    };

    @Provides
    BankConfig getConfig(ConfigManager configManager) {
        return configManager.getConfig(BankConfig.class);
    }

    @Override
    protected void startUp() {
        this.keyManager.registerKeyListener(this.searchHotkeyListener);
    }

    @Override
    protected void shutDown() {
        this.keyManager.unregisterKeyListener(this.searchHotkeyListener);
        this.clientThread.invokeLater(() -> this.bankSearch.reset(false));
        this.forceRightClickFlag = false;
        this.itemQuantities = null;
        this.searchString = null;
    }

    @Subscribe
    public void onMenuShouldLeftClick(MenuShouldLeftClick event) {
        MenuEntry[] menuEntries;
        if (!this.forceRightClickFlag) {
            return;
        }
        this.forceRightClickFlag = false;
        for (MenuEntry entry : menuEntries = this.client.getMenuEntries()) {
            if (!(entry.getOption().equals(DEPOSIT_WORN) && this.config.rightClickBankEquip() || entry.getOption().equals(DEPOSIT_INVENTORY) && this.config.rightClickBankInventory() || entry.getOption().equals(DEPOSIT_LOOT) && this.config.rightClickBankLoot()) && (!entry.getTarget().contains(TOGGLE_PLACEHOLDERS) || !this.config.rightClickPlaceholders())) continue;
            event.setForceRightClick(true);
            return;
        }
    }

    @Subscribe
    public void onMenuEntryAdded(MenuEntryAdded event) {
        if (event.getOption().equals(DEPOSIT_WORN) && this.config.rightClickBankEquip() || event.getOption().equals(DEPOSIT_INVENTORY) && this.config.rightClickBankInventory() || event.getOption().equals(DEPOSIT_LOOT) && this.config.rightClickBankLoot() || event.getTarget().contains(TOGGLE_PLACEHOLDERS) && this.config.rightClickPlaceholders()) {
            this.forceRightClickFlag = true;
        }
    }

    @Subscribe
    public void onScriptCallbackEvent(ScriptCallbackEvent event) {
        int[] intStack = this.client.getIntStack();
        Object[] objectStack = this.client.getObjectStack();
        int intStackSize = this.client.getIntStackSize();
        int objectStackSize = this.client.getObjectStackSize();
        switch (event.getEventName()) {
            case "bankSearchFilter": {
                int itemId = intStack[intStackSize - 1];
                String search = (String)objectStack[objectStackSize - 1];
                if (!this.valueSearch(itemId, search)) break;
                intStack[intStackSize - 2] = 1;
                break;
            }
            case "bankpinButtonSetup": {
                if (!this.config.bankPinKeyboard()) {
                    return;
                }
                int compId = intStack[intStackSize - 2];
                int buttonId = intStack[intStackSize - 1];
                Widget button = this.client.getWidget(compId);
                Widget buttonRect = button.getChild(0);
                Object[] onOpListener = buttonRect.getOnOpListener();
                buttonRect.setOnKeyListener(new Object[]{e -> {
                    int typedChar = e.getTypedKeyChar() - 48;
                    if (typedChar != buttonId) {
                        return;
                    }
                    log.debug("Bank pin keypress");
                    this.client.runScript(onOpListener);
                    this.client.setVarcIntValue(187, this.client.getGameCycle() + 1);
                }});
                break;
            }
        }
    }

    @Subscribe
    public void onWidgetLoaded(WidgetLoaded event) {
        if (event.getGroupId() == 631 && this.config.seedVaultValue()) {
            this.clientThread.invokeLater(this::updateSeedVaultTotal);
        }
    }

    @Subscribe(priority=1.0f)
    public void onScriptPreFired(ScriptPreFired event) {
        Object text;
        if (event.getScriptId() == 505) {
            if (this.client.getVarbitValue(4150) != 15) {
                this.prices = this.getWidgetContainerPrices(786445, 95);
            }
        } else if (event.getScriptId() == 4212 && ((String)(text = event.getScriptEvent().getArguments()[1])).startsWith("Want more bank space?") && this.config.blockJagexAccountAd()) {
            WidgetNode wn = (WidgetNode)this.client.getComponentTable().get(786559L);
            this.clientThread.invokeAtTickEnd(() -> this.client.closeInterface(wn, true));
        }
    }

    @Subscribe
    public void onScriptPostFired(ScriptPostFired event) {
        int scriptId = event.getScriptId();
        if (scriptId == 6080 || scriptId == 6555) {
            this.prices = this.getPotionStoragePrice();
            Widget bankTitle = this.client.getWidget(786435);
            bankTitle.setText("Potion store " + this.createValueText(this.prices.getGePrice(), this.prices.getHighAlchPrice()));
        } else if (scriptId == 505) {
            if (this.prices != null) {
                Widget bankTitle = this.client.getWidget(786435);
                bankTitle.setText(bankTitle.getText() + this.createValueText(this.prices.getGePrice(), this.prices.getHighAlchPrice()));
            }
        } else if (scriptId == 283) {
            String inputText = this.client.getVarcStrValue(359);
            if (this.searchString != inputText && this.client.getGameCycle() % 40 != 0) {
                this.clientThread.invokeLater(this.bankSearch::layoutBank);
                this.searchString = inputText;
            }
        } else if (scriptId == 5269) {
            ContainerPrices price = this.getWidgetContainerPrices(47448074, 659);
            if (price == null) {
                return;
            }
            Widget bankTitle = this.client.getWidget(47448066).getChild(1);
            bankTitle.setText(bankTitle.getText() + this.createValueText(price.getGePrice(), price.getHighAlchPrice()));
        }
    }

    @Subscribe
    public void onItemContainerChanged(ItemContainerChanged event) {
        int containerId = event.getContainerId();
        if (containerId == 95) {
            this.itemQuantities = null;
        } else if (containerId == 626 && this.config.seedVaultValue()) {
            this.updateSeedVaultTotal();
        }
    }

    private String createValueText(long gePrice, long haPrice) {
        StringBuilder stringBuilder = new StringBuilder();
        if (this.config.showGE() && gePrice != 0L) {
            stringBuilder.append(" (");
            if (this.config.showHA()) {
                stringBuilder.append("GE: ");
            }
            if (this.config.showExact()) {
                stringBuilder.append(QuantityFormatter.formatNumber(gePrice));
            } else {
                stringBuilder.append(QuantityFormatter.quantityToStackSize(gePrice));
            }
            stringBuilder.append(')');
        }
        if (this.config.showHA() && haPrice != 0L) {
            stringBuilder.append(" (");
            if (this.config.showGE()) {
                stringBuilder.append("HA: ");
            }
            if (this.config.showExact()) {
                stringBuilder.append(QuantityFormatter.formatNumber(haPrice));
            } else {
                stringBuilder.append(QuantityFormatter.quantityToStackSize(haPrice));
            }
            stringBuilder.append(')');
        }
        return stringBuilder.toString();
    }

    private void updateSeedVaultTotal() {
        Widget titleContainer = this.client.getWidget(0x2770002);
        if (titleContainer == null) {
            return;
        }
        Widget title = titleContainer.getChild(1);
        if (title == null) {
            return;
        }
        ContainerPrices prices = this.calculate(this.getSeedVaultItems());
        if (prices == null) {
            return;
        }
        String titleText = this.createValueText(prices.getGePrice(), prices.getHighAlchPrice());
        title.setText(SEED_VAULT_TITLE + titleText);
    }

    private Item[] getSeedVaultItems() {
        ItemContainer itemContainer = this.client.getItemContainer(626);
        if (itemContainer == null) {
            return null;
        }
        return itemContainer.getItems();
    }

    @VisibleForTesting
    boolean valueSearch(int itemId, String str) {
        String op;
        Matcher matcher = VALUE_SEARCH_PATTERN.matcher(str);
        if (!matcher.matches()) {
            return false;
        }
        if (this.itemQuantities == null) {
            this.itemQuantities = this.getBankItemSet();
        }
        ItemComposition itemComposition = this.itemManager.getItemComposition(itemId);
        int qty = matcher.group("individual") != null ? 1 : this.itemQuantities.count((Object)itemId);
        long gePrice = (long)this.itemManager.getItemPrice(itemId) * (long)qty;
        long haPrice = (long)itemComposition.getHaPrice() * (long)qty;
        boolean isPlaceholder = itemComposition.getPlaceholderTemplateId() != -1;
        int sanctumValue = this.client.getEnum(20176).getIntValue(itemId);
        long value = Math.max(gePrice, haPrice);
        String mode = matcher.group("mode");
        if (mode != null) {
            switch (mode.toLowerCase()) {
                case "qty": {
                    value = isPlaceholder ? 0L : (long)qty;
                    break;
                }
                case "ge": {
                    value = gePrice;
                    break;
                }
                case "ha": 
                case "alch": {
                    value = haPrice;
                    break;
                }
                case "sanctum": {
                    value = sanctumValue;
                }
            }
        }
        if ((op = matcher.group("op")) != null) {
            long compare;
            try {
                compare = QuantityFormatter.parseQuantity(matcher.group("num"));
            }
            catch (ParseException e) {
                return false;
            }
            switch (op) {
                case ">": {
                    return value > compare;
                }
                case "<": {
                    return value < compare;
                }
                case "=": {
                    return value == compare;
                }
                case ">=": {
                    return value >= compare;
                }
                case "<=": {
                    return value <= compare;
                }
            }
        }
        String num1 = matcher.group("num1");
        String num2 = matcher.group("num2");
        if (num1 != null && num2 != null) {
            long compare2;
            long compare1;
            try {
                compare1 = QuantityFormatter.parseQuantity(num1);
                compare2 = QuantityFormatter.parseQuantity(num2);
            }
            catch (ParseException e) {
                return false;
            }
            return compare1 <= value && compare2 >= value;
        }
        return false;
    }

    private Multiset<Integer> getBankItemSet() {
        ItemContainer itemContainer = this.client.getItemContainer(95);
        if (itemContainer == null) {
            return HashMultiset.create();
        }
        HashMultiset set = HashMultiset.create();
        for (Item item : itemContainer.getItems()) {
            if (item.getId() == 20594) continue;
            set.add((Object)item.getId(), item.getQuantity());
        }
        return set;
    }

    @Nullable
    ContainerPrices calculate(@Nullable Item[] items) {
        if (items == null) {
            return null;
        }
        long ge = 0L;
        long alch = 0L;
        for (Item item : items) {
            int qty = item.getQuantity();
            int id = item.getId();
            if (id <= 0 || qty == 0) continue;
            alch += (long)this.getHaPrice(id) * (long)qty;
            ge += (long)this.itemManager.getItemPrice(id) * (long)qty;
        }
        return new ContainerPrices(ge, alch);
    }

    private int getHaPrice(int itemId) {
        switch (itemId) {
            case 995: {
                return 1;
            }
            case 13204: {
                return 1000;
            }
        }
        return this.itemManager.getItemComposition(itemId).getHaPrice();
    }

    private ContainerPrices getWidgetContainerPrices(int componentId, int inventoryID) {
        Widget widget = this.client.getWidget(componentId);
        ItemContainer itemContainer = this.client.getItemContainer(inventoryID);
        Widget[] children = widget.getChildren();
        ContainerPrices prices = null;
        if (itemContainer != null && children != null) {
            long geTotal = 0L;
            long haTotal = 0L;
            log.debug("Computing bank price of {} items", (Object)itemContainer.size());
            for (int i = 0; i < itemContainer.size(); ++i) {
                Widget child = children[i];
                if (child == null || child.isSelfHidden() || child.getItemId() <= -1) continue;
                int alchPrice = this.getHaPrice(child.getItemId());
                geTotal += (long)this.itemManager.getItemPrice(child.getItemId()) * (long)child.getItemQuantity();
                haTotal += (long)alchPrice * (long)child.getItemQuantity();
            }
            prices = new ContainerPrices(geTotal, haTotal);
        }
        return prices;
    }

    private ContainerPrices getPotionStoragePrice() {
        EnumComposition potionEnum;
        HashMap<Integer, EnumComposition> potionMap = new HashMap<Integer, EnumComposition>();
        EnumComposition potionStorePotions = this.client.getEnum(4826);
        for (int potionEnumId : potionStorePotions.getIntVals()) {
            potionEnum = this.client.getEnum(potionEnumId);
            for (int doses = 1; doses <= 4; ++doses) {
                int itemId = potionEnum.getIntValue(doses);
                if (itemId <= -1) continue;
                potionMap.put(itemId, potionEnum);
            }
        }
        potionStorePotions = this.client.getEnum(4829);
        for (int potionEnumId : potionStorePotions.getIntVals()) {
            potionEnum = this.client.getEnum(potionEnumId);
            int itemId = potionEnum.getIntValue(1);
            potionMap.put(itemId, potionEnum);
        }
        Widget w = this.client.getWidget(786484);
        Widget[] children = w.getDynamicChildren();
        long geTotal = 0L;
        long haTotal = 0L;
        int i = 0;
        while (i + 4 < children.length) {
            Widget wItem = children[i + 1];
            Widget wDoses = children[i + 3];
            if (wItem.getItemId() != -1 && !Strings.isNullOrEmpty((String)wDoses.getText())) {
                int itemId = wItem.getItemId();
                int doses = Integer.parseInt(wDoses.getText().split(": ")[1]);
                EnumComposition potionEnum2 = (EnumComposition)potionMap.get(itemId);
                if (potionEnum2 != null) {
                    int withdrawDoses;
                    for (withdrawDoses = 1; withdrawDoses < 4 && potionEnum2.getIntValue(withdrawDoses) != itemId; ++withdrawDoses) {
                    }
                    int qty = doses / withdrawDoses;
                    log.debug("Potion store has {} of {} (doses={}, withdrawDoses={})", new Object[]{qty, itemId, doses, withdrawDoses});
                    geTotal += (long)this.itemManager.getItemPrice(itemId) * (long)qty;
                    haTotal += (long)this.getHaPrice(itemId) * (long)qty;
                }
            }
            i += 5;
        }
        return new ContainerPrices(geTotal, haTotal);
    }
}

