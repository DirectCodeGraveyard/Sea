package org.directcode.neo.sea.ui;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import org.directcode.neo.sea.R;
import org.directcode.neo.sea.SeaController;
import org.directcode.neo.sea.core.SeaLog;
import org.directcode.neo.sea.core.SeaService;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class ControllerActivity extends Activity {
    private SeaController controller;
    private ServiceConnection connection;
    private Timer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences prefs = getSharedPreferences(getPackageName(), Context.MODE_PRIVATE);
        if (prefs.getBoolean("firstStart", true)) {
            Intent intent = new Intent(this, FirstRunActivity.class);
            startActivity(intent);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("firstStart", false);
            editor.apply();
        }

        final ProgressDialog loadingDialog = ProgressDialog.show(this, "Connecting...", "Controller is connecting to the Sea Service.");

        connection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                loadingDialog.hide();
                controller = SeaController.Stub.asInterface(iBinder);
                reload();
            }

            @Override
            public void onServiceDisconnected(ComponentName componentName) {
                Dialog dialog = new Dialog(getApplicationContext());

                dialog.setTitle("Disconnected from Sea");
                TextView content = new TextView(getApplicationContext());
                content.setText("Sea Controller was disconnected from the Sea Service!");
                dialog.setContentView(content);
                dialog.setCancelable(false);

                dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialogInterface) {
                        finish();
                    }
                });

                dialog.show();
            }
        };

        bindService(new Intent(getApplicationContext(), SeaService.class), connection, BIND_AUTO_CREATE);

        loadingDialog.show();

        setContentView(R.layout.controller);

        timer = new Timer("Refresher");
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        reload();
                    }
                });
            }
        }, 5000, 5000);
    }

    private void reload() {
        LinearLayout layout = (LinearLayout) findViewById(R.id.controller_layout);
        layout.removeAllViewsInLayout();

        ScrollView view = new ScrollView(this);

        try {
            final List<String> modules = controller.modules();

            for (final String moduleName : modules) {
                boolean loaded = controller.isLoaded(moduleName);

                CheckBox box = new CheckBox(getApplicationContext());

                box.setText(moduleName);

                if (loaded) {
                    box.setChecked(true);
                }

                box.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                        try {
                            if (checked && !controller.isLoaded(moduleName)) {
                                controller.load(moduleName);
                            } else if (!checked && controller.isLoaded(moduleName)) {
                                controller.unload(moduleName);
                            }
                        } catch (RemoteException e) {
                            SeaLog.error("Failed to handle checkbox change!", e);
                        }
                    }
                });

                view.addView(box);
            }

        } catch (Exception e) {
            SeaLog.error("Failed to initialize controller!", e);
        }

        layout.addView(view);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        timer.cancel();
        unbindService(connection);
    }
}
