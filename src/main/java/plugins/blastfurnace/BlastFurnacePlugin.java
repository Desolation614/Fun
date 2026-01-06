/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.inject.Provides
 *  javax.inject.Inject
 *  net.runelite.api.Client
 *  net.runelite.api.GameObject
 *  net.runelite.api.GameState
 *  net.runelite.api.Skill
 *  net.runelite.api.events.GameObjectDespawned
 *  net.runelite.api.events.GameObjectSpawned
 *  net.runelite.api.events.GameStateChanged
 *  net.runelite.api.events.GameTick
 *  net.runelite.api.widgets.Widget
 */
package net.runelite.client.plugins.blastfurnace;

import com.google.inject.Provides;
import java.time.Duration;
import java.time.Instant;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.GameObject;
import net.runelite.api.GameState;
import net.runelite.api.Skill;
import net.runelite.api.events.GameObjectDespawned;
import net.runelite.api.events.GameObjectSpawned;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.api.widgets.Widget;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.blastfurnace.BlastFurnaceClickBoxOverlay;
import net.runelite.client.plugins.blastfurnace.BlastFurnaceCofferOverlay;
import net.runelite.client.plugins.blastfurnace.BlastFurnaceConfig;
import net.runelite.client.plugins.blastfurnace.BlastFurnaceOverlay;
import net.runelite.client.plugins.blastfurnace.ForemanTimer;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.ui.overlay.infobox.InfoBoxManager;
import net.runelite.client.util.Text;

@PluginDescriptor(name="Blast Furnace", description="Show helpful information for the Blast Furnace minigame", tags={"minigame", "overlay", "skilling", "smithing"})
public class BlastFurnacePlugin
extends Plugin {
    private static final int BAR_DISPENSER = 9092;
    private static final String FOREMAN_PERMISSION_TEXT = "Okay, you can use the furnace for ten minutes. Remember, you only need half as much coal as with a regular furnace.";
    private GameObject conveyorBelt;
    private GameObject barDispenser;
    private ForemanTimer foremanTimer;
    @Inject
    private OverlayManager overlayManager;
    @Inject
    private BlastFurnaceOverlay overlay;
    @Inject
    private BlastFurnaceCofferOverlay cofferOverlay;
    @Inject
    private BlastFurnaceClickBoxOverlay clickBoxOverlay;
    @Inject
    private Client client;
    @Inject
    private ItemManager itemManager;
    @Inject
    private InfoBoxManager infoBoxManager;

    @Override
    protected void startUp() throws Exception {
        this.overlayManager.add(this.overlay);
        this.overlayManager.add(this.cofferOverlay);
        this.overlayManager.add(this.clickBoxOverlay);
    }

    @Override
    protected void shutDown() {
        this.infoBoxManager.removeIf(ForemanTimer.class::isInstance);
        this.overlayManager.remove(this.overlay);
        this.overlayManager.remove(this.cofferOverlay);
        this.overlayManager.remove(this.clickBoxOverlay);
        this.conveyorBelt = null;
        this.barDispenser = null;
        this.foremanTimer = null;
    }

    @Provides
    BlastFurnaceConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(BlastFurnaceConfig.class);
    }

    @Subscribe
    public void onGameObjectSpawned(GameObjectSpawned event) {
        GameObject gameObject = event.getGameObject();
        switch (gameObject.getId()) {
            case 9100: {
                this.conveyorBelt = gameObject;
                break;
            }
            case 9092: {
                this.barDispenser = gameObject;
            }
        }
    }

    @Subscribe
    public void onGameObjectDespawned(GameObjectDespawned event) {
        GameObject gameObject = event.getGameObject();
        switch (gameObject.getId()) {
            case 9100: {
                this.conveyorBelt = null;
                break;
            }
            case 9092: {
                this.barDispenser = null;
            }
        }
    }

    @Subscribe
    public void onGameStateChanged(GameStateChanged event) {
        if (event.getGameState() == GameState.LOADING) {
            this.conveyorBelt = null;
            this.barDispenser = null;
        }
    }

    @Subscribe
    public void onGameTick(GameTick event) {
        String npcText;
        boolean shouldCheckForemanFee;
        Widget npcDialog = this.client.getWidget(15138822);
        if (npcDialog == null) {
            return;
        }
        boolean bl = shouldCheckForemanFee = this.client.getRealSkillLevel(Skill.SMITHING) < 60 && (this.foremanTimer == null || Duration.between(Instant.now(), this.foremanTimer.getEndTime()).toMinutes() <= 5L);
        if (shouldCheckForemanFee && (npcText = Text.sanitizeMultilineText(npcDialog.getText())).equals(FOREMAN_PERMISSION_TEXT)) {
            this.infoBoxManager.removeIf(ForemanTimer.class::isInstance);
            this.foremanTimer = new ForemanTimer(this, this.itemManager);
            this.infoBoxManager.addInfoBox(this.foremanTimer);
        }
    }

    GameObject getConveyorBelt() {
        return this.conveyorBelt;
    }

    GameObject getBarDispenser() {
        return this.barDispenser;
    }
}

