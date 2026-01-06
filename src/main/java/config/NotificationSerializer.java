/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.Gson
 *  com.google.inject.Inject
 */
package net.runelite.client.config;

import com.google.gson.Gson;
import com.google.inject.Inject;
import net.runelite.client.config.Notification;
import net.runelite.client.config.Serializer;

class NotificationSerializer
implements Serializer<Notification> {
    private final Gson gson;

    @Inject
    private NotificationSerializer(Gson gson) {
        this.gson = gson;
    }

    @Override
    public String serialize(Notification value) {
        return this.gson.toJson((Object)value);
    }

    @Override
    public Notification deserialize(String s) {
        if ("true".equals(s) || "false".equals(s)) {
            return new Notification().withEnabled(Boolean.parseBoolean(s));
        }
        return (Notification)this.gson.fromJson(s, Notification.class);
    }
}

