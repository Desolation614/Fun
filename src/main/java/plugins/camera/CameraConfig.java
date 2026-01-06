/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.plugins.camera;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;
import net.runelite.client.config.Range;
import net.runelite.client.plugins.camera.ControlFunction;

@ConfigGroup(value="zoom")
public interface CameraConfig
extends Config {
    public static final int OUTER_LIMIT_MIN = -400;
    public static final int OUTER_LIMIT_MAX = 400;
    public static final int INNER_ZOOM_LIMIT = 1004;
    @ConfigSection(name="Mouse settings", description="Mouse settings.", position=10)
    public static final String mouseSettingsSection = "mouseSettings";

    @ConfigItem(keyName="inner", name="Expand inner zoom limit", description="Configures whether or not the inner zoom limit is reduced.", position=1)
    default public boolean innerLimit() {
        return false;
    }

    @Range(min=-400, max=400)
    @ConfigItem(keyName="outerLimit", name="Expand outer zoom limit", description="Configures how much the outer zoom limit is adjusted.", position=2)
    default public int outerLimit() {
        return 0;
    }

    @ConfigItem(keyName="relaxCameraPitch", name="Expand pitch limit", description="Relax the camera's upper and lower pitch limits. Allows vertical and horizontal camera movement.", position=3)
    default public boolean relaxCameraPitch() {
        return false;
    }

    @ConfigItem(keyName="controlFunction", name="Control function", description="Configures the zoom function when control is pressed.", position=4)
    default public ControlFunction controlFunction() {
        return ControlFunction.NONE;
    }

    @ConfigItem(keyName="ctrlZoomValue", name="Reset zoom position", description="Position of zoom when it is reset.", position=5)
    @Range(min=-400, max=1004)
    default public int ctrlZoomValue() {
        return 512;
    }

    @ConfigItem(keyName="zoomIncrement", name="Zoom speed", description="Speed of zoom.", position=6)
    default public int zoomIncrement() {
        return 25;
    }

    @ConfigItem(keyName="cameraSpeed", name="Camera speed", description="Speed which the camera moves from input.", position=7)
    default public double cameraSpeed() {
        return 1.0;
    }

    @ConfigItem(keyName="disableCameraShake", name="Disable camera shake", description="Disables camera shake.", position=8)
    default public boolean disableCameraShake() {
        return false;
    }

    @ConfigItem(keyName="rightClickMovesCamera", name="Right-click moves camera", description="Remaps right-click to middle mouse click.", position=7, section="mouseSettings")
    default public boolean rightClickMovesCamera() {
        return false;
    }

    @ConfigItem(keyName="rightClickObjects", name="Right-click objects", description="Right-clicking objects opens the menu when 'Right-click moves camera' is on.", position=8, section="mouseSettings")
    default public boolean rightClickObjects() {
        return true;
    }

    @ConfigItem(keyName="rightClickExamine", name="Right-click examine", description="Right-clicking examinable objects opens the menu when 'Right-click moves camera' is on.", position=9, section="mouseSettings")
    default public boolean rightClickExamine() {
        return false;
    }

    @ConfigItem(keyName="rightClickMenuBlocksCamera", name="Right-click menu blocks camera", description="Prevents camera movement when 'Right-click moves camera' is on and the right-click menu<br>is opened due to either 'Right-click objects' or 'Right-click examine' being on.", position=10, section="mouseSettings")
    default public boolean rightClickMenuBlocksCamera() {
        return true;
    }

    @ConfigItem(keyName="middleClickMenu", name="Middle-button opens menu", description="Remaps middle mouse click to right-click.", position=11, section="mouseSettings")
    default public boolean middleClickMenu() {
        return false;
    }

    @ConfigItem(keyName="invertYaw", name="Invert yaw", description="Makes moving the camera horizontally with the mouse backwards.", position=12, section="mouseSettings")
    default public boolean invertYaw() {
        return false;
    }

    @ConfigItem(keyName="invertPitch", name="Invert pitch", description="Makes moving the camera vertically with the mouse backwards.", position=13, section="mouseSettings")
    default public boolean invertPitch() {
        return false;
    }
}

