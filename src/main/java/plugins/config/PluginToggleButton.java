/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.plugins.config;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.util.List;
import javax.swing.ImageIcon;
import javax.swing.JToggleButton;
import net.runelite.client.plugins.config.ConfigPanel;
import net.runelite.client.util.ImageUtil;
import net.runelite.client.util.SwingUtil;

class PluginToggleButton
extends JToggleButton {
    private static final ImageIcon ON_SWITCHER;
    private static final ImageIcon OFF_SWITCHER;
    private String conflictString = "";

    public PluginToggleButton() {
        super(OFF_SWITCHER);
        this.setSelectedIcon(ON_SWITCHER);
        SwingUtil.removeButtonDecorations(this);
        this.setPreferredSize(new Dimension(25, 0));
        this.addItemListener(l -> this.updateTooltip());
        this.updateTooltip();
    }

    private void updateTooltip() {
        this.setToolTipText((String)(this.isSelected() ? "Disable plugin" : "<html>Enable plugin" + this.conflictString));
    }

    public void setConflicts(List<String> conflicts) {
        if (conflicts != null && !conflicts.isEmpty()) {
            StringBuilder sb = new StringBuilder("<br>Plugin conflicts: ");
            for (int i = 0; i < conflicts.size() - 2; ++i) {
                sb.append(conflicts.get(i));
                sb.append(", ");
            }
            if (conflicts.size() >= 2) {
                sb.append(conflicts.get(conflicts.size() - 2));
                sb.append(" and ");
            }
            sb.append(conflicts.get(conflicts.size() - 1));
            this.conflictString = sb.toString();
        } else {
            this.conflictString = "";
        }
        this.updateTooltip();
    }

    static {
        BufferedImage onSwitcher = ImageUtil.loadImageResource(ConfigPanel.class, "switcher_on.png");
        ON_SWITCHER = new ImageIcon(onSwitcher);
        OFF_SWITCHER = new ImageIcon(ImageUtil.flipImage(ImageUtil.luminanceScale(ImageUtil.grayscaleImage(onSwitcher), 0.61f), true, false));
    }
}

