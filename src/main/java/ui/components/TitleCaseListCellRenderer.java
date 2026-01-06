/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.ui.components;

import java.awt.Component;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;
import net.runelite.client.util.Text;

public final class TitleCaseListCellRenderer
extends DefaultListCellRenderer {
    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        String text = value instanceof Enum ? Text.titleCase((Enum)value) : value.toString();
        return super.getListCellRendererComponent(list, text, index, isSelected, cellHasFocus);
    }
}

