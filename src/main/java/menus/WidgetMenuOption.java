/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.runelite.api.MenuEntry
 *  net.runelite.api.widgets.WidgetInfo
 */
package net.runelite.client.menus;

import java.awt.Color;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.runelite.api.MenuEntry;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.ui.JagexColors;
import net.runelite.client.util.ColorUtil;

public final class WidgetMenuOption {
    private String menuOption;
    private String menuTarget;
    private Color color = JagexColors.MENU_TARGET;
    @Nullable
    private final WidgetInfo widget;
    private final int widgetId;
    Consumer<MenuEntry> callback;

    @Deprecated
    public WidgetMenuOption(String menuOption, String menuTarget, WidgetInfo widget) {
        this.menuOption = menuOption;
        this.setMenuTarget(menuTarget);
        this.widget = widget;
        this.widgetId = widget.getId();
    }

    public WidgetMenuOption(String menuOption, String menuTarget, int widgetId) {
        this.menuOption = menuOption;
        this.setMenuTarget(menuTarget);
        this.widget = null;
        this.widgetId = widgetId;
    }

    public void setMenuTarget(String target) {
        this.menuTarget = ColorUtil.wrapWithColorTag(target, this.color);
    }

    public String getMenuOption() {
        return this.menuOption;
    }

    public void setMenuOption(String menuOption) {
        this.menuOption = menuOption;
    }

    public String getMenuTarget() {
        return this.menuTarget;
    }

    public Color getColor() {
        return this.color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    @Nullable
    public WidgetInfo getWidget() {
        return this.widget;
    }

    public int getWidgetId() {
        return this.widgetId;
    }
}

