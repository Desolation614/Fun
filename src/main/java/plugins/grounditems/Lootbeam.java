/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.runelite.api.Animation
 *  net.runelite.api.Client
 *  net.runelite.api.JagexColor
 *  net.runelite.api.Model
 *  net.runelite.api.ModelData
 *  net.runelite.api.RuneLiteObject
 *  net.runelite.api.coords.LocalPoint
 *  net.runelite.api.coords.WorldPoint
 */
package net.runelite.client.plugins.grounditems;

import java.awt.Color;
import java.util.function.Function;
import net.runelite.api.Animation;
import net.runelite.api.Client;
import net.runelite.api.JagexColor;
import net.runelite.api.Model;
import net.runelite.api.ModelData;
import net.runelite.api.RuneLiteObject;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.callback.ClientThread;

class Lootbeam {
    private final RuneLiteObject runeLiteObject;
    private final Client client;
    private final ClientThread clientThread;
    private Color color;
    private Style style;

    private static Function<Lootbeam, Animation> anim(int id) {
        return b -> b.client.loadAnimation(id);
    }

    public Lootbeam(Client client, ClientThread clientThread, WorldPoint worldPoint, Color color, Style style) {
        this.client = client;
        this.clientThread = clientThread;
        this.runeLiteObject = client.createRuneLiteObject();
        this.color = color;
        this.style = style;
        this.update();
        this.runeLiteObject.setShouldLoop(true);
        LocalPoint lp = LocalPoint.fromWorld((Client)client, (WorldPoint)worldPoint);
        this.runeLiteObject.setLocation(lp, client.getPlane());
        this.runeLiteObject.setActive(true);
    }

    public void setColor(Color color) {
        if (this.color != null && this.color.equals(color)) {
            return;
        }
        this.color = color;
        this.update();
    }

    public void setStyle(Style style) {
        if (this.style == style) {
            return;
        }
        this.style = style;
        this.update();
    }

    private void update() {
        this.clientThread.invoke(() -> {
            Model model = this.style.modelSupplier.apply(this);
            if (model == null) {
                return false;
            }
            Animation anim = this.style.animationSupplier.apply(this);
            this.runeLiteObject.setAnimation(anim);
            this.runeLiteObject.setModel(model);
            return true;
        });
    }

    public void remove() {
        this.runeLiteObject.setActive(false);
    }

    public static enum Style {
        LIGHT(l -> l.client.loadModel(5809, new short[]{6371}, new short[]{JagexColor.rgbToHSL((int)l.color.getRGB(), (double)1.0)}), Lootbeam.anim(3101)),
        MODERN(l -> {
            ModelData md = l.client.loadModelData(43330);
            if (md == null) {
                return null;
            }
            short hsl = JagexColor.rgbToHSL((int)l.color.getRGB(), (double)1.0);
            int hue = JagexColor.unpackHue((short)hsl);
            int sat = JagexColor.unpackSaturation((short)hsl);
            int lum = JagexColor.unpackLuminance((short)hsl);
            int satDelta = sat > 2 ? 1 : 0;
            return md.cloneColors().recolor((short)26432, JagexColor.packHSL((int)hue, (int)(sat - satDelta), (int)lum)).recolor((short)26584, JagexColor.packHSL((int)hue, (int)sat, (int)Math.min(lum + 24, 127))).light(139, 2643, -50, -10, -50);
        }, Lootbeam.anim(9260));

        private final Function<Lootbeam, Model> modelSupplier;
        private final Function<Lootbeam, Animation> animationSupplier;

        private Style(Function<Lootbeam, Model> modelSupplier, Function<Lootbeam, Animation> animationSupplier) {
            this.modelSupplier = modelSupplier;
            this.animationSupplier = animationSupplier;
        }
    }
}

