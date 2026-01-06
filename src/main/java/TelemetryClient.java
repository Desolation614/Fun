/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.Gson
 *  net.runelite.http.api.RuneLiteAPI
 *  net.runelite.http.api.telemetry.Telemetry
 *  okhttp3.Call
 *  okhttp3.Callback
 *  okhttp3.HttpUrl
 *  okhttp3.HttpUrl$Builder
 *  okhttp3.MediaType
 *  okhttp3.OkHttpClient
 *  okhttp3.Request
 *  okhttp3.Request$Builder
 *  okhttp3.RequestBody
 *  okhttp3.Response
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package net.runelite.client;

import com.google.gson.Gson;
import com.sun.management.OperatingSystemMXBean;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.management.ManagementFactory;
import java.nio.file.Files;
import java.time.Duration;
import java.util.Collections;
import java.util.Map;
import net.runelite.client.util.OSType;
import net.runelite.http.api.RuneLiteAPI;
import net.runelite.http.api.telemetry.Telemetry;
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

public class TelemetryClient {
    private static final Logger log = LoggerFactory.getLogger(TelemetryClient.class);
    private final OkHttpClient okHttpClient;
    private final Gson gson;
    private final HttpUrl apiBase;

    void submitTelemetry() {
        HttpUrl url = this.apiBase.newBuilder().addPathSegment("telemetry").build();
        Request request = new Request.Builder().url(url).post(RequestBody.create((MediaType)RuneLiteAPI.JSON, (String)this.gson.toJson((Object)TelemetryClient.buildTelemetry()))).build();
        this.okHttpClient.newCall(request).enqueue(new Callback(){

            public void onFailure(Call call, IOException e) {
                log.debug("Error submitting telemetry", (Throwable)e);
            }

            public void onResponse(Call call, Response response) {
                log.debug("Submitted telemetry");
                response.close();
            }
        });
    }

    void submitVmErrors(File logsDir) {
        try {
            long yesterday = System.currentTimeMillis() - Duration.ofDays(1L).toMillis();
            for (File f : logsDir.listFiles()) {
                if (!f.getName().startsWith("jvm_crash_") || !f.getName().endsWith(".log") || f.getName().endsWith("_r.log") || f.lastModified() < yesterday) continue;
                String hsErr = Files.readString(f.toPath());
                String destName = f.getName().substring(0, f.getName().length() - 4) + "_r.log";
                File dest = new File(logsDir, destName);
                if (!f.renameTo(dest)) continue;
                String username = System.getProperty("user.name");
                String home = System.getProperty("user.home");
                hsErr = hsErr.replace(username, "%USERNAME%").replace(home, "%HOME%");
                this.submitError("vm crash", hsErr, Collections.emptyMap());
            }
        }
        catch (Exception ex) {
            log.error("error reporting errors", (Throwable)ex);
        }
    }

    public void submitError(String type, String error, Map<String, String> params) {
        HttpUrl.Builder urlBuilder = this.apiBase.newBuilder().addPathSegment("telemetry").addPathSegment("error").addQueryParameter("type", type).addQueryParameter("osname", System.getProperty("os.name")).addQueryParameter("osver", System.getProperty("os.version")).addQueryParameter("osarch", System.getProperty("os.arch")).addQueryParameter("javaversion", System.getProperty("java.version")).addQueryParameter("javavendor", System.getProperty("java.vendor")).addQueryParameter("cpumodel", TelemetryClient.cpuName());
        params.forEach((arg_0, arg_1) -> ((HttpUrl.Builder)urlBuilder).addQueryParameter(arg_0, arg_1));
        HttpUrl url = urlBuilder.build();
        Request request = new Request.Builder().url(url).post(RequestBody.create((MediaType)MediaType.get((String)"text/plain"), (String)error)).build();
        this.okHttpClient.newCall(request).enqueue(new Callback(){

            public void onFailure(Call call, IOException e) {
                log.debug("Error submitting error", (Throwable)e);
            }

            public void onResponse(Call call, Response response) {
                log.debug("Submitted error");
                response.close();
            }
        });
    }

    private static Telemetry buildTelemetry() {
        Telemetry telemetry = new Telemetry();
        telemetry.setJavaVendor(System.getProperty("java.vendor"));
        telemetry.setJavaVersion(System.getProperty("java.version"));
        telemetry.setOsName(System.getProperty("os.name"));
        telemetry.setOsVersion(System.getProperty("os.version"));
        telemetry.setOsArch(System.getProperty("os.arch"));
        telemetry.setLauncherVersion(System.getProperty("runelite.launcher.version"));
        java.lang.management.OperatingSystemMXBean operatingSystemMXBean = ManagementFactory.getOperatingSystemMXBean();
        if (operatingSystemMXBean instanceof OperatingSystemMXBean) {
            long totalPhysicalMemorySize = ((OperatingSystemMXBean)operatingSystemMXBean).getTotalPhysicalMemorySize();
            telemetry.setTotalMemory(totalPhysicalMemorySize);
        }
        telemetry.setCpuName(TelemetryClient.cpuName());
        return telemetry;
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    private static String cpuName() {
        if (OSType.getOSType() != OSType.Windows) {
            return null;
        }
        try {
            Process p = Runtime.getRuntime().exec("wmic cpu get name");
            try (BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));){
                String line;
                do {
                    if ((line = in.readLine()) == null) return null;
                } while ((line = line.trim()).isEmpty() || line.equalsIgnoreCase("name"));
                String string = line;
                return string;
            }
        }
        catch (IOException ex) {
            log.debug("unable to get cpu name", (Throwable)ex);
        }
        return null;
    }

    public TelemetryClient(OkHttpClient okHttpClient, Gson gson, HttpUrl apiBase) {
        this.okHttpClient = okHttpClient;
        this.gson = gson;
        this.apiBase = apiBase;
    }
}

