/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.CharMatcher
 */
package net.runelite.client.util;

import com.google.common.base.CharMatcher;

class JagexPrintableCharMatcher
extends CharMatcher {
    JagexPrintableCharMatcher() {
    }

    public boolean matches(char c) {
        return c >= ' ' && c <= '~' || c == '\u0080' || c >= '\u00a0' && c <= '\u00ff';
    }
}

