/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.inject.Inject
 *  com.google.inject.Provider
 *  com.google.inject.Singleton
 *  net.runelite.api.Client
 *  net.runelite.api.GameState
 *  net.runelite.api.events.GameStateChanged
 *  net.runelite.api.events.ScriptPreFired
 *  net.runelite.api.vars.InputType
 *  net.runelite.api.widgets.Widget
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package net.runelite.client.game.chatbox;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.ScriptPreFired;
import net.runelite.api.vars.InputType;
import net.runelite.api.widgets.Widget;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.game.chatbox.ChatboxInput;
import net.runelite.client.game.chatbox.ChatboxTextInput;
import net.runelite.client.game.chatbox.ChatboxTextMenuInput;
import net.runelite.client.input.KeyListener;
import net.runelite.client.input.KeyManager;
import net.runelite.client.input.MouseListener;
import net.runelite.client.input.MouseManager;
import net.runelite.client.input.MouseWheelListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class ChatboxPanelManager {
    private static final Logger log = LoggerFactory.getLogger(ChatboxPanelManager.class);
    private final Client client;
    private final ClientThread clientThread;
    private final EventBus eventBus;
    private final KeyManager keyManager;
    private final MouseManager mouseManager;
    private final Provider<ChatboxTextMenuInput> chatboxTextMenuInputProvider;
    private final Provider<ChatboxTextInput> chatboxTextInputProvider;
    private ChatboxInput currentInput = null;

    @Inject
    private ChatboxPanelManager(EventBus eventBus, Client client, ClientThread clientThread, KeyManager keyManager, MouseManager mouseManager, Provider<ChatboxTextMenuInput> chatboxTextMenuInputProvider, Provider<ChatboxTextInput> chatboxTextInputProvider) {
        this.client = client;
        this.clientThread = clientThread;
        this.eventBus = eventBus;
        this.keyManager = keyManager;
        this.mouseManager = mouseManager;
        this.chatboxTextMenuInputProvider = chatboxTextMenuInputProvider;
        this.chatboxTextInputProvider = chatboxTextInputProvider;
        eventBus.register(this);
    }

    public void close() {
        this.clientThread.invokeLater(this::unsafeCloseInput);
    }

    private void unsafeCloseInput() {
        this.client.runScript(new Object[]{299, 0, 1, 0});
        if (this.currentInput != null) {
            this.killCurrentPanel();
        }
    }

    private void unsafeOpenInput(ChatboxInput input) {
        this.client.runScript(new Object[]{677, 0});
        this.eventBus.register(input);
        if (input instanceof KeyListener) {
            this.keyManager.registerKeyListener((KeyListener)((Object)input));
        }
        if (input instanceof MouseListener) {
            this.mouseManager.registerMouseListener((MouseListener)((Object)input));
        }
        if (input instanceof MouseWheelListener) {
            this.mouseManager.registerMouseWheelListener((MouseWheelListener)((Object)input));
        }
        if (this.currentInput != null) {
            this.killCurrentPanel();
        }
        this.currentInput = input;
        this.client.setVarcIntValue(5, InputType.RUNELITE_CHATBOX_PANEL.getType());
        this.client.getWidget(0xA2002A).setHidden(true);
        this.client.getWidget(10616875).setHidden(true);
        Widget c = this.getContainerWidget();
        c.deleteAllChildren();
        c.setOnDialogAbortListener(new Object[]{ev -> this.unsafeCloseInput()});
        input.open();
    }

    public void openInput(ChatboxInput input) {
        this.clientThread.invokeLater(() -> this.unsafeOpenInput(input));
    }

    public ChatboxTextMenuInput openTextMenuInput(String title) {
        return ((ChatboxTextMenuInput)this.chatboxTextMenuInputProvider.get()).title(title);
    }

    public ChatboxTextInput openTextInput(String prompt) {
        return ((ChatboxTextInput)this.chatboxTextInputProvider.get()).prompt(prompt);
    }

    @Subscribe
    public void onScriptPreFired(ScriptPreFired ev) {
        if (this.currentInput != null && ev.getScriptId() == 299) {
            this.killCurrentPanel();
        }
    }

    @Subscribe
    private void onGameStateChanged(GameStateChanged ev) {
        if (this.currentInput != null && ev.getGameState() == GameState.LOGIN_SCREEN) {
            this.killCurrentPanel();
        }
    }

    private void killCurrentPanel() {
        try {
            this.currentInput.close();
        }
        catch (Exception e) {
            log.warn("Exception closing {}", this.currentInput.getClass(), (Object)e);
        }
        this.eventBus.unregister(this.currentInput);
        if (this.currentInput instanceof KeyListener) {
            this.keyManager.unregisterKeyListener((KeyListener)((Object)this.currentInput));
        }
        if (this.currentInput instanceof MouseListener) {
            this.mouseManager.unregisterMouseListener((MouseListener)((Object)this.currentInput));
        }
        if (this.currentInput instanceof MouseWheelListener) {
            this.mouseManager.unregisterMouseWheelListener((MouseWheelListener)((Object)this.currentInput));
        }
        this.currentInput = null;
    }

    public Widget getContainerWidget() {
        return this.client.getWidget(10616870);
    }

    public boolean shouldTakeInput() {
        Widget worldMapSearch = this.client.getWidget(38993946);
        return worldMapSearch == null || this.client.getVarcIntValue(190) != 1;
    }

    public ChatboxInput getCurrentInput() {
        return this.currentInput;
    }
}

