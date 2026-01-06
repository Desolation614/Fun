/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.base.MoreObjects
 *  com.google.common.base.Strings
 *  com.google.common.collect.ImmutableMap
 *  com.google.gson.Gson
 *  com.google.gson.JsonSyntaxException
 *  com.google.gson.reflect.TypeToken
 *  com.google.inject.Provides
 *  javax.inject.Inject
 *  net.runelite.api.ChatMessageType
 *  net.runelite.api.Client
 *  net.runelite.api.EnumComposition
 *  net.runelite.api.Experience
 *  net.runelite.api.GameState
 *  net.runelite.api.IconID
 *  net.runelite.api.IndexedSprite
 *  net.runelite.api.ItemComposition
 *  net.runelite.api.MessageNode
 *  net.runelite.api.Player
 *  net.runelite.api.Skill
 *  net.runelite.api.events.ChatMessage
 *  net.runelite.api.events.GameStateChanged
 *  net.runelite.api.events.GameTick
 *  net.runelite.api.events.ScriptPostFired
 *  net.runelite.api.events.VarbitChanged
 *  net.runelite.api.events.WidgetLoaded
 *  net.runelite.api.widgets.Widget
 *  net.runelite.http.api.chat.Duels
 *  net.runelite.http.api.item.ItemPrice
 *  org.apache.commons.text.WordUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package net.runelite.client.plugins.chatcommands;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.MoreObjects;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.google.inject.Provides;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ScheduledExecutorService;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import javax.inject.Inject;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.EnumComposition;
import net.runelite.api.Experience;
import net.runelite.api.GameState;
import net.runelite.api.IconID;
import net.runelite.api.IndexedSprite;
import net.runelite.api.ItemComposition;
import net.runelite.api.MessageNode;
import net.runelite.api.Player;
import net.runelite.api.Skill;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.ScriptPostFired;
import net.runelite.api.events.VarbitChanged;
import net.runelite.api.events.WidgetLoaded;
import net.runelite.api.widgets.Widget;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.chat.ChatClient;
import net.runelite.client.chat.ChatColorType;
import net.runelite.client.chat.ChatCommandManager;
import net.runelite.client.chat.ChatMessageBuilder;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.config.RuneLiteConfig;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ChatInput;
import net.runelite.client.game.ItemManager;
import net.runelite.client.hiscore.HiscoreClient;
import net.runelite.client.hiscore.HiscoreEndpoint;
import net.runelite.client.hiscore.HiscoreResult;
import net.runelite.client.hiscore.HiscoreSkill;
import net.runelite.client.input.KeyManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.chatcommands.ChatCommandsConfig;
import net.runelite.client.plugins.chatcommands.ChatKeyboardListener;
import net.runelite.client.plugins.chatcommands.Pet;
import net.runelite.client.util.AsyncBufferedImage;
import net.runelite.client.util.ImageUtil;
import net.runelite.client.util.QuantityFormatter;
import net.runelite.client.util.Text;
import net.runelite.http.api.chat.Duels;
import net.runelite.http.api.item.ItemPrice;
import org.apache.commons.text.WordUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@PluginDescriptor(name="Chat Commands", description="Enable chat commands", tags={"grand", "exchange", "level", "prices"})
public class ChatCommandsPlugin
extends Plugin {
    private static final Logger log = LoggerFactory.getLogger(ChatCommandsPlugin.class);
    private static final Pattern KILLCOUNT_PATTERN = Pattern.compile("Your (?<pre>completion count for |subdued |completed )?(?:<col=[0-9a-f]{6}>)?(?<boss>.+?)(?:</col>)? (?<post>(?:(?:kill|harvest|lap|completion|success) )?(?:count )?)is: ?<col=[0-9a-f]{6}>(?<kc>[0-9,]+)</col>");
    private static final String TEAM_SIZES = "(?<teamsize>\\d+(?:\\+|-\\d+)? players?|Solo)";
    private static final Pattern RAIDS_PB_PATTERN = Pattern.compile("<col=ef20ff>Congratulations - your raid is complete!</col><br>Team size: <col=ff0000>(?<teamsize>\\d+(?:\\+|-\\d+)? players?|Solo)</col> Duration:</col> <col=ff0000>(?<pb>[0-9:]+(?:\\.[0-9]+)?)</col> \\(new personal best\\)</col>");
    private static final Pattern RAIDS_DURATION_PATTERN = Pattern.compile("<col=ef20ff>Congratulations - your raid is complete!</col><br>Team size: <col=ff0000>(?<teamsize>\\d+(?:\\+|-\\d+)? players?|Solo)</col> Duration:</col> <col=ff0000>[0-9:.]+</col> Personal best: </col><col=ff0000>(?<pb>[0-9:]+(?:\\.[0-9]+)?)</col>");
    private static final Pattern KILL_DURATION_PATTERN = Pattern.compile("(?i)(?:(?:Fight |Lap |Challenge |Corrupted challenge )?duration:|Subdued in|(?<!total )completion time:) <col=[0-9a-f]{6}>[0-9:.]+</col>\\. Personal best: (?:<col=ff0000>)?(?<pb>[0-9:]+(?:\\.[0-9]+)?)");
    private static final Pattern NEW_PB_PATTERN = Pattern.compile("(?i)(?:(?:Fight |Lap |Challenge |Corrupted challenge )?duration:|Subdued in|(?<!total )completion time:) <col=[0-9a-f]{6}>(?<pb>[0-9:]+(?:\\.[0-9]+)?)</col> \\(new personal best\\)");
    private static final Pattern DUEL_ARENA_WINS_PATTERN = Pattern.compile("You (were defeated|won)! You have(?: now)? won ([\\d,]+|one) duels?");
    private static final Pattern DUEL_ARENA_LOSSES_PATTERN = Pattern.compile("You have(?: now)? lost ([\\d,]+|one) duels?");
    private static final Pattern ADVENTURE_LOG_TITLE_PATTERN = Pattern.compile("The Exploits of (.+)");
    private static final Pattern ADVENTURE_LOG_PB_PATTERN = Pattern.compile("Fastest (?:kill|run|Room time)(?: - \\(Team size: \\(?(?<teamsize>\\d+(?:\\+|-\\d+)? players?|Solo)\\)\\)?)?: (?<time>[0-9:]+(?:\\.[0-9]+)?)");
    private static final Pattern HS_PB_PATTERN = Pattern.compile("Floor (?<floor>\\d) time: <col=ff0000>(?<floortime>[0-9:]+(?:\\.[0-9]+)?)</col>(?: \\(new personal best\\)|. Personal best: (?<floorpb>[0-9:]+(?:\\.[0-9]+)?))(?:<br>Overall time: <col=ff0000>(?<otime>[0-9:]+(?:\\.[0-9]+)?)</col>(?: \\(new personal best\\)|. Personal best: (?<opb>[0-9:]+(?:\\.[0-9]+)?)))?");
    private static final Pattern HS_KC_FLOOR_PATTERN = Pattern.compile("You have completed Floor (\\d) of the Hallowed Sepulchre! Total completions: <col=ff0000>([0-9,]+)</col>\\.");
    private static final Pattern HS_KC_GHC_PATTERN = Pattern.compile("You have opened the Grand Hallowed Coffin <col=ff0000>([0-9,]+)</col> times?!");
    private static final Pattern COLLECTION_LOG_ITEM_PATTERN = Pattern.compile("New item added to your collection log: (.*)");
    private static final Pattern GUARDIANS_OF_THE_RIFT_PATTERN = Pattern.compile("Amount of Rifts you have closed: <col=ff0000>([0-9,]+)</col>\\.", 2);
    private static final Pattern HUNTER_RUMOUR_KC_PATTERN = Pattern.compile("You have completed <col=[0-9a-f]{6}>([0-9,]+)</col> rumours? for the Hunter Guild\\.");
    private static final Pattern BIRD_EGG_OFFERING_PATTERN = Pattern.compile("You have made <col=ff0000>(?<kc>[\\d,]+|one)</col> offerings?\\.");
    private static final Pattern CHEST_OPENING_PATTERN = Pattern.compile("You have (?<never>never )?opened (the )?(?<chest>crystal chest|Larran's big chest|Larran's small chest|Brimstone chest)( (?<kc>[\\d,]+ times|once))?\\.");
    private static final String TOTAL_LEVEL_COMMAND_STRING = "!total";
    private static final String PRICE_COMMAND_STRING = "!price";
    private static final String LEVEL_COMMAND_STRING = "!lvl";
    private static final String BOUNTY_HUNTER_HUNTER_COMMAND = "!bh";
    private static final String BOUNTY_HUNTER_ROGUE_COMMAND = "!bhrogue";
    private static final String CLUES_COMMAND_STRING = "!clues";
    private static final String LAST_MAN_STANDING_COMMAND = "!lms";
    private static final String KILLCOUNT_COMMAND_STRING = "!kc";
    private static final String CMB_COMMAND_STRING = "!cmb";
    private static final String QP_COMMAND_STRING = "!qp";
    private static final String PB_COMMAND = "!pb";
    private static final String GC_COMMAND_STRING = "!gc";
    private static final String DUEL_ARENA_COMMAND = "!duels";
    private static final String LEAGUE_POINTS_COMMAND = "!lp";
    private static final String SOUL_WARS_ZEAL_COMMAND = "!sw";
    private static final String PET_LIST_COMMAND = "!pets";
    private static final String CA_COMMAND = "!ca";
    private static final String CLOG_COMMAND = "!clog";
    @VisibleForTesting
    static final int ADV_LOG_EXPLOITS_TEXT_INDEX = 1;
    static final int COL_LOG_ENTRY_HEADER_TITLE_INDEX = 0;
    private static final Map<String, String> KILLCOUNT_RENAMES = ImmutableMap.of((Object)"Barrows chest", (Object)"Barrows Chests");
    private boolean bossLogLoaded;
    private boolean advLogLoaded;
    private boolean scrollInterfaceLoaded;
    private String pohOwner;
    private HiscoreEndpoint hiscoreEndpoint;
    private String lastBossKill;
    private int lastBossTime = -1;
    private double lastPb = -1.0;
    private String lastTeamSize;
    private int petsIconIdx = -1;
    private int[] pets;
    @Inject
    private Client client;
    @Inject
    private ClientThread clientThread;
    @Inject
    private ChatCommandsConfig config;
    @Inject
    private ConfigManager configManager;
    @Inject
    private ItemManager itemManager;
    @Inject
    private ChatCommandManager chatCommandManager;
    @Inject
    private ScheduledExecutorService executor;
    @Inject
    private KeyManager keyManager;
    @Inject
    private ChatKeyboardListener chatKeyboardListener;
    @Inject
    private HiscoreClient hiscoreClient;
    @Inject
    private ChatClient chatClient;
    @Inject
    private RuneLiteConfig runeLiteConfig;
    @Inject
    private Gson gson;

    @Override
    public void startUp() {
        this.keyManager.registerKeyListener(this.chatKeyboardListener);
        this.chatCommandManager.registerCommandAsync(TOTAL_LEVEL_COMMAND_STRING, this::playerSkillLookup);
        this.chatCommandManager.registerCommandAsync(CMB_COMMAND_STRING, this::combatLevelLookup);
        this.chatCommandManager.registerCommand(PRICE_COMMAND_STRING, this::itemPriceLookup);
        this.chatCommandManager.registerCommandAsync(LEVEL_COMMAND_STRING, this::playerSkillLookup);
        this.chatCommandManager.registerCommandAsync(BOUNTY_HUNTER_HUNTER_COMMAND, this::bountyHunterHunterLookup);
        this.chatCommandManager.registerCommandAsync(BOUNTY_HUNTER_ROGUE_COMMAND, this::bountyHunterRogueLookup);
        this.chatCommandManager.registerCommandAsync(CLUES_COMMAND_STRING, this::clueLookup);
        this.chatCommandManager.registerCommandAsync(LAST_MAN_STANDING_COMMAND, this::lastManStandingLookup);
        this.chatCommandManager.registerCommandAsync(LEAGUE_POINTS_COMMAND, this::leaguePointsLookup);
        this.chatCommandManager.registerCommandAsync(KILLCOUNT_COMMAND_STRING, this::killCountLookup, this::killCountSubmit);
        this.chatCommandManager.registerCommandAsync(QP_COMMAND_STRING, this::questPointsLookup, this::questPointsSubmit);
        this.chatCommandManager.registerCommandAsync(PB_COMMAND, this::personalBestLookup, this::personalBestSubmit);
        this.chatCommandManager.registerCommandAsync(GC_COMMAND_STRING, this::gambleCountLookup, this::gambleCountSubmit);
        this.chatCommandManager.registerCommandAsync(DUEL_ARENA_COMMAND, this::duelArenaLookup, this::duelArenaSubmit);
        this.chatCommandManager.registerCommandAsync(SOUL_WARS_ZEAL_COMMAND, this::soulWarsZealLookup);
        this.chatCommandManager.registerCommandAsync(PET_LIST_COMMAND, this::petListLookup, this::petListSubmit);
        this.chatCommandManager.registerCommandAsync(CA_COMMAND, this::caLookup, this::caSubmit);
        this.chatCommandManager.registerCommandAsync(CLOG_COMMAND, this::clogLookup, this::clogSubmit);
        this.clientThread.invoke(() -> {
            if (this.client.getGameState().getState() >= GameState.LOGIN_SCREEN.getState() && this.petsIconIdx == -1) {
                this.loadPets();
            }
        });
    }

    @Override
    public void shutDown() {
        this.pets = null;
        this.lastBossKill = null;
        this.lastBossTime = -1;
        this.keyManager.unregisterKeyListener(this.chatKeyboardListener);
        this.chatCommandManager.unregisterCommand(TOTAL_LEVEL_COMMAND_STRING);
        this.chatCommandManager.unregisterCommand(CMB_COMMAND_STRING);
        this.chatCommandManager.unregisterCommand(PRICE_COMMAND_STRING);
        this.chatCommandManager.unregisterCommand(LEVEL_COMMAND_STRING);
        this.chatCommandManager.unregisterCommand(BOUNTY_HUNTER_HUNTER_COMMAND);
        this.chatCommandManager.unregisterCommand(BOUNTY_HUNTER_ROGUE_COMMAND);
        this.chatCommandManager.unregisterCommand(CLUES_COMMAND_STRING);
        this.chatCommandManager.unregisterCommand(LAST_MAN_STANDING_COMMAND);
        this.chatCommandManager.unregisterCommand(LEAGUE_POINTS_COMMAND);
        this.chatCommandManager.unregisterCommand(KILLCOUNT_COMMAND_STRING);
        this.chatCommandManager.unregisterCommand(QP_COMMAND_STRING);
        this.chatCommandManager.unregisterCommand(PB_COMMAND);
        this.chatCommandManager.unregisterCommand(GC_COMMAND_STRING);
        this.chatCommandManager.unregisterCommand(DUEL_ARENA_COMMAND);
        this.chatCommandManager.unregisterCommand(SOUL_WARS_ZEAL_COMMAND);
        this.chatCommandManager.unregisterCommand(PET_LIST_COMMAND);
        this.chatCommandManager.unregisterCommand(CA_COMMAND);
        this.chatCommandManager.unregisterCommand(CLOG_COMMAND);
    }

    @Provides
    ChatCommandsConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(ChatCommandsConfig.class);
    }

    private void setKc(String boss, int killcount) {
        this.configManager.setRSProfileConfiguration("killcount", boss.toLowerCase(), killcount);
    }

    private void unsetKc(String boss) {
        this.configManager.unsetRSProfileConfiguration("killcount", boss.toLowerCase());
    }

    private int getKc(String boss) {
        Integer killCount = (Integer)this.configManager.getRSProfileConfiguration("killcount", boss.toLowerCase(), Integer.TYPE);
        return killCount == null ? 0 : killCount;
    }

    private void setPb(String boss, double seconds) {
        this.configManager.setRSProfileConfiguration("personalbest", boss.toLowerCase(), seconds);
    }

    private void unsetPb(String boss) {
        this.configManager.unsetRSProfileConfiguration("personalbest", boss.toLowerCase());
    }

    private double getPb(String boss) {
        Double personalBest = (Double)this.configManager.getRSProfileConfiguration("personalbest", boss.toLowerCase(), Double.TYPE);
        return personalBest == null ? 0.0 : personalBest;
    }

    private void loadPets() {
        assert (this.petsIconIdx == -1);
        EnumComposition petsEnum = this.client.getEnum(2158);
        this.pets = new int[petsEnum.size()];
        for (int i = 0; i < petsEnum.size(); ++i) {
            this.pets[i] = petsEnum.getIntValue(i);
        }
        IndexedSprite[] modIcons = this.client.getModIcons();
        assert (modIcons != null);
        IndexedSprite[] newModIcons = Arrays.copyOf(modIcons, modIcons.length + this.pets.length);
        this.petsIconIdx = modIcons.length;
        this.client.setModIcons(newModIcons);
        for (int i = 0; i < this.pets.length; ++i) {
            int petId = this.pets[i];
            AsyncBufferedImage abi = this.itemManager.getImage(petId);
            int idx = this.petsIconIdx + i;
            abi.onLoaded(() -> {
                IndexedSprite sprite;
                BufferedImage image = ImageUtil.resizeImage(abi, 18, 16);
                this.client.getModIcons()[idx] = sprite = ImageUtil.getImageIndexedSprite(image, this.client);
            });
        }
    }

    private void setPetList(List<Integer> petList) {
        if (petList == null) {
            return;
        }
        this.configManager.setRSProfileConfiguration("chatcommands", "pets2", this.gson.toJson(petList));
        this.configManager.unsetRSProfileConfiguration("chatcommands", "pets");
    }

    private List<Pet> getPetListOld() {
        List<Pet> petList;
        String petListJson = (String)this.configManager.getRSProfileConfiguration("chatcommands", "pets", (Type)((Object)String.class));
        try {
            petList = (List<Pet>)this.gson.fromJson(petListJson, new TypeToken<List<Pet>>(){}.getType());
        }
        catch (JsonSyntaxException ex) {
            return Collections.emptyList();
        }
        return petList != null ? petList : Collections.emptyList();
    }

    private List<Integer> getPetList() {
        List<Integer> petList;
        List<Pet> old = this.getPetListOld();
        if (!old.isEmpty()) {
            List<Integer> l = old.stream().map(Pet::getIconID).collect(Collectors.toList());
            this.setPetList(l);
            return l;
        }
        String petListJson = (String)this.configManager.getRSProfileConfiguration("chatcommands", "pets2", (Type)((Object)String.class));
        try {
            petList = (List<Integer>)this.gson.fromJson(petListJson, new TypeToken<List<Integer>>(){}.getType());
        }
        catch (JsonSyntaxException ex) {
            return Collections.emptyList();
        }
        return petList != null ? petList : Collections.emptyList();
    }

    @Subscribe
    public void onChatMessage(ChatMessage chatMessage) {
        ArrayList<Integer> petList;
        String item;
        int petId;
        int kc;
        int floor;
        if (chatMessage.getType() != ChatMessageType.TRADE && chatMessage.getType() != ChatMessageType.GAMEMESSAGE && chatMessage.getType() != ChatMessageType.SPAM && chatMessage.getType() != ChatMessageType.FRIENDSCHATNOTIFICATION) {
            return;
        }
        String message = chatMessage.getMessage();
        Matcher matcher = KILLCOUNT_PATTERN.matcher(message);
        if (matcher.find()) {
            String boss = matcher.group("boss");
            int kc2 = Integer.parseInt(matcher.group("kc").replace(",", ""));
            String pre = matcher.group("pre");
            String post = matcher.group("post");
            if (Strings.isNullOrEmpty((String)pre) && Strings.isNullOrEmpty((String)post)) {
                this.unsetKc(boss);
                return;
            }
            String renamedBoss = KILLCOUNT_RENAMES.getOrDefault(boss, boss).replace(":", "");
            if (boss != renamedBoss) {
                this.unsetKc(boss);
                this.unsetPb(boss);
                this.unsetKc(boss.replace(":", "."));
                this.unsetPb(boss.replace(":", "."));
                this.unsetKc("Theatre of Blood Story Mode");
                this.unsetPb("Theatre of Blood Story Mode");
            }
            this.setKc(renamedBoss, kc2);
            if (this.lastPb > -1.0) {
                log.debug("Got out-of-order personal best for {}: {}", (Object)renamedBoss, (Object)this.lastPb);
                if (renamedBoss.contains("Theatre of Blood")) {
                    int tobTeamSize = this.tobTeamSize();
                    this.lastTeamSize = tobTeamSize == 1 ? "Solo" : tobTeamSize + " players";
                } else if (renamedBoss.contains("Tombs of Amascut")) {
                    int toaTeamSize = this.toaTeamSize();
                    this.lastTeamSize = toaTeamSize == 1 ? "Solo" : toaTeamSize + " players";
                }
                double pb = this.getPb(renamedBoss);
                if (this.lastTeamSize == null || pb == 0.0 || this.lastPb < pb) {
                    log.debug("Setting overall pb (old: {})", (Object)pb);
                    this.setPb(renamedBoss, this.lastPb);
                }
                if (this.lastTeamSize != null) {
                    log.debug("Setting team size pb: {}", (Object)this.lastTeamSize);
                    this.setPb(renamedBoss + " " + this.lastTeamSize, this.lastPb);
                }
                this.lastPb = -1.0;
                this.lastTeamSize = null;
            } else {
                this.lastBossKill = renamedBoss;
                this.lastBossTime = this.client.getTickCount();
            }
            return;
        }
        matcher = DUEL_ARENA_WINS_PATTERN.matcher(message);
        if (matcher.find()) {
            int oldWins = this.getKc("Duel Arena Wins");
            int wins = matcher.group(2).equals("one") ? 1 : Integer.parseInt(matcher.group(2).replace(",", ""));
            String result = matcher.group(1);
            int winningStreak = this.getKc("Duel Arena Win Streak");
            int losingStreak = this.getKc("Duel Arena Lose Streak");
            if (result.equals("won") && wins > oldWins) {
                losingStreak = 0;
                ++winningStreak;
            } else if (result.equals("were defeated")) {
                ++losingStreak;
                winningStreak = 0;
            } else {
                log.warn("unrecognized duel streak chat message: {}", (Object)message);
            }
            this.setKc("Duel Arena Wins", wins);
            this.setKc("Duel Arena Win Streak", winningStreak);
            this.setKc("Duel Arena Lose Streak", losingStreak);
        }
        if ((matcher = DUEL_ARENA_LOSSES_PATTERN.matcher(message)).find()) {
            int losses = matcher.group(1).equals("one") ? 1 : Integer.parseInt(matcher.group(1).replace(",", ""));
            this.setKc("Duel Arena Losses", losses);
        }
        if ((matcher = KILL_DURATION_PATTERN.matcher(message)).find()) {
            this.matchPb(matcher);
        }
        if ((matcher = NEW_PB_PATTERN.matcher(message)).find()) {
            this.matchPb(matcher);
        }
        if ((matcher = RAIDS_PB_PATTERN.matcher(message)).find()) {
            this.matchPb(matcher);
        }
        if ((matcher = RAIDS_DURATION_PATTERN.matcher(message)).find()) {
            this.matchPb(matcher);
        }
        if ((matcher = HS_PB_PATTERN.matcher(message)).find()) {
            floor = Integer.parseInt(matcher.group("floor"));
            String floortime = matcher.group("floortime");
            String floorpb = matcher.group("floorpb");
            String otime = matcher.group("otime");
            String opb = matcher.group("opb");
            String pb = (String)MoreObjects.firstNonNull((Object)floorpb, (Object)floortime);
            this.setPb("Hallowed Sepulchre Floor " + floor, ChatCommandsPlugin.timeStringToSeconds(pb));
            if (otime != null) {
                pb = (String)MoreObjects.firstNonNull((Object)opb, (Object)otime);
                this.setPb("Hallowed Sepulchre", ChatCommandsPlugin.timeStringToSeconds(pb));
            }
        }
        if ((matcher = HS_KC_FLOOR_PATTERN.matcher(message)).find()) {
            floor = Integer.parseInt(matcher.group(1));
            int kc3 = Integer.parseInt(matcher.group(2).replace(",", ""));
            this.setKc("Hallowed Sepulchre Floor " + floor, kc3);
        }
        if ((matcher = HS_KC_GHC_PATTERN.matcher(message)).find()) {
            kc = Integer.parseInt(matcher.group(1).replace(",", ""));
            this.setKc("Hallowed Sepulchre", kc);
        }
        if ((matcher = HUNTER_RUMOUR_KC_PATTERN.matcher(message)).find()) {
            kc = Integer.parseInt(matcher.group(1).replace(",", ""));
            this.setKc("Hunter Rumours", kc);
        }
        if (this.lastBossKill != null && this.lastBossTime != this.client.getTickCount()) {
            this.lastBossKill = null;
            this.lastBossTime = -1;
        }
        if ((matcher = COLLECTION_LOG_ITEM_PATTERN.matcher(message)).find() && (petId = this.findPet(item = matcher.group(1))) != -1 && !(petList = new ArrayList<Integer>(this.getPetList())).contains(petId)) {
            log.debug("New pet added: {}/{}", (Object)item, (Object)petId);
            petList.add(petId);
            this.setPetList(petList);
        }
        if ((matcher = GUARDIANS_OF_THE_RIFT_PATTERN.matcher(message)).find()) {
            int kc4 = Integer.parseInt(matcher.group(1).replace(",", ""));
            this.setKc("Guardians of the Rift", kc4);
        }
        if ((matcher = BIRD_EGG_OFFERING_PATTERN.matcher(message)).find()) {
            String kcString = matcher.group("kc");
            int kc5 = kcString.equals("one") ? 1 : Integer.parseInt(kcString.replace(",", ""));
            this.setKc("Bird's egg offerings", kc5);
        }
        if ((matcher = CHEST_OPENING_PATTERN.matcher(message)).find()) {
            String kcString;
            int kc6 = matcher.group("never") != null ? 0 : ((kcString = matcher.group("kc")).equals("once") ? 1 : Integer.parseInt(kcString.split(" ")[0].replace(",", "")));
            String chest = matcher.group("chest");
            this.setKc(chest, kc6);
        }
    }

    @VisibleForTesting
    static double timeStringToSeconds(String timeString) {
        String[] s = timeString.split(":");
        if (s.length == 2) {
            return (double)(Integer.parseInt(s[0]) * 60) + Double.parseDouble(s[1]);
        }
        if (s.length == 3) {
            return (double)(Integer.parseInt(s[0]) * 60 * 60 + Integer.parseInt(s[1]) * 60) + Double.parseDouble(s[2]);
        }
        return Double.parseDouble(timeString);
    }

    @VisibleForTesting
    static String secondsToTimeString(double seconds) {
        int hours = (int)(Math.floor(seconds) / 3600.0);
        int minutes = (int)(Math.floor(seconds / 60.0) % 60.0);
        String timeString = hours > 0 ? String.format("%d:%02d:", hours, minutes) : String.format("%d:", minutes);
        return timeString + (Math.floor(seconds) == seconds ? String.format("%02d", (int)seconds) : String.format("%05.2f", seconds %= 60.0));
    }

    private void matchPb(Matcher matcher) {
        double seconds = ChatCommandsPlugin.timeStringToSeconds(matcher.group("pb"));
        if (this.lastBossKill != null) {
            log.debug("Got personal best for {}: {}", (Object)this.lastBossKill, (Object)seconds);
            this.setPb(this.lastBossKill, seconds);
            this.lastPb = -1.0;
            this.lastTeamSize = null;
        } else {
            this.lastPb = seconds;
            try {
                this.lastTeamSize = matcher.group("teamsize");
            }
            catch (IllegalArgumentException ex) {
                this.lastTeamSize = null;
            }
        }
    }

    @Subscribe
    public void onGameTick(GameTick event) {
        if (this.client.getLocalPlayer() == null) {
            return;
        }
        if (this.advLogLoaded) {
            Matcher advLogExploitsText;
            this.advLogLoaded = false;
            Widget adventureLog = this.client.getWidget(0xBB0000);
            if (adventureLog != null && (advLogExploitsText = ADVENTURE_LOG_TITLE_PATTERN.matcher(adventureLog.getChild(1).getText())).find()) {
                this.pohOwner = advLogExploitsText.group(1);
            }
        }
        if (this.bossLogLoaded && (this.pohOwner == null || this.pohOwner.equals(this.client.getLocalPlayer().getName()))) {
            this.bossLogLoaded = false;
            Widget title = this.client.getWidget(35979267);
            Widget bossMonster = this.client.getWidget(35979277);
            Widget bossKills = this.client.getWidget(35979278);
            if (title == null || bossMonster == null || bossKills == null || !"Boss Kill Log".equals(title.getText())) {
                return;
            }
            Widget[] bossChildren = bossMonster.getChildren();
            Widget[] killsChildren = bossKills.getChildren();
            for (int i = 0; i < bossChildren.length; ++i) {
                Widget boss = bossChildren[i];
                Widget kill = killsChildren[i];
                String bossName = boss.getText().replace(":", "");
                int kc = Integer.parseInt(kill.getText().replace(",", ""));
                if (kc == this.getKc(ChatCommandsPlugin.longBossName(bossName))) continue;
                this.setKc(ChatCommandsPlugin.longBossName(bossName), kc);
            }
        }
        if (this.scrollInterfaceLoaded) {
            this.scrollInterfaceLoaded = false;
            if (this.client.getLocalPlayer().getName().equals(this.pohOwner)) {
                Widget parent = this.client.getWidget(48562179);
                Widget[] children = parent.getStaticChildren();
                String[] text = (String[])Arrays.stream(children).map(Widget::getText).map(Text::removeTags).toArray(String[]::new);
                for (int i = 0; i < text.length; ++i) {
                    String line;
                    String boss = ChatCommandsPlugin.longBossName(text[i]);
                    ++i;
                    while (i < text.length && !(line = text[i]).isEmpty()) {
                        Matcher matcher = ADVENTURE_LOG_PB_PATTERN.matcher(line);
                        if (matcher.find()) {
                            double s = ChatCommandsPlugin.timeStringToSeconds(matcher.group("time"));
                            Object teamSize = matcher.group("teamsize");
                            if (teamSize != null) {
                                if (((String)teamSize).equals("1 player")) {
                                    teamSize = "Solo";
                                } else if (((String)teamSize).endsWith("player")) {
                                    teamSize = (String)teamSize + "s";
                                }
                                log.debug("Found team-size adventure log PB for {} {}: {}", new Object[]{boss, teamSize, s});
                                this.setPb(boss + " " + (String)teamSize, s);
                            } else {
                                log.debug("Found adventure log PB for {}: {}", (Object)boss, (Object)s);
                                this.setPb(boss, s);
                            }
                        }
                        ++i;
                    }
                }
            }
        }
    }

    @Subscribe
    public void onScriptPostFired(ScriptPostFired scriptPostFired) {
        Widget collectionLogEntryItems;
        Widget entryTitle;
        Widget collectionLogEntryHeader;
        if (scriptPostFired.getScriptId() != 2731) {
            return;
        }
        if ((this.pohOwner == null || this.pohOwner.equals(this.client.getLocalPlayer().getName())) && (collectionLogEntryHeader = this.client.getWidget(40697876)) != null && collectionLogEntryHeader.getChildren() != null && (entryTitle = collectionLogEntryHeader.getChild(0)).getText().equals("All Pets") && (collectionLogEntryItems = this.client.getWidget(40697893)) != null && collectionLogEntryItems.getChildren() != null) {
            ArrayList<Integer> petList = new ArrayList<Integer>();
            for (Widget child : collectionLogEntryItems.getChildren()) {
                if (child.getOpacity() != 0) continue;
                petList.add(child.getItemId());
            }
            this.setPetList(petList);
            log.debug("Loaded {} pets", (Object)petList.size());
        }
    }

    @Subscribe
    public void onWidgetLoaded(WidgetLoaded widget) {
        switch (widget.getGroupId()) {
            case 187: {
                this.advLogLoaded = true;
                break;
            }
            case 549: {
                this.bossLogLoaded = true;
                break;
            }
            case 741: {
                this.scrollInterfaceLoaded = true;
            }
        }
    }

    @Subscribe
    public void onGameStateChanged(GameStateChanged event) {
        switch (event.getGameState()) {
            case LOADING: 
            case HOPPING: {
                this.pohOwner = null;
                break;
            }
            case STARTING: {
                this.petsIconIdx = -1;
                this.pets = null;
                break;
            }
            case LOGIN_SCREEN: {
                if (this.petsIconIdx != -1) break;
                this.loadPets();
            }
        }
    }

    @Subscribe
    public void onVarbitChanged(VarbitChanged event) {
        this.hiscoreEndpoint = this.getLocalHiscoreEndpointType();
        if (event.getVarpId() == 4824 && event.getValue() > 0) {
            this.setKc("Doom of Mokhaiotl", event.getValue());
        }
    }

    private boolean killCountSubmit(ChatInput chatInput, String value) {
        int idx = value.indexOf(32);
        String boss = ChatCommandsPlugin.longBossName(value.substring(idx + 1));
        int kc = this.getKc(boss);
        if (kc <= 0) {
            return false;
        }
        String playerName = this.client.getLocalPlayer().getName();
        this.executor.execute(() -> {
            try {
                this.chatClient.submitKc(playerName, boss, kc);
            }
            catch (Exception ex) {
                log.warn("unable to submit killcount", (Throwable)ex);
            }
            finally {
                chatInput.resume();
            }
        });
        return true;
    }

    @VisibleForTesting
    void killCountLookup(ChatMessage chatMessage, String message) {
        int kc;
        if (!this.config.killcount()) {
            return;
        }
        if (message.length() <= KILLCOUNT_COMMAND_STRING.length()) {
            return;
        }
        ChatMessageType type = chatMessage.getType();
        String search = message.substring(KILLCOUNT_COMMAND_STRING.length() + 1);
        String player = type.equals((Object)ChatMessageType.PRIVATECHATOUT) ? this.client.getLocalPlayer().getName() : Text.sanitize(chatMessage.getName());
        search = ChatCommandsPlugin.longBossName(search);
        try {
            kc = this.chatClient.getKc(player, search);
        }
        catch (IOException ex) {
            log.debug("unable to lookup killcount", (Throwable)ex);
            return;
        }
        String response = new ChatMessageBuilder().append(ChatColorType.HIGHLIGHT).append(search).append(ChatColorType.NORMAL).append(" kill count: ").append(ChatColorType.HIGHLIGHT).append(String.format("%,d", kc)).build();
        log.debug("Setting response {}", (Object)response);
        MessageNode messageNode = chatMessage.getMessageNode();
        messageNode.setRuneLiteFormatMessage(response);
        this.client.refreshChat();
    }

    private boolean duelArenaSubmit(ChatInput chatInput, String value) {
        int wins = this.getKc("Duel Arena Wins");
        int losses = this.getKc("Duel Arena Losses");
        int winningStreak = this.getKc("Duel Arena Win Streak");
        int losingStreak = this.getKc("Duel Arena Lose Streak");
        if (wins <= 0 && losses <= 0 && winningStreak <= 0 && losingStreak <= 0) {
            return false;
        }
        String playerName = this.client.getLocalPlayer().getName();
        this.executor.execute(() -> {
            try {
                this.chatClient.submitDuels(playerName, wins, losses, winningStreak, losingStreak);
            }
            catch (Exception ex) {
                log.warn("unable to submit duels", (Throwable)ex);
            }
            finally {
                chatInput.resume();
            }
        });
        return true;
    }

    private void duelArenaLookup(ChatMessage chatMessage, String message) {
        Duels duels;
        if (!this.config.duels()) {
            return;
        }
        ChatMessageType type = chatMessage.getType();
        String player = type == ChatMessageType.PRIVATECHATOUT ? this.client.getLocalPlayer().getName() : Text.sanitize(chatMessage.getName());
        try {
            duels = this.chatClient.getDuels(player);
        }
        catch (IOException ex) {
            log.debug("unable to lookup duels", (Throwable)ex);
            return;
        }
        int wins = duels.getWins();
        int losses = duels.getLosses();
        int winningStreak = duels.getWinningStreak();
        int losingStreak = duels.getLosingStreak();
        String response = new ChatMessageBuilder().append(ChatColorType.NORMAL).append("Duel Arena wins: ").append(ChatColorType.HIGHLIGHT).append(String.format("%,d", wins)).append(ChatColorType.NORMAL).append("   losses: ").append(ChatColorType.HIGHLIGHT).append(String.format("%,d", losses)).append(ChatColorType.NORMAL).append("   streak: ").append(ChatColorType.HIGHLIGHT).append(String.format("%,d", winningStreak != 0 ? winningStreak : -losingStreak)).build();
        log.debug("Setting response {}", (Object)response);
        MessageNode messageNode = chatMessage.getMessageNode();
        messageNode.setRuneLiteFormatMessage(response);
        this.client.refreshChat();
    }

    private void questPointsLookup(ChatMessage chatMessage, String message) {
        int qp;
        if (!this.config.qp()) {
            return;
        }
        ChatMessageType type = chatMessage.getType();
        String player = type.equals((Object)ChatMessageType.PRIVATECHATOUT) ? this.client.getLocalPlayer().getName() : Text.sanitize(chatMessage.getName());
        try {
            qp = this.chatClient.getQp(player);
        }
        catch (IOException ex) {
            log.debug("unable to lookup quest points", (Throwable)ex);
            return;
        }
        String response = new ChatMessageBuilder().append(ChatColorType.NORMAL).append("Quest points: ").append(ChatColorType.HIGHLIGHT).append(Integer.toString(qp)).build();
        log.debug("Setting response {}", (Object)response);
        MessageNode messageNode = chatMessage.getMessageNode();
        messageNode.setRuneLiteFormatMessage(response);
        this.client.refreshChat();
    }

    private boolean questPointsSubmit(ChatInput chatInput, String value) {
        int qp = this.client.getVarpValue(101);
        String playerName = this.client.getLocalPlayer().getName();
        this.executor.execute(() -> {
            try {
                this.chatClient.submitQp(playerName, qp);
            }
            catch (Exception ex) {
                log.warn("unable to submit quest points", (Throwable)ex);
            }
            finally {
                chatInput.resume();
            }
        });
        return true;
    }

    private void personalBestLookup(ChatMessage chatMessage, String message) {
        double pb;
        if (!this.config.pb()) {
            return;
        }
        if (message.length() <= PB_COMMAND.length()) {
            return;
        }
        ChatMessageType type = chatMessage.getType();
        String search = message.substring(PB_COMMAND.length() + 1);
        String player = type.equals((Object)ChatMessageType.PRIVATECHATOUT) ? this.client.getLocalPlayer().getName() : Text.sanitize(chatMessage.getName());
        search = ChatCommandsPlugin.longBossName(search);
        try {
            pb = this.chatClient.getPb(player, search);
        }
        catch (IOException ex) {
            log.debug("unable to lookup personal best", (Throwable)ex);
            return;
        }
        String response = new ChatMessageBuilder().append(ChatColorType.HIGHLIGHT).append(search).append(ChatColorType.NORMAL).append(" personal best: ").append(ChatColorType.HIGHLIGHT).append(ChatCommandsPlugin.secondsToTimeString(pb)).build();
        log.debug("Setting response {}", (Object)response);
        MessageNode messageNode = chatMessage.getMessageNode();
        messageNode.setRuneLiteFormatMessage(response);
        this.client.refreshChat();
    }

    private boolean personalBestSubmit(ChatInput chatInput, String value) {
        int idx = value.indexOf(32);
        String boss = ChatCommandsPlugin.longBossName(value.substring(idx + 1));
        double pb = this.getPb(boss);
        if (pb <= 0.0) {
            return false;
        }
        String playerName = this.client.getLocalPlayer().getName();
        this.executor.execute(() -> {
            try {
                this.chatClient.submitPb(playerName, boss, pb);
            }
            catch (Exception ex) {
                log.warn("unable to submit personal best", (Throwable)ex);
            }
            finally {
                chatInput.resume();
            }
        });
        return true;
    }

    private void gambleCountLookup(ChatMessage chatMessage, String message) {
        int gc;
        if (!this.config.gc()) {
            return;
        }
        ChatMessageType type = chatMessage.getType();
        String player = type == ChatMessageType.PRIVATECHATOUT ? this.client.getLocalPlayer().getName() : Text.sanitize(chatMessage.getName());
        try {
            gc = this.chatClient.getGc(player);
        }
        catch (IOException ex) {
            log.debug("unable to lookup gamble count", (Throwable)ex);
            return;
        }
        String response = new ChatMessageBuilder().append(ChatColorType.NORMAL).append("Barbarian Assault High-level gambles: ").append(ChatColorType.HIGHLIGHT).append(String.format("%,d", gc)).build();
        log.debug("Setting response {}", (Object)response);
        MessageNode messageNode = chatMessage.getMessageNode();
        messageNode.setRuneLiteFormatMessage(response);
        this.client.refreshChat();
    }

    private boolean gambleCountSubmit(ChatInput chatInput, String value) {
        int gc = this.client.getVarbitValue(4768);
        String playerName = this.client.getLocalPlayer().getName();
        this.executor.execute(() -> {
            try {
                this.chatClient.submitGc(playerName, gc);
            }
            catch (Exception ex) {
                log.warn("unable to submit gamble count", (Throwable)ex);
            }
            finally {
                chatInput.resume();
            }
        });
        return true;
    }

    private void petListLookup(ChatMessage chatMessage, String message) {
        Set<Integer> playerPetList;
        if (!this.config.pets()) {
            return;
        }
        ChatMessageType type = chatMessage.getType();
        String player = type.equals((Object)ChatMessageType.PRIVATECHATOUT) ? this.client.getLocalPlayer().getName() : Text.sanitize(chatMessage.getName());
        try {
            playerPetList = this.chatClient.getPetList(player);
        }
        catch (IOException ex) {
            log.debug("unable to lookup pet list", (Throwable)ex);
            if (player.equals(this.client.getLocalPlayer().getName())) {
                String response = "Open the 'All Pets' tab in the Collection Log to update your pet list";
                log.debug("Setting response {}", (Object)response);
                MessageNode messageNode = chatMessage.getMessageNode();
                messageNode.setValue(response);
                this.client.refreshChat();
            }
            return;
        }
        ChatMessageBuilder responseBuilder = new ChatMessageBuilder().append(ChatColorType.NORMAL).append("Pets: ").append("(" + playerPetList.size() + ")");
        for (int petIdx = 0; petIdx < this.pets.length; ++petIdx) {
            int petId = this.pets[petIdx];
            if (!playerPetList.contains(petId)) continue;
            responseBuilder.append(" ").img(this.petsIconIdx + petIdx);
        }
        String response = responseBuilder.build();
        log.debug("Setting response {}", (Object)response);
        MessageNode messageNode = chatMessage.getMessageNode();
        messageNode.setRuneLiteFormatMessage(response);
        this.client.refreshChat();
    }

    private boolean petListSubmit(ChatInput chatInput, String value) {
        String playerName = this.client.getLocalPlayer().getName();
        this.executor.execute(() -> {
            try {
                List<Integer> petList = this.getPetList();
                if (!petList.isEmpty()) {
                    this.chatClient.submitPetList(playerName, petList);
                }
            }
            catch (Exception ex) {
                log.warn("unable to submit pet list", (Throwable)ex);
            }
            finally {
                chatInput.resume();
            }
        });
        return true;
    }

    private void caLookup(ChatMessage chatMessage, String message) {
        int num;
        if (!this.config.ca()) {
            return;
        }
        ChatMessageType type = chatMessage.getType();
        String player = type.equals((Object)ChatMessageType.PRIVATECHATOUT) ? this.client.getLocalPlayer().getName() : Text.sanitize(chatMessage.getName());
        try {
            num = this.chatClient.getKc(player, "Combat Achievements");
        }
        catch (IOException ex) {
            log.debug("unable to lookup combat achievements");
            return;
        }
        String response = new ChatMessageBuilder().append(ChatColorType.NORMAL).append("Combat Achievements: ").append(ChatColorType.HIGHLIGHT).append(Integer.toString(num)).build();
        log.debug("Setting response {}", (Object)response);
        MessageNode messageNode = chatMessage.getMessageNode();
        messageNode.setRuneLiteFormatMessage(response);
        this.client.refreshChat();
    }

    private boolean caSubmit(ChatInput chatInput, String value) {
        int tasks = this.client.getVarbitValue(12885) + this.client.getVarbitValue(12886) + this.client.getVarbitValue(12887) + this.client.getVarbitValue(12888) + this.client.getVarbitValue(12889) + this.client.getVarbitValue(12890);
        String playerName = this.client.getLocalPlayer().getName();
        this.executor.execute(() -> {
            try {
                this.chatClient.submitKc(playerName, "Combat Achievements", tasks);
            }
            catch (Exception ex) {
                log.warn("unable to submit combat achievements", (Throwable)ex);
            }
            finally {
                chatInput.resume();
            }
        });
        return true;
    }

    private void clogLookup(ChatMessage chatMessage, String message) {
        int num;
        if (!this.config.clog()) {
            return;
        }
        ChatMessageType type = chatMessage.getType();
        String player = type.equals((Object)ChatMessageType.PRIVATECHATOUT) ? this.client.getLocalPlayer().getName() : Text.sanitize(chatMessage.getName());
        try {
            num = this.chatClient.getKc(player, "Collections Logged");
        }
        catch (IOException ex) {
            log.debug("unable to lookup clog");
            return;
        }
        String response = new ChatMessageBuilder().append(ChatColorType.NORMAL).append("Collections Logged: ").append(ChatColorType.HIGHLIGHT).append(Integer.toString(num)).build();
        log.debug("Setting response {}", (Object)response);
        MessageNode messageNode = chatMessage.getMessageNode();
        messageNode.setRuneLiteFormatMessage(response);
        this.client.refreshChat();
    }

    private boolean clogSubmit(ChatInput chatInput, String value) {
        int clog = this.client.getVarpValue(2943);
        String playerName = this.client.getLocalPlayer().getName();
        this.executor.execute(() -> {
            try {
                this.chatClient.submitKc(playerName, "Collections Logged", clog);
            }
            catch (Exception ex) {
                log.warn("unable to submit clog", (Throwable)ex);
            }
            finally {
                chatInput.resume();
            }
        });
        return true;
    }

    private void itemPriceLookup(ChatMessage chatMessage, String message) {
        if (!this.config.price()) {
            return;
        }
        if (message.length() <= PRICE_COMMAND_STRING.length()) {
            return;
        }
        MessageNode messageNode = chatMessage.getMessageNode();
        String search = message.substring(PRICE_COMMAND_STRING.length() + 1);
        List<ItemPrice> results = this.itemManager.search(search);
        if (!results.isEmpty()) {
            ItemPrice item = this.retrieveFromList(results, search);
            int itemId = item.getId();
            int itemPrice = this.runeLiteConfig.useWikiItemPrices() ? this.itemManager.getWikiPrice(item) : item.getPrice();
            ChatMessageBuilder builder = new ChatMessageBuilder().append(ChatColorType.NORMAL).append("Price of ").append(ChatColorType.HIGHLIGHT).append(item.getName()).append(ChatColorType.NORMAL).append(": GE average ").append(ChatColorType.HIGHLIGHT).append(QuantityFormatter.formatNumber(itemPrice));
            ItemComposition itemComposition = this.itemManager.getItemComposition(itemId);
            int alchPrice = itemComposition.getHaPrice();
            builder.append(ChatColorType.NORMAL).append(" HA value ").append(ChatColorType.HIGHLIGHT).append(QuantityFormatter.formatNumber(alchPrice));
            String response = builder.build();
            log.debug("Setting response {}", (Object)response);
            messageNode.setRuneLiteFormatMessage(response);
            this.client.refreshChat();
        }
    }

    @VisibleForTesting
    void playerSkillLookup(ChatMessage chatMessage, String message) {
        String search;
        if (!this.config.lvl()) {
            return;
        }
        if (message.equalsIgnoreCase(TOTAL_LEVEL_COMMAND_STRING)) {
            search = "total";
        } else {
            if (message.length() <= LEVEL_COMMAND_STRING.length()) {
                return;
            }
            search = message.substring(LEVEL_COMMAND_STRING.length() + 1);
        }
        HiscoreSkill skill = ChatCommandsPlugin.findHiscoreSkill(search);
        if (skill == null) {
            return;
        }
        HiscoreLookup lookup = this.getCorrectLookupFor(chatMessage);
        try {
            HiscoreResult result = this.hiscoreClient.lookup(lookup.getName(), lookup.getEndpoint());
            if (result == null) {
                log.warn("unable to look up skill {} for {}: not found", (Object)skill, (Object)search);
                return;
            }
            net.runelite.client.hiscore.Skill hiscoreSkill = result.getSkill(skill);
            ChatMessageBuilder chatMessageBuilder = new ChatMessageBuilder().append(ChatColorType.NORMAL).append("Level ").append(ChatColorType.HIGHLIGHT).append(skill.getName()).append(": ").append(hiscoreSkill.getLevel() > -1 ? String.valueOf(hiscoreSkill.getLevel()) : "unranked").append(ChatColorType.NORMAL);
            if (hiscoreSkill.getExperience() != -1L) {
                chatMessageBuilder.append(" Experience: ").append(ChatColorType.HIGHLIGHT).append(String.format("%,d", hiscoreSkill.getExperience())).append(ChatColorType.NORMAL);
            }
            if (hiscoreSkill.getRank() != -1) {
                chatMessageBuilder.append(" Rank: ").append(ChatColorType.HIGHLIGHT).append(String.format("%,d", hiscoreSkill.getRank()));
            }
            String response = chatMessageBuilder.build();
            log.debug("Setting response {}", (Object)response);
            MessageNode messageNode = chatMessage.getMessageNode();
            messageNode.setRuneLiteFormatMessage(response);
            this.client.refreshChat();
        }
        catch (IOException ex) {
            log.warn("unable to look up skill {} for {}", new Object[]{skill, search, ex});
        }
    }

    private void combatLevelLookup(ChatMessage chatMessage, String message) {
        if (!this.config.lvl()) {
            return;
        }
        HiscoreLookup lookup = this.getCorrectLookupFor(chatMessage);
        try {
            HiscoreResult playerStats = this.hiscoreClient.lookup(lookup.getName(), lookup.getEndpoint());
            if (playerStats == null) {
                log.warn("Error fetching hiscore data: not found");
                return;
            }
            int attack = playerStats.getSkill(HiscoreSkill.ATTACK).getLevel();
            int strength = playerStats.getSkill(HiscoreSkill.STRENGTH).getLevel();
            int defence = playerStats.getSkill(HiscoreSkill.DEFENCE).getLevel();
            int hitpoints = playerStats.getSkill(HiscoreSkill.HITPOINTS).getLevel();
            int ranged = playerStats.getSkill(HiscoreSkill.RANGED).getLevel();
            int prayer = playerStats.getSkill(HiscoreSkill.PRAYER).getLevel();
            int magic = playerStats.getSkill(HiscoreSkill.MAGIC).getLevel();
            int combatLevel = Experience.getCombatLevel((int)attack, (int)strength, (int)defence, (int)hitpoints, (int)magic, (int)ranged, (int)prayer);
            String response = new ChatMessageBuilder().append(ChatColorType.NORMAL).append("Combat Level: ").append(ChatColorType.HIGHLIGHT).append(String.valueOf(combatLevel)).append(ChatColorType.NORMAL).append(" A: ").append(ChatColorType.HIGHLIGHT).append(String.valueOf(attack)).append(ChatColorType.NORMAL).append(" S: ").append(ChatColorType.HIGHLIGHT).append(String.valueOf(strength)).append(ChatColorType.NORMAL).append(" D: ").append(ChatColorType.HIGHLIGHT).append(String.valueOf(defence)).append(ChatColorType.NORMAL).append(" H: ").append(ChatColorType.HIGHLIGHT).append(String.valueOf(hitpoints)).append(ChatColorType.NORMAL).append(" R: ").append(ChatColorType.HIGHLIGHT).append(String.valueOf(ranged)).append(ChatColorType.NORMAL).append(" P: ").append(ChatColorType.HIGHLIGHT).append(String.valueOf(prayer)).append(ChatColorType.NORMAL).append(" M: ").append(ChatColorType.HIGHLIGHT).append(String.valueOf(magic)).build();
            log.debug("Setting response {}", (Object)response);
            MessageNode messageNode = chatMessage.getMessageNode();
            messageNode.setRuneLiteFormatMessage(response);
            this.client.refreshChat();
        }
        catch (IOException ex) {
            log.warn("Error fetching hiscore data", (Throwable)ex);
        }
    }

    private void leaguePointsLookup(ChatMessage chatMessage, String message) {
        if (!this.config.lp()) {
            return;
        }
        this.minigameLookup(chatMessage, HiscoreSkill.LEAGUE_POINTS);
    }

    private void bountyHunterHunterLookup(ChatMessage chatMessage, String message) {
        if (!this.config.bh()) {
            return;
        }
        this.minigameLookup(chatMessage, HiscoreSkill.BOUNTY_HUNTER_HUNTER);
    }

    private void bountyHunterRogueLookup(ChatMessage chatMessage, String message) {
        if (!this.config.bhRogue()) {
            return;
        }
        this.minigameLookup(chatMessage, HiscoreSkill.BOUNTY_HUNTER_ROGUE);
    }

    private void lastManStandingLookup(ChatMessage chatMessage, String message) {
        if (!this.config.lms()) {
            return;
        }
        this.minigameLookup(chatMessage, HiscoreSkill.LAST_MAN_STANDING);
    }

    private void soulWarsZealLookup(ChatMessage chatMessage, String message) {
        if (!this.config.sw()) {
            return;
        }
        this.minigameLookup(chatMessage, HiscoreSkill.SOUL_WARS_ZEAL);
    }

    private void minigameLookup(ChatMessage chatMessage, HiscoreSkill minigame) {
        try {
            net.runelite.client.hiscore.Skill hiscoreSkill;
            HiscoreLookup lookup = this.getCorrectLookupFor(chatMessage);
            HiscoreEndpoint endPoint = minigame == HiscoreSkill.LEAGUE_POINTS ? HiscoreEndpoint.LEAGUE : lookup.getEndpoint();
            HiscoreResult result = this.hiscoreClient.lookup(lookup.getName(), endPoint);
            if (result == null) {
                log.warn("error looking up {} score: not found", (Object)minigame.getName().toLowerCase());
                return;
            }
            switch (minigame) {
                case BOUNTY_HUNTER_HUNTER: 
                case BOUNTY_HUNTER_ROGUE: 
                case LAST_MAN_STANDING: 
                case LEAGUE_POINTS: 
                case SOUL_WARS_ZEAL: {
                    hiscoreSkill = result.getSkill(minigame);
                    break;
                }
                default: {
                    log.warn("error looking up {} score: not implemented", (Object)minigame.getName().toLowerCase());
                    return;
                }
            }
            int score = hiscoreSkill.getLevel();
            if (score == -1) {
                return;
            }
            ChatMessageBuilder chatMessageBuilder = new ChatMessageBuilder().append(ChatColorType.NORMAL).append(minigame.getName()).append(" Score: ").append(ChatColorType.HIGHLIGHT).append(String.format("%,d", score));
            int rank = hiscoreSkill.getRank();
            if (rank != -1) {
                chatMessageBuilder.append(ChatColorType.NORMAL).append(" Rank: ").append(ChatColorType.HIGHLIGHT).append(String.format("%,d", rank));
            }
            String response = chatMessageBuilder.build();
            log.debug("Setting response {}", (Object)response);
            MessageNode messageNode = chatMessage.getMessageNode();
            messageNode.setRuneLiteFormatMessage(response);
            this.client.refreshChat();
        }
        catch (IOException ex) {
            log.warn("error looking up {}", (Object)minigame.getName().toLowerCase(), (Object)ex);
        }
    }

    private void clueLookup(ChatMessage chatMessage, String message) {
        if (!this.config.clue()) {
            return;
        }
        String search = message.equalsIgnoreCase(CLUES_COMMAND_STRING) ? "total" : message.substring(CLUES_COMMAND_STRING.length() + 1);
        try {
            net.runelite.client.hiscore.Skill hiscoreSkill;
            String level;
            HiscoreLookup lookup = this.getCorrectLookupFor(chatMessage);
            HiscoreResult result = this.hiscoreClient.lookup(lookup.getName(), lookup.getEndpoint());
            if (result == null) {
                log.warn("error looking up clues: not found");
                return;
            }
            switch (level = search.toLowerCase()) {
                case "beginner": {
                    hiscoreSkill = result.getSkill(HiscoreSkill.CLUE_SCROLL_BEGINNER);
                    break;
                }
                case "easy": {
                    hiscoreSkill = result.getSkill(HiscoreSkill.CLUE_SCROLL_EASY);
                    break;
                }
                case "medium": {
                    hiscoreSkill = result.getSkill(HiscoreSkill.CLUE_SCROLL_MEDIUM);
                    break;
                }
                case "hard": {
                    hiscoreSkill = result.getSkill(HiscoreSkill.CLUE_SCROLL_HARD);
                    break;
                }
                case "elite": {
                    hiscoreSkill = result.getSkill(HiscoreSkill.CLUE_SCROLL_ELITE);
                    break;
                }
                case "master": {
                    hiscoreSkill = result.getSkill(HiscoreSkill.CLUE_SCROLL_MASTER);
                    break;
                }
                case "total": {
                    hiscoreSkill = result.getSkill(HiscoreSkill.CLUE_SCROLL_ALL);
                    break;
                }
                default: {
                    return;
                }
            }
            int quantity = hiscoreSkill.getLevel();
            int rank = hiscoreSkill.getRank();
            if (quantity == -1) {
                return;
            }
            ChatMessageBuilder chatMessageBuilder = new ChatMessageBuilder().append(ChatColorType.NORMAL).append("Clue scroll (" + level + ")").append(": ").append(ChatColorType.HIGHLIGHT).append(String.format("%,d", quantity));
            if (rank != -1) {
                chatMessageBuilder.append(ChatColorType.NORMAL).append(" Rank: ").append(ChatColorType.HIGHLIGHT).append(String.format("%,d", rank));
            }
            String response = chatMessageBuilder.build();
            log.debug("Setting response {}", (Object)response);
            MessageNode messageNode = chatMessage.getMessageNode();
            messageNode.setRuneLiteFormatMessage(response);
            this.client.refreshChat();
        }
        catch (IOException ex) {
            log.warn("error looking up clues", (Throwable)ex);
        }
    }

    private HiscoreLookup getCorrectLookupFor(ChatMessage chatMessage) {
        HiscoreEndpoint endpoint;
        Player localPlayer = this.client.getLocalPlayer();
        String player = Text.sanitize(chatMessage.getName());
        if (chatMessage.getType().equals((Object)ChatMessageType.PRIVATECHATOUT) || player.equals(localPlayer.getName())) {
            return new HiscoreLookup(localPlayer.getName(), this.hiscoreEndpoint);
        }
        if ((chatMessage.getType() == ChatMessageType.PUBLICCHAT || chatMessage.getType() == ChatMessageType.MODCHAT) && (endpoint = HiscoreEndpoint.fromWorldTypes(this.client.getWorldType())) != HiscoreEndpoint.NORMAL) {
            return new HiscoreLookup(player, endpoint);
        }
        endpoint = ChatCommandsPlugin.getHiscoreEndpointByName(chatMessage.getName());
        return new HiscoreLookup(player, endpoint);
    }

    private ItemPrice retrieveFromList(List<ItemPrice> items, String originalInput) {
        ItemPrice shortest = null;
        for (ItemPrice item : items) {
            if (item.getName().toLowerCase().equals(originalInput.toLowerCase())) {
                return item;
            }
            if (shortest != null && item.getName().length() >= shortest.getName().length()) continue;
            shortest = item;
        }
        return shortest;
    }

    private HiscoreEndpoint getLocalHiscoreEndpointType() {
        EnumSet worldType = this.client.getWorldType();
        HiscoreEndpoint endpoint = HiscoreEndpoint.fromWorldTypes(worldType);
        if (endpoint != HiscoreEndpoint.NORMAL) {
            return endpoint;
        }
        return ChatCommandsPlugin.toEndPoint(this.client.getVarbitValue(1777));
    }

    private static HiscoreEndpoint getHiscoreEndpointByName(String name) {
        if (name.contains(IconID.IRONMAN.toString())) {
            return HiscoreEndpoint.IRONMAN;
        }
        if (name.contains(IconID.ULTIMATE_IRONMAN.toString())) {
            return HiscoreEndpoint.ULTIMATE_IRONMAN;
        }
        if (name.contains(IconID.HARDCORE_IRONMAN.toString())) {
            return HiscoreEndpoint.HARDCORE_IRONMAN;
        }
        if (name.contains(IconID.LEAGUE.toString())) {
            return HiscoreEndpoint.LEAGUE;
        }
        return HiscoreEndpoint.NORMAL;
    }

    private static HiscoreEndpoint toEndPoint(int accountType) {
        switch (accountType) {
            case 1: {
                return HiscoreEndpoint.IRONMAN;
            }
            case 2: {
                return HiscoreEndpoint.ULTIMATE_IRONMAN;
            }
            case 3: {
                return HiscoreEndpoint.HARDCORE_IRONMAN;
            }
        }
        return HiscoreEndpoint.NORMAL;
    }

    private static String longBossName(String boss) {
        String lowerBoss = boss.toLowerCase();
        if (lowerBoss.endsWith(" (echo)")) {
            String actualBoss = lowerBoss.substring(0, lowerBoss.length() - " (echo)".length());
            return ChatCommandsPlugin.longBossName(actualBoss) + " (Echo)";
        }
        switch (lowerBoss) {
            case "corp": {
                return "Corporeal Beast";
            }
            case "jad": 
            case "tzhaar fight cave": {
                return "TzTok-Jad";
            }
            case "kq": {
                return "Kalphite Queen";
            }
            case "chaos ele": {
                return "Chaos Elemental";
            }
            case "dusk": 
            case "dawn": 
            case "gargs": 
            case "ggs": 
            case "gg": {
                return "Grotesque Guardians";
            }
            case "crazy arch": {
                return "Crazy Archaeologist";
            }
            case "deranged arch": {
                return "Deranged Archaeologist";
            }
            case "mole": {
                return "Giant Mole";
            }
            case "vetion": {
                return "Vet'ion";
            }
            case "calvarion": 
            case "calv": {
                return "Calvar'ion";
            }
            case "vene": {
                return "Venenatis";
            }
            case "kbd": {
                return "King Black Dragon";
            }
            case "vork": {
                return "Vorkath";
            }
            case "sire": {
                return "Abyssal Sire";
            }
            case "smoke devil": 
            case "thermy": {
                return "Thermonuclear Smoke Devil";
            }
            case "cerb": {
                return "Cerberus";
            }
            case "zuk": 
            case "inferno": {
                return "TzKal-Zuk";
            }
            case "hydra": {
                return "Alchemical Hydra";
            }
            case "sara": 
            case "saradomin": 
            case "zilyana": 
            case "zily": {
                return "Commander Zilyana";
            }
            case "zammy": 
            case "zamorak": 
            case "kril": 
            case "kril tsutsaroth": {
                return "K'ril Tsutsaroth";
            }
            case "arma": 
            case "kree": 
            case "kreearra": 
            case "armadyl": {
                return "Kree'arra";
            }
            case "bando": 
            case "bandos": 
            case "graardor": {
                return "General Graardor";
            }
            case "supreme": {
                return "Dagannoth Supreme";
            }
            case "rex": {
                return "Dagannoth Rex";
            }
            case "prime": {
                return "Dagannoth Prime";
            }
            case "wt": {
                return "Wintertodt";
            }
            case "barrows": {
                return "Barrows Chests";
            }
            case "herbi": {
                return "Herbiboar";
            }
            case "cox": 
            case "xeric": 
            case "chambers": 
            case "olm": 
            case "raids": {
                return "Chambers of Xeric";
            }
            case "cox 1": 
            case "cox solo": {
                return "Chambers of Xeric Solo";
            }
            case "cox 2": 
            case "cox duo": {
                return "Chambers of Xeric 2 players";
            }
            case "cox 3": {
                return "Chambers of Xeric 3 players";
            }
            case "cox 4": {
                return "Chambers of Xeric 4 players";
            }
            case "cox 5": {
                return "Chambers of Xeric 5 players";
            }
            case "cox 6": {
                return "Chambers of Xeric 6 players";
            }
            case "cox 7": {
                return "Chambers of Xeric 7 players";
            }
            case "cox 8": {
                return "Chambers of Xeric 8 players";
            }
            case "cox 9": {
                return "Chambers of Xeric 9 players";
            }
            case "cox 10": {
                return "Chambers of Xeric 10 players";
            }
            case "cox 11-15": 
            case "cox 11": 
            case "cox 12": 
            case "cox 13": 
            case "cox 14": 
            case "cox 15": {
                return "Chambers of Xeric 11-15 players";
            }
            case "cox 16-23": 
            case "cox 16": 
            case "cox 17": 
            case "cox 18": 
            case "cox 19": 
            case "cox 20": 
            case "cox 21": 
            case "cox 22": 
            case "cox 23": {
                return "Chambers of Xeric 16-23 players";
            }
            case "cox 24": 
            case "cox 24+": {
                return "Chambers of Xeric 24+ players";
            }
            case "chambers of xeric: challenge mode": 
            case "cox cm": 
            case "xeric cm": 
            case "chambers cm": 
            case "olm cm": 
            case "raids cm": 
            case "chambers of xeric - challenge mode": {
                return "Chambers of Xeric Challenge Mode";
            }
            case "cox cm 1": 
            case "cox cm solo": {
                return "Chambers of Xeric Challenge Mode Solo";
            }
            case "cox cm 2": 
            case "cox cm duo": {
                return "Chambers of Xeric Challenge Mode 2 players";
            }
            case "cox cm 3": {
                return "Chambers of Xeric Challenge Mode 3 players";
            }
            case "cox cm 4": {
                return "Chambers of Xeric Challenge Mode 4 players";
            }
            case "cox cm 5": {
                return "Chambers of Xeric Challenge Mode 5 players";
            }
            case "cox cm 6": {
                return "Chambers of Xeric Challenge Mode 6 players";
            }
            case "cox cm 7": {
                return "Chambers of Xeric Challenge Mode 7 players";
            }
            case "cox cm 8": {
                return "Chambers of Xeric Challenge Mode 8 players";
            }
            case "cox cm 9": {
                return "Chambers of Xeric Challenge Mode 9 players";
            }
            case "cox cm 10": {
                return "Chambers of Xeric Challenge Mode 10 players";
            }
            case "cox cm 11-15": 
            case "cox cm 11": 
            case "cox cm 12": 
            case "cox cm 13": 
            case "cox cm 14": 
            case "cox cm 15": {
                return "Chambers of Xeric Challenge Mode 11-15 players";
            }
            case "cox cm 16-23": 
            case "cox cm 16": 
            case "cox cm 17": 
            case "cox cm 18": 
            case "cox cm 19": 
            case "cox cm 20": 
            case "cox cm 21": 
            case "cox cm 22": 
            case "cox cm 23": {
                return "Chambers of Xeric Challenge Mode 16-23 players";
            }
            case "cox cm 24": 
            case "cox cm 24+": {
                return "Chambers of Xeric Challenge Mode 24+ players";
            }
            case "tob": 
            case "theatre": 
            case "verzik": 
            case "verzik vitur": 
            case "raids 2": {
                return "Theatre of Blood";
            }
            case "tob 1": 
            case "tob solo": {
                return "Theatre of Blood Solo";
            }
            case "tob 2": 
            case "tob duo": {
                return "Theatre of Blood 2 players";
            }
            case "tob 3": {
                return "Theatre of Blood 3 players";
            }
            case "tob 4": {
                return "Theatre of Blood 4 players";
            }
            case "tob 5": {
                return "Theatre of Blood 5 players";
            }
            case "theatre of blood: story mode": 
            case "tob sm": 
            case "tob story mode": 
            case "tob story": 
            case "theatre of blood: entry mode": 
            case "tob em": 
            case "tob entry mode": 
            case "tob entry": {
                return "Theatre of Blood Entry Mode";
            }
            case "theatre of blood: hard mode": 
            case "tob cm": 
            case "tob hm": 
            case "tob hard mode": 
            case "tob hard": 
            case "hmt": {
                return "Theatre of Blood Hard Mode";
            }
            case "hmt 1": 
            case "hmt solo": {
                return "Theatre of Blood Hard Mode Solo";
            }
            case "hmt 2": 
            case "hmt duo": {
                return "Theatre of Blood Hard Mode 2 players";
            }
            case "hmt 3": {
                return "Theatre of Blood Hard Mode 3 players";
            }
            case "hmt 4": {
                return "Theatre of Blood Hard Mode 4 players";
            }
            case "hmt 5": {
                return "Theatre of Blood Hard Mode 5 players";
            }
            case "toa": 
            case "tombs": 
            case "amascut": 
            case "warden": 
            case "wardens": 
            case "raids 3": {
                return "Tombs of Amascut";
            }
            case "toa 1": 
            case "toa solo": {
                return "Tombs of Amascut Solo";
            }
            case "toa 2": 
            case "toa duo": {
                return "Tombs of Amascut 2 players";
            }
            case "toa 3": {
                return "Tombs of Amascut 3 players";
            }
            case "toa 4": {
                return "Tombs of Amascut 4 players";
            }
            case "toa 5": {
                return "Tombs of Amascut 5 players";
            }
            case "toa 6": {
                return "Tombs of Amascut 6 players";
            }
            case "toa 7": {
                return "Tombs of Amascut 7 players";
            }
            case "toa 8": {
                return "Tombs of Amascut 8 players";
            }
            case "toa entry": 
            case "tombs of amascut - entry": 
            case "toa entry mode": {
                return "Tombs of Amascut Entry Mode";
            }
            case "toa entry 1": 
            case "toa entry solo": {
                return "Tombs of Amascut Entry Mode Solo";
            }
            case "toa entry 2": 
            case "toa entry duo": {
                return "Tombs of Amascut Entry Mode 2 players";
            }
            case "toa entry 3": {
                return "Tombs of Amascut Entry Mode 3 players";
            }
            case "toa entry 4": {
                return "Tombs of Amascut Entry Mode 4 players";
            }
            case "toa entry 5": {
                return "Tombs of Amascut Entry Mode 5 players";
            }
            case "toa entry 6": {
                return "Tombs of Amascut Entry Mode 6 players";
            }
            case "toa entry 7": {
                return "Tombs of Amascut Entry Mode 7 players";
            }
            case "toa entry 8": {
                return "Tombs of Amascut Entry Mode 8 players";
            }
            case "tombs of amascut: expert mode": 
            case "toa expert": 
            case "tombs of amascut - expert": 
            case "toa expert mode": {
                return "Tombs of Amascut Expert Mode";
            }
            case "toa expert 1": 
            case "toa expert solo": {
                return "Tombs of Amascut Expert Mode Solo";
            }
            case "toa expert 2": 
            case "toa expert duo": {
                return "Tombs of Amascut Expert Mode 2 players";
            }
            case "toa expert 3": {
                return "Tombs of Amascut Expert Mode 3 players";
            }
            case "toa expert 4": {
                return "Tombs of Amascut Expert Mode 4 players";
            }
            case "toa expert 5": {
                return "Tombs of Amascut Expert Mode 5 players";
            }
            case "toa expert 6": {
                return "Tombs of Amascut Expert Mode 6 players";
            }
            case "toa expert 7": {
                return "Tombs of Amascut Expert Mode 7 players";
            }
            case "toa expert 8": {
                return "Tombs of Amascut Expert Mode 8 players";
            }
            case "gaunt": 
            case "gauntlet": 
            case "the gauntlet": {
                return "Gauntlet";
            }
            case "cgaunt": 
            case "cgauntlet": 
            case "the corrupted gauntlet": 
            case "cg": {
                return "Corrupted Gauntlet";
            }
            case "nm": 
            case "tnm": 
            case "nmare": 
            case "the nightmare": {
                return "Nightmare";
            }
            case "pnm": 
            case "phosani": 
            case "phosanis": 
            case "phosani nm": 
            case "phosani nightmare": 
            case "phosanis nightmare": {
                return "Phosani's Nightmare";
            }
            case "hs": 
            case "sepulchre": 
            case "ghc": {
                return "Hallowed Sepulchre";
            }
            case "hs1": 
            case "hs 1": {
                return "Hallowed Sepulchre Floor 1";
            }
            case "hs2": 
            case "hs 2": {
                return "Hallowed Sepulchre Floor 2";
            }
            case "hs3": 
            case "hs 3": {
                return "Hallowed Sepulchre Floor 3";
            }
            case "hs4": 
            case "hs 4": {
                return "Hallowed Sepulchre Floor 4";
            }
            case "hs5": 
            case "hs 5": {
                return "Hallowed Sepulchre Floor 5";
            }
            case "wbac": 
            case "cwbac": 
            case "wyrmb": 
            case "wyrmbasic": 
            case "wyrm basic": 
            case "colossal basic": 
            case "colossal wyrm basic": {
                return "Colossal Wyrm Agility Course (Basic)";
            }
            case "waac": 
            case "cwaac": 
            case "wyrma": 
            case "wyrmadvanced": 
            case "wyrm advanced": 
            case "colossal advanced": 
            case "colossal wyrm advanced": {
                return "Colossal Wyrm Agility Course (Advanced)";
            }
            case "prif": 
            case "prifddinas": {
                return "Prifddinas Agility Course";
            }
            case "shayb": 
            case "sbac": 
            case "shayzienbasic": 
            case "shayzien basic": {
                return "Shayzien Basic Agility Course";
            }
            case "shaya": 
            case "saac": 
            case "shayadv": 
            case "shayadvanced": 
            case "shayzien advanced": {
                return "Shayzien Advanced Agility Course";
            }
            case "aa": 
            case "ape atoll": {
                return "Ape Atoll Agility";
            }
            case "draynor": 
            case "draynor agility": {
                return "Draynor Village Rooftop";
            }
            case "al kharid": 
            case "al kharid agility": 
            case "al-kharid": 
            case "al-kharid agility": 
            case "alkharid": 
            case "alkharid agility": {
                return "Al Kharid Rooftop";
            }
            case "varrock": 
            case "varrock agility": {
                return "Varrock Rooftop";
            }
            case "canifis": 
            case "canifis agility": {
                return "Canifis Rooftop";
            }
            case "fally": 
            case "fally agility": 
            case "falador": 
            case "falador agility": {
                return "Falador Rooftop";
            }
            case "seers": 
            case "seers agility": 
            case "seers village": 
            case "seers village agility": 
            case "seers'": 
            case "seers' agility": 
            case "seers' village": 
            case "seers' village agility": 
            case "seer's": 
            case "seer's agility": 
            case "seer's village": 
            case "seer's village agility": {
                return "Seers' Village Rooftop";
            }
            case "pollnivneach": 
            case "pollnivneach agility": {
                return "Pollnivneach Rooftop";
            }
            case "rellekka": 
            case "rellekka agility": {
                return "Rellekka Rooftop";
            }
            case "ardy": 
            case "ardy agility": 
            case "ardy rooftop": 
            case "ardougne": 
            case "ardougne agility": {
                return "Ardougne Rooftop";
            }
            case "ap": 
            case "pyramid": {
                return "Agility Pyramid";
            }
            case "barb": 
            case "barb outpost": {
                return "Barbarian Outpost";
            }
            case "brimhaven": 
            case "brimhaven agility": {
                return "Agility Arena";
            }
            case "dorg": 
            case "dorgesh kaan": 
            case "dorgesh-kaan": {
                return "Dorgesh-Kaan Agility";
            }
            case "gnome stronghold": {
                return "Gnome Stronghold Agility";
            }
            case "penguin": {
                return "Penguin Agility";
            }
            case "werewolf": {
                return "Werewolf Agility";
            }
            case "skullball": {
                return "Werewolf Skullball";
            }
            case "wildy": 
            case "wildy agility": {
                return "Wilderness Agility";
            }
            case "jad 1": {
                return "TzHaar-Ket-Rak's First Challenge";
            }
            case "jad 2": {
                return "TzHaar-Ket-Rak's Second Challenge";
            }
            case "jad 3": {
                return "TzHaar-Ket-Rak's Third Challenge";
            }
            case "jad 4": {
                return "TzHaar-Ket-Rak's Fourth Challenge";
            }
            case "jad 5": {
                return "TzHaar-Ket-Rak's Fifth Challenge";
            }
            case "jad 6": {
                return "TzHaar-Ket-Rak's Sixth Challenge";
            }
            case "gotr": 
            case "runetodt": 
            case "rifts closed": {
                return "Guardians of the Rift";
            }
            case "fishingtodt": 
            case "fishtodt": {
                return "Tempoross";
            }
            case "phantom": 
            case "muspah": 
            case "pm": {
                return "Phantom Muspah";
            }
            case "the leviathan": 
            case "levi": {
                return "Leviathan";
            }
            case "duke": {
                return "Duke Sucellus";
            }
            case "the whisperer": 
            case "whisp": 
            case "wisp": {
                return "Whisperer";
            }
            case "vard": {
                return "Vardorvis";
            }
            case "leviathan awakened": 
            case "the leviathan awakened": 
            case "levi awakened": {
                return "Leviathan (awakened)";
            }
            case "duke sucellus awakened": 
            case "duke awakened": {
                return "Duke Sucellus (awakened)";
            }
            case "whisperer awakened": 
            case "the whisperer awakened": 
            case "whisp awakened": 
            case "wisp awakened": {
                return "Whisperer (awakened)";
            }
            case "vardorvis awakened": 
            case "vard awakened": {
                return "Vardorvis (awakened)";
            }
            case "lunar chests": 
            case "perilous moons": 
            case "perilous moon": 
            case "moons of peril": {
                return "Lunar Chest";
            }
            case "hunterrumour": 
            case "hunter contract": 
            case "hunter contracts": 
            case "hunter tasks": 
            case "hunter task": 
            case "rumours": 
            case "rumour": {
                return "Hunter Rumours";
            }
            case "sol": 
            case "colo": 
            case "colosseum": 
            case "fortis colosseum": {
                return "Sol Heredit";
            }
            case "bird egg": 
            case "bird eggs": 
            case "bird's egg": 
            case "bird's eggs": {
                return "Bird's egg offerings";
            }
            case "amox": {
                return "Amoxliatl";
            }
            case "the hueycoatl": 
            case "huey": {
                return "Hueycoatl";
            }
            case "crystal chest": {
                return "crystal chest";
            }
            case "larran small chest": 
            case "larran's small chest": {
                return "Larran's small chest";
            }
            case "larran chest": 
            case "larran's chest": 
            case "larran big chest": 
            case "larran's big chest": {
                return "Larran's big chest";
            }
            case "brimstone chest": {
                return "Brimstone chest";
            }
            case "dom": 
            case "doom": {
                return "Doom of Mokhaiotl";
            }
        }
        return WordUtils.capitalize((String)boss);
    }

    private static String longSkillName(String skill) {
        switch (skill.toUpperCase()) {
            case "ATK": 
            case "ATT": {
                return Skill.ATTACK.getName();
            }
            case "DEF": {
                return Skill.DEFENCE.getName();
            }
            case "STR": {
                return Skill.STRENGTH.getName();
            }
            case "HEALTH": 
            case "HIT": 
            case "HITPOINT": 
            case "HP": {
                return Skill.HITPOINTS.getName();
            }
            case "RANGE": 
            case "RANGING": 
            case "RNG": {
                return Skill.RANGED.getName();
            }
            case "PRAY": {
                return Skill.PRAYER.getName();
            }
            case "MAG": 
            case "MAGE": {
                return Skill.MAGIC.getName();
            }
            case "COOK": {
                return Skill.COOKING.getName();
            }
            case "WC": 
            case "WOOD": 
            case "WOODCUT": {
                return Skill.WOODCUTTING.getName();
            }
            case "FLETCH": {
                return Skill.FLETCHING.getName();
            }
            case "FISH": {
                return Skill.FISHING.getName();
            }
            case "FM": 
            case "FIRE": {
                return Skill.FIREMAKING.getName();
            }
            case "CRAFT": {
                return Skill.CRAFTING.getName();
            }
            case "SMITH": {
                return Skill.SMITHING.getName();
            }
            case "MINE": {
                return Skill.MINING.getName();
            }
            case "HL": 
            case "HERB": {
                return Skill.HERBLORE.getName();
            }
            case "AGI": 
            case "AGIL": {
                return Skill.AGILITY.getName();
            }
            case "THIEF": {
                return Skill.THIEVING.getName();
            }
            case "SLAY": {
                return Skill.SLAYER.getName();
            }
            case "FARM": {
                return Skill.FARMING.getName();
            }
            case "RC": 
            case "RUNE": 
            case "RUNECRAFTING": {
                return Skill.RUNECRAFT.getName();
            }
            case "HUNT": {
                return Skill.HUNTER.getName();
            }
            case "CON": 
            case "CONSTRUCT": {
                return Skill.CONSTRUCTION.getName();
            }
            case "ALL": 
            case "TOTAL": {
                return "Overall";
            }
        }
        return skill;
    }

    private static HiscoreSkill findHiscoreSkill(String search) {
        String s = ChatCommandsPlugin.longSkillName(search);
        if (s == search) {
            s = ChatCommandsPlugin.longBossName(search);
        }
        for (HiscoreSkill skill : HiscoreSkill.values()) {
            if (!ChatCommandsPlugin.longBossName(skill.getName()).equalsIgnoreCase(s)) continue;
            return skill;
        }
        return null;
    }

    private int tobTeamSize() {
        return Math.min(this.client.getVarbitValue(6442), 1) + Math.min(this.client.getVarbitValue(6443), 1) + Math.min(this.client.getVarbitValue(6444), 1) + Math.min(this.client.getVarbitValue(6445), 1) + Math.min(this.client.getVarbitValue(6446), 1);
    }

    private int toaTeamSize() {
        return Math.min(this.client.getVarbitValue(14346), 1) + Math.min(this.client.getVarbitValue(14347), 1) + Math.min(this.client.getVarbitValue(14348), 1) + Math.min(this.client.getVarbitValue(14349), 1) + Math.min(this.client.getVarbitValue(14350), 1) + Math.min(this.client.getVarbitValue(14351), 1) + Math.min(this.client.getVarbitValue(14352), 1) + Math.min(this.client.getVarbitValue(14353), 1);
    }

    private int findPet(String name) {
        for (int petId : this.pets) {
            ItemComposition item = this.itemManager.getItemComposition(petId);
            if (!item.getName().equals(name)) continue;
            return item.getId();
        }
        return -1;
    }

    private static final class HiscoreLookup {
        private final String name;
        private final HiscoreEndpoint endpoint;

        public HiscoreLookup(String name, HiscoreEndpoint endpoint) {
            this.name = name;
            this.endpoint = endpoint;
        }

        public String getName() {
            return this.name;
        }

        public HiscoreEndpoint getEndpoint() {
            return this.endpoint;
        }

        public boolean equals(Object o) {
            if (o == this) {
                return true;
            }
            if (!(o instanceof HiscoreLookup)) {
                return false;
            }
            HiscoreLookup other = (HiscoreLookup)o;
            String this$name = this.getName();
            String other$name = other.getName();
            if (this$name == null ? other$name != null : !this$name.equals(other$name)) {
                return false;
            }
            HiscoreEndpoint this$endpoint = this.getEndpoint();
            HiscoreEndpoint other$endpoint = other.getEndpoint();
            return !(this$endpoint == null ? other$endpoint != null : !((Object)((Object)this$endpoint)).equals((Object)other$endpoint));
        }

        public int hashCode() {
            int PRIME = 59;
            int result = 1;
            String $name = this.getName();
            result = result * 59 + ($name == null ? 43 : $name.hashCode());
            HiscoreEndpoint $endpoint = this.getEndpoint();
            result = result * 59 + ($endpoint == null ? 43 : ((Object)((Object)$endpoint)).hashCode());
            return result;
        }

        public String toString() {
            return "ChatCommandsPlugin.HiscoreLookup(name=" + this.getName() + ", endpoint=" + String.valueOf((Object)this.getEndpoint()) + ")";
        }
    }
}

