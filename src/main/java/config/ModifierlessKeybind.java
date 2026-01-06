/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.config;

import java.awt.event.KeyEvent;
import net.runelite.client.config.Keybind;

public class ModifierlessKeybind
extends Keybind {
    public ModifierlessKeybind(int keyCode, int modifiers) {
        super(keyCode, modifiers, true);
    }

    public ModifierlessKeybind(KeyEvent e) {
        this(e.getExtendedKeyCode(), e.getModifiersEx());
        assert (this.matches(e));
    }

    @Override
    public boolean matches(KeyEvent e) {
        return this.matches(e, true);
    }
}

