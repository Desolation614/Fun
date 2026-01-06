/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.ui;

import java.awt.CardLayout;
import java.awt.Component;
import net.runelite.client.ui.PluginPanel;

public class MultiplexingPluginPanel
extends PluginPanel {
    private final CardLayout layout = new CardLayout();
    private boolean active = false;
    private PluginPanel current;

    public MultiplexingPluginPanel(PluginPanel root) {
        super(false);
        this.setLayout(this.layout);
        this.pushState(root);
    }

    public void destroy() {
        for (int i = this.getComponentCount() - 1; i > 0; --i) {
            this.onRemove((PluginPanel)this.getComponent(i));
            this.remove(i);
        }
    }

    public void pushState(PluginPanel subpanel) {
        int index = -1;
        for (int i = this.getComponentCount() - 1; i >= 0; --i) {
            if (this.getComponent(i) != subpanel) continue;
            index = i;
            break;
        }
        if (this.active) {
            this.current.onDeactivate();
            subpanel.onActivate();
        }
        this.current = subpanel;
        String name = "" + System.identityHashCode(subpanel);
        if (index != -1) {
            for (int i = this.getComponentCount() - 1; i > index; --i) {
                this.popState();
            }
        } else {
            this.add((Component)subpanel, name);
            this.onAdd(subpanel);
        }
        this.layout.show(this, name);
        this.revalidate();
    }

    public void popState() {
        int count = this.getComponentCount();
        if (count <= 1) {
            assert (false) : "Cannot pop last component";
            return;
        }
        PluginPanel subpanel = (PluginPanel)this.getComponent(count - 2);
        if (this.active) {
            this.current.onDeactivate();
            subpanel.onActivate();
            this.current = subpanel;
        }
        this.layout.show(this, "" + System.identityHashCode(subpanel));
        this.onRemove((PluginPanel)this.getComponent(count - 1));
        this.remove(count - 1);
        this.revalidate();
    }

    protected void onAdd(PluginPanel p) {
    }

    protected void onRemove(PluginPanel p) {
    }

    @Override
    public void onActivate() {
        this.active = true;
        this.current.onActivate();
    }

    @Override
    public void onDeactivate() {
        this.active = false;
        this.current.onDeactivate();
    }
}

