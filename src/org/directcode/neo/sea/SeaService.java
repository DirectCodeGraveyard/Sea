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
        SeaLog.info("SeaService Created");
        sea = new Sea(this);
        SeaUtils.applyModules(sea);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        SeaLog.info("SeaService Starting");
        sea.loadAll();
        SeaLog.info("SeaService Started");
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        SeaLog.info("SeaService Stopping");
        sea.unloadAll();
        SeaLog.info("SeaService Stopped");
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
    }
}
