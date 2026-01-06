/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.plugins.party;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import net.runelite.client.party.PartyMember;
import net.runelite.client.party.PartyService;
import net.runelite.client.plugins.party.PartyConfig;
import net.runelite.client.plugins.party.data.PartyData;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.DynamicGridLayout;
import net.runelite.client.ui.FontManager;
import net.runelite.client.ui.components.MouseDragEventForwarder;
import net.runelite.client.ui.components.ProgressBar;
import net.runelite.client.util.ImageUtil;

class PartyMemberBox
extends JPanel {
    private static final Color HP_FG = new Color(0, 146, 54, 230);
    private static final Color HP_BG = new Color(102, 15, 16, 230);
    private static final Color PRAY_FG = new Color(0, 149, 151);
    private static final Color PRAY_BG = Color.black;
    private final PartyData memberPartyData;
    private final PartyService partyService;
    private final ProgressBar hpBar = new ProgressBar();
    private final ProgressBar prayerBar = new ProgressBar();
    private final JLabel name = new JLabel();
    private final JLabel avatar = new JLabel();
    private final PartyConfig config;
    private boolean avatarSet;

    PartyMemberBox(PartyConfig config, JComponent panel, PartyData memberPartyData, PartyService partyService) {
        this.config = config;
        this.memberPartyData = memberPartyData;
        this.partyService = partyService;
        this.setLayout(new BorderLayout());
        this.setBorder(new EmptyBorder(5, 0, 0, 0));
        JPanel container = new JPanel();
        container.setLayout(new BorderLayout());
        container.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        container.setBorder(new EmptyBorder(5, 5, 5, 5));
        Border border = BorderFactory.createLineBorder(Color.gray, 1);
        this.avatar.setBorder(border);
        this.avatar.setHorizontalAlignment(0);
        this.avatar.setVerticalAlignment(0);
        this.avatar.setPreferredSize(new Dimension(35, 35));
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        headerPanel.setLayout(new BorderLayout());
        headerPanel.setBorder(new EmptyBorder(0, 0, 3, 0));
        JPanel namesPanel = new JPanel();
        namesPanel.setLayout(new DynamicGridLayout(2, 1));
        namesPanel.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        namesPanel.setBorder(new EmptyBorder(2, 5, 2, 5));
        this.name.setFont(FontManager.getRunescapeSmallFont());
        this.name.putClientProperty("html.disable", Boolean.TRUE);
        namesPanel.add(this.name);
        headerPanel.add((Component)this.avatar, "West");
        headerPanel.add((Component)namesPanel, "Center");
        JPanel progressWrapper = new JPanel();
        progressWrapper.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        progressWrapper.setLayout(new DynamicGridLayout(2, 1, 0, 2));
        this.hpBar.setBackground(HP_BG);
        this.hpBar.setForeground(HP_FG);
        this.prayerBar.setBackground(PRAY_BG);
        this.prayerBar.setForeground(PRAY_FG);
        progressWrapper.add(this.hpBar);
        progressWrapper.add(this.prayerBar);
        container.add((Component)headerPanel, "North");
        container.add((Component)progressWrapper, "South");
        MouseDragEventForwarder mouseDragEventForwarder = new MouseDragEventForwarder(panel);
        container.addMouseListener(mouseDragEventForwarder);
        container.addMouseMotionListener(mouseDragEventForwarder);
        this.add((Component)container, "North");
        this.update();
    }

    void update() {
        PartyMember member = this.partyService.getMemberById(this.memberPartyData.getMemberId());
        if (!this.avatarSet && member.getAvatar() != null) {
            ImageIcon icon = new ImageIcon(ImageUtil.resizeImage(member.getAvatar(), 32, 32));
            icon.getImage().flush();
            this.avatar.setIcon(icon);
            this.avatarSet = true;
        }
        this.hpBar.setValue(this.memberPartyData.getHitpoints());
        this.hpBar.setMaximumValue(this.memberPartyData.getMaxHitpoints());
        this.hpBar.setCenterLabel(PartyMemberBox.progressBarLabel(this.memberPartyData.getHitpoints(), this.memberPartyData.getMaxHitpoints()));
        this.prayerBar.setValue(this.memberPartyData.getPrayer());
        this.prayerBar.setMaximumValue(this.memberPartyData.getMaxPrayer());
        this.prayerBar.setCenterLabel(PartyMemberBox.progressBarLabel(this.memberPartyData.getPrayer(), this.memberPartyData.getMaxPrayer()));
        Color playerColor = this.config.recolorNames() ? this.memberPartyData.getColor() : Color.WHITE;
        boolean isLoggedIn = member.isLoggedIn();
        this.name.setForeground(isLoggedIn ? playerColor : Color.GRAY);
        this.name.setText(member.getDisplayName());
    }

    private static String progressBarLabel(int current, int max) {
        return current + "/" + max;
    }

    PartyData getMemberPartyData() {
        return this.memberPartyData;
    }
}

