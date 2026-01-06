/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.config;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(value=RetentionPolicy.RUNTIME)
@Target(value={ElementType.METHOD})
public @interface ConfigItem {
    public int position() default -1;

    public String keyName();

    public String name();

    public String description();

    public boolean hidden() default false;

    public String warning() default "";

    public boolean secret() default false;

    public String section() default "";
}

