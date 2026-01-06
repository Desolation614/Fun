/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.formdev.flatlaf.ui.FlatScrollBarUI
 */
package net.runelite.client.ui.laf;

import com.formdev.flatlaf.ui.FlatScrollBarUI;
import javax.swing.JComponent;
import javax.swing.JScrollBar;
import javax.swing.plaf.ComponentUI;

public class RuneLiteScrollBarUI
extends FlatScrollBarUI {
    public static ComponentUI createUI(JComponent c) {
        JScrollBar bar = (JScrollBar)c;
        bar.setUnitIncrement(16);
        return new RuneLiteScrollBarUI();
    }
}

