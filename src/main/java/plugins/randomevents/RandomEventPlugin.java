/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableSet
 *  com.google.inject.Provides
 *  javax.inject.Inject
 *  net.runelite.api.Actor
 *  net.runelite.api.Client
 *  net.runelite.api.MenuAction
 *  net.runelite.api.NPC
 *  net.runelite.api.Player
 *  net.runelite.api.events.InteractingChanged
 *  net.runelite.api.events.MenuEntryAdded
 *  net.runelite.api.events.NpcDespawned
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package net.runelite.client.plugins.randomevents;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Provides;
import java.util.Arrays;
import java.util.Set;
import javax.inject.Inject;
import net.runelite.api.Actor;
import net.runelite.api.Client;
import net.runelite.api.MenuAction;
import net.runelite.api.NPC;
import net.runelite.api.Player;
import net.runelite.api.events.InteractingChanged;
import net.runelite.api.events.MenuEntryAdded;
import net.runelite.api.events.NpcDespawned;
import net.runelite.client.Notifier;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.config.Notification;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.randomevents.RandomEventConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@PluginDescriptor(name="Random Events", description="Notify when random events appear and remove talk/dismiss options on events that aren't yours.", enabledByDefault=false)
public class RandomEventPlugin
extends Plugin {
    private static final Logger log = LoggerFactory.getLogger(RandomEventPlugin.class);
    private static final Set<Integer> EVENT_NPCS = ImmutableSet.of((Object)6747, (Object)5426, (Object)307, (Object)314, (Object)322, (Object)6749, (Object[])new Integer[]{390, 6754, 6744, 6748, 5429, 326, 327, 5438, 5441, 6746, 5437, 5440, 6750, 6751, 6752, 6753, 5436, 5439, 380, 6738, 6755, 375, 376, 5510, 6743, 12551, 12552});
    private static final Set<String> EVENT_OPTIONS = ImmutableSet.of((Object)"Talk-to", (Object)"Dismiss");
    private static final int RANDOM_EVENT_TIMEOUT = 150;
    private NPC currentRandomEvent;
    private int lastNotificationTick = -150;
    @Inject
    private Client client;
    @Inject
    private Notifier notifier;
    @Inject
    private RandomEventConfig config;

    @Provides
    RandomEventConfig getConfig(ConfigManager configManager) {
        return configManager.getConfig(RandomEventConfig.class);
    }

    @Override
    protected void shutDown() throws Exception {
        this.lastNotificationTick = 0;
        this.currentRandomEvent = null;
    }

    @Subscribe
    public void onInteractingChanged(InteractingChanged event) {
        Actor source = event.getSource();
        Actor target = event.getTarget();
        Player player = this.client.getLocalPlayer();
        if (player == null || target != player || player.getInteracting() == source || !(source instanceof NPC) || !EVENT_NPCS.contains(((NPC)source).getId())) {
            return;
        }
        log.debug("Random event spawn: {}", (Object)source.getName());
        this.currentRandomEvent = (NPC)source;
        if (this.client.getTickCount() - this.lastNotificationTick > 150) {
            this.lastNotificationTick = this.client.getTickCount();
            this.notifier.notify(this.shouldNotify(this.currentRandomEvent.getId()), "Random event spawned: " + this.currentRandomEvent.getName());
        }
    }

    @Subscribe
    public void onNpcDespawned(NpcDespawned npcDespawned) {
        NPC npc = npcDespawned.getNpc();
        if (npc == this.currentRandomEvent) {
            this.currentRandomEvent = null;
        }
    }

    @Subscribe
    public void onMenuEntryAdded(MenuEntryAdded event) {
        NPC npc;
        if (event.getType() >= MenuAction.NPC_FIRST_OPTION.getId() && event.getType() <= MenuAction.NPC_FIFTH_OPTION.getId() && EVENT_OPTIONS.contains(event.getOption()) && (npc = event.getMenuEntry().getNpc()) != null && EVENT_NPCS.contains(npc.getId()) && npc != this.currentRandomEvent && this.config.removeMenuOptions()) {
            this.client.setMenuEntries(Arrays.copyOf(this.client.getMenuEntries(), this.client.getMenuEntries().length - 1));
        }
    }

    private Notification shouldNotify(int id) {
        Notification notification = null;
        switch (id) {
            case 6747: {
                notification = this.config.notifyBeekeeper();
                break;
            }
            case 5426: {
                notification = this.config.notifyArnav();
                break;
            }
            case 322: {
                notification = this.config.notifyDwarf();
                break;
            }
            case 6743: {
                notification = this.config.notifyDemon();
                break;
            }
            case 6748: {
                notification = this.config.notifyForester();
                break;
            }
            case 5429: {
                notification = this.config.notifyFrog();
                break;
            }
            case 326: 
            case 327: {
                notification = this.config.notifyGenie();
                break;
            }
            case 5436: 
            case 5437: 
            case 5438: 
            case 5439: 
            case 5440: 
            case 5441: {
                notification = this.config.notifyCerters();
                break;
            }
            case 307: 
            case 314: {
                notification = this.config.notifyJekyll();
                break;
            }
            case 390: {
                notification = this.config.notifyBob();
                break;
            }
            case 6754: {
                notification = this.config.notifyPrison();
                break;
            }
            case 6746: {
                notification = this.config.notifyGravedigger();
                break;
            }
            case 6750: 
            case 6751: {
                notification = this.config.notifyMoM();
                break;
            }
            case 6752: {
                notification = this.config.notifyMaze();
                break;
            }
            case 6753: {
                notification = this.config.notifyMime();
                break;
            }
            case 380: {
                notification = this.config.notifyPillory();
                break;
            }
            case 6738: {
                notification = this.config.notifyTwin();
                break;
            }
            case 6755: {
                notification = this.config.notifyQuiz();
                break;
            }
            case 375: 
            case 376: {
                notification = this.config.notifyTurpentine();
                break;
            }
            case 6749: {
                notification = this.config.notifyDunce();
                break;
            }
            case 5510: {
                notification = this.config.notifySandwich();
                break;
            }
            case 6744: {
                notification = this.config.notifyFlippa();
                break;
            }
            case 12551: 
            case 12552: {
                notification = this.config.notifyCountCheck();
            }
        }
        if (notification != null && notification.isEnabled()) {
            return notification;
        }
        return this.config.notifyAllEvents();
    }
}

