/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.io.ByteStreams
 *  com.google.inject.Injector
 *  com.google.inject.Key
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package net.runelite.client.util;

import com.google.common.io.ByteStreams;
import com.google.inject.Injector;
import com.google.inject.Key;
import java.io.IOException;
import java.io.InputStream;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Executable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReflectUtil {
    private static final Logger log = LoggerFactory.getLogger(ReflectUtil.class);
    private static Set<Class<?>> annotationClasses = Collections.newSetFromMap(new WeakHashMap());

    public static MethodHandles.Lookup privateLookupIn(Class<?> clazz) {
        try {
            MethodHandles.Lookup caller = clazz.getClassLoader() instanceof PrivateLookupableClassLoader ? ((PrivateLookupableClassLoader)((Object)clazz.getClassLoader())).getLookup() : MethodHandles.lookup();
            return MethodHandles.privateLookupIn(clazz, caller);
        }
        catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static void installLookupHelper(PrivateLookupableClassLoader cl) {
        String name = PrivateLookupHelper.class.getName();
        try (InputStream in = ReflectUtil.class.getResourceAsStream("/" + name.replace('.', '/') + ".class");){
            byte[] classData = ByteStreams.toByteArray((InputStream)in);
            Class<?> clazz = cl.defineClass0(name, classData, 0, classData.length);
            clazz.getConstructor(new Class[0]).newInstance(new Object[0]);
        }
        catch (IOException | ReflectiveOperationException e) {
            throw new RuntimeException("unable to install lookup helper", e);
        }
    }

    public static synchronized void queueInjectorAnnotationCacheInvalidation(Injector injector) {
        if (annotationClasses == null) {
            return;
        }
        for (Key key : injector.getAllBindings().keySet()) {
            for (Class clazz = key.getTypeLiteral().getRawType(); clazz != null; clazz = clazz.getSuperclass()) {
                annotationClasses.add(clazz);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static synchronized void invalidateAnnotationCaches() {
        try {
            for (Class<?> clazz : annotationClasses) {
                for (Method method : clazz.getDeclaredMethods()) {
                    ReflectUtil.uncacheAnnotations(method, Executable.class);
                }
                for (AccessibleObject accessibleObject : clazz.getDeclaredFields()) {
                    ReflectUtil.uncacheAnnotations(accessibleObject, Field.class);
                }
                for (AccessibleObject accessibleObject : clazz.getDeclaredConstructors()) {
                    ReflectUtil.uncacheAnnotations(accessibleObject, Executable.class);
                }
            }
        }
        catch (Exception ex) {
            log.debug(null, (Throwable)ex);
        }
        finally {
            annotationClasses.clear();
            annotationClasses = null;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static void uncacheAnnotations(Object object, Class<?> declaredAnnotationsClazz) throws Exception {
        if (object == null) {
            return;
        }
        Field declaredAnnotations = declaredAnnotationsClazz.getDeclaredField("declaredAnnotations");
        declaredAnnotations.setAccessible(true);
        Object object2 = object;
        synchronized (object2) {
            Map m = (Map)declaredAnnotations.get(object);
            if (m != null && m != Collections.emptyMap()) {
                declaredAnnotations.set(object, null);
            }
        }
        Field rootField = object.getClass().getDeclaredField("root");
        rootField.setAccessible(true);
        Object root = rootField.get(object);
        ReflectUtil.uncacheAnnotations(root, declaredAnnotationsClazz);
    }

    private ReflectUtil() {
    }

    public static class PrivateLookupHelper {
        static {
            PrivateLookupableClassLoader pcl = (PrivateLookupableClassLoader)((Object)PrivateLookupHelper.class.getClassLoader());
            pcl.setLookup(MethodHandles.lookup());
        }
    }

    public static interface PrivateLookupableClassLoader {
        public Class<?> defineClass0(String var1, byte[] var2, int var3, int var4) throws ClassFormatError;

        public MethodHandles.Lookup getLookup();

        public void setLookup(MethodHandles.Lookup var1);
    }
}

