/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.inject.Inject
 *  net.runelite.api.ChatMessageType
 *  net.runelite.api.Client
 *  net.runelite.api.ItemContainer
 *  net.runelite.api.NPC
 *  net.runelite.api.Skill
 *  net.runelite.api.coords.WorldPoint
 *  net.runelite.api.events.ChatMessage
 *  net.runelite.api.events.MenuOptionClicked
 */
package net.runelite.client.plugins.crowdsourcing.thieving;

import java.util.regex.Pattern;
import javax.inject.Inject;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.ItemContainer;
import net.runelite.api.NPC;
import net.runelite.api.Skill;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.MenuOptionClicked;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.crowdsourcing.CrowdsourcingManager;
import net.runelite.client.plugins.crowdsourcing.thieving.PickpocketData;

public class CrowdsourcingThieving {
    private static final String BLACKJACK_SUCCESS = "You smack the bandit over the head and render them unconscious.";
    private static final String BLACKJACK_FAIL = "Your blow only glances off the bandit's head.";
    private static final Pattern PICKPOCKET_SUCCESS = Pattern.compile("You pick .*'s pocket\\.");
    private static final Pattern PICKPOCKET_FAIL = Pattern.compile("You fail to pick .*'s pocket\\.");
    @Inject
    private Client client;
    @Inject
    private CrowdsourcingManager manager;
    private int lastPickpocketTarget;

    private boolean hasGlovesOfSilence() {
        ItemContainer equipmentContainer = this.client.getItemContainer(94);
        if (equipmentContainer == null) {
            return false;
        }
        return equipmentContainer.contains(10075);
    }

    private boolean hasThievingCape() {
        ItemContainer equipmentContainer = this.client.getItemContainer(94);
        if (equipmentContainer == null) {
            return false;
        }
        return equipmentContainer.contains(9777) || equipmentContainer.contains(9778) || equipmentContainer.contains(13342);
    }

    private int getArdougneDiary() {
        int easy = this.client.getVarbitValue(4458);
        int medium = this.client.getVarbitValue(4459);
        int hard = this.client.getVarbitValue(4460);
        int elite = this.client.getVarbitValue(4461);
        return easy + 2 * medium + 4 * hard + 8 * elite;
    }

    @Subscribe
    public void onChatMessage(ChatMessage event) {
        if (event.getType() != ChatMessageType.SPAM) {
            return;
        }
        String message = event.getMessage();
        if (BLACKJACK_SUCCESS.equals(message) || BLACKJACK_FAIL.equals(message) || PICKPOCKET_FAIL.matcher(message).matches() || PICKPOCKET_SUCCESS.matcher(message).matches()) {
            WorldPoint location = this.client.getLocalPlayer().getWorldLocation();
            int ardougneDiary = this.getArdougneDiary();
            boolean silence = this.hasGlovesOfSilence();
            boolean thievingCape = this.hasThievingCape();
            int thievingLevel = this.client.getBoostedSkillLevel(Skill.THIEVING);
            PickpocketData data = new PickpocketData(thievingLevel, this.lastPickpocketTarget, message, location, silence, thievingCape, ardougneDiary);
            this.manager.storeEvent(data);
        }
    }

    @Subscribe
    public void onMenuOptionClicked(MenuOptionClicked event) {
        if (event.getMenuOption().equals("Pickpocket") || event.getMenuOption().equals("Knock-Out")) {
            NPC npc = event.getMenuEntry().getNpc();
            this.lastPickpocketTarget = npc != null ? npc.getId() : -1;
        }
    }
}

