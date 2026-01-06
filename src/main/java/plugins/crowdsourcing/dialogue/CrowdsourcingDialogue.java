/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.inject.Inject
 *  net.runelite.api.ChatMessageType
 *  net.runelite.api.Client
 *  net.runelite.api.events.ChatMessage
 *  net.runelite.api.events.GameTick
 *  net.runelite.api.widgets.Widget
 */
package net.runelite.client.plugins.crowdsourcing.dialogue;

import javax.inject.Inject;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.GameTick;
import net.runelite.api.widgets.Widget;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.crowdsourcing.CrowdsourcingManager;
import net.runelite.client.plugins.crowdsourcing.dialogue.ChatMessageData;
import net.runelite.client.plugins.crowdsourcing.dialogue.DialogueOptionsData;
import net.runelite.client.plugins.crowdsourcing.dialogue.DoubleSpriteTextData;
import net.runelite.client.plugins.crowdsourcing.dialogue.NpcDialogueData;
import net.runelite.client.plugins.crowdsourcing.dialogue.PlayerDialogueData;
import net.runelite.client.plugins.crowdsourcing.dialogue.SpriteTextData;
import net.runelite.client.plugins.crowdsourcing.dialogue.StartEndData;

public class CrowdsourcingDialogue {
    private static final String USERNAME_TOKEN = "%USERNAME%";
    @Inject
    private Client client;
    @Inject
    private CrowdsourcingManager manager;
    private boolean inDialogue = false;
    private String lastDialogueText = null;
    private int lastItemId1 = -1;
    private int lastItemId2 = -1;
    private Widget[] dialogueOptions;

    private String sanitize(String dialogue) {
        String username = this.client.getLocalPlayer().getName();
        return dialogue.replace("\u00a0", " ").replace(username, USERNAME_TOKEN);
    }

    @Subscribe
    public void onGameTick(GameTick tick) {
        Object data;
        Widget npcDialogueTextWidget = this.client.getWidget(15138822);
        Widget playerDialogueTextWidget = this.client.getWidget(14221318);
        Widget playerDialogueOptionsWidget = this.client.getWidget(14352385);
        Widget spriteWidget = this.client.getWidget(0xC10001);
        Widget spriteTextWidget = this.client.getWidget(12648450);
        Widget doubleSpriteTextWidget = this.client.getWidget(720898);
        Widget doubleSprite1Widget = this.client.getWidget(720897);
        Widget doubleSprite2Widget = this.client.getWidget(720899);
        if (!(this.inDialogue || npcDialogueTextWidget == null && playerDialogueTextWidget == null && playerDialogueOptionsWidget == null && spriteTextWidget == null && doubleSpriteTextWidget == null)) {
            this.inDialogue = true;
            this.manager.storeEvent(new StartEndData(true));
        } else if (this.inDialogue && npcDialogueTextWidget == null && playerDialogueTextWidget == null && playerDialogueOptionsWidget == null && spriteTextWidget == null && doubleSpriteTextWidget == null) {
            this.inDialogue = false;
            this.manager.storeEvent(new StartEndData(false));
            this.lastDialogueText = null;
        }
        if (npcDialogueTextWidget != null && !npcDialogueTextWidget.getText().equals(this.lastDialogueText)) {
            this.lastDialogueText = npcDialogueTextWidget.getText();
            String npcName = this.client.getWidget(15138820).getText();
            NpcDialogueData data2 = new NpcDialogueData(this.sanitize(this.lastDialogueText), npcName);
            this.manager.storeEvent(data2);
        }
        if (playerDialogueTextWidget != null && !playerDialogueTextWidget.getText().equals(this.lastDialogueText)) {
            this.lastDialogueText = playerDialogueTextWidget.getText();
            data = new PlayerDialogueData(this.sanitize(this.lastDialogueText));
            this.manager.storeEvent(data);
        }
        if (playerDialogueOptionsWidget != null && playerDialogueOptionsWidget.getChildren() != this.dialogueOptions) {
            this.lastDialogueText = null;
            this.dialogueOptions = playerDialogueOptionsWidget.getChildren();
            String[] optionsText = new String[this.dialogueOptions.length];
            for (int i = 0; i < this.dialogueOptions.length; ++i) {
                optionsText[i] = this.sanitize(this.dialogueOptions[i].getText());
            }
            DialogueOptionsData data3 = new DialogueOptionsData(optionsText);
            this.manager.storeEvent(data3);
        }
        if (!(spriteWidget == null || spriteTextWidget == null || spriteTextWidget.getText().equals(this.lastDialogueText) && spriteWidget.getItemId() == this.lastItemId1)) {
            this.lastItemId1 = spriteWidget.getItemId();
            this.lastDialogueText = spriteTextWidget.getText();
            data = new SpriteTextData(this.sanitize(this.lastDialogueText), this.lastItemId1);
            this.manager.storeEvent(data);
        }
        if (!(doubleSprite1Widget == null || doubleSpriteTextWidget == null || doubleSpriteTextWidget.getText().equals(this.lastDialogueText) && doubleSprite1Widget.getItemId() == this.lastItemId1 && doubleSprite2Widget.getItemId() == this.lastItemId2)) {
            this.lastItemId1 = doubleSprite1Widget.getItemId();
            this.lastItemId2 = doubleSprite2Widget.getItemId();
            this.lastDialogueText = doubleSpriteTextWidget.getText();
            data = new DoubleSpriteTextData(this.sanitize(this.lastDialogueText), this.lastItemId1, this.lastItemId2);
            this.manager.storeEvent(data);
        }
    }

    @Subscribe
    public void onChatMessage(ChatMessage chatMessage) {
        if ((chatMessage.getType() == ChatMessageType.DIALOG || chatMessage.getType() == ChatMessageType.MESBOX) && this.client.getLocalPlayer().getName() != null) {
            ChatMessageData data = new ChatMessageData(this.sanitize(chatMessage.getMessage()), chatMessage.getType());
            this.manager.storeEvent(data);
        }
    }
}

