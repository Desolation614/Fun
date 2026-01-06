/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.config;

public interface Serializer<T> {
    public String serialize(T var1);

    public T deserialize(String var1);
}

