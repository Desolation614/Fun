/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.primitives.Ints
 *  net.runelite.api.Client
 *  net.runelite.api.IndexedSprite
 *  net.runelite.api.SpritePixels
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package net.runelite.client.util;

import com.google.common.primitives.Ints;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.DirectColorModel;
import java.awt.image.PixelGrabber;
import java.awt.image.RescaleOp;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import javax.imageio.ImageIO;
import javax.swing.GrayFilter;
import net.runelite.api.Client;
import net.runelite.api.IndexedSprite;
import net.runelite.api.SpritePixels;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ImageUtil {
    private static final Logger log = LoggerFactory.getLogger(ImageUtil.class);

    public static BufferedImage bufferedImageFromImage(Image image) {
        if (image instanceof BufferedImage) {
            return (BufferedImage)image;
        }
        return ImageUtil.toARGB(image);
    }

    public static BufferedImage toARGB(Image image) {
        if (image instanceof BufferedImage && ((BufferedImage)image).getType() == 2) {
            return (BufferedImage)image;
        }
        BufferedImage out = new BufferedImage(image.getWidth(null), image.getHeight(null), 2);
        Graphics2D g2d = out.createGraphics();
        g2d.drawImage(image, 0, 0, null);
        g2d.dispose();
        return out;
    }

    public static BufferedImage recolorImage(Image image, Color rgb) {
        BufferedImage out = new BufferedImage(image.getWidth(null), image.getHeight(null), 2);
        Graphics2D g2d = out.createGraphics();
        g2d.drawImage(image, 0, 0, null);
        g2d.setComposite(AlphaComposite.SrcAtop);
        g2d.setColor(rgb);
        g2d.fillRect(0, 0, out.getWidth(), out.getHeight());
        g2d.dispose();
        return out;
    }

    public static BufferedImage luminanceOffset(Image rawImg, int offset) {
        BufferedImage image = ImageUtil.toARGB(rawImg);
        int numComponents = image.getColorModel().getNumComponents();
        float[] scales = new float[numComponents];
        float[] offsets = new float[numComponents];
        Arrays.fill(scales, 1.0f);
        for (int i = 0; i < numComponents; ++i) {
            offsets[i] = offset;
        }
        offsets[numComponents - 1] = 0.0f;
        return ImageUtil.offset(image, scales, offsets);
    }

    public static BufferedImage luminanceScale(Image rawImg, float percentage) {
        BufferedImage image = ImageUtil.toARGB(rawImg);
        int numComponents = image.getColorModel().getNumComponents();
        float[] scales = new float[numComponents];
        float[] offsets = new float[numComponents];
        Arrays.fill(offsets, 0.0f);
        for (int i = 0; i < numComponents; ++i) {
            scales[i] = percentage;
        }
        scales[numComponents - 1] = 1.0f;
        return ImageUtil.offset(image, scales, offsets);
    }

    public static BufferedImage alphaOffset(Image rawImg, int offset) {
        BufferedImage image = ImageUtil.toARGB(rawImg);
        int numComponents = image.getColorModel().getNumComponents();
        float[] scales = new float[numComponents];
        float[] offsets = new float[numComponents];
        Arrays.fill(scales, 1.0f);
        Arrays.fill(offsets, 0.0f);
        offsets[numComponents - 1] = offset;
        return ImageUtil.offset(image, scales, offsets);
    }

    public static BufferedImage alphaOffset(Image rawImg, float percentage) {
        BufferedImage image = ImageUtil.toARGB(rawImg);
        int numComponents = image.getColorModel().getNumComponents();
        float[] scales = new float[numComponents];
        float[] offsets = new float[numComponents];
        Arrays.fill(scales, 1.0f);
        Arrays.fill(offsets, 0.0f);
        scales[numComponents - 1] = percentage;
        return ImageUtil.offset(image, scales, offsets);
    }

    public static BufferedImage grayscaleImage(BufferedImage image) {
        Image grayImage = GrayFilter.createDisabledImage(image);
        return ImageUtil.bufferedImageFromImage(grayImage);
    }

    public static BufferedImage resizeImage(BufferedImage image, int newWidth, int newHeight) {
        return ImageUtil.resizeImage(image, newWidth, newHeight, false);
    }

    public static BufferedImage resizeImage(BufferedImage image, int newWidth, int newHeight, boolean preserveAspectRatio) {
        Image resized = preserveAspectRatio ? (image.getWidth() > image.getHeight() ? image.getScaledInstance(newWidth, -1, 4) : image.getScaledInstance(-1, newHeight, 4)) : image.getScaledInstance(newWidth, newHeight, 4);
        return ImageUtil.bufferedImageFromImage(resized);
    }

    public static BufferedImage resizeCanvas(BufferedImage image, int newWidth, int newHeight) {
        BufferedImage dimg = new BufferedImage(newWidth, newHeight, 2);
        int centeredX = newWidth / 2 - image.getWidth() / 2;
        int centeredY = newHeight / 2 - image.getHeight() / 2;
        Graphics2D g2d = dimg.createGraphics();
        g2d.drawImage((Image)image, centeredX, centeredY, null);
        g2d.dispose();
        return dimg;
    }

    public static BufferedImage rotateImage(BufferedImage image, double theta) {
        AffineTransform transform = new AffineTransform();
        transform.rotate(theta, (double)image.getWidth() / 2.0, (double)image.getHeight() / 2.0);
        AffineTransformOp transformOp = new AffineTransformOp(transform, 2);
        return transformOp.filter(image, null);
    }

    public static BufferedImage flipImage(BufferedImage image, boolean horizontal, boolean vertical) {
        int x = 0;
        int y = 0;
        int w = image.getWidth();
        int h = image.getHeight();
        BufferedImage out = new BufferedImage(w, h, 2);
        Graphics2D g2d = out.createGraphics();
        if (horizontal) {
            x = w;
            w *= -1;
        }
        if (vertical) {
            y = h;
            h *= -1;
        }
        g2d.drawImage(image, x, y, w, h, null);
        g2d.dispose();
        return out;
    }

    public static BufferedImage outlineImage(BufferedImage image, Color color) {
        return ImageUtil.outlineImage(image, color, false);
    }

    public static BufferedImage outlineImage(BufferedImage image, Color color, Boolean outlineCorners) {
        BufferedImage filledImage = ImageUtil.fillImage(image, color);
        BufferedImage outlinedImage = new BufferedImage(image.getWidth(), image.getHeight(), 2);
        Graphics2D g2d = outlinedImage.createGraphics();
        for (int x = -1; x <= 1; ++x) {
            for (int y = -1; y <= 1; ++y) {
                if (x == 0 && y == 0 || !outlineCorners.booleanValue() && Math.abs(x) + Math.abs(y) != 1) continue;
                g2d.drawImage((Image)filledImage, x, y, null);
            }
        }
        g2d.drawImage((Image)image, 0, 0, null);
        g2d.dispose();
        return outlinedImage;
    }

    @Deprecated
    public static BufferedImage getResourceStreamFromClass(Class<?> c, String path) {
        return ImageUtil.loadImageResource(c, path);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public static BufferedImage loadImageResource(Class<?> c, String path) {
        try (InputStream in = c.getResourceAsStream(path);){
            Class<ImageIO> clazz = ImageIO.class;
            synchronized (ImageIO.class) {
                BufferedImage bufferedImage = ImageIO.read(in);
                // ** MonitorExit[var3_5] (shouldn't be in output)
                return bufferedImage;
            }
        }
        catch (IllegalArgumentException e) {
            Object filePath = path.startsWith("/") ? path : c.getPackage().getName().replace('.', '/') + "/" + path;
            log.warn("Failed to load image from class: {}, path: {}", (Object)c.getName(), filePath);
            throw new IllegalArgumentException(path, e);
        }
        catch (IOException e) {
            throw new RuntimeException(path, e);
        }
    }

    public static BufferedImage fillImage(BufferedImage image, Color color) {
        BufferedImage filledImage = new BufferedImage(image.getWidth(), image.getHeight(), 2);
        for (int x = 0; x < filledImage.getWidth(); ++x) {
            for (int y = 0; y < filledImage.getHeight(); ++y) {
                int pixel = image.getRGB(x, y);
                int a = pixel >>> 24;
                if (a == 0) continue;
                filledImage.setRGB(x, y, color.getRGB());
            }
        }
        return filledImage;
    }

    private static BufferedImage offset(BufferedImage image, float[] scales, float[] offsets) {
        return new RescaleOp(scales, offsets, null).filter(image, null);
    }

    public static SpritePixels getImageSpritePixels(BufferedImage image, Client client2) {
        int[] pixels = new int[image.getWidth() * image.getHeight()];
        try {
            PixelGrabber g = new PixelGrabber(image, 0, 0, image.getWidth(), image.getHeight(), pixels, 0, image.getWidth());
            g.setColorModel(new DirectColorModel(32, 0xFF0000, 65280, 255, -16777216));
            g.grabPixels();
            for (int i = 0; i < pixels.length; ++i) {
                if ((pixels[i] & 0xFF000000) != 0) continue;
                pixels[i] = 0;
            }
        }
        catch (InterruptedException ex) {
            log.debug("PixelGrabber was interrupted: ", (Throwable)ex);
        }
        return client2.createSpritePixels(pixels, image.getWidth(), image.getHeight());
    }

    public static IndexedSprite getImageIndexedSprite(BufferedImage image, Client client2) {
        byte[] pixels = new byte[image.getWidth() * image.getHeight()];
        ArrayList<Integer> palette = new ArrayList<Integer>();
        palette.add(0);
        int[] sourcePixels = image.getRGB(0, 0, image.getWidth(), image.getHeight(), null, 0, image.getWidth());
        for (int j = 0; j < sourcePixels.length; ++j) {
            int argb = sourcePixels[j];
            int a = argb >> 24 & 0xFF;
            int rgb = argb & 0xFFFFFF;
            int paletteIdx = 0;
            if (a == 255 && (paletteIdx = palette.indexOf(rgb)) == -1) {
                paletteIdx = palette.size();
                palette.add(rgb);
            }
            pixels[j] = (byte)paletteIdx;
        }
        if (palette.size() > 256) {
            throw new RuntimeException("Passed in image had " + (palette.size() - 1) + " different colors, exceeding the max of 255.");
        }
        IndexedSprite sprite = client2.createIndexedSprite();
        sprite.setPixels(pixels);
        sprite.setPalette(Ints.toArray(palette));
        sprite.setWidth(image.getWidth());
        sprite.setHeight(image.getHeight());
        sprite.setOriginalWidth(image.getWidth());
        sprite.setOriginalHeight(image.getHeight());
        sprite.setOffsetX(0);
        sprite.setOffsetY(0);
        return sprite;
    }

    static {
        ImageIO.setUseCache(false);
    }
}

