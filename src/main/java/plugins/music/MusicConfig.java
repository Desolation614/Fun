/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.plugins.music;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup(value="music")
public interface MusicConfig
extends Config {
    public static final String GROUP = "music";
    public static final String GRANULAR_SLIDERS = "granularSliders";
    public static final String MUTE_AMBIENT_SOUNDS = "muteAmbientSounds";

    @ConfigItem(keyName="muteOwnAreaSounds", name="Mute player area sounds", description="Mute area sounds caused by yourself.", position=0)
    default public boolean muteOwnAreaSounds() {
        return false;
    }

    @ConfigItem(keyName="muteOtherAreaSounds", name="Mute other players' area sounds", description="Mute area sounds caused by other players.", position=1)
    default public boolean muteOtherAreaSounds() {
        return false;
    }

    @ConfigItem(keyName="muteOtherAreaNPCSounds", name="Mute NPCs' area sounds", description="Mute area sounds caused by NPCs.", position=2)
    default public boolean muteNpcAreaSounds() {
        return false;
    }

    @ConfigItem(keyName="muteOtherAreaEnvironmentSounds", name="Mute environment area sounds", description="Mute area sounds caused by neither NPCs nor players.", position=3)
    default public boolean muteEnvironmentAreaSounds() {
        return false;
    }

    @ConfigItem(keyName="muteAmbientSounds", name="Mute ambient sounds", description="Mute background noise such as magic trees and furnaces.", position=4)
    default public boolean muteAmbientSounds() {
        return false;
    }

    @ConfigItem(keyName="mutePrayerSounds", name="Mute prayer sounds", description="Mute prayer activation and deactivation sounds.", position=5)
    default public boolean mutePrayerSounds() {
        return false;
    }

    @ConfigItem(keyName="granularSliders", name="Granular volume sliders", description="Make the volume sliders allow better control of volume.", position=6)
    default public boolean granularSliders() {
        return true;
    }

    @ConfigItem(keyName="musicVolume", name="", description="", hidden=true)
    default public int getMusicVolume() {
        return 0;
    }

    @ConfigItem(keyName="musicVolume", name="", description="", hidden=true)
    public void setMusicVolume(int var1);

    @ConfigItem(keyName="soundEffectVolume", name="", description="", hidden=true)
    default public int getSoundEffectVolume() {
        return 0;
    }

    @ConfigItem(keyName="soundEffectVolume", name="", description="", hidden=true)
    public void setSoundEffectVolume(int var1);

    @ConfigItem(keyName="areaSoundEffectVolume", name="", description="", hidden=true)
    default public int getAreaSoundEffectVolume() {
        return 0;
    }

    @ConfigItem(keyName="areaSoundEffectVolume", name="", description="", hidden=true)
    public void setAreaSoundEffectVolume(int var1);
}

