package org.neo.sea.core;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashSet;
import java.util.Set;

public class RemoteModuleRegistry {
    private SharedPreferences prefs;

    public RemoteModuleRegistry(Context context) {
        prefs = context.getSharedPreferences("remote_modules", Context.MODE_MULTI_PROCESS);
    }

    public void addRemoteModule(String packageName, String className) {
        Set<String> modules = prefs.getStringSet("modules", new HashSet<String>());
        modules.add(packageName + "@" + className);
        prefs.edit().putStringSet("modules", modules).apply();
    }

    public void removeRemoteModule(String packageName, String className) {
        Set<String> modules = prefs.getStringSet("modules", new HashSet<String>());
        modules.remove(packageName + "@" + className);
        prefs.edit().putStringSet("modules", modules).apply();
    }

    public SharedPreferences getPrefs() {
        return prefs;
    }

    public Set<String> getRemoteModules() {
        return prefs.getStringSet("modules", new HashSet<String>());
    }
}
