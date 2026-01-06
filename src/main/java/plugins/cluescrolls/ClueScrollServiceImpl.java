/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.inject.Inject
 *  javax.inject.Singleton
 */
package net.runelite.client.plugins.cluescrolls;

import javax.inject.Inject;
import javax.inject.Singleton;
import net.runelite.client.plugins.cluescrolls.ClueScrollPlugin;
import net.runelite.client.plugins.cluescrolls.ClueScrollService;
import net.runelite.client.plugins.cluescrolls.clues.ClueScroll;

@Singleton
class ClueScrollServiceImpl
implements ClueScrollService {
    private final ClueScrollPlugin plugin;

    @Inject
    private ClueScrollServiceImpl(ClueScrollPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public ClueScroll getClue() {
        return this.plugin.getClue();
    }
}

