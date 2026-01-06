/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.annotations.VisibleForTesting
 *  javax.inject.Inject
 *  net.runelite.api.Client
 *  net.runelite.api.Experience
 *  net.runelite.api.Skill
 *  net.runelite.api.WorldType
 */
package net.runelite.client.plugins.skillcalculator;

import com.google.common.annotations.VisibleForTesting;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import javax.inject.Inject;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import net.runelite.api.Client;
import net.runelite.api.Experience;
import net.runelite.api.Skill;
import net.runelite.api.WorldType;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.game.ItemManager;
import net.runelite.client.game.SpriteManager;
import net.runelite.client.plugins.skillcalculator.CalculatorType;
import net.runelite.client.plugins.skillcalculator.UIActionSlot;
import net.runelite.client.plugins.skillcalculator.UICalculatorInputArea;
import net.runelite.client.plugins.skillcalculator.UICombinedActionSlot;
import net.runelite.client.plugins.skillcalculator.skills.SkillAction;
import net.runelite.client.plugins.skillcalculator.skills.SkillBonus;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.DynamicGridLayout;
import net.runelite.client.ui.FontManager;
import net.runelite.client.ui.components.IconTextField;

class SkillCalculator
extends JPanel {
    static final int MAX_XP_MULTIPLIER = 32;
    private static final JLabel EMPTY_PANEL = new JLabel("No F2P actions to show.");
    private final UICalculatorInputArea uiInput;
    private final Client client;
    private final ClientThread clientThread;
    private final SpriteManager spriteManager;
    private final ItemManager itemManager;
    private final List<UIActionSlot> uiActionSlots = new ArrayList<UIActionSlot>();
    private final UICombinedActionSlot combinedActionSlot;
    private final ArrayList<UIActionSlot> combinedActionSlots = new ArrayList();
    private final Map<SkillBonus, JCheckBox> bonusCheckBoxes = new HashMap<SkillBonus, JCheckBox>();
    private final IconTextField searchBar = new IconTextField();
    private CalculatorType currentCalculator;
    private int currentLevel = 1;
    private int currentXP = Experience.getXpForLevel((int)this.currentLevel);
    private int targetLevel = this.currentLevel + 1;
    private int targetXP = Experience.getXpForLevel((int)this.targetLevel);
    private int xpMultiplier = 1;
    private final Set<SkillBonus> currentBonuses = new HashSet<SkillBonus>();

    @Inject
    SkillCalculator(Client client, ClientThread clientThread, UICalculatorInputArea uiInput, SpriteManager spriteManager, ItemManager itemManager) {
        this.client = client;
        this.clientThread = clientThread;
        this.uiInput = uiInput;
        this.spriteManager = spriteManager;
        this.itemManager = itemManager;
        this.combinedActionSlot = new UICombinedActionSlot(spriteManager);
        this.searchBar.setIcon(IconTextField.Icon.SEARCH);
        this.searchBar.setPreferredSize(new Dimension(205, 30));
        this.searchBar.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        this.searchBar.setHoverBackgroundColor(ColorScheme.DARK_GRAY_HOVER_COLOR);
        this.searchBar.addClearListener(this::onSearch);
        this.searchBar.addKeyListener(new KeyAdapter(){

            @Override
            public void keyTyped(KeyEvent e) {
                SkillCalculator.this.onSearch();
            }
        });
        this.setLayout(new DynamicGridLayout(0, 1, 0, 5));
        uiInput.getUiFieldCurrentLevel().addActionListener(e -> {
            this.onFieldCurrentLevelUpdated();
            uiInput.getUiFieldTargetLevel().requestFocusInWindow();
        });
        uiInput.getUiFieldCurrentXP().addActionListener(e -> {
            this.onFieldCurrentXPUpdated();
            uiInput.getUiFieldTargetXP().requestFocusInWindow();
        });
        uiInput.getUiFieldTargetLevel().addActionListener(e -> this.onFieldTargetLevelUpdated());
        uiInput.getUiFieldTargetXP().addActionListener(e -> this.onFieldTargetXPUpdated());
        uiInput.getUiFieldXPMultiplier().addChangeListener(e -> this.onFieldXPMultiplierUpdated());
        uiInput.getUiFieldCurrentLevel().addFocusListener(SkillCalculator.buildFocusAdapter(e -> this.onFieldCurrentLevelUpdated()));
        uiInput.getUiFieldCurrentXP().addFocusListener(SkillCalculator.buildFocusAdapter(e -> this.onFieldCurrentXPUpdated()));
        uiInput.getUiFieldTargetLevel().addFocusListener(SkillCalculator.buildFocusAdapter(e -> this.onFieldTargetLevelUpdated()));
        uiInput.getUiFieldTargetXP().addFocusListener(SkillCalculator.buildFocusAdapter(e -> this.onFieldTargetXPUpdated()));
        uiInput.getUiFieldXPMultiplier().addFocusListener(SkillCalculator.buildFocusAdapter(e -> this.onFieldXPMultiplierUpdated()));
    }

    void openCalculator(CalculatorType calculatorType, boolean forceReload) {
        this.currentXP = this.client.getSkillExperience(calculatorType.getSkill());
        this.currentLevel = Experience.getLevelForXp((int)this.currentXP);
        if (forceReload || this.currentCalculator != calculatorType) {
            this.currentCalculator = calculatorType;
            this.currentBonuses.clear();
            int endGoalVarp = SkillCalculator.endGoalVarpForSkill(calculatorType.getSkill());
            int endGoal = this.client.getVarpValue(endGoalVarp);
            if (endGoal != -1) {
                this.targetLevel = Experience.getLevelForXp((int)endGoal);
                this.targetXP = endGoal;
            } else {
                this.targetLevel = SkillCalculator.enforceSkillBounds(this.currentLevel + 1);
                this.targetXP = Experience.getXpForLevel((int)this.targetLevel);
            }
            this.removeAll();
            this.searchBar.setText(null);
            this.clearCombinedSlots();
            if (this.client.getWorldType().contains(WorldType.MEMBERS)) {
                this.renderBonusOptions();
            }
            this.add(this.combinedActionSlot);
            this.add(this.searchBar);
            this.renderActionSlots();
        }
        this.updateInputFields();
    }

    private void updateCombinedAction() {
        int size = this.combinedActionSlots.size();
        if (size > 1) {
            this.combinedActionSlot.setTitle(size + " actions selected");
        } else if (size == 1) {
            this.combinedActionSlot.setTitle("1 action selected");
        } else {
            this.combinedActionSlot.setTitle("No action selected");
            this.combinedActionSlot.setText("Shift-click to select multiple");
            return;
        }
        int actionCount = 0;
        int neededXP = this.targetXP - this.currentXP;
        int xp = 0;
        for (UIActionSlot slot : this.combinedActionSlots) {
            xp += slot.getValue();
        }
        if (neededXP > 0) {
            assert (xp != 0);
            actionCount = ((neededXP *= 10) - 1) / xp + 1;
        }
        this.combinedActionSlot.setText(SkillCalculator.formatXPActionString(xp, actionCount, "exp - "));
    }

    private void clearCombinedSlots() {
        for (UIActionSlot slot : this.combinedActionSlots) {
            slot.setSelected(false);
        }
        this.combinedActionSlots.clear();
    }

    private void renderBonusOptions() {
        SkillBonus[] skillBonuses = this.currentCalculator.getSkillBonuses();
        if (skillBonuses == null) {
            return;
        }
        for (SkillBonus bonus : skillBonuses) {
            JPanel checkboxPanel = this.buildCheckboxPanel(bonus);
            this.add(checkboxPanel);
        }
        if (skillBonuses.length > 0) {
            this.add(Box.createRigidArea(new Dimension(0, 4)));
        }
    }

    private JPanel buildCheckboxPanel(SkillBonus bonus) {
        JPanel uiOption = new JPanel(new BorderLayout());
        JLabel uiLabel = new JLabel(SkillCalculator.generateDisplayNameForBonus(bonus));
        JCheckBox uiCheckbox = new JCheckBox();
        uiLabel.setForeground(Color.WHITE);
        uiLabel.setFont(FontManager.getRunescapeSmallFont());
        uiOption.setBorder(BorderFactory.createEmptyBorder(1, 7, 1, 0));
        uiCheckbox.addActionListener(event -> this.adjustCheckboxes(uiCheckbox, bonus));
        uiOption.add((Component)uiLabel, "West");
        uiOption.add((Component)uiCheckbox, "East");
        this.bonusCheckBoxes.put(bonus, uiCheckbox);
        return uiOption;
    }

    private static String generateDisplayNameForBonus(SkillBonus bonus) {
        return bonus.getName() + " (" + SkillCalculator.formatBonusPercentage(bonus.getValue()) + "%)";
    }

    @VisibleForTesting
    static String formatBonusPercentage(float bonus) {
        int bonusPercentInt;
        int bonusValue = Math.round(10000.0f * bonus);
        float bonusPercent = (float)bonusValue / 100.0f;
        if (bonusPercent == (float)(bonusPercentInt = (int)bonusPercent)) {
            return String.valueOf(bonusPercentInt);
        }
        return String.valueOf(bonusPercent);
    }

    private void adjustCheckboxes(JCheckBox target, SkillBonus bonus) {
        for (Map.Entry<SkillBonus, JCheckBox> entry : this.bonusCheckBoxes.entrySet()) {
            if (entry.getValue() == target || entry.getKey().getCanBeStackedWith().contains(bonus)) continue;
            this.currentBonuses.remove(entry.getKey());
            entry.getValue().setSelected(false);
        }
        if (target.isSelected()) {
            this.currentBonuses.add(bonus);
        } else {
            this.currentBonuses.remove(bonus);
        }
        this.calculate();
    }

    private void renderActionSlots() {
        this.uiActionSlots.clear();
        for (SkillAction action : this.currentCalculator.getSkillActions()) {
            JLabel uiIcon = new JLabel();
            if (action.getIcon() != -1) {
                this.itemManager.getImage(action.getIcon()).addTo(uiIcon);
            } else if (action.getSprite() != -1) {
                this.spriteManager.addSpriteTo(uiIcon, action.getSprite(), 0);
            }
            final UIActionSlot slot = new UIActionSlot(action, this.clientThread, this.itemManager, uiIcon);
            this.uiActionSlots.add(slot);
            slot.addMouseListener(new MouseAdapter(){

                @Override
                public void mousePressed(MouseEvent e) {
                    if (!e.isShiftDown()) {
                        SkillCalculator.this.clearCombinedSlots();
                    }
                    if (slot.isSelected()) {
                        SkillCalculator.this.combinedActionSlots.remove(slot);
                    } else {
                        SkillCalculator.this.combinedActionSlots.add(slot);
                    }
                    slot.setSelected(!slot.isSelected());
                    SkillCalculator.this.updateCombinedAction();
                }
            });
        }
        if (this.client.getWorldType().contains(WorldType.MEMBERS)) {
            this.uiActionSlots.forEach(this::add);
            this.revalidate();
            this.repaint();
        } else {
            this.clientThread.invokeLater(() -> {
                List membersActions = this.uiActionSlots.stream().filter(slot -> !slot.getAction().isMembers(this.itemManager)).collect(Collectors.toList());
                SwingUtilities.invokeLater(() -> {
                    if (membersActions.isEmpty()) {
                        this.add(EMPTY_PANEL);
                    } else {
                        membersActions.forEach(this::add);
                    }
                    this.revalidate();
                    this.repaint();
                });
            });
        }
    }

    private void calculate() {
        for (UIActionSlot slot : this.uiActionSlots) {
            int actionCount = 0;
            int neededXP = this.targetXP - this.currentXP;
            SkillAction action = slot.getAction();
            float bonus = 1.0f;
            for (SkillBonus skillBonus : this.currentBonuses) {
                if (!action.isBonusApplicable(skillBonus)) continue;
                bonus *= skillBonus.getValue();
            }
            int xp = (int)Math.floor(action.getXp() * 10.0f * bonus * (float)this.xpMultiplier);
            if (neededXP > 0) {
                actionCount = ((neededXP *= 10) - 1) / xp + 1;
            }
            slot.setText("Lvl. " + action.getLevel() + " (" + SkillCalculator.formatXPActionString(xp, actionCount, "exp) - "));
            slot.setAvailable(this.currentLevel >= action.getLevel());
            slot.setOverlapping(action.getLevel() < this.targetLevel);
            slot.setValue(xp);
        }
        this.updateCombinedAction();
    }

    private static String formatXPActionString(int xp, int actionCount, String expExpression) {
        int integer = xp / 10;
        int frac = xp % 10;
        return String.valueOf(frac != 0 ? integer + "." + frac : Integer.valueOf(integer)) + expExpression + NumberFormat.getIntegerInstance().format(actionCount) + (actionCount == 1 ? " action" : " actions");
    }

    private void updateInputFields() {
        if (this.targetXP < this.currentXP) {
            this.targetLevel = SkillCalculator.enforceSkillBounds(this.currentLevel + 1);
            this.targetXP = Experience.getXpForLevel((int)this.targetLevel);
        }
        String cXP = String.format("%,d", this.currentXP);
        String tXP = String.format("%,d", this.targetXP);
        String nXP = String.format("%,d", this.targetXP - this.currentXP);
        this.uiInput.setCurrentLevelInput(this.currentLevel);
        this.uiInput.setCurrentXPInput(cXP);
        this.uiInput.setTargetLevelInput(this.targetLevel);
        this.uiInput.setTargetXPInput(tXP);
        this.uiInput.setNeededXP(nXP + " XP required to reach target XP");
        this.uiInput.setXPMultiplier(this.xpMultiplier);
        this.calculate();
    }

    private void onFieldCurrentLevelUpdated() {
        this.currentLevel = SkillCalculator.enforceSkillBounds(this.uiInput.getCurrentLevelInput());
        this.currentXP = Experience.getXpForLevel((int)this.currentLevel);
        this.updateInputFields();
    }

    private void onFieldXPMultiplierUpdated() {
        this.xpMultiplier = SkillCalculator.enforceMultiplierBounds(this.uiInput.getXPMultiplierInput());
        this.updateInputFields();
    }

    private void onFieldCurrentXPUpdated() {
        this.currentXP = SkillCalculator.enforceXPBounds(this.uiInput.getCurrentXPInput());
        this.currentLevel = Experience.getLevelForXp((int)this.currentXP);
        this.updateInputFields();
    }

    private void onFieldTargetLevelUpdated() {
        this.targetLevel = SkillCalculator.enforceSkillBounds(this.uiInput.getTargetLevelInput());
        this.targetXP = Experience.getXpForLevel((int)this.targetLevel);
        this.updateInputFields();
    }

    private void onFieldTargetXPUpdated() {
        this.targetXP = SkillCalculator.enforceXPBounds(this.uiInput.getTargetXPInput());
        this.targetLevel = Experience.getLevelForXp((int)this.targetXP);
        this.updateInputFields();
    }

    private static int enforceSkillBounds(int input) {
        return Math.min(126, Math.max(1, input));
    }

    private static int enforceXPBounds(int input) {
        return Math.min(200000000, Math.max(0, input));
    }

    private static int enforceMultiplierBounds(int input) {
        return Math.min(32, Math.max(1, input));
    }

    private void onSearch() {
        this.uiActionSlots.forEach(slot -> {
            if (SkillCalculator.slotContainsText(slot, this.searchBar.getText())) {
                super.add((Component)slot);
            } else {
                super.remove((Component)slot);
            }
            this.revalidate();
        });
    }

    private static boolean slotContainsText(UIActionSlot slot, String text) {
        return slot.getActionName().toLowerCase().contains(text.toLowerCase());
    }

    private static FocusAdapter buildFocusAdapter(final Consumer<FocusEvent> focusLostConsumer) {
        return new FocusAdapter(){

            @Override
            public void focusLost(FocusEvent e) {
                focusLostConsumer.accept(e);
            }
        };
    }

    private static int endGoalVarpForSkill(Skill skill) {
        switch (skill) {
            case ATTACK: {
                return 1253;
            }
            case MINING: {
                return 1265;
            }
            case WOODCUTTING: {
                return 1270;
            }
            case DEFENCE: {
                return 1257;
            }
            case MAGIC: {
                return 1256;
            }
            case RANGED: {
                return 1255;
            }
            case HITPOINTS: {
                return 1258;
            }
            case AGILITY: {
                return 1260;
            }
            case STRENGTH: {
                return 1254;
            }
            case PRAYER: {
                return 1259;
            }
            case SLAYER: {
                return 1272;
            }
            case FISHING: {
                return 1267;
            }
            case RUNECRAFT: {
                return 1264;
            }
            case HERBLORE: {
                return 1261;
            }
            case FIREMAKING: {
                return 1269;
            }
            case CONSTRUCTION: {
                return 1274;
            }
            case HUNTER: {
                return 1275;
            }
            case COOKING: {
                return 1268;
            }
            case FARMING: {
                return 1273;
            }
            case CRAFTING: {
                return 1263;
            }
            case SMITHING: {
                return 1266;
            }
            case THIEVING: {
                return 1262;
            }
            case FLETCHING: {
                return 1271;
            }
        }
        throw new IllegalArgumentException();
    }

    static {
        EMPTY_PANEL.setHorizontalAlignment(0);
        EMPTY_PANEL.setBorder(new EmptyBorder(50, 0, 0, 0));
    }
}

