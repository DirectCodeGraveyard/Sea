package org.neo.sea.ui;

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
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.neo.sea.R;
import org.neo.sea.core.SeaController;
import org.neo.sea.core.SeaService;

public class ModuleToggleActivity extends Activity {
    private SeaController controller;
    private ServiceConnection connection;
    private RecyclerView recyclerView;

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

        final ProgressDialog loadingDialog = ProgressDialog.show(this, "Connecting...", "Module Manager is connecting to Sea.");

        connection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                loadingDialog.hide();
                controller = SeaController.Stub.asInterface(iBinder);
                recyclerView.setAdapter(new ModuleAdapter());
            }

            @Override
            public void onServiceDisconnected(ComponentName componentName) {
                Dialog dialog = new Dialog(getApplicationContext());

                dialog.setTitle("Disconnected from Sea");
                TextView content = new TextView(getApplicationContext());
                content.setText("Module Manager was disconnected from Sea!");
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

        setContentView(R.layout.module_toggle);

        recyclerView = new RecyclerView(this);

        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.controller_layout);
        linearLayout.addView(recyclerView, linearLayout.getWidth(), linearLayout.getHeight());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbindService(connection);
    }

    public class ModuleAdapter extends RecyclerView.Adapter<ModuleAdapter.ViewHolder> {
        public class ViewHolder extends RecyclerView.ViewHolder {
            public CardView view;

            public ViewHolder(CardView view) {
                super(view);
                this.view = view;
            }
        }

        @Override
        public ModuleAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            return new ViewHolder(new CardView(ModuleToggleActivity.this));
        }

        @Override
        public void onBindViewHolder(ModuleAdapter.ViewHolder viewHolder, int i) {
            final String name;
            boolean loaded;
            try {
                name = controller.modules().get(i);
                loaded = controller.isLoaded(name);
            } catch (RemoteException e) {
                e.printStackTrace();
                return;
            }

            TextView nameView = new TextView(ModuleToggleActivity.this);
            nameView.setTextSize(36);
            nameView.setText(name);
            CheckBox box = new CheckBox(ModuleToggleActivity.this);
            box.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        if (controller.isLoaded(name)) {
                            controller.unload(name);
                        } else {
                            controller.load(name);
                        }
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            });

            box.setChecked(loaded);
            viewHolder.view.addView(nameView);
            viewHolder.view.addView(box);
        }

        @Override
        public int getItemCount() {
            try {
                return controller.modules().size();
            } catch (RemoteException e) {
                return 0;
            }
        }
    }
}
