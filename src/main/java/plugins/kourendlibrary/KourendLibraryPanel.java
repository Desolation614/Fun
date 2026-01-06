/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.inject.Inject
 *  javax.inject.Singleton
 */
package net.runelite.client.plugins.kourendlibrary;

import com.google.inject.Inject;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.inject.Singleton;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import net.runelite.client.plugins.kourendlibrary.Book;
import net.runelite.client.plugins.kourendlibrary.BookPanel;
import net.runelite.client.plugins.kourendlibrary.Bookcase;
import net.runelite.client.plugins.kourendlibrary.KourendLibraryPlugin;
import net.runelite.client.plugins.kourendlibrary.Library;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.PluginPanel;
import net.runelite.client.util.ImageUtil;

@Singleton
class KourendLibraryPanel
extends PluginPanel {
    private static final ImageIcon RESET_ICON;
    private static final ImageIcon RESET_HOVER_ICON;
    private final KourendLibraryPlugin plugin;
    private final Library library;
    private final HashMap<Book, BookPanel> bookPanels = new HashMap();

    @Inject
    KourendLibraryPanel(KourendLibraryPlugin plugin, Library library) {
        this.plugin = plugin;
        this.library = library;
    }

    void init() {
        this.setLayout(new BorderLayout(0, 5));
        this.setBorder(new EmptyBorder(10, 10, 10, 10));
        this.setBackground(ColorScheme.DARK_GRAY_COLOR);
        JPanel books = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.fill = 2;
        c.weightx = 1.0;
        c.gridx = 0;
        c.gridy = 0;
        Stream.of(Book.values()).filter(b -> b != Book.VARLAMORE_ENVOY || this.plugin.showVarlamoreEnvoy()).sorted(Comparator.comparing(Book::getShortName)).forEach(b -> {
            BookPanel p = new BookPanel((Book)((Object)b));
            this.bookPanels.put((Book)((Object)b), p);
            books.add((Component)p, c);
            ++c.gridy;
        });
        JButton reset = new JButton("Reset", RESET_ICON);
        reset.setRolloverIcon(RESET_HOVER_ICON);
        reset.addActionListener(ev -> {
            this.library.reset();
            this.update();
        });
        this.add((Component)reset, "North");
        this.add((Component)books, "Center");
        this.update();
    }

    void update() {
        SwingUtilities.invokeLater(() -> {
            Book customerBook = this.library.getCustomerBook();
            for (Map.Entry<Book, BookPanel> b : this.bookPanels.entrySet()) {
                Book book = b.getKey();
                BookPanel panel = b.getValue();
                panel.setIsTarget(customerBook == book);
                panel.setIsHeld(this.plugin.doesPlayerContainBook(book));
            }
            HashMap<Book, HashSet> bookLocations = new HashMap<Book, HashSet>();
            for (Bookcase bookcase : this.library.getBookcases()) {
                if (bookcase.getBook() != null) {
                    bookLocations.computeIfAbsent(bookcase.getBook(), a -> new HashSet()).add(bookcase.getLocationString());
                    continue;
                }
                for (Book book : bookcase.getPossibleBooks()) {
                    if (book == null) continue;
                    bookLocations.computeIfAbsent(book, a -> new HashSet()).add(bookcase.getLocationString());
                }
            }
            for (Map.Entry entry : this.bookPanels.entrySet()) {
                HashSet locs = (HashSet)bookLocations.get(entry.getKey());
                if (locs == null || locs.size() > 3) {
                    ((BookPanel)entry.getValue()).setLocation("Unknown");
                    continue;
                }
                ((BookPanel)entry.getValue()).setLocation("<html>" + locs.stream().collect(Collectors.joining("<br>")) + "</html>");
            }
        });
    }

    void reload() {
        this.bookPanels.clear();
        this.removeAll();
        this.init();
    }

    static {
        BufferedImage resetIcon = ImageUtil.loadImageResource(KourendLibraryPanel.class, "/util/reset.png");
        RESET_ICON = new ImageIcon(resetIcon);
        RESET_HOVER_ICON = new ImageIcon(ImageUtil.alphaOffset((Image)resetIcon, -100));
    }
}

