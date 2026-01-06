/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.Gson
 *  javax.inject.Inject
 *  javax.inject.Named
 *  javax.inject.Singleton
 *  net.runelite.http.api.account.OAuthResponse
 *  okhttp3.HttpUrl
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package net.runelite.client.account;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpServer;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.ScheduledExecutorService;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import net.runelite.client.account.AccountClient;
import net.runelite.client.account.AccountSession;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.events.SessionClose;
import net.runelite.client.events.SessionOpen;
import net.runelite.client.util.LinkBrowser;
import net.runelite.http.api.account.OAuthResponse;
import okhttp3.HttpUrl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class SessionManager {
    private static final Logger log = LoggerFactory.getLogger(SessionManager.class);
    private AccountSession accountSession;
    private final EventBus eventBus;
    private final File sessionFile;
    private final AccountClient accountClient;
    private final Gson gson;
    private final String oauthRedirect;
    private final ScheduledExecutorService scheduledExecutorService;
    private HttpServer server;

    @Inject
    private SessionManager(@Named(value="sessionfile") File sessionfile, EventBus eventBus, AccountClient accountClient, Gson gson, @Named(value="runelite.oauth.redirect") String oauthRedirect, ScheduledExecutorService scheduledExecutorService) {
        this.eventBus = eventBus;
        this.sessionFile = sessionfile;
        this.accountClient = accountClient;
        this.gson = gson;
        this.oauthRedirect = oauthRedirect;
        this.scheduledExecutorService = scheduledExecutorService;
        eventBus.register(this);
    }

    public void loadSession() {
        AccountSession session;
        if (!this.sessionFile.exists()) {
            log.info("No session file exists");
            return;
        }
        try (FileInputStream in = new FileInputStream(this.sessionFile);){
            session = (AccountSession)this.gson.fromJson((Reader)new InputStreamReader((InputStream)in, StandardCharsets.UTF_8), AccountSession.class);
            log.debug("Loaded session for {}", (Object)session.getUsername());
        }
        catch (Exception ex) {
            log.warn("Unable to load session file", (Throwable)ex);
            return;
        }
        this.accountClient.setUuid(session.getUuid());
        if (!this.accountClient.sessionCheck()) {
            log.debug("Loaded session {} is invalid", (Object)session.getUuid());
            return;
        }
        this.accountSession = session;
    }

    private void saveSession() {
        if (this.accountSession == null) {
            return;
        }
        try (OutputStreamWriter fw = new OutputStreamWriter(Files.newOutputStream(this.sessionFile.toPath(), new OpenOption[0]), StandardCharsets.UTF_8);){
            this.gson.toJson((Object)this.accountSession, (Appendable)fw);
            log.debug("Saved session to {}", (Object)this.sessionFile);
        }
        catch (IOException ex) {
            log.warn("Unable to save session file", (Throwable)ex);
        }
    }

    private void deleteSession() {
        this.sessionFile.delete();
    }

    private void openSession(AccountSession session) {
        this.accountSession = session;
        this.eventBus.post(new SessionOpen());
    }

    private void closeSession() {
        if (this.accountSession == null) {
            return;
        }
        log.debug("Logging out of account {}", (Object)this.accountSession.getUsername());
        this.accountClient.setUuid(this.accountSession.getUuid());
        try {
            this.accountClient.logout();
        }
        catch (IOException ex) {
            log.warn("Unable to sign out of session", (Throwable)ex);
        }
        this.accountSession = null;
        this.eventBus.post(new SessionClose());
    }

    public void login() {
        OAuthResponse login;
        if (this.server == null) {
            try {
                this.startServer();
            }
            catch (IOException ex) {
                log.error("Unable to start http server", (Throwable)ex);
                return;
            }
        }
        try {
            login = this.accountClient.login(this.server.getAddress().getPort());
        }
        catch (IOException ex) {
            log.error("Unable to get oauth url", (Throwable)ex);
            return;
        }
        LinkBrowser.browse(login.getOauthUrl());
    }

    public void logout() {
        this.closeSession();
        this.deleteSession();
    }

    private void startServer() throws IOException {
        this.server = HttpServer.create(new InetSocketAddress("localhost", 0), 1);
        this.server.createContext("/oauth", req -> {
            try {
                HttpUrl url = HttpUrl.get((String)("http://localhost" + String.valueOf(req.getRequestURI())));
                String username = url.queryParameter("username");
                UUID sessionId = UUID.fromString(url.queryParameter("sessionId"));
                log.debug("Now signed in as {}", (Object)username);
                AccountSession session = new AccountSession(sessionId, Instant.now(), username);
                this.openSession(session);
                this.saveSession();
                HttpUrl redirect = HttpUrl.get((String)this.oauthRedirect).newBuilder().addQueryParameter("username", username).addQueryParameter("sessionId", sessionId.toString()).build();
                req.getResponseHeaders().set("Location", redirect.toString());
                req.sendResponseHeaders(302, 0L);
            }
            catch (Exception e) {
                log.warn("failure serving oauth response", (Throwable)e);
                req.sendResponseHeaders(400, 0L);
                req.getResponseBody().write(e.getMessage().getBytes(StandardCharsets.UTF_8));
            }
            finally {
                req.close();
                this.scheduledExecutorService.execute(this::stopServer);
            }
        });
        log.debug("Starting server {}", (Object)this.server);
        this.server.start();
    }

    private void stopServer() {
        log.debug("Stopping server {}", (Object)this.server);
        this.server.stop(0);
        this.server = null;
    }

    public AccountSession getAccountSession() {
        return this.accountSession;
    }
}

