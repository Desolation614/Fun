/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.inject.Inject
 */
package net.runelite.client.plugins.info;

import java.awt.image.BufferedImage;
import javax.inject.Inject;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.info.InfoPanel;
import net.runelite.client.ui.ClientToolbar;
import net.runelite.client.ui.NavigationButton;
import net.runelite.client.util.ImageUtil;

@PluginDescriptor(name="Info Panel", description="Enable the Info panel")
public class InfoPlugin
extends Plugin {
    @Inject
    private ClientToolbar clientToolbar;
    private InfoPanel panel;
    private NavigationButton navButton;

    @Override
    protected void startUp() throws Exception {
        this.panel = (InfoPanel)this.injector.getInstance(InfoPanel.class);
        this.panel.init();
        BufferedImage icon = ImageUtil.loadImageResource(this.getClass(), "info_icon.png");
        this.navButton = NavigationButton.builder().tooltip("Info").icon(icon).priority(10).panel(this.panel).build();
        this.clientToolbar.addNavigation(this.navButton);
    }

    @Override
    protected void shutDown() {
        this.panel.deinit();
        this.clientToolbar.removeNavigation(this.navButton);
        this.panel = null;
        this.navButton = null;
    }
}

