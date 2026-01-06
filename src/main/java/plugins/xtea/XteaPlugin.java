/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.Gson
 *  com.google.gson.reflect.TypeToken
 *  javax.inject.Inject
 *  net.runelite.api.Client
 *  net.runelite.api.GameState
 *  net.runelite.api.events.GameStateChanged
 *  net.runelite.http.api.xtea.XteaKey
 *  net.runelite.http.api.xtea.XteaRequest
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package net.runelite.client.plugins.xtea;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.events.GameStateChanged;
import net.runelite.client.RuneLite;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.xtea.XteaClient;
import net.runelite.http.api.xtea.XteaKey;
import net.runelite.http.api.xtea.XteaRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@PluginDescriptor(name="Xtea", hidden=true, forceDisabled=true)
public class XteaPlugin
extends Plugin {
    private static final Logger log = LoggerFactory.getLogger(XteaPlugin.class);
    private static final File XTEA_CACHE = new File(RuneLite.CACHE_DIR, "xtea.json");
    @Inject
    private Client client;
    @Inject
    private XteaClient xteaClient;
    @Inject
    private ScheduledExecutorService executorService;
    @Inject
    private Gson gson;
    private Map<Integer, int[]> xteas;

    @Override
    protected void startUp() {
        this.executorService.execute(() -> {
            this.xteas = this.load();
        });
    }

    /*
     * Exception decompiling
     */
    private Map<Integer, int[]> load() {
        /*
         * This method has failed to decompile.  When submitting a bug report, please provide this stack trace, and (if you hold appropriate legal rights) the relevant class file.
         * 
         * org.benf.cfr.reader.util.ConfusedCFRException: Started 3 blocks at once
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.getStartingBlocks(Op04StructuredStatement.java:412)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.buildNestedBlocks(Op04StructuredStatement.java:487)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op03SimpleStatement.createInitialStructuredBlock(Op03SimpleStatement.java:736)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisInner(CodeAnalyser.java:850)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisOrWrapFail(CodeAnalyser.java:278)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysis(CodeAnalyser.java:201)
         *     at org.benf.cfr.reader.entities.attributes.AttributeCode.analyse(AttributeCode.java:94)
         *     at org.benf.cfr.reader.entities.Method.analyse(Method.java:531)
         *     at org.benf.cfr.reader.entities.ClassFile.analyseMid(ClassFile.java:1055)
         *     at org.benf.cfr.reader.entities.ClassFile.analyseTop(ClassFile.java:942)
         *     at org.benf.cfr.reader.Driver.doJarVersionTypes(Driver.java:257)
         *     at org.benf.cfr.reader.Driver.doJar(Driver.java:139)
         *     at org.benf.cfr.reader.CfrDriverImpl.analyse(CfrDriverImpl.java:76)
         *     at org.benf.cfr.reader.Main.main(Main.java:54)
         */
        throw new IllegalStateException("Decompilation failed");
    }

    private void save() {
        try (FileOutputStream out = new FileOutputStream(XTEA_CACHE);
             FileChannel channel = out.getChannel();
             OutputStreamWriter writer = new OutputStreamWriter((OutputStream)out, StandardCharsets.UTF_8);){
            channel.lock();
            this.gson.toJson(this.xteas, new TypeToken<Map<Integer, int[]>>(){}.getType(), (Appendable)writer);
        }
        catch (IOException e) {
            log.debug("error saving xteas", (Throwable)e);
        }
    }

    @Subscribe
    public void onGameStateChanged(GameStateChanged gameStateChanged) {
        if (gameStateChanged.getGameState() != GameState.LOGGED_IN) {
            return;
        }
        int revision = this.client.getRevision();
        int[] regions = this.client.getMapRegions();
        int[][] xteaKeys = this.client.getXteaKeys();
        XteaRequest xteaRequest = new XteaRequest();
        xteaRequest.setRevision(revision);
        for (int idx = 0; idx < regions.length; ++idx) {
            int region = regions[idx];
            int[] keys = xteaKeys[idx];
            int[] seenKeys = this.xteas.get(region);
            if (Arrays.equals(seenKeys, keys)) continue;
            this.xteas.put(region, keys);
            log.debug("Region {} keys {}, {}, {}, {}", new Object[]{region, keys[0], keys[1], keys[2], keys[3]});
            XteaKey xteaKey = new XteaKey();
            xteaKey.setRegion(region);
            xteaKey.setKeys(keys);
            xteaRequest.addKey(xteaKey);
        }
        if (xteaRequest.getKeys().isEmpty()) {
            return;
        }
        this.xteaClient.submit(xteaRequest);
        this.executorService.execute(this::save);
    }
}

