/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.inject.Inject
 *  net.runelite.api.Actor
 *  net.runelite.api.Client
 *  net.runelite.api.MenuAction
 *  net.runelite.api.MenuEntry
 *  net.runelite.api.NPC
 *  net.runelite.api.Point
 *  net.runelite.api.TileObject
 *  net.runelite.api.widgets.WidgetUtil
 */
package net.runelite.client.plugins.interacthighlight;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import javax.inject.Inject;
import net.runelite.api.Actor;
import net.runelite.api.Client;
import net.runelite.api.MenuAction;
import net.runelite.api.MenuEntry;
import net.runelite.api.NPC;
import net.runelite.api.Point;
import net.runelite.api.TileObject;
import net.runelite.api.widgets.WidgetUtil;
import net.runelite.client.plugins.interacthighlight.InteractHighlightConfig;
import net.runelite.client.plugins.interacthighlight.InteractHighlightPlugin;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.outline.ModelOutlineRenderer;
import net.runelite.client.util.ColorUtil;

class InteractHighlightOverlay
extends Overlay {
    private static final Color INTERACT_CLICK_COLOR = new Color(-1862270977);
    private final Client client;
    private final InteractHighlightPlugin plugin;
    private final InteractHighlightConfig config;
    private final ModelOutlineRenderer modelOutlineRenderer;

    @Inject
    private InteractHighlightOverlay(Client client, InteractHighlightPlugin plugin, InteractHighlightConfig config, ModelOutlineRenderer modelOutlineRenderer) {
        this.client = client;
        this.plugin = plugin;
        this.config = config;
        this.modelOutlineRenderer = modelOutlineRenderer;
        this.setPosition(OverlayPosition.DYNAMIC);
        this.setLayer(OverlayLayer.ABOVE_SCENE);
        this.setPriority(0.75f);
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        this.renderMouseover();
        this.renderTarget();
        return null;
    }

    private void renderMouseover() {
        MenuEntry[] menuEntries = this.client.getMenuEntries();
        if (menuEntries.length == 0) {
            return;
        }
        MenuEntry entry = this.client.isMenuOpen() ? this.hoveredMenuEntry(menuEntries) : menuEntries[menuEntries.length - 1];
        MenuAction menuAction = entry.getType();
        switch (menuAction) {
            case WIDGET_TARGET_ON_GAME_OBJECT: 
            case GAME_OBJECT_FIRST_OPTION: 
            case GAME_OBJECT_SECOND_OPTION: 
            case GAME_OBJECT_THIRD_OPTION: 
            case GAME_OBJECT_FOURTH_OPTION: 
            case GAME_OBJECT_FIFTH_OPTION: 
            case EXAMINE_OBJECT: {
                int x = entry.getParam0();
                int y = entry.getParam1();
                int id = entry.getIdentifier();
                TileObject tileObject = this.plugin.findTileObject(x, y, id);
                if (tileObject == null || !this.config.objectShowHover() || tileObject == this.plugin.getInteractedObject() && this.config.objectShowInteract()) break;
                this.modelOutlineRenderer.drawOutline(tileObject, this.config.borderWidth(), this.config.objectHoverHighlightColor(), this.config.outlineFeather());
                break;
            }
            case WIDGET_TARGET_ON_NPC: 
            case NPC_FIRST_OPTION: 
            case NPC_SECOND_OPTION: 
            case NPC_THIRD_OPTION: 
            case NPC_FOURTH_OPTION: 
            case NPC_FIFTH_OPTION: 
            case EXAMINE_NPC: {
                NPC npc = entry.getNpc();
                if (npc == null || !this.config.npcShowHover() || npc == this.plugin.getInteractedTarget() && this.config.npcShowInteract()) break;
                Color highlightColor = menuAction == MenuAction.NPC_SECOND_OPTION || menuAction == MenuAction.WIDGET_TARGET_ON_NPC && WidgetUtil.componentToInterface((int)this.client.getSelectedWidget().getId()) == 218 ? this.config.npcAttackHoverHighlightColor() : this.config.npcHoverHighlightColor();
                this.modelOutlineRenderer.drawOutline(npc, this.config.borderWidth(), highlightColor, this.config.outlineFeather());
                break;
            }
        }
    }

    private void renderTarget() {
        Actor target;
        TileObject interactedObject = this.plugin.getInteractedObject();
        if (interactedObject != null && this.config.objectShowInteract()) {
            Color clickColor = this.getClickColor(this.config.objectHoverHighlightColor(), this.config.objectInteractHighlightColor(), this.client.getGameCycle() - this.plugin.getGameCycle());
            this.modelOutlineRenderer.drawOutline(interactedObject, this.config.borderWidth(), clickColor, this.config.outlineFeather());
        }
        if ((target = this.plugin.getInteractedTarget()) instanceof NPC && this.config.npcShowInteract()) {
            Color startColor = this.plugin.isAttacked() ? this.config.npcAttackHoverHighlightColor() : this.config.npcHoverHighlightColor();
            Color endColor = this.plugin.isAttacked() ? this.config.npcAttackHighlightColor() : this.config.npcInteractHighlightColor();
            Color clickColor = this.getClickColor(startColor, endColor, this.client.getGameCycle() - this.plugin.getGameCycle());
            this.modelOutlineRenderer.drawOutline((NPC)target, this.config.borderWidth(), clickColor, this.config.outlineFeather());
        }
    }

    private Color getClickColor(Color start, Color end, long time) {
        if (time < 5L) {
            return ColorUtil.colorLerp(start, INTERACT_CLICK_COLOR, (float)time / 5.0f);
        }
        if (time < 10L) {
            return ColorUtil.colorLerp(INTERACT_CLICK_COLOR, end, (float)(time - 5L) / 5.0f);
        }
        return end;
    }

    private MenuEntry hoveredMenuEntry(MenuEntry[] menuEntries) {
        int menuX = this.client.getMenuX();
        int menuY = this.client.getMenuY();
        int menuWidth = this.client.getMenuWidth();
        Point mousePosition = this.client.getMouseCanvasPosition();
        int dy = mousePosition.getY() - menuY;
        if ((dy -= 19) < 0) {
            return menuEntries[menuEntries.length - 1];
        }
        int idx = dy / 15;
        idx = menuEntries.length - 1 - idx;
        if (mousePosition.getX() > menuX && mousePosition.getX() < menuX + menuWidth && idx >= 0 && idx < menuEntries.length) {
            return menuEntries[idx];
        }
        return menuEntries[menuEntries.length - 1];
    }
}

