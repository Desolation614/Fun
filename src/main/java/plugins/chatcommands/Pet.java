/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.plugins.chatcommands;

@Deprecated
enum Pet {
    ABYSSAL_ORPHAN("Abyssal orphan", 13262),
    IKKLE_HYDRA("Ikkle hydra", 22746),
    CALLISTO_CUB("Callisto cub", 13178),
    HELLPUPPY("Hellpuppy", 13247),
    PET_CHAOS_ELEMENTAL("Pet chaos elemental", 11995),
    PET_ZILYANA("Pet zilyana", 12651),
    PET_DARK_CORE("Pet dark core", 12816),
    PET_DAGANNOTH_PRIME("Pet dagannoth prime", 12644),
    PET_DAGANNOTH_SUPREME("Pet dagannoth supreme", 12643),
    PET_DAGANNOTH_REX("Pet dagannoth rex", 12645),
    TZREKJAD("Tzrek-jad", 13225),
    PET_GENERAL_GRAARDOR("Pet general graardor", 12650),
    BABY_MOLE("Baby mole", 12646),
    NOON("Noon", 21748),
    JALNIBREK("Jal-nib-rek", 21291),
    KALPHITE_PRINCESS("Kalphite princess", 12647),
    PRINCE_BLACK_DRAGON("Prince black dragon", 12653),
    PET_KRAKEN("Pet kraken", 12655),
    PET_KREEARRA("Pet kree'arra", 12649),
    PET_KRIL_TSUTSAROTH("Pet k'ril tsutsaroth", 12652),
    SCORPIAS_OFFSPRING("Scorpia's offspring", 13181),
    SKOTOS("Skotos", 21273),
    PET_SMOKE_DEVIL("Pet smoke devil", 12648),
    VENENATIS_SPIDERLING("Venenatis spiderling", 13177),
    VETION_JR("Vet'ion jr.", 13179),
    VORKI("Vorki", 21992),
    PHOENIX("Phoenix", 20693),
    PET_SNAKELING("Pet snakeling", 12921),
    OLMLET("Olmlet", 20851),
    LIL_ZIK("Lil' zik", 22473),
    BLOODHOUND("Bloodhound", 19730),
    PET_PENANCE_QUEEN("Pet penance queen", 12703),
    HERON("Heron", 13320),
    ROCK_GOLEM("Rock golem", 13321),
    BEAVER("Beaver", 13322),
    BABY_CHINCHOMPA("Baby chinchompa", 13324),
    GIANT_SQUIRREL("Giant squirrel", 20659),
    TANGLEROOT("Tangleroot", 20661),
    ROCKY("Rocky", 20663),
    RIFT_GUARDIAN("Rift guardian", 20665),
    HERBI("Herbi", 21509),
    CHOMPY_CHICK("Chompy chick", 13071),
    SRARACHA("Sraracha", 23495),
    SMOLCANO("Smolcano", 23760),
    YOUNGLLEF("Youngllef", 23757),
    LITTLE_NIGHTMARE("Little nightmare", 24491),
    LIL_CREATOR("Lil' creator", 25348),
    TINY_TEMPOR("Tiny tempor", 25602),
    NEXLING("Nexling", 26348),
    ABYSSAL_PROTECTOR("Abyssal protector", 26901);

    private final String name;
    private final Integer iconID;

    static Pet findPet(String petName) {
        for (Pet pet : Pet.values()) {
            if (!pet.name.equals(petName)) continue;
            return pet;
        }
        return null;
    }

    private Pet(String name, Integer iconID) {
        this.name = name;
        this.iconID = iconID;
    }

    public String getName() {
        return this.name;
    }

    public Integer getIconID() {
        return this.iconID;
    }
}

