/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.util;

import java.awt.Component;
import javax.swing.Popup;
import javax.swing.PopupFactory;

public class MacOSPopupFactory
extends PopupFactory {
    @Override
    protected Popup getPopup(Component owner, Component contents, int x, int y, boolean isHeavyWeightPopup) throws IllegalArgumentException {
        return super.getPopup(owner, contents, x, y, true);
    }
}

