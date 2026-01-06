/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.Gson
 *  com.google.gson.JsonParseException
 *  com.google.gson.reflect.TypeToken
 *  javax.inject.Inject
 *  javax.inject.Named
 *  net.runelite.http.api.RuneLiteAPI
 *  net.runelite.http.api.config.ConfigPatch
 *  net.runelite.http.api.config.ConfigPatchResult
 *  net.runelite.http.api.config.Configuration
 *  net.runelite.http.api.config.Profile
 *  okhttp3.Call
 *  okhttp3.Callback
 *  okhttp3.HttpUrl
 *  okhttp3.MediaType
 *  okhttp3.OkHttpClient
 *  okhttp3.Request
 *  okhttp3.Request$Builder
 *  okhttp3.RequestBody
 *  okhttp3.Response
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package net.runelite.client.config;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import javax.inject.Inject;
import javax.inject.Named;
import net.runelite.http.api.RuneLiteAPI;
import net.runelite.http.api.config.ConfigPatch;
import net.runelite.http.api.config.ConfigPatchResult;
import net.runelite.http.api.config.Configuration;
import net.runelite.http.api.config.Profile;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfigClient {
    private static final Logger log = LoggerFactory.getLogger(ConfigClient.class);
    private final OkHttpClient client;
    private final HttpUrl apiBase;
    private final Gson gson;
    private UUID uuid;

    @Inject
    private ConfigClient(OkHttpClient client, @Named(value="runelite.api.base") HttpUrl apiBase, Gson gson) {
        this.client = client;
        this.apiBase = apiBase;
        this.gson = gson;
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public List<Profile> profiles() throws IOException {
        HttpUrl url = this.apiBase.newBuilder().addPathSegment("config").addPathSegment("v3").addPathSegment("list").build();
        log.debug("Built URI: {}", (Object)url);
        Request request = new Request.Builder().header("RUNELITE-AUTH", this.uuid.toString()).url(url).build();
        try (Response response = this.client.newCall(request).execute();){
            if (!response.isSuccessful()) {
                log.error("non-successful response loading profiles: {}", (Object)response.code());
                List<Profile> list2 = null;
                return list2;
            }
            InputStream in = response.body().byteStream();
            Type type = new TypeToken<List<Profile>>(){}.getType();
            List list = (List)this.gson.fromJson((Reader)new InputStreamReader(in, StandardCharsets.UTF_8), type);
            return list;
        }
        catch (JsonParseException ex) {
            throw new IOException(ex);
        }
    }

    public Configuration get(long profile) throws IOException {
        Configuration configuration;
        block8: {
            HttpUrl url = this.apiBase.newBuilder().addPathSegment("config").addPathSegment("v3").addPathSegment(Long.toString(profile)).build();
            log.debug("Built URI: {}", (Object)url);
            Request request = new Request.Builder().header("RUNELITE-AUTH", this.uuid.toString()).url(url).build();
            Response response = this.client.newCall(request).execute();
            try {
                InputStream in = response.body().byteStream();
                configuration = (Configuration)this.gson.fromJson((Reader)new InputStreamReader(in, StandardCharsets.UTF_8), Configuration.class);
                if (response == null) break block8;
            }
            catch (Throwable throwable) {
                try {
                    if (response != null) {
                        try {
                            response.close();
                        }
                        catch (Throwable throwable2) {
                            throwable.addSuppressed(throwable2);
                        }
                    }
                    throw throwable;
                }
                catch (JsonParseException ex) {
                    throw new IOException(ex);
                }
            }
            response.close();
        }
        return configuration;
    }

    public CompletableFuture<ConfigPatchResult> patch(final ConfigPatch patch, long profile) {
        HttpUrl url = this.apiBase.newBuilder().addPathSegment("config").addPathSegment("v3").addPathSegment(Long.toString(profile)).build();
        log.debug("Built URI: {}", (Object)url);
        Request request = new Request.Builder().patch(RequestBody.create((MediaType)RuneLiteAPI.JSON, (String)this.gson.toJson((Object)patch))).header("RUNELITE-AUTH", this.uuid.toString()).url(url).build();
        final CompletableFuture<ConfigPatchResult> future = new CompletableFuture<ConfigPatchResult>();
        this.client.newCall(request).enqueue(new Callback(){

            public void onFailure(Call call, IOException e) {
                log.warn("Unable to synchronize configuration item", (Throwable)e);
                future.completeExceptionally(e);
            }

            public void onResponse(Call call, Response response) {
                block11: {
                    try (Response response2 = response;){
                        if (response.code() != 200) {
                            String body = "bad response";
                            try {
                                body = response.body().string();
                            }
                            catch (IOException iOException) {
                                // empty catch block
                            }
                            log.warn("failed to synchronize some of {}/{} configuration values: {}", new Object[]{patch.getEdit().size(), patch.getUnset().size(), body});
                            future.complete(null);
                            break block11;
                        }
                        log.debug("Synchronized {}/{} configuration values", (Object)patch.getEdit().size(), (Object)patch.getUnset().size());
                        future.complete((ConfigPatchResult)ConfigClient.this.gson.fromJson((Reader)new InputStreamReader(response.body().byteStream(), StandardCharsets.UTF_8), ConfigPatchResult.class));
                    }
                    catch (Exception ex) {
                        future.completeExceptionally(ex);
                    }
                }
            }
        });
        return future;
    }

    public void delete(final long profile) {
        HttpUrl url = this.apiBase.newBuilder().addPathSegment("config").addPathSegment("v3").addPathSegment(Long.toString(profile)).build();
        log.debug("Built URI: {}", (Object)url);
        Request request = new Request.Builder().delete().header("RUNELITE-AUTH", this.uuid.toString()).url(url).build();
        this.client.newCall(request).enqueue(new Callback(){

            public void onFailure(Call call, IOException e) {
                log.warn("error deleting profile {}", (Object)profile, (Object)e);
            }

            public void onResponse(Call call, Response response) {
                log.debug("deleted profile {}", (Object)profile);
                response.close();
            }
        });
    }

    public void rename(final long profile, final String name) {
        HttpUrl url = this.apiBase.newBuilder().addPathSegment("config").addPathSegment("v3").addPathSegment(Long.toString(profile)).addPathSegment("name").build();
        log.debug("Built URI: {}", (Object)url);
        Request request = new Request.Builder().post(RequestBody.create(null, (String)name)).header("RUNELITE-AUTH", this.uuid.toString()).url(url).build();
        this.client.newCall(request).enqueue(new Callback(){

            public void onFailure(Call call, IOException e) {
                log.warn("error renaming profile {}", (Object)profile, (Object)e);
            }

            public void onResponse(Call call, Response response) {
                if (!response.isSuccessful()) {
                    log.debug("unable to rename profile {} to {}", (Object)profile, (Object)name);
                } else {
                    log.debug("renamed profile {} to {}", (Object)profile, (Object)name);
                }
                response.close();
            }
        });
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }
}

