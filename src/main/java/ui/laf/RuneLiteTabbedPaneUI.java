/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.formdev.flatlaf.ui.FlatStylingSupport$Styleable
 *  com.formdev.flatlaf.ui.FlatTabbedPaneUI
 *  com.formdev.flatlaf.ui.FlatTabbedPaneUI$FlatTabbedPaneLayout
 */
package net.runelite.client.ui.laf;

import com.formdev.flatlaf.ui.FlatStylingSupport;
import com.formdev.flatlaf.ui.FlatTabbedPaneUI;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.JComponent;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;

public class RuneLiteTabbedPaneUI
extends FlatTabbedPaneUI {
    @FlatStylingSupport.Styleable
    protected boolean expandWrappedTabs = UIManager.getBoolean("TabbedPane.expandWrappedTabs");
    @FlatStylingSupport.Styleable
    protected boolean evenlyWrapTabs = UIManager.getBoolean("TabbedPane.evenlyWrapTabs");
    @FlatStylingSupport.Styleable
    protected boolean variableSize = false;
    @FlatStylingSupport.Styleable
    protected boolean deselectable = false;

    public static ComponentUI createUI(JComponent c) {
        return new RuneLiteTabbedPaneUI();
    }

    protected LayoutManager createLayoutManager() {
        if (this.tabPane.getTabLayoutPolicy() == 0) {
            return new RuneLiteTabbedPaneLayout();
        }
        return super.createLayoutManager();
    }

    protected boolean shouldPadTabRun(int tabPlacement, int run) {
        return this.expandWrappedTabs && super.shouldPadTabRun(tabPlacement, run);
    }

    protected MouseListener createMouseListener() {
        final MouseListener delegate = super.createMouseListener();
        return new MouseListener(){

            @Override
            public void mouseClicked(MouseEvent e) {
                delegate.mouseClicked(e);
            }

            @Override
            public void mousePressed(MouseEvent e) {
                this.hackUpdateRollover(e);
                if (!RuneLiteTabbedPaneUI.this.deselectable) {
                    delegate.mousePressed(e);
                    return;
                }
                if (!RuneLiteTabbedPaneUI.this.tabPane.isEnabled() || e.getButton() != 1) {
                    return;
                }
                int tabIndex = RuneLiteTabbedPaneUI.this.tabForCoordinate(RuneLiteTabbedPaneUI.this.tabPane, e.getX(), e.getY());
                if (tabIndex < 0 || !RuneLiteTabbedPaneUI.this.tabPane.isEnabledAt(tabIndex)) {
                    return;
                }
                RuneLiteTabbedPaneUI.this.tabPane.setSelectedIndex(RuneLiteTabbedPaneUI.this.tabPane.getSelectedIndex() == tabIndex ? -1 : tabIndex);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                delegate.mouseClicked(e);
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                delegate.mouseEntered(e);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                delegate.mouseExited(e);
            }

            private void hackUpdateRollover(MouseEvent e) {
                delegate.mouseEntered(e);
            }
        };
    }

    protected class RuneLiteTabbedPaneLayout
    extends FlatTabbedPaneUI.FlatTabbedPaneLayout {
        protected RuneLiteTabbedPaneLayout() {
            super((FlatTabbedPaneUI)RuneLiteTabbedPaneUI.this);
        }

        protected void normalizeTabRuns(int tabPlacement, int tabCount, int start, int max) {
            if (!RuneLiteTabbedPaneUI.this.evenlyWrapTabs) {
                return;
            }
            boolean verticalTabRuns = tabPlacement == 2 || tabPlacement == 4;
            int tab = 0;
            for (int run = 0; run < RuneLiteTabbedPaneUI.this.runCount; ++run) {
                ((RuneLiteTabbedPaneUI)RuneLiteTabbedPaneUI.this).tabRuns[run] = tab;
                int remainingTabs = tabCount - tab;
                int remainingRuns = RuneLiteTabbedPaneUI.this.runCount - run;
                int nextRun = tab + (remainingTabs + remainingRuns - 1) / remainingRuns;
                int off = start;
                for (int i = tab; i < nextRun; ++i) {
                    if (verticalTabRuns) {
                        ((RuneLiteTabbedPaneUI)RuneLiteTabbedPaneUI.this).rects[i].y = off;
                        off += ((RuneLiteTabbedPaneUI)RuneLiteTabbedPaneUI.this).rects[i].height;
                        continue;
                    }
                    ((RuneLiteTabbedPaneUI)RuneLiteTabbedPaneUI.this).rects[i].x = off;
                    off += ((RuneLiteTabbedPaneUI)RuneLiteTabbedPaneUI.this).rects[i].width;
                }
                tab = nextRun;
            }
        }

        protected Dimension calculateSize(boolean minimum) {
            if (!RuneLiteTabbedPaneUI.this.variableSize) {
                return super.calculateSize(minimum);
            }
            int tabPlacement = RuneLiteTabbedPaneUI.this.tabPane.getTabPlacement();
            boolean verticalTabRuns = tabPlacement == 2 || tabPlacement == 4;
            int width = 0;
            int height = 0;
            Insets contentInsets = RuneLiteTabbedPaneUI.this.getContentBorderInsets(tabPlacement);
            Insets insets = RuneLiteTabbedPaneUI.this.tabPane.getInsets();
            Insets tabAreaInsets = RuneLiteTabbedPaneUI.this.getTabAreaInsets(tabPlacement);
            int xInsets = insets.left + insets.right + contentInsets.left + contentInsets.right;
            int yInsets = insets.bottom + insets.top + contentInsets.top + contentInsets.bottom;
            Component component = RuneLiteTabbedPaneUI.this.tabPane.getSelectedComponent();
            if (component != null) {
                Dimension size;
                Dimension dimension = size = minimum ? component.getMinimumSize() : component.getPreferredSize();
                if (size != null) {
                    width = Math.max(0, size.width);
                    height = Math.max(0, size.height);
                }
            }
            if (verticalTabRuns) {
                height = Math.max(height, RuneLiteTabbedPaneUI.this.calculateMaxTabHeight(tabPlacement));
                width += this.preferredTabAreaWidth(tabPlacement, RuneLiteTabbedPaneUI.this.tabPane.getHeight() - yInsets - tabAreaInsets.top - tabAreaInsets.bottom);
            } else {
                width = Math.max(width, RuneLiteTabbedPaneUI.this.calculateMaxTabWidth(tabPlacement));
                height += this.preferredTabAreaHeight(tabPlacement, RuneLiteTabbedPaneUI.this.tabPane.getWidth() - xInsets - tabAreaInsets.left - tabAreaInsets.right);
            }
            return new Dimension(width + xInsets, height + yInsets);
        }
    }
}

