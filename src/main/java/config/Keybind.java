/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.BiMap
 *  com.google.common.collect.ImmutableBiMap$Builder
 *  javax.annotation.Nullable
 */
package net.runelite.client.config;

import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import javax.annotation.Nullable;

public class Keybind {
    private static final BiMap<Integer, Integer> MODIFIER_TO_KEY_CODE = new ImmutableBiMap.Builder().put((Object)128, (Object)17).put((Object)512, (Object)18).put((Object)64, (Object)16).put((Object)256, (Object)157).build();
    private static final int KEYBOARD_MODIFIER_MASK = (Integer)MODIFIER_TO_KEY_CODE.keySet().stream().reduce((a, b) -> a | b).get();
    public static final Keybind NOT_SET = new Keybind(0, 0);
    public static final Keybind CTRL = new Keybind(0, 128);
    public static final Keybind ALT = new Keybind(0, 512);
    public static final Keybind SHIFT = new Keybind(0, 64);
    private final int keyCode;
    private final int modifiers;

    protected Keybind(int keyCode, int modifiers, boolean ignoreModifiers) {
        modifiers &= KEYBOARD_MODIFIER_MASK;
        Integer mf = Keybind.getModifierForKeyCode(keyCode);
        if (mf != null) {
            assert ((modifiers & mf) != 0);
            keyCode = 0;
        }
        if (ignoreModifiers && keyCode != 0) {
            modifiers = 0;
        }
        this.keyCode = keyCode;
        this.modifiers = modifiers;
    }

    public Keybind(int keyCode, int modifiers) {
        this(keyCode, modifiers, false);
    }

    public Keybind(KeyEvent e) {
        this(e.getExtendedKeyCode(), e.getModifiersEx());
        assert (this.matches(e));
    }

    public boolean matches(KeyEvent e) {
        return this.matches(e, false);
    }

    protected boolean matches(KeyEvent e, boolean ignoreModifiers) {
        if (NOT_SET.equals(this)) {
            return false;
        }
        int keyCode = e.getExtendedKeyCode();
        int modifiers = e.getModifiersEx() & KEYBOARD_MODIFIER_MASK;
        Integer mf = Keybind.getModifierForKeyCode(keyCode);
        if (mf != null) {
            modifiers |= mf.intValue();
            keyCode = 0;
        }
        if (e.getID() == 402) {
            if (keyCode != 0) {
                return this.keyCode == keyCode;
            }
            if (mf != null) {
                return this.keyCode == keyCode && (this.modifiers & modifiers) == this.modifiers && (mf & this.modifiers) == mf;
            }
        }
        if (ignoreModifiers && keyCode != 0) {
            return this.keyCode == keyCode;
        }
        return this.keyCode == keyCode && this.modifiers == modifiers;
    }

    public String toString() {
        if (this.keyCode == 0 && this.modifiers == 0) {
            return "Not set";
        }
        String key = this.keyCode == 0 ? "" : KeyEvent.getKeyText(this.keyCode);
        String mod = "";
        if (this.modifiers != 0) {
            mod = InputEvent.getModifiersExText(this.modifiers);
        }
        if (mod.isEmpty() && key.isEmpty()) {
            return "Not set";
        }
        if (!mod.isEmpty() && !key.isEmpty()) {
            return mod + "+" + key;
        }
        if (mod.isEmpty()) {
            return key;
        }
        return mod;
    }

    @Nullable
    public static Integer getModifierForKeyCode(int keyCode) {
        return (Integer)MODIFIER_TO_KEY_CODE.inverse().get((Object)keyCode);
    }

    public int getKeyCode() {
        return this.keyCode;
    }

    public int getModifiers() {
        return this.modifiers;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof Keybind)) {
            return false;
        }
        Keybind other = (Keybind)o;
        if (!other.canEqual(this)) {
            return false;
        }
        if (this.getKeyCode() != other.getKeyCode()) {
            return false;
        }
        return this.getModifiers() == other.getModifiers();
    }

    protected boolean canEqual(Object other) {
        return other instanceof Keybind;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        result = result * 59 + this.getKeyCode();
        result = result * 59 + this.getModifiers();
        return result;
    }
}

