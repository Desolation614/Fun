/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.Gson
 *  com.google.gson.JsonSyntaxException
 *  javax.inject.Inject
 *  javax.inject.Singleton
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package net.runelite.client.config;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import javax.inject.Inject;
import javax.inject.Singleton;
import net.runelite.client.RuneLite;
import net.runelite.client.config.ConfigProfile;
import net.runelite.client.config.Profiles;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class ProfileManager {
    private static final Logger log = LoggerFactory.getLogger(ProfileManager.class);
    private static final File PROFILES_DIR = new File(RuneLite.RUNELITE_DIR, "profiles2");
    private static final File PROFILES = new File(PROFILES_DIR, "profiles.json");
    private final Gson gson;

    public Lock lock() {
        return new Lock();
    }

    public static File profileConfigFile(ConfigProfile profile) {
        return new File(PROFILES_DIR, profile.getName() + "-" + profile.getId() + ".properties");
    }

    @Inject
    public ProfileManager(Gson gson) {
        this.gson = gson;
    }

    static {
        PROFILES_DIR.mkdirs();
    }

    public class Lock
    implements AutoCloseable {
        private final File lockFile = new File(PROFILES_DIR, "profiles.lck");
        private final FileOutputStream lockOut = new FileOutputStream(this.lockFile);
        private final FileChannel lockChannel = this.lockOut.getChannel();
        private final List<ConfigProfile> profiles;
        private boolean modified = false;

        public Lock() {
            this.lockChannel.lock();
            this.profiles = new ArrayList<ConfigProfile>(this.load());
        }

        private List<ConfigProfile> load() {
            List<ConfigProfile> list;
            FileInputStream in = new FileInputStream(PROFILES);
            try {
                list = ((Profiles)ProfileManager.this.gson.fromJson((Reader)new InputStreamReader((InputStream)in, StandardCharsets.UTF_8), Profiles.class)).getProfiles();
            }
            catch (Throwable throwable) {
                try {
                    try {
                        in.close();
                    }
                    catch (Throwable throwable2) {
                        throwable.addSuppressed(throwable2);
                    }
                    throw throwable;
                }
                catch (FileNotFoundException ex) {
                    return Collections.emptyList();
                }
                catch (JsonSyntaxException | IOException e) {
                    log.error("unable to read profiles", e);
                    return Collections.emptyList();
                }
            }
            in.close();
            return list;
        }

        @Override
        public void close() {
            if (this.modified) {
                log.debug("saving {} profiles", (Object)this.profiles.size());
                File tempFile = File.createTempFile("runelite_profiles", null, PROFILES_DIR);
                try (FileOutputStream out = new FileOutputStream(tempFile);
                     FileChannel channel = out.getChannel();
                     OutputStreamWriter writer = new OutputStreamWriter((OutputStream)out, StandardCharsets.UTF_8);){
                    Profiles profilesData = new Profiles();
                    profilesData.setProfiles(this.profiles);
                    ProfileManager.this.gson.toJson((Object)profilesData, (Appendable)writer);
                    writer.flush();
                    channel.force(true);
                }
                try {
                    Files.move(tempFile.toPath(), PROFILES.toPath(), StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
                }
                catch (AtomicMoveNotSupportedException ex) {
                    log.debug("atomic move not supported", (Throwable)ex);
                    Files.move(tempFile.toPath(), PROFILES.toPath(), StandardCopyOption.REPLACE_EXISTING);
                }
            }
            this.lockOut.close();
            this.lockFile.delete();
        }

        public List<ConfigProfile> getProfiles() {
            return this.profiles;
        }

        public ConfigProfile createProfile(String name, long id) {
            if (this.findProfile(id) != null) {
                throw new IllegalArgumentException("profile " + id + " already exists");
            }
            ConfigProfile profile = new ConfigProfile(id);
            profile.setName(name);
            profile.setSync(false);
            profile.setRev(-1L);
            profile.setDefaultForRsProfiles(new ArrayList<String>());
            this.profiles.add(profile);
            this.modified = true;
            log.debug("Created profile {}", (Object)profile);
            return profile;
        }

        public ConfigProfile createProfile(String name) {
            return this.createProfile(name, System.nanoTime());
        }

        public ConfigProfile findProfile(String name) {
            return this.findProfile((ConfigProfile profile) -> profile.getName().equals(name));
        }

        public ConfigProfile findProfile(long id) {
            return this.findProfile((ConfigProfile profile) -> profile.getId() == id);
        }

        public ConfigProfile findProfile(Predicate<ConfigProfile> condition) {
            for (ConfigProfile configProfile : this.profiles) {
                if (!condition.test(configProfile)) continue;
                return configProfile;
            }
            return null;
        }

        public void removeProfile(long id) {
            this.modified |= this.profiles.removeIf(p -> p.getId() == id);
        }

        public void renameProfile(ConfigProfile profile, String name) {
            File oldFile = ProfileManager.profileConfigFile(profile);
            profile.setName(name);
            this.modified = true;
            File newFile = ProfileManager.profileConfigFile(profile);
            if (!oldFile.exists()) {
                log.info("Old profile file {} does not exist", (Object)oldFile.getName());
                return;
            }
            try {
                Files.move(oldFile.toPath(), newFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                log.info("Renamed profile file {} to {}", (Object)oldFile.getName(), (Object)newFile.getName());
            }
            catch (IOException e) {
                log.error("error renaming profile", (Throwable)e);
            }
        }

        public void dirty() {
            this.modified = true;
        }
    }
}

