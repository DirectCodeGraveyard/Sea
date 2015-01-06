package org.directcode.neo.sea;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;

import java.util.List;

public class SeaService extends Service {
    private Sea sea;
    private AutoLoadManager autoLoadManager;

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onCreate() {
        SeaLog.info("Created");
        sea = new Sea(this);
        SeaUtils.applyModules(sea);
        autoLoadManager = new AutoLoadManager(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        SeaLog.info("Starting");
        Intent readyIntent = new Intent("org.directcode.neo.sea.READY");
        sendBroadcast(readyIntent);
        SeaLog.info("Started");
        SeaLog.info("Autoloading Modules");

        autoLoadManager.load(sea);

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        SeaLog.info("Stopping");
        sea.unloadAll();
        SeaLog.info("Stopped");
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
    }

    private final SeaController.Stub binder = new SeaController.Stub() {

        @Override
        public boolean isLoaded(String name) throws RemoteException {
            return sea.isLoaded(name);
        }

        @Override
        public void load(String name) throws RemoteException {
            sea.load(name);
        }

        @Override
        public void unload(String name) throws RemoteException {
            sea.unload(name);
        }

        @Override
        public List<String> loadedModules() throws RemoteException {
            return sea.getLoadedModules();
        }

        @Override
        public List<String> modules() throws RemoteException {
            return sea.getModules();
        }

        @Override
        public boolean isAutoLoaded(String name) throws RemoteException {
            return autoLoadManager.isAutoLoaded(name);
        }

        @Override
        public void setAutoLoaded(String name, boolean autoLoaded) throws RemoteException {
            autoLoadManager.setAutoLoaded(name, autoLoaded);
        }
    };
}
