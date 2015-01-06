package org.directcode.neo.sea;

import org.directcode.neo.sea.modules.BatteryIconPercentage;
import org.directcode.neo.sea.modules.VolumeMusicController;

public class SeaUtils {
    public static void applyModules(Sea sea) {
        sea.addModule(new VolumeMusicController());
        sea.addModule(new BatteryIconPercentage());
    }
}
