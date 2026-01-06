/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  javax.inject.Inject
 *  javax.inject.Singleton
 *  net.runelite.api.Client
 *  net.runelite.api.EnumComposition
 *  net.runelite.api.FriendsChatRank
 *  net.runelite.api.GameState
 *  net.runelite.api.IndexedSprite
 *  net.runelite.api.clan.ClanTitle
 *  net.runelite.api.events.GameStateChanged
 */
package net.runelite.client.game;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Singleton;
import net.runelite.api.Client;
import net.runelite.api.EnumComposition;
import net.runelite.api.FriendsChatRank;
import net.runelite.api.GameState;
import net.runelite.api.IndexedSprite;
import net.runelite.api.clan.ClanTitle;
import net.runelite.api.events.GameStateChanged;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.game.SpriteManager;
import net.runelite.client.util.AsyncBufferedImage;
import net.runelite.client.util.ImageUtil;

@Singleton
public class ChatIconManager {
    private static final Dimension IMAGE_DIMENSION = new Dimension(11, 11);
    private static final Color IMAGE_OUTLINE_COLOR = new Color(33, 33, 33);
    private final Client client;
    private final SpriteManager spriteManager;
    private final ClientThread clientThread;
    private BufferedImage[] friendsChatRankImages;
    private BufferedImage[] clanRankImages;
    private int friendsChatOffset = -1;
    private int clanOffset = -1;
    private final List<ChatIcon> icons = new ArrayList<ChatIcon>();

    @Inject
    private ChatIconManager(Client client, SpriteManager spriteManager, ClientThread clientThread, EventBus eventBus) {
        this.client = client;
        this.spriteManager = spriteManager;
        this.clientThread = clientThread;
        eventBus.register(this);
        clientThread.invokeLater(() -> {
            if (client.getGameState().getState() >= GameState.LOGIN_SCREEN.getState()) {
                if (this.friendsChatOffset == -1) {
                    this.loadRankIcons();
                }
                this.refreshIcons();
            }
        });
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Subscribe
    private void onGameStateChanged(GameStateChanged gameStateChanged) {
        GameState state = gameStateChanged.getGameState();
        if (state == GameState.STARTING) {
            this.clanOffset = -1;
            this.friendsChatOffset = -1;
            ChatIconManager chatIconManager = this;
            synchronized (chatIconManager) {
                for (ChatIcon icon : this.icons) {
                    icon.idx = -1;
                }
            }
        } else if (state == GameState.LOGIN_SCREEN) {
            if (this.friendsChatOffset == -1) {
                this.loadRankIcons();
            }
            this.refreshIcons();
        }
    }

    public synchronized int registerChatIcon(BufferedImage image) {
        if (image == null || image instanceof AsyncBufferedImage) {
            throw new IllegalArgumentException("invalid image");
        }
        IndexedSprite i = ImageUtil.getImageIndexedSprite(image, this.client);
        this.icons.add(new ChatIcon(-1, i));
        this.clientThread.invokeLater(this::refreshIcons);
        return this.icons.size() - 1;
    }

    public synchronized int reserveChatIcon() {
        assert (this.client.isClientThread());
        BufferedImage dummy = new BufferedImage(13, 13, 1);
        IndexedSprite sprite = ImageUtil.getImageIndexedSprite(dummy, this.client);
        IndexedSprite[] modicons = this.client.getModIcons();
        modicons = Arrays.copyOf(modicons, modicons.length + 1);
        modicons[modicons.length - 1] = sprite;
        this.client.setModIcons(modicons);
        this.icons.add(new ChatIcon(modicons.length - 1, sprite));
        return this.icons.size() - 1;
    }

    public synchronized void updateChatIcon(int iconId, BufferedImage image) {
        ChatIcon ci = this.icons.get(iconId);
        IndexedSprite sprite = ImageUtil.getImageIndexedSprite(image, this.client);
        int idx = ci.idx;
        if (idx == -1) {
            ci.sprite = sprite;
            return;
        }
        IndexedSprite[] modicons = this.client.getModIcons();
        modicons[idx] = sprite;
    }

    public int chatIconIndex(int iconId) {
        if (iconId < 0 || iconId >= this.icons.size()) {
            return -1;
        }
        return this.icons.get((int)iconId).idx;
    }

    @Nullable
    public BufferedImage getRankImage(FriendsChatRank friendsChatRank) {
        if (friendsChatRank == FriendsChatRank.UNRANKED) {
            return null;
        }
        return this.friendsChatRankImages[friendsChatRank.ordinal() - 1];
    }

    @Nullable
    public BufferedImage getRankImage(ClanTitle clanTitle) {
        int rank = clanTitle.getId();
        int idx = ChatIconManager.clanRankToIdx(rank);
        return this.clanRankImages[idx];
    }

    public int getIconNumber(FriendsChatRank friendsChatRank) {
        return this.friendsChatOffset == -1 ? -1 : this.friendsChatOffset + friendsChatRank.ordinal() - 1;
    }

    public int getIconNumber(ClanTitle clanTitle) {
        int rank = clanTitle.getId();
        return this.clanOffset == -1 ? -1 : this.clanOffset + ChatIconManager.clanRankToIdx(rank);
    }

    private synchronized void refreshIcons() {
        IndexedSprite[] chatIcons = this.client.getModIcons();
        if (chatIcons == null) {
            return;
        }
        int offset = chatIcons.length;
        int newIcons = 0;
        for (ChatIcon icon : this.icons) {
            assert (icon.idx < offset);
            if (icon.idx != -1) continue;
            ++newIcons;
        }
        if (newIcons == 0) {
            return;
        }
        IndexedSprite[] newChatIcons = Arrays.copyOf(chatIcons, chatIcons.length + newIcons);
        newIcons = 0;
        for (ChatIcon icon : this.icons) {
            if (icon.idx != -1) continue;
            icon.idx = offset + newIcons++;
            newChatIcons[icon.idx] = icon.sprite;
        }
        this.client.setModIcons(newChatIcons);
    }

    private void loadRankIcons() {
        int i;
        EnumComposition friendsChatIcons = this.client.getEnum(1543);
        EnumComposition clanIcons = this.client.getEnum(3798);
        Object[] modIcons = this.client.getModIcons();
        this.friendsChatOffset = modIcons.length;
        this.clanOffset = this.friendsChatOffset + friendsChatIcons.size();
        IndexedSprite blank = ImageUtil.getImageIndexedSprite(new BufferedImage(modIcons[0].getWidth(), modIcons[0].getHeight(), 2), this.client);
        modIcons = Arrays.copyOf(modIcons, this.friendsChatOffset + friendsChatIcons.size() + clanIcons.size());
        Arrays.fill(modIcons, this.friendsChatOffset, modIcons.length, blank);
        this.client.setModIcons((IndexedSprite[])modIcons);
        this.friendsChatRankImages = new BufferedImage[friendsChatIcons.size()];
        this.clanRankImages = new BufferedImage[clanIcons.size()];
        for (i = 0; i < friendsChatIcons.size(); ++i) {
            int fi = i;
            this.spriteManager.getSpriteAsync(friendsChatIcons.getIntValue(friendsChatIcons.getKeys()[i]), 0, sprite -> {
                IndexedSprite[] modIcons = this.client.getModIcons();
                this.friendsChatRankImages[fi] = ChatIconManager.friendsChatImageFromSprite(sprite);
                modIcons[this.friendsChatOffset + fi] = ImageUtil.getImageIndexedSprite(this.friendsChatRankImages[fi], this.client);
            });
        }
        for (i = 0; i < clanIcons.size(); ++i) {
            int key = clanIcons.getKeys()[i];
            int idx = ChatIconManager.clanRankToIdx(key);
            assert (idx >= 0 && idx < clanIcons.size());
            this.spriteManager.getSpriteAsync(clanIcons.getIntValue(key), 0, sprite -> {
                BufferedImage img;
                IndexedSprite[] modIcons = this.client.getModIcons();
                this.clanRankImages[idx] = img = ImageUtil.resizeCanvas(sprite, ChatIconManager.IMAGE_DIMENSION.width, ChatIconManager.IMAGE_DIMENSION.height);
                modIcons[this.clanOffset + idx] = ImageUtil.getImageIndexedSprite(img, this.client);
            });
        }
    }

    private static BufferedImage friendsChatImageFromSprite(BufferedImage sprite) {
        BufferedImage canvas = ImageUtil.resizeCanvas(sprite, ChatIconManager.IMAGE_DIMENSION.width, ChatIconManager.IMAGE_DIMENSION.height);
        return ImageUtil.outlineImage(canvas, IMAGE_OUTLINE_COLOR);
    }

    private static int clanRankToIdx(int key) {
        return key < 0 ? ~key : key + 5;
    }

    private static class ChatIcon {
        int idx;
        IndexedSprite sprite;

        public ChatIcon(int idx, IndexedSprite sprite) {
            this.idx = idx;
            this.sprite = sprite;
        }
    }
}

