/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.Gson
 *  javax.inject.Inject
 *  javax.inject.Named
 *  net.runelite.http.api.RuneLiteAPI
 *  net.runelite.http.api.ge.GrandExchangeTrade
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
package net.runelite.client.plugins.grandexchange;

import com.google.gson.Gson;
import java.io.IOException;
import java.util.UUID;
import javax.inject.Inject;
import javax.inject.Named;
import net.runelite.http.api.RuneLiteAPI;
import net.runelite.http.api.ge.GrandExchangeTrade;
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

public class GrandExchangeClient {
    private static final Logger log = LoggerFactory.getLogger(GrandExchangeClient.class);
    private final OkHttpClient client;
    private final HttpUrl apiBase;
    private final Gson gson;
    private UUID uuid;
    private String machineId;

    @Inject
    private GrandExchangeClient(OkHttpClient client, @Named(value="runelite.api.base") HttpUrl apiBase, Gson gson) {
        this.client = client;
        this.apiBase = apiBase;
        this.gson = gson;
    }

    public void submit(GrandExchangeTrade grandExchangeTrade) {
        HttpUrl url = this.apiBase.newBuilder().addPathSegment("ge").build();
        Request.Builder builder = new Request.Builder();
        if (this.uuid != null) {
            builder.header("RUNELITE-AUTH", this.uuid.toString());
        }
        if (this.machineId != null) {
            builder.header("RUNELITE-MACHINEID", this.machineId);
        }
        Request request = builder.post(RequestBody.create((MediaType)RuneLiteAPI.JSON, (String)this.gson.toJson((Object)grandExchangeTrade))).url(url).build();
        this.client.newCall(request).enqueue(new Callback(){

            public void onFailure(Call call, IOException e) {
                log.debug("unable to submit trade", (Throwable)e);
            }

            public void onResponse(Call call, Response response) {
                log.debug("Submitted trade");
                response.close();
            }
        });
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public void setMachineId(String machineId) {
        this.machineId = machineId;
    }
}

