/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.plugins.kourendlibrary;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import net.runelite.client.game.ItemManager;
import net.runelite.client.util.AsyncBufferedImage;

enum Book {
    RADAS_CENSUS(13524, "Rada's Census", "Census of King Rada III, by Matthias Vorseth."),
    RICKTORS_DIARY_7(13525, "Ricktor's Diary 7", "Diary of Steklan Ricktor, volume 7."),
    EATHRAM_RADA_EXTRACT(13526, "Eathram & Rada extract", "An extract from Eathram & Rada, by Anonymous."),
    KILLING_OF_A_KING(13527, "Killing of a King", "Killing of a King, by Griselle."),
    HOSIDIUS_LETTER(13528, "Hosidius Letter", "A letter from Lord Hosidius to the Council of Elders."),
    WINTERTODT_PARABLE(13529, "Wintertodt Parable", "The Parable of the Wintertodt, by Anonymous."),
    TWILL_ACCORD(13530, "Twill Accord", "The Royal Accord of Twill."),
    BYRNES_CORONATION_SPEECH(13531, "Byrnes Coronation Speech", "Speech of King Byrne I, on the occasion of his coronation."),
    IDEOLOGY_OF_DARKNESS(13532, "The Ideology of Darkness", "The Ideology of Darkness, by Philophaire."),
    RADAS_JOURNEY(13533, "Rada's Journey", "The Journey of Rada, by Griselle."),
    TRANSVERGENCE_THEORY(13534, "Transvergence Theory", "The Theory of Transvergence, by Amon Ducot."),
    TRISTESSAS_TRAGEDY(13535, "Tristessa's Tragedy", "The Tragedy of Tristessa."),
    TREACHERY_OF_ROYALTY(13536, "The Treachery of Royalty", "The Treachery of Royalty, by Professor Answith."),
    TRANSPORTATION_INCANTATIONS(13537, "Transportation Incantations", "Transportation Incantations, by Amon Ducot."),
    SOUL_JOURNEY(19637, "Soul Journey", "The Journey of Souls, by Aretha."),
    VARLAMORE_ENVOY(21756, "Varlamore Envoy", "The Envoy to Varlamore, by Deryk Paulson.");

    private static final Map<Integer, Book> BY_ID;
    private static final Map<String, Book> BY_NAME;
    private final int item;
    private final String name;
    private final String shortName;
    private AsyncBufferedImage icon;

    private static Map<Integer, Book> buildById() {
        HashMap<Integer, Book> byId = new HashMap<Integer, Book>();
        for (Book b : Book.values()) {
            byId.put(b.item, b);
        }
        return Collections.unmodifiableMap(byId);
    }

    private static Map<String, Book> buildByName() {
        HashMap<String, Book> byName = new HashMap<String, Book>();
        for (Book b : Book.values()) {
            byName.put(b.name, b);
        }
        return Collections.unmodifiableMap(byName);
    }

    static Book byId(int id) {
        return BY_ID.get(id);
    }

    static Book byName(String name) {
        return BY_NAME.get(name);
    }

    private Book(int id, String shortName, String name) {
        this.item = id;
        this.shortName = shortName;
        this.name = name;
    }

    static void fillImages(ItemManager itemManager) {
        for (Book b : Book.values()) {
            b.icon = itemManager.getImage(b.item);
        }
    }

    public int getItem() {
        return this.item;
    }

    public String getName() {
        return this.name;
    }

    public String getShortName() {
        return this.shortName;
    }

    public AsyncBufferedImage getIcon() {
        return this.icon;
    }

    static {
        BY_ID = Book.buildById();
        BY_NAME = Book.buildByName();
    }
}

