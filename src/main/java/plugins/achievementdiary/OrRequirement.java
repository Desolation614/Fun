/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Joiner
 *  com.google.common.collect.ImmutableList
 *  net.runelite.api.Client
 */
package net.runelite.client.plugins.achievementdiary;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import java.util.List;
import net.runelite.api.Client;
import net.runelite.client.plugins.achievementdiary.Requirement;

public class OrRequirement
implements Requirement {
    private final List<Requirement> requirements;

    public OrRequirement(Requirement ... reqs) {
        this.requirements = ImmutableList.copyOf((Object[])reqs);
    }

    public String toString() {
        return Joiner.on((String)" or ").join(this.requirements);
    }

    @Override
    public boolean satisfiesRequirement(Client client) {
        for (Requirement r : this.getRequirements()) {
            if (!r.satisfiesRequirement(client)) continue;
            return true;
        }
        return false;
    }

    public List<Requirement> getRequirements() {
        return this.requirements;
    }
}

