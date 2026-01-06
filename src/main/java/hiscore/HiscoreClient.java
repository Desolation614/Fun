/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableMap$Builder
 *  com.google.gson.Gson
 *  com.google.gson.JsonSyntaxException
 *  javax.inject.Inject
 *  javax.inject.Singleton
 *  okhttp3.Call
 *  okhttp3.Callback
 *  okhttp3.HttpUrl
 *  okhttp3.OkHttpClient
 *  okhttp3.Request
 *  okhttp3.Request$Builder
 *  okhttp3.Response
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package net.runelite.client.hiscore;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.inject.Inject;
import javax.inject.Singleton;
import net.runelite.client.hiscore.HiscoreEndpoint;
import net.runelite.client.hiscore.HiscoreResponse;
import net.runelite.client.hiscore.HiscoreResult;
import net.runelite.client.hiscore.HiscoreSkill;
import net.runelite.client.hiscore.Skill;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class HiscoreClient {
    private static final Logger log = LoggerFactory.getLogger(HiscoreClient.class);
    private final OkHttpClient client;
    private final Gson gson;

    @Inject
    private HiscoreClient(OkHttpClient client, Gson gson) {
        this.client = client;
        this.gson = gson;
    }

    public HiscoreResult lookup(String username) throws IOException {
        return this.lookup(username, HiscoreEndpoint.NORMAL);
    }

    public HiscoreResult lookup(String username, HiscoreEndpoint endpoint) throws IOException {
        return this.lookup(username, endpoint.getHiscoreURL());
    }

    private HiscoreResult lookup(String username, HttpUrl url) throws IOException {
        try (Response response = this.client.newCall(HiscoreClient.buildRequest(username, url)).execute();){
            HiscoreResult hiscoreResult = this.processResponse(username, response);
            return hiscoreResult;
        }
    }

    public CompletableFuture<HiscoreResult> lookupAsync(final String username, HiscoreEndpoint endpoint) {
        final CompletableFuture<HiscoreResult> future = new CompletableFuture<HiscoreResult>();
        this.client.newCall(HiscoreClient.buildRequest(username, endpoint.getHiscoreURL())).enqueue(new Callback(){

            public void onFailure(Call call, IOException e) {
                future.completeExceptionally(e);
            }

            public void onResponse(Call call, Response response) throws IOException {
                try (Response response2 = response;){
                    future.complete(HiscoreClient.this.processResponse(username, response));
                }
            }
        });
        return future;
    }

    private static Request buildRequest(String username, HttpUrl hiscoreUrl) {
        HttpUrl url = hiscoreUrl.newBuilder().addQueryParameter("player", username).build();
        log.debug("Built URL {}", (Object)url);
        return new Request.Builder().url(url).build();
    }

    private HiscoreResult processResponse(String username, Response response) throws IOException {
        HiscoreSkill s;
        HiscoreResponse hiscoreResponse;
        if (!response.isSuccessful()) {
            if (response.code() == 404) {
                return null;
            }
            throw new IOException("Error retrieving data from hiscores: " + String.valueOf(response));
        }
        try {
            hiscoreResponse = (HiscoreResponse)this.gson.fromJson(response.body().charStream(), HiscoreResponse.class);
        }
        catch (JsonSyntaxException ex) {
            throw new IOException("Error deserializing hiscore response", ex);
        }
        if (hiscoreResponse == null) {
            throw new IOException("Error retrieving data from hiscores: " + String.valueOf(response));
        }
        Map skillMap = Arrays.stream(HiscoreSkill.values()).collect(Collectors.toMap(HiscoreSkill::getName, Function.identity()));
        ImmutableMap.Builder skills = ImmutableMap.builder();
        for (HiscoreResponse.Skill skill : hiscoreResponse.skills) {
            s = (HiscoreSkill)((Object)skillMap.get(skill.name));
            if (s == null) {
                log.debug("unknown skill in hiscore: {}", (Object)skill.name);
                continue;
            }
            skills.put((Object)s, (Object)new Skill(skill.rank, skill.level, skill.xp));
        }
        for (HiscoreResponse.Activity activity : hiscoreResponse.activities) {
            s = (HiscoreSkill)((Object)skillMap.get(activity.name));
            if (s == null) {
                log.debug("unknown activity in hiscore: {}", (Object)activity.name);
                continue;
            }
            skills.put((Object)s, (Object)new Skill(activity.rank, (int)activity.score, -1L));
        }
        return new HiscoreResult(username, (Map<HiscoreSkill, Skill>)skills.build());
    }
}

