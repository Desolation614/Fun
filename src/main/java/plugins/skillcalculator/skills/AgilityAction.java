/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.plugins.skillcalculator.skills;

import java.util.EnumSet;
import java.util.Set;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.skillcalculator.skills.AgilityBonus;
import net.runelite.client.plugins.skillcalculator.skills.NamedSkillAction;

public enum AgilityAction implements NamedSkillAction
{
    GNOME_STRONGHOLD_COURSE("Gnome Stronghold", 1, 110.5f, 2150),
    DRAYNOR_VILLAGE_ROOFTOP("Draynor Village Rooftop", 1, 120.0f, 11849),
    SHAYZIEN_BASIC_COURSE("Shayzien Basic Course", 1, 153.5f, 20263),
    LEAPING_TROUT("Leaping trout", 15, 5.0f, 11328),
    AL_KHARID_ROOFTOP("Al Kharid Rooftop", 20, 216.0f, 11849),
    LEAPING_SALMON("Leaping salmon", 30, 6.0f, 11330),
    VARROCK_ROOFTOP("Varrock Rooftop", 30, 269.7f, 11849),
    PENGUIN_AGILITY_COURSE("Penguin Agility Course", 30, 540.0f, 10595),
    BARBARIAN_OUTPOST("Barbarian Outpost", 35, 152.5f, 1365),
    CANIFIS_ROOFTOP("Canifis Rooftop", 40, 240.0f, 11849),
    LEAPING_STURGEON("Leaping sturgeon", 45, 7.0f, 11332),
    SHAYZIEN_ADVANCED_COURSE("Shayzien Advanced Course", 45, 508.0f, 20263),
    APE_ATOLL_COURSE("Ape Atoll", 48, 580.0f, 4026),
    COLOSSAL_WYRM_BASIC_COURSE("Colossal Wyrm Basic", 50, 504.1f, 30038),
    FALADOR_ROOFTOP("Falador Rooftop", 50, 586.0f, 11849),
    WILDERNESS_AGILITY_COURSE_TICKET("Wilderness Agility Ticket", 52, 200.0f, 29460),
    WILDERNESS_AGILITY_COURSE("Wilderness Agility Course", 52, 571.4f, 964),
    HALLOWED_SEPULCHRE_FLOOR_1("Hallowed Sepulchre Floor 1", 52, 575.0f, 24736),
    SEERS_VILLAGE_ROOFTOP("Seers' Village Rooftop", 60, 570.0f, 11849),
    WEREWOLF_AGILITY_COURSE("Werewolf Agility Course", 60, 730.0f, 4179),
    COLOSSAL_WYRM_ADVANCED_COURSE("Colossal Wyrm Advanced", 62, 749.6f, 30038),
    HALLOWED_SEPULCHRE_FLOOR_2("Hallowed Sepulchre Floor 2", 62, 925.0f, 24736),
    POLLNIVNEACH_ROOFTOP("Pollnivneach Rooftop", 70, 890.0f, 11849),
    DORGESH_KAAN_AGILITY_COURSE("Dorgesh-Kaan Agility Course", 70, 2750.0f, 10993),
    HALLOWED_SEPULCHRE_FLOOR_3("Hallowed Sepulchre Floor 3", 72, 1600.0f, 24736),
    PRIFDDINAS_AGILITY_COURSE("Prifddinas Agility Course", 75, 1337.0f, 23962),
    RELLEKKA_ROOFTOP("Rellekka Rooftop", 80, 780.0f, 11849),
    HALLOWED_SEPULCHRE_FLOOR_4("Hallowed Sepulchre Floor 4", 82, 2875.0f, 24736),
    ARDOUGNE_ROOFTOP("Ardougne Rooftop", 90, 889.0f, 11849),
    HALLOWED_SEPULCHRE_FLOOR_5("Hallowed Sepulchre Floor 5", 92, 5725.0f, 24736);

    private final String name;
    private final int level;
    private final float xp;
    private final int icon;

    @Override
    public boolean isMembers(ItemManager itemManager) {
        return true;
    }

    public Set<AgilityBonus> getExcludedSkillBonuses() {
        EnumSet<AgilityBonus> others = EnumSet.allOf(AgilityBonus.class);
        switch (this) {
            case WILDERNESS_AGILITY_COURSE_TICKET: {
                others.remove(AgilityBonus.WILDERNESS_AGILITY_TICKET_11_TO_50);
                others.remove(AgilityBonus.WILDERNESS_AGILITY_TICKET_51_TO_100);
                others.remove(AgilityBonus.WILDERNESS_AGILITY_TICKET_101_PLUS);
                break;
            }
            case POLLNIVNEACH_ROOFTOP: {
                others.remove(AgilityBonus.DESERT_HARD_DIARY);
                break;
            }
            case RELLEKKA_ROOFTOP: {
                others.remove(AgilityBonus.FREMENNIK_HARD_DIARY);
            }
        }
        return others;
    }

    private AgilityAction(String name, int level, float xp, int icon) {
        this.name = name;
        this.level = level;
        this.xp = xp;
        this.icon = icon;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public int getLevel() {
        return this.level;
    }

    @Override
    public float getXp() {
        return this.xp;
    }

    @Override
    public int getIcon() {
        return this.icon;
    }
}

