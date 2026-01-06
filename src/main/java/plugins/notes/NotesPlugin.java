/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.inject.Provides
 *  javax.inject.Inject
 */
package net.runelite.client.plugins.notes;

import com.google.inject.Provides;
import java.awt.image.BufferedImage;
import javax.inject.Inject;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ProfileChanged;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.notes.NotesConfig;
import net.runelite.client.plugins.notes.NotesPanel;
import net.runelite.client.ui.ClientToolbar;
import net.runelite.client.ui.NavigationButton;
import net.runelite.client.util.ImageUtil;

@PluginDescriptor(name="Notes", description="Enable the Notes panel", tags={"panel"})
public class NotesPlugin
extends Plugin {
    @Inject
    private ClientToolbar clientToolbar;
    @Inject
    private NotesConfig config;
    private NotesPanel panel;
    private NavigationButton navButton;

    @Provides
    NotesConfig getConfig(ConfigManager configManager) {
        return configManager.getConfig(NotesConfig.class);
    }

    @Override
    protected void startUp() throws Exception {
        this.panel = (NotesPanel)this.injector.getInstance(NotesPanel.class);
        this.panel.init(this.config);
        BufferedImage icon = ImageUtil.loadImageResource(this.getClass(), "notes_icon.png");
        this.navButton = NavigationButton.builder().tooltip("Notes").icon(icon).priority(7).panel(this.panel).build();
        this.clientToolbar.addNavigation(this.navButton);
    }

    @Override
    protected void shutDown() {
        this.clientToolbar.removeNavigation(this.navButton);
    }

    @Subscribe
    public void onProfileChanged(ProfileChanged profileChanged) {
        String data = this.config.notesData();
        this.panel.setNotes(data);
    }
}

