package org.directcode.neo.sea.modules;

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

import org.directcode.neo.sea.Sea;
import org.directcode.neo.sea.SeaLog;
import org.directcode.neo.sea.SeaModule;

public class VolumeMusicController extends SeaModule {
    private Sea sea;
    private DisplayManager displayManager;
    private DisplayManager.DisplayListener displayListener;
    private VolumeButtonReceiver volumeReceiver;
    private AudioManager audioManager;

    @Override
    public String name() {
        return "Volume Music Controller";
    }

    @Override
    public void load(Sea sea) {
        this.sea = sea;

        displayManager = (DisplayManager) sea.getSystemService(Context.DISPLAY_SERVICE);
        volumeReceiver = new VolumeButtonReceiver();
        audioManager = (AudioManager) sea.getSystemService(Context.AUDIO_SERVICE);

        Display[] displays = displayManager.getDisplays(DisplayManager.DISPLAY_CATEGORY_PRESENTATION);

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
        IntentFilter filter = new IntentFilter(Intent.ACTION_MEDIA_BUTTON);
        filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
        sea.getContext().registerReceiver(volumeReceiver, filter);
    }

    private void deactivateController() {
        sea.getContext().unregisterReceiver(volumeReceiver);
    }

    @Override
    public void unload(Sea sea) {
        deactivateController();
        displayManager.unregisterDisplayListener(displayListener);
    }

    public class VolumeButtonReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (Intent.ACTION_MEDIA_BUTTON.equals(intent.getAction())) {
                KeyEvent event = intent.getParcelableExtra(Intent.EXTRA_KEY_EVENT);

                if (!(audioManager.isMusicActive() &&
                        (KeyEvent.KEYCODE_VOLUME_UP == event.getKeyCode() ||
                                KeyEvent.KEYCODE_VOLUME_DOWN == event.getKeyCode())) ||
                        event.isLongPress()) {
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
