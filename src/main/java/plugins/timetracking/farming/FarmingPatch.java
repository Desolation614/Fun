/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.plugins.timetracking.farming;

import net.runelite.client.plugins.timetracking.farming.FarmingRegion;
import net.runelite.client.plugins.timetracking.farming.PatchImplementation;

class FarmingPatch {
    private FarmingRegion region;
    private final String name;
    private final int varbit;
    private final PatchImplementation implementation;
    private final int farmer;
    private final int patchNumber;

    FarmingPatch(String name, int varbit, PatchImplementation implementation) {
        this(name, varbit, implementation, -1);
    }

    FarmingPatch(String name, int varbit, PatchImplementation implementation, int farmer) {
        this(name, varbit, implementation, farmer, -1);
    }

    FarmingPatch(String name, int varbit, PatchImplementation implementation, int farmer, int patchNumber) {
        this.name = name;
        this.varbit = varbit;
        this.implementation = implementation;
        this.farmer = farmer;
        this.patchNumber = patchNumber;
    }

    String configKey() {
        return this.region.getRegionID() + "." + this.varbit;
    }

    String notifyConfigKey() {
        return "notify." + this.region.getRegionID() + "." + this.varbit;
    }

    public FarmingRegion getRegion() {
        return this.region;
    }

    public String getName() {
        return this.name;
    }

    public PatchImplementation getImplementation() {
        return this.implementation;
    }

    public int getFarmer() {
        return this.farmer;
    }

    public int getPatchNumber() {
        return this.patchNumber;
    }

    public String toString() {
        return "FarmingPatch(region=" + String.valueOf(this.getRegion()) + ", name=" + this.getName() + ", implementation=" + String.valueOf((Object)this.getImplementation()) + ")";
    }

    void setRegion(FarmingRegion region) {
        this.region = region;
    }

    public int getVarbit() {
        return this.varbit;
    }
}

