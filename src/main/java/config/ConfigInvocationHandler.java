/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.cache.Cache
 *  com.google.common.cache.CacheBuilder
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package net.runelite.client.config;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Objects;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.util.ReflectUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class ConfigInvocationHandler
implements InvocationHandler {
    private static final Logger log = LoggerFactory.getLogger(ConfigInvocationHandler.class);
    private static final Object NULL = new Object();
    private final ConfigManager manager;
    private final Cache<Method, Object> cache = CacheBuilder.newBuilder().maximumSize(256L).build();

    ConfigInvocationHandler(ConfigManager manager) {
        this.manager = manager;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Object defaultValue;
        Object cachedValue;
        if (args == null && (cachedValue = this.cache.getIfPresent((Object)method)) != null) {
            return cachedValue == NULL ? null : cachedValue;
        }
        Class<?> iface = proxy.getClass().getInterfaces()[0];
        if ("toString".equals(method.getName()) && args == null) {
            return iface.getSimpleName();
        }
        if ("hashCode".equals(method.getName()) && args == null) {
            return System.identityHashCode(proxy);
        }
        if ("equals".equals(method.getName()) && args != null && args.length == 1) {
            return proxy == args[0];
        }
        ConfigGroup group = iface.getAnnotation(ConfigGroup.class);
        ConfigItem item = method.getAnnotation(ConfigItem.class);
        if (group == null) {
            log.warn("Configuration proxy class {} has no @ConfigGroup!", proxy.getClass());
            return null;
        }
        if (item == null) {
            log.warn("Configuration method {} has no @ConfigItem!", (Object)method);
            return null;
        }
        if (args == null) {
            log.trace("cache miss (size: {}, group: {}, key: {})", new Object[]{this.cache.size(), group.value(), item.keyName()});
            String value = this.manager.getConfiguration(group.value(), item.keyName());
            if (value == null) {
                if (method.isDefault()) {
                    Object defaultValue2 = ConfigInvocationHandler.callDefaultMethod(proxy, method, null);
                    this.cache.put((Object)method, defaultValue2 == null ? NULL : defaultValue2);
                    return defaultValue2;
                }
                this.cache.put((Object)method, NULL);
                return null;
            }
            try {
                Object objectValue = this.manager.stringToObject(value, method.getGenericReturnType());
                this.cache.put((Object)method, objectValue == null ? NULL : objectValue);
                return objectValue;
            }
            catch (Exception e) {
                log.warn("Unable to unmarshal {}.{} ", new Object[]{group.value(), item.keyName(), e});
                if (method.isDefault()) {
                    return ConfigInvocationHandler.callDefaultMethod(proxy, method, null);
                }
                return null;
            }
        }
        if (args.length != 1) {
            throw new RuntimeException("Invalid number of arguments to configuration method");
        }
        Object newValue = args[0];
        Class<?> type = method.getParameterTypes()[0];
        Object oldValue = this.manager.getConfiguration(group.value(), item.keyName(), type);
        if (Objects.equals(oldValue, newValue)) {
            return null;
        }
        if (method.isDefault() && Objects.equals(newValue, defaultValue = ConfigInvocationHandler.callDefaultMethod(proxy, method, args))) {
            this.manager.unsetConfiguration(group.value(), item.keyName());
            return null;
        }
        if (newValue == null) {
            this.manager.unsetConfiguration(group.value(), item.keyName());
        } else {
            String newValueStr = this.manager.objectToString(newValue);
            this.manager.setConfiguration(group.value(), item.keyName(), newValueStr);
        }
        return null;
    }

    static Object callDefaultMethod(Object proxy, Method method, Object[] args) throws Throwable {
        Class<?> declaringClass = method.getDeclaringClass();
        return ReflectUtil.privateLookupIn(declaringClass).unreflectSpecial(method, declaringClass).bindTo(proxy).invokeWithArguments(args);
    }

    void invalidate() {
        log.trace("cache invalidate");
        this.cache.invalidateAll();
    }
}

