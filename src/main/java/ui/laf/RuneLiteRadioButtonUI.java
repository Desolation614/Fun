/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.formdev.flatlaf.ui.FlatRadioButtonUI
 *  com.formdev.flatlaf.ui.FlatStylingSupport$Styleable
 *  com.formdev.flatlaf.ui.FlatUIUtils
 */
package net.runelite.client.ui.laf;

import com.formdev.flatlaf.ui.FlatRadioButtonUI;
import com.formdev.flatlaf.ui.FlatStylingSupport;
import com.formdev.flatlaf.ui.FlatUIUtils;
import java.awt.AlphaComposite;
import java.awt.Composite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;
import net.runelite.client.ui.laf.RuneLiteButtonUI;

public class RuneLiteRadioButtonUI
extends FlatRadioButtonUI {
    @FlatStylingSupport.Styleable
    protected float rolloverIconAlpha = 1.0f;

    public static ComponentUI createUI(JComponent c) {
        return FlatUIUtils.canUseSharedUI((JComponent)c) ? FlatUIUtils.createSharedUI(RuneLiteRadioButtonUI.class, () -> new RuneLiteRadioButtonUI(true)) : new RuneLiteRadioButtonUI(false);
    }

    protected RuneLiteRadioButtonUI(boolean shared) {
        super(shared);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void paintIcon(Graphics g, JComponent c, Rectangle iconRect) {
        if (this.rolloverIconAlpha != 1.0f && RuneLiteButtonUI.useRolloverEffect(c)) {
            Graphics2D g2d = (Graphics2D)g;
            Composite composite = g2d.getComposite();
            try {
                g2d.setComposite(AlphaComposite.getInstance(3, this.rolloverIconAlpha));
                super.paintIcon(g, c, iconRect);
            }
            finally {
                g2d.setComposite(composite);
            }
            return;
        }
        super.paintIcon(g, c, iconRect);
    }
}

