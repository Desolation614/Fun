/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.EvictingQueue
 *  com.google.inject.Inject
 *  javax.inject.Singleton
 *  lombok.NonNull
 *  net.runelite.api.Actor
 *  net.runelite.api.Client
 *  net.runelite.api.Friend
 *  net.runelite.api.FriendContainer
 *  net.runelite.api.FriendsChatManager
 *  net.runelite.api.FriendsChatMember
 *  net.runelite.api.Nameable
 *  net.runelite.api.WorldView
 *  net.runelite.api.clan.ClanMember
 *  net.runelite.api.clan.ClanSettings
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package net.runelite.client.plugins.hiscore;

import com.google.common.collect.EvictingQueue;
import com.google.inject.Inject;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Pattern;
import javax.inject.Singleton;
import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import lombok.NonNull;
import net.runelite.api.Actor;
import net.runelite.api.Client;
import net.runelite.api.Friend;
import net.runelite.api.FriendContainer;
import net.runelite.api.FriendsChatManager;
import net.runelite.api.FriendsChatMember;
import net.runelite.api.Nameable;
import net.runelite.api.WorldView;
import net.runelite.api.clan.ClanMember;
import net.runelite.api.clan.ClanSettings;
import net.runelite.client.plugins.hiscore.HiscoreConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
class NameAutocompleter
implements KeyListener {
    private static final Logger log = LoggerFactory.getLogger(NameAutocompleter.class);
    private static final String NBSP = Character.toString('\u00a0');
    private static final Pattern INVALID_CHARS = Pattern.compile("[^a-zA-Z0-9_ -]");
    private static final int MAX_SEARCH_HISTORY = 25;
    private final Client client;
    private final HiscoreConfig hiscoreConfig;
    private final EvictingQueue<String> searchHistory = EvictingQueue.create((int)25);
    private String autocompleteName;
    private Pattern autocompleteNamePattern;

    @Inject
    private NameAutocompleter(Client client, HiscoreConfig hiscoreConfig) {
        this.client = client;
        this.hiscoreConfig = hiscoreConfig;
    }

    @Override
    public void keyPressed(KeyEvent e) {
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

    @Override
    public void keyTyped(KeyEvent e) {
        if (!this.hiscoreConfig.autocomplete()) {
            return;
        }
        JTextComponent input = (JTextComponent)e.getSource();
        String inputText = input.getText();
        if (input.getSelectionEnd() != inputText.length()) {
            return;
        }
        String charToInsert = Character.toString(e.getKeyChar());
        if (INVALID_CHARS.matcher(charToInsert).find() || INVALID_CHARS.matcher(inputText).find()) {
            return;
        }
        if (this.autocompleteName != null && this.autocompleteNamePattern.matcher(inputText).matches()) {
            if (this.isExpectedNext(input, charToInsert)) {
                try {
                    int insertIndex = input.getSelectionStart();
                    Document doc = input.getDocument();
                    doc.remove(insertIndex, 1);
                    doc.insertString(insertIndex, charToInsert, null);
                    input.select(insertIndex + 1, input.getSelectionEnd());
                }
                catch (BadLocationException ex) {
                    log.warn("Could not insert character.", (Throwable)ex);
                }
                e.consume();
            } else {
                this.newAutocomplete(e);
            }
        } else {
            this.newAutocomplete(e);
        }
    }

    private void newAutocomplete(KeyEvent e) {
        JTextComponent input = (JTextComponent)e.getSource();
        String inputText = input.getText();
        String nameStart = inputText.substring(0, input.getSelectionStart()) + e.getKeyChar();
        if (this.findAutocompleteName(nameStart)) {
            String name = this.autocompleteName;
            SwingUtilities.invokeLater(() -> {
                try {
                    input.getDocument().insertString(nameStart.length(), name.substring(nameStart.length()), null);
                    input.select(nameStart.length(), name.length());
                }
                catch (BadLocationException ex) {
                    log.warn("Could not autocomplete name.", (Throwable)ex);
                }
            });
        }
    }

    private boolean findAutocompleteName(String nameStart) {
        FriendsChatManager friendsChatManager;
        FriendContainer friendContainer;
        Pattern pattern = Pattern.compile("(?i)^" + nameStart.replaceAll("[ _-]", "[ _" + NBSP + "-]") + ".+?");
        Optional<String> autocompleteName = this.searchHistory.stream().filter(n -> pattern.matcher((CharSequence)n).matches()).findFirst();
        if (!autocompleteName.isPresent() && (friendContainer = this.client.getFriendContainer()) != null) {
            autocompleteName = Arrays.stream((Friend[])friendContainer.getMembers()).map(Nameable::getName).filter(n -> pattern.matcher((CharSequence)n).matches()).findFirst();
        }
        if (!autocompleteName.isPresent() && (friendsChatManager = this.client.getFriendsChatManager()) != null) {
            autocompleteName = Arrays.stream((FriendsChatMember[])friendsChatManager.getMembers()).map(Nameable::getName).filter(n -> pattern.matcher((CharSequence)n).matches()).findFirst();
        }
        if (!autocompleteName.isPresent()) {
            ClanSettings[] clanSettings = new ClanSettings[]{this.client.getClanSettings(0), this.client.getClanSettings(1), this.client.getGuestClanSettings()};
            autocompleteName = Arrays.stream(clanSettings).filter(Objects::nonNull).flatMap(cs -> cs.getMembers().stream()).map(ClanMember::getName).filter(n -> pattern.matcher((CharSequence)n).matches()).findFirst();
        }
        if (!autocompleteName.isPresent()) {
            WorldView wv = this.client.getTopLevelWorldView();
            autocompleteName = wv.players().stream().filter(Objects::nonNull).map(Actor::getName).filter(Objects::nonNull).filter(n -> pattern.matcher((CharSequence)n).matches()).findFirst();
        }
        if (autocompleteName.isPresent()) {
            this.autocompleteName = autocompleteName.get().replace(NBSP, " ");
            this.autocompleteNamePattern = Pattern.compile("(?i)^" + this.autocompleteName.replaceAll("[ _-]", "[ _-]") + "$");
        } else {
            this.autocompleteName = null;
            this.autocompleteNamePattern = null;
        }
        return autocompleteName.isPresent();
    }

    void addToSearchHistory(@NonNull String name) {
        if (name == null) {
            throw new NullPointerException("name is marked non-null but is null");
        }
        if (!this.searchHistory.contains((Object)name)) {
            this.searchHistory.offer((Object)name);
        }
    }

    private boolean isExpectedNext(JTextComponent input, String nextChar) {
        String expected;
        if (input.getSelectionStart() < input.getSelectionEnd()) {
            try {
                expected = input.getText(input.getSelectionStart(), 1);
            }
            catch (BadLocationException ex) {
                log.warn("Could not get first character from input selection.", (Throwable)ex);
                return false;
            }
        } else {
            expected = "";
        }
        return nextChar.equalsIgnoreCase(expected);
    }
}

