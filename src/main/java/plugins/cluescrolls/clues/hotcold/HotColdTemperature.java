/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Sets
 *  javax.annotation.Nullable
 */
package net.runelite.client.plugins.cluescrolls.clues.hotcold;

import com.google.common.collect.Sets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Set;
import javax.annotation.Nullable;
import net.runelite.client.util.Text;

public enum HotColdTemperature {
    ICE_COLD("ice cold", 500, 5000),
    VERY_COLD("very cold", 200, 499),
    COLD("cold", 150, 199),
    WARM("warm", 100, 149),
    HOT("hot", 70, 99),
    VERY_HOT("very hot", 30, 69),
    BEGINNER_INCREDIBLY_HOT("incredibly hot", 4, 29),
    BEGINNER_VISIBLY_SHAKING("visibly shaking", 0, 3),
    MASTER_INCREDIBLY_HOT("incredibly hot", 5, 29),
    MASTER_VISIBLY_SHAKING("visibly shaking", 0, 4);

    public static final Set<HotColdTemperature> BEGINNER_HOT_COLD_TEMPERATURES;
    public static final Set<HotColdTemperature> MASTER_HOT_COLD_TEMPERATURES;
    private final String text;
    private final int minDistance;
    private final int maxDistance;
    private static final String DEVICE_USED_START_TEXT = "The device is ";

    @Nullable
    public static HotColdTemperature getFromTemperatureSet(Set<HotColdTemperature> temperatureSet, String message) {
        if (!message.startsWith(DEVICE_USED_START_TEXT) || temperatureSet == null) {
            return null;
        }
        String messageStart = Text.fromCSV(message).get(0);
        ArrayList<HotColdTemperature> possibleTemperatures = new ArrayList<HotColdTemperature>();
        for (HotColdTemperature temperature : temperatureSet) {
            if (!messageStart.contains(temperature.getText())) continue;
            possibleTemperatures.add(temperature);
        }
        return possibleTemperatures.stream().max(Comparator.comparingInt(x -> x.getText().length())).orElse(null);
    }

    private HotColdTemperature(String text, int minDistance, int maxDistance) {
        this.text = text;
        this.minDistance = minDistance;
        this.maxDistance = maxDistance;
    }

    public String getText() {
        return this.text;
    }

    public int getMinDistance() {
        return this.minDistance;
    }

    public int getMaxDistance() {
        return this.maxDistance;
    }

    static {
        BEGINNER_HOT_COLD_TEMPERATURES = Sets.immutableEnumSet((Enum)ICE_COLD, (Enum[])new HotColdTemperature[]{VERY_COLD, COLD, WARM, HOT, VERY_HOT, BEGINNER_INCREDIBLY_HOT, BEGINNER_VISIBLY_SHAKING});
        MASTER_HOT_COLD_TEMPERATURES = Sets.immutableEnumSet((Enum)ICE_COLD, (Enum[])new HotColdTemperature[]{VERY_COLD, COLD, WARM, HOT, VERY_HOT, MASTER_INCREDIBLY_HOT, MASTER_VISIBLY_SHAKING});
    }
}

