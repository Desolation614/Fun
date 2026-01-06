/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.rs;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import net.runelite.client.rs.ClientConfigLoader;
import net.runelite.client.rs.RSConfig;

public class ClientConfigLocalLoader {
    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    RSConfig fetch() {
        RSConfig config = new RSConfig();
        try (BufferedReader in = new BufferedReader(new InputStreamReader(ClientConfigLoader.class.getResourceAsStream("/jav_config.ws"), StandardCharsets.UTF_8));){
            String str;
            block15: while ((str = in.readLine()) != null) {
                String s;
                int idx = str.indexOf(61);
                if (idx == -1) continue;
                switch (s = str.substring(0, idx)) {
                    case "param": {
                        str = str.substring(idx + 1);
                        idx = str.indexOf(61);
                        s = str.substring(0, idx);
                        config.getAppletProperties().put(s, str.substring(idx + 1));
                        continue block15;
                    }
                    case "msg": {
                        continue block15;
                    }
                }
                config.getClassLoaderProperties().put(s, str.substring(idx + 1));
            }
            return config;
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return config;
    }
}

