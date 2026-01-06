/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.inject.Singleton
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  javax.inject.Inject
 *  net.runelite.api.Client
 *  net.runelite.api.coords.WorldPoint
 *  net.runelite.api.widgets.Widget
 */
package net.runelite.client.plugins.timetracking.farming;

import com.google.inject.Singleton;
import java.time.Instant;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.widgets.Widget;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.timetracking.SummaryState;
import net.runelite.client.plugins.timetracking.Tab;
import net.runelite.client.plugins.timetracking.TimeTrackingConfig;
import net.runelite.client.plugins.timetracking.TimeTrackingPlugin;
import net.runelite.client.plugins.timetracking.farming.CropState;
import net.runelite.client.plugins.timetracking.farming.FarmingContractInfoBox;
import net.runelite.client.plugins.timetracking.farming.FarmingPatch;
import net.runelite.client.plugins.timetracking.farming.FarmingTracker;
import net.runelite.client.plugins.timetracking.farming.FarmingWorld;
import net.runelite.client.plugins.timetracking.farming.PatchImplementation;
import net.runelite.client.plugins.timetracking.farming.PatchPrediction;
import net.runelite.client.plugins.timetracking.farming.Produce;
import net.runelite.client.ui.overlay.infobox.InfoBoxManager;
import net.runelite.client.util.Text;

@Singleton
public class FarmingContractManager {
    private static final int GUILDMASTER_JANE_NPC_ID = 8628;
    private static final int FARMING_GUILD_REGION_ID = 4922;
    private static final Pattern CONTRACT_ASSIGN_PATTERN = Pattern.compile("(?:We need you to grow|Please could you grow) (?:some|a|an) ([a-zA-Z ]+)(?: for us\\?|\\.)");
    private static final String CONTRACT_REWARDED = "You'll be wanting a reward then. Here you go.";
    private static final String CONFIG_KEY_CONTRACT = "contract";
    private SummaryState summary = SummaryState.UNKNOWN;
    private CropState contractCropState;
    @Inject
    private Client client;
    @Inject
    private ItemManager itemManager;
    @Inject
    private TimeTrackingConfig config;
    @Inject
    private TimeTrackingPlugin plugin;
    @Inject
    private FarmingWorld farmingWorld;
    @Inject
    private FarmingTracker farmingTracker;
    @Inject
    private InfoBoxManager infoBoxManager;
    @Inject
    private ConfigManager configManager;
    private Produce contract = null;
    private FarmingContractInfoBox infoBox;
    private long completionTime;

    public void setContract(@Nullable Produce contract) {
        this.contract = contract;
        this.setStoredContract(contract);
        this.handleContractState();
    }

    public boolean hasContract() {
        return this.contract != null;
    }

    @Nullable
    public Tab getContractTab() {
        return this.hasContract() ? this.contract.getPatchImplementation().getTab() : null;
    }

    @Nullable
    public String getContractName() {
        return this.hasContract() ? this.contract.getContractName() : null;
    }

    public boolean shouldHighlightFarmingTabPanel(@Nonnull FarmingPatch patch) {
        PatchPrediction patchPrediction = this.farmingTracker.predictPatch(patch);
        return this.contract != null && patch.getRegion().getRegionID() == 4922 && this.contract.getPatchImplementation() == patch.getImplementation() && patchPrediction != null && (this.summary == SummaryState.EMPTY && (patchPrediction.getProduce() == null || patchPrediction.getProduce() == Produce.WEEDS) || patchPrediction.getProduce().equals((Object)this.contract));
    }

    public void loadContractFromConfig() {
        this.contract = this.getStoredContract();
        this.handleContractState();
    }

    public boolean updateData(WorldPoint loc) {
        SummaryState oldSummary = this.summary;
        this.handleContractState();
        if (loc.getRegionID() == 4922) {
            this.handleGuildmasterJaneWidgetDialog();
            this.handleInfoBox();
        } else if (this.infoBox != null) {
            this.infoBoxManager.removeInfoBox(this.infoBox);
            this.infoBox = null;
        }
        return oldSummary != this.summary;
    }

    private void handleInfoBox() {
        if (this.contract != (this.infoBox == null ? null : this.infoBox.getContract())) {
            if (this.infoBox != null) {
                this.infoBoxManager.removeInfoBox(this.infoBox);
                this.infoBox = null;
            }
            if (this.contract != null) {
                this.infoBox = new FarmingContractInfoBox(this.itemManager.getImage(this.contract.getItemID()), this.plugin, this.contract, this.config, this);
                this.infoBoxManager.addInfoBox(this.infoBox);
            }
        }
    }

    private void handleGuildmasterJaneWidgetDialog() {
        Matcher matcher;
        Widget npcDialog = this.client.getWidget(15138818);
        if (npcDialog == null || npcDialog.getModelId() != 8628) {
            return;
        }
        String dialogText = Text.removeTags(this.client.getWidget(15138822).getText());
        if (dialogText.equals(CONTRACT_REWARDED)) {
            this.setContract(null);
        }
        if (!(matcher = CONTRACT_ASSIGN_PATTERN.matcher(dialogText)).find()) {
            return;
        }
        String name = matcher.group(1);
        Produce farmingContract = Produce.getByContractName(name);
        if (farmingContract == null) {
            return;
        }
        Produce currentFarmingContract = this.contract;
        if (farmingContract == currentFarmingContract) {
            return;
        }
        this.setContract(farmingContract);
    }

    private void handleContractState() {
        if (this.contract == null) {
            this.summary = SummaryState.UNKNOWN;
            return;
        }
        PatchImplementation patchImplementation = this.contract.getPatchImplementation();
        boolean hasEmptyPatch = false;
        boolean hasDiseasedPatch = false;
        boolean hasDeadPatch = false;
        this.completionTime = Long.MAX_VALUE;
        this.contractCropState = null;
        for (FarmingPatch patch : this.farmingWorld.getFarmingGuildRegion().getPatches()) {
            PatchPrediction prediction;
            if (patch.getImplementation() != patchImplementation || (prediction = this.farmingTracker.predictPatch(patch)) == null) continue;
            Produce produce = prediction.getProduce();
            CropState state = prediction.getCropState();
            if (this.completionTime == Long.MAX_VALUE) {
                if (produce == null || produce == Produce.WEEDS) {
                    if (!hasDiseasedPatch && !hasDeadPatch) {
                        this.summary = SummaryState.EMPTY;
                    }
                    hasEmptyPatch = true;
                    continue;
                }
                if (this.contract.getPatchImplementation().isHealthCheckRequired() && state == CropState.HARVESTABLE && !hasEmptyPatch && !hasDiseasedPatch && !hasDeadPatch) {
                    this.summary = SummaryState.OCCUPIED;
                    continue;
                }
            }
            if (produce != this.contract && produce != Produce.ANYHERB) {
                if (hasEmptyPatch || hasDiseasedPatch || hasDeadPatch || this.completionTime != Long.MAX_VALUE) continue;
                this.summary = SummaryState.OCCUPIED;
                continue;
            }
            if (state == CropState.DEAD && (hasDiseasedPatch || this.completionTime != Long.MAX_VALUE) || state == CropState.DISEASED && this.completionTime != Long.MAX_VALUE) continue;
            this.contractCropState = state;
            if (this.contractCropState == CropState.DISEASED) {
                hasDiseasedPatch = true;
                this.summary = SummaryState.IN_PROGRESS;
                continue;
            }
            if (this.contractCropState == CropState.DEAD) {
                hasDeadPatch = true;
                this.summary = SummaryState.IN_PROGRESS;
                continue;
            }
            long estimatedTime = Math.min(prediction.getDoneEstimate(), this.completionTime);
            if (estimatedTime <= Instant.now().getEpochSecond()) {
                this.summary = SummaryState.COMPLETED;
                this.completionTime = 0L;
                break;
            }
            this.summary = SummaryState.IN_PROGRESS;
            this.completionTime = estimatedTime;
        }
    }

    @Nullable
    private Produce getStoredContract() {
        try {
            return Produce.getByItemID(Integer.parseInt(this.configManager.getRSProfileConfiguration("timetracking", CONFIG_KEY_CONTRACT)));
        }
        catch (NumberFormatException ignored) {
            return null;
        }
    }

    private void setStoredContract(@Nullable Produce contract) {
        if (contract != null) {
            this.configManager.setRSProfileConfiguration("timetracking", CONFIG_KEY_CONTRACT, String.valueOf(contract.getItemID()));
        } else {
            this.configManager.unsetRSProfileConfiguration("timetracking", CONFIG_KEY_CONTRACT);
        }
    }

    public SummaryState getSummary() {
        return this.summary;
    }

    public CropState getContractCropState() {
        return this.contractCropState;
    }

    public Produce getContract() {
        return this.contract;
    }

    public FarmingContractInfoBox getInfoBox() {
        return this.infoBox;
    }

    public void setInfoBox(FarmingContractInfoBox infoBox) {
        this.infoBox = infoBox;
    }

    public long getCompletionTime() {
        return this.completionTime;
    }
}

