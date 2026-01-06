/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.events;

public abstract class ChatInput {
    private final Runnable resume;
    private boolean consumed;

    protected ChatInput(Runnable resume) {
        this.resume = resume;
    }

    public void resume() {
        this.resume.run();
    }

    public void consume() {
        this.consumed = true;
    }

    public boolean isConsumed() {
        return this.consumed;
    }
}

