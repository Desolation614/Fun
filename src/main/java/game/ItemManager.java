/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.cache.CacheBuilder
 *  com.google.common.cache.CacheLoader
 *  com.google.common.cache.LoadingCache
 *  com.google.common.collect.ImmutableMap
 *  com.google.inject.Inject
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  javax.inject.Named
 *  javax.inject.Singleton
 *  net.runelite.api.Client
 *  net.runelite.api.GameState
 *  net.runelite.api.ItemComposition
 *  net.runelite.api.SpritePixels
 *  net.runelite.http.api.item.ItemPrice
 *  net.runelite.http.api.item.ItemStats
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package net.runelite.client.game;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableMap;
import com.google.inject.Inject;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Named;
import javax.inject.Singleton;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.ItemComposition;
import net.runelite.api.SpritePixels;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.RuneLiteConfig;
import net.runelite.client.game.ItemClient;
import net.runelite.client.game.ItemMapping;
import net.runelite.client.game.ItemStats;
import net.runelite.client.util.AsyncBufferedImage;
import net.runelite.http.api.item.ItemPrice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class ItemManager {
    private static final Logger log = LoggerFactory.getLogger(ItemManager.class);
    private final Client client;
    private final ClientThread clientThread;
    private final ItemClient itemClient;
    private final RuneLiteConfig runeLiteConfig;
    @Inject(optional=true)
    @Named(value="activePriceThreshold")
    private double activePriceThreshold = 5.0;
    @Inject(optional=true)
    @Named(value="lowPriceThreshold")
    private int lowPriceThreshold = 1000;
    private Map<Integer, ItemPrice> itemPrices = Collections.emptyMap();
    private Map<Integer, ItemStats> itemStats = Collections.emptyMap();
    private final LoadingCache<ImageKey, AsyncBufferedImage> itemImages;
    private final LoadingCache<OutlineKey, BufferedImage> itemOutlines;
    private static final ImmutableMap<Integer, Integer> WORN_ITEMS = ImmutableMap.builder().put((Object)89, (Object)88).put((Object)10554, (Object)10553).put((Object)11851, (Object)11850).put((Object)11853, (Object)11852).put((Object)11855, (Object)11854).put((Object)11857, (Object)11856).put((Object)11859, (Object)11858).put((Object)11861, (Object)11860).put((Object)13580, (Object)13579).put((Object)13582, (Object)13581).put((Object)13584, (Object)13583).put((Object)13586, (Object)13585).put((Object)13588, (Object)13587).put((Object)13590, (Object)13589).put((Object)13592, (Object)13591).put((Object)13594, (Object)13593).put((Object)13596, (Object)13595).put((Object)13598, (Object)13597).put((Object)13600, (Object)13599).put((Object)13602, (Object)13601).put((Object)13604, (Object)13603).put((Object)13606, (Object)13605).put((Object)13608, (Object)13607).put((Object)13610, (Object)13609).put((Object)13612, (Object)13611).put((Object)13614, (Object)13613).put((Object)13616, (Object)13615).put((Object)13618, (Object)13617).put((Object)13620, (Object)13619).put((Object)13622, (Object)13621).put((Object)13624, (Object)13623).put((Object)13626, (Object)13625).put((Object)13628, (Object)13627).put((Object)13630, (Object)13629).put((Object)13632, (Object)13631).put((Object)13634, (Object)13633).put((Object)13636, (Object)13635).put((Object)13638, (Object)13637).put((Object)13668, (Object)13667).put((Object)13670, (Object)13669).put((Object)13672, (Object)13671).put((Object)13674, (Object)13673).put((Object)13676, (Object)13675).put((Object)13678, (Object)13677).put((Object)21063, (Object)21061).put((Object)21066, (Object)21064).put((Object)21069, (Object)21067).put((Object)21072, (Object)21070).put((Object)21075, (Object)21073).put((Object)21078, (Object)21076).put((Object)24745, (Object)24743).put((Object)24748, (Object)24746).put((Object)24751, (Object)24749).put((Object)24754, (Object)24752).put((Object)24757, (Object)24755).put((Object)24760, (Object)24758).put((Object)25071, (Object)25069).put((Object)25074, (Object)25072).put((Object)25077, (Object)25075).put((Object)25080, (Object)25078).put((Object)25083, (Object)25081).put((Object)25086, (Object)25084).put((Object)27446, (Object)27444).put((Object)27449, (Object)27447).put((Object)27452, (Object)27450).put((Object)27455, (Object)27453).put((Object)27458, (Object)27456).put((Object)27461, (Object)27459).put((Object)30047, (Object)30045).put((Object)30050, (Object)30048).put((Object)30053, (Object)30051).put((Object)30056, (Object)30054).put((Object)30059, (Object)30057).put((Object)30062, (Object)30060).put((Object)13342, (Object)13280).put((Object)10073, (Object)10069).put((Object)10074, (Object)10071).put((Object)13341, (Object)9772).put((Object)13340, (Object)9771).put((Object)28839, (Object)10053).put((Object)28842, (Object)10055).put((Object)28845, (Object)10057).put((Object)28848, (Object)10059).put((Object)28851, (Object)10061).put((Object)28854, (Object)10063).put((Object)28857, (Object)10065).put((Object)28860, (Object)10067).build();

    @Inject
    public ItemManager(Client client, ScheduledExecutorService scheduledExecutorService, ClientThread clientThread, ItemClient itemClient, RuneLiteConfig runeLiteConfig) {
        this.client = client;
        this.clientThread = clientThread;
        this.itemClient = itemClient;
        this.runeLiteConfig = runeLiteConfig;
        scheduledExecutorService.scheduleWithFixedDelay(this::loadPrices, 0L, 30L, TimeUnit.MINUTES);
        scheduledExecutorService.submit(this::loadStats);
        this.itemImages = CacheBuilder.newBuilder().maximumSize(128L).expireAfterAccess(1L, TimeUnit.HOURS).build((CacheLoader)new CacheLoader<ImageKey, AsyncBufferedImage>(){

            public AsyncBufferedImage load(ImageKey key) throws Exception {
                return ItemManager.this.loadImage(key.itemId, key.itemQuantity, key.stackable);
            }
        });
        this.itemOutlines = CacheBuilder.newBuilder().maximumSize(128L).expireAfterAccess(1L, TimeUnit.HOURS).build((CacheLoader)new CacheLoader<OutlineKey, BufferedImage>(){

            public BufferedImage load(OutlineKey key) throws Exception {
                return ItemManager.this.loadItemOutline(key.itemId, key.itemQuantity, key.outlineColor);
            }
        });
    }

    private void loadPrices() {
    }

    private void loadStats() {
        try {
            Map<Integer, ItemStats> stats = this.itemClient.getStats();
            if (stats != null) {
                this.itemStats = ImmutableMap.copyOf(stats);
            }
            log.debug("Loaded {} stats", (Object)this.itemStats.size());
        }
        catch (IOException e) {
            log.warn("error loading stats!", (Throwable)e);
        }
    }

    public int getItemPrice(int itemID) {
        return this.getItemPriceWithSource(itemID, this.runeLiteConfig.useWikiItemPrices());
    }

    public int getItemPriceWithSource(int itemID, boolean useWikiPrice) {
        if (itemID == 995) {
            return 1;
        }
        if (itemID == 13204) {
            return 1000;
        }
        ItemComposition itemComposition = this.getItemComposition(itemID);
        if (itemComposition.getNote() != -1) {
            itemID = itemComposition.getLinkedNoteId();
        }
        itemID = (Integer)WORN_ITEMS.getOrDefault((Object)itemID, (Object)itemID);
        int price = 0;
        Collection<ItemMapping> mappedItems = ItemMapping.map(itemID);
        if (mappedItems == null) {
            ItemPrice ip = this.itemPrices.get(itemID);
            if (ip != null) {
                price = useWikiPrice ? this.getWikiPrice(ip) : ip.getPrice();
            }
        } else {
            for (ItemMapping mappedItem : mappedItems) {
                price = (int)((long)price + (long)this.getItemPriceWithSource(mappedItem.getTradeableItem(), useWikiPrice) * mappedItem.getQuantity());
            }
        }
        return price;
    }

    public int getWikiPrice(ItemPrice itemPrice) {
        int wikiPrice = itemPrice.getWikiPrice();
        int jagPrice = itemPrice.getPrice();
        if (wikiPrice <= 0) {
            return jagPrice;
        }
        if (wikiPrice <= this.lowPriceThreshold) {
            return wikiPrice;
        }
        return (double)wikiPrice < (double)jagPrice * this.activePriceThreshold ? wikiPrice : jagPrice;
    }

    @Nullable
    public ItemStats getItemStats(int itemId) {
        ItemComposition itemComposition = this.getItemComposition(itemId);
        if (itemComposition.getName() == null || itemComposition.getNote() != -1) {
            return null;
        }
        return this.itemStats.get(this.canonicalize(itemId));
    }

    @Nullable
    @Deprecated
    public net.runelite.http.api.item.ItemStats getItemStats(int itemId, boolean allowNote) {
        ItemComposition itemComposition = this.getItemComposition(itemId);
        if (itemComposition.getName() == null || !allowNote && itemComposition.getNote() != -1) {
            return null;
        }
        ItemStats stats = this.itemStats.get(this.canonicalize(itemId));
        return stats != null ? stats.toHttpApiFormat() : null;
    }

    public List<ItemPrice> search(String itemName) {
        itemName = itemName.toLowerCase();
        ArrayList<ItemPrice> result = new ArrayList<ItemPrice>();
        for (ItemPrice itemPrice : this.itemPrices.values()) {
            String name = itemPrice.getName();
            if (!name.toLowerCase().contains(itemName)) continue;
            result.add(itemPrice);
        }
        return result;
    }

    @Nonnull
    public ItemComposition getItemComposition(int itemId) {
        return this.client.getItemDefinition(itemId);
    }

    public int canonicalize(int itemID) {
        ItemComposition itemComposition = this.getItemComposition(itemID);
        if (itemComposition.getNote() != -1) {
            return itemComposition.getLinkedNoteId();
        }
        if (itemComposition.getPlaceholderTemplateId() != -1) {
            return itemComposition.getPlaceholderId();
        }
        return (Integer)WORN_ITEMS.getOrDefault((Object)itemID, (Object)itemID);
    }

    private AsyncBufferedImage loadImage(int itemId, int quantity, boolean stackable) {
        AsyncBufferedImage img = new AsyncBufferedImage(this.clientThread, 36, 32, 2);
        this.clientThread.invoke(() -> {
            if (this.client.getGameState().ordinal() < GameState.LOGIN_SCREEN.ordinal()) {
                return false;
            }
            SpritePixels sprite = this.client.createItemSprite(itemId, quantity, 1, 0x302020, stackable ? 1 : 0, false, 512);
            if (sprite == null) {
                return false;
            }
            sprite.toBufferedImage((BufferedImage)img);
            img.loaded();
            return true;
        });
        return img;
    }

    public AsyncBufferedImage getImage(int itemId) {
        return this.getImage(itemId, 1, false);
    }

    public AsyncBufferedImage getImage(int itemId, int quantity, boolean stackable) {
        try {
            return (AsyncBufferedImage)this.itemImages.get((Object)new ImageKey(itemId, quantity, stackable));
        }
        catch (ExecutionException ex) {
            return null;
        }
    }

    private BufferedImage loadItemOutline(int itemId, int itemQuantity, Color outlineColor) {
        SpritePixels itemSprite = this.client.createItemSprite(itemId, itemQuantity, 1, 0, 0, false, 512);
        return itemSprite.toBufferedOutline(outlineColor);
    }

    public BufferedImage getItemOutline(int itemId, int itemQuantity, Color outlineColor) {
        try {
            return (BufferedImage)this.itemOutlines.get((Object)new OutlineKey(itemId, itemQuantity, outlineColor));
        }
        catch (ExecutionException e) {
            return null;
        }
    }

    private static final class OutlineKey {
        private final int itemId;
        private final int itemQuantity;
        private final Color outlineColor;

        public OutlineKey(int itemId, int itemQuantity, Color outlineColor) {
            this.itemId = itemId;
            this.itemQuantity = itemQuantity;
            this.outlineColor = outlineColor;
        }

        public int getItemId() {
            return this.itemId;
        }

        public int getItemQuantity() {
            return this.itemQuantity;
        }

        public Color getOutlineColor() {
            return this.outlineColor;
        }

        public boolean equals(Object o) {
            if (o == this) {
                return true;
            }
            if (!(o instanceof OutlineKey)) {
                return false;
            }
            OutlineKey other = (OutlineKey)o;
            if (this.getItemId() != other.getItemId()) {
                return false;
            }
            if (this.getItemQuantity() != other.getItemQuantity()) {
                return false;
            }
            Color this$outlineColor = this.getOutlineColor();
            Color other$outlineColor = other.getOutlineColor();
            return !(this$outlineColor == null ? other$outlineColor != null : !((Object)this$outlineColor).equals(other$outlineColor));
        }

        public int hashCode() {
            int PRIME = 59;
            int result = 1;
            result = result * 59 + this.getItemId();
            result = result * 59 + this.getItemQuantity();
            Color $outlineColor = this.getOutlineColor();
            result = result * 59 + ($outlineColor == null ? 43 : ((Object)$outlineColor).hashCode());
            return result;
        }

        public String toString() {
            return "ItemManager.OutlineKey(itemId=" + this.getItemId() + ", itemQuantity=" + this.getItemQuantity() + ", outlineColor=" + String.valueOf(this.getOutlineColor()) + ")";
        }
    }

    private static final class ImageKey {
        private final int itemId;
        private final int itemQuantity;
        private final boolean stackable;

        public ImageKey(int itemId, int itemQuantity, boolean stackable) {
            this.itemId = itemId;
            this.itemQuantity = itemQuantity;
            this.stackable = stackable;
        }

        public int getItemId() {
            return this.itemId;
        }

        public int getItemQuantity() {
            return this.itemQuantity;
        }

        public boolean isStackable() {
            return this.stackable;
        }

        public boolean equals(Object o) {
            if (o == this) {
                return true;
            }
            if (!(o instanceof ImageKey)) {
                return false;
            }
            ImageKey other = (ImageKey)o;
            if (this.getItemId() != other.getItemId()) {
                return false;
            }
            if (this.getItemQuantity() != other.getItemQuantity()) {
                return false;
            }
            return this.isStackable() == other.isStackable();
        }

        public int hashCode() {
            int PRIME = 59;
            int result = 1;
            result = result * 59 + this.getItemId();
            result = result * 59 + this.getItemQuantity();
            result = result * 59 + (this.isStackable() ? 79 : 97);
            return result;
        }

        public String toString() {
            return "ItemManager.ImageKey(itemId=" + this.getItemId() + ", itemQuantity=" + this.getItemQuantity() + ", stackable=" + this.isStackable() + ")";
        }
    }
}

