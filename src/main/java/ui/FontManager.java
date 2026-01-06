/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.ui;

import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.GraphicsEnvironment;
import java.io.IOException;
import java.io.InputStream;
import javax.swing.text.StyleContext;

public class FontManager {
    private static final Font runescapeFont;
    private static final Font runescapeSmallFont;
    private static final Font runescapeBoldFont;
    private static final Font defaultFont;
    private static final Font defaultBoldFont;

    public static Font getRunescapeFont() {
        return runescapeFont;
    }

    public static Font getRunescapeSmallFont() {
        return runescapeSmallFont;
    }

    public static Font getRunescapeBoldFont() {
        return runescapeBoldFont;
    }

    public static Font getDefaultFont() {
        return defaultFont;
    }

    public static Font getDefaultBoldFont() {
        return defaultBoldFont;
    }

    static {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        try (InputStream inRunescape = FontManager.class.getResourceAsStream("runescape.ttf");
             InputStream inRunescapeSmall = FontManager.class.getResourceAsStream("runescape_small.ttf");
             InputStream inRunescapeBold = FontManager.class.getResourceAsStream("runescape_bold.ttf");){
            Font font = Font.createFont(0, inRunescape).deriveFont(0, 16.0f);
            ge.registerFont(font);
            runescapeFont = StyleContext.getDefaultStyleContext().getFont(font.getName(), 0, 16);
            ge.registerFont(runescapeFont);
            Font smallFont = Font.createFont(0, inRunescapeSmall).deriveFont(0, 16.0f);
            ge.registerFont(smallFont);
            runescapeSmallFont = StyleContext.getDefaultStyleContext().getFont(smallFont.getName(), 0, 16);
            ge.registerFont(runescapeSmallFont);
            Font boldFont = Font.createFont(0, inRunescapeBold).deriveFont(1, 16.0f);
            ge.registerFont(boldFont);
            runescapeBoldFont = StyleContext.getDefaultStyleContext().getFont(boldFont.getName(), 1, 16);
            ge.registerFont(runescapeBoldFont);
        }
        catch (FontFormatException ex) {
            throw new RuntimeException("Font loaded, but format incorrect.", ex);
        }
        catch (IOException ex) {
            throw new RuntimeException("Font file not found.", ex);
        }
        defaultFont = new Font("Dialog", 0, 16);
        defaultBoldFont = new Font("Dialog", 1, 16);
    }
}

