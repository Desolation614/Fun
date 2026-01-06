/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  okhttp3.HttpUrl
 *  okhttp3.OkHttpClient
 *  okhttp3.Request
 *  okhttp3.Request$Builder
 *  okhttp3.Response
 */
package net.runelite.client.rs;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import net.runelite.client.rs.RSConfig;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

class ClientConfigLoader {
    private final OkHttpClient okHttpClient;

    RSConfig fetch(HttpUrl url) throws IOException {
        Request request = new Request.Builder().url(url).build();
        RSConfig config = new RSConfig();
        try (Response response = this.okHttpClient.newCall(request).execute();){
            String str;
            if (!response.isSuccessful()) {
                throw new IOException("Unsuccessful response: " + response.message());
            }
            BufferedReader in = new BufferedReader(new InputStreamReader(response.body().byteStream(), StandardCharsets.UTF_8));
            block13: while ((str = in.readLine()) != null) {
                String s;
                int idx = str.indexOf(61);
                if (idx == -1) continue;
                switch (s = str.substring(0, idx)) {
                    case "param": {
                        str = str.substring(idx + 1);
                        idx = str.indexOf(61);
                        s = str.substring(0, idx);
                        config.getAppletProperties().put(s, str.substring(idx + 1));
                        continue block13;
                    }
                    case "msg": {
                        continue block13;
                    }
                }
                config.getClassLoaderProperties().put(s, str.substring(idx + 1));
            }
        }
        return config;
    }

    public ClientConfigLoader(OkHttpClient okHttpClient) {
        this.okHttpClient = okHttpClient;
    }
}

