/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.hash.Hashing
 *  com.google.common.io.FileWriteMode
 *  com.google.common.io.Files
 *  com.google.common.util.concurrent.Runnables
 *  com.google.gson.Gson
 *  com.google.gson.annotations.SerializedName
 *  javax.annotation.Nullable
 *  javax.inject.Inject
 *  javax.inject.Named
 *  net.runelite.api.Client
 *  net.runelite.api.MessageNode
 *  net.runelite.api.Player
 *  net.runelite.api.events.ChatMessage
 *  net.runelite.api.events.OverheadTextChanged
 *  okhttp3.Call
 *  okhttp3.Callback
 *  okhttp3.HttpUrl
 *  okhttp3.OkHttpClient
 *  okhttp3.Request
 *  okhttp3.Request$Builder
 *  okhttp3.Response
 *  org.jetbrains.annotations.VisibleForTesting
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package net.runelite.client.plugins.emojis;

import com.google.common.hash.Hashing;
import com.google.common.io.FileWriteMode;
import com.google.common.io.Files;
import com.google.common.util.concurrent.Runnables;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import javax.annotation.Nullable;
import javax.imageio.ImageIO;
import javax.inject.Inject;
import javax.inject.Named;
import net.runelite.api.Client;
import net.runelite.api.MessageNode;
import net.runelite.api.Player;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.OverheadTextChanged;
import net.runelite.client.RuneLite;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.game.ChatIconManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.emojis.Emoji;
import net.runelite.client.util.Text;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.jetbrains.annotations.VisibleForTesting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@PluginDescriptor(name="Emojis", description="Replaces common emoticons such as :) with their corresponding emoji in the chat", enabledByDefault=false)
public class EmojiPlugin
extends Plugin {
    private static final Logger log = LoggerFactory.getLogger(EmojiPlugin.class);
    private static final File EMOJI_DIR = new File(RuneLite.CACHE_DIR, "emojis");
    @Inject
    private Client client;
    @Inject
    private ClientThread clientThread;
    @Inject
    private ChatIconManager chatIconManager;
    @Inject
    private OkHttpClient okHttpClient;
    @Inject
    private Gson gson;
    @Inject
    private ScheduledExecutorService scheduledExecutorService;
    @Inject
    @Named(value="runelite.static.base")
    private HttpUrl staticBase;
    @VisibleForTesting
    Index index;
    private final Map<String, Integer> imageCache = new HashMap<String, Integer>();

    @Override
    protected void startUp() {
        this.scheduledExecutorService.execute(this::initEmojiCache);
    }

    @Subscribe
    public void onChatMessage(ChatMessage chatMessage) {
        switch (chatMessage.getType()) {
            case PUBLICCHAT: 
            case MODCHAT: 
            case FRIENDSCHAT: 
            case CLAN_CHAT: 
            case CLAN_GUEST_CHAT: 
            case CLAN_GIM_CHAT: 
            case PRIVATECHAT: 
            case PRIVATECHATOUT: 
            case MODPRIVATECHAT: {
                break;
            }
            default: {
                return;
            }
        }
        MessageNode messageNode = chatMessage.getMessageNode();
        String message = messageNode.getValue();
        String updatedMessage = this.updateMessage(message);
        if (updatedMessage == null) {
            return;
        }
        messageNode.setValue(updatedMessage);
    }

    @Subscribe
    public void onOverheadTextChanged(OverheadTextChanged event) {
        if (!(event.getActor() instanceof Player)) {
            return;
        }
        String message = event.getOverheadText();
        String updatedMessage = this.updateMessage(message);
        if (updatedMessage == null) {
            return;
        }
        event.getActor().setOverheadText(updatedMessage);
    }

    @Nullable
    String updateMessage(String message) {
        if (this.index == null) {
            return null;
        }
        String editedMessage = message;
        int idxStart = -1;
        int idxStartWs = -1;
        for (int i = 0; i < message.length(); ++i) {
            char c = message.charAt(i);
            if (Character.isWhitespace(c) || c == '\u00a0' || i + 1 == message.length()) {
                int idxEndWs = i + 1 == message.length() ? message.length() : i;
                String shortname = Text.removeFormattingTags(message.substring(idxStartWs + 1, idxEndWs));
                idxStartWs = i;
                Emoji emoji = Emoji.getEmoji(shortname);
                if (emoji != null) {
                    String id = Integer.toHexString(emoji.codepoint);
                    int emojiId = this.getEmojiChatIconIndex(emoji.name(), id);
                    editedMessage = editedMessage.replace(shortname, "<img=" + this.chatIconManager.chatIconIndex(emojiId) + ">");
                }
            }
            if (c != ':') continue;
            if (idxStart == -1) {
                idxStart = i;
                continue;
            }
            String emojiName = message.substring(idxStart + 1, i);
            idxStart = -1;
            String id = this.index.names.get(emojiName);
            if (id == null) continue;
            int emojiId = this.getEmojiChatIconIndex(emojiName, id);
            editedMessage = editedMessage.replace(":" + emojiName + ":", "<img=" + this.chatIconManager.chatIconIndex(emojiId) + ">");
        }
        return message == editedMessage ? null : editedMessage;
    }

    private int getEmojiChatIconIndex(String name, String codepoint) {
        Integer emojiId = this.imageCache.get(codepoint);
        if (emojiId != null) {
            return emojiId;
        }
        int iconId = this.chatIconManager.reserveChatIcon();
        this.imageCache.put(codepoint, iconId);
        this.scheduledExecutorService.submit(() -> {
            try {
                BufferedImage image = this.loadEmojiFromDisk(name, codepoint);
                this.chatIconManager.updateChatIcon(iconId, image);
            }
            catch (IOException ex) {
                log.error("Unable to load emoji {}", (Object)name, (Object)ex);
            }
        });
        return iconId;
    }

    private void initEmojiCache() {
        EMOJI_DIR.mkdirs();
        File indexFile = new File(EMOJI_DIR, "index.json");
        this.download("emoji/index.json", indexFile, () -> {
            try (InputStreamReader in = new InputStreamReader(Files.asByteSource((File)indexFile).openStream());){
                this.index = (Index)this.gson.fromJson((Reader)in, Index.class);
            }
            catch (IOException ex) {
                log.error("Unable to load emoji index", (Throwable)ex);
            }
            try {
                File assetFile = new File(EMOJI_DIR, "assets.zip");
                String hash = Files.asByteSource((File)assetFile).hash(Hashing.sha256()).toString();
                if (this.index != null && hash.equals(this.index.assetsHash)) {
                    log.debug("Emoji assets are up to date");
                    return;
                }
            }
            catch (IOException ex) {
                log.debug(null, (Throwable)ex);
            }
            log.info("Downloading emoji assets");
            this.download("emoji/assets.zip", new File(EMOJI_DIR, "assets.zip"), Runnables.doNothing());
        });
    }

    private void download(String srnPath, final File to, final Runnable cb) {
        final HttpUrl url = this.staticBase.newBuilder().addPathSegments(srnPath).build();
        Request request = new Request.Builder().url(url).build();
        this.okHttpClient.newCall(request).enqueue(new Callback(){

            public void onResponse(Call call, Response response) throws IOException {
                try (Response response2 = response;
                     InputStream in = response.body().byteStream();){
                    Files.asByteSink((File)to, (FileWriteMode[])new FileWriteMode[0]).writeFrom(in);
                }
                cb.run();
            }

            public void onFailure(Call call, IOException e) {
                log.error("Unable to download {}", (Object)url, (Object)e);
                cb.run();
            }
        });
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private BufferedImage loadEmojiFromDisk(String name, String id) throws IOException {
        try (ZipFile zipFile = new ZipFile(new File(EMOJI_DIR, "assets.zip"));){
            ZipEntry entry = zipFile.getEntry(id + ".png");
            if (entry != null) {
                try (InputStream in = zipFile.getInputStream(entry);){
                    Object object = ImageIO.class;
                    synchronized (ImageIO.class) {
                        BufferedImage image = ImageIO.read(in);
                        // ** MonitorExit[var7_8] (shouldn't be in output)
                        log.debug("Loaded emoji {}: {}", (Object)name, (Object)id);
                        object = image;
                        return object;
                    }
                }
            }
            throw new IOException("file " + id + ".png doesn't exist");
        }
    }

    static class Index {
        Map<String, String> names = Collections.emptyMap();
        @SerializedName(value="assets_hash")
        String assetsHash;

        public Map<String, String> getNames() {
            return this.names;
        }

        public String getAssetsHash() {
            return this.assetsHash;
        }

        public void setNames(Map<String, String> names) {
            this.names = names;
        }

        public void setAssetsHash(String assetsHash) {
            this.assetsHash = assetsHash;
        }

        public boolean equals(Object o) {
            if (o == this) {
                return true;
            }
            if (!(o instanceof Index)) {
                return false;
            }
            Index other = (Index)o;
            if (!other.canEqual(this)) {
                return false;
            }
            Map<String, String> this$names = this.getNames();
            Map<String, String> other$names = other.getNames();
            if (this$names == null ? other$names != null : !((Object)this$names).equals(other$names)) {
                return false;
            }
            String this$assetsHash = this.getAssetsHash();
            String other$assetsHash = other.getAssetsHash();
            return !(this$assetsHash == null ? other$assetsHash != null : !this$assetsHash.equals(other$assetsHash));
        }

        protected boolean canEqual(Object other) {
            return other instanceof Index;
        }

        public int hashCode() {
            int PRIME = 59;
            int result = 1;
            Map<String, String> $names = this.getNames();
            result = result * 59 + ($names == null ? 43 : ((Object)$names).hashCode());
            String $assetsHash = this.getAssetsHash();
            result = result * 59 + ($assetsHash == null ? 43 : $assetsHash.hashCode());
            return result;
        }

        public String toString() {
            return "EmojiPlugin.Index(names=" + String.valueOf(this.getNames()) + ", assetsHash=" + this.getAssetsHash() + ")";
        }
    }
}

