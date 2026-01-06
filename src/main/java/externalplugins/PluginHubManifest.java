/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.hash.HashCode
 *  com.google.common.hash.Hashing
 *  com.google.common.io.Files
 *  javax.annotation.Nullable
 */
package net.runelite.client.externalplugins;

import com.google.common.hash.HashCode;
import com.google.common.hash.Hashing;
import com.google.common.io.Files;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import javax.annotation.Nullable;
import net.runelite.client.RuneLite;

public class PluginHubManifest {
    public static final Base64.Encoder HASH_ENCODER = Base64.getUrlEncoder().withoutPadding();

    public static class Stub
    extends DisplayData {
        private String[] plugins;

        public String[] getPlugins() {
            return this.plugins;
        }

        public void setPlugins(String[] plugins) {
            this.plugins = plugins;
        }

        @Override
        public String toString() {
            return "PluginHubManifest.Stub(plugins=" + Arrays.deepToString(this.getPlugins()) + ")";
        }

        @Override
        public boolean equals(Object o) {
            if (o == this) {
                return true;
            }
            if (!(o instanceof Stub)) {
                return false;
            }
            Stub other = (Stub)o;
            if (!other.canEqual(this)) {
                return false;
            }
            if (!super.equals(o)) {
                return false;
            }
            return Arrays.deepEquals(this.getPlugins(), other.getPlugins());
        }

        @Override
        protected boolean canEqual(Object other) {
            return other instanceof Stub;
        }

        @Override
        public int hashCode() {
            int PRIME = 59;
            int result = super.hashCode();
            result = result * 59 + Arrays.deepHashCode(this.getPlugins());
            return result;
        }
    }

    public static class DisplayData {
        private String internalName;
        private String displayName;
        private String version;
        @Nullable
        private String iconHash;
        private long createdAt;
        private long lastUpdatedAt;
        private String author;
        @Nullable
        private String description;
        @Nullable
        private String warning;
        @Nullable
        private String[] tags;
        @Nullable
        private String unavailableReason;

        public boolean hasIcon() {
            return this.iconHash != null;
        }

        public String getInternalName() {
            return this.internalName;
        }

        public String getDisplayName() {
            return this.displayName;
        }

        public String getVersion() {
            return this.version;
        }

        @Nullable
        public String getIconHash() {
            return this.iconHash;
        }

        public long getCreatedAt() {
            return this.createdAt;
        }

        public long getLastUpdatedAt() {
            return this.lastUpdatedAt;
        }

        public String getAuthor() {
            return this.author;
        }

        @Nullable
        public String getDescription() {
            return this.description;
        }

        @Nullable
        public String getWarning() {
            return this.warning;
        }

        @Nullable
        public String[] getTags() {
            return this.tags;
        }

        @Nullable
        public String getUnavailableReason() {
            return this.unavailableReason;
        }

        public void setInternalName(String internalName) {
            this.internalName = internalName;
        }

        public void setDisplayName(String displayName) {
            this.displayName = displayName;
        }

        public void setVersion(String version) {
            this.version = version;
        }

        public void setIconHash(@Nullable String iconHash) {
            this.iconHash = iconHash;
        }

        public void setCreatedAt(long createdAt) {
            this.createdAt = createdAt;
        }

        public void setLastUpdatedAt(long lastUpdatedAt) {
            this.lastUpdatedAt = lastUpdatedAt;
        }

        public void setAuthor(String author) {
            this.author = author;
        }

        public void setDescription(@Nullable String description) {
            this.description = description;
        }

        public void setWarning(@Nullable String warning) {
            this.warning = warning;
        }

        public void setTags(@Nullable String[] tags) {
            this.tags = tags;
        }

        public void setUnavailableReason(@Nullable String unavailableReason) {
            this.unavailableReason = unavailableReason;
        }

        public boolean equals(Object o) {
            if (o == this) {
                return true;
            }
            if (!(o instanceof DisplayData)) {
                return false;
            }
            DisplayData other = (DisplayData)o;
            if (!other.canEqual(this)) {
                return false;
            }
            if (this.getCreatedAt() != other.getCreatedAt()) {
                return false;
            }
            if (this.getLastUpdatedAt() != other.getLastUpdatedAt()) {
                return false;
            }
            String this$internalName = this.getInternalName();
            String other$internalName = other.getInternalName();
            if (this$internalName == null ? other$internalName != null : !this$internalName.equals(other$internalName)) {
                return false;
            }
            String this$displayName = this.getDisplayName();
            String other$displayName = other.getDisplayName();
            if (this$displayName == null ? other$displayName != null : !this$displayName.equals(other$displayName)) {
                return false;
            }
            String this$version = this.getVersion();
            String other$version = other.getVersion();
            if (this$version == null ? other$version != null : !this$version.equals(other$version)) {
                return false;
            }
            String this$iconHash = this.getIconHash();
            String other$iconHash = other.getIconHash();
            if (this$iconHash == null ? other$iconHash != null : !this$iconHash.equals(other$iconHash)) {
                return false;
            }
            String this$author = this.getAuthor();
            String other$author = other.getAuthor();
            if (this$author == null ? other$author != null : !this$author.equals(other$author)) {
                return false;
            }
            String this$description = this.getDescription();
            String other$description = other.getDescription();
            if (this$description == null ? other$description != null : !this$description.equals(other$description)) {
                return false;
            }
            String this$warning = this.getWarning();
            String other$warning = other.getWarning();
            if (this$warning == null ? other$warning != null : !this$warning.equals(other$warning)) {
                return false;
            }
            if (!Arrays.deepEquals(this.getTags(), other.getTags())) {
                return false;
            }
            String this$unavailableReason = this.getUnavailableReason();
            String other$unavailableReason = other.getUnavailableReason();
            return !(this$unavailableReason == null ? other$unavailableReason != null : !this$unavailableReason.equals(other$unavailableReason));
        }

        protected boolean canEqual(Object other) {
            return other instanceof DisplayData;
        }

        public int hashCode() {
            int PRIME = 59;
            int result = 1;
            long $createdAt = this.getCreatedAt();
            result = result * 59 + (int)($createdAt >>> 32 ^ $createdAt);
            long $lastUpdatedAt = this.getLastUpdatedAt();
            result = result * 59 + (int)($lastUpdatedAt >>> 32 ^ $lastUpdatedAt);
            String $internalName = this.getInternalName();
            result = result * 59 + ($internalName == null ? 43 : $internalName.hashCode());
            String $displayName = this.getDisplayName();
            result = result * 59 + ($displayName == null ? 43 : $displayName.hashCode());
            String $version = this.getVersion();
            result = result * 59 + ($version == null ? 43 : $version.hashCode());
            String $iconHash = this.getIconHash();
            result = result * 59 + ($iconHash == null ? 43 : $iconHash.hashCode());
            String $author = this.getAuthor();
            result = result * 59 + ($author == null ? 43 : $author.hashCode());
            String $description = this.getDescription();
            result = result * 59 + ($description == null ? 43 : $description.hashCode());
            String $warning = this.getWarning();
            result = result * 59 + ($warning == null ? 43 : $warning.hashCode());
            result = result * 59 + Arrays.deepHashCode(this.getTags());
            String $unavailableReason = this.getUnavailableReason();
            result = result * 59 + ($unavailableReason == null ? 43 : $unavailableReason.hashCode());
            return result;
        }

        public String toString() {
            return "PluginHubManifest.DisplayData(internalName=" + this.getInternalName() + ", displayName=" + this.getDisplayName() + ", version=" + this.getVersion() + ", iconHash=" + this.getIconHash() + ", createdAt=" + this.getCreatedAt() + ", lastUpdatedAt=" + this.getLastUpdatedAt() + ", author=" + this.getAuthor() + ", description=" + this.getDescription() + ", warning=" + this.getWarning() + ", tags=" + Arrays.deepToString(this.getTags()) + ", unavailableReason=" + this.getUnavailableReason() + ")";
        }
    }

    public static class ManifestFull
    extends ManifestLite {
        private List<DisplayData> display;

        public List<DisplayData> getDisplay() {
            return this.display;
        }

        public void setDisplay(List<DisplayData> display) {
            this.display = display;
        }

        @Override
        public String toString() {
            return "PluginHubManifest.ManifestFull(display=" + String.valueOf(this.getDisplay()) + ")";
        }

        @Override
        public boolean equals(Object o) {
            if (o == this) {
                return true;
            }
            if (!(o instanceof ManifestFull)) {
                return false;
            }
            ManifestFull other = (ManifestFull)o;
            if (!other.canEqual(this)) {
                return false;
            }
            if (!super.equals(o)) {
                return false;
            }
            List<DisplayData> this$display = this.getDisplay();
            List<DisplayData> other$display = other.getDisplay();
            return !(this$display == null ? other$display != null : !((Object)this$display).equals(other$display));
        }

        @Override
        protected boolean canEqual(Object other) {
            return other instanceof ManifestFull;
        }

        @Override
        public int hashCode() {
            int PRIME = 59;
            int result = super.hashCode();
            List<DisplayData> $display = this.getDisplay();
            result = result * 59 + ($display == null ? 43 : ((Object)$display).hashCode());
            return result;
        }
    }

    public static class ManifestLite {
        private List<JarData> jars;

        public List<JarData> getJars() {
            return this.jars;
        }

        public void setJars(List<JarData> jars) {
            this.jars = jars;
        }

        public boolean equals(Object o) {
            if (o == this) {
                return true;
            }
            if (!(o instanceof ManifestLite)) {
                return false;
            }
            ManifestLite other = (ManifestLite)o;
            if (!other.canEqual(this)) {
                return false;
            }
            List<JarData> this$jars = this.getJars();
            List<JarData> other$jars = other.getJars();
            return !(this$jars == null ? other$jars != null : !((Object)this$jars).equals(other$jars));
        }

        protected boolean canEqual(Object other) {
            return other instanceof ManifestLite;
        }

        public int hashCode() {
            int PRIME = 59;
            int result = 1;
            List<JarData> $jars = this.getJars();
            result = result * 59 + ($jars == null ? 43 : ((Object)$jars).hashCode());
            return result;
        }

        public String toString() {
            return "PluginHubManifest.ManifestLite(jars=" + String.valueOf(this.getJars()) + ")";
        }
    }

    public static class JarData {
        private String internalName;
        private String displayName;
        private String jarHash;
        private int jarSize;

        File getJarFile() {
            return new File(RuneLite.PLUGINS_DIR, this.internalName + "_" + this.jarHash + ".jar");
        }

        boolean isValid() {
            File file = this.getJarFile();
            try {
                HashCode hash;
                if (file.exists() && this.jarHash.equals(HASH_ENCODER.encodeToString((hash = Files.asByteSource((File)file).hash(Hashing.sha256())).asBytes()))) {
                    return true;
                }
            }
            catch (IOException iOException) {
                // empty catch block
            }
            return false;
        }

        public String getInternalName() {
            return this.internalName;
        }

        public String getDisplayName() {
            return this.displayName;
        }

        public String getJarHash() {
            return this.jarHash;
        }

        public int getJarSize() {
            return this.jarSize;
        }

        public void setInternalName(String internalName) {
            this.internalName = internalName;
        }

        public void setDisplayName(String displayName) {
            this.displayName = displayName;
        }

        public void setJarHash(String jarHash) {
            this.jarHash = jarHash;
        }

        public void setJarSize(int jarSize) {
            this.jarSize = jarSize;
        }

        public boolean equals(Object o) {
            if (o == this) {
                return true;
            }
            if (!(o instanceof JarData)) {
                return false;
            }
            JarData other = (JarData)o;
            if (!other.canEqual(this)) {
                return false;
            }
            if (this.getJarSize() != other.getJarSize()) {
                return false;
            }
            String this$internalName = this.getInternalName();
            String other$internalName = other.getInternalName();
            if (this$internalName == null ? other$internalName != null : !this$internalName.equals(other$internalName)) {
                return false;
            }
            String this$displayName = this.getDisplayName();
            String other$displayName = other.getDisplayName();
            if (this$displayName == null ? other$displayName != null : !this$displayName.equals(other$displayName)) {
                return false;
            }
            String this$jarHash = this.getJarHash();
            String other$jarHash = other.getJarHash();
            return !(this$jarHash == null ? other$jarHash != null : !this$jarHash.equals(other$jarHash));
        }

        protected boolean canEqual(Object other) {
            return other instanceof JarData;
        }

        public int hashCode() {
            int PRIME = 59;
            int result = 1;
            result = result * 59 + this.getJarSize();
            String $internalName = this.getInternalName();
            result = result * 59 + ($internalName == null ? 43 : $internalName.hashCode());
            String $displayName = this.getDisplayName();
            result = result * 59 + ($displayName == null ? 43 : $displayName.hashCode());
            String $jarHash = this.getJarHash();
            result = result * 59 + ($jarHash == null ? 43 : $jarHash.hashCode());
            return result;
        }

        public String toString() {
            return "PluginHubManifest.JarData(internalName=" + this.getInternalName() + ", displayName=" + this.getDisplayName() + ", jarHash=" + this.getJarHash() + ", jarSize=" + this.getJarSize() + ")";
        }
    }
}

