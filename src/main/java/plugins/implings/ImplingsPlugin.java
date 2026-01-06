/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.inject.Provides
 *  javax.inject.Inject
 *  net.runelite.api.NPC
 *  net.runelite.api.events.NpcChanged
 *  net.runelite.api.events.NpcSpawned
 */
package net.runelite.client.plugins.implings;

import com.google.inject.Provides;
import java.awt.Color;
import java.util.function.Function;
import javax.inject.Inject;
import net.runelite.api.NPC;
import net.runelite.api.events.NpcChanged;
import net.runelite.api.events.NpcSpawned;
import net.runelite.client.Notifier;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.game.npcoverlay.HighlightedNpc;
import net.runelite.client.game.npcoverlay.NpcOverlayService;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.implings.Impling;
import net.runelite.client.plugins.implings.ImplingType;
import net.runelite.client.plugins.implings.ImplingsConfig;
import net.runelite.client.plugins.implings.ImplingsOverlay;
import net.runelite.client.ui.overlay.OverlayManager;

@PluginDescriptor(name="Implings", description="Highlight nearby implings on the minimap and on-screen", tags={"hunter", "minimap", "overlay", "imp"})
public class ImplingsPlugin
extends Plugin {
    @Inject
    private OverlayManager overlayManager;
    @Inject
    private ImplingsOverlay overlay;
    @Inject
    private ImplingsConfig config;
    @Inject
    private Notifier notifier;
    @Inject
    private NpcOverlayService npcOverlayService;
    public final Function<NPC, HighlightedNpc> isTarget = npc -> {
        Impling impling = Impling.findImpling(npc.getId());
        if (impling != null && this.showImpling(impling)) {
            Color color = this.implingColor(impling);
            return HighlightedNpc.builder().npc((NPC)npc).highlightColor(color).tile(true).name(true).nameOnMinimap(this.config.showName()).build();
        }
        return null;
    };

    @Provides
    ImplingsConfig getConfig(ConfigManager configManager) {
        return configManager.getConfig(ImplingsConfig.class);
    }

    @Override
    protected void startUp() {
        this.overlayManager.add(this.overlay);
        this.npcOverlayService.registerHighlighter(this.isTarget);
    }

    @Override
    protected void shutDown() {
        this.npcOverlayService.unregisterHighlighter(this.isTarget);
        this.overlayManager.remove(this.overlay);
    }

    @Subscribe
    private void onConfigChanged(ConfigChanged event) {
        if (!event.getGroup().equals("implings")) {
            return;
        }
        this.npcOverlayService.rebuild();
    }

    @Subscribe
    public void onNpcSpawned(NpcSpawned npcSpawned) {
        NPC npc = npcSpawned.getNpc();
        Impling impling = Impling.findImpling(npc.getId());
        if (impling != null && this.showImplingType(impling.getImplingType()) == ImplingsConfig.ImplingMode.NOTIFY) {
            this.notifier.notify(impling.getImplingType().getName() + " impling is in the area");
        }
    }

    @Subscribe
    public void onNpcChanged(NpcChanged npcCompositionChanged) {
        NPC npc = npcCompositionChanged.getNpc();
        Impling impling = Impling.findImpling(npc.getId());
        if (impling != null && this.showImplingType(impling.getImplingType()) == ImplingsConfig.ImplingMode.NOTIFY) {
            this.notifier.notify(impling.getImplingType().getName() + " impling is in the area");
        }
    }

    private boolean showImpling(Impling impling) {
        ImplingsConfig.ImplingMode impMode = this.showImplingType(impling.getImplingType());
        return impMode == ImplingsConfig.ImplingMode.HIGHLIGHT || impMode == ImplingsConfig.ImplingMode.NOTIFY;
    }

    ImplingsConfig.ImplingMode showImplingType(ImplingType implingType) {
        switch (implingType) {
            case BABY: {
                return this.config.showBaby();
            }
            case YOUNG: {
                return this.config.showYoung();
            }
            case GOURMET: {
                return this.config.showGourmet();
            }
            case EARTH: {
                return this.config.showEarth();
            }
            case ESSENCE: {
                return this.config.showEssence();
            }
            case ECLECTIC: {
                return this.config.showEclectic();
            }
            case NATURE: {
                return this.config.showNature();
            }
            case MAGPIE: {
                return this.config.showMagpie();
            }
            case NINJA: {
                return this.config.showNinja();
            }
            case CRYSTAL: {
                return this.config.showCrystal();
            }
            case DRAGON: {
                return this.config.showDragon();
            }
            case LUCKY: {
                return this.config.showLucky();
            }
        }
        return ImplingsConfig.ImplingMode.NONE;
    }

    private Color implingColor(Impling impling) {
        switch (impling.getImplingType()) {
            case BABY: {
                return this.config.getBabyColor();
            }
            case YOUNG: {
                return this.config.getYoungColor();
            }
            case GOURMET: {
                return this.config.getGourmetColor();
            }
            case EARTH: {
                return this.config.getEarthColor();
            }
            case ESSENCE: {
                return this.config.getEssenceColor();
            }
            case ECLECTIC: {
                return this.config.getEclecticColor();
            }
            case NATURE: {
                return this.config.getNatureColor();
            }
            case MAGPIE: {
                return this.config.getMagpieColor();
            }
            case NINJA: {
                return this.config.getNinjaColor();
            }
            case CRYSTAL: {
                return this.config.getCrystalColor();
            }
            case DRAGON: {
                return this.config.getDragonColor();
            }
            case LUCKY: {
                return this.config.getLuckyColor();
            }
        }
        throw new IllegalArgumentException();
    }
}

