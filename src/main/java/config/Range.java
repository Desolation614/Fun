/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.config;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(value=RetentionPolicy.RUNTIME)
@Target(value={ElementType.METHOD})
@Documented
public @interface Range {
    public int min() default 0;

    public int max() default 0x7FFFFFFF;
}

