/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.inject.Binder
 *  javax.inject.Inject
 *  net.runelite.api.events.GameStateChanged
 */
package net.runelite.client.plugins.instancemap;

import com.google.inject.Binder;
import javax.inject.Inject;
import net.runelite.api.events.GameStateChanged;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.input.KeyManager;
import net.runelite.client.input.MouseManager;
import net.runelite.client.menus.MenuManager;
import net.runelite.client.menus.WidgetMenuOption;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.instancemap.InstanceMapInputListener;
import net.runelite.client.plugins.instancemap.InstanceMapOverlay;
import net.runelite.client.ui.overlay.OverlayManager;

@PluginDescriptor(name="Instance Map", description="Add an instanced map, accessible by right-clicking the map button")
public class InstanceMapPlugin
extends Plugin {
    private final WidgetMenuOption openMapOption = new WidgetMenuOption("Show", "Instance Map", 10485813);
    @Inject
    private InstanceMapInputListener inputListener;
    @Inject
    private OverlayManager overlayManager;
    @Inject
    private InstanceMapOverlay overlay;
    @Inject
    private MenuManager menuManager;
    @Inject
    private KeyManager keyManager;
    @Inject
    private MouseManager mouseManager;

    @Override
    public void configure(Binder binder) {
        binder.bind(InstanceMapInputListener.class);
    }

    private void addCustomOptions() {
        this.menuManager.addManagedCustomMenu(this.openMapOption, entry -> {
            if (this.overlay.isMapShown()) {
                this.closeMap();
            } else {
                this.showMap();
            }
        });
    }

    private void removeCustomOptions() {
        this.menuManager.removeManagedCustomMenu(this.openMapOption);
    }

    @Override
    protected void startUp() throws Exception {
        this.overlayManager.add(this.overlay);
        this.addCustomOptions();
        this.keyManager.registerKeyListener(this.inputListener);
        this.mouseManager.registerMouseListener(this.inputListener);
        this.mouseManager.registerMouseWheelListener(this.inputListener);
    }

    @Override
    protected void shutDown() throws Exception {
        this.overlay.setShowMap(false);
        this.overlayManager.remove(this.overlay);
        this.removeCustomOptions();
        this.keyManager.unregisterKeyListener(this.inputListener);
        this.mouseManager.unregisterMouseListener(this.inputListener);
        this.mouseManager.unregisterMouseWheelListener(this.inputListener);
    }

    @Subscribe
    public void onGameStateChanged(GameStateChanged event) {
        this.overlay.onGameStateChange(event);
    }

    public void showMap() {
        this.overlay.setShowMap(true);
        this.openMapOption.setMenuOption("Hide");
    }

    public void closeMap() {
        this.overlay.setShowMap(false);
        this.openMapOption.setMenuOption("Show");
    }

    public void ascendMap() {
        this.overlay.onAscend();
    }

    public void descendMap() {
        this.overlay.onDescend();
    }
}

