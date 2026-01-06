/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Strings
 */
package net.runelite.client.plugins.cluescrolls.clues;

import com.google.common.base.Strings;
import java.awt.Graphics2D;
import net.runelite.client.plugins.cluescrolls.ClueScrollOverlay;
import net.runelite.client.plugins.cluescrolls.ClueScrollPlugin;
import net.runelite.client.plugins.cluescrolls.clues.Enemy;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.PanelComponent;

public abstract class ClueScroll {
    private boolean requiresSpade;
    private boolean requiresLight;
    private int firePitVarbitId = -1;
    private Enemy enemy;

    public abstract void makeOverlayHint(PanelComponent var1, ClueScrollPlugin var2);

    public abstract void makeWorldOverlayHint(Graphics2D var1, ClueScrollPlugin var2);

    public int[] getConfigKeys() {
        return null;
    }

    public void renderOverlayNote(PanelComponent panelComponent, ClueScrollPlugin plugin) {
        int[] keys = this.getConfigKeys();
        if (keys == null) {
            return;
        }
        String note = plugin.getClueNote(keys[0]);
        if (!Strings.isNullOrEmpty((String)note)) {
            panelComponent.getChildren().add(LineComponent.builder().left("Note:").build());
            panelComponent.getChildren().add(LineComponent.builder().left(note).leftColor(ClueScrollOverlay.TITLED_CONTENT_COLOR).build());
        }
    }

    protected void setRequiresSpade(boolean requiresSpade) {
        this.requiresSpade = requiresSpade;
    }

    public boolean isRequiresSpade() {
        return this.requiresSpade;
    }

    protected void setRequiresLight(boolean requiresLight) {
        this.requiresLight = requiresLight;
    }

    public boolean isRequiresLight() {
        return this.requiresLight;
    }

    protected void setFirePitVarbitId(int firePitVarbitId) {
        this.firePitVarbitId = firePitVarbitId;
    }

    public int getFirePitVarbitId() {
        return this.firePitVarbitId;
    }

    protected void setEnemy(Enemy enemy) {
        this.enemy = enemy;
    }

    public Enemy getEnemy() {
        return this.enemy;
    }
}

