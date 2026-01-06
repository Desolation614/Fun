/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Strings
 *  com.google.common.util.concurrent.Runnables
 *  com.google.gson.Gson
 *  com.google.gson.JsonSyntaxException
 *  com.google.gson.reflect.TypeToken
 *  javax.inject.Inject
 *  net.runelite.api.ChatMessageType
 *  net.runelite.api.Client
 *  net.runelite.api.MenuEntry
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package net.runelite.client.plugins.groundmarkers;

import com.google.common.base.Strings;
import com.google.common.util.concurrent.Runnables;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.inject.Inject;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.MenuEntry;
import net.runelite.client.chat.ChatMessageManager;
import net.runelite.client.chat.QueuedMessage;
import net.runelite.client.game.chatbox.ChatboxPanelManager;
import net.runelite.client.menus.MenuManager;
import net.runelite.client.menus.WidgetMenuOption;
import net.runelite.client.plugins.groundmarkers.GroundMarkerPlugin;
import net.runelite.client.plugins.groundmarkers.GroundMarkerPoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class GroundMarkerSharingManager {
    private static final Logger log = LoggerFactory.getLogger(GroundMarkerSharingManager.class);
    private static final WidgetMenuOption EXPORT_MARKERS_OPTION = new WidgetMenuOption("Export", "Ground Markers", 10485813);
    private static final WidgetMenuOption IMPORT_MARKERS_OPTION = new WidgetMenuOption("Import", "Ground Markers", 10485813);
    private static final WidgetMenuOption CLEAR_MARKERS_OPTION = new WidgetMenuOption("Clear", "Ground Markers", 10485813);
    private final GroundMarkerPlugin plugin;
    private final Client client;
    private final MenuManager menuManager;
    private final ChatMessageManager chatMessageManager;
    private final ChatboxPanelManager chatboxPanelManager;
    private final Gson gson;

    @Inject
    private GroundMarkerSharingManager(GroundMarkerPlugin plugin, Client client, MenuManager menuManager, ChatMessageManager chatMessageManager, ChatboxPanelManager chatboxPanelManager, Gson gson) {
        this.plugin = plugin;
        this.client = client;
        this.menuManager = menuManager;
        this.chatMessageManager = chatMessageManager;
        this.chatboxPanelManager = chatboxPanelManager;
        this.gson = gson;
    }

    void addImportExportMenuOptions() {
        this.menuManager.addManagedCustomMenu(EXPORT_MARKERS_OPTION, this::exportGroundMarkers);
        this.menuManager.addManagedCustomMenu(IMPORT_MARKERS_OPTION, this::promptForImport);
    }

    void addClearMenuOption() {
        this.menuManager.addManagedCustomMenu(CLEAR_MARKERS_OPTION, this::promptForClear);
    }

    void removeMenuOptions() {
        this.menuManager.removeManagedCustomMenu(EXPORT_MARKERS_OPTION);
        this.menuManager.removeManagedCustomMenu(IMPORT_MARKERS_OPTION);
        this.menuManager.removeManagedCustomMenu(CLEAR_MARKERS_OPTION);
    }

    private void exportGroundMarkers(MenuEntry menuEntry) {
        int[] regions = this.client.getMapRegions();
        if (regions == null) {
            return;
        }
        List activePoints = Arrays.stream(regions).mapToObj(regionId -> this.plugin.getPoints(regionId).stream()).flatMap(Function.identity()).collect(Collectors.toList());
        if (activePoints.isEmpty()) {
            this.sendChatMessage("You have no ground markers to export.");
            return;
        }
        String exportDump = this.gson.toJson(activePoints);
        log.debug("Exported ground markers: {}", (Object)exportDump);
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(exportDump), null);
        this.sendChatMessage(activePoints.size() + " ground markers were copied to your clipboard.");
    }

    private void promptForImport(MenuEntry menuEntry) {
        List importPoints;
        String clipboardText;
        try {
            clipboardText = Toolkit.getDefaultToolkit().getSystemClipboard().getData(DataFlavor.stringFlavor).toString();
        }
        catch (UnsupportedFlavorException | IOException ex) {
            this.sendChatMessage("Unable to read system clipboard.");
            log.warn("error reading clipboard", (Throwable)ex);
            return;
        }
        log.debug("Clipboard contents: {}", (Object)clipboardText);
        if (Strings.isNullOrEmpty((String)clipboardText)) {
            this.sendChatMessage("You do not have any ground markers copied in your clipboard.");
            return;
        }
        try {
            importPoints = (List)this.gson.fromJson(clipboardText, new TypeToken<List<GroundMarkerPoint>>(){}.getType());
        }
        catch (JsonSyntaxException e) {
            log.debug("Malformed JSON for clipboard import", (Throwable)e);
            this.sendChatMessage("You do not have any ground markers copied in your clipboard.");
            return;
        }
        if (importPoints.isEmpty()) {
            this.sendChatMessage("You do not have any ground markers copied in your clipboard.");
            return;
        }
        this.chatboxPanelManager.openTextMenuInput("Are you sure you want to import " + importPoints.size() + " ground markers?").option("Yes", () -> this.importGroundMarkers(importPoints)).option("No", Runnables.doNothing()).build();
    }

    private void importGroundMarkers(Collection<GroundMarkerPoint> importPoints) {
        Map<Integer, List<GroundMarkerPoint>> regionGroupedPoints = importPoints.stream().collect(Collectors.groupingBy(GroundMarkerPoint::getRegionId));
        regionGroupedPoints.forEach((regionId, groupedPoints) -> {
            log.debug("Importing {} points to region {}", (Object)groupedPoints.size(), regionId);
            Collection<GroundMarkerPoint> regionPoints = this.plugin.getPoints((int)regionId);
            ArrayList<GroundMarkerPoint> mergedList = new ArrayList<GroundMarkerPoint>(regionPoints.size() + groupedPoints.size());
            mergedList.addAll(regionPoints);
            for (GroundMarkerPoint point : groupedPoints) {
                if (mergedList.contains(point)) continue;
                mergedList.add(point);
            }
            this.plugin.savePoints((int)regionId, (Collection<GroundMarkerPoint>)mergedList);
        });
        log.debug("Reloading points after import");
        this.plugin.loadPoints();
        this.sendChatMessage(importPoints.size() + " ground markers were imported from the clipboard.");
    }

    private void promptForClear(MenuEntry entry) {
        int[] regions = this.client.getMapRegions();
        if (regions == null) {
            return;
        }
        long numActivePoints = Arrays.stream(regions).mapToLong(regionId -> this.plugin.getPoints(regionId).size()).sum();
        if (numActivePoints == 0L) {
            this.sendChatMessage("You have no ground markers to clear.");
            return;
        }
        this.chatboxPanelManager.openTextMenuInput("Are you sure you want to clear the<br>" + numActivePoints + " currently loaded ground markers?").option("Yes", () -> {
            for (int regionId : regions) {
                this.plugin.savePoints(regionId, null);
            }
            this.plugin.loadPoints();
            this.sendChatMessage(numActivePoints + " ground marker" + (numActivePoints == 1L ? " was cleared." : "s were cleared."));
        }).option("No", Runnables.doNothing()).build();
    }

    private void sendChatMessage(String message) {
        this.chatMessageManager.queue(QueuedMessage.builder().type(ChatMessageType.CONSOLE).runeLiteFormattedMessage(message).build());
    }
}

