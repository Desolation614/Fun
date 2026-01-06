/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.inject.Inject
 *  javax.inject.Singleton
 */
package net.runelite.client.ui;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.swing.SwingUtilities;
import net.runelite.client.ui.ClientUI;
import net.runelite.client.ui.NavigationButton;

@Singleton
public class ClientToolbar {
    private final ClientUI clientUI;

    @Inject
    private ClientToolbar(ClientUI clientUI) {
        this.clientUI = clientUI;
    }

    public void addNavigation(NavigationButton button) {
        SwingUtilities.invokeLater(() -> this.clientUI.addNavigation(button));
    }

    public void removeNavigation(NavigationButton button) {
        SwingUtilities.invokeLater(() -> this.clientUI.removeNavigation(button));
    }

    public void openPanel(NavigationButton button) {
        assert (SwingUtilities.isEventDispatchThread()) : "must be on EDT";
        this.clientUI.openPanel(button, true);
    }
}

