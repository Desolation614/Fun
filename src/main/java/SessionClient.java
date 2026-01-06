/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.Gson
 *  com.google.gson.JsonParseException
 *  javax.inject.Inject
 *  javax.inject.Named
 *  okhttp3.HttpUrl
 *  okhttp3.OkHttpClient
 *  okhttp3.Request
 *  okhttp3.Request$Builder
 *  okhttp3.RequestBody
 *  okhttp3.Response
 *  okhttp3.ResponseBody
 */
package net.runelite.client;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import javax.inject.Inject;
import javax.inject.Named;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

class SessionClient {
    private final OkHttpClient client;
    private final HttpUrl sessionUrl;
    private final Gson gson;

    @Inject
    private SessionClient(OkHttpClient client, @Named(value="runelite.session") HttpUrl sessionUrl, Gson gson) {
        this.client = client;
        this.sessionUrl = sessionUrl;
        this.gson = gson;
    }

    UUID open() throws IOException {
        UUID uUID;
        block8: {
            HttpUrl url = this.sessionUrl.newBuilder().build();
            Request request = new Request.Builder().post(RequestBody.create(null, (byte[])new byte[0])).url(url).build();
            Response response = this.client.newCall(request).execute();
            try {
                ResponseBody body = response.body();
                InputStream in = body.byteStream();
                uUID = (UUID)this.gson.fromJson((Reader)new InputStreamReader(in, StandardCharsets.UTF_8), UUID.class);
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
                catch (JsonParseException | IllegalArgumentException ex) {
                    throw new IOException(ex);
                }
            }
            response.close();
        }
        return uUID;
    }

    void ping(UUID uuid, boolean loggedIn) throws IOException {
        HttpUrl url = this.sessionUrl.newBuilder().addPathSegment("ping").addQueryParameter("session", uuid.toString()).addQueryParameter("logged-in", String.valueOf(loggedIn)).build();
        Request request = new Request.Builder().post(RequestBody.create(null, (byte[])new byte[0])).url(url).build();
        try (Response response = this.client.newCall(request).execute();){
            if (!response.isSuccessful()) {
                throw new IOException("Unsuccessful ping");
            }
        }
    }

    void delete(UUID uuid) throws IOException {
        HttpUrl url = this.sessionUrl.newBuilder().addQueryParameter("session", uuid.toString()).build();
        Request request = new Request.Builder().delete().url(url).build();
        this.client.newCall(request).execute().close();
    }
}

