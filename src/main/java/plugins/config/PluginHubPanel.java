/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Strings
 *  com.google.common.collect.HashMultimap
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.Multimap
 *  com.google.common.collect.Sets
 *  com.google.common.html.HtmlEscapers
 *  javax.annotation.Nullable
 *  javax.inject.Inject
 *  javax.inject.Singleton
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package net.runelite.client.plugins.config;

import com.google.common.base.Strings;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import com.google.common.html.HtmlEscapers;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ScheduledExecutorService;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import javax.swing.LayoutStyle;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import net.runelite.client.config.Config;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ExternalPluginsChanged;
import net.runelite.client.externalplugins.ExternalPluginClient;
import net.runelite.client.externalplugins.ExternalPluginManager;
import net.runelite.client.externalplugins.PluginHubManifest;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginManager;
import net.runelite.client.plugins.config.FixedWidthPanel;
import net.runelite.client.plugins.config.PluginSearch;
import net.runelite.client.plugins.config.SearchablePlugin;
import net.runelite.client.plugins.config.TopLevelConfigPanel;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.DynamicGridLayout;
import net.runelite.client.ui.FontManager;
import net.runelite.client.ui.PluginPanel;
import net.runelite.client.ui.components.IconTextField;
import net.runelite.client.util.ImageUtil;
import net.runelite.client.util.LinkBrowser;
import net.runelite.client.util.SwingUtil;
import net.runelite.client.util.VerificationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
class PluginHubPanel
extends PluginPanel {
    private static final Logger log = LoggerFactory.getLogger(PluginHubPanel.class);
    private static final ImageIcon MISSING_ICON;
    private static final ImageIcon HELP_ICON;
    private static final ImageIcon CONFIGURE_ICON;
    private static final ImageIcon PLUGIN_UNAVAILABLE_ICON;
    private static final Pattern SPACES;
    private final TopLevelConfigPanel topLevelConfigPanel;
    private final ExternalPluginManager externalPluginManager;
    private final PluginManager pluginManager;
    private final ExternalPluginClient externalPluginClient;
    private final ScheduledExecutorService executor;
    private final Deque<PluginIcon> iconLoadQueue = new ArrayDeque<PluginIcon>();
    private final IconTextField searchBar;
    private final JLabel refreshing;
    private final JPanel mainPanel;
    private List<PluginItem> plugins = null;
    private PluginHubManifest.ManifestFull lastManifest;

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void pumpIconQueue() {
        PluginIcon pi;
        Deque<PluginIcon> deque = this.iconLoadQueue;
        synchronized (deque) {
            pi = this.iconLoadQueue.poll();
        }
        if (pi == null) {
            return;
        }
        pi.load();
        deque = this.iconLoadQueue;
        synchronized (deque) {
            if (this.iconLoadQueue.isEmpty()) {
                return;
            }
        }
        this.executor.submit(this::pumpIconQueue);
    }

    @Inject
    PluginHubPanel(TopLevelConfigPanel topLevelConfigPanel, ExternalPluginManager externalPluginManager, PluginManager pluginManager, ExternalPluginClient externalPluginClient, final ScheduledExecutorService executor) {
        super(false);
        this.topLevelConfigPanel = topLevelConfigPanel;
        this.externalPluginManager = externalPluginManager;
        this.pluginManager = pluginManager;
        this.externalPluginClient = externalPluginClient;
        this.executor = executor;
        String refresh = "this could just be a lambda, but no, it has to be abstracted";
        this.getInputMap(1).put(KeyStroke.getKeyStroke(116, 0), refresh);
        this.getActionMap().put(refresh, new AbstractAction(){

            @Override
            public void actionPerformed(ActionEvent e) {
                PluginHubPanel.this.reloadPluginList();
            }
        });
        this.setBackground(ColorScheme.DARK_GRAY_COLOR);
        this.searchBar = new IconTextField();
        this.searchBar.setIcon(IconTextField.Icon.SEARCH);
        this.searchBar.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        this.searchBar.setHoverBackgroundColor(ColorScheme.DARK_GRAY_HOVER_COLOR);
        this.searchBar.getDocument().addDocumentListener(new DocumentListener(){

            @Override
            public void insertUpdate(DocumentEvent e) {
                executor.execute(PluginHubPanel.this::filter);
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                executor.execute(PluginHubPanel.this::filter);
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                executor.execute(PluginHubPanel.this::filter);
            }
        });
        JLabel externalPluginWarning1 = new JLabel("<html>External plugins are verified to not be malicious or rule-breaking, but are not maintained by the RuneLite developers. They may cause bugs or instability.</html>");
        externalPluginWarning1.setBackground(new Color(0xFFBB33));
        externalPluginWarning1.setForeground(Color.BLACK);
        externalPluginWarning1.setBorder(new EmptyBorder(5, 5, 5, 2));
        externalPluginWarning1.setOpaque(true);
        JLabel externalPluginWarning2 = new JLabel("Use at your own risk!");
        externalPluginWarning2.setHorizontalAlignment(0);
        externalPluginWarning2.setFont(FontManager.getRunescapeBoldFont());
        externalPluginWarning2.setBackground(externalPluginWarning1.getBackground());
        externalPluginWarning2.setForeground(externalPluginWarning1.getForeground());
        externalPluginWarning2.setBorder(new EmptyBorder(0, 5, 5, 5));
        externalPluginWarning2.setOpaque(true);
        this.mainPanel = new JPanel();
        this.mainPanel.setBorder(BorderFactory.createEmptyBorder(0, 7, 7, 7));
        this.mainPanel.setLayout(new DynamicGridLayout(0, 1, 0, 5));
        this.mainPanel.setAlignmentX(0.0f);
        this.refreshing = new JLabel("Loading...");
        this.refreshing.setHorizontalAlignment(0);
        FixedWidthPanel mainPanelWrapper = new FixedWidthPanel();
        GroupLayout layout = new GroupLayout(mainPanelWrapper);
        mainPanelWrapper.setLayout(layout);
        layout.setVerticalGroup(layout.createSequentialGroup().addComponent(externalPluginWarning1).addComponent(externalPluginWarning2).addGap(7).addComponent(this.mainPanel, -1, -2, -2).addComponent(this.refreshing).addGap(0, 0, 28672));
        layout.setHorizontalGroup(layout.createParallelGroup().addComponent(externalPluginWarning1, 0, Short.MAX_VALUE, Short.MAX_VALUE).addComponent(externalPluginWarning2, 0, Short.MAX_VALUE, Short.MAX_VALUE).addComponent(this.mainPanel).addComponent(this.refreshing, 0, Short.MAX_VALUE, Short.MAX_VALUE));
        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setHorizontalScrollBarPolicy(31);
        scrollPane.setPreferredSize(new Dimension(28672, 28672));
        scrollPane.setViewportView(mainPanelWrapper);
        GroupLayout layout2 = new GroupLayout(this);
        this.setLayout(layout2);
        layout2.setVerticalGroup(layout2.createSequentialGroup().addGap(10).addComponent(this.searchBar, 30, 30, 30).addGap(10).addComponent(scrollPane));
        layout2.setHorizontalGroup(layout2.createParallelGroup().addGroup(layout2.createSequentialGroup().addGap(10).addComponent(this.searchBar).addGap(10)).addComponent(scrollPane));
        this.revalidate();
        this.refreshing.setVisible(false);
        this.reloadPluginList();
    }

    private void reloadPluginList() {
        if (this.refreshing.isVisible()) {
            return;
        }
        this.refreshing.setVisible(true);
        this.mainPanel.removeAll();
        this.executor.submit(() -> {
            PluginHubManifest.ManifestFull manifest;
            try {
                manifest = this.externalPluginClient.downloadManifestFull();
            }
            catch (IOException | VerificationException e) {
                log.error("", (Throwable)e);
                SwingUtilities.invokeLater(() -> {
                    this.refreshing.setVisible(false);
                    this.mainPanel.add(new JLabel("Downloading the plugin manifest failed"));
                    JButton retry = new JButton("Retry");
                    retry.addActionListener(l -> this.reloadPluginList());
                    this.mainPanel.add(retry);
                });
                return;
            }
            Map<String, Integer> pluginCounts = Collections.emptyMap();
            try {
                pluginCounts = this.externalPluginClient.getPluginCounts();
            }
            catch (IOException e) {
                log.warn("unable to download plugin counts", (Throwable)e);
            }
            this.reloadPluginList(manifest, pluginCounts);
        });
    }

    private void reloadPluginList(PluginHubManifest.ManifestFull manifest, Map<String, Integer> pluginCounts) {
        this.lastManifest = manifest;
        Map display = (Map)manifest.getDisplay().stream().collect(ImmutableMap.toImmutableMap(PluginHubManifest.DisplayData::getInternalName, Function.identity()));
        Map jars = (Map)manifest.getJars().stream().collect(ImmutableMap.toImmutableMap(PluginHubManifest.JarData::getInternalName, Function.identity()));
        HashMultimap loadedPlugins = HashMultimap.create();
        for (Plugin p : this.pluginManager.getPlugins()) {
            Class<?> clazz = p.getClass();
            String iname = ExternalPluginManager.getInternalName(clazz);
            if (iname == null) continue;
            loadedPlugins.put((Object)iname, (Object)p);
        }
        HashSet<String> installed = new HashSet<String>(this.externalPluginManager.getInstalledExternalPlugins());
        this.plugins = Sets.union(display.keySet(), (Set)loadedPlugins.keySet()).stream().map(arg_0 -> this.lambda$reloadPluginList$3(display, jars, (Multimap)loadedPlugins, pluginCounts, installed, arg_0)).collect(Collectors.toList());
        SwingUtilities.invokeLater(() -> {
            if (!this.refreshing.isVisible()) {
                return;
            }
            this.refreshing.setVisible(false);
            this.executor.execute(this::filter);
        });
    }

    void filter() {
        if (this.refreshing.isVisible() || this.plugins == null) {
            return;
        }
        String query = this.searchBar.getText();
        boolean isSearching = query != null && !query.trim().isEmpty();
        List<PluginItem> pluginItems = isSearching ? PluginSearch.search(this.plugins, query) : this.plugins.stream().filter(p -> p.isInstalled() || p.getJarData() != null).sorted(Comparator.comparing(p -> p.getJarData() == null).thenComparing(PluginItem::isInstalled).thenComparingInt(PluginItem::getUserCount).reversed().thenComparing(p -> p.manifest.getDisplayName())).collect(Collectors.toList());
        SwingUtilities.invokeLater(() -> {
            this.mainPanel.removeAll();
            pluginItems.forEach(this.mainPanel::add);
            this.mainPanel.revalidate();
        });
    }

    @Override
    public void onActivate() {
        this.revalidate();
        this.reloadPluginList();
        this.searchBar.setText("");
        this.searchBar.requestFocusInWindow();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void onDeactivate() {
        this.mainPanel.removeAll();
        this.refreshing.setVisible(false);
        this.plugins = null;
        this.lastManifest = null;
        Deque<PluginIcon> deque = this.iconLoadQueue;
        synchronized (deque) {
            PluginIcon pi;
            while ((pi = this.iconLoadQueue.poll()) != null) {
                pi.loadingStarted = false;
            }
        }
    }

    @Subscribe
    private void onExternalPluginsChanged(ExternalPluginsChanged ev) {
        if (!this.refreshing.isVisible() && this.lastManifest != null) {
            this.refreshing.setVisible(true);
            Map pluginCounts = this.plugins == null ? Collections.emptyMap() : this.plugins.stream().collect(Collectors.toMap(pi -> pi.manifest.getInternalName(), PluginItem::getUserCount));
            this.executor.submit(() -> this.reloadPluginList(this.lastManifest, pluginCounts));
        }
    }

    private /* synthetic */ PluginItem lambda$reloadPluginList$3(Map display, Map jars, Multimap loadedPlugins, Map pluginCounts, Set installed, String id) {
        return new PluginItem((PluginHubManifest.DisplayData)display.get(id), (PluginHubManifest.JarData)jars.get(id), loadedPlugins.get((Object)id), pluginCounts.getOrDefault(id, -1), installed.contains(id));
    }

    static {
        SPACES = Pattern.compile(" +");
        BufferedImage missingIcon = ImageUtil.loadImageResource(PluginHubPanel.class, "pluginhub_missingicon.png");
        MISSING_ICON = new ImageIcon(missingIcon);
        BufferedImage helpIcon = ImageUtil.loadImageResource(PluginHubPanel.class, "pluginhub_help.png");
        HELP_ICON = new ImageIcon(helpIcon);
        BufferedImage configureIcon = ImageUtil.loadImageResource(PluginHubPanel.class, "pluginhub_configure.png");
        CONFIGURE_ICON = new ImageIcon(configureIcon);
        PLUGIN_UNAVAILABLE_ICON = new ImageIcon(ImageUtil.loadImageResource(PluginHubPanel.class, "mdi_alert.png"));
    }

    private class PluginItem
    extends JPanel
    implements SearchablePlugin {
        private static final int HEIGHT = 70;
        private static final int ICON_WIDTH = 48;
        private static final int BOTTOM_LINE_HEIGHT = 16;
        private final PluginHubManifest.DisplayData manifest;
        @Nullable
        private final PluginHubManifest.JarData jarData;
        private final List<String> keywords = new ArrayList<String>();
        private final int userCount;
        private final boolean installed;

        PluginItem(PluginHubManifest.DisplayData newManifest, PluginHubManifest.JarData jarData, Collection<Plugin> loadedPlugins, int userCount, boolean installed) {
            this.manifest = newManifest != null ? newManifest : ExternalPluginManager.getDisplayData(loadedPlugins.iterator().next().getClass());
            this.jarData = jarData;
            this.userCount = userCount;
            this.installed = installed;
            Collections.addAll(this.keywords, SPACES.split(this.manifest.getDisplayName().toLowerCase()));
            if (this.manifest.getDescription() != null) {
                Collections.addAll(this.keywords, SPACES.split(this.manifest.getDescription().toLowerCase()));
            }
            Collections.addAll(this.keywords, this.manifest.getAuthor().toLowerCase());
            if (this.manifest.getTags() != null) {
                Collections.addAll(this.keywords, this.manifest.getTags());
            }
            this.setBackground(ColorScheme.DARKER_GRAY_COLOR);
            this.setOpaque(true);
            GroupLayout layout = new GroupLayout(this);
            this.setLayout(layout);
            JLabel pluginName = new JLabel(this.manifest.getDisplayName());
            pluginName.setFont(FontManager.getRunescapeBoldFont());
            pluginName.setToolTipText(this.manifest.getDisplayName());
            JLabel author = new JLabel(this.manifest.getAuthor());
            author.setFont(FontManager.getRunescapeSmallFont());
            author.setToolTipText(this.manifest.getAuthor());
            JLabel version = new JLabel(this.manifest.getVersion());
            version.setFont(FontManager.getRunescapeSmallFont());
            version.setToolTipText(this.manifest.getVersion());
            Object descriptionText = this.manifest.getDescription();
            if (jarData == null) {
                descriptionText = !Strings.isNullOrEmpty((String)this.manifest.getUnavailableReason()) ? this.manifest.getUnavailableReason() : "Plugin is incompatible and requires its author to update it";
            }
            if (!((String)descriptionText).startsWith("<html>")) {
                descriptionText = "<html>" + HtmlEscapers.htmlEscaper().escape((String)descriptionText) + "</html>";
            }
            JLabel description = new JLabel((String)descriptionText);
            description.setVerticalAlignment(1);
            description.setToolTipText((String)descriptionText);
            PluginIcon icon = new PluginIcon(this.manifest);
            icon.setHorizontalAlignment(0);
            JLabel badge = new JLabel();
            if (jarData == null) {
                badge.setIcon(PLUGIN_UNAVAILABLE_ICON);
                badge.setToolTipText((String)descriptionText);
            }
            JButton help = new JButton(HELP_ICON);
            SwingUtil.removeButtonDecorations(help);
            help.setBorder(null);
            help.setToolTipText("Open help");
            help.addActionListener(ev -> LinkBrowser.browse("https://runelite.net/plugin-hub/show/" + this.manifest.getInternalName()));
            JButton configure = new JButton(CONFIGURE_ICON);
            SwingUtil.removeButtonDecorations(configure);
            configure.setToolTipText("Configure");
            configure.setBorder(null);
            if (!loadedPlugins.isEmpty()) {
                String search = null;
                if (loadedPlugins.size() > 1) {
                    search = this.manifest.getInternalName();
                } else {
                    Plugin plugin = loadedPlugins.iterator().next();
                    Config cfg = PluginHubPanel.this.pluginManager.getPluginConfigProxy(plugin);
                    if (cfg == null) {
                        search = this.manifest.getInternalName();
                    } else {
                        configure.addActionListener(l -> PluginHubPanel.this.topLevelConfigPanel.openConfigurationPanel(plugin));
                    }
                }
                if (search != null) {
                    String javaIsABadLanguage = search;
                    configure.addActionListener(l -> PluginHubPanel.this.topLevelConfigPanel.openWithFilter(javaIsABadLanguage));
                }
            } else {
                configure.setVisible(false);
            }
            boolean install = !installed && jarData != null;
            boolean update = jarData != null && !loadedPlugins.isEmpty() && !jarData.equals(ExternalPluginManager.getJarData(loadedPlugins.iterator().next().getClass()));
            boolean remove = installed && !update;
            JButton addrm = new JButton();
            if (install) {
                addrm.setText("Install");
                addrm.setBackground(new Color(2670120));
                addrm.addActionListener(l -> {
                    int result;
                    if (this.manifest.getWarning() != null && (result = JOptionPane.showConfirmDialog(this, "<html><p>" + this.manifest.getWarning() + "</p><strong>Are you sure you want to install this plugin?</strong></html>", "Installing " + this.manifest.getDisplayName(), 0, 2)) != 0) {
                        return;
                    }
                    addrm.setText("Installing");
                    addrm.setBackground(ColorScheme.MEDIUM_GRAY_COLOR);
                    PluginHubPanel.this.externalPluginManager.install(this.manifest.getInternalName());
                });
            } else if (remove) {
                addrm.setText("Remove");
                addrm.setBackground(new Color(12462120));
                addrm.addActionListener(l -> {
                    addrm.setText("Removing");
                    addrm.setBackground(ColorScheme.MEDIUM_GRAY_COLOR);
                    PluginHubPanel.this.externalPluginManager.remove(this.manifest.getInternalName());
                });
            } else if (update) {
                addrm.setText("Update");
                addrm.setBackground(new Color(2056735));
                addrm.addActionListener(l -> {
                    addrm.setText("Updating");
                    addrm.setBackground(ColorScheme.MEDIUM_GRAY_COLOR);
                    PluginHubPanel.this.externalPluginManager.update();
                });
            } else {
                addrm.setText("Unavailable");
                addrm.setBackground(Color.GRAY);
                addrm.setEnabled(false);
            }
            addrm.setBorder(new LineBorder(addrm.getBackground().darker()));
            addrm.setFocusPainted(false);
            layout.setHorizontalGroup(layout.createSequentialGroup().addGroup(layout.createParallelGroup().addComponent(badge, GroupLayout.Alignment.TRAILING).addComponent(icon, 48, 48, 48)).addGap(5).addGroup(layout.createParallelGroup().addGroup(layout.createSequentialGroup().addComponent(pluginName, 0, -2, Short.MAX_VALUE).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, -2, Short.MAX_VALUE).addComponent(author, 0, -2, Short.MAX_VALUE)).addComponent(description, 0, -2, Short.MAX_VALUE).addGroup(layout.createSequentialGroup().addComponent(version, 0, -2, Short.MAX_VALUE).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, -2, 100).addComponent(help, 0, 24, 24).addComponent(configure, 0, 24, 24).addComponent(addrm, 0, 57, -2).addGap(5))));
            int lineHeight = description.getFontMetrics(description.getFont()).getHeight();
            layout.setVerticalGroup(layout.createParallelGroup().addComponent(badge, GroupLayout.Alignment.TRAILING).addComponent(icon, 70, -1, 70 + lineHeight).addGroup(layout.createSequentialGroup().addGap(5).addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(pluginName).addComponent(author)).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, -2, Short.MAX_VALUE).addComponent(description, lineHeight, -2, lineHeight * 2).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, -2, Short.MAX_VALUE).addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(version, 16, 16, 16).addComponent(help, 16, 16, 16).addComponent(configure, 16, 16, 16).addComponent(addrm, 16, 16, 16)).addGap(5)));
        }

        @Override
        public String getSearchableName() {
            return this.manifest.getDisplayName();
        }

        @Override
        public int installs() {
            return this.userCount;
        }

        @Nullable
        public PluginHubManifest.JarData getJarData() {
            return this.jarData;
        }

        @Override
        public List<String> getKeywords() {
            return this.keywords;
        }

        public int getUserCount() {
            return this.userCount;
        }

        public boolean isInstalled() {
            return this.installed;
        }
    }

    private class PluginIcon
    extends JLabel {
        @Nullable
        private final PluginHubManifest.DisplayData manifest;
        private boolean loadingStarted;
        private boolean loaded;

        PluginIcon(PluginHubManifest.DisplayData manifest) {
            this.setIcon(MISSING_ICON);
            this.manifest = manifest.hasIcon() ? manifest : null;
            this.loaded = !manifest.hasIcon();
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void paint(Graphics g) {
            super.paint(g);
            if (!this.loaded && !this.loadingStarted) {
                this.loadingStarted = true;
                Deque<PluginIcon> deque = PluginHubPanel.this.iconLoadQueue;
                synchronized (deque) {
                    PluginHubPanel.this.iconLoadQueue.add(this);
                    if (PluginHubPanel.this.iconLoadQueue.size() == 1) {
                        PluginHubPanel.this.executor.submit(() -> PluginHubPanel.this.pumpIconQueue());
                    }
                }
            }
        }

        private void load() {
            try {
                BufferedImage img = PluginHubPanel.this.externalPluginClient.downloadIcon(this.manifest);
                this.loaded = true;
                SwingUtilities.invokeLater(() -> this.setIcon(new ImageIcon(img)));
            }
            catch (IOException e) {
                log.info("Cannot download icon for plugin \"{}\"", (Object)this.manifest.getInternalName(), (Object)e);
            }
        }
    }
}

