/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.base.Strings
 *  com.google.common.graph.Graph
 *  com.google.common.graph.GraphBuilder
 *  com.google.common.graph.Graphs
 *  com.google.common.graph.MutableGraph
 *  com.google.common.reflect.ClassPath
 *  com.google.common.reflect.ClassPath$ClassInfo
 *  com.google.inject.CreationException
 *  com.google.inject.Injector
 *  com.google.inject.Key
 *  com.google.inject.Module
 *  javax.inject.Inject
 *  javax.inject.Named
 *  javax.inject.Provider
 *  javax.inject.Singleton
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package net.runelite.client.plugins;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Strings;
import com.google.common.graph.Graph;
import com.google.common.graph.GraphBuilder;
import com.google.common.graph.Graphs;
import com.google.common.graph.MutableGraph;
import com.google.common.reflect.ClassPath;
import com.google.inject.CreationException;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Module;
import java.io.File;
import java.io.IOException;
import java.lang.invoke.CallSite;
import java.lang.invoke.LambdaMetafactory;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;
import javax.swing.SwingUtilities;
import net.runelite.client.RuneLite;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.PluginChanged;
import net.runelite.client.events.ProfileChanged;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginClassLoader;
import net.runelite.client.plugins.PluginDependency;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.PluginInstantiationException;
import net.runelite.client.task.Schedule;
import net.runelite.client.task.ScheduledMethod;
import net.runelite.client.task.Scheduler;
import net.runelite.client.ui.SplashScreen;
import net.runelite.client.util.GameEventManager;
import net.runelite.client.util.ReflectUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class PluginManager {
    private static final Logger log = LoggerFactory.getLogger(PluginManager.class);
    private static final String PLUGIN_PACKAGE = "net.runelite.client.plugins";
    private static final File SIDELOADED_PLUGINS = new File(RuneLite.RUNELITE_DIR, "sideloaded-plugins");
    private final boolean developerMode;
    private final boolean safeMode;
    private final EventBus eventBus;
    private final Scheduler scheduler;
    private final ConfigManager configManager;
    private final Provider<GameEventManager> sceneTileManager;
    private final List<Plugin> plugins = new CopyOnWriteArrayList<Plugin>();
    private final List<Plugin> activePlugins = new CopyOnWriteArrayList<Plugin>();

    @Inject
    @VisibleForTesting
    PluginManager(@Named(value="developerMode") boolean developerMode, @Named(value="safeMode") boolean safeMode, EventBus eventBus, Scheduler scheduler, ConfigManager configManager, Provider<GameEventManager> sceneTileManager) {
        this.developerMode = developerMode;
        this.safeMode = safeMode;
        this.eventBus = eventBus;
        this.scheduler = scheduler;
        this.configManager = configManager;
        this.sceneTileManager = sceneTileManager;
    }

    @Subscribe
    public void onProfileChanged(ProfileChanged profileChanged) {
        this.refreshPlugins();
    }

    private void refreshPlugins() {
        this.loadDefaultPluginConfiguration(null);
        SwingUtilities.invokeLater(() -> {
            for (Plugin plugin : this.getPlugins()) {
                try {
                    if (this.isPluginEnabled(plugin) == this.activePlugins.contains(plugin)) continue;
                    if (this.activePlugins.contains(plugin)) {
                        this.stopPlugin(plugin);
                        continue;
                    }
                    this.startPlugin(plugin);
                }
                catch (PluginInstantiationException e) {
                    log.error("Error during starting/stopping plugin {}", (Object)plugin.getClass().getSimpleName(), (Object)e);
                }
            }
        });
    }

    public Config getPluginConfigProxy(Plugin plugin) {
        try {
            Injector injector = plugin.getInjector();
            for (Key key : injector.getBindings().keySet()) {
                Class type = key.getTypeLiteral().getRawType();
                if (!Config.class.isAssignableFrom(type)) continue;
                return (Config)injector.getInstance(key);
            }
        }
        catch (ThreadDeath e) {
            throw e;
        }
        catch (Throwable e) {
            log.error("Unable to get plugin config", e);
        }
        return null;
    }

    public List<Config> getPluginConfigProxies(Collection<Plugin> plugins) {
        ArrayList<Injector> injectors = new ArrayList<Injector>();
        if (plugins == null) {
            injectors.add(RuneLite.getInjector());
            plugins = this.getPlugins();
        }
        plugins.forEach(pl -> injectors.add(pl.getInjector()));
        ArrayList<Config> list = new ArrayList<Config>();
        for (Injector injector : injectors) {
            for (Key key : injector.getBindings().keySet()) {
                Class type = key.getTypeLiteral().getRawType();
                if (!Config.class.isAssignableFrom(type)) continue;
                Config config = (Config)injector.getInstance(key);
                list.add(config);
            }
        }
        return list;
    }

    public void loadDefaultPluginConfiguration(Collection<Plugin> plugins) {
        try {
            for (Config config : this.getPluginConfigProxies(plugins)) {
                this.configManager.setDefaultConfiguration(config, false);
            }
        }
        catch (ThreadDeath e) {
            throw e;
        }
        catch (Throwable ex) {
            log.error("Unable to reset plugin configuration", ex);
        }
    }

    public void startPlugins() {
        ArrayList<Plugin> scannedPlugins = new ArrayList<Plugin>(this.plugins);
        int loaded = 0;
        for (Plugin plugin : scannedPlugins) {
            try {
                SwingUtilities.invokeAndWait(() -> {
                    try {
                        this.startPlugin(plugin);
                    }
                    catch (PluginInstantiationException ex) {
                        log.error("Unable to start plugin {}", (Object)plugin.getClass().getSimpleName(), (Object)ex);
                        this.plugins.remove(plugin);
                    }
                });
            }
            catch (InterruptedException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
            SplashScreen.stage(0.8, 1.0, null, "Starting plugins", ++loaded, scannedPlugins.size(), false);
        }
        for (Plugin plugin : this.plugins) {
            ReflectUtil.queueInjectorAnnotationCacheInvalidation(plugin.injector);
        }
    }

    public void loadCorePlugins() throws IOException, PluginInstantiationException {
        SplashScreen.stage(0.59, null, "Loading plugins");
        ClassPath classPath = ClassPath.from((ClassLoader)this.getClass().getClassLoader());
        List<Class<?>> plugins = classPath.getTopLevelClassesRecursive(PLUGIN_PACKAGE).stream().map(ClassPath.ClassInfo::load).collect(Collectors.toList());
        this.loadPlugins(plugins, (loaded, total) -> SplashScreen.stage(0.6, 0.7, null, "Loading plugins", loaded, total, false));
    }

    public void loadSideLoadPlugins() {
        if (!this.developerMode) {
            return;
        }
        File[] files = SIDELOADED_PLUGINS.listFiles();
        if (files == null) {
            return;
        }
        for (File f : files) {
            if (!f.getName().endsWith(".jar")) continue;
            log.info("Side-loading plugin {}", (Object)f);
            try {
                PluginClassLoader classLoader = new PluginClassLoader(f, this.getClass().getClassLoader());
                List<Class<?>> plugins = ClassPath.from((ClassLoader)classLoader).getAllClasses().stream().map(ClassPath.ClassInfo::load).collect(Collectors.toList());
                this.loadPlugins(plugins, null);
            }
            catch (IOException | PluginInstantiationException ex) {
                log.error("error sideloading plugin", (Throwable)ex);
            }
        }
    }

    public List<Plugin> loadPlugins(List<Class<?>> plugins, BiConsumer<Integer, Integer> onPluginLoaded) throws PluginInstantiationException {
        MutableGraph graph = GraphBuilder.directed().build();
        for (Class<?> clazz : plugins) {
            PluginDescriptor pluginDescriptor = clazz.getAnnotation(PluginDescriptor.class);
            if (pluginDescriptor == null) {
                if (clazz.getSuperclass() != Plugin.class) continue;
                log.error("Class {} is a plugin, but has no plugin descriptor", clazz);
                continue;
            }
            if (clazz.getSuperclass() != Plugin.class) {
                log.error("Class {} has plugin descriptor, but is not a plugin", clazz);
                continue;
            }
            if (pluginDescriptor.forceDisabled() || pluginDescriptor.developerPlugin() && !this.developerMode) continue;
            if (this.safeMode && !pluginDescriptor.loadInSafeMode()) {
                log.debug("Disabling {} due to safe mode", clazz);
                this.configManager.unsetConfiguration("runelite", (Strings.isNullOrEmpty((String)pluginDescriptor.configName()) ? clazz.getSimpleName() : pluginDescriptor.configName()).toLowerCase());
                continue;
            }
            graph.addNode(clazz);
        }
        for (Class<Object> pluginClazz : graph.nodes()) {
            PluginDependency[] pluginDependencies = (PluginDependency[])pluginClazz.getAnnotationsByType(PluginDependency.class);
            for (PluginDependency pluginDependency : pluginDependencies) {
                if (!graph.nodes().contains(pluginDependency.value())) continue;
                graph.putEdge(pluginDependency.value(), pluginClazz);
            }
        }
        if (Graphs.hasCycle((Graph)graph)) {
            throw new PluginInstantiationException("Plugin dependency graph contains a cycle!");
        }
        List<Class> sortedPlugins = PluginManager.topologicalSort(graph);
        int loaded = 0;
        ArrayList<Plugin> newPlugins = new ArrayList<Plugin>();
        for (Class pluginClazz : sortedPlugins) {
            try {
                Plugin plugin = this.instantiate(this.plugins, pluginClazz);
                newPlugins.add(plugin);
                this.plugins.add(plugin);
            }
            catch (PluginInstantiationException ex) {
                log.error("Error instantiating plugin!", (Throwable)ex);
            }
            ++loaded;
            if (onPluginLoaded == null) continue;
            onPluginLoaded.accept(loaded, sortedPlugins.size());
        }
        return newPlugins;
    }

    public boolean startPlugin(Plugin plugin) throws PluginInstantiationException {
        assert (SwingUtilities.isEventDispatchThread());
        if (this.activePlugins.contains(plugin) || !this.isPluginEnabled(plugin)) {
            return false;
        }
        List<Plugin> conflicts = this.conflictsForPlugin(plugin);
        for (Plugin conflict : conflicts) {
            if (this.isPluginEnabled(conflict)) {
                this.setPluginEnabled(conflict, false);
            }
            if (!this.activePlugins.contains(conflict)) continue;
            this.stopPlugin(conflict);
        }
        this.activePlugins.add(plugin);
        try {
            GameEventManager gameEventManager;
            plugin.startUp();
            log.debug("Plugin {} is now running", (Object)plugin.getClass().getSimpleName());
            if (this.sceneTileManager != null && (gameEventManager = (GameEventManager)this.sceneTileManager.get()) != null) {
                gameEventManager.simulateGameEvents(plugin);
            }
            this.eventBus.register(plugin);
            this.schedule(plugin);
            this.eventBus.post(new PluginChanged(plugin, true));
        }
        catch (ThreadDeath e) {
            throw e;
        }
        catch (Throwable ex) {
            throw new PluginInstantiationException(ex);
        }
        return true;
    }

    public boolean stopPlugin(Plugin plugin) throws PluginInstantiationException {
        assert (SwingUtilities.isEventDispatchThread());
        if (!this.activePlugins.remove(plugin)) {
            return false;
        }
        this.unschedule(plugin);
        this.eventBus.unregister(plugin);
        try {
            plugin.shutDown();
            log.debug("Plugin {} is now stopped", (Object)plugin.getClass().getSimpleName());
            this.eventBus.post(new PluginChanged(plugin, false));
        }
        catch (Exception ex) {
            throw new PluginInstantiationException(ex);
        }
        return true;
    }

    public void setPluginEnabled(Plugin plugin, boolean enabled) {
        PluginDescriptor pluginDescriptor = plugin.getClass().getAnnotation(PluginDescriptor.class);
        String keyName = Strings.isNullOrEmpty((String)pluginDescriptor.configName()) ? plugin.getClass().getSimpleName() : pluginDescriptor.configName();
        this.configManager.setConfiguration("runelite", keyName.toLowerCase(), String.valueOf(enabled));
        if (enabled) {
            List<Plugin> conflicts = this.conflictsForPlugin(plugin);
            for (Plugin conflict : conflicts) {
                if (!this.isPluginEnabled(conflict)) continue;
                this.setPluginEnabled(conflict, false);
            }
        }
    }

    public boolean isPluginEnabled(Plugin plugin) {
        PluginDescriptor pluginDescriptor = plugin.getClass().getAnnotation(PluginDescriptor.class);
        String keyName = Strings.isNullOrEmpty((String)pluginDescriptor.configName()) ? plugin.getClass().getSimpleName() : pluginDescriptor.configName();
        String value = this.configManager.getConfiguration("runelite", keyName.toLowerCase());
        return value != null ? Boolean.parseBoolean(value) : pluginDescriptor.enabledByDefault();
    }

    private Plugin instantiate(List<Plugin> scannedPlugins, Class<Plugin> clazz) throws PluginInstantiationException {
        Plugin plugin;
        PluginDependency[] pluginDependencies = (PluginDependency[])clazz.getAnnotationsByType(PluginDependency.class);
        ArrayList<Plugin> deps = new ArrayList<Plugin>();
        for (PluginDependency pluginDependency : pluginDependencies) {
            Optional<Plugin> dependency = scannedPlugins.stream().filter(p -> p.getClass() == pluginDependency.value()).findFirst();
            if (!dependency.isPresent()) {
                throw new PluginInstantiationException("Unmet dependency for " + clazz.getSimpleName() + ": " + pluginDependency.value().getSimpleName());
            }
            deps.add(dependency.get());
        }
        try {
            plugin = clazz.getDeclaredConstructor(new Class[0]).newInstance(new Object[0]);
        }
        catch (ThreadDeath e) {
            throw e;
        }
        catch (Throwable ex) {
            throw new PluginInstantiationException(ex);
        }
        try {
            Injector pluginInjector;
            Injector parent = RuneLite.getInjector();
            if (deps.size() > 1) {
                ArrayList<Module> modules = new ArrayList<Module>(deps.size());
                for (Plugin p2 : deps) {
                    Module module = binder -> {
                        binder.bind(p2.getClass()).toInstance((Object)p2);
                        binder.install((Module)p2);
                    };
                    modules.add(module);
                }
                parent = parent.createChildInjector(modules);
            } else if (!deps.isEmpty()) {
                parent = ((Plugin)deps.get((int)0)).injector;
            }
            Module pluginModule = binder -> {
                binder.bind(clazz).toInstance((Object)plugin);
                binder.install((Module)plugin);
            };
            plugin.injector = pluginInjector = parent.createChildInjector(new Module[]{pluginModule});
        }
        catch (CreationException ex) {
            throw new PluginInstantiationException(ex);
        }
        log.debug("Loaded plugin {}", (Object)clazz.getSimpleName());
        return plugin;
    }

    public void add(Plugin plugin) {
        this.plugins.add(plugin);
    }

    public void remove(Plugin plugin) {
        this.plugins.remove(plugin);
    }

    public Collection<Plugin> getPlugins() {
        return this.plugins;
    }

    private void schedule(Plugin plugin) {
        for (Method method : plugin.getClass().getMethods()) {
            Schedule schedule = method.getAnnotation(Schedule.class);
            if (schedule == null) continue;
            Runnable runnable = null;
            try {
                Class<?> clazz = method.getDeclaringClass();
                MethodHandles.Lookup caller = ReflectUtil.privateLookupIn(clazz);
                MethodType subscription = MethodType.methodType(method.getReturnType(), method.getParameterTypes());
                MethodHandle target = caller.findVirtual(clazz, method.getName(), subscription);
                CallSite site = LambdaMetafactory.metafactory(caller, "run", MethodType.methodType(Runnable.class, clazz), subscription, target, subscription);
                MethodHandle factory = site.getTarget();
                runnable = factory.bindTo(plugin).invokeExact();
            }
            catch (Throwable e) {
                log.warn("Unable to create lambda for method {}", (Object)method, (Object)e);
            }
            ScheduledMethod scheduledMethod = new ScheduledMethod(schedule, method, plugin, runnable);
            log.debug("Scheduled task {}", (Object)scheduledMethod);
            this.scheduler.addScheduledMethod(scheduledMethod);
        }
    }

    private void unschedule(Plugin plugin) {
        ArrayList<ScheduledMethod> methods = new ArrayList<ScheduledMethod>(this.scheduler.getScheduledMethods());
        for (ScheduledMethod method : methods) {
            if (method.getObject() != plugin) continue;
            log.debug("Removing scheduled task {}", (Object)method);
            this.scheduler.removeScheduledMethod(method);
        }
    }

    @VisibleForTesting
    static <T> List<T> topologicalSort(Graph<T> graph) {
        MutableGraph graphCopy = Graphs.copyOf(graph);
        ArrayList l = new ArrayList();
        Set s = graphCopy.nodes().stream().filter(node -> graphCopy.inDegree(node) == 0).collect(Collectors.toSet());
        while (!s.isEmpty()) {
            Iterator it = s.iterator();
            Object n = it.next();
            it.remove();
            l.add(n);
            for (Object m : new HashSet(graphCopy.successors(n))) {
                graphCopy.removeEdge(n, m);
                if (graphCopy.inDegree(m) != 0) continue;
                s.add(m);
            }
        }
        if (!graphCopy.edges().isEmpty()) {
            throw new RuntimeException("Graph has at least one cycle");
        }
        return l;
    }

    public List<Plugin> conflictsForPlugin(Plugin plugin) {
        PluginDescriptor desc = plugin.getClass().getAnnotation(PluginDescriptor.class);
        HashSet<String> conflicts = new HashSet<String>(Arrays.asList(desc.conflicts()));
        conflicts.add(desc.name());
        return this.plugins.stream().filter(p -> {
            if (p == plugin) {
                return false;
            }
            PluginDescriptor desc = p.getClass().getAnnotation(PluginDescriptor.class);
            if (conflicts.contains(desc.name())) {
                return true;
            }
            for (String conflict : desc.conflicts()) {
                if (!conflicts.contains(conflict)) continue;
                return true;
            }
            return false;
        }).collect(Collectors.toList());
    }
}

