package org.directcode.neo.sea;

import android.app.Service;
import android.content.Context;
import android.hardware.display.DisplayManager;
import android.os.Handler;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Sea {
    private static final String TAG = "Sea";

    private final SeaService service;
    private final List<SeaModule> modules;
    private final Set<SeaModule> loadedModules;

    public Sea(SeaService service) {
        this.service = service;
        this.modules = new ArrayList<>();
        this.loadedModules = new HashSet<>();
    }

    /**
     * Load all modules
     */
    public void loadAll() {
        for (SeaModule module : modules) {
            load(module);
        }
    }

    public void load(String name) {
        SeaModule module = getModuleByName(name);
        if (isLoaded(name)) {
            throw new RuntimeException("Module already loaded!");
        } else {
            module.load(this);
            loadedModules.add(module);
        }
    }

    private void unload(SeaModule module) {
        unload(module.name());
    }

    public void unload(String name) {
        SeaModule module = getModuleByName(name);
        if (!isLoaded(name)) {
            throw new RuntimeException("Module not loaded!");
        } else {
            module.unload(this);
            loadedModules.remove(module);
        }
    }

    private void load(SeaModule module) {
        load(module.name());
    }

    public boolean isLoaded(String name) {
        return loadedModules.contains(getModuleByName(name));
    }

    public SeaModule getModuleByName(String name) {
        for (SeaModule module : modules) {
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
        for (SeaModule module : loadedModules) {
            unload(module);
        }
    }

    public void addModule(SeaModule module) {
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
}
