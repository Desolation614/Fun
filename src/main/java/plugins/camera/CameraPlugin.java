/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.primitives.Ints
 *  com.google.inject.Provides
 *  javax.inject.Inject
 *  net.runelite.api.Client
 *  net.runelite.api.MenuAction
 *  net.runelite.api.MenuEntry
 *  net.runelite.api.events.BeforeRender
 *  net.runelite.api.events.ClientTick
 *  net.runelite.api.events.FocusChanged
 *  net.runelite.api.events.ScriptCallbackEvent
 *  net.runelite.api.events.ScriptPreFired
 *  net.runelite.api.events.WidgetLoaded
 *  net.runelite.api.widgets.Widget
 */
package net.runelite.client.plugins.camera;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.primitives.Ints;
import com.google.inject.Provides;
import java.awt.Component;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import javax.inject.Inject;
import javax.swing.SwingUtilities;
import net.runelite.api.Client;
import net.runelite.api.MenuAction;
import net.runelite.api.MenuEntry;
import net.runelite.api.events.BeforeRender;
import net.runelite.api.events.ClientTick;
import net.runelite.api.events.FocusChanged;
import net.runelite.api.events.ScriptCallbackEvent;
import net.runelite.api.events.ScriptPreFired;
import net.runelite.api.events.WidgetLoaded;
import net.runelite.api.widgets.Widget;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.input.KeyListener;
import net.runelite.client.input.KeyManager;
import net.runelite.client.input.MouseListener;
import net.runelite.client.input.MouseManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.camera.CameraConfig;
import net.runelite.client.plugins.camera.ControlFunction;
import net.runelite.client.ui.overlay.tooltip.Tooltip;
import net.runelite.client.ui.overlay.tooltip.TooltipManager;

@PluginDescriptor(name="Camera", description="Expands zoom limit, provides vertical camera, and remaps mouse input keys", tags={"zoom", "limit", "vertical", "click", "mouse"}, enabledByDefault=false)
public class CameraPlugin
extends Plugin
implements KeyListener,
MouseListener {
    private static final int DEFAULT_ZOOM_INCREMENT = 25;
    private static final int DEFAULT_OUTER_ZOOM_LIMIT = 128;
    static final int DEFAULT_INNER_ZOOM_LIMIT = 896;
    private boolean controlDown;
    private boolean rightClick;
    private boolean middleClick;
    private boolean menuHasEntries;
    @Inject
    private Client client;
    @Inject
    private ClientThread clientThread;
    @Inject
    private CameraConfig config;
    @Inject
    private KeyManager keyManager;
    @Inject
    private MouseManager mouseManager;
    @Inject
    private TooltipManager tooltipManager;
    private Tooltip sliderTooltip;

    @Provides
    CameraConfig getConfig(ConfigManager configManager) {
        return configManager.getConfig(CameraConfig.class);
    }

    @Override
    protected void startUp() {
        this.rightClick = false;
        this.middleClick = false;
        this.menuHasEntries = false;
        this.copyConfigs();
        this.keyManager.registerKeyListener(this);
        this.mouseManager.registerMouseListener(this);
        this.clientThread.invoke(() -> {
            Widget settingsInit;
            Widget sideSlider = this.client.getWidget(7602233);
            if (sideSlider != null) {
                this.addZoomTooltip(sideSlider);
            }
            if ((settingsInit = this.client.getWidget(8781825)) != null) {
                this.client.createScriptEvent(settingsInit.getOnLoadListener()).setSource(settingsInit).run();
            }
        });
    }

    @Override
    protected void shutDown() {
        this.client.setCameraMouseButtonMask(0);
        this.client.setCameraSpeed(1.0f);
        this.client.setCameraPitchRelaxerEnabled(false);
        this.client.setInvertYaw(false);
        this.client.setInvertPitch(false);
        this.client.setCameraShakeDisabled(false);
        this.keyManager.unregisterKeyListener(this);
        this.mouseManager.unregisterMouseListener(this);
        this.controlDown = false;
        this.clientThread.invoke(() -> {
            Widget settingsInit;
            Widget sideSlider = this.client.getWidget(7602233);
            if (sideSlider != null) {
                sideSlider.setOnMouseRepeatListener((Object[])null);
            }
            if ((settingsInit = this.client.getWidget(8781825)) != null) {
                this.client.createScriptEvent(settingsInit.getOnLoadListener()).setSource(settingsInit).run();
            }
        });
    }

    void copyConfigs() {
        this.client.setCameraMouseButtonMask(this.config.rightClickMovesCamera() && !this.config.rightClickMenuBlocksCamera() ? 20 : 0);
        this.client.setCameraSpeed((float)this.config.cameraSpeed());
        this.client.setCameraPitchRelaxerEnabled(this.config.relaxCameraPitch());
        this.client.setInvertYaw(this.config.invertYaw());
        this.client.setInvertPitch(this.config.invertPitch());
        this.client.setCameraShakeDisabled(this.config.disableCameraShake());
    }

    @Subscribe
    public void onScriptCallbackEvent(ScriptCallbackEvent event) {
        if (this.client.getIndexScripts().isOverlayOutdated()) {
            return;
        }
        int[] intStack = this.client.getIntStack();
        int intStackSize = this.client.getIntStackSize();
        if (!this.controlDown && "scrollWheelZoom".equals(event.getEventName()) && this.config.controlFunction() == ControlFunction.CONTROL_TO_ZOOM) {
            intStack[intStackSize - 1] = 1;
        }
        if ("innerZoomLimit".equals(event.getEventName()) && this.config.innerLimit()) {
            intStack[intStackSize - 1] = 1004;
            return;
        }
        if ("outerZoomLimit".equals(event.getEventName())) {
            int outerZoomLimit;
            int outerLimit = Ints.constrainToRange((int)this.config.outerLimit(), (int)-400, (int)400);
            intStack[intStackSize - 1] = outerZoomLimit = 128 - outerLimit;
            return;
        }
        if ("scrollWheelZoomIncrement".equals(event.getEventName()) && this.config.zoomIncrement() != 25) {
            intStack[intStackSize - 1] = this.config.zoomIncrement();
            return;
        }
        if (this.config.innerLimit()) {
            double exponent = 2.0;
            switch (event.getEventName()) {
                case "zoomLinToExp": {
                    double range = intStack[intStackSize - 1];
                    double value = intStack[intStackSize - 2];
                    value = Math.pow(value / range, 2.0) * range;
                    intStack[intStackSize - 2] = (int)value;
                    break;
                }
                case "zoomExpToLin": {
                    double range = intStack[intStackSize - 1];
                    double value = intStack[intStackSize - 2];
                    value = Math.pow(value / range, 0.5) * range;
                    intStack[intStackSize - 2] = (int)value;
                    break;
                }
            }
        }
    }

    @Subscribe
    public void onFocusChanged(FocusChanged event) {
        if (!event.isFocused()) {
            this.controlDown = false;
        }
    }

    @Subscribe
    public void onConfigChanged(ConfigChanged ev) {
        if (ev.getGroup().equals("zoom")) {
            this.copyConfigs();
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == 17) {
            this.controlDown = true;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == 17) {
            this.controlDown = false;
            if (this.config.controlFunction() == ControlFunction.CONTROL_TO_RESET) {
                int zoomValue = Ints.constrainToRange((int)this.config.ctrlZoomValue(), (int)-400, (int)1004);
                this.clientThread.invokeLater(() -> this.client.runScript(new Object[]{42, zoomValue, zoomValue}));
            }
        }
    }

    @VisibleForTesting
    boolean hasMenuEntries(MenuEntry[] menuEntries) {
        block5: for (MenuEntry menuEntry : menuEntries) {
            MenuAction action = menuEntry.getType();
            switch (action) {
                case CANCEL: 
                case WALK: {
                    continue block5;
                }
                case EXAMINE_OBJECT: 
                case EXAMINE_NPC: 
                case EXAMINE_ITEM_GROUND: 
                case EXAMINE_ITEM: 
                case CC_OP_LOW_PRIORITY: {
                    if (!this.config.rightClickExamine()) continue block5;
                    return true;
                }
                case GAME_OBJECT_FIRST_OPTION: 
                case GAME_OBJECT_SECOND_OPTION: 
                case GAME_OBJECT_THIRD_OPTION: 
                case GAME_OBJECT_FOURTH_OPTION: 
                case GAME_OBJECT_FIFTH_OPTION: 
                case NPC_FIRST_OPTION: 
                case NPC_SECOND_OPTION: 
                case NPC_THIRD_OPTION: 
                case NPC_FOURTH_OPTION: 
                case NPC_FIFTH_OPTION: 
                case GROUND_ITEM_FIRST_OPTION: 
                case GROUND_ITEM_SECOND_OPTION: 
                case GROUND_ITEM_THIRD_OPTION: 
                case GROUND_ITEM_FOURTH_OPTION: 
                case GROUND_ITEM_FIFTH_OPTION: 
                case PLAYER_FIRST_OPTION: 
                case PLAYER_SECOND_OPTION: 
                case PLAYER_THIRD_OPTION: 
                case PLAYER_FOURTH_OPTION: 
                case PLAYER_FIFTH_OPTION: 
                case PLAYER_SIXTH_OPTION: 
                case PLAYER_SEVENTH_OPTION: 
                case PLAYER_EIGHTH_OPTION: {
                    if (!this.config.rightClickObjects()) continue block5;
                    return true;
                }
                default: {
                    return true;
                }
            }
        }
        return false;
    }

    @Subscribe
    public void onClientTick(ClientTick event) {
        this.menuHasEntries = this.hasMenuEntries(this.client.getMenuEntries());
        this.sliderTooltip = null;
    }

    @Subscribe
    private void onScriptPreFired(ScriptPreFired ev) {
        switch (ev.getScriptId()) {
            case 3885: {
                int arg = this.client.getIntStackSize() - 11;
                int[] is = this.client.getIntStack();
                if (is[arg] != 14) break;
                this.addZoomTooltip(this.client.getScriptActiveWidget());
                break;
            }
            case 833: 
            case 3896: {
                this.sliderTooltip = this.makeSliderTooltip();
            }
        }
    }

    @Subscribe
    private void onWidgetLoaded(WidgetLoaded ev) {
        if (ev.getGroupId() == 116) {
            this.addZoomTooltip(this.client.getWidget(7602233));
        }
    }

    private void addZoomTooltip(Widget w) {
        w.setOnMouseRepeatListener(new Object[]{ev -> {
            this.sliderTooltip = this.makeSliderTooltip();
        }});
    }

    private Tooltip makeSliderTooltip() {
        int value = this.client.getVarcIntValue(74);
        int max = this.config.innerLimit() ? 1004 : 896;
        return new Tooltip("Camera Zoom: " + value + " / " + max);
    }

    @Subscribe
    private void onBeforeRender(BeforeRender ev) {
        if (this.sliderTooltip != null) {
            this.tooltipManager.add(this.sliderTooltip);
        }
    }

    @Override
    public MouseEvent mousePressed(MouseEvent mouseEvent) {
        if (SwingUtilities.isRightMouseButton(mouseEvent) && this.config.rightClickMovesCamera()) {
            boolean oneButton;
            boolean bl = oneButton = this.client.getVarpValue(170) == 1;
            if (!this.menuHasEntries || oneButton) {
                this.rightClick = true;
                mouseEvent = new MouseEvent((Component)mouseEvent.getSource(), mouseEvent.getID(), mouseEvent.getWhen(), mouseEvent.getModifiersEx(), mouseEvent.getX(), mouseEvent.getY(), mouseEvent.getClickCount(), mouseEvent.isPopupTrigger(), 2);
            }
        } else if (SwingUtilities.isMiddleMouseButton(mouseEvent) && this.config.middleClickMenu()) {
            this.middleClick = true;
            mouseEvent = new MouseEvent((Component)mouseEvent.getSource(), mouseEvent.getID(), mouseEvent.getWhen(), mouseEvent.getModifiersEx(), mouseEvent.getX(), mouseEvent.getY(), mouseEvent.getClickCount(), mouseEvent.isPopupTrigger(), 3);
        }
        return mouseEvent;
    }

    @Override
    public MouseEvent mouseReleased(MouseEvent mouseEvent) {
        if (this.rightClick) {
            this.rightClick = false;
            mouseEvent = new MouseEvent((Component)mouseEvent.getSource(), mouseEvent.getID(), mouseEvent.getWhen(), mouseEvent.getModifiersEx(), mouseEvent.getX(), mouseEvent.getY(), mouseEvent.getClickCount(), mouseEvent.isPopupTrigger(), 2);
        }
        if (this.middleClick) {
            this.middleClick = false;
            mouseEvent = new MouseEvent((Component)mouseEvent.getSource(), mouseEvent.getID(), mouseEvent.getWhen(), mouseEvent.getModifiersEx(), mouseEvent.getX(), mouseEvent.getY(), mouseEvent.getClickCount(), mouseEvent.isPopupTrigger(), 3);
        }
        return mouseEvent;
    }

    @Override
    public MouseEvent mouseDragged(MouseEvent mouseEvent) {
        return mouseEvent;
    }

    @Override
    public MouseEvent mouseMoved(MouseEvent mouseEvent) {
        return mouseEvent;
    }

    @Override
    public MouseEvent mouseClicked(MouseEvent mouseEvent) {
        return mouseEvent;
    }

    @Override
    public MouseEvent mouseEntered(MouseEvent mouseEvent) {
        return mouseEvent;
    }

    @Override
    public MouseEvent mouseExited(MouseEvent mouseEvent) {
        return mouseEvent;
    }
}

