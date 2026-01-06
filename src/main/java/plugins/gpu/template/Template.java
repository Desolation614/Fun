/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.io.CharStreams
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package net.runelite.client.plugins.gpu.template;

import com.google.common.io.CharStreams;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Template {
    private static final Logger log = LoggerFactory.getLogger(Template.class);
    private final List<Function<String, String>> resourceLoaders = new ArrayList<Function<String, String>>();

    public String process(String str) {
        StringBuilder sb = new StringBuilder();
        for (String line : str.split("\r?\n")) {
            if (line.startsWith("#include ")) {
                String resource = line.substring(9);
                if (resource.startsWith("\"") && resource.endsWith("\"")) {
                    resource = resource.substring(1, resource.length() - 1);
                }
                String resourceStr = this.load(resource);
                sb.append(resourceStr);
                continue;
            }
            sb.append(line).append('\n');
        }
        return sb.toString();
    }

    public String load(String filename) {
        for (Function<String, String> loader : this.resourceLoaders) {
            String value = loader.apply(filename);
            if (value == null) continue;
            return this.process(value);
        }
        return "";
    }

    public Template add(Function<String, String> fn) {
        this.resourceLoaders.add(fn);
        return this;
    }

    public Template addInclude(Class<?> clazz) {
        return this.add(f -> {
            try (InputStream is = clazz.getResourceAsStream((String)f);){
                if (is == null) return null;
                String string = Template.inputStreamToString(is);
                return string;
            }
            catch (IOException ex) {
                log.warn(null, (Throwable)ex);
            }
            return null;
        });
    }

    private static String inputStreamToString(InputStream in) {
        try {
            return CharStreams.toString((Readable)new InputStreamReader(in, StandardCharsets.UTF_8));
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

