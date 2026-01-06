/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.inject.Provides
 *  javax.inject.Inject
 *  net.runelite.api.Client
 *  net.runelite.api.GameState
 *  net.runelite.api.events.ScriptCallbackEvent
 *  net.runelite.api.widgets.Widget
 */
package net.runelite.client.plugins.keyremapping;

import com.google.inject.Provides;
import java.awt.Color;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.events.ScriptCallbackEvent;
import net.runelite.api.widgets.Widget;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.input.KeyManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.keyremapping.KeyRemappingConfig;
import net.runelite.client.plugins.keyremapping.KeyRemappingListener;
import net.runelite.client.ui.JagexColors;
import net.runelite.client.util.ColorUtil;

@PluginDescriptor(name="Key Remapping", description="Allows use of WASD keys for camera movement with 'Press Enter to Chat', and remapping number keys to F-keys", tags={"enter", "chat", "wasd", "camera"}, enabledByDefault=false)
public class KeyRemappingPlugin
extends Plugin {
    private static final String PRESS_ENTER_TO_CHAT = "Press Enter to Chat...";
    @Inject
    private Client client;
    @Inject
    private ClientThread clientThread;
    @Inject
    private KeyManager keyManager;
    @Inject
    private KeyRemappingListener inputListener;
    private boolean typing;

    @Override
    protected void startUp() throws Exception {
        this.typing = false;
        this.keyManager.registerKeyListener(this.inputListener);
        this.clientThread.invoke(() -> {
            if (this.client.getGameState() == GameState.LOGGED_IN) {
                this.lockChat();
                this.client.setVarcStrValue(335, "");
            }
        });
    }

    @Override
    protected void shutDown() throws Exception {
        this.clientThread.invoke(() -> {
            if (this.client.getGameState() == GameState.LOGGED_IN) {
                this.unlockChat();
            }
        });
        this.keyManager.unregisterKeyListener(this.inputListener);
    }

    @Provides
    KeyRemappingConfig getConfig(ConfigManager configManager) {
        return configManager.getConfig(KeyRemappingConfig.class);
    }

    boolean chatboxFocused() {
        Widget chatboxParent = this.client.getWidget(0xA20000);
        if (chatboxParent == null || chatboxParent.getOnKeyListener() == null) {
            return false;
        }
        Widget worldMapSearch = this.client.getWidget(38993946);
        if (worldMapSearch != null && this.client.getVarcIntValue(190) == 1) {
            return false;
        }
        Widget report = this.client.getWidget(57344000);
        return report == null;
    }

    boolean isDialogOpen() {
        return this.isHidden(10616886) || this.isHidden(10616887) || !this.isHidden(0xD50000);
    }

    boolean isOptionsDialogOpen() {
        return this.client.getWidget(14352385) != null;
    }

    private boolean isHidden(int component) {
        Widget w = this.client.getWidget(component);
        return w == null || w.isSelfHidden();
    }

    @Subscribe
    public void onScriptCallbackEvent(ScriptCallbackEvent scriptCallbackEvent) {
        switch (scriptCallbackEvent.getEventName()) {
            case "setChatboxInput": {
                Widget chatboxInput = this.client.getWidget(10616888);
                if (chatboxInput == null || this.typing) break;
                this.setChatboxWidgetInput(chatboxInput, PRESS_ENTER_TO_CHAT);
                break;
            }
            case "blockChatInput": {
                if (this.typing) break;
                int[] intStack = this.client.getIntStack();
                int intStackSize = this.client.getIntStackSize();
                intStack[intStackSize - 1] = 1;
            }
        }
    }

    void lockChat() {
        Widget chatboxInput = this.client.getWidget(10616888);
        if (chatboxInput != null) {
            this.setChatboxWidgetInput(chatboxInput, PRESS_ENTER_TO_CHAT);
        }
    }

    void unlockChat() {
        Widget chatboxInput = this.client.getWidget(10616888);
        if (chatboxInput != null && this.client.getGameState() == GameState.LOGGED_IN) {
            boolean isChatboxTransparent = this.client.isResized() && this.client.getVarbitValue(4608) == 1;
            Color textColor = isChatboxTransparent ? JagexColors.CHAT_TYPED_TEXT_TRANSPARENT_BACKGROUND : JagexColors.CHAT_TYPED_TEXT_OPAQUE_BACKGROUND;
            this.setChatboxWidgetInput(chatboxInput, ColorUtil.wrapWithColorTag(this.client.getVarcStrValue(335) + "*", textColor));
        }
    }

    private void setChatboxWidgetInput(Widget widget, String input) {
        String text = widget.getText();
        int idx = text.indexOf(58);
        if (idx != -1) {
            String newText = text.substring(0, idx) + ": " + input;
            widget.setText(newText);
        }
    }

    boolean isTyping() {
        return this.typing;
    }

    void setTyping(boolean typing) {
        this.typing = typing;
    }
}

