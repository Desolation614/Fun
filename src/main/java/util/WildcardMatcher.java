/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WildcardMatcher {
    private static final Pattern WILDCARD_PATTERN = Pattern.compile("(?i)[^*]+|(\\*+)");

    public static boolean matches(String pattern, String text) {
        Matcher matcher = WILDCARD_PATTERN.matcher(pattern);
        StringBuffer buffer = new StringBuffer();
        buffer.append("(?i)");
        while (matcher.find()) {
            if (matcher.group(1) != null) {
                matcher.appendReplacement(buffer, ".*");
                continue;
            }
            matcher.appendReplacement(buffer, Matcher.quoteReplacement(Pattern.quote(matcher.group(0))));
        }
        matcher.appendTail(buffer);
        String replaced = buffer.toString();
        return text.matches(replaced);
    }
}

