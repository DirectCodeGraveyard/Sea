package org.directcode.neo.sea;

import org.directcode.neo.sea.modules.VolumeMusicController;

public class SeaUtils {
    public static void applyModules(Sea sea) {
        sea.addModule(new VolumeMusicController());
    }
}
