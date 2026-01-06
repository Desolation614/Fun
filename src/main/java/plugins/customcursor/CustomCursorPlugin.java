/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.inject.Provides
 *  javax.inject.Inject
 *  net.runelite.api.Client
 *  net.runelite.api.EquipmentInventorySlot
 *  net.runelite.api.Item
 *  net.runelite.api.ItemContainer
 *  net.runelite.api.events.ItemContainerChanged
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package net.runelite.client.plugins.customcursor;

import com.google.inject.Provides;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.EquipmentInventorySlot;
import net.runelite.api.Item;
import net.runelite.api.ItemContainer;
import net.runelite.api.events.ItemContainerChanged;
import net.runelite.client.RuneLite;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.customcursor.CustomCursor;
import net.runelite.client.plugins.customcursor.CustomCursorConfig;
import net.runelite.client.ui.ClientUI;
import net.runelite.client.util.AsyncBufferedImage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@PluginDescriptor(name="Custom Cursor", description="Replaces your mouse cursor image", enabledByDefault=false)
public class CustomCursorPlugin
extends Plugin {
    private static final Logger log = LoggerFactory.getLogger(CustomCursorPlugin.class);
    private static final File CUSTOM_IMAGE_FILE = new File(RuneLite.RUNELITE_DIR, "cursor.png");
    @Inject
    private Client client;
    @Inject
    private ClientThread clientThread;
    @Inject
    private ClientUI clientUI;
    @Inject
    private CustomCursorConfig config;
    @Inject
    private ItemManager itemManager;

    @Provides
    CustomCursorConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(CustomCursorConfig.class);
    }

    @Override
    protected void startUp() {
        this.updateCursor();
    }

    @Override
    protected void shutDown() {
        this.clientUI.resetCursor();
    }

    @Subscribe
    public void onConfigChanged(ConfigChanged event) {
        if (event.getGroup().equals("customcursor") && event.getKey().equals("cursorStyle")) {
            this.updateCursor();
        }
    }

    @Subscribe
    public void onItemContainerChanged(ItemContainerChanged event) {
        if (this.config.selectedCursor() == CustomCursor.EQUIPPED_WEAPON && event.getContainerId() == 94) {
            this.updateCursor();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void updateCursor() {
        CustomCursor selectedCursor = this.config.selectedCursor();
        if (selectedCursor == CustomCursor.CUSTOM_IMAGE) {
            if (CUSTOM_IMAGE_FILE.exists()) {
                try {
                    Class<ImageIO> clazz = ImageIO.class;
                    synchronized (ImageIO.class) {
                        BufferedImage image = ImageIO.read(CUSTOM_IMAGE_FILE);
                        // ** MonitorExit[var3_2] (shouldn't be in output)
                        this.clientUI.setCursor(image, selectedCursor.getName());
                    }
                }
                catch (Exception e) {
                    log.error("error setting custom cursor", (Throwable)e);
                    this.clientUI.resetCursor();
                }
            } else {
                this.clientUI.resetCursor();
            }
        } else if (selectedCursor == CustomCursor.EQUIPPED_WEAPON) {
            this.clientThread.invokeLater(() -> {
                ItemContainer equipment = this.client.getItemContainer(94);
                if (equipment == null) {
                    this.clientUI.resetCursor();
                    return;
                }
                Item weapon = equipment.getItem(EquipmentInventorySlot.WEAPON.getSlotIdx());
                if (weapon == null) {
                    this.clientUI.resetCursor();
                    return;
                }
                AsyncBufferedImage image = this.itemManager.getImage(weapon.getId());
                if (weapon.getQuantity() > 0) {
                    this.clientUI.setCursor(image, selectedCursor.getName());
                } else {
                    this.clientUI.resetCursor();
                }
            });
        } else {
            assert (selectedCursor.getCursorImage() != null);
            this.clientUI.setCursor(selectedCursor.getCursorImage(), selectedCursor.getName());
        }
    }
}

