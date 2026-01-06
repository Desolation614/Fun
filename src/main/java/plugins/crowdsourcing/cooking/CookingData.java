/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.plugins.crowdsourcing.cooking;

public class CookingData {
    private final String message;
    private final boolean hasCookingGauntlets;
    private final boolean inHosidiusKitchen;
    private final boolean kourendElite;
    private final int lastGameObjectClicked;
    private final int level;

    public String getMessage() {
        return this.message;
    }

    public boolean isHasCookingGauntlets() {
        return this.hasCookingGauntlets;
    }

    public boolean isInHosidiusKitchen() {
        return this.inHosidiusKitchen;
    }

    public boolean isKourendElite() {
        return this.kourendElite;
    }

    public int getLastGameObjectClicked() {
        return this.lastGameObjectClicked;
    }

    public int getLevel() {
        return this.level;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof CookingData)) {
            return false;
        }
        CookingData other = (CookingData)o;
        if (!other.canEqual(this)) {
            return false;
        }
        if (this.isHasCookingGauntlets() != other.isHasCookingGauntlets()) {
            return false;
        }
        if (this.isInHosidiusKitchen() != other.isInHosidiusKitchen()) {
            return false;
        }
        if (this.isKourendElite() != other.isKourendElite()) {
            return false;
        }
        if (this.getLastGameObjectClicked() != other.getLastGameObjectClicked()) {
            return false;
        }
        if (this.getLevel() != other.getLevel()) {
            return false;
        }
        String this$message = this.getMessage();
        String other$message = other.getMessage();
        return !(this$message == null ? other$message != null : !this$message.equals(other$message));
    }

    protected boolean canEqual(Object other) {
        return other instanceof CookingData;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        result = result * 59 + (this.isHasCookingGauntlets() ? 79 : 97);
        result = result * 59 + (this.isInHosidiusKitchen() ? 79 : 97);
        result = result * 59 + (this.isKourendElite() ? 79 : 97);
        result = result * 59 + this.getLastGameObjectClicked();
        result = result * 59 + this.getLevel();
        String $message = this.getMessage();
        result = result * 59 + ($message == null ? 43 : $message.hashCode());
        return result;
    }

    public String toString() {
        return "CookingData(message=" + this.getMessage() + ", hasCookingGauntlets=" + this.isHasCookingGauntlets() + ", inHosidiusKitchen=" + this.isInHosidiusKitchen() + ", kourendElite=" + this.isKourendElite() + ", lastGameObjectClicked=" + this.getLastGameObjectClicked() + ", level=" + this.getLevel() + ")";
    }

    public CookingData(String message, boolean hasCookingGauntlets, boolean inHosidiusKitchen, boolean kourendElite, int lastGameObjectClicked, int level) {
        this.message = message;
        this.hasCookingGauntlets = hasCookingGauntlets;
        this.inHosidiusKitchen = inHosidiusKitchen;
        this.kourendElite = kourendElite;
        this.lastGameObjectClicked = lastGameObjectClicked;
        this.level = level;
    }
}

