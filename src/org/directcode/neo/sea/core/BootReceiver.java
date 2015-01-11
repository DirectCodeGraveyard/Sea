package org.directcode.neo.sea.core;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        SeaLog.info("Boot Completed");
        context.startService(new Intent(context, SeaService.class));
    }
}
