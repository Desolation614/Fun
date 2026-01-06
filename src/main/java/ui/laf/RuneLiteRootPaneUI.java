/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.formdev.flatlaf.ui.FlatRootPaneUI
 */
package net.runelite.client.ui.laf;

import com.formdev.flatlaf.ui.FlatRootPaneUI;
import java.awt.LayoutManager;
import java.beans.PropertyChangeEvent;
import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;

public class RuneLiteRootPaneUI
extends FlatRootPaneUI {
    public static final String PROP_RUNELITE_TITLEBAR = "runelite.titleBar";

    public static ComponentUI createUI(JComponent c) {
        return new RuneLiteRootPaneUI();
    }

    public void propertyChange(PropertyChangeEvent e) {
        super.propertyChange(e);
        if (e.getPropertyName().equals(PROP_RUNELITE_TITLEBAR)) {
            boolean titleBar;
            boolean bl = titleBar = this.rootPane.getClientProperty(PROP_RUNELITE_TITLEBAR) == Boolean.TRUE;
            if (!titleBar) {
                throw new IllegalStateException();
            }
            this.setTitlePane(this.createTitlePane());
            this.rootPane.setLayout((LayoutManager)this.createRootLayout());
        }
    }
}

