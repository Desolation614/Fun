/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.Gson
 *  javax.inject.Inject
 *  javax.inject.Singleton
 *  okhttp3.Call
 *  okhttp3.Callback
 *  okhttp3.MediaType
 *  okhttp3.OkHttpClient
 *  okhttp3.Request
 *  okhttp3.Request$Builder
 *  okhttp3.RequestBody
 *  okhttp3.Response
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package net.runelite.client.plugins.crowdsourcing;

import com.google.gson.Gson;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class CrowdsourcingManager {
    private static final Logger log = LoggerFactory.getLogger(CrowdsourcingManager.class);
    private static final String CROWDSOURCING_BASE = "https://crowdsource.runescape.wiki/runelite";
    private static final MediaType JSON = MediaType.get((String)"application/json; charset=utf-8");
    @Inject
    private OkHttpClient okHttpClient;
    @Inject
    private Gson gson;
    private List<Object> data = new ArrayList<Object>();

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void storeEvent(Object event) {
        CrowdsourcingManager crowdsourcingManager = this;
        synchronized (crowdsourcingManager) {
            this.data.add(event);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void submitToAPI() {
        List<Object> temp;
        CrowdsourcingManager crowdsourcingManager = this;
        synchronized (crowdsourcingManager) {
            if (this.data.isEmpty()) {
                return;
            }
            temp = this.data;
            this.data = new ArrayList<Object>();
        }
        Request r = new Request.Builder().url(CROWDSOURCING_BASE).post(RequestBody.create((MediaType)JSON, (String)this.gson.toJson(temp))).build();
        this.okHttpClient.newCall(r).enqueue(new Callback(){

            public void onFailure(Call call, IOException e) {
                log.debug("Error sending crowdsourcing data", (Throwable)e);
            }

            public void onResponse(Call call, Response response) {
                log.debug("Successfully sent crowdsourcing data");
                response.close();
            }
        });
    }
}

