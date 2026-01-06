/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.inject.Inject
 *  net.runelite.api.ChatMessageType
 *  net.runelite.api.Client
 *  net.runelite.api.ItemContainer
 *  net.runelite.api.MenuAction
 *  net.runelite.api.Player
 *  net.runelite.api.Skill
 *  net.runelite.api.events.ChatMessage
 *  net.runelite.api.events.MenuOptionClicked
 */
package net.runelite.client.plugins.crowdsourcing.cooking;

import javax.inject.Inject;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.ItemContainer;
import net.runelite.api.MenuAction;
import net.runelite.api.Player;
import net.runelite.api.Skill;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.MenuOptionClicked;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.crowdsourcing.CrowdsourcingManager;
import net.runelite.client.plugins.crowdsourcing.cooking.CookingData;

public class CrowdsourcingCooking {
    private static final int HOSIDIUS_KITCHEN_REGION = 6712;
    @Inject
    private CrowdsourcingManager manager;
    @Inject
    private Client client;
    private int lastGameObjectClicked;

    private boolean hasCookingGauntlets() {
        ItemContainer equipmentContainer = this.client.getItemContainer(94);
        if (equipmentContainer == null) {
            return false;
        }
        return equipmentContainer.contains(775);
    }

    @Subscribe
    public void onChatMessage(ChatMessage event) {
        if (event.getType() != ChatMessageType.SPAM) {
            return;
        }
        String message = event.getMessage();
        if (message.startsWith("You successfully cook") || message.startsWith("You successfully bake") || message.startsWith("You successfully fry") || message.startsWith("You manage to cook") || message.startsWith("You roast a") || message.startsWith("You spit-roast") || message.startsWith("You cook") || message.equals("You burn the mushroom in the fire.") || message.startsWith("Eventually the Jubbly") || message.startsWith("Unfortunately the Jubbly") || message.startsWith("You accidentally burn") || message.startsWith("You half-cook") || message.startsWith("The undead meat is now cooked") || message.startsWith("The undead chicken is now cooked") || message.startsWith("You successfully scramble") || message.startsWith("You accidentally spoil")) {
            boolean inHosidiusKitchen = false;
            Player local = this.client.getLocalPlayer();
            if (local != null && local.getWorldLocation().getRegionID() == 6712) {
                inHosidiusKitchen = true;
            }
            int cookingLevel = this.client.getBoostedSkillLevel(Skill.COOKING);
            boolean hasCookingGauntlets = this.hasCookingGauntlets();
            boolean kourendElite = this.client.getVarbitValue(7928) == 1;
            CookingData data = new CookingData(message, hasCookingGauntlets, inHosidiusKitchen, kourendElite, this.lastGameObjectClicked, cookingLevel);
            this.manager.storeEvent(data);
        }
    }

    @Subscribe
    public void onMenuOptionClicked(MenuOptionClicked menuOptionClicked) {
        MenuAction action = menuOptionClicked.getMenuAction();
        if (action == MenuAction.GAME_OBJECT_FIRST_OPTION || action == MenuAction.GAME_OBJECT_SECOND_OPTION || action == MenuAction.GAME_OBJECT_THIRD_OPTION || action == MenuAction.GAME_OBJECT_FOURTH_OPTION || action == MenuAction.GAME_OBJECT_FIFTH_OPTION || action == MenuAction.WIDGET_TARGET_ON_GAME_OBJECT && this.client.getSelectedWidget() != null && this.client.getSelectedWidget().getId() == 0x950000) {
            this.lastGameObjectClicked = menuOptionClicked.getId();
        }
    }
}

