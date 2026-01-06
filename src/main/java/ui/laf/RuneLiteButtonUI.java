/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.formdev.flatlaf.ui.FlatButtonUI
 *  com.formdev.flatlaf.ui.FlatStylingSupport$Styleable
 *  com.formdev.flatlaf.ui.FlatUIUtils
 */
package net.runelite.client.ui.laf;

import com.formdev.flatlaf.ui.FlatButtonUI;
import com.formdev.flatlaf.ui.FlatStylingSupport;
import com.formdev.flatlaf.ui.FlatUIUtils;
import java.awt.AlphaComposite;
import java.awt.Composite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import javax.swing.AbstractButton;
import javax.swing.ButtonModel;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;

public class RuneLiteButtonUI
extends FlatButtonUI {
    @FlatStylingSupport.Styleable
    protected float rolloverIconAlpha = 1.0f;

    public static ComponentUI createUI(JComponent c) {
        return FlatUIUtils.canUseSharedUI((JComponent)c) ? FlatUIUtils.createSharedUI(RuneLiteButtonUI.class, () -> new RuneLiteButtonUI(true)) : new RuneLiteButtonUI(false);
    }

    protected RuneLiteButtonUI(boolean shared) {
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

    static boolean useRolloverEffect(JComponent c) {
        AbstractButton btn = (AbstractButton)c;
        ButtonModel model = btn.getModel();
        if (btn.isRolloverEnabled() && model.isRollover()) {
            Icon icon;
            Icon icon2 = icon = model.isSelected() ? btn.getRolloverSelectedIcon() : null;
            if (icon == null) {
                icon = btn.getRolloverIcon();
            }
            return icon == null;
        }
        return false;
    }
}

