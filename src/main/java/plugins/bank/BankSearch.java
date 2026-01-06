/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.inject.Inject
 *  javax.inject.Singleton
 *  net.runelite.api.Client
 *  net.runelite.api.vars.InputType
 *  net.runelite.api.widgets.Widget
 *  org.apache.commons.lang3.ArrayUtils
 */
package net.runelite.client.plugins.bank;

import javax.inject.Inject;
import javax.inject.Singleton;
import net.runelite.api.Client;
import net.runelite.api.vars.InputType;
import net.runelite.api.widgets.Widget;
import net.runelite.client.callback.ClientThread;
import org.apache.commons.lang3.ArrayUtils;

@Singleton
public class BankSearch {
    private final Client client;
    private final ClientThread clientThread;

    @Inject
    private BankSearch(Client client, ClientThread clientThread) {
        this.client = client;
        this.clientThread = clientThread;
    }

    public void layoutBank() {
        Widget bankContainer = this.client.getWidget(786445);
        if (bankContainer == null) {
            return;
        }
        Object[] scriptArgs = bankContainer.getOnInvTransmitListener();
        if (scriptArgs == null) {
            return;
        }
        this.client.runScript(scriptArgs);
    }

    public void initSearch() {
        this.clientThread.invoke(() -> {
            Widget bankContainer = this.client.getWidget(786445);
            if (bankContainer == null || bankContainer.isHidden()) {
                return;
            }
            Object[] bankBuildArgs = bankContainer.getOnInvTransmitListener();
            if (bankBuildArgs == null) {
                return;
            }
            Object[] searchToggleArgs = ArrayUtils.insert((int)1, (Object[])bankBuildArgs, (Object[])new Object[]{1});
            searchToggleArgs[0] = 281;
            this.reset(true);
            this.client.runScript(searchToggleArgs);
        });
    }

    public void reset(boolean closeChat) {
        this.clientThread.invoke(() -> {
            if (closeChat) {
                this.client.runScript(new Object[]{299, 1, 1, 0});
            } else {
                this.client.setVarcIntValue(5, InputType.NONE.getType());
                this.client.setVarcStrValue(359, "");
            }
            this.layoutBank();
        });
    }
}

