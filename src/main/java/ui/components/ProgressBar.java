/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.ui.components;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.util.Collections;
import java.util.List;
import javax.swing.JLabel;
import javax.swing.border.EmptyBorder;
import net.runelite.client.ui.FontManager;
import net.runelite.client.ui.components.DimmableJPanel;
import net.runelite.client.ui.components.shadowlabel.JShadowedLabel;

public class ProgressBar
extends DimmableJPanel {
    private int maximumValue;
    private int value;
    private List<Integer> positions = Collections.emptyList();
    private final JLabel leftLabel = new JShadowedLabel();
    private final JLabel rightLabel = new JShadowedLabel();
    private final JLabel centerLabel = new JShadowedLabel();
    private String centerLabelText = "";
    private String dimmedText = "";

    public ProgressBar() {
        this.setLayout(new GridLayout(1, 3));
        this.setBackground(Color.GREEN.darker());
        this.setForeground(Color.GREEN.brighter());
        this.setPreferredSize(new Dimension(100, 16));
        this.leftLabel.setFont(FontManager.getRunescapeSmallFont());
        this.leftLabel.setForeground(Color.WHITE);
        this.leftLabel.setBorder(new EmptyBorder(2, 5, 0, 0));
        this.rightLabel.setFont(FontManager.getRunescapeSmallFont());
        this.rightLabel.setForeground(Color.WHITE);
        this.rightLabel.setHorizontalAlignment(4);
        this.rightLabel.setBorder(new EmptyBorder(2, 0, 0, 5));
        this.centerLabel.setFont(FontManager.getRunescapeSmallFont());
        this.centerLabel.setForeground(Color.WHITE);
        this.centerLabel.setHorizontalAlignment(0);
        this.centerLabel.setBorder(new EmptyBorder(2, 0, 0, 0));
        this.add(this.leftLabel);
        this.add(this.centerLabel);
        this.add(this.rightLabel);
    }

    @Override
    public void paint(Graphics g) {
        int percentage = this.getPercentage();
        int topWidth = (int)((float)this.getSize().width * ((float)percentage / 100.0f));
        super.paint(g);
        g.setColor(this.getForeground());
        g.fillRect(0, 0, topWidth, 16);
        for (Integer position : this.positions) {
            int xCord = this.getSize().width * position / this.maximumValue;
            if (xCord <= topWidth) continue;
            g.fillRect(xCord, 0, 1, 16);
        }
        super.paintComponents(g);
    }

    @Override
    public void setDimmed(boolean dimmed) {
        super.setDimmed(dimmed);
        if (dimmed) {
            this.leftLabel.setForeground(Color.GRAY);
            this.rightLabel.setForeground(Color.GRAY);
            this.centerLabel.setText(this.dimmedText);
        } else {
            this.leftLabel.setForeground(Color.WHITE);
            this.rightLabel.setForeground(Color.WHITE);
            this.centerLabel.setText(this.centerLabelText);
        }
    }

    public void setLeftLabel(String txt) {
        this.leftLabel.setText(txt);
    }

    public void setRightLabel(String txt) {
        this.rightLabel.setText(txt);
    }

    public void setCenterLabel(String txt) {
        this.centerLabelText = txt;
        this.centerLabel.setText(this.isDimmed() ? this.dimmedText : txt);
    }

    public void setDimmedText(String txt) {
        this.dimmedText = txt;
        this.centerLabel.setText(this.isDimmed() ? txt : this.centerLabelText);
    }

    public int getPercentage() {
        if (this.maximumValue == 0) {
            return 0;
        }
        return this.value * 100 / this.maximumValue;
    }

    public void setMaximumValue(int maximumValue) {
        this.maximumValue = maximumValue;
        this.repaint();
    }

    public void setValue(int value) {
        this.value = value;
        this.repaint();
    }

    public void setPositions(List<Integer> positions) {
        this.positions = positions;
        this.repaint();
    }
}

