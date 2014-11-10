package org.directcode.neo.sea;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

public class ControllerActivity extends Activity {
    private SeaController controller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final ProgressDialog loadingDialog = ProgressDialog.show(this, "Connecting...", "Controller is connecting to the Sea Service.");

        bindService(new Intent(getApplicationContext(), SeaService.class), new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                loadingDialog.hide();
                controller = SeaController.Stub.asInterface(iBinder);
                init();
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
        }, BIND_AUTO_CREATE);

        loadingDialog.show();

        setContentView(R.layout.controller);
    }

    private void init() {
        ListView listView = (ListView) findViewById(R.id.moduleCheckboxes);

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

                listView.addView(box);
            }

        } catch (Exception e) {
            SeaLog.error("Failed to initialize controller!", e);
        }
    }
}
