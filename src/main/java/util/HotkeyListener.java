/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.util;

import java.awt.event.KeyEvent;
import java.util.function.Supplier;
import net.runelite.client.config.Keybind;
import net.runelite.client.input.KeyListener;

public abstract class HotkeyListener
implements KeyListener {
    private final Supplier<Keybind> keybind;
    private boolean isPressed = false;
    private boolean isConsumingTyped = false;
    private boolean enabledOnLoginScreen;

    @Override
    public boolean isEnabledOnLoginScreen() {
        return this.enabledOnLoginScreen;
    }

    @Override
    public void focusLost() {
        if (!this.isPressed) {
            return;
        }
        this.isPressed = false;
        this.isConsumingTyped = false;
        this.hotkeyReleased();
    }

    @Override
    public void keyTyped(KeyEvent e) {
        if (this.isConsumingTyped) {
            e.consume();
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (this.keybind.get().matches(e)) {
            boolean wasPressed = this.isPressed;
            this.isPressed = true;
            if (!wasPressed) {
                this.hotkeyPressed();
            }
            if (Keybind.getModifierForKeyCode(e.getKeyCode()) == null) {
                this.isConsumingTyped = true;
                e.consume();
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (this.keybind.get().matches(e) && this.isPressed) {
            this.isPressed = false;
            this.isConsumingTyped = false;
            this.hotkeyReleased();
        }
    }

    public void hotkeyPressed() {
    }

    public void hotkeyReleased() {
    }

    public HotkeyListener(Supplier<Keybind> keybind) {
        this.keybind = keybind;
    }

    public void setEnabledOnLoginScreen(boolean enabledOnLoginScreen) {
        this.enabledOnLoginScreen = enabledOnLoginScreen;
    }
}

