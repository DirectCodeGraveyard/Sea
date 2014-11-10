package org.directcode.neo.sea;

import android.provider.Settings;

import org.directcode.neo.sea.modules.VolumeMusicController;

public class SeaUtils {
    public static void applyModules(Sea sea) {
        try {
            boolean enabled = Settings.System.getInt(sea.getContext().getContentResolver(), "volume_controls_music_track") == 1;
            if (enabled) {
                sea.addModule(new VolumeMusicController());
            }
        } catch (Settings.SettingNotFoundException e) {
            SeaLog.warn("Unable to find setting volume_controls_music_track");
        }
    }
}
