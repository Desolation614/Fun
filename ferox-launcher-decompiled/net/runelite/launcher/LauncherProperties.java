/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.launcher;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LauncherProperties {
    private static final Logger log = LoggerFactory.getLogger(LauncherProperties.class);
    private static final String LAUNCHER_VERSION = "runelite.launcher.version";
    private static final String DISCORD_INVITE = "runelite.discord.invite";
    private static final String TROUBLESHOOTING_LINK = "runelite.wiki.troubleshooting.link";
    private static final String DNS_CHANGE_LINK = "runelite.dnschange.link";
    private static final String DOWNLOAD_LINK = "runelite.download.link";
    private static final String BOOTSTRAP = "runelite.bootstrap";
    private static final String BOOTSTRAPSIG = "runelite.bootstrapsig";
    private static final String MAIN = "runelite.main";
    private static final String RUNELITE_128 = "runelite.128";
    private static final String RUNELITE_SPLASH = "runelite.splash";
    private static final Properties properties = new Properties();

    public static String getVersionKey() {
        return LAUNCHER_VERSION;
    }

    public static String getVersion() {
        return properties.getProperty(LAUNCHER_VERSION);
    }

    public static String getDiscordInvite() {
        return properties.getProperty(DISCORD_INVITE);
    }

    public static String getTroubleshootingLink() {
        return properties.getProperty(TROUBLESHOOTING_LINK);
    }

    public static String getDNSChangeLink() {
        return properties.getProperty(DNS_CHANGE_LINK);
    }

    public static String getDownloadLink() {
        return properties.getProperty(DOWNLOAD_LINK);
    }

    public static String getBootstrap() {
        return properties.getProperty(BOOTSTRAP);
    }

    public static String getBootstrapSig() {
        return properties.getProperty(BOOTSTRAPSIG);
    }

    public static String getMain() {
        return properties.getProperty(MAIN);
    }

    public static String getRuneLite128() {
        return properties.getProperty(RUNELITE_128);
    }

    public static String getRuneLiteSplash() {
        return properties.getProperty(RUNELITE_SPLASH);
    }

    static {
        InputStream in = LauncherProperties.class.getResourceAsStream("launcher.properties");
        try {
            properties.load(in);
        }
        catch (IOException ex) {
            log.warn("Unable to load properties", ex);
        }
    }
}

