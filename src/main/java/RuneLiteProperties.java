/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package net.runelite.client;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import javax.annotation.Nullable;

public class RuneLiteProperties {
    private static final String RUNELITE_VERSION = "runelite.version";
    private static final String RUNELITE_COMMIT = "runelite.commit";
    private static final String RUNELITE_DIRTY = "runelite.dirty";
    private static final String DISCORD_INVITE = "runelite.discord.invite";
    private static final String LAUNCHER_VERSION_PROPERTY = "runelite.launcher.version";
    private static final String INSECURE_SKIP_TLS_VERIFICATION_PROPERTY = "runelite.insecure-skip-tls-verification";
    private static final String TROUBLESHOOTING_LINK = "runelite.wiki.troubleshooting.link";
    private static final String BUILDING_LINK = "runelite.wiki.building.link";
    private static final String DNS_CHANGE_LINK = "runelite.dnschange.link";
    private static final String JAV_CONFIG = "runelite.jav_config";
    private static final String JAV_CONFIG_BACKUP = "runelite.jav_config_backup";
    private static final String PLUGINHUB_VERSION = "runelite.pluginhub.version";
    private static final String API_BASE = "runelite.api.base";
    private static final String RUNELITE_CONFIG = "runelite.config";
    private static final String OSRS_TWITTER_LINK = "runelite.osrstwitter.link";
    private static final String JAGEX_DOMAINBLOCK = "runelite.jagex.domainblock";
    private static final Properties properties = new Properties();

    public static String getVersion() {
        return properties.getProperty(RUNELITE_VERSION);
    }

    public static String getCommit() {
        return properties.getProperty(RUNELITE_COMMIT);
    }

    public static boolean isDirty() {
        return Boolean.parseBoolean(properties.getProperty(RUNELITE_DIRTY));
    }

    public static String getDiscordInvite() {
        return properties.getProperty(DISCORD_INVITE);
    }

    @Nullable
    public static String getLauncherVersion() {
        return System.getProperty(LAUNCHER_VERSION_PROPERTY);
    }

    public static boolean isInsecureSkipTlsVerification() {
        return Boolean.getBoolean(INSECURE_SKIP_TLS_VERIFICATION_PROPERTY);
    }

    public static String getTroubleshootingLink() {
        return properties.getProperty(TROUBLESHOOTING_LINK);
    }

    public static String getBuildingLink() {
        return properties.getProperty(BUILDING_LINK);
    }

    public static String getDNSChangeLink() {
        return properties.getProperty(DNS_CHANGE_LINK);
    }

    public static String getJavConfig() {
        return properties.getProperty(JAV_CONFIG);
    }

    public static String getJavConfigBackup() {
        return properties.getProperty(JAV_CONFIG_BACKUP);
    }

    public static String getPluginHubVersion() {
        return System.getProperty(PLUGINHUB_VERSION, properties.getProperty(PLUGINHUB_VERSION));
    }

    public static String getApiBase() {
        return properties.getProperty(API_BASE);
    }

    public static String getRuneLiteConfig() {
        return properties.getProperty(RUNELITE_CONFIG);
    }

    public static String getOSRSTwitterLink() {
        return properties.getProperty(OSRS_TWITTER_LINK);
    }

    public static String[] getJagexBlockedDomains() {
        return properties.getProperty(JAGEX_DOMAINBLOCK).split(",");
    }

    static Properties getProperties() {
        return properties;
    }

    static {
        try (InputStream in = RuneLiteProperties.class.getResourceAsStream("runelite.properties");){
            properties.load(in);
        }
        catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }
}

