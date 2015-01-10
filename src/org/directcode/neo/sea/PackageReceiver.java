package org.directcode.neo.sea;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;

import java.util.Set;

public class PackageReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        RemoteModuleRegistry rmr = new RemoteModuleRegistry(context);
        if (intent.getAction().equals(Intent.ACTION_PACKAGE_ADDED) || intent.getAction().equals(Intent.ACTION_PACKAGE_REPLACED)) {
            int uid = intent.getIntExtra(Intent.EXTRA_UID, -1);
            String name = context.getPackageManager().getNameForUid(uid);

            ApplicationInfo app;
            try {
                app = context.getPackageManager().getApplicationInfo(name, PackageManager.GET_META_DATA);
            } catch (PackageManager.NameNotFoundException e) {
                SeaLog.error("Failed to get application info for package: " + name, e);
                return;
            }

            Bundle meta = app.metaData;

            int hasProvideModulePermission = context.getPackageManager().checkPermission(Sea.PERMISSION_PROVIDE_MODULE, name);

            if (hasProvideModulePermission == PackageManager.PERMISSION_GRANTED) {
                if (meta.containsKey(Sea.METADATA_SERVICE_CLASS)) {
                    String className = meta.getString(Sea.METADATA_SERVICE_CLASS);
                    rmr.addRemoteModule(name, className);
                } else {
                    SeaLog.warn("Package " + name + " has permission to provide a remote module, however it does not provide a service class.");
                }
            }
        } else if (intent.getAction().equals(Intent.ACTION_PACKAGE_REMOVED)) {
            String pkg = intent.getDataString();
            Set<String> m = rmr.getRemoteModules();
            for (String i : m) {
                if (i.startsWith(pkg + "@")) {
                    rmr.removeRemoteModule(pkg, i.substring((pkg + "@").length()));
                }
            }
        }
    }
}
