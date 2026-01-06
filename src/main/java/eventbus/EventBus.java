/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.ImmutableMultimap
 *  com.google.common.collect.ImmutableMultimap$Builder
 *  com.google.common.collect.Iterables
 *  javax.annotation.Nonnull
 *  javax.annotation.concurrent.ThreadSafe
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.slf4j.Marker
 *  org.slf4j.MarkerFactory
 */
package net.runelite.client.eventbus;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Iterables;
import java.lang.invoke.CallSite;
import java.lang.invoke.LambdaMetafactory;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Comparator;
import java.util.function.Consumer;
import javax.annotation.Nonnull;
import javax.annotation.concurrent.ThreadSafe;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.util.ReflectUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

@ThreadSafe
public class EventBus {
    private static final Logger log = LoggerFactory.getLogger(EventBus.class);
    private static final Marker DEDUPLICATE = MarkerFactory.getMarker((String)"DEDUPLICATE");
    private final Consumer<Throwable> exceptionHandler;
    @Nonnull
    private ImmutableMultimap<Class<?>, Subscriber> subscribers = ImmutableMultimap.of();

    public EventBus() {
        this(e -> log.warn(DEDUPLICATE, "Uncaught exception in event subscriber", e));
    }

    public synchronized void register(@Nonnull Object object) {
        ImmutableMultimap.Builder builder = ImmutableMultimap.builder();
        builder.putAll(this.subscribers);
        builder.orderValuesBy(Comparator.comparingDouble(Subscriber::getPriority).reversed().thenComparing(s -> s.object.getClass().getName()));
        for (Class<?> clazz = object.getClass(); clazz != null; clazz = clazz.getSuperclass()) {
            for (Method method : clazz.getDeclaredMethods()) {
                Subscribe sub = method.getAnnotation(Subscribe.class);
                if (sub == null) continue;
                Preconditions.checkArgument((method.getReturnType() == Void.TYPE ? 1 : 0) != 0, (Object)("@Subscribed method \"" + String.valueOf(method) + "\" cannot return a value"));
                Preconditions.checkArgument((method.getParameterCount() == 1 ? 1 : 0) != 0, (Object)("@Subscribed method \"" + String.valueOf(method) + "\" must take exactly 1 argument"));
                Preconditions.checkArgument((!Modifier.isStatic(method.getModifiers()) ? 1 : 0) != 0, (Object)("@Subscribed method \"" + String.valueOf(method) + "\" cannot be static"));
                Class<?> parameterClazz = method.getParameterTypes()[0];
                Preconditions.checkArgument((!parameterClazz.isPrimitive() ? 1 : 0) != 0, (Object)("@Subscribed method \"" + String.valueOf(method) + "\" cannot subscribe to primitives"));
                Preconditions.checkArgument(((parameterClazz.getModifiers() & 0x600) == 0 ? 1 : 0) != 0, (Object)("@Subscribed method \"" + String.valueOf(method) + "\" cannot subscribe to polymorphic classes"));
                for (Class<?> psc = parameterClazz.getSuperclass(); psc != null; psc = psc.getSuperclass()) {
                    if (!this.subscribers.containsKey(psc)) continue;
                    throw new IllegalArgumentException("@Subscribed method \"" + String.valueOf(method) + "\" cannot subscribe to class which inherits from subscribed class \"" + String.valueOf(psc) + "\"");
                }
                String preferredName = "on" + parameterClazz.getSimpleName();
                Preconditions.checkArgument((boolean)method.getName().equals(preferredName), (Object)("Subscribed method " + String.valueOf(method) + " should be named " + preferredName));
                method.setAccessible(true);
                Consumer lambda = null;
                try {
                    MethodHandles.Lookup caller = ReflectUtil.privateLookupIn(clazz);
                    MethodType subscription = MethodType.methodType(Void.TYPE, parameterClazz);
                    MethodHandle target = caller.findVirtual(clazz, method.getName(), subscription);
                    CallSite site = LambdaMetafactory.metafactory(caller, "accept", MethodType.methodType(Consumer.class, clazz), subscription.changeParameterType(0, Object.class), target, subscription);
                    MethodHandle factory = site.getTarget();
                    lambda = factory.bindTo(object).invokeExact();
                }
                catch (Throwable e) {
                    log.warn("Unable to create lambda for method {}", (Object)method, (Object)e);
                }
                Subscriber subscriber = new Subscriber(object, method, sub.priority(), lambda);
                builder.put(parameterClazz, (Object)subscriber);
                log.debug("Registering {} - {}", parameterClazz, (Object)subscriber);
            }
        }
        this.subscribers = builder.build();
    }

    public synchronized <T> Subscriber register(Class<T> clazz, Consumer<T> subFn, float priority) {
        ImmutableMultimap.Builder builder = ImmutableMultimap.builder();
        builder.putAll(this.subscribers);
        builder.orderValuesBy(Comparator.comparingDouble(Subscriber::getPriority).reversed().thenComparing(s -> s.object.getClass().getName()));
        Subscriber sub = new Subscriber(subFn, null, priority, subFn);
        builder.put(clazz, (Object)sub);
        this.subscribers = builder.build();
        return sub;
    }

    public synchronized void unregister(@Nonnull Object object) {
        this.subscribers = ImmutableMultimap.copyOf((Iterable)Iterables.filter((Iterable)this.subscribers.entries(), e -> ((Subscriber)e.getValue()).getObject() != object));
    }

    public synchronized void unregister(Subscriber sub) {
        if (sub == null) {
            return;
        }
        this.subscribers = ImmutableMultimap.copyOf((Iterable)Iterables.filter((Iterable)this.subscribers.entries(), e -> sub != e.getValue()));
    }

    public void post(@Nonnull Object event) {
        for (Subscriber subscriber : this.subscribers.get(event.getClass())) {
            try {
                subscriber.invoke(event);
            }
            catch (Exception e) {
                this.exceptionHandler.accept(e);
            }
        }
    }

    public EventBus(Consumer<Throwable> exceptionHandler) {
        this.exceptionHandler = exceptionHandler;
    }

    public static final class Subscriber {
        private final Object object;
        private final Method method;
        private final float priority;
        private final Consumer<Object> lambda;

        void invoke(Object arg) throws Exception {
            if (this.lambda != null) {
                this.lambda.accept(arg);
            } else {
                this.method.invoke(this.object, arg);
            }
        }

        public Subscriber(Object object, Method method, float priority, Consumer<Object> lambda) {
            this.object = object;
            this.method = method;
            this.priority = priority;
            this.lambda = lambda;
        }

        public Object getObject() {
            return this.object;
        }

        public Method getMethod() {
            return this.method;
        }

        public float getPriority() {
            return this.priority;
        }

        public Consumer<Object> getLambda() {
            return this.lambda;
        }

        public boolean equals(Object o) {
            if (o == this) {
                return true;
            }
            if (!(o instanceof Subscriber)) {
                return false;
            }
            Subscriber other = (Subscriber)o;
            if (Float.compare(this.getPriority(), other.getPriority()) != 0) {
                return false;
            }
            Object this$object = this.getObject();
            Object other$object = other.getObject();
            if (this$object == null ? other$object != null : !this$object.equals(other$object)) {
                return false;
            }
            Method this$method = this.getMethod();
            Method other$method = other.getMethod();
            return !(this$method == null ? other$method != null : !((Object)this$method).equals(other$method));
        }

        public int hashCode() {
            int PRIME = 59;
            int result = 1;
            result = result * 59 + Float.floatToIntBits(this.getPriority());
            Object $object = this.getObject();
            result = result * 59 + ($object == null ? 43 : $object.hashCode());
            Method $method = this.getMethod();
            result = result * 59 + ($method == null ? 43 : ((Object)$method).hashCode());
            return result;
        }

        public String toString() {
            return "EventBus.Subscriber(object=" + String.valueOf(this.getObject()) + ", method=" + String.valueOf(this.getMethod()) + ", priority=" + this.getPriority() + ", lambda=" + String.valueOf(this.getLambda()) + ")";
        }
    }
}

