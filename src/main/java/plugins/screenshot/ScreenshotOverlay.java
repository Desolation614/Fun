/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.inject.Inject
 *  net.runelite.api.Client
 *  net.runelite.api.MainBufferProvider
 */
package net.runelite.client.plugins.screenshot;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Consumer;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.MainBufferProvider;
import net.runelite.client.plugins.screenshot.ScreenshotPlugin;
import net.runelite.client.ui.DrawManager;
import net.runelite.client.ui.FontManager;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;

class ScreenshotOverlay
extends Overlay {
    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("MMM. dd, yyyy");
    private static final int REPORT_BUTTON_X_OFFSET = 437;
    private final Client client;
    private final DrawManager drawManager;
    private final ScreenshotPlugin plugin;
    private final Queue<Consumer<Image>> consumers = new ConcurrentLinkedQueue<Consumer<Image>>();

    @Inject
    private ScreenshotOverlay(Client client, DrawManager drawManager, ScreenshotPlugin plugin) {
        this.setPosition(OverlayPosition.DYNAMIC);
        this.setPriority(0.75f);
        this.setLayer(OverlayLayer.ABOVE_WIDGETS);
        this.client = client;
        this.drawManager = drawManager;
        this.plugin = plugin;
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        Consumer<Image> consumer;
        if (this.consumers.isEmpty()) {
            return null;
        }
        MainBufferProvider bufferProvider = (MainBufferProvider)this.client.getBufferProvider();
        int imageHeight = ((BufferedImage)bufferProvider.getImage()).getHeight();
        int y = imageHeight - this.plugin.getReportButton().getHeight() - 1;
        graphics.drawImage((Image)this.plugin.getReportButton(), 437, y, null);
        graphics.setFont(FontManager.getRunescapeSmallFont());
        FontMetrics fontMetrics = graphics.getFontMetrics();
        String date = DATE_FORMAT.format(new Date());
        int dateWidth = fontMetrics.stringWidth(date);
        int dateHeight = fontMetrics.getHeight();
        int textX = 437 + this.plugin.getReportButton().getWidth() / 2 - dateWidth / 2;
        int textY = y + this.plugin.getReportButton().getHeight() / 2 + dateHeight / 2;
        graphics.setColor(Color.BLACK);
        graphics.drawString(date, textX + 1, textY + 1);
        graphics.setColor(Color.WHITE);
        graphics.drawString(date, textX, textY);
        while ((consumer = this.consumers.poll()) != null) {
            this.drawManager.requestNextFrameListener(consumer);
        }
        return null;
    }

    void queueForTimestamp(Consumer<Image> screenshotConsumer) {
        if (this.plugin.getReportButton() == null) {
            return;
        }
        this.consumers.add(screenshotConsumer);
    }
}

