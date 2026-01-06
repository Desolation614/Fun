/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.Gson
 */
package net.runelite.client.externalplugins;

import com.google.gson.Gson;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.invoke.MethodHandles;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import net.runelite.client.externalplugins.PluginHubManifest;
import net.runelite.client.util.ReflectUtil;

class PluginHubClassLoader
extends URLClassLoader
implements ReflectUtil.PrivateLookupableClassLoader {
    private final PluginHubManifest.JarData jarData;
    private final PluginHubManifest.Stub stub;
    private MethodHandles.Lookup lookup;

    PluginHubClassLoader(PluginHubManifest.JarData jarData, URL[] urls, Gson gson) throws IOException {
        super(urls, PluginHubClassLoader.class.getClassLoader());
        this.jarData = jarData;
        try (InputStream is = this.getResourceAsStream("runelite_plugin.json");){
            this.stub = (PluginHubManifest.Stub)gson.fromJson((Reader)new InputStreamReader(is, StandardCharsets.UTF_8), PluginHubManifest.Stub.class);
        }
        ReflectUtil.installLookupHelper(this);
    }

    @Override
    public Class<?> defineClass0(String name, byte[] b, int off, int len) throws ClassFormatError {
        return super.defineClass(name, b, off, len);
    }

    public PluginHubManifest.JarData getJarData() {
        return this.jarData;
    }

    public PluginHubManifest.Stub getStub() {
        return this.stub;
    }

    @Override
    public MethodHandles.Lookup getLookup() {
        return this.lookup;
    }

    @Override
    public void setLookup(MethodHandles.Lookup lookup) {
        this.lookup = lookup;
    }
}

