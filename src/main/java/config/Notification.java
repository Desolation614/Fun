/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package net.runelite.client.config;

import java.awt.Color;
import java.awt.TrayIcon;
import javax.annotation.Nullable;
import net.runelite.client.config.ConfigSerializer;
import net.runelite.client.config.FlashNotification;
import net.runelite.client.config.NotificationSerializer;
import net.runelite.client.config.NotificationSound;
import net.runelite.client.config.RequestFocusType;

@ConfigSerializer(value=NotificationSerializer.class)
public class Notification {
    public static final Notification OFF = new Notification();
    public static final Notification ON = new Notification().withEnabled(true);
    boolean enabled;
    boolean initialized;
    boolean override;
    boolean tray;
    transient TrayIcon.MessageType trayIconType = TrayIcon.MessageType.NONE;
    RequestFocusType requestFocus;
    NotificationSound sound;
    @Nullable
    String soundName;
    int volume;
    int timeout;
    boolean gameMessage;
    FlashNotification flash;
    Color flashColor;
    boolean sendWhenFocused;

    public Notification(boolean enabled, boolean initialized, boolean override, boolean tray, TrayIcon.MessageType trayIconType, RequestFocusType requestFocus, NotificationSound sound, @Nullable String soundName, int volume, int timeout, boolean gameMessage, FlashNotification flash, Color flashColor, boolean sendWhenFocused) {
        this.enabled = enabled;
        this.initialized = initialized;
        this.override = override;
        this.tray = tray;
        this.trayIconType = trayIconType;
        this.requestFocus = requestFocus;
        this.sound = sound;
        this.soundName = soundName;
        this.volume = volume;
        this.timeout = timeout;
        this.gameMessage = gameMessage;
        this.flash = flash;
        this.flashColor = flashColor;
        this.sendWhenFocused = sendWhenFocused;
    }

    public Notification() {
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public boolean isInitialized() {
        return this.initialized;
    }

    public boolean isOverride() {
        return this.override;
    }

    public boolean isTray() {
        return this.tray;
    }

    public TrayIcon.MessageType getTrayIconType() {
        return this.trayIconType;
    }

    public RequestFocusType getRequestFocus() {
        return this.requestFocus;
    }

    public NotificationSound getSound() {
        return this.sound;
    }

    @Nullable
    public String getSoundName() {
        return this.soundName;
    }

    public int getVolume() {
        return this.volume;
    }

    public int getTimeout() {
        return this.timeout;
    }

    public boolean isGameMessage() {
        return this.gameMessage;
    }

    public FlashNotification getFlash() {
        return this.flash;
    }

    public Color getFlashColor() {
        return this.flashColor;
    }

    public boolean isSendWhenFocused() {
        return this.sendWhenFocused;
    }

    public Notification withEnabled(boolean enabled) {
        return this.enabled == enabled ? this : new Notification(enabled, this.initialized, this.override, this.tray, this.trayIconType, this.requestFocus, this.sound, this.soundName, this.volume, this.timeout, this.gameMessage, this.flash, this.flashColor, this.sendWhenFocused);
    }

    public Notification withInitialized(boolean initialized) {
        return this.initialized == initialized ? this : new Notification(this.enabled, initialized, this.override, this.tray, this.trayIconType, this.requestFocus, this.sound, this.soundName, this.volume, this.timeout, this.gameMessage, this.flash, this.flashColor, this.sendWhenFocused);
    }

    public Notification withOverride(boolean override) {
        return this.override == override ? this : new Notification(this.enabled, this.initialized, override, this.tray, this.trayIconType, this.requestFocus, this.sound, this.soundName, this.volume, this.timeout, this.gameMessage, this.flash, this.flashColor, this.sendWhenFocused);
    }

    public Notification withTray(boolean tray) {
        return this.tray == tray ? this : new Notification(this.enabled, this.initialized, this.override, tray, this.trayIconType, this.requestFocus, this.sound, this.soundName, this.volume, this.timeout, this.gameMessage, this.flash, this.flashColor, this.sendWhenFocused);
    }

    public Notification withTrayIconType(TrayIcon.MessageType trayIconType) {
        return this.trayIconType == trayIconType ? this : new Notification(this.enabled, this.initialized, this.override, this.tray, trayIconType, this.requestFocus, this.sound, this.soundName, this.volume, this.timeout, this.gameMessage, this.flash, this.flashColor, this.sendWhenFocused);
    }

    public Notification withRequestFocus(RequestFocusType requestFocus) {
        return this.requestFocus == requestFocus ? this : new Notification(this.enabled, this.initialized, this.override, this.tray, this.trayIconType, requestFocus, this.sound, this.soundName, this.volume, this.timeout, this.gameMessage, this.flash, this.flashColor, this.sendWhenFocused);
    }

    public Notification withSound(NotificationSound sound) {
        return this.sound == sound ? this : new Notification(this.enabled, this.initialized, this.override, this.tray, this.trayIconType, this.requestFocus, sound, this.soundName, this.volume, this.timeout, this.gameMessage, this.flash, this.flashColor, this.sendWhenFocused);
    }

    public Notification withSoundName(@Nullable String soundName) {
        return this.soundName == soundName ? this : new Notification(this.enabled, this.initialized, this.override, this.tray, this.trayIconType, this.requestFocus, this.sound, soundName, this.volume, this.timeout, this.gameMessage, this.flash, this.flashColor, this.sendWhenFocused);
    }

    public Notification withVolume(int volume) {
        return this.volume == volume ? this : new Notification(this.enabled, this.initialized, this.override, this.tray, this.trayIconType, this.requestFocus, this.sound, this.soundName, volume, this.timeout, this.gameMessage, this.flash, this.flashColor, this.sendWhenFocused);
    }

    public Notification withTimeout(int timeout) {
        return this.timeout == timeout ? this : new Notification(this.enabled, this.initialized, this.override, this.tray, this.trayIconType, this.requestFocus, this.sound, this.soundName, this.volume, timeout, this.gameMessage, this.flash, this.flashColor, this.sendWhenFocused);
    }

    public Notification withGameMessage(boolean gameMessage) {
        return this.gameMessage == gameMessage ? this : new Notification(this.enabled, this.initialized, this.override, this.tray, this.trayIconType, this.requestFocus, this.sound, this.soundName, this.volume, this.timeout, gameMessage, this.flash, this.flashColor, this.sendWhenFocused);
    }

    public Notification withFlash(FlashNotification flash) {
        return this.flash == flash ? this : new Notification(this.enabled, this.initialized, this.override, this.tray, this.trayIconType, this.requestFocus, this.sound, this.soundName, this.volume, this.timeout, this.gameMessage, flash, this.flashColor, this.sendWhenFocused);
    }

    public Notification withFlashColor(Color flashColor) {
        return this.flashColor == flashColor ? this : new Notification(this.enabled, this.initialized, this.override, this.tray, this.trayIconType, this.requestFocus, this.sound, this.soundName, this.volume, this.timeout, this.gameMessage, this.flash, flashColor, this.sendWhenFocused);
    }

    public Notification withSendWhenFocused(boolean sendWhenFocused) {
        return this.sendWhenFocused == sendWhenFocused ? this : new Notification(this.enabled, this.initialized, this.override, this.tray, this.trayIconType, this.requestFocus, this.sound, this.soundName, this.volume, this.timeout, this.gameMessage, this.flash, this.flashColor, sendWhenFocused);
    }
}

