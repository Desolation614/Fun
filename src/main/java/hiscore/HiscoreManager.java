/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.cache.CacheBuilder
 *  com.google.common.cache.CacheLoader
 *  com.google.common.cache.LoadingCache
 *  com.google.common.collect.ImmutableMap
 *  javax.inject.Inject
 *  javax.inject.Singleton
 */
package net.runelite.client.hiscore;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableMap;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;
import javax.inject.Singleton;
import net.runelite.client.hiscore.HiscoreClient;
import net.runelite.client.hiscore.HiscoreEndpoint;
import net.runelite.client.hiscore.HiscoreLoader;
import net.runelite.client.hiscore.HiscoreResult;
import net.runelite.client.hiscore.HiscoreSkill;
import net.runelite.client.hiscore.Skill;

@Singleton
public class HiscoreManager {
    static final HiscoreResult EMPTY = new HiscoreResult(null, (Map<HiscoreSkill, Skill>)ImmutableMap.of());
    static final HiscoreResult NONE = new HiscoreResult(null, (Map<HiscoreSkill, Skill>)ImmutableMap.of());
    private final LoadingCache<HiscoreKey, HiscoreResult> hiscoreCache;
    private final HiscoreClient hiscoreClient;

    @Inject
    private HiscoreManager(ScheduledExecutorService executor, HiscoreClient hiscoreClient) {
        this.hiscoreClient = hiscoreClient;
        this.hiscoreCache = CacheBuilder.newBuilder().maximumSize(128L).expireAfterWrite(1L, TimeUnit.HOURS).build((CacheLoader)new HiscoreLoader(executor, hiscoreClient));
    }

    public HiscoreResult lookup(String username, HiscoreEndpoint endpoint) throws IOException {
        HiscoreKey hiscoreKey = new HiscoreKey(username, endpoint);
        HiscoreResult hiscoreResult = (HiscoreResult)this.hiscoreCache.getIfPresent((Object)hiscoreKey);
        if (hiscoreResult != null && hiscoreResult != EMPTY) {
            return hiscoreResult == NONE ? null : hiscoreResult;
        }
        hiscoreResult = this.hiscoreClient.lookup(username, endpoint);
        if (hiscoreResult == null) {
            this.hiscoreCache.put((Object)hiscoreKey, (Object)NONE);
            return null;
        }
        this.hiscoreCache.put((Object)hiscoreKey, (Object)hiscoreResult);
        return hiscoreResult;
    }

    public HiscoreResult lookupAsync(String username, HiscoreEndpoint endpoint) {
        HiscoreKey hiscoreKey = new HiscoreKey(username, endpoint);
        HiscoreResult hiscoreResult = (HiscoreResult)this.hiscoreCache.getIfPresent((Object)hiscoreKey);
        if (hiscoreResult != null && hiscoreResult != EMPTY) {
            return hiscoreResult == NONE ? null : hiscoreResult;
        }
        this.hiscoreCache.refresh((Object)hiscoreKey);
        return null;
    }

    static class HiscoreKey {
        String username;
        HiscoreEndpoint type;

        public HiscoreKey(String username, HiscoreEndpoint type) {
            this.username = username;
            this.type = type;
        }

        public String getUsername() {
            return this.username;
        }

        public HiscoreEndpoint getType() {
            return this.type;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public void setType(HiscoreEndpoint type) {
            this.type = type;
        }

        public boolean equals(Object o) {
            if (o == this) {
                return true;
            }
            if (!(o instanceof HiscoreKey)) {
                return false;
            }
            HiscoreKey other = (HiscoreKey)o;
            if (!other.canEqual(this)) {
                return false;
            }
            String this$username = this.getUsername();
            String other$username = other.getUsername();
            if (this$username == null ? other$username != null : !this$username.equals(other$username)) {
                return false;
            }
            HiscoreEndpoint this$type = this.getType();
            HiscoreEndpoint other$type = other.getType();
            return !(this$type == null ? other$type != null : !((Object)((Object)this$type)).equals((Object)other$type));
        }

        protected boolean canEqual(Object other) {
            return other instanceof HiscoreKey;
        }

        public int hashCode() {
            int PRIME = 59;
            int result = 1;
            String $username = this.getUsername();
            result = result * 59 + ($username == null ? 43 : $username.hashCode());
            HiscoreEndpoint $type = this.getType();
            result = result * 59 + ($type == null ? 43 : ((Object)((Object)$type)).hashCode());
            return result;
        }

        public String toString() {
            return "HiscoreManager.HiscoreKey(username=" + this.getUsername() + ", type=" + String.valueOf((Object)this.getType()) + ")";
        }
    }
}

