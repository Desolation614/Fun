/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonParseException
 *  net.runelite.http.api.RuneLiteAPI
 *  net.runelite.http.api.worlds.WorldResult
 *  okhttp3.HttpUrl
 *  okhttp3.OkHttpClient
 *  okhttp3.Request
 *  okhttp3.Request$Builder
 *  okhttp3.Response
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package net.runelite.client.game;

import com.google.gson.JsonParseException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import net.runelite.http.api.RuneLiteAPI;
import net.runelite.http.api.worlds.WorldResult;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WorldClient {
    private static final Logger log = LoggerFactory.getLogger(WorldClient.class);
    private final OkHttpClient client;
    private final HttpUrl apiBase;

    public WorldResult lookupWorlds() throws IOException {
        WorldResult worldResult;
        block9: {
            HttpUrl url = this.apiBase.newBuilder().addPathSegment("ferox").addPathSegment("worlds").addPathSegment("json").build();
            log.debug("Built URI: {}", (Object)url);
            Request request = new Request.Builder().url(url).build();
            Response response = this.client.newCall(request).execute();
            try {
                if (!response.isSuccessful()) {
                    log.debug("Error looking up worlds: {}", (Object)response);
                    throw new IOException("unsuccessful response looking up worlds");
                }
                InputStream in = response.body().byteStream();
                worldResult = (WorldResult)RuneLiteAPI.GSON.fromJson((Reader)new InputStreamReader(in, StandardCharsets.UTF_8), WorldResult.class);
                if (response == null) break block9;
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
        return worldResult;
    }

    public WorldClient(OkHttpClient client, HttpUrl apiBase) {
        this.client = client;
        this.apiBase = apiBase;
    }
}

