/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap$Builder
 */
package net.runelite.client.plugins.emojis;

import com.google.common.collect.ImmutableMap;
import java.util.Map;

enum Emoji {
    SLIGHT_SMILE(128578, ":)"),
    JOY(128514, "=')"),
    COWBOY(129312, "3:)"),
    BLUSH(128522, "^_^"),
    SMILE(128513, ":D"),
    GRINNING(128512, "=D"),
    WINK(128521, ";)"),
    STUCK_OUT_TONGUE_CLOSED_EYES(128541, "X-P"),
    STUCK_OUT_TONGUE(128539, ":P"),
    YUM(128523, "=P~"),
    HUGGING(129303, "<gt>:D<lt>"),
    TRIUMPH(128548, ":<gt>"),
    THINKING(129300, ":-?"),
    CONFUSED(128533, ":/"),
    NEUTRAL_FACE(128528, "=|"),
    EXPRESSIONLESS(128529, ":|"),
    UNAMUSED(128530, ":-|"),
    SLIGHT_FROWN(128577, ":("),
    FROWNING2(9785, "=("),
    CRY(128546, ":'("),
    SOB(128557, ":_("),
    FLUSHED(128563, ":$"),
    ZIPPER_MOUTH(129296, ":-#"),
    PERSEVERE(128547, "<gt>_<lt>"),
    SUNGLASSES(128526, "8-)"),
    INNOCENT(128519, "O:)"),
    SMILING_IMP(128520, "<gt>:)"),
    RAGE(128545, "<gt>:("),
    HUSHED(128559, ":-O"),
    OPEN_MOUTH(128558, ":O"),
    SCREAM(128561, ":-@"),
    SEE_NO_EVIL(128584, "X_X"),
    DANCER(128131, "\\:D/"),
    OK_HAND(128076, "(Ok)"),
    THUMBSUP(128077, "(Y)"),
    THUMBSDOWN(128078, "(N)"),
    HEARTS(10084, "<lt>3"),
    BROKEN_HEART(128148, "<lt>/3"),
    ZZZ(128164, "Zzz"),
    FISH(128031, "<lt><gt><lt>"),
    CAT(128570, ":3"),
    DOG(128054, "=3"),
    CRAB(129408, "V(;,;)V"),
    FORK_AND_KNIFE(127860, "--E"),
    COOKING(127859, "--(o)"),
    PARTY_POPPER(127881, "@@@"),
    EYES(128064, "O.O"),
    SWEAT(128166, ";;"),
    PILE_OF_POO(128169, "~@~"),
    FIRE(128293, "(/\\)"),
    ALIEN(128125, "(@.@)"),
    EGGPLANT(127814, "8=D"),
    WAVE(128075, "(^_^)/"),
    HEART_EYES(128525, "(*.*)"),
    FACEPALM(129318, "M-)"),
    PENSIVE(128532, "V_V"),
    ACORN(127792, "<lt>D~"),
    GORILLA(129421, ":G"),
    PLEADING(129402, "(n_n)"),
    XD(128518, "Xd"),
    SPOON(129348, "--o"),
    WEARY_FACE(128553, "Dx"),
    ROCKETSHIP(128640, "<gt>==<gt>"),
    CLOWN(129313, ":O)"),
    COW(128046, "3:O"),
    HANDSHAKE(129309, "(=)"),
    RABBIT(128048, "=:3");

    private static final Map<String, Emoji> emojiMap;
    private final String trigger;
    final int codepoint;

    private Emoji(int codepoint, String shortName) {
        this.trigger = shortName;
        this.codepoint = codepoint;
    }

    static Emoji getEmoji(String trigger) {
        return emojiMap.get(trigger);
    }

    static {
        ImmutableMap.Builder builder = new ImmutableMap.Builder();
        for (Emoji emoji : Emoji.values()) {
            builder.put((Object)emoji.trigger, (Object)emoji);
        }
        emojiMap = builder.build();
    }
}

