/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.reflect.TypeToken
 *  com.google.gson.Gson
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonParseException
 *  com.google.gson.JsonParser
 *  com.google.inject.Inject
 *  javax.inject.Named
 *  net.runelite.api.widgets.Widget
 *  okhttp3.Call
 *  okhttp3.Callback
 *  okhttp3.HttpUrl
 *  okhttp3.OkHttpClient
 *  okhttp3.Request
 *  okhttp3.Request$Builder
 *  okhttp3.Response
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package net.runelite.client.plugins.wiki;

import com.google.common.collect.ImmutableList;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.google.inject.Inject;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javax.inject.Named;
import net.runelite.api.widgets.Widget;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.game.chatbox.ChatboxPanelManager;
import net.runelite.client.game.chatbox.ChatboxTextInput;
import net.runelite.client.plugins.wiki.WikiPlugin;
import net.runelite.client.util.LinkBrowser;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WikiSearchChatboxTextInput
extends ChatboxTextInput {
    private static final Logger log = LoggerFactory.getLogger(WikiSearchChatboxTextInput.class);
    private static final int LINE_HEIGHT = 20;
    private static final int CHATBOX_HEIGHT = 120;
    private static final int MAX_NUM_PREDICTIONS = 4;
    private static final int PREDICTION_DEBOUNCE_DELAY_MS = 200;
    private final ChatboxPanelManager chatboxPanelManager;
    private Future<?> runningRequest = null;
    private List<String> predictions = ImmutableList.of();
    private int selectedPrediction = -1;
    private String offPrediction = null;

    @Inject
    public WikiSearchChatboxTextInput(ChatboxPanelManager chatboxPanelManager, final ClientThread clientThread, ScheduledExecutorService scheduledExecutorService, @Named(value="developerMode") boolean developerMode, OkHttpClient okHttpClient, final Gson gson) {
        super(chatboxPanelManager, clientThread);
        this.chatboxPanelManager = chatboxPanelManager;
        this.lines(1);
        this.prompt("OSRS Wiki Search");
        this.onDone((String string) -> {
            if (string != null && string.length() > 0) {
                this.search((String)string);
            }
        });
        this.onChanged(searchString -> {
            this.selectedPrediction = -1;
            Future<?> rr = this.runningRequest;
            if (rr != null) {
                rr.cancel(false);
            }
            if (searchString.length() <= 1) {
                this.runningRequest = null;
                clientThread.invokeLater(() -> {
                    this.predictions = ImmutableList.of();
                    this.update();
                });
                return;
            }
            this.runningRequest = scheduledExecutorService.schedule(() -> {
                HttpUrl url = WikiPlugin.WIKI_API.newBuilder().addQueryParameter("action", "opensearch").addQueryParameter("search", searchString).addQueryParameter("redirects", "resolve").addQueryParameter("format", "json").addQueryParameter("warningsaserror", Boolean.toString(developerMode)).build();
                Request req = new Request.Builder().url(url).build();
                okHttpClient.newCall(req).enqueue(new Callback(){

                    public void onFailure(Call call, IOException e) {
                        log.warn("error searching wiki", (Throwable)e);
                    }

                    public void onResponse(Call call, Response response) throws IOException {
                        String body = response.body().string();
                        try (Response response2 = response;){
                            JsonArray jar = new JsonParser().parse(body).getAsJsonArray();
                            List apredictions = (List)gson.fromJson(jar.get(1), new TypeToken<List<String>>(){}.getType());
                            if (apredictions.size() > 4) {
                                apredictions = apredictions.subList(0, 4);
                            }
                            List bpredictions = apredictions;
                            clientThread.invokeLater(() -> {
                                WikiSearchChatboxTextInput.this.predictions = bpredictions;
                                WikiSearchChatboxTextInput.this.update();
                            });
                        }
                        catch (JsonParseException | IllegalStateException | IndexOutOfBoundsException e) {
                            log.warn("error parsing wiki response {}", (Object)body, (Object)e);
                        }
                    }
                });
                this.runningRequest = null;
            }, 200L, TimeUnit.MILLISECONDS);
        });
    }

    @Override
    protected void update() {
        Widget container = this.chatboxPanelManager.getContainerWidget();
        container.deleteAllChildren();
        Widget promptWidget = container.createChild(-1, 4);
        promptWidget.setText(this.getPrompt());
        promptWidget.setTextColor(0x800000);
        promptWidget.setFontId(this.getFontID());
        promptWidget.setXPositionMode(1);
        promptWidget.setOriginalX(0);
        promptWidget.setYPositionMode(0);
        promptWidget.setOriginalY(5);
        promptWidget.setOriginalHeight(20);
        promptWidget.setXTextAlignment(1);
        promptWidget.setYTextAlignment(1);
        promptWidget.setWidthMode(1);
        promptWidget.revalidate();
        this.buildEdit(0, 25, container.getWidth(), 20);
        Widget separator = container.createChild(-1, 9);
        separator.setXPositionMode(1);
        separator.setOriginalX(0);
        separator.setYPositionMode(0);
        separator.setOriginalY(44);
        separator.setOriginalHeight(0);
        separator.setOriginalWidth(16);
        separator.setWidthMode(1);
        separator.revalidate();
        for (int i = 0; i < this.predictions.size(); ++i) {
            String pred = this.predictions.get(i);
            int y = 6 + 20 * (2 + i);
            Widget bg = container.createChild(-1, 3);
            bg.setTextColor(0x4444DD);
            bg.setFilled(true);
            bg.setXPositionMode(1);
            bg.setOriginalX(1);
            bg.setYPositionMode(0);
            bg.setOriginalY(y);
            bg.setOriginalHeight(20);
            bg.setOriginalWidth(16);
            bg.setWidthMode(1);
            bg.revalidate();
            bg.setName("<col=ff9040>" + pred);
            bg.setAction(0, "Open");
            bg.setHasListener(true);
            bg.setOnOpListener(new Object[]{ev -> this.search(pred)});
            Widget text = container.createChild(-1, 4);
            text.setText(pred);
            text.setFontId(this.getFontID());
            text.setXPositionMode(1);
            text.setOriginalX(0);
            text.setYPositionMode(0);
            text.setOriginalY(y);
            text.setOriginalHeight(20);
            text.setXTextAlignment(1);
            text.setYTextAlignment(1);
            text.setWidthMode(1);
            text.revalidate();
            if (i == this.selectedPrediction) {
                text.setTextColor(0xFFFFFF);
                continue;
            }
            bg.setOpacity(255);
            text.setTextColor(0);
            bg.setOnMouseRepeatListener(new Object[]{ev -> text.setTextColor(0xFFFFFF)});
            bg.setOnMouseLeaveListener(new Object[]{ev -> text.setTextColor(0)});
        }
    }

    @Override
    public void keyPressed(KeyEvent ev) {
        if (!this.chatboxPanelManager.shouldTakeInput()) {
            return;
        }
        switch (ev.getKeyCode()) {
            case 38: {
                ev.consume();
                if (this.selectedPrediction <= -1) break;
                --this.selectedPrediction;
                if (this.selectedPrediction == -1) {
                    this.value(this.offPrediction);
                    break;
                }
                this.value(this.predictions.get(this.selectedPrediction));
                break;
            }
            case 40: {
                ev.consume();
                if (this.selectedPrediction == -1) {
                    this.offPrediction = this.getValue();
                }
                ++this.selectedPrediction;
                if (this.selectedPrediction >= this.predictions.size()) {
                    this.selectedPrediction = this.predictions.size() - 1;
                }
                if (this.selectedPrediction == -1) break;
                this.value(this.predictions.get(this.selectedPrediction));
                break;
            }
            default: {
                super.keyPressed(ev);
            }
        }
    }

    private void search(String search) {
        LinkBrowser.browse(WikiPlugin.WIKI_BASE.newBuilder().addQueryParameter("search", search).addQueryParameter("utm_source", "runelite").build().toString());
        this.chatboxPanelManager.close();
    }
}

