/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.plugins.gpu;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.Range;
import net.runelite.client.plugins.gpu.config.AntiAliasingMode;
import net.runelite.client.plugins.gpu.config.ColorBlindMode;
import net.runelite.client.plugins.gpu.config.UIScalingMode;

@ConfigGroup(value="gpu")
public interface GpuPluginConfig
extends Config {
    public static final String GROUP = "gpu";

    @Range(max=184)
    @ConfigItem(keyName="drawDistance", name="Draw distance", description="Draw distance.", position=1)
    default public int drawDistance() {
        return 50;
    }

    @ConfigItem(keyName="hideUnrelatedMaps", name="Hide unrelated maps", description="Hide unrelated map areas you shouldn't see.", position=2)
    default public boolean hideUnrelatedMaps() {
        return true;
    }

    @Range(max=5)
    @ConfigItem(keyName="expandedMapLoadingChunks", name="Extended map loading", description="Extra map area to load, in 8 tile chunks.", position=1)
    default public int expandedMapLoadingChunks() {
        return 3;
    }

    @ConfigItem(keyName="smoothBanding", name="Remove color banding", description="Smooths out the color banding that is present in the CPU renderer.", position=2)
    default public boolean smoothBanding() {
        return true;
    }

    @ConfigItem(keyName="antiAliasingMode", name="Anti aliasing", description="Configures the anti-aliasing mode.", position=3)
    default public AntiAliasingMode antiAliasingMode() {
        return AntiAliasingMode.MSAA_2;
    }

    @ConfigItem(keyName="uiScalingMode", name="UI scaling mode", description="Sampling function to use for the UI in stretched mode.", position=4)
    default public UIScalingMode uiScalingMode() {
        return UIScalingMode.LINEAR;
    }

    @Range(max=100)
    @ConfigItem(keyName="fogDepth", name="Fog depth", description="Distance from the scene edge the fog starts.", position=5)
    default public int fogDepth() {
        return 0;
    }

    @ConfigItem(keyName="useComputeShaders", name="Compute shaders", description="Offloads face sorting to GPU. Requires plugin restart.", warning="This feature requires OpenGL 4.3 to use. Please check that your GPU supports this.\nRestart the plugin for changes to take effect.", position=6)
    default public boolean useComputeShaders() {
        return true;
    }

    @Range(min=0, max=16)
    @ConfigItem(keyName="anisotropicFilteringLevel", name="Anisotropic filtering", description="Configures the anisotropic filtering level.", position=7)
    default public int anisotropicFilteringLevel() {
        return 1;
    }

    @ConfigItem(keyName="colorBlindMode", name="Colorblindness correction", description="Adjusts colors to account for colorblindness.", position=8)
    default public ColorBlindMode colorBlindMode() {
        return ColorBlindMode.NONE;
    }

    @ConfigItem(keyName="brightTextures", name="Bright textures", description="Use old texture lighting method which results in brighter game textures.", position=9)
    default public boolean brightTextures() {
        return false;
    }

    @ConfigItem(keyName="unlockFps", name="Unlock FPS", description="Removes the 50 FPS cap for camera movement.", position=10)
    default public boolean unlockFps() {
        return true;
    }

    @ConfigItem(keyName="vsyncMode", name="Vsync mode", description="Method to synchronize frame rate with refresh rate.", position=11)
    default public SyncMode syncMode() {
        return SyncMode.OFF;
    }

    @ConfigItem(keyName="fpsTarget", name="FPS target", description="Target FPS when 'Unlock FPS' is enabled and 'Vsync mode' is off.", position=12)
    @Range(min=1, max=999)
    default public int fpsTarget() {
        return 60;
    }

    @ConfigItem(keyName="removeVertexSnapping", name="Remove vertex snapping", description="Removes vertex snapping from most animations.", position=13)
    default public boolean removeVertexSnapping() {
        return true;
    }

    public static enum SyncMode {
        OFF,
        ON,
        ADAPTIVE;

    }
}

