/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.inject.Provides
 *  javax.inject.Inject
 *  net.runelite.api.Client
 *  net.runelite.api.Experience
 *  net.runelite.api.Skill
 *  net.runelite.api.events.ScriptCallbackEvent
 */
package net.runelite.client.plugins.virtuallevels;

import com.google.inject.Provides;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.Experience;
import net.runelite.api.Skill;
import net.runelite.api.events.ScriptCallbackEvent;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.events.PluginChanged;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.virtuallevels.VirtualLevelsConfig;

@PluginDescriptor(name="Virtual Levels", description="Shows virtual levels (beyond 99) and virtual skill total on the skills tab.", tags={"skill", "total", "max"}, enabledByDefault=false)
public class VirtualLevelsPlugin
extends Plugin {
    private static final String TOTAL_LEVEL_TEXT_PREFIX = "Total level:<br>";
    @Inject
    private VirtualLevelsConfig config;
    @Inject
    private Client client;
    @Inject
    private ClientThread clientThread;

    @Provides
    VirtualLevelsConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(VirtualLevelsConfig.class);
    }

    @Override
    protected void shutDown() {
        this.clientThread.invoke(this::simulateSkillChange);
    }

    @Subscribe
    public void onPluginChanged(PluginChanged pluginChanged) {
        if (pluginChanged.getPlugin() == this) {
            this.clientThread.invoke(this::simulateSkillChange);
        }
    }

    @Subscribe
    public void onConfigChanged(ConfigChanged configChanged) {
        if (!configChanged.getGroup().equals("virtuallevels")) {
            return;
        }
        this.clientThread.invoke(this::simulateSkillChange);
    }

    @Subscribe
    public void onScriptCallbackEvent(ScriptCallbackEvent e) {
        String eventName = e.getEventName();
        int[] intStack = this.client.getIntStack();
        int intStackSize = this.client.getIntStackSize();
        Object[] objectStack = this.client.getObjectStack();
        int objectStackSize = this.client.getObjectStackSize();
        switch (eventName) {
            case "skillTabBaseLevel": {
                int skillId = intStack[intStackSize - 2];
                Skill skill = Skill.values()[skillId];
                int exp = this.client.getSkillExperience(skill);
                intStack[intStackSize - 1] = Experience.getLevelForXp((int)exp);
                break;
            }
            case "skillTabMaxLevel": {
                intStack[intStackSize - 1] = 126;
                break;
            }
            case "skillTabTotalLevel": {
                if (!this.config.virtualTotalLevel()) break;
                int level = 0;
                for (Skill s : Skill.values()) {
                    level += Experience.getLevelForXp((int)this.client.getSkillExperience(s));
                }
                objectStack[objectStackSize - 1] = TOTAL_LEVEL_TEXT_PREFIX + level;
            }
        }
    }

    private void simulateSkillChange() {
        for (Skill skill : Skill.values()) {
            this.client.queueChangedSkill(skill);
        }
    }
}

