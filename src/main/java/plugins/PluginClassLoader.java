/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.plugins;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

class PluginClassLoader
extends URLClassLoader {
    private final ClassLoader parent;

    PluginClassLoader(File plugin, ClassLoader parent) throws MalformedURLException {
        super(new URL[]{plugin.toURI().toURL()}, (ClassLoader)null);
        this.parent = parent;
    }

    @Override
    public Class<?> loadClass(String name) throws ClassNotFoundException {
        try {
            return super.loadClass(name);
        }
        catch (ClassNotFoundException ex) {
            return this.parent.loadClass(name);
        }
    }
}

