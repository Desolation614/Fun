/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.ui;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Insets;
import java.util.function.Function;

public class DynamicGridLayout
extends GridLayout {
    public DynamicGridLayout() {
        this(1, 0, 0, 0);
    }

    public DynamicGridLayout(int rows, int cols) {
        this(rows, cols, 0, 0);
    }

    public DynamicGridLayout(int rows, int cols, int hgap, int vgap) {
        super(rows, cols, hgap, vgap);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Dimension preferredLayoutSize(Container parent) {
        Object object = parent.getTreeLock();
        synchronized (object) {
            return this.calculateSize(parent, Component::getPreferredSize);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Dimension minimumLayoutSize(Container parent) {
        Object object = parent.getTreeLock();
        synchronized (object) {
            return this.calculateSize(parent, Component::getMinimumSize);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void layoutContainer(Container parent) {
        Object object = parent.getTreeLock();
        synchronized (object) {
            Insets insets = parent.getInsets();
            int ncomponents = parent.getComponentCount();
            int nrows = this.getRows();
            int ncols = this.getColumns();
            if (ncomponents == 0) {
                return;
            }
            if (nrows > 0) {
                ncols = (ncomponents + nrows - 1) / nrows;
            } else {
                nrows = (ncomponents + ncols - 1) / ncols;
            }
            int hgap = this.getHgap();
            int vgap = this.getVgap();
            Dimension pd = this.preferredLayoutSize(parent);
            Insets parentInsets = parent.getInsets();
            int wborder = parentInsets.left + parentInsets.right;
            int hborder = parentInsets.top + parentInsets.bottom;
            double sw = (1.0 * (double)parent.getWidth() - (double)wborder) / (double)(pd.width - wborder);
            double sh = (1.0 * (double)parent.getHeight() - (double)hborder) / (double)(pd.height - hborder);
            int[] w = new int[ncols];
            int[] h = new int[nrows];
            for (int i = 0; i < ncomponents; ++i) {
                int r = i / ncols;
                int c = i % ncols;
                Component comp = parent.getComponent(i);
                Dimension d = comp.getPreferredSize();
                d.width = (int)(sw * (double)d.width);
                d.height = (int)(sh * (double)d.height);
                if (w[c] < d.width) {
                    w[c] = d.width;
                }
                if (h[r] >= d.height) continue;
                h[r] = d.height;
            }
            int x = insets.left;
            for (int c = 0; c < ncols; ++c) {
                int y = insets.top;
                for (int r = 0; r < nrows; ++r) {
                    int i = r * ncols + c;
                    if (i < ncomponents) {
                        parent.getComponent(i).setBounds(x, y, w[c], h[r]);
                    }
                    y += h[r] + vgap;
                }
                x += w[c] + hgap;
            }
        }
    }

    private Dimension calculateSize(Container parent, Function<Component, Dimension> sizer) {
        int ncomponents = parent.getComponentCount();
        int nrows = this.getRows();
        int ncols = this.getColumns();
        if (nrows > 0) {
            ncols = (ncomponents + nrows - 1) / nrows;
        } else {
            nrows = (ncomponents + ncols - 1) / ncols;
        }
        int[] w = new int[ncols];
        int[] h = new int[nrows];
        for (int i = 0; i < ncomponents; ++i) {
            int r = i / ncols;
            int c = i % ncols;
            Component comp = parent.getComponent(i);
            Dimension d = sizer.apply(comp);
            if (w[c] < d.width) {
                w[c] = d.width;
            }
            if (h[r] >= d.height) continue;
            h[r] = d.height;
        }
        int nw = 0;
        for (int j = 0; j < ncols; ++j) {
            nw += w[j];
        }
        int nh = 0;
        for (int i = 0; i < nrows; ++i) {
            nh += h[i];
        }
        Insets insets = parent.getInsets();
        return new Dimension(insets.left + insets.right + nw + (ncols - 1) * this.getHgap(), insets.top + insets.bottom + nh + (nrows - 1) * this.getVgap());
    }
}

