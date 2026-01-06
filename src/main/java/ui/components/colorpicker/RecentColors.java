/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.EvictingQueue
 */
package net.runelite.client.ui.components.colorpicker;

import com.google.common.collect.EvictingQueue;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.function.Consumer;
import javax.swing.JPanel;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.util.ColorUtil;
import net.runelite.client.util.Text;

final class RecentColors {
    private static final String CONFIG_KEY = "recentColors";
    private static final int MAX = 16;
    private static final int BOX_SIZE = 16;
    private final EvictingQueue<String> recentColors = EvictingQueue.create((int)16);
    private final ConfigManager configManager;

    RecentColors(ConfigManager configManager) {
        this.configManager = configManager;
    }

    private void load() {
        String str = this.configManager.getConfiguration("colorpicker", CONFIG_KEY);
        if (str != null) {
            this.recentColors.addAll(Text.fromCSV(str));
        }
    }

    void add(String color) {
        if (ColorUtil.fromString(color) == null) {
            return;
        }
        this.recentColors.remove((Object)color);
        this.recentColors.add((Object)color);
        this.configManager.setConfiguration("colorpicker", CONFIG_KEY, Text.toCSV(this.recentColors));
    }

    JPanel build(Consumer<Color> consumer, boolean alphaHidden) {
        this.load();
        JPanel container = new JPanel(new GridBagLayout());
        GridBagConstraints cx = new GridBagConstraints();
        cx.insets = new Insets(0, 1, 4, 2);
        cx.gridy = 0;
        cx.gridx = 0;
        cx.anchor = 17;
        for (String s : this.recentColors) {
            if (cx.gridx == 8) {
                ++cx.gridy;
                cx.gridx = 0;
            }
            if (container.getComponentCount() == this.recentColors.size() - 1) {
                cx.weightx = 1.0;
                cx.gridwidth = 8 - cx.gridx;
            }
            container.add((Component)RecentColors.createBox(ColorUtil.fromString(s), consumer, alphaHidden), cx);
            ++cx.gridx;
        }
        return container;
    }

    private static JPanel createBox(final Color color, final Consumer<Color> consumer, boolean alphaHidden) {
        JPanel box = new JPanel();
        String hex = alphaHidden ? ColorUtil.colorToHexCode(color) : ColorUtil.colorToAlphaHexCode(color);
        box.setBackground(color);
        box.setOpaque(true);
        box.setPreferredSize(new Dimension(16, 16));
        box.setToolTipText("#" + hex.toUpperCase());
        box.addMouseListener(new MouseAdapter(){

            @Override
            public void mouseClicked(MouseEvent e) {
                consumer.accept(color);
            }
        });
        return box;
    }
}

