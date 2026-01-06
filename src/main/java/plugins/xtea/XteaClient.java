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
 *  net.runelite.http.api.xtea.XteaKey
 *  net.runelite.http.api.xtea.XteaRequest
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
package net.runelite.client.plugins.xtea;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Named;
import net.runelite.http.api.RuneLiteAPI;
import net.runelite.http.api.xtea.XteaKey;
import net.runelite.http.api.xtea.XteaRequest;
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

public class XteaClient {
    private static final Logger log = LoggerFactory.getLogger(XteaClient.class);
    private final OkHttpClient client;
    private final HttpUrl apiBase;
    private final Gson gson;

    @Inject
    private XteaClient(OkHttpClient client, @Named(value="runelite.api.base") HttpUrl apiBase, Gson gson) {
        this.client = client;
        this.apiBase = apiBase;
        this.gson = gson;
    }

    public void submit(XteaRequest xteaRequest) {
        HttpUrl url = this.apiBase.newBuilder().addPathSegment("xtea").build();
        log.debug("Built URI: {}", (Object)url);
        Request request = new Request.Builder().post(RequestBody.create((MediaType)RuneLiteAPI.JSON, (String)this.gson.toJson((Object)xteaRequest))).url(url).build();
        this.client.newCall(request).enqueue(new Callback(){

            public void onFailure(Call call, IOException e) {
                log.warn("unable to submit xtea keys", (Throwable)e);
            }

            public void onResponse(Call call, Response response) {
                try (Response response2 = response;){
                    if (!response.isSuccessful()) {
                        log.debug("unsuccessful xtea response");
                    }
                }
            }
        });
    }

    public List<XteaKey> get() throws IOException {
        List list;
        block8: {
            HttpUrl url = this.apiBase.newBuilder().addPathSegment("xtea").build();
            Request request = new Request.Builder().url(url).build();
            Response response = this.client.newCall(request).execute();
            try {
                InputStream in = response.body().byteStream();
                list = (List)this.gson.fromJson((Reader)new InputStreamReader(in, StandardCharsets.UTF_8), new TypeToken<List<XteaKey>>(){}.getType());
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
        return list;
    }

    public XteaKey get(int region) throws IOException {
        XteaKey xteaKey;
        block8: {
            HttpUrl url = this.apiBase.newBuilder().addPathSegment("xtea").addPathSegment(Integer.toString(region)).build();
            Request request = new Request.Builder().url(url).build();
            Response response = this.client.newCall(request).execute();
            try {
                InputStream in = response.body().byteStream();
                xteaKey = (XteaKey)this.gson.fromJson((Reader)new InputStreamReader(in, StandardCharsets.UTF_8), XteaKey.class);
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
        return xteaKey;
    }
}

