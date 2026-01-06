/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.ui;

import java.awt.image.BufferedImage;
import java.util.Comparator;
import java.util.Map;
import net.runelite.client.ui.PluginPanel;

public final class NavigationButton {
    static final Comparator<NavigationButton> COMPARATOR = Comparator.comparing(NavigationButton::getPriority).thenComparing(NavigationButton::getTooltip);
    private final BufferedImage icon;
    private final String tooltip;
    private final Runnable onClick;
    private final PluginPanel panel;
    private final int priority;
    private final Map<String, Runnable> popup;

    private static String $default$tooltip() {
        return "";
    }

    NavigationButton(BufferedImage icon, String tooltip, Runnable onClick, PluginPanel panel, int priority, Map<String, Runnable> popup) {
        this.icon = icon;
        this.tooltip = tooltip;
        this.onClick = onClick;
        this.panel = panel;
        this.priority = priority;
        this.popup = popup;
    }

    public static NavigationButtonBuilder builder() {
        return new NavigationButtonBuilder();
    }

    public BufferedImage getIcon() {
        return this.icon;
    }

    public String getTooltip() {
        return this.tooltip;
    }

    public Runnable getOnClick() {
        return this.onClick;
    }

    public PluginPanel getPanel() {
        return this.panel;
    }

    public int getPriority() {
        return this.priority;
    }

    public Map<String, Runnable> getPopup() {
        return this.popup;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof NavigationButton)) {
            return false;
        }
        NavigationButton other = (NavigationButton)o;
        if (this.getPriority() != other.getPriority()) {
            return false;
        }
        BufferedImage this$icon = this.getIcon();
        BufferedImage other$icon = other.getIcon();
        if (this$icon == null ? other$icon != null : !this$icon.equals(other$icon)) {
            return false;
        }
        String this$tooltip = this.getTooltip();
        String other$tooltip = other.getTooltip();
        if (this$tooltip == null ? other$tooltip != null : !this$tooltip.equals(other$tooltip)) {
            return false;
        }
        Runnable this$onClick = this.getOnClick();
        Runnable other$onClick = other.getOnClick();
        if (this$onClick == null ? other$onClick != null : !this$onClick.equals(other$onClick)) {
            return false;
        }
        PluginPanel this$panel = this.getPanel();
        PluginPanel other$panel = other.getPanel();
        if (this$panel == null ? other$panel != null : !this$panel.equals(other$panel)) {
            return false;
        }
        Map<String, Runnable> this$popup = this.getPopup();
        Map<String, Runnable> other$popup = other.getPopup();
        return !(this$popup == null ? other$popup != null : !((Object)this$popup).equals(other$popup));
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        result = result * 59 + this.getPriority();
        BufferedImage $icon = this.getIcon();
        result = result * 59 + ($icon == null ? 43 : $icon.hashCode());
        String $tooltip = this.getTooltip();
        result = result * 59 + ($tooltip == null ? 43 : $tooltip.hashCode());
        Runnable $onClick = this.getOnClick();
        result = result * 59 + ($onClick == null ? 43 : $onClick.hashCode());
        PluginPanel $panel = this.getPanel();
        result = result * 59 + ($panel == null ? 43 : $panel.hashCode());
        Map<String, Runnable> $popup = this.getPopup();
        result = result * 59 + ($popup == null ? 43 : ((Object)$popup).hashCode());
        return result;
    }

    public String toString() {
        return "NavigationButton(icon=" + String.valueOf(this.getIcon()) + ", tooltip=" + this.getTooltip() + ", onClick=" + String.valueOf(this.getOnClick()) + ", panel=" + String.valueOf(this.getPanel()) + ", priority=" + this.getPriority() + ", popup=" + String.valueOf(this.getPopup()) + ")";
    }

    public static class NavigationButtonBuilder {
        private BufferedImage icon;
        private boolean tooltip$set;
        private String tooltip$value;
        private Runnable onClick;
        private PluginPanel panel;
        private int priority;
        private Map<String, Runnable> popup;

        NavigationButtonBuilder() {
        }

        public NavigationButtonBuilder icon(BufferedImage icon) {
            this.icon = icon;
            return this;
        }

        public NavigationButtonBuilder tooltip(String tooltip) {
            this.tooltip$value = tooltip;
            this.tooltip$set = true;
            return this;
        }

        public NavigationButtonBuilder onClick(Runnable onClick) {
            this.onClick = onClick;
            return this;
        }

        public NavigationButtonBuilder panel(PluginPanel panel) {
            this.panel = panel;
            return this;
        }

        public NavigationButtonBuilder priority(int priority) {
            this.priority = priority;
            return this;
        }

        public NavigationButtonBuilder popup(Map<String, Runnable> popup) {
            this.popup = popup;
            return this;
        }

        public NavigationButton build() {
            String tooltip$value = this.tooltip$value;
            if (!this.tooltip$set) {
                tooltip$value = NavigationButton.$default$tooltip();
            }
            return new NavigationButton(this.icon, tooltip$value, this.onClick, this.panel, this.priority, this.popup);
        }

        public String toString() {
            return "NavigationButton.NavigationButtonBuilder(icon=" + String.valueOf(this.icon) + ", tooltip$value=" + this.tooltip$value + ", onClick=" + String.valueOf(this.onClick) + ", panel=" + String.valueOf(this.panel) + ", priority=" + this.priority + ", popup=" + String.valueOf(this.popup) + ")";
        }
    }
}

