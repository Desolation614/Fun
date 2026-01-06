/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.collect.ImmutableSet
 *  javax.inject.Inject
 *  javax.inject.Singleton
 *  net.runelite.api.ChatMessageType
 *  net.runelite.api.Client
 *  net.runelite.api.GameObject
 *  net.runelite.api.ObjectComposition
 *  net.runelite.api.Tile
 *  net.runelite.api.coords.LocalPoint
 *  net.runelite.api.coords.WorldPoint
 *  net.runelite.api.events.ChatMessage
 *  net.runelite.api.events.GameStateChanged
 *  net.runelite.api.events.MenuOptionClicked
 *  net.runelite.api.widgets.Widget
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package net.runelite.client.plugins.timetracking.farming;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableSet;
import java.lang.reflect.Type;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.inject.Inject;
import javax.inject.Singleton;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.GameObject;
import net.runelite.api.ObjectComposition;
import net.runelite.api.Tile;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.MenuOptionClicked;
import net.runelite.api.widgets.Widget;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.timetracking.farming.CompostState;
import net.runelite.client.plugins.timetracking.farming.FarmingPatch;
import net.runelite.client.plugins.timetracking.farming.FarmingWorld;
import net.runelite.client.plugins.timetracking.farming.PatchImplementation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class CompostTracker {
    private static final Logger log = LoggerFactory.getLogger(CompostTracker.class);
    private static final Duration COMPOST_ACTION_TIMEOUT = Duration.ofSeconds(30L);
    private static final Pattern COMPOST_USED_ON_PATCH = Pattern.compile("You treat the .+ with (?<compostType>ultra|super|)compost\\.");
    private static final Pattern FERTILE_SOIL_CAST = Pattern.compile("^The .+ has been treated with (?<compostType>ultra|super|)compost");
    private static final Pattern ALREADY_TREATED = Pattern.compile("This .+ has already been (treated|fertilised) with (?<compostType>ultra|super|)compost(?: - the spell can't make it any more fertile)?\\.");
    private static final Pattern INSPECT_PATCH = Pattern.compile("This is an? .+\\. The soil has been treated with (?<compostType>ultra|super|)compost\\..*");
    private static final ImmutableSet<Integer> COMPOST_ITEMS = ImmutableSet.of((Object)6032, (Object)6034, (Object)21483, (Object)22997);
    private final Client client;
    private final FarmingWorld farmingWorld;
    private final ConfigManager configManager;
    @VisibleForTesting
    final Map<FarmingPatch, PendingCompost> pendingCompostActions = new HashMap<FarmingPatch, PendingCompost>();

    private static String configKey(FarmingPatch fp) {
        return fp.configKey() + ".compost";
    }

    public void setCompostState(FarmingPatch fp, CompostState state) {
        log.debug("Storing compost state [{}] for patch [{}]", (Object)state, (Object)fp);
        if (state == null) {
            this.configManager.unsetRSProfileConfiguration("timetracking", CompostTracker.configKey(fp));
        } else {
            this.configManager.setRSProfileConfiguration("timetracking", CompostTracker.configKey(fp), state);
        }
    }

    public CompostState getCompostState(FarmingPatch fp) {
        return (CompostState)((Object)this.configManager.getRSProfileConfiguration("timetracking", CompostTracker.configKey(fp), (Type)((Object)CompostState.class)));
    }

    @Subscribe
    public void onMenuOptionClicked(MenuOptionClicked e) {
        if (!this.isCompostAction(e)) {
            return;
        }
        ObjectComposition patchDef = this.client.getObjectDefinition(e.getId());
        WorldPoint actionLocation = WorldPoint.fromScene((Client)this.client, (int)e.getParam0(), (int)e.getParam1(), (int)this.client.getPlane());
        FarmingPatch targetPatch = this.farmingWorld.getRegionsForLocation(actionLocation).stream().flatMap(fr -> Arrays.stream(fr.getPatches())).filter(fp -> fp.getVarbit() == patchDef.getVarbitId()).filter(fp -> fp.getImplementation() != PatchImplementation.COMPOST && fp.getImplementation() != PatchImplementation.BIG_COMPOST).findFirst().orElse(null);
        if (targetPatch == null) {
            return;
        }
        log.debug("Storing pending compost action for patch [{}]", (Object)targetPatch);
        PendingCompost pc = new PendingCompost(Instant.now().plus(COMPOST_ACTION_TIMEOUT), actionLocation, targetPatch);
        this.pendingCompostActions.put(targetPatch, pc);
    }

    private boolean isCompostAction(MenuOptionClicked e) {
        switch (e.getMenuAction()) {
            case WIDGET_TARGET_ON_GAME_OBJECT: {
                Widget w = this.client.getSelectedWidget();
                return w != null && (COMPOST_ITEMS.contains((Object)w.getItemId()) || w.getId() == 14286977);
            }
            case GAME_OBJECT_FIRST_OPTION: 
            case GAME_OBJECT_SECOND_OPTION: 
            case GAME_OBJECT_THIRD_OPTION: 
            case GAME_OBJECT_FOURTH_OPTION: 
            case GAME_OBJECT_FIFTH_OPTION: {
                return "Inspect".equals(e.getMenuOption());
            }
        }
        return false;
    }

    @Subscribe
    public void onChatMessage(ChatMessage e) {
        if (e.getType() != ChatMessageType.GAMEMESSAGE && e.getType() != ChatMessageType.SPAM) {
            return;
        }
        CompostState compostUsed = CompostTracker.determineCompostUsed(e.getMessage());
        if (compostUsed == null) {
            return;
        }
        this.expirePendingActions();
        this.pendingCompostActions.values().stream().filter(this::playerIsBesidePatch).findFirst().ifPresent(pc -> {
            this.setCompostState(pc.getFarmingPatch(), compostUsed);
            this.pendingCompostActions.remove(pc.getFarmingPatch());
        });
    }

    @Subscribe
    public void onGameStateChanged(GameStateChanged e) {
        switch (e.getGameState()) {
            case LOGGED_IN: 
            case LOADING: {
                return;
            }
        }
        this.pendingCompostActions.clear();
    }

    private boolean playerIsBesidePatch(PendingCompost pendingCompost) {
        LocalPoint localPatchLocation = LocalPoint.fromWorld((Client)this.client, (WorldPoint)pendingCompost.getPatchLocation());
        if (localPatchLocation == null) {
            return false;
        }
        int patchVarb = pendingCompost.getFarmingPatch().getVarbit();
        Tile patchTile = this.client.getScene().getTiles()[this.client.getPlane()][localPatchLocation.getSceneX()][localPatchLocation.getSceneY()];
        GameObject patchObject = null;
        for (GameObject go : patchTile.getGameObjects()) {
            if (go == null || this.client.getObjectDefinition(go.getId()).getVarbitId() != patchVarb) continue;
            patchObject = go;
            break;
        }
        assert (patchObject != null);
        WorldPoint playerPos = this.client.getLocalPlayer().getWorldLocation();
        int playerX = playerPos.getX();
        int playerY = playerPos.getY();
        WorldPoint patchBase = pendingCompost.getPatchLocation();
        int minX = patchBase.getX();
        int minY = patchBase.getY();
        int maxX = minX + patchObject.sizeX() - 1;
        int maxY = minY + patchObject.sizeY() - 1;
        return playerX >= minX - 1 && playerX <= maxX + 1 && playerY >= minY - 1 && playerY <= maxY + 1;
    }

    private void expirePendingActions() {
        this.pendingCompostActions.values().removeIf(e -> Instant.now().isAfter(e.getTimeout()));
    }

    @VisibleForTesting
    static CompostState determineCompostUsed(String chatMessage) {
        if (!chatMessage.contains("compost")) {
            return null;
        }
        Matcher matcher = COMPOST_USED_ON_PATCH.matcher(chatMessage);
        if (matcher.matches() || (matcher = FERTILE_SOIL_CAST.matcher(chatMessage)).find() || (matcher = ALREADY_TREATED.matcher(chatMessage)).matches() || (matcher = INSPECT_PATCH.matcher(chatMessage)).matches()) {
            String compostGroup;
            switch (compostGroup = matcher.group("compostType")) {
                case "ultra": {
                    return CompostState.ULTRACOMPOST;
                }
                case "super": {
                    return CompostState.SUPERCOMPOST;
                }
            }
            return CompostState.COMPOST;
        }
        return null;
    }

    @Inject
    public CompostTracker(Client client, FarmingWorld farmingWorld, ConfigManager configManager) {
        this.client = client;
        this.farmingWorld = farmingWorld;
        this.configManager = configManager;
    }

    @VisibleForTesting
    static final class PendingCompost {
        private final Instant timeout;
        private final WorldPoint patchLocation;
        private final FarmingPatch farmingPatch;

        public PendingCompost(Instant timeout, WorldPoint patchLocation, FarmingPatch farmingPatch) {
            this.timeout = timeout;
            this.patchLocation = patchLocation;
            this.farmingPatch = farmingPatch;
        }

        public Instant getTimeout() {
            return this.timeout;
        }

        public WorldPoint getPatchLocation() {
            return this.patchLocation;
        }

        public FarmingPatch getFarmingPatch() {
            return this.farmingPatch;
        }

        public boolean equals(Object o) {
            if (o == this) {
                return true;
            }
            if (!(o instanceof PendingCompost)) {
                return false;
            }
            PendingCompost other = (PendingCompost)o;
            Instant this$timeout = this.getTimeout();
            Instant other$timeout = other.getTimeout();
            if (this$timeout == null ? other$timeout != null : !((Object)this$timeout).equals(other$timeout)) {
                return false;
            }
            WorldPoint this$patchLocation = this.getPatchLocation();
            WorldPoint other$patchLocation = other.getPatchLocation();
            if (this$patchLocation == null ? other$patchLocation != null : !this$patchLocation.equals(other$patchLocation)) {
                return false;
            }
            FarmingPatch this$farmingPatch = this.getFarmingPatch();
            FarmingPatch other$farmingPatch = other.getFarmingPatch();
            return !(this$farmingPatch == null ? other$farmingPatch != null : !this$farmingPatch.equals(other$farmingPatch));
        }

        public int hashCode() {
            int PRIME = 59;
            int result = 1;
            Instant $timeout = this.getTimeout();
            result = result * 59 + ($timeout == null ? 43 : ((Object)$timeout).hashCode());
            WorldPoint $patchLocation = this.getPatchLocation();
            result = result * 59 + ($patchLocation == null ? 43 : $patchLocation.hashCode());
            FarmingPatch $farmingPatch = this.getFarmingPatch();
            result = result * 59 + ($farmingPatch == null ? 43 : $farmingPatch.hashCode());
            return result;
        }

        public String toString() {
            return "CompostTracker.PendingCompost(timeout=" + String.valueOf(this.getTimeout()) + ", patchLocation=" + String.valueOf(this.getPatchLocation()) + ", farmingPatch=" + String.valueOf(this.getFarmingPatch()) + ")";
        }
    }
}

