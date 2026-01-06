/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.plugins.keyremapping;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;
import net.runelite.client.config.ModifierlessKeybind;

@ConfigGroup(value="keyremapping")
public interface KeyRemappingConfig
extends Config {
    @ConfigSection(name="Camera remapping", description="Settings for remapping the camera.", position=0)
    public static final String cameraSection = "camera";
    @ConfigSection(name="F-key remapping", description="Settings for remapping the F-keys.", position=1)
    public static final String fKeySection = "fKeys";

    @ConfigItem(position=1, keyName="cameraRemap", name="Remap camera", description="Configures whether the camera movement uses remapped keys.", section="camera")
    default public boolean cameraRemap() {
        return true;
    }

    @ConfigItem(position=2, keyName="up", name="Camera up key", description="The key which will replace up.", section="camera")
    default public ModifierlessKeybind up() {
        return new ModifierlessKeybind(87, 0);
    }

    @ConfigItem(position=3, keyName="down", name="Camera down key", description="The key which will replace down.", section="camera")
    default public ModifierlessKeybind down() {
        return new ModifierlessKeybind(83, 0);
    }

    @ConfigItem(position=4, keyName="left", name="Camera left key", description="The key which will replace left.", section="camera")
    default public ModifierlessKeybind left() {
        return new ModifierlessKeybind(65, 0);
    }

    @ConfigItem(position=5, keyName="right", name="Camera right key", description="The key which will replace right.", section="camera")
    default public ModifierlessKeybind right() {
        return new ModifierlessKeybind(68, 0);
    }

    @ConfigItem(position=6, keyName="fkeyRemap", name="Remap F-keys", description="Configures whether f-keys use remapped keys.", section="fKeys")
    default public boolean fkeyRemap() {
        return false;
    }

    @ConfigItem(position=7, keyName="f1", name="F1", description="The key which will replace F1.", section="fKeys")
    default public ModifierlessKeybind f1() {
        return new ModifierlessKeybind(49, 0);
    }

    @ConfigItem(position=8, keyName="f2", name="F2", description="The key which will replace F2.", section="fKeys")
    default public ModifierlessKeybind f2() {
        return new ModifierlessKeybind(50, 0);
    }

    @ConfigItem(position=9, keyName="f3", name="F3", description="The key which will replace F3.", section="fKeys")
    default public ModifierlessKeybind f3() {
        return new ModifierlessKeybind(51, 0);
    }

    @ConfigItem(position=10, keyName="f4", name="F4", description="The key which will replace F4.", section="fKeys")
    default public ModifierlessKeybind f4() {
        return new ModifierlessKeybind(52, 0);
    }

    @ConfigItem(position=11, keyName="f5", name="F5", description="The key which will replace F5.", section="fKeys")
    default public ModifierlessKeybind f5() {
        return new ModifierlessKeybind(53, 0);
    }

    @ConfigItem(position=12, keyName="f6", name="F6", description="The key which will replace F6.", section="fKeys")
    default public ModifierlessKeybind f6() {
        return new ModifierlessKeybind(54, 0);
    }

    @ConfigItem(position=13, keyName="f7", name="F7", description="The key which will replace F7.", section="fKeys")
    default public ModifierlessKeybind f7() {
        return new ModifierlessKeybind(55, 0);
    }

    @ConfigItem(position=14, keyName="f8", name="F8", description="The key which will replace F8.", section="fKeys")
    default public ModifierlessKeybind f8() {
        return new ModifierlessKeybind(56, 0);
    }

    @ConfigItem(position=15, keyName="f9", name="F9", description="The key which will replace F9.", section="fKeys")
    default public ModifierlessKeybind f9() {
        return new ModifierlessKeybind(57, 0);
    }

    @ConfigItem(position=16, keyName="f10", name="F10", description="The key which will replace F10.", section="fKeys")
    default public ModifierlessKeybind f10() {
        return new ModifierlessKeybind(48, 0);
    }

    @ConfigItem(position=17, keyName="f11", name="F11", description="The key which will replace F11.", section="fKeys")
    default public ModifierlessKeybind f11() {
        return new ModifierlessKeybind(45, 0);
    }

    @ConfigItem(position=18, keyName="f12", name="F12", description="The key which will replace F12.", section="fKeys")
    default public ModifierlessKeybind f12() {
        return new ModifierlessKeybind(61, 0);
    }

    @ConfigItem(position=19, keyName="esc", name="ESC", description="The key which will replace ESC.", section="fKeys")
    default public ModifierlessKeybind esc() {
        return new ModifierlessKeybind(27, 0);
    }

    @ConfigItem(position=20, keyName="space", name="Space", description="The key which will replace space when dialogs are open.")
    default public ModifierlessKeybind space() {
        return new ModifierlessKeybind(32, 0);
    }

    @ConfigItem(position=21, keyName="control", name="Control", description="The key which will replace control.")
    default public ModifierlessKeybind control() {
        return new ModifierlessKeybind(0, 128);
    }
}

