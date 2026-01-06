/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.common.primitives.Shorts
 *  com.google.inject.Binder
 *  com.google.inject.Provides
 *  javax.inject.Inject
 *  javax.inject.Named
 *  net.runelite.api.Client
 *  net.runelite.api.ItemComposition
 *  net.runelite.api.MenuAction
 *  net.runelite.api.MenuEntry
 *  net.runelite.api.events.GrandExchangeSearched
 *  net.runelite.api.events.MenuEntryAdded
 *  net.runelite.api.events.ScriptCallbackEvent
 *  net.runelite.api.widgets.Widget
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package net.runelite.client.plugins.banktags;

import com.google.common.collect.Lists;
import com.google.common.primitives.Shorts;
import com.google.inject.Binder;
import com.google.inject.Provides;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
import javax.inject.Inject;
import javax.inject.Named;
import net.runelite.api.Client;
import net.runelite.api.ItemComposition;
import net.runelite.api.MenuAction;
import net.runelite.api.MenuEntry;
import net.runelite.api.events.GrandExchangeSearched;
import net.runelite.api.events.MenuEntryAdded;
import net.runelite.api.events.ScriptCallbackEvent;
import net.runelite.api.widgets.Widget;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.game.ItemManager;
import net.runelite.client.game.ItemVariationMapping;
import net.runelite.client.game.SpriteManager;
import net.runelite.client.game.chatbox.ChatboxPanelManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.bank.BankSearch;
import net.runelite.client.plugins.banktags.BankTag;
import net.runelite.client.plugins.banktags.BankTagsConfig;
import net.runelite.client.plugins.banktags.BankTagsService;
import net.runelite.client.plugins.banktags.TagManager;
import net.runelite.client.plugins.banktags.tabs.Layout;
import net.runelite.client.plugins.banktags.tabs.LayoutManager;
import net.runelite.client.plugins.banktags.tabs.TabInterface;
import net.runelite.client.plugins.banktags.tabs.TabSprites;
import net.runelite.client.util.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@PluginDescriptor(name="Bank Tags", description="Enable tagging of bank items and searching of bank tags", tags={"searching", "tagging"})
public class BankTagsPlugin
extends Plugin
implements BankTagsService {
    private static final Logger log = LoggerFactory.getLogger(BankTagsPlugin.class);
    public static final String CONFIG_GROUP = "banktags";
    public static final String TAG_ICON_PREFIX = "icon_";
    public static final String TAG_TABS_CONFIG = "tagtabs";
    public static final String TAG_LAYOUT_PREFIX = "layout_";
    static final String ITEM_KEY_PREFIX = "item_";
    static final String TAG_HIDDEN_PREFIX = "hidden_";
    public static final String TAG_SEARCH = "tag:";
    private static final String EDIT_TAGS_MENU_OPTION = "Edit-tags";
    public static final String VAR_TAG_SUFFIX = "*";
    private static final int MAX_RESULT_COUNT = 250;
    private static final String SEARCH_BANK_INPUT_TEXT = "Show items whose names or tags contain the following text:<br>(To show only tagged items, start your search with 'tag:')";
    private static final String SEARCH_BANK_INPUT_TEXT_FOUND = "Show items whose names or tags contain the following text: (%d found)<br>(To show only tagged items, start your search with 'tag:')";
    public static final int BANK_ITEM_WIDTH = 36;
    public static final int BANK_ITEM_HEIGHT = 32;
    public static final int BANK_ITEM_X_PADDING = 12;
    public static final int BANK_ITEM_Y_PADDING = 4;
    public static final int BANK_ITEMS_PER_ROW = 8;
    public static final int BANK_ITEM_START_X = 51;
    public static final int BANK_ITEM_START_Y = 0;
    @Inject
    private ItemManager itemManager;
    @Inject
    private Client client;
    @Inject
    private ClientThread clientThread;
    @Inject
    private ChatboxPanelManager chatboxPanelManager;
    @Inject
    private TagManager tagManager;
    @Inject
    private TabInterface tabInterface;
    @Inject
    private LayoutManager layoutManager;
    @Inject
    private SpriteManager spriteManager;
    @Inject
    private ConfigManager configManager;
    @Inject
    private EventBus eventBus;
    @Inject
    private BankSearch bankSearch;
    @Inject
    private BankTagsConfig config;
    @Inject
    @Named(value="developerMode")
    boolean developerMode;
    private String activeTag;
    private BankTag activeBankTag;
    private Layout activeLayout;
    private int options;

    @Override
    public void configure(Binder binder) {
        binder.bind(BankTagsService.class).toInstance((Object)this);
    }

    @Provides
    BankTagsConfig getConfig(ConfigManager configManager) {
        return configManager.getConfig(BankTagsConfig.class);
    }

    @Override
    public void resetConfiguration() {
        ArrayList extraKeys = Lists.newArrayList((Object[])new String[]{"banktags.item_", "banktags.icon_", "banktags.tagtabs", "banktags.layout_"});
        for (String prefix : extraKeys) {
            List<String> keys = this.configManager.getConfigurationKeys(prefix);
            for (String key : keys) {
                String[] str = key.split("\\.", 2);
                if (str.length != 2) continue;
                this.configManager.unsetConfiguration(str[0], str[1]);
            }
        }
        this.clientThread.invokeLater(this::reinitBank);
    }

    @Override
    public void startUp() {
        this.cleanConfig();
        this.spriteManager.addSpriteOverrides(TabSprites.values());
        this.eventBus.register(this.tabInterface);
        this.layoutManager.register();
        this.clientThread.invokeLater(this::reinitBank);
    }

    @Override
    public void shutDown() {
        this.eventBus.unregister(this.tabInterface);
        this.layoutManager.unregister();
        this.clientThread.invokeLater(() -> {
            this.tabInterface.deinit();
            this.reinitBank();
        });
        this.spriteManager.removeSpriteOverrides(TabSprites.values());
    }

    private void reinitBank() {
        Widget w = this.client.getWidget(786433);
        if (w != null) {
            this.client.createScriptEvent(w.getOnLoadListener()).setSource(w).run();
        }
    }

    @Deprecated
    private void cleanConfig() {
        this.removeInvalidTags(TAG_TABS_CONFIG);
        List<String> tags = this.configManager.getConfigurationKeys("banktags.item_");
        tags.forEach(s -> {
            String[] split = s.split("\\.", 2);
            this.removeInvalidTags(split[1]);
        });
        List<String> icons = this.configManager.getConfigurationKeys("banktags.icon_");
        icons.forEach(s -> {
            String replaced;
            String[] split = s.split("\\.", 2);
            if (!split[1].equals(replaced = split[1].replaceAll("[<>/]", ""))) {
                String value = this.configManager.getConfiguration(CONFIG_GROUP, split[1]);
                this.configManager.unsetConfiguration(CONFIG_GROUP, split[1]);
                if (replaced.length() > TAG_ICON_PREFIX.length()) {
                    this.configManager.setConfiguration(CONFIG_GROUP, replaced, value);
                }
            }
        });
    }

    @Deprecated
    private void removeInvalidTags(String key) {
        String value = this.configManager.getConfiguration(CONFIG_GROUP, key);
        if (value == null) {
            return;
        }
        String replaced = value.replaceAll("[<>:/]", "");
        if (!value.equals(replaced)) {
            if ((replaced = Text.toCSV(Text.fromCSV(replaced))).isEmpty()) {
                this.configManager.unsetConfiguration(CONFIG_GROUP, key);
            } else {
                this.configManager.setConfiguration(CONFIG_GROUP, key, replaced);
            }
        }
    }

    @Subscribe
    public void onGrandExchangeSearched(GrandExchangeSearched event) {
        String input = this.client.getVarcStrValue(359);
        if (!input.startsWith(TAG_SEARCH)) {
            return;
        }
        event.consume();
        String tag = input.substring(TAG_SEARCH.length()).trim();
        Set ids = this.tagManager.getItemsForTag(tag).stream().mapToInt(Math::abs).mapToObj(ItemVariationMapping::getVariations).flatMap(Collection::stream).distinct().filter(i -> this.itemManager.getItemComposition((int)i).isTradeable()).limit(250L).collect(Collectors.toCollection(TreeSet::new));
        this.client.setGeSearchResultIndex(0);
        this.client.setGeSearchResultCount(ids.size());
        this.client.setGeSearchResultIds(Shorts.toArray((Collection)ids));
    }

    @Subscribe
    public void onScriptCallbackEvent(ScriptCallbackEvent event) {
        String eventName = event.getEventName();
        int[] intStack = this.client.getIntStack();
        Object[] objectStack = this.client.getObjectStack();
        int intStackSize = this.client.getIntStackSize();
        int objectStackSize = this.client.getObjectStackSize();
        switch (eventName) {
            case "setSearchBankInputText": {
                objectStack[objectStackSize - 1] = SEARCH_BANK_INPUT_TEXT;
                break;
            }
            case "setSearchBankInputTextFound": {
                int matches = intStack[intStackSize - 1];
                objectStack[objectStackSize - 1] = String.format(SEARCH_BANK_INPUT_TEXT_FOUND, matches);
                break;
            }
            case "bankSearchFilter": {
                boolean bankOpen;
                int itemId = intStack[intStackSize - 1];
                String searchfilter = (String)objectStack[objectStackSize - 1];
                BankTag tag = this.activeBankTag;
                boolean tagSearch = true;
                boolean bl = bankOpen = this.client.getItemContainer(95) != null;
                if (tag == null || !bankOpen) {
                    if (searchfilter.isEmpty()) {
                        return;
                    }
                    tagSearch = searchfilter.startsWith(TAG_SEARCH);
                    if (tagSearch) {
                        searchfilter = searchfilter.substring(TAG_SEARCH.length()).trim();
                    }
                    tag = this.buildSearchFilterBankTag(searchfilter);
                }
                if (itemId == -1 && this.activeLayout != null) {
                    return;
                }
                if (itemId > -1 && tag.contains(itemId)) {
                    intStack[intStackSize - 2] = 1;
                    break;
                }
                if (!tagSearch) break;
                intStack[intStackSize - 2] = 0;
                break;
            }
            case "getSearchingTagTab": {
                intStack[intStackSize - 1] = this.activeBankTag != null ? 1 : 0;
                break;
            }
            case "bankBuildTab": {
                if (this.activeBankTag == null || !this.tabInterface.isTagTabActive() && !this.config.removeSeparators() && this.activeLayout == null) break;
                int[] stack = this.client.getIntStack();
                int sz = this.client.getIntStackSize();
                stack[sz - 1] = 1;
            }
        }
    }

    @Subscribe
    public void onMenuEntryAdded(MenuEntryAdded event) {
        if (event.getActionParam1() == 786445 && (event.getOption().equals("Examine") || event.getOption().equals("Withdraw-All-but-1") && !this.client.getItemContainer(95).contains(event.getItemId()))) {
            Widget container = this.client.getWidget(786445);
            Widget item = container.getChild(event.getActionParam0());
            int itemId = item.getItemId();
            Collection<String> tags = this.tagManager.getTags(itemId, false);
            tags.addAll(this.tagManager.getTags(itemId, true));
            int tagCount = (int)tags.stream().filter(tag -> this.developerMode || !this.tagManager.isHidden((String)tag)).count();
            Object text = EDIT_TAGS_MENU_OPTION;
            if (tagCount > 0) {
                text = (String)text + " (" + tagCount + ")";
            }
            int index = event.getOption().equals("Examine") ? -1 : -2;
            this.client.createMenuEntry(index).setParam0(event.getActionParam0()).setParam1(event.getActionParam1()).setTarget(event.getTarget()).setOption((String)text).setType(MenuAction.RUNELITE).setIdentifier(event.getIdentifier()).setItemId(event.getItemId()).onClick(this::editTags);
        }
    }

    private void editTags(MenuEntry entry) {
        int itemId = entry.getItemId();
        ItemComposition itemComposition = this.itemManager.getItemComposition(itemId);
        String name = itemComposition.getName();
        List<String> tags = this.tagManager.getTags(itemId, false).stream().filter(tag -> this.developerMode || !this.tagManager.isHidden((String)tag)).collect(Collectors.toList());
        this.tagManager.getTags(itemId, true).stream().filter(tag -> this.developerMode || !this.tagManager.isHidden((String)tag)).map(tag -> tag + VAR_TAG_SUFFIX).forEach(tags::add);
        String initialValue = Text.toCSV(tags);
        this.chatboxPanelManager.openTextInput(name + " tags:<br>(append * for variation tag)").addCharValidator(TabInterface.FILTERED_CHARS).value(initialValue).onDone(newValue -> this.clientThread.invoke(() -> {
            ArrayList<String> newTags = new ArrayList<String>(Text.fromCSV(newValue.toLowerCase()));
            Collection newVarTags = new ArrayList<String>(newTags).stream().filter(s -> s.endsWith(VAR_TAG_SUFFIX)).map(s -> {
                newTags.remove(s);
                return s.substring(0, s.length() - VAR_TAG_SUFFIX.length());
            }).collect(Collectors.toList());
            this.tagManager.setTagString(itemId, Text.toCSV(newTags), false);
            this.tagManager.setTagString(itemId, Text.toCSV(newVarTags), true);
            this.tabInterface.reloadActiveTab();
        })).build();
    }

    @Subscribe
    public void onConfigChanged(ConfigChanged configChanged) {
        if (configChanged.getGroup().equals(CONFIG_GROUP) && configChanged.getKey().equals("useTabs")) {
            this.clientThread.invokeLater(this::reinitBank);
        }
    }

    public void openTag(String tag, Layout layout) {
        this.openTag(tag, layout, 1);
    }

    public void openTag(String tag, Layout layout, int options) {
        if (tag == null) {
            this.activeTag = null;
            this.activeBankTag = null;
            this.activeLayout = null;
            this.options = 0;
            return;
        }
        this.activeTag = tag;
        this.activeBankTag = this.buildSearchFilterBankTag(tag);
        this.activeLayout = layout;
        this.options = options;
        this.tabInterface.openTag(tag, layout, options, true);
    }

    private BankTag buildSearchFilterBankTag(String tag) {
        BankTag custom = this.tagManager.findTag(tag);
        return itemId -> this.tagManager.findTag(itemId, tag) || custom != null && custom.contains(itemId);
    }

    public void openBankTag(String name) {
        this.openBankTag(name, 1);
    }

    @Override
    public void openBankTag(String name, int options) {
        Layout layout = (options & 4) != 0 ? null : this.layoutManager.loadLayout(name);
        this.openTag(name, layout, options);
    }

    @Override
    public void closeBankTag() {
        this.tabInterface.closeTag(false);
        this.activeTag = null;
        this.activeBankTag = null;
        this.activeLayout = null;
        this.options = 0;
        this.bankSearch.layoutBank();
    }

    @Override
    public String getActiveTag() {
        return this.activeTag;
    }

    @Override
    public BankTag getActiveBankTag() {
        return this.activeBankTag;
    }

    @Override
    public Layout getActiveLayout() {
        return this.activeLayout;
    }

    public int getOptions() {
        return this.options;
    }
}

