/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package net.runelite.client.plugins.twitch.irc;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import javax.net.SocketFactory;
import javax.net.ssl.SSLSocketFactory;
import net.runelite.client.plugins.twitch.irc.Message;
import net.runelite.client.plugins.twitch.irc.TwitchListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TwitchIRCClient
extends Thread
implements AutoCloseable {
    private static final Logger log = LoggerFactory.getLogger(TwitchIRCClient.class);
    private static final String HOST = "irc.chat.twitch.tv";
    private static final int PORT = 6697;
    private static final int READ_TIMEOUT = 60000;
    private static final int PING_TIMEOUT = 20000;
    private final TwitchListener twitchListener;
    private final String username;
    private final String password;
    private final String channel;
    private Socket socket;
    private BufferedReader in;
    private Writer out;
    private long last;
    private boolean pingSent;

    public TwitchIRCClient(TwitchListener twitchListener, String username, String password, String channel) {
        this.setName("Twitch");
        this.twitchListener = twitchListener;
        this.username = username;
        this.password = password;
        this.channel = channel;
    }

    @Override
    public void close() {
        try {
            if (this.socket != null) {
                this.socket.close();
            }
        }
        catch (IOException ex) {
            log.warn("error closing socket", (Throwable)ex);
        }
        this.in = null;
        this.out = null;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void run() {
        try {
            SocketFactory socketFactory = SSLSocketFactory.getDefault();
            this.socket = socketFactory.createSocket(HOST, 6697);
            this.socket.setSoTimeout(60000);
            this.in = new BufferedReader(new InputStreamReader(this.socket.getInputStream(), StandardCharsets.UTF_8));
            this.out = new OutputStreamWriter(this.socket.getOutputStream(), StandardCharsets.UTF_8);
        }
        catch (IOException ex) {
            log.warn("unable to setup irc client", (Throwable)ex);
            return;
        }
        try {
            String line;
            this.register(this.username, this.password);
            this.join(this.channel);
            while ((line = this.read()) != null) {
                log.debug("<- {}", (Object)line);
                this.last = System.currentTimeMillis();
                this.pingSent = false;
                Message message = Message.parse(line);
                switch (message.getCommand()) {
                    case "PING": {
                        this.send("PONG", message.getArguments()[0]);
                        break;
                    }
                    case "PRIVMSG": {
                        this.twitchListener.privmsg(message.getSource().substring(0, message.getSource().indexOf(33)), message.getTags(), message.getArguments()[1]);
                        break;
                    }
                    case "ROOMSTATE": {
                        this.twitchListener.roomstate(message.getTags());
                        break;
                    }
                    case "USERNOTICE": {
                        this.twitchListener.usernotice(message.getTags(), message.getArguments().length > 0 ? message.getArguments()[0] : null);
                    }
                }
            }
        }
        catch (IOException ex) {
            log.debug("error in twitch irc client", (Throwable)ex);
        }
        finally {
            try {
                this.socket.close();
            }
            catch (IOException e) {
                log.warn(null, (Throwable)e);
            }
        }
    }

    public boolean isConnected() {
        return this.socket != null && this.socket.isConnected() && !this.socket.isClosed();
    }

    public void pingCheck() {
        if (this.out == null) {
            return;
        }
        if (!this.pingSent && System.currentTimeMillis() - this.last >= 20000L) {
            try {
                this.ping("twitch");
                this.pingSent = true;
            }
            catch (IOException e) {
                log.debug("Ping failure, disconnecting.", (Throwable)e);
                this.close();
            }
        } else if (this.pingSent) {
            log.debug("Ping timeout, disconnecting.");
            this.close();
        }
    }

    private void register(String username, String oauth) throws IOException {
        this.send("CAP", "REQ", "twitch.tv/commands twitch.tv/tags");
        this.send("PASS", oauth);
        this.send("USER", "runelite", ".", HOST, "runelite");
        this.send("NICK", username);
    }

    private void join(String channel) throws IOException {
        this.send("JOIN", channel);
    }

    private void ping(String destination) throws IOException {
        this.send("PING", destination);
    }

    public void privmsg(String message) throws IOException {
        this.send("PRIVMSG", this.channel, message);
    }

    private void send(String command, String ... args) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(command);
        for (int i = 0; i < args.length; ++i) {
            stringBuilder.append(' ');
            if (i + 1 == args.length) {
                stringBuilder.append(':');
            }
            stringBuilder.append(args[i]);
        }
        log.debug("-> {}", (Object)stringBuilder.toString());
        stringBuilder.append("\r\n");
        this.out.write(stringBuilder.toString());
        this.out.flush();
    }

    private String read() throws IOException {
        int len;
        String line = this.in.readLine();
        if (line == null) {
            return null;
        }
        for (len = line.length(); len > 0 && (line.charAt(len - 1) == '\r' || line.charAt(len - 1) == '\n'); --len) {
        }
        return line.substring(0, len);
    }
}

