/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.rs;

import java.util.HashMap;
import java.util.Map;

class RSConfig {
    private final Map<String, String> appletProperties = new HashMap<String, String>();
    private final Map<String, String> classLoaderProperties = new HashMap<String, String>();

    RSConfig() {
    }

    String getCodeBase() {
        return this.classLoaderProperties.get("codebase");
    }

    void setCodebase(String codebase) {
        this.classLoaderProperties.put("codebase", codebase);
    }

    String getInitialJar() {
        return this.classLoaderProperties.get("initial_jar");
    }

    String getInitialClass() {
        return this.classLoaderProperties.get("initial_class").replace(".class", "");
    }

    String getRuneLiteWorldParam() {
        return this.classLoaderProperties.get("runelite.worldparam");
    }

    public Map<String, String> getAppletProperties() {
        return this.appletProperties;
    }

    public Map<String, String> getClassLoaderProperties() {
        return this.classLoaderProperties;
    }
}

