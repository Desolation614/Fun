/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.plugins.roofremoval;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import net.runelite.client.plugins.roofremoval.RoofRemovalConfig;

enum RoofRemovalConfigOverride {
    POH(RoofRemovalConfig::overridePOH, 7257, 7513, 7514, 7769, 7770, 8025, 8026);

    private final Predicate<RoofRemovalConfig> enabled;
    private final List<Integer> regions;

    private RoofRemovalConfigOverride(Predicate<RoofRemovalConfig> enabled, Integer ... regions) {
        this.enabled = enabled;
        this.regions = Arrays.asList(regions);
    }

    public Predicate<RoofRemovalConfig> getEnabled() {
        return this.enabled;
    }

    public List<Integer> getRegions() {
        return this.regions;
    }
}

