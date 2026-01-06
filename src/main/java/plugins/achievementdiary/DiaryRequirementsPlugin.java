/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.inject.Inject
 *  net.runelite.api.Client
 *  net.runelite.api.FontTypeFace
 *  net.runelite.api.events.WidgetLoaded
 *  net.runelite.api.widgets.Widget
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package net.runelite.client.plugins.achievementdiary;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.FontTypeFace;
import net.runelite.api.events.WidgetLoaded;
import net.runelite.api.widgets.Widget;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.achievementdiary.DiaryRequirement;
import net.runelite.client.plugins.achievementdiary.GenericDiaryRequirement;
import net.runelite.client.plugins.achievementdiary.Requirement;
import net.runelite.client.plugins.achievementdiary.diaries.ArdougneDiaryRequirement;
import net.runelite.client.plugins.achievementdiary.diaries.DesertDiaryRequirement;
import net.runelite.client.plugins.achievementdiary.diaries.FaladorDiaryRequirement;
import net.runelite.client.plugins.achievementdiary.diaries.FremennikDiaryRequirement;
import net.runelite.client.plugins.achievementdiary.diaries.KandarinDiaryRequirement;
import net.runelite.client.plugins.achievementdiary.diaries.KaramjaDiaryRequirement;
import net.runelite.client.plugins.achievementdiary.diaries.KourendDiaryRequirement;
import net.runelite.client.plugins.achievementdiary.diaries.LumbridgeDiaryRequirement;
import net.runelite.client.plugins.achievementdiary.diaries.MorytaniaDiaryRequirement;
import net.runelite.client.plugins.achievementdiary.diaries.VarrockDiaryRequirement;
import net.runelite.client.plugins.achievementdiary.diaries.WesternDiaryRequirement;
import net.runelite.client.plugins.achievementdiary.diaries.WildernessDiaryRequirement;
import net.runelite.client.util.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@PluginDescriptor(name="Diary Requirements", description="Display level requirements in Achievement Diary interface", tags={"achievements", "tasks"}, forceDisabled=true)
public class DiaryRequirementsPlugin
extends Plugin {
    private static final Logger log = LoggerFactory.getLogger(DiaryRequirementsPlugin.class);
    private static final String AND_JOINER = ", ";
    private static final Pattern AND_JOINER_PATTERN = Pattern.compile("(?<=, )");
    @Inject
    private Client client;
    @Inject
    private ClientThread clientThread;

    @Subscribe
    public void onWidgetLoaded(WidgetLoaded event) {
        String widgetTitle;
        if (event.getGroupId() == 741 && (widgetTitle = Text.removeTags(this.client.getWidget(48562178).getText()).replace(' ', '_').toUpperCase()).startsWith("ACHIEVEMENT_DIARY")) {
            this.showDiaryRequirements();
        }
    }

    private void showDiaryRequirements() {
        Widget widget = this.client.getWidget(48562179);
        Widget[] children = widget.getStaticChildren();
        Widget titleWidget = children[0];
        if (titleWidget == null) {
            return;
        }
        FontTypeFace font = titleWidget.getFont();
        int maxWidth = titleWidget.getWidth();
        List<String> originalAchievements = this.getOriginalAchievements(children);
        ArrayList<String> newRequirements = new ArrayList<String>(originalAchievements);
        GenericDiaryRequirement requirements = this.getRequirementsForTitle(titleWidget.getText());
        if (requirements == null) {
            log.debug("Unknown achievement diary {}", (Object)titleWidget.getText());
            return;
        }
        Map<String, String> skillRequirements = this.buildRequirements(requirements.getRequirements());
        if (skillRequirements == null) {
            return;
        }
        int offset = 0;
        Object taskBuffer = "";
        for (int i = 0; i < originalAchievements.size(); ++i) {
            String strike;
            String rowText = Text.removeTags(originalAchievements.get(i));
            if (skillRequirements.get(taskBuffer = skillRequirements.get((String)taskBuffer + " " + rowText) != null ? (String)taskBuffer + " " + rowText : rowText) == null) continue;
            String levelRequirement = skillRequirements.get(taskBuffer);
            String task = originalAchievements.get(i);
            int taskWidth = font.getTextWidth(task);
            int ourWidth = font.getTextWidth(levelRequirement);
            String string = strike = task.startsWith("<str>") ? "<str>" : "";
            if (ourWidth + taskWidth < maxWidth) {
                newRequirements.set(i + offset, task + levelRequirement);
                continue;
            }
            if (ourWidth < maxWidth) {
                newRequirements.add(i + ++offset, strike + levelRequirement);
                continue;
            }
            StringBuilder b = new StringBuilder();
            b.append(task);
            int runningWidth = font.getTextWidth(b.toString());
            for (String word : AND_JOINER_PATTERN.split(levelRequirement)) {
                int wordWidth = font.getTextWidth(word);
                if (runningWidth == 0 || wordWidth + runningWidth < maxWidth) {
                    runningWidth += wordWidth;
                    b.append(word);
                    continue;
                }
                newRequirements.add(i + offset++, b.toString());
                b.delete(0, b.length());
                runningWidth = wordWidth;
                b.append(strike);
                b.append(word);
            }
            newRequirements.set(i + offset, b.toString());
        }
        int lastLine = 0;
        for (int i = 0; i < newRequirements.size() && i < children.length; ++i) {
            Widget achievementWidget = children[i];
            String text = (String)newRequirements.get(i);
            achievementWidget.setText(text);
            if (text == null || text.isEmpty()) continue;
            lastLine = i;
        }
        int numLines = lastLine;
        this.clientThread.invokeLater(() -> this.client.runScript(new Object[]{6845, 1, numLines}));
    }

    private List<String> getOriginalAchievements(Widget[] children) {
        ArrayList<String> preloadedRequirements = new ArrayList<String>(children.length);
        for (Widget requirementWidget : children) {
            preloadedRequirements.add(requirementWidget.getText());
        }
        return preloadedRequirements;
    }

    private GenericDiaryRequirement getRequirementsForTitle(String title) {
        GenericDiaryRequirement diaryRequirementContainer;
        String diaryName;
        switch (diaryName = Text.removeTags(title.replace(" ", "_").toUpperCase())) {
            case "ARDOUGNE_AREA_TASKS": {
                diaryRequirementContainer = new ArdougneDiaryRequirement();
                break;
            }
            case "DESERT_TASKS": {
                diaryRequirementContainer = new DesertDiaryRequirement();
                break;
            }
            case "FALADOR_AREA_TASKS": {
                diaryRequirementContainer = new FaladorDiaryRequirement();
                break;
            }
            case "FREMENNIK_TASKS": {
                diaryRequirementContainer = new FremennikDiaryRequirement();
                break;
            }
            case "KANDARIN_TASKS": {
                diaryRequirementContainer = new KandarinDiaryRequirement();
                break;
            }
            case "KARAMJA_AREA_TASKS": {
                diaryRequirementContainer = new KaramjaDiaryRequirement();
                break;
            }
            case "KOUREND_&_KEBOS_TASKS": {
                diaryRequirementContainer = new KourendDiaryRequirement();
                break;
            }
            case "LUMBRIDGE_&_DRAYNOR_TASKS": {
                diaryRequirementContainer = new LumbridgeDiaryRequirement();
                break;
            }
            case "MORYTANIA_TASKS": {
                diaryRequirementContainer = new MorytaniaDiaryRequirement();
                break;
            }
            case "VARROCK_TASKS": {
                diaryRequirementContainer = new VarrockDiaryRequirement();
                break;
            }
            case "WESTERN_AREA_TASKS": {
                diaryRequirementContainer = new WesternDiaryRequirement();
                break;
            }
            case "WILDERNESS_AREA_TASKS": {
                diaryRequirementContainer = new WildernessDiaryRequirement();
                break;
            }
            default: {
                return null;
            }
        }
        return diaryRequirementContainer;
    }

    private Map<String, String> buildRequirements(Collection<DiaryRequirement> requirements) {
        HashMap<String, String> reqs = new HashMap<String, String>();
        for (DiaryRequirement req : requirements) {
            StringBuilder b = new StringBuilder();
            b.append("<col=ffffff>(");
            assert (!req.getRequirements().isEmpty());
            for (Requirement ireq : req.getRequirements()) {
                boolean satifisfied = ireq.satisfiesRequirement(this.client);
                b.append(satifisfied ? "<col=000080><str>" : "<col=800000>");
                b.append(ireq.toString());
                b.append(satifisfied ? "</str>" : "<col=000080>");
                b.append(AND_JOINER);
            }
            b.delete(b.length() - AND_JOINER.length(), b.length());
            b.append("<col=ffffff>)");
            reqs.put(req.getTask(), b.toString());
        }
        return reqs;
    }
}

