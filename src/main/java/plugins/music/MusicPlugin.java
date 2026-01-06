/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.primitives.Ints
 *  com.google.inject.Provides
 *  javax.inject.Inject
 *  net.runelite.api.Actor
 *  net.runelite.api.Client
 *  net.runelite.api.GameState
 *  net.runelite.api.NPC
 *  net.runelite.api.Player
 *  net.runelite.api.Preferences
 *  net.runelite.api.ScriptEvent
 *  net.runelite.api.StructComposition
 *  net.runelite.api.events.AmbientSoundEffectCreated
 *  net.runelite.api.events.AreaSoundEffectPlayed
 *  net.runelite.api.events.BeforeRender
 *  net.runelite.api.events.ClientTick
 *  net.runelite.api.events.GameStateChanged
 *  net.runelite.api.events.PostStructComposition
 *  net.runelite.api.events.ScriptPreFired
 *  net.runelite.api.events.SoundEffectPlayed
 *  net.runelite.api.events.VarClientIntChanged
 *  net.runelite.api.events.VolumeChanged
 *  net.runelite.api.events.WidgetLoaded
 *  net.runelite.api.widgets.Widget
 *  net.runelite.api.widgets.WidgetConfig
 */
package net.runelite.client.plugins.music;

import com.google.common.collect.ImmutableSet;
import com.google.common.primitives.Ints;
import com.google.inject.Provides;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.IntConsumer;
import java.util.function.IntSupplier;
import java.util.stream.Collectors;
import javax.inject.Inject;
import net.runelite.api.Actor;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.NPC;
import net.runelite.api.Player;
import net.runelite.api.Preferences;
import net.runelite.api.ScriptEvent;
import net.runelite.api.StructComposition;
import net.runelite.api.events.AmbientSoundEffectCreated;
import net.runelite.api.events.AreaSoundEffectPlayed;
import net.runelite.api.events.BeforeRender;
import net.runelite.api.events.ClientTick;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.PostStructComposition;
import net.runelite.api.events.ScriptPreFired;
import net.runelite.api.events.SoundEffectPlayed;
import net.runelite.api.events.VarClientIntChanged;
import net.runelite.api.events.VolumeChanged;
import net.runelite.api.events.WidgetLoaded;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetConfig;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.game.chatbox.ChatboxPanelManager;
import net.runelite.client.game.chatbox.ChatboxTextInput;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.music.MusicConfig;
import net.runelite.client.ui.overlay.tooltip.Tooltip;
import net.runelite.client.ui.overlay.tooltip.TooltipManager;

@PluginDescriptor(name="Music", description="Adds search and filter for the music list, and additional volume control", tags={"sound", "volume"})
public class MusicPlugin
extends Plugin {
    private static final int SLIDER_HANDLE_SIZE = 16;
    private static final Set<Integer> SOURCELESS_PLAYER_SOUNDS = ImmutableSet.of((Object)200);
    private static final Set<Integer> PRAYER_SOUNDS = ImmutableSet.of((Object)2690, (Object)2688, (Object)2664, (Object)2685, (Object)2670, (Object)2684, (Object[])new Integer[]{2689, 2662, 2679, 2678, 1982, 2666, 2668, 2687, 2691, 2667, 2675, 2677, 2676, 2665, 10194, 2669, 10100, 2682, 2680, 2686, 3826, 3825, 2663});
    @Inject
    private Client client;
    @Inject
    private ClientThread clientThread;
    @Inject
    private MusicConfig musicConfig;
    @Inject
    private ChatboxPanelManager chatboxPanelManager;
    @Inject
    private TooltipManager tooltipManager;
    private Channel musicChannel;
    private Channel effectChannel;
    private Channel areaChannel;
    private Channel[] channels;
    private ChatboxTextInput searchInput;
    private Widget musicSearchButton;
    private Widget musicFilterButton;
    private Collection<Widget> tracks;
    private MusicState currentMusicFilter = MusicState.ALL;
    private Tooltip sliderTooltip;
    private boolean shuttingDown = false;

    @Override
    protected void startUp() {
        this.clientThread.invoke(() -> {
            this.shuttingDown = false;
            Preferences preferences = this.client.getPreferences();
            this.musicChannel = new Channel("Music", 168, 12426, this.musicConfig::getMusicVolume, this.musicConfig::setMusicVolume, arg_0 -> ((Client)this.client).setMusicVolume(arg_0), 255, 7602274);
            this.effectChannel = new Channel("Sound Effects", 169, 12427, this.musicConfig::getSoundEffectVolume, this.musicConfig::setSoundEffectVolume, arg_0 -> ((Preferences)preferences).setSoundEffectVolume(arg_0), 127, 0x740070);
            this.areaChannel = new Channel("Area Sounds", 872, 12428, this.musicConfig::getAreaSoundEffectVolume, this.musicConfig::setAreaSoundEffectVolume, arg_0 -> ((Preferences)preferences).setAreaSoundEffectVolume(arg_0), 127, 7602302);
            this.channels = new Channel[]{this.musicChannel, this.effectChannel, this.areaChannel};
            this.addMusicButtons();
            if (this.client.getGameState() == GameState.LOGGED_IN) {
                if (this.musicConfig.granularSliders()) {
                    this.updateMusicOptions();
                    this.resetSettingsWindow();
                }
                if (this.musicConfig.muteAmbientSounds()) {
                    this.client.setGameState(GameState.LOADING);
                }
            }
        });
    }

    @Override
    protected void shutDown() {
        Widget header = this.client.getWidget(0xEF0000);
        if (header != null) {
            header.deleteAllChildren();
        }
        this.tracks = null;
        this.clientThread.invoke(() -> {
            this.shuttingDown = true;
            this.teardownMusicOptions();
            if (this.musicConfig.muteAmbientSounds() && this.client.getGameState() == GameState.LOGGED_IN) {
                this.client.setGameState(GameState.LOADING);
            }
        });
    }

    @Provides
    MusicConfig getConfig(ConfigManager configManager) {
        return configManager.getConfig(MusicConfig.class);
    }

    @Subscribe
    public void onGameStateChanged(GameStateChanged gameStateChanged) {
        GameState gameState = gameStateChanged.getGameState();
        if (gameState == GameState.LOGIN_SCREEN) {
            this.currentMusicFilter = MusicState.ALL;
            this.tracks = null;
        } else if (gameState == GameState.LOGGED_IN && this.musicConfig.muteAmbientSounds()) {
            this.client.getAmbientSoundEffects().clear();
        }
    }

    @Subscribe
    public void onWidgetLoaded(WidgetLoaded widgetLoaded) {
        if (widgetLoaded.getGroupId() == 239) {
            this.tracks = null;
            this.currentMusicFilter = MusicState.ALL;
            this.addMusicButtons();
        }
        if ((widgetLoaded.getGroupId() == 134 || widgetLoaded.getGroupId() == 116) && this.musicConfig.granularSliders()) {
            this.updateMusicOptions();
        }
    }

    private void addMusicButtons() {
        Widget header = this.client.getWidget(0xEF0000);
        if (header == null) {
            return;
        }
        header.deleteAllChildren();
        this.musicSearchButton = header.createChild(-1, 5);
        this.musicSearchButton.setSpriteId(1113);
        this.musicSearchButton.setOriginalWidth(18);
        this.musicSearchButton.setOriginalHeight(17);
        this.musicSearchButton.setXPositionMode(2);
        this.musicSearchButton.setOriginalX(5);
        this.musicSearchButton.setOriginalY(32);
        this.musicSearchButton.setHasListener(true);
        this.musicSearchButton.setAction(1, "Open");
        this.musicSearchButton.setOnOpListener(new Object[]{e -> this.openSearch()});
        this.musicSearchButton.setName("Search");
        this.musicSearchButton.revalidate();
        this.musicFilterButton = header.createChild(-1, 5);
        this.musicFilterButton.setSpriteId(1063);
        this.musicFilterButton.setOriginalWidth(15);
        this.musicFilterButton.setOriginalHeight(15);
        this.musicFilterButton.setXPositionMode(2);
        this.musicFilterButton.setOriginalX(25);
        this.musicFilterButton.setOriginalY(34);
        this.musicFilterButton.setHasListener(true);
        this.musicFilterButton.setAction(1, "Toggle");
        this.musicFilterButton.setOnOpListener(new Object[]{e -> this.toggleStatus()});
        this.musicFilterButton.setName("All");
        this.musicFilterButton.revalidate();
    }

    @Subscribe
    public void onVarClientIntChanged(VarClientIntChanged varClientIntChanged) {
        if (this.isChatboxOpen() && !this.isOnMusicTab()) {
            this.chatboxPanelManager.close();
        }
    }

    @Subscribe
    public void onVolumeChanged(VolumeChanged volumeChanged) {
        if (this.musicConfig.granularSliders()) {
            this.updateMusicOptions();
        }
    }

    @Subscribe
    public void onConfigChanged(ConfigChanged configChanged) {
        if (configChanged.getGroup().equals("music")) {
            this.clientThread.invoke(() -> {
                if ("granularSliders".equals(configChanged.getKey())) {
                    if (this.musicConfig.granularSliders()) {
                        this.updateMusicOptions();
                        this.resetSettingsWindow();
                    } else {
                        this.teardownMusicOptions();
                    }
                } else if ("muteAmbientSounds".equals(configChanged.getKey())) {
                    if (this.client.getGameState() == GameState.LOGGED_IN) {
                        this.client.setGameState(GameState.LOADING);
                    }
                } else if (this.musicConfig.granularSliders()) {
                    this.updateMusicOptions();
                }
            });
        }
    }

    private boolean isOnMusicTab() {
        return this.client.getVarcIntValue(171) == 13;
    }

    private boolean isChatboxOpen() {
        return this.searchInput != null && this.chatboxPanelManager.getCurrentInput() == this.searchInput;
    }

    private String getChatboxInput() {
        return this.isChatboxOpen() ? this.searchInput.getValue() : "";
    }

    private void toggleStatus() {
        MusicState[] states = MusicState.values();
        this.currentMusicFilter = states[(this.currentMusicFilter.ordinal() + 1) % states.length];
        this.musicFilterButton.setSpriteId(this.currentMusicFilter.getSpriteID());
        this.musicFilterButton.setName(this.currentMusicFilter.getName());
        this.updateFilter(this.getChatboxInput());
        this.client.playSoundEffect(2266);
    }

    private void openSearch() {
        this.updateFilter("");
        this.client.playSoundEffect(2266);
        this.musicSearchButton.setAction(1, "Close");
        this.musicSearchButton.setOnOpListener(new Object[]{e -> this.closeSearch()});
        this.searchInput = this.chatboxPanelManager.openTextInput("Search music list").onChanged(s -> this.clientThread.invokeLater(() -> this.updateFilter(s.trim()))).onDone(s -> false).onClose(() -> {
            this.clientThread.invokeLater(() -> this.updateFilter(""));
            this.musicSearchButton.setOnOpListener(new Object[]{e -> this.openSearch()});
            this.musicSearchButton.setAction(1, "Open");
        }).build();
    }

    private void closeSearch() {
        this.updateFilter("");
        this.chatboxPanelManager.close();
        this.client.playSoundEffect(2266);
    }

    private void updateFilter(String input) {
        Widget container = this.client.getWidget(0xEF0000);
        Widget musicList = this.client.getWidget(15663110);
        Widget scrollContainer = this.client.getWidget(15663108);
        if (container == null || musicList == null) {
            return;
        }
        String filter = input.toLowerCase();
        this.updateList(scrollContainer, musicList, filter);
    }

    private void updateList(Widget scrollContainer, Widget musicList, String filter) {
        if (this.tracks == null) {
            this.tracks = Arrays.stream(musicList.getDynamicChildren()).sorted(Comparator.comparingInt(Widget::getRelativeY)).collect(Collectors.toList());
        }
        this.tracks.forEach(w -> w.setHidden(true));
        Collection relevantTracks = this.tracks.stream().filter(w -> w.getText().toLowerCase().contains(filter)).filter(w -> this.currentMusicFilter == MusicState.ALL || w.getTextColor() == this.currentMusicFilter.getColor()).collect(Collectors.toList());
        int y = 3;
        for (Widget track : relevantTracks) {
            track.setHidden(false);
            track.setOriginalY(y);
            track.revalidate();
            y += track.getHeight();
        }
        y += 3;
        int newHeight = 0;
        if (scrollContainer.getScrollHeight() > 0) {
            newHeight = scrollContainer.getScrollY() * y / scrollContainer.getScrollHeight();
        }
        scrollContainer.setScrollHeight(y);
        scrollContainer.revalidateScroll();
        this.client.runScript(new Object[]{72, 15663111, 15663108, newHeight});
    }

    @Subscribe
    private void onPostStructComposition(PostStructComposition ev) {
        if (this.shuttingDown) {
            return;
        }
        StructComposition sc = ev.getStructComposition();
        switch (sc.getId()) {
            case 2753: 
            case 2754: 
            case 2755: {
                if (!this.musicConfig.granularSliders()) {
                    return;
                }
                sc.setValue(1101, 1);
                sc.setValue(1085, 0);
                sc.setValue(1105, 0);
                sc.setValue(1106, 1);
                sc.setValue(1107, 1);
                sc.setValue(1108, 1);
                sc.setValue(1109, 0);
                sc.setValue(1110, 0);
            }
        }
    }

    @Subscribe
    private void onScriptPreFired(ScriptPreFired ev) {
        if (this.shuttingDown) {
            return;
        }
        if (ev.getScriptId() == 3885) {
            Channel channel;
            if (!this.musicConfig.granularSliders()) {
                return;
            }
            int arg = this.client.getIntStackSize() - 11;
            int[] is = this.client.getIntStack();
            switch (is[arg]) {
                case 30: {
                    channel = this.musicChannel;
                    break;
                }
                case 31: {
                    channel = this.effectChannel;
                    break;
                }
                case 32: {
                    channel = this.areaChannel;
                    break;
                }
                default: {
                    return;
                }
            }
            Widget track = this.client.getScriptActiveWidget();
            Widget handle = this.client.getWidget(is[arg + 1]).getChild(is[arg + 2]);
            Widget realTrack = this.client.getWidget(is[arg + 7]);
            SettingsSlider s = new SettingsSlider(channel, handle, track, is[arg + 3], is[arg + 4], is[arg + 5], realTrack);
            s.update();
            s.getChannel().setWindowSlider(s);
        }
        if (ev.getScriptId() == 907 && this.musicConfig.granularSliders()) {
            for (Channel c : this.channels) {
                c.updateVar();
            }
        }
    }

    private void updateMusicOptions() {
        for (Channel channel : this.channels) {
            channel.update();
        }
    }

    private void teardownMusicOptions() {
        this.client.getStructCompositionCache().reset();
        for (Channel channel : this.channels) {
            channel.shutDown();
        }
        this.resetSettingsWindow();
    }

    private void resetSettingsWindow() {
        this.client.getStructCompositionCache().reset();
        Widget init = this.client.getWidget(8781825);
        if (init != null) {
            this.client.createScriptEvent(init.getOnLoadListener()).setSource(init).run();
        }
    }

    @Subscribe
    private void onBeforeRender(BeforeRender ev) {
        if (this.sliderTooltip != null) {
            this.tooltipManager.add(this.sliderTooltip);
        }
    }

    @Subscribe
    public void onClientTick(ClientTick event) {
        this.sliderTooltip = null;
    }

    @Subscribe
    public void onAreaSoundEffectPlayed(AreaSoundEffectPlayed areaSoundEffectPlayed) {
        Actor source = areaSoundEffectPlayed.getSource();
        int soundId = areaSoundEffectPlayed.getSoundId();
        if (source == this.client.getLocalPlayer() && this.musicConfig.muteOwnAreaSounds()) {
            areaSoundEffectPlayed.consume();
        } else if (source != this.client.getLocalPlayer() && (source instanceof Player || source == null && SOURCELESS_PLAYER_SOUNDS.contains(soundId)) && this.musicConfig.muteOtherAreaSounds()) {
            areaSoundEffectPlayed.consume();
        } else if (source instanceof NPC && this.musicConfig.muteNpcAreaSounds()) {
            areaSoundEffectPlayed.consume();
        } else if (source == null && !SOURCELESS_PLAYER_SOUNDS.contains(soundId) && this.musicConfig.muteEnvironmentAreaSounds()) {
            areaSoundEffectPlayed.consume();
        }
    }

    @Subscribe
    public void onSoundEffectPlayed(SoundEffectPlayed soundEffectPlayed) {
        if (this.musicConfig.mutePrayerSounds() && PRAYER_SOUNDS.contains(soundEffectPlayed.getSoundId())) {
            soundEffectPlayed.consume();
        }
    }

    @Subscribe
    public void onAmbientSoundEffectCreated(AmbientSoundEffectCreated ev) {
        if (this.musicConfig.muteAmbientSounds()) {
            this.client.getAmbientSoundEffects().clear();
        }
    }

    private class Channel {
        private final String name;
        private final int var;
        private final int mutedVarbitId;
        private final IntSupplier getter;
        private final Consumer<Integer> setter;
        private final IntConsumer volumeChanger;
        private final int max;
        private final Slider sideSlider;
        private Slider windowSlider;

        Channel(String name, int var, int mutedVarbitId, IntSupplier getter, Consumer<Integer> setter, IntConsumer volumeChanger, int max, int sideRoot) {
            this.name = name;
            this.var = var;
            this.mutedVarbitId = mutedVarbitId;
            this.getter = getter;
            this.setter = setter;
            this.volumeChanger = volumeChanger;
            this.max = max;
            this.sideSlider = new SettingsSideSlider(this, sideRoot);
        }

        private int getValueRaw() {
            int value = this.getter.getAsInt();
            if (value == 0) {
                int raw = MusicPlugin.this.client.getVarpValue(this.var);
                if (raw == 0) {
                    raw = -MusicPlugin.this.client.getVarbitValue(this.mutedVarbitId);
                }
                value += (value = raw * this.max / 100) < 0 ? -1 : 1;
            }
            return value;
        }

        private int getValue() {
            int value = this.getValueRaw();
            if (value < 0) {
                return 0;
            }
            return value - 1;
        }

        public void toggleMute() {
            int val = -this.getValueRaw();
            if (val == -1) {
                val = this.max / 2;
            }
            this.setter.accept(val);
        }

        public void setLevel(int level) {
            this.setter.accept(level + 1);
            this.update();
        }

        public void update() {
            this.volumeChanger.accept(this.getValue());
            this.sideSlider.update();
            if (this.windowSlider != null) {
                this.windowSlider.update();
            }
        }

        public void updateVar() {
            int varVal;
            int val = this.getValue();
            MusicPlugin.this.client.getVarps()[this.var] = varVal = Math.round((float)val / ((float)this.max / 100.0f));
        }

        public void shutDown() {
            this.sideSlider.shutDown();
            if (this.windowSlider != null) {
                this.windowSlider.shutDown();
            }
            this.volumeChanger.accept(MusicPlugin.this.client.getVarpValue(this.var) * this.max / 100);
        }

        public String getName() {
            return this.name;
        }

        public int getMax() {
            return this.max;
        }

        public void setWindowSlider(Slider windowSlider) {
            this.windowSlider = windowSlider;
        }
    }

    private class SettingsSlider
    extends Slider {
        private final int offsetX;
        private final int offsetY;
        private final int width;
        private final Widget realTrack;

        SettingsSlider(Channel channel, Widget handle, Widget track, int width, int offsetY, int offsetX, Widget realTrack) {
            super(channel);
            this.handle = handle;
            this.track = track;
            this.width = width;
            this.offsetX = offsetX;
            this.offsetY = offsetY;
            this.realTrack = realTrack;
        }

        @Override
        public void update() {
            super.update();
            int val = this.channel.getValue();
            this.handle.setOriginalX(this.offsetX + val * this.getWidth() / this.channel.getMax());
            this.handle.setOriginalY(this.offsetY);
            this.handle.revalidate();
        }

        @Override
        protected int getWidth() {
            return this.width - 16;
        }

        @Override
        protected void click(ScriptEvent ev) {
            super.click(ev);
            this.realTrack.setOriginalX(this.offsetX);
            this.realTrack.setOriginalY(this.offsetY);
            this.realTrack.setOriginalWidth(this.width);
            this.realTrack.setOriginalHeight(16);
            this.realTrack.revalidate();
        }

        @Override
        public void shutDown() {
            super.shutDown();
        }
    }

    private class SettingsSideSlider
    extends Slider {
        private final int root;
        private Widget icon;

        SettingsSideSlider(Channel channel, int root) {
            super(channel);
            this.root = root;
        }

        @Override
        public void update() {
            Widget root = MusicPlugin.this.client.getWidget(this.root);
            if (root == null) {
                return;
            }
            Object[] onLoad = root.getOnLoadListener();
            if (onLoad == null || onLoad.length != 6) {
                return;
            }
            this.icon = MusicPlugin.this.client.getWidget(((Integer)onLoad[1]).intValue());
            this.track = MusicPlugin.this.client.getWidget(((Integer)onLoad[2]).intValue());
            this.handle = MusicPlugin.this.client.getWidget(((Integer)onLoad[3]).intValue());
            if (this.track == null || this.handle == null) {
                return;
            }
            Widget[] trackChildren = this.track.getChildren();
            if (trackChildren != null) {
                for (Widget w : trackChildren) {
                    if (w == null) continue;
                    w.setAction(0, null);
                }
            }
            this.handle.setOnVarTransmitListener((Object[])null);
            this.handle.setDragParent(this.track);
            this.handle.setSpriteId(2860);
            super.update();
            int val = this.channel.getValue();
            this.handle.setOriginalX(val * this.getWidth() / this.channel.getMax());
            this.handle.revalidate();
            boolean unmuted = val != 0;
            Widget strikethrough = this.icon.getChild(1);
            if (strikethrough != null) {
                strikethrough.setHidden(unmuted);
            }
            this.icon.setAction(0, unmuted ? "Mute" : "Unmute");
            this.icon.setName(this.channel.getName());
            this.icon.setOnMouseRepeatListener((Object[])null);
            this.icon.setOnOpListener(new Object[]{ev -> this.channel.toggleMute()});
            this.icon.setClickMask(0);
        }

        @Override
        public void shutDown() {
            Widget root;
            super.shutDown();
            if (this.handle != null) {
                this.handle.setSpriteId(2858);
            }
            if (this.icon != null) {
                this.icon.setOnOpListener((Object[])null);
                this.icon.setClickMask(WidgetConfig.transmitAction((int)0));
            }
            if ((root = MusicPlugin.this.client.getWidget(this.root)) != null) {
                MusicPlugin.this.client.createScriptEvent(root.getOnLoadListener()).setSource(root).run();
            }
            this.icon = null;
            this.track = null;
            this.handle = null;
        }
    }

    private class Slider {
        protected final Channel channel;
        protected Widget track;
        protected Widget handle;

        public void update() {
            this.handle.setNoClickThrough(false);
            this.handle.setOnDragListener(new Object[]{this::drag});
            this.handle.setOnDragCompleteListener(new Object[]{this::drag});
            this.handle.setHasListener(true);
            this.track.setOnMouseRepeatListener(new Object[]{ev -> {
                int value = this.channel.getValue();
                int percent = (int)Math.round((double)value * 100.0 / (double)this.channel.getMax());
                MusicPlugin.this.sliderTooltip = new Tooltip(this.channel.getName() + ": " + percent + "%");
            }});
            this.track.setOnClickListener(new Object[]{this::click});
            this.track.setHasListener(true);
        }

        public void shutDown() {
            if (this.handle != null) {
                this.handle.setDragParent(null);
                this.handle.setOnDragListener((Object[])null);
                this.handle.setOnDragCompleteListener((Object[])null);
            }
            if (this.track != null) {
                this.track.setOnMouseRepeatListener((Object[])null);
                this.track.setOnClickListener((Object[])null);
            }
        }

        protected void drag(ScriptEvent ev) {
            this.moveHandle(ev.getMouseX());
        }

        protected void click(ScriptEvent ev) {
            this.moveHandle(ev.getMouseX() - 8);
        }

        protected void moveHandle(int x) {
            int level = x * this.channel.max / this.getWidth();
            level = Ints.constrainToRange((int)level, (int)0, (int)this.channel.max);
            this.channel.setLevel(level);
            int percent = (int)Math.round((double)level * 100.0 / (double)this.channel.getMax());
            MusicPlugin.this.sliderTooltip = new Tooltip(this.channel.getName() + ": " + percent + "%");
        }

        protected int getWidth() {
            return this.track.getWidth() - 16;
        }

        public Slider(Channel channel) {
            this.channel = channel;
        }

        public Channel getChannel() {
            return this.channel;
        }
    }

    private static enum MusicState {
        NOT_FOUND(0xFF0000, "Locked", 1060),
        FOUND(901389, "Unlocked", 1061),
        ALL(0, "All", 1063);

        private final int color;
        private final String name;
        private final int spriteID;

        private MusicState(int color, String name, int spriteID) {
            this.color = color;
            this.name = name;
            this.spriteID = spriteID;
        }

        public int getColor() {
            return this.color;
        }

        public String getName() {
            return this.name;
        }

        public int getSpriteID() {
            return this.spriteID;
        }
    }
}

