/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.inject.Inject
 *  javax.annotation.Nullable
 *  net.runelite.api.Client
 *  net.runelite.api.NPC
 *  net.runelite.api.Perspective
 *  net.runelite.api.Player
 *  net.runelite.api.Point
 *  net.runelite.api.coords.LocalPoint
 *  net.runelite.api.coords.WorldPoint
 */
package net.runelite.client.plugins.kourendlibrary;

import com.google.inject.Inject;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Polygon;
import java.awt.geom.Rectangle2D;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import javax.annotation.Nullable;
import net.runelite.api.Client;
import net.runelite.api.NPC;
import net.runelite.api.Perspective;
import net.runelite.api.Player;
import net.runelite.api.Point;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.kourendlibrary.Book;
import net.runelite.client.plugins.kourendlibrary.Bookcase;
import net.runelite.client.plugins.kourendlibrary.KourendLibraryConfig;
import net.runelite.client.plugins.kourendlibrary.KourendLibraryPlugin;
import net.runelite.client.plugins.kourendlibrary.Library;
import net.runelite.client.plugins.kourendlibrary.SolvedState;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayUtil;

class KourendLibraryOverlay
extends Overlay {
    private static final int MAXIMUM_DISTANCE = 24;
    private final Library library;
    private final Client client;
    private final KourendLibraryConfig config;
    private final KourendLibraryPlugin plugin;

    @Inject
    private KourendLibraryOverlay(Library library, Client client, KourendLibraryConfig config, KourendLibraryPlugin plugin) {
        this.library = library;
        this.client = client;
        this.config = config;
        this.plugin = plugin;
        this.setPosition(OverlayPosition.DYNAMIC);
        this.setLayer(OverlayLayer.ABOVE_SCENE);
    }

    @Override
    public Dimension render(Graphics2D g) {
        Player player = this.client.getLocalPlayer();
        if (player == null) {
            return null;
        }
        WorldPoint playerLoc = player.getWorldLocation();
        if (playerLoc.getRegionID() != 6459) {
            return null;
        }
        List<Bookcase> allBookcases = this.library.getBookcasesOnLevel(this.client.getPlane());
        for (Bookcase bookcase : allBookcases) {
            Polygon poly;
            Color color;
            Point screenBookcase;
            LocalPoint localBookcase;
            WorldPoint caseLoc = bookcase.getLocation();
            if (Math.abs(playerLoc.getX() - caseLoc.getX()) > 24 || Math.abs(playerLoc.getY() - caseLoc.getY()) > 24 || (localBookcase = LocalPoint.fromWorld((Client)this.client, (WorldPoint)caseLoc)) == null || (screenBookcase = Perspective.localToCanvas((Client)this.client, (LocalPoint)localBookcase, (int)caseLoc.getPlane(), (int)25)) == null) continue;
            boolean bookIsKnown = bookcase.isBookSet();
            Book book = bookcase.getBook();
            Set<Book> possible = bookcase.getPossibleBooks();
            if (!bookIsKnown && possible.size() == 1) {
                book = possible.iterator().next();
                bookIsKnown = true;
            }
            if (book == Book.VARLAMORE_ENVOY && !this.plugin.showVarlamoreEnvoy()) continue;
            Color color2 = bookIsKnown ? (book == this.library.getCustomerBook() ? Color.GREEN : Color.ORANGE) : (color = Color.WHITE);
            if (!(bookIsKnown && book == null || this.library.getState() != SolvedState.NO_DATA && book == null && possible.isEmpty() || this.shouldHideOverlayIfDuplicateBook(book) || (poly = Perspective.getCanvasTilePoly((Client)this.client, (LocalPoint)localBookcase)) == null)) {
                OverlayUtil.renderPolygon(g, poly, color);
            }
            int height = 0;
            if (bookIsKnown) {
                if (book == null || this.shouldHideOverlayIfDuplicateBook(book)) continue;
                FontMetrics fm = g.getFontMetrics();
                Rectangle2D bounds = fm.getStringBounds(book.getShortName(), g);
                height = (int)bounds.getHeight() + book.getIcon().getHeight() + 6;
                Point textLoc = new Point((int)((double)screenBookcase.getX() - bounds.getWidth() / 2.0), screenBookcase.getY() - height / 2 + (int)bounds.getHeight());
                OverlayUtil.renderTextLocation(g, textLoc, book.getShortName(), color);
                g.drawImage((Image)book.getIcon(), screenBookcase.getX() - book.getIcon().getWidth() / 2, screenBookcase.getY() + height / 2 - book.getIcon().getHeight(), null);
                continue;
            }
            int BOOK_ICON_SIZE = 32;
            Book[] books = (Book[])possible.stream().filter(Objects::nonNull).limit(9L).toArray(Book[]::new);
            if (books.length <= 1 || books.length > 9) continue;
            int cols = (int)Math.ceil(Math.sqrt(books.length));
            int rows = (int)Math.ceil((double)books.length / (double)cols);
            height = rows * 32;
            int xbase = screenBookcase.getX() - cols * 32 / 2;
            int ybase = screenBookcase.getY() - rows * 32 / 2;
            for (int i = 0; i < books.length; ++i) {
                int col = i % cols;
                int row = i / cols;
                int x = col * 32;
                int y = row * 32;
                if (row == rows - 1) {
                    x += 32 * (books.length % rows) / 2;
                }
                g.drawImage((Image)books[i].getIcon(), xbase + x, ybase + y, null);
            }
        }
        int customerId = this.library.getCustomerId();
        if (customerId != -1) {
            for (NPC n : this.plugin.getNpcsToMark()) {
                if (n.getId() != customerId) continue;
                Book b = this.library.getCustomerBook();
                boolean doesPlayerContainBook = this.plugin.doesPlayerContainBook(b);
                LocalPoint local = n.getLocalLocation();
                Polygon poly = Perspective.getCanvasTilePoly((Client)this.client, (LocalPoint)local);
                OverlayUtil.renderPolygon(g, poly, doesPlayerContainBook ? Color.GREEN : Color.WHITE);
                Point screen = Perspective.localToCanvas((Client)this.client, (LocalPoint)local, (int)this.client.getPlane(), (int)n.getLogicalHeight());
                if (screen == null) continue;
                g.drawImage((Image)b.getIcon(), screen.getX() - b.getIcon().getWidth() / 2, screen.getY() - b.getIcon().getHeight(), null);
            }
        }
        return null;
    }

    private boolean shouldHideOverlayIfDuplicateBook(@Nullable Book book) {
        return this.config.hideDuplicateBook() && book != null && this.plugin.doesPlayerContainBook(book);
    }
}

