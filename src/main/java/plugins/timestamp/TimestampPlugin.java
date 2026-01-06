/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.inject.Provides
 *  javax.inject.Inject
 *  net.runelite.api.Client
 *  net.runelite.api.MessageNode
 *  net.runelite.api.events.ScriptCallbackEvent
 */
package net.runelite.client.plugins.timestamp;

import com.google.inject.Provides;
import java.awt.Color;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.MessageNode;
import net.runelite.api.events.ScriptCallbackEvent;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.timestamp.TimestampConfig;
import net.runelite.client.util.ColorUtil;

@PluginDescriptor(name="Chat Timestamps", description="Add timestamps to chat messages", tags={"timestamp"}, enabledByDefault=false)
public class TimestampPlugin
extends Plugin {
    @Inject
    private Client client;
    @Inject
    private ClientThread clientThread;
    @Inject
    private TimestampConfig config;
    private SimpleDateFormat formatter;

    @Provides
    public TimestampConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(TimestampConfig.class);
    }

    @Override
    protected void startUp() throws Exception {
        this.updateFormatter();
    }

    @Override
    protected void shutDown() throws Exception {
        this.formatter = null;
    }

    @Subscribe
    public void onConfigChanged(ConfigChanged event) {
        if (event.getGroup().equals("timestamp")) {
            switch (event.getKey()) {
                case "format": {
                    this.updateFormatter();
                    break;
                }
                case "opaqueTimestamp": 
                case "transparentTimestamp": {
                    this.clientThread.invokeLater(() -> this.client.runScript(new Object[]{83}));
                }
            }
        }
    }

    @Subscribe
    private void onScriptCallbackEvent(ScriptCallbackEvent event) {
        if (!"chatMessageBuilding".equals(event.getEventName())) {
            return;
        }
        int uid = this.client.getIntStack()[this.client.getIntStackSize() - 1];
        MessageNode messageNode = (MessageNode)this.client.getMessages().get((long)uid);
        assert (messageNode != null) : "chat message build for unknown message";
        String timestamp = this.generateTimestamp(messageNode.getTimestamp(), ZoneId.systemDefault());
        Color timestampColour = this.getTimestampColour();
        if (timestampColour != null) {
            timestamp = ColorUtil.wrapWithColorTag(timestamp, timestampColour);
        }
        this.client.getObjectStack()[this.client.getObjectStackSize() - 1] = timestamp;
    }

    private Color getTimestampColour() {
        boolean isChatboxTransparent = this.client.isResized() && this.client.getVarbitValue(4608) == 1;
        return isChatboxTransparent ? this.config.transparentTimestamp() : this.config.opaqueTimestamp();
    }

    String generateTimestamp(int timestamp, ZoneId zoneId) {
        ZonedDateTime time = ZonedDateTime.ofInstant(Instant.ofEpochSecond(timestamp), zoneId);
        return this.formatter.format(Date.from(time.toInstant()));
    }

    private void updateFormatter() {
        try {
            this.formatter = new SimpleDateFormat(this.config.timestampFormat());
        }
        catch (IllegalArgumentException e) {
            this.formatter = new SimpleDateFormat("[HH:mm]");
        }
    }

    public SimpleDateFormat getFormatter() {
        return this.formatter;
    }
}

