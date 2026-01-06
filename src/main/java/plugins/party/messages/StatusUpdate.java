/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.annotations.SerializedName
 */
package net.runelite.client.plugins.party.messages;

import com.google.gson.annotations.SerializedName;
import java.awt.Color;
import net.runelite.client.party.messages.PartyMemberMessage;

public class StatusUpdate
extends PartyMemberMessage {
    @SerializedName(value="n")
    private String characterName = null;
    @SerializedName(value="hc")
    private Integer healthCurrent = null;
    @SerializedName(value="hm")
    private Integer healthMax = null;
    @SerializedName(value="pc")
    private Integer prayerCurrent = null;
    @SerializedName(value="pm")
    private Integer prayerMax = null;
    @SerializedName(value="r")
    private Integer runEnergy = null;
    @SerializedName(value="s")
    private Integer specEnergy = null;
    @SerializedName(value="v")
    private Boolean vengeanceActive = null;
    @SerializedName(value="c")
    private Color memberColor = null;

    public String getCharacterName() {
        return this.characterName;
    }

    public Integer getHealthCurrent() {
        return this.healthCurrent;
    }

    public Integer getHealthMax() {
        return this.healthMax;
    }

    public Integer getPrayerCurrent() {
        return this.prayerCurrent;
    }

    public Integer getPrayerMax() {
        return this.prayerMax;
    }

    public Integer getRunEnergy() {
        return this.runEnergy;
    }

    public Integer getSpecEnergy() {
        return this.specEnergy;
    }

    public Boolean getVengeanceActive() {
        return this.vengeanceActive;
    }

    public Color getMemberColor() {
        return this.memberColor;
    }

    public void setCharacterName(String characterName) {
        this.characterName = characterName;
    }

    public void setHealthCurrent(Integer healthCurrent) {
        this.healthCurrent = healthCurrent;
    }

    public void setHealthMax(Integer healthMax) {
        this.healthMax = healthMax;
    }

    public void setPrayerCurrent(Integer prayerCurrent) {
        this.prayerCurrent = prayerCurrent;
    }

    public void setPrayerMax(Integer prayerMax) {
        this.prayerMax = prayerMax;
    }

    public void setRunEnergy(Integer runEnergy) {
        this.runEnergy = runEnergy;
    }

    public void setSpecEnergy(Integer specEnergy) {
        this.specEnergy = specEnergy;
    }

    public void setVengeanceActive(Boolean vengeanceActive) {
        this.vengeanceActive = vengeanceActive;
    }

    public void setMemberColor(Color memberColor) {
        this.memberColor = memberColor;
    }

    public String toString() {
        return "StatusUpdate(characterName=" + this.getCharacterName() + ", healthCurrent=" + this.getHealthCurrent() + ", healthMax=" + this.getHealthMax() + ", prayerCurrent=" + this.getPrayerCurrent() + ", prayerMax=" + this.getPrayerMax() + ", runEnergy=" + this.getRunEnergy() + ", specEnergy=" + this.getSpecEnergy() + ", vengeanceActive=" + this.getVengeanceActive() + ", memberColor=" + String.valueOf(this.getMemberColor()) + ")";
    }

    public StatusUpdate() {
    }

    public StatusUpdate(String characterName, Integer healthCurrent, Integer healthMax, Integer prayerCurrent, Integer prayerMax, Integer runEnergy, Integer specEnergy, Boolean vengeanceActive, Color memberColor) {
        this.characterName = characterName;
        this.healthCurrent = healthCurrent;
        this.healthMax = healthMax;
        this.prayerCurrent = prayerCurrent;
        this.prayerMax = prayerMax;
        this.runEnergy = runEnergy;
        this.specEnergy = specEnergy;
        this.vengeanceActive = vengeanceActive;
        this.memberColor = memberColor;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof StatusUpdate)) {
            return false;
        }
        StatusUpdate other = (StatusUpdate)o;
        if (!other.canEqual(this)) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        Integer this$healthCurrent = this.getHealthCurrent();
        Integer other$healthCurrent = other.getHealthCurrent();
        if (this$healthCurrent == null ? other$healthCurrent != null : !((Object)this$healthCurrent).equals(other$healthCurrent)) {
            return false;
        }
        Integer this$healthMax = this.getHealthMax();
        Integer other$healthMax = other.getHealthMax();
        if (this$healthMax == null ? other$healthMax != null : !((Object)this$healthMax).equals(other$healthMax)) {
            return false;
        }
        Integer this$prayerCurrent = this.getPrayerCurrent();
        Integer other$prayerCurrent = other.getPrayerCurrent();
        if (this$prayerCurrent == null ? other$prayerCurrent != null : !((Object)this$prayerCurrent).equals(other$prayerCurrent)) {
            return false;
        }
        Integer this$prayerMax = this.getPrayerMax();
        Integer other$prayerMax = other.getPrayerMax();
        if (this$prayerMax == null ? other$prayerMax != null : !((Object)this$prayerMax).equals(other$prayerMax)) {
            return false;
        }
        Integer this$runEnergy = this.getRunEnergy();
        Integer other$runEnergy = other.getRunEnergy();
        if (this$runEnergy == null ? other$runEnergy != null : !((Object)this$runEnergy).equals(other$runEnergy)) {
            return false;
        }
        Integer this$specEnergy = this.getSpecEnergy();
        Integer other$specEnergy = other.getSpecEnergy();
        if (this$specEnergy == null ? other$specEnergy != null : !((Object)this$specEnergy).equals(other$specEnergy)) {
            return false;
        }
        Boolean this$vengeanceActive = this.getVengeanceActive();
        Boolean other$vengeanceActive = other.getVengeanceActive();
        if (this$vengeanceActive == null ? other$vengeanceActive != null : !((Object)this$vengeanceActive).equals(other$vengeanceActive)) {
            return false;
        }
        String this$characterName = this.getCharacterName();
        String other$characterName = other.getCharacterName();
        if (this$characterName == null ? other$characterName != null : !this$characterName.equals(other$characterName)) {
            return false;
        }
        Color this$memberColor = this.getMemberColor();
        Color other$memberColor = other.getMemberColor();
        return !(this$memberColor == null ? other$memberColor != null : !((Object)this$memberColor).equals(other$memberColor));
    }

    protected boolean canEqual(Object other) {
        return other instanceof StatusUpdate;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = super.hashCode();
        Integer $healthCurrent = this.getHealthCurrent();
        result = result * 59 + ($healthCurrent == null ? 43 : ((Object)$healthCurrent).hashCode());
        Integer $healthMax = this.getHealthMax();
        result = result * 59 + ($healthMax == null ? 43 : ((Object)$healthMax).hashCode());
        Integer $prayerCurrent = this.getPrayerCurrent();
        result = result * 59 + ($prayerCurrent == null ? 43 : ((Object)$prayerCurrent).hashCode());
        Integer $prayerMax = this.getPrayerMax();
        result = result * 59 + ($prayerMax == null ? 43 : ((Object)$prayerMax).hashCode());
        Integer $runEnergy = this.getRunEnergy();
        result = result * 59 + ($runEnergy == null ? 43 : ((Object)$runEnergy).hashCode());
        Integer $specEnergy = this.getSpecEnergy();
        result = result * 59 + ($specEnergy == null ? 43 : ((Object)$specEnergy).hashCode());
        Boolean $vengeanceActive = this.getVengeanceActive();
        result = result * 59 + ($vengeanceActive == null ? 43 : ((Object)$vengeanceActive).hashCode());
        String $characterName = this.getCharacterName();
        result = result * 59 + ($characterName == null ? 43 : $characterName.hashCode());
        Color $memberColor = this.getMemberColor();
        result = result * 59 + ($memberColor == null ? 43 : ((Object)$memberColor).hashCode());
        return result;
    }
}

