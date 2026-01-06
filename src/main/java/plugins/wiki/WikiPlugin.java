/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.inject.Provides
 *  javax.inject.Inject
 *  javax.inject.Provider
 *  net.runelite.api.Client
 *  net.runelite.api.Menu
 *  net.runelite.api.MenuAction
 *  net.runelite.api.MenuEntry
 *  net.runelite.api.NPC
 *  net.runelite.api.NPCComposition
 *  net.runelite.api.ObjectComposition
 *  net.runelite.api.coords.WorldPoint
 *  net.runelite.api.events.MenuEntryAdded
 *  net.runelite.api.events.MenuOptionClicked
 *  net.runelite.api.events.ScriptPostFired
 *  net.runelite.api.events.WidgetLoaded
 *  net.runelite.api.widgets.Widget
 *  net.runelite.api.widgets.WidgetUtil
 *  okhttp3.HttpUrl
 *  okhttp3.HttpUrl$Builder
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package net.runelite.client.plugins.wiki;

import com.google.inject.Provides;
import java.util.Arrays;
import java.util.stream.Stream;
import javax.inject.Inject;
import javax.inject.Provider;
import net.runelite.api.Client;
import net.runelite.api.Menu;
import net.runelite.api.MenuAction;
import net.runelite.api.MenuEntry;
import net.runelite.api.NPC;
import net.runelite.api.NPCComposition;
import net.runelite.api.ObjectComposition;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.MenuEntryAdded;
import net.runelite.api.events.MenuOptionClicked;
import net.runelite.api.events.ScriptPostFired;
import net.runelite.api.events.WidgetLoaded;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetUtil;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.wiki.WikiConfig;
import net.runelite.client.plugins.wiki.WikiDpsManager;
import net.runelite.client.plugins.wiki.WikiSearchChatboxTextInput;
import net.runelite.client.util.LinkBrowser;
import net.runelite.client.util.Text;
import okhttp3.HttpUrl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@PluginDescriptor(name="Wiki", description="Adds a Wiki button that takes you to the OSRS Wiki")
public class WikiPlugin
extends Plugin {
    private static final Logger log = LoggerFactory.getLogger(WikiPlugin.class);
    static final HttpUrl WIKI_BASE = HttpUrl.get((String)"https://oldschool.runescape.wiki");
    static final HttpUrl WIKI_API = WIKI_BASE.newBuilder().addPathSegments("api.php").build();
    static final String UTM_SOURCE_KEY = "utm_source";
    static final String UTM_SOURCE_VALUE = "runelite";
    private static final String MENUOP_WIKI = "Wiki";
    @Inject
    private WikiConfig config;
    @Inject
    private ClientThread clientThread;
    @Inject
    private Client client;
    @Inject
    private ItemManager itemManager;
    @Inject
    private Provider<WikiSearchChatboxTextInput> wikiSearchChatboxTextInputProvider;
    @Inject
    private WikiDpsManager wikiDpsManager;
    private Widget icon;
    private boolean wikiSelected = false;
    static final String CONFIG_GROUP_KEY = "wiki";

    @Provides
    WikiConfig getConfig(ConfigManager configManager) {
        return configManager.getConfig(WikiConfig.class);
    }

    @Override
    public void startUp() {
        this.clientThread.invokeLater(this::addWidgets);
        this.wikiDpsManager.startUp();
    }

    @Override
    public void shutDown() {
        this.clientThread.invokeLater(this::removeWidgets);
        this.wikiDpsManager.shutDown();
    }

    private void removeWidgets() {
        Widget wikiBannerParent = this.client.getWidget(10485809);
        if (wikiBannerParent == null) {
            return;
        }
        Widget[] children = wikiBannerParent.getChildren();
        if (children == null || children.length < 1) {
            return;
        }
        children[0] = null;
        Widget vanilla = this.client.getWidget(10485810);
        if (vanilla != null && this.client.getVarbitValue(10113) == 0) {
            vanilla.setHidden(false);
        }
        this.onDeselect();
        this.client.setWidgetSelected(false);
    }

    @Subscribe
    private void onWidgetLoaded(WidgetLoaded l) {
        if (l.getGroupId() == 160) {
            this.addWidgets();
        }
    }

    private void addWidgets() {
        Widget wikiBannerParent = this.client.getWidget(10485809);
        if (wikiBannerParent == null) {
            return;
        }
        if (this.client.getVarbitValue(10113) == 1) {
            wikiBannerParent.setOriginalX(this.client.isResized() ? 0 : 8);
            wikiBannerParent.setOriginalY(135);
            wikiBannerParent.setXPositionMode(2);
            wikiBannerParent.setYPositionMode(0);
            wikiBannerParent.revalidate();
            return;
        }
        Widget vanilla = this.client.getWidget(10485810);
        if (vanilla != null) {
            vanilla.setHidden(true);
        }
        if (!this.config.showWikiMinimapButton()) {
            return;
        }
        this.icon = wikiBannerParent.createChild(0, 5);
        this.icon.setSpriteId(2420);
        this.icon.setOriginalX(0);
        this.icon.setOriginalY(0);
        this.icon.setXPositionMode(1);
        this.icon.setYPositionMode(1);
        this.icon.setOriginalWidth(40);
        this.icon.setOriginalHeight(14);
        this.icon.setTargetVerb("Lookup");
        this.icon.setName(MENUOP_WIKI);
        this.icon.setClickMask(79872);
        this.icon.setNoClickThrough(true);
        this.icon.setOnTargetEnterListener(new Object[]{ev -> {
            this.wikiSelected = true;
            this.icon.setSpriteId(2421);
            this.client.setAllWidgetsAreOpTargetable(true);
        }});
        int searchIndex = this.config.leftClickSearch() ? 4 : 5;
        this.icon.setAction(searchIndex, "Search");
        this.icon.setAction(6, "DPS");
        this.icon.setOnOpListener(new Object[]{ev -> {
            int op = ev.getOp() - 1;
            if (op == searchIndex) {
                this.openSearchInput();
            } else if (op == 6) {
                this.wikiDpsManager.launch();
            }
        }});
        this.icon.setOnTargetLeaveListener(new Object[]{ev -> this.onDeselect()});
        this.icon.revalidate();
    }

    @Subscribe
    public void onScriptPostFired(ScriptPostFired scriptPostFired) {
        Widget wikiIcon;
        if (scriptPostFired.getScriptId() == 3306 && (wikiIcon = this.client.getWidget(10485810)) != null) {
            if (this.client.getVarbitValue(10113) == 0) {
                this.addWidgets();
            } else {
                this.removeWidgets();
            }
        }
    }

    @Subscribe
    public void onConfigChanged(ConfigChanged event) {
        if (event.getGroup().equals(CONFIG_GROUP_KEY)) {
            this.clientThread.invokeLater(() -> {
                this.removeWidgets();
                this.addWidgets();
            });
        }
    }

    private void onDeselect() {
        this.client.setAllWidgetsAreOpTargetable(false);
        this.wikiSelected = false;
        if (this.icon != null) {
            this.icon.setSpriteId(2420);
        }
    }

    @Subscribe
    private void onMenuOptionClicked(MenuOptionClicked ev) {
        block14: {
            if (this.wikiSelected) {
                WorldPoint location;
                String name;
                int id;
                String type;
                this.onDeselect();
                this.client.setWidgetSelected(false);
                ev.consume();
                switch (ev.getMenuAction()) {
                    case RUNELITE: {
                        break block14;
                    }
                    case CANCEL: {
                        return;
                    }
                    case WIDGET_TARGET_ON_GROUND_ITEM: {
                        type = "item";
                        id = this.itemManager.canonicalize(ev.getId());
                        name = this.itemManager.getItemComposition(id).getMembersName();
                        location = null;
                        break;
                    }
                    case WIDGET_TARGET_ON_NPC: {
                        type = "npc";
                        NPC npc = ev.getMenuEntry().getNpc();
                        if (npc == null) {
                            return;
                        }
                        NPCComposition nc = npc.getTransformedComposition();
                        if (nc == null) {
                            return;
                        }
                        id = nc.getId();
                        name = nc.getName();
                        location = npc.getWorldLocation();
                        break;
                    }
                    case WIDGET_TARGET_ON_GAME_OBJECT: {
                        type = "object";
                        ObjectComposition lc = this.client.getObjectDefinition(ev.getId());
                        if (lc.getImpostorIds() != null) {
                            lc = lc.getImpostor();
                        }
                        id = lc.getId();
                        name = lc.getName();
                        location = WorldPoint.fromScene((Client)this.client, (int)ev.getParam0(), (int)ev.getParam1(), (int)this.client.getPlane());
                        break;
                    }
                    case WIDGET_TARGET_ON_WIDGET: {
                        Widget w = this.getWidget(ev.getParam1(), ev.getParam0());
                        if (w.getType() == 5 && w.getItemId() != -1) {
                            type = "item";
                            id = this.itemManager.canonicalize(w.getItemId());
                            name = this.itemManager.getItemComposition(id).getMembersName();
                            location = null;
                            break;
                        }
                    }
                    default: {
                        log.info("Unknown menu option: {} {} {}", new Object[]{ev, ev.getMenuAction(), ev.getMenuAction() == MenuAction.CANCEL});
                        return;
                    }
                }
                name = Text.removeTags(name);
                HttpUrl.Builder urlBuilder = WIKI_BASE.newBuilder();
                urlBuilder.addPathSegments("w/Special:Lookup").addQueryParameter("type", type).addQueryParameter("id", "" + id).addQueryParameter("name", name).addQueryParameter(UTM_SOURCE_KEY, UTM_SOURCE_VALUE);
                if (location != null) {
                    urlBuilder.addQueryParameter("x", "" + location.getX()).addQueryParameter("y", "" + location.getY()).addQueryParameter("plane", "" + location.getPlane());
                }
                HttpUrl url = urlBuilder.build();
                LinkBrowser.browse(url.toString());
                return;
            }
        }
    }

    private void openSearchInput() {
        ((WikiSearchChatboxTextInput)this.wikiSearchChatboxTextInputProvider.get()).build();
    }

    private Widget getWidget(int wid, int index) {
        Widget w = this.client.getWidget(wid);
        if (index != -1) {
            w = w.getChild(index);
        }
        return w;
    }

    @Subscribe
    public void onMenuEntryAdded(MenuEntryAdded event) {
        Widget w;
        int widgetIndex = event.getActionParam0();
        int widgetID = event.getActionParam1();
        if (this.wikiSelected && event.getType() == MenuAction.WIDGET_TARGET_ON_WIDGET.getId()) {
            Menu menu = this.client.getMenu();
            MenuEntry[] menuEntries = menu.getMenuEntries();
            Widget w2 = this.getWidget(widgetID, widgetIndex);
            if (w2.getType() == 5 && w2.getItemId() != -1 && w2.getItemId() != 6512) {
                for (int ourEntry = menuEntries.length - 1; ourEntry >= 0; --ourEntry) {
                    MenuEntry entry = menuEntries[ourEntry];
                    if (entry.getType() != MenuAction.WIDGET_TARGET_ON_WIDGET) continue;
                    int id = this.itemManager.canonicalize(w2.getItemId());
                    String name = this.itemManager.getItemComposition(id).getMembersName();
                    if ("null".equals(name)) {
                        menu.removeMenuEntry(entry);
                    } else {
                        entry.setTarget("<col=ff9040>" + name);
                    }
                    break;
                }
            } else {
                MenuEntry[] oldEntries = menuEntries;
                menuEntries = Arrays.copyOf(menuEntries, menuEntries.length - 1);
                for (int ourEntry = oldEntries.length - 1; ourEntry >= 2 && oldEntries[oldEntries.length - 1].getType() != MenuAction.WIDGET_TARGET_ON_WIDGET; --ourEntry) {
                    menuEntries[ourEntry - 1] = oldEntries[ourEntry];
                }
                this.client.setMenuEntries(menuEntries);
            }
        }
        if (event.getType() == MenuAction.CC_OP.getId() && WidgetUtil.componentToInterface((int)widgetID) == 320) {
            w = this.getWidget(widgetID, widgetIndex);
            if (w.getActions() == null || w.getParentId() != 0x1400000) {
                return;
            }
            String action = Stream.of(w.getActions()).filter(s -> s != null && !s.isEmpty()).findFirst().orElse(null);
            if (action == null) {
                return;
            }
            this.client.createMenuEntry(-1).setTarget(action.replace("View ", "").replace(" guide", "")).setOption(MENUOP_WIKI).setType(MenuAction.RUNELITE).onClick(ev -> LinkBrowser.browse(WIKI_BASE.newBuilder().addPathSegment("w").addPathSegment(Text.removeTags(ev.getTarget())).addQueryParameter(UTM_SOURCE_KEY, UTM_SOURCE_VALUE).build().toString()));
        }
        if (event.getType() == MenuAction.CC_OP.getId() && WidgetUtil.componentToInterface((int)widgetID) == 715) {
            w = this.getWidget(widgetID, widgetIndex);
            if (w.getActions() == null || w.getParentId() != 46858249) {
                return;
            }
            this.client.getMenu().createMenuEntry(-1).setTarget(w.getName()).setOption(MENUOP_WIKI).setType(MenuAction.RUNELITE).onClick(ev -> LinkBrowser.browse(WIKI_BASE.newBuilder().addPathSegment("w").addPathSegment(Text.removeTags(ev.getTarget())).addQueryParameter(UTM_SOURCE_KEY, UTM_SOURCE_VALUE).build().toString()));
        }
    }
}

