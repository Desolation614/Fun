/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Strings
 *  com.google.inject.Provides
 *  javax.annotation.Nullable
 *  javax.inject.Inject
 *  net.runelite.api.Client
 *  net.runelite.api.MenuAction
 *  net.runelite.api.MenuEntry
 *  net.runelite.api.ScriptEvent
 *  net.runelite.api.events.GameTick
 *  net.runelite.api.events.MenuEntryAdded
 *  net.runelite.api.events.ScriptPostFired
 *  net.runelite.api.events.VarbitChanged
 *  net.runelite.api.events.WidgetLoaded
 *  net.runelite.api.widgets.Widget
 *  net.runelite.api.widgets.WidgetUtil
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package net.runelite.client.plugins.fairyring;

import com.google.common.base.Strings;
import com.google.inject.Provides;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.TreeMap;
import javax.annotation.Nullable;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.MenuAction;
import net.runelite.api.MenuEntry;
import net.runelite.api.ScriptEvent;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.MenuEntryAdded;
import net.runelite.api.events.ScriptPostFired;
import net.runelite.api.events.VarbitChanged;
import net.runelite.api.events.WidgetLoaded;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetUtil;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.game.chatbox.ChatboxPanelManager;
import net.runelite.client.game.chatbox.ChatboxTextInput;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.fairyring.FairyRingConfig;
import net.runelite.client.plugins.fairyring.FairyRings;
import net.runelite.client.util.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@PluginDescriptor(name="Fairy Rings", description="Show the location of the fairy ring teleport", tags={"teleportation"})
public class FairyRingPlugin
extends Plugin {
    private static final Logger log = LoggerFactory.getLogger(FairyRingPlugin.class);
    private static final String[] leftDial = new String[]{"A", "D", "C", "B"};
    private static final String[] middleDial = new String[]{"I", "L", "K", "J"};
    private static final String[] rightDial = new String[]{"P", "S", "R", "Q"};
    private static final int ENTRY_PADDING = 3;
    private static final String MENU_OPEN = "Open";
    private static final String MENU_CLOSE = "Close";
    private static final String EDIT_TAGS_MENU_OPTION = "Edit Tags";
    @Inject
    private Client client;
    @Inject
    private FairyRingConfig config;
    @Inject
    private ChatboxPanelManager chatboxPanelManager;
    @Inject
    private ClientThread clientThread;
    @Inject
    private ConfigManager configManager;
    private ChatboxTextInput searchInput = null;
    private ChatboxTextInput tagInput;
    private Widget searchBtn;
    private Collection<CodeWidgets> codes = null;

    @Provides
    FairyRingConfig getConfig(ConfigManager configManager) {
        return configManager.getConfig(FairyRingConfig.class);
    }

    @Override
    public void resetConfiguration() {
        List<String> extraKeys = this.configManager.getConfigurationKeys("fairyrings.fairyringtags");
        for (String prefix : extraKeys) {
            List<String> keys = this.configManager.getConfigurationKeys(prefix);
            for (String key : keys) {
                String[] str = key.split("\\.", 2);
                if (str.length != 2) continue;
                this.configManager.unsetConfiguration(str[0], str[1]);
            }
        }
    }

    @Subscribe
    public void onVarbitChanged(VarbitChanged event) {
        this.setWidgetTextToDestination();
    }

    @Subscribe
    public void onWidgetLoaded(WidgetLoaded widgetLoaded) {
        if (widgetLoaded.getGroupId() == 381) {
            this.setWidgetTextToDestination();
            Widget header = this.client.getWidget(24969218);
            if (header != null) {
                this.searchBtn = header.createChild(-1, 5);
                this.searchBtn.setSpriteId(1113);
                this.searchBtn.setOriginalWidth(17);
                this.searchBtn.setOriginalHeight(17);
                this.searchBtn.setOriginalX(11);
                this.searchBtn.setOriginalY(11);
                this.searchBtn.setHasListener(true);
                this.searchBtn.setAction(1, MENU_OPEN);
                this.searchBtn.setOnOpListener(new Object[]{this::menuOpen});
                this.searchBtn.setName("Search");
                this.searchBtn.revalidate();
                this.codes = null;
                if (this.config.autoOpen()) {
                    this.openSearch();
                }
            }
        }
    }

    private void menuOpen(ScriptEvent e) {
        this.openSearch();
        this.client.playSoundEffect(2266);
    }

    private void menuClose(ScriptEvent e) {
        this.updateFilter("");
        this.chatboxPanelManager.close();
        this.client.playSoundEffect(2266);
    }

    private void setWidgetTextToDestination() {
        Widget fairyRingTeleportButton = this.client.getWidget(26083354);
        if (fairyRingTeleportButton != null && !fairyRingTeleportButton.isHidden()) {
            String destination;
            try {
                FairyRings fairyRingDestination = this.getFairyRingDestination(this.client.getVarbitValue(3985), this.client.getVarbitValue(3986), this.client.getVarbitValue(3987));
                destination = fairyRingDestination.getDestination();
            }
            catch (IllegalArgumentException ex) {
                destination = "Invalid location";
            }
            fairyRingTeleportButton.setText(destination);
        }
    }

    private FairyRings getFairyRingDestination(int varbitValueDialLeft, int varbitValueDialMiddle, int varbitValueDialRight) {
        return FairyRings.valueOf(leftDial[varbitValueDialLeft] + middleDial[varbitValueDialMiddle] + rightDial[varbitValueDialRight]);
    }

    private void openSearch() {
        this.updateFilter("");
        this.searchBtn.setAction(1, MENU_CLOSE);
        this.searchBtn.setOnOpListener(new Object[]{this::menuClose});
        this.searchInput = this.chatboxPanelManager.openTextInput("Filter fairy rings").onChanged(s -> this.clientThread.invokeLater(() -> this.updateFilter((String)s))).onDone(s -> false).onClose(() -> {
            this.clientThread.invokeLater(() -> this.updateFilter(""));
            this.searchBtn.setOnOpListener(new Object[]{this::menuOpen});
            this.searchBtn.setAction(1, MENU_OPEN);
        }).build();
    }

    @Subscribe
    public void onGameTick(GameTick t) {
        boolean tagInputBoxOpen;
        Widget fairyRingTeleportButton = this.client.getWidget(26083354);
        boolean fairyRingWidgetOpen = fairyRingTeleportButton != null && !fairyRingTeleportButton.isHidden();
        boolean searchInputBoxOpen = this.searchInput != null && this.chatboxPanelManager.getCurrentInput() == this.searchInput;
        boolean bl = tagInputBoxOpen = this.tagInput != null && this.chatboxPanelManager.getCurrentInput() == this.tagInput;
        if (!fairyRingWidgetOpen && (searchInputBoxOpen || tagInputBoxOpen)) {
            this.chatboxPanelManager.close();
        }
    }

    private void updateFilter(String input) {
        Widget separator;
        String filter = input.toLowerCase();
        Widget list = this.client.getWidget(24969223);
        Widget favorites = this.client.getWidget(24969224);
        if (list == null) {
            return;
        }
        if (this.codes != null && this.codes.stream().noneMatch(w -> {
            Widget codeWidget = w.getCode();
            if (codeWidget == null) {
                return false;
            }
            return list.getChild(codeWidget.getIndex()) == codeWidget;
        })) {
            this.codes = null;
        }
        if (this.codes == null) {
            TreeMap<Integer, CodeWidgets> codeMap = new TreeMap<Integer, CodeWidgets>();
            for (Widget w2 : list.getStaticChildren()) {
                if (w2.isSelfHidden()) continue;
                if (w2.getSpriteId() != -1) {
                    codeMap.computeIfAbsent(w2.getRelativeY(), k -> new CodeWidgets()).setFavorite(w2);
                    continue;
                }
                if (Strings.isNullOrEmpty((String)w2.getText())) continue;
                codeMap.computeIfAbsent(w2.getRelativeY(), k -> new CodeWidgets()).setDescription(w2);
            }
            for (Widget w2 : list.getDynamicChildren()) {
                if (w2.isSelfHidden()) continue;
                CodeWidgets c = codeMap.computeIfAbsent(w2.getRelativeY(), k -> new CodeWidgets());
                c.setCode(w2);
            }
            if (favorites != null) {
                for (Widget w2 : favorites.getStaticChildren()) {
                    if (w2.getId() == 24969225) continue;
                    if (w2.getSpriteId() != -1 && !w2.isSelfHidden()) {
                        codeMap.computeIfAbsent(w2.getRelativeY(), k -> new CodeWidgets()).setFavorite(w2);
                        continue;
                    }
                    if (!Strings.isNullOrEmpty((String)w2.getName()) && !w2.isSelfHidden()) {
                        codeMap.computeIfAbsent(w2.getRelativeY(), k -> new CodeWidgets()).setDescription(w2);
                        continue;
                    }
                    if (Strings.isNullOrEmpty((String)w2.getText()) || w2.isSelfHidden()) continue;
                    codeMap.computeIfAbsent(w2.getRelativeY(), k -> new CodeWidgets()).setCode(w2);
                }
            }
            this.codes = codeMap.values();
        }
        if ((separator = this.client.getWidget(24969225)) != null) {
            separator.setHidden(true);
            separator.setOriginalY(3);
        }
        int y = 0;
        CodeWidgets lastFavorite = null;
        boolean hasFavorites = false;
        boolean hasNormal = false;
        for (CodeWidgets c : this.codes) {
            boolean hidden;
            String code = Text.removeTags(c.getDescription().getName()).replace(" ", "");
            String tags = null;
            if (!code.isEmpty()) {
                try {
                    FairyRings ring = FairyRings.valueOf(code);
                    tags = ring.getTags();
                }
                catch (IllegalArgumentException e) {
                    log.warn("Unable to find ring with code '{}'", (Object)code, (Object)e);
                }
            }
            boolean bl = hidden = !filter.isEmpty() && !Text.removeTags(c.getDescription().getText()).toLowerCase().contains(filter) && !code.toLowerCase().contains(filter) && (tags == null || !tags.contains(filter)) && !this.getConfigTags(code).stream().anyMatch(s -> s.contains(filter));
            if (!(hidden || lastFavorite == null || c.getFavorite() != null && c.getFavorite().getSpriteId() != 1341)) {
                y += 3;
                lastFavorite = null;
            }
            if (c.getCode() != null) {
                c.getCode().setHidden(hidden);
                c.getCode().setOriginalY(y);
            }
            if (c.getFavorite() != null) {
                c.getFavorite().setHidden(hidden);
                c.getFavorite().setOriginalY(y);
            }
            c.getDescription().setHidden(hidden);
            c.getDescription().setOriginalY(y);
            if (!hidden) {
                y += c.getDescription().getHeight() + 3;
                if (c.getFavorite() != null && c.getFavorite().getSpriteId() == 1340) {
                    hasFavorites = true;
                    lastFavorite = c;
                } else {
                    hasNormal = true;
                }
                if (!hasFavorites || !hasNormal) continue;
                separator.setHidden(false);
                continue;
            }
            if (c.getFavorite() == null || c.getFavorite().getSpriteId() != 1340) continue;
            separator.setOriginalY(separator.getOriginalY() + c.getDescription().getHeight() + 3);
        }
        if ((y -= 3) < 0) {
            y = 0;
        }
        int newHeight = 0;
        if (list.getScrollHeight() > 0) {
            newHeight = list.getScrollY() * y / list.getScrollHeight();
        }
        list.setScrollHeight(y);
        list.revalidateScroll();
        this.client.runScript(new Object[]{72, 24969368, 24969223, newHeight});
    }

    @Subscribe
    public void onMenuEntryAdded(MenuEntryAdded event) {
        if (WidgetUtil.componentToInterface((int)event.getActionParam1()) == 381 && event.getOption().equals("Use code")) {
            this.client.getMenu().createMenuEntry(-1).setParam0(event.getActionParam0()).setParam1(event.getActionParam1()).setTarget(event.getTarget()).setOption(EDIT_TAGS_MENU_OPTION).setType(MenuAction.RUNELITE).setIdentifier(event.getIdentifier()).onClick(this::setTagMenuOpen);
        }
    }

    @Subscribe
    public void onScriptPostFired(ScriptPostFired event) {
        if (event.getScriptId() == 402 && this.searchInput != null && this.tagInput == null) {
            this.clientThread.invokeLater(() -> this.updateFilter(this.searchInput.getValue()));
        }
    }

    private List<String> getConfigTags(String fairyRingCode) {
        String config = Optional.ofNullable(this.configManager.getConfiguration("fairyrings.fairyringtags", fairyRingCode)).orElse("").toLowerCase();
        return Text.fromCSV(config);
    }

    private void setConfigTags(String fairyRingCode, String tags) {
        if (Strings.isNullOrEmpty((String)tags)) {
            this.configManager.unsetConfiguration("fairyrings.fairyringtags", fairyRingCode);
        } else {
            this.configManager.setConfiguration("fairyrings.fairyringtags", fairyRingCode, tags);
        }
    }

    private void setTagMenuOpen(MenuEntry menuEntry) {
        String code = Text.removeTags(menuEntry.getTarget()).replaceAll(" ", "");
        String initialValue = Text.toCSV(this.getConfigTags(code));
        this.client.playSoundEffect(2266);
        this.tagInput = this.chatboxPanelManager.openTextInput("Code " + code + " tags:").value(initialValue).onDone(s -> {
            this.setConfigTags(code, (String)s);
            if (this.config.autoOpen()) {
                this.clientThread.invokeLater(this::openSearch);
            }
        }).build();
    }

    private static class CodeWidgets {
        @Nullable
        private Widget favorite;
        @Nullable
        private Widget code;
        private Widget description;

        @Nullable
        public Widget getFavorite() {
            return this.favorite;
        }

        @Nullable
        public Widget getCode() {
            return this.code;
        }

        public Widget getDescription() {
            return this.description;
        }

        public void setFavorite(@Nullable Widget favorite) {
            this.favorite = favorite;
        }

        public void setCode(@Nullable Widget code) {
            this.code = code;
        }

        public void setDescription(Widget description) {
            this.description = description;
        }

        public boolean equals(Object o) {
            if (o == this) {
                return true;
            }
            if (!(o instanceof CodeWidgets)) {
                return false;
            }
            CodeWidgets other = (CodeWidgets)o;
            if (!other.canEqual(this)) {
                return false;
            }
            Widget this$favorite = this.getFavorite();
            Widget other$favorite = other.getFavorite();
            if (this$favorite == null ? other$favorite != null : !this$favorite.equals(other$favorite)) {
                return false;
            }
            Widget this$code = this.getCode();
            Widget other$code = other.getCode();
            if (this$code == null ? other$code != null : !this$code.equals(other$code)) {
                return false;
            }
            Widget this$description = this.getDescription();
            Widget other$description = other.getDescription();
            return !(this$description == null ? other$description != null : !this$description.equals(other$description));
        }

        protected boolean canEqual(Object other) {
            return other instanceof CodeWidgets;
        }

        public int hashCode() {
            int PRIME = 59;
            int result = 1;
            Widget $favorite = this.getFavorite();
            result = result * 59 + ($favorite == null ? 43 : $favorite.hashCode());
            Widget $code = this.getCode();
            result = result * 59 + ($code == null ? 43 : $code.hashCode());
            Widget $description = this.getDescription();
            result = result * 59 + ($description == null ? 43 : $description.hashCode());
            return result;
        }

        public String toString() {
            return "FairyRingPlugin.CodeWidgets(favorite=" + String.valueOf(this.getFavorite()) + ", code=" + String.valueOf(this.getCode()) + ", description=" + String.valueOf(this.getDescription()) + ")";
        }
    }
}

