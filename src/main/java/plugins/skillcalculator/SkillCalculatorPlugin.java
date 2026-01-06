/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.inject.Provider
 *  javax.inject.Inject
 *  net.runelite.api.Client
 *  net.runelite.api.WorldType
 *  net.runelite.api.events.WorldChanged
 */
package net.runelite.client.plugins.skillcalculator;

import com.google.inject.Provider;
import java.awt.image.BufferedImage;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.WorldType;
import net.runelite.api.events.WorldChanged;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.skillcalculator.SkillCalculatorPanel;
import net.runelite.client.ui.ClientToolbar;
import net.runelite.client.ui.NavigationButton;
import net.runelite.client.ui.PluginPanel;
import net.runelite.client.util.ImageUtil;

@PluginDescriptor(name="Skill Calculator", description="Enable the Skill Calculator panel", tags={"panel", "skilling"})
public class SkillCalculatorPlugin
extends Plugin {
    @Inject
    private Client client;
    @Inject
    private ClientToolbar clientToolbar;
    @Inject
    private Provider<SkillCalculatorPanel> uiPanel;
    private NavigationButton uiNavigationButton;
    private boolean lastWorldWasMembers;

    @Override
    protected void startUp() throws Exception {
        BufferedImage icon = ImageUtil.loadImageResource(this.getClass(), "calc.png");
        this.uiNavigationButton = NavigationButton.builder().tooltip("Skill Calculator").icon(icon).priority(6).panel((PluginPanel)this.uiPanel.get()).build();
        this.clientToolbar.addNavigation(this.uiNavigationButton);
    }

    @Override
    protected void shutDown() throws Exception {
        this.clientToolbar.removeNavigation(this.uiNavigationButton);
    }

    @Subscribe
    public void onWorldChanged(WorldChanged event) {
        boolean currentWorldIsMembers = this.client.getWorldType().contains(WorldType.MEMBERS);
        if (currentWorldIsMembers != this.lastWorldWasMembers) {
            ((SkillCalculatorPanel)this.uiPanel.get()).reloadCurrentCalculator();
        }
        this.lastWorldWasMembers = currentWorldIsMembers;
    }
}

