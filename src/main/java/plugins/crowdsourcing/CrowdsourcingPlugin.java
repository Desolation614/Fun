/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.inject.Inject
 */
package net.runelite.client.plugins.crowdsourcing;

import java.time.temporal.ChronoUnit;
import javax.inject.Inject;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.crowdsourcing.CrowdsourcingManager;
import net.runelite.client.plugins.crowdsourcing.cooking.CrowdsourcingCooking;
import net.runelite.client.plugins.crowdsourcing.dialogue.CrowdsourcingDialogue;
import net.runelite.client.plugins.crowdsourcing.music.CrowdsourcingMusic;
import net.runelite.client.plugins.crowdsourcing.thieving.CrowdsourcingThieving;
import net.runelite.client.plugins.crowdsourcing.woodcutting.CrowdsourcingWoodcutting;
import net.runelite.client.plugins.crowdsourcing.zmi.CrowdsourcingZMI;
import net.runelite.client.task.Schedule;

@PluginDescriptor(name="OSRS Wiki Crowdsourcing", description="Send data to the wiki to help figure out skilling success rates, burn rates, more. See osrs.wiki/RS:CROWD", forceDisabled=true)
public class CrowdsourcingPlugin
extends Plugin {
    private static final int SECONDS_BETWEEN_UPLOADS = 300;
    @Inject
    private EventBus eventBus;
    @Inject
    private CrowdsourcingManager manager;
    @Inject
    private CrowdsourcingCooking cooking;
    @Inject
    private CrowdsourcingDialogue dialogue;
    @Inject
    private CrowdsourcingMusic music;
    @Inject
    private CrowdsourcingThieving thieving;
    @Inject
    private CrowdsourcingWoodcutting woodcutting;
    @Inject
    private CrowdsourcingZMI zmi;

    @Override
    protected void startUp() throws Exception {
        this.eventBus.register(this.cooking);
        this.eventBus.register(this.dialogue);
        this.eventBus.register(this.music);
        this.eventBus.register(this.thieving);
        this.eventBus.register(this.woodcutting);
        this.eventBus.register(this.zmi);
    }

    @Override
    protected void shutDown() throws Exception {
        this.eventBus.unregister(this.cooking);
        this.eventBus.unregister(this.dialogue);
        this.eventBus.unregister(this.music);
        this.eventBus.unregister(this.thieving);
        this.eventBus.unregister(this.woodcutting);
        this.eventBus.unregister(this.zmi);
    }

    @Schedule(period=300L, unit=ChronoUnit.SECONDS, asynchronous=true)
    public void submitToAPI() {
        this.manager.submitToAPI();
    }
}

