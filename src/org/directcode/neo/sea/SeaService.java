package org.directcode.neo.sea;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class SeaService extends Service {
    private Sea sea;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        sea = new Sea(this);
        SeaUtils.applyModules(sea);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        sea.loadAll();
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        sea.unloadAll();
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
    }
}
