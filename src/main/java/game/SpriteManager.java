/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.cache.Cache
 *  com.google.common.cache.CacheBuilder
 *  javax.annotation.Nullable
 *  javax.inject.Inject
 *  javax.inject.Singleton
 *  net.runelite.api.Client
 *  net.runelite.api.GameState
 *  net.runelite.api.SpritePixels
 */
package net.runelite.client.game;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.SpritePixels;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.game.SpriteOverride;
import net.runelite.client.ui.overlay.infobox.InfoBox;
import net.runelite.client.ui.overlay.infobox.InfoBoxManager;
import net.runelite.client.util.ImageUtil;

@Singleton
public class SpriteManager {
    private final Client client;
    private final ClientThread clientThread;
    private final InfoBoxManager infoBoxManager;
    private final Cache<Long, BufferedImage> cache = CacheBuilder.newBuilder().maximumSize(128L).expireAfterAccess(1L, TimeUnit.HOURS).build();

    @Inject
    private SpriteManager(Client client, ClientThread clientThread, InfoBoxManager infoBoxManager) {
        this.client = client;
        this.clientThread = clientThread;
        this.infoBoxManager = infoBoxManager;
    }

    @Nullable
    public BufferedImage getSprite(int archive, int file) {
        assert (this.client.isClientThread());
        if (this.client.getGameState().ordinal() < GameState.LOGIN_SCREEN.ordinal()) {
            return null;
        }
        Long key = (long)archive << 32 | (long)file;
        BufferedImage cached = (BufferedImage)this.cache.getIfPresent((Object)key);
        if (cached != null) {
            return cached;
        }
        SpritePixels[] sp = this.client.getSprites(this.client.getIndexSprites(), archive, 0);
        if (sp == null) {
            return null;
        }
        BufferedImage img = sp[file].toBufferedImage();
        this.cache.put((Object)key, (Object)img);
        return img;
    }

    public void getSpriteAsync(int archive, int file, Consumer<BufferedImage> user) {
        BufferedImage cached = (BufferedImage)this.cache.getIfPresent((Object)((long)archive << 32 | (long)file));
        if (cached != null) {
            user.accept(cached);
            return;
        }
        this.clientThread.invoke(() -> {
            BufferedImage img = this.getSprite(archive, file);
            if (img == null) {
                return false;
            }
            user.accept(img);
            return true;
        });
    }

    public void getSpriteAsync(int archive, int file, InfoBox infoBox) {
        this.getSpriteAsync(archive, file, (BufferedImage img) -> {
            infoBox.setImage((BufferedImage)img);
            this.infoBoxManager.updateInfoBoxImage(infoBox);
        });
    }

    public void addSpriteTo(JButton c, int archive, int file) {
        this.getSpriteAsync(archive, file, (BufferedImage img) -> SwingUtilities.invokeLater(() -> c.setIcon(new ImageIcon((Image)img))));
    }

    public void addSpriteTo(JLabel c, int archive, int file) {
        this.getSpriteAsync(archive, file, (BufferedImage img) -> SwingUtilities.invokeLater(() -> c.setIcon(new ImageIcon((Image)img))));
    }

    public void addSpriteOverrides(SpriteOverride[] add) {
        if (add.length <= 0) {
            return;
        }
        this.clientThread.invokeLater(() -> {
            Map overrides = this.client.getSpriteOverrides();
            Class<?> owner = add[0].getClass();
            for (SpriteOverride o : add) {
                BufferedImage image = ImageUtil.loadImageResource(owner, o.getFileName());
                SpritePixels sp = ImageUtil.getImageSpritePixels(image, this.client);
                overrides.put(o.getSpriteId(), sp);
            }
        });
    }

    public void removeSpriteOverrides(SpriteOverride[] remove) {
        this.clientThread.invokeLater(() -> {
            Map overrides = this.client.getSpriteOverrides();
            for (SpriteOverride o : remove) {
                overrides.remove(o.getSpriteId());
            }
        });
    }
}

