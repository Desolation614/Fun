/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.inject.Provides
 *  javax.inject.Inject
 *  net.runelite.api.Client
 *  net.runelite.api.MenuAction
 *  net.runelite.api.events.MenuEntryAdded
 *  net.runelite.api.widgets.WidgetUtil
 */
package net.runelite.client.plugins.ferox;

import com.google.inject.Provides;
import java.awt.event.KeyEvent;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.MenuAction;
import net.runelite.api.events.MenuEntryAdded;
import net.runelite.api.widgets.WidgetUtil;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.input.KeyListener;
import net.runelite.client.input.KeyManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.ferox.ClueScrollStepOverlay;
import net.runelite.client.plugins.ferox.FeroxConfig;
import net.runelite.client.plugins.ferox.WeaponUpgradeOverlay;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.util.Text;

@PluginDescriptor(name="Ferox", description="Ferox specific settings.", tags={"ferox"})
public class FeroxPlugin
extends Plugin {
    private static final String MESSAGE = "Message";
    private static final int SPELLBOOK_STANDARD = 0;
    private static final int SPELLBOOK_ZAROS = 1;
    private static final int SPELLBOOK_LUNAR = 2;
    private static final int SPELLBOOK_ARCEUUS = 3;
    @Inject
    private FeroxConfig config;
    @Inject
    private Client client;
    @Inject
    private ClientThread clientThread;
    @Inject
    private KeyManager keyManager;
    @Inject
    private OverlayManager overlayManager;
    @Inject
    private WeaponUpgradeOverlay overlay;
    @Inject
    private ClueScrollStepOverlay clueStepOverlay;
    private final KeyListener keyListener = new KeyListener(){

        @Override
        public void keyTyped(KeyEvent e) {
        }

        @Override
        public void keyPressed(KeyEvent e) {
            if (FeroxPlugin.this.config.teleporterKeybind().matches(e)) {
                FeroxPlugin.this.clientThread.invoke(() -> FeroxPlugin.this.teleportButton(2));
                e.consume();
            } else if (FeroxPlugin.this.config.lastTeleportKeybind().matches(e)) {
                FeroxPlugin.this.clientThread.invoke(() -> FeroxPlugin.this.teleportButton(3));
                e.consume();
            } else if (FeroxPlugin.this.config.spawnKeybind().matches(e)) {
                FeroxPlugin.this.clientThread.invoke(() -> FeroxPlugin.this.client.runScript(new Object[]{49, "spawn"}));
                e.consume();
            } else if (FeroxPlugin.this.config.presetsKeybind().matches(e)) {
                FeroxPlugin.this.clientThread.invoke(() -> FeroxPlugin.this.client.runScript(new Object[]{49, "presets"}));
                e.consume();
            } else if (FeroxPlugin.this.config.lastPresetKeybind().matches(e)) {
                FeroxPlugin.this.clientThread.invoke(() -> FeroxPlugin.this.client.runScript(new Object[]{49, "loadlastpreset"}));
                e.consume();
            } else if (FeroxPlugin.this.config.bankKeybind().matches(e)) {
                FeroxPlugin.this.clientThread.invoke(() -> FeroxPlugin.this.client.runScript(new Object[]{49, "b"}));
                e.consume();
            } else if (FeroxPlugin.this.config.homeKeybind().matches(e)) {
                FeroxPlugin.this.clientThread.invoke(() -> FeroxPlugin.this.client.runScript(new Object[]{49, "home"}));
                e.consume();
            }
        }

        @Override
        public void keyReleased(KeyEvent e) {
        }
    };

    @Provides
    FeroxConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(FeroxConfig.class);
    }

    @Override
    protected void startUp() throws Exception {
        this.keyManager.registerKeyListener(this.keyListener);
        this.overlayManager.add(this.overlay);
        this.overlayManager.add(this.clueStepOverlay);
    }

    @Override
    protected void shutDown() throws Exception {
        this.keyManager.unregisterKeyListener(this.keyListener);
        this.overlayManager.remove(this.overlay);
        this.overlayManager.remove(this.clueStepOverlay);
    }

    @Subscribe
    public void onMenuEntryAdded(MenuEntryAdded event) {
        if (event.getType() != MenuAction.CC_OP.getId() && event.getType() != MenuAction.CC_OP_LOW_PRIORITY.getId()) {
            return;
        }
        String option = event.getOption();
        int componentId = event.getActionParam1();
        int groupId = WidgetUtil.componentToInterface((int)componentId);
        if ((groupId == 162 || groupId == 7 || groupId == 163) && option.equals("Add ignore")) {
            this.client.createMenuEntry(-2).setOption(MESSAGE).setTarget(event.getTarget()).setType(MenuAction.RUNELITE).setIdentifier(event.getIdentifier()).onClick(e -> {
                String target = Text.removeTags(e.getTarget());
                this.clientThread.invoke(() -> this.client.runScript(new Object[]{107, target}));
            });
        }
    }

    private void teleportButton(int id) {
        int button;
        switch (this.client.getVarbitValue(4070)) {
            case 1: {
                button = 14286951;
                break;
            }
            case 2: {
                button = 14286952;
                break;
            }
            case 3: {
                button = 14286996;
                break;
            }
            default: {
                button = 14286855;
            }
        }
        this.client.menuAction(-1, button, MenuAction.CC_OP, id, -1, "", "");
    }
}

