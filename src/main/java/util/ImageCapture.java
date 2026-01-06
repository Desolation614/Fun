/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Strings
 *  javax.annotation.Nullable
 *  javax.inject.Inject
 *  javax.inject.Singleton
 *  net.runelite.api.Client
 *  net.runelite.api.GameState
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package net.runelite.client.util;

import com.google.common.base.Strings;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.datatransfer.Clipboard;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ScheduledExecutorService;
import javax.annotation.Nullable;
import javax.imageio.ImageIO;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.swing.SwingUtilities;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.client.Notifier;
import net.runelite.client.RuneLite;
import net.runelite.client.config.RuneScapeProfileType;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.events.ScreenshotTaken;
import net.runelite.client.ui.ClientUI;
import net.runelite.client.ui.DrawManager;
import net.runelite.client.util.ImageUploadStyle;
import net.runelite.client.util.ImageUtil;
import net.runelite.client.util.Text;
import net.runelite.client.util.TransferableBufferedImage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class ImageCapture {
    private static final Logger log = LoggerFactory.getLogger(ImageCapture.class);
    private static final DateFormat TIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
    private final Client client;
    private final Notifier notifier;
    private final ClientUI clientUi;
    private final DrawManager drawManager;
    private final ScheduledExecutorService executor;
    private final EventBus eventBus;

    public void takeScreenshot(@Nullable String subDir, String fileName, boolean includeClientFrame, boolean notify, boolean copyToClipboard) {
        this.drawManager.requestNextFrameListener(img -> this.executor.submit(() -> {
            BufferedImage screenshot = includeClientFrame ? this.addClientFrame((Image)img) : ImageUtil.bufferedImageFromImage(img);
            this.saveScreenshot(screenshot, fileName, subDir, notify, copyToClipboard);
        }));
    }

    public BufferedImage addClientFrame(Image image) {
        AffineTransform transform = this.clientUi.getGraphicsConfiguration().getDefaultTransform();
        Insets insets = this.clientUi.getInsets();
        transform.translate(-insets.left, -insets.top);
        int screenshotWidth = ImageCapture.getScaledValue(transform.getScaleX(), this.clientUi.getWidth() - insets.left - insets.right);
        int screenshotHeight = ImageCapture.getScaledValue(transform.getScaleY(), this.clientUi.getHeight() - insets.top - insets.bottom);
        BufferedImage screenshot = new BufferedImage(screenshotWidth, screenshotHeight, 2);
        Graphics2D graphics = (Graphics2D)screenshot.getGraphics();
        AffineTransform originalTransform = graphics.getTransform();
        graphics.setTransform(transform);
        try {
            SwingUtilities.invokeAndWait(() -> this.clientUi.paint(graphics));
        }
        catch (InterruptedException | InvocationTargetException e) {
            log.warn("unable to paint client UI on screenshot", (Throwable)e);
        }
        Point canvasOffset = this.clientUi.getCanvasOffset();
        canvasOffset.x -= insets.left;
        canvasOffset.y -= insets.top;
        int gameOffsetX = ImageCapture.getScaledValue(transform.getScaleX(), (int)canvasOffset.getX());
        int gameOffsetY = ImageCapture.getScaledValue(transform.getScaleY(), (int)canvasOffset.getY());
        graphics.setTransform(originalTransform);
        graphics.drawImage(image, gameOffsetX, gameOffsetY, null);
        graphics.dispose();
        return screenshot;
    }

    private static int getScaledValue(double scale, int value) {
        return (int)((double)value * scale);
    }

    public void saveScreenshot(BufferedImage screenshot, String fileName, @Nullable String subDir, boolean notify, boolean saveToClipboard) {
        File playerFolder;
        if (this.client.getGameState() == GameState.LOGIN_SCREEN) {
            log.debug("Login screenshot prevented");
            return;
        }
        if (this.client.getLocalPlayer() != null && this.client.getLocalPlayer().getName() != null) {
            Object playerDir = this.client.getLocalPlayer().getName();
            RuneScapeProfileType profileType = RuneScapeProfileType.getCurrent(this.client);
            if (profileType != RuneScapeProfileType.STANDARD) {
                playerDir = (String)playerDir + "-" + Text.titleCase(profileType);
            }
            if (!Strings.isNullOrEmpty((String)subDir)) {
                playerDir = (String)playerDir + File.separator + subDir;
            }
            playerFolder = new File(RuneLite.SCREENSHOT_DIR, (String)playerDir);
        } else {
            playerFolder = RuneLite.SCREENSHOT_DIR;
        }
        playerFolder.mkdirs();
        fileName = (String)fileName + (((String)fileName).isEmpty() ? "" : " ") + ImageCapture.format(new Date());
        File screenshotFile = new File(playerFolder, (String)fileName + ".png");
        int i = 1;
        while (screenshotFile.exists()) {
            screenshotFile = new File(playerFolder, (String)fileName + String.format("(%d)", i++) + ".png");
        }
        try {
            ImageIO.write((RenderedImage)screenshot, "PNG", screenshotFile);
        }
        catch (IOException ex) {
            log.error("error writing screenshot", (Throwable)ex);
            return;
        }
        if (saveToClipboard) {
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            TransferableBufferedImage transferableBufferedImage = new TransferableBufferedImage(screenshot);
            clipboard.setContents(transferableBufferedImage, null);
            if (notify) {
                this.notifier.notify("A screenshot was saved and inserted into your clipboard!", TrayIcon.MessageType.INFO);
            }
        } else if (notify) {
            this.notifier.notify("A screenshot was saved to " + String.valueOf(screenshotFile), TrayIcon.MessageType.INFO);
        }
        ScreenshotTaken screenshotTaken = new ScreenshotTaken(screenshotFile, screenshot);
        this.eventBus.post(screenshotTaken);
    }

    @Deprecated
    public void takeScreenshot(BufferedImage screenshot, String fileName, @Nullable String subDir, boolean notify, ImageUploadStyle imageUploadStyle) {
        this.saveScreenshot(screenshot, fileName, subDir, notify, imageUploadStyle == ImageUploadStyle.CLIPBOARD);
    }

    @Deprecated
    public void takeScreenshot(BufferedImage screenshot, String fileName, boolean notify, ImageUploadStyle imageUploadStyle) {
        this.takeScreenshot(screenshot, fileName, null, notify, imageUploadStyle);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static String format(Date date) {
        DateFormat dateFormat = TIME_FORMAT;
        synchronized (dateFormat) {
            return TIME_FORMAT.format(date);
        }
    }

    @Inject
    public ImageCapture(Client client2, Notifier notifier, ClientUI clientUi, DrawManager drawManager, ScheduledExecutorService executor, EventBus eventBus) {
        this.client = client2;
        this.notifier = notifier;
        this.clientUi = clientUi;
        this.drawManager = drawManager;
        this.executor = executor;
        this.eventBus = eventBus;
    }
}

