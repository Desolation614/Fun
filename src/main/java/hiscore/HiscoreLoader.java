/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.cache.CacheLoader
 *  com.google.common.util.concurrent.ListenableFuture
 *  com.google.common.util.concurrent.ListeningExecutorService
 *  com.google.common.util.concurrent.MoreExecutors
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package net.runelite.client.hiscore;

import com.google.common.cache.CacheLoader;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import java.io.IOException;
import java.util.concurrent.ScheduledExecutorService;
import net.runelite.client.hiscore.HiscoreClient;
import net.runelite.client.hiscore.HiscoreEndpoint;
import net.runelite.client.hiscore.HiscoreManager;
import net.runelite.client.hiscore.HiscoreResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class HiscoreLoader
extends CacheLoader<HiscoreManager.HiscoreKey, HiscoreResult> {
    private static final Logger log = LoggerFactory.getLogger(HiscoreLoader.class);
    private final ListeningExecutorService executorService;
    private final HiscoreClient hiscoreClient;

    HiscoreLoader(ScheduledExecutorService executor, HiscoreClient client) {
        this.executorService = MoreExecutors.listeningDecorator((ScheduledExecutorService)executor);
        this.hiscoreClient = client;
    }

    public HiscoreResult load(HiscoreManager.HiscoreKey hiscoreKey) throws Exception {
        return HiscoreManager.EMPTY;
    }

    public ListenableFuture<HiscoreResult> reload(HiscoreManager.HiscoreKey hiscoreKey, HiscoreResult oldValue) {
        log.debug("Submitting hiscore lookup for {} type {}", (Object)hiscoreKey.getUsername(), (Object)hiscoreKey.getType());
        return this.executorService.submit(() -> this.fetch(hiscoreKey));
    }

    private HiscoreResult fetch(HiscoreManager.HiscoreKey hiscoreKey) {
        String username = hiscoreKey.getUsername();
        HiscoreEndpoint endpoint = hiscoreKey.getType();
        try {
            HiscoreResult result = this.hiscoreClient.lookup(username, endpoint);
            if (result == null) {
                return HiscoreManager.NONE;
            }
            return result;
        }
        catch (IOException ex) {
            log.warn("Unable to look up hiscore!", (Throwable)ex);
            return HiscoreManager.NONE;
        }
    }
}

