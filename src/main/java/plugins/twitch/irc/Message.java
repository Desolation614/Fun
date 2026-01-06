/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.plugins.twitch.irc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

class Message {
    private final Map<String, String> tags = new HashMap<String, String>();
    private String source;
    private String command;
    private String[] arguments;

    Message() {
    }

    public static Message parse(String in) {
        int sp;
        Message message = new Message();
        if (in.startsWith("@")) {
            String[] tags;
            for (String tag : tags = in.substring(1).split(";")) {
                int eq = tag.indexOf(61);
                if (eq == -1) continue;
                String key = tag.substring(0, eq);
                String value = tag.substring(eq + 1).replace("\\:", ";").replace("\\s", " ").replace("\\\\", "\\").replace("\\r", "\r").replace("\\n", "\n");
                message.tags.put(key, value);
            }
            int sp2 = in.indexOf(32);
            in = in.substring(sp2 + 1);
        }
        if (in.startsWith(":")) {
            int sp3 = in.indexOf(32);
            message.source = in.substring(1, sp3);
            in = in.substring(sp3 + 1);
        }
        if ((sp = in.indexOf(32)) == -1) {
            message.command = in;
            message.arguments = new String[0];
            return message;
        }
        message.command = in.substring(0, sp);
        String args = in.substring(sp + 1);
        ArrayList<String> argList = new ArrayList<String>();
        do {
            String arg;
            if (args.startsWith(":")) {
                arg = args.substring(1);
                sp = -1;
            } else {
                sp = args.indexOf(32);
                arg = sp == -1 ? args : args.substring(0, sp);
            }
            args = args.substring(sp + 1);
            argList.add(arg);
        } while (sp != -1);
        message.arguments = argList.toArray(new String[0]);
        return message;
    }

    public Map<String, String> getTags() {
        return this.tags;
    }

    public String getSource() {
        return this.source;
    }

    public String getCommand() {
        return this.command;
    }

    public String[] getArguments() {
        return this.arguments;
    }
}

