/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.base.Strings
 *  com.google.common.collect.ImmutableList
 *  javax.inject.Inject
 *  net.runelite.api.Client
 *  net.runelite.api.Experience
 *  net.runelite.api.Player
 *  org.apache.commons.lang3.ArrayUtils
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package net.runelite.client.plugins.hiscore;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import net.runelite.api.Client;
import net.runelite.api.Experience;
import net.runelite.api.Player;
import net.runelite.client.game.SpriteManager;
import net.runelite.client.hiscore.HiscoreClient;
import net.runelite.client.hiscore.HiscoreEndpoint;
import net.runelite.client.hiscore.HiscoreResult;
import net.runelite.client.hiscore.HiscoreSkill;
import net.runelite.client.hiscore.HiscoreSkillType;
import net.runelite.client.hiscore.Skill;
import net.runelite.client.plugins.hiscore.HiscoreConfig;
import net.runelite.client.plugins.hiscore.HiscorePlugin;
import net.runelite.client.plugins.hiscore.NameAutocompleter;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.FontManager;
import net.runelite.client.ui.PluginPanel;
import net.runelite.client.ui.components.IconTextField;
import net.runelite.client.ui.components.materialtabs.MaterialTab;
import net.runelite.client.ui.components.materialtabs.MaterialTabGroup;
import net.runelite.client.util.ImageUtil;
import net.runelite.client.util.QuantityFormatter;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HiscorePanel
extends PluginPanel {
    private static final Logger log = LoggerFactory.getLogger(HiscorePanel.class);
    private static final int MAX_USERNAME_LENGTH = 12;
    private static final List<HiscoreSkill> SKILLS = ImmutableList.of((Object)((Object)HiscoreSkill.ATTACK), (Object)((Object)HiscoreSkill.HITPOINTS), (Object)((Object)HiscoreSkill.MINING), (Object)((Object)HiscoreSkill.STRENGTH), (Object)((Object)HiscoreSkill.AGILITY), (Object)((Object)HiscoreSkill.SMITHING), (Object)((Object)HiscoreSkill.DEFENCE), (Object)((Object)HiscoreSkill.HERBLORE), (Object)((Object)HiscoreSkill.FISHING), (Object)((Object)HiscoreSkill.RANGED), (Object)((Object)HiscoreSkill.THIEVING), (Object)((Object)HiscoreSkill.COOKING), (Object[])new HiscoreSkill[]{HiscoreSkill.PRAYER, HiscoreSkill.CRAFTING, HiscoreSkill.FIREMAKING, HiscoreSkill.MAGIC, HiscoreSkill.FLETCHING, HiscoreSkill.WOODCUTTING, HiscoreSkill.RUNECRAFT, HiscoreSkill.SLAYER, HiscoreSkill.FARMING, HiscoreSkill.CONSTRUCTION, HiscoreSkill.HUNTER});
    private static final List<HiscoreSkill> BOSSES = ImmutableList.of((Object)((Object)HiscoreSkill.ABYSSAL_SIRE), (Object)((Object)HiscoreSkill.ALCHEMICAL_HYDRA), (Object)((Object)HiscoreSkill.AMOXLIATL), (Object)((Object)HiscoreSkill.ARAXXOR), (Object)((Object)HiscoreSkill.ARTIO), (Object)((Object)HiscoreSkill.BARROWS_CHESTS), (Object)((Object)HiscoreSkill.BRYOPHYTA), (Object)((Object)HiscoreSkill.CALLISTO), (Object)((Object)HiscoreSkill.CALVARION), (Object)((Object)HiscoreSkill.CERBERUS), (Object)((Object)HiscoreSkill.CHAMBERS_OF_XERIC), (Object)((Object)HiscoreSkill.CHAMBERS_OF_XERIC_CHALLENGE_MODE), (Object[])new HiscoreSkill[]{HiscoreSkill.CHAOS_ELEMENTAL, HiscoreSkill.CHAOS_FANATIC, HiscoreSkill.COMMANDER_ZILYANA, HiscoreSkill.CORPOREAL_BEAST, HiscoreSkill.CRAZY_ARCHAEOLOGIST, HiscoreSkill.DAGANNOTH_PRIME, HiscoreSkill.DAGANNOTH_REX, HiscoreSkill.DAGANNOTH_SUPREME, HiscoreSkill.DERANGED_ARCHAEOLOGIST, HiscoreSkill.DOOM_OF_MOKHAIOTL, HiscoreSkill.DUKE_SUCELLUS, HiscoreSkill.GENERAL_GRAARDOR, HiscoreSkill.GIANT_MOLE, HiscoreSkill.GROTESQUE_GUARDIANS, HiscoreSkill.HESPORI, HiscoreSkill.KALPHITE_QUEEN, HiscoreSkill.KING_BLACK_DRAGON, HiscoreSkill.KRAKEN, HiscoreSkill.KREEARRA, HiscoreSkill.KRIL_TSUTSAROTH, HiscoreSkill.LUNAR_CHESTS, HiscoreSkill.MIMIC, HiscoreSkill.NEX, HiscoreSkill.NIGHTMARE, HiscoreSkill.PHOSANIS_NIGHTMARE, HiscoreSkill.OBOR, HiscoreSkill.PHANTOM_MUSPAH, HiscoreSkill.SARACHNIS, HiscoreSkill.SCORPIA, HiscoreSkill.SCURRIUS, HiscoreSkill.SKOTIZO, HiscoreSkill.SOL_HEREDIT, HiscoreSkill.SPINDEL, HiscoreSkill.TEMPOROSS, HiscoreSkill.THE_GAUNTLET, HiscoreSkill.THE_CORRUPTED_GAUNTLET, HiscoreSkill.THE_HUEYCOATL, HiscoreSkill.THE_LEVIATHAN, HiscoreSkill.THE_ROYAL_TITANS, HiscoreSkill.THE_WHISPERER, HiscoreSkill.THEATRE_OF_BLOOD, HiscoreSkill.THEATRE_OF_BLOOD_HARD_MODE, HiscoreSkill.THERMONUCLEAR_SMOKE_DEVIL, HiscoreSkill.TOMBS_OF_AMASCUT, HiscoreSkill.TOMBS_OF_AMASCUT_EXPERT, HiscoreSkill.TZKAL_ZUK, HiscoreSkill.TZTOK_JAD, HiscoreSkill.VARDORVIS, HiscoreSkill.VENENATIS, HiscoreSkill.VETION, HiscoreSkill.VORKATH, HiscoreSkill.WINTERTODT, HiscoreSkill.YAMA, HiscoreSkill.ZALCANO, HiscoreSkill.ZULRAH});
    private static final HiscoreEndpoint[] ENDPOINTS = new HiscoreEndpoint[]{HiscoreEndpoint.NORMAL, HiscoreEndpoint.IRONMAN, HiscoreEndpoint.HARDCORE_IRONMAN, HiscoreEndpoint.ULTIMATE_IRONMAN, HiscoreEndpoint.DEADMAN, HiscoreEndpoint.PURE, HiscoreEndpoint.LEVEL_3_SKILLER, HiscoreEndpoint.LEAGUE};
    private final HiscorePlugin plugin;
    private final HiscoreConfig config;
    private final NameAutocompleter nameAutocompleter;
    private final HiscoreClient hiscoreClient;
    private final SpriteManager spriteManager;
    private final IconTextField searchBar;
    private final Map<HiscoreSkill, JLabel> skillLabels = new HashMap<HiscoreSkill, JLabel>();
    private final MaterialTabGroup tabGroup;
    private HiscoreEndpoint selectedEndPoint;
    private boolean loading = false;

    @Inject
    public HiscorePanel(final Client client, final HiscorePlugin plugin, HiscoreConfig config, NameAutocompleter nameAutocompleter, HiscoreClient hiscoreClient, SpriteManager spriteManager) {
        this.plugin = plugin;
        this.config = config;
        this.nameAutocompleter = nameAutocompleter;
        this.hiscoreClient = hiscoreClient;
        this.spriteManager = spriteManager;
        this.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));
        this.setBackground(ColorScheme.DARK_GRAY_COLOR);
        this.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.fill = 2;
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 1.0;
        c.weighty = 0.0;
        c.insets = new Insets(0, 0, 10, 0);
        this.searchBar = new IconTextField();
        this.searchBar.setIcon(IconTextField.Icon.SEARCH);
        this.searchBar.setPreferredSize(new Dimension(205, 30));
        this.searchBar.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        this.searchBar.setHoverBackgroundColor(ColorScheme.DARK_GRAY_HOVER_COLOR);
        this.searchBar.setMinimumSize(new Dimension(0, 30));
        this.searchBar.addActionListener(e -> this.lookup());
        this.searchBar.addMouseListener(new MouseAdapter(){

            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() != 2) {
                    return;
                }
                Player localPlayer = client.getLocalPlayer();
                if (localPlayer != null) {
                    HiscorePanel.this.lookup(localPlayer.getName(), plugin.getLocalHiscoreEndpoint());
                }
            }
        });
        this.searchBar.addClearListener(() -> {
            this.searchBar.setIcon(IconTextField.Icon.SEARCH);
            this.searchBar.setEditable(true);
            this.loading = false;
        });
        this.add((Component)this.searchBar, c);
        ++c.gridy;
        this.tabGroup = new MaterialTabGroup();
        this.tabGroup.setLayout(new GridLayout(2, 4, 7, 7));
        for (HiscoreEndpoint endpoint : ENDPOINTS) {
            BufferedImage iconImage = ImageUtil.loadImageResource(this.getClass(), endpoint.name().toLowerCase() + ".png");
            MaterialTab tab = new MaterialTab(new ImageIcon(iconImage), this.tabGroup, null);
            tab.setToolTipText(endpoint.getName() + " Hiscores");
            tab.setOnSelectEvent(() -> {
                if (this.loading) {
                    return false;
                }
                this.selectedEndPoint = endpoint;
                return true;
            });
            tab.addMouseListener(new MouseAdapter(){

                @Override
                public void mousePressed(MouseEvent mouseEvent) {
                    if (HiscorePanel.this.loading) {
                        return;
                    }
                    HiscorePanel.this.lookup();
                }
            });
            this.tabGroup.addTab(tab);
        }
        this.resetEndpoints();
        this.add((Component)this.tabGroup, c);
        ++c.gridy;
        JPanel statsPanel = new JPanel();
        statsPanel.setLayout(new GridLayout(8, 3));
        statsPanel.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        statsPanel.setBorder(new EmptyBorder(5, 0, 5, 0));
        for (HiscoreSkill skill : SKILLS) {
            JPanel panel = this.makeHiscorePanel(skill);
            statsPanel.add(panel);
        }
        this.add((Component)statsPanel, c);
        ++c.gridy;
        JPanel totalPanel = new JPanel();
        totalPanel.setLayout(new GridLayout(1, 2));
        totalPanel.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        totalPanel.add(this.makeHiscorePanel(null));
        totalPanel.add(this.makeHiscorePanel(HiscoreSkill.OVERALL));
        this.add((Component)totalPanel, c);
        ++c.gridy;
        JPanel minigamePanel = new JPanel();
        minigamePanel.setLayout(new GridLayout(0, 3));
        minigamePanel.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        minigamePanel.add(this.makeHiscorePanel(HiscoreSkill.CLUE_SCROLL_ALL));
        minigamePanel.add(this.makeHiscorePanel(HiscoreSkill.LEAGUE_POINTS));
        minigamePanel.add(this.makeHiscorePanel(HiscoreSkill.LAST_MAN_STANDING));
        minigamePanel.add(this.makeHiscorePanel(HiscoreSkill.SOUL_WARS_ZEAL));
        minigamePanel.add(this.makeHiscorePanel(HiscoreSkill.RIFTS_CLOSED));
        minigamePanel.add(this.makeHiscorePanel(HiscoreSkill.COLOSSEUM_GLORY));
        minigamePanel.add(this.makeHiscorePanel(HiscoreSkill.COLLECTIONS_LOGGED));
        minigamePanel.add(this.makeHiscorePanel(HiscoreSkill.BOUNTY_HUNTER_ROGUE));
        minigamePanel.add(this.makeHiscorePanel(HiscoreSkill.BOUNTY_HUNTER_HUNTER));
        minigamePanel.add(this.makeHiscorePanel(HiscoreSkill.PVP_ARENA_RANK));
        this.add((Component)minigamePanel, c);
        ++c.gridy;
        JPanel bossPanel = new JPanel();
        bossPanel.setLayout(new GridLayout(0, 3));
        bossPanel.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        for (HiscoreSkill skill : BOSSES) {
            JPanel panel = this.makeHiscorePanel(skill);
            bossPanel.add(panel);
        }
        this.add((Component)bossPanel, c);
        ++c.gridy;
        this.addInputKeyListener(nameAutocompleter);
    }

    void shutdown() {
        this.removeInputKeyListener(this.nameAutocompleter);
    }

    @Override
    public void onActivate() {
        super.onActivate();
        this.searchBar.requestFocusInWindow();
    }

    private JPanel makeHiscorePanel(HiscoreSkill skill) {
        HiscoreSkillType skillType = skill == null ? HiscoreSkillType.SKILL : skill.getType();
        JLabel label = new JLabel();
        label.setToolTipText(skill == null ? "Combat" : skill.getName());
        label.setFont(FontManager.getRunescapeSmallFont());
        label.setText(HiscorePanel.pad("--", skillType));
        this.spriteManager.getSpriteAsync(skill == null ? 168 : skill.getSpriteId(), 0, sprite -> SwingUtilities.invokeLater(() -> {
            BufferedImage scaledSprite = ImageUtil.resizeImage(ImageUtil.resizeCanvas(sprite, 25, 25), 20, 20);
            label.setIcon(new ImageIcon(scaledSprite));
        }));
        boolean totalLabel = skill == HiscoreSkill.OVERALL || skill == null;
        label.setIconTextGap(totalLabel ? 10 : 4);
        JPanel skillPanel = new JPanel();
        skillPanel.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        skillPanel.setBorder(new EmptyBorder(2, 0, 2, 0));
        this.skillLabels.put(skill, label);
        skillPanel.add(label);
        return skillPanel;
    }

    public void lookup(String username, HiscoreEndpoint endpoint) {
        this.searchBar.setText(username);
        this.tabGroup.select(this.tabGroup.getTab(ArrayUtils.indexOf((Object[])ENDPOINTS, (Object)((Object)endpoint))));
        this.lookup();
    }

    private void lookup() {
        String lookup = HiscorePanel.sanitize(this.searchBar.getText());
        if (Strings.isNullOrEmpty((String)lookup)) {
            return;
        }
        if (lookup.length() > 12) {
            this.searchBar.setIcon(IconTextField.Icon.ERROR);
            this.loading = false;
            return;
        }
        this.repaint();
        this.searchBar.setEditable(false);
        this.searchBar.setIcon(IconTextField.Icon.LOADING_DARKER);
        this.loading = true;
        for (Map.Entry<HiscoreSkill, JLabel> entry : this.skillLabels.entrySet()) {
            HiscoreSkill skill = entry.getKey();
            JLabel label = entry.getValue();
            HiscoreSkillType skillType = skill == null ? HiscoreSkillType.SKILL : skill.getType();
            label.setText(HiscorePanel.pad("--", skillType));
            label.setToolTipText(skill == null ? "Combat" : skill.getName());
        }
        if (this.selectedEndPoint == null) {
            this.selectedEndPoint = HiscoreEndpoint.NORMAL;
        }
        this.hiscoreClient.lookupAsync(lookup, this.selectedEndPoint).whenCompleteAsync((result, ex) -> SwingUtilities.invokeLater(() -> {
            if (!HiscorePanel.sanitize(this.searchBar.getText()).equals(lookup)) {
                return;
            }
            if (result == null || ex != null) {
                if (ex != null) {
                    log.warn("Error fetching Hiscore data " + ex.getMessage());
                }
                this.searchBar.setIcon(IconTextField.Icon.ERROR);
                this.searchBar.setEditable(true);
                this.loading = false;
                return;
            }
            this.searchBar.setIcon(IconTextField.Icon.SEARCH);
            this.searchBar.setEditable(true);
            this.loading = false;
            this.applyHiscoreResult((HiscoreResult)result);
        }));
    }

    private void applyHiscoreResult(HiscoreResult result) {
        assert (SwingUtilities.isEventDispatchThread());
        this.repaint();
        this.nameAutocompleter.addToSearchHistory(result.getPlayer().toLowerCase());
        for (Map.Entry<HiscoreSkill, JLabel> entry : this.skillLabels.entrySet()) {
            HiscoreSkill skill = entry.getKey();
            JLabel label = entry.getValue();
            if (skill == null) {
                if (result.getPlayer() != null) {
                    int combatLevel = Experience.getCombatLevel((int)result.getSkill(HiscoreSkill.ATTACK).getLevel(), (int)result.getSkill(HiscoreSkill.STRENGTH).getLevel(), (int)result.getSkill(HiscoreSkill.DEFENCE).getLevel(), (int)result.getSkill(HiscoreSkill.HITPOINTS).getLevel(), (int)result.getSkill(HiscoreSkill.MAGIC).getLevel(), (int)result.getSkill(HiscoreSkill.RANGED).getLevel(), (int)result.getSkill(HiscoreSkill.PRAYER).getLevel());
                    label.setText(Integer.toString(combatLevel));
                }
            } else {
                Skill s = result.getSkill(skill);
                if (s != null) {
                    long exp = s.getExperience();
                    boolean isSkill = skill.getType() == HiscoreSkillType.SKILL;
                    int level = -1;
                    if (this.config.virtualLevels() && isSkill && exp > -1L) {
                        level = Experience.getLevelForXp((int)((int)exp));
                    } else if (!isSkill || exp != -1L) {
                        level = s.getLevel();
                    }
                    if (level != -1) {
                        label.setText(HiscorePanel.pad(HiscorePanel.formatLevel(level), skill.getType()));
                    }
                }
            }
            label.setToolTipText(this.detailsHtml(result, skill));
        }
    }

    void addInputKeyListener(KeyListener l) {
        this.searchBar.addKeyListener(l);
    }

    void removeInputKeyListener(KeyListener l) {
        this.searchBar.removeKeyListener(l);
    }

    private String detailsHtml(HiscoreResult result, HiscoreSkill skill) {
        long experience;
        String openingTags = "<html><body style = 'padding: 5px;color:#989898'>";
        String closingTags = "</html><body>";
        Object content = "";
        if (skill == null) {
            double combatLevel = Experience.getCombatLevelPrecise((int)result.getSkill(HiscoreSkill.ATTACK).getLevel(), (int)result.getSkill(HiscoreSkill.STRENGTH).getLevel(), (int)result.getSkill(HiscoreSkill.DEFENCE).getLevel(), (int)result.getSkill(HiscoreSkill.HITPOINTS).getLevel(), (int)result.getSkill(HiscoreSkill.MAGIC).getLevel(), (int)result.getSkill(HiscoreSkill.RANGED).getLevel(), (int)result.getSkill(HiscoreSkill.PRAYER).getLevel());
            double combatExperience = result.getSkill(HiscoreSkill.ATTACK).getExperience() + result.getSkill(HiscoreSkill.STRENGTH).getExperience() + result.getSkill(HiscoreSkill.DEFENCE).getExperience() + result.getSkill(HiscoreSkill.HITPOINTS).getExperience() + result.getSkill(HiscoreSkill.MAGIC).getExperience() + result.getSkill(HiscoreSkill.RANGED).getExperience() + result.getSkill(HiscoreSkill.PRAYER).getExperience();
            content = (String)content + "<p><span style = 'color:white'>Combat</span></p>";
            content = (String)content + "<p><span style = 'color:white'>Exact Combat Level:</span> " + QuantityFormatter.formatNumber(combatLevel) + "</p>";
            content = (String)content + "<p><span style = 'color:white'>Experience:</span> " + QuantityFormatter.formatNumber(combatExperience) + "</p>";
        } else {
            switch (skill) {
                case CLUE_SCROLL_ALL: {
                    content = (String)content + "<p><span style = 'color:white'>Clues</span></p>";
                    content = (String)content + HiscorePanel.buildClueLine(result, "All", HiscoreSkill.CLUE_SCROLL_ALL);
                    content = (String)content + HiscorePanel.buildClueLine(result, "Beginner", HiscoreSkill.CLUE_SCROLL_BEGINNER);
                    content = (String)content + HiscorePanel.buildClueLine(result, "Easy", HiscoreSkill.CLUE_SCROLL_EASY);
                    content = (String)content + HiscorePanel.buildClueLine(result, "Medium", HiscoreSkill.CLUE_SCROLL_MEDIUM);
                    content = (String)content + HiscorePanel.buildClueLine(result, "Hard", HiscoreSkill.CLUE_SCROLL_HARD);
                    content = (String)content + HiscorePanel.buildClueLine(result, "Elite", HiscoreSkill.CLUE_SCROLL_ELITE);
                    content = (String)content + HiscorePanel.buildClueLine(result, "Master", HiscoreSkill.CLUE_SCROLL_MASTER);
                    break;
                }
                case BOUNTY_HUNTER_ROGUE: 
                case BOUNTY_HUNTER_HUNTER: 
                case PVP_ARENA_RANK: 
                case LAST_MAN_STANDING: 
                case SOUL_WARS_ZEAL: 
                case RIFTS_CLOSED: 
                case COLOSSEUM_GLORY: 
                case COLLECTIONS_LOGGED: {
                    content = (String)content + HiscorePanel.buildMinigameTooltip(result.getSkill(skill), skill);
                    break;
                }
                case LEAGUE_POINTS: {
                    Skill leaguePoints = result.getSkill(HiscoreSkill.LEAGUE_POINTS);
                    String rank = leaguePoints.getRank() == -1 ? "Unranked" : QuantityFormatter.formatNumber(leaguePoints.getRank());
                    content = (String)content + "<p><span style = 'color:white'>League Points</span></p>";
                    content = (String)content + "<p><span style = 'color:white'>Rank:</span> " + rank + "</p>";
                    if (leaguePoints.getLevel() <= -1) break;
                    content = (String)content + "<p><span style = 'color:white'>Points:</span> " + QuantityFormatter.formatNumber(leaguePoints.getLevel()) + "</p>";
                    break;
                }
                case OVERALL: {
                    Skill requestedSkill = result.getSkill(skill);
                    String rank = requestedSkill.getRank() == -1 ? "Unranked" : QuantityFormatter.formatNumber(requestedSkill.getRank());
                    String exp = requestedSkill.getExperience() == -1L ? "Unranked" : QuantityFormatter.formatNumber(requestedSkill.getExperience());
                    content = (String)content + "<p><span style = 'color:white'>" + skill.getName() + "</span></p>";
                    content = (String)content + "<p><span style = 'color:white'>Rank:</span> " + rank + "</p>";
                    content = (String)content + "<p><span style = 'color:white'>Experience:</span> " + exp + "</p>";
                    break;
                }
                default: {
                    int currentLevel;
                    String exp;
                    if (skill.getType() == HiscoreSkillType.BOSS) {
                        String rank = "Unranked";
                        String lvl = null;
                        Skill requestedSkill = result.getSkill(skill);
                        if (requestedSkill != null) {
                            if (requestedSkill.getRank() > -1) {
                                rank = QuantityFormatter.formatNumber(requestedSkill.getRank());
                            }
                            if (requestedSkill.getLevel() > -1) {
                                lvl = QuantityFormatter.formatNumber(requestedSkill.getLevel());
                            }
                        }
                        content = (String)content + "<p><span style = 'color:white'>Boss:</span> " + skill.getName() + "</p>";
                        content = (String)content + "<p><span style = 'color:white'>Rank:</span> " + rank + "</p>";
                        if (lvl == null) break;
                        content = (String)content + "<p><span style = 'color:white'>KC:</span> " + lvl + "</p>";
                        break;
                    }
                    Skill requestedSkill = result.getSkill(skill);
                    long experience2 = requestedSkill.getExperience();
                    String rank = requestedSkill.getRank() == -1 ? "Unranked" : QuantityFormatter.formatNumber(requestedSkill.getRank());
                    String string = exp = experience2 == -1L ? "Unranked" : QuantityFormatter.formatNumber(experience2);
                    String remainingXp = experience2 == -1L ? "Unranked" : ((currentLevel = Experience.getLevelForXp((int)((int)experience2))) + 1 <= 126 ? QuantityFormatter.formatNumber((long)Experience.getXpForLevel((int)(currentLevel + 1)) - experience2) : "0");
                    content = (String)content + "<p><span style = 'color:white'>Skill:</span> " + skill.getName() + "</p>";
                    content = (String)content + "<p><span style = 'color:white'>Rank:</span> " + rank + "</p>";
                    content = (String)content + "<p><span style = 'color:white'>Experience:</span> " + exp + "</p>";
                    content = (String)content + "<p><span style = 'color:white'>Remaining XP:</span> " + remainingXp + "</p>";
                }
            }
        }
        if (skill != null && skill.getType() == HiscoreSkillType.SKILL && (experience = result.getSkill(skill).getExperience()) >= 0L) {
            int currentXp = (int)experience;
            int currentLevel = Experience.getLevelForXp((int)currentXp);
            int xpForCurrentLevel = Experience.getXpForLevel((int)currentLevel);
            int xpForNextLevel = currentLevel + 1 <= 126 ? Experience.getXpForLevel((int)(currentLevel + 1)) : -1;
            double xpGained = currentXp - xpForCurrentLevel;
            double xpGoal = xpForNextLevel != -1 ? (double)(xpForNextLevel - xpForCurrentLevel) : 100.0;
            int progress = (int)(xpGained / xpGoal * 100.0);
            content = (String)content + "<div style = 'margin-top:3px'><div style = 'background: #070707; border: 1px solid #070707; height: 6px; width: 100%;'><div style = 'height: 6px; width: " + progress + "%; background: #dc8a00;'></div></div></div>";
        }
        return openingTags + (String)content + closingTags;
    }

    private static String buildMinigameTooltip(Skill s, HiscoreSkill hiscoreSkill) {
        String rank = s.getRank() == -1 ? "Unranked" : QuantityFormatter.formatNumber(s.getRank());
        Object content = "";
        content = (String)content + "<p><span style = 'color:white'>" + hiscoreSkill.getName() + "</span></p>";
        content = (String)content + "<p><span style = 'color:white'>Rank:</span> " + rank + "</p>";
        if (s.getLevel() > -1) {
            content = (String)content + "<p><span style = 'color:white'>Score:</span> " + QuantityFormatter.formatNumber(s.getLevel()) + "</p>";
        }
        return content;
    }

    private static String buildClueLine(HiscoreResult result, String name, HiscoreSkill skill) {
        Skill sk = result.getSkill(skill);
        String count = sk.getLevel() == -1 ? "0" : QuantityFormatter.formatNumber(sk.getLevel());
        String rank = sk.getRank() == -1 ? "Unranked" : QuantityFormatter.formatNumber(sk.getRank());
        return "<p><span style = 'color:white'>" + name + ":</span> " + count + " <span style = 'color:white'>Rank:</span> " + rank + "</p>";
    }

    private static String sanitize(String lookup) {
        return lookup.replace('\u00a0', ' ');
    }

    private void resetEndpoints() {
        HiscoreEndpoint endpoint = this.plugin.getWorldEndpoint();
        int idx = ArrayUtils.indexOf((Object[])ENDPOINTS, (Object)((Object)endpoint));
        this.tabGroup.select(this.tabGroup.getTab(idx));
    }

    @VisibleForTesting
    static String formatLevel(int level) {
        if (level < 10000) {
            return Integer.toString(level);
        }
        return level / 1000 + "k";
    }

    private static String pad(String str, HiscoreSkillType type) {
        int pad = type == HiscoreSkillType.BOSS ? 4 : 2;
        return StringUtils.leftPad((String)str, (int)pad);
    }
}

