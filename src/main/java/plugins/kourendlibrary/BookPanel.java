/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.plugins.kourendlibrary;

import java.awt.Color;
import javax.swing.GroupLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import net.runelite.client.plugins.kourendlibrary.Book;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.FontManager;

class BookPanel
extends JPanel {
    private final JLabel location = new JLabel();

    BookPanel(Book b) {
        this.setBorder(new EmptyBorder(3, 3, 3, 3));
        this.setBackground(ColorScheme.DARK_GRAY_COLOR);
        GroupLayout layout = new GroupLayout(this);
        this.setLayout(layout);
        JLabel image = new JLabel();
        b.getIcon().addTo(image);
        JLabel name = new JLabel(b.getShortName());
        this.location.setFont(FontManager.getRunescapeSmallFont());
        layout.setVerticalGroup(layout.createParallelGroup().addComponent(image).addGroup(layout.createSequentialGroup().addComponent(name).addComponent(this.location)));
        layout.setHorizontalGroup(layout.createSequentialGroup().addComponent(image).addGap(8).addGroup(layout.createParallelGroup().addComponent(name).addComponent(this.location)));
        this.setComponentZOrder(image, this.getComponentCount() - 1);
    }

    void setLocation(String location) {
        this.location.setText(location);
    }

    void setIsTarget(boolean target) {
        this.location.setForeground(target ? Color.GREEN : Color.ORANGE);
    }

    void setIsHeld(boolean held) {
        if (held) {
            this.location.setForeground(Color.WHITE);
        }
    }
}

