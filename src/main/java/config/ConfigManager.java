/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.base.MoreObjects
 *  com.google.common.base.Strings
 *  com.google.common.collect.ComparisonChain
 *  com.google.gson.Gson
 *  com.google.inject.Module
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  javax.inject.Inject
 *  javax.inject.Named
 *  javax.inject.Singleton
 *  lombok.NonNull
 *  net.runelite.api.Client
 *  net.runelite.api.Player
 *  net.runelite.api.coords.WorldPoint
 *  net.runelite.api.events.AccountHashChanged
 *  net.runelite.api.events.PlayerChanged
 *  net.runelite.api.events.WorldChanged
 *  net.runelite.http.api.config.ConfigPatch
 *  net.runelite.http.api.config.ConfigPatchResult
 *  net.runelite.http.api.config.Configuration
 *  net.runelite.http.api.config.Profile
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package net.runelite.client.config;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.MoreObjects;
import com.google.common.base.Strings;
import com.google.common.collect.ComparisonChain;
import com.google.gson.Gson;
import com.google.inject.Module;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import lombok.NonNull;
import net.runelite.api.Client;
import net.runelite.api.Player;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.AccountHashChanged;
import net.runelite.api.events.PlayerChanged;
import net.runelite.api.events.WorldChanged;
import net.runelite.client.RuneLite;
import net.runelite.client.account.AccountSession;
import net.runelite.client.account.SessionManager;
import net.runelite.client.config.Alpha;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigClient;
import net.runelite.client.config.ConfigData;
import net.runelite.client.config.ConfigDescriptor;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigInvocationHandler;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigItemDescriptor;
import net.runelite.client.config.ConfigProfile;
import net.runelite.client.config.ConfigSection;
import net.runelite.client.config.ConfigSectionDescriptor;
import net.runelite.client.config.ConfigSerializer;
import net.runelite.client.config.Keybind;
import net.runelite.client.config.ModifierlessKeybind;
import net.runelite.client.config.ProfileManager;
import net.runelite.client.config.Range;
import net.runelite.client.config.RuneScapeProfile;
import net.runelite.client.config.RuneScapeProfileType;
import net.runelite.client.config.Serializer;
import net.runelite.client.config.Units;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ClientShutdown;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.events.ConfigSync;
import net.runelite.client.events.ProfileChanged;
import net.runelite.client.events.RuneScapeProfileChanged;
import net.runelite.client.events.SessionClose;
import net.runelite.client.events.SessionOpen;
import net.runelite.client.util.ColorUtil;
import net.runelite.client.util.RunnableExceptionLogger;
import net.runelite.http.api.config.ConfigPatch;
import net.runelite.http.api.config.ConfigPatchResult;
import net.runelite.http.api.config.Configuration;
import net.runelite.http.api.config.Profile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class ConfigManager {
    private static final Logger log = LoggerFactory.getLogger(ConfigManager.class);
    public static final String RSPROFILE_GROUP = "rsprofile";
    public static final String RSPROFILE_DISPLAY_NAME = "displayName";
    public static final String RSPROFILE_TYPE = "type";
    private static final String RSPROFILE_ACCOUNT_HASH = "accountHash";
    private static final long RSPROFILE_ID = -1L;
    private static final String RSPROFILE_NAME = "$rsprofile";
    private static final int KEY_SPLITTER_GROUP = 0;
    private static final int KEY_SPLITTER_PROFILE = 1;
    private static final int KEY_SPLITTER_KEY = 2;
    @Nullable
    private final String configProfileName;
    private final ScheduledExecutorService executor;
    private final EventBus eventBus;
    private final Client client;
    private final Gson gson;
    @Nonnull
    private final ConfigClient configClient;
    private final ProfileManager profileManager;
    private final SessionManager sessionManager;
    private final ConfigInvocationHandler handler = new ConfigInvocationHandler(this);
    private ConfigProfile profile;
    private ConfigProfile rsProfile;
    private ConfigData configProfile;
    private ConfigData rsProfileConfigProfile;
    @Nullable
    private String rsProfileKey;
    private final Map<Type, Serializer<?>> serializers = Collections.synchronizedMap(new WeakHashMap());

    @Inject
    private ConfigManager(@Nullable @Named(value="profile") String profile, ScheduledExecutorService scheduledExecutorService, EventBus eventBus, Client client, Gson gson, @Nonnull ConfigClient configClient, ProfileManager profileManager, SessionManager sessionManager) {
        this.configProfileName = profile;
        this.executor = scheduledExecutorService;
        this.eventBus = eventBus;
        this.client = client;
        this.gson = gson;
        this.configClient = configClient;
        this.profileManager = profileManager;
        this.sessionManager = sessionManager;
        scheduledExecutorService.scheduleWithFixedDelay(RunnableExceptionLogger.wrap(this::sendConfig), 30 + (int)(300.0 * Math.random()), 300L, TimeUnit.SECONDS);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void switchProfile(ConfigProfile newProfile) {
        ConfigData oldData;
        if (newProfile.getId() == this.profile.getId()) {
            log.warn("switching to already-active profile!");
            return;
        }
        this.sendConfig();
        log.info("Switching profile to: {} ({})", (Object)newProfile.getName(), (Object)newProfile.getId());
        if (this.sessionManager.getAccountSession() != null && newProfile.isSync()) {
            try (ProfileManager.Lock lock = this.profileManager.lock();){
                ConfigProfile profile = lock.findProfile(newProfile.getId());
                if (profile == null) {
                    log.warn("lost profile while switching!");
                    return;
                }
                List<Profile> profiles = this.configClient.profiles();
                if (profiles != null) {
                    this.syncRemote(lock, profile, profiles);
                }
            }
            catch (IOException ex) {
                log.error("error fetching remote profile", (Throwable)ex);
            }
        }
        ConfigData newData = new ConfigData(ProfileManager.profileConfigFile(newProfile));
        HashSet<String> allKeys = new HashSet<String>(newData.keySet());
        ConfigManager configManager = this;
        synchronized (configManager) {
            this.handler.invalidate();
            oldData = this.configProfile;
            this.profile = newProfile;
            this.configProfile = newData;
        }
        allKeys.addAll(oldData.keySet());
        for (String wholeKey : allKeys) {
            String newValue;
            String[] split = ConfigManager.splitKey(wholeKey);
            if (split == null) continue;
            String groupName = split[0];
            String profile = split[1];
            String key = split[2];
            String oldValue = oldData.getProperty(wholeKey);
            if (Objects.equals(oldValue, newValue = newData.getProperty(wholeKey))) continue;
            log.debug("Loading configuration value {}: {}", (Object)wholeKey, (Object)newValue);
            ConfigChanged configChanged = new ConfigChanged();
            configChanged.setGroup(groupName);
            configChanged.setProfile(profile);
            configChanged.setKey(key);
            configChanged.setOldValue(oldValue);
            configChanged.setNewValue(newValue);
            this.eventBus.post(configChanged);
        }
        this.eventBus.post(new ProfileChanged());
    }

    public String getRSProfileKey() {
        return this.rsProfileKey;
    }

    @Subscribe
    public void onSessionOpen(SessionOpen sessionOpen) {
        AccountSession session = this.sessionManager.getAccountSession();
        this.configClient.setUuid(session.getUuid());
        try {
            List<Profile> profiles = this.configClient.profiles();
            if (profiles != null) {
                this.mergeRemoteProfiles(profiles);
            }
        }
        catch (IOException e) {
            log.error("error syncing remote profiles", (Throwable)e);
        }
        ConfigPatch patch = ConfigManager.buildConfigPatch(this.rsProfile.getName(), this.rsProfileConfigProfile.get());
        this.configClient.patch(patch, this.rsProfile.getId());
        log.debug("patched remote {}", (Object)RSPROFILE_NAME);
    }

    @Subscribe
    public void onSessionClose(SessionClose sessionClose) {
        this.configClient.setUuid(null);
        try (ProfileManager.Lock lock = this.profileManager.lock();){
            this.profile = ConfigManager.updateProfile(lock, this.profile);
            this.rsProfile = ConfigManager.updateProfile(lock, this.rsProfile);
            lock.getProfiles().removeIf(p -> p != this.profile && !p.isInternal() && p.isSync());
            if (this.profile.isSync()) {
                log.info("Active remote profile '{}' lost due to session close, converting to a local profile.", (Object)this.profile.getName());
                this.profile.setSync(false);
                this.profile.setRev(-1L);
            }
            lock.dirty();
        }
    }

    public void toggleSync(ConfigProfile profile, boolean sync) {
        log.debug("Setting sync for {}: {}", (Object)profile.getName(), (Object)sync);
        this.sendConfig();
        try (ProfileManager.Lock lock = this.profileManager.lock();){
            profile = lock.findProfile(profile.getId());
            if (profile == null || profile.isSync() == sync) {
                return;
            }
            profile.setSync(sync);
            lock.dirty();
            if (sync) {
                File from = ProfileManager.profileConfigFile(profile);
                ConfigData data = new ConfigData(from);
                ConfigPatch patch = ConfigManager.buildConfigPatch(profile.getName(), data.get());
                this.configClient.patch(patch, profile.getId());
            } else {
                this.configClient.delete(profile.getId());
            }
        }
    }

    public void renameProfile(ConfigProfile profile, String name) {
        if (profile.isSync() && this.sessionManager.getAccountSession() != null) {
            this.configClient.rename(profile.getId(), name);
        }
    }

    private void migrate() {
        try (ProfileManager.Lock lock = this.profileManager.lock();){
            List<ConfigProfile> profiles = lock.getProfiles();
            File configFile = new File(RuneLite.RUNELITE_DIR, "settings.properties");
            if (profiles.isEmpty() && configFile.exists()) {
                String targetProfileName = "default";
                log.info("Performing migration of config from {} to profile '{}'", (Object)configFile.getName(), (Object)targetProfileName);
                ConfigProfile targetProfile = lock.createProfile(targetProfileName);
                profiles.forEach(p -> p.setActive(false));
                targetProfile.setActive(true);
                if (this.rsProfile == null) {
                    this.rsProfile = lock.findProfile(RSPROFILE_NAME);
                    if (this.rsProfile == null) {
                        this.rsProfile = lock.createProfile(RSPROFILE_NAME, -1L);
                    }
                    this.rsProfile.setSync(true);
                }
                if (this.rsProfileConfigProfile == null) {
                    this.rsProfileConfigProfile = new ConfigData(ProfileManager.profileConfigFile(this.rsProfile));
                }
                this.importAndMigrate(lock, configFile, targetProfile);
            }
        }
    }

    public void importAndMigrate(ProfileManager.Lock lock, File from, ConfigProfile targetProfile) {
        ConfigData migratingData = new ConfigData(from);
        ConfigData configData = new ConfigData(ProfileManager.profileConfigFile(targetProfile));
        log.debug("Importing profile from {}", (Object)from);
        HashSet<String> rsProfileKeys = new HashSet<String>();
        ArrayList<Map.Entry<String, String>> rsProfileEntries = new ArrayList<Map.Entry<String, String>>();
        int keys = 0;
        for (Map.Entry<String, String> entry : migratingData.get().entrySet()) {
            String[] split = ConfigManager.splitKey(entry.getKey());
            if (split == null) continue;
            String string = split[1];
            if (string != null) {
                rsProfileKeys.add(string);
                rsProfileEntries.add(entry);
                continue;
            }
            configData.setProperty(entry.getKey(), entry.getValue());
            ++keys;
        }
        if (rsProfileKeys.size() > 0) {
            HashMap<String, String> oldToNewRSProfile = new HashMap<String, String>();
            List<RuneScapeProfile> existingProfiles = this.getRSProfiles();
            for (String string : rsProfileKeys) {
                try {
                    RuneScapeProfileType type;
                    long accHash;
                    RuneScapeProfile newProfile;
                    String strHash = migratingData.getProperty(ConfigManager.getWholeKey(RSPROFILE_GROUP, string, RSPROFILE_ACCOUNT_HASH));
                    String strType = migratingData.getProperty(ConfigManager.getWholeKey(RSPROFILE_GROUP, string, RSPROFILE_TYPE));
                    if (!Strings.isNullOrEmpty((String)strHash) && !Strings.isNullOrEmpty((String)strType) && (newProfile = this.findRSProfile(existingProfiles, accHash = Long.parseLong(strHash), type = RuneScapeProfileType.valueOf(strType), null, true)) != null) {
                        existingProfiles.add(newProfile);
                        oldToNewRSProfile.put(string, newProfile.getKey());
                        log.info("importing rsprofile \"{}\" as \"{}\"", (Object)string, (Object)newProfile.getKey());
                        continue;
                    }
                    log.info("not importing rsprofile key \"{}\" (hash={} type={})", new Object[]{string, strHash, strType});
                }
                catch (IllegalArgumentException e) {
                    log.info("failed to unmarshal imported rsprofile data for key \"{}\"", (Object)string, (Object)e);
                }
            }
            for (Map.Entry entry : rsProfileEntries) {
                String[] split = ConfigManager.splitKey((String)entry.getKey());
                assert (split != null);
                String profile = split[1];
                if ((profile = (String)oldToNewRSProfile.get(profile)) == null || this.getConfiguration(split[0], profile, split[2]) != null) continue;
                this.setConfiguration(split[0], profile, split[2], (String)entry.getValue());
            }
        }
        configData.patch(configData.swapChanges());
        this.rsProfile = ConfigManager.updateProfile(lock, this.rsProfile);
        this.saveConfiguration(lock, this.rsProfile, this.rsProfileConfigProfile);
        log.info("Finished importing {} keys", (Object)keys);
    }

    private static void removeDuplicateProfiles(ProfileManager.Lock lock) {
        HashMap<Long, ConfigProfile> seen = new HashMap<Long, ConfigProfile>();
        Iterator<ConfigProfile> it = lock.getProfiles().iterator();
        while (it.hasNext()) {
            ConfigProfile profile = it.next();
            if (seen.containsKey(profile.getId())) {
                ConfigProfile existing = (ConfigProfile)seen.get(profile.getId());
                log.warn("Duplicate profiles detected: {} and {}. Removing the latter.", (Object)existing, (Object)profile);
                it.remove();
                lock.dirty();
                continue;
            }
            seen.put(profile.getId(), profile);
        }
    }

    private static void fixRsProfileName(ProfileManager.Lock lock) {
        ConfigProfile rsProfile = lock.findProfile(-1L);
        if (rsProfile != null && !rsProfile.getName().equals(RSPROFILE_NAME)) {
            log.warn("renaming {} to {}", (Object)rsProfile, (Object)RSPROFILE_NAME);
            rsProfile.setName(RSPROFILE_NAME);
            lock.dirty();
        }
    }

    public void load() {
        AccountSession session = this.sessionManager.getAccountSession();
        List<Profile> remoteProfiles = Collections.emptyList();
        if (session != null) {
            this.configClient.setUuid(session.getUuid());
            try {
                List<Profile> profiles = this.configClient.profiles();
                if (profiles != null) {
                    remoteProfiles = profiles;
                    this.mergeRemoteProfiles(remoteProfiles);
                }
            }
            catch (IOException ex) {
                log.error("error loading remote profiles", (Throwable)ex);
            }
        }
        this.migrate();
        try (ProfileManager.Lock lock = this.profileManager.lock();){
            ConfigManager.removeDuplicateProfiles(lock);
            ConfigManager.fixRsProfileName(lock);
            ConfigProfile profile = null;
            ConfigProfile rsProfile = null;
            for (ConfigProfile p2 : lock.getProfiles()) {
                if (p2.isInternal()) {
                    log.debug("Profile '{}' (sync: {}, active: {}, id: {}, internal)", new Object[]{p2.getName(), p2.isSync(), p2.getId(), p2.isActive()});
                    if (!p2.getName().equals(RSPROFILE_NAME)) continue;
                    rsProfile = p2;
                    continue;
                }
                log.info("Profile '{}' (sync: {}, active: {}, id: {})", new Object[]{p2.getName(), p2.isSync(), p2.isActive(), p2.getId()});
            }
            if (rsProfile == null) {
                rsProfile = lock.createProfile(RSPROFILE_NAME, -1L);
            }
            rsProfile.setSync(true);
            this.syncRemote(lock, rsProfile, remoteProfiles);
            this.rsProfile = rsProfile;
            this.rsProfileConfigProfile = new ConfigData(ProfileManager.profileConfigFile(rsProfile));
            String launcherDisplayName = this.client.getLauncherDisplayName();
            if (this.configProfileName != null) {
                profile = lock.findProfile(p -> !p.isInternal() && this.configProfileName.equals(p.getName()));
            } else {
                if (launcherDisplayName != null) {
                    profile = lock.findProfile(p -> {
                        if (p.isInternal()) {
                            return false;
                        }
                        List<String> defaultRsProfilesForProfile = p.getDefaultForRsProfiles();
                        if (defaultRsProfilesForProfile == null) {
                            return false;
                        }
                        for (String defaultRsProfile : defaultRsProfilesForProfile) {
                            String profileDisplayName;
                            RuneScapeProfileType rsProfileType = (RuneScapeProfileType)((Object)((Object)this.getConfiguration(RSPROFILE_GROUP, defaultRsProfile, RSPROFILE_TYPE, (Type)((Object)RuneScapeProfileType.class))));
                            if (rsProfileType != RuneScapeProfileType.STANDARD || !launcherDisplayName.equals(profileDisplayName = this.getConfiguration(RSPROFILE_GROUP, defaultRsProfile, RSPROFILE_DISPLAY_NAME))) continue;
                            return true;
                        }
                        return false;
                    });
                }
                if (profile == null) {
                    profile = lock.findProfile(p -> !p.isInternal() && p.isActive());
                }
                if (profile == null) {
                    profile = lock.findProfile(p -> !p.isInternal());
                }
            }
            if (profile != null) {
                log.info("Using profile: {} ({})", (Object)profile.getName(), (Object)profile.getId());
            } else {
                profile = lock.createProfile(this.configProfileName != null ? this.configProfileName : "default");
                if (this.configProfileName == null) {
                    lock.getProfiles().forEach(p -> p.setActive(false));
                    profile.setActive(true);
                }
                log.info("Creating profile: {} ({})", (Object)profile.getName(), (Object)profile.getId());
            }
            this.syncRemote(lock, profile, remoteProfiles);
            this.profile = profile;
            this.configProfile = new ConfigData(ProfileManager.profileConfigFile(profile));
        }
        this.eventBus.post(new ProfileChanged());
    }

    private void mergeRemoteProfiles(List<Profile> remoteProfiles) {
        try (ProfileManager.Lock lock = this.profileManager.lock();){
            boolean migrating = lock.getProfiles().isEmpty();
            block5: for (Profile remoteProfile : remoteProfiles) {
                for (ConfigProfile profile : lock.getProfiles()) {
                    if (profile.getId() != remoteProfile.getId()) continue;
                    log.debug("Found local profile {} for remote {}", (Object)profile, (Object)remoteProfile);
                    profile.setName((String)MoreObjects.firstNonNull((Object)remoteProfile.getName(), (Object)""));
                    profile.setSync(true);
                    lock.dirty();
                    continue block5;
                }
                log.debug("Creating local profile for remote {}", (Object)remoteProfile);
                ConfigProfile profile = lock.createProfile((String)MoreObjects.firstNonNull((Object)remoteProfile.getName(), (Object)""), remoteProfile.getId());
                profile.setSync(true);
                if (!migrating || remoteProfile.getId() != 0L) continue;
                log.info("Using remote profile {} as the active profile", (Object)profile.getName());
                profile.setActive(true);
            }
            block7: for (ConfigProfile localProfile : lock.getProfiles()) {
                for (Profile remoteProfile : remoteProfiles) {
                    if (localProfile.getId() != remoteProfile.getId()) continue;
                    continue block7;
                }
                log.debug("Found local profile {}", (Object)localProfile);
                if (!localProfile.isSync() || localProfile.isInternal()) continue;
                log.warn("Lost remote profile for '{}'", (Object)localProfile.getName());
                localProfile.setSync(false);
                localProfile.setRev(-1L);
                lock.dirty();
            }
        }
    }

    private void syncRemote(ProfileManager.Lock lock, ConfigProfile profile, List<Profile> remoteProfiles) {
        if (!profile.isSync()) {
            return;
        }
        long id = profile.getId();
        Profile remoteProfile = remoteProfiles.stream().filter(p -> p.getId() == id).findFirst().orElse(null);
        if (remoteProfile == null) {
            if (!profile.isInternal()) {
                log.warn("synced profile {} has no remote!", (Object)profile);
            }
            return;
        }
        if (profile.getRev() == remoteProfile.getRev()) {
            log.info("Profile '{}' is up to date", (Object)profile.getName());
        } else {
            log.info("Loading remote configuration for profile '{}'", (Object)profile.getName());
            try {
                Configuration remoteConfiguration = this.configClient.get(profile.getId());
                if (remoteConfiguration == null || remoteConfiguration.getConfig() == null || remoteConfiguration.getConfig().isEmpty()) {
                    log.debug("no remote configuration for {}", (Object)profile);
                    return;
                }
                File configFile = ProfileManager.profileConfigFile(profile);
                configFile.delete();
                ConfigData configData = new ConfigData(configFile);
                configData.putAll(remoteConfiguration.getConfig());
                configData.patch(configData.swapChanges());
                log.debug("synced remote profile {} rev {} to disk", (Object)profile, (Object)remoteConfiguration.getRev());
                profile.setRev(remoteConfiguration.getRev());
                lock.dirty();
            }
            catch (IOException ex) {
                log.error("unable to load remote configuration for {}", (Object)profile, (Object)ex);
            }
        }
    }

    public <T extends Config> T getConfig(Class<T> clazz) {
        if (!Modifier.isPublic(clazz.getModifiers())) {
            throw new RuntimeException("Non-public configuration classes can't have default methods invoked");
        }
        Config t = (Config)Proxy.newProxyInstance(clazz.getClassLoader(), new Class[]{clazz}, (InvocationHandler)this.handler);
        return (T)t;
    }

    public List<String> getConfigurationKeys(String prefix) {
        return this.configProfile.keySet().stream().filter(k -> k.startsWith(prefix)).collect(Collectors.toList());
    }

    public List<String> getRSProfileConfigurationKeys(String group, String profile, String keyPrefix) {
        if (profile == null) {
            return Collections.emptyList();
        }
        assert (profile.startsWith(RSPROFILE_GROUP));
        String prefix = group + "." + profile + "." + keyPrefix;
        return this.rsProfileConfigProfile.keySet().stream().filter(k -> k.startsWith(prefix)).map(k -> ConfigManager.splitKey(k)[2]).collect(Collectors.toList());
    }

    public static String getWholeKey(String groupName, String profile, String key) {
        if (profile == null) {
            return groupName + "." + key;
        }
        return groupName + "." + profile + "." + key;
    }

    private String getConfiguration(ConfigData configData, String groupName, String rsProfile, String key) {
        return configData.getProperty(ConfigManager.getWholeKey(groupName, rsProfile, key));
    }

    public String getConfiguration(String groupName, String key) {
        return this.getConfiguration(this.configProfile, groupName, null, key);
    }

    public String getRSProfileConfiguration(String groupName, String key) {
        String rsProfileKey = this.rsProfileKey;
        if (rsProfileKey == null) {
            return null;
        }
        return this.getConfiguration(this.rsProfileConfigProfile, groupName, rsProfileKey, key);
    }

    public String getConfiguration(String groupName, String profile, String key) {
        if (profile != null) {
            return this.getConfiguration(this.rsProfileConfigProfile, groupName, profile, key);
        }
        return this.getConfiguration(this.configProfile, groupName, null, key);
    }

    public <T> T getConfiguration(String groupName, String key, Type clazz) {
        return this.getConfiguration(groupName, null, key, clazz);
    }

    public <T> T getRSProfileConfiguration(String groupName, String key, Type clazz) {
        String rsProfileKey = this.rsProfileKey;
        if (rsProfileKey == null) {
            return null;
        }
        return this.getConfiguration(groupName, rsProfileKey, key, clazz);
    }

    public <T> T getConfiguration(String groupName, String profile, String key, Type type) {
        String value = this.getConfiguration(groupName, profile, key);
        if (!Strings.isNullOrEmpty((String)value)) {
            try {
                return (T)this.stringToObject(value, type);
            }
            catch (Exception e) {
                log.warn("Unable to unmarshal {} ", (Object)ConfigManager.getWholeKey(groupName, profile, key), (Object)e);
            }
        }
        return null;
    }

    private void setConfiguration(ConfigData configData, String groupName, String profile, String key, @NonNull String value) {
        if (value == null) {
            throw new NullPointerException("value is marked non-null but is null");
        }
        if (Strings.isNullOrEmpty((String)groupName) || Strings.isNullOrEmpty((String)key) || key.indexOf(58) != -1 || key.startsWith("$")) {
            throw new IllegalArgumentException();
        }
        assert (!key.startsWith("rsprofile."));
        String wholeKey = ConfigManager.getWholeKey(groupName, profile, key);
        String oldValue = configData.setProperty(wholeKey, value);
        if (Objects.equals(oldValue, value)) {
            return;
        }
        log.debug("Setting configuration value for {} to {}", (Object)wholeKey, (Object)value);
        this.handler.invalidate();
        ConfigChanged configChanged = new ConfigChanged();
        configChanged.setGroup(groupName);
        configChanged.setProfile(profile);
        configChanged.setKey(key);
        configChanged.setOldValue(oldValue);
        configChanged.setNewValue(value);
        this.eventBus.post(configChanged);
    }

    public void setConfiguration(String groupName, String profile, String key, @NonNull String value) {
        if (value == null) {
            throw new NullPointerException("value is marked non-null but is null");
        }
        if (profile != null) {
            this.setConfiguration(this.rsProfileConfigProfile, groupName, profile, key, value);
        } else {
            this.setConfiguration(this.configProfile, groupName, null, key, value);
        }
    }

    public void setConfiguration(String groupName, String key, String value) {
        this.setConfiguration(this.configProfile, groupName, null, key, value);
    }

    public <T> void setConfiguration(String groupName, String profile, String key, T value) {
        this.setConfiguration(groupName, profile, key, this.objectToString(value));
    }

    public <T> void setConfiguration(String groupName, String key, T value) {
        this.setConfiguration(groupName, null, key, value);
    }

    public <T> void setRSProfileConfiguration(String groupName, String key, T value) {
        String rsProfileKey = this.rsProfileKey;
        if (rsProfileKey == null) {
            if (this.client == null) {
                log.warn("trying to use profile without injected client");
                return;
            }
            String displayName = null;
            Player p = this.client.getLocalPlayer();
            if (p == null) {
                log.warn("trying to create profile without display name");
            } else {
                displayName = p.getName();
            }
            RuneScapeProfile prof = this.findRSProfile(this.getRSProfiles(), this.client.getAccountHash(), RuneScapeProfileType.getCurrent(this.client), displayName, true);
            if (prof == null) {
                log.warn("trying to create a profile while not logged in");
                return;
            }
            rsProfileKey = prof.getKey();
            String previousProfile = this.rsProfileKey;
            this.rsProfileKey = rsProfileKey;
            log.debug("RS profile changed to {}", (Object)rsProfileKey);
            this.eventBus.post(new RuneScapeProfileChanged(previousProfile, rsProfileKey));
        }
        this.setConfiguration(groupName, rsProfileKey, key, value);
    }

    private void unsetConfiguration(ConfigData configData, String groupName, String profile, String key) {
        assert (!key.startsWith("rsprofile."));
        String wholeKey = ConfigManager.getWholeKey(groupName, profile, key);
        String oldValue = configData.unset(wholeKey);
        if (oldValue == null) {
            return;
        }
        log.debug("Unsetting configuration value for {}", (Object)wholeKey);
        this.handler.invalidate();
        ConfigChanged configChanged = new ConfigChanged();
        configChanged.setGroup(groupName);
        configChanged.setProfile(profile);
        configChanged.setKey(key);
        configChanged.setOldValue(oldValue);
        this.eventBus.post(configChanged);
    }

    public void unsetConfiguration(String groupName, String profile, String key) {
        if (profile != null) {
            this.unsetConfiguration(this.rsProfileConfigProfile, groupName, profile, key);
        } else {
            this.unsetConfiguration(this.configProfile, groupName, null, key);
        }
    }

    public void unsetConfiguration(String groupName, String key) {
        this.unsetConfiguration(this.configProfile, groupName, null, key);
    }

    public void unsetRSProfileConfiguration(String groupName, String key) {
        String rsProfileKey = this.rsProfileKey;
        if (rsProfileKey == null) {
            return;
        }
        this.unsetConfiguration(this.rsProfileConfigProfile, groupName, rsProfileKey, key);
    }

    public ConfigDescriptor getConfigDescriptor(Config configurationProxy) {
        Class<?> inter = configurationProxy.getClass().getInterfaces()[0];
        ConfigGroup group = inter.getAnnotation(ConfigGroup.class);
        if (group == null) {
            throw new IllegalArgumentException("Not a config group");
        }
        List<ConfigSectionDescriptor> sections = Arrays.stream(inter.getDeclaredFields()).filter(m -> m.isAnnotationPresent(ConfigSection.class) && m.getType() == String.class).map(m -> {
            try {
                return new ConfigSectionDescriptor(String.valueOf(m.get(inter)), m.getDeclaredAnnotation(ConfigSection.class));
            }
            catch (IllegalAccessException e) {
                log.warn("Unable to load section {}::{}", (Object)inter.getSimpleName(), (Object)m.getName());
                return null;
            }
        }).filter(Objects::nonNull).sorted((a, b) -> ComparisonChain.start().compare(a.getSection().position(), b.getSection().position()).compare((Comparable)((Object)a.getSection().name()), (Comparable)((Object)b.getSection().name())).result()).collect(Collectors.toList());
        List<ConfigItemDescriptor> items = Arrays.stream(inter.getMethods()).filter(m -> m.getParameterCount() == 0 && m.isAnnotationPresent(ConfigItem.class)).map(m -> new ConfigItemDescriptor(m.getDeclaredAnnotation(ConfigItem.class), m.getGenericReturnType(), m.getDeclaredAnnotation(Range.class), m.getDeclaredAnnotation(Alpha.class), m.getDeclaredAnnotation(Units.class))).sorted((a, b) -> ComparisonChain.start().compare(a.getItem().position(), b.getItem().position()).compare((Comparable)((Object)a.getItem().name()), (Comparable)((Object)b.getItem().name())).result()).collect(Collectors.toList());
        return new ConfigDescriptor(group, sections, items);
    }

    public <T extends Config> void setDefaultConfiguration(T proxy, boolean override) {
        Class<?> clazz = proxy.getClass().getInterfaces()[0];
        ConfigGroup group = clazz.getAnnotation(ConfigGroup.class);
        if (group == null) {
            return;
        }
        for (Method method : clazz.getDeclaredMethods()) {
            Object defaultValue;
            String current;
            ConfigItem item = method.getAnnotation(ConfigItem.class);
            if (item == null || method.getParameterCount() != 0) continue;
            if (!method.isDefault()) {
                if (!override || (current = this.getConfiguration(group.value(), item.keyName())) == null) continue;
                this.unsetConfiguration(group.value(), item.keyName());
                continue;
            }
            if (!override && (current = this.getConfiguration(group.value(), item.keyName(), method.getGenericReturnType())) != null) continue;
            try {
                defaultValue = ConfigInvocationHandler.callDefaultMethod(proxy, method, null);
            }
            catch (Throwable ex) {
                log.warn(null, ex);
                continue;
            }
            String current2 = this.getConfiguration(group.value(), item.keyName());
            String valueString = this.objectToString(defaultValue);
            if (Objects.equals(current2, valueString) || Strings.isNullOrEmpty((String)current2) && Strings.isNullOrEmpty((String)valueString)) continue;
            log.debug("Setting default configuration value for {}.{} to {}", new Object[]{group.value(), item.keyName(), defaultValue});
            if (Strings.isNullOrEmpty((String)valueString)) {
                this.unsetConfiguration(group.value(), item.keyName());
                continue;
            }
            this.setConfiguration(group.value(), item.keyName(), valueString);
        }
    }

    Object stringToObject(String str, Type type) {
        Class clazz;
        ConfigSerializer configSerializer;
        ParameterizedType parameterizedType;
        if (type == Boolean.TYPE || type == Boolean.class) {
            return Boolean.parseBoolean(str);
        }
        if (type == Integer.TYPE || type == Integer.class) {
            return Integer.parseInt(str);
        }
        if (type == Long.TYPE || type == Long.class) {
            return Long.parseLong(str);
        }
        if (type == Double.TYPE || type == Double.class) {
            return Double.parseDouble(str);
        }
        if (type == Color.class) {
            return ColorUtil.fromString(str);
        }
        if (type == Dimension.class) {
            String[] splitStr = str.split("x");
            int width = Integer.parseInt(splitStr[0]);
            int height = Integer.parseInt(splitStr[1]);
            return new Dimension(width, height);
        }
        if (type == Point.class) {
            String[] splitStr = str.split(":");
            int width = Integer.parseInt(splitStr[0]);
            int height = Integer.parseInt(splitStr[1]);
            return new Point(width, height);
        }
        if (type == Rectangle.class) {
            String[] splitStr = str.split(":");
            int x = Integer.parseInt(splitStr[0]);
            int y = Integer.parseInt(splitStr[1]);
            int width = Integer.parseInt(splitStr[2]);
            int height = Integer.parseInt(splitStr[3]);
            return new Rectangle(x, y, width, height);
        }
        if (type instanceof Class && ((Class)type).isEnum()) {
            return Enum.valueOf((Class)type, str);
        }
        if (type == Instant.class) {
            return Instant.parse(str);
        }
        if (type == Keybind.class || type == ModifierlessKeybind.class) {
            String[] splitStr = str.split(":");
            int code = Integer.parseInt(splitStr[0]);
            int mods = Integer.parseInt(splitStr[1]);
            if (type == ModifierlessKeybind.class) {
                return new ModifierlessKeybind(code, mods);
            }
            return new Keybind(code, mods);
        }
        if (type == WorldPoint.class) {
            String[] splitStr = str.split(":");
            int x = Integer.parseInt(splitStr[0]);
            int y = Integer.parseInt(splitStr[1]);
            int plane = Integer.parseInt(splitStr[2]);
            return new WorldPoint(x, y, plane);
        }
        if (type == Duration.class) {
            return Duration.ofMillis(Long.parseLong(str));
        }
        if (type == byte[].class) {
            return Base64.getUrlDecoder().decode(str);
        }
        if (type instanceof ParameterizedType && (parameterizedType = (ParameterizedType)type).getRawType() == Set.class) {
            return this.gson.fromJson(str, (Type)parameterizedType);
        }
        if (type instanceof Class && (configSerializer = (clazz = (Class)type).getAnnotation(ConfigSerializer.class)) != null) {
            Class<? extends Serializer<?>> serializerClass = configSerializer.value();
            Serializer serializer = this.serializers.get(type);
            if (serializer == null) {
                serializer = (Serializer)RuneLite.getInjector().createChildInjector(new Module[0]).getInstance(serializerClass);
                this.serializers.put(type, serializer);
            }
            return serializer.deserialize(str);
        }
        return str;
    }

    @Nullable
    String objectToString(Object object) {
        ConfigSerializer configSerializer;
        if (object instanceof Color) {
            return String.valueOf(((Color)object).getRGB());
        }
        if (object instanceof Enum) {
            return ((Enum)object).name();
        }
        if (object instanceof Dimension) {
            Dimension d = (Dimension)object;
            return d.width + "x" + d.height;
        }
        if (object instanceof Point) {
            Point p = (Point)object;
            return p.x + ":" + p.y;
        }
        if (object instanceof Rectangle) {
            Rectangle r = (Rectangle)object;
            return r.x + ":" + r.y + ":" + r.width + ":" + r.height;
        }
        if (object instanceof Instant) {
            return ((Instant)object).toString();
        }
        if (object instanceof Keybind) {
            Keybind k = (Keybind)object;
            return k.getKeyCode() + ":" + k.getModifiers();
        }
        if (object instanceof WorldPoint) {
            WorldPoint wp = (WorldPoint)object;
            return wp.getX() + ":" + wp.getY() + ":" + wp.getPlane();
        }
        if (object instanceof Duration) {
            return Long.toString(((Duration)object).toMillis());
        }
        if (object instanceof byte[]) {
            return Base64.getUrlEncoder().encodeToString((byte[])object);
        }
        if (object instanceof Set) {
            return this.gson.toJson(object, Set.class);
        }
        if (object != null && (configSerializer = object.getClass().getAnnotation(ConfigSerializer.class)) != null) {
            Class<? extends Serializer<?>> serializerClass = configSerializer.value();
            Serializer serializer = this.serializers.get(serializerClass);
            if (serializer == null) {
                serializer = (Serializer)RuneLite.getInjector().createChildInjector(new Module[0]).getInstance(serializerClass);
                this.serializers.put(serializerClass, serializer);
            }
            return serializer.serialize(object);
        }
        return object == null ? null : object.toString();
    }

    @Subscribe(priority=-100.0f)
    private void onClientShutdown(ClientShutdown e) {
        this.sendConfig();
    }

    public void sendConfig() {
        this.eventBus.post(new ConfigSync());
        try (ProfileManager.Lock lock = this.profileManager.lock();){
            this.profile = ConfigManager.updateProfile(lock, this.profile);
            this.rsProfile = ConfigManager.updateProfile(lock, this.rsProfile);
            this.saveConfiguration(lock, this.profile, this.configProfile);
            this.saveConfiguration(lock, this.rsProfile, this.rsProfileConfigProfile);
        }
    }

    private static ConfigProfile updateProfile(ProfileManager.Lock lock, ConfigProfile profile) {
        ConfigProfile p = lock.findProfile(profile.getId());
        if (p == null) {
            log.warn("Lost active profile {}!", (Object)profile.getName());
            p = lock.createProfile(profile.getName(), profile.getId());
            p.setActive(profile.isActive());
        } else if (profile.getRev() != p.getRev()) {
            log.debug("Profile {} changed on disk", (Object)p.getName());
        }
        return p;
    }

    private void saveConfiguration(ProfileManager.Lock lock, ConfigProfile profile, ConfigData data) {
        Map<String, String> patch = data.swapChanges();
        if (patch.isEmpty()) {
            return;
        }
        log.debug("Saving profile {} (patch size: {})", (Object)profile.getName(), (Object)patch.size());
        if (profile.isSync() && this.sessionManager.getAccountSession() != null) {
            try {
                ConfigPatchResult patchResult = this.configClient.patch(ConfigManager.buildConfigPatch(profile.isInternal() ? profile.getName() : null, patch), profile.getId()).get();
                if (patchResult == null) {
                    profile.setRev(-1L);
                } else {
                    long oldRev = patchResult.getRev() - 1L;
                    long newRev = patchResult.getRev();
                    if (oldRev == profile.getRev()) {
                        profile.setRev(newRev);
                        log.debug("incremental patch applied {} -> {}", (Object)oldRev, (Object)newRev);
                    } else {
                        log.debug("rev mismatch {} != {}, invalidating", (Object)oldRev, (Object)newRev);
                        profile.setRev(-1L);
                    }
                }
                lock.dirty();
            }
            catch (InterruptedException | ExecutionException e) {
                profile.setRev(-1L);
                lock.dirty();
                log.error("error applying incremental patch", (Throwable)e);
            }
        }
        data.patch(patch);
    }

    private static ConfigPatch buildConfigPatch(@Nullable String profileName, Map<String, String> patchChanges) {
        ConfigPatch patch = new ConfigPatch();
        patch.setProfileName(profileName);
        for (Map.Entry<String, String> entry : patchChanges.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            if (value == null) {
                patch.getUnset().add(key);
                continue;
            }
            patch.getEdit().put(key, value);
        }
        return patch;
    }

    public List<RuneScapeProfile> getRSProfiles() {
        String prefix = "rsprofile.rsprofile.";
        HashSet<String> profileKeys = new HashSet<String>();
        for (String key2 : this.rsProfileConfigProfile.keySet()) {
            String[] split;
            if (!key2.startsWith(prefix) || (split = ConfigManager.splitKey(key2)) == null) continue;
            profileKeys.add(split[1]);
        }
        return profileKeys.stream().map(key -> {
            Long accid = (Long)this.getConfiguration(RSPROFILE_GROUP, (String)key, RSPROFILE_ACCOUNT_HASH, Long.TYPE);
            RuneScapeProfile prof = new RuneScapeProfile(this.getConfiguration(RSPROFILE_GROUP, (String)key, RSPROFILE_DISPLAY_NAME), (RuneScapeProfileType)((Object)((Object)this.getConfiguration(RSPROFILE_GROUP, (String)key, RSPROFILE_TYPE, (Type)((Object)RuneScapeProfileType.class)))), accid == null ? -1L : accid, (String)key);
            return prof;
        }).sorted(Comparator.comparing(RuneScapeProfile::getKey)).collect(Collectors.toCollection(ArrayList::new));
    }

    private synchronized RuneScapeProfile findRSProfile(List<RuneScapeProfile> profiles, long accountHash, RuneScapeProfileType type, String displayName, boolean create) {
        if (accountHash == -1L) {
            return null;
        }
        List matches = profiles.stream().filter(p -> p.getType() == type && accountHash == p.getAccountHash()).collect(Collectors.toList());
        if (matches.size() > 1) {
            log.warn("multiple matching profiles, choosing {}, ignoring {}", matches.get(0), matches.subList(1, matches.size()));
        }
        if (matches.size() >= 1) {
            return (RuneScapeProfile)matches.get(0);
        }
        if (!create) {
            return null;
        }
        Set keys = profiles.stream().map(RuneScapeProfile::getKey).collect(Collectors.toSet());
        byte[] key = new byte[]{(byte)accountHash, (byte)(accountHash >> 8), (byte)(accountHash >> 16), (byte)(accountHash >> 24), (byte)(accountHash >> 32), (byte)(accountHash >> 40)};
        key[0] = (byte)(key[0] + type.ordinal());
        for (int i = 0; i < 255; ++i) {
            String keyStr = "rsprofile." + Base64.getUrlEncoder().encodeToString(key);
            if (!keys.contains(keyStr)) {
                log.info("creating new profile {} for account hash {} ({})", new Object[]{keyStr, accountHash, type});
                this.setConfiguration(RSPROFILE_GROUP, keyStr, RSPROFILE_ACCOUNT_HASH, accountHash);
                this.setConfiguration(RSPROFILE_GROUP, keyStr, RSPROFILE_TYPE, type);
                if (displayName != null) {
                    this.setConfiguration(RSPROFILE_GROUP, keyStr, RSPROFILE_DISPLAY_NAME, displayName);
                }
                return new RuneScapeProfile(displayName, type, accountHash, keyStr);
            }
            key[1] = (byte)(key[1] + 1);
        }
        throw new RuntimeException("too many rs profiles");
    }

    private void updateRSProfile() {
        String key;
        if (this.client == null) {
            return;
        }
        List<RuneScapeProfile> profiles = this.getRSProfiles();
        RuneScapeProfile prof = this.findRSProfile(profiles, this.client.getAccountHash(), RuneScapeProfileType.getCurrent(this.client), null, false);
        String string = key = prof == null ? null : prof.getKey();
        if (Objects.equals(key, this.rsProfileKey)) {
            return;
        }
        String previousProfile = this.rsProfileKey;
        this.rsProfileKey = key;
        log.debug("RS profile changed to {}", (Object)key);
        this.eventBus.post(new RuneScapeProfileChanged(previousProfile, key));
    }

    @Subscribe
    private void onAccountHashChanged(AccountHashChanged ev) {
        this.updateRSProfile();
    }

    @Subscribe
    private void onWorldChanged(WorldChanged ev) {
        this.updateRSProfile();
    }

    @Subscribe
    private void onPlayerChanged(PlayerChanged ev) {
        if (ev.getPlayer() == this.client.getLocalPlayer()) {
            String name = ev.getPlayer().getName();
            this.setRSProfileConfiguration(RSPROFILE_GROUP, RSPROFILE_DISPLAY_NAME, name);
        }
    }

    @Subscribe
    private void onRuneScapeProfileChanged(RuneScapeProfileChanged ev) {
        try (ProfileManager.Lock lock = this.profileManager.lock();){
            for (ConfigProfile profile : lock.getProfiles()) {
                List<String> get = profile.getDefaultForRsProfiles();
                if (get == null || !get.contains(this.rsProfileKey)) continue;
                lock.getProfiles().forEach(p -> p.setActive(false));
                profile.setActive(true);
                lock.dirty();
                log.debug("Switching to default profile {} for rsprofile {}", (Object)profile.getName(), (Object)this.rsProfileKey);
                this.executor.submit(() -> this.switchProfile(profile));
                break;
            }
        }
    }

    @Nullable
    @VisibleForTesting
    static String[] splitKey(String key) {
        int i = key.indexOf(46);
        if (i == -1) {
            return null;
        }
        String group = key.substring(0, i);
        String profile = null;
        if ((key = key.substring(i + 1)).startsWith("rsprofile.")) {
            i = key.indexOf(46, RSPROFILE_GROUP.length() + 2);
            profile = key.substring(0, i);
            key = key.substring(i + 1);
        }
        return new String[]{group, profile, key};
    }

    public ConfigProfile getProfile() {
        return this.profile;
    }
}

