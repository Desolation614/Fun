/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.annotations.VisibleForTesting
 *  javax.inject.Singleton
 *  net.runelite.api.coords.WorldPoint
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package net.runelite.client.plugins.kourendlibrary;

import com.google.common.annotations.VisibleForTesting;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;
import javax.inject.Singleton;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.kourendlibrary.Book;
import net.runelite.client.plugins.kourendlibrary.Bookcase;
import net.runelite.client.plugins.kourendlibrary.SolvedState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
class Library {
    private static final Logger log = LoggerFactory.getLogger(Library.class);
    private final Map<WorldPoint, Bookcase> byPoint = new HashMap<WorldPoint, Bookcase>();
    private final Map<Integer, ArrayList<Bookcase>> byLevel = new HashMap<Integer, ArrayList<Bookcase>>();
    private final List<Bookcase> byIndex = new ArrayList<Bookcase>();
    private final List<List<Book>> sequences = this.populateSequences();
    @VisibleForTesting
    final int step;
    private SolvedState state;
    private Book customerBook;
    private int customerId;

    Library() {
        this.populateBooks();
        this.step = this.byIndex.size() / Book.values().length;
        this.reset();
    }

    synchronized List<Bookcase> getBookcasesOnLevel(int z) {
        return Collections.unmodifiableList((List)this.byLevel.get(z));
    }

    synchronized List<Bookcase> getBookcases() {
        return Collections.unmodifiableList(this.byIndex);
    }

    void setCustomer(int customerId, Book book) {
        this.customerId = customerId;
        this.customerBook = book;
    }

    synchronized void reset() {
        this.state = SolvedState.NO_DATA;
        for (Bookcase b : this.byIndex) {
            b.clearBook();
            b.getPossibleBooks().clear();
        }
        log.info("Library is now reset");
    }

    void mark(WorldPoint loc, Book book) {
        Bookcase bookcase = this.byPoint.get(loc);
        if (bookcase == null) {
            log.debug("Requested non-existent bookcase at {}", (Object)loc);
            return;
        }
        this.mark(bookcase, book);
    }

    @VisibleForTesting
    void mark(int bookcaseIndex, Book book) {
        this.mark(this.byIndex.get(bookcaseIndex), book);
    }

    synchronized void mark(Bookcase bookcase, Book book) {
        int max;
        int[] certainty;
        int bookcaseIndex;
        if (bookcase.isBookSet()) {
            if (book != bookcase.getBook() && (book != null || bookcase.getBook() != Book.VARLAMORE_ENVOY)) {
                this.reset();
            }
        } else if (this.state != SolvedState.NO_DATA && book != null && !bookcase.getPossibleBooks().contains((Object)book)) {
            this.reset();
        }
        if (this.state == SolvedState.COMPLETE) {
            if (book == null && !bookcase.getPossibleBooks().isEmpty() && bookcase.getPossibleBooks().stream().noneMatch(b -> b == Book.VARLAMORE_ENVOY)) {
                this.reset();
            } else {
                return;
            }
        }
        log.info("Setting bookcase {} to {}", bookcase.getIndex(), (Object)book);
        while (true) {
            bookcase.setBook(book);
            if (book == null) {
                return;
            }
            if (bookcase.getIndex().size() != 1) {
                return;
            }
            bookcaseIndex = bookcase.getIndex().get(0);
            this.state = SolvedState.INCOMPLETE;
            certainty = this.sequences.stream().mapToInt(sequence -> {
                int zero = this.getBookcaseZeroIndexForSequenceWithBook((List<Book>)sequence, bookcaseIndex, book);
                int found = 0;
                for (int i = 0; i < this.byIndex.size(); ++i) {
                    int ai = (i + zero) % this.byIndex.size();
                    Bookcase iBookcase = this.byIndex.get(ai);
                    if (i % this.step == 0) {
                        int seqI = i / this.step;
                        if (!iBookcase.isBookSet() || seqI >= sequence.size()) continue;
                        Book seqBook = (Book)((Object)((Object)sequence.get(seqI)));
                        if (iBookcase.getBook() != seqBook) {
                            log.debug("Bailing @ i={} ai={} {}; {} != {}", new Object[]{i, ai, iBookcase.getIndex(), iBookcase.getBook(), seqBook});
                            found = 0;
                            break;
                        }
                        ++found;
                        continue;
                    }
                    if (!iBookcase.isBookSet() || iBookcase.getBook() == null || iBookcase.getIndex().size() != 1) continue;
                    log.debug("Bailing @ i={} ai={} {}; {} is set", new Object[]{i, ai, iBookcase.getIndex(), iBookcase.getBook()});
                    found = 0;
                    break;
                }
                return found;
            }).toArray();
            log.info("Certainty is now {}", (Object)certainty);
            for (Bookcase b2 : this.byIndex) {
                b2.getPossibleBooks().clear();
            }
            max = IntStream.of(certainty).max().getAsInt();
            if (max != 0) break;
            this.reset();
        }
        IntStream.range(0, this.sequences.size()).filter(i -> certainty[i] == max).forEach(isequence -> {
            List<Book> sequence = this.sequences.get(isequence);
            int zero = this.getBookcaseZeroIndexForSequenceWithBook(sequence, bookcaseIndex, book);
            for (int i = 0; i < this.byIndex.size(); ++i) {
                int ai = (i + zero) % this.byIndex.size();
                Bookcase iBookcase = this.byIndex.get(ai);
                if (iBookcase.getBook() != null) continue;
                int iseq = i / this.step;
                if (i % this.step != 0 || iseq >= sequence.size()) continue;
                Book seqBook = sequence.get(iseq);
                iBookcase.getPossibleBooks().add(seqBook);
            }
        });
        if (IntStream.range(0, certainty.length).filter(i -> certainty[i] == max).count() == 1L) {
            this.state = SolvedState.COMPLETE;
        }
    }

    private int getBookcaseZeroIndexForSequenceWithBook(List<Book> sequences, int bookcaseIndex, Book book) {
        int bookSequence = sequences.indexOf((Object)book);
        assert (bookSequence >= 0);
        bookcaseIndex -= this.step * bookSequence;
        while (bookcaseIndex < 0) {
            bookcaseIndex += this.byIndex.size();
        }
        return bookcaseIndex;
    }

    private List<List<Book>> populateSequences() {
        List<List> books = Arrays.asList(Arrays.asList(Book.KILLING_OF_A_KING, Book.IDEOLOGY_OF_DARKNESS, Book.RADAS_JOURNEY, Book.TRANSVERGENCE_THEORY, Book.TRISTESSAS_TRAGEDY, Book.RADAS_CENSUS, Book.TREACHERY_OF_ROYALTY, Book.HOSIDIUS_LETTER, Book.RICKTORS_DIARY_7, Book.EATHRAM_RADA_EXTRACT, Book.VARLAMORE_ENVOY, Book.WINTERTODT_PARABLE, Book.TWILL_ACCORD, Book.BYRNES_CORONATION_SPEECH, Book.SOUL_JOURNEY, Book.TRANSPORTATION_INCANTATIONS), Arrays.asList(Book.KILLING_OF_A_KING, Book.IDEOLOGY_OF_DARKNESS, Book.RADAS_JOURNEY, Book.TRANSVERGENCE_THEORY, Book.TRISTESSAS_TRAGEDY, Book.RADAS_CENSUS, Book.TREACHERY_OF_ROYALTY, Book.HOSIDIUS_LETTER, Book.VARLAMORE_ENVOY, Book.RICKTORS_DIARY_7, Book.EATHRAM_RADA_EXTRACT, Book.SOUL_JOURNEY, Book.WINTERTODT_PARABLE, Book.TWILL_ACCORD, Book.BYRNES_CORONATION_SPEECH, Book.TRANSPORTATION_INCANTATIONS), Arrays.asList(Book.RICKTORS_DIARY_7, Book.VARLAMORE_ENVOY, Book.EATHRAM_RADA_EXTRACT, Book.IDEOLOGY_OF_DARKNESS, Book.RADAS_CENSUS, Book.KILLING_OF_A_KING, Book.TREACHERY_OF_ROYALTY, Book.HOSIDIUS_LETTER, Book.BYRNES_CORONATION_SPEECH, Book.SOUL_JOURNEY, Book.WINTERTODT_PARABLE, Book.TWILL_ACCORD, Book.RADAS_JOURNEY, Book.TRANSVERGENCE_THEORY, Book.TRISTESSAS_TRAGEDY, Book.TRANSPORTATION_INCANTATIONS), Arrays.asList(Book.RADAS_CENSUS, Book.RICKTORS_DIARY_7, Book.EATHRAM_RADA_EXTRACT, Book.KILLING_OF_A_KING, Book.HOSIDIUS_LETTER, Book.WINTERTODT_PARABLE, Book.TWILL_ACCORD, Book.BYRNES_CORONATION_SPEECH, Book.IDEOLOGY_OF_DARKNESS, Book.RADAS_JOURNEY, Book.TRANSVERGENCE_THEORY, Book.TRISTESSAS_TRAGEDY, Book.TREACHERY_OF_ROYALTY, Book.TRANSPORTATION_INCANTATIONS, Book.SOUL_JOURNEY, Book.VARLAMORE_ENVOY), Arrays.asList(Book.RADAS_CENSUS, Book.TRANSVERGENCE_THEORY, Book.TREACHERY_OF_ROYALTY, Book.RADAS_JOURNEY, Book.KILLING_OF_A_KING, Book.VARLAMORE_ENVOY, Book.BYRNES_CORONATION_SPEECH, Book.HOSIDIUS_LETTER, Book.TRISTESSAS_TRAGEDY, Book.RICKTORS_DIARY_7, Book.IDEOLOGY_OF_DARKNESS, Book.WINTERTODT_PARABLE, Book.TWILL_ACCORD, Book.SOUL_JOURNEY, Book.EATHRAM_RADA_EXTRACT, Book.TRANSPORTATION_INCANTATIONS));
        for (int i = 0; i < books.size(); ++i) {
            assert (new HashSet(books.get(i)).size() == books.get(i).size());
            books.set(i, Collections.unmodifiableList(books.get(i)));
        }
        return Collections.unmodifiableList(books);
    }

    private void add(int x, int y, int z, int i) {
        WorldPoint p = new WorldPoint(x, y, z);
        Bookcase b = this.byPoint.get(p);
        if (b == null) {
            b = new Bookcase(p);
            this.byPoint.put(p, b);
            this.byLevel.computeIfAbsent(z, a -> new ArrayList()).add(b);
        }
        b.getIndex().add(i);
        assert (i == this.byIndex.size());
        this.byIndex.add(b);
    }

    private void populateBooks() {
        this.add(1626, 3795, 0, 0);
        this.add(1625, 3793, 0, 1);
        this.add(1623, 3793, 0, 2);
        this.add(1620, 3792, 0, 3);
        this.add(1624, 3792, 0, 4);
        this.add(1626, 3788, 0, 5);
        this.add(1626, 3787, 0, 6);
        this.add(1624, 3784, 0, 7);
        this.add(1623, 3784, 0, 8);
        this.add(1621, 3784, 0, 9);
        this.add(1615, 3785, 0, 10);
        this.add(1615, 3788, 0, 11);
        this.add(1615, 3790, 0, 12);
        this.add(1614, 3790, 0, 13);
        this.add(1614, 3788, 0, 14);
        this.add(1614, 3786, 0, 15);
        this.add(1612, 3784, 0, 16);
        this.add(1610, 3784, 0, 17);
        this.add(1609, 3784, 0, 18);
        this.add(1607, 3786, 0, 19);
        this.add(1607, 3789, 0, 20);
        this.add(1607, 3795, 0, 21);
        this.add(1607, 3796, 0, 22);
        this.add(1607, 3799, 0, 23);
        this.add(1610, 3801, 0, 24);
        this.add(1612, 3801, 0, 25);
        this.add(1618, 3801, 0, 26);
        this.add(1620, 3801, 0, 27);
        this.add(1620, 3814, 0, 28);
        this.add(1618, 3814, 0, 29);
        this.add(1617, 3814, 0, 30);
        this.add(1615, 3816, 0, 31);
        this.add(1615, 3817, 0, 32);
        this.add(1615, 3820, 0, 33);
        this.add(1614, 3820, 0, 34);
        this.add(1614, 3817, 0, 35);
        this.add(1614, 3816, 0, 36);
        this.add(1612, 3814, 0, 37);
        this.add(1610, 3814, 0, 38);
        this.add(1607, 3816, 0, 39);
        this.add(1607, 3817, 0, 40);
        this.add(1607, 3820, 0, 41);
        this.add(1607, 3826, 0, 42);
        this.add(1607, 3828, 0, 43);
        this.add(1609, 3831, 0, 44);
        this.add(1612, 3831, 0, 45);
        this.add(1614, 3831, 0, 46);
        this.add(1619, 3831, 0, 47);
        this.add(1621, 3831, 0, 48);
        this.add(1624, 3831, 0, 49);
        this.add(1626, 3829, 0, 50);
        this.add(1626, 3827, 0, 51);
        this.add(1624, 3823, 0, 52);
        this.add(1622, 3823, 0, 53);
        this.add(1620, 3823, 0, 54);
        this.add(1621, 3822, 0, 55);
        this.add(1624, 3822, 0, 56);
        this.add(1626, 3820, 0, 57);
        this.add(1639, 3821, 0, 58);
        this.add(1639, 3822, 0, 59);
        this.add(1639, 3827, 0, 60);
        this.add(1639, 3829, 0, 61);
        this.add(1642, 3831, 0, 62);
        this.add(1645, 3831, 0, 63);
        this.add(1646, 3829, 0, 64);
        this.add(1646, 3827, 0, 65);
        this.add(1646, 3826, 0, 66);
        this.add(1647, 3827, 0, 67);
        this.add(1647, 3829, 0, 68);
        this.add(1647, 3830, 0, 69);
        this.add(1652, 3831, 0, 70);
        this.add(1653, 3831, 0, 71);
        this.add(1656, 3831, 0, 72);
        this.add(1658, 3829, 0, 73);
        this.add(1658, 3826, 0, 74);
        this.add(1658, 3825, 0, 75);
        this.add(1658, 3820, 0, 76);
        this.add(1658, 3819, 0, 77);
        this.add(1658, 3816, 0, 78);
        this.add(1655, 3814, 0, 79);
        this.add(1654, 3814, 0, 80);
        this.add(1651, 3817, 0, 81);
        this.add(1651, 3819, 0, 82);
        this.add(1651, 3820, 0, 83);
        this.add(1650, 3821, 0, 84);
        this.add(1650, 3819, 0, 85);
        this.add(1650, 3816, 0, 86);
        this.add(1648, 3814, 0, 87);
        this.add(1646, 3814, 0, 88);
        this.add(1645, 3814, 0, 89);
        this.add(1607, 3820, 1, 90);
        this.add(1607, 3821, 1, 91);
        this.add(1609, 3822, 1, 92);
        this.add(1612, 3823, 1, 93);
        this.add(1611, 3823, 1, 94);
        this.add(1607, 3824, 1, 95);
        this.add(1607, 3825, 1, 96);
        this.add(1607, 3827, 1, 97);
        this.add(1611, 3831, 1, 98);
        this.add(1612, 3831, 1, 99);
        this.add(1613, 3831, 1, 100);
        this.add(1617, 3831, 1, 101);
        this.add(1618, 3831, 1, 102);
        this.add(1620, 3831, 1, 103);
        this.add(1624, 3831, 1, 104);
        this.add(1624, 3829, 1, 105);
        this.add(1624, 3825, 1, 106);
        this.add(1624, 3824, 1, 107);
        this.add(1624, 3819, 1, 108);
        this.add(1624, 3817, 1, 109);
        this.add(1623, 3816, 1, 110);
        this.add(1621, 3816, 1, 111);
        this.add(1617, 3816, 1, 112);
        this.add(1616, 3816, 1, 113);
        this.add(1611, 3816, 1, 114);
        this.add(1609, 3816, 1, 115);
        this.add(1620, 3820, 1, 116);
        this.add(1620, 3822, 1, 117);
        this.add(1620, 3824, 1, 118);
        this.add(1620, 3825, 1, 119);
        this.add(1620, 3827, 1, 120);
        this.add(1621, 3826, 1, 121);
        this.add(1621, 3822, 1, 122);
        this.add(1621, 3820, 1, 123);
        this.add(1607, 3788, 1, 124);
        this.add(1607, 3789, 1, 125);
        this.add(1609, 3790, 1, 126);
        this.add(1611, 3790, 1, 127);
        this.add(1613, 3790, 1, 128);
        this.add(1614, 3789, 1, 129);
        this.add(1615, 3788, 1, 130);
        this.add(1615, 3790, 1, 131);
        this.add(1614, 3791, 1, 132);
        this.add(1613, 3791, 1, 133);
        this.add(1610, 3791, 1, 134);
        this.add(1609, 3791, 1, 135);
        this.add(1608, 3791, 1, 136);
        this.add(1607, 3793, 1, 137);
        this.add(1607, 3794, 1, 138);
        this.add(1608, 3799, 1, 139);
        this.add(1610, 3799, 1, 140);
        this.add(1615, 3799, 1, 141);
        this.add(1616, 3799, 1, 142);
        this.add(1621, 3799, 1, 143);
        this.add(1623, 3799, 1, 144);
        this.add(1624, 3798, 1, 145);
        this.add(1624, 3796, 1, 146);
        this.add(1624, 3792, 1, 147);
        this.add(1624, 3791, 1, 148);
        this.add(1623, 3789, 1, 149);
        this.add(1621, 3789, 1, 150);
        this.add(1620, 3788, 1, 151);
        this.add(1621, 3788, 1, 152);
        this.add(1624, 3787, 1, 153);
        this.add(1624, 3786, 1, 154);
        this.add(1619, 3784, 1, 155);
        this.add(1618, 3784, 1, 156);
        this.add(1616, 3784, 1, 157);
        this.add(1612, 3784, 1, 158);
        this.add(1611, 3784, 1, 159);
        this.add(1625, 3801, 1, 160);
        this.add(1625, 3802, 1, 161);
        this.add(1625, 3803, 1, 162);
        this.add(1625, 3804, 1, 163);
        this.add(1625, 3806, 1, 164);
        this.add(1625, 3807, 1, 165);
        this.add(1625, 3808, 1, 166);
        this.add(1625, 3809, 1, 167);
        this.add(1625, 3811, 1, 168);
        this.add(1625, 3812, 1, 169);
        this.add(1625, 3813, 1, 170);
        this.add(1625, 3814, 1, 171);
        this.add(1626, 3815, 1, 172);
        this.add(1627, 3815, 1, 173);
        this.add(1631, 3815, 1, 174);
        this.add(1632, 3815, 1, 175);
        this.add(1633, 3815, 1, 176);
        this.add(1634, 3815, 1, 177);
        this.add(1638, 3815, 1, 178);
        this.add(1639, 3815, 1, 179);
        this.add(1640, 3814, 1, 180);
        this.add(1640, 3813, 1, 181);
        this.add(1640, 3803, 1, 182);
        this.add(1640, 3802, 1, 183);
        this.add(1640, 3801, 1, 184);
        this.add(1639, 3800, 1, 185);
        this.add(1638, 3800, 1, 186);
        this.add(1634, 3800, 1, 187);
        this.add(1633, 3800, 1, 188);
        this.add(1632, 3800, 1, 189);
        this.add(1631, 3800, 1, 190);
        this.add(1627, 3800, 1, 191);
        this.add(1626, 3800, 1, 192);
        this.add(1641, 3817, 1, 193);
        this.add(1641, 3818, 1, 194);
        this.add(1641, 3819, 1, 195);
        this.add(1641, 3824, 1, 196);
        this.add(1641, 3825, 1, 197);
        this.add(1641, 3829, 1, 198);
        this.add(1645, 3831, 1, 199);
        this.add(1646, 3831, 1, 200);
        this.add(1647, 3831, 1, 201);
        this.add(1648, 3831, 1, 202);
        this.add(1649, 3830, 1, 203);
        this.add(1649, 3828, 1, 204);
        this.add(1650, 3829, 1, 205);
        this.add(1652, 3831, 1, 206);
        this.add(1653, 3831, 1, 207);
        this.add(1658, 3827, 1, 208);
        this.add(1658, 3826, 1, 209);
        this.add(1658, 3823, 1, 210);
        this.add(1658, 3822, 1, 211);
        this.add(1658, 3821, 1, 212);
        this.add(1658, 3820, 1, 213);
        this.add(1656, 3816, 1, 214);
        this.add(1655, 3816, 1, 215);
        this.add(1651, 3816, 1, 216);
        this.add(1649, 3816, 1, 217);
        this.add(1648, 3816, 1, 218);
        this.add(1644, 3816, 1, 219);
        this.add(1643, 3816, 1, 220);
        this.add(1607, 3785, 2, 221);
        this.add(1607, 3786, 2, 222);
        this.add(1607, 3796, 2, 223);
        this.add(1607, 3797, 2, 224);
        this.add(1608, 3799, 2, 225);
        this.add(1610, 3799, 2, 226);
        this.add(1611, 3799, 2, 227);
        this.add(1618, 3799, 2, 228);
        this.add(1621, 3799, 2, 229);
        this.add(1624, 3797, 2, 230);
        this.add(1624, 3795, 2, 231);
        this.add(1624, 3794, 2, 232);
        this.add(1624, 3792, 2, 233);
        this.add(1623, 3791, 2, 234);
        this.add(1622, 3791, 2, 235);
        this.add(1618, 3792, 2, 236);
        this.add(1618, 3793, 2, 237);
        this.add(1618, 3794, 2, 238);
        this.add(1617, 3793, 2, 239);
        this.add(1617, 3792, 2, 240);
        this.add(1618, 3790, 2, 241);
        this.add(1620, 3790, 2, 242);
        this.add(1622, 3790, 2, 243);
        this.add(1624, 3789, 2, 244);
        this.add(1624, 3788, 2, 245);
        this.add(1624, 3786, 2, 246);
        this.add(1624, 3785, 2, 247);
        this.add(1623, 3784, 2, 248);
        this.add(1621, 3784, 2, 249);
        this.add(1611, 3784, 2, 250);
        this.add(1609, 3784, 2, 251);
        this.add(1612, 3789, 2, 252);
        this.add(1612, 3791, 2, 253);
        this.add(1612, 3794, 2, 254);
        this.add(1613, 3793, 2, 255);
        this.add(1613, 3792, 2, 256);
        this.add(1613, 3791, 2, 257);
        this.add(1617, 3791, 2, 258);
        this.add(1617, 3793, 2, 259);
        this.add(1618, 3794, 2, 260);
        this.add(1618, 3792, 2, 261);
        this.add(1619, 3791, 2, 262);
        this.add(1623, 3791, 2, 263);
        this.add(1623, 3790, 2, 264);
        this.add(1622, 3790, 2, 265);
        this.add(1619, 3790, 2, 266);
        this.add(1611, 3816, 2, 267);
        this.add(1610, 3816, 2, 268);
        this.add(1609, 3816, 2, 269);
        this.add(1607, 3817, 2, 270);
        this.add(1607, 3819, 2, 271);
        this.add(1607, 3829, 2, 272);
        this.add(1608, 3831, 2, 273);
        this.add(1610, 3831, 2, 274);
        this.add(1611, 3831, 2, 275);
        this.add(1622, 3831, 2, 276);
        this.add(1623, 3831, 2, 277);
        this.add(1624, 3829, 2, 278);
        this.add(1624, 3828, 2, 279);
        this.add(1624, 3821, 2, 280);
        this.add(1624, 3819, 2, 281);
        this.add(1622, 3816, 2, 282);
        this.add(1620, 3816, 2, 283);
        this.add(1618, 3816, 2, 284);
        this.add(1615, 3821, 2, 285);
        this.add(1617, 3821, 2, 286);
        this.add(1619, 3822, 2, 287);
        this.add(1619, 3824, 2, 288);
        this.add(1618, 3826, 2, 289);
        this.add(1617, 3826, 2, 290);
        this.add(1615, 3827, 2, 291);
        this.add(1616, 3827, 2, 292);
        this.add(1618, 3827, 2, 293);
        this.add(1620, 3826, 2, 294);
        this.add(1620, 3824, 2, 295);
        this.add(1620, 3822, 2, 296);
        this.add(1620, 3821, 2, 297);
        this.add(1619, 3820, 2, 298);
        this.add(1617, 3820, 2, 299);
        this.add(1615, 3820, 2, 300);
        this.add(1641, 3818, 2, 301);
        this.add(1641, 3820, 2, 302);
        this.add(1641, 3821, 2, 303);
        this.add(1641, 3829, 2, 304);
        this.add(1643, 3831, 2, 305);
        this.add(1644, 3831, 2, 306);
        this.add(1654, 3831, 2, 307);
        this.add(1656, 3831, 2, 308);
        this.add(1658, 3830, 2, 309);
        this.add(1658, 3828, 2, 310);
        this.add(1658, 3818, 2, 311);
        this.add(1658, 3817, 2, 312);
        this.add(1656, 3816, 2, 313);
        this.add(1655, 3816, 2, 314);
        this.add(1652, 3816, 2, 315);
        this.add(1648, 3817, 2, 316);
        this.add(1648, 3819, 2, 317);
        this.add(1648, 3821, 2, 318);
        this.add(1649, 3823, 2, 319);
        this.add(1650, 3823, 2, 320);
        this.add(1652, 3823, 2, 321);
        this.add(1654, 3822, 2, 322);
        this.add(1654, 3820, 2, 323);
        this.add(1655, 3820, 2, 324);
        this.add(1655, 3821, 2, 325);
        this.add(1655, 3823, 2, 326);
        this.add(1653, 3824, 2, 327);
        this.add(1652, 3824, 2, 328);
        this.add(1649, 3824, 2, 329);
        this.add(1648, 3824, 2, 330);
        this.add(1647, 3822, 2, 331);
        this.add(1647, 3820, 2, 332);
        this.add(1647, 3818, 2, 333);
        this.add(1645, 3816, 2, 334);
        this.add(1644, 3816, 2, 335);
        this.add(1625, 3802, 2, 336);
        this.add(1625, 3804, 2, 337);
        this.add(1625, 3811, 2, 338);
        this.add(1625, 3812, 2, 339);
        this.add(1627, 3815, 2, 340);
        this.add(1628, 3815, 2, 341);
        this.add(1635, 3815, 2, 342);
        this.add(1637, 3815, 2, 343);
        this.add(1638, 3815, 2, 344);
        this.add(1640, 3813, 2, 345);
        this.add(1640, 3811, 2, 346);
        this.add(1640, 3810, 2, 347);
        this.add(1638, 3800, 2, 348);
        this.add(1632, 3800, 2, 349);
        this.add(1630, 3800, 2, 350);
        this.add(1629, 3800, 2, 351);
        this.add(1627, 3800, 2, 352);
    }

    public SolvedState getState() {
        return this.state;
    }

    public Book getCustomerBook() {
        return this.customerBook;
    }

    public int getCustomerId() {
        return this.customerId;
    }
}

