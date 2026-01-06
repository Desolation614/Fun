/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.plugins.cooking;

import java.time.Instant;

class CookingSession {
    private Instant lastCookingAction;
    private int cookAmount;
    private int burnAmount;

    CookingSession() {
    }

    void updateLastCookingAction() {
        this.lastCookingAction = Instant.now();
    }

    void increaseCookAmount() {
        ++this.cookAmount;
    }

    void increaseBurnAmount() {
        ++this.burnAmount;
    }

    double getBurntPercentage() {
        return (double)this.getBurnAmount() / (double)(this.getCookAmount() + this.getBurnAmount()) * 100.0;
    }

    Instant getLastCookingAction() {
        return this.lastCookingAction;
    }

    int getCookAmount() {
        return this.cookAmount;
    }

    int getBurnAmount() {
        return this.burnAmount;
    }
}

