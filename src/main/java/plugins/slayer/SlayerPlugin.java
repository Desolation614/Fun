/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.base.Strings
 *  com.google.inject.Binder
 *  com.google.inject.Provides
 *  javax.inject.Inject
 *  javax.inject.Named
 *  net.runelite.api.ChatMessageType
 *  net.runelite.api.Client
 *  net.runelite.api.EnumComposition
 *  net.runelite.api.GameState
 *  net.runelite.api.MenuAction
 *  net.runelite.api.MessageNode
 *  net.runelite.api.NPC
 *  net.runelite.api.NPCComposition
 *  net.runelite.api.events.ChatMessage
 *  net.runelite.api.events.CommandExecuted
 *  net.runelite.api.events.GameStateChanged
 *  net.runelite.api.events.GameTick
 *  net.runelite.api.events.MenuOptionClicked
 *  net.runelite.api.events.NpcDespawned
 *  net.runelite.api.events.NpcSpawned
 *  net.runelite.api.events.VarbitChanged
 *  net.runelite.api.widgets.Widget
 *  net.runelite.http.api.chat.Task
 *  org.apache.commons.lang3.ArrayUtils
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package net.runelite.client.plugins.slayer;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Strings;
import com.google.inject.Binder;
import com.google.inject.Provides;
import java.awt.Color;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ScheduledExecutorService;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.inject.Inject;
import javax.inject.Named;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.EnumComposition;
import net.runelite.api.GameState;
import net.runelite.api.MenuAction;
import net.runelite.api.MessageNode;
import net.runelite.api.NPC;
import net.runelite.api.NPCComposition;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.CommandExecuted;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.MenuOptionClicked;
import net.runelite.api.events.NpcDespawned;
import net.runelite.api.events.NpcSpawned;
import net.runelite.api.events.VarbitChanged;
import net.runelite.api.widgets.Widget;
import net.runelite.client.Notifier;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.chat.ChatClient;
import net.runelite.client.chat.ChatColorType;
import net.runelite.client.chat.ChatCommandManager;
import net.runelite.client.chat.ChatMessageBuilder;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ChatInput;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.game.ItemManager;
import net.runelite.client.game.ItemVariationMapping;
import net.runelite.client.game.npcoverlay.HighlightedNpc;
import net.runelite.client.game.npcoverlay.NpcOverlayService;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.slayer.SlayerConfig;
import net.runelite.client.plugins.slayer.SlayerOverlay;
import net.runelite.client.plugins.slayer.SlayerPluginService;
import net.runelite.client.plugins.slayer.SlayerPluginServiceImpl;
import net.runelite.client.plugins.slayer.TargetWeaknessOverlay;
import net.runelite.client.plugins.slayer.Task;
import net.runelite.client.plugins.slayer.TaskCounter;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.ui.overlay.infobox.InfoBoxManager;
import net.runelite.client.util.AsyncBufferedImage;
import net.runelite.client.util.ColorUtil;
import net.runelite.client.util.Text;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@PluginDescriptor(name="Slayer", description="Show additional slayer task related information", tags={"combat", "notifications", "overlay", "tasks"})
public class SlayerPlugin
extends Plugin {
    private static final Logger log = LoggerFactory.getLogger(SlayerPlugin.class);
    private static final String CHAT_SUPERIOR_MESSAGE = "A superior foe has appeared...";
    private static final String TASK_COMMAND_STRING = "!task";
    private static final Pattern TASK_STRING_VALIDATION = Pattern.compile("[^a-zA-Z0-9' -]");
    private static final int TASK_STRING_MAX_LENGTH = 50;
    @Inject
    private Client client;
    @Inject
    private SlayerConfig config;
    @Inject
    private ConfigManager configManager;
    @Inject
    private OverlayManager overlayManager;
    @Inject
    private SlayerOverlay overlay;
    @Inject
    private InfoBoxManager infoBoxManager;
    @Inject
    private ItemManager itemManager;
    @Inject
    private Notifier notifier;
    @Inject
    private ClientThread clientThread;
    @Inject
    private TargetWeaknessOverlay targetWeaknessOverlay;
    @Inject
    private ChatCommandManager chatCommandManager;
    @Inject
    private ScheduledExecutorService executor;
    @Inject
    private ChatClient chatClient;
    @Inject
    private NpcOverlayService npcOverlayService;
    private final List<NPC> targets = new ArrayList<NPC>();
    @Inject
    @Named(value="developerMode")
    boolean developerMode;
    private int amount;
    private int initialAmount;
    private String taskLocation;
    private String taskName;
    private TaskCounter counter;
    private Instant infoTimer;
    private boolean loginFlag;
    private final List<Pattern> targetNames = new ArrayList<Pattern>();
    private String[] taskLocations;
    public final Function<NPC, HighlightedNpc> isTarget = n -> {
        if ((this.config.highlightHull() || this.config.highlightTile() || this.config.highlightOutline()) && this.targets.contains(n)) {
            Color color = this.config.getTargetColor();
            return HighlightedNpc.builder().npc((NPC)n).highlightColor(color).fillColor(ColorUtil.colorWithAlpha(color, color.getAlpha() / 12)).hull(this.config.highlightHull()).tile(this.config.highlightTile()).outline(this.config.highlightOutline()).build();
        }
        return null;
    };

    @Override
    public void configure(Binder binder) {
        binder.bind(SlayerPluginService.class).to(SlayerPluginServiceImpl.class);
    }

    @Override
    protected void startUp() {
        this.chatCommandManager.registerCommandAsync(TASK_COMMAND_STRING, this::taskLookup, this::taskSubmit);
        this.npcOverlayService.registerHighlighter(this.isTarget);
        this.overlayManager.add(this.overlay);
        this.overlayManager.add(this.targetWeaknessOverlay);
        if (this.client.getGameState() == GameState.LOGGED_IN) {
            this.loginFlag = true;
            this.clientThread.invoke(this::updateTask);
        }
        this.clientThread.invoke(() -> {
            if (this.client.getGameState().getState() < GameState.LOGIN_SCREEN.getState()) {
                return false;
            }
            EnumComposition e = this.client.getEnum(4064);
            this.taskLocations = (String[])e.getStringVals().clone();
            return true;
        });
    }

    @Override
    protected void shutDown() {
        this.chatCommandManager.unregisterCommand(TASK_COMMAND_STRING);
        this.npcOverlayService.unregisterHighlighter(this.isTarget);
        this.overlayManager.remove(this.overlay);
        this.overlayManager.remove(this.targetWeaknessOverlay);
        this.removeCounter();
        this.targets.clear();
        this.taskLocations = null;
    }

    @Provides
    SlayerConfig provideSlayerConfig(ConfigManager configManager) {
        return configManager.getConfig(SlayerConfig.class);
    }

    @Subscribe
    public void onGameStateChanged(GameStateChanged event) {
        switch (event.getGameState()) {
            case HOPPING: 
            case LOGGING_IN: 
            case CONNECTION_LOST: {
                this.taskName = "";
                this.amount = 0;
                this.loginFlag = true;
                this.targets.clear();
            }
        }
    }

    @Subscribe
    public void onCommandExecuted(CommandExecuted commandExecuted) {
        if (this.developerMode && commandExecuted.getCommand().equalsIgnoreCase("task")) {
            String task = String.join((CharSequence)" ", commandExecuted.getArguments());
            this.setTask(task, 42, 42);
            log.debug("Set task to {}", (Object)task);
        }
    }

    @VisibleForTesting
    int getIntProfileConfig(String key) {
        Integer value = (Integer)this.configManager.getRSProfileConfiguration("slayer", key, Integer.TYPE);
        return value == null ? -1 : value;
    }

    private void setProfileConfig(String key, Object value) {
        if (value != null) {
            this.configManager.setRSProfileConfiguration("slayer", key, value);
        } else {
            this.configManager.unsetRSProfileConfiguration("slayer", key);
        }
    }

    private void save() {
        this.setProfileConfig("amount", this.amount);
        this.setProfileConfig("initialAmount", this.initialAmount);
        this.setProfileConfig("taskName", this.taskName);
        this.setProfileConfig("taskLocation", this.taskLocation);
    }

    @Subscribe
    public void onNpcSpawned(NpcSpawned npcSpawned) {
        NPC npc = npcSpawned.getNpc();
        if (this.isTarget(npc)) {
            this.targets.add(npc);
        }
    }

    @Subscribe
    public void onNpcDespawned(NpcDespawned npcDespawned) {
        NPC npc = npcDespawned.getNpc();
        this.targets.remove(npc);
    }

    @Subscribe
    public void onVarbitChanged(VarbitChanged varbitChanged) {
        int varpId = varbitChanged.getVarpId();
        int varbitId = varbitChanged.getVarbitId();
        if (varpId == 394 || varpId == 2096 || varpId == 395 || varbitId == 4723) {
            this.clientThread.invokeLater(this::updateTask);
        } else if (varbitId == 4068) {
            this.setProfileConfig("points", varbitChanged.getValue());
            if (this.counter != null) {
                this.removeCounter();
                this.addCounter();
            }
        } else if (varbitId == 4069) {
            this.setProfileConfig("streak", varbitChanged.getValue());
            if (this.counter != null) {
                this.removeCounter();
                this.addCounter();
            }
        }
    }

    private void updateTask() {
        int amount = this.client.getVarpValue(394);
        if (amount > 0) {
            String taskName;
            int taskId = this.client.getVarpValue(395);
            if (taskId == 98) {
                int structId = this.client.getEnum(5008).getIntValue(this.client.getVarbitValue(4723));
                taskName = this.client.getStructComposition(structId).getStringValue(1801);
            } else {
                taskName = this.client.getEnum(693).getStringValue(taskId);
            }
            int areaId = this.client.getVarpValue(2096);
            String taskLocation = null;
            if (areaId > 0) {
                taskLocation = StringUtils.capitalize((String)this.client.getEnum(4064).getStringValue(areaId));
            }
            if (this.loginFlag) {
                log.debug("Sync slayer task: {}x {} at {}", new Object[]{amount, taskName, taskLocation});
                this.initialAmount = this.getIntProfileConfig("initialAmount");
                this.setTask(taskName, amount, this.initialAmount, taskLocation, false);
                this.setProfileConfig("points", this.client.getVarbitValue(4068));
                this.setProfileConfig("streak", this.client.getVarbitValue(4069));
            } else if (!Objects.equals(taskName, this.taskName) || !Objects.equals(taskLocation, this.taskLocation)) {
                log.debug("Task change: {}x {} at {}", new Object[]{amount, taskName, taskLocation});
                this.setTask(taskName, amount, amount, taskLocation, true);
            } else if (amount != this.amount) {
                log.debug("Amount change: {} -> {}", (Object)this.amount, (Object)amount);
                this.amount = amount;
                this.setProfileConfig("amount", amount);
                if (this.config.showInfobox()) {
                    this.addCounter();
                    this.counter.setCount(amount);
                    this.infoTimer = Instant.now();
                }
            }
        } else if (this.amount > 0) {
            log.debug("Task complete");
            this.setTask("", 0, 0);
        }
    }

    @Subscribe
    public void onGameTick(GameTick tick) {
        Duration statTimeout;
        Duration timeSinceInfobox;
        if (this.infoTimer != null && this.config.statTimeout() != 0 && (timeSinceInfobox = Duration.between(this.infoTimer, Instant.now())).compareTo(statTimeout = Duration.ofMinutes(this.config.statTimeout())) >= 0) {
            this.removeCounter();
        }
        this.loginFlag = false;
    }

    @Subscribe
    public void onChatMessage(ChatMessage event) {
        if (event.getType() != ChatMessageType.GAMEMESSAGE && event.getType() != ChatMessageType.SPAM) {
            return;
        }
        String chatMsg = Text.removeTags(event.getMessage());
        if (chatMsg.equals(CHAT_SUPERIOR_MESSAGE)) {
            this.notifier.notify(this.config.showSuperiorNotification(), CHAT_SUPERIOR_MESSAGE);
        }
    }

    @Subscribe
    private void onConfigChanged(ConfigChanged event) {
        if (!event.getGroup().equals("slayer")) {
            return;
        }
        if (event.getKey().equals("infobox")) {
            if (this.config.showInfobox()) {
                this.clientThread.invoke(this::addCounter);
            } else {
                this.removeCounter();
            }
        } else {
            this.npcOverlayService.rebuild();
        }
    }

    @Subscribe
    public void onMenuOptionClicked(MenuOptionClicked menuOptionClicked) {
        if ((menuOptionClicked.getMenuAction() == MenuAction.CC_OP || menuOptionClicked.getMenuAction() == MenuAction.CC_OP_LOW_PRIORITY) && menuOptionClicked.getMenuOption().equals("Check")) {
            Widget w = this.client.getWidget(menuOptionClicked.getParam1());
            if (w == null) {
                return;
            }
            if (menuOptionClicked.getParam0() != -1 && (w = w.getChild(menuOptionClicked.getParam0())) == null) {
                return;
            }
            int itemId = w.getItemId();
            for (Widget child : w.getDynamicChildren()) {
                if (itemId != -1) continue;
                itemId = child.getItemId();
            }
            if ((itemId = ItemVariationMapping.map(itemId)) == 11864 || itemId == 11866 || itemId == 4155) {
                log.debug("Checked slayer task");
                this.infoTimer = Instant.now();
                this.addCounter();
            }
        }
    }

    @VisibleForTesting
    boolean isTarget(NPC npc) {
        if (this.targetNames.isEmpty()) {
            return false;
        }
        NPCComposition composition = npc.getTransformedComposition();
        if (composition == null) {
            return false;
        }
        String name = composition.getName().replace('\u00a0', ' ').toLowerCase();
        for (Pattern target : this.targetNames) {
            Matcher targetMatcher = target.matcher(name);
            if (!targetMatcher.find() || !ArrayUtils.contains((Object[])composition.getActions(), (Object)"Attack") && !ArrayUtils.contains((Object[])composition.getActions(), (Object)"Pick")) continue;
            return true;
        }
        return false;
    }

    private void rebuildTargetNames(Task task) {
        this.targetNames.clear();
        if (task != null) {
            Arrays.stream(task.getTargetNames()).map(SlayerPlugin::targetNamePattern).forEach(this.targetNames::add);
            this.targetNames.add(SlayerPlugin.targetNamePattern(this.taskName.replaceAll("s$", "")));
        }
    }

    private static Pattern targetNamePattern(String targetName) {
        return Pattern.compile("(?:\\s|^)" + targetName + "(?:\\s|$)", 2);
    }

    private void rebuildTargetList() {
        this.targets.clear();
        for (NPC npc : this.client.getNpcs()) {
            if (!this.isTarget(npc)) continue;
            this.targets.add(npc);
        }
    }

    @VisibleForTesting
    void setTask(String name, int amt, int initAmt) {
        this.setTask(name, amt, initAmt, null, true);
    }

    private void setTask(String name, int amt, int initAmt, String location, boolean addCounter) {
        this.taskName = name;
        this.amount = amt;
        this.initialAmount = initAmt;
        this.taskLocation = location;
        this.save();
        this.removeCounter();
        if (addCounter) {
            this.infoTimer = Instant.now();
            this.addCounter();
        }
        Task task = Task.getTask(name);
        this.rebuildTargetNames(task);
        this.rebuildTargetList();
        this.npcOverlayService.rebuild();
    }

    private void addCounter() {
        if (!this.config.showInfobox() || this.counter != null || Strings.isNullOrEmpty((String)this.taskName)) {
            return;
        }
        Task task = Task.getTask(this.taskName);
        int itemSpriteId = 4155;
        if (task != null) {
            itemSpriteId = task.getItemSpriteId();
        }
        AsyncBufferedImage taskImg = this.itemManager.getImage(itemSpriteId);
        String taskTooltip = ColorUtil.wrapWithColorTag("%s", new Color(255, 119, 0)) + "</br>";
        if (this.taskLocation != null && !this.taskLocation.isEmpty()) {
            taskTooltip = taskTooltip + this.taskLocation + "</br>";
        }
        taskTooltip = taskTooltip + ColorUtil.wrapWithColorTag("Pts:", Color.YELLOW) + " %s</br>" + ColorUtil.wrapWithColorTag("Streak:", Color.YELLOW) + " %s";
        if (this.initialAmount > 0) {
            taskTooltip = taskTooltip + "</br>" + ColorUtil.wrapWithColorTag("Start:", Color.YELLOW) + " " + this.initialAmount;
        }
        this.counter = new TaskCounter(taskImg, this, this.amount);
        this.counter.setTooltip(String.format(taskTooltip, SlayerPlugin.capsString(this.taskName), this.getIntProfileConfig("points"), this.getIntProfileConfig("streak")));
        this.infoBoxManager.addInfoBox(this.counter);
    }

    private void removeCounter() {
        if (this.counter == null) {
            return;
        }
        this.infoBoxManager.removeInfoBox(this.counter);
        this.counter = null;
    }

    void taskLookup(ChatMessage chatMessage, String message) {
        net.runelite.http.api.chat.Task task;
        if (!this.config.taskCommand()) {
            return;
        }
        ChatMessageType type = chatMessage.getType();
        String player = type.equals((Object)ChatMessageType.PRIVATECHATOUT) ? this.client.getLocalPlayer().getName() : Text.removeTags(chatMessage.getName()).replace('\u00a0', ' ');
        try {
            task = this.chatClient.getTask(player);
        }
        catch (IOException ex) {
            log.debug("unable to lookup slayer task", (Throwable)ex);
            return;
        }
        if (TASK_STRING_VALIDATION.matcher(task.getTask()).find() || task.getTask().length() > 50 || TASK_STRING_VALIDATION.matcher(task.getLocation()).find() || task.getLocation().length() > 50 || Task.getTask(task.getTask()) == null || !this.isValidLocation(task.getLocation())) {
            log.debug("Validation failed for task name or location: {}", (Object)task);
            return;
        }
        int killed = task.getInitialAmount() - task.getAmount();
        StringBuilder sb = new StringBuilder();
        sb.append(task.getTask());
        if (!Strings.isNullOrEmpty((String)task.getLocation())) {
            sb.append(" (").append(task.getLocation()).append(')');
        }
        sb.append(": ");
        if (killed < 0) {
            sb.append(task.getAmount()).append(" left");
        } else {
            sb.append(killed).append('/').append(task.getInitialAmount()).append(" killed");
        }
        String response = new ChatMessageBuilder().append(ChatColorType.NORMAL).append("Slayer Task: ").append(ChatColorType.HIGHLIGHT).append(sb.toString()).build();
        MessageNode messageNode = chatMessage.getMessageNode();
        messageNode.setRuneLiteFormatMessage(response);
        this.client.refreshChat();
    }

    private boolean taskSubmit(ChatInput chatInput, String value) {
        if (Strings.isNullOrEmpty((String)this.taskName)) {
            return false;
        }
        String playerName = this.client.getLocalPlayer().getName();
        this.executor.execute(() -> {
            try {
                this.chatClient.submitTask(playerName, SlayerPlugin.capsString(this.taskName), this.amount, this.initialAmount, this.taskLocation);
            }
            catch (Exception ex) {
                log.warn("unable to submit slayer task", (Throwable)ex);
            }
            finally {
                chatInput.resume();
            }
        });
        return true;
    }

    private boolean isValidLocation(String location) {
        if (location == null || location.isEmpty()) {
            return true;
        }
        if (this.taskLocations != null) {
            for (String l : this.taskLocations) {
                if (!l.equalsIgnoreCase(location)) continue;
                return true;
            }
        }
        return false;
    }

    private static String capsString(String str) {
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    List<NPC> getTargets() {
        return this.targets;
    }

    int getAmount() {
        return this.amount;
    }

    void setAmount(int amount) {
        this.amount = amount;
    }

    int getInitialAmount() {
        return this.initialAmount;
    }

    void setInitialAmount(int initialAmount) {
        this.initialAmount = initialAmount;
    }

    String getTaskLocation() {
        return this.taskLocation;
    }

    void setTaskLocation(String taskLocation) {
        this.taskLocation = taskLocation;
    }

    String getTaskName() {
        return this.taskName;
    }

    void setTaskName(String taskName) {
        this.taskName = taskName;
    }
}

