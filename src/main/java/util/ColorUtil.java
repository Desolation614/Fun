/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.primitives.Ints
 *  javax.annotation.Nonnull
 */
package net.runelite.client.util;

import com.google.common.primitives.Ints;
import java.awt.Color;
import java.util.regex.Pattern;
import javax.annotation.Nonnull;

public class ColorUtil {
    public static final int MAX_RGB_VALUE = 255;
    public static final int MIN_RGB_VALUE = 0;
    private static final String OPENING_COLOR_TAG_START = "<col=";
    private static final String OPENING_COLOR_TAG_END = ">";
    public static final String CLOSING_COLOR_TAG = "</col>";
    private static final Pattern ALPHA_HEX_PATTERN = Pattern.compile("^(#|0x)?[0-9a-fA-F]{7,8}");
    private static final Pattern HEX_PATTERN = Pattern.compile("^(#|0x)?[0-9a-fA-F]{1,8}");

    public static String colorTag(Color color) {
        return OPENING_COLOR_TAG_START + ColorUtil.colorToHexCode(color) + OPENING_COLOR_TAG_END;
    }

    public static String prependColorTag(String str, Color color) {
        return ColorUtil.colorTag(color) + str;
    }

    public static String wrapWithColorTag(String str, Color color) {
        return ColorUtil.prependColorTag(str, color) + CLOSING_COLOR_TAG;
    }

    public static String toHexColor(Color color) {
        return "#" + ColorUtil.colorToHexCode(color);
    }

    public static Color colorLerp(Color a, Color b, double t) {
        double r1 = a.getRed();
        double r2 = b.getRed();
        double g1 = a.getGreen();
        double g2 = b.getGreen();
        double b1 = a.getBlue();
        double b2 = b.getBlue();
        double a1 = a.getAlpha();
        double a2 = b.getAlpha();
        return new Color((int)Math.round(r1 + t * (r2 - r1)), (int)Math.round(g1 + t * (g2 - g1)), (int)Math.round(b1 + t * (b2 - b1)), (int)Math.round(a1 + t * (a2 - a1)));
    }

    public static String colorToHexCode(Color color) {
        return String.format("%06x", color.getRGB() & 0xFFFFFF);
    }

    public static String colorToAlphaHexCode(Color color) {
        return String.format("%08x", color.getRGB());
    }

    public static Color colorWithAlpha(Color color, int alpha) {
        if (color.getAlpha() == alpha) {
            return color;
        }
        alpha = ColorUtil.constrainValue(alpha);
        return new Color(color.getRGB() & 0xFFFFFF | alpha << 24, true);
    }

    public static boolean isAlphaHex(String hex) {
        return ALPHA_HEX_PATTERN.matcher(hex).matches();
    }

    public static boolean isHex(String hex) {
        return HEX_PATTERN.matcher(hex).matches();
    }

    public static int constrainValue(int value) {
        return Ints.constrainToRange((int)value, (int)0, (int)255);
    }

    public static Color fromString(String string) {
        try {
            int i = Integer.decode(string);
            return new Color(i, true);
        }
        catch (NumberFormatException e) {
            return null;
        }
    }

    public static Color fromHex(String hex) {
        if (!((String)hex).startsWith("#") && !((String)hex).startsWith("0x")) {
            hex = "#" + (String)hex;
        }
        if (((String)hex).length() <= 7 && ((String)hex).startsWith("#") || ((String)hex).length() <= 8 && ((String)hex).startsWith("0x")) {
            try {
                return Color.decode((String)hex);
            }
            catch (NumberFormatException e) {
                return null;
            }
        }
        try {
            return new Color(Long.decode((String)hex).intValue(), true);
        }
        catch (NumberFormatException e) {
            return null;
        }
    }

    public static Color fromObject(@Nonnull Object object) {
        int i = object.hashCode();
        float h = (float)(i % 360) / 360.0f;
        return Color.getHSBColor(h, 1.0f, 1.0f);
    }
}

