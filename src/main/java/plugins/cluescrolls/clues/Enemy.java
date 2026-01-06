/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.plugins.cluescrolls.clues;

public enum Enemy {
    DOUBLE_AGENT_65("Double Agent level 65"),
    DOUBLE_AGENT_108("Double Agent level 108"),
    DOUBLE_AGENT_141("Double Agent level 141"),
    ZAMORAK_WIZARD("Zamorak Wizard"),
    SARADOMIN_WIZARD("Saradomin Wizard"),
    ARMADYLEAN_OR_BANDOSIAN_GUARD("Armadylean OR Bandosian Guard"),
    ARMADYLEAN_GUARD("Armadylean Guard"),
    BANDOSIAN_GUARD("Bandosian Guard"),
    BRASSICAN_MAGE("Brassican Mage"),
    ANCIENT_WIZARDS("Ancient Wizard Trio"),
    BRASSICAN_OR_WIZARDS("Brassican Mage OR Ancient Wizards");

    private final String text;

    private Enemy(String text) {
        this.text = text;
    }

    public String getText() {
        return this.text;
    }
}

