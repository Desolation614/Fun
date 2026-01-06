/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.formdev.flatlaf.FlatDarkLaf
 *  com.formdev.flatlaf.FlatLaf
 */
package net.runelite.client.ui.laf;

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLaf;
import java.awt.Color;
import java.awt.RenderingHints;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import javax.swing.LookAndFeel;
import javax.swing.UIDefaults;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.FontManager;

public class RuneLiteLAF
extends FlatDarkLaf {
    public static boolean setup() {
        System.setProperty("flatlaf.uiScale.enabled", "false");
        return RuneLiteLAF.setup((LookAndFeel)((Object)new RuneLiteLAF()));
    }

    public RuneLiteLAF() {
        HashMap<Object, String> extras = new HashMap<Object, String>();
        Properties p = new Properties();
        for (Class clazz : new Class[]{FlatLaf.class, FlatDarkLaf.class, RuneLiteLAF.class}) {
            try (InputStream is = clazz.getResourceAsStream("/" + clazz.getName().replace('.', '/') + ".properties");){
                if (is == null) continue;
                p.load(new InputStreamReader(is, StandardCharsets.UTF_8));
            }
        }
        for (Map.Entry entry : p.entrySet()) {
            String k = (String)entry.getKey();
            String v = (String)entry.getValue();
            if (k.charAt(0) == '[' && !k.startsWith("[style]")) continue;
            extras.put(k, v);
        }
        for (Field f : ColorScheme.class.getDeclaredFields()) {
            if (!Modifier.isStatic(f.getModifiers()) || Color.class != f.getType()) continue;
            String name = f.getName();
            if (name.endsWith("_COLOR")) {
                name = name.substring(0, name.length() - 6);
            }
            Color color = (Color)f.get(null);
            extras.put("@" + name, String.format("#%06x%02x", color.getRGB() & 0xFFFFFF, color.getRGB() >>> 24));
        }
        this.setExtraDefaults(extras);
    }

    protected List<Class<?>> getLafClassesForDefaultsLoading() {
        return Collections.emptyList();
    }

    public String getName() {
        return "RuneLite";
    }

    public String getDescription() {
        return "RuneLite";
    }

    public UIDefaults getDefaults() {
        UIDefaults d = super.getDefaults();
        d.put("defaultFont", FontManager.getRunescapeFont());
        d.put(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_GASP);
        return d;
    }
}

