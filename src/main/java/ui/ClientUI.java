/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.formdev.flatlaf.ui.FlatNativeWindowBorder
 *  com.formdev.flatlaf.util.SystemInfo
 *  com.google.common.base.Strings
 *  com.google.common.collect.Iterables
 *  com.google.inject.Inject
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  javax.inject.Named
 *  javax.inject.Provider
 *  javax.inject.Singleton
 *  net.runelite.api.Client
 *  net.runelite.api.GameState
 *  net.runelite.api.Player
 *  net.runelite.api.events.GameStateChanged
 *  net.runelite.api.widgets.Widget
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package net.runelite.client.ui;

import com.formdev.flatlaf.ui.FlatNativeWindowBorder;
import com.formdev.flatlaf.util.SystemInfo;
import com.google.common.base.Strings;
import com.google.common.collect.Iterables;
import com.google.inject.Inject;
import java.awt.AWTException;
import java.awt.Canvas;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Insets;
import java.awt.KeyboardFocusManager;
import java.awt.LayoutManager2;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.SystemTray;
import java.awt.Taskbar;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.desktop.QuitStrategy;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.awt.image.BufferedImage;
import java.lang.reflect.Type;
import java.time.Duration;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.List;
import java.util.TreeSet;
import java.util.function.Function;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRootPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.ToolTipManager;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.event.HyperlinkEvent;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.Player;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.widgets.Widget;
import net.runelite.client.RuneLiteProperties;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.config.ExpandResizeType;
import net.runelite.client.config.RuneLiteConfig;
import net.runelite.client.config.WarningOnExit;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ClientShutdown;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.input.KeyListener;
import net.runelite.client.input.MouseAdapter;
import net.runelite.client.input.MouseManager;
import net.runelite.client.ui.ClientPanel;
import net.runelite.client.ui.ClientToolbarPanel;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.ContainableFrame;
import net.runelite.client.ui.NavigationButton;
import net.runelite.client.ui.laf.RuneLiteLAF;
import net.runelite.client.util.HotkeyListener;
import net.runelite.client.util.ImageUtil;
import net.runelite.client.util.LinkBrowser;
import net.runelite.client.util.OSType;
import net.runelite.client.util.OSXUtil;
import net.runelite.client.util.SwingUtil;
import net.runelite.client.util.WinUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class ClientUI {
    private static final Logger log = LoggerFactory.getLogger(ClientUI.class);
    private static final String CONFIG_GROUP = "runelite";
    private static final String CONFIG_CLIENT_BOUNDS = "clientBounds";
    private static final String CONFIG_CLIENT_MAXIMIZED = "clientMaximized";
    private static final String CONFIG_CLIENT_SIDEBAR_CLOSED = "clientSidebarClosed";
    public static final BufferedImage ICON_128 = ImageUtil.loadImageResource(ClientUI.class, "runelite_128.png");
    public static final BufferedImage ICON_16 = ImageUtil.loadImageResource(ClientUI.class, "runelite_16.png");
    private TrayIcon trayIcon;
    private final RuneLiteConfig config;
    private final MouseManager mouseManager;
    private final Component client;
    private final ConfigManager configManager;
    private final Provider<ClientThread> clientThreadProvider;
    private final EventBus eventBus;
    private final boolean safeMode;
    private final String title;
    private final Rectangle sidebarButtonPosition = new Rectangle();
    private BufferedImage sidebarOpenIcon;
    private BufferedImage sidebarCloseIcon;
    private JTabbedPane sidebar;
    private final TreeSet<NavigationButton> sidebarEntries = new TreeSet<NavigationButton>(NavigationButton.COMPARATOR);
    private final Deque<HistoryEntry> selectedTabHistory = new ArrayDeque<HistoryEntry>();
    private NavigationButton selectedTab;
    private ClientToolbarPanel toolbarPanel;
    private boolean withTitleBar;
    private ContainableFrame frame;
    private JPanel content;
    private ClientPanel clientPanel;
    private JButton sidebarNavBtn;
    private Dimension lastClientSize;
    private Cursor defaultCursor;
    private String lastNormalBounds;
    private final Timer normalBoundsTimer;
    @Inject(optional=true)
    @Named(value="minMemoryLimit")
    private int minMemoryLimit = 400;
    @Inject(optional=true)
    @Named(value="recommendedMemoryLimit")
    private int recommendedMemoryLimit = 512;
    private List<KeyListener> keyListeners;

    @Inject
    private ClientUI(RuneLiteConfig config, MouseManager mouseManager, Client client2, ConfigManager configManager, Provider<ClientThread> clientThreadProvider, EventBus eventBus, @Named(value="safeMode") boolean safeMode, @Named(value="runelite.title") String title) {
        this.config = config;
        this.mouseManager = mouseManager;
        this.client = (Component)client2;
        this.configManager = configManager;
        this.clientThreadProvider = clientThreadProvider;
        this.eventBus = eventBus;
        this.safeMode = safeMode;
        this.title = title + (safeMode ? " (safe mode)" : "");
        this.normalBoundsTimer = new Timer(250, _ev -> this.setLastNormalBounds());
        this.normalBoundsTimer.setRepeats(false);
    }

    @Subscribe
    private void onConfigChanged(ConfigChanged event) {
        if (!event.getGroup().equals(CONFIG_GROUP) || event.getKey().equals(CONFIG_CLIENT_MAXIMIZED) || event.getKey().equals(CONFIG_CLIENT_BOUNDS)) {
            return;
        }
        SwingUtilities.invokeLater(() -> this.updateFrameConfig(event.getKey().equals("lockWindowSize")));
    }

    void addNavigation(NavigationButton navBtn) {
        if (navBtn.getPanel() == null) {
            this.toolbarPanel.add(navBtn, true);
            return;
        }
        if (!this.sidebarEntries.add(navBtn)) {
            return;
        }
        int TAB_SIZE = 16;
        ImageIcon icon = new ImageIcon(ImageUtil.resizeImage(navBtn.getIcon(), 16, 16));
        this.sidebar.insertTab(null, icon, navBtn.getPanel().getWrappedPanel(), navBtn.getTooltip(), this.sidebarEntries.headSet(navBtn).size());
        if (this.sidebar.getTabCount() == 1) {
            this.sidebar.setSelectedIndex(-1);
        }
    }

    void removeNavigation(NavigationButton navBtn) {
        if (navBtn.getPanel() == null) {
            this.toolbarPanel.remove(navBtn);
        } else {
            boolean closingOpenTab = !this.selectedTabHistory.isEmpty() && this.selectedTabHistory.getLast().navBtn == navBtn;
            this.selectedTabHistory.removeIf(it -> it.navBtn == navBtn);
            this.sidebar.remove(navBtn.getPanel().getWrappedPanel());
            if (closingOpenTab) {
                HistoryEntry entry = this.selectedTabHistory.isEmpty() ? new HistoryEntry(true, null) : this.selectedTabHistory.removeLast();
                this.openPanel(entry.navBtn, entry.sidebarOpen);
            }
        }
        this.sidebarEntries.remove(navBtn);
    }

    @Subscribe
    private void onGameStateChanged(GameStateChanged event) {
        if (event.getGameState() != GameState.LOGGED_IN || !this.config.usernameInTitle()) {
            return;
        }
        Client client2 = (Client)this.client;
        ClientThread clientThread = (ClientThread)this.clientThreadProvider.get();
        clientThread.invokeLater(() -> {
            if (client2.getGameState() != GameState.LOGGED_IN) {
                return true;
            }
            Player player = client2.getLocalPlayer();
            if (player == null) {
                return false;
            }
            String name = player.getName();
            if (Strings.isNullOrEmpty((String)name)) {
                return false;
            }
            this.frame.setTitle(this.title + " - " + name);
            return true;
        });
    }

    public void init() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            ClientUI.setupDefaults();
            RuneLiteLAF.setup();
            this.frame = new ContainableFrame();
            OSXUtil.tryEnableFullscreen(this.frame);
            this.frame.setTitle(this.title);
            this.frame.setIconImages(Arrays.asList(ICON_128, ICON_16));
            this.frame.setLocationRelativeTo(this.frame.getOwner());
            this.frame.setResizable(true);
            this.frame.setDefaultCloseOperation(0);
            if (OSType.getOSType() == OSType.MacOS) {
                Desktop.getDesktop().setQuitStrategy(QuitStrategy.CLOSE_ALL_WINDOWS);
            }
            this.frame.addWindowListener(new WindowAdapter(){

                @Override
                public void windowClosing(WindowEvent event) {
                    int result = 0;
                    if (ClientUI.this.showWarningOnExit()) {
                        try {
                            result = JOptionPane.showConfirmDialog(ClientUI.this.frame, "Are you sure you want to exit?", "Exit", 2, 3);
                        }
                        catch (Exception e) {
                            log.warn("Unexpected exception occurred while check for confirm required", (Throwable)e);
                        }
                    }
                    if (result == 0) {
                        ClientUI.this.shutdownClient();
                    }
                }
            });
            this.frame.addComponentListener(new ComponentAdapter(){

                @Override
                public void componentResized(ComponentEvent e) {
                    ClientUI.this.windowBoundsChanged();
                }

                @Override
                public void componentMoved(ComponentEvent e) {
                    ClientUI.this.windowBoundsChanged();
                }
            });
            this.content = new JPanel();
            this.content.setLayout(new Layout());
            this.clientPanel = new ClientPanel(this.client);
            this.content.add(this.clientPanel);
            this.sidebar = new JTabbedPane(4);
            this.sidebar.setBackground(ColorScheme.DARKER_GRAY_COLOR);
            this.sidebar.setOpaque(true);
            this.sidebar.putClientProperty("FlatLaf.style", "tabInsets: 2,5,2,5; variableSize: true; deselectable: true; tabHeight: 26");
            this.sidebar.setSelectedIndex(-1);
            this.sidebar.addChangeListener(ev -> {
                NavigationButton oldSelectedTab = this.selectedTab;
                int index = this.sidebar.getSelectedIndex();
                NavigationButton newSelectedTab = index < 0 ? null : (NavigationButton)Iterables.get(this.sidebarEntries, (int)index);
                if (oldSelectedTab == newSelectedTab) {
                    return;
                }
                this.selectedTab = newSelectedTab;
                if (this.sidebar.isVisible()) {
                    this.pushHistory();
                    if (oldSelectedTab != null) {
                        SwingUtil.deactivate(oldSelectedTab.getPanel());
                    }
                    if (newSelectedTab != null) {
                        SwingUtil.activate(newSelectedTab.getPanel());
                    }
                    if (newSelectedTab == null) {
                        this.giveClientFocus();
                    }
                }
            });
            this.sidebar.addMouseListener(new java.awt.event.MouseAdapter(){

                @Override
                public void mouseClicked(MouseEvent e) {
                    if (e.getButton() == 3) {
                        int index = 0;
                        for (NavigationButton navBtn : ClientUI.this.sidebarEntries) {
                            Rectangle bounds;
                            if ((bounds = ClientUI.this.sidebar.getBoundsAt(index++)) == null || !bounds.contains(e.getX(), e.getY())) continue;
                            if (navBtn.getPopup() != null) {
                                JPopupMenu menu = new JPopupMenu();
                                navBtn.getPopup().forEach((name, cb) -> {
                                    JMenuItem menuItem = new JMenuItem((String)name);
                                    menuItem.addActionListener(ev -> cb.run());
                                    menu.add(menuItem);
                                });
                                menu.show(ClientUI.this.sidebar, e.getX(), e.getY());
                            }
                            return;
                        }
                    }
                }
            });
            this.content.add(this.sidebar);
            this.frame.setContentPane(this.content);
            this.keyListeners = List.of(new HotkeyListener(this.config::sidebarToggleKey){

                @Override
                public void hotkeyPressed() {
                    ClientUI.this.toggleSidebar();
                }
            }, new HotkeyListener(this.config::panelToggleKey){

                @Override
                public void hotkeyPressed() {
                    ClientUI.this.togglePluginPanel();
                }
            });
            KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(this::dispatchWindowKeyEvent);
            this.frame.addWindowFocusListener(new WindowFocusListener(){

                @Override
                public void windowGainedFocus(WindowEvent e) {
                }

                @Override
                public void windowLostFocus(WindowEvent e) {
                    for (KeyListener keyListener : ClientUI.this.keyListeners) {
                        keyListener.focusLost();
                    }
                }
            });
            MouseAdapter mouseListener = new MouseAdapter(){

                @Override
                public MouseEvent mousePressed(MouseEvent mouseEvent) {
                    if (SwingUtilities.isLeftMouseButton(mouseEvent) && ClientUI.this.sidebarButtonPosition.contains(mouseEvent.getPoint())) {
                        SwingUtilities.invokeLater(() -> ClientUI.this.toggleSidebar());
                        mouseEvent.consume();
                    }
                    return mouseEvent;
                }
            };
            this.mouseManager.registerMouseListener(mouseListener);
            this.withTitleBar = this.config.enableCustomChrome();
            this.toolbarPanel = new ClientToolbarPanel(!this.withTitleBar);
            this.sidebarOpenIcon = ImageUtil.loadImageResource(ClientUI.class, this.withTitleBar ? "open.png" : "open_rs.png");
            this.sidebarCloseIcon = ImageUtil.flipImage(this.sidebarOpenIcon, true, false);
            if (this.withTitleBar) {
                JMenuBar menuBar = new JMenuBar();
                menuBar.add(Box.createGlue());
                menuBar.add(this.toolbarPanel);
                this.frame.setJMenuBar(menuBar);
                JRootPane rp = this.frame.getRootPane();
                if (FlatNativeWindowBorder.isSupported()) {
                    rp.putClientProperty("JRootPane.useWindowDecorations", true);
                } else if (OSType.getOSType() == OSType.MacOS && SystemInfo.isMacFullWindowContentSupported) {
                    rp.putClientProperty("apple.awt.fullWindowContent", true);
                    rp.putClientProperty("apple.awt.transparentTitleBar", true);
                    if (Runtime.version().feature() >= 17) {
                        rp.putClientProperty("apple.awt.windowTitleVisible", false);
                        rp.putClientProperty("runelite.titleBar", true);
                        rp.putClientProperty("JRootPane.titleBarShowClose", false);
                        rp.putClientProperty("JRootPane.titleBarShowMaximize", false);
                        rp.putClientProperty("JRootPane.titleBarShowIconify", false);
                        rp.putClientProperty("JRootPane.titleBarShowIcon", false);
                    }
                    menuBar.setBorder(new EmptyBorder(3, 70, 3, 10));
                } else {
                    if (OSType.getOSType() == OSType.Linux) {
                        JDialog.setDefaultLookAndFeelDecorated(true);
                        JFrame.setDefaultLookAndFeelDecorated(true);
                    }
                    this.frame.setUndecorated(true);
                    rp.setWindowDecorationStyle(1);
                }
                this.frame.addWindowStateListener(_ev -> this.applyCustomChromeBorder());
                this.applyCustomChromeBorder();
                this.sidebarNavBtn = this.toolbarPanel.add(NavigationButton.builder().priority(100).icon(this.sidebarCloseIcon).tooltip("Close sidebar").onClick(this::toggleSidebar).build(), false);
            } else {
                this.sidebar.putClientProperty("JTabbedPane.trailingComponent", this.toolbarPanel.createSidebarPanel());
            }
            this.updateFrameConfig(false);
            if (this.configManager.getConfiguration(CONFIG_GROUP, CONFIG_CLIENT_SIDEBAR_CLOSED, (Type)((Object)Boolean.class)) == Boolean.TRUE) {
                this.toggleSidebar(false, true);
            }
        });
    }

    private void applyCustomChromeBorder() {
        this.content.setBorder((this.frame.getExtendedState() & 6) == 6 ? null : new MatteBorder(4, 4, 4, 4, ColorScheme.DARKER_GRAY_COLOR));
    }

    public void show() {
        this.logGraphicsEnvironment();
        SwingUtilities.invokeLater(() -> {
            this.frame.pack();
            if (this.config.enableTrayIcon()) {
                this.trayIcon = ClientUI.createTrayIcon(ICON_16, this.title, this.frame);
            }
            boolean appliedSize = false;
            if (this.config.rememberScreenBounds() && !this.safeMode) {
                appliedSize = this.restoreClientBoundsConfig();
                if (appliedSize) {
                    Insets insets = this.frame.getInsets();
                    Rectangle clientBounds = this.frame.getBounds();
                    clientBounds = new Rectangle(clientBounds.x + insets.left, clientBounds.y + insets.top, clientBounds.width - (insets.left + insets.right), clientBounds.height - (insets.top + insets.bottom));
                    GraphicsConfiguration gc = this.findDisplayFromBounds(clientBounds);
                    if (gc == null) {
                        log.info("Reset client position. Client bounds: {}x{}x{}x{}", new Object[]{clientBounds.x, clientBounds.y, clientBounds.width, clientBounds.height});
                        this.frame.setLocationRelativeTo(this.frame.getOwner());
                    }
                }
                if (this.configManager.getConfiguration(CONFIG_GROUP, CONFIG_CLIENT_MAXIMIZED) != null) {
                    this.frame.setExtendedState(6);
                    this.applyCustomChromeBorder();
                }
            }
            if (!appliedSize) {
                this.applyGameSize(true);
                this.frame.setLocationRelativeTo(this.frame.getOwner());
            }
            this.frame.setVisible(true);
            this.frame.setResizable(!this.config.lockWindowSize());
            this.frame.toFront();
            this.requestFocus();
            log.debug("Showing frame {}", (Object)this.frame);
            this.frame.revalidateMinimumSize();
            this.frame.updateContainsInScreen();
        });
        int maxMemory = (int)(Runtime.getRuntime().maxMemory() / 1024L / 1024L);
        if (maxMemory < this.minMemoryLimit) {
            SwingUtilities.invokeLater(() -> {
                JEditorPane ep = new JEditorPane("text/html", "Your Java memory limit is " + maxMemory + "mb, which is lower than the recommended " + this.recommendedMemoryLimit + "mb.<br>This can cause instability, and it is recommended you remove or increase this limit.<br>Join <a href=\"" + RuneLiteProperties.getDiscordInvite() + "\">Discord</a> for assistance.");
                ep.addHyperlinkListener(e -> {
                    if (e.getEventType().equals(HyperlinkEvent.EventType.ACTIVATED)) {
                        LinkBrowser.browse(e.getURL().toString());
                    }
                });
                ep.setEditable(false);
                ep.setOpaque(false);
                JOptionPane.showMessageDialog(this.frame, ep, "Max memory limit low", 2);
            });
        }
    }

    private boolean dispatchWindowKeyEvent(KeyEvent ev) {
        if (!this.frame.isFocused()) {
            return false;
        }
        for (KeyListener listener : this.keyListeners) {
            switch (ev.getID()) {
                case 400: {
                    listener.keyTyped(ev);
                    break;
                }
                case 401: {
                    listener.keyPressed(ev);
                    break;
                }
                case 402: {
                    listener.keyReleased(ev);
                }
            }
            if (!ev.isConsumed()) continue;
            return true;
        }
        return false;
    }

    private void logGraphicsEnvironment() {
        GraphicsEnvironment graphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();
        for (GraphicsDevice graphicsDevice : graphicsEnvironment.getScreenDevices()) {
            GraphicsConfiguration configuration = graphicsDevice.getDefaultConfiguration();
            log.debug("Graphics device {}: bounds {} transform: {}", new Object[]{graphicsDevice, configuration.getBounds(), configuration.getDefaultTransform()});
        }
    }

    private GraphicsConfiguration findDisplayFromBounds(Rectangle bounds) {
        GraphicsDevice[] gds;
        for (GraphicsDevice gd : gds = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices()) {
            GraphicsConfiguration gc = gd.getDefaultConfiguration();
            Rectangle displayBounds = gc.getBounds();
            if (!displayBounds.contains(bounds)) continue;
            return gc;
        }
        return null;
    }

    private boolean showWarningOnExit() {
        if (this.config.warningOnExit() == WarningOnExit.ALWAYS) {
            return true;
        }
        if (this.config.warningOnExit() == WarningOnExit.LOGGED_IN) {
            return ((Client)this.client).getGameState() != GameState.LOGIN_SCREEN;
        }
        return false;
    }

    private void shutdownClient() {
        this.saveClientBoundsConfig();
        ClientShutdown csev = new ClientShutdown();
        this.eventBus.post(csev);
        new Thread(() -> {
            csev.waitForAllConsumers(Duration.ofSeconds(10L));
            ((Client)this.client).stopNow();
            try {
                Thread.sleep(1000L);
            }
            catch (InterruptedException interruptedException) {
                // empty catch block
            }
            System.exit(0);
        }, "RuneLite Shutdown").start();
    }

    public void paint(Graphics graphics) {
        assert (SwingUtilities.isEventDispatchThread()) : "paint must be called on EDT";
        this.frame.paint(graphics);
    }

    public int getWidth() {
        return this.frame.getWidth();
    }

    public int getHeight() {
        return this.frame.getHeight();
    }

    public boolean isFocused() {
        return this.frame.isFocused();
    }

    public void requestFocus() {
        switch (OSType.getOSType()) {
            case MacOS: {
                OSXUtil.requestUserAttention();
                break;
            }
            default: {
                this.frame.requestFocus();
            }
        }
        this.giveClientFocus();
    }

    public void forceFocus() {
        switch (OSType.getOSType()) {
            case MacOS: {
                OSXUtil.requestForeground();
                this.frame.setState(0);
                break;
            }
            case Windows: {
                WinUtil.requestForeground(this.frame);
                break;
            }
            default: {
                this.frame.requestFocus();
            }
        }
        this.giveClientFocus();
    }

    public void flashTaskbar() {
        if (!Taskbar.isTaskbarSupported()) {
            log.debug("Taskbar is not supported on this platform");
            return;
        }
        Taskbar taskbar = Taskbar.getTaskbar();
        if (taskbar.isSupported(Taskbar.Feature.USER_ATTENTION_WINDOW)) {
            taskbar.requestWindowUserAttention(this.frame);
        } else {
            log.debug("USER_ATTENTION_WINDOW is not supported");
        }
    }

    public Cursor getCurrentCursor() {
        return this.content.getCursor();
    }

    public Cursor getDefaultCursor() {
        return this.defaultCursor != null ? this.defaultCursor : Cursor.getDefaultCursor();
    }

    public void setCursor(BufferedImage image, String name) {
        Cursor cursorAwt;
        if (this.content == null) {
            return;
        }
        Point hotspot = new Point(0, 0);
        this.defaultCursor = cursorAwt = Toolkit.getDefaultToolkit().createCustomCursor(image, hotspot, name);
        this.setCursor(cursorAwt);
    }

    public void setCursor(Cursor cursor) {
        this.content.setCursor(cursor);
    }

    public void resetCursor() {
        if (this.content == null) {
            return;
        }
        this.defaultCursor = null;
        this.content.setCursor(Cursor.getDefaultCursor());
    }

    public Point getCanvasOffset() {
        Canvas canvas = ((Client)this.client).getCanvas();
        if (canvas != null) {
            return SwingUtilities.convertPoint(canvas, 0, 0, this.frame);
        }
        return new Point(0, 0);
    }

    public Insets getInsets() {
        return this.frame.getInsets();
    }

    public void paintOverlays(Graphics2D graphics) {
        Point mousePosition;
        if (this.withTitleBar) {
            return;
        }
        Client client2 = (Client)this.client;
        int x = client2.getRealDimensions().width - this.sidebarOpenIcon.getWidth() - 5;
        Widget logoutButton = client2.getWidget(10747938);
        int y = logoutButton != null && !logoutButton.isHidden() && logoutButton.getParent() != null ? logoutButton.getHeight() + logoutButton.getRelativeY() : 5;
        BufferedImage image = this.sidebar.isVisible() ? this.sidebarCloseIcon : this.sidebarOpenIcon;
        Rectangle sidebarButtonRange = new Rectangle(x - 15, 0, image.getWidth() + 25, client2.getRealDimensions().height);
        if (sidebarButtonRange.contains((mousePosition = new Point(client2.getMouseCanvasPosition().getX() + client2.getViewportXOffset(), client2.getMouseCanvasPosition().getY() + client2.getViewportYOffset())).getX(), mousePosition.getY())) {
            graphics.drawImage((Image)image, x, y, null);
        }
        this.sidebarButtonPosition.setBounds(x, y, image.getWidth(), image.getHeight());
    }

    public GraphicsConfiguration getGraphicsConfiguration() {
        return this.frame.getGraphicsConfiguration();
    }

    void openPanel(NavigationButton navBtn, boolean showSidebar) {
        if (navBtn != null && !this.sidebarEntries.contains(navBtn)) {
            return;
        }
        int index = navBtn == null ? -1 : this.sidebarEntries.headSet(navBtn).size();
        this.sidebar.setSelectedIndex(index);
        this.toggleSidebar(showSidebar, false);
        this.pushHistory();
    }

    private void toggleSidebar() {
        this.toggleSidebar(!this.sidebar.isVisible(), true);
    }

    private void toggleSidebar(boolean open, boolean pushHistory) {
        if (this.sidebar.isVisible() == open) {
            return;
        }
        if (open) {
            this.configManager.unsetConfiguration(CONFIG_GROUP, CONFIG_CLIENT_SIDEBAR_CLOSED);
        } else {
            this.configManager.setConfiguration(CONFIG_GROUP, CONFIG_CLIENT_SIDEBAR_CLOSED, true);
        }
        this.sidebar.setVisible(open);
        this.content.revalidate();
        if (pushHistory) {
            this.pushHistory();
        }
        if (this.selectedTab != null) {
            if (open) {
                SwingUtil.activate(this.selectedTab.getPanel());
            } else {
                SwingUtil.deactivate(this.selectedTab.getPanel());
            }
        }
        if (!open) {
            this.giveClientFocus();
        }
        if (this.sidebarNavBtn != null) {
            this.sidebarNavBtn.setIcon(new ImageIcon(open ? this.sidebarCloseIcon : this.sidebarOpenIcon));
            this.sidebarNavBtn.setToolTipText(open ? "Close sidebar" : "Open sidebar");
        }
    }

    private void togglePluginPanel() {
        if (!this.sidebar.isVisible() || this.sidebar.getSelectedIndex() < 0) {
            this.toggleSidebar(true, false);
            NavigationButton open = null;
            while (!this.selectedTabHistory.isEmpty()) {
                HistoryEntry historyEntry = this.selectedTabHistory.removeLast();
                if (historyEntry.navBtn == null) continue;
                open = historyEntry.navBtn;
                break;
            }
            if (open == null) {
                open = this.sidebarEntries.first();
            }
            this.openPanel(open, true);
        } else {
            this.sidebar.setSelectedIndex(-1);
        }
    }

    private void pushHistory() {
        this.selectedTabHistory.addLast(new HistoryEntry(this.sidebar.isVisible(), this.selectedTab));
        if (this.selectedTabHistory.size() > 4) {
            HistoryEntry ent = this.selectedTabHistory.removeFirst();
            if (ent.navBtn != null && this.selectedTabHistory.stream().noneMatch(it -> it.navBtn != null)) {
                this.selectedTabHistory.removeFirst();
                this.selectedTabHistory.addFirst(ent);
            }
        }
    }

    private void giveClientFocus() {
        Canvas c = ((Client)this.client).getCanvas();
        if (c != null) {
            c.requestFocusInWindow();
        } else {
            this.client.requestFocusInWindow();
        }
    }

    private void updateFrameConfig(boolean updateResizable) {
        if (this.frame == null) {
            return;
        }
        if (this.frame.getGraphicsConfiguration().getDevice().getFullScreenWindow() == null && !this.safeMode) {
            this.frame.setOpacity((float)this.config.windowOpacity() / 100.0f);
        }
        if (this.config.usernameInTitle()) {
            Player player = ((Client)this.client).getLocalPlayer();
            if (player != null && player.getName() != null) {
                this.frame.setTitle(this.title + " - " + player.getName());
            }
        } else {
            this.frame.setTitle(this.title);
        }
        if (this.frame.isAlwaysOnTopSupported()) {
            this.frame.setAlwaysOnTop(this.config.gameAlwaysOnTop());
        }
        if (updateResizable) {
            this.frame.setResizable(!this.config.lockWindowSize());
        }
        this.frame.setContainedInScreen(this.config.containInScreen());
        this.frame.updateContainsInScreen();
        if (!this.config.rememberScreenBounds()) {
            this.configManager.unsetConfiguration(CONFIG_GROUP, CONFIG_CLIENT_MAXIMIZED);
            this.configManager.unsetConfiguration(CONFIG_GROUP, CONFIG_CLIENT_BOUNDS);
        }
        this.applyGameSize(false);
    }

    private void applyGameSize(boolean force) {
        if (this.client == null) {
            return;
        }
        int width = Math.max(Math.min(this.config.gameSize().width, 7680), 765);
        int height = Math.max(Math.min(this.config.gameSize().height, 2160), 503);
        Dimension size = new Dimension(width, height);
        if (force || !size.equals(this.lastClientSize)) {
            this.lastClientSize = size;
            ((Layout)this.content.getLayout()).forceClientSize(width, height);
        }
    }

    private void windowBoundsChanged() {
        this.normalBoundsTimer.stop();
        if ((this.frame.getExtendedState() & 6) == 0) {
            this.normalBoundsTimer.start();
        }
    }

    private void setLastNormalBounds() {
        if ((this.frame.getExtendedState() & 6) == 0) {
            Dimension size;
            char mode;
            Insets insets = this.frame.getInsets();
            if (this.config.automaticResizeType() == ExpandResizeType.KEEP_GAME_SIZE) {
                mode = 'g';
                size = this.clientPanel.getSize();
            } else {
                mode = 'c';
                size = this.frame.getSize();
                size.width -= insets.left + insets.right;
                size.height -= insets.top + insets.bottom;
            }
            Point point = this.frame.getLocation();
            point.x += insets.left;
            point.y += insets.top;
            this.lastNormalBounds = point.x + ":" + point.y + ":" + size.width + ":" + size.height + ":" + mode;
        }
    }

    private void saveClientBoundsConfig() {
        if (this.lastNormalBounds != null) {
            this.configManager.setConfiguration(CONFIG_GROUP, CONFIG_CLIENT_BOUNDS, this.lastNormalBounds);
        }
        if ((this.frame.getExtendedState() & 6) != 0) {
            this.configManager.setConfiguration(CONFIG_GROUP, CONFIG_CLIENT_MAXIMIZED, true);
        } else {
            this.configManager.unsetConfiguration(CONFIG_GROUP, CONFIG_CLIENT_MAXIMIZED);
        }
    }

    private boolean restoreClientBoundsConfig() {
        String str = this.configManager.getConfiguration(CONFIG_GROUP, CONFIG_CLIENT_BOUNDS);
        if (str == null) {
            return false;
        }
        try {
            String[] splitStr = str.split(":");
            int x = Integer.parseInt(splitStr[0]);
            int y = Integer.parseInt(splitStr[1]);
            int width = Integer.parseInt(splitStr[2]);
            int height = Integer.parseInt(splitStr[3]);
            String mode = null;
            if (splitStr.length > 4) {
                mode = splitStr[4];
            }
            Insets insets = this.frame.getInsets();
            if (mode != null) {
                x -= insets.left;
                y -= insets.top;
            }
            this.frame.setLocation(x, y);
            if ("g".equals(mode)) {
                ((Layout)this.content.getLayout()).forceClientSize(width, height);
            } else {
                this.frame.setSize(width + insets.left + insets.right, height + insets.top + insets.bottom);
            }
            return true;
        }
        catch (RuntimeException ignored) {
            return false;
        }
    }

    private static void setupDefaults() {
        ToolTipManager tooltipManager = ToolTipManager.sharedInstance();
        tooltipManager.setLightWeightPopupEnabled(false);
        tooltipManager.setInitialDelay(300);
        tooltipManager.setDismissDelay(10000);
        JPopupMenu.setDefaultLightWeightPopupEnabled(false);
        System.setProperty("sun.awt.noerasebackground", "true");
    }

    @Nullable
    private static TrayIcon createTrayIcon(@Nonnull Image icon, @Nonnull String title, final @Nonnull Frame frame) {
        if (!SystemTray.isSupported()) {
            return null;
        }
        SystemTray systemTray = SystemTray.getSystemTray();
        TrayIcon trayIcon = new TrayIcon(icon, title);
        trayIcon.setImageAutoSize(true);
        try {
            systemTray.add(trayIcon);
        }
        catch (AWTException ex) {
            log.debug("Unable to add system tray icon", (Throwable)ex);
            return trayIcon;
        }
        trayIcon.addMouseListener(new java.awt.event.MouseAdapter(){

            @Override
            public void mouseClicked(MouseEvent e) {
                if (OSType.getOSType() == OSType.MacOS && !frame.isFocused()) {
                    frame.setVisible(false);
                    OSXUtil.requestForeground();
                }
                frame.setVisible(true);
                frame.setState(0);
            }
        });
        return trayIcon;
    }

    public TrayIcon getTrayIcon() {
        return this.trayIcon;
    }

    private class Layout
    implements LayoutManager2 {
        private int prevState;
        private int previousContentWidth;
        private boolean doingLayout;

        private Layout() {
        }

        @Override
        public void addLayoutComponent(String name, Component comp) {
        }

        @Override
        public void removeLayoutComponent(Component comp) {
        }

        @Override
        public void addLayoutComponent(Component comp, Object constraints) {
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public Dimension preferredLayoutSize(Container content) {
            Object object = content.getTreeLock();
            synchronized (object) {
                return this.size(content, Component::getPreferredSize);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public Dimension minimumLayoutSize(Container content) {
            Object object = content.getTreeLock();
            synchronized (object) {
                return this.size(content, c -> {
                    if (c == content.getComponent(1)) {
                        return new Dimension(c.getPreferredSize().width, c.getMinimumSize().height);
                    }
                    return c.getMinimumSize();
                });
            }
        }

        void forceClientSize(int width, int height) {
            Component client2 = ClientUI.this.content.getComponent(0);
            client2.setSize(width, height);
            Insets insets = ClientUI.this.content.getInsets();
            ClientUI.this.content.setSize(ClientUI.this.content.getWidth(), height + insets.top + insets.bottom);
            this.layout(ClientUI.this.content, true);
        }

        @Override
        public void layoutContainer(Container content) {
            this.layout(content, false);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        private void layout(Container content, boolean forceSizingClient) {
            int changed = this.prevState ^ ClientUI.this.frame.getExtendedState();
            this.prevState = ClientUI.this.frame.getExtendedState();
            Insets insets = content.getInsets();
            int insetWidth = insets.left + insets.right;
            int insetHeight = insets.top + insets.bottom;
            Component client2 = content.getComponent(0);
            Component sidebar = content.getComponent(1);
            log.trace("starting layout  - content={} client={} sidebar={} frame={} prevContent={}", new Object[]{content.getWidth(), client2.getWidth(), sidebar.getWidth(), ClientUI.this.frame.getWidth(), this.previousContentWidth});
            int innerHeight = Math.max(content.getHeight() - insetHeight, Math.max(client2.getMinimumSize().height, sidebar.getMinimumSize().height));
            sidebar.setSize(sidebar.getWidth(), innerHeight);
            Dimension minimumSize = this.minimumLayoutSize(content);
            int contentWidth = Math.max(minimumSize.width, content.getWidth()) - insetWidth;
            if (this.previousContentWidth <= 0) {
                this.previousContentWidth = contentWidth;
            }
            int clientMinWidth = client2.getMinimumSize().width;
            int clientWidth = Math.max(client2.getWidth(), clientMinWidth);
            int sidebarWidth = sidebar.isVisible() ? sidebar.getPreferredSize().width : 0;
            boolean keepGameSize = (ClientUI.this.frame.getExtendedState() & 2) == 0 && (ClientUI.this.config.automaticResizeType() == ExpandResizeType.KEEP_GAME_SIZE || forceSizingClient);
            clientWidth = keepGameSize ? Math.max(clientMinWidth, clientWidth + content.getWidth() - insetWidth - this.previousContentWidth) : Math.max(clientMinWidth, contentWidth - sidebarWidth);
            int width = clientWidth + sidebarWidth;
            content.setSize(width + insetWidth, innerHeight + insetHeight);
            content.setPreferredSize(content.getSize());
            this.previousContentWidth = width;
            client2.setBounds(insets.left, insets.top, clientWidth, innerHeight);
            sidebar.setBounds(insets.left + clientWidth, insets.top, sidebarWidth, innerHeight);
            Rectangle oldBounds = ClientUI.this.frame.getBounds();
            ClientUI.this.frame.revalidateMinimumSize();
            if (!(OSType.getOSType() == OSType.Windows && (changed & 6) != 0 || ClientUI.this.frame.getPreferredSize().equals(oldBounds.getSize()))) {
                ClientUI.this.frame.containedSetSize(ClientUI.this.frame.getPreferredSize(), oldBounds);
                if (!this.doingLayout) {
                    try {
                        this.doingLayout = true;
                        ClientUI.this.frame.validate();
                    }
                    finally {
                        this.doingLayout = false;
                    }
                }
            }
            log.trace("finishing layout - content={} client={} sidebar={} frame={}", new Object[]{content.getWidth(), client2.getWidth(), sidebar.getWidth(), ClientUI.this.frame.getWidth()});
        }

        private Dimension size(Container content, Function<Component, Dimension> sizer) {
            Dimension out = new Dimension(0, 0);
            for (int i = 0; i < content.getComponentCount(); ++i) {
                Component child = content.getComponent(i);
                if (!child.isVisible()) continue;
                Dimension dim = sizer.apply(child);
                out.width += dim.width;
                out.height = Math.max(out.height, dim.height);
            }
            Insets is = content.getInsets();
            out.width += is.left + is.right;
            out.height += is.top + is.bottom;
            return out;
        }

        @Override
        public Dimension maximumLayoutSize(Container content) {
            return this.size(content, Component::getMaximumSize);
        }

        @Override
        public float getLayoutAlignmentX(Container target) {
            return 0.0f;
        }

        @Override
        public float getLayoutAlignmentY(Container target) {
            return 0.0f;
        }

        @Override
        public void invalidateLayout(Container target) {
        }
    }

    private static class HistoryEntry {
        private final boolean sidebarOpen;
        private final NavigationButton navBtn;

        public HistoryEntry(boolean sidebarOpen, NavigationButton navBtn) {
            this.sidebarOpen = sidebarOpen;
            this.navBtn = navBtn;
        }
    }
}

