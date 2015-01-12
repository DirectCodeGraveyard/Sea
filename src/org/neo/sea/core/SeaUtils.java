package org.neo.sea.core;

import org.neo.sea.modules.VolumeMusicController;

public class SeaUtils {
    public static void applyModules(Sea sea) {
        sea.addModule(new VolumeMusicController());
    }
}
