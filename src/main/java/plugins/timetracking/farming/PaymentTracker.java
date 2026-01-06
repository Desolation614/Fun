/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableSet
 *  javax.inject.Inject
 *  javax.inject.Singleton
 *  net.runelite.api.Client
 *  net.runelite.api.MenuAction
 *  net.runelite.api.events.GameTick
 *  net.runelite.api.events.MenuOptionClicked
 *  net.runelite.api.events.ScriptPreFired
 *  net.runelite.api.widgets.Widget
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package net.runelite.client.plugins.timetracking.farming;

import com.google.common.collect.ImmutableSet;
import java.lang.reflect.Type;
import java.util.Set;
import javax.inject.Inject;
import javax.inject.Singleton;
import net.runelite.api.Client;
import net.runelite.api.MenuAction;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.MenuOptionClicked;
import net.runelite.api.events.ScriptPreFired;
import net.runelite.api.widgets.Widget;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.timetracking.farming.FarmingPatch;
import net.runelite.client.plugins.timetracking.farming.FarmingRegion;
import net.runelite.client.plugins.timetracking.farming.FarmingWorld;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class PaymentTracker {
    private static final Logger log = LoggerFactory.getLogger(PaymentTracker.class);
    private static final Set<String> PAYMENT_TEXT = ImmutableSet.of((Object)"That'll do nicely, sir. Leave it with me - I'll make sure<br>that patch grows for you.", (Object)"That'll do nicely, madam. Leave it with me - I'll make<br>sure that patch grows for you.", (Object)"That'll do nicely. Leave it with me - I'll make sure that<br>patch grows for you.", (Object)"That'll do nicely, iknami. Leave it with me - I'll make<br>sure that patch grows for you.");
    private final Client client;
    private final ConfigManager configManager;
    private final FarmingWorld farmingWorld;
    private int lastSelectedOption;

    @Subscribe
    public void onGameTick(GameTick gameTick) {
        Widget text = this.client.getWidget(15138822);
        if (text == null || !PAYMENT_TEXT.contains(text.getText())) {
            return;
        }
        Widget name = this.client.getWidget(15138820);
        Widget head = this.client.getWidget(15138818);
        if (name == null || head == null || head.getModelType() != 2) {
            return;
        }
        int npcId = head.getModelId();
        FarmingPatch patch = this.findPatchForNpc(npcId);
        if (patch == null) {
            return;
        }
        if (this.getProtectedState(patch)) {
            return;
        }
        log.debug("Detected patch payment for {} ({}) patch {}", new Object[]{name.getText(), npcId, patch});
        this.setProtectedState(patch, true);
    }

    @Subscribe
    public void onMenuOptionClicked(MenuOptionClicked opt) {
        MenuAction action = opt.getMenuAction();
        if (action == MenuAction.WIDGET_CONTINUE) {
            Widget w = opt.getWidget();
            if (w != null && w.getId() == 14352385 && w.getIndex() > -1 && PaymentTracker.isPatchOption(w.getText())) {
                this.lastSelectedOption = w.getIndex() - 1;
                log.debug("Selected option via click: {}", (Object)this.lastSelectedOption);
            }
        } else if ((action == MenuAction.NPC_THIRD_OPTION || action == MenuAction.NPC_FOURTH_OPTION) && opt.getMenuOption().startsWith("Pay")) {
            this.lastSelectedOption = action == MenuAction.NPC_THIRD_OPTION ? 0 : 1;
            log.debug("Selected option via npc op: {}", (Object)this.lastSelectedOption);
        }
    }

    @Subscribe
    public void onScriptPreFired(ScriptPreFired scriptPreFired) {
        if (scriptPreFired.getScriptId() == 2153) {
            int[] intStack = this.client.getIntStack();
            int componentId = intStack[0];
            int subId = intStack[1];
            Widget w = this.client.getWidget(componentId).getChild(subId);
            if (componentId == 14352385 && subId > -1 && PaymentTracker.isPatchOption(w.getText())) {
                this.lastSelectedOption = subId - 1;
                log.debug("Selected option via keypress: {}", (Object)this.lastSelectedOption);
            }
        }
    }

    private static boolean isPatchOption(String name) {
        if (name == null) {
            return false;
        }
        return name.contains("Patch") || name.contains("allotment");
    }

    private static String configKey(FarmingPatch fp) {
        return fp.configKey() + ".protected";
    }

    void setProtectedState(FarmingPatch fp, boolean state) {
        if (!state) {
            this.configManager.unsetRSProfileConfiguration("timetracking", PaymentTracker.configKey(fp));
        } else {
            this.configManager.setRSProfileConfiguration("timetracking", PaymentTracker.configKey(fp), state);
        }
    }

    boolean getProtectedState(FarmingPatch fp) {
        return Boolean.TRUE.equals(this.configManager.getRSProfileConfiguration("timetracking", PaymentTracker.configKey(fp), (Type)((Object)Boolean.class)));
    }

    private FarmingPatch findPatchForNpc(int npcId) {
        FarmingPatch p = null;
        for (FarmingRegion region : this.farmingWorld.getRegionsForLocation(this.client.getLocalPlayer().getWorldLocation())) {
            for (FarmingPatch patch : region.getPatches()) {
                if (patch.getFarmer() != npcId || patch.getPatchNumber() != -1 && patch.getPatchNumber() != this.lastSelectedOption) continue;
                p = patch;
            }
        }
        return p;
    }

    @Inject
    private PaymentTracker(Client client, ConfigManager configManager, FarmingWorld farmingWorld) {
        this.client = client;
        this.configManager = configManager;
        this.farmingWorld = farmingWorld;
    }
}

