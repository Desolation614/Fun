/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.runelite.api.MenuAction
 */
package net.runelite.client.plugins.menuentryswapper;

import net.runelite.api.MenuAction;

public enum ShiftWithdrawMode {
    WITHDRAW_1("Withdraw-1", MenuAction.CC_OP, 2, 1),
    WITHDRAW_5("Withdraw-5", MenuAction.CC_OP, 3, 2),
    WITHDRAW_10("Withdraw-10", MenuAction.CC_OP, 4, 3),
    WITHDRAW_X("Withdraw-X", MenuAction.CC_OP, 5, 5),
    WITHDRAW_ALL("Withdraw-All", MenuAction.CC_OP_LOW_PRIORITY, 7, 4),
    WITHDRAW_ALL_BUT_1("Withdraw-All-But-1", MenuAction.CC_OP_LOW_PRIORITY, 8, 4),
    WITHDRAW_PLACEHOLDER("Placeholder", MenuAction.CC_OP_LOW_PRIORITY, 9, 4),
    OFF("Off", MenuAction.UNKNOWN, 0, 0);

    private final String name;
    private final MenuAction menuAction;
    private final int identifier;
    private final int identifierChambersStorageUnit;

    public String toString() {
        return this.name;
    }

    public String getName() {
        return this.name;
    }

    public MenuAction getMenuAction() {
        return this.menuAction;
    }

    public int getIdentifier() {
        return this.identifier;
    }

    public int getIdentifierChambersStorageUnit() {
        return this.identifierChambersStorageUnit;
    }

    private ShiftWithdrawMode(String name, MenuAction menuAction, int identifier, int identifierChambersStorageUnit) {
        this.name = name;
        this.menuAction = menuAction;
        this.identifier = identifier;
        this.identifierChambersStorageUnit = identifierChambersStorageUnit;
    }
}

