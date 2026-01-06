/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.inject.Inject
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package net.runelite.client.plugins.account;

import java.awt.image.BufferedImage;
import java.util.concurrent.ScheduledExecutorService;
import javax.inject.Inject;
import javax.swing.JOptionPane;
import net.runelite.client.account.AccountSession;
import net.runelite.client.account.SessionManager;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.SessionClose;
import net.runelite.client.events.SessionOpen;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.ClientToolbar;
import net.runelite.client.ui.NavigationButton;
import net.runelite.client.util.ImageUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@PluginDescriptor(name="Account", description="Sync RuneLite config settings with your Google account", tags={"external", "google", "integration"}, forceDisabled=true)
public class AccountPlugin
extends Plugin {
    private static final Logger log = LoggerFactory.getLogger(AccountPlugin.class);
    @Inject
    private SessionManager sessionManager;
    @Inject
    private ConfigManager configManager;
    @Inject
    private ClientToolbar clientToolbar;
    @Inject
    private ScheduledExecutorService executor;
    private NavigationButton loginButton;
    private NavigationButton logoutButton;
    private static final BufferedImage LOGIN_IMAGE = ImageUtil.loadImageResource(AccountPlugin.class, "login_icon.png");
    private static final BufferedImage LOGOUT_IMAGE = ImageUtil.loadImageResource(AccountPlugin.class, "logout_icon.png");

    @Override
    protected void startUp() throws Exception {
        this.loginButton = NavigationButton.builder().icon(LOGIN_IMAGE).tooltip("Sign in to RuneLite").onClick(this::loginClick).build();
        this.logoutButton = NavigationButton.builder().icon(LOGOUT_IMAGE).tooltip("Sign out of RuneLite").onClick(this::logoutClick).build();
        this.addAndRemoveButtons();
    }

    private void addAndRemoveButtons() {
        this.clientToolbar.removeNavigation(this.loginButton);
        this.clientToolbar.removeNavigation(this.logoutButton);
        this.clientToolbar.addNavigation(this.sessionManager.getAccountSession() == null ? this.loginButton : this.logoutButton);
    }

    @Override
    protected void shutDown() throws Exception {
        this.clientToolbar.removeNavigation(this.loginButton);
        this.clientToolbar.removeNavigation(this.logoutButton);
    }

    private void loginClick() {
        this.executor.execute(this.sessionManager::login);
    }

    private void logoutClick() {
        if (0 == JOptionPane.showConfirmDialog(null, "Are you sure you want to sign out of RuneLite?", "Sign Out Confirmation", 0)) {
            this.executor.execute(() -> {
                this.configManager.sendConfig();
                this.sessionManager.logout();
            });
        }
    }

    @Subscribe
    public void onSessionClose(SessionClose e) {
        this.addAndRemoveButtons();
    }

    @Subscribe
    public void onSessionOpen(SessionOpen sessionOpen) {
        AccountSession session = this.sessionManager.getAccountSession();
        if (session.getUsername() == null) {
            return;
        }
        log.debug("Session opened as {}", (Object)session.getUsername());
        this.addAndRemoveButtons();
    }
}

