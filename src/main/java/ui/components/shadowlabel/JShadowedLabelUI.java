/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.ui.components.shadowlabel;

import java.awt.Graphics;
import javax.swing.JLabel;
import javax.swing.plaf.basic.BasicLabelUI;
import net.runelite.client.ui.components.shadowlabel.JShadowedLabel;

class JShadowedLabelUI
extends BasicLabelUI {
    JShadowedLabelUI() {
    }

    @Override
    protected void paintEnabledText(JLabel l, Graphics g, String s, int textX, int textY) {
        if (l instanceof JShadowedLabel) {
            JShadowedLabel ll = (JShadowedLabel)l;
            g.setColor(ll.getShadow());
            g.drawString(s, textX + ll.getShadowSize().x, textY + ll.getShadowSize().y);
        }
        g.setColor(l.getForeground());
        g.drawString(s, textX, textY);
    }
}

