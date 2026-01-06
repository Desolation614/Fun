/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.ImmutableMap$Builder
 *  javax.annotation.Nullable
 */
package net.runelite.client.plugins.slayer;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import java.util.Map;
import javax.annotation.Nullable;

enum Task {
    ABERRANT_SPECTRES("Aberrant spectres", 4144, "Spectre"),
    ABYSSAL_DEMONS("Abyssal demons", 4149, "Abyssal Sire"),
    ABYSSAL_SIRE("The Abyssal Sire", 13262, new String[0]),
    ADAMANT_DRAGONS("Adamant dragons", 23270, new String[0]),
    ALCHEMICAL_HYDRA("The Alchemical Hydra", 22746, new String[0]),
    ANKOU("Ankou", 20095, new String[0]),
    ARAXXOR("Araxxor", 29836, new String[0]),
    ARAXYTES("Araxytes", 29788, "Araxxor"),
    AVIANSIES("Aviansies", 13504, "Kree'arra", "Flight Kilisa", "Flockleader Geerin", "Wingman Skree"),
    BANDITS("Bandits", 4625, "Bandit", "Black Heather", "Donny the Lad", "Speedy Keith"),
    BANSHEES("Banshees", 4135, new String[0]),
    BARROWS_BROTHERS("Barrows Brothers", 4732, new String[0]),
    BASILISKS("Basilisks", 4139, new String[0]),
    BATS("Bats", 20875, "Death wing"),
    BEARS("Bears", 13462, "Callisto", "Artio"),
    BIRDS("Birds", 314, "Chicken", "Rooster", "Terrorbird", "Seagull", "Vulture", "Duck", "Penguin", "Baby Roc"),
    BLACK_DEMONS("Black demons", 20026, "Demonic gorilla", "Balfrug Kreeyath", "Skotizo", "Porazdir"),
    BLACK_DRAGONS("Black dragons", 12524, new String[0]),
    BLACK_KNIGHTS("Black Knights", 1165, "Black Knight"),
    BLOODVELD("Bloodveld", 4141, new String[0]),
    BLUE_DRAGONS("Blue dragons", 12520, "Vorkath"),
    BRINE_RATS("Brine rats", 11047, new String[0]),
    BRONZE_DRAGONS("Bronze dragons", 12363, new String[0]),
    CALLISTO("Callisto", 13178, new String[0]),
    CATABLEPON("Catablepon", 9008, new String[0]),
    CAVE_BUGS("Cave bugs", 4521, new String[0]),
    CAVE_CRAWLERS("Cave crawlers", 4134, "Chasm crawler"),
    CAVE_HORRORS("Cave horrors", 8900, "Cave abomination"),
    CAVE_KRAKEN("Cave kraken", 3272, "Kraken"),
    CAVE_SLIMES("Cave slimes", 4520, new String[0]),
    CERBERUS("Cerberus", 13247, new String[0]),
    CHAOS_DRUIDS("Chaos druids", 20595, new String[0]),
    CHAOS_ELEMENTAL("The Chaos Elemental", 11995, new String[0]),
    CHAOS_FANATIC("The Chaos Fanatic", 4675, new String[0]),
    COCKATRICE("Cockatrice", 4137, "Cockathrice"),
    COWS("Cows", 11919, new String[0]),
    CRAWLING_HANDS("Crawling hands", 4133, "Crushing hand"),
    CRAZY_ARCHAEOLOGIST("Crazy Archaeologists", 11990, new String[0]),
    CROCODILES("Crocodiles", 10149, new String[0]),
    DAGANNOTH("Dagannoth", 8141, new String[0]),
    DAGANNOTH_KINGS("Dagannoth Kings", 12644, new String[0]),
    DARK_BEASTS("Dark beasts", 6637, "Night beast"),
    DARK_WARRIORS("Dark warriors", 1151, "Dark warrior"),
    DERANGED_ARCHAEOLOGIST("Deranged Archaeologist", 21629, new String[0]),
    DOGS("Dogs", 8132, "Jackal", "Temple Guardian"),
    DRAKES("Drakes", 23041, new String[0]),
    DUKE_SUCELLUS("Duke Sucellus", 28250, new String[0]),
    DUST_DEVILS("Dust devils", 4145, "Choke devil"),
    DWARVES("Dwarves", 11200, "Dwarf", "Black Guard"),
    EARTH_WARRIORS("Earth warriors", 12221, new String[0]),
    ELVES("Elves", 6105, "Elf", "Iorwerth Warrior", "Iorwerth Archer"),
    ENTS("Ents", 8174, new String[0]),
    FEVER_SPIDERS("Fever spiders", 6709, new String[0]),
    FIRE_GIANTS("Fire giants", 30622, "Branda the Fire Queen"),
    FLESH_CRAWLERS("Fleshcrawlers", 13459, "Flesh crawler"),
    FOSSIL_ISLAND_WYVERNS("Fossil island wyverns", 21507, "Ancient wyvern", "Long-tailed wyvern", "Spitting wyvern", "Taloned wyvern"),
    GARGOYLES("Gargoyles", 4147, 9, 4162, "Dusk", "Dawn"),
    GENERAL_GRAARDOR("General Graardor", 12650, new String[0]),
    GHOSTS("Ghosts", 552, "Death wing", "Tortured soul", "Forgotten Soul", "Revenant"),
    GHOULS("Ghouls", 6722, new String[0]),
    GIANT_MOLE("The Giant Mole", 12646, new String[0]),
    GOBLINS("Goblins", 13447, "Sergeant Strongstack", "Sergeant Grimspike", "Sergeant Steelwill"),
    GREATER_DEMONS("Greater demons", 20023, "K'ril Tsutsaroth", "Tstanon Karlak", "Skotizo", "Tormented Demon"),
    GREEN_DRAGONS("Green dragons", 12518, "Elvarg"),
    GROTESQUE_GUARDIANS("The Grotesque Guardians", 21750, 0, 4162, "Dusk", "Dawn"),
    HARPIE_BUG_SWARMS("Harpie bug swarms", 7050, new String[0]),
    HELLHOUNDS("Hellhounds", 8137, "Cerberus"),
    HILL_GIANTS("Hill giants", 13474, "Cyclops", "Reanimated giant", "Obor"),
    HOBGOBLINS("Hobgoblins", 8133, new String[0]),
    HYDRAS("Hydras", 23042, new String[0]),
    ICEFIENDS("Icefiends", 4671, new String[0]),
    ICE_GIANTS("Ice giants", 30624, "Eldric the Ice King"),
    ICE_WARRIORS("Ice warriors", 12293, "Icelord"),
    INFERNAL_MAGES("Infernal mages", 4140, "Malevolent mage"),
    IRON_DRAGONS("Iron dragons", 12365, new String[0]),
    JAD("TzTok-Jad", 13225, new String[0]),
    JELLIES("Jellies", 4142, "Jelly"),
    JUNGLE_HORROR("Jungle horrors", 13486, new String[0]),
    KALPHITE("Kalphite", 8139, new String[0]),
    KALPHITE_QUEEN("The Kalphite Queen", 12647, new String[0]),
    KILLERWATTS("Killerwatts", 7160, new String[0]),
    KING_BLACK_DRAGON("The King Black Dragon", 12653, new String[0]),
    KRAKEN("The Cave Kraken Boss", 12655, "Kraken"),
    KREEARRA("Kree'arra", 12649, new String[0]),
    KRIL_TSUTSAROTH("K'ril Tsutsaroth", 12652, new String[0]),
    KURASK("Kurask", 4146, new String[0]),
    LAVA_DRAGONS("Lava Dragons", 11992, "Lava dragon"),
    LESSER_DEMONS("Lesser demons", 20020, "Zakl'n Gritch"),
    LESSER_NAGUA("Lesser Nagua", 28823, "Sulphur Nagua", "Frost Nagua", "Amoxliatl"),
    LIZARDMEN("Lizardmen", 13391, "Lizardman"),
    LIZARDS("Lizards", 6695, new String[0]),
    MAGIC_AXES("Magic axes", 1363, new String[0]),
    MAMMOTHS("Mammoths", 10516, new String[0]),
    MINIONS_OF_SCABARAS("Minions of scabaras", 9028, "Scarab swarm", "Locust rider", "Scarab mage"),
    MINOTAURS("Minotaurs", 13456, new String[0]),
    MITHRIL_DRAGONS("Mithril dragons", 12369, new String[0]),
    MOGRES("Mogres", 6661, new String[0]),
    MOLANISKS("Molanisks", 10997, new String[0]),
    MONKEYS("Monkeys", 13450, "Tortured gorilla", "Demonic gorilla", "Padulah"),
    MOSS_GIANTS("Moss giants", 22374, "Bryophyta"),
    MUTATED_ZYGOMITES("Mutated zygomites", 7420, 7, 7431, "Zygomite", "Fungi"),
    NECHRYAEL("Nechryael", 4148, "Nechryarch"),
    OGRES("Ogres", 13477, "Enclave guard", "Mogre", "Ogress", "Skogre", "Zogre"),
    OTHERWORLDLY_BEING("Otherworldly beings", 6109, new String[0]),
    PHANTOM_MUSPAH("The Phantom Muspah", 27590, new String[0]),
    PIRATES("Pirates", 8950, "Pirate"),
    PYREFIENDS("Pyrefiends", 4138, "Flaming pyrelord"),
    RATS("Rats", 300, new String[0]),
    RED_DRAGONS("Red dragons", 8134, new String[0]),
    REVENANTS("Revenants", 21816, new String[0]),
    ROCKSLUGS("Rockslugs", 4136, 4, 4161, new String[0]),
    ROGUES("Rogues", 5554, new String[0]),
    RUNE_DRAGONS("Rune dragons", 23273, new String[0]),
    SARACHNIS("Sarachnis", 23495, new String[0]),
    SCORPIA("Scorpia", 13181, new String[0]),
    SCORPIONS("Scorpions", 13459, "Scorpia", "Lobstrosity"),
    SEA_SNAKES("Sea snakes", 7576, new String[0]),
    SHADES("Shades", 546, "Loar", "Phrin", "Riyl", "Asyn", "Fiyr", "Urium"),
    SHADOW_WARRIORS("Shadow warriors", 1165, new String[0]),
    SKELETAL_WYVERNS("Skeletal wyverns", 6811, new String[0]),
    SKELETONS("Skeletons", 8131, "Vet'ion", "Calvar'ion", "Skeletal Mystic"),
    SMOKE_DEVILS("Smoke devils", 5349, new String[0]),
    SOURHOGS("Sourhogs", 24944, new String[0]),
    SPIDERS("Spiders", 8135, "Kalrag", "Sarachnis", "Venenatis", "Spindel", "Araxxor", "Araxyte"),
    SPIRITUAL_CREATURES("Spiritual creatures", 11840, "Spiritual ranger", "Spiritual mage", "Spiritual warrior"),
    STEEL_DRAGONS("Steel dragons", 8142, new String[0]),
    SUQAHS("Suqahs", 9079, new String[0]),
    TERROR_DOGS("Terror dogs", 10591, new String[0]),
    THE_LEVIATHAN("The Leviathan", 28252, new String[0]),
    THE_WHISPERER("The Whisperer", 28246, new String[0]),
    THERMONUCLEAR_SMOKE_DEVIL("The Thermonuclear Smoke Devil", 12648, new String[0]),
    TROLLS("Trolls", 8136, "Dad", "Arrg", "Stick", "Kraka", "Pee Hat", "Rock", "Twig", "Berry"),
    TUROTH("Turoth", 4143, new String[0]),
    TZHAAR("Tzhaar", 13498, "TzTok-Jad", "TzKal-Zuk"),
    VAMPYRES("Vampyres", 1549, "Vyrewatch"),
    VARDORVIS("Vardorvis", 28248, new String[0]),
    VENENATIS("Venenatis", 13177, new String[0]),
    VETION("Vet'ion", 13179, new String[0]),
    VORKATH("Vorkath", 21992, new String[0]),
    WALL_BEASTS("Wall beasts", 4519, new String[0]),
    WARPED_CREATURES("Warped Creatures", 28582, "Warped terrorbird", "Warped tortoise", "Mutated terrorbird", "Mutated tortoise"),
    WATERFIENDS("Waterfiends", 571, new String[0]),
    WEREWOLVES("Werewolves", 2952, "Werewolf"),
    WOLVES("Wolves", 958, "Wolf"),
    WYRMS("Wyrms", 23040, "Wyrmling"),
    ZILYANA("Commander Zilyana", 12651, new String[0]),
    ZOMBIES("Zombies", 6722, "Undead", "Vorkath", "Zogre"),
    ZUK("TzKal-Zuk", 22319, new String[0]),
    ZULRAH("Zulrah", 12921, new String[0]);

    private static final Map<String, Task> tasks;
    private final String name;
    private final int itemSpriteId;
    private final String[] targetNames;
    private final int weaknessThreshold;
    private final int weaknessItem;

    private Task(String name, int itemSpriteId, String ... targetNames) {
        Preconditions.checkArgument((itemSpriteId >= 0 ? 1 : 0) != 0);
        this.name = name;
        this.itemSpriteId = itemSpriteId;
        this.weaknessThreshold = -1;
        this.weaknessItem = -1;
        this.targetNames = targetNames;
    }

    private Task(String name, int itemSpriteId, int weaknessThreshold, int weaknessItem, String ... targetNames) {
        Preconditions.checkArgument((itemSpriteId >= 0 ? 1 : 0) != 0);
        this.name = name;
        this.itemSpriteId = itemSpriteId;
        this.weaknessThreshold = weaknessThreshold;
        this.weaknessItem = weaknessItem;
        this.targetNames = targetNames;
    }

    @Nullable
    static Task getTask(String taskName) {
        return tasks.get(taskName.toLowerCase());
    }

    public String getName() {
        return this.name;
    }

    public int getItemSpriteId() {
        return this.itemSpriteId;
    }

    public String[] getTargetNames() {
        return this.targetNames;
    }

    public int getWeaknessThreshold() {
        return this.weaknessThreshold;
    }

    public int getWeaknessItem() {
        return this.weaknessItem;
    }

    static {
        ImmutableMap.Builder builder = new ImmutableMap.Builder();
        for (Task task : Task.values()) {
            builder.put((Object)task.getName().toLowerCase(), (Object)task);
        }
        tasks = builder.build();
    }
}

