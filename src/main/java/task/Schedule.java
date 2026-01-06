/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.task;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.time.temporal.ChronoUnit;

@Retention(value=RetentionPolicy.RUNTIME)
@Target(value={ElementType.METHOD})
public @interface Schedule {
    public long period();

    public ChronoUnit unit();

    public boolean asynchronous() default false;
}

