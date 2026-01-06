/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.inject.Binder
 *  com.google.inject.Injector
 *  com.google.inject.Module
 */
package net.runelite.client.plugins;

import com.google.inject.Binder;
import com.google.inject.Injector;
import com.google.inject.Module;
import net.runelite.client.plugins.PluginDescriptor;

public abstract class Plugin
implements Module {
    protected Injector injector;

    public final int hashCode() {
        return super.hashCode();
    }

    public final boolean equals(Object obj) {
        return super.equals(obj);
    }

    public void configure(Binder binder) {
    }

    protected void startUp() throws Exception {
    }

    protected void shutDown() throws Exception {
    }

    public void resetConfiguration() {
    }

    public final Injector getInjector() {
        return this.injector;
    }

    public String getName() {
        return this.getClass().getAnnotation(PluginDescriptor.class).name();
    }
}

