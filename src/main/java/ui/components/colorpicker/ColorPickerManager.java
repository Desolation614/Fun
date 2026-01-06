/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.inject.Inject
 *  javax.inject.Singleton
 *  net.runelite.api.Client
 */
package net.runelite.client.ui.components.colorpicker;

import java.awt.Color;
import java.awt.Component;
import java.awt.Window;
import java.awt.event.WindowEvent;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.swing.SwingUtilities;
import net.runelite.api.Client;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.ui.components.colorpicker.RuneliteColorPicker;

@Singleton
public class ColorPickerManager {
    private final ConfigManager configManager;
    private RuneliteColorPicker currentPicker;

    public RuneliteColorPicker create(Client client2, Color previousColor, String title, boolean alphaHidden) {
        return this.create((Component)client2, previousColor, title, alphaHidden);
    }

    public RuneliteColorPicker create(Component owner, Color previousColor, String title, boolean alphaHidden) {
        return this.create(SwingUtilities.windowForComponent(owner), previousColor, title, alphaHidden);
    }

    public RuneliteColorPicker create(Window owner, Color previousColor, String title, boolean alphaHidden) {
        if (this.currentPicker != null) {
            this.currentPicker.dispatchEvent(new WindowEvent(this.currentPicker, 201));
        }
        this.currentPicker = new RuneliteColorPicker(owner, previousColor, title, alphaHidden, this.configManager, this);
        if (this.currentPicker.isAlwaysOnTopSupported() && owner != null) {
            this.currentPicker.setAlwaysOnTop(owner.isAlwaysOnTop());
        }
        return this.currentPicker;
    }

    @Inject
    public ColorPickerManager(ConfigManager configManager) {
        this.configManager = configManager;
    }

    void setCurrentPicker(RuneliteColorPicker currentPicker) {
        this.currentPicker = currentPicker;
    }

    RuneliteColorPicker getCurrentPicker() {
        return this.currentPicker;
    }
}

