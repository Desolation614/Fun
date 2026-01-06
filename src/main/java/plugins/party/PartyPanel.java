/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.inject.Inject
 */
package net.runelite.client.plugins.party;

import com.google.inject.Inject;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.party.PartyService;
import net.runelite.client.plugins.party.PartyConfig;
import net.runelite.client.plugins.party.PartyMemberBox;
import net.runelite.client.plugins.party.PartyPlugin;
import net.runelite.client.plugins.party.data.PartyData;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.PluginPanel;
import net.runelite.client.ui.components.DragAndDropReorderPane;
import net.runelite.client.ui.components.PluginErrorPanel;

class PartyPanel
extends PluginPanel {
    private static final String BTN_CREATE_TEXT = "Create party";
    private static final String BTN_LEAVE_TEXT = "Leave";
    private final PartyPlugin plugin;
    private final PartyService party;
    private final PartyConfig config;
    private final Map<Long, PartyMemberBox> memberBoxes = new HashMap<Long, PartyMemberBox>();
    private final JButton startButton = new JButton();
    private final JButton joinPartyButton = new JButton();
    private final JButton rejoinPartyButton = new JButton();
    private final JButton copyPartyIdButton = new JButton();
    private final PluginErrorPanel noPartyPanel = new PluginErrorPanel();
    private final PluginErrorPanel partyEmptyPanel = new PluginErrorPanel();
    private final JComponent memberBoxPanel = new DragAndDropReorderPane();

    @Inject
    PartyPanel(ClientThread clientThread, PartyPlugin plugin, PartyConfig config, PartyService party) {
        this.plugin = plugin;
        this.party = party;
        this.config = config;
        this.setBorder(new EmptyBorder(10, 10, 10, 10));
        this.setBackground(ColorScheme.DARK_GRAY_COLOR);
        this.setLayout(new BorderLayout());
        JPanel layoutPanel = new JPanel();
        BoxLayout boxLayout = new BoxLayout(layoutPanel, 1);
        layoutPanel.setLayout(boxLayout);
        this.add((Component)layoutPanel, "North");
        JPanel topPanel = new JPanel();
        topPanel.setBorder(new EmptyBorder(0, 0, 4, 0));
        topPanel.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.fill = 2;
        c.insets = new Insets(0, 2, 4, 2);
        c.gridx = 0;
        c.gridy = 0;
        topPanel.add((Component)this.startButton, c);
        c.gridx = 1;
        c.gridy = 0;
        topPanel.add((Component)this.joinPartyButton, c);
        c.gridx = 1;
        c.gridy = 0;
        topPanel.add((Component)this.copyPartyIdButton, c);
        c.gridx = 0;
        c.gridy = 1;
        c.gridwidth = 2;
        topPanel.add((Component)this.rejoinPartyButton, c);
        layoutPanel.add(topPanel);
        layoutPanel.add(this.memberBoxPanel);
        this.startButton.setText(party.isInParty() ? BTN_LEAVE_TEXT : BTN_CREATE_TEXT);
        this.startButton.setFocusable(false);
        this.joinPartyButton.setText("Join party");
        this.joinPartyButton.setFocusable(false);
        this.rejoinPartyButton.setText("Join previous party");
        this.rejoinPartyButton.setFocusable(false);
        this.copyPartyIdButton.setText("Copy passphrase");
        this.copyPartyIdButton.setFocusable(false);
        this.startButton.addActionListener(e -> {
            if (party.isInParty()) {
                int result = JOptionPane.showOptionDialog(this.startButton, "Are you sure you want to leave the party?", "Leave party?", 0, 2, null, new String[]{"Yes", "No"}, "No");
                if (result == 0) {
                    plugin.leaveParty();
                }
            } else {
                clientThread.invokeLater(() -> party.changeParty(party.generatePassphrase()));
            }
        });
        this.joinPartyButton.addActionListener(e -> {
            if (!party.isInParty()) {
                String s = (String)JOptionPane.showInputDialog(this.joinPartyButton, "Please enter the party passphrase:", "Party Passphrase", -1, null, null, "");
                if (s == null) {
                    return;
                }
                s = s.toLowerCase(Locale.US);
                for (int i = 0; i < s.length(); ++i) {
                    char ch = s.charAt(i);
                    if (Character.isLetter(ch) || Character.isDigit(ch) || ch == '-') continue;
                    JOptionPane.showMessageDialog(this.joinPartyButton, "Party passphrase must be a combination of alphanumeric or hyphen characters.", "Invalid party passphrase", 0);
                    return;
                }
                party.changeParty(s);
            }
        });
        this.rejoinPartyButton.addActionListener(e -> {
            if (!party.isInParty()) {
                party.changeParty(config.previousPartyId());
            }
        });
        this.copyPartyIdButton.addActionListener(e -> {
            if (party.isInParty()) {
                Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                clipboard.setContents(new StringSelection(party.getPartyPassphrase()), null);
            }
        });
        this.noPartyPanel.setContent("Not in a party", "Create a party to begin.");
        this.updateParty();
    }

    void updateParty() {
        this.remove(this.noPartyPanel);
        this.remove(this.partyEmptyPanel);
        this.startButton.setText(this.party.isInParty() ? BTN_LEAVE_TEXT : BTN_CREATE_TEXT);
        this.joinPartyButton.setVisible(!this.party.isInParty());
        this.rejoinPartyButton.setVisible(!this.party.isInParty());
        this.copyPartyIdButton.setVisible(this.party.isInParty());
        if (!this.party.isInParty()) {
            this.add(this.noPartyPanel);
        } else if (this.plugin.getPartyDataMap().size() <= 1) {
            this.partyEmptyPanel.setContent("Party created", "You can now invite friends!<br/>Your party passphrase is: " + this.party.getPartyPassphrase() + ".");
            this.add(this.partyEmptyPanel);
        }
    }

    void addMember(PartyData partyData) {
        if (!this.memberBoxes.containsKey(partyData.getMemberId())) {
            PartyMemberBox partyMemberBox = new PartyMemberBox(this.config, this.memberBoxPanel, partyData, this.party);
            this.memberBoxes.put(partyData.getMemberId(), partyMemberBox);
            this.memberBoxPanel.add(partyMemberBox);
            this.memberBoxPanel.revalidate();
        }
        this.updateParty();
    }

    void removeAllMembers() {
        this.memberBoxes.forEach((key, value) -> this.memberBoxPanel.remove((Component)value));
        this.memberBoxPanel.revalidate();
        this.memberBoxes.clear();
        this.updateParty();
    }

    void removeMember(long memberId) {
        PartyMemberBox memberBox = this.memberBoxes.remove(memberId);
        if (memberBox != null) {
            this.memberBoxPanel.remove(memberBox);
            this.memberBoxPanel.revalidate();
        }
        this.updateParty();
    }

    void updateMember(long userId) {
        PartyMemberBox memberBox = this.memberBoxes.get(userId);
        if (memberBox != null) {
            memberBox.update();
        }
    }

    void updateAll() {
        this.memberBoxes.forEach((key, value) -> value.update());
    }
}

