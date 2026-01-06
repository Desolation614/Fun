/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.inject.Inject
 *  net.runelite.api.Client
 *  net.runelite.api.MenuEntry
 *  net.runelite.api.Point
 *  net.runelite.api.Prayer
 *  net.runelite.api.Skill
 *  net.runelite.api.widgets.Widget
 */
package net.runelite.client.plugins.statusbars;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.EnumMap;
import java.util.Map;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.MenuEntry;
import net.runelite.api.Point;
import net.runelite.api.Prayer;
import net.runelite.api.Skill;
import net.runelite.api.widgets.Widget;
import net.runelite.client.game.AlternateSprites;
import net.runelite.client.game.SkillIconManager;
import net.runelite.client.game.SpriteManager;
import net.runelite.client.plugins.itemstats.Effect;
import net.runelite.client.plugins.itemstats.ItemStatChangesService;
import net.runelite.client.plugins.itemstats.StatChange;
import net.runelite.client.plugins.statusbars.BarRenderer;
import net.runelite.client.plugins.statusbars.StatusBarsConfig;
import net.runelite.client.plugins.statusbars.StatusBarsPlugin;
import net.runelite.client.plugins.statusbars.Viewport;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.util.ImageUtil;

class StatusBarsOverlay
extends Overlay {
    private static final Color PRAYER_COLOR = new Color(50, 200, 200, 175);
    private static final Color ACTIVE_PRAYER_COLOR = new Color(57, 255, 186, 225);
    private static final Color HEALTH_COLOR = new Color(225, 35, 0, 125);
    private static final Color POISONED_COLOR = new Color(0, 145, 0, 150);
    private static final Color VENOMED_COLOR = new Color(0, 65, 0, 150);
    private static final Color HEAL_COLOR = new Color(255, 112, 6, 150);
    private static final Color PRAYER_HEAL_COLOR = new Color(57, 255, 186, 75);
    private static final Color ENERGY_HEAL_COLOR = new Color(199, 118, 0, 218);
    private static final Color RUN_STAMINA_COLOR = new Color(160, 124, 72, 255);
    private static final Color SPECIAL_ATTACK_COLOR = new Color(3, 153, 0, 195);
    private static final Color ENERGY_COLOR = new Color(199, 174, 0, 220);
    private static final Color DISEASE_COLOR = new Color(255, 193, 75, 181);
    private static final Color PARASITE_COLOR = new Color(196, 62, 109, 181);
    private static final int HEIGHT = 252;
    private static final int RESIZED_BOTTOM_HEIGHT = 272;
    private static final int RESIZED_BOTTOM_OFFSET_Y = 12;
    private static final int RESIZED_BOTTOM_OFFSET_X = 10;
    private static final int MAX_SPECIAL_ATTACK_VALUE = 100;
    private static final int MAX_RUN_ENERGY_VALUE = 100;
    private final Client client;
    private final StatusBarsPlugin plugin;
    private final StatusBarsConfig config;
    private final ItemStatChangesService itemStatService;
    private final SkillIconManager skillIconManager;
    private final SpriteManager spriteManager;
    private final Image heartDisease;
    private final Image heartPoison;
    private final Image heartVenom;
    private final Map<StatusBarsConfig.BarMode, BarRenderer> barRenderers = new EnumMap<StatusBarsConfig.BarMode, BarRenderer>(StatusBarsConfig.BarMode.class);

    @Inject
    private StatusBarsOverlay(Client client, StatusBarsPlugin plugin, StatusBarsConfig config, SkillIconManager skillIconManager, ItemStatChangesService itemstatservice, SpriteManager spriteManager) {
        this.setPosition(OverlayPosition.DYNAMIC);
        this.setLayer(OverlayLayer.ABOVE_WIDGETS);
        this.client = client;
        this.plugin = plugin;
        this.config = config;
        this.itemStatService = itemstatservice;
        this.skillIconManager = skillIconManager;
        this.spriteManager = spriteManager;
        this.heartDisease = ImageUtil.loadImageResource(AlternateSprites.class, "1067-DISEASE.png");
        this.heartPoison = ImageUtil.loadImageResource(AlternateSprites.class, "1067-POISON.png");
        this.heartVenom = ImageUtil.loadImageResource(AlternateSprites.class, "1067-VENOM.png");
        this.initRenderers();
    }

    private void initRenderers() {
        this.barRenderers.put(StatusBarsConfig.BarMode.HITPOINTS, new BarRenderer(() -> this.inLms() ? 99 : this.client.getRealSkillLevel(Skill.HITPOINTS), () -> this.client.getBoostedSkillLevel(Skill.HITPOINTS), () -> this.getRestoreValue(Skill.HITPOINTS.getName()), () -> {
            int poisonState = this.client.getVarpValue(102);
            if (poisonState >= 1000000) {
                return VENOMED_COLOR;
            }
            if (poisonState > 0) {
                return POISONED_COLOR;
            }
            if (this.client.getVarpValue(456) > 0) {
                return DISEASE_COLOR;
            }
            if (this.client.getVarbitValue(10151) >= 1) {
                return PARASITE_COLOR;
            }
            return HEALTH_COLOR;
        }, () -> HEAL_COLOR, () -> {
            int poisonState = this.client.getVarpValue(102);
            if (poisonState > 0 && poisonState < 50) {
                return this.heartPoison;
            }
            if (poisonState >= 1000000) {
                return this.heartVenom;
            }
            if (this.client.getVarpValue(456) > 0) {
                return this.heartDisease;
            }
            return this.loadSprite(1067);
        }));
        this.barRenderers.put(StatusBarsConfig.BarMode.PRAYER, new BarRenderer(() -> this.inLms() ? 99 : this.client.getRealSkillLevel(Skill.PRAYER), () -> this.client.getBoostedSkillLevel(Skill.PRAYER), () -> this.getRestoreValue(Skill.PRAYER.getName()), () -> {
            Color prayerColor = PRAYER_COLOR;
            for (Prayer pray : Prayer.values()) {
                if (!this.client.isPrayerActive(pray)) continue;
                prayerColor = ACTIVE_PRAYER_COLOR;
                break;
            }
            return prayerColor;
        }, () -> PRAYER_HEAL_COLOR, () -> this.skillIconManager.getSkillImage(Skill.PRAYER, true)));
        this.barRenderers.put(StatusBarsConfig.BarMode.RUN_ENERGY, new BarRenderer(() -> 100, () -> this.client.getEnergy() / 100, () -> this.getRestoreValue("Run Energy"), () -> {
            if (this.client.getVarbitValue(25) != 0) {
                return RUN_STAMINA_COLOR;
            }
            return ENERGY_COLOR;
        }, () -> ENERGY_HEAL_COLOR, () -> this.loadSprite(1069)));
        this.barRenderers.put(StatusBarsConfig.BarMode.SPECIAL_ATTACK, new BarRenderer(() -> 100, () -> this.client.getVarpValue(300) / 10, () -> 0, () -> SPECIAL_ATTACK_COLOR, () -> null, () -> this.loadSprite(1610)));
        this.barRenderers.put(StatusBarsConfig.BarMode.WARMTH, new BarRenderer(() -> 100, () -> this.client.getVarbitValue(11434) / 10, () -> 0, () -> new Color(244, 97, 0), () -> null, () -> this.skillIconManager.getSkillImage(Skill.FIREMAKING, true)));
    }

    @Override
    public Dimension render(Graphics2D g) {
        int offsetRightBarY;
        int offsetRightBarX;
        int offsetLeftBarY;
        int offsetLeftBarX;
        int height;
        int width;
        if (!this.plugin.isBarsDisplayed()) {
            return null;
        }
        Viewport curViewport = null;
        Widget curWidget = null;
        for (Viewport viewport : Viewport.values()) {
            Widget viewportWidget = this.client.getWidget(viewport.getViewport());
            if (viewportWidget == null || viewportWidget.isHidden()) continue;
            curViewport = viewport;
            curWidget = viewportWidget;
            break;
        }
        if (curViewport == null) {
            return null;
        }
        Point offsetLeft = curViewport.getOffsetLeft();
        Point offsetRight = curViewport.getOffsetRight();
        Point location = curWidget.getCanvasLocation();
        if (curViewport == Viewport.RESIZED_BOTTOM) {
            width = this.config.barWidth();
            height = 272;
            int barWidthOffset = width - 20;
            offsetLeftBarX = location.getX() + 10 - offsetLeft.getX() - 2 * barWidthOffset;
            offsetLeftBarY = location.getY() - 12 - offsetLeft.getY();
            offsetRightBarX = location.getX() + 10 - offsetRight.getX() - barWidthOffset;
            offsetRightBarY = location.getY() - 12 - offsetRight.getY();
        } else {
            width = 20;
            height = 252;
            offsetLeftBarX = location.getX() - offsetLeft.getX();
            offsetLeftBarY = location.getY() - offsetLeft.getY();
            offsetRightBarX = location.getX() - offsetRight.getX() + curWidget.getWidth();
            offsetRightBarY = location.getY() - offsetRight.getY();
        }
        BarRenderer left = this.barRenderers.get((Object)this.config.leftBarMode());
        BarRenderer right = this.barRenderers.get((Object)this.config.rightBarMode());
        if (left != null) {
            left.renderBar(this.config, g, offsetLeftBarX, offsetLeftBarY, width, height);
        }
        if (right != null) {
            right.renderBar(this.config, g, offsetRightBarX, offsetRightBarY, width, height);
        }
        return null;
    }

    private int getRestoreValue(String skill) {
        Effect change;
        MenuEntry[] menu = this.client.getMenuEntries();
        int menuSize = menu.length;
        if (menuSize == 0) {
            return 0;
        }
        MenuEntry entry = menu[menuSize - 1];
        Widget widget = entry.getWidget();
        int restoreValue = 0;
        if (widget != null && widget.getId() == 0x950000 && (change = this.itemStatService.getItemStatChanges(widget.getItemId())) != null) {
            for (StatChange c : change.calculate(this.client).getStatChanges()) {
                int value = c.getTheoretical();
                if (value == 0 || !c.getStat().getName().equals(skill)) continue;
                restoreValue = value;
            }
        }
        return restoreValue;
    }

    private BufferedImage loadSprite(int spriteId) {
        return this.spriteManager.getSprite(spriteId, 0);
    }

    private boolean inLms() {
        return this.client.getWidget(21495812) != null;
    }
}

