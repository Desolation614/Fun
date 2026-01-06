/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.plugins;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDependencies;

@Retention(value=RetentionPolicy.RUNTIME)
@Target(value={ElementType.TYPE})
@Documented
@Repeatable(value=PluginDependencies.class)
public @interface PluginDependency {
    public Class<? extends Plugin> value();
}

