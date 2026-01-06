/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.config;

import java.util.Collection;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItemDescriptor;
import net.runelite.client.config.ConfigSectionDescriptor;

public class ConfigDescriptor {
    private final ConfigGroup group;
    private final Collection<ConfigSectionDescriptor> sections;
    private final Collection<ConfigItemDescriptor> items;

    public ConfigDescriptor(ConfigGroup group, Collection<ConfigSectionDescriptor> sections, Collection<ConfigItemDescriptor> items) {
        this.group = group;
        this.sections = sections;
        this.items = items;
    }

    public ConfigGroup getGroup() {
        return this.group;
    }

    public Collection<ConfigSectionDescriptor> getSections() {
        return this.sections;
    }

    public Collection<ConfigItemDescriptor> getItems() {
        return this.items;
    }
}

