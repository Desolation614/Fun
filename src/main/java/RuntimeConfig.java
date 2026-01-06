/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Strings
 *  com.google.gson.JsonArray
 */
package net.runelite.client;

import com.google.common.base.Strings;
import com.google.gson.JsonArray;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import javax.swing.SwingUtilities;
import net.runelite.client.RuneLiteProperties;
import net.runelite.client.ui.FatalErrorDialog;
import net.runelite.client.util.LinkBrowser;

public class RuntimeConfig {
    private Map<String, ?> props = Collections.emptyMap();
    private Map<String, String> sysProps = Collections.emptyMap();
    private JsonArray compilerControl;
    private String outageMessage;
    private Map<String, String> outageLinks;
    private Instant outageStart;
    private Instant outageEnd;
    private Set<Integer> ignoreDeadNpcs;
    private Set<Integer> forceDeadNpcs;
    private Set<Integer> resetDeadOnChangeNpcs;
    private Set<Integer> forceDeadAnimations;
    private Set<Integer> healthCheckDeadNpcs;
    private Set<String> outdatedClientVersions;
    private String[] updateLauncherWinVers;
    private double updateRollout;

    public boolean showOutageMessage() {
        if (Strings.isNullOrEmpty((String)this.getOutageMessage()) || this.outageStart != null && Instant.now().isBefore(this.outageStart) || this.outageEnd != null && Instant.now().isAfter(this.outageEnd)) {
            return false;
        }
        SwingUtilities.invokeLater(() -> {
            FatalErrorDialog fed = new FatalErrorDialog(this.getOutageMessage());
            if (this.getOutageLinks() != null) {
                for (Map.Entry<String, String> e : this.getOutageLinks().entrySet()) {
                    fed.addButton(e.getKey(), () -> LinkBrowser.browse((String)e.getValue()));
                }
            } else {
                fed.addButton("OSRS Twitter", () -> LinkBrowser.browse(RuneLiteProperties.getOSRSTwitterLink()));
            }
            fed.open();
        });
        return true;
    }

    void refresh(RuntimeConfig config) {
        this.ignoreDeadNpcs = config.ignoreDeadNpcs;
        this.forceDeadNpcs = config.forceDeadNpcs;
        this.resetDeadOnChangeNpcs = config.resetDeadOnChangeNpcs;
        this.forceDeadAnimations = config.forceDeadAnimations;
        this.healthCheckDeadNpcs = config.healthCheckDeadNpcs;
        this.outdatedClientVersions = config.outdatedClientVersions;
    }

    public Map<String, ?> getProps() {
        return this.props;
    }

    public Map<String, String> getSysProps() {
        return this.sysProps;
    }

    public JsonArray getCompilerControl() {
        return this.compilerControl;
    }

    public String getOutageMessage() {
        return this.outageMessage;
    }

    public Map<String, String> getOutageLinks() {
        return this.outageLinks;
    }

    public Instant getOutageStart() {
        return this.outageStart;
    }

    public Instant getOutageEnd() {
        return this.outageEnd;
    }

    public Set<Integer> getIgnoreDeadNpcs() {
        return this.ignoreDeadNpcs;
    }

    public Set<Integer> getForceDeadNpcs() {
        return this.forceDeadNpcs;
    }

    public Set<Integer> getResetDeadOnChangeNpcs() {
        return this.resetDeadOnChangeNpcs;
    }

    public Set<Integer> getForceDeadAnimations() {
        return this.forceDeadAnimations;
    }

    public Set<Integer> getHealthCheckDeadNpcs() {
        return this.healthCheckDeadNpcs;
    }

    public Set<String> getOutdatedClientVersions() {
        return this.outdatedClientVersions;
    }

    public String[] getUpdateLauncherWinVers() {
        return this.updateLauncherWinVers;
    }

    public double getUpdateRollout() {
        return this.updateRollout;
    }

    public void setProps(Map<String, ?> props) {
        this.props = props;
    }

    public void setSysProps(Map<String, String> sysProps) {
        this.sysProps = sysProps;
    }

    public void setCompilerControl(JsonArray compilerControl) {
        this.compilerControl = compilerControl;
    }

    public void setOutageMessage(String outageMessage) {
        this.outageMessage = outageMessage;
    }

    public void setOutageLinks(Map<String, String> outageLinks) {
        this.outageLinks = outageLinks;
    }

    public void setOutageStart(Instant outageStart) {
        this.outageStart = outageStart;
    }

    public void setOutageEnd(Instant outageEnd) {
        this.outageEnd = outageEnd;
    }

    public void setIgnoreDeadNpcs(Set<Integer> ignoreDeadNpcs) {
        this.ignoreDeadNpcs = ignoreDeadNpcs;
    }

    public void setForceDeadNpcs(Set<Integer> forceDeadNpcs) {
        this.forceDeadNpcs = forceDeadNpcs;
    }

    public void setResetDeadOnChangeNpcs(Set<Integer> resetDeadOnChangeNpcs) {
        this.resetDeadOnChangeNpcs = resetDeadOnChangeNpcs;
    }

    public void setForceDeadAnimations(Set<Integer> forceDeadAnimations) {
        this.forceDeadAnimations = forceDeadAnimations;
    }

    public void setHealthCheckDeadNpcs(Set<Integer> healthCheckDeadNpcs) {
        this.healthCheckDeadNpcs = healthCheckDeadNpcs;
    }

    public void setOutdatedClientVersions(Set<String> outdatedClientVersions) {
        this.outdatedClientVersions = outdatedClientVersions;
    }

    public void setUpdateLauncherWinVers(String[] updateLauncherWinVers) {
        this.updateLauncherWinVers = updateLauncherWinVers;
    }

    public void setUpdateRollout(double updateRollout) {
        this.updateRollout = updateRollout;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof RuntimeConfig)) {
            return false;
        }
        RuntimeConfig other = (RuntimeConfig)o;
        if (!other.canEqual(this)) {
            return false;
        }
        if (Double.compare(this.getUpdateRollout(), other.getUpdateRollout()) != 0) {
            return false;
        }
        Map<String, ?> this$props = this.getProps();
        Map<String, ?> other$props = other.getProps();
        if (this$props == null ? other$props != null : !((Object)this$props).equals(other$props)) {
            return false;
        }
        Map<String, String> this$sysProps = this.getSysProps();
        Map<String, String> other$sysProps = other.getSysProps();
        if (this$sysProps == null ? other$sysProps != null : !((Object)this$sysProps).equals(other$sysProps)) {
            return false;
        }
        JsonArray this$compilerControl = this.getCompilerControl();
        JsonArray other$compilerControl = other.getCompilerControl();
        if (this$compilerControl == null ? other$compilerControl != null : !this$compilerControl.equals(other$compilerControl)) {
            return false;
        }
        String this$outageMessage = this.getOutageMessage();
        String other$outageMessage = other.getOutageMessage();
        if (this$outageMessage == null ? other$outageMessage != null : !this$outageMessage.equals(other$outageMessage)) {
            return false;
        }
        Map<String, String> this$outageLinks = this.getOutageLinks();
        Map<String, String> other$outageLinks = other.getOutageLinks();
        if (this$outageLinks == null ? other$outageLinks != null : !((Object)this$outageLinks).equals(other$outageLinks)) {
            return false;
        }
        Instant this$outageStart = this.getOutageStart();
        Instant other$outageStart = other.getOutageStart();
        if (this$outageStart == null ? other$outageStart != null : !((Object)this$outageStart).equals(other$outageStart)) {
            return false;
        }
        Instant this$outageEnd = this.getOutageEnd();
        Instant other$outageEnd = other.getOutageEnd();
        if (this$outageEnd == null ? other$outageEnd != null : !((Object)this$outageEnd).equals(other$outageEnd)) {
            return false;
        }
        Set<Integer> this$ignoreDeadNpcs = this.getIgnoreDeadNpcs();
        Set<Integer> other$ignoreDeadNpcs = other.getIgnoreDeadNpcs();
        if (this$ignoreDeadNpcs == null ? other$ignoreDeadNpcs != null : !((Object)this$ignoreDeadNpcs).equals(other$ignoreDeadNpcs)) {
            return false;
        }
        Set<Integer> this$forceDeadNpcs = this.getForceDeadNpcs();
        Set<Integer> other$forceDeadNpcs = other.getForceDeadNpcs();
        if (this$forceDeadNpcs == null ? other$forceDeadNpcs != null : !((Object)this$forceDeadNpcs).equals(other$forceDeadNpcs)) {
            return false;
        }
        Set<Integer> this$resetDeadOnChangeNpcs = this.getResetDeadOnChangeNpcs();
        Set<Integer> other$resetDeadOnChangeNpcs = other.getResetDeadOnChangeNpcs();
        if (this$resetDeadOnChangeNpcs == null ? other$resetDeadOnChangeNpcs != null : !((Object)this$resetDeadOnChangeNpcs).equals(other$resetDeadOnChangeNpcs)) {
            return false;
        }
        Set<Integer> this$forceDeadAnimations = this.getForceDeadAnimations();
        Set<Integer> other$forceDeadAnimations = other.getForceDeadAnimations();
        if (this$forceDeadAnimations == null ? other$forceDeadAnimations != null : !((Object)this$forceDeadAnimations).equals(other$forceDeadAnimations)) {
            return false;
        }
        Set<Integer> this$healthCheckDeadNpcs = this.getHealthCheckDeadNpcs();
        Set<Integer> other$healthCheckDeadNpcs = other.getHealthCheckDeadNpcs();
        if (this$healthCheckDeadNpcs == null ? other$healthCheckDeadNpcs != null : !((Object)this$healthCheckDeadNpcs).equals(other$healthCheckDeadNpcs)) {
            return false;
        }
        Set<String> this$outdatedClientVersions = this.getOutdatedClientVersions();
        Set<String> other$outdatedClientVersions = other.getOutdatedClientVersions();
        if (this$outdatedClientVersions == null ? other$outdatedClientVersions != null : !((Object)this$outdatedClientVersions).equals(other$outdatedClientVersions)) {
            return false;
        }
        return Arrays.deepEquals(this.getUpdateLauncherWinVers(), other.getUpdateLauncherWinVers());
    }

    protected boolean canEqual(Object other) {
        return other instanceof RuntimeConfig;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        long $updateRollout = Double.doubleToLongBits(this.getUpdateRollout());
        result = result * 59 + (int)($updateRollout >>> 32 ^ $updateRollout);
        Map<String, ?> $props = this.getProps();
        result = result * 59 + ($props == null ? 43 : ((Object)$props).hashCode());
        Map<String, String> $sysProps = this.getSysProps();
        result = result * 59 + ($sysProps == null ? 43 : ((Object)$sysProps).hashCode());
        JsonArray $compilerControl = this.getCompilerControl();
        result = result * 59 + ($compilerControl == null ? 43 : $compilerControl.hashCode());
        String $outageMessage = this.getOutageMessage();
        result = result * 59 + ($outageMessage == null ? 43 : $outageMessage.hashCode());
        Map<String, String> $outageLinks = this.getOutageLinks();
        result = result * 59 + ($outageLinks == null ? 43 : ((Object)$outageLinks).hashCode());
        Instant $outageStart = this.getOutageStart();
        result = result * 59 + ($outageStart == null ? 43 : ((Object)$outageStart).hashCode());
        Instant $outageEnd = this.getOutageEnd();
        result = result * 59 + ($outageEnd == null ? 43 : ((Object)$outageEnd).hashCode());
        Set<Integer> $ignoreDeadNpcs = this.getIgnoreDeadNpcs();
        result = result * 59 + ($ignoreDeadNpcs == null ? 43 : ((Object)$ignoreDeadNpcs).hashCode());
        Set<Integer> $forceDeadNpcs = this.getForceDeadNpcs();
        result = result * 59 + ($forceDeadNpcs == null ? 43 : ((Object)$forceDeadNpcs).hashCode());
        Set<Integer> $resetDeadOnChangeNpcs = this.getResetDeadOnChangeNpcs();
        result = result * 59 + ($resetDeadOnChangeNpcs == null ? 43 : ((Object)$resetDeadOnChangeNpcs).hashCode());
        Set<Integer> $forceDeadAnimations = this.getForceDeadAnimations();
        result = result * 59 + ($forceDeadAnimations == null ? 43 : ((Object)$forceDeadAnimations).hashCode());
        Set<Integer> $healthCheckDeadNpcs = this.getHealthCheckDeadNpcs();
        result = result * 59 + ($healthCheckDeadNpcs == null ? 43 : ((Object)$healthCheckDeadNpcs).hashCode());
        Set<String> $outdatedClientVersions = this.getOutdatedClientVersions();
        result = result * 59 + ($outdatedClientVersions == null ? 43 : ((Object)$outdatedClientVersions).hashCode());
        result = result * 59 + Arrays.deepHashCode(this.getUpdateLauncherWinVers());
        return result;
    }

    public String toString() {
        return "RuntimeConfig(props=" + String.valueOf(this.getProps()) + ", sysProps=" + String.valueOf(this.getSysProps()) + ", compilerControl=" + String.valueOf(this.getCompilerControl()) + ", outageMessage=" + this.getOutageMessage() + ", outageLinks=" + String.valueOf(this.getOutageLinks()) + ", outageStart=" + String.valueOf(this.getOutageStart()) + ", outageEnd=" + String.valueOf(this.getOutageEnd()) + ", ignoreDeadNpcs=" + String.valueOf(this.getIgnoreDeadNpcs()) + ", forceDeadNpcs=" + String.valueOf(this.getForceDeadNpcs()) + ", resetDeadOnChangeNpcs=" + String.valueOf(this.getResetDeadOnChangeNpcs()) + ", forceDeadAnimations=" + String.valueOf(this.getForceDeadAnimations()) + ", healthCheckDeadNpcs=" + String.valueOf(this.getHealthCheckDeadNpcs()) + ", outdatedClientVersions=" + String.valueOf(this.getOutdatedClientVersions()) + ", updateLauncherWinVers=" + Arrays.deepToString(this.getUpdateLauncherWinVers()) + ", updateRollout=" + this.getUpdateRollout() + ")";
    }
}

