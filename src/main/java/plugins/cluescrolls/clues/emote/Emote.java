/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.plugins.cluescrolls.clues.emote;

public enum Emote {
    BULL_ROARER("Bull Roarer", -1),
    YES("Yes", 700),
    NO("No", 701),
    THINK("Think", 702),
    BOW("Bow", 703),
    ANGRY("Angry", 704),
    CRY("Cry", 705),
    LAUGH("Laugh", 706),
    CHEER("Cheer", 707),
    WAVE("Wave", 708),
    BECKON("Beckon", 709),
    DANCE("Dance", 710),
    CLAP("Clap", 711),
    PANIC("Panic", 712),
    JIG("Jig", 713),
    SPIN("Spin", 714),
    HEADBANG("Headbang", 715),
    JUMP_FOR_JOY("Jump for Joy", 716),
    RASPBERRY("Raspberry", 717),
    YAWN("Yawn", 718),
    SALUTE("Salute", 719),
    SHRUG("Shrug", 720),
    BLOW_KISS("Blow Kiss", 721),
    GOBLIN_SALUTE("Goblin Salute", 727),
    SLAP_HEAD("Slap Head", 729),
    STAMP("Stamp", 730),
    FLAP("Flap", 731),
    PUSH_UP("Push up", 1204),
    FORTIS_SALUTE("Fortis Salute", 5251);

    private final String name;
    private final int spriteId;

    private Emote(String name, int spriteId) {
        this.name = name;
        this.spriteId = spriteId;
    }

    public boolean hasSprite() {
        return this.spriteId != -1;
    }

    public String getName() {
        return this.name;
    }

    public int getSpriteId() {
        return this.spriteId;
    }
}

