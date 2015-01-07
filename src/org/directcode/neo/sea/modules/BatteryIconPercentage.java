package org.directcode.neo.sea.modules;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;

import com.android.systemui.SystemUIProxy;

import org.directcode.neo.sea.Sea;
import org.directcode.neo.sea.SeaLog;
import org.directcode.neo.sea.SeaModule;

public class BatteryIconPercentage extends SeaModule {
    private SystemUIProxy proxy;
    private Sea sea;
    private MyServiceConnection connection;
    private boolean unloading = false;

    @Override
    public String name() {
        return "Battery Icon Percentage";
    }

    @Override
    public void load(Sea sea) {
        this.sea = sea;
        connect();
    }

    private void connect() {
        connection = new MyServiceConnection();
        sea.getContext().bindService(new Intent(SystemUIProxy.class.getName()), connection, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void unload(Sea sea) {
        unloading = true;
        sea.getContext().unbindService(connection);
    }

    private class MyServiceConnection implements ServiceConnection {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            proxy = SystemUIProxy.Stub.asInterface(service);
            SeaLog.info(name() + " has connected to the SystemUI Proxy");
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            SeaLog.info(name() + " has disconnected from the SystemUI Proxy");
            if (!unloading) {
                connect();
            }
        }
    }
}
