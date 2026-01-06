/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.plugins.menuentryswapper;

public enum ShiftDepositMode {
    DEPOSIT_1("Deposit-1", 3, 2, 1, 1),
    DEPOSIT_5("Deposit-5", 4, 3, 3, 2),
    DEPOSIT_10("Deposit-10", 5, 4, 4, 3),
    DEPOSIT_X("Deposit-X", 6, 6, 5, 5),
    DEPOSIT_ALL("Deposit-All", 8, 5, 7, 4),
    EXTRA_OP("Eat/Wield/Etc.", 9, 9, 0, 0),
    OFF("Off", 0, 0, 0, 0);

    private final String name;
    private final int identifier;
    private final int identifierDepositBox;
    private final int identifierGroupStorage;
    private final int identifierChambersStorageUnit;

    public String toString() {
        return this.name;
    }

    public String getName() {
        return this.name;
    }

    public int getIdentifier() {
        return this.identifier;
    }

    public int getIdentifierDepositBox() {
        return this.identifierDepositBox;
    }

    public int getIdentifierGroupStorage() {
        return this.identifierGroupStorage;
    }

    public int getIdentifierChambersStorageUnit() {
        return this.identifierChambersStorageUnit;
    }

    private ShiftDepositMode(String name, int identifier, int identifierDepositBox, int identifierGroupStorage, int identifierChambersStorageUnit) {
        this.name = name;
        this.identifier = identifier;
        this.identifierDepositBox = identifierDepositBox;
        this.identifierGroupStorage = identifierGroupStorage;
        this.identifierChambersStorageUnit = identifierChambersStorageUnit;
    }
}

