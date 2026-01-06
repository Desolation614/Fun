/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.inject.Inject
 *  net.runelite.api.ChatMessageType
 *  net.runelite.api.Client
 *  net.runelite.api.GameObject
 *  net.runelite.api.GameState
 *  net.runelite.api.Perspective
 *  net.runelite.api.Player
 *  net.runelite.api.Point
 *  net.runelite.api.coords.LocalPoint
 *  net.runelite.api.coords.WorldPoint
 *  net.runelite.api.events.ChatMessage
 *  net.runelite.api.events.GameObjectSpawned
 *  net.runelite.api.events.GameStateChanged
 *  net.runelite.api.events.GameTick
 *  net.runelite.api.widgets.Widget
 *  net.runelite.api.widgets.WidgetItem
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package net.runelite.client.plugins.mta.alchemy;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.Objects;
import javax.inject.Inject;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.GameObject;
import net.runelite.api.GameState;
import net.runelite.api.Perspective;
import net.runelite.api.Player;
import net.runelite.api.Point;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.GameObjectSpawned;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetItem;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.mta.MTAConfig;
import net.runelite.client.plugins.mta.MTAPlugin;
import net.runelite.client.plugins.mta.MTARoom;
import net.runelite.client.plugins.mta.alchemy.AlchemyItem;
import net.runelite.client.plugins.mta.alchemy.AlchemyRoomTimer;
import net.runelite.client.plugins.mta.alchemy.Cupboard;
import net.runelite.client.ui.overlay.infobox.InfoBoxManager;
import net.runelite.client.util.AsyncBufferedImage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AlchemyRoom
extends MTARoom {
    private static final Logger log = LoggerFactory.getLogger(AlchemyRoom.class);
    private static final int MTA_ALCH_REGION = 13462;
    private static final int IMAGE_Z_OFFSET = 150;
    private static final int NUM_CUPBOARDS = 6;
    private static final int INFO_ITEM_START = 7;
    private static final int INFO_POINT_START = 12;
    private static final int INFO_LENGTH = 5;
    private static final int BEST_POINTS = 30;
    private static final String YOU_FOUND = "You found:";
    private static final String EMPTY = "The cupboard is empty.";
    private final Cupboard[] cupboards = new Cupboard[6];
    private final MTAPlugin plugin;
    private final Client client;
    private final ItemManager itemManager;
    private final InfoBoxManager infoBoxManager;
    private AlchemyItem best;
    private Cupboard suggestion;
    private boolean hintSet;

    @Inject
    private AlchemyRoom(Client client, MTAConfig config, MTAPlugin plugin, ItemManager itemManager, InfoBoxManager infoBoxManager) {
        super(config);
        this.client = client;
        this.plugin = plugin;
        this.itemManager = itemManager;
        this.infoBoxManager = infoBoxManager;
    }

    @Subscribe
    public void onGameTick(GameTick event) {
        if (!this.inside() || !this.config.alchemy()) {
            return;
        }
        AlchemyItem bestItem = this.getBest();
        if (this.best == null || this.best != bestItem) {
            if (this.best != null) {
                this.infoBoxManager.removeIf(e -> e instanceof AlchemyRoomTimer);
                this.infoBoxManager.addInfoBox(new AlchemyRoomTimer(this.plugin));
            }
            log.debug("Item change to {}!", (Object)this.best);
            this.best = bestItem;
            Arrays.stream(this.cupboards).filter(Objects::nonNull).forEach(e -> {
                e.alchemyItem = AlchemyItem.UNKNOWN;
            });
            this.client.clearHintArrow();
        }
        this.suggestion = this.getSuggestion();
    }

    @Subscribe
    public void onGameObjectSpawned(GameObjectSpawned event) {
        int cupboardId;
        if (!this.inside()) {
            return;
        }
        GameObject spawn = event.getGameObject();
        switch (spawn.getId()) {
            case 23678: 
            case 23679: {
                cupboardId = 0;
                break;
            }
            case 23680: 
            case 23681: {
                cupboardId = 1;
                break;
            }
            case 23682: 
            case 23683: {
                cupboardId = 2;
                break;
            }
            case 23684: 
            case 23685: {
                cupboardId = 3;
                break;
            }
            case 23686: 
            case 23687: {
                cupboardId = 4;
                break;
            }
            case 23688: 
            case 23689: {
                cupboardId = 5;
                break;
            }
            default: {
                return;
            }
        }
        Cupboard cupboard = this.cupboards[cupboardId];
        if (cupboard != null) {
            cupboard.gameObject = spawn;
        } else {
            cupboard = new Cupboard();
            cupboard.gameObject = spawn;
            cupboard.alchemyItem = AlchemyItem.UNKNOWN;
            this.cupboards[cupboardId] = cupboard;
        }
    }

    @Subscribe
    public void onGameStateChanged(GameStateChanged gameStateChanged) {
        if (gameStateChanged.getGameState() == GameState.LOGGED_IN && !this.inside()) {
            this.reset();
            if (this.hintSet) {
                this.client.clearHintArrow();
                this.hintSet = false;
            }
        }
    }

    @Subscribe
    public void onChatMessage(ChatMessage wrapper) {
        if (!this.inside() || !this.config.alchemy()) {
            return;
        }
        String message = wrapper.getMessage();
        if (wrapper.getType() == ChatMessageType.GAMEMESSAGE) {
            if (message.contains(YOU_FOUND)) {
                String item = message.replace(YOU_FOUND, "").trim();
                AlchemyItem alchemyItem = AlchemyItem.find(item);
                Cupboard clicked = this.getClicked();
                if (clicked.alchemyItem != alchemyItem) {
                    this.fill(clicked, alchemyItem);
                }
            } else if (message.equals(EMPTY)) {
                Cupboard clicked = this.getClicked();
                if (clicked.alchemyItem != AlchemyItem.EMPTY) {
                    this.fill(clicked, AlchemyItem.EMPTY);
                }
            }
        }
    }

    private void reset() {
        Arrays.fill(this.cupboards, null);
    }

    @Override
    public boolean inside() {
        Player player = this.client.getLocalPlayer();
        return player != null && player.getWorldLocation().getRegionID() == 13462 && player.getWorldLocation().getPlane() == 2;
    }

    private AlchemyItem getBest() {
        for (int i = 0; i < 5; ++i) {
            Widget textWidget = this.client.getWidget(194, 7 + i);
            if (textWidget == null) {
                return null;
            }
            String item = textWidget.getText();
            Widget pointsWidget = this.client.getWidget(194, 12 + i);
            int points = Integer.parseInt(pointsWidget.getText());
            if (points != 30) continue;
            return AlchemyItem.find(item);
        }
        return null;
    }

    private Cupboard getClicked() {
        Cupboard nearest = null;
        double distance = Double.MAX_VALUE;
        WorldPoint mine = this.client.getLocalPlayer().getWorldLocation();
        for (Cupboard cupboard : this.cupboards) {
            if (cupboard == null) continue;
            double objectDistance = cupboard.gameObject.getWorldLocation().distanceTo(mine);
            if (nearest != null && !(objectDistance < distance)) continue;
            nearest = cupboard;
            distance = objectDistance;
        }
        return nearest;
    }

    private void fill(Cupboard cupboard, AlchemyItem alchemyItem) {
        int idx = Arrays.asList(this.cupboards).indexOf(cupboard);
        assert (idx != -1);
        int itemIdx = alchemyItem.ordinal();
        log.debug("Filling cupboard {} with {}", (Object)idx, (Object)alchemyItem);
        for (int i = 0; i < 6; ++i) {
            int cupIdx = (idx + i) % 6;
            int itemIndex = (itemIdx + i) % 6;
            this.cupboards[cupIdx].alchemyItem = itemIndex <= 4 ? AlchemyItem.values()[itemIndex] : AlchemyItem.EMPTY;
        }
    }

    @Override
    public void under(Graphics2D graphics) {
        if (!this.getConfig().alchemy() || this.best == null || !this.inside()) {
            return;
        }
        boolean found = false;
        for (Cupboard cupboard : this.cupboards) {
            if (cupboard == null) continue;
            GameObject object = cupboard.gameObject;
            AlchemyItem alchemyItem = cupboard.alchemyItem;
            if (alchemyItem == AlchemyItem.EMPTY) continue;
            if (alchemyItem == this.best) {
                this.client.setHintArrow(object.getWorldLocation());
                found = true;
                this.hintSet = true;
            }
            AsyncBufferedImage image = this.itemManager.getImage(alchemyItem.getId());
            Point canvasLoc = Perspective.getCanvasImageLocation((Client)this.client, (LocalPoint)object.getLocalLocation(), (BufferedImage)image, (int)150);
            if (canvasLoc == null) continue;
            graphics.drawImage((Image)image, canvasLoc.getX(), canvasLoc.getY(), null);
        }
        if (!found && this.suggestion != null) {
            this.client.setHintArrow(this.suggestion.gameObject.getWorldLocation());
            this.hintSet = true;
        }
    }

    private Cupboard getSuggestion() {
        if (this.best != null) {
            for (Cupboard cupboard : this.cupboards) {
                if (cupboard == null || cupboard.alchemyItem != this.best) continue;
                return cupboard;
            }
        }
        return null;
    }

    @Override
    public void renderItemOverlay(Graphics2D graphics, int itemId, WidgetItem widgetItem) {
        assert (this.inside());
        if (this.best == null || this.best.getId() != itemId || !this.config.alchemy()) {
            return;
        }
        this.drawItem(graphics, widgetItem, Color.GREEN);
    }

    private void drawItem(Graphics2D graphics, WidgetItem item, Color border) {
        Rectangle bounds = item.getCanvasBounds();
        graphics.setColor(border);
        graphics.draw(bounds);
    }
}

