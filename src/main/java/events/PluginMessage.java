/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.NonNull
 */
package net.runelite.client.events;

import java.util.Collections;
import java.util.Map;
import lombok.NonNull;

public final class PluginMessage {
    private final String namespace;
    private final String name;
    private final Map<String, Object> data;

    public PluginMessage(@NonNull String namespace, @NonNull String name) {
        this(namespace, name, Collections.emptyMap());
        if (namespace == null) {
            throw new NullPointerException("namespace is marked non-null but is null");
        }
        if (name == null) {
            throw new NullPointerException("name is marked non-null but is null");
        }
    }

    public PluginMessage(@NonNull String namespace, @NonNull String name, @NonNull Map<String, Object> data) {
        if (namespace == null) {
            throw new NullPointerException("namespace is marked non-null but is null");
        }
        if (name == null) {
            throw new NullPointerException("name is marked non-null but is null");
        }
        if (data == null) {
            throw new NullPointerException("data is marked non-null but is null");
        }
        this.namespace = namespace;
        this.name = name;
        this.data = data;
    }

    public String getNamespace() {
        return this.namespace;
    }

    public String getName() {
        return this.name;
    }

    public Map<String, Object> getData() {
        return this.data;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof PluginMessage)) {
            return false;
        }
        PluginMessage other = (PluginMessage)o;
        String this$namespace = this.getNamespace();
        String other$namespace = other.getNamespace();
        if (this$namespace == null ? other$namespace != null : !this$namespace.equals(other$namespace)) {
            return false;
        }
        String this$name = this.getName();
        String other$name = other.getName();
        if (this$name == null ? other$name != null : !this$name.equals(other$name)) {
            return false;
        }
        Map<String, Object> this$data = this.getData();
        Map<String, Object> other$data = other.getData();
        return !(this$data == null ? other$data != null : !((Object)this$data).equals(other$data));
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        String $namespace = this.getNamespace();
        result = result * 59 + ($namespace == null ? 43 : $namespace.hashCode());
        String $name = this.getName();
        result = result * 59 + ($name == null ? 43 : $name.hashCode());
        Map<String, Object> $data = this.getData();
        result = result * 59 + ($data == null ? 43 : ((Object)$data).hashCode());
        return result;
    }

    public String toString() {
        return "PluginMessage(namespace=" + this.getNamespace() + ", name=" + this.getName() + ", data=" + String.valueOf(this.getData()) + ")";
    }
}

