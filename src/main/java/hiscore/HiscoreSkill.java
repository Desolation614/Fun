/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.hiscore;

import net.runelite.client.hiscore.HiscoreSkillType;

public enum HiscoreSkill {
    OVERALL("Overall", HiscoreSkillType.OVERALL, 222),
    ATTACK("Attack", HiscoreSkillType.SKILL, 197),
    DEFENCE("Defence", HiscoreSkillType.SKILL, 199),
    STRENGTH("Strength", HiscoreSkillType.SKILL, 198),
    HITPOINTS("Hitpoints", HiscoreSkillType.SKILL, 203),
    RANGED("Ranged", HiscoreSkillType.SKILL, 200),
    PRAYER("Prayer", HiscoreSkillType.SKILL, 201),
    MAGIC("Magic", HiscoreSkillType.SKILL, 202),
    COOKING("Cooking", HiscoreSkillType.SKILL, 212),
    WOODCUTTING("Woodcutting", HiscoreSkillType.SKILL, 214),
    FLETCHING("Fletching", HiscoreSkillType.SKILL, 208),
    FISHING("Fishing", HiscoreSkillType.SKILL, 211),
    FIREMAKING("Firemaking", HiscoreSkillType.SKILL, 213),
    CRAFTING("Crafting", HiscoreSkillType.SKILL, 207),
    SMITHING("Smithing", HiscoreSkillType.SKILL, 210),
    MINING("Mining", HiscoreSkillType.SKILL, 209),
    HERBLORE("Herblore", HiscoreSkillType.SKILL, 205),
    AGILITY("Agility", HiscoreSkillType.SKILL, 204),
    THIEVING("Thieving", HiscoreSkillType.SKILL, 206),
    SLAYER("Slayer", HiscoreSkillType.SKILL, 216),
    FARMING("Farming", HiscoreSkillType.SKILL, 217),
    RUNECRAFT("Runecraft", HiscoreSkillType.SKILL, 215),
    HUNTER("Hunter", HiscoreSkillType.SKILL, 220),
    CONSTRUCTION("Construction", HiscoreSkillType.SKILL, 221),
    LEAGUE_POINTS("League Points", HiscoreSkillType.ACTIVITY, 5857),
    BOUNTY_HUNTER_HUNTER("Bounty Hunter - Hunter", HiscoreSkillType.ACTIVITY, 5854),
    BOUNTY_HUNTER_ROGUE("Bounty Hunter - Rogue", HiscoreSkillType.ACTIVITY, 5855),
    CLUE_SCROLL_ALL("Clue Scrolls (all)", HiscoreSkillType.ACTIVITY, 5853),
    CLUE_SCROLL_BEGINNER("Clue Scrolls (beginner)", HiscoreSkillType.ACTIVITY),
    CLUE_SCROLL_EASY("Clue Scrolls (easy)", HiscoreSkillType.ACTIVITY),
    CLUE_SCROLL_MEDIUM("Clue Scrolls (medium)", HiscoreSkillType.ACTIVITY),
    CLUE_SCROLL_HARD("Clue Scrolls (hard)", HiscoreSkillType.ACTIVITY),
    CLUE_SCROLL_ELITE("Clue Scrolls (elite)", HiscoreSkillType.ACTIVITY),
    CLUE_SCROLL_MASTER("Clue Scrolls (master)", HiscoreSkillType.ACTIVITY),
    LAST_MAN_STANDING("LMS - Rank", HiscoreSkillType.ACTIVITY, 5856),
    PVP_ARENA_RANK("PvP Arena - Rank", HiscoreSkillType.ACTIVITY, 5859),
    SOUL_WARS_ZEAL("Soul Wars Zeal", HiscoreSkillType.ACTIVITY, 5858),
    RIFTS_CLOSED("Rifts closed", HiscoreSkillType.ACTIVITY, 5860),
    COLOSSEUM_GLORY("Colosseum Glory", HiscoreSkillType.ACTIVITY, 5862),
    COLLECTIONS_LOGGED("Collections Logged", HiscoreSkillType.ACTIVITY, 6390),
    ABYSSAL_SIRE("Abyssal Sire", HiscoreSkillType.BOSS, 4276),
    ALCHEMICAL_HYDRA("Alchemical Hydra", HiscoreSkillType.BOSS, 4289),
    AMOXLIATL("Amoxliatl", HiscoreSkillType.BOSS, 5639),
    ARAXXOR("Araxxor", HiscoreSkillType.BOSS, 5638),
    ARTIO("Artio", HiscoreSkillType.BOSS, 5622),
    BARROWS_CHESTS("Barrows Chests", HiscoreSkillType.BOSS, 4267),
    BRYOPHYTA("Bryophyta", HiscoreSkillType.BOSS, 4262),
    CALLISTO("Callisto", HiscoreSkillType.BOSS, 5622),
    CALVARION("Calvar'ion", HiscoreSkillType.BOSS, 5623),
    CERBERUS("Cerberus", HiscoreSkillType.BOSS, 4280),
    CHAMBERS_OF_XERIC("Chambers of Xeric", HiscoreSkillType.BOSS, 4288),
    CHAMBERS_OF_XERIC_CHALLENGE_MODE("Chambers of Xeric: Challenge Mode", HiscoreSkillType.BOSS, 4296),
    CHAOS_ELEMENTAL("Chaos Elemental", HiscoreSkillType.BOSS, 5621),
    CHAOS_FANATIC("Chaos Fanatic", HiscoreSkillType.BOSS, 5625),
    COMMANDER_ZILYANA("Commander Zilyana", HiscoreSkillType.BOSS, 4284),
    CORPOREAL_BEAST("Corporeal Beast", HiscoreSkillType.BOSS, 4287),
    CRAZY_ARCHAEOLOGIST("Crazy Archaeologist", HiscoreSkillType.BOSS, 5626),
    DAGANNOTH_PRIME("Dagannoth Prime", HiscoreSkillType.BOSS, 4294),
    DAGANNOTH_REX("Dagannoth Rex", HiscoreSkillType.BOSS, 4293),
    DAGANNOTH_SUPREME("Dagannoth Supreme", HiscoreSkillType.BOSS, 4292),
    DERANGED_ARCHAEOLOGIST("Deranged Archaeologist", HiscoreSkillType.BOSS, 5627),
    DOOM_OF_MOKHAIOTL("Doom of Mokhaiotl", HiscoreSkillType.BOSS, 6347),
    DUKE_SUCELLUS("Duke Sucellus", HiscoreSkillType.BOSS, 5632),
    GENERAL_GRAARDOR("General Graardor", HiscoreSkillType.BOSS, 4282),
    GIANT_MOLE("Giant Mole", HiscoreSkillType.BOSS, 4263),
    GROTESQUE_GUARDIANS("Grotesque Guardians", HiscoreSkillType.BOSS, 4264),
    HESPORI("Hespori", HiscoreSkillType.BOSS, 4271),
    KALPHITE_QUEEN("Kalphite Queen", HiscoreSkillType.BOSS, 4270),
    KING_BLACK_DRAGON("King Black Dragon", HiscoreSkillType.BOSS, 4274),
    KRAKEN("Kraken", HiscoreSkillType.BOSS, 4275),
    KREEARRA("Kree'Arra", HiscoreSkillType.BOSS, 4285),
    KRIL_TSUTSAROTH("K'ril Tsutsaroth", HiscoreSkillType.BOSS, 4283),
    LUNAR_CHESTS("Lunar Chests", HiscoreSkillType.BOSS, 5637),
    MIMIC("Mimic", HiscoreSkillType.BOSS, 4260),
    NEX("Nex", HiscoreSkillType.BOSS, 4291),
    NIGHTMARE("Nightmare", HiscoreSkillType.BOSS, 4286),
    PHOSANIS_NIGHTMARE("Phosani's Nightmare", HiscoreSkillType.BOSS, 4286),
    OBOR("Obor", HiscoreSkillType.BOSS, 4261),
    PHANTOM_MUSPAH("Phantom Muspah", HiscoreSkillType.BOSS, 4299),
    SARACHNIS("Sarachnis", HiscoreSkillType.BOSS, 4269),
    SCORPIA("Scorpia", HiscoreSkillType.BOSS, 5628),
    SCURRIUS("Scurrius", HiscoreSkillType.BOSS, 5635),
    SKOTIZO("Skotizo", HiscoreSkillType.BOSS, 4272),
    SOL_HEREDIT("Sol Heredit", HiscoreSkillType.BOSS, 5636),
    SPINDEL("Spindel", HiscoreSkillType.BOSS, 5624),
    TEMPOROSS("Tempoross", HiscoreSkillType.BOSS, 4265),
    THE_GAUNTLET("The Gauntlet", HiscoreSkillType.BOSS, 4278),
    THE_CORRUPTED_GAUNTLET("The Corrupted Gauntlet", HiscoreSkillType.BOSS, 4295),
    THE_HUEYCOATL("The Hueycoatl", HiscoreSkillType.BOSS, 5640),
    THE_LEVIATHAN("The Leviathan", HiscoreSkillType.BOSS, 5633),
    THE_ROYAL_TITANS("The Royal Titans", HiscoreSkillType.BOSS, 6345),
    THE_WHISPERER("The Whisperer", HiscoreSkillType.BOSS, 5631),
    THEATRE_OF_BLOOD("Theatre of Blood", HiscoreSkillType.BOSS, 4290),
    THEATRE_OF_BLOOD_HARD_MODE("Theatre of Blood: Hard Mode", HiscoreSkillType.BOSS, 4290),
    THERMONUCLEAR_SMOKE_DEVIL("Thermonuclear Smoke Devil", HiscoreSkillType.BOSS, 4277),
    TOMBS_OF_AMASCUT("Tombs of Amascut", HiscoreSkillType.BOSS, 4297),
    TOMBS_OF_AMASCUT_EXPERT("Tombs of Amascut: Expert Mode", HiscoreSkillType.BOSS, 4298),
    TZKAL_ZUK("TzKal-Zuk", HiscoreSkillType.BOSS, 5630),
    TZTOK_JAD("TzTok-Jad", HiscoreSkillType.BOSS, 5629),
    VARDORVIS("Vardorvis", HiscoreSkillType.BOSS, 5634),
    VENENATIS("Venenatis", HiscoreSkillType.BOSS, 5624),
    VETION("Vet'ion", HiscoreSkillType.BOSS, 5623),
    VORKATH("Vorkath", HiscoreSkillType.BOSS, 4281),
    WINTERTODT("Wintertodt", HiscoreSkillType.BOSS, 4266),
    YAMA("Yama", HiscoreSkillType.BOSS, 6346),
    ZALCANO("Zalcano", HiscoreSkillType.BOSS, 4273),
    ZULRAH("Zulrah", HiscoreSkillType.BOSS, 4279);

    private final String name;
    private final HiscoreSkillType type;
    private final int spriteId;

    private HiscoreSkill(String name, HiscoreSkillType type) {
        this.name = name;
        this.type = type;
        this.spriteId = -1;
    }

    private HiscoreSkill(String name, HiscoreSkillType type, int spriteId) {
        this.name = name;
        this.type = type;
        this.spriteId = spriteId;
    }

    public String getName() {
        return this.name;
    }

    public HiscoreSkillType getType() {
        return this.type;
    }

    public int getSpriteId() {
        return this.spriteId;
    }
}

