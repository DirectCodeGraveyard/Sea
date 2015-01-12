package org.neo.sea.core;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;

import java.util.ArrayList;
import java.util.List;

public class RemoteModule extends Module {
    private Sea sea;
    private IRemoteModule module;
    private ServiceConnection connection;
    private String pkg;
    private List<Runnable> actionQueue = new ArrayList<>();
    private String clazz;
    private boolean connected = false;

    public RemoteModule(Sea sea, String pkg, String clazz) {
        this.sea = sea;
        this.pkg = pkg;
        this.clazz = clazz;
        this.connection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                module = IRemoteModule.Stub.asInterface(service);
                connected = true;

                for (Runnable runnable : actionQueue) {
                    runnable.run();
                }

                actionQueue.clear();
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                connected = false;
                module = null;
                connect();
            }
        };

        connect();
    }

    public void connect() {
        sea.getContext().bindService(new Intent(clazz), connection, Service.BIND_AUTO_CREATE);
    }

    @Override
    public String name() {
        try {
            return module.name();
        } catch (RemoteException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void load() {
        if (module == null || !connected) {
            actionQueue.add(new Runnable() {
                @Override
                public void run() {
                    try {
                        module.load();
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            });
        } else {
            try {
                module.load();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void unload() {

        if (module == null || !connected) {
            actionQueue.add(new Runnable() {
                @Override
                public void run() {
                    try {
                        module.unload();
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            });
        } else {
            try {
                module.unload();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }
}
