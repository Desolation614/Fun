/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.config;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(value=RetentionPolicy.RUNTIME)
@Target(value={ElementType.FIELD})
public @interface ConfigSection {
    public String name();

    public String description();

    public int position();

    public boolean closedByDefault() default false;
}

