/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableSet
 *  com.google.inject.Provides
 *  javax.inject.Inject
 *  net.runelite.api.Actor
 *  net.runelite.api.ChatMessageType
 *  net.runelite.api.Client
 *  net.runelite.api.GameState
 *  net.runelite.api.Player
 *  net.runelite.api.events.ActorDeath
 *  net.runelite.api.events.AnimationChanged
 *  net.runelite.api.events.ChatMessage
 *  net.runelite.api.events.GameTick
 *  net.runelite.api.events.ScriptCallbackEvent
 *  net.runelite.api.events.ScriptPreFired
 *  net.runelite.api.events.WidgetLoaded
 *  net.runelite.api.widgets.Widget
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package net.runelite.client.plugins.screenshot;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Provides;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.time.LocalDate;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ScheduledExecutorService;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.inject.Inject;
import net.runelite.api.Actor;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.Player;
import net.runelite.api.events.ActorDeath;
import net.runelite.api.events.AnimationChanged;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.ScriptCallbackEvent;
import net.runelite.api.events.ScriptPreFired;
import net.runelite.api.events.WidgetLoaded;
import net.runelite.api.widgets.Widget;
import net.runelite.client.RuneLite;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.PlayerLootReceived;
import net.runelite.client.game.SpriteManager;
import net.runelite.client.input.KeyManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.screenshot.ScreenshotConfig;
import net.runelite.client.plugins.screenshot.ScreenshotOverlay;
import net.runelite.client.ui.ClientToolbar;
import net.runelite.client.ui.DrawManager;
import net.runelite.client.ui.NavigationButton;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.util.HotkeyListener;
import net.runelite.client.util.ImageCapture;
import net.runelite.client.util.ImageUtil;
import net.runelite.client.util.LinkBrowser;
import net.runelite.client.util.Text;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@PluginDescriptor(name="Screenshot", description="Enable the manual and automatic taking of screenshots", tags={"external", "images", "integration", "notifications"}, enabledByDefault=false)
public class ScreenshotPlugin
extends Plugin {
    private static final Logger log = LoggerFactory.getLogger(ScreenshotPlugin.class);
    private static final String COLLECTION_LOG_TEXT = "New item added to your collection log: ";
    private static final String CHEST_LOOTED_MESSAGE = "You find some treasure in the chest!";
    private static final Map<Integer, String> CHEST_LOOT_EVENTS = ImmutableMap.of((Object)12127, (Object)"The Gauntlet");
    private static final int GAUNTLET_REGION = 7512;
    private static final int CORRUPTED_GAUNTLET_REGION = 7768;
    private static final Pattern NUMBER_PATTERN = Pattern.compile("([,0-9]+)");
    private static final Pattern LEVEL_UP_PATTERN = Pattern.compile(".*Your ([a-zA-Z]+) (?:level is|are)? now (\\d+)\\.");
    private static final Pattern LEVEL_UP_MESSAGE_PATTERN = Pattern.compile("Congratulations, you've (just advanced your (?<skill>[a-zA-Z]+) level\\. You are now level (?<level>\\d+)|reached the highest possible (?<skill99>[a-zA-Z]+) level of 99)\\.");
    private static final Pattern BOSSKILL_MESSAGE_PATTERN = Pattern.compile("Your (.+) (?:kill|success) count is: ?<col=[0-9a-f]{6}>([0-9,]+)</col>");
    private static final Pattern VALUABLE_DROP_PATTERN = Pattern.compile(".*Valuable drop: ([^<>]+?\\(((?:\\d+,?)+) coins\\))(?:</col>)?");
    private static final Pattern UNTRADEABLE_DROP_PATTERN = Pattern.compile(".*Untradeable drop: ([^<>]+)(?:</col>)?");
    private static final Pattern DUEL_END_PATTERN = Pattern.compile("You have now (won|lost) ([0-9,]+) duels?\\.");
    private static final Pattern QUEST_PATTERN_1 = Pattern.compile(".+?ve\\.*? (?<verb>been|rebuilt|.+?ed)? ?(?:the )?'?(?<quest>.+?)'?(?: [Qq]uest)?[!.]?$");
    private static final Pattern QUEST_PATTERN_2 = Pattern.compile("'?(?<quest>.+?)'?(?: [Qq]uest)? (?<verb>[a-z]\\w+?ed)?(?: f.*?)?[!.]?$");
    private static final Pattern COMBAT_ACHIEVEMENTS_PATTERN = Pattern.compile("Congratulations, you've completed an? (?<tier>\\w+) combat task: <col=[0-9a-f]+>(?<task>(.+))</col>");
    private static final ImmutableList<String> RFD_TAGS = ImmutableList.of((Object)"Another Cook", (Object)"freed", (Object)"defeated", (Object)"saved");
    private static final ImmutableList<String> WORD_QUEST_IN_NAME_TAGS = ImmutableList.of((Object)"Another Cook", (Object)"Doric", (Object)"Heroes", (Object)"Legends", (Object)"Observatory", (Object)"Olaf", (Object)"Waterfall");
    private static final ImmutableList<String> PET_MESSAGES = ImmutableList.of((Object)"You have a funny feeling like you're being followed", (Object)"You feel something weird sneaking into your backpack", (Object)"You have a funny feeling like you would have been followed");
    private static final Pattern BA_HIGH_GAMBLE_REWARD_PATTERN = Pattern.compile("(?<reward>.+)!<br>High level gamble count: <col=7f0000>(?<gambleCount>.+)</col>");
    private static final Set<Integer> REPORT_BUTTON_TLIS = ImmutableSet.of((Object)548, (Object)161, (Object)164);
    private static final String SD_KINGDOM_REWARDS = "Kingdom Rewards";
    private static final String SD_BOSS_KILLS = "Boss Kills";
    private static final String SD_CLUE_SCROLL_REWARDS = "Clue Scroll Rewards";
    private static final String SD_FRIENDS_CHAT_KICKS = "Friends Chat Kicks";
    private static final String SD_PETS = "Pets";
    private static final String SD_CHEST_LOOT = "Chest Loot";
    private static final String SD_VALUABLE_DROPS = "Valuable Drops";
    private static final String SD_UNTRADEABLE_DROPS = "Untradeable Drops";
    private static final String SD_DUELS = "Duels";
    private static final String SD_COLLECTION_LOG = "Collection Log";
    private static final String SD_PVP_KILLS = "PvP Kills";
    private static final String SD_DEATHS = "Deaths";
    private static final String SD_COMBAT_ACHIEVEMENTS = "Combat Achievements";
    private static final String SD_WILDERNESS_LOOT_CHEST = "Wilderness Loot Chest";
    private static final String SD_LEVELS = "Levels";
    private static final String SD_LEAGUE_TASKS = "League Tasks";
    private String clueType;
    private Integer clueNumber;
    private KillType killType;
    private Integer killCountNumber;
    private boolean shouldTakeScreenshot;
    private boolean notificationStarted;
    @Inject
    private ScreenshotConfig config;
    @Inject
    private OverlayManager overlayManager;
    @Inject
    private ScreenshotOverlay screenshotOverlay;
    @Inject
    private Client client;
    @Inject
    private ClientToolbar clientToolbar;
    @Inject
    private DrawManager drawManager;
    @Inject
    private ScheduledExecutorService executor;
    @Inject
    private KeyManager keyManager;
    @Inject
    private SpriteManager spriteManager;
    @Inject
    private ImageCapture imageCapture;
    private BufferedImage reportButton;
    private NavigationButton titleBarButton;
    private String kickPlayerName;
    private final HotkeyListener hotkeyListener = new HotkeyListener(() -> this.config.hotkey()){

        @Override
        public void hotkeyPressed() {
            ScreenshotPlugin.this.manualScreenshot();
        }
    };

    @Provides
    ScreenshotConfig getConfig(ConfigManager configManager) {
        return configManager.getConfig(ScreenshotConfig.class);
    }

    @Override
    protected void startUp() throws Exception {
        this.overlayManager.add(this.screenshotOverlay);
        RuneLite.SCREENSHOT_DIR.mkdirs();
        this.keyManager.registerKeyListener(this.hotkeyListener);
        BufferedImage iconImage = ImageUtil.loadImageResource(this.getClass(), "screenshot.png");
        this.titleBarButton = NavigationButton.builder().tooltip("Take screenshot").icon(iconImage).onClick(this::manualScreenshot).popup((Map<String, Runnable>)ImmutableMap.builder().put((Object)"Open screenshot folder...", () -> LinkBrowser.open(RuneLite.SCREENSHOT_DIR.toString())).build()).build();
        this.clientToolbar.addNavigation(this.titleBarButton);
        this.spriteManager.getSpriteAsync(3057, 0, s -> {
            this.reportButton = s;
        });
    }

    @Override
    protected void shutDown() throws Exception {
        this.overlayManager.remove(this.screenshotOverlay);
        this.clientToolbar.removeNavigation(this.titleBarButton);
        this.keyManager.unregisterKeyListener(this.hotkeyListener);
        this.kickPlayerName = null;
        this.notificationStarted = false;
    }

    @Subscribe
    public void onGameTick(GameTick event) {
        if (!this.shouldTakeScreenshot) {
            return;
        }
        this.shouldTakeScreenshot = false;
        String screenshotSubDir = null;
        String fileName = null;
        if (this.client.getWidget(15269890) != null) {
            fileName = this.parseLevelUpWidget(15269890);
            screenshotSubDir = SD_LEVELS;
        } else if (this.client.getWidget(12648450) != null) {
            String text = this.client.getWidget(12648450).getText();
            if (Text.removeTags(text).contains("High level gamble")) {
                if (this.config.screenshotHighGamble()) {
                    fileName = ScreenshotPlugin.parseBAHighGambleWidget(text);
                    screenshotSubDir = "BA High Gambles";
                }
            } else if (this.config.screenshotLevels()) {
                fileName = this.parseLevelUpWidget(12648450);
                screenshotSubDir = SD_LEVELS;
            }
        } else if (this.client.getWidget(0x990004) != null) {
            String text = this.client.getWidget(0x990004).getText();
            fileName = ScreenshotPlugin.parseQuestCompletedWidget(text);
            screenshotSubDir = "Quests";
        }
        if (fileName != null) {
            this.takeScreenshot(fileName, screenshotSubDir);
        }
    }

    @Subscribe
    public void onActorDeath(ActorDeath actorDeath) {
        Actor actor = actorDeath.getActor();
        if (actor instanceof Player) {
            Player player = (Player)actor;
            if (player == this.client.getLocalPlayer() && this.config.screenshotPlayerDeath()) {
                this.takeScreenshot("Death", SD_DEATHS);
            } else if (player != this.client.getLocalPlayer() && player.getCanvasTilePoly() != null && ((player.isFriendsChatMember() || player.isFriend()) && this.config.screenshotFriendDeath() || player.isClanMember() && this.config.screenshotClanDeath())) {
                this.takeScreenshot("Death " + player.getName(), SD_DEATHS);
            }
        }
    }

    @Subscribe
    public void onAnimationChanged(AnimationChanged animationChanged) {
        Actor actor = animationChanged.getActor();
        if (actor == this.client.getLocalPlayer() && actor.getAnimation() == 10873 && this.config.screenshotPlayerDeath()) {
            this.takeScreenshot("Doom Death", SD_DEATHS);
        }
    }

    @Subscribe
    public void onPlayerLootReceived(PlayerLootReceived playerLootReceived) {
        if (this.config.screenshotKills()) {
            Player player = playerLootReceived.getPlayer();
            String name = player.getName();
            String fileName = "Kill " + name;
            this.takeScreenshot(fileName, SD_PVP_KILLS);
        }
    }

    @Subscribe
    public void onScriptCallbackEvent(ScriptCallbackEvent e) {
        if (!"confirmFriendsChatKick".equals(e.getEventName())) {
            return;
        }
        Object[] objectStack = this.client.getObjectStack();
        int objectStackSize = this.client.getObjectStackSize();
        this.kickPlayerName = (String)objectStack[objectStackSize - 1];
    }

    @Subscribe
    public void onChatMessage(ChatMessage event) {
        String fileName;
        int valuableDropValue;
        int regionID;
        String eventName;
        String fileName2;
        Matcher m;
        if (event.getType() != ChatMessageType.GAMEMESSAGE && event.getType() != ChatMessageType.SPAM && event.getType() != ChatMessageType.TRADE && event.getType() != ChatMessageType.FRIENDSCHATNOTIFICATION) {
            return;
        }
        String chatMessage = event.getMessage();
        if (chatMessage.contains("You have completed") && chatMessage.contains("Treasure") && (m = NUMBER_PATTERN.matcher(Text.removeTags(chatMessage))).find()) {
            this.clueNumber = Integer.valueOf(m.group().replace(",", ""));
            this.clueType = chatMessage.substring(chatMessage.lastIndexOf(m.group()) + m.group().length() + 1, chatMessage.indexOf("Treasure") - 1);
            return;
        }
        if (chatMessage.startsWith("Your Barrows chest count is") && (m = NUMBER_PATTERN.matcher(Text.removeTags(chatMessage))).find()) {
            this.killType = KillType.BARROWS;
            this.killCountNumber = Integer.valueOf(m.group().replace(",", ""));
            return;
        }
        if (chatMessage.startsWith("Your completed Chambers of Xeric count is:") && (m = NUMBER_PATTERN.matcher(Text.removeTags(chatMessage))).find()) {
            this.killType = KillType.COX;
            this.killCountNumber = Integer.valueOf(m.group().replace(",", ""));
            return;
        }
        if (chatMessage.startsWith("Your completed Chambers of Xeric Challenge Mode count is:") && (m = NUMBER_PATTERN.matcher(Text.removeTags(chatMessage))).find()) {
            this.killType = KillType.COX_CM;
            this.killCountNumber = Integer.valueOf(m.group().replace(",", ""));
            return;
        }
        if (chatMessage.startsWith("Your completed Theatre of Blood") && (m = NUMBER_PATTERN.matcher(Text.removeTags(chatMessage))).find()) {
            this.killType = chatMessage.contains("Hard Mode") ? KillType.TOB_HM : (chatMessage.contains("Story Mode") ? KillType.TOB_SM : KillType.TOB);
            this.killCountNumber = Integer.valueOf(m.group().replace(",", ""));
            return;
        }
        if (chatMessage.startsWith("Your completed Tombs of Amascut") && (m = NUMBER_PATTERN.matcher(Text.removeTags(chatMessage))).find()) {
            this.killType = chatMessage.contains("Expert Mode") ? KillType.TOA_EXPERT_MODE : (chatMessage.contains("Entry Mode") ? KillType.TOA_ENTRY_MODE : KillType.TOA);
            this.killCountNumber = Integer.valueOf(m.group().replace(",", ""));
            return;
        }
        if (chatMessage.startsWith("Your Lunar Chest count is") && (m = NUMBER_PATTERN.matcher(Text.removeTags(chatMessage))).find()) {
            this.killType = KillType.MOONS_OF_PERIL;
            this.killCountNumber = Integer.valueOf(m.group().replace(",", ""));
            return;
        }
        if (chatMessage.contains("Search the chest nearby to retrieve your earned rewards!")) {
            this.killType = KillType.FORTIS_COLOSSEUM;
            return;
        }
        if (chatMessage.equals("Your request to kick/ban this user was successful.") && this.config.screenshotKick()) {
            if (this.kickPlayerName == null) {
                return;
            }
            this.takeScreenshot("Kick " + this.kickPlayerName, SD_FRIENDS_CHAT_KICKS);
            this.kickPlayerName = null;
        }
        if (PET_MESSAGES.stream().anyMatch(chatMessage::contains) && this.config.screenshotPet()) {
            String fileName3 = "Pet";
            this.takeScreenshot(fileName3, SD_PETS);
        }
        if (this.config.screenshotBossKills() && (m = BOSSKILL_MESSAGE_PATTERN.matcher(chatMessage)).find()) {
            String bossName = Text.removeTags(m.group(1));
            String bossKillcount = m.group(2).replace(",", "");
            fileName2 = bossName + "(" + bossKillcount + ")";
            this.takeScreenshot(fileName2, SD_BOSS_KILLS);
        }
        if (chatMessage.equals(CHEST_LOOTED_MESSAGE) && this.config.screenshotRewards() && (eventName = CHEST_LOOT_EVENTS.get(regionID = this.client.getLocalPlayer().getWorldLocation().getRegionID())) != null) {
            this.takeScreenshot(eventName, SD_CHEST_LOOT);
        }
        if (this.config.screenshotValuableDrop() && (m = VALUABLE_DROP_PATTERN.matcher(chatMessage)).matches() && (valuableDropValue = Integer.parseInt(m.group(2).replace(",", ""))) >= this.config.valuableDropThreshold()) {
            String valuableDropName = m.group(1);
            if (valuableDropName.contains("(GE:")) {
                valuableDropName = valuableDropName.substring(0, valuableDropName.indexOf("(GE:") - 1);
            }
            fileName2 = "Valuable drop " + valuableDropName;
            this.takeScreenshot(fileName2, SD_VALUABLE_DROPS);
        }
        if (this.config.screenshotUntradeableDrop() && !this.isInsideGauntlet() && (m = UNTRADEABLE_DROP_PATTERN.matcher(chatMessage)).matches()) {
            String untradeableDropName = m.group(1);
            String fileName4 = "Untradeable drop " + untradeableDropName;
            this.takeScreenshot(fileName4, SD_UNTRADEABLE_DROPS);
        }
        if (this.config.screenshotDuels() && (m = DUEL_END_PATTERN.matcher(chatMessage)).find()) {
            String result = m.group(1);
            String count = m.group(2).replace(",", "");
            fileName2 = "Duel " + result + " (" + count + ")";
            this.takeScreenshot(fileName2, SD_DUELS);
        }
        if (chatMessage.startsWith(COLLECTION_LOG_TEXT) && this.client.getVarbitValue(11959) == 1 && this.config.screenshotCollectionLogEntries()) {
            String entry = Text.removeTags(chatMessage).substring(COLLECTION_LOG_TEXT.length());
            String fileName5 = "Collection log (" + entry + ")";
            this.takeScreenshot(fileName5, SD_COLLECTION_LOG);
        }
        if (chatMessage.contains("combat task") && this.config.screenshotCombatAchievements() && this.client.getVarbitValue(12455) == 1 && !(fileName = ScreenshotPlugin.parseCombatAchievementWidget(chatMessage)).isEmpty()) {
            this.takeScreenshot(fileName, SD_COMBAT_ACHIEVEMENTS);
        }
        if (this.client.getVarbitValue(9452) == 1 && this.config.screenshotLevels() && (m = LEVEL_UP_MESSAGE_PATTERN.matcher(chatMessage)).matches()) {
            String skillName = StringUtils.capitalize((String)(m.group("skill") != null ? m.group("skill") : m.group("skill99")));
            String skillLevel = m.group("level") != null ? m.group("level") : "99";
            fileName2 = skillName + "(" + skillLevel + ")";
            this.takeScreenshot(fileName2, SD_LEVELS);
        }
    }

    @Subscribe
    public void onWidgetLoaded(WidgetLoaded event) {
        String screenshotSubDir;
        Object fileName;
        int groupId = event.getGroupId();
        switch (groupId) {
            case 23: 
            case 73: 
            case 153: 
            case 155: 
            case 539: 
            case 771: 
            case 864: 
            case 868: {
                if (this.config.screenshotRewards()) break;
                return;
            }
            case 233: {
                if (this.config.screenshotLevels()) break;
                return;
            }
            case 193: {
                if (this.config.screenshotLevels() || this.config.screenshotHighGamble()) break;
                return;
            }
            case 616: {
                if (this.config.screenshotKingdom()) break;
                return;
            }
            case 742: {
                if (this.config.screenshotWildernessLootChest()) break;
                return;
            }
        }
        switch (groupId) {
            case 616: {
                fileName = "Kingdom " + String.valueOf(LocalDate.now());
                screenshotSubDir = SD_KINGDOM_REWARDS;
                break;
            }
            case 539: {
                if (this.killType == KillType.COX) {
                    fileName = "Chambers of Xeric(" + this.killCountNumber + ")";
                    screenshotSubDir = SD_BOSS_KILLS;
                    this.killType = null;
                    this.killCountNumber = 0;
                    break;
                }
                if (this.killType == KillType.COX_CM) {
                    fileName = "Chambers of Xeric Challenge Mode(" + this.killCountNumber + ")";
                    screenshotSubDir = SD_BOSS_KILLS;
                    this.killType = null;
                    this.killCountNumber = 0;
                    break;
                }
                return;
            }
            case 23: {
                if (this.killType != KillType.TOB && this.killType != KillType.TOB_SM && this.killType != KillType.TOB_HM) {
                    return;
                }
                switch (this.killType) {
                    case TOB: {
                        fileName = "Theatre of Blood(" + this.killCountNumber + ")";
                        break;
                    }
                    case TOB_SM: {
                        fileName = "Theatre of Blood Story Mode(" + this.killCountNumber + ")";
                        break;
                    }
                    case TOB_HM: {
                        fileName = "Theatre of Blood Hard Mode(" + this.killCountNumber + ")";
                        break;
                    }
                    default: {
                        throw new IllegalStateException();
                    }
                }
                screenshotSubDir = SD_BOSS_KILLS;
                this.killType = null;
                this.killCountNumber = 0;
                break;
            }
            case 771: {
                if (this.killType != KillType.TOA && this.killType != KillType.TOA_ENTRY_MODE && this.killType != KillType.TOA_EXPERT_MODE) {
                    return;
                }
                switch (this.killType) {
                    case TOA: {
                        fileName = "Tombs of Amascut(" + this.killCountNumber + ")";
                        break;
                    }
                    case TOA_ENTRY_MODE: {
                        fileName = "Tombs of Amascut Entry Mode(" + this.killCountNumber + ")";
                        break;
                    }
                    case TOA_EXPERT_MODE: {
                        fileName = "Tombs of Amascut Expert Mode(" + this.killCountNumber + ")";
                        break;
                    }
                    default: {
                        throw new IllegalStateException();
                    }
                }
                screenshotSubDir = SD_BOSS_KILLS;
                this.killType = null;
                this.killCountNumber = 0;
                break;
            }
            case 155: {
                if (this.killType != KillType.BARROWS) {
                    return;
                }
                fileName = "Barrows(" + this.killCountNumber + ")";
                screenshotSubDir = SD_BOSS_KILLS;
                this.killType = null;
                this.killCountNumber = 0;
                break;
            }
            case 868: {
                if (this.killType != KillType.MOONS_OF_PERIL) {
                    return;
                }
                fileName = "Moons of Peril(" + this.killCountNumber + ")";
                screenshotSubDir = SD_BOSS_KILLS;
                this.killType = null;
                this.killCountNumber = 0;
                break;
            }
            case 153: 
            case 193: 
            case 233: {
                this.shouldTakeScreenshot = true;
                return;
            }
            case 73: {
                if (this.clueType == null || this.clueNumber == null) {
                    return;
                }
                fileName = Character.toUpperCase(this.clueType.charAt(0)) + this.clueType.substring(1) + "(" + this.clueNumber + ")";
                screenshotSubDir = SD_CLUE_SCROLL_REWARDS;
                this.clueType = null;
                this.clueNumber = null;
                break;
            }
            case 742: {
                fileName = "Loot key";
                screenshotSubDir = SD_WILDERNESS_LOOT_CHEST;
                break;
            }
            case 864: {
                if (this.killType != KillType.FORTIS_COLOSSEUM) {
                    return;
                }
                fileName = "Fortis Colosseum Chest";
                screenshotSubDir = SD_CHEST_LOOT;
                this.killType = null;
                break;
            }
            default: {
                return;
            }
        }
        this.takeScreenshot((String)fileName, screenshotSubDir);
    }

    @Subscribe
    public void onScriptPreFired(ScriptPreFired scriptPreFired) {
        switch (scriptPreFired.getScriptId()) {
            case 3346: {
                this.notificationStarted = true;
                break;
            }
            case 3347: {
                String fileName;
                String task;
                String[] s;
                if (!this.notificationStarted) {
                    return;
                }
                String topText = this.client.getVarcStrValue(387);
                String bottomText = this.client.getVarcStrValue(388);
                log.debug("Notification: top: {} bottom: {}", (Object)topText, (Object)bottomText);
                if (topText.equalsIgnoreCase("Collection log") && this.config.screenshotCollectionLogEntries()) {
                    String entry = Text.removeTags(bottomText).substring("New item:".length());
                    String fileName2 = "Collection log (" + entry + ")";
                    this.takeScreenshot(fileName2, SD_COLLECTION_LOG);
                }
                if (topText.equalsIgnoreCase("Combat Task Completed!") && this.config.screenshotCombatAchievements() && this.client.getVarbitValue(12455) == 0) {
                    s = bottomText.split("<.*?>");
                    task = s[1];
                    fileName = "Combat task (" + task.replaceAll("[:?]", "") + ")";
                    this.takeScreenshot(fileName, SD_COMBAT_ACHIEVEMENTS);
                }
                if (topText.equalsIgnoreCase("League Task Complete!") && this.config.screenshotLeagueTasks()) {
                    s = bottomText.split("<.*?>");
                    task = s[1];
                    fileName = "League task (" + task.replaceAll("[:?]", "") + ")";
                    this.takeScreenshot(fileName, SD_LEAGUE_TASKS);
                }
                this.notificationStarted = false;
            }
        }
    }

    private void manualScreenshot() {
        this.takeScreenshot("", null);
    }

    String parseLevelUpWidget(int levelUpLevel) {
        Widget levelChild = this.client.getWidget(levelUpLevel);
        if (levelChild == null) {
            return null;
        }
        Matcher m = LEVEL_UP_PATTERN.matcher(levelChild.getText());
        if (!m.matches()) {
            return null;
        }
        String skillName = m.group(1);
        String skillLevel = m.group(2);
        return skillName + "(" + skillLevel + ")";
    }

    @VisibleForTesting
    static String parseQuestCompletedWidget(String text) {
        String verb;
        Matcher questMatchFinal;
        Matcher questMatch1 = QUEST_PATTERN_1.matcher(text);
        Matcher questMatch2 = QUEST_PATTERN_2.matcher(text);
        Matcher matcher = questMatchFinal = questMatch1.matches() ? questMatch1 : questMatch2;
        if (!questMatchFinal.matches()) {
            return "Quest(quest not found)";
        }
        Object quest = questMatchFinal.group("quest");
        String string = verb = questMatchFinal.group("verb") != null ? questMatchFinal.group("verb") : "";
        if (verb.contains("kind of")) {
            quest = (String)quest + " partial completion";
        } else if (verb.contains("completely")) {
            quest = (String)quest + " II";
        }
        if (RFD_TAGS.stream().anyMatch(((String)quest + verb)::contains)) {
            quest = "Recipe for Disaster - " + (String)quest;
        }
        if (WORD_QUEST_IN_NAME_TAGS.stream().anyMatch(((String)quest)::contains)) {
            quest = (String)quest + " Quest";
        }
        return "Quest(" + (String)quest + ")";
    }

    @VisibleForTesting
    static String parseBAHighGambleWidget(String text) {
        Matcher highGambleMatch = BA_HIGH_GAMBLE_REWARD_PATTERN.matcher(text);
        if (highGambleMatch.find()) {
            String gambleCount = highGambleMatch.group("gambleCount");
            return String.format("High Gamble(%s)", gambleCount);
        }
        return "High Gamble(count not found)";
    }

    @VisibleForTesting
    static String parseCombatAchievementWidget(String text) {
        Matcher m = COMBAT_ACHIEVEMENTS_PATTERN.matcher(text);
        if (m.find()) {
            String task = m.group("task").replaceAll("[:?]", "");
            return "Combat task (" + task + ")";
        }
        return "";
    }

    @VisibleForTesting
    void takeScreenshot(String fileName, String subDir) {
        if (this.client.getGameState() == GameState.LOGIN_SCREEN) {
            log.info("Login screenshot prevented");
            return;
        }
        Consumer<Image> imageCallback = img -> this.executor.submit(() -> this.saveScreenshot(fileName, subDir, (Image)img));
        if (this.config.displayDate() && REPORT_BUTTON_TLIS.contains(this.client.getTopLevelInterfaceId())) {
            this.screenshotOverlay.queueForTimestamp(imageCallback);
        } else {
            this.drawManager.requestNextFrameListener(imageCallback);
        }
    }

    private void saveScreenshot(String fileName, String subDir, Image image) {
        BufferedImage screenshot = !this.config.includeFrame() ? ImageUtil.bufferedImageFromImage(image) : this.imageCapture.addClientFrame(image);
        this.imageCapture.saveScreenshot(screenshot, fileName, subDir, this.config.notifyWhenTaken(), this.config.copyToClipboard());
    }

    private boolean isInsideGauntlet() {
        return this.client.isInInstancedRegion() && this.client.getMapRegions().length > 0 && (this.client.getMapRegions()[0] == 7512 || this.client.getMapRegions()[0] == 7768);
    }

    @VisibleForTesting
    int getClueNumber() {
        return this.clueNumber;
    }

    @VisibleForTesting
    String getClueType() {
        return this.clueType;
    }

    @VisibleForTesting
    KillType getKillType() {
        return this.killType;
    }

    @VisibleForTesting
    int getKillCountNumber() {
        return this.killCountNumber;
    }

    BufferedImage getReportButton() {
        return this.reportButton;
    }

    static enum KillType {
        BARROWS,
        COX,
        COX_CM,
        MOONS_OF_PERIL,
        TOB,
        TOB_SM,
        TOB_HM,
        TOA_ENTRY_MODE,
        TOA,
        TOA_EXPERT_MODE,
        FORTIS_COLOSSEUM;

    }
}

