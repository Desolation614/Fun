/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Strings
 *  javax.inject.Inject
 *  net.runelite.api.Client
 */
package net.runelite.client.plugins.keyremapping;

import com.google.common.base.Strings;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.input.KeyListener;
import net.runelite.client.plugins.keyremapping.KeyRemappingConfig;
import net.runelite.client.plugins.keyremapping.KeyRemappingPlugin;

class KeyRemappingListener
implements KeyListener {
    @Inject
    private KeyRemappingPlugin plugin;
    @Inject
    private KeyRemappingConfig config;
    @Inject
    private Client client;
    @Inject
    private ClientThread clientThread;
    private final Map<Integer, Integer> modified = new HashMap<Integer, Integer>();
    private final Set<Character> blockedChars = new HashSet<Character>();

    KeyRemappingListener() {
    }

    @Override
    public void keyTyped(KeyEvent e) {
        char keyChar = e.getKeyChar();
        if (keyChar != '\uffff' && this.blockedChars.contains(Character.valueOf(keyChar)) && this.plugin.chatboxFocused()) {
            e.consume();
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (!this.plugin.chatboxFocused()) {
            return;
        }
        if (!this.plugin.isTyping()) {
            int mappedKeyCode = 0;
            if (this.config.cameraRemap()) {
                if (this.config.up().matches(e)) {
                    mappedKeyCode = 38;
                } else if (this.config.down().matches(e)) {
                    mappedKeyCode = 40;
                } else if (this.config.left().matches(e)) {
                    mappedKeyCode = 37;
                } else if (this.config.right().matches(e)) {
                    mappedKeyCode = 39;
                }
            }
            if (this.config.fkeyRemap() && !this.plugin.isDialogOpen()) {
                if (this.config.f1().matches(e)) {
                    mappedKeyCode = 112;
                } else if (this.config.f2().matches(e)) {
                    mappedKeyCode = 113;
                } else if (this.config.f3().matches(e)) {
                    mappedKeyCode = 114;
                } else if (this.config.f4().matches(e)) {
                    mappedKeyCode = 115;
                } else if (this.config.f5().matches(e)) {
                    mappedKeyCode = 116;
                } else if (this.config.f6().matches(e)) {
                    mappedKeyCode = 117;
                } else if (this.config.f7().matches(e)) {
                    mappedKeyCode = 118;
                } else if (this.config.f8().matches(e)) {
                    mappedKeyCode = 119;
                } else if (this.config.f9().matches(e)) {
                    mappedKeyCode = 120;
                } else if (this.config.f10().matches(e)) {
                    mappedKeyCode = 121;
                } else if (this.config.f11().matches(e)) {
                    mappedKeyCode = 122;
                } else if (this.config.f12().matches(e)) {
                    mappedKeyCode = 123;
                } else if (this.config.esc().matches(e)) {
                    mappedKeyCode = 27;
                }
            }
            if (this.plugin.isDialogOpen() && !this.plugin.isOptionsDialogOpen() && this.config.space().matches(e)) {
                mappedKeyCode = 32;
            }
            if (!this.plugin.isOptionsDialogOpen() && this.config.control().matches(e)) {
                mappedKeyCode = 17;
            }
            if (mappedKeyCode != 0 && mappedKeyCode != e.getKeyCode()) {
                char keyChar = e.getKeyChar();
                this.modified.put(e.getKeyCode(), mappedKeyCode);
                e.setKeyCode(mappedKeyCode);
                e.setKeyChar('\uffff');
                if (keyChar != '\uffff') {
                    this.blockedChars.add(Character.valueOf(keyChar));
                }
            }
            switch (e.getKeyCode()) {
                case 10: 
                case 47: 
                case 513: {
                    this.plugin.setTyping(true);
                    this.clientThread.invoke(this.plugin::unlockChat);
                }
            }
        } else {
            switch (e.getKeyCode()) {
                case 27: {
                    e.consume();
                    this.plugin.setTyping(false);
                    this.clientThread.invoke(() -> {
                        this.client.setVarcStrValue(335, "");
                        this.plugin.lockChat();
                    });
                    break;
                }
                case 10: {
                    this.plugin.setTyping(false);
                    this.clientThread.invoke(this.plugin::lockChat);
                    break;
                }
                case 8: {
                    if (!Strings.isNullOrEmpty((String)this.client.getVarcStrValue(335))) break;
                    this.plugin.setTyping(false);
                    this.clientThread.invoke(this.plugin::lockChat);
                }
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        Integer mappedKeyCode;
        int keyCode = e.getKeyCode();
        char keyChar = e.getKeyChar();
        if (keyChar != '\uffff') {
            this.blockedChars.remove(Character.valueOf(keyChar));
        }
        if ((mappedKeyCode = this.modified.remove(keyCode)) != null) {
            e.setKeyCode(mappedKeyCode);
            e.setKeyChar('\uffff');
        }
    }
}

