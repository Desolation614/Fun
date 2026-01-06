/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  javax.inject.Inject
 *  javax.inject.Singleton
 *  net.runelite.api.Client
 *  net.runelite.api.GameState
 *  net.runelite.api.events.FocusChanged
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package net.runelite.client.input;

import java.awt.event.KeyEvent;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Singleton;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.events.FocusChanged;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.input.KeyListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class KeyManager {
    private static final Logger log = LoggerFactory.getLogger(KeyManager.class);
    private final Client client;
    private final List<KeyListener> keyListeners = new CopyOnWriteArrayList<KeyListener>();

    @Inject
    private KeyManager(@Nullable Client client, EventBus eventBus) {
        this.client = client;
        eventBus.register(this);
    }

    public void registerKeyListener(KeyListener keyListener) {
        if (!this.keyListeners.contains(keyListener)) {
            log.debug("Registering key listener: {}", (Object)keyListener);
            this.keyListeners.add(keyListener);
        }
    }

    public void unregisterKeyListener(KeyListener keyListener) {
        boolean unregistered = this.keyListeners.remove(keyListener);
        if (unregistered) {
            log.debug("Unregistered key listener: {}", (Object)keyListener);
        }
    }

    public void processKeyPressed(KeyEvent keyEvent) {
        if (keyEvent.isConsumed()) {
            return;
        }
        for (KeyListener keyListener : this.keyListeners) {
            if (!this.shouldProcess(keyListener)) continue;
            log.trace("Processing key pressed {} for key listener {}", (Object)keyEvent.paramString(), (Object)keyListener);
            keyListener.keyPressed(keyEvent);
            if (!keyEvent.isConsumed()) continue;
            log.debug("Consuming key pressed {} for key listener {}", (Object)keyEvent.paramString(), (Object)keyListener);
            break;
        }
    }

    public void processKeyReleased(KeyEvent keyEvent) {
        if (keyEvent.isConsumed()) {
            return;
        }
        for (KeyListener keyListener : this.keyListeners) {
            if (!this.shouldProcess(keyListener)) continue;
            log.trace("Processing key released {} for key listener {}", (Object)keyEvent.paramString(), (Object)keyListener);
            keyListener.keyReleased(keyEvent);
            if (!keyEvent.isConsumed()) continue;
            log.debug("Consuming key released {} for listener {}", (Object)keyEvent.paramString(), (Object)keyListener);
            break;
        }
    }

    public void processKeyTyped(KeyEvent keyEvent) {
        if (keyEvent.isConsumed()) {
            return;
        }
        for (KeyListener keyListener : this.keyListeners) {
            if (!this.shouldProcess(keyListener)) continue;
            log.trace("Processing key typed {} for key listener {}", (Object)keyEvent.paramString(), (Object)keyListener);
            keyListener.keyTyped(keyEvent);
            if (!keyEvent.isConsumed()) continue;
            log.debug("Consuming key typed {} for key listener {}", (Object)keyEvent.paramString(), (Object)keyListener);
            break;
        }
    }

    private boolean shouldProcess(KeyListener keyListener) {
        if (this.client == null) {
            return true;
        }
        GameState gameState = this.client.getGameState();
        if (gameState == GameState.LOGIN_SCREEN || gameState == GameState.LOGIN_SCREEN_AUTHENTICATOR) {
            return keyListener.isEnabledOnLoginScreen();
        }
        return true;
    }

    @Subscribe
    private void onFocusChanged(FocusChanged event) {
        if (!event.isFocused()) {
            for (KeyListener keyListener : this.keyListeners) {
                keyListener.focusLost();
            }
        }
    }
}

