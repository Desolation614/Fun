/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  javax.inject.Inject
 *  javax.inject.Singleton
 *  net.runelite.api.Client
 *  net.runelite.api.Player
 *  net.runelite.api.SpritePixels
 *  net.runelite.api.coords.LocalPoint
 *  net.runelite.api.events.GameStateChanged
 */
package net.runelite.client.plugins.instancemap;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Singleton;
import net.runelite.api.Client;
import net.runelite.api.Player;
import net.runelite.api.SpritePixels;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.events.GameStateChanged;
import net.runelite.client.game.SpriteManager;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.BackgroundComponent;

@Singleton
class InstanceMapOverlay
extends Overlay {
    static final int TILE_SIZE = 4;
    private static final int PLAYER_MARKER_SIZE = 4;
    private static final int MAX_PLANE = 3;
    private static final int MIN_PLANE = 0;
    private int viewedPlane = 0;
    private final Client client;
    private final SpriteManager spriteManager;
    private volatile BufferedImage mapImage;
    private volatile boolean showMap = false;
    private final BackgroundComponent backgroundComponent = new BackgroundComponent();
    private boolean isCloseButtonHovered;
    private Rectangle closeButtonBounds;
    private BufferedImage closeButtonImage;
    private BufferedImage closeButtonHoveredImage;

    @Inject
    InstanceMapOverlay(Client client, SpriteManager spriteManager) {
        this.client = client;
        this.spriteManager = spriteManager;
        this.setPriority(0.75f);
        this.setPosition(OverlayPosition.TOP_LEFT);
        this.setLayer(OverlayLayer.ABOVE_WIDGETS);
        this.backgroundComponent.setFill(false);
    }

    public boolean isMapShown() {
        return this.showMap;
    }

    public synchronized void setShowMap(boolean show) {
        this.showMap = show;
        if (this.showMap) {
            this.viewedPlane = this.client.getPlane();
        }
        this.mapImage = null;
        this.closeButtonBounds = null;
    }

    public synchronized void onAscend() {
        if (this.viewedPlane >= 3) {
            return;
        }
        ++this.viewedPlane;
        this.mapImage = null;
    }

    public synchronized void onDescend() {
        if (this.viewedPlane <= 0) {
            return;
        }
        --this.viewedPlane;
        this.mapImage = null;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Dimension render(Graphics2D graphics) {
        if (!this.showMap) {
            return null;
        }
        BufferedImage image = this.mapImage;
        if (image == null) {
            SpritePixels map = this.client.drawInstanceMap(this.viewedPlane);
            image = InstanceMapOverlay.minimapToBufferedImage(map, this.client.isResized() ? this.client.getExpandedMapLoading() : 0);
            InstanceMapOverlay instanceMapOverlay = this;
            synchronized (instanceMapOverlay) {
                if (this.showMap) {
                    this.mapImage = image;
                }
            }
        }
        BufferedImage closeButton = this.getCloseButtonImage();
        BufferedImage closeButtonHover = this.getCloseButtonHoveredImage();
        if (closeButton != null && this.closeButtonBounds == null) {
            this.closeButtonBounds = new Rectangle(image.getWidth() - closeButton.getWidth() - 5, 6, closeButton.getWidth(), closeButton.getHeight());
        }
        graphics.drawImage((Image)image, 0, 0, null);
        this.backgroundComponent.setRectangle(new Rectangle(0, 0, image.getWidth(), image.getHeight()));
        this.backgroundComponent.render(graphics);
        if (this.client.getPlane() == this.viewedPlane) {
            this.drawPlayerDot(graphics, this.client.getLocalPlayer(), Color.white, Color.black, this.client.isResized() ? this.client.getExpandedMapLoading() : 0);
        }
        if (this.isCloseButtonHovered) {
            closeButton = closeButtonHover;
        }
        if (closeButton != null) {
            graphics.drawImage((Image)closeButton, (int)this.closeButtonBounds.getX(), (int)this.closeButtonBounds.getY(), null);
        }
        return new Dimension(image.getWidth(), image.getHeight());
    }

    private void drawPlayerDot(Graphics2D graphics, Player player, Color dotColor, Color outlineColor, int expandedChunks) {
        LocalPoint playerLoc = player.getLocalLocation();
        int tileX = playerLoc.getSceneX() + expandedChunks * 8;
        int tileY = 104 + expandedChunks * 16 - 1 - (playerLoc.getSceneY() + expandedChunks * 8);
        int x = tileX * 4;
        int y = tileY * 4;
        graphics.setColor(dotColor);
        graphics.fillRect(x, y, 4, 4);
        graphics.setColor(outlineColor);
        graphics.drawRect(x, y, 4, 4);
    }

    public void onGameStateChange(GameStateChanged event) {
        this.mapImage = null;
        this.closeButtonBounds = null;
    }

    private static BufferedImage minimapToBufferedImage(SpritePixels spritePixels, int expandedChunks) {
        int width = spritePixels.getWidth();
        int height = spritePixels.getHeight();
        int[] pixels = spritePixels.getPixels();
        BufferedImage img = new BufferedImage(width, height, 1);
        img.setRGB(0, 0, width, height, pixels, 0, width);
        int maxChunks = 5;
        if (expandedChunks < maxChunks) {
            int cropChunks = maxChunks - expandedChunks;
            img = img.getSubimage(4 * (cropChunks * 8), 4 * (cropChunks * 8), (104 + expandedChunks * 2 * 8) * 4, (104 + expandedChunks * 2 * 8) * 4);
        }
        return img;
    }

    @Nullable
    private BufferedImage getCloseButtonImage() {
        if (this.closeButtonImage == null) {
            this.closeButtonImage = this.spriteManager.getSprite(539, 0);
        }
        return this.closeButtonImage;
    }

    @Nullable
    private BufferedImage getCloseButtonHoveredImage() {
        if (this.closeButtonHoveredImage == null) {
            this.closeButtonHoveredImage = this.spriteManager.getSprite(540, 0);
        }
        return this.closeButtonHoveredImage;
    }

    public void setCloseButtonHovered(boolean isCloseButtonHovered) {
        this.isCloseButtonHovered = isCloseButtonHovered;
    }

    public Rectangle getCloseButtonBounds() {
        return this.closeButtonBounds;
    }
}

