/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.reflect.TypeToken
 *  com.google.gson.Gson
 *  com.google.gson.JsonSyntaxException
 *  javax.inject.Inject
 *  javax.inject.Named
 *  net.runelite.http.api.RuneLiteAPI
 *  okhttp3.Call
 *  okhttp3.Callback
 *  okhttp3.HttpUrl
 *  okhttp3.MediaType
 *  okhttp3.OkHttpClient
 *  okhttp3.Request
 *  okhttp3.Request$Builder
 *  okhttp3.RequestBody
 *  okhttp3.Response
 *  okio.BufferedSource
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package net.runelite.client.externalplugins;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.Signature;
import java.security.SignatureException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.util.List;
import java.util.Map;
import javax.imageio.ImageIO;
import javax.inject.Inject;
import javax.inject.Named;
import net.runelite.client.RuneLiteProperties;
import net.runelite.client.externalplugins.PluginHubManifest;
import net.runelite.client.util.VerificationException;
import net.runelite.http.api.RuneLiteAPI;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.BufferedSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExternalPluginClient {
    private static final Logger log = LoggerFactory.getLogger(ExternalPluginClient.class);
    private final OkHttpClient okHttpClient;
    private final Gson gson;
    private final HttpUrl apiBase;
    private final HttpUrl pluginHubBase;

    @Inject
    private ExternalPluginClient(OkHttpClient okHttpClient, Gson gson, @Named(value="runelite.api.base") HttpUrl apiBase, @Named(value="runelite.pluginhub.url") HttpUrl pluginHubBase) {
        this.okHttpClient = okHttpClient;
        this.gson = gson;
        this.apiBase = apiBase;
        this.pluginHubBase = pluginHubBase;
    }

    public PluginHubManifest.ManifestLite downloadManifestLite() throws IOException, VerificationException {
        return this.downloadManifest("lite", PluginHubManifest.ManifestLite.class);
    }

    public PluginHubManifest.ManifestFull downloadManifestFull() throws IOException, VerificationException {
        return this.downloadManifest("full", PluginHubManifest.ManifestFull.class);
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    private <T> T downloadManifest(String name, Class<T> clazz) throws IOException, VerificationException {
        HttpUrl manifest = this.pluginHubBase.newBuilder().addPathSegment("manifest").addPathSegment(RuneLiteProperties.getPluginHubVersion() + "_" + name + ".js").build();
        try (Response res = this.okHttpClient.newCall(new Request.Builder().url(manifest).build()).execute();){
            if (res.code() != 200) {
                throw new IOException("Non-OK response code: " + res.code());
            }
            BufferedSource src = res.body().source();
            byte[] signature = new byte[src.readInt()];
            src.readFully(signature);
            byte[] data = src.readByteArray();
            Signature s = Signature.getInstance("SHA256withRSA");
            s.initVerify(ExternalPluginClient.loadCertificate());
            s.update(data);
            if (!s.verify(signature)) {
                throw new VerificationException("Unable to verify external plugin manifest");
            }
            Object object = this.gson.fromJson(new String(data, StandardCharsets.UTF_8), clazz);
            return (T)object;
        }
        catch (InvalidKeyException | NoSuchAlgorithmException | SignatureException e) {
            throw new VerificationException(e);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public BufferedImage downloadIcon(PluginHubManifest.DisplayData plugin) throws IOException {
        if (plugin.getIconHash() == null) {
            return null;
        }
        HttpUrl url = this.pluginHubBase.newBuilder().addPathSegment("icon").addPathSegment(plugin.getInternalName() + "_" + plugin.getIconHash() + ".png").build();
        try (Response res = this.okHttpClient.newCall(new Request.Builder().url(url).build()).execute();){
            byte[] bytes = res.body().bytes();
            Class<ImageIO> clazz = ImageIO.class;
            synchronized (ImageIO.class) {
                BufferedImage bufferedImage = ImageIO.read(new ByteArrayInputStream(bytes));
                // ** MonitorExit[var5_6] (shouldn't be in output)
                return bufferedImage;
            }
        }
    }

    HttpUrl getJarURL(PluginHubManifest.JarData plugin) {
        return this.pluginHubBase.newBuilder().addPathSegment("jar").addPathSegment(plugin.getInternalName() + "_" + plugin.getJarHash() + ".jar").build();
    }

    private static Certificate loadCertificate() {
        Certificate certificate;
        block8: {
            InputStream in = ExternalPluginClient.class.getResourceAsStream("externalplugins.crt");
            try {
                CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
                certificate = certFactory.generateCertificate(in);
                if (in == null) break block8;
            }
            catch (Throwable throwable) {
                try {
                    if (in != null) {
                        try {
                            in.close();
                        }
                        catch (Throwable throwable2) {
                            throwable.addSuppressed(throwable2);
                        }
                    }
                    throw throwable;
                }
                catch (IOException | CertificateException e) {
                    throw new RuntimeException(e);
                }
            }
            in.close();
        }
        return certificate;
    }

    void submitPlugins(List<String> plugins) {
        if (plugins.isEmpty()) {
            return;
        }
        HttpUrl url = this.apiBase.newBuilder().addPathSegment("pluginhub").build();
        Request request = new Request.Builder().url(url).post(RequestBody.create((MediaType)RuneLiteAPI.JSON, (String)this.gson.toJson(plugins))).build();
        this.okHttpClient.newCall(request).enqueue(new Callback(){

            public void onFailure(Call call, IOException e) {
                log.debug("Error submitting plugins", (Throwable)e);
            }

            public void onResponse(Call call, Response response) {
                log.debug("Submitted plugin list");
                response.close();
            }
        });
    }

    public Map<String, Integer> getPluginCounts() throws IOException {
        Map map;
        block9: {
            HttpUrl url = this.apiBase.newBuilder().addPathSegments("pluginhub").build();
            Response res = this.okHttpClient.newCall(new Request.Builder().url(url).build()).execute();
            try {
                if (res.code() != 200) {
                    throw new IOException("Non-OK response code: " + res.code());
                }
                map = (Map)this.gson.fromJson(res.body().string(), new TypeToken<Map<String, Integer>>(){}.getType());
                if (res == null) break block9;
            }
            catch (Throwable throwable) {
                try {
                    if (res != null) {
                        try {
                            res.close();
                        }
                        catch (Throwable throwable2) {
                            throwable.addSuppressed(throwable2);
                        }
                    }
                    throw throwable;
                }
                catch (JsonSyntaxException ex) {
                    throw new IOException(ex);
                }
            }
            res.close();
        }
        return map;
    }
}

