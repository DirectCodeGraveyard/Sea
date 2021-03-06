package org.neo.sea.modules;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.display.DisplayManager;
import android.media.AudioManager;
import android.os.Handler;
import android.os.Looper;
import android.view.Display;
import android.view.KeyEvent;

import org.neo.sea.core.LocalModule;
import org.neo.sea.core.SeaLog;

public class VolumeMusicController extends LocalModule {
    private DisplayManager displayManager;
    private DisplayManager.DisplayListener displayListener;
    private VolumeButtonReceiver volumeReceiver;
    private AudioManager audioManager;
    private boolean enabled = false;

    @Override
    public String name() {
        return "Volume Music Controller";
    }

    @Override
    public void load() {
        SeaLog.info("Loading Volume Music Controller");

        displayManager = (DisplayManager) sea.getSystemService(Context.DISPLAY_SERVICE);
        volumeReceiver = new VolumeButtonReceiver();
        audioManager = (AudioManager) sea.getSystemService(Context.AUDIO_SERVICE);

        Display[] displays = displayManager.getDisplays();

        if (displays.length == 0) {
            SeaLog.warn("Volume Music Controller: No Displays Found");
            return;
        }

        final Display display = displays[0];

        SeaLog.info("Using " + display.getName() + " as the default display.");

        displayListener = new DisplayManager.DisplayListener() {
            @Override
            public void onDisplayAdded(int i) {
            }

            @Override
            public void onDisplayRemoved(int i) {
            }

            @Override
            public void onDisplayChanged(int i) {
                boolean isOff = display.getState() == Display.STATE_OFF || display.getState() == Display.STATE_DOZE;

                if (isOff) {
                    activateController();
                } else {
                    deactivateController();
                }
            }
        };

        displayManager.registerDisplayListener(displayListener, new Handler(Looper.myLooper()));
    }

    private void activateController() {
        SeaLog.info("Activating Volume Music Controller");
        IntentFilter filter = new IntentFilter(Intent.ACTION_MEDIA_BUTTON);
        filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
        sea.getContext().registerReceiver(volumeReceiver, filter);
        enabled = true;
    }

    private void deactivateController() {
        SeaLog.info("Deactivating Volume Music Controller");
        sea.getContext().unregisterReceiver(volumeReceiver);
        enabled = false;
    }

    @Override
    public void unload() {
        SeaLog.info("Unloading Volume Music Controller");

        if (enabled) {
            deactivateController();
        }

        if (displayListener != null) {
            displayManager.unregisterDisplayListener(displayListener);
        }
    }

    public class VolumeButtonReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            SeaLog.info("Volume Button Receiver - Got Something");
            if (Intent.ACTION_MEDIA_BUTTON.equals(intent.getAction())) {
                KeyEvent event = intent.getParcelableExtra(Intent.EXTRA_KEY_EVENT);

                if (!(audioManager.isMusicActive() &&
                        (KeyEvent.KEYCODE_VOLUME_UP == event.getKeyCode() ||
                                KeyEvent.KEYCODE_VOLUME_DOWN == event.getKeyCode())) ||
                        event.isLongPress()) {
                    SeaLog.info("Got Volume Key Press: Delegating to default action.");
                    return;
                }

                abortBroadcast();

                if (KeyEvent.KEYCODE_VOLUME_UP == event.getKeyCode()) {
                    sendMusicCommand("next");
                } else {
                    sendMusicCommand("previous");
                }
            }
        }

        public void sendMusicCommand(final String command) {
            Intent intent = new Intent("com.android.music.musicservicecommand");
            intent.putExtra("command", command);
            sea.getContext().sendBroadcast(intent);
        }
    }
}
