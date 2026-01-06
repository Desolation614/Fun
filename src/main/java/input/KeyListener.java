/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.input;

public interface KeyListener
extends java.awt.event.KeyListener {
    default public boolean isEnabledOnLoginScreen() {
        return false;
    }

    default public void focusLost() {
    }
}

