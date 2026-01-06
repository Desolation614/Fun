/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.inject.Inject
 *  com.google.inject.Provides
 *  net.runelite.api.Client
 *  net.runelite.api.GameState
 *  net.runelite.api.Player
 *  net.runelite.api.coords.LocalPoint
 *  net.runelite.api.coords.WorldPoint
 *  net.runelite.api.events.BeforeRender
 *  net.runelite.api.events.GameStateChanged
 */
package net.runelite.client.plugins.skybox;

import com.google.inject.Inject;
import com.google.inject.Provides;
import java.awt.Color;
import java.io.IOException;
import java.io.InputStream;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.Player;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.BeforeRender;
import net.runelite.api.events.GameStateChanged;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.skybox.Skybox;
import net.runelite.client.plugins.skybox.SkyboxPluginConfig;

@PluginDescriptor(name="Skybox", description="Draws an oldschool styled skybox", enabledByDefault=false, tags={"sky"})
public class SkyboxPlugin
extends Plugin {
    @Inject
    private Client client;
    @Inject
    private SkyboxPluginConfig config;
    private Skybox skybox;

    @Override
    public void startUp() throws IOException {
        try (InputStream in = SkyboxPlugin.class.getResourceAsStream("skybox.txt");){
            this.skybox = new Skybox(in, "skybox.txt");
        }
    }

    @Override
    public void shutDown() {
        this.client.setSkyboxColor(0);
        this.skybox = null;
    }

    @Provides
    SkyboxPluginConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(SkyboxPluginConfig.class);
    }

    private int mapChunk(int cx, int cy, int plane) {
        int[][] instanceTemplateChunks = this.client.getInstanceTemplateChunks()[plane];
        if ((cx -= this.client.getBaseX() / 8) < 0 || cx >= instanceTemplateChunks.length || (cy -= this.client.getBaseY() / 8) < 0 || cy >= instanceTemplateChunks[cx].length) {
            return -1;
        }
        return instanceTemplateChunks[cx][cy];
    }

    @Subscribe
    public void onBeforeRender(BeforeRender r) {
        int py;
        int px;
        Color overrideColor;
        if (this.skybox == null || this.client.getGameState() != GameState.LOGGED_IN) {
            return;
        }
        Player player = this.client.getLocalPlayer();
        if (player == null) {
            return;
        }
        Color color = overrideColor = WorldPoint.getMirrorPoint((WorldPoint)player.getWorldLocation(), (boolean)true).getY() < 4160 ? this.config.customOverworldColor() : this.config.customOtherColor();
        if (overrideColor != null) {
            this.client.setSkyboxColor(overrideColor.getRGB());
            return;
        }
        if (this.client.getOculusOrbState() == 1) {
            px = this.client.getOculusOrbFocalPointX();
            py = this.client.getOculusOrbFocalPointY();
        } else {
            LocalPoint p = player.getLocalLocation();
            px = p.getX();
            py = p.getY();
        }
        int spx = -(this.client.getCameraX() - px >> 1);
        int spy = -(this.client.getCameraY() - py >> 1);
        int baseX = this.client.getBaseX();
        int baseY = this.client.getBaseY();
        this.client.setSkyboxColor(this.skybox.getColorForPoint((float)baseX + (float)(px + spx) / 128.0f, (float)baseY + (float)(py + spy) / 128.0f, baseX + px / 128, baseY + py / 128, this.client.getPlane(), this.client.getTextureProvider().getBrightness(), this.client.isInInstancedRegion() ? this::mapChunk : null));
    }

    @Subscribe
    public void onGameStateChanged(GameStateChanged gameStateChanged) {
        if (gameStateChanged.getGameState() == GameState.LOGIN_SCREEN) {
            this.client.setSkyboxColor(0);
        }
    }
}

