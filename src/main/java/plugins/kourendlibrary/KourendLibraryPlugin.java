/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.inject.Provides
 *  javax.inject.Inject
 *  net.runelite.api.ChatMessageType
 *  net.runelite.api.Client
 *  net.runelite.api.GameState
 *  net.runelite.api.Item
 *  net.runelite.api.ItemContainer
 *  net.runelite.api.MenuAction
 *  net.runelite.api.NPC
 *  net.runelite.api.Player
 *  net.runelite.api.Quest
 *  net.runelite.api.QuestState
 *  net.runelite.api.coords.WorldPoint
 *  net.runelite.api.events.AnimationChanged
 *  net.runelite.api.events.ChatMessage
 *  net.runelite.api.events.GameStateChanged
 *  net.runelite.api.events.GameTick
 *  net.runelite.api.events.ItemContainerChanged
 *  net.runelite.api.events.MenuOptionClicked
 *  net.runelite.api.events.NpcDespawned
 *  net.runelite.api.events.NpcSpawned
 *  net.runelite.api.widgets.Widget
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package net.runelite.client.plugins.kourendlibrary;

import com.google.inject.Provides;
import java.awt.image.BufferedImage;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.inject.Inject;
import javax.swing.SwingUtilities;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.Item;
import net.runelite.api.ItemContainer;
import net.runelite.api.MenuAction;
import net.runelite.api.NPC;
import net.runelite.api.Player;
import net.runelite.api.Quest;
import net.runelite.api.QuestState;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.AnimationChanged;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.ItemContainerChanged;
import net.runelite.api.events.MenuOptionClicked;
import net.runelite.api.events.NpcDespawned;
import net.runelite.api.events.NpcSpawned;
import net.runelite.api.widgets.Widget;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.kourendlibrary.Book;
import net.runelite.client.plugins.kourendlibrary.Bookcase;
import net.runelite.client.plugins.kourendlibrary.KourendLibraryConfig;
import net.runelite.client.plugins.kourendlibrary.KourendLibraryOverlay;
import net.runelite.client.plugins.kourendlibrary.KourendLibraryPanel;
import net.runelite.client.plugins.kourendlibrary.KourendLibraryTutorialOverlay;
import net.runelite.client.plugins.kourendlibrary.Library;
import net.runelite.client.plugins.kourendlibrary.SolvedState;
import net.runelite.client.ui.ClientToolbar;
import net.runelite.client.ui.NavigationButton;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.util.ImageUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@PluginDescriptor(name="Kourend Library", description="Show where the books are found in the Kourend Library", tags={"arceuus", "magic", "runecrafting", "overlay", "panel"})
public class KourendLibraryPlugin
extends Plugin {
    private static final Logger log = LoggerFactory.getLogger(KourendLibraryPlugin.class);
    private static final Pattern BOOK_EXTRACTOR = Pattern.compile("'<col=0000ff>(.*)</col>'");
    private static final Pattern TAG_MATCHER = Pattern.compile("(<[^>]*>)");
    static final int REGION = 6459;
    static final boolean debug = false;
    @Inject
    private ClientToolbar clientToolbar;
    @Inject
    private Client client;
    @Inject
    private Library library;
    @Inject
    private OverlayManager overlayManager;
    @Inject
    private KourendLibraryOverlay overlay;
    @Inject
    private KourendLibraryTutorialOverlay tutorialOverlay;
    @Inject
    private KourendLibraryConfig config;
    @Inject
    private ItemManager itemManager;
    @Inject
    private ClientThread clientThread;
    private KourendLibraryPanel panel;
    private NavigationButton navButton;
    private boolean buttonAttached = false;
    private WorldPoint lastBookcaseClick = null;
    private WorldPoint lastBookcaseAnimatedOn = null;
    private EnumSet<Book> playerBooks = EnumSet.noneOf(Book.class);
    private QuestState depthsOfDespairState = QuestState.FINISHED;
    private final Set<NPC> npcsToMark = new HashSet<NPC>();

    @Provides
    KourendLibraryConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(KourendLibraryConfig.class);
    }

    @Override
    protected void startUp() throws Exception {
        Book.fillImages(this.itemManager);
        this.panel = (KourendLibraryPanel)this.injector.getInstance(KourendLibraryPanel.class);
        this.panel.init();
        BufferedImage icon = ImageUtil.loadImageResource(this.getClass(), "panel_icon.png");
        this.navButton = NavigationButton.builder().tooltip("Kourend Library").priority(6).icon(icon).panel(this.panel).build();
        this.overlayManager.add(this.overlay);
        this.overlayManager.add(this.tutorialOverlay);
        this.clientThread.invoke(() -> {
            if (this.client.getGameState() == GameState.LOGGED_IN) {
                this.updatePlayerBooks();
            }
        });
        if (!this.config.hideButton()) {
            this.clientToolbar.addNavigation(this.navButton);
        }
    }

    @Override
    protected void shutDown() {
        this.overlayManager.remove(this.overlay);
        this.overlayManager.remove(this.tutorialOverlay);
        this.clientToolbar.removeNavigation(this.navButton);
        this.buttonAttached = false;
        this.lastBookcaseClick = null;
        this.lastBookcaseAnimatedOn = null;
        this.playerBooks = EnumSet.noneOf(Book.class);
        this.npcsToMark.clear();
    }

    @Subscribe
    public void onConfigChanged(ConfigChanged ev) {
        if (!"kourendLibrary".equals(ev.getGroup())) {
            return;
        }
        if (ev.getKey().equals("hideVarlamoreEnvoy")) {
            SwingUtilities.invokeLater(this.panel::reload);
        } else if (ev.getKey().equals("hideButton")) {
            SwingUtilities.invokeLater(() -> {
                if (!this.config.hideButton()) {
                    this.clientToolbar.addNavigation(this.navButton);
                } else {
                    boolean inRegion;
                    Player lp = this.client.getLocalPlayer();
                    boolean bl = inRegion = lp != null && lp.getWorldLocation().getRegionID() == 6459;
                    if (inRegion) {
                        this.clientToolbar.addNavigation(this.navButton);
                    } else {
                        this.clientToolbar.removeNavigation(this.navButton);
                    }
                }
            });
        } else if (ev.getKey().equals("showTargetHintArrow")) {
            if (this.client.getLocalPlayer() == null || this.client.getLocalPlayer().getWorldLocation().getRegionID() != 6459) {
                return;
            }
            this.updateBookcaseHintArrow();
        }
    }

    @Subscribe
    public void onMenuOptionClicked(MenuOptionClicked menuOpt) {
        if (MenuAction.GAME_OBJECT_FIRST_OPTION == menuOpt.getMenuAction() && menuOpt.getMenuTarget().contains("Bookshelf")) {
            this.lastBookcaseClick = WorldPoint.fromScene((Client)this.client, (int)menuOpt.getParam0(), (int)menuOpt.getParam1(), (int)this.client.getPlane());
        }
    }

    @Subscribe
    public void onAnimationChanged(AnimationChanged anim) {
        if (anim.getActor() == this.client.getLocalPlayer() && anim.getActor().getAnimation() == 832) {
            this.lastBookcaseAnimatedOn = this.lastBookcaseClick;
        }
    }

    @Subscribe
    public void onChatMessage(ChatMessage event) {
        if (event.getType() != ChatMessageType.GAMEMESSAGE) {
            return;
        }
        if (this.lastBookcaseAnimatedOn != null && event.getMessage().equals("You don't find anything useful here.")) {
            this.library.mark(this.lastBookcaseAnimatedOn, null);
            this.updateBooksPanel();
            this.lastBookcaseAnimatedOn = null;
        }
        if (event.getMessage().endsWith("You hear the shifting of books due to a mysterious force...or are you just hearing things?")) {
            this.library.reset();
        }
    }

    @Subscribe
    public void onGameStateChanged(GameStateChanged event) {
        if (event.getGameState() == GameState.LOGIN_SCREEN || event.getGameState() == GameState.HOPPING) {
            this.npcsToMark.clear();
        }
    }

    @Subscribe
    public void onGameTick(GameTick tick) {
        Widget npcHead;
        Book book;
        Widget find;
        boolean inRegion;
        boolean bl = inRegion = this.client.getLocalPlayer().getWorldLocation().getRegionID() == 6459;
        if (this.config.hideButton() && inRegion != this.buttonAttached) {
            SwingUtilities.invokeLater(() -> {
                if (inRegion) {
                    this.panel.reload();
                    this.clientToolbar.addNavigation(this.navButton);
                } else {
                    this.clientToolbar.removeNavigation(this.navButton);
                }
            });
            this.buttonAttached = inRegion;
        }
        if (!inRegion) {
            return;
        }
        this.depthsOfDespairState = Quest.THE_DEPTHS_OF_DESPAIR.getState(this.client);
        if (this.lastBookcaseAnimatedOn != null && (find = this.client.getWidget(0xC10001)) != null && (book = Book.byId(find.getItemId())) != null) {
            this.library.mark(this.lastBookcaseAnimatedOn, book);
            this.updateBooksPanel();
            this.lastBookcaseAnimatedOn = null;
        }
        if ((npcHead = this.client.getWidget(15138818)) != null && KourendLibraryPlugin.isLibraryCustomer(npcHead.getModelId())) {
            Widget textw = this.client.getWidget(15138822);
            String text = textw.getText();
            Matcher m = BOOK_EXTRACTOR.matcher(text);
            if (m.find()) {
                String bookName = TAG_MATCHER.matcher(m.group(1).replace("<br>", " ")).replaceAll("");
                Book book2 = Book.byName(bookName);
                if (book2 == null) {
                    log.warn("Book '{}' is not recognised", (Object)bookName);
                    return;
                }
                this.library.setCustomer(npcHead.getModelId(), book2);
                this.updateBooksPanel();
            } else if (text.contains("You can have this other book") || text.contains("please accept a token of my thanks.") || text.contains("Thanks, I'll get on with reading it.")) {
                this.library.setCustomer(-1, null);
                this.updateBooksPanel();
            }
        }
    }

    @Subscribe
    public void onItemContainerChanged(ItemContainerChanged itemContainerChangedEvent) {
        this.updatePlayerBooks();
    }

    @Subscribe
    public void onNpcSpawned(NpcSpawned event) {
        if (KourendLibraryPlugin.isLibraryCustomer(event.getNpc().getId())) {
            this.npcsToMark.add(event.getNpc());
        }
    }

    @Subscribe
    public void onNpcDespawned(NpcDespawned event) {
        this.npcsToMark.remove(event.getNpc());
    }

    boolean doesPlayerContainBook(Book book) {
        return this.playerBooks.contains((Object)book);
    }

    private void updatePlayerBooks() {
        ItemContainer itemContainer = this.client.getItemContainer(93);
        EnumSet<Book> books = EnumSet.noneOf(Book.class);
        if (itemContainer != null) {
            for (Item item : itemContainer.getItems()) {
                Book book = Book.byId(item.getId());
                if (book == null) continue;
                books.add(book);
            }
        }
        this.playerBooks = books;
    }

    private void updateBooksPanel() {
        this.panel.update();
        this.updateBookcaseHintArrow();
    }

    private void updateBookcaseHintArrow() {
        Book customerBook = this.library.getCustomerBook();
        SolvedState state = this.library.getState();
        if (customerBook == null || this.doesPlayerContainBook(customerBook) || !this.config.showTargetHintArrow()) {
            this.client.clearHintArrow();
        } else if (state == SolvedState.COMPLETE && this.client.getHintArrowPoint() == null) {
            for (Bookcase bookcase : this.library.getBookcases()) {
                Book book;
                Set<Book> books = bookcase.getPossibleBooks();
                if (books.isEmpty() || (book = books.iterator().next()) != customerBook) continue;
                WorldPoint correctLocation = bookcase.getLocation();
                this.client.setHintArrow(correctLocation);
                break;
            }
        }
    }

    boolean showVarlamoreEnvoy() {
        return this.config.alwaysShowVarlamoreEnvoy() || this.depthsOfDespairState == QuestState.IN_PROGRESS;
    }

    static boolean isLibraryCustomer(int npcId) {
        return npcId == 7047 || npcId == 7048 || npcId == 7049;
    }

    Set<NPC> getNpcsToMark() {
        return this.npcsToMark;
    }
}

