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
 *  net.runelite.http.api.chat.Duels
 *  net.runelite.http.api.chat.LayoutRoom
 *  net.runelite.http.api.chat.Task
 *  okhttp3.HttpUrl
 *  okhttp3.MediaType
 *  okhttp3.OkHttpClient
 *  okhttp3.Request
 *  okhttp3.Request$Builder
 *  okhttp3.RequestBody
 *  okhttp3.Response
 */
package net.runelite.client.chat;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Set;
import javax.inject.Inject;
import javax.inject.Named;
import net.runelite.http.api.RuneLiteAPI;
import net.runelite.http.api.chat.Duels;
import net.runelite.http.api.chat.LayoutRoom;
import net.runelite.http.api.chat.Task;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ChatClient {
    private final OkHttpClient client;
    private final HttpUrl apiBase;
    private final Gson gson;

    @Inject
    private ChatClient(OkHttpClient client, @Named(value="runelite.api.base") HttpUrl apiBase, Gson gson) {
        this.client = client;
        this.apiBase = apiBase;
        this.gson = gson;
    }

    public boolean submitKc(String username, String boss, int kc) throws IOException {
        HttpUrl url = this.apiBase.newBuilder().addPathSegment("chat").addPathSegment("kc").addQueryParameter("name", username).addQueryParameter("boss", boss).addQueryParameter("kc", Integer.toString(kc)).build();
        Request request = new Request.Builder().post(RequestBody.create(null, (byte[])new byte[0])).url(url).build();
        try (Response response = this.client.newCall(request).execute();){
            boolean bl = response.isSuccessful();
            return bl;
        }
    }

    public int getKc(String username, String boss) throws IOException {
        HttpUrl url = this.apiBase.newBuilder().addPathSegment("chat").addPathSegment("kc").addQueryParameter("name", username).addQueryParameter("boss", boss).build();
        Request request = new Request.Builder().url(url).build();
        try (Response response = this.client.newCall(request).execute();){
            if (!response.isSuccessful()) {
                throw new IOException("Unable to look up killcount!");
            }
            int n = Integer.parseInt(response.body().string());
            return n;
        }
    }

    public boolean submitQp(String username, int qp) throws IOException {
        HttpUrl url = this.apiBase.newBuilder().addPathSegment("chat").addPathSegment("qp").addQueryParameter("name", username).addQueryParameter("qp", Integer.toString(qp)).build();
        Request request = new Request.Builder().post(RequestBody.create(null, (byte[])new byte[0])).url(url).build();
        try (Response response = this.client.newCall(request).execute();){
            boolean bl = response.isSuccessful();
            return bl;
        }
    }

    public int getQp(String username) throws IOException {
        HttpUrl url = this.apiBase.newBuilder().addPathSegment("chat").addPathSegment("qp").addQueryParameter("name", username).build();
        Request request = new Request.Builder().url(url).build();
        try (Response response = this.client.newCall(request).execute();){
            if (!response.isSuccessful()) {
                throw new IOException("Unable to look up quest points!");
            }
            int n = Integer.parseInt(response.body().string());
            return n;
        }
    }

    public boolean submitTask(String username, String task, int amount, int initialAmount, String location) throws IOException {
        HttpUrl url = this.apiBase.newBuilder().addPathSegment("chat").addPathSegment("task").addQueryParameter("name", username).addQueryParameter("task", task).addQueryParameter("amount", Integer.toString(amount)).addQueryParameter("initialAmount", Integer.toString(initialAmount)).addQueryParameter("location", location).build();
        Request request = new Request.Builder().post(RequestBody.create(null, (byte[])new byte[0])).url(url).build();
        try (Response response = this.client.newCall(request).execute();){
            boolean bl = response.isSuccessful();
            return bl;
        }
    }

    public Task getTask(String username) throws IOException {
        Task task;
        block9: {
            HttpUrl url = this.apiBase.newBuilder().addPathSegment("chat").addPathSegment("task").addQueryParameter("name", username).build();
            Request request = new Request.Builder().url(url).build();
            Response response = this.client.newCall(request).execute();
            try {
                if (!response.isSuccessful()) {
                    throw new IOException("Unable to look up task!");
                }
                InputStream in = response.body().byteStream();
                task = (Task)this.gson.fromJson((Reader)new InputStreamReader(in, StandardCharsets.UTF_8), Task.class);
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
        return task;
    }

    public boolean submitPb(String username, String boss, double pb) throws IOException {
        HttpUrl url = this.apiBase.newBuilder().addPathSegment("chat").addPathSegment("pb").addQueryParameter("name", username).addQueryParameter("boss", boss).addQueryParameter("pb", Double.toString(pb)).build();
        Request request = new Request.Builder().post(RequestBody.create(null, (byte[])new byte[0])).url(url).build();
        try (Response response = this.client.newCall(request).execute();){
            boolean bl = response.isSuccessful();
            return bl;
        }
    }

    public double getPb(String username, String boss) throws IOException {
        HttpUrl url = this.apiBase.newBuilder().addPathSegment("chat").addPathSegment("pb").addQueryParameter("name", username).addQueryParameter("boss", boss).build();
        Request request = new Request.Builder().url(url).build();
        try (Response response = this.client.newCall(request).execute();){
            if (!response.isSuccessful()) {
                throw new IOException("Unable to look up personal best!");
            }
            double d = Double.parseDouble(response.body().string());
            return d;
        }
    }

    public boolean submitGc(String username, int gc) throws IOException {
        HttpUrl url = this.apiBase.newBuilder().addPathSegment("chat").addPathSegment("gc").addQueryParameter("name", username).addQueryParameter("gc", Integer.toString(gc)).build();
        Request request = new Request.Builder().post(RequestBody.create(null, (byte[])new byte[0])).url(url).build();
        try (Response response = this.client.newCall(request).execute();){
            boolean bl = response.isSuccessful();
            return bl;
        }
    }

    public int getGc(String username) throws IOException {
        HttpUrl url = this.apiBase.newBuilder().addPathSegment("chat").addPathSegment("gc").addQueryParameter("name", username).build();
        Request request = new Request.Builder().url(url).build();
        try (Response response = this.client.newCall(request).execute();){
            if (!response.isSuccessful()) {
                throw new IOException("Unable to look up gamble count!");
            }
            int n = Integer.parseInt(response.body().string());
            return n;
        }
    }

    public boolean submitDuels(String username, int wins, int losses, int winningStreak, int losingStreak) throws IOException {
        HttpUrl url = this.apiBase.newBuilder().addPathSegment("chat").addPathSegment("duels").addQueryParameter("name", username).addQueryParameter("wins", Integer.toString(wins)).addQueryParameter("losses", Integer.toString(losses)).addQueryParameter("winningStreak", Integer.toString(winningStreak)).addQueryParameter("losingStreak", Integer.toString(losingStreak)).build();
        Request request = new Request.Builder().post(RequestBody.create(null, (byte[])new byte[0])).url(url).build();
        try (Response response = this.client.newCall(request).execute();){
            boolean bl = response.isSuccessful();
            return bl;
        }
    }

    public Duels getDuels(String username) throws IOException {
        Duels duels;
        block9: {
            HttpUrl url = this.apiBase.newBuilder().addPathSegment("chat").addPathSegment("duels").addQueryParameter("name", username).build();
            Request request = new Request.Builder().url(url).build();
            Response response = this.client.newCall(request).execute();
            try {
                if (!response.isSuccessful()) {
                    throw new IOException("Unable to look up duels!");
                }
                InputStream in = response.body().byteStream();
                duels = (Duels)this.gson.fromJson((Reader)new InputStreamReader(in, StandardCharsets.UTF_8), Duels.class);
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
        return duels;
    }

    public boolean submitLayout(String username, LayoutRoom[] rooms) throws IOException {
        HttpUrl url = this.apiBase.newBuilder().addPathSegment("chat").addPathSegment("layout").addQueryParameter("name", username).build();
        Request request = new Request.Builder().post(RequestBody.create((MediaType)RuneLiteAPI.JSON, (String)this.gson.toJson((Object)rooms))).url(url).build();
        try (Response response = this.client.newCall(request).execute();){
            boolean bl = response.isSuccessful();
            return bl;
        }
    }

    public LayoutRoom[] getLayout(String username) throws IOException {
        LayoutRoom[] layoutRoomArray;
        block9: {
            HttpUrl url = this.apiBase.newBuilder().addPathSegment("chat").addPathSegment("layout").addQueryParameter("name", username).build();
            Request request = new Request.Builder().url(url).build();
            Response response = this.client.newCall(request).execute();
            try {
                if (!response.isSuccessful()) {
                    throw new IOException("Unable to look up layout!");
                }
                InputStream in = response.body().byteStream();
                layoutRoomArray = (LayoutRoom[])this.gson.fromJson((Reader)new InputStreamReader(in, StandardCharsets.UTF_8), LayoutRoom[].class);
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
        return layoutRoomArray;
    }

    public boolean submitPetList(String username, Collection<Integer> petList) throws IOException {
        HttpUrl url = this.apiBase.newBuilder().addPathSegment("chat").addPathSegment("pets").addQueryParameter("name", username).build();
        Request request = new Request.Builder().post(RequestBody.create((MediaType)RuneLiteAPI.JSON, (String)this.gson.toJson(petList))).url(url).build();
        try (Response response = this.client.newCall(request).execute();){
            boolean bl = response.isSuccessful();
            return bl;
        }
    }

    public Set<Integer> getPetList(String username) throws IOException {
        Set set;
        block9: {
            HttpUrl url = this.apiBase.newBuilder().addPathSegment("chat").addPathSegment("pets").addQueryParameter("name", username).build();
            Request request = new Request.Builder().url(url).build();
            Response response = this.client.newCall(request).execute();
            try {
                if (!response.isSuccessful()) {
                    throw new IOException("Unable to look up pet list!");
                }
                InputStream in = response.body().byteStream();
                set = (Set)this.gson.fromJson((Reader)new InputStreamReader(in, StandardCharsets.UTF_8), new TypeToken<Set<Integer>>(){}.getType());
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
        return set;
    }
}

