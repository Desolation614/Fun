/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.CharMatcher
 *  com.google.common.base.Joiner
 *  com.google.common.base.Splitter
 *  org.apache.commons.text.WordUtils
 *  org.apache.commons.text.similarity.JaroWinklerDistance
 */
package net.runelite.client.util;

import com.google.common.base.CharMatcher;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.runelite.client.util.JagexPrintableCharMatcher;
import org.apache.commons.text.WordUtils;
import org.apache.commons.text.similarity.JaroWinklerDistance;

public class Text {
    private static final JaroWinklerDistance DISTANCE = new JaroWinklerDistance();
    private static final Pattern TAG_REGEXP = Pattern.compile("<[^>]*>");
    private static final Splitter COMMA_SPLITTER = Splitter.on((String)",").omitEmptyStrings().trimResults();
    private static final Joiner COMMA_JOINER = Joiner.on((String)",").skipNulls();
    public static final CharMatcher JAGEX_PRINTABLE_CHAR_MATCHER = new JagexPrintableCharMatcher();

    public static List<String> fromCSV(String input) {
        return COMMA_SPLITTER.splitToList((CharSequence)input);
    }

    public static String toCSV(Collection<String> input) {
        return COMMA_JOINER.join(input);
    }

    public static String removeTags(String str) {
        return TAG_REGEXP.matcher(str).replaceAll("");
    }

    public static String removeFormattingTags(String str) {
        StringBuilder sb = new StringBuilder();
        Matcher matcher = TAG_REGEXP.matcher(str);
        while (matcher.find()) {
            String match;
            matcher.appendReplacement(sb, "");
            switch (match = matcher.group(0)) {
                case "<lt>": 
                case "<gt>": {
                    sb.append(match);
                }
            }
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    public static String standardize(String str) {
        return Text.removeTags(str).replace('\u00a0', ' ').trim().toLowerCase();
    }

    public static String toJagexName(String str) {
        return CharMatcher.ascii().retainFrom((CharSequence)str.replaceAll("[\u00a0_-]", " ")).trim();
    }

    public static String sanitizeMultilineText(String str) {
        return Text.removeTags(str.replace("-<br>", "-").replace("<br>", " ").replaceAll("[ ]+", " "));
    }

    public static String escapeJagex(String str) {
        StringBuilder out = new StringBuilder(str.length());
        for (int i = 0; i < str.length(); ++i) {
            char c = str.charAt(i);
            if (c == '<') {
                out.append("<lt>");
                continue;
            }
            if (c == '>') {
                out.append("<gt>");
                continue;
            }
            if (c == '\n') {
                out.append("<br>");
                continue;
            }
            if (c == '\r') continue;
            out.append(c);
        }
        return out.toString();
    }

    public static String sanitize(String name) {
        String cleaned = name.contains("<img") ? name.substring(name.lastIndexOf(62) + 1) : name;
        return cleaned.replace('\u00a0', ' ');
    }

    public static String titleCase(Enum o) {
        String toString = o.toString();
        if (o.name().equals(toString)) {
            return WordUtils.capitalize((String)toString.toLowerCase(), (char[])new char[]{'_'}).replace("_", " ");
        }
        return toString;
    }

    public static boolean matchesSearchTerms(Iterable<String> searchTerms, Collection<String> keywords) {
        for (String term : searchTerms) {
            if (!keywords.stream().noneMatch(t -> t.contains(term) || DISTANCE.apply((CharSequence)t, (CharSequence)term) > 0.9)) continue;
            return false;
        }
        return true;
    }
}

