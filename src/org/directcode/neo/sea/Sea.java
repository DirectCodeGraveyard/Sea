package org.directcode.neo.sea;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Sea {
    public static final String TAG = "Sea";
    public static final String PERMISSION_PROVIDE_MODULE = "org.directcode.neo.sea.PROVIDE_MODULE";
    public static final String METADATA_SERVICE_CLASS = "org.directcode.neo.sea.SERVICE_CLASS";

    private final RemoteModuleRegistry rmr;

    private final SeaService service;
    private final List<Module> modules;
    private final Set<Module> loadedModules;
    private final SharedPreferences.OnSharedPreferenceChangeListener rmrChangeListener;
    private final Map<String, RemoteModule> remoteModules = new HashMap<>();

    public Sea(SeaService service) {
        this.service = service;
        this.modules = new ArrayList<>();
        this.loadedModules = new HashSet<>();
        this.rmr = new RemoteModuleRegistry(getContext());
        this.rmrChangeListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                if (key.equals("modules")) {
                    Set<String> m = rmr.getRemoteModules();
                    Set<String> un = new HashSet<>();
                    for (String k : remoteModules.keySet()) {
                        if (!m.contains(k)) {
                            String pkg = k.substring(0, k.indexOf("@") - 1);
                            String clazz = k.substring(pkg.length());
                            unload(remoteModules.get(clazz));
                            un.add(k);
                        }
                    }

                    for (String u : un) {
                        remoteModules.remove(u);
                    }

                    for (String module : m) {
                        if (remoteModules.containsKey(module)) {
                            continue;
                        }

                        int l = module.indexOf("@");
                        String pkg = module.substring(0, l - 1);
                        String clazz = module.substring(pkg.length());
                        RemoteModule rm = new RemoteModule(Sea.this, pkg, clazz);
                        remoteModules.put(module, rm);
                        addModule(rm);
                    }
                }
            }
        };

        rmr.getPrefs().registerOnSharedPreferenceChangeListener(rmrChangeListener);
    }

    /**
     * Load all modules
     */
    public void loadAll() {
        for (Module module : modules) {
            load(module);
        }
    }

    public void load(String name) {
        Module module = getModuleByName(name);
        if (isLoaded(name)) {
            throw new RuntimeException("Module already loaded!");
        } else {
            if (module instanceof LocalModule) {
                ((LocalModule) module).sea = this;
            }
            module.load();
            loadedModules.add(module);

            Intent loadedIntent = new Intent("org.directcode.neo.sea.MODULE_LOADED");
            loadedIntent.putExtra("module", name);
            getContext().sendBroadcast(loadedIntent);
        }
    }

    private void unload(Module module) {
        unload(module.name());
    }

    public void unload(String name) {
        Module module = getModuleByName(name);
        if (!isLoaded(name)) {
            throw new RuntimeException("Module not loaded!");
        } else {
            module.unload();
            loadedModules.remove(module);
            Intent unloadedIntent = new Intent("org.directcode.neo.sea.MODULE_UNLOADED");
            unloadedIntent.putExtra("module", name);
            getContext().sendBroadcast(unloadedIntent);
        }
    }

    private void load(Module module) {
        load(module.name());
    }

    public boolean isLoaded(String name) {
        return loadedModules.contains(getModuleByName(name));
    }

    public Module getModuleByName(String name) {
        for (Module module : modules) {
            if (module.name().equals(name)) {
                return module;
            }
        }

        throw new RuntimeException("No Module Found named '" + name + "'");
    }

    /**
     * Unload all modules
     */
    public void unloadAll() {
        for (Module module : loadedModules) {
            unload(module);
        }
    }

    public void addModule(Module module) {
        modules.add(module);
    }

    public SeaService getSeaService() {
        return service;
    }

    public Object getSystemService(String name) {
        return getSeaService().getSystemService(name);
    }

    public Context getContext() {
        return getSeaService().getApplicationContext();
    }

    public List<String> getLoadedModules() {
        List<String> names = new ArrayList<>();
        for (Module module : loadedModules) {
            names.add(module.name());
        }
        return names;
    }

    public List<String> getModules() {
        List<String> names = new ArrayList<>();
        for (Module module : modules) {
            names.add(module.name());
        }
        return names;
    }
}
