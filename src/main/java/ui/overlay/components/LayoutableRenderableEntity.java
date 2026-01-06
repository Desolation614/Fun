/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.ui.overlay.components;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import net.runelite.client.ui.overlay.RenderableEntity;

public interface LayoutableRenderableEntity
extends RenderableEntity {
    public Rectangle getBounds();

    public void setPreferredLocation(Point var1);

    public void setPreferredSize(Dimension var1);
}

