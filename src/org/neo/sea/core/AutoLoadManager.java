package org.neo.sea.core;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.HashSet;
import java.util.Set;

public class AutoLoadManager {
    private SharedPreferences preferences;

    public AutoLoadManager(Context context) {
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public boolean isAutoLoaded(String name) {
        Set<String> autoLoadedModules = preferences.getStringSet("modules.autoloaded", new HashSet<String>());
        return autoLoadedModules.contains(name);
    }

    public void setAutoLoaded(String name, boolean autoLoaded) {
        Set<String> autoLoadedModules = preferences.getStringSet("modules.autoloaded", new HashSet<String>());
        if (autoLoaded) {
            autoLoadedModules.add(name);
        } else {
            autoLoadedModules.remove(name);
        }
        preferences.edit().putStringSet("modules.autoloaded", autoLoadedModules).apply();
    }

    public void load(Sea sea) {
        Set<String> autoLoadedModules = preferences.getStringSet("modules.autoloaded", new HashSet<String>());

        for (String name : autoLoadedModules) {
            if (!sea.isLoaded(name)) {
                sea.load(name);
            }
        }
    }
}
