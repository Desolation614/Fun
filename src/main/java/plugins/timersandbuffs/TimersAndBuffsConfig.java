/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.plugins.timersandbuffs;

import java.time.Instant;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;

@ConfigGroup(value="timers")
public interface TimersAndBuffsConfig
extends Config {
    public static final String GROUP = "timers";
    @ConfigSection(name="Bosses", description="Timers and buffs related to bosses.", position=0)
    public static final String bossesSection = "bossesSection";
    @ConfigSection(name="Potions & consumables", description="Timers and buffs related to potions/consumables/boosts.", position=1)
    public static final String consumablesSection = "consumablesSection";
    @ConfigSection(name="Spells", description="Timers and buffs related to spells you cast.", position=2)
    public static final String spellsSection = "spellsSection";
    @ConfigSection(name="Miscellaneous", description="Timers and buffs related to miscellaneous items or activities.", position=3)
    public static final String miscellaneousSection = "miscellaneousSection";

    @ConfigItem(keyName="showHomeMinigameTeleports", name="Teleport cooldown timers", description="Configures whether timers for home and minigame teleport cooldowns are displayed.", section="miscellaneousSection")
    default public boolean showHomeMinigameTeleports() {
        return true;
    }

    @ConfigItem(keyName="showAntipoison", name="Antipoison/venom timers", description="Configures whether timers for poison and venom protection are displayed.", section="consumablesSection")
    default public boolean showAntiPoison() {
        return true;
    }

    @ConfigItem(keyName="showAntiFire", name="Antifire timer", description="Configures whether antifire timer is displayed.", section="consumablesSection")
    default public boolean showAntiFire() {
        return true;
    }

    @ConfigItem(keyName="showStamina", name="Stamina timer", description="Configures whether stamina timer is displayed.", section="consumablesSection")
    default public boolean showStamina() {
        return true;
    }

    @ConfigItem(keyName="showOverload", name="Overload timer", description="Configures whether overload timer is displayed.", section="consumablesSection")
    default public boolean showOverload() {
        return true;
    }

    @ConfigItem(keyName="showLiquidAdrenaline", name="Liquid adrenaline timer", description="Configures whether liquid adrenaline timer is displayed.", section="consumablesSection")
    default public boolean showLiquidAdrenaline() {
        return true;
    }

    @ConfigItem(keyName="showMenaphiteRemedy", name="Menaphite remedy timer", description="Configures whether menaphite remedy timer is displayed.", section="consumablesSection")
    default public boolean showMenaphiteRemedy() {
        return true;
    }

    @ConfigItem(keyName="showSilkDressing", name="Silk dressing timer", description="Configures whether silk dressing timer is displayed.", section="consumablesSection")
    default public boolean showSilkDressing() {
        return true;
    }

    @ConfigItem(keyName="showBlessedCrystalScarab", name="Blessed crystal scarab timer", description="Configures whether blessed crystal scarab timer is displayed.", section="consumablesSection")
    default public boolean showBlessedCrystalScarab() {
        return true;
    }

    @ConfigItem(keyName="showPrayerEnhance", name="Prayer enhance timer", description="Configures whether prayer enhance timer is displayed.", section="consumablesSection")
    default public boolean showPrayerEnhance() {
        return true;
    }

    @ConfigItem(keyName="showGoading", name="Goading potion timer", description="Configures whether goading potion timer is displayed.", section="consumablesSection")
    default public boolean showGoading() {
        return true;
    }

    @ConfigItem(keyName="showPrayerRegeneration", name="Prayer regeneration timer", description="Configures whether prayer regeneration timer is displayed.", section="consumablesSection")
    default public boolean showPrayerRegneration() {
        return true;
    }

    @ConfigItem(keyName="showDivine", name="Divine potion timer", description="Configures whether divine potion timer is displayed.", section="consumablesSection")
    default public boolean showDivine() {
        return true;
    }

    @ConfigItem(keyName="showCannon", name="Cannon timer", description="Configures whether cannon timer is displayed.", section="miscellaneousSection")
    default public boolean showCannon() {
        return true;
    }

    @ConfigItem(keyName="showMagicImbue", name="Magic imbue timer", description="Configures whether magic imbue timer is displayed.", section="spellsSection")
    default public boolean showMagicImbue() {
        return true;
    }

    @ConfigItem(keyName="showCharge", name="Charge timer", description="Configures whether to show a timer for the charge spell.", section="spellsSection")
    default public boolean showCharge() {
        return true;
    }

    @ConfigItem(keyName="showImbuedHeart", name="Imbued heart timer", description="Configures whether imbued heart timer is displayed.", section="consumablesSection")
    default public boolean showImbuedHeart() {
        return true;
    }

    @ConfigItem(keyName="showVengeance", name="Vengeance timer", description="Configures whether vengeance and vengeance other timer is displayed.", section="spellsSection")
    default public boolean showVengeance() {
        return true;
    }

    @ConfigItem(keyName="showVengeanceActive", name="Vengeance active", description="Configures whether an indicator for vengeance being active is displayed.", section="spellsSection")
    default public boolean showVengeanceActive() {
        return true;
    }

    @ConfigItem(keyName="showHealGroup", name="Heal group timer", description="Configures whether heal group timer is displayed.", section="spellsSection")
    default public boolean showHealGroup() {
        return true;
    }

    @ConfigItem(keyName="showTeleblock", name="Teleblock timer", description="Configures whether teleblock timer is displayed.", section="miscellaneousSection")
    default public boolean showTeleblock() {
        return true;
    }

    @ConfigItem(keyName="showFreezes", name="Freeze timer", description="Configures whether freeze timer is displayed.", section="miscellaneousSection")
    default public boolean showFreezes() {
        return true;
    }

    @ConfigItem(keyName="showGodWarsAltar", name="God wars altar timer", description="Configures whether god wars altar timer is displayed.", section="bossesSection")
    default public boolean showGodWarsAltar() {
        return true;
    }

    @ConfigItem(keyName="showTzhaarTimers", name="Fight caves and inferno timers", description="Display elapsed time in the fight caves and inferno.", section="bossesSection")
    default public boolean showTzhaarTimers() {
        return true;
    }

    @ConfigItem(keyName="tzhaarStartTime", name="", description="", hidden=true)
    public Instant tzhaarStartTime();

    @ConfigItem(keyName="tzhaarStartTime", name="", description="")
    public void tzhaarStartTime(Instant var1);

    @ConfigItem(keyName="tzhaarLastTime", name="", description="", hidden=true)
    public Instant tzhaarLastTime();

    @ConfigItem(keyName="tzhaarLastTime", name="", description="")
    public void tzhaarLastTime(Instant var1);

    @ConfigItem(keyName="showStaffOfTheDead", name="Staff of the dead timer", description="Configures whether staff of the dead timer is displayed.", section="miscellaneousSection")
    default public boolean showStaffOfTheDead() {
        return true;
    }

    @ConfigItem(keyName="showAbyssalSireStun", name="Abyssal sire stun timer", description="Configures whether the Abyssal sire stun timer is displayed.", section="bossesSection")
    default public boolean showAbyssalSireStun() {
        return true;
    }

    @ConfigItem(keyName="showDfsSpecial", name="Dragonfire shield special timer", description="Configures whether the special attack cooldown timer for the dragonfire shield is displayed.", section="miscellaneousSection")
    default public boolean showDFSSpecial() {
        return true;
    }

    @ConfigItem(keyName="showArceuus", name="Arceuus spells duration", description="Whether to show Arceuus spellbook spell timers.", section="spellsSection")
    default public boolean showArceuus() {
        return true;
    }

    @ConfigItem(keyName="showArceuusCooldown", name="Arceuus spells cooldown", description="Whether to show cooldown timers for Arceuus spellbook spells.", section="spellsSection")
    default public boolean showArceuusCooldown() {
        return false;
    }

    @ConfigItem(keyName="showPickpocketStun", name="Pickpocket stun timer", description="Configures whether pickpocket stun timer is displayed.", section="miscellaneousSection")
    default public boolean showPickpocketStun() {
        return true;
    }

    @ConfigItem(keyName="showFarmersAffinity", name="Farmer's affinity", description="Configures whether farmer's affinity (Puro-Puro) timer is displayed.", section="miscellaneousSection")
    default public boolean showFarmersAffinity() {
        return true;
    }

    @ConfigItem(keyName="showSpellbookSwap", name="Spellbook swap timer", description="Configures whether spellbook swap timer is displayed.", section="spellsSection")
    default public boolean showSpellbookSwap() {
        return true;
    }

    @ConfigItem(keyName="showCurseOfTheMoons", name="Curse of the moons buff", description="Configures whether curse of the moons buff is displayed.", section="bossesSection")
    default public boolean showCurseOfTheMoons() {
        return true;
    }

    @ConfigItem(keyName="showColosseumDoom", name="Colosseum doom buff", description="Configures whether Fortis Colosseum doom buff is displayed.", section="bossesSection")
    default public boolean showColosseumDoom() {
        return true;
    }

    @ConfigItem(keyName="showMoonlightPotion", name="Moonlight potion timer", description="Configures whether moonlight potion timer is displayed.", section="consumablesSection")
    default public boolean showMoonlightPotion() {
        return true;
    }

    @ConfigItem(keyName="showBurnDamageAccumulated", name="Burn damage accumulated", description="Configures whether the accumulated burn damage on the local player is displayed.", section="miscellaneousSection")
    default public boolean showBurnDamageAccumulated() {
        return true;
    }

    @ConfigItem(keyName="showBurnDamageNextHit", name="Burn damage next hit", description="Configures whether the next hit of burn damage on the local player is displayed.", section="miscellaneousSection")
    default public boolean showBurnDamageNextHit() {
        return false;
    }

    @ConfigItem(keyName="showScurriusFoodPile", name="Scurrius food pile", description="Configures whether the Scurrius food pile timer is displayed.", section="bossesSection")
    default public boolean showScurriusFoodPile() {
        return true;
    }

    @ConfigItem(keyName="showTormentedDemonBuffs", name="Tormented demon buffs", description="Configures whether Tormented demon-related buffs are displayed.", section="bossesSection")
    default public boolean showTormentedDemonBuffs() {
        return true;
    }

    @ConfigItem(keyName="showSurgePotion", name="Surge potion timer", description="Configures whether surge potion timer is displayed.", section="consumablesSection")
    default public boolean showSurge() {
        return true;
    }

    @ConfigItem(keyName="showBonusXP", name="Bonus XP timer", description="Configures whether bonus XP timer is displayed.", section="consumablesSection")
    default public boolean showBonusXP() {
        return true;
    }

    @ConfigItem(keyName="showBonusDropRates", name="Bonus drop rates timer", description="Configures whether bonus drop rates timer is displayed.", section="consumablesSection")
    default public boolean showBonusDropRates() {
        return true;
    }

    @ConfigItem(keyName="showBonusPetRates", name="Bonus pet rates timer", description="Configures whether bonus pet rates timer is displayed.", section="consumablesSection")
    default public boolean showBonusPetRates() {
        return true;
    }

    @ConfigItem(keyName="showBonusCoxRates", name="Bonus CoX points timer", description="Configures whether bonus CoX points timer is displayed.", section="consumablesSection")
    default public boolean showBonusCoxRates() {
        return true;
    }
}

